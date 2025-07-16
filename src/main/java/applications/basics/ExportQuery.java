package applications.basics;

/** Export Query exports a query to a file and is a part of Admin...
 *  Version 1.0 15. Aug. 1999
 *  Fredy Fischer 
 *
 *  this has been created to export SQL-Resultsets
 */


/** Admin is a Tool around SQL to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

    		       Fredy Fischer
		       Hulmenweg 36
		       8405 Winterthur
		       Switzerland

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
import java.io.*;
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

public class ExportQuery extends Thread { 

    private File file = null;
    private ResultSet sqlresult;
    private JDialog dialog;
    private JTextArea errMsg;
    //private JTextArea info1,info2;
    public ImageButton cancel;
    private JProgressBar progressBar;
    public JFrame frame;
    String fieldSeparator;
   
    
    int numberOfRows;
    
    /**
       * Get the value of numberOfRows.
       * @return Value of numberOfRows.
       */
    public int getNumberOfRows() {return numberOfRows;}
    
    /**
       * Set the value of numberOfRows.
       * @param v  Value to assign to numberOfRows.
       */
    public void setNumberOfRows(int  v) {this.numberOfRows = v;}
    
 
    /**
       * Get the value of fieldSeparator.
       * @return Value of fieldSeparator.
       */
    public String getFieldSeparator() {return fieldSeparator;}
    
    /**
       * Set the value of fieldSeparator.
       * @param v  Value to assign to fieldSeparator.
       */
    public void setFieldSeparator(String  v) {this.fieldSeparator = v;}
    

   String host;
    
    /**
       * Get the value of host.
       * @return Value of host.
       */
    public String getHost() {return host;}
    
    /**
       * Set the value of host.
       * @param v  Value to assign to host.
       */
    public void setHost(String  v) {this.host = v;}
    

    String user;
    
    /**
       * Get the value of user.
       * @return Value of user.
       */
    public String getUser() {return user;}
    
    /**
       * Set the value of user.
       * @param v  Value to assign to user.
       */
    public void setUser(String  v) {this.user = v;}
    

    String password;
    
    /**
       * Get the value of password.
       * @return Value of password.
       */
    public String getPassword() {return password;}
    
    /**
       * Set the value of password.
       * @param v  Value to assign to password.
       */
    public void setPassword(String  v) {this.password = v;}
    

    String database;
    
    /**
       * Get the value of database.
       * @return Value of database.
       */
    public String getDatabase() {return database;}
    
    /**
       * Set the value of database.
       * @param v  Value to assign to database.
       */
    public void setDatabase(String  v) {this.database = v;}
    
    
    boolean gui=false;
    
    /**
       * Get the value of gui.
       * @return Value of gui.
       */
    public boolean getGui() {return gui;}
    
    /**
       * Set the value of gui.
       * @param v  Value to assign to gui.
       */
    public void setGui(boolean  v) {this.gui = v;}
    


    private String countQuery(String q) {
  
      String q1 = q.toLowerCase();
      int i = q1.indexOf("select");
      int j = q1.indexOf("from");
      //q = "select count(" + q.substring(i+6,j) + ") " + q.substring(j);
      q = "select count(*) " + q.substring(j);
      return q;
   }


    private void editQuery() {
	dialog = new JDialog(frame,"Edit query",true);
	dialog.getContentPane().setLayout(new BorderLayout());

	final JTextArea theQuery = new JTextArea(10,10);
	theQuery.setText(getQuery());
	JScrollPane scrollpane = new JScrollPane(theQuery);
	
	ImageButton ok = new ImageButton("OK","ok.gif",null);
	ok.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setQuery(theQuery.getText());
		dialog.dispose();
		}});
	ImageButton quit = new ImageButton("Cancel","exit.gif",null);
	quit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		dialog.dispose();
		}});


	JPanel d_bp = new JPanel();
	d_bp.add(ok);
	d_bp.add(quit);

	dialog.getContentPane().add(scrollpane,BorderLayout.CENTER);
	dialog.getContentPane().add(d_bp,BorderLayout.SOUTH);
	dialog.pack();
	dialog.setVisible(true);


    }

    private void createGUI() {
	frame = new JFrame("Fredy's export-tool");
	
	frame.getContentPane().setLayout(new BorderLayout());

	progressBar = new JProgressBar();
	frame.getContentPane().add(progressBar,BorderLayout.NORTH);

	JPanel bp = new JPanel();
	bp.setLayout(new FlowLayout());

	cancel = new ImageButton("Cancel","exit.gif","Exit");
	bp.add(cancel);

	ImageButton edit = new ImageButton("Edit query","edit.gif","Edit query");
	edit.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		editQuery();
		}});	
	bp.add(edit);

	ImageButton selFile = new ImageButton("Select file","open.gif","Select File to store into");
	selFile.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		selectFile();
		}});
	bp.add(selFile);

	ImageButton exec = new ImageButton("Execute","exec.gif","Export");
	exec.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		export();
		}});
	bp.add(exec);

	errMsg = new JTextArea(5,25);
	JScrollPane scp = new JScrollPane(errMsg);	
	frame.getContentPane().add(scp,BorderLayout.CENTER);
	frame.getContentPane().add(bp,BorderLayout.SOUTH);
	frame.pack();
	frame.setVisible(true);




    }

    public void export() { this.start(); }

    private void setFile(String f) {
	file = new File(f);
   
    }


    public ExportQuery (String host,String user, String password, String database, String query, String file,boolean gui) {


	setHost(host);
	setUser(user);
	setPassword(password);
	setDatabase(database);
	setQuery(query);
	setFieldSeparator(";");
	setGui(gui);
	setFile(file);
	if (getGui() ) createGUI();
	if (! getGui() ) this.start();


    }

    public ExportQuery (String host,String user, String password, String database) {


	setHost(host);
	setUser(user);
	setPassword(password);
	setDatabase(database);
	setQuery(null);
	//	setFile(null);
	setFieldSeparator(";");
	setGui(false);

    }
    
    String query;
    
    /**
       * Get the value of query.
       * @return Value of query.
       */
    public String getQuery() {return query;}
    
    /**
       * Set the value of query.
       * @param v  Value to assign to query.
       */
    public void setQuery(String  v) {this.query = v;}
    
    public void selectFile() {


	JFileChooser chooser = new JFileChooser();
	chooser.setDialogType(JFileChooser.SAVE_DIALOG);
	chooser.setDialogTitle("Select Exportfile");
	
	int returnVal = chooser.showSaveDialog(frame);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	    file = chooser.getSelectedFile();
	    verify();
	} else { file = null; }

    }

    public void verify() {

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


  private void pgb() {
  
	  t_connect con = new t_connect(getHost(), getUser(), getPassword(),getDatabase());  
	  if (con.getError() != null) message("Connection Error: "+ con.getError());
	  
	  try {
	      message("Counting rows for "  + countQuery(getQuery()));
	      ResultSet rs = con.stmt.executeQuery(countQuery(getQuery()));
	      rs.next();
	      setNumberOfRows(rs.getInt(1));
	      message("There are " + Integer.toString(getNumberOfRows()) + " rows to proceed");
	      if (getGui() ) {
		  progressBar.setMinimum(0);
		  progressBar.setMaximum(getNumberOfRows());
		  progressBar.setValue(0);
		  progressBar.setStringPainted(true);
		  progressBar.updateUI();
	      }
	      rs.close();
	  } catch (Exception e) { 
	      message("Count-Error: (query=\n" + countQuery(getQuery()) +")\n " + e.getMessage().toString());
	  }
	  
	  con.close();
  }


    public void run() {

	if ( (getQuery() != null )  && ( file != null ) ) {
	         message("Connecting to database...");
	         t_connect con = new t_connect(getHost(), getUser(), getPassword(),getDatabase());  
		 if (con.getError() != null) {
		     message("Connection Error: "+ con.getError());
		 } else { message("Successfully connected"); }
		     
		pgb();

		 try {
		   sqlresult = con.stmt.executeQuery(getQuery());
		 } catch (Exception e) { 
		   message("Query-Error: " +e.getMessage().toString()); 
		 }

		 String fileContent = "";
		 try {
		     ResultSetMetaData metaData = sqlresult.getMetaData();
		     int columnCount = metaData.getColumnCount();

		     // create Header
		     for (int i = 0; i < columnCount; i++) {
			 if (i >0) fileContent = fileContent + getFieldSeparator();
			 fileContent = fileContent + "\"" + metaData.getColumnName(i+1) + "\"";
		     }
		     fileContent = fileContent + System.getProperty("line.separator");

		     while ( sqlresult.next() ) {
			 for (int i = 0; i < columnCount; i++) {
			   try {			        
				if (i >0) fileContent = fileContent + getFieldSeparator();
				switch (metaData.getColumnType(i+1) ) {
				case java.sql.Types.INTEGER:
				    fileContent = fileContent + Integer.toString(sqlresult.getInt(i+1));
				    break;
				case java.sql.Types.FLOAT:
				    fileContent = fileContent + Float.toString(sqlresult.getFloat(i+1));
				    break;
				case java.sql.Types.DOUBLE:
				    fileContent = fileContent + Double.toString(sqlresult.getDouble(i+1));
				    break;
				case java.sql.Types.DATE:
				    fileContent = fileContent + "\"" + sqlresult.getDate(i+1).toString() + "\"";
				    break;
				default:
				    fileContent = fileContent + "\"" + sqlresult.getString(i+1) + "\"";
				    break;
				}
			  			      
			   } catch (Exception excp) { 			    
			     message(excp.getMessage()); }
			 }
			
			  fileContent = fileContent + System.getProperty("line.separator");
			  if (getGui() ){
			      progressBar.setValue(progressBar.getValue()+1);		 
			  } else { System.out.print("."); }
		     }
		 } catch (Exception e) { message("Exception while exporting: " + e.getMessage().toString());
		 }
		con.close();
		save(fileContent);
 
	} else { 
	    message("Either query is empty or no output-file is set");
	}

       
    }

    private void save(String s) {
       
	try {
	      	DataOutputStream outptstr = new DataOutputStream(
		     new BufferedOutputStream(
		     new FileOutputStream(file)));
			 outptstr.writeBytes(s);
			 outptstr.flush();
			 outptstr.close();
			 message("successfully exported...");
	} catch(IOException execp) { 
	    message(execp.toString()); 
	    if ( ! getGui() ) System.out.println(s); 
	}
    }


    public void message(String msg) {

	if (getGui() )  {
	    if (msg != null) errMsg.append(msg + "\n");
	} else { System.out.println(msg); }

    }

    public static void main(String args[] ) {
	if (args.length < 6 ) {
	    System.out.println("Syntax: java applications.basics.ExportQuery host user password database query targetFile [gui]");
	    System.exit(0);
	}

        boolean g=false;
	if ( args.length == 7)    if (args[6].toLowerCase().startsWith("gui") ) g = true;
	    

	ExportQuery eq = new ExportQuery(args[0], args[1], args[2], args[3],args[4],args[5],g);

	if (g) {
	    eq.cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		}});
	    eq.frame.addWindowListener(new WindowAdapter() {
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {System.exit(0);}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}});
	}
	
	
    }
}
	    
