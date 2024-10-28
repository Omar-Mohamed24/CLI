package org.os;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class SystemCommands
{
    private static File currentDirectory = new File(System.getProperty("user.dir"));

    public static void pwd()
    {
        // Get the current working directory
        System.out.println(currentDirectory.getAbsolutePath());
    }

    public static void cd(String newDir)
    {
        // If the user tries to go up a directory
        if ("./".equals(newDir))
        {
            File parentDir = currentDirectory.getParentFile();
            if (parentDir != null)
            {
                currentDirectory = parentDir; // Update the current directory
            }
            else
            {
                System.out.println("Already at the root directory.");
            }
        }
        else if ("..".equals(newDir))
        {
            // Change to home directory
            currentDirectory = new File(System.getProperty("user.home"));
        }
        else
        {
            File targetDir = new File(newDir);

            // Check if the path is absolute
            // Update current directory
            if (!targetDir.isAbsolute())
            {
                targetDir = new File(currentDirectory, newDir);
            }

            if (targetDir.exists() && targetDir.isDirectory())
            {
                currentDirectory = targetDir; // Update current directory
            }
            else
            {
                System.out.println("Directory does not exist: " + newDir);
            }
        }
        System.setProperty("user.dir", currentDirectory.getAbsolutePath());
    }

    public static void ls(String[] options)
    {
        File[] files = currentDirectory.listFiles();

        if (files == null || files.length == 0)
        {
            System.out.println("No files found in the directory.");
            return;
        }

        boolean showAll = Arrays.asList(options).contains("-a");
        boolean reverse = Arrays.asList(options).contains("-r");

        // Filter files to show based on the options
        if (!showAll)
        {
            files = Arrays.stream(files)
                    .filter(file -> !file.getName().startsWith("."))
                    .toArray(File[]::new);
        }

        if (reverse)
        {
            Arrays.sort(files, (f1, f2) -> f2.getName().compareTo(f1.getName())); // Sort in reverse
        }
        else
        {
            Arrays.sort(files); // Sort in natural order
        }

        // Print the files
        for (File file : files)
        {
            System.out.println(file.getName());
        }
    }

    public static void mkdir(String dirPath)
    {
        // Create a File object representing the directory
        File newDir = new File(dirPath);

        // Check if the directory already exists
        if (newDir.exists())
        {
            System.out.println("Directory already exists: " + dirPath);
        }
        else
        {
            // Attempt to create the directory
            boolean success = newDir.mkdirs();  // Creates the directory, including any missing parent directories
            if (success)
            {
                System.out.println("Directory created successfully: " + dirPath);
            }
            else
            {
                System.out.println("Failed to create directory: " + dirPath);
            }
        }
    }

    public static void rmdir(String dirPath)
    {
        File dir = new File(dirPath);

        if (!dir.exists())
        {
            System.out.println("Directory does not exist: " + dirPath);
        }
        else if (!dir.isDirectory())
        {
            System.out.println("Not a directory: " + dirPath);
        }
        else
        {
            // Check if we can list the contents of the directory (avoid NullPointerException)
            String[] files = dir.list();
            if (files == null)
            {
                System.out.println("Unable to access the directory: " + dirPath);
            }
            else if (files.length > 0)
            {
                System.out.println("Directory is not empty: " + dirPath);
            }
            else
            {
                if (dir.delete())
                {
                    System.out.println("Directory deleted: " + dirPath);
                }
                else
                {
                    System.out.println("Failed to delete directory: " + dirPath);
                }
            }
        }
    }

    public static void touch(String[] filePaths)
    {
        for (String filePath : filePaths)
        {
            // Create a File object for the specified file path
            File file = new File(filePath.trim());

            try
            {
                if (file.createNewFile())
                {
                    System.out.println("File created: " + file.getAbsolutePath());
                }
                else
                {
                    // If the file already exists, update its timestamp
                    if (file.setLastModified(System.currentTimeMillis()))
                    {
                        System.out.println("Timestamp updated for: " + file.getAbsolutePath());
                    }
                    else
                    {
                        System.out.println("Failed to update timestamp for: " + file.getAbsolutePath());
                    }
                }
            }
            catch (IOException e)
            {
                System.out.println("An error occurred while trying to create/update the file: " + e.getMessage());
            }
        }
    }

    public static void mv(String[] commandParts)
    {
        Scanner scanner = new Scanner(System.in); // Scanner for user input

        // Extract destination from last part of command, remove any quotes
        String destination = commandParts[commandParts.length - 1].replace("\"", "").trim();

        // Everything before the last part is considered source files
        String[] sourceFiles = new String[commandParts.length - 1];
        System.arraycopy(commandParts, 0, sourceFiles, 0, commandParts.length - 1);

        File distDir = new File(destination);

        // Determine if the operation is renaming (single source and non-directory destination)
        boolean isRenaming = sourceFiles.length == 1 && !distDir.isDirectory();

        // Iterate over each source file
        for (String source : sourceFiles)
        {
            String sourceTrimmed = source.replace("\"", "").trim();
            File sourceFile = new File(sourceTrimmed);

            if (!sourceFile.exists())
            {
                System.out.println("Source file does not exist: " + sourceTrimmed);
                continue; // Skip to the next source
            }

            // Handle renaming case if only one source file is provided
            if (isRenaming)
            {
                try
                {
                    if (sourceFile.renameTo(distDir))
                    {
                        System.out.println("Renamed " + sourceTrimmed + " to " + destination);
                    }
                    else
                    {
                        System.out.println("Failed to rename " + sourceTrimmed);
                    }
                }
                catch (Exception e)
                {
                    System.out.println("Error renaming file: " + e.getMessage());
                }
            }
            else
            {
                // Handle moving to destination directory
                if (!distDir.exists())
                {
                    System.out.println("Destination directory does not exist. Creating directory...");
                    if (!distDir.mkdirs())
                    {
                        System.out.println("Failed to create destination directory: " + distDir.getAbsolutePath());
                        return;
                    }
                }

                // Check if destination is a directory and proceed
                if (distDir.isDirectory())
                {
                    File targetFile = new File(distDir, sourceFile.getName());

                    // Handle overwrite prompt if the target file already exists
                    if (targetFile.exists())
                    {
                        System.out.print("File " + targetFile.getName() + " already exists. Overwrite? (y/n): ");
                        String response = scanner.nextLine().trim().toLowerCase();
                        if (!response.startsWith("y"))
                        {
                            System.out.println("Skipped moving " + sourceTrimmed);
                            continue; // Move on to the next source file
                        }
                        else if (!targetFile.delete())
                        {
                            System.out.println("Failed to delete existing file: " + targetFile.getAbsolutePath());
                            continue;
                        }
                    }

                    // Attempt to move the file
                    try
                    {
                        if (sourceFile.renameTo(targetFile))
                        {
                            System.out.println("Moved " + sourceTrimmed + " to " + distDir.getAbsolutePath());
                        }
                        else
                        {
                            System.out.println("Failed to move " + sourceTrimmed);
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println("Error moving file: " + e.getMessage());
                    }
                }
                else
                {
                    System.out.println("Destination is not a directory: " + distDir.getAbsolutePath());
                }
            }
        }
    }

    public static void rm(String[] commandParts)
    {
        for (int i = 0; i < commandParts.length; i++)
        {
            if (commandParts[i].equals("-r"))
            {
                continue;
            }
            String path = commandParts[i].replace("\"", "").trim();
            File file = new File(path);

            // Check if the file/directory exists
            if (!file.exists())
            {
                System.out.println("File or directory does not exist: " + path);
                continue; // Skip to the next
            }

            // Check if the current part is a directory
            if (file.isDirectory())
            {
                File[] files = file.listFiles();
                if (files == null || files.length == 0)
                {
                    // If empty, delete the directory
                    if (file.delete())
                    {
                        System.out.println("Removed directory: " + path);
                    }
                    else
                    {
                        System.out.println("Failed to remove directory: " + path);
                    }
                }
                else
                {
                    // Directory is not empty
                    if (i > 0 && "-r".equals(commandParts[i - 1]))
                    {
                        // If -r is before the directory, delete it recursively
                        deleteDirectoryRecursively(file);
                    }
                    else
                    {
                        System.out.println("Directory not empty: " + path + " (use -r to delete recursively)");
                    }
                }
            }
            else
            {
                if (file.delete())
                {
                    System.out.println("Removed file: " + path);
                }
                else
                {
                    System.out.println("Failed to remove file: " + path);
                }
            }
        }
    }

    // Recursive method to delete a directory and its contents for rm command
    private static void deleteDirectoryRecursively(File dir)
    {
        File[] files = dir.listFiles();
        if (files != null)
        {
            for (File file : files)
            {
                if (file.isDirectory())
                {
                    deleteDirectoryRecursively(file); // Recursively delete subdirectories
                }
                else
                {
                    if (!file.delete())
                    {
                        System.out.println("Failed to delete file: " + file.getAbsolutePath());
                    }
                    else
                    {
                        System.out.println("Removed file: " + file.getAbsolutePath());
                    }
                }
            }
        }

        if (dir.delete())
        {
            System.out.println("Removed directory: " + dir.getAbsolutePath());
        }
        else
        {
            System.out.println("Failed to delete directory: " + dir.getAbsolutePath());
        }
    }


}
