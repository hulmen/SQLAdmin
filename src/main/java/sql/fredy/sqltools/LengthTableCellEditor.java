/*
   LengthTableCellEditor.java
 
   Created on October 3, 2004, 10:39 AM
 
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



/**
 *
 * @author sql@hulmen.ch
 */
public class LengthTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    
    private JButton button;
    
    private JDialog dialog;
    
    private JSpinner lengthSpinner;
    
    protected static final String EDIT = "edit";
    
    private int value = 0;
    
    /** Creates a new instance of LengthTableCellEditor */
    public LengthTableCellEditor() {
        SpinnerModel lengthModel = new SpinnerNumberModel(0,0,255,1);
        lengthSpinner    = new JSpinner(lengthModel);
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
        lengthSpinner.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                value = ((Integer)lengthSpinner.getValue()).intValue();
                button.setText(Integer.toString(value));
            }}
        );
        
        
        dialog = new JDialog();
        dialog.setTitle("Field length");
        dialog.setLayout(new FlowLayout());
        dialog.add(lengthSpinner);
        
        JButton ok = new JButton("OK");
        ok.setActionCommand("OK");
        ok.addActionListener(this);
        
        dialog.add(ok);
    }
    
    
    public Object getCellEditorValue() {
        return Integer.valueOf(value);
    }
    
    public java.awt.Component getTableCellEditorComponent(JTable table, Object nv, boolean isSelected, int row, int column) {
        //lengthSpinner.setValue(nv);
        value = ((Integer)nv).intValue();
        //button.setText(((Integer)nv).toString());
        return button;
    }
    

    
    public void actionPerformed(ActionEvent e) {
        if ( EDIT.equals(e.getActionCommand() )) {
            button.setText(Integer.toString(value));
            
            dialog.pack();
            dialog.setLocationRelativeTo(button);
            dialog.setVisible(true);
            
            fireEditingStopped();
        } else {            
            value = ((Integer)lengthSpinner.getValue()).intValue();
            button.setText(Integer.toString(value));
            dialog.dispose();
        }
    }
    
}
