package sql.fredy.metadata;


/** 

    TableRows is the one that creates a list out of the columns
    inside a specified table. It is one of the Meta-Data-things I did
    for Admin


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
import javax.swing.JList;    
import java.util.*;
import java.sql.*;
import sql.fredy.share.t_connect;

public class TableRows extends JList {

// Fredy's make Version
private static String fredysVersion = "Version 1.4   2. Jan.2002";

public String getVersion() {return fredysVersion; }

    Vector rows;

    String dbTable;
    
    /**
       * Get the value of the Table.
       * @return Value of the Table.
       */
    public String getDbTable() {return dbTable;}
    
    /**
       * Set the value of dbTable.
       * @param v  Value to assign to dbTable.
       */
    public void setDbTable(String  v) {this.dbTable = v;}
    

    /**
     * TableRow need the following:
     * @con = a object of type t_connect
     * @dbTable = the Table to read Meta-Data from
     */

    public TableRows (t_connect con, String dbTable) {

	setDbTable(dbTable);
      
	rows = new Vector();

        try {
	       DatabaseMetaData md = con.con.getMetaData();
	       ResultSet cols = md.getColumns(null,null,getDbTable(),"%");
	       while (cols.next()) {
		   rows.addElement(cols.getString(4));
	       }
	       this.setListData(rows);
	       this.setSelectedIndex(0);
	 } catch (Exception exception) {
	      
        }
     
    }

    public void removeElement(int i) {
	rows.removeElementAt(i);
	this.updateUI();
    }

}
