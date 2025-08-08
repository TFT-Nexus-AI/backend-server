# Using Gemini CLI for Large Codebase Analysis

When analyzing large codebases or multiple files that might exceed context limits, 
use the Gemini CLI with its massive context window. Use `gemini -p` to leverage 
Google Gemini's large context capacity.

## File and Directory Inclusion Syntax

Use the `@` syntax to include files and directories in your Gemini prompts. 
The paths should be relative to WHERE you run the gemini command.
