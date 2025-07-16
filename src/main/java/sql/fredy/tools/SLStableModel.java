package sql.fredy.tools;

/**
   SLSTableModel is the TableModel used within the
   SLS (SimpleLogServer)
   
   SimpleLogServer is part of Admin 

   Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
   for DB-Administrations, as create / delete / alter and query tables
   it also creates indices and generates simple Java-Code to access DBMS-tables
   and exports data into various formats
 
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
import java.util.StringTokenizer;

public class SLStableModel extends AbstractTableModel {

// Fredy's make Version
private static String fredysVersion = "Version 1.0  18. Jan. 2003";

public String getVersion() {return fredysVersion; }
public Vector  data;

    public static final int DATE         = 0;
    public static final int SOURCECLASS  = 1;
    public static final int SOURCEMETHOD = 2;
    public static final int LEVEL        = 3;
    public static final int MESSAGE      = 4;

public String[] columnNames = { "Date",
		       	       "Source class",
		       	       "Source method",
		       	       "Log Level",
			       "Log Message"
                              };

    public SLStableModel () {

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
	try {
	    Vector rowData = (Vector)data.elementAt(row);
	    return rowData.elementAt(col);
	} catch (Exception e) { return " ";}
    }

    public boolean isCellEditable(int row, int col) { return false; }

    public void setValueAt(Object value, int row, int col) {
	Vector rowData = (Vector)data.elementAt(row);
	rowData.setElementAt(value,col);
	data.setElementAt(rowData,row);
	fireTableCellUpdated(row, col);
    }

    public void setTextData(String s) {
	clearData();

	StringTokenizer st = new StringTokenizer(s,"\n");
	while (st.hasMoreTokens()) {
	    addSingleRow(st.nextToken());
	}
    }
    public void addSingleRow(String s) {
	StringTokenizer st = new StringTokenizer(s,"|");
	Vector v = new Vector();
	while (st.hasMoreTokens()) {
	    v.addElement((String)st.nextToken());
	}
	addRow(v);    
    }

    public String getDataAsText() {
	StringBuffer s = new StringBuffer();
	for (int i=0;i < data.size();i++) {
	    for (int j = 0;j <= getColumnCount();j++) {
		if ( j > 0)  s.append(" |");
		s.append((String)getValueAt(i,j));
	    }
	    s.append("\n");
	}
	return s.toString();
    }

    public void half() {
	for (int i=0;i < getRowCount();i++) data.remove(i);
    }
}
