/*
 * The MIT License
 *
 * Copyright 2018 fredy.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sql.fredy.share;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author fredy
 */
public class FindResultTable extends AbstractTableModel {

    private String[] columnNames = {"Text", "Row", "Column"};
    Vector data;

    public FindResultTable() {
        data = new Vector();
    }

    public void setContent(ArrayList<TableFindResult> tfr) {
        clearData();
        Iterator iter = tfr.iterator();
        while (iter.hasNext()) {
            TableFindResult t = (TableFindResult) iter.next();
            Vector v = new Vector();
            v.add((String) t.getContent());
            v.add((Integer) t.getRow());
            v.add((Integer) t.getCol());
            addRow(v);
        }
         fireTableChanged(new TableModelEvent(this));
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    public void removeRow(int row) {
        data.removeElementAt(row);
        fireTableChanged(new TableModelEvent(this));
    }

    public void addRow(Vector v) {
        data.addElement(v);
        fireTableChanged(new TableModelEvent(this));
    }

    public void clearData() {
        data.removeAllElements();
        fireTableDataChanged();
        fireTableChanged(new TableModelEvent(this));
        
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            Vector rowData = (Vector) data.elementAt(rowIndex);
            return rowData.elementAt(columnIndex);
        } catch (Exception e) {
            return null;
        }

    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Vector rowData = (Vector) data.elementAt(row);
        rowData.setElementAt(value, col);
        data.setElementAt(rowData, row);

        fireTableCellUpdated(row, col);
    }

    @Override
    public String getColumnName(int column) {
        if (columnNames[column] != null) {
            return columnNames[column];
        } else {
            return "";
        }
    }
}
