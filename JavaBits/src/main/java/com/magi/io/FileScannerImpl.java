package com.magi.io;

import java.io.File;
import java.io.FileFilter;

public class FileScannerImpl extends FileScanner {

    public FileScannerImpl( String path, String pattern, boolean sorted,
                          boolean includeSubs ) {
        super(path, pattern, sorted, includeSubs);
    }

    /**
     * Scan (or process) a file.
     *
     * @param afile the file to process.
     */
    protected void scanFile(File afile) {
        super.scanFile(afile);
    }

    public static void main(String[] args) {

        FileScannerArgParser parser = new FileScannerArgParser(args);

        if (parser.isInError()) {
            parser.printUsage(System.out, "MyFileScanner");
            return;
        }

        FileScannerImpl scan = new FileScannerImpl( parser.getPath(),
                                                    parser.getPattern(),
                                                    parser.getSorted(),
                                                    parser.getIncludeSubdirs() );
        scan.scan();
    }
}
