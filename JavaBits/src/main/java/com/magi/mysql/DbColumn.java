package com.magi.mysql;

/**
 * Capture basic information about a Database Table Column.
 * 
 * @author patkins
 */
public class DbColumn
{
	private String  tableName;
	private String  columnName;
	// TYPE_NAME: 
	// INT UNSIGNED, VARCHAR, DECIMAL
	private String  typeName;
	private Integer columnSize;
	private Integer decimalDigits = null; // can be null
	private boolean nullable;
	private boolean autoIncrement;
	
	private Integer csvColumn = null;
	
	public DbColumn(String tableName, String columnName, String typeName, int columnSize, String decimalDigits, int nullable, String autoIncrement)
	{
		this.tableName     = tableName;
		this.columnName    = columnName;
		this.typeName      = typeName;
		this.columnSize    = new Integer(columnSize);
		this.decimalDigits = decimalDigits == null ? null : new Integer(decimalDigits);
		this.nullable      = (nullable == 1);
		this.autoIncrement = "YES".equals(autoIncrement);
	}

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public String getColumnName()
	{
		return columnName;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	public String getTypeName()
	{
		return typeName;
	}

	public void setTypeName(String typeName)
	{
		this.typeName = typeName;
	}

	public Integer getColumnSize()
	{
		return columnSize;
	}

	public void setColumnSize(Integer columnSize)
	{
		this.columnSize = columnSize;
	}

	public Integer getDecimalDigits()
	{
		return decimalDigits;
	}

	public void setDecimalDigits(Integer decimalDigits)
	{
		this.decimalDigits = decimalDigits;
	}

	public boolean isNullable()
	{
		return nullable;
	}

	public void setNullable(boolean nullable)
	{
		this.nullable = nullable;
	}

	public boolean isAutoIncrement()
	{
		return autoIncrement;
	}

	public void setAutoIncrement(boolean autoIncrement)
	{
		this.autoIncrement = autoIncrement;
	}

	public Integer getCsvColumn()
	{
		return csvColumn;
	}

	public void setCsvColumn(Integer csvColumn)
	{
		this.csvColumn = csvColumn;
	}
	
}
