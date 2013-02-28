/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.magi.util.QuoteAwareTokenizer;

/** 
 * Comma Separated Values file format class, extended directly from
 * Java's File class
 *  
 * @author patkins
 */
public class CSVFile extends File
{
   private BufferedReader  reader      = null;
   private FileReader      fileReader  = null;
   private String          quotes      = "\"'";
   private boolean         trimFields  = false;
   private boolean         trimLines   = false;

   /** Standard constructors delegated to File class */
   public CSVFile(File parent, String child)   { super(parent, child);          }
   public CSVFile(String pathname)             { super(pathname);               }
   public CSVFile(String parent, String child) { super(parent, child);          }
   public CSVFile(File file)                   { super(file.getAbsolutePath()); }

   /** Open this file for reading */
   public void open() throws FileNotFoundException
   {
      if (fileReader == null)
         fileReader = new FileReader(this);
      reader = new BufferedReader(fileReader);
   }

   /** Close this file */
   public void close() throws IOException
   {
      if (reader != null)
      {
         reader.close();
         reader = null;
      }

      if (fileReader != null)
      {
         fileReader.close();
         fileReader = null;
      }
   }

   /** Turns on/off field trimming. When trimming, additional spaces before and
     * after token text are removed. This setting is false by default.
     */
   public void trimFields(boolean trimFields) { this.trimFields = trimFields; }

   /** Turns on/off line trimming. If field trimming is on, then line trimming
     * is implicit.  When line trimming, additional spaces before and
     * after the text line are removed. This setting is false by default.
     */
   public void trimLines(boolean trimLines) { this.trimLines = trimLines; }

   /** Allows class user to determine which quotes to check for (if any).
     */
   public void setQuotes(String quoteText) { this.quotes = quoteText; }

   /** Read a comma separated line, and return an array of elements.  Parsing
     * is quotation aware. When no more lines exist, null is returned. */
   public String[] readLine() throws IOException
   {
      int i = 0;
      String token;

      // Read the next line of text
      String line = reader.readLine();
      if (line == null) return null;

      if (trimLines)
         line = line.trim();

      QuoteAwareTokenizer tokenizer = new QuoteAwareTokenizer(line);
      tokenizer.setQuotes(quotes);

      // Parse out the comma separated text
      String[] tokens = new String[tokenizer.countTokens()];
      while (tokenizer.hasMoreTokens())
      {
         token = tokenizer.nextToken();
         if (trimFields)
            token = token.trim();
         tokens[i++] = token;
      }

      // Return the String array of tokens
      return tokens;
   }
}

