# Custom Command Line Interface (CLI)

This project is a custom Command Line Interface (CLI) implemented in Java. The CLI can execute common system and internal commands such as directory navigation, file manipulation, and process handling. The project mimics basic command-line behaviors, providing functionality similar to Unix-based systems and enhancing command handling with user-friendly feedback and error handling.

## Features

- **System Commands**: Supports commands like `pwd`, `cd`, `ls`, `mkdir`, `rmdir`, `touch`, `mv`, `rm`, `cat`, and redirection operators (`>`, `>>`, `|`).
- **Internal Commands**: Includes `exit` to terminate the CLI and `help` to display available commands with usage instructions.
- **Command Suggestions**: Provides suggestions for unknown commands using Levenshtein distance.
- **Error Handling**: Enhanced feedback for invalid commands or usage errors.
- **Path Handling**: Supports both absolute and relative paths.
