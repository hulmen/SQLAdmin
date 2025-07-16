/*
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
package sql.fredy.ui;

import java.util.ArrayList;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author sql@hulmen.ch
 */
public class CodeCompletionList extends AbstractTableModel {

    public static final int NAME = 0;
    public static final int ABBREVIATION = 1;
    public static final int CODETEMPLATE = 2;
    public static final int DESCRIPTION = 3;

    private Vector data;

    private String[] columnNames = {"Name",
        "Abbreviation",
        "Code Template",
        "Description"
    };

    public CodeCompletionList() {
        data = new Vector();
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
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
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

    public CodeCompletionRow getRowAt(int row) {
        CodeCompletionRow ccr = new CodeCompletionRow();
        ccr.setName((String) getValueAt(row,NAME));
        ccr.setAbbreviation((String) getValueAt(row,ABBREVIATION));
        ccr.setDescription((String) getValueAt(row,DESCRIPTION));
        ccr.setCodeTemplate((String) getValueAt(row,CODETEMPLATE));
        return ccr;
    }
    
    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
    public void addTableRowObject(CodeCompletionRow v) {
        addRow(v.getVector());
    }
    
     public void removeRow(int row) {
        data.removeElementAt(row);
	fireTableChanged(null);	
    }
    
    public void addRow(Vector v) {
        data.addElement(v);
        fireTableChanged(null);
    }

    public void addBunch(ArrayList<CodeCompletionRow> v){
        for (int i = 0; i < v.size();i++) {
            addRow(((CodeCompletionRow) v.get(i)).getVector());
        }
    }
    
    public void clearData() {
        data.removeAllElements();
        fireTableDataChanged();
    }
    
}
