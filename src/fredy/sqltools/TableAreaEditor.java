/*
   TableAreaEditor.java
  
   Created on October 5, 2004, 10:20 AM
  
   This software is part of the Admin-Framework 
 
   Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
   for DB-Administrations, as create / delete / alter and query tables
   it also creates indices and generates simple Java-Code to access DBMS-tables
 
 
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
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.*;
/**
 *
 * @author sql@hulmen.ch
 */
public class TableAreaEditor extends AbstractCellEditor implements TableCellEditor {
    
    private JComponent area;
    
    /** Creates a new instance of TableAreaEditor */
    public TableAreaEditor(int rows, int columns) {
        area = new JTextArea(rows,columns);
    }
    
    // Enables the editor only for double-clicks.
    public boolean isCellEditable(EventObject evt) {
        if (evt instanceof MouseEvent) {
            return ((MouseEvent)evt).getClickCount() >= 1;
        }
        return true;
    }
    
    public Object getCellEditorValue() {
        return ((JTextArea)area).getText();
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        if (isSelected) {
            // cell (and perhaps other cells) are selected
        }
        
        ((JTextArea)area).setText((String)value);
        
        // Return the configured component
        //return area;
        //JFrame frame = new JFrame();
        //frame.getContentPane().setLayout(new BorderLayout());
        //frame.getContentPane().add(BorderLayout.CENTER,new JScrollPane(area));
        return (new JScrollPane(area));
        
    }
    
}
