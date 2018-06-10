package com.magi.util;

import java.util.Calendar;

/**
 * Date related utility methods.
 * 
 * @author patkins
 */
public class DateUtils {

	/**
	 * Returns the current Calendar instance with the time zeroed out.
	 */
	public static Calendar getCalendarAsMidnight() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}
}
