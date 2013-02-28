package com.magi.image;

import java.io.File;
import java.io.FileFilter;

/**
 * File filter used to accept image file types and directories.
 *
 * @author patkins
 */
public class ImageFileFilter implements FileFilter {

    private String[] supportedExtensions;

    public ImageFileFilter() {
        // Just set the default
        this.supportedExtensions = new String[] { ".jpg", ".jpeg", ".gif" };
    }

    public ImageFileFilter(String[] supportedExtensions) {
        this.supportedExtensions = supportedExtensions;
    }

    public void setSupportedExtensions(String[] ext) {
        supportedExtensions = ext;
    }

    public String[] getSupportedExtensions() {
        return supportedExtensions;
    }

    public boolean accept(File file) {
        if (file.isDirectory())
            return true;

        String name = file.getName().toLowerCase();

        for (int i = 0; i < supportedExtensions.length; i++) {
            if (name.endsWith(supportedExtensions[i]))
                return true;
        }

        return false;
    }
}
