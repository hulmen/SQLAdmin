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

package sql.fredy.test;

import javax.swing.JTable;
import javax.swing.table.*;
import java.util.Vector;
import java.util.Properties;
import java.util.Enumeration;

/**
 *
 * @author sql@hulmen.ch
 */
public class PropertiesTable extends AbstractTableModel  {
    
    
    public String[] columnNames = { "Property", "Value"    };
    
    Vector data;
    
    public void clearData() {
        data.removeAllElements();
        fireTableDataChanged();
    }
    
    
    public void addRow(Vector v) {
        
        data.addElement(v);
        fireTableChanged(null);
    }
    
    public void removeRow(int row) {
        data.removeElementAt(row);
        fireTableChanged(null);
    }
    
    public String getColumnName(int i) {
        
        return columnNames[i];
    }
    
    public int getColumnCount() { return columnNames.length; }
    
    public int getRowCount() { return data.size(); }
    
    public Object getValueAt(int row, int col) {
        Vector rowData = (Vector)data.elementAt(row);
        return rowData.elementAt(col);
    }
    
    public boolean isCellEditable(int row, int col) {
        boolean b = false;
        return b;
    }
    
    public void setValueAt(Object value, int row, int col) {
        
    }
    
    
    
    /** Creates a new instance of ProertiesTable */
    public PropertiesTable() {
        
        data = new Vector();
        
        Properties props = System.getProperties();
        
        Enumeration e = props.propertyNames();
        for (; e.hasMoreElements(); ) {
            String propName = (String)e.nextElement();
            String propValue = (String)props.get(propName);
            Vector v = new Vector();
            v.addElement(propName);
            v.addElement(propValue);
            addRow(v);
        }
        
    }
}