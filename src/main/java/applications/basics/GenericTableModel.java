package applications.basics;

/** 

    GenericTableModel is generic AbstractTableModel extend for Admin purpose

    Admin is a Tool around SQL databases to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and other usefull things in DB-arena
    
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

import javax.swing.JTable;
import javax.swing.table.*;
import java.util.Vector;
import java.sql.*;
import java.util.logging.*;



public class GenericTableModel extends AbstractTableModel {

    public DbInfo dbi;

    public String[] columnNames = { "1", 
				    "2",
				    "3",
				    "4",
				    "5"
    };


    private int maxRowsToRead = 0;

    private Logger logger = Logger.getLogger("applications.basics");

    t_connect con = null;
    public t_connect getCon() {
	if ( con == null) con = new t_connect(getHost(),getUser(),getPassword(),getDatabase());
	return con;
    }

    public void setCon(t_connect v) { this.con = v; }


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
    
    
    String schema;
    
    /**
       * Get the value of schema.
       * @return value of schema.
       */
    public String getSchema() {return schema;}
    
    /**
       * Set the value of schema.
       * @param v  Value to assign to schema.
       */
    public void setSchema(String  v) {this.schema = v;}
    

    public Vector data;

    
    String SQLError;
    
    /**
       * Get the value of SQLError.
       * @return value of SQLError.
       */
    public String getSQLError() {return SQLError;}
    
    /**
       * Set the value of SQLError.
       * @param v  Value to assign to SQLError.
       */
    public void setSQLError(String  v) {this.SQLError = v;}
    

    
    String query;
    
    /**
       * Get the value of query.
       * @return value of query.
       */
    public String getQuery() {return query;}
    
    /**
       * Set the value of query.
       * @param v  Value to assign to query.
       */
    public void setQuery(String  v) {this.query = v;}
    

    Connection          connection;
    Statement           statement;
    ResultSet           resultSet;
    Class[]             columnTpyes = {};
    ResultSetMetaData   metaData;

     

    public GenericTableModel(t_connect con,
			     String table,
			     String schema){

	logger = Logger.getLogger("applications.basics");
	this.setHost(con.getHost());
	this.setUser(con.getUser());
	this.setPassword(con.getPassword());
	this.setDatabase(con.getDatabase());
	this.setTable(table);
	this.setSchema(schema);

	dbi = new DbInfo(getCon());
	data = new Vector();
	open();

    }  
    

    public GenericTableModel(String host, 
			     String user, 
			     String password, 
			     String database, 
			     String table,
			     String schema){

	this.setHost(host);
	this.setUser(user);
	this.setPassword(password);
	this.setDatabase(database);
	this.setTable(table);
	this.setSchema(schema);

	dbi = new DbInfo(getHost(), getUser(), getPassword(), getDatabase());
	data = new Vector();
	open();

    }
    public void clearData() {
        data.removeAllElements(); 
        fireTableDataChanged();
    }


    public void addRow(Vector v) {

        data.addElement(v);
        fireTableChanged(null);
    }

    public void removeRow(int row) {
        data.removeElementAt(row);
	fireTableChanged(null);	
    }

    public String getColumnName(int i) {

        return columnNames[i];
    }


    /**
     * Does not work!!!
     public Class getColumnClass(int c) {
     	try {
             return getValueAt(0, c).getClass();
      	} catch (Exception e) {
      	    System.out.println("(GenericTableModel)Fatal Error in method: getColumnClass(int " + c + ")\n");
     	    e.printStackTrace();
   	    return null;
	}
    }
    **/
    public Class getColumnClass(int column) {
        int type;
        try {
            type = metaData.getColumnType(column+1);
            logger.log(Level.FINE,"Data Type for column " + column + " is: " + type);
        }
        catch (SQLException e) {
	    logger.log(Level.WARNING,"Exception " + e.getMessage().toString());
            return super.getColumnClass(column);
        }

        switch(type) {
        case Types.CHAR:
	    return String.class;
        case Types.VARCHAR:
	    return String.class;
        case Types.LONGVARCHAR:
            return String.class;

        case Types.BIT:
            return Boolean.class;

        case Types.TINYINT:
	    return Integer.class;
        case Types.SMALLINT:
	    return Integer.class;
        case Types.INTEGER:
            return Integer.class;

        case Types.BIGINT:
            return Long.class;

        case Types.FLOAT:
	     return Float.class;
        case Types.DOUBLE:
            return Double.class;

        case Types.DECIMAL:
            return Float.class;    
         
        case Types.DATE:
            return java.sql.Date.class;

        default:
            return Object.class;
        }
    }


    public int getColumnCount() { return columnNames.length; }

    public int getRowCount() { return data.size(); }

    public Object getValueAt(int row, int col) {
        Vector rowData = (Vector)data.elementAt(row);
        return rowData.elementAt(col);
    }

    public boolean isCellEditable(int row, int col) { 
        boolean b = false;
        return b;
    }

    public void setValueAt(Object value, int row, int col) {
    
    }


    public String getKomma(String column) {
	String s ="";
	SingleColumnInfo sci = new SingleColumnInfo();
	sci = dbi.getColumnInfo(getDatabase(),getTable(),column);


	if (sci.getData_type() == java.sql.Types.CHAR ) s = "'";
	if (sci.getData_type() == java.sql.Types.VARCHAR ) s = "'"; 
	if (sci.getData_type() == java.sql.Types.BINARY ) s = "'";
	if (sci.getData_type() == java.sql.Types.LONGVARBINARY ) s = "'";
	if (sci.getData_type() == java.sql.Types.VARBINARY ) s = "'";
	if (sci.getData_type() == java.sql.Types.TIME ) s = "'";
	if (sci.getData_type() == java.sql.Types.DATE ) s = "'";
	if (sci.getData_type() == java.sql.Types.TIMESTAMP ) s = "'";
	if (sci.getData_type() == java.sql.Types.INTEGER ) s = "";
	if (sci.getData_type() == java.sql.Types.NUMERIC ) s = "";
	if (sci.getData_type() == java.sql.Types.DOUBLE  ) s = "";
	if (sci.getData_type() == java.sql.Types.FLOAT ) s = "";
	if (sci.getData_type() == java.sql.Types.BIGINT ) s = "";
	if (sci.getData_type() == java.sql.Types.BIT ) s = "";
	if (sci.getData_type() == java.sql.Types.BLOB) s= "'";
	if (sci.getData_type() == java.sql.Types.CLOB ) s = "";
	if (sci.getData_type() == java.sql.Types.DECIMAL ) s = "";
	if (sci.getData_type() == java.sql.Types.OTHER ) s = "'";

	return s;
    }

    public void executeQuery() {
        if (connection == null || statement == null) {
	    logger.log(Level.INFO,"There is no database to execute the query."); 
            return;
        }

        int rowRead = 0;
        try {	    
	    if ( connection.isClosed() ) open();
            resultSet = statement.executeQuery(getQuery());
            metaData = resultSet.getMetaData();

            // Get all rows.
            clearData();
            while (resultSet.next()) {

                Vector newRow = new Vector();
                for (int i = 1; i <= getColumnCount(); i++) {
		    // System.out.println(metaData.getColumnLabel(i) + " = " +metaData.getColumnTypeName(i));
		    newRow.addElement(getObject(i));		    
                }
                data.addElement(newRow);

                rowRead++;
                if ( ( maxRowsToRead > 0 ) && ( rowRead == maxRowsToRead)) break;
            }
            //close();
            fireTableChanged(null); // Tell the listeners a new table has arrived.
        }
        catch (SQLException ex) {
	    logger.log(Level.WARNING,"Error in executeQuery: " + ex.getMessage().toString());      
	    SQLError = ex.toString();
        }
    }

    public void close() throws SQLException {
        //System.out.println("Closing db connection");
        resultSet.close();
        statement.close();
        connection.close();
    }

     public void open() {

        TConnectProperties  prop = new TConnectProperties();
        try {
            setMaxRowsToRead(prop.getMaxRowsToRead());
        } catch (Exception prpException) {
            logger.log(Level.INFO,"Something went wrong while reading properties {0}",prpException.getMessage());
        }
        logger.log(Level.INFO,"reopening connection");
	getCon();
	if (con.getError() != null) {
	    logger.log(Level.WARNING,"Can not connect to the database: " + con.getError());
	}
	statement  = con.stmt;
	connection = con.con;
    }



    /** getting the object from the resultSet, because there seems to
        be something strange with the resultSet.getObject() of my JDBC-Driver
    **/
    public Object getObject(int i) {
      try{

          logger.log(Level.FINEST," Attribute Type: " + metaData.getColumnTypeName(i).toUpperCase());

	  // the upperCase stuff is something because of Postgres...
	  // as well as the startsWith-thing....

	  //System.out.println(metaData.getColumnTypeName(i).toUpperCase());

	  if       (metaData.getColumnTypeName(i).toUpperCase().startsWith("INTEGER"))	{ 
			Integer k = resultSet.getInt(i);
			return k;
	  }
	  if       (metaData.getColumnTypeName(i).toUpperCase().startsWith("INT"))  { 
	      Integer k = resultSet.getInt(i);
	      return k; }
	  else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("VARCHAR"))   { 
	      return resultSet.getString(i); }
	  else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("VARBINARY")) {
	      return resultSet.getString(i); }
	  else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("LONGVARBINARY")) { 
	      return resultSet.getString(i); }
	  else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("BINARY")) {
	      return resultSet.getString(i); }
	  else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("CHAR")) { 
	      return resultSet.getString(i); }
	  else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("LONGVARBINARY"))
	      { return resultSet.getString(i); }
	  else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("BIT")) {  
	      return resultSet.getBoolean(i); }
	  else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("DOUBLE"))    { 
	      Double d = resultSet.getDouble(i); 
	      return d; }
	  else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("FLOAT"))     { 
	      Float f= resultSet.getFloat(i);
	      return f;  }
           else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("DECIMAL"))     {
	      Float f= resultSet.getFloat(i);
	      return f;  }
	  else if  (metaData.getColumnTypeName(i).toUpperCase().startsWith("DATE"))     
	      { return resultSet.getDate(i) ; }
            else if  (metaData.getColumnTypeName(i).toUpperCase().endsWith("TIMESTAMP"))     { 
		Timestamp f = new Timestamp(System.currentTimeMillis());
		f= (Timestamp)resultSet.getObject(i);
		return f;  }

	    else  if  ( metaData.getColumnTypeName(i).toUpperCase().startsWith("BLOB") ) {
		return resultSet.getString(i); }
	  
            return resultSet.getObject(i);

      } catch (SQLException e) { return null;}

   }

    /**
     * @return the maxRowsToRead
     */
    public int getMaxRowsToRead() {
        return maxRowsToRead;
    }

    /**
     * @param maxRowsToRead set the maximum of rows to loop from the DB, 0 means do not care
     */
    public void setMaxRowsToRead(int maxRowsToRead) {
        this.maxRowsToRead = maxRowsToRead;
    }



}
