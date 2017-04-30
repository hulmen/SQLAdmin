
package sql.fredy.ui;

/** 
    DBcomboBox creates a combobox out of the 1st Field a SQL-Query
    returns.


    Admin is a Tool around mySQL to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

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
import java.util.*;
import java.net.*;
import java.sql.*;
import sql.fredy.share.t_connect;

public class DBcomboBox extends JComboBox {  

    private ResultSet sqlresult;
    private ResultSetMetaData metaData;

    private String dbgMessage = "";
    public String getMessage() { return dbgMessage;}

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
    
    /**
       * Get the value of l.
       * @return Value of l.
       */

    /**
       * Set the value of l.
       * @param v  Value to assign to l.
       */

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
    

    public void setText(String t) {
      try {
	this.setSelectedItem((String)t);
      } catch (Exception e) { ;}
    }

    public String getText() { return (String)this.getSelectedItem();}


    public void clear() { 
      try {
	this.setSelectedIndex(0);
      } catch (Exception e) { ;}
    }


    java.sql.Connection connection;
    
    /**
     * Get the value of connection.
     * @return value of connection.
     */
    public java.sql.Connection getConnection() {
	return connection;
    }
    
    /**
     * Set the value of connection.
     * @param v  Value to assign to connection.
     */
    public void setConnection(java.sql.Connection  v) {
	this.connection = v;
    }
    
    java.sql.Statement stmt;
    
    /**
     * Get the value of stmt.
     * @return value of stmt.
     */
    public java.sql.Statement getStmt() {
	return stmt;
    }
    
    /**
     * Set the value of stmt.
     * @param v  Value to assign to stmt.
     */
    public void setStmt(java.sql.Statement  v) {
	this.stmt = v;
    }
    
    boolean standAlone = true;
    
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
    
    public DBcomboBox (String host, String user, String password, String database, String query) {

	

	setHost(host);
	setUser(user);
	setPassword(password);
	setDatabase(database);
	t_connect con = new t_connect(getHost(), getUser(), getPassword(),getDatabase());  
	if (con.getError() != null) message("Connection Error: "+ con.getError());

	setConnection(con.con);
	setStmt(con.stmt);
	setQuery(query);
	setItems();
	try {
	  this.setSelectedIndex(0);
	} catch (Exception excp) {;}

    }

    public DBcomboBox (java.sql.Statement stmt, String query) {

	setStmt(stmt);
	setQuery(query);
	setStandAlone(false); // do not close any connection
	setItems();
	try {
	  this.setSelectedIndex(0);
	} catch (Exception excp) {;}

    }
    private void message(String msg) {
	   dbgMessage = dbgMessage + "\n" + msg;
    }

    public void reload() {
	removeItems();
	setItems();
    }


    public void removeItems() { 
    	try {
    		this.removeAllItems();
    	} catch (Exception excp) { ; }
    }
    
    public void setItems() {


	//this.removeAllItems();
	//create a connection-Object
  

	// execute this query
	try {
	    sqlresult = getStmt().executeQuery(getQuery());
	    metaData = sqlresult.getMetaData();
	} catch (Exception e) {  message("Query-Error: " +e.getMessage().toString()); }
	try {
	while ( sqlresult.next() ) {
	    switch (metaData.getColumnType(1) ) {
	    case java.sql.Types.INTEGER:
		this.addItem(Integer.toString(sqlresult.getInt(1)));
		break;
	    case java.sql.Types.FLOAT:
		this.addItem(Float.toString(sqlresult.getFloat(1)));
		break;
	    case java.sql.Types.DOUBLE:
		this.addItem(Double.toString(sqlresult.getDouble(1)));
		break;
	    case java.sql.Types.DATE:
		this.addItem(sqlresult.getDate(1).toString());
		break;
	    default:
		this.addItem(sqlresult.getString(1));
		break;
	    }
	}
	} catch (Exception sqle) { message("Exception while reading: " + sqle.getMessage().toString() + "\n\tQuery = " + getQuery());
	}
	if (isStandAlone()) {
	    try {
		getConnection().close();
	    } catch (java.sql.SQLException sqle) {}
	}
	
    }
}
