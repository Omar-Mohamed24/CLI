package org.os;
import java.util.Scanner;
import java.util.Arrays;
import java.io.File;

public class CLI
{
    public static void main(String[] args)
    {
        try (Scanner scanner = new Scanner(System.in))
        {
            while (true)
            {
                String dir = System.getProperty("user.dir");
                System.out.print(dir + ">");  // Command prompt
                String input = scanner.nextLine().trim(); // Read input and trim whitespace

                if (input.isEmpty())
                {
                    continue;
                }
                String[] commandParts = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                boolean redirectOutput = false;
                boolean redirectOutput_ex = false;
                boolean pipe = false;
                String outputFileName = null;

                // Check for the presence of ">"
                for (int i = 0; i < commandParts.length; i++)
                {
                    if (">".equals(commandParts[i]))
                    {
                        redirectOutput = true;
                        if (i + 1 < commandParts.length)
                        {
                            outputFileName = commandParts[i + 1].replace("\"", "").trim(); // Get the output file name
                        }
                        commandParts = Arrays.copyOfRange(commandParts, 0, i); // Keep only command parts before ">"
                        break;
                    }
                    if (">>".equals(commandParts[i]))
                    {
                        redirectOutput_ex = true;
                        if (i + 1 < commandParts.length)
                        {
                            outputFileName = commandParts[i + 1].replace("\"", "").trim(); // Get the output file name
                        }
                        commandParts = Arrays.copyOfRange(commandParts, 0, i); // Keep only command parts before ">"
                        break;
                    }
                    if ("|".equals(commandParts[i]))
                    {
                        pipe = true;
                        break;
                    }
                }

                String command = commandParts[0].toLowerCase();
                if (redirectOutput)
                {
                    SystemCommands.redirectOutput(command, commandParts, outputFileName, scanner);
                }
                else if (redirectOutput_ex)
                {
                    SystemCommands.redirectOutput_ex(command, commandParts, outputFileName, scanner);
                }
                else if(pipe)
                {
                    SystemCommands.pipe(commandParts);
                }
                else
                {
                    switch (command)
                    {
                        case "pwd":
                            SystemCommands.pwd();
                            break;

                        case "cd":
                            if (commandParts.length < 2)
                            {
                                System.out.println("Usage: cd <directory>");
                            }
                            else
                            {
                                // Join everything after "cd" to handle directory paths with spaces
                                String path = String.join(" ", Arrays.copyOfRange(commandParts, 1, commandParts.length)).trim();
                                path = path.replace("\"", ""); // Remove quotes if any
                                SystemCommands.cd(path);
                            }
                            break;

                        case "ls":
                            SystemCommands.ls(commandParts.length > 1 ? commandParts : new String[]{});
                            break;

                        case "mkdir":
                            if (commandParts.length < 2)
                            {
                                System.out.println("Usage: mkdir <directory_name>");
                            }
                            else
                            {
                                // Join everything after "mkdir" to handle directory names with spaces
                                String dirPath = String.join(" ", Arrays.copyOfRange(commandParts, 1, commandParts.length)).trim();
                                SystemCommands.mkdir(dirPath);
                            }
                            break;

                        case "rmdir":
                            if (commandParts.length < 2)
                            {
                                System.out.println("Usage: rmdir <directory_name>");
                            }
                            else
                            {
                                // Join everything after "rmdir" to handle directory paths with spaces
                                String dirPath = String.join(" ", Arrays.copyOfRange(commandParts, 1, commandParts.length)).trim();
                                dirPath = dirPath.replace("\"", ""); // Remove quotes if any
                                SystemCommands.rmdir(dirPath);
                            }
                            break;

                        case "touch":
                            if (commandParts.length < 2)
                            {
                                System.out.println("Usage: touch <file_name>");
                            }
                            else
                            {
                                String[] filePaths = Arrays.copyOfRange(commandParts, 1, commandParts.length);
                                for (int i = 0; i < filePaths.length; i++)
                                {
                                    filePaths[i] = filePaths[i].replace("\"", "").trim(); // Remove quotes and trim spaces
                                }
                                SystemCommands.touch(filePaths);
                            }
                            break;

                        case "mv":
                            if (commandParts.length < 2)
                            {
                                System.out.println("Usage: mv <source_files> <destination>");
                            }
                            else
                            {
                                // Join everything after "mv" to handle multiple files and destination
                                String[] filesToMove = Arrays.copyOfRange(commandParts, 1, commandParts.length);
                                SystemCommands.mv(filesToMove);
                            }
                            break;

                        case "rm":
                            if (commandParts.length < 2)
                            {
                                System.out.println("Usage: rm <file(s)/directory(s)>");
                            }
                            else
                            {
                                String[] filesTo_del = Arrays.copyOfRange(commandParts, 1, commandParts.length);
                                SystemCommands.rm(filesTo_del);
                            }
                            break;

                        case "cat":
                            String[] files = Arrays.copyOfRange(commandParts, 1, commandParts.length);
                            SystemCommands.cat(files, scanner);
                            break;

                        case "exit":
                            InternalCommands.exit();
                            return;

                        case "help":
                            InternalCommands.help();
                            break;

                        default:
                            handleUnknownCommand(command);
                            break;
                    }
                }
            }
        }
    }

    private static int levenshteinDistance(String a, String b)
    {
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        // Initialize the distance array
        for (int i = 0; i <= a.length(); i++)
        {
            dp[i][0] = i; // Deleting all characters from a
        }
        for (int j = 0; j <= b.length(); j++)
        {
            dp[0][j] = j; // Inserting all characters to a
        }

        // Calculate the distances
        for (int i = 1; i <= a.length(); i++)
        {
            for (int j = 1; j <= b.length(); j++)
            {
                int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[a.length()][b.length()];
    }

    private static void handleUnknownCommand(String command)
    {
        System.out.println("Unknown command: " + command);
        System.out.println("Type 'help' for a list of available commands.");

        if (!command.isEmpty())
        {
            String[] knownCommands = {"exit", "help", "pwd", "cd", "ls", "ls -a", "ls -r", "mkdir", "rmdir", "touch", "mv", "rm", "cat", ">", ">>", "|"};
            int minDistance = Integer.MAX_VALUE;
            String closestCommand = null;

            // Find the closest command based on the Levenshtein distance
            for (String knownCommand : knownCommands)
            {
                int distance = levenshteinDistance(command, knownCommand);
                if (distance < minDistance)
                {
                    minDistance = distance;
                    closestCommand = knownCommand;
                }
            }

            // Suggest the closest command if the distance is reasonable
            if (closestCommand != null && minDistance <= 2)
            { // You can adjust this threshold
                System.out.println("Did you mean: " + closestCommand + "?");
            }
        }
    }
}
