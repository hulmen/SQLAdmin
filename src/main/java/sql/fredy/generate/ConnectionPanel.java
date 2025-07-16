package sql.fredy.generate;

/**
   This little panel is to enter the user parameters to connect to the DB
**/



/**  * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
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


import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import java.io.File;
import javax.swing.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import javax.swing.tree.*;
import java.awt.datatransfer.*;
import java.lang.reflect.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;




public class ConnectionPanel extends JPanel {

    private JTextField host,user;
    private JPasswordField password;

    public String getUser()     { return user.getText(); }
    public String getHost()     { return host.getText(); }
    public String getPassword() { return String.valueOf(password.getPassword()); }
    public void setHost(String v) { host.setText(v); }
    public void setUser(String v) { user.setText(v); }
    public void setPassword(String v) { password.setText(v); }

    public ConnectionPanel() {
	this.setLayout(new GridBagLayout());
	this.setBorder(new TitledBorder(new EtchedBorder(),"Connection Values"));

	GridBagConstraints gbc;
	Insets insets = new Insets(2,2,2,2);
	gbc = new GridBagConstraints();
	gbc.weightx = 1.0;     
	gbc.anchor= GridBagConstraints.NORTHWEST;
	gbc.fill  = GridBagConstraints.HORIZONTAL;
	gbc.insets = insets;

	gbc.gridx = 0;
	gbc.gridy = 0;        
	this.add(new JLabel("Host"),gbc);

	gbc.gridx = 1;
	gbc.gridy = 0; 
	host = new JTextField("",15);
	this.add(host,gbc);

	gbc.gridx = 0;
	gbc.gridy = 1;        
	this.add(new JLabel("User"),gbc);

	gbc.gridx = 1;
	gbc.gridy = 1; 
	user = new JTextField("",15);
	this.add(user,gbc);

       	gbc.gridx = 0;
	gbc.gridy = 2;        
	this.add(new JLabel("Password"),gbc);

	gbc.gridx = 1;
	gbc.gridy = 2; 
	password = new JPasswordField("",15);
	this.add(password,gbc);

    }
}
