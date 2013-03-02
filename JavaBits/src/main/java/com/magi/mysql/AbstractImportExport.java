package com.magi.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractImportExport
{
	private String databaseURL;
	private String databaseUser;
	private String databasePwd;
	
	public AbstractImportExport(String url, String user, String pwd) {
		this.databaseURL  = url;
		this.databaseUser = user;
		this.databasePwd  = pwd;
	}
	protected Connection getConnection() throws SQLException
	{
		try
		{
			// Load MySQL Driver
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection(databaseURL, databaseUser, databasePwd);
		}
		catch (ClassNotFoundException ex)
		{
			logError("Unable to load the MySQL Database Driver.", ex);
		}
		
		return null;
	}
	
    protected List<DbColumn> deriveDbColumnInfo(Connection conn, String tablename) throws SQLException
    {
        List<DbColumn> allColumnNames = new ArrayList<DbColumn>();
        ResultSet rs = conn.getMetaData().getColumns(null, null, tablename, null);
        while (rs.next())
        {
            /* int colCount = rs.getMetaData().getColumnCount();
            System.out.println("Metadata columns: " + colCount);
            for (int i = 1; i <= colCount; i++)
            {
                System.out.println("RESULT COLUMN: " + rs.getMetaData().getColumnName(i));
            } */
//          COLUMN: TABLE_CAT
//          COLUMN: TABLE_SCHEM
//          COLUMN: TABLE_NAME
//          COLUMN: COLUMN_NAME
//          COLUMN: DATA_TYPE
//          COLUMN: TYPE_NAME
//          COLUMN: COLUMN_SIZE
//          COLUMN: BUFFER_LENGTH
//          COLUMN: DECIMAL_DIGITS
//          COLUMN: NUM_PREC_RADIX
//          COLUMN: NULLABLE
//          COLUMN: REMARKS
//          COLUMN: COLUMN_DEF
//          COLUMN: SQL_DATA_TYPE
//          COLUMN: SQL_DATETIME_SUB
//          COLUMN: CHAR_OCTET_LENGTH
//          COLUMN: ORDINAL_POSITION
//          COLUMN: IS_NULLABLE
//          COLUMN: SCOPE_CATALOG
//          COLUMN: SCOPE_SCHEMA
//          COLUMN: SCOPE_TABLE
//          COLUMN: SOURCE_DATA_TYPE
//          COLUMN: IS_AUTOINCREMENT            
            System.out.println("TABLE_NAME: " + rs.getString("TABLE_NAME"));
            System.out.println("COLUMN_NAME: " + rs.getString("COLUMN_NAME"));
            System.out.println("TYPE_NAME: " + rs.getString("TYPE_NAME"));
            System.out.println("COLUMN_SIZE: " + rs.getInt("COLUMN_SIZE"));
            System.out.println("DECIMAL_DIGITS: " + rs.getString("DECIMAL_DIGITS"));
            System.out.println("NULLABLE: " + rs.getInt("NULLABLE"));
            System.out.println("IS_AUTOINCREMENT: " + rs.getString("IS_AUTOINCREMENT"));
            System.out.println("");
            allColumnNames.add(
                new DbColumn(
                    rs.getString("TABLE_NAME"), 
                    rs.getString("COLUMN_NAME"), 
                    rs.getString("TYPE_NAME"),  
                    rs.getInt("COLUMN_SIZE"), 
                    rs.getString("DECIMAL_DIGITS"), 
                    rs.getInt("NULLABLE"), 
                    rs.getString("IS_AUTOINCREMENT") 
                ) 
            );
            // TYPE_NAME: 
            // INT UNSIGNED, VARCHAR, DECIMAL
        }
        return allColumnNames;
    }

    // Update logging mechanism here as appropriate
	protected void logError(String message, Exception ex) {
		System.out.println(message);
		if (ex != null) {
			ex.printStackTrace();
		}
	}

	protected void logDebug(String message) {
		System.out.println(message);
	}

}
