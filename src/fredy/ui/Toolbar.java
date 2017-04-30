package sql.fredy.ui;

/** 
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

public class Toolbar extends JPanel {

LoadImage loadImage = new LoadImage();
    
public JButton dbInfo,generateCode, exit, qbe, form, sqlMonitor;
public JButton dropTable, createTable, createIndex,createUser,createDB,dropDB;
public JButton background,execPrg,windows,motif,metal;
public JButton about;


    public Toolbar() {

	this.setLayout(new FlowLayout());

	JToolBar toolbar = new JToolBar();
	toolbar.setOrientation(JToolBar.VERTICAL);

	dbInfo = new JButton(loadImage.getImage("cameraflash.gif"));
	dbInfo.setToolTipText("Database Info");

	toolbar.add(dbInfo);

	generateCode = new JButton(loadImage.getImage("hammer.gif"));
	generateCode.setToolTipText("Generate Java Code");
	toolbar.add(generateCode);

	qbe  = new JButton(loadImage.getImage("binocular.gif"));
	qbe.setToolTipText("Guided Query");
	toolbar.add(qbe);

	form = new JButton(loadImage.getImage("documentdraw.gif"));
	form.setToolTipText("Form");
	toolbar.add(form);

	sqlMonitor  = new JButton(loadImage.getImage("computer.gif"));
	sqlMonitor.setToolTipText("SQL Monitor");
	toolbar.add(sqlMonitor);

	createTable = new JButton(loadImage.getImage("newsheet.gif"));
	createTable.setToolTipText("Create Table");
	toolbar.add(createTable);

	createIndex = new JButton(loadImage.getImage("updatecolumn.gif"));
	createIndex.setToolTipText("Create Index");
	toolbar.add(createIndex);

	createUser  = new JButton(loadImage.getImage("user.gif"));
	createUser.setToolTipText("Create mysql-User");
	toolbar.add(createUser);

        createDB    = new JButton(loadImage.getImage("data.gif"));
	createDB.setToolTipText("Create mysql-Database (must have access)");
	toolbar.add(createDB);


        dropTable   = new JButton(loadImage.getImage("deletesheet.gif"));
	dropTable.setToolTipText("Drop Table");
	toolbar.add(dropTable);

	dropDB      = new JButton(loadImage.getImage("delete.gif"));
	dropDB.setToolTipText("Drop Database");
	toolbar.add(dropDB);


        background =  new JButton(loadImage.getImage("palette.gif"));
	background.setToolTipText("change Background color");
	toolbar.add(background);

        about       = new JButton(loadImage.getImage("bulb.gif"));
	about.setToolTipText("about Admin");
	toolbar.add(about);

	exit        = new JButton(loadImage.getImage("exit.gif"));
	exit.setToolTipText("Exit Admin");
	toolbar.add(exit);
	this.add(toolbar);
    }


    private ImageIcon loadImage(String image) {
	return loadImage.getImage(image);
   }

    public static void main(String args[]) {
	JFrame frame = new JFrame();
	Toolbar tb = new Toolbar();
	frame.getContentPane().add(tb);
	frame.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent e) {}
	    public void windowClosed(WindowEvent e) {}
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	    public void windowDeactivated(WindowEvent e) {}
	    public void windowDeiconified(WindowEvent e) {}
	    public void windowIconified(WindowEvent e) {}
	    public void windowOpened(WindowEvent e) {}});
	
	frame.pack();
	frame.setVisible(true);
    }

}
