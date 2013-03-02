package com.magi.mysql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 * Import CSV data with matching column names into the specified table.
 * Simple data type conversion is performed where possible.
 * 
 * USAGE: ImportCSVToTable <filename> <tablename>
 * 
 * @author patkins
 */
public class ImportCSVToTable extends AbstractImportExport
{
	private File importCsvFile;
	
	public ImportCSVToTable(File importCsvFile, String dbUrl, String user, String pwd)
	{
		super(dbUrl, user, pwd);
		this.importCsvFile = importCsvFile;
	}
	
	/**
	 * USAGE: ImportCSVToTable <filename> <tablename>
	 * 
	 * @param args (0) Filename (1) Tablename
	 */
	public static void main(String[] args)
	{
		if (args.length == 2)
		{
			String filename  = args[0];
			String tablename = args[1];
			File inFile      = new File(filename);
			
			if (inFile.exists())
			{
				ImportCSVToTable imp;
				
				imp = new ImportCSVToTable(inFile,"dbUrl","user","pwd");
				imp.importTo(tablename);
			}
			else
			{
				System.out.println("Import filename does not exist: " + inFile);
			}
		}
		else
		{
			System.out.println("USAGE: ImportCSVToTable <filename> <tablename>");
		}
	}
	
	public void importTo(String tablename)
	{
		Connection conn = null;
		
		try
		{
		    conn = getConnection();
		    List<String[]> csvData = parseCsv(importCsvFile);
		    List<DbColumn> dbColumnNames = deriveDbColumnInfo(conn, tablename);
		    List<DbColumn> matchedColumns = null;
		    String insertPreamble = null;
		    String dataInsertion = null;
		    
		    for (String[] record: csvData)
		    {
		    	if (insertPreamble == null) // first row
		    	{
		    		matchedColumns = matchColumnNames(record, dbColumnNames);
		    		insertPreamble = generateInsertPreamble(tablename, matchedColumns);
		    		// System.out.println(insertPreamble);
		    	}
		    	else // data rows
		    	{
		    		dataInsertion = generateDataInsertion(record, matchedColumns);
		    		System.out.print(insertPreamble);
		    		System.out.print(dataInsertion);
		    		System.out.println(");");
		    	}
		    }
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			if (conn != null) try { conn.close(); } catch (Exception e) { }
		}
	}
	
	private String generateDataInsertion(String[] record, List<DbColumn> matchedColumns)
	{
		StringBuffer data = new StringBuffer();
		for (DbColumn dbCol: matchedColumns)
		{
			if (data.length() > 0)
				data.append(",");
			
			if ("VARCHAR".equals(dbCol.getTypeName()))
			{
				data.append("'").append(sqlEscape(record[dbCol.getCsvColumn()])).append("'");
			}
			else if ("INT UNSIGNED".equals(dbCol.getTypeName()))
			{
				data.append(record[dbCol.getCsvColumn()]);
			}
			else if ("DECIMAL".equals(dbCol.getTypeName()))
			{
				data.append(record[dbCol.getCsvColumn()]);		
			} 
			else
			{
				throw new IllegalArgumentException("UNSUPPORTED DATA TYPE NAME: " + dbCol.getTypeName() + " !!!");
			}
		}
		return data.toString();
	}

	private String sqlEscape(String text)
	{
		return text.replaceAll("'", "''");
	}

	private String generateInsertPreamble(String tablename, List<DbColumn> matchedColumns)
	{
		StringBuffer ins = new StringBuffer();
		for (DbColumn dbCol: matchedColumns)
		{
			if (ins.length() > 0)
				ins.append(",");
			ins.append(dbCol.getColumnName());
		}
		ins.append(") VALUES (");
		ins.insert(0, "INSERT INTO " + tablename + " (");
		return ins.toString();
	}

	private List<DbColumn> matchColumnNames(String[] record, List<DbColumn> allColumnNames)
	{
		List<DbColumn> matches = new ArrayList<DbColumn>();
		for (int i = 0; i < record.length; i++)
		{
			for (DbColumn dbCol: allColumnNames)
			{
				if (dbCol.getColumnName().equals(record[i]))
				{
					dbCol.setCsvColumn(new Integer(i));
					matches.add(dbCol);
				}
			}
		}
		return matches;
	}

	private List<String[]> parseCsv(File importFile) throws IOException
	{
		FileReader fread = null;
		BufferedReader reader = null;
		
		try
		{
			fread = new FileReader(importFile);
			reader = new BufferedReader(fread);
			List<String[]> csvData = new ArrayList<String[]>();
			String line;
			
			while ((line = reader.readLine()) != null)
			{
				csvData.add(line.split(","));
			}
			
			return csvData;
		}
		finally
		{
			if (reader != null) reader.close();
			if (fread != null) fread.close();
		}
	}
}
