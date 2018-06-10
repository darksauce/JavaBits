/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.util;

/**
 * File utility methods.
 * 
 * @author patkins
 */
public class FileUtils {
	
	/** Removes any special characters that cannot be used in a Windows filename string */
	public static String removeSpecialFilenameChars(String fname) {
		if (fname == null) {
			return null;
		}
		// Cannot use invalid characters \ / : * ? " < > |
		String fnm = fname.replaceAll("\\\\", "");
		fnm =  fnm.replaceAll("/", "");
		fnm =  fnm.replaceAll(":", "");
		fnm =  fnm.replaceAll("\\*", "");
		fnm =  fnm.replaceAll("\\?", "");
		fnm =  fnm.replaceAll("\"", "");
		fnm =  fnm.replaceAll("<", "");
		fnm =  fnm.replaceAll(">", "");
		return fnm.replaceAll("|", "").trim();
	}
}
