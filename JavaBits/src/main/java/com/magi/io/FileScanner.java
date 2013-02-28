package com.magi.io;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Scans a directory of files, and sub-directories of files if required.
 * Files can be matched with a DOS-style wildcard String, or a customized
 * FileFilter of your choice.
 *
 * The easiest way to implement the FileScanner is to extend this class, and
 * override the scanFile(File) method.
 *
 * @author Paul Atkinson
 */
public class FileScanner {

    protected File       path;
    protected boolean    sorted         = false;
    protected boolean    includeSubdirs = false;
    protected FileFilter fileFilter     = null;
    protected Comparator fileSorter     = null;

    /**
     * Create a File Scanner for the directory specified.
     * Will scan all files in this directory, but not sub-directories.
     *
     * @param path the directory to scan.
     */
    public FileScanner(String path) {
        super();

        this.path       = new File(path);
        this.fileFilter = new WildcardFileFilter("*", includeSubdirs);
        this.fileSorter = new FileScannerSort();
    }

    /**
     * Create a File Scanner for the directory specified.
     * Uses the DOS-style filePattern passed in, and will include sub-directories
     * if specified.
     *
     * @param path the directory to scan.
     * @param filePattern the DOS-style file pattern to match files on.
     * @param sorted true if the file list should be alphabetically sorted.
     * @param includeSubdirs true if sub-directories should be scanned recursively.
     */
    public FileScanner( String path, String filePattern, boolean sorted,
                        boolean includeSubdirs ) {
        this.path           = new File(path);
        this.fileFilter     = new WildcardFileFilter(filePattern, includeSubdirs);
        this.sorted         = sorted;
        this.includeSubdirs = includeSubdirs;
        this.fileSorter     = new FileScannerSort();
    }

    /**
     * Create a File Scanner for the directory specified.
     * Uses the custom FileFilter object passed in, and will include
     * sub-directories if specified.
     *
     * @param path the directory to scan.
     * @param fileFilter a custom supplied FileFilter object for file matching.
     * @param sorted true if the file list should be alphabetically sorted.
     * @param includeSubdirs true if sub-directories should be scanned recursively.
     */
    public FileScanner(String path, FileFilter fileFilter, boolean sorted, boolean includeSubdirs) {
        this.path           = new File(path);
        this.fileFilter     = fileFilter;
        this.sorted         = sorted;
        this.includeSubdirs = includeSubdirs;
        this.fileSorter     = new FileScannerSort();
    }

    /**
     * Returns true if sub-directories are being scanned recursively.
     *
     * @return true if sub-directories will be scanned.
     */
    public boolean getIncludeSubdirs() {
        return includeSubdirs;
    }

    /**
     * Returns true if the file list should be sorted.
     *
     * @return true if sorted.
     */
    public boolean getSorted() {
        return sorted;
    }

    /**
     * Set to true if the file list should be alphabetically sorted.
     *
     * @param sorted true if sorted.
     */
    public void setSorted(boolean sorted) {
        this.sorted = sorted;
    }

    /**
     * Returns the Comparator used for file sorting (if sorting is enabled).
     *
     * @return the Comparator instance.
     */
    public Comparator getFileSorter() {
        return fileSorter;
    }

    /**
     * Set a customized file sorter to use (if sorting is enabled).
     *
     * @param sorter the custom instance of Comparator to use.
     */
    public void setFileSorter(Comparator sorter) {
        this.fileSorter = sorter;
    }

    /**
     * Sets a customized file filter to use.
     *
     * @param customFilter custom filter.
     */
    public void setFileFilter(FileFilter customFilter) {
        this.fileFilter = customFilter;
    }

    /**
     * Returns the current file filter in use.
     *
     * @return a FileFilter instance.
     */
    public FileFilter getFileFilter() {
        return fileFilter;
    }

    /**
     * Set if sub-directories should be scanned recursively.
     *
     * @param includeSubdirs true to scan sub-directories, and false if not.
     */
    public void setIncludeSubdirs(boolean includeSubdirs) {
        this.includeSubdirs = includeSubdirs;

        if (fileFilter instanceof WildcardFileFilter) {
            ((WildcardFileFilter)fileFilter).setIncludeSubdirs(includeSubdirs);
        }
    }

    /**
     * Sets the DOS-style file pattern "wildcard", for matching files.
     *
     * @param pattern the file match pattern.
     */
    public void setFilePattern(String pattern) {
        if (fileFilter instanceof WildcardFileFilter) {
            ((WildcardFileFilter)fileFilter).setFilePattern(pattern);
        }
        else {
            fileFilter = new WildcardFileFilter(pattern, includeSubdirs);
        }
    }

    /**
     * Returns the file matching pattern if any, or null if there is none.
     *
     * @return pattern String or null.
     */
    public String getFilePattern() {
        if (fileFilter instanceof WildcardFileFilter) {
            return ((WildcardFileFilter)fileFilter).getFilePattern();
        }

        return null;
    }

    /**
     * SCAN the directory(ies).
     */
    public void scan() {
        scanDirectory(path, true);
    }

    /**
     * Scan a directory for files and sub-directories.
     *
     * @param directory   the directory path to scan.
     * @param includeSubs scan recursively.
     */
    protected void scanDirectory(File directory, boolean includeSubs) {
        if (includeSubs) {
            File[] dirFiles = directory.listFiles(fileFilter);
            if (dirFiles != null) {
                if (sorted)
                    Arrays.sort(dirFiles, fileSorter);

                scanFiles(dirFiles); // recurse downward
            }
        }
    }

    /**
     * Scan the array of File objects passed in.
     *
     * @param files an array of File objects to scan.
     */
    protected void scanFiles(File[] files) {

        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory())
                scanDirectory(files[i], includeSubdirs);
            else
                scanFile(files[i]);
        }
    }

    /**
     * Scan (or process) a file.
     *
     * @param afile the file to process.
     */
    protected void scanFile(File afile) {
        // Do some stuff with each file here
        System.out.println( afile.toString() );
    }
}
