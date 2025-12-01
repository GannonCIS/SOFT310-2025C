package org.example;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;

public class DeletionBranchCovTest {

    @Rule
    public TemporaryFolder tempDir = new TemporaryFolder();

    private Path testFile;

    @Before
    public void setUp() throws IOException {
        testFile = tempDir.newFile("test_deletion_db.txt").toPath();
    }

    private void createTestFile(String content) throws IOException {
        try (FileWriter writer = new FileWriter(testFile.toFile())) {
            writer.write(content);
        }
    }

    private String readFile() throws IOException {
        return Files.exists(testFile) ? Files.readString(testFile) : "";
    }

    @Test
    public void testStandardDeletion() throws IOException {
        createTestFile("101 user1\n102 user2\n103 user3");

        Deletion deletion = new Deletion();
        deletion.delLine(102, testFile.toString());

        String result = readFile();
        String expected = "101 user1\n103 user3";
        assertEquals(expected, result);
    }

    @Test
    public void testDeleteLastRemainingAccount() throws IOException {
        createTestFile("101 user1");

        Deletion deletion = new Deletion();
        deletion.delLine(101, testFile.toString());

        String result = readFile();
        assertEquals("", result);
    }

    @Test
    public void testEmptyFile() throws IOException {
        createTestFile("");

        Deletion deletion = new Deletion();

        deletion.delLine(101, testFile.toString());

        String result = readFile();
        assertEquals("", result);
    }
}
