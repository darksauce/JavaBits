package com.magi.io;

import java.io.File;
import java.util.Comparator;

public class FileScannerSort implements Comparator {

    public int compare(Object o1, Object o2) {
        File f1 = (File) o1;
        File f2 = (File) o2;

        // Ensure directories are sorted to the bottom
        if (f1.isDirectory() && !f2.isDirectory())
            return 1;
        else if (f2.isDirectory() && !f1.isDirectory())
            return -1;

        return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
    }
}
