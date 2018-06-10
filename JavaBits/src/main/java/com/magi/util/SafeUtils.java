/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.util;

import org.apache.commons.lang.StringUtils;

/**
 * Implementations of several "safe" methods for accessing basic Java objects and handling NULLs in a safe, appropriate way.
 * 
 * @author patkins
 */
public class SafeUtils {

	/** Returns a safe String, where nulls are translated into a zero length String */
	public static String safeString(String text) {
		return text == null ? "" : text;
	}

	/** Return an Integer as a String safely, nulls are translated to a zero length String */
	public static String safeIntegerAsString(Integer value) {
		if (value == null) {
			return "";
		}
		return value.toString();
	}

	/** Return an Integer safely from a String if possible, otherwise null is returned */
	public static Integer safeIntegerFromString(String intValue) {
		try {
		    if (StringUtils.isNotBlank(intValue)) {
			    return new Integer(intValue);
		    }
		}
		catch (NumberFormatException ex) {
			// do nothing - just return null
		}
		return null;
	}

	/** Return a trimmed String or return a NULL if the string is empty. */
	public static String safeStringTrimOrNull(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return value.trim();
	}
}
