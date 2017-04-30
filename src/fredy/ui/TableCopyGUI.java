package sql.fredy.ui;

/** 
   TableCopyGUI is a GUI around TableCopy and is a part of Admin...
   Version 1.0  31.January 2000 
   Fredy Fischer 
  
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
import java.io.*;
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
import sql.fredy.sqltools.TableCopy;

public class TableCopyGUI extends JPanel {

// Fredy's make Version
private static String fredysVersion = "Version 1.4  2. Jan. 2002 ";

public String getVersion() {return fredysVersion; }

public  JButton cancel;
private JTextField sourceDriver, sourceUrl, sourceUser, sourceTable;
private JTextField destDriver,   destUrl,   destUser,   destTable;
private JPasswordField sourcePassword, destPassword;
private JCheckBox  sourceAuth, destAuth, createDst, emptyDst;

    private JPanel srcPanel() {

	FfAlignerLayoutPanel panel = new FfAlignerLayoutPanel();
	panel.setBorder(BorderFactory.createTitledBorder("Source"));

	sourceDriver   = new JTextField("",20);
	sourceUrl      = new JTextField("",20);
	sourceTable    = new JTextField("",20);
	sourceUser     = new JTextField("",20);
	sourcePassword = new JPasswordField("",20);

	sourceAuth = new JCheckBox("Authentication required");
	
	panel.addComponent(new JLabel("JDBC-Driver"));
	panel.addComponent(sourceDriver);
	panel.addComponent(new JLabel("JDBC-URL"));
	panel.addComponent(sourceUrl);
	panel.addComponent(new JLabel("Table"));
	panel.addComponent(sourceTable);

	panel.setNumberOfRows(2);
	panel.addComponent(new JLabel("User-ID"));
	panel.addComponent(sourceUser);
	panel.addComponent(new JLabel("Password"));
	panel.addComponent(sourcePassword);

	panel.setNumberOfRows(1);
	panel.addComponent(sourceAuth);
	sourceAuth.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (sourceAuth.isSelected() ) {
		    sourceUser.setEditable(true);
		    sourcePassword.setEditable(true); 
		} else {
		    sourceUser.setEditable(false);
		    sourcePassword.setEditable(false); 
		}
		}});


	return panel;
    }

    private JPanel destPanel() {

	FfAlignerLayoutPanel panel = new FfAlignerLayoutPanel();
	panel.setBorder(BorderFactory.createTitledBorder("Destination"));

	destDriver   = new JTextField("",20);
	destUrl      = new JTextField("",20);
	destTable    = new JTextField("",20);
	destUser     = new JTextField("",20);
	destPassword = new JPasswordField("",20);

	destAuth = new JCheckBox("Authentication required");
	
	panel.addComponent(new JLabel("JDBC-Driver"));
	panel.addComponent(destDriver);
	panel.addComponent(new JLabel("JDBC-URL"));
	panel.addComponent(destUrl);
	panel.addComponent(new JLabel("Table"));
	panel.addComponent(destTable);

	panel.setNumberOfRows(2);
	panel.addComponent(new JLabel("User-ID"));
	panel.addComponent(destUser);
	panel.addComponent(new JLabel("Password"));
	panel.addComponent(destPassword);

	createDst = new JCheckBox("Create Destination Table");
	emptyDst  = new JCheckBox("Empty Destination Table before inserting");

	destAuth.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (destAuth.isSelected() ) {
		    destUser.setEditable(true);
		    destPassword.setEditable(true); 
		} else {
		    destUser.setEditable(false);
		    destPassword.setEditable(false); 
		}
		}});
	panel.setNumberOfRows(1);
	panel.addComponent(destAuth);
	panel.addComponent(createDst);
	panel.addComponent(emptyDst);


	return panel;
    }	


    private JPanel buttonPanel() {

	JPanel panel = new JPanel();
	panel.setBorder(BorderFactory.createEtchedBorder());
	panel.setLayout(new FlowLayout());
	
	ImageButton doIt = new ImageButton("Import","documentin.gif",null);
  	          cancel = new ImageButton("Cancel","exit.gif",null);

	panel.add(doIt);
	doIt.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		doCopy();
		}});
	
	panel.add(cancel);

	return panel;
    }
 

    private void doCopy() {

	boolean exec = true;
	if ( sourceDriver.getText().length() < 1 ) exec = false;
	if ( destDriver.getText().length() < 1 ) exec = false;
	if ( sourceUrl.getText().length() < 1 ) exec = false;
	if ( destUrl.getText().length() < 1 ) exec = false;
	if ( sourceTable.getText().length() < 1 ) exec = false;
	if ( destTable.getText().length() < 1 ) exec = false;

	String sDriver=sourceDriver.getText();
	String sUrl=sourceUrl.getText();
	String sUser=sourceUser.getText();
	String sPassword= String.valueOf(sourcePassword.getPassword());
	boolean sAuth=false;
	if ( sourceAuth.isSelected() ) sAuth = true;
	String sTable  = sourceTable.getText();
	String dDriver = destDriver.getText(); 
	String dUrl    = destUrl.getText();
	String dUser   = destUser.getText();
	String dPassword= String.valueOf(destPassword.getPassword());
	boolean dAuth=false;
        if ( destAuth.isSelected() ) dAuth = true;
	String  dTable = destTable.getText();
	boolean cdTable=false;
        if ( createDst.isSelected() ) cdTable = true;
	boolean dEmpty = false;
        if ( emptyDst.isSelected() ) dEmpty = true; 
	boolean vb = false; 

        TableCopy tc = new TableCopy(sDriver, sUrl, sUser, sPassword, sAuth, sTable, dDriver, dUrl, dUser, dPassword, dAuth, dTable, cdTable, dEmpty, vb);


    }


    public TableCopyGUI() {


	this.setLayout(new BorderLayout());
	this.add("West",srcPanel());
	this.add("East",destPanel());
	this.add("South",buttonPanel());

    }

    public static void main(String args[]) {


	JFrame f = new JFrame("TableCopy");
	TableCopyGUI tcg = new TableCopyGUI();
	f.getContentPane().add(tcg);
	f.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent e) {}
	    public void windowClosed(WindowEvent e) {}
	    public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {}});
	    tcg.cancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
		}});
	f.pack();
	f.setVisible(true);
    }
}
	




