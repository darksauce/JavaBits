package com.magi.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.magi.io.FileScanner;

public class JPEGParser extends FileScanner {

    public JPEGParser(String path, boolean includeSubdirs) {
        super( path, new ImageFileFilter(new String[] { ".jpg", ".jpeg" } ),
               true, includeSubdirs );
    }

    /**
     * Scan (or process) a file.
     *
     * @param afile the file to process.
     */
    protected void scanFile(File afile) {
        System.out.println(afile.toString() + ":");
        long fileLength = afile.length();
        System.out.println("\tFile length: " + fileLength + " bytes");
        try {
          RandomAccessFile raFile = new RandomAccessFile(afile, "r");
          boolean inHeader = true;
          long lc = raFile.getFilePointer();
          int  b1 = raFile.read();
          int  b2 = raFile.read();

          // Verify SOI (Start of Image Marker)
          if (fileLength > 0 && b1 == 0xFF && b2 == 0xD8) {
              System.out.println("\tVerified SOI (Start of Image) Marker [0xFFD8] at location " + loc(lc));

              boolean valid = true;
              while (inHeader && valid) {
                  lc = raFile.getFilePointer();
                  b1 = raFile.read();
                  b2 = raFile.read();

                  long segStart = raFile.getFilePointer();
                  int  length   = readLengthBytes(raFile);

                  if (segStart + length > fileLength) {
                      valid = false;
                      System.out.println("\tInvalid. File appears to be truncated (incomplete).");
                  }

                  if (valid && b1 == 0xFF) {
                      switch (b2) {
                          case 0xC0: { // SOF (Start of Frame Marker)
                              System.out.println("\tVerified SOF (Start of Frame) Marker [0xFFC0] at location " + loc(lc));
                              b1 = raFile.read();
                              System.out.println("\tSample precision: " + b1 + " bits.");
                              int value = readTwoByteValue(raFile);
                              System.out.println("\tX: " + readTwoByteValue(raFile));
                              System.out.println("\tY: " + value);
                              value = raFile.read();
                              System.out.println("\tColour components: " + value);
                              break;
                          }
                          case 0xC1: // SOF(n) Markers
                          case 0xC2:
                          case 0xC3:
                          case 0xC5:
                          case 0xC6:
                          case 0xC7:
                          case 0xC9:
                          case 0xCA:
                          case 0xCB:
                          case 0xCD:
                          case 0xCE:
                          case 0xCF:
                              System.out.println("\tVerified SOF" + (b2 - (int)0xC0) + " Marker [0xFF" + longToHex(b2,false) + "] at location " + loc(lc));
                              break;
                          case 0xC4: // DHT (Define Huffman Table Marker)
                              System.out.println("\tVerified DHT (Define Huffman Table) Marker [0xFFC4] at location " + loc(lc));
                              break;
                          case 0xC8:
                              System.out.println("\tVerified JPG Extension Marker [0xFFC8] at location " + loc(lc));
                              break;
                          case 0xCC:
                              System.out.println("\tVerified DAC (Define Arithmetic Coding Marker) [0xFFCC] at location " + loc(lc));
                              break;
                          case 0xD0:
                          case 0xD1:
                          case 0xD2:
                          case 0xD3:
                          case 0xD4:
                          case 0xD5:
                          case 0xD6:
                          case 0xD7:
                              System.out.println("\tVerified RST" + (b2 - (int)0xD0) + " Marker [0xFF" + longToHex(b2,false) + "] at location " + loc(lc));
                              break;
                          case 0xDA: // SOS (Start of Scan Marker)
                              System.out.println("\tVerified SOS (Start of Scan) Marker [0xFFDA] at location " + loc(lc));
                              int value = raFile.read();
                              System.out.println("\tScan components: " + value);
                              System.out.println("\t... image data (starts at location " + loc(segStart + length) + ") ...");
                              inHeader = false;
                              break;
                          case 0xDB: // DQT (Define Quantization Table Marker)
                              System.out.println("\tVerified DQT (Define Quantization Table) Marker [0xFFDB] at location");
                              System.out.println("\t   " + loc(lc));
                              System.out.println("\tQ-table length: " + length);
                              break;
                          case 0xDC: // DNL (Define Number of Lines Marker)
                              System.out.println("\tVerified DNL (Define Number of Lines) Marker [0xFFDC] at location " + loc(lc));
                              break;
                          case 0xDD: // DRI (Define Restart Interval Marker)
                              System.out.println("\tVerified DRI (Define Restart Interval) Marker [0xFFDD] at location");
                              System.out.println("\t   " + loc(lc));
                              break;
                          case 0xDE: // DHP (Define Hierarchical Progression Marker)
                              System.out.println("\tVerified DHP (Define Hierarchical Progression) Marker [0xFFDE] at location " + loc(lc));
                              break;
                          case 0xDF: // EXP (Expand Reference Component Marker)
                              System.out.println("\tVerified EXP (Expand Reference Component) Marker [0xFFDF] at location " + loc(lc));
                              break;
                          case 0xE0: { // JFIF (JPEG File Interchange Format Marker) - APP0
                              System.out.println("\tVerified JFIF (JPEG File Interchange Format) Marker [0xFFE0] at");
                              System.out.println("\t   location " + loc(lc));
                              lc = raFile.getFilePointer();
                              byte[] id  = new byte[5];
                              raFile.read(id);
                              if (new String(id, 0, 4).equals("JFIF") && id[4] == 0x00) {
                                  System.out.println("\tVerified \"JFIF\" id string at location " + loc(lc));
                                  b1 = raFile.read();
                                  b2 = raFile.read();
                                  System.out.println("\tJFIF format version: " + b1 + "." + b2);
                              }
                              else {
                                  valid = false;
                                  System.out.println("\tInvalid. \"JFIF\" id string not found in segment.");
                                }
                              }
                              break;
                          case 0xE1: { // EXIF (Exchangeable Image File Format Marker) - APP1
                              System.out.println("\tVerified EXIF (Exchangeable Image File Format) Marker [0xFFE1] at");
                              System.out.println("\t   location " + loc(lc));
                              lc = raFile.getFilePointer();
                              byte[] id  = new byte[5];
                              raFile.read(id);
                              System.out.println("\tEXIF id string: " + new String(id, 0, 4));
                            }

                            //
                            // Other EXIF data can be extracted here ...
                            //

                            break;
                          case 0xE2: // APP2  (Application Marker)
                          case 0xE3: // APP3  (Application Marker)
                          case 0xE4: // APP4  (Application Marker)
                          case 0xE5: // APP5  (Application Marker)
                          case 0xE6: // APP6  (Application Marker)
                          case 0xE7: // APP7  (Application Marker)
                          case 0xE8: // APP8  (Application Marker)
                          case 0xE9: // APP9  (Application Marker)
                          case 0xEA: // APP10 (Application Marker)
                          case 0xEB: // APP11 (Application Marker)
                          case 0xEC: // APP12 (Application Marker)
                          case 0xED: // APP13 (Application Marker)
                          case 0xEE: // APP14 (Application Marker)
                          case 0xEF: // APP15 (Application Marker)
                              System.out.println("\tVerified APP" + (b2 - (int)0xE0) + " (Application Info Marker) [0xFF" + longToHex(b2,false) + "] at location " + loc(lc));
                              break;
                          case 0xF0: // JPG0  (Extension 0  Marker)
                          case 0xF1: // JPG1  (Extension 1  Marker)
                          case 0xF2: // JPG2  (Extension 2  Marker)
                          case 0xF3: // JPG3  (Extension 3  Marker)
                          case 0xF4: // JPG4  (Extension 4  Marker)
                          case 0xF5: // JPG5  (Extension 5  Marker)
                          case 0xF6: // JPG6  (Extension 6  Marker)
                          case 0xF7: // JPG7  (Extension 7  Marker)
                          case 0xF8: // JPG8  (Extension 8  Marker)
                          case 0xF9: // JPG9  (Extension 9  Marker)
                          case 0xFA: // JPG10 (Extension 10 Marker)
                          case 0xFB: // JPG11 (Extension 11 Marker)
                          case 0xFC: // JPG12 (Extension 12 Marker)
                          case 0xFD: // JPG13 (Extension 13 Marker)
                              System.out.println("\tVerified JPG" + (b2 - (int)0xF0) + " (Extension Marker) [0xFF" + longToHex(b2,false) + "] at location " + loc(lc));
                              break;
                          case 0xFE: // COM (Commment Marker)
                              System.out.println("\tVerified COM (Comment) Marker [0xFFFE] at location " + loc(lc));
                              byte[] comment = new byte[length - 2];
                              raFile.read(comment);
                              System.out.println("\tComment: " + new String(comment));
                              break;
                          default:
                              valid = false;
                              System.out.println("\tInvalid. Unknown segment found [0xFF" + longToHex(b2, false) + "] at location " + loc(lc));
                              break;
                      }

                      if (valid)
                          raFile.seek(segStart + length);
                  }
                  else {
                      if (valid) {
                          valid = false;
                          System.out.println("\tInvalid. No JPEG segment marker was found at location " + loc(lc));
                      }
                  }
              }

              if (valid) {
                  lc = fileLength - 4;
                  raFile.seek(lc);
                  byte[] end = new byte[4];
                  raFile.read(end);

                  if (end[0] == (byte)0xFF && end[1] == (byte)0xD9)
                      System.out.println("\tVerified EOI (End of Image) Marker [0xFFD9] at location " + loc(lc));
                  else if (end[1] == (byte)0xFF && end[2] == (byte)0xD9)
                      System.out.println("\tVerified EOI (End of Image) Marker [0xFFD9] at location " + loc(lc+1));
                  else if (end[2] == (byte)0xFF && end[3] == (byte)0xD9)
                      System.out.println("\tVerified EOI (End of Image) Marker [0xFFD9] at location " + loc(lc+2));
                  else {
                      valid = false; // MAY indicate a file trunction
                      System.out.println("\tInvalid. No EOI (End of Image) Marker was found at (or after) location");
                      System.out.println("\t   " + loc(lc));
                  }
              }

              if (valid)
                  System.out.println("\tValid.");
          }
          else {
              if (fileLength == 0)
                  System.out.println("\tInvalid. Zero length file found.");
              else {
                  // SOI not found - NOT a JPEG formatted file.
                  if (b1 == 0x42 && b2 == 0x4D) { // 'BM'
                      System.out.println("\tInvalid. Not a JPEG file, but a BMP (Windows Bitmap) file.");
                  }
                  else if (b1 == 0x47 && b2 == 0x49) { // 'GI'
                      byte[] prefix = new byte[6];
                      raFile.seek(0L);
                      raFile.read(prefix);
                      if (new String(prefix).equals("GIF87a"))
                          System.out.println("\tInvalid. Not a JPEG file, but a GIF (GIF87a) file.");
                      else if (new String(prefix).equals("GIF89a"))
                          System.out.println("\tInvalid. Not a JPEG file, but a GIF (GIF89a) file.");
                  }
                  else
                      System.out.println("\tInvalid. SOI (Start of Image) Marker not found. Not a JPEG file.");
              }
          }

          raFile.close();
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** Read and convert the two-byte length field */
    private int readLengthBytes(RandomAccessFile raFile) throws IOException {
        return readTwoByteValue(raFile);
    }

    /** Read and convert the two-byte value field */
    private int readTwoByteValue(RandomAccessFile raFile) throws IOException {
        int b1 = raFile.read();
        int b2 = raFile.read();
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
            parser.printUsage(System.out, "JPEGParser");
            return;
        }

        new JPEGParser(parser.getPath(), parser.getIncludeSubdirs())
           .scan();
    }
}
