package sql.fredy.ui;

/** 
   MsgBoard displays a Message in a Frame and is a part of Admin...
   Version 1.1 01. June 1999
   Fredy Fischer 
 
   this has been created to query mySQL-Databases
   it only returns a JPanel, so it can easily been 
   used in different kind of windows
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
import java.awt.datatransfer.*;


public class MsgBoard  {

// Fredy's make Version
private static String fredysVersion = "Version 1.4  2. Jan. 2002 ";

public String getVersion() {return fredysVersion; }

    public int pollRate = 60000;
    public File file=null;
    private JTextArea text, sysText;
    public JFrame frame;
    public JButton cancel;
    public ImageButton setLog;

    public MsgBoard () {


	frame = new JFrame("Console Info");
	frame.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent e) {}
	    public void windowClosed(WindowEvent e) {}
	    public void windowClosing(WindowEvent e) {frame.dispose();}
	    public void windowDeactivated(WindowEvent e) {}
	    public void windowDeiconified(WindowEvent e) {}
	    public void windowIconified(WindowEvent e) {}
	    public void windowOpened(WindowEvent e) {}});
	

	frame.getContentPane().setLayout(new BorderLayout());


	JPanel buttonpanel = new JPanel();
	buttonpanel.setLayout(new FlowLayout());

	ImageButton clear = new ImageButton(null,"clear.gif","Clear Messages");
	clear.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		text.setText("Admin-Messages\n");
		sysText.setText("System Messages\n");
		}});

	cancel = new JButton();

	try {
	    ImageButton imb = new ImageButton();
	    ImageIcon i1 = imb.getImageIcon("exit.gif");
	    ImageIcon i2 = imb.grayed(i1.getImage());
	    cancel.setIcon(i2);
	    cancel.setRolloverIcon(i1);
	    cancel.setRolloverEnabled(true);
	} catch (Exception e) { ; }

	cancel.setToolTipText("Exit");
	cancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		frame.setVisible(false);
		}});
	

	setLog = new ImageButton(null,"opendocument.gif","Set logfile");
	
	ImageButton refresh= new ImageButton(null,"refresh.gif","Refresh Log-output");
	refresh.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		refreshOutput();
		}});
	
	ImageButton tester= new ImageButton(null,"cameraflash.gif","Test");
	tester.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		test();
		}});
	


	buttonpanel.add(clear);
	buttonpanel.add(setLog);
	buttonpanel.add(refresh);
	//buttonpanel.add(tester);

	buttonpanel.add(cancel);
	frame.getContentPane().add(BorderLayout.SOUTH,buttonpanel);

	addSplit();
	addSlider();
	frame.pack();
	frame.setVisible(false);

	file = new File( System.getProperty("user.home"),"admin.log");

	
    }

    public void poller() {

        Thread t = new Thread() {
	    public void run() {
		
		while (true) {
		    refreshOutput();
		    try {
			sleep(pollRate);
		    } catch (Exception e) {;}
		}
	    }
	};
	t.start();
    }


    public void openFile() {
	
	JFileChooser chooser = new JFileChooser(file.getParent());
	chooser.setDialogType(JFileChooser.OPEN_DIALOG);
	chooser.setDialogTitle("Select logfile");
	
	int returnVal = chooser.showSaveDialog(frame);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	    file = chooser.getSelectedFile();
	    verify();
	} else { file = null; }

    }

    private void verify() {


	if (file.exists() ) {

             String string1 = "Overwrite";
             String string2 = "Cancel";
             Object[] options = {string1, string2};
             int n = JOptionPane.showOptionDialog(null,
                              "This file already exists!",
                              "File exists",
                              JOptionPane.YES_NO_OPTION,
                              JOptionPane.QUESTION_MESSAGE,
                              null,     //don't use a custom Icon
                              options,  //the titles of buttons
                              string2); //the title of the default button
               if (n == JOptionPane.NO_OPTION)   file = null;
	}
    }



    private void addSplit() {


	JPanel upperPanel = new JPanel();
        upperPanel.setBorder(new TitledBorder(new EtchedBorder(),"Admin Messages"));
	JPanel lowerPanel = new JPanel();
        lowerPanel.setBorder(new TitledBorder(new EtchedBorder(),"System Messages"));

        text = new JTextArea("Admin-Messages\n");
	text.setEditable(false);
	java.awt.Font f = new  java.awt.Font("Monospaced", Font.PLAIN, 12);
	text.setFont(f);
	text.setBackground(Color.lightGray);
	text.setToolTipText("Admin-Messages displayed here");
	JScrollPane scrollpane = new JScrollPane(text);

        sysText = new JTextArea("System Messages\n");
	sysText.setEditable(false);

	sysText.setFont(f);
	sysText.setBackground(Color.lightGray);
	sysText.setToolTipText("Java System-Messages displayed here");
	JScrollPane scrollsys = new JScrollPane(sysText);



	JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,scrollsys,scrollpane);
	pane.setOneTouchExpandable(true);
	frame.getContentPane().add(BorderLayout.CENTER,pane);

    }


    private void refreshOutput() {

		String text="";
		String s;
	     
		try {		    
		    DataInputStream ipstr = new DataInputStream(
					    new BufferedInputStream(
                                            new FileInputStream(file)));
		    BufferedReader  bufrd = new BufferedReader(
                                            new InputStreamReader(ipstr));
		    while ((s = bufrd.readLine()) != null) {
			text = text + "\n" + s;
		    }
		    ipstr.close();
		} catch(IOException exep) {
		    System.out.println("IO Fehler");
		}
	    sysText.setText("System Messages\n" + text);

    }


    private void addSlider() {

	JSlider slider = new JSlider(SwingConstants.VERTICAL,0,600,pollRate/1000);
	slider.setMajorTickSpacing(60);
	slider.setMinorTickSpacing(10);
	slider.setPaintTicks(true);
	slider.setPaintLabels(true);
	slider.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent event) {
		JSlider s = (JSlider)event.getSource();
		pollRate = s.getValue() * 1000;
		System.out.println("new Pollrate is: " + pollRate/1000 + " seconds");
	    }
	});
	slider.setToolTipText("Changes Pollingrate");
        frame.getContentPane().add(BorderLayout.EAST,slider);				 
    }


    private void test() { System.out.println("TEST-Message"); }


    public void addText(String t) {
	text.append(t + "\n");
	frame.setVisible(true);
    }

    public void addSysMsg(String t) {
	sysText.append(t + "\n");
	frame.setVisible(true);
    }

    public static void main(String args[]) {
	MsgBoard m = new MsgBoard();
	m.frame.setVisible(true);
    }

}
