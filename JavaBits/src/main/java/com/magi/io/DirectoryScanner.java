/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;

/**
 * Scan a directory of files, which may contain other sub-directories of files.
 * This class is designed for easy extension by application specific sub-classes.
 *
 * Subclasses should implement the processDirectory(File) and processFile(File)
 * abstract methods.
 *
 * @author patkins
 */
public abstract class DirectoryScanner
{
    protected File       directory;
    protected boolean    subDirs;
    protected FileFilter fileFilter = null;

    /**
     * Create a directory scanner.
     *
     * @param directory  the directory root to begin scanning from.
     * @param fileFilter an (optional) FileFilter to limit directories/files processed or null.
     * @param subDirs    true if sub-directories should also be processed recursively.
     */
    public DirectoryScanner(File directory, FileFilter fileFilter, boolean subDirs)
    {
        this.directory  = directory;
        this.fileFilter = fileFilter;
        this.subDirs    = subDirs;
    }

    /**
     * Set an (optional) FileFilter to limit directories/files processed.
     *
     * @param fileFilter the FileFilter instance.
     */
    public void setFileFilter(FileFilter fileFilter)
    {
        this.fileFilter = fileFilter;
    }

    /**
     * START the directory and file scanning.
     *
     * @throws FileNotFoundException if the directory supplied does not exist or
     *         is not a valid directory.
     */
    public void scan() throws FileNotFoundException
    {
        if (directory.exists())
        {
            if (directory.isDirectory())
            {
                if (processDirectory(directory))
                {
                    scanDirectory(directory);
                }
            }
            else
                throw new FileNotFoundException("The filename specified is not a directory: " + directory);
        }
        else
            throw new FileNotFoundException("The directory specified does not exist: " + directory);
    }

    /**
     * Subclasses MUST override this method to perform custom processing on each
     * directory found.
     *
     * @param  directory the directory found to process.
     * @return true if the process should continue on, and false to abort.
     */
    public abstract boolean processDirectory(File directory);

    /**
     * Subclasses MUST override this method to perform custom processing on each
     * file found.
     *
     * @param  file the file found to process.
     * @return true if the process should continue on, and false to abort.
     */
    public abstract boolean processFile(File file);

    /**
     * Recursive directory and file scanning processor.
     *
     * @param dir the directory to scan.
     */
    protected void scanDirectory(File dir)
    {
        boolean ok    = true;
        File[]  files = ( fileFilter != null ? dir.listFiles(fileFilter) :
                                               dir.listFiles() );

        // Process all the FILES first
        for (int i = 0; i < files.length && ok; i++)
        {
            if (files[i].isFile())
                ok = processFile(files[i]);
        }

        // Process all the SUB-DIRECTORIES
        for (int i = 0; i < files.length && ok; i++)
        {
            if (files[i].isDirectory())
            {
                ok = processDirectory(files[i]);

                if (ok && subDirs) // recurse downward
                    scanDirectory(files[i]);
            }
        }
    }
}
