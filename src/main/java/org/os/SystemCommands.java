package org.os;
import java.io.*;
import java.util.Arrays;

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
}
