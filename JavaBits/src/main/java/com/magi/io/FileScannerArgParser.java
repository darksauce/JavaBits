package com.magi.io;

import java.io.PrintStream;

public class FileScannerArgParser {

    private String  path = null;
    private String  pattern = "*";
    private boolean sorted = false;
    private boolean includeSubdirs = false;
    private boolean inError = false;

    /**
     * Handle default FileScanner command line arguments:
     *
     * FileScanner [-s] [-a] [<path-spec> [<pattern>]]
     *
     * @param args command line args
     * @see   #getPath()
     * @see   #getSorted()
     * @see   #getIncludeSubdirs()
     * @see   #getPattern()
     */
    public FileScannerArgParser(String[] args) {

        inError = !isValidArgLength(args);

        if (!inError) {
            for (int i = 0; i < args.length; i++) {
                boolean success;
                if (args[i].charAt(0) == '-')
                    success = parseSwitch(args[i]);
                else
                    success = parseArg(args[i]);

                if (!success)
                    inError = true;
            }
        }

        if (path == null)
            path = ".";
    }

    public void printUsage(PrintStream out, String appName) {
        out.println("\nUSAGE: " + appName + " [-s] [-a] [<path-spec> [<pattern>]]\n");
    }

    public String getPath() {
        return path;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean getSorted() {
        return sorted;
    }

    public boolean getIncludeSubdirs() {
        return includeSubdirs;
    }

    public boolean isInError() {
        return inError;
    }

    protected boolean isValidArgLength(String[] args) {
        return (args.length <= 4);
    }

    /**
     * Parse a single argument that starts with "-",
     * ie. a command line switch.
     *
     * @param arg the switch argument.
     */
    protected boolean parseSwitch(String arg) {

        if (arg.length() > 1) {
            switch (arg.charAt(1)) {
                case 's':
                    includeSubdirs = true;
                    break;
                case 'a': // alphabetic sorting
                    sorted = true;
                    break;
            }

            return true;
        }

        return false;
    }

    /**
     * Parse an argument that does not start with "-",
     * ie. is not a switch.
     *
     * @param arg the non-switch argument.
     */
    protected boolean parseArg(String arg) {
        if (path == null)
            path = arg;
        else
            pattern = arg;

        return true;
    }
}
