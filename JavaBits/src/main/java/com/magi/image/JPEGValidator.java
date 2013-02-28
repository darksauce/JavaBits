package com.magi.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class JPEGValidator {

    /** Image structure is valid */
    public static final int JV_VALID = 0;

    /** File is truncated - segment length overruns end of file */
    public static final int JV_TRUNCATED = 1;

    /** JFIF segment does not have correct ID string */
    public static final int JV_NO_JFIF_ID = 2;

    /** Unknown JPEG segment identifier found */
    public static final int JV_UNKNOWN_SEGMENT = 3;

    /** No JPEG segment identifier found */
    public static final int JV_NO_SEGMENT = 4;

    /** No End of Image marker was found at the end of the file */
    public static final int JV_NO_EOI = 5;

    /** File is zero bytes in length */
    public static final int JV_ZERO_LENGTH = 6;

    /** File is not a JPEG - but a BMP (Windows Bitmap) file */
    public static final int JV_BMP_FILE = 7;

    /** File is not a JPEG - but a GIF (GIF87a) file */
    public static final int JV_GIF_87A_FILE = 8;

    /** File is not a JPEG - but a GIF (GIF89a) file */
    public static final int JV_GIF_89A_FILE = 9;

    /** File is not a JPEG - does not have Start of Image marker */
    public static final int JV_NO_SOI = 10;

    /** The file specified does not exist */
    public static final int JV_NO_FILE = 11;

    /** Experienced an IOException while reading the file contents */
    public static final int JV_IO_ERROR = 12;

    /**
     * Validate a JPEG file, by examining the internal data structure,
     * and verifying it's contents.
     *
     * @param  afile the JPEG file to scan and validate.
     * @return one of the JV int constants defined by this class, ie JV_VALID.
     */
    public static int validateFile(File afile) {

        int  result     = JV_VALID;
        long fileLength = afile.length();

        try {
          RandomAccessFile raFile = new RandomAccessFile(afile, "r");
          boolean inHeader = true;
          long    lc = raFile.getFilePointer();
          int     b1 = raFile.read();
          int     b2 = raFile.read();

          // Verify SOI (Start of Image Marker)
          if (fileLength > 0 && b1 == 0xFF && b2 == 0xD8) {

              while (inHeader && result == JV_VALID) {
                  lc = raFile.getFilePointer();
                  b1 = raFile.read();
                  b2 = raFile.read();

                  long segStart = raFile.getFilePointer();
                  int  length   = readLengthBytes(raFile);

                  if (segStart + length > fileLength) {
                      result = JV_TRUNCATED;
                  }

                  if (result == JV_VALID && b1 == 0xFF) {
                      switch (b2) {
                          case 0xC0: // SOF(n) (Start of Frame) Markers
                          case 0xC1:
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
                              break;
                          case 0xC4: // DHT (Define Huffman Table Marker)
                              break;
                          case 0xC8:
                              break;
                          case 0xCC:
                              break;
                          case 0xD0:
                          case 0xD1:
                          case 0xD2:
                          case 0xD3:
                          case 0xD4:
                          case 0xD5:
                          case 0xD6:
                          case 0xD7:
                              break;
                          case 0xDA: // SOS (Start of Scan Marker)
                              inHeader = false;
                              break;
                          case 0xDB: // DQT (Define Quantization Table Marker)
                              break;
                          case 0xDC: // DNL (Define Number of Lines Marker)
                              break;
                          case 0xDD: // DRI (Define Restart Interval Marker)
                              break;
                          case 0xDE: // DHP (Define Hierarchical Progression Marker)
                              break;
                          case 0xDF: // EXP (Expand Reference Component Marker)
                              break;
                          case 0xE0: { // JFIF (JPEG File Interchange Format Marker) - APP0
                              lc = raFile.getFilePointer();
                              byte[] id  = new byte[5];
                              raFile.read(id);
                              if (new String(id, 0, 4).equals("JFIF") && id[4] == 0x00) {
                                  // ok
                              }
                              else {
                                  result = JV_NO_JFIF_ID;
                                }
                              }
                              break;
                          case 0xE1: // EXIF (Exchangeable Image File Format Marker) - APP1
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
                              break;
                          case 0xFE: // COM (Commment Marker)
                              break;
                          default:
                              result = JV_UNKNOWN_SEGMENT;
                              break;
                      }

                      if (result == JV_VALID)
                          raFile.seek(segStart + length);
                  }
                  else {
                      if (result == JV_VALID) {
                          result = JV_NO_SEGMENT;
                      }
                  }
              }

              if (result == JV_VALID) {
                  lc = fileLength - 4;
                  raFile.seek(lc);
                  byte[] end = new byte[4];
                  raFile.read(end);

                  if ( !(end[0] == (byte)0xFF && end[1] == (byte)0xD9) &&
                       !(end[1] == (byte)0xFF && end[2] == (byte)0xD9) &&
                       !(end[2] == (byte)0xFF && end[3] == (byte)0xD9) ) {
                    // MAY indicate a file trunction
                    result = JV_NO_EOI;
                  }
              }
          }
          else {
              if (fileLength == 0)
                  result = JV_ZERO_LENGTH;
              else {
                  // SOI not found - NOT a JPEG formatted file.
                  if (b1 == 0x42 && b2 == 0x4D) { // 'BM'
                      result = JV_BMP_FILE;
                  }
                  else if (b1 == 0x47 && b2 == 0x49) { // 'GI'
                      byte[] prefix = new byte[6];
                      raFile.seek(0L);
                      raFile.read(prefix);
                      if (new String(prefix).equals("GIF87a"))
                          result = JV_GIF_87A_FILE;
                      else if (new String(prefix).equals("GIF89a"))
                          result = JV_GIF_89A_FILE;
                      else
                          result = JV_NO_SOI;
                  }
                  else
                      result = JV_NO_SOI;
              }
          }

          raFile.close();
        }
        catch (FileNotFoundException ex) {
            result = JV_NO_FILE;
        }
        catch (IOException ex) {
            result = JV_IO_ERROR;
        }

        return result;
    }

    /**
     * Convert the JV validation error code to an error String.
     * If JV_VALID is passed in, null is returned.
     *
     * @param  jvErrorCode the return code from validateFile(File).
     * @return a String error message.
     */
    public static String getValidationError(int jvErrorCode) {
        switch (jvErrorCode) {
            case JV_TRUNCATED:
                 return "JPEG file is truncated";
            case JV_NO_JFIF_ID:
                 return "JFIF segment does not have the correct ID string";
            case JV_UNKNOWN_SEGMENT:
                 return "Unknown JPEG segment was found";
            case JV_NO_SEGMENT:
                 return "No JPEG segment was found at location";
            case JV_NO_EOI:
                 return "No EOI marker was found at the end of the file";
            case JV_ZERO_LENGTH:
                 return "File is zero bytes in length";
            case JV_BMP_FILE:
                 return "File is not a JPEG, but a BMP (Windows Bitmap) image";
            case JV_GIF_87A_FILE:
                 return "File is not a JPEG, but a GIF (GIF87a) image";
            case JV_GIF_89A_FILE:
                 return "File is not a JPEG, but a GIF (GIF89a) image";
            case JV_NO_SOI:
                 return "File is not a JPEG, no SOI segment was found";
            case JV_NO_FILE:
                 return "File not found";
            case JV_IO_ERROR:
                 return "IO error occurred while reading the file";
        }
        return null;
    }

    /** Read and convert the two-byte length field */
    private static int readLengthBytes(RandomAccessFile raFile) throws IOException {
        return readTwoByteValue(raFile);
    }

    /** Read and convert the two-byte value field */
    private static int readTwoByteValue(RandomAccessFile raFile) throws IOException {
        int b1 = raFile.read();
        int b2 = raFile.read();
        return (b1 << 8 | b2);
    }

    /**
     * Java runtime entry point.
     *
     * @param args
     */
    public static void main(String[] args) {

        File file  = new File("\\FBZ602_1.jpg");
        int result = JPEGValidator.validateFile(file);

        if (result == JPEGValidator.JV_VALID)
            System.out.println(file.getName() + ": VALID.");
        else {
            System.out.print(file.getName() + ": INVALID - ");
            System.out.println( JPEGValidator.getValidationError(result) );
        }
    }
}
