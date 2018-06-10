/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.util;

/**
 * Java Reflections based utility methods.
 * 
 * @author patkins
 */
public class ReflectUtils {

	/**
	 * Return a short version of the class name, with no package prefix.
	 */
	public static <T> String getShortClassname(Class<T> clazz) {
		String name = clazz.getName();
		int  dotpos = name.lastIndexOf('.');
		if (dotpos != -1) {
			return name.substring(dotpos + 1);
		}
		return name;
	}
}
