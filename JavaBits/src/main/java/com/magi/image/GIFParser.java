package com.magi.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.magi.io.FileScanner;

public class GIFParser extends FileScanner {

    public GIFParser(String path, boolean includeSubdirs) {
        super( path, new ImageFileFilter(new String[] { ".gif" } ),
               true, includeSubdirs );
    }

    /**
     * Scan (or process) a file.
     *
     * @param afile the file to process.
     */
    protected void scanFile(File afile) {
        System.out.println(afile.toString() + ":");
        boolean valid = true;
        long fileLength = afile.length();
        System.out.println("\tFile length: " + fileLength + " bytes");

        if (fileLength == 0) {
            valid = false;
            System.out.println("\tInvalid. Zero length file found.");
        }
        else if (fileLength < 12) {
            // Minimum header length
            valid = false;
            System.out.println("\tInvalid. File is truncated (incomplete).");
        }

        try {
          RandomAccessFile raFile = new RandomAccessFile(afile, "r");
          byte[] id = new byte[6];
          raFile.read(id);

          // Verify GIF id marker
          String idStr = new String(id);
          if (valid && idStr.equals("GIF87a") || idStr.equals("GIF89a")) {

              System.out.println("\t" + idStr + " version GIF image.");

              // Image dimensions
              int b1 = readLengthBytesLSBF(raFile);
              int b2 = readLengthBytesLSBF(raFile);
              System.out.println("\tX: " + b1);
              System.out.println("\tY: " + b2);

              if (b1 < 1 || b2 < 1 || b1 > 5000 || b2 > 5000) {
                  // It's hard to validate image dimensions, but they must be
                  // within a sensible value range.
                  valid = false;
                  System.out.println("\tInvalid. Image dimensions are not valid values.");
              }

              if (valid) {
                  // Colour info
                  b1 = raFile.read();
                  if ((b1 & 0x80) == 0x80)
                      System.out.println("\tGlobal colour map follows descriptor.");
                  else
                      System.out.println("\tGlobal colour map does not follow descriptor.");

                  int cr = ((b1 & 0x70) >> 4) + 1;
                  System.out.println("\tBits of colour resolution: " + cr);

                  if ((b1 & 0x08) == 0)
                      System.out.println("\tVerified that bit 3/byte 5 is zero.");
                  else // non-fatal, since some images don't conform
                      System.out.println("\tWarning! Bit 3/byte 5 should be zero, but is not.");
              }

              if (valid) {
                  int bpp = (b1 & 0x07) + 1;
                  System.out.println("\tNumber of bits per pixel: " + bpp);

                  b1 = raFile.read();
                  System.out.println("\tBackground colour palette index: " + b1);
              }

              if (valid)
                  System.out.println("\tValid.");
          }
          else {
              if (valid) {
                  // GIF id not found - NOT a GIF formatted file.
                  if ((int)id[0] == 0xFF && (int)id[1] == 0xD8) { // 'JPEG'
                      System.out.println("\tInvalid. Not a GIF file, but a JPEG file with the wrong extension.");
                  }
                  else if ( (int)id[0] == 0x89 &&
                             new String(id, 1, 3).equals("PNG") &&
                            (int)id[4] == 0x0D && (int)id[5] == 0x0A ) { // 'PNG'
                      System.out.println("\tInvalid. Not a GIF file, but a PNG file with the wrong extension.");
                  }
                  else
                      System.out.println("\tInvalid. GIF Id Marker not found. Not a GIF file.");
              }
          }

          raFile.close();
        }
        catch (FileNotFoundException ex) {
            System.out.println(afile.toString() + ":");
            ex.printStackTrace();
        }
        catch (IOException ex) {
            System.out.println(afile.toString() + ":");
            ex.printStackTrace();
        }
    }

    /** Read and convert the two-byte length field */
    private int readLengthBytes(RandomAccessFile raFile) throws IOException {
        return readTwoByteValue(raFile, false);
    }

    /** Read and convert the two-byte length field - Least sig bits first */
    private int readLengthBytesLSBF(RandomAccessFile raFile) throws IOException {
        return readTwoByteValue(raFile, true);
    }

    /** Read and convert the two-byte value field */
    private int readTwoByteValue(RandomAccessFile raFile, boolean lsbFirst) throws IOException {
        int b1 = raFile.read();
        int b2 = raFile.read();

        if (lsbFirst)
            return (b2 << 8 | b1);
        else
            return (b1 << 8 | b2);
    }

    /** Return a String containing the current file location (in hex) */
    private String loc(long lc) throws IOException {
        return longToHex(lc, true);
    }

    private String longToHex(long value, boolean prefix) {
        String hex = Long.toHexString(value).toUpperCase();
        if (hex.length() % 2 == 1)
            hex = "0" + hex;

        if (prefix)
            hex = "0x" + hex;

        return hex;
    }

    /**
     * Java runtime entry point.
     *
     * @param args
     */
    public static void main(String[] args) {

        ImageValidatorArgParser parser = new ImageValidatorArgParser(args);

        if (parser.isInError()) {
            parser.printUsage(System.out, "GIFParser");
            return;
        }

        new GIFParser(parser.getPath(), parser.getIncludeSubdirs())
           .scan();
    }
}
