package sql.fredy.metadata;

import java.sql.*;
import sql.fredy.share.t_connect;

/**
   This is to figure out, if a column is auto-increment.
   You instantiate this class once and then you have
   to set the column and have the possiblity to set the table
   to use only one connection to run it.

 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 * and exports data into various formats
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


public class IsAutoIncrement {
	
    t_connect con=null;
    
    /**
     * Get the value of con.
     * @return value of con.
     */
    public t_connect getCon() {
        if ( con == null) {
	    con = new t_connect(getHost(),
				getUser(),
				getPassword(),
				getDatabase());
	    if ( ! con.acceptsConnection() ) con = null;
	}	

	return con;
    }
    
    /**
     * Set the value of con.
     * @param v  Value to assign to con.
     */
    public void setCon(t_connect  v) {
	this.con = v;
        setHost(con.getHost());
	setUser(con.getUser());
	setPassword(con.getPassword());
	setDatabase(con.getDatabase());
    }
    
    
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


    
    String schema;
    
    /**
       * Get the value of schema.
       * @return Value of schema.
       */
    public String getSchema() {return schema;}
    
    /**
       * Set the value of schema.
       * @param v  Value to assign to schema.
       */
    public void setSchema(String  v) {this.schema = v;}


    String table;
    
    /**
     * Get the value of table.
     * @return value of table.
     */
    public String getTable() {
	return table;
    }
    
    /**
     * Set the value of table.
     * @param v  Value to assign to table.
     */
    public void setTable(String  v) {
	this.table = v;
    }
    

    String column;
    
    /**
     * Get the value of column.
     * @return value of column.
     */
    public String getColumn() {
	return column;
    }
    
    /**
     * Set the value of column.
     * @param v  Value to assign to column.
     */
    public void setColumn(String  v) {
	this.column = v;
    }
    


    public IsAutoIncrement( String host,
			    String user,
			    String password,
			    String database,
			    String table) {

	setHost(host);
	setUser(user);
	setPassword(password);
	setDatabase(database);
	setTable(table);
    }

    public IsAutoIncrement(t_connect con,String table) {
	setCon(con);
	setTable(table);
    }

    public boolean autoIncrement() {
	getCon();
	try {
            ResultSet rs   = con.stmt.executeQuery(
		"select " + getColumn() + " from " + getTable());
	    ResultSetMetaData rsmd = rs.getMetaData();
            return rsmd.isAutoIncrement(1);
	} catch (SQLException sqe ){	   
	    System.out.println("IsAutoIncrement throws excpetion:\n" + 
			      sqe.getMessage());
	    return false;
	}
    }

    public static void main(String args[]) {

	String host = "localhost";
	String user = System.getProperty("user.name");
	String password = null;
	String database = null;
	String table    = null;
	String column   = null;

	System.out.println("Fredy's Meta Data Tools\n" +
			   "this is IsAutoIncrement?\n" +
			   "Parameters: -h Host (default: " + host + ")\n" +
			   "            -u User (default: " + user + ")\n" +
			   "            -p Password\n" +
			   "            -d database\n" +
			   "            -t table\n" +
			   "            -c column");
	int i = 0;
	while ( i < args.length) {
            if (args[i].equals("-h")) {
                i++;
                host = args[i];
            }
            if (args[i].equals("-u")) {
                i++;
                user = args[i];
            }
            if (args[i].equals("-p")) {
                i++;
                password = args[i];
            }
            if (args[i].equals("-d")) {
                i++;
                database = args[i];
            }

            if (args[i].equals("-t")) {
                i++;
                table = args[i];
            }
            if (args[i].equals("-c")) {
                i++;
                column = args[i];
            }
	    i++;
	};
       


	IsAutoIncrement isa = new IsAutoIncrement(host,user,password,database,table);
	isa.setColumn(column);
	System.out.println("Database: " + database + "\n" +
			   "Table   : " + table   );
			  
	if (isa.autoIncrement() ) {
	    System.out.println("Column  : " + column + " is AUTO_INCREMENT");
	} else {
	    System.out.println("Column  : " + column + " is not AUTO_INCREMENT");
	}
        System.exit(0);
    }

}
