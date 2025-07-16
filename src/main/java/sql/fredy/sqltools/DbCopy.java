package sql.fredy.sqltools;

/**
 * DbCopy is a tool to copy Tables from one Database to another
 * also in between different RDBMS
 *
 **/


/** 
 
  Admin is a Tool around SQL-Databases to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

  This software is part of the Admin-Framework 
  
  Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
  for DB-Administrations, as create / delete / alter and query tables
  it also creates indices and generates simple Java-Code to access DBMS-tables
  and exports data into various formats
 
 
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


public class DbCopy  {

// Fredy's make Version
private static String fredysVersion = "Version 1.4  2. Jan. 2002";

public String getVersion() {return fredysVersion; }

    private Connection srcCon, dstCon;
    private Statement  srcStmt,dstStmt;
    private ResultSet  srcResult;
    
    String srcDriver;
    
    /**
       * Get the value of the JDBC-Driver of the source.
       * @return Value of the JDBC-Driver of the source.
       */
    public String getSrcDriver() {return srcDriver;}
    
    /**
       * Set the value of JDBC-Driver of the source.
       * @param v  Value to assign to JDBC-Driver of the source.
       */
    public void setSrcDriver(String  v) {this.srcDriver = v;}
    

    
    String srcUrl;
    
    /**
       * Get the value of srcUrl.
       * @return Value of srcUrl.
       */
    public String getSrcUrl() {return srcUrl;}
    
    /**
       * Set the value of srcUrl.
       * @param v  Value to assign to srcUrl.
       */
    public void setSrcUrl(String  v) {this.srcUrl = v;}
    

    
    String dstDriver;
    
    /**
       * Get the value of dstDriver.
       * @return Value of dstDriver.
       */
    public String getDstDriver() {return dstDriver;}
    
    /**
       * Set the value of dstDriver.
       * @param v  Value to assign to dstDriver.
       */
    public void setDstDriver(String  v) {this.dstDriver = v;}
    
 

    String dstUrl;
    
    /**
       * Get the value of dstUrl.
       * @return Value of dstUrl.
       */
    public String getDstUrl() {return dstUrl;}
    
    /**
       * Set the value of dstUrl.
       * @param v  Value to assign to dstUrl.
       */
    public void setDstUrl(String  v) {this.dstUrl = v;}
    

    
    String srcUser;
    
    /**
       * Get the value of srcUser.
       * @return Value of srcUser.
       */
    public String getSrcUser() {return srcUser;}
    
    /**
       * Set the value of srcUser.
       * @param v  Value to assign to srcUser.
       */
    public void setSrcUser(String  v) {this.srcUser = v;}
    
    
    String srcPassword;
    
    /**
       * Get the value of srcPassword.
       * @return Value of srcPassword.
       */
    public String getSrcPassword() {return srcPassword;}
    
    /**
       * Set the value of srcPassword.
       * @param v  Value to assign to srcPassword.
       */
    public void setSrcPassword(String  v) {this.srcPassword = v;}
    
    
    String dstUser;
    
    /**
       * Get the value of dstUser.
       * @return Value of dstUser.
       */
    public String getDstUser() {return dstUser;}
    
    /**
       * Set the value of dstUser.
       * @param v  Value to assign to dstUser.
       */
    public void setDstUser(String  v) {this.dstUser = v;}
    
    String dstPassword;

    /**
     * Get the value of dstPassword.
     * @return Value of dstPassword.
     */
    public String getDstPassword() {return dstPassword;}

    /**
     * Set the value of dstPassword.
     * @param v  Value to assign to dstPassword.
     */
    public void setDstPassword(String  v) {this.dstPassword = v;}

    
    boolean useAuthsrc;
    
    /**
       * Get the value of useAuthsrc.
       * @return Value of useAuthsrc.
       */
    public boolean getUseAuthsrc() {return useAuthsrc;}
    
    /**
       * Set the value of useAuthsrc 
       * @param v  Value to assign to useAuthsrc.
       */
    public void setUseAuthsrc(boolean  v) {this.useAuthsrc = v;}
    

    
    boolean useAuthdst;
    
    /**
       * Get the value of useAuthdst.
       * @return Value of useAuthdst.
       */
    public boolean getUseAuthdst() {return useAuthdst;}
    
    /**
       * Set the value of useAuthdst
       * @param v  Value to assign to useAuthdst.
       */
    public void setUseAuthdst(boolean  v) {this.useAuthdst = v;}
    

    
    boolean createDstTable;
    
    /**
       * Get the value of createDstTable.
       * @return Value of createDstTable.
       */
    public boolean getCreateDstTable() {return createDstTable;}
    
    /**
       * Set the value of createDstTable.
       * @param v  Value to assign to createDstTable.
       */
    public void setCreateDstTable(boolean  v) {this.createDstTable = v;}
    
    
    
    String sourceTable;
    
    /**
       * Get the value of sourceTable.
       * @return Value of sourceTable.
       */
    public String getSourceTable() {return sourceTable;}
    
    /**
       * Set the value of sourceTable.
       * @param v  Value to assign to sourceTable.
       */
    public void setSourceTable(String  v) {this.sourceTable = v;}
    

    
    String dstTable;
    
    /**
       * Get the value of dstTable.
       * @return Value of dstTable.
       */
    public String getDstTable() {return dstTable;}
    
    /**
       * Set the value of dstTable.
       * @param v  Value to assign to dstTable.
       */
    public void setDstTable(String  v) {this.dstTable = v;}
    
    
    boolean dstEmpty;
    
    /**
       * Get the value of dstEmpty.
       * @return Value of dstEmpty.
       */
    public boolean getDstEmpty() {return dstEmpty;}
    
    /**
       * Set the value of dstEmpty.
       * @param v  Value to assign to dstEmpty.
       */
    public void setDstEmpty(boolean  v) {this.dstEmpty = v;}
 
    
    boolean verbose;
    
    /**
       * Get the value of verbose.
       * @return Value of verbose.
       */
    public boolean getVerbose() {return verbose;}
    
    /**
       * Set the value of verbose.
       * @param v  Value to assign to verbose.
       */
    public void setVerbose(boolean  v) {this.verbose = v;}
    
   
    public DbCopy(String sDriver, String sUrl, String sUser, String sPassword,
		  boolean sAuth, String sTable,
		  String dDriver, String dUrl, String dUser, String dPassword,
		  boolean dAuth, String dTable, boolean cdTable, boolean dEmpty, boolean vb) {


	setSrcDriver(sDriver);
	setSrcUrl(sUrl);
	setDstDriver(dDriver);
	setDstUrl(dUrl);
	setSrcUser(sUser);
	setSrcPassword(sPassword);
	setDstUser(dUser);
	setDstPassword(dPassword);
	setUseAuthsrc(sAuth);
	setUseAuthdst(dAuth);
	setCreateDstTable(cdTable);
	setSourceTable(sTable);
	setDstTable(dTable);
	setDstEmpty(dEmpty);
	setVerbose(vb);

	createSrc();
	createDst();
	readSrc();
	close();

    }
    private void close() {
	try {
	    srcStmt.close();
	    dstStmt.close();
	    srcCon.close();
	    dstCon.close();
	} catch (SQLException e) { ; }
    }

    private void readSrc() {

	if (verbose) System.out.println("select * from " + getSourceTable());
	try {
	    srcResult    = srcStmt.executeQuery("select * from " + getSourceTable());
	    ResultSetMetaData metaData = srcResult.getMetaData();  
	   
	    if (createDstTable) cdt();
            if (dstEmpty)  clearDestination();


	    while  (srcResult.next()) {
		String row = "insert into " + getDstTable() + " values (";
		for (int i = 1; i < metaData.getColumnCount()+1; i++) {
		    if (i > 1 ) row = row + ",";
		    if       (metaData.getColumnTypeName(i) == "INTEGER")   { row = row  + Integer.toString(srcResult.getInt(i)); }
		    else if  (metaData.getColumnTypeName(i) == "VARCHAR")   { row = row  + "\'" + srcResult.getString(i) + "\'"; }
		    else if  (metaData.getColumnTypeName(i) == "VARBINARY") { row = row  + "\'" + srcResult.getString(i) + "\'"; }
		    else if  (metaData.getColumnTypeName(i) == "DOUBLE")    { row = row  + Double.toString(srcResult.getDouble(i)) ; }
		    else if  (metaData.getColumnTypeName(i) == "FLOAT")     { row = row  + Float.toString(srcResult.getFloat(i)) ; }
		    else if  (metaData.getColumnTypeName(i) == "DATE")      { row = row  + "\'" + srcResult.getDate(i) + "\'" ; }
		    else     {row = row + "\'" + srcResult.getString(i) + "\'"; }
		}
		row = row + ")";
		writeDst(row);
	    }
	} catch (Exception e) { 
	    System.out.println("Exception while reading Source ") ;
	    if (verbose) System.out.println(e.getMessage().toString());
	}
    }


    private void clearDestination() {

	writeDst("delete from " + getDstTable());

    }

    private void cdt() {
	String row = "create table " + getDstTable() + " (" ;
	try {
	    DatabaseMetaData dmd = srcCon.getMetaData();
	    ResultSet rs = dmd.getColumns(null, null, getSourceTable(), null);
	    boolean first = true;
	    while (rs.next()) { 
		if (! first) {
		    	row = row + ", ";
		} else { first = false; }
		row = row + rs.getString("COLUMN_NAME") + " ";
		row = row + rs.getString("TYPE_NAME")   + " ";
                if ( ! rs.getString("TYPE_NAME").equalsIgnoreCase("DATE") ) {
		    row = row + "(" + Integer.toString( rs.getInt("COLUMN_SIZE") );
		    if (rs.getString("TYPE_NAME").equalsIgnoreCase("FLOAT")) row = row + "," + Integer.toString(rs.getInt("DECIMAL_DIGITS"));
		    row = row + " )";
		}

		if ( rs.getString("IS_NULLABLE").equalsIgnoreCase("NO"))  row = row + "not null ";
	
	    }
	    row = row + ")";
	    writeDst(row);
	 }catch (Exception e) {
	    System.out.println("Exception while creating Destination Table") ;
	    if (verbose) System.out.println(e.getMessage().toString());
	}
	

    }



    private void writeDst(String query) {
	if (verbose) System.out.println(query);
        try {
	    int records = dstStmt.executeUpdate(query);
	} catch (Exception e) {
	    System.out.println("Exception while writing Destination ") ;
	    if (verbose) {
		System.out.println(e.getMessage().toString());
		System.out.println("Query: \n" + query + " \n");
	    }
	}
    }


    private void createSrc() {

	try {
	    Class.forName(getSrcDriver());
	    if (getUseAuthsrc()) {
		srcCon = DriverManager.getConnection(getSrcUrl(),getSrcUser(),getSrcPassword());
	    } else {
		srcCon = DriverManager.getConnection(getSrcUrl());
	    }
       
	    srcStmt = srcCon.createStatement();
	    if (verbose)  System.out.println("Connection established to source");
	} catch (Exception e) {
	    System.out.println("Exception while opening Source Database");
	    if ( verbose ) { System.out.println(e.getMessage().toString());
	    }
	}
    
    }

    private void createDst() {

	try {
	    Class.forName(getDstDriver()).newInstance();
	    if (getUseAuthdst()) {
                if ( verbose ) System.out.println("Authenticate at destination: " + getDstUrl() + "  " + getDstUser());
		dstCon = DriverManager.getConnection(getDstUrl(),getDstUser(),getDstPassword());
	    } else {
               if ( verbose ) System.out.println("no authentication at destination");
		srcCon = DriverManager.getConnection(getDstUrl());
	    }
	
	    dstStmt = dstCon.createStatement();
	    if (verbose)  System.out.println("Connection established to destination");
	} catch (Exception e) {
	    System.out.println("Exception while opening Destination Database");
	    if ( verbose ) { System.out.println(e.getMessage().toString());
	    }
	}
    
    }


    public static void main(String args[] ) {

	System.out.println("\n");
	System.out.println("DbCopy Copys from one DB to another");
	System.out.println("Parameters:");
	System.out.println("-d [word] JDBC-Driver Source");
	System.out.println("-h [word] JDBC-URL Source");
	System.out.println("-u [word] User Source");
	System.out.println("-p [word] Password Source");
	System.out.println("-a use authentication for Source");
	System.out.println("-t [word] name of the source table");
	System.out.println("-D [word] JDBC-Driver Destination");
	System.out.println("-H [word] JDBC-URL Destination");
	System.out.println("-U [word] User Destination");
	System.out.println("-P [word] Password Destination");
	System.out.println("-A use Authentication for Destination");
	System.out.println("-T [word] name of the destination Table");
	System.out.println("-C create destination Table");
	System.out.println("-E empty destination Table before inserting");
	System.out.println("-v verbose");

	if (args.length < 6 ) {
	    System.out.println("\n\nERROR\n*****\nI need in minimum to know the following: \n" +
			       "-d JDBC-Driver source\n" +
			       "-h JDBC-URL    source\n" +
			       "-t Source Table to read from\n" +
			       "-D JDBC-Driver destination\n" +
			       "-H JDBC-URL    destination\n" +
			       "-T Destination Table to write into\n");
	    System.exit(0);
	}



	
	String sDriver=null;
	String sUrl=null;
	String sUser=null;
	String sPassword=null;
	boolean sAuth=false;
	String sTable=null;
	String dDriver=null;
	String dUrl=null;
	String dUser=null;
	String dPassword=null;
	boolean dAuth=false;
	String dTable=null;
	boolean cdTable=false;
	boolean dEmpty = false;
	boolean vb = false;

	int i = 0;

	while ( i < args.length) {

	    if (args[i].equals("-d")) {
		i++;
		sDriver = args[i];
	    }
	    if (args[i].equals("-h")) {
		i++;
		sUrl = args[i];
	    }
	    if (args[i].equals("-u")) {
		i++;
		sUser = args[i];
	    }
	    if (args[i].equals("-p")) {
		i++;
		sPassword = args[i];
	    }
	    if (args[i].equals("-a")) {
		sAuth = true;
	    }
	    if (args[i].equals("-t")) {
		i++;
		sTable = args[i];
	    }
	    if (args[i].equals("-D")) {
		i++;
		dDriver = args[i];
	    }
	    if (args[i].equals("-H")) {
		i++;
	        dUrl = args[i];
	    }
	    if (args[i].equals("-U")) {
		i++;
		dUser = args[i];
	    }
	    if (args[i].equals("-P")) {
		i++;
		dPassword = args[i];
	    }
	    if (args[i].equals("-A")) {
		dAuth = true;
	    }
	    if (args[i].equals("-T")) {
		i++;
		dTable = args[i];
	    }
	    if (args[i].equals("-C")) {
		cdTable = true;
	    }
	    if (args[i].equals("-E")) {
		dEmpty = true;
	    }
	    if (args[i].equals("-v")) {
		vb = true;
	    }
	    if (i == 0 ) { 
		System.out.println("oops, you really need to tell me what to do");
		System.exit(0);
	    }
	    i++;
	}

	DbCopy dbc = new DbCopy(sDriver, sUrl, sUser, sPassword, sAuth, sTable, dDriver, dUrl, dUser, dPassword, dAuth, dTable, cdTable, dEmpty, vb);
    }
}
