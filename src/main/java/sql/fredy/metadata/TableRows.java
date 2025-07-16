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
import java.beans.PropertyVetoException;
import java.io.IOException;
import javax.swing.JList;    
import java.util.*;
import java.sql.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import sql.fredy.connection.DataSource;


public class TableRows extends JList {
    
    Logger logger = Logger.getLogger("sql.fredy.metadata");
    

// Fredy's make Version
private static String fredysVersion = "Version 2.0  13. Dec. 2020";

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
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    public Connection getConnection() {

        Connection con = null;
        try {
            con = DataSource.getInstance().getConnection();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {
            return con;
        }

    }
    
    
    
    /**
     * TableRow need the following:
     * @con = a object of type t_connect
     * @dbTable = the Table to read Meta-Data from
     */

    public TableRows ( String dbTable) {

	setDbTable(dbTable);
      
	rows = new Vector();

        Connection con = null;
        
        
        try {
            con = getConnection();
	       DatabaseMetaData md = con.getMetaData();
	       ResultSet cols = md.getColumns(null,null,getDbTable(),"%");
	       while (cols.next()) {
		   rows.addElement(cols.getString(4));
	       }
	       this.setListData(rows);
	       this.setSelectedIndex(0);
	 } catch (SQLException exception) {
	      logger.log(Level.WARNING,"Exception on reading Table Fields {0}",exception.getMessage());
        } finally {
            if ( con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING,"Exception while closing Connection [0]", e.getMessage());
                }
            }
        }
     
    }

    public void removeElement(int i) {
	rows.removeElementAt(i);
	this.updateUI();
    }

}
