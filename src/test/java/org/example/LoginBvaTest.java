package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class LoginBvaTest {

    private final Login login = new Login();
    private final Path dbDir = Paths.get("db");
    private final Path credFile = dbDir.resolve("credentials.txt");
    private boolean credExisted;
    private String originalCredContent;

    @BeforeEach
    void setUp() throws IOException {
        // Ensure credentials file exists with known test data, preserving prior state
        Files.createDirectories(dbDir);
        credExisted = Files.exists(credFile);
        if (credExisted) {
            originalCredContent = Files.readString(credFile);
        }
        Files.writeString(credFile, """
                1000000 000
                1000001 abc
                1000002 time
                """);
    }

    @AfterEach
    void tearDown() throws IOException {
        if (credExisted) {
            Files.writeString(credFile, originalCredContent);
        } else {
            Files.deleteIfExists(credFile);
        }
    }

    // 🔹 ACCOUNT NUMBER BVA
    @Test
    void AC01_negativeAccount() throws IOException {
        assertFalse(login.loginAuthCheck(-1, "test"));
    }

    @Test
    void AC02_zeroAccount() throws IOException {
        assertFalse(login.loginAuthCheck(0, "test"));
    }

    @Test
    void AC03_validAccount_correctPin() throws IOException {
        assertTrue(login.loginAuthCheck(1000000, "000"));
    }

    @Test
    void AC04_validAccount_wrongPin() throws IOException {
        assertFalse(login.loginAuthCheck(1000000, "123"));
    }

    @Test
    void AC05_validAccount_correctPin2() throws IOException {
        assertTrue(login.loginAuthCheck(1000001, "abc")); // must match file
    }

    // 🔹 PIN BVA
    @Test
    void PIN01_emptyPin() throws IOException {
        assertFalse(login.loginAuthCheck(1000000, ""));
    }

    @Test
    void PIN02_tooShort() throws IOException {
        assertFalse(login.loginAuthCheck(1000000, "0"));
    }

    @Test
    void PIN03_exactMatch() throws IOException {
        assertTrue(login.loginAuthCheck(1000000, "000"));
    }

    @Test
    void PIN04_tooLong() throws IOException {
        assertFalse(login.loginAuthCheck(1000000, "0000"));
    }

    @Test
    void PIN05_wrongCase() throws IOException {
        assertFalse(login.loginAuthCheck(1000001, "Abc"));
    }

    @Test
    void PIN06_specialChars() throws IOException {
        assertFalse(login.loginAuthCheck(1000002, "t!me"));
    }

    // 🔹 COMBINED BVA
    @Test
    void COM01_bothInvalid() throws IOException {
        assertFalse(login.loginAuthCheck(0, ""));
    }

    @Test
    void COM02_validAcc_emptyPin() throws IOException {
        assertFalse(login.loginAuthCheck(1000000, ""));
    }

    @Test
    void COM03_validAcc_correctPin3() throws IOException {
        assertTrue(login.loginAuthCheck(1000002, "time")); // must match file
    }

    @Test
    void COM04_invalidAcc_validPinFormat() throws IOException {
        assertFalse(login.loginAuthCheck(1000003, "000"));
    }
}
