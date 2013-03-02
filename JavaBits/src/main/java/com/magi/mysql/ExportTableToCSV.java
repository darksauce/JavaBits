package com.magi.mysql;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Export CSV data with all column names from the specified table.
 * Simple data type conversion is performed where possible.
 * 
 * @author patkins
 */
public class ExportTableToCSV extends AbstractImportExport
{
	private File exportCsvFile;
	
	public ExportTableToCSV(File exportCsvFile, String dbUrl, String user, String pwd)
	{
		super(dbUrl,user,pwd);
		this.exportCsvFile = exportCsvFile;
	}
	
//  ExportTableToCSV exp = new ExportTableToCSV(new File(tableName.toLowerCase() + ".csv"));
//  exp.exportFrom(tableName);
	
	/**
	 * USAGE: ExportTableToCSV <filename> <tablename>
	 * 
	 * @param args (0) Filename (1) Tablename
	 */
	public static void main(String[] args)
	{
		if (args.length == 2)
		{
			String filename  = args[0];
			String tablename = args[1];
			File outFile     = new File(filename);
			ExportTableToCSV exp;
			
			exp = new ExportTableToCSV(outFile,"dbUrl","user","pwd");
			exp.exportFrom(tablename);
		}
		else
		{
			System.out.println("USAGE: ExportTableToCSV <filename> <tablename>");
		}
	}
	
	public void exportFrom(String tablename)
	{
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		FileWriter writer = null;
		
		logDebug("Exporting " + tablename + " to path " + new File(".").getAbsolutePath());
		
		try
		{
		    conn = getConnection();
		    writer = new FileWriter(this.exportCsvFile);
		    
		    // Write Header Line
		    boolean isFirst = true;
		    List<DbColumn> dbColumnNames = deriveDbColumnInfo(conn, tablename);
		    for (DbColumn col: dbColumnNames)
		    {
		        if (!isFirst)
		            writer.write(",");
		        
		        writer.write(col.getColumnName());
		        isFirst = false;
		    }
		    writer.write("\n");
		    
		    stmt = conn.createStatement();
		    rs = stmt.executeQuery("SELECT * FROM " + tablename);
		    
		    while (rs.next()) 
		    {
	            isFirst = true;
	            for (DbColumn col: dbColumnNames)
	            {
	                if (!isFirst)
	                    writer.write(",");
	                
	                Object o = rs.getObject(col.getColumnName());
	                if (o == null)
	                    writer.write("");
	                else
	                    writer.write(quoteTextIfNecessary(o.toString()));
	                isFirst = false;
	            }
	            writer.write("\n");
		    }
		}
		catch (Exception ex)
		{
			logError("Export Error", ex);
			ex.printStackTrace();
		}
		finally
		{
		    if (rs     != null) try { rs.close();     } catch (Exception e) { }
		    if (stmt   != null) try { stmt.close();   } catch (Exception e) { }
			if (conn   != null) try { conn.close();   } catch (Exception e) { }
			if (writer != null) try { writer.close(); } catch (Exception e) { }
		}
	}

	private String quoteTextIfNecessary(String text) {
		if (text.indexOf(',') != -1) {
			return "\"" + text + "\"";
		}
		return text;
	}	
}
