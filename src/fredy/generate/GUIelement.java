package sql.fredy.generate;

/** 
    GUIelement is part of the generator in Admin

 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 * and exports data into various formats
 *
 *
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
**/


import sql.fredy.ui.CloseableFrame;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.JFrame;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;

public class GUIelement extends JDialog {


  private String[] filter = { "JTextFilter.NUMERIC",
			      "JTextFilter.FLOAT",
			      "JTextFilter.ALPHA",
			      "JTextFilter.ALPHA_NUMERIC",
			      "JTextFilter.LOWERCASE",
			      "JTextFilter.UPPERCASE"  };

  private String[] fieldString = { "FieldPanel",
				   "AreaPanel",
				   "DBcomboBoxPanel",
				   "TwinBoxPanel" };

  private String[] fieldNoArea = { "FieldPanel",
				   "DBcomboBoxPanel",
				   "TwinBoxPanel" };

  private String[] fieldDate   = { "DateButtonPanel",
				   "DateFieldPanel" };


  private JComboBox fieldType,textFilter;

  private JPanel comp(String typ) {
    JPanel panel = new JPanel();

    textFilter = new JComboBox(filter);
    textFilter.setEditable(true);
    textFilter.setAlignmentX(Component.LEFT_ALIGNMENT);
    textFilter.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox)e.getSource();
                String newSelection = (String)cb.getSelectedItem();
		System.out.println("ComboBox Content: " + newSelection);
            }
    });    
    panel.add(textFilter);
    return panel;
  }

  public GUIelement() {
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(BorderLayout.CENTER,comp("int"));
  }
  public static void main(String args[] ) {
    CloseableFrame cf = new CloseableFrame();
    GUIelement ge = new GUIelement();
    ge.pack();
    ge.setVisible(true);
  }
}

