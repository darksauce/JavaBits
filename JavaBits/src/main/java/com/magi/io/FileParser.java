// ============================================================================
// The Magi Toolkit for Java.
// ----------------------------------------------------------------------------
// Developed by Magi Systems Pty Ltd, Australia © 2004, All Rights Reserved.
// Reproduction, adaptation or translation without prior written permission
// is prohibited, except as allowed under the copyright laws.
// ----------------------------------------------------------------------------
// Name:        FileParser
// Description: A text file parsing class, designed for easy extension.
// ============================================================================

package com.magi.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Parse a text file, line by line.
 * This class is designed for easy extension by application specific sub-classes.
 *
 * @author P.Atkinson
 */
public class FileParser
{
    private File toParse;

    /**
     * Create a file parser.
     *
     * @param toParse a File object to parse.
     */
    public FileParser(File toParse)
    {
        super();

        this.toParse = toParse;
    }

    /**
     * Create a file parser.
     *
     * @param toParse the file name to parse.
     */
    public FileParser(String toParse)
    {
        this(new File(toParse));
    }

    /**
     * Begin parsing the file.
     *
     * @throws FileParserException if an error occurs during parsing.
     */
    public void parse() throws FileParserException
    {
        if (toParse == null || !toParse.exists())
            throw new FileParserException("The file to parse does not exist [ " + toParse + " ].");

        if (toParse.isDirectory())
            throw new FileParserException("The file to parse is actually a directory [ " + toParse + " ].");

        try
        {
            FileReader     fReader = new FileReader(toParse);
            BufferedReader reader  = new BufferedReader(fReader);

            // Parse the file, using the Reader
            parseWithReader(reader);

            reader.close();
            fReader.close();
        }
        catch (FileNotFoundException e)
        {
            new FileParserException( "File not found error while parsing the file [" +
                                     toParse + "]: " + e.getMessage(), e);
        }
        catch (IOException e)
        {
            new FileParserException( "I/O error while parsing the file [" +
                                     toParse + "]: " + e.getMessage(), e);
        }
    }

    /**
     * Parse the file using the BufferedReader provided.
     *
     * @param  reader the BufferedReader object.
     * @throws IOException if an I/O error occurs during parsing.
     * @throws FileParserException if there is any syntactical problem or
     *         equivalent during parsing.
     */
    protected void parseWithReader(BufferedReader reader) throws IOException,
                                                          FileParserException
    {
        String line = reader.readLine();
        while (line != null)
        {
            // Parse a single line of text
            parseLine(line);

            line = reader.readLine();
        }
    }

    /**
     * Parses a single line of text from the input text file.
     * Sub-classes extending the FileParser can easily override this method to
     * process lines of text from the input file.
     *
     * @param  line the String line of text to process.
     * @throws FileParserException if there is any syntactical problem or
     *         equivalent during parsing.
     */
    protected void parseLine(String line) throws FileParserException
    {
        System.out.println(line);
    }

    /** Test */
    public static void main(String[] args)
    {
        String     fName  = "C:/temp/myserver.log";
        FileParser parser = new FileParser(fName);

        try
        {
            System.out.println("###### Begin Parsing " + fName + " ######\n");

            // Start the parsing
            parser.parse();

            System.out.println("\n###### Complete ######");
        }
        catch (FileParserException e)
        {
            e.printStackTrace();
        }
    }
}
