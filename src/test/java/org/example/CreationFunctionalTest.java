package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreationFunctionalTest {

    private final Path dbDir = Paths.get("db");
    private final Path credFile = dbDir.resolve("credentials.txt");
    private final Path balFile = dbDir.resolve("balanceDB.txt");
    private final Path userFile = dbDir.resolve("userDB.txt");

    private String originalCredContent;
    private String originalBalContent;
    private String originalUserContent;
    private boolean credExisted;
    private boolean balExisted;
    private boolean userExisted;

    @BeforeEach
    public void setUp() throws IOException {
        // Prepare clean db directory and remember original state so we can restore it
        Files.createDirectories(dbDir);
        credExisted = Files.exists(credFile);
        balExisted = Files.exists(balFile);
        userExisted = Files.exists(userFile);

        if (credExisted) {
            originalCredContent = Files.readString(credFile);
        } else {
            Files.createFile(credFile);
        }
        Files.writeString(credFile, "");
        if (balExisted) {
            originalBalContent = Files.readString(balFile);
        }
        if (userExisted) {
            originalUserContent = Files.readString(userFile);
        }
        // Force creation paths to run from empty balance/user files
        Files.deleteIfExists(balFile);
        Files.deleteIfExists(userFile);
    }

    @AfterEach
    public void tearDown() throws IOException {
        if (credExisted) {
            Files.writeString(credFile, originalCredContent);
        } else {
            Files.deleteIfExists(credFile);
        }

        if (balExisted) {
            Files.writeString(balFile, originalBalContent);
        } else {
            Files.deleteIfExists(balFile);
        }

        if (userExisted) {
            Files.writeString(userFile, originalUserContent);
        } else {
            Files.deleteIfExists(userFile);
        }
    }

    @Test
    public void testAccNoCreationWhenEmpty() throws IOException {
        // When credentials file is empty, the first generated account number should be 1
        Creation creation = new Creation();

        int accNo = creation.accNoCreation();
        assertEquals(1, accNo);
    }

    @Test
    public void testAccNoCreationWithExistingRecords() throws IOException {
        // With existing max account 5, the next generated number should be 6
        Files.writeString(credFile, "1 pass\n5 pass");
        Creation creation = new Creation();

        int accNo = creation.accNoCreation();
        assertEquals(6, accNo);
    }

    @Test
    public void testCredentialBalanceAndUserWrites() throws IOException {
        // Verify credWrite/balWrite/userWrite append the expected lines to each file
        Creation creation = new Creation();
        int accNo = 12;
        String[] accLineInfo = new String[] {
                "Jane", "Doe", "1990-01-01", "F", "1 Main St", "555-5555", "jane@example.com", "ABC123", "secret"
        };

        creation.credWrite(accNo, accLineInfo);
        creation.balWrite(accNo);
        creation.userWrite(accNo, accLineInfo);

        String credContent = Files.readString(credFile).trim();
        String balContent = Files.readString(balFile).trim();
        String userContent = Files.readString(userFile).trim();

        assertEquals("12 secret", credContent);
        assertEquals("12 69", balContent);
        assertEquals("12 Jane Doe 1990-01-01 F 1 Main St 555-5555 jane@example.com ABC123", userContent);
    }
}
