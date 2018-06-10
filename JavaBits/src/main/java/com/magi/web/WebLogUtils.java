/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.web;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Web logging utilities.
 * 
 * @author patkins
 */
public class WebLogUtils {

	public static void logRequestParameters(Logger log, HttpServletRequest request) {
		for (String name: request.getParameterMap().keySet()) {
			String val = request.getParameter(name);
			log.debug("PARAMETER " + name + ": " + val);
		}
	}

	public static void logRequestAttributes(Logger log, HttpServletRequest request) {
		for (Enumeration<String> en = request.getAttributeNames(); en.hasMoreElements(); ) {
			String name = en.nextElement();
			Object val = request.getAttribute(name);
			log.debug("ATTRIBUTE " + name + ": " + val);
		}
	}
}
