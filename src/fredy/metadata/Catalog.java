package sql.fredy.metadata;

/** 
 * Catalog is a part of Admin. It displays a number of MetaData Information around a 
 * catalog.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
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
import javax.swing.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.TableColumn;
import javax.swing.DefaultCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

import sql.fredy.share.t_connect;

public class Catalog extends JPanel {


// Fredy's make Version
private static String fredysVersion = "Version 1.4 2. Jan. 2002";

public String getVersion() {return fredysVersion; }

    t_connect con;
    
    /**
     * Get the value of con.
     * @return value of con.
     */
    public t_connect getCon() {
	return con;
    }
    
    /**
     * Set the value of con.
     * @param v  Value to assign to con.
     */
    public void setCon(t_connect  v) {
	this.con = v;
    }
    

    public JButton cancel;
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
    
    String table;
    
    /**
       * Get the value of table.
       * @return Value of table.
       */
    public String getTable() {return table;}
    
    /**
       * Set the value of table.
       * @param v  Value to assign to table.
       */
    public void setTable(String  v) {this.table = v;}

    boolean standAlone=true;
    
    /**
     * Get the value of standAlone.
     * @return value of standAlone.
     */
    public boolean isStandAlone() {
	return standAlone;
    }
    
    /**
     * Set the value of standAlone.
     * @param v  Value to assign to standAlone.
     */
    public void setStandAlone(boolean  v) {
	this.standAlone = v;
    }
    

    
    private Vector name, type,size, isNull, remarks, defaults;

    public Catalog(String host, String user, String password, String database, String table) {

       setHost(host);
       setUser(user);
       setPassword(password);
       setDatabase(database);
       setTable(table);
       setStandAlone(true);
       con = new t_connect(getHost(),getUser(),getPassword(),getDatabase());
       
       inits();

       }

    public Catalog(t_connect con, String table) {

	setStandAlone(false);
	setHost(con.getHost());
	setUser(con.getUser());
	setPassword(con.getPassword());
	setDatabase(con.getDatabase());
	setCon(con);
	setTable(table);
	inits();	

    }
    

    private void inits() { 

	   this.setLayout(new BorderLayout());
	   this.add("Center",infoPanel());
	   this.add("South",buttonPanel());


    }

    private JPanel infoPanel() {

	   JPanel panel = new JPanel();
	   panel.setLayout(new BorderLayout());

	   JTextArea t = new JTextArea();
	   JScrollPane scrollpane = new JScrollPane(t);
	   panel.add("Center",t);
	   DatabaseMetaData dmd = null;
	   ResultSet         rs = null;

	   try {
	       dmd = con.con.getMetaData();
 
	       t.setText("SCHEMATAS\n");
	       rs = dmd.getSchemas();

	       while (rs.next()) { 
		   t.append(rs.getString("TABLE_SCHEM")+"\n");
	       }

	       rs = dmd.getCatalogs();
	       t.append("\nCATALOGS\n");
	       while (rs.next()) { 
		   String t1 = rs.getString("TABLE_CAT");
		   setTable(t1);
		   t.append(getTable() +"\n");
	     
	       }
	       
	       rs = dmd.getColumnPrivileges(null, null, getTable(), "*");
	       t.append("\nPrivilegs\n");
	       while (rs.next()) { 

		   t.append("Table: "   + rs.getString("TABLE_NAME")+"\n");
		   t.append("Grantor: " + rs.getString("GRANTOR")+"\n");
		   t.append("Grantee: " + rs.getString("GRANTEE")+"\n");
		   t.append("Privilege: " + rs.getString("PRIVILEGE")+"\n");
		   t.append("Is Grantable: " + rs.getString("IS_GRANTABLE")+"\n");

	       }

	   } catch (SQLException sqe) { 
	       t.append("Exception: \n"+ sqe.getMessage());
	       t.append("\nError-Code: " + sqe.getErrorCode());
	       t.append("\nSQL-State: " + sqe.getSQLState());
	   }
	   try {
	       rs = dmd.getPrimaryKeys(null,"%", getTable());
	       t.append("\nPrimary Keys\n");
	       while (rs.next()) { 
		   
		   t.append("Table: "   + rs.getString("TABLE_NAME")+"\n");
		   t.append("Column: " + rs.getString("COLUMN_NAME")+"\n");
		   
	       }
	   } catch (SQLException primEx) { 
	       t.append("Exception: \n"+ primEx.getMessage());
	       t.append("\nError-Code: " + primEx.getErrorCode());
	       t.append("\nSQL-State: " + primEx.getSQLState());
	   }




	   if ( isStandAlone() ) con.close();
	   return panel;
    }

       private JPanel buttonPanel() {

	   JPanel panel = new JPanel();
	   panel.setLayout(new FlowLayout());
	   cancel = new JButton("Cancel");
	   panel.add(cancel);
	   return panel;
       }


       public static void main(String args[]) {

	   if (args.length != 5) {
	       System.out.println("Syntax: java sql.fredy.metadata.Catalog host user password database table");
	       System.exit(0);
	   }

	   JFrame f = new JFrame("Catalog");
	   f.addWindowListener(new WindowAdapter() {
	       public void windowActivated(WindowEvent e) {}
	       public void windowClosed(WindowEvent e) {}
	       public void windowClosing(WindowEvent e) {System.exit(0);}
	       public void windowDeactivated(WindowEvent e) {}
	       public void windowDeiconified(WindowEvent e) {}
	       public void windowIconified(WindowEvent e) {}
	       public void windowOpened(WindowEvent e) {}});
	   f.getContentPane().setLayout(new BorderLayout());
	   Catalog tmd = new Catalog(args[0],args[1],args[2],args[3], args[4]);
	   f.getContentPane().add("Center",tmd);
	   tmd.cancel.addActionListener(new ActionListener() {
	       public void actionPerformed(ActionEvent e) {
		   System.exit(0);
		   }});
	   
	   f.pack();
	   f.setVisible(true);
       }
    }
