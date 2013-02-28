/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.util;

/** This tokenizer is similar to the Java StringTokenizer except that it is
  * quotation aware.  Quotation marks around a string remove the need to parse
  * and break down any text within them.
  */
public class QuoteAwareTokenizer
{
    private static final int NO_POS  = -1;
    private static final int END_POS = -2;

    private String   str;
    private String   delim;
    private String   quotes;
    private char[]   strChar  = null;
    private int      pos      = NO_POS;
    private int      lastPos  = NO_POS;
    private boolean  inQuote  = false;

    /** Standard constructors based on those for StringTokenizer */
    public QuoteAwareTokenizer(String str)               { init(str, ",");   }
    public QuoteAwareTokenizer(String str, String delim) { init(str, delim); }

    /** Initialise this instance */
    private void init(String str, String delim)
    {
       this.str     = str;
       this.delim   = delim;
       this.quotes  = "\"'";
       this.strChar = str.toCharArray();

       skip();
    }

    /** Skip to the next delimiter position */
    private void skip()
    {
       if (pos != NO_POS) lastPos = pos;
       if (pos == END_POS) return;

       while (++pos < strChar.length)
       {
          if (quotes.indexOf(strChar[pos]) > -1)
             inQuote = !inQuote;

          if (!inQuote && delim.indexOf(strChar[pos]) > -1)
             return;
       }

       if (pos >= strChar.length) pos = END_POS;
    }

    /** Retrieve the next token. This may be called if hasMoreTokens() has returned true. */
    public String nextToken()
    {
       if (lastPos == END_POS) return null;

       String s;
       if (pos == END_POS || pos == NO_POS)
          s = str.substring(lastPos + 1);
       else
          s = str.substring(lastPos + 1, pos);

       skip();
       return removeQuotes(s);
    }

    /** Indicates if more tokens exist for retrieval */
    public boolean hasMoreTokens() { return (lastPos != END_POS); }

    /** Returns a count of the tokens (separated by the delimiters) */
    public int countTokens()
    {
       boolean inQuotes = false;
       int     count    = 0;

       for (int i = 0; i < strChar.length; i++)
       {
          if (quotes.indexOf(strChar[i]) > -1)
             inQuotes = !inQuotes;

          if (!inQuotes && delim.indexOf(strChar[i]) > -1)
             count++;
       }

       return count + 1;
    }

    /** Allows class user to determine which type of quotes to check for (if any).
      */
    public void setQuotes(String quoteText) { this.quotes = quoteText; }

    /** Remove any quote characters from this string */
    private String removeQuotes(String str)
    {
       int i = 0;
       StringBuffer buff = new StringBuffer(str);

       while (i < buff.length())
       {
          if (quotes.indexOf(buff.charAt(i)) > -1)
             buff.deleteCharAt(i);
          else
             i++;
       }
       return buff.toString();
    }
}

