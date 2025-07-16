/*
 * CreateTableModel.java
 *
 * Created on February 15, 2004, 4:34 PM
 * This model is the base for the seconde generation of the CreateTable-
 * application.  The goal is, to enter the complete description of a table
 * within a grid.  Also the definition of the constraints are placed here
 *
 * This software is part of the Admin-Framework and free software (MIT-License)
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 *
 *
   Copyright (c) 2017 Fredy Fischer, sql@hulmen.ch
    
   Permission is hereby granted, free of charge, to any person obtaining a copy 
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction, including without limitation the rights
   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
   copies of the Software, and to permit persons to whom the Software is
   furnished to do so, subject to the following conditions:
  
   The above copyright notice and this permission notice shall be included in
   all copies or substantial portions of the Software.

   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
   SOFTWARE.
 */

package sql.fredy.sqltools;

import sql.fredy.metadata.DbInfo;
import sql.fredy.metadata.SingleColumnInfo;
import java.awt.*;
import javax.swing.JTable;
import javax.swing.table.*;
import java.util.Vector;
import java.util.StringTokenizer;


/**
 *
 * @author  Fredy Fischer
 */
public class CreateTableModel extends AbstractTableModel {
    
    public static final int PRIMARYKEY      = 0;
    public static final int NAME            = 1;
    public static final int TYPE            = 2;
    public static final int LENGTH          = 3;
    public static final int NOTNULL         = 4;
    public static final int DEFAULT         = 5;
    public static final int CHECKCONSTRAINT = 6;
    public static final int REFERENCES      = 7;
    public static final int OTHER           = 8;
    
    
    private String[] columnNames = { "Primarykey",
    "Name",
    "Type",
    "Length",
    "not Null",
    "Default",
    "Check Constraint",
    "References",
    "Other"};
    
    
    private Vector data;
    
    private DbInfo dbi;
    
    /** Creates a new instance of CreateTableModel */
    public CreateTableModel(DbInfo dbi) {
        this.setDbi(dbi);
        data = new Vector();
    }
    
    public void clearData() {
        data.removeAllElements();
        fireTableDataChanged();
    }
    
    public void addTableRowObject(CreateTableRowObject v) {
        addRow(v.getVector());
    }
    
    public void addRow(Vector v) {
        data.addElement(v);
        fireTableChanged(null);
    }
    
    
    public String getColumnName(int i) {
        return columnNames[i];
    }
    
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
    
    public int getColumnCount() { return columnNames.length; }
    
    public int getRowCount() { return data.size(); }
    
    public Object getValueAt(int row, int col) {
        Vector rowData = (Vector)data.elementAt(row);
        return rowData.elementAt(col);
    }
    
    public boolean isCellEditable(int row, int col) {
        //boolean primkey = ((Boolean)getValueAt(row,PRIMARYKEY)).booleanValue();
        //boolean notnull = ((Boolean)getValueAt(row,NOTNULL)).booleanValue();
        
        boolean retValue = true;
        
        return retValue;
    }
    
    public void setValueAt(Object value, int row, int col) {
        Vector rowData = (Vector)data.elementAt(row);
        rowData.setElementAt(value,col);
        data.setElementAt(rowData,row);
        
        if ( col == REFERENCES) {
            fireTableCellUpdated(row, col);
            verifyRowData(row,(String)value);
        } else {
            fireTableCellUpdated(row, col);
        }
        
    }
    
    public CreateTableRowObject getRow(int row) {
        return new CreateTableRowObject((Vector)data.elementAt(row));
    }
    
    /**
     * Getter for property dbi.
     * @return Value of property dbi.
     */
    public sql.fredy.metadata.DbInfo getDbi() {
        return dbi;
    }
    
    /**
     * Setter for property dbi.
     * @param dbi New value of property dbi.
     */
    public void setDbi(sql.fredy.metadata.DbInfo dbi) {
        this.dbi = dbi;
    }
    
    /*
     * this method sets the data correct, if the references-Field changes
     * it puts the name, length and type of the referenced field and puts this field to NOT NULL
     */
    public void verifyRowData(int row, String value) {
        String tableName = null;
        String colName   = null;
        try {
            StringTokenizer st = new StringTokenizer(value,".");
            tableName = st.nextToken();
            colName   = st.nextToken();
            
            
            SingleColumnInfo sci = (SingleColumnInfo) dbi.getColumnInfo(dbi.getDatabase(),tableName.trim(),colName.trim());
            
            if ( sci != null ) {
                //setValueAt(sci.getColumn_name(),row,NAME);
                setValueAt(tableName.trim().toLowerCase() + sci.getColumn_name(),row,NAME);
                setValueAt(sci.getColumn_size(),row,LENGTH);
                setValueAt(getTypeName(sci.getSql_data_type(),sci.getType_name()),row,TYPE);
                
                // make it NOT NULL
                setValueAt(true,row,NOTNULL);
            }
        } catch (Exception e) {}
    }
    
    private String getTypeName(int type,String def) {
        String s = def;
        if ( type == java.sql.Types.ARRAY)         s = "ARRAY";
        if ( type == java.sql.Types.BIGINT)        s = "BIGINT";
        if ( type == java.sql.Types.BINARY)        s = "BINARY";
        if ( type == java.sql.Types.BIT)           s = "BIT";
        if ( type == java.sql.Types.BLOB)          s = "BLOB";
        if ( type == java.sql.Types.BOOLEAN)       s = "BOOLEAN";
        if ( type == java.sql.Types.CHAR)          s = "CHAR";
        if ( type == java.sql.Types.CLOB)          s = "CLOB";
        if ( type == java.sql.Types.DATALINK)      s = "DATALINK";
        if ( type == java.sql.Types.DATE)          s = "DATE";
        if ( type == java.sql.Types.DECIMAL)       s = "DECIMAL";
        if ( type == java.sql.Types.DISTINCT)      s = "DISTINCT";
        if ( type == java.sql.Types.DOUBLE)        s = "DOUBLE";
        if ( type == java.sql.Types.FLOAT)         s = "FLOAT";
        if ( type == java.sql.Types.INTEGER)       s = "INTEGER";
        if ( type == java.sql.Types.JAVA_OBJECT)   s = "JAVA_OBJECT";
        if ( type == java.sql.Types.LONGVARBINARY) s = "LONGVARBINARY";
        if ( type == java.sql.Types.NUMERIC)       s = "NUMERIC";
        if ( type == java.sql.Types.OTHER)         s = "OTHER";
        if ( type == java.sql.Types.REAL)          s = "REAL";
        if ( type == java.sql.Types.SMALLINT)      s = "SMALLINT";
        if ( type == java.sql.Types.STRUCT)        s = "STRUCT";
        if ( type == java.sql.Types.TIME)          s = "TIME";
        if ( type == java.sql.Types.TIMESTAMP)     s = "TIMESTAMP";
        if ( type == java.sql.Types.TINYINT)       s = "TINYINT";
        if ( type == java.sql.Types.VARBINARY)     s = "VARBINARY";
        if ( type == java.sql.Types.VARCHAR)       s = "VARCHAR";
        
        return s;
        
    }
    
}
