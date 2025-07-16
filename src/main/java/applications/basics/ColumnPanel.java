package applications.basics;

/** 



    ColumnPanel is an class representing a standard Column-GUI
    consisting of:
    - JLabel
    - JTextField
    - JTextFieldFilter
    - GridBagConstraints
    - Border
    - Foreground- and BackGroundColor
    - HTML-Fragment 
   


    Admin is a Tool around SQL-Databases to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

    Admin Version see below
    
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


import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

public class ColumnPanel {


  String name="";
  public void setName(String v) { name = v; }
  public String getName() { return name; }


  JTextField textField;
  public JTextField getTextField() { return textField; }
  public void setTextField(int l,String t) { textField = new JTextField(t,l); }
  public String toString() { return textField.getText(); }
  
  GridBagConstraints gbcLabel;
  public GridBagConstraints getGbcLabel() { return gbcLabel; }
  public void setGbcLabel(GridBagConstraints v) { gbcLabel = v; }

  GridBagConstraints gbcText;
  public GridBagConstraints getGbcText() { return gbcText; }
  public void setGbcText(GridBagConstraints v) { gbcText = v; }


  public void setStandardConstraints() {
	Insets insets = new Insets(2,2,2,2);
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.anchor= GridBagConstraints.EAST;
        gbc.fill  = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	gbc.weighty = 1.0;
	gbc.gridwidth = 1;
	gbc.insets = insets;
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbcLabel = gbc;

	gbc.anchor = GridBagConstraints.WEST;
        gbc.fill  = GridBagConstraints.BOTH;
	gbc.gridx  = 1;
	gbcText    = gbc;


  }

  public JPanel getPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    
    panel.add(new JLabel(getName()),gbcLabel);
    panel.add(textField,gbcText);
    
    return panel;
    
  }

  public void init() {

    gbcLabel = new GridBagConstraints();
    gbcText  = new GridBagConstraints();
    
  }

  public ColumnPanel(String name,String content, int length) {
    setName(name);
    setTextField(length,content);
    init();
  }
  public ColumnPanel(String name,int length) {
    setName(name);
    setTextField(length,"");
    init();
  }
  public ColumnPanel(String name) {
    setName(name);
    setTextField(15,"");
    init();
  }

  public ColumnPanel() {
    setName("FieldName");
    setTextField(15,"");
    init();
  }

  public static void main(String args[] ) {

    ColumnPanel cp = new ColumnPanel(args[0]);
    CloseableFrame cf = new CloseableFrame("ColumnPanel");
    cf.getContentPane().add(cp.getPanel());
    cf.pack();
    cf.setVisible(true);
  }
}




