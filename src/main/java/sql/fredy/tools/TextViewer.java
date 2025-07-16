package sql.fredy.tools;

/**
   TextViewer is used within the
   SLS (SimpleLogServer) to zoom a value.
   
   SimpleLogServer is part of Admin and is free software

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
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;

public class TextViewer extends JDialog {

    public JButton close;
    private JTextArea text;

    public void setText(String s) {
	text.setText(s);
	text.updateUI();
    }
    
    private JPanel textPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
       
	text = new JTextArea(10,100);
	text.setFont(new java.awt.Font("Monospaced", Font.PLAIN, 12));
	text.setEditable(false);

	panel.add(BorderLayout.CENTER,new JScrollPane(text));
	panel.setBorder(new BevelBorder(BevelBorder.RAISED));

	return panel;
    }

    private JPanel buttonPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
	panel.setBorder(new EtchedBorder());

	close = new JButton("Close");

	panel.add(close);
	return panel;
    }

    public TextViewer(JFrame f,String title,boolean m) {

        super(f,title,m);
	
	this.getContentPane().setLayout(new BorderLayout());
	this.getContentPane().add(BorderLayout.CENTER,textPanel());
	this.getContentPane().add(BorderLayout.SOUTH,buttonPanel());
	this.pack();
    }
}
	
