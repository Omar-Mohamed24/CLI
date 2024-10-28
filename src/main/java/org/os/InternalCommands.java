package org.os;

public class InternalCommands
{
    public static void exit()
    {
        System.out.println("Exiting CLI...");
        System.exit(0);
    }

    public static void help()
    {
        System.out.println("Available commands:");
        System.out.println();

        System.out.println("exit - Terminate the CLI.");
        System.out.println();

        System.out.println("help - Displays available commands and their usage.");
        System.out.println();

        System.out.println("pwd - Print the current working directory.");
        System.out.println();

        System.out.println("cd <directory> - Change the current directory to the specified directory.");
        System.out.println("Usage: cd /path/to/directory");
        System.out.println();

        System.out.println("ls - List files in the current directory.");
        System.out.println("Usage: ls");
        System.out.println();

        System.out.println("ls -a - List all files in the current directory, including hidden files.");
        System.out.println("Usage: ls -a");
        System.out.println();

        System.out.println("ls -r - List files in the current directory in reverse order.");
        System.out.println("Usage: ls -r");
        System.out.println();

        System.out.println("mkdir <directory> - Create a new directory with the specified name.");
        System.out.println("Usage: mkdir newDirectoryName");
        System.out.println();

        System.out.println("rmdir <directory> - Remove the specified directory (must be empty).");
        System.out.println("Usage: rmdir directoryName");
        System.out.println();

        System.out.println("touch <file> - Create a new empty file or update the timestamp of an existing file.");
        System.out.println("Usage: touch filename.txt");
        System.out.println();

        System.out.println("mv <source> <destination> - Move or rename a file or directory.");
        System.out.println("Usage: mv oldFileName.txt newFileName.txt");
        System.out.println("Usage: mv oldDirectoryName newDirectoryName");
        System.out.println();

        System.out.println("rm <file> - Remove the specified file.");
        System.out.println("Usage: rm filename.txt");
        System.out.println();

        System.out.println("cat <file> - Display the contents of the specified file.");
        System.out.println("Usage: cat filename.txt");
        System.out.println();

        System.out.println("> <command> <file> - Redirect the output of a command to the specified file (overwrites).");
        System.out.println("Usage: command > outputFile.txt");
        System.out.println();

        System.out.println(">> <command> <file> - Redirect the output of a command to the specified file (appends).");
        System.out.println("Usage: command >> outputFile.txt");
        System.out.println();

        System.out.println("| <command1> | <command2> - Pipe the output of command1 as input to command2.");
        System.out.println("Usage: command1 | command2");
        System.out.println();
    }
}
