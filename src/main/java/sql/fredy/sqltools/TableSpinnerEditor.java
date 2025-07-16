/*
  TableSpinnerEditor.java
  
  This software is part of the Admin-Framework   
   
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
public class TableSpinnerEditor extends AbstractCellEditor implements TableCellEditor {
    
                          
        final JSpinner spinner = new JSpinner();
    
        // Initializes the spinner.
        public TableSpinnerEditor(int value, int minimum, int maximum,int stepSize) {
            SpinnerModel lengthModel = new SpinnerNumberModel(value, minimum,maximum,stepSize);    
            spinner.setModel(lengthModel);
        }
    
        // Prepares the spinner component and returns it.
        public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
            spinner.setValue(value);
            return spinner;
        }
    
        // Enables the editor only for single-clicks.
        public boolean isCellEditable(EventObject evt) {
            if (evt instanceof MouseEvent) {
                return ((MouseEvent)evt).getClickCount() >= 1;
            }
            return true;
        }
    
        // Returns the spinners current value.
        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }
