/* Open Source Licensed under GNU LGPL 3.0
 * See http://www.gnu.org/copyleft/lesser.html for details. */
package com.magi.web;

import java.security.Principal;
import javax.servlet.http.HttpServletRequest;

/**
 * Web authorisation related utility code.
 * 
 * @author patkins
 */
public class WebAuthUtils {

	/** Determines the user name of the logged in user, returns null if not logged on */
	public static String getLoggedInUser(HttpServletRequest request) {
		
		if (request != null) {
			Principal principal = request.getUserPrincipal();
			if (principal != null) {
			    return request.getUserPrincipal().getName();
			}
		}
		
		return null;
	}
}
