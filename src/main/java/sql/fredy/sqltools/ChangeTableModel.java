package sql.fredy.sqltools;

/** 
    ChangeTableModel is apart of Admin

    Admin is a Tool around SQL-RDBMS to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

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

public class ChangeTableModel extends AbstractTableModel {

// Fredy's make Version
private static String fredysVersion = "Version 1.4  2. Jan. 2002";

public String getVersion() {return fredysVersion; }
public Vector  data;

    public static final int NAME_FIELD   = 0;
    public static final int TYP_FIELD    = 1;
    public static final int NULL_FIELD   = 2;
    public static final int LENGTH_FIELD = 3;
    public static final int ADD_FIELD    = 4;

public String[] columnNames = { "Field Name",
		       	       "Typ of the field",
		       	       "NULL allowed",
		       	       "length or enumeration/set",
			       "additional Parameters"
                              };

    public ChangeTableModel () {

	data   = new Vector();
    }

    public void clearData() {
	data.removeAllElements(); 
	fireTableDataChanged();
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
