package sql.fredy.ui;

/** 
 
   ChooseColor is a part of Admin, done to change Colors.....


   Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
   for DB-Administrations, as create / delete / alter and query tables
   it also creates indices and generates simple Java-Code to access DBMS-tables
   and exports data into various formats
 
 
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
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.JFrame;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;

public class ChooseColor extends JPanel {

// Fredy's make Version
private static String fredysVersion = "Version 1.4  2. Jan. 2002 ";

public String getVersion() {return fredysVersion; }



    public JColorChooser cc;
    public JButton cancel, select;
    private JButton deflt;
    private Color defaultColor;
    public ChooseColor(Color defaultColor) {

	
	this.defaultColor = defaultColor;
	this.setLayout(new BorderLayout());
	cc = new JColorChooser();
	this.add("Center",cc);
	this.add("South",buttonPanel());
    }


    public Color getColor() { return cc.getColor();}


    private JPanel buttonPanel() {

	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

	deflt = new JButton("Default");
	deflt.setToolTipText("default color");
	deflt.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		cc.setColor(defaultColor);
		}});
	

	
	select = new JButton("Select");
	select.setToolTipText("Selects background Color");

	cancel = new JButton("Cancel");
	cancel.setToolTipText("leave as it is...");

	panel.add(deflt);
	panel.add(select);
	panel.add(cancel);
	return panel;

    }


    public static void main(String args[]) {

	JFrame frame = new JFrame();
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
	ChooseColor panel = new ChooseColor(Color.blue);
	panel.cancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		{System.exit(0);}
		}});
	

        frame.getContentPane().add("Center", panel);
	frame.pack();
        frame.setVisible(true);

    }
}
           
