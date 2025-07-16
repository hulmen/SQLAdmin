/*
  ReferencesTableCellEditor.java
 
  Created on October 5, 2004, 2:10 PM
 
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
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sql@hulmen.ch
 */
public class ReferencesTableCellEditor  extends AbstractCellEditor implements TableCellEditor, ActionListener {
    
    private JButton button;
    
    private Logger logger = Logger.getLogger("sql.fredy.sqltools");
    
    private JDialog dialog;
    
    private JTextArea area = new JTextArea();
    
    private String reference;
    
    protected static final String EDIT   = "edit";
    
    /** Creates a new instance of ReferencesTableCellEditor */
    public ReferencesTableCellEditor(String title, String schema, java.sql.Connection con) {
        button = new JButton();
        button.setActionCommand("");
        button.addActionListener(this);
        button.setBorderPainted(false);
        dialog = new JDialog();
        dialog.setTitle(title);
        dialog.getContentPane().setLayout(new BorderLayout());
              
        SqlPickList sqlPicklist = new SqlPickList(this,schema);
        dialog.getContentPane().add(BorderLayout.CENTER,sqlPicklist);
        dialog.setModal(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        
            button.setText(e.getActionCommand());
            reference = e.getActionCommand();
            dialog.pack();
            dialog.setLocationRelativeTo(button);
            dialog.setVisible(true);                        
            fireEditingStopped();
            if (e.getActionCommand() != "") dialog.dispose();
    }
    public Object getCellEditorValue() {
        return reference.trim();
        
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        reference = (String) value;
        
        return button;
    }
    
}
