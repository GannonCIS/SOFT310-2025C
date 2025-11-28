package org.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.junit.Assert.*;

public class DeletionBranchCovTest {

    private final String TEST_FILE = "test_deletion_db.txt";

    @Before
    public void setUp() {
        new File(TEST_FILE).delete();
    }

    @After
    public void tearDown() {
        new File(TEST_FILE).delete();
    }

    private void createTestFile(String content) throws IOException {
        FileWriter writer = new FileWriter(TEST_FILE);
        writer.write(content);
        writer.close();
    }

    private String readFile() throws IOException {
        return new String(Files.readAllBytes(Paths.get(TEST_FILE)));
    }

    @Test
    public void testStandardDeletion() throws IOException {
        createTestFile("101 user1\n102 user2\n103 user3");

        Deletion deletion = new Deletion();
        deletion.delLine(102, TEST_FILE);

        String result = readFile();
        String expected = "101 user1\n103 user3";
        assertEquals(expected, result);
    }

    @Test
    public void testDeleteLastRemainingAccount() throws IOException {
        createTestFile("101 user1");

        Deletion deletion = new Deletion();
        deletion.delLine(101, TEST_FILE);

        String result = readFile();
        assertEquals("", result);
    }

    @Test
    public void testEmptyFile() throws IOException {
        createTestFile("");

        Deletion deletion = new Deletion();

        deletion.delLine(101, TEST_FILE);

        String result = readFile();
        assertEquals("", result);
    }
}