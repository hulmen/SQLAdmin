package sql.fredy.metadata;



/** 
    Tables lists the tables found in the Database.
    It is very often used inside Admin.

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
import java.util.*;
import java.sql.*;
import sql.fredy.share.t_connect;


public class TableColumns {

// Fredy's make Version
private static String fredysVersion = "Version 1.4   2. Jan.2002";

public String getVersion() {return fredysVersion; }
    Vector allCols;
    Vector allNames;
    Vector columnInfo;
    String Table;


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
    


    
    /**
       * Get the value of Table.
       * @return Value of Table.
       */
    public String getTable() {return Table;}
    
    /**
       * Set the value of Table.
       * @param v  Value to assign to Table.
       */
    public void setTable(String  v) {this.Table = v;}
    
    
    /**
       * Get the value of allCols.
       * @return Value of allCols.
       */
    public Vector  getAllCols() {return allCols;}
    
        
    /**
       * Get the value of columnInfo.
       * @return Value of columnInfo.
       */
    public Vector getColumnInfo() {return columnInfo;}
    
    /**
       * Set the value of columnInfo.
       * @param v  Value to assign to columnInfo.
       */
    public void setColumnInfo(Vector  v) {this.columnInfo = v;}
    



    /**
     * get the all Column Names.
     * @return Value of allNames.
     */
    public Vector getAllNames() {return allNames;}


   /**
       * Get the value of host.
       * @return Value of host.
       */

    private String host;
    public String getHost() {return host;}
    
    /**
       * Set the value of host.
       * @param v  Value to assign to host.
       */
    public void setHost(String  v) {this.host = v;}
    

    /** to find out, when the user wants to close
     *  this application, set a listener onto (JButton)AutoForm.cancel 
     **/

    private String user;
    
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
    

    private String password;
    
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


    private String database;
    
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
    
    

    public SingleColumnInfo getColumn(int columnNo) {
      
	try {
	    return (SingleColumnInfo) columnInfo.elementAt(columnNo); 
	} catch (ArrayIndexOutOfBoundsException aiob) {
	    System.out.println("\nClass TableColumn, Method getColumn(" + columnNo + ") Error: "+ aiob);
	    return null;
	}


    }

    public Object getColumnInfo(int columnNo, int columnDescription) {

     SingleColumnInfo sci = new SingleColumnInfo();
     sci = getColumn(columnNo); 
     Vector v = new Vector();
     v = sci.getDataVector();
     return v.elementAt(columnDescription);

    }


    public int getDecDigits(int columnNo) {

	SingleColumnInfo sci = new SingleColumnInfo();
	sci = getColumn(columnNo); 	
	return sci.getDecimal_digits();

    }

    
    int NumberOfColumns;
    
    /**
       * Get the value of NumberOfColumns.
       * @return Value of NumberOfColumns.
       */
    public int getNumberOfColumns() {return NumberOfColumns;}
    
    /**
       * Set the value of NumberOfColumns.
       * @param v  Value to assign to NumberOfColumns.
       */
    public void setNumberOfColumns(int  v) {this.NumberOfColumns = v;}
    

    public TableColumns(String host, String user, String password, String database, String table) {

	setStandAlone(true);
	setHost(host);
	setUser(user);
	setPassword(password);
	setDatabase(database);
	setTable(table);
        con = new t_connect(getHost(), getUser(), getPassword(),getDatabase());
	
	inits();
    }

    public TableColumns(t_connect con,String table) {
	this.setCon(con);
	setStandAlone(false);
	setTable(table);	

	inits();
    }

    private void inits() {

	allCols    = new Vector();
	allNames   = new Vector();
	columnInfo = new Vector();
	
       if (con.getError() != null) {
           
       } else {
	   try {
	       DatabaseMetaData md = con.con.getMetaData();
	       ResultSet cols = md.getColumns(null,null,getTable(),"%");
	       int i = 0;	      
	       while (cols.next()) {

		   Columns col = new Columns(i,cols.getString(4),cols.getString(5),cols.getInt(7),cols.getInt(9),getTable());
		   allCols.addElement(col);
		   allNames.addElement(cols.getString(4));

		   try {
		        // doing complete ColumnInfo
		        Vector colTemp = new Vector();
			colTemp.addElement((String)cols.getString(1));
			colTemp.addElement((String)cols.getString(2));
			colTemp.addElement((String)cols.getString(3));
			colTemp.addElement((String)cols.getString(4));
			colTemp.addElement((Short)new Short(cols.getShort(5)));
			colTemp.addElement((String)cols.getString(6));
			colTemp.addElement((Integer)new Integer(cols.getInt(7)));
			colTemp.addElement((Integer)new Integer(0));
			colTemp.addElement((Integer)new Integer(cols.getInt(9)));
			colTemp.addElement((Integer)new Integer(cols.getInt(10)));
			colTemp.addElement((Integer)new Integer(cols.getInt(11)));
			colTemp.addElement((String)cols.getString(12));
			colTemp.addElement((String)cols.getString(13));
			colTemp.addElement((Integer)new Integer(cols.getInt(14)));
			colTemp.addElement((Integer)new Integer(cols.getInt(15)));
			colTemp.addElement((Integer)new Integer(cols.getInt(16)));
			colTemp.addElement((Integer)new Integer(cols.getInt(17)));
			colTemp.addElement((String)cols.getString(18));
			SingleColumnInfo sctt = new SingleColumnInfo(colTemp);
			sctt.setPrimaryKey(md);
			columnInfo.addElement(sctt);
		   } catch (Exception excp1) {
		      System.out.println("Exception in TableColumns, Table: " + cols.getString(4));
		      excp1.printStackTrace();		     
		   }

		   i++;
	       }
	       setNumberOfColumns(i-1);
	   } catch (Exception exception) {
	     System.out.println("TableColumns: " + exception.getMessage().toString());
	   }
	  
       }
       if (isStandAlone()) {
	   try {
	       con.close();
	   } catch (Exception ec1) {
	       ec1.printStackTrace();
	   }
       }
    }


}
