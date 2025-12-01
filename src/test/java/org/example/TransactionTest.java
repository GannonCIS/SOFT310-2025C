package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionTest {

    private final Path dbDir = Paths.get("db");
    private final Path balanceFile = dbDir.resolve("balanceDB.txt");
    private final Path statementDir = dbDir.resolve("Bank Statement");

    private boolean balanceExisted;
    private String originalBalanceContent;
    private boolean statementDirExisted;
    private Map<Path, String> originalStatements;

    @BeforeEach
    public void setUp() throws IOException {
        statementDirExisted = Files.exists(statementDir);
        Files.createDirectories(statementDir);
        balanceExisted = Files.exists(balanceFile);
        if (balanceExisted) {
            originalBalanceContent = Files.readString(balanceFile);
        }
        Files.writeString(balanceFile, "1 200\n2 100");

        if (statementDirExisted) {
            try (var stream = Files.list(statementDir)) {
                originalStatements = stream
                        .filter(Files::isRegularFile)
                        .collect(Collectors.toMap(Path::getFileName, path -> {
                            try {
                                return Files.readString(path);
                            } catch (IOException e) {
                                return "";
                            }
                        }));
            }
        } else {
            originalStatements = new HashMap<>();
        }
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (balanceExisted) {
            Files.writeString(balanceFile, originalBalanceContent);
        } else {
            Files.deleteIfExists(balanceFile);
        }

        if (statementDirExisted) {
            // Restore original files
            for (Map.Entry<Path, String> entry : originalStatements.entrySet()) {
                Path target = statementDir.resolve(entry.getKey());
                Files.writeString(target, entry.getValue());
            }
            // Remove any new files not originally present
            try (var stream = Files.list(statementDir)) {
                stream.filter(path -> !originalStatements.containsKey(path.getFileName()))
                        .forEach(path -> {
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException ignored) {
                            }
                        });
            }
        } else {
            // Directory was created by tests; remove it entirely
            if (Files.exists(statementDir)) {
                try (var stream = Files.list(statementDir)) {
                    stream.forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
                }
                Files.deleteIfExists(statementDir);
            }
        }
    }

    @Test
    public void testReceiverAccountCheck() throws IOException {
        Transaction transaction = new Transaction();

        assertTrue(transaction.rAccCheck(1));
        assertFalse(transaction.rAccCheck(999));
    }

    @Test
    public void testSenderBalanceCheck() throws IOException {
        Transaction transaction = new Transaction();

        assertTrue(transaction.sAccBalCheck(1, 150));
        assertFalse(transaction.sAccBalCheck(1, 500));
        assertFalse(transaction.sAccBalCheck(3, 10));
    }

    @Test
    public void testTransactionUpdatesBothAccounts() throws IOException {
        Transaction transaction = new Transaction();

        transaction.transaction(1, 2, 50);

        String contents = Files.readString(balanceFile).trim();
        assertEquals("1 150\n2 150", contents);
    }

    @Test
    public void testWriteTransactionCreatesStatements() throws IOException {
        Transaction transaction = new Transaction();

        transaction.writeTransaction(1, 2, 75, "gift");

        Path senderStmt = statementDir.resolve("acc_1.txt");
        Path receiverStmt = statementDir.resolve("acc_2.txt");

        String sender = Files.readString(senderStmt).trim();
        String receiver = Files.readString(receiverStmt).trim();

        assertTrue(sender.startsWith("Transfer to 2 Debit 75 gift"));
        assertTrue(receiver.startsWith("Transfer from 1 Credit 75 gift"));
    }
}
