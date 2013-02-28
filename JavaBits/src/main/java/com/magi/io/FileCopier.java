/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileCopier
{
    private static final int BUFFER_SIZE = 1048576; // 1 MB

    public FileCopier() { }

    public static void copyFile(String from, String to) throws FileNotFoundException, IOException
    {
       int        read;
       char[]     buff = new char[BUFFER_SIZE];
       FileReader rd   = new FileReader(from);
       FileWriter wt   = new FileWriter(to);

       while ((read = rd.read(buff, 0, BUFFER_SIZE)) > 0)
          wt.write(buff, 0, read);

       wt.close();
       rd.close();
    }

    public static void moveFile(String from, String to) throws FileNotFoundException, IOException
    {
      copyFile(from, to);

      File fromFile = new File(from);
      if (fromFile.exists())
      {
        if (!fromFile.delete())
          throw new IOException("Unable to delete source file: " + from);
      }
      else
        throw new IOException("Source file does not exist: " + from);
    }
}
