package org.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class LoginStatmentCovTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private String originalCredentialContent;
    private boolean credentialFileExisted;
    private final Path credFile = Paths.get("db", "credentials.txt");

    @Before
    public void setUp() throws IOException {
        System.setOut(new PrintStream(outContent));

        File dir = credFile.getParent().toFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Unable to create db directory");
        }

        credentialFileExisted = Files.exists(credFile);
        if (credentialFileExisted) {
            originalCredentialContent = Files.readString(credFile);
        }

        try (FileWriter writer = new FileWriter(credFile.toFile())) {
            writer.write("12345 password" + System.lineSeparator());
            writer.write("99999 otherpass" + System.lineSeparator());
        }
    }

    @After
    public void tearDown() throws IOException {
        System.setOut(originalOut);
        System.setIn(originalIn);

        if (credentialFileExisted) {
            Files.writeString(credFile, originalCredentialContent);
        } else {
            Files.deleteIfExists(credFile);
        }
    }

    @Test
    public void testLoginSuccess() throws IOException {
        Login login = new Login();

        login.loginAuth(12345, "password");
        assertTrue(outContent.toString().contains("Login Successful!!"));
    }

    @Test
    public void testIncorrectPasswordTriggersRetry() throws IOException {
        Login login = new Login();

        String simulatedInput = "12345\npassword\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        login.loginAuth(12345, "wrongpass");
        String output = outContent.toString();
        assertTrue(output.contains("Incorrect Password!"));
        assertTrue(output.contains("Login Successful!!"));
    }

    @Test
    public void testAccountDoesNotExistTriggersCreationPrompt() throws IOException {
        Login login = new Login();

        String simulatedInput = "12345\npassword\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        login.loginAuth(88888, "anypass");
        String output = outContent.toString();
        assertTrue(output.contains("Account doesn't exists!"));
        assertTrue(output.contains("Login Successful!!"));
    }
}
