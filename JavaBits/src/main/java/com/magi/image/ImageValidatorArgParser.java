package com.magi.image;

import java.io.PrintStream;

public class ImageValidatorArgParser {

    private String  path = null;
    private boolean includeSubdirs = false;
    private boolean inError = false;

    /**
     * Handle default ImageValidator command line arguments:
     *
     * ImageValidator [-s] [ path ]
     *
     * @param args command line args
     * @see   #getPath()
     * @see   #getIncludeSubdirs()
     */
    public ImageValidatorArgParser(String[] args) {

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

        if (path == null) {
            path = ".";
        }
    }

    public void printUsage(PrintStream out, String appName) {
        out.println("\nUSAGE: " + appName + " [-s] [ path ]\n");
        out.println("\t\t-s\tinclude sub-directories.\n");
    }

    public String getPath() {
        return path;
    }

    public boolean getIncludeSubdirs() {
        return includeSubdirs;
    }

    public boolean isInError() {
        return inError;
    }

    protected boolean isValidArgLength(String[] args) {
        return (args.length <= 2);
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
        path = arg;
        return true;
    }
}
