// ============================================================================
// The Magi Toolkit for Java.
// ----------------------------------------------------------------------------
// Developed by Magi Systems Pty Ltd, Australia © 2004, All Rights Reserved.
// Reproduction, adaptation or translation without prior written permission
// is prohibited, except as allowed under the copyright laws.
// ----------------------------------------------------------------------------
// Name:        FileParserException
// Description: An exception class for all file parsing errors.
// ============================================================================

package com.magi.io;

/**
 * Exceptions thrown by the FileParser, and associated classes.
 *
 * @author P.Atkinson
 */
public class FileParserException extends Exception
{
    /**
     * Create a File Parser Exception.
     */
    public FileParserException()
    {
        super();
    }

    /**
     * Create a File Parser Exception.
     *
     * @param message the message to send back.
     */
    public FileParserException(String message)
    {
        super(message);
    }

    /**
     * Create a File Parser Exception.
     *
     * @param cause the Throwable object that caused this exception.
     */
    public FileParserException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Create a File Parser Exception.
     *
     * @param message the message to send back.
     * @param cause the Throwable object that caused this exception.
     */
    public FileParserException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
