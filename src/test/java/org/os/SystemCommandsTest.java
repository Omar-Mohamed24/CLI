package org.os;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class SystemCommandsTest
{
    private String initialDir;

    @BeforeEach
    void setUp()
    {
        SystemCommands.output="";
        initialDir = System.getProperty("user.dir");
    }

    @AfterEach
    void tearDown()
    {
        System.setProperty("user.dir", initialDir);
        SystemCommands.output="";
    }

    @Test
    public void testPwd()
    {
        SystemCommands.pwd();
        assertEquals(SystemCommands.output, System.getProperty("user.dir"));
    }

    @Test
    public void testCd()
    {
        File testDir = new File(initialDir, "testDir");
        SystemCommands.mkdir("testDir");
        SystemCommands.cd("testDir");
        assertEquals(testDir.getAbsolutePath(), System.getProperty("user.dir"));
        SystemCommands.rmdir("testDir");
    }

    @Test
    public void testCdAbsolute()
    {
        File testDir = new File(initialDir, "testDir");
        SystemCommands.mkdir("testDir");
        SystemCommands.cd(testDir.getAbsolutePath());
        assertEquals(testDir.getAbsolutePath(), System.getProperty("user.dir"));
        SystemCommands.rmdir("testDir");
    }

    @Test
    public void testLs()
    {
        SystemCommands.ls(new String[]{"ls"});
        assertFalse(SystemCommands.output.isEmpty(), "Output should not be empty for 'ls' command");
    }

    @Test
    public void testLs_a()
    {
        SystemCommands.ls(new String[]{"ls", "-a"});
        assertFalse(SystemCommands.output.isEmpty(), "Output should not be empty for 'ls -a' command");
    }

    @Test
    public void testLs_r()
    {
        SystemCommands.ls(new String[]{"ls", "-r"});
        assertFalse(SystemCommands.output.isEmpty(), "Output should not be empty for 'ls -r' command");
    }

    @Test
    public void testMkdir()
    {
        String dirName = "testDir";
        SystemCommands.mkdir(dirName);
        File testDir = new File(initialDir, "testDir");
        assertTrue(testDir.exists() && testDir.isDirectory(), "Directory 'testDir' created");
        SystemCommands.rmdir(dirName);
    }

    @Test
    public void testMkdir_ExistingDirectory()
    {
        String dirName = "testDir";
        SystemCommands.mkdir(dirName);
        SystemCommands.mkdir(dirName);
        assertEquals("Directory already exists: " + dirName + "\n", SystemCommands.output);
        SystemCommands.rmdir(dirName);
    }

    @Test
    public void testRmdir()
    {
        String dirName = "testDir";
        SystemCommands.mkdir(dirName);
        SystemCommands.rmdir(dirName);
        assertEquals("Directory deleted: " + dirName + "\n", SystemCommands.output);

        // Verify the directory no longer exists
        File testDir = new File(initialDir, dirName);
        assertFalse(testDir.exists(), "Directory should not exist after removal");
    }

    @Test
    public void testRmdir_nonExistentDir()
    {
        String dirName = "testDir";
        SystemCommands.rmdir(dirName);
        assertEquals("Directory does not exist: " + dirName + "\n", SystemCommands.output);
    }

    @Test
    public void testRmdir_notDir() throws IOException
    {
        String fileName = "testFile.txt";
        File testFile = new File(initialDir, fileName);
        try (FileWriter writer = new FileWriter(testFile))
        {
            writer.write("This is a test file.");
        }
        SystemCommands.rmdir(fileName);
        assertEquals("Not a directory: " + fileName + "\n", SystemCommands.output);
        assertTrue(testFile.exists(), "File should still exist after attempting to remove it as a directory");
        // Clean up by deleting the file after the test
        assertTrue(testFile.delete());
    }

    @Test
    public void testTouch()
    {
        String fileName = "testFile.txt";
        SystemCommands.touch(new String[]{fileName});
        File testFile = new File(initialDir, fileName);
        assertTrue(testFile.exists(), "The file should exist after using the touch command");
        assertEquals("File created: " + fileName + "\n", SystemCommands.output);
        assertTrue(testFile.delete());
    }

    @Test
    public void testTouch_files()
    {
        String[] fileNames = {"file1.txt", "file2.txt"};
        SystemCommands.touch(new String[]{"file1.txt", "file2.txt"});
        for (String fileName : fileNames)
        {
            assertTrue(new File(fileName).exists());
            assertTrue(new File(fileName).delete());
        }
    }

    @Test
    public void testTouch_update()
    {
        String fileName = "testFile.txt";
        SystemCommands.touch(new String[]{fileName}); //create
        SystemCommands.touch(new String[]{fileName}); //update
        File testFile = new File(initialDir, fileName);
        assertTrue(testFile.exists(), "The file should exist after using the touch command");
        assertEquals("Timestamp updated for: " + testFile.getAbsolutePath() + "\n", SystemCommands.output);
        assertTrue(testFile.delete());
    }

    @Test
    public void testMv() throws IOException
    {
        String source = "SourceFile.txt",  destinationDir = "destinationDir";
        assertTrue(new File(source).createNewFile());
        SystemCommands.mkdir(destinationDir);
        SystemCommands.mv(new String[]{source, destinationDir});
        assertFalse(new File(source).exists());
        assertTrue(new File(destinationDir, source).exists());
        assertTrue(new File(destinationDir, source).delete());
        assertTrue(new File(destinationDir).delete());
    }

    @Test
    public void testMv_rename() throws IOException
    {
        String name="firstname.txt", newname="newname.txt";
        assertTrue(new File(name).createNewFile());
        SystemCommands.mv(new String[]{name, newname});
        assertFalse(new File(name).exists());
        assertTrue(new  File(newname).exists());
        assertTrue(new File(newname).delete());
    }

    @Test
    public void testMv_files() throws IOException
    {
        String source = "SourceFile.txt", source2 = "Sourcefile2.txt" ,destinationDir = "destinationDir";
        assertTrue(new File(source).createNewFile());
        assertTrue(new File(source2).createNewFile());
        SystemCommands.mkdir(destinationDir);
        SystemCommands.mv(new String[]{source ,source2 ,destinationDir});
        assertFalse(new File(source).exists());
        assertFalse(new File(source2).exists());
        assertTrue(new File(destinationDir, source).exists());
        assertTrue(new File(destinationDir, source2).exists());
        assertTrue(new File(destinationDir, source).delete());
        assertTrue(new File(destinationDir, source2).delete());
        assertTrue(new File(destinationDir).delete());
    }

    @Test
    public void testRm()throws IOException
    {
        String fileName = "fileToDelete.txt";
        SystemCommands.touch(new String[]{fileName});
        File fileToDelete = new File(initialDir, fileName);
        SystemCommands.rm(new String[]{fileName});
        assertFalse(fileToDelete.exists(), "The file should not exist after deletion.");
        assertEquals("Removed file: " + fileName + "\n", SystemCommands.output);
    }

    @Test
    public void testRm_NonExistentFile()throws IOException
    {
        String fileName = "fileToDelete.txt";
        SystemCommands.rm(new String[]{fileName});
        assertEquals("File or directory does not exist: " + fileName + "\n", SystemCommands.output);
    }

    @Test
    public void testRm_files()throws IOException
    {
        String fileName = "fileToDelete.txt" ,fileName2 = "fileToDelete2.txt";
        SystemCommands.touch(new String[]{fileName ,fileName2});
        File fileToDelete = new File(initialDir, fileName);
        File fileToDelete2 = new File(initialDir, fileName2);
        SystemCommands.rm(new String[]{fileName, fileName2});
        assertFalse(fileToDelete.exists(), "The file should not exist after deletion.");
        assertFalse(fileToDelete2.exists(), "The file should not exist after deletion.");
    }

    @Test
    public void testRm_DirRecursive()throws IOException
    {
        File testDir = new File(initialDir, "testDir");
        File subDir = new File(testDir, "subDir");
        File file1 = new File(testDir, "file1.txt");
        File file2 = new File(subDir, "file2.txt");
        assertTrue(testDir.mkdir());
        assertTrue(subDir.mkdir());
        try
        {
            assertTrue(file1.createNewFile());
            assertTrue(file2.createNewFile());
        }
        catch (IOException e)
        {
            fail("Failed to set up test files");
        }
        // Verify structure is created
        assertTrue(testDir.exists() && testDir.isDirectory(), "testDir should exist as a directory");
        assertTrue(subDir.exists() && subDir.isDirectory(), "subDir should exist as a directory");
        assertTrue(file1.exists() && file1.isFile(), "file1 should exist as a file");
        assertTrue(file2.exists() && file2.isFile(), "file2 should exist as a file");

        SystemCommands.rm(new String[]{"-r", "testDir"});
        assertFalse(testDir.exists(), "testDir should be deleted recursively along with its contents");
        assertEquals("Removed directory: " + testDir.getAbsolutePath() + "\n", SystemCommands.output);
    }

    @Test
    public void testCat() throws IOException
    {
        String fileName = "testFile.txt";
        String fileContent = "Hello, world!\nThis is a test file.";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
        {
            writer.write(fileContent);
        }

        try (Scanner scanner = new Scanner(new File(fileName)))
        {
            SystemCommands.cat(new String[]{"testFile.txt"},scanner);
            assertEquals(fileContent + "\n", SystemCommands.output);
        }
        assertTrue(new File(fileName).delete());
    }

    @Test
    public void testCat_files() throws IOException
    {
        String fileName = "testFile.txt", fileName2 = "testFile2.txt";
        String fileContent = "Hello, world!\nThis is a test file.";
        String fileContent2 = "Hello, world!\nThis is the second test file.";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName)))
        {
            writer.write(fileContent);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName2)))
        {
            writer.write(fileContent2);
        }

        try (Scanner scanner = new Scanner(new File(fileName)))
        {
            SystemCommands.cat(new String[]{"testFile.txt","testFile2.txt"},scanner);
            assertEquals(fileContent + "\n" + fileContent2 + "\n" , SystemCommands.output);
        }
        assertTrue(new File(fileName).delete());
        assertTrue(new File(fileName2).delete());
    }

    @Test
    public void testRedirectPwd() throws IOException
    {
        String outputFile = "pwdOutput.txt";
        SystemCommands.redirectOutput("pwd", new String[]{"pwd"}, outputFile, new Scanner(System.in));

        String expectedOutput = System.getProperty("user.dir") + System.lineSeparator();
        String fileContent = Files.readString(Paths.get(outputFile));
        assertEquals(expectedOutput, fileContent);

        // Clean up
        assertTrue(new File(outputFile).delete(), "Output file should be deleted after test.");
    }

    @Test
    public void testRedirectLs() throws IOException
    {
        String outputFile = "lsOutput.txt";
        SystemCommands.redirectOutput("ls", new String[]{"ls"}, outputFile, new Scanner(System.in));

        // Verify that the output file contains the list of files in the current directory
        File currentDir = new File(System.getProperty("user.dir"));
        String[] expectedFiles = currentDir.list();
        assertNotNull(expectedFiles, "Current directory should contain files.");

        List<String> fileContent = Files.readAllLines(Paths.get(outputFile));
        assertEquals(Arrays.asList(expectedFiles), fileContent);

        // Clean up
        assertTrue(new File(outputFile).delete(), "Output file should be deleted after test.");
    }

    @Test
    public void testRedirectCatFromInput() throws IOException
    {
        String outputFile = "catInputOutput.txt";
        String inputText = "This is a test input.\nexit\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(inputText.getBytes()));

        SystemCommands.redirectOutput("cat", new String[]{"cat"}, outputFile, scanner);

        String expectedOutput = "This is a test input.\n".replace("\n", System.lineSeparator());
        String fileContent = Files.readString(Paths.get(outputFile)).replace("\r\n", "\n").replace("\n", System.lineSeparator());
        assertEquals(expectedOutput, fileContent);

        // Clean up
        assertTrue(new File(outputFile).delete(), "Output file should be deleted after test.");
    }

    @Test
    public void testRedirectCatWithFiles() throws IOException
    {
        String file1 = "file1.txt";
        String file2 = "file2.txt";
        String outputFile = "catFilesOutput.txt";
        String content1 = "Hello from file1!";
        String content2 = "Hello from file2!";

        // Set up test files
        Files.writeString(Paths.get(file1), content1);
        Files.writeString(Paths.get(file2), content2);

        SystemCommands.redirectOutput("cat", new String[]{"cat", file1, file2}, outputFile, new Scanner(System.in));

        // Verify that the output file contains content from both files
        String expectedOutput = content1 + System.lineSeparator() + content2 + System.lineSeparator();
        String fileContent = Files.readString(Paths.get(outputFile));
        assertEquals(expectedOutput, fileContent);

        // Clean up
        Files.deleteIfExists(Paths.get(file1));
        Files.deleteIfExists(Paths.get(file2));
        assertTrue(new File(outputFile).delete(), "Output file should be deleted after test.");
    }

    @Test
    public void testRedirectPwdEx() throws IOException
    {
        String outputFile = "pwdOutput_ex.txt";

        // Invoke the redirectOutput_ex method
        SystemCommands.redirectOutput_ex("pwd",new String[]{"pwd"}, outputFile, new Scanner(System.in));
        assertEquals("Error: Output file does not exist." + "\n" ,SystemCommands.output);
    }

    @Test
    public void testRedirectCatExToFileWithSingleFile() throws IOException
    {
        String inputFileName = "inputFile_ex.txt";
        String outputFile = "catOutput_ex.txt";
        assertTrue(new File(outputFile).createNewFile());
        String fileContent = "Hello, World!";
        String fileContent2 = "output file\n";

        // Create input file
        Files.writeString(Paths.get(inputFileName), fileContent);
        Files.writeString(Paths.get(outputFile), fileContent2);

        SystemCommands.redirectOutput_ex("cat",new String[]{"cat", inputFileName}, outputFile, new Scanner(System.in));

        String expectedOutput = fileContent2 + fileContent + System.lineSeparator();
        String fileOutputContent = Files.readString(Paths.get(outputFile));
        assertEquals(expectedOutput, fileOutputContent);

        // Clean up
        assertTrue(new File(inputFileName).delete(), "Input file should be deleted after test.");
        assertTrue(new File(outputFile).delete(), "Output file should be deleted after test.");
    }

    @Test
    public void testPipePwdToRmdir() throws IOException
    {
        // Setup: Create a directory to test rmdir
        String testDirName = "testDir_ex_2";
        assertTrue(new File(testDirName).mkdir());
        SystemCommands.cd("testDir_ex_2");

        // Command parts: pwd | rmdir testDir_ex_2
        String[] commandParts = new String[]{"pwd", "|", "rmdir"};
        SystemCommands.pipe(commandParts);

        // Verify: The directory should have been removed
        assertFalse(new File(testDirName).exists(), "The directory should have been removed by rmdir.");
    }
}
