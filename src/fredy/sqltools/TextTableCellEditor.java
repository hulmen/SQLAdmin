/*
  
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

import sql.fredy.ui.ImageButton;
import javax.swing.*;
import javax.swing.JTable;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

/**
 *
 * @author sql@hulmen.ch
 */
public class TextTableCellEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    
    private JButton button;
    
    private JDialog dialog;
    
    private JTextArea area;
    
    private String buffer;
    
    protected static final String EDIT   = "edit";
    protected static final String ACCEPT = "accept";
    protected static final String CANCEL = "cancel";
    
    /** Creates a new instance of TextTableCellEditor */
    public TextTableCellEditor(String title,int rows, int columns) {
        area = new JTextArea(rows,columns);
        button = new JButton();
        button.setActionCommand(EDIT);
        button.addActionListener(this);
        button.setBorderPainted(false);
        dialog = new JDialog();
        dialog.setTitle(title);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(BorderLayout.CENTER,new JScrollPane(area));
        dialog.getContentPane().add(BorderLayout.SOUTH,buttonPanel());
        dialog.setModal(true);
        
    }
    
    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        
        ImageButton ok = new ImageButton(null,"ok.gif","accept entries");
        ok.setActionCommand(ACCEPT);
        ok.addActionListener(this);
        
        ImageButton cancel = new ImageButton(null,"cancel.gif","revert entries");
        cancel.setActionCommand(CANCEL);
        cancel.addActionListener(this);
        
        panel.add(ok);
        panel.add(cancel);
        
        panel.setBorder(new EtchedBorder());
        return panel;
    }
    
    public void actionPerformed(ActionEvent e) {
        if ( EDIT.equals(e.getActionCommand() )) {
            button.setText(area.getText());
            
            dialog.pack();
            dialog.setLocationRelativeTo(button);
            dialog.setVisible(true);
            
            fireEditingStopped();
        } else {
            if ( ACCEPT.equals(e.getActionCommand() )) {
                button.setText(area.getText());
                dialog.dispose();
                fireEditingStopped();
            }
            if ( CANCEL.equals(e.getActionCommand() )) {
                area.setText(buffer);
                dialog.dispose();
                fireEditingStopped();
            }
        }
        
        
    }
    
    public Object getCellEditorValue() {
        return area.getText();
    }
    
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        buffer = (String) value;
        area.setText((String)value);
        return button;
    }
    
}
