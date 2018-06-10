package com.magi.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Database access utility methods.
 * 
 * @author patkins
 */
public class DbUtils {

	private static final Logger log = Logger.getLogger(DbUtils.class);
	private static final DateFormat SQL_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/** Safely close a prepared statement if not null */
	public static void safeClose(PreparedStatement stmt) {
		if (stmt != null) {
			try {
				if (!stmt.isClosed()) {
					stmt.close();
				}
			}
			catch (SQLException ex) {
				log.error(ex.getClass().getName() + ": " + ex.getMessage());
			}
		}
	}

	/** Safely close a result set if not null */
	public static void safeClose(ResultSet rs) {
		if (rs != null) {
			try {
				if (!rs.isClosed()) {
				    rs.close();
				}
			}
			catch (SQLException ex) {
				log.error(ex.getClass().getName() + ": " + ex.getMessage());
			}
		}
	}
	
	/** Safely close a connection if not null */
	public static void safeClose(Connection conn) {
		if (conn != null) {
			try {
				if (!conn.isClosed()) {
					conn.close();
				}
			}
			catch (SQLException ex) {
				log.error(ex.getClass().getName() + ": " + ex.getMessage());
			}
		}
	}

	/** Converts a Java Boolean/boolean to a SQL BIT flag (0 or 1). */
	public static int booleanToBit(Boolean flag) {
		if (flag == null) {
			return 0;
		}
		
		return Boolean.TRUE.equals(flag) ? 1 : 0;
	}

	/** Converts a java.util.Date and Time to a SQL formatted date/time String */
	public static String dateTimeToSqlString(Date dateTime) {
		if (dateTime == null) {
			return "NULL";
		}
		
		return "'" + SQL_FORMAT.format(dateTime) + "'";
	}
}
