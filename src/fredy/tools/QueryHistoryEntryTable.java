/*
 * This code is generated by Fredy's SQL-Admin Tool visit http://www.hulmen.ch/admin
 * SQL Admin is free software and licensed under  MIT License
 
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

   This class represents a JTable for display only of the query printed below.
   you can fill this table with the calling these methods:
   addBunch(ArrayList<sql.fredy.tools.QueryHistoryEntry> a);
   addQueryHistoryEntry(QueryHistoryEntry row);
   addRow(Vector v);  // where v is the vector called from QueryHistoryEntry.getVector()
   addTableRowObject(QueryHistoryEntry v); 
 */
package sql.fredy.tools;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;
import sql.fredy.tools.QueryHistoryEntry;

public class QueryHistoryEntryTable extends AbstractTableModel {

    public static final int RUNAT = 0;
    public static final int DATABASE = 1;
    public static final int QUERY = 2;
    private Vector data;
    private String[] columnNames = {
        "Runat",
        "Database",
        "Query",};

    public QueryHistoryEntryTable() {
        data = new Vector();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
   
   @Override
    public String getColumnName(int column) {
        if (columnNames[column] != null) {
            return columnNames[column];
        } else {
            return "";
        }
    }
        
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Vector rowData = (Vector) data.elementAt(rowIndex);
        return rowData.elementAt(columnIndex);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Vector rowData = (Vector) data.elementAt(row);
        rowData.setElementAt(value, col);
        data.setElementAt(rowData, row);

        fireTableCellUpdated(row, col);
    }

    public QueryHistoryEntry getRowAt(int row) {
        QueryHistoryEntry a = new QueryHistoryEntry();
        a.setRunat((java.sql.Timestamp) getValueAt(row, RUNAT));
        a.setDatabase((String) getValueAt(row, DATABASE));
        a.setQuery((String) getValueAt(row, QUERY));
        return a;
    }

    public void addTableRowObject(QueryHistoryEntry v) {
        addRow(v.getVector());
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void removeRow(int row) {
        data.removeElementAt(row);
        fireTableChanged(null);
    }

    public void addRow(Vector v) {
        data.addElement(v);
        fireTableChanged(null);
    }

    public void addBunch(ArrayList a) {
        clearData();
        Vector v;
        QueryHistoryEntry satz;
        for (int i = 0; i < a.size(); i++) {
            v = new Vector();
            satz = new QueryHistoryEntry();
            satz = (QueryHistoryEntry) a.get(i);
            v.addElement((java.sql.Timestamp) satz.getRunat());
            v.addElement((String) satz.getDatabase());
            v.addElement((String) satz.getQuery());
            addRow(v);
        }
    }

    public void clearData() {
        data.removeAllElements();
        fireTableDataChanged();
    }
}