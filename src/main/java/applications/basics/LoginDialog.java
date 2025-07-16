package applications.basics;

/** Logindialog is a Basic LoginDialog and is a part of Admin...
 *  Version 1.1 01. June 1999
 *  Fredy Fischer 
 *
 *  this has been created to query mySQL-Databases
 *  it only returns a JPanel, so it can easily been 
 *  used in different kind of windows
 */


/** 
    Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
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
import java.lang.reflect.*;

public class LoginDialog extends JDialog {

      private JTextField     askUser, askHost, askDatabase, askSchema;
      private JPasswordField askPassword;
      


	public ImageButton connect,cancel;
	String aHost=null;
  
    /**
       * Get the value of host.
       * @return Value of host.
       */
    public String getAHost() {return askHost.getText();}
    
    /**
       * Set the value of host.
       * @param v  Value to assign to host
       */
    public void setAHost(String  v) {
	this.aHost = v;
	askHost.setText(v);
    }
    
    
    String aUser=null;
    
    /**
    	* Get the value of user
    	* @return Value of user
    	*/
    public String getAUser() { return askUser.getText(); }
    
    /**
       * Set the Value of user
       * @param v Value to assign to user
       */
    public void setAUser(String v) { 
	this.aUser = v; 
	askUser.setText(v);
    }
    
    String aPassword=null;
    
    /**
    	* Get the value of password
    	* @return Value of password
    	*/
    public String getAPassword() { return String.valueOf(askPassword.getPassword()); }
    
    /**
       * Set the Value of password
       * @param v Value to assign to password
       */
    public void setAPassword(String v) { 
	this.aPassword = v;
	askPassword.setText(v); 
    }
    
    String aDatabase=null;
    
    /**
    	* Get the value of database
    	* @return Value of database
    	*/
    public String getADatabase() { return askDatabase.getText(); }
    
    /**
       * Set the Value of Database
       * @param v Value to assign to Database
       */
    public void setADatabase(String v) { 
	this.aDatabase = v; 
	askDatabase.setText(v);
    }
    
    
    String aSchema="%";
    
    /**
    	* Get the value of schema
    	* @return Value of schema
    	*/
    public String getASchema() { return askSchema.getText(); }
    
    /**
       * Set the Value of schema
       * @param v Value to assign to schema
       */
    public void setASchema(String v) { 
	this.aSchema = v; 
	askSchema.setText(v);
    }
    

    public LoginDialog() {
	this.getContentPane().add(loginpanel());
	this.pack();
	this.setVisible(true);

    }

    private JPanel loginpanel() {

      JPanel panel = new JPanel();
      panel.setLayout(new GridBagLayout());
      GridBagConstraints gbc;
      Insets insets = new Insets(5,5,5,5);
      gbc = new GridBagConstraints();
      gbc.weightx = 1.0;     
      gbc.anchor= GridBagConstraints.WEST;
      gbc.fill  = GridBagConstraints.NONE;
      gbc.insets = insets;


      askUser = new JTextField(15);
      askUser.setText(getAUser());
  
      
      askHost = new JTextField(15);
      askHost.setText(getAHost());
      
      askDatabase = new JTextField(15);
      askDatabase.setText(getADatabase());
      
      askSchema = new JTextField(15);
      askSchema.setText(getASchema());
      
      askPassword = new JPasswordField(15);
      
      gbc.gridx = 0;
      gbc.gridy = 0;
      gbc.anchor= GridBagConstraints.EAST;
      gbc.fill  = GridBagConstraints.HORIZONTAL;
      panel.add(new JLabel("Host"),gbc);
      gbc.gridx = 1;
      gbc.anchor= GridBagConstraints.WEST;
      gbc.fill  = GridBagConstraints.HORIZONTAL; 
      panel.add(askHost,gbc);
      
      gbc.gridx = 0;
      gbc.gridy = 1; 
      gbc.anchor= GridBagConstraints.EAST;
      gbc.fill  = GridBagConstraints.HORIZONTAL;     
      panel.add(new JLabel("User"),gbc);
      gbc.gridx = 1;
      gbc.anchor= GridBagConstraints.WEST;
      gbc.fill  = GridBagConstraints.HORIZONTAL; 
      panel.add(askUser,gbc);
      
      gbc.gridx = 0;
      gbc.gridy = 2;
      gbc.anchor= GridBagConstraints.EAST;
      gbc.fill  = GridBagConstraints.HORIZONTAL;
      panel.add(new JLabel("Password"),gbc);
      gbc.gridx = 1;
      gbc.anchor= GridBagConstraints.WEST;
      gbc.fill  = GridBagConstraints.HORIZONTAL; 
      panel.add(askPassword,gbc);
      
      gbc.gridx = 0;
      gbc.gridy = 3; 
      gbc.anchor= GridBagConstraints.EAST;
      gbc.fill  = GridBagConstraints.HORIZONTAL;
      panel.add(new JLabel("Database"),gbc);
      gbc.gridx = 1;
      gbc.anchor= GridBagConstraints.WEST;
      gbc.fill  = GridBagConstraints.HORIZONTAL; 
      panel.add(askDatabase,gbc);
      

      gbc.gridx = 0;
      gbc.gridy = 4;
      gbc.anchor= GridBagConstraints.EAST;
      gbc.fill  = GridBagConstraints.HORIZONTAL;
      panel.add(new JLabel("Schema"),gbc);
      gbc.gridx = 1;
      gbc.anchor= GridBagConstraints.WEST;
      gbc.fill  = GridBagConstraints.HORIZONTAL; 
      panel.add(askSchema,gbc);
      
      JPanel buttonPanel = new JPanel();
      connect = new ImageButton(null,"plug.gif","Connect to database");
      cancel  = new ImageButton(null,"exit.gif","Quit");
      buttonPanel.add(connect);
      buttonPanel.add(cancel);
      buttonPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));    
      panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));	
    	
      JPanel aPanel = new JPanel();
      aPanel.setLayout(new BorderLayout());
      aPanel.add(panel,BorderLayout.CENTER);
      aPanel.add(buttonPanel,BorderLayout.SOUTH);

      return aPanel;
    }
}
