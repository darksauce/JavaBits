/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.md5;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 hash checksum generator for supplied File.
 *
 * @author Paul Atkinson
 */
public class MD5Checksum
{
    private static final int BUFFER_SIZE = 1024;

    private File file = null;

    /**
     * Create a MD5 hash checksum generator with this file.
     */
    public MD5Checksum(File file)
    {
        this.file = file;
    }

    /**
     * Set this file as current data source.
     *
     * @param file the file to read when generating checksum.
     */
    public void setFile(File file)
    {
        this.file = file;
    }

    /**
     * Generate a MD5 hash checksum as a raw byte array.
     *
     * @return a byte[] containing the checksum.
     * @throws FileNotFoundException, IOException if an error occurs.
     */
    public byte[] generateByteArray() throws FileNotFoundException, IOException
    {
        MessageDigest digest = null;
        InputStream   in     = new FileInputStream(file);
        byte[]        buf    = new byte[BUFFER_SIZE];
        int           numRead;

        try
        {
            digest = MessageDigest.getInstance("MD5");

            do
            {
                numRead = in.read(buf);
                if (numRead > 0)
                {
                    digest.update(buf, 0, numRead);
                }
            }
            while (numRead != -1);
            return digest.digest();
        }
        catch (NoSuchAlgorithmException ex)
        {
            throw new IOException("MD5 algorithm cannot be found during checksum generation.");
        }
        finally
        {
            in.close();
        }
    }

    /**
     * Generate a MD5 hash checksum as a lower-case 32 character hex encoded String.
     *
     * @return a byte[] containing the checksum.
     * @throws FileNotFoundException, IOException if an error occurs.
     */
    public String generate() throws FileNotFoundException, IOException
    {
        byte[] chk = generateByteArray();

        String result = "";
        for (int i = 0; i < chk.length; i++)
        {
            result += Integer.toString( ( chk[i] & 0xff ) + 0x100, 16).substring( 1 );
        }

        return result;
    }
}
