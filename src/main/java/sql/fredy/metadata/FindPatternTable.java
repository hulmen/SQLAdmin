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
package sql.fredy.metadata;

import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author sql@hulmen.ch
 */
public class FindPatternTable extends AbstractTableModel {

    private ArrayList<FindPattern> findPattern = new ArrayList();
    private Object[][] rowData;

    public String[] columnNames = {
        "Database",
        "Table",
        "Column",
        "Pattern"
    };

    @Override
    public String getColumnName(int i) {
        return columnNames[i];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    public static final int DATABASE = 0;
    public static final int TABLE = 1;
    public static final int COLUMN = 2;
    public static final int PATTERN = 3;

    @Override
    public String getValueAt(int row, int col) {
        FindPattern fp = findPattern.get(row);
        if (col == DATABASE) {
            return fp.getDatabase();
        }
        if (col == TABLE) {
            return fp.getTable();
        }
        if (col == COLUMN) {
            return fp.getColumn();
        }
        if (col == PATTERN) {
            return fp.getPattern();
        }

        return null;

    }

    @Override
    public int getRowCount() {
        return findPattern.size();
    }

    /**
     * @return the findPattern
     */
    public ArrayList<FindPattern> getFindPattern() {
        return findPattern;
    }

    /**
     * @param findPattern the findPattern to set
     */
    public void setFindPattern(ArrayList<FindPattern> findPattern) {
        this.findPattern = findPattern;
        int i = 0;

        for (FindPattern fp : findPattern) {
            setValueAt(fp, i, 0);
            /*
             setValueAt(fp.getDatabase(), i, DATABASE);
             setValueAt(fp.getTable(), i, TABLE);
             setValueAt(fp.getColumn(), i, COLUMN);
             setValueAt(fp.getPattern(), i, PATTERN);
             */

            i++;
        }

    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        FindPattern fp = (FindPattern) value;

        /*
         fp.setDatabase("");
         fp.setTable("");
         fp.setColumn("");
         fp.setPattern("");

         if (col == DATABASE) {
         fp.setDatabase((String) value);
         }
         if (col == TABLE) {
         fp.setTable((String) value);
         }
         if (col == COLUMN) {
         fp.setColumn((String) value);
         }
         if (col == PATTERN) {
         fp.setPattern((String) value);
         }

         */
        findPattern.set(row, fp);

        fireTableCellUpdated(row, col);
    }

    public FindPatternTable() {

    }

    public FindPatternTable(ArrayList<FindPattern> findPattern) {
        setFindPattern(findPattern);
    }
}
