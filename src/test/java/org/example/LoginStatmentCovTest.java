package org.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.*;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class LoginStatmentCovTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private final String DB_DIR = "db";
    private final String CRED_FILE = "db" + File.separator + "credentials.txt";

    @Before
    public void setUp() throws IOException {
        System.setOut(new PrintStream(outContent));

        File dir = new File(DB_DIR);
        if (!dir.exists()) dir.mkdirs();

        File file = new File(CRED_FILE);
        if (file.exists() && !file.delete()) {
            System.err.println("Warning: Could not delete credentials.txt");
        }

        FileWriter writer = new FileWriter(file);
        writer.write("12345 password" + System.lineSeparator());
        writer.write("99999 otherpass" + System.lineSeparator());
        writer.close();
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
        new File(CRED_FILE).delete();
        new File(DB_DIR).delete();
    }

    @Test
    public void testLoginSuccess() throws IOException {
        Login login = new Login();

        login.loginAuth(12345, "password");
        assertTrue(outContent.toString().contains("Login Successful!!"));
    }

    @Test
    public void testIncorrectPassword() throws IOException {
        Login login = new Login();

        String simulatedInput = "12345\npassword\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        login.loginAuth(12345, "wrongpass");
        assertTrue(outContent.toString().contains("Incorrect Password!"));
        assertTrue(outContent.toString().contains("Login Successful!!"));
    }

    @Test
    public void testAccountDoesNotExist() throws IOException {
        Login login = new Login();

        String simulatedInput = "12345\npassword\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));
        login.loginAuth(88888, "anypass");

        assertTrue(outContent.toString().contains("Account doesn't exists!"));
        assertTrue(outContent.toString().contains("Login Successful!!"));
    }
}