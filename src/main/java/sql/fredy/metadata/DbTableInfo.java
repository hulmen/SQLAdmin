package sql.fredy.metadata;

/** 

    DbTableInfo is Part of Admin and delivers lot of the Meta-Data-Info of a Table

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


import java.sql.*;
import java.util.Vector;


public class DbTableInfo {


    
    Connection con;
    
    /**
       * Get the value of con.
       * @return Value of con.
       */
    public Connection getCon() {return con;}
    
    /**
       * Set the value of con.
       * @param v  Value to assign to con.
       */
    public void setCon(Connection  v) {this.con = v;}
    
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
    
    
    DatabaseMetaData dmd;
    
    /**
       * Get the value of dmd.
       * @return Value of dmd.
       */
    public DatabaseMetaData getDmd() {return dmd;}
    
    /**
       * Set the value of dmd.
       * @param v  Value to assign to dmd.
       */
    public void setDmd(DatabaseMetaData  v) {this.dmd = v;}
    

    /**
     * @param con  the connection used
     * @param dmd  the DatabaseMetaData
     * @param table the tableName 
     */

    public DbTableInfo(Connection con, DatabaseMetaData dmd, String table) {
     
	this.setCon(con);
	this.setDmd(dmd);
	this.setTable(table);

    }
    /**
     * @param con  the connection used
     * @param table the tableName 
     */

    public DbTableInfo(Connection con, String table) {
     
	this.setCon(con);
	this.setTable(table);

	try {
	    setDmd(getCon().getMetaData());
	} catch (SQLException sqe) { 
	    System.out.println("\nError while gathering MetaData\n");
	    System.out.println("Exception: \n"+ sqe.getMessage());
	    System.out.println("\nError-Code: " + sqe.getErrorCode());
	    System.out.println("\nSQL-State: " + sqe.getSQLState() + "\n");
	}	

    }


    /**
     * This returns a Vector contaning objects of type PrimaryKey
     */
    public Vector getPrimaryKeys() {

	Vector v = new Vector();
	try {
	    ResultSet rs = getDmd().getPrimaryKeys(null,null, getTable());
	    while ( rs.next() ) {
		PrimaryKey pk = new PrimaryKey(rs.getString("TABLE_NAME"),
					       rs.getString("COLUMN_NAME"),
					       rs.getShort("KEY_SEQ"),
					       rs.getString("PK_NAME"));
		v.addElement((PrimaryKey)pk);
	    }
	} catch (SQLException sqe) { 
	    System.out.println("\nError while gathering MetaData\n" +
	                       "Exception: \n"+ sqe.getMessage() +
	                       "\nError-Code: " + sqe.getErrorCode() +
	                       "\nSQL-State: " + sqe.getSQLState() + "\n");
	}
	return v;
    }

    /**
     * this returns a Vector containing Strings with the names of the columns
     */
    public Vector getColumnNames() {
	Vector v = new Vector();
	return v;
    }

    public Vector getColumnInfo(String column) {

	//hier gebe ich einen Vector mit SingleColumnInfo s zurueck

	Vector v = new Vector();
	return v;

    }


}
