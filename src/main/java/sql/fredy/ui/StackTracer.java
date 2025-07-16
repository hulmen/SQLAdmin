package sql.fredy.ui;

/** 
   StackTracer is used to display StackTraces of exceptions
   Version 1.1 17. January 2003
   Fredy Fischer 
 
 
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
import java.util.logging.*;


public class StackTracer extends JDialog {

    private Logger logger;
    private JTextArea message;
    private JTextArea trace;

    public ImageButton cancel;

    private JPanel msgPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());

	message = new JTextArea(5,100);
	message.setEditable(false);
	message.setFont(new  java.awt.Font("Monospaced", Font.PLAIN, 12));

	panel.add(BorderLayout.WEST,new ImageLabel(null,"stop.gif",null));
	panel.add(BorderLayout.CENTER,new JScrollPane(message));

	panel.setBorder(new TitledBorder(new EtchedBorder(),"Message"));

	return panel;
    }

    private JPanel stackPanel() {
	JPanel panel = new JPanel();

	panel.setLayout(new BorderLayout());

	trace = new JTextArea(10,100);
	trace.setEditable(false);
	trace.setFont(new  java.awt.Font("Monospaced", Font.PLAIN, 12));
	panel.add(BorderLayout.WEST,new ImageLabel(null,"thread.gif",null));
	panel.add(BorderLayout.CENTER,new JScrollPane(trace));
	panel.setBorder(new TitledBorder(new EtchedBorder(),"Stack Trace"));

	return panel;
    }

    private JPanel buttonPanel() {
	JPanel panel = new JPanel();
	cancel = new ImageButton(null,"exit.gif","Exit");

	ImageButton save = new ImageButton(null,"save.gif","Save to File");
	ImageButton copy = new ImageButton(null,"copy.gif","copy trace to clipboard");

	panel.setLayout(new FlowLayout());
	panel.add(save);
	panel.add(copy);
	panel.add(cancel);

	panel.setBorder(new EtchedBorder());

	return panel;
    }
       
    public void setExcpt(Exception e) {
	try {	
	    if ( e.getCause().getMessage() != null )
		setMessage(e.getCause().getMessage().toString());	    
	    setST(e.getStackTrace());	
	} catch (Exception exx) { e.printStackTrace(); }

    }

    private void setMessage(String m ){
	message.setText(m);
	message.updateUI();
    }

    private void setST(StackTraceElement[] ste) {
	for (int i=0;i < ste.length;i++ ) {
	    appendST(ste[i] );   
	    trace.append("\n");
	}
	trace.append("\n");
	trace.updateUI();
    }
    private void appendST(StackTraceElement ste) {

	trace.append("at " + ste.getClassName() );
	trace.append("." + ste.getMethodName());
	trace.append(" ( " +  ste.getFileName());
	trace.append(": " +Integer.toString(ste.getLineNumber()));
	trace.append(" )");

    }

    public StackTracer(JFrame f,String title,boolean m) {
	super(f,title,m);

	logger = Logger.getLogger("sql.fredy.admin");
	this.getContentPane().setLayout(new BorderLayout());
	this.getContentPane().add(BorderLayout.NORTH,msgPanel());
	this.getContentPane().add(BorderLayout.CENTER,stackPanel());
	this.getContentPane().add(BorderLayout.SOUTH,buttonPanel());

	this.pack();

    }

    public static void main(String args[]) {

	JFrame aFrame = new JFrame("TEST");
	aFrame.setVisible(true);
	aFrame.addWindowListener(new WindowAdapter() {
		public void windowActivated(WindowEvent e) {
		}
		public void windowClosed(WindowEvent e){
		    
		}
		public void windowClosing(WindowEvent e) {
		    
		    System.exit(0);
		}
		public void windowDeactivated(WindowEvent e) {
		    
		}
		public void windowDeiconified(WindowEvent e) {
		    
		}
		public void windowIconified(WindowEvent e) {
		    
		}
		public void windowOpened(WindowEvent e) {
		    
		}
	    });
	


	StackTracer st = new StackTracer(aFrame,"Exception",true);
	st.cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		}
	    });
	Exception e = new Exception("This is a Testexception");
	e.initCause(new Throwable("this is the cause"));
	st.setExcpt(e);
	st.setVisible(true);


    }
}
