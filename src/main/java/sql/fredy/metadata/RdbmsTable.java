package sql.fredy.metadata;

/** 
 * RdbmsTable is part of Admin and is a TableModel with the info
 * of a table 
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 * and exports data into various formats
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

**/


import java.awt.*;
import java.awt.event.*;
import javax.swing.JTable;
import javax.swing.table.*;
import java.util.Vector;

public class RdbmsTable extends AbstractTableModel {

// Fredy's make Version
private static String fredysVersion = "Version 1.4   2. Jan.2002";

public String getVersion() {return fredysVersion; }

public Vector  data;
public String[] columnNames = { "Product Name",
		       	       "JDBC-Driver",
		       	       "DB-Url",
		       	       "DB Port",
                               "Image"};

    public RdbmsTable () {

	data   = new Vector();
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

    public boolean isCellEditable(int row, int col) { return true; }

    public void setValueAt(Object value, int row, int col) {
	// to be implemented...
	Vector rowData = (Vector)data.elementAt(row);
	rowData.setElementAt(value,col);
	data.setElementAt(rowData,row);
	fireTableCellUpdated(row, col);
    }

}
