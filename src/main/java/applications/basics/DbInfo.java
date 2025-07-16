package applications.basics;

/** 

    DbInfo is Part of Admin and delivers lot of the Meta-Data-Info of a Database

    Admin is a Tool around mySQL to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

    Admin Version see below
    

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


    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

**/


import java.sql.*;
import java.util.Vector;
import java.util.logging.*;


public class DbInfo extends BasicAdmin {


    private Logger logger;

    public DbInfo(String host, String user, String password, String database) {

	super(host, user, password, database, null);

	logger = Logger.getLogger("sql.fredy.admin");
	setConnection();

	setProductName();
	setProductVersion();
	setDriverName();
	setDriverVersion();	
        
    }

    public DbInfo(t_connect con) {
	super(con);
	logger = Logger.getLogger("sql.fredy.admin");

	setProductName();
	setProductVersion();
	setDriverName();
	setDriverVersion();
	

    }

    /**
       * Get the value of con.
       * @return Value of con.
       */
    public Connection getConnection() {
	getCon();
	return con.con;}
    
    /**
       * Set the value of con.
       * @param v  Value to assign to con.
       */
    private boolean setConnection() {
       boolean ok = true;
       con = new t_connect(getHost(), getUser(), getPassword(),getDatabase());
       setCon(con);
       if (con.getError() != null) {
	   logger.log(Level.SEVERE,"User: " + getUser() + 
		      " Can not connect to the Database!\nError: " + con.getError());
	   ok = false;
       }
       return ok;
    }
    
       

    public void close() {
	
	try {	   
	    getConnection().close();
	}  catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception on closing connetion " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString());
	}       


    }



    DatabaseMetaData dmd;
    
    /**
       * Get the value of dmd.
       * @return Value of dmd.
       */
    public DatabaseMetaData getDmd() {
	try {
	    dmd = getConnection().getMetaData();
	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while gathering MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString());
	}
	return dmd;

    }
    
 
    
    
    Vector catalog;
    
    /**
       * Get the value of catalogs.
       * @return Value of catalogs.
       */
    public Vector getCatalog() {
	catalog = new Vector();
	ResultSet rs;
	try {
	    try {
	      rs = getDmd().getCatalogs();
	    }catch (Exception unexpected) { rs = null; }

	    if ( rs != null) {
		while (rs.next()) { 
		    String s = rs.getString("TABLE_CAT"); 
		    
		    // I'm struggeling around with PostgreSQL and its template_
		    if ( ! ( ( getProductName().toLowerCase().startsWith("postgresql") ) && 
			     ( s.startsWith("template") ) ) ) 
			catalog.addElement((String)rs.getString("TABLE_CAT"));
		}        
	    }
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while reading catalogs from MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString());
	}       
       if (catalog.size() < 1) catalog.addElement(getDatabase());

	return catalog;
    }
    

    
    Vector schemas;
    
    /**
       * Get the value of schemas.
       * @return Value of schemas.
       */

    public Vector getSchemas() {
	schemas = new Vector();
	ResultSet rs;
	try {
	    try {
		rs = getDmd().getSchemas();
	    } catch (Exception unexpectedException) { rs = null; }
	    if (rs != null ) {
		while (rs.next()) { 
		    //schemas.addElement((String)rs.getString("TABLE_SCHEM"));
		    schemas.addElement((String)rs.getString(1));

		}        
	    }	   
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while schematas from MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString());
	}    
	if (schemas.size() < 1) schemas.addElement("%");
	return schemas;
    }


    private String spaceFixer(String s) {
	s = s.trim();
	if (s.indexOf(' ') > 0 ) s = "'" + s + "'";
	return s;
    }
 
    Vector tables;
    
    /**
       * Get the value of tables.
       * @return Value of tables.
       */
    public Vector getTables(String db) {
	tables = new Vector();
	String[] tableTypes = { "TABLE", "VIEW","ALIAS","SYNONYM" };
 
	// Speciallity for Empress
	if (getProductName().toLowerCase().startsWith("empress") ) tableTypes = null;


	try {
	    ResultSet rs = getDmd().getTables(db,"%","%",tableTypes);
	    while (rs.next()) {               
		logger.log(Level.INFO,"Fetching Metadata, Table: " + 
			   rs.getString(3));
		tables.addElement(spaceFixer(rs.getString(3)));                 
	    }
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while getTables(string) from MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString());
	}       
	return tables;
    }
	    
   
    public Vector getTables(String db, String schema) {
	tables = new Vector();
	String[] tableTypes = { "TABLE", "VIEW","ALIAS","SYNONYM" };
        StringBuffer tt = new StringBuffer();;
	for (int i=0;i< tableTypes.length;i++) 
	    tt.append(tableTypes[i] + ",");

	try {
	    ResultSet rs = getDmd().getTables(db,schema,"%",tableTypes);
	    while (rs.next()) {
		logger.log(Level.INFO,"Fetching Metadata (for Schema:" + 
			   schema + ", TableTypes: " + tt + "), Table: " + 
			   rs.getString(3));
		tables.addElement(rs.getString(3));                 
	    }
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while getTables(string,string) from MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString());
	}       
	return tables;
    }
   public Vector getTables(String db, String schema,String type) {
	tables = new Vector();
	String[] tableTypes = { type };
        StringBuffer tt = new StringBuffer();;
	for (int i=0;i< tableTypes.length;i++) 
	    tt.append(tableTypes[i] + ",");

	// Speciallity for Empress
	if (getProductName().toLowerCase().startsWith("empress") ) tableTypes = null;


	try {
	    ResultSet rs = getDmd().getTables(db,schema,"%",tableTypes);
	    while (rs.next()) {
 		logger.log(Level.INFO,"Fetching Metadata (for Schema:" + 
			   schema + ", Tabletypes: " + tt + "), Table: " + 
			   rs.getString(3));
		tables.addElement(rs.getString(3));                 
	    }
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while getTables(string,string,string) from MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString());
	}       
	return tables;
    }


   public Vector getTables(String db, String schema,String type,String tableNamePattern) {
	tables = new Vector();
	String[] tableTypes = { type };

	// Speciallity for Empress
	if (getProductName().toLowerCase().startsWith("empress") ) tableTypes = null;

	try {
	    ResultSet rs = getDmd().getTables(db,schema,tableNamePattern,tableTypes);
	    while (rs.next()) {
		tables.addElement(rs.getString(3));                 
	    }
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while getTables(string,string,string,string) from MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString());
	}       
	return tables;
    }



    /**
       * Set the value of tables.
       * @param v  Value to assign to tables.
       */
    public void setTables(Vector  v) {this.tables = v;}
    

    /**
     * retrieve all SQL-Keywords, available for this DB
     **/
    public Vector getSQLWords() {
	Vector v = new Vector();
	try {
	    ResultSet rs = getDmd().getTypeInfo();
	    while (rs.next()) {
		v.addElement(rs.getString(1));                 
	    }
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception on getting SQL-KeyWords " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString());
	}
	return v;
    }
	

    
    String productName;
    
    /**
       * Get the value of productName.
       * @return Value of productName.
       */
    public String getProductName() {return productName;}
    
    /**
       * Set the value of productName.
       * @param v  Value to assign to productName.
       */
    private void setProductName() {

	productName = "";
	try {
	    productName = getDmd().getDatabaseProductName();
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while reading ProductName from MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString()); 
	} catch (Exception sqe) {
	    logger.log(Level.SEVERE,"User: " + getUser() + "unexpected exception: " + 
		       sqe.getMessage().toString());
	}   
    }

    
    String productVersion;
    
    /**
       * Get the value of productVersion.
       * @return Value of productVersion.
       */
    public String getProductVersion() {return productVersion;}
    
    /**
       * Set the value of productVersion.
       * @param v  Value to assign to productVersion.
       */
    public void setProductVersion() {

	productVersion = "";
	try {
	    productVersion = getDmd().getDatabaseProductVersion();
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while reading ProductVersion from MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString()); 
	} catch (Exception sqe) {
	    logger.log(Level.SEVERE,"User: " + getUser() + "unexpected exception: " + 
		       sqe.getMessage().toString());
	}
    }	

    
    String driverName;
    
    /**
       * Get the value of driverName.
       * @return Value of driverName.
       */
    public String getDriverName() {return driverName;}
    
    /**
       * Set the value of driverName.
       * @param v  Value to assign to driverName.
       */
    public void setDriverName() {

	driverName = "";
	try {
	    driverName = getDmd().getDriverName();
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while reading JDBC-Driver-Name from MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString()); 
	} catch (Exception sqe) {
	    logger.log(Level.SEVERE,"User: " + getUser() + "unexpected exception: " + 
		       sqe.getMessage().toString());
	}
    }
    
    
    String driverVersion;
    
    /**
       * Get the value of driverVersion.
       * @return Value of driverVersion.
       */
    public String getDriverVersion() {return driverVersion;}
    
    /**
       * Set the value of driverVersion.
       * @param v  Value to assign to driverVersion.
       */
    public void setDriverVersion() {

	driverVersion = "";
	try {
	    driverVersion = getDmd().getDriverVersion();
 	} catch (SQLException sqe) { 
	    logger.log(Level.WARNING,"User " + getUser() + 
		       " throws Exception while reading JDBC-Driver Version from MetaData " + 
		       " |JDBC-Driver: " + getDriverName() + 
		       " |Database: " +  getDatabase() + 
		       " |Error-Code: " + sqe.getErrorCode() +
		       " |SQL-State: " + sqe.getSQLState()   +
		       " |Exception: " + sqe.getMessage().toString()); 
	} catch (Exception sqe) {
	    logger.log(Level.SEVERE,"User: " + getUser() + "unexpected exception: " + 
		       sqe.getMessage().toString());
	}
    }

    public SingleColumnInfo getColumnInfo(String db,String table,String column) {
	SingleColumnInfo s = new SingleColumnInfo(getDmd(),db,table,column);
	return s;
    }

    public String getAllInfo() {


	String s="";
	s =     "Productname:         " + getProductName() + "\n";
	s = s + "Productversion:      " + getProductVersion() + "\n";
	s = s + "JDBC-Driver:         " + getDriverName() + "\n";
	s = s + "JDBC-Driver Version: " + getDriverVersion() + "\n"; 

	Vector v = new Vector();
	Vector v0= new Vector();
	Vector v1= new Vector();
	Vector v2= new Vector();

	v = getCatalog();
	s = s + "\nCatalogs:            \n";
              
	if (v.size() < 1) v.addElement(getDatabase());

	for (int i = 0; i < v.size();i++) {
	    
	    s = s + (String)v.elementAt(i) + "\n";

	    v0 = getSchemas();
	    for (int j=0;j < v0.size(); j++){
		s = s + "\n\nSchema: " + (String) v0.elementAt(j) + "\n";
		
		v2 = new Vector();
		v2 = getTables((String)v.elementAt(i),(String)v0.elementAt(j));

		for (int l=0; l < v2.size();l++) {
		    s = s + "\n\tTable " + (String) v2.elementAt(l) + "\n";		                    
		    Vector pkv = new Vector();
		    pkv = getPk((String)v2.elementAt(l));
		    s = s + "\t\tPrimarykeys: ";
		    //System.out.println("Schema: " +(String) v0.elementAt(j) + "\tTable:" + v2.elementAt(l));
		    for (int m=0;m < pkv.size();m++) {

			PrimaryKey pk = new PrimaryKey();
			pk = (PrimaryKey)pkv.elementAt(m);
			s = s + "   " + pk.getColumnName();
		    }
		    s = s + "\n\n";    

		    v1 = new Vector();
		    v1 = getColumnNames((String)v2.elementAt(l));
		    for (int k = 0; k < v1.size();k++) {
			SingleColumnInfo si = new SingleColumnInfo(getDmd(),(String)v.elementAt(i),(String)v0.elementAt(j),(String)v2.elementAt(l),(String)v1.elementAt(k));
			s = s + "\t\tColumnname: " + si.getColumn_name() + "\n";
			s = s + "\t\tType name : " + si.getType_name()   + "\n";
			s = s + "\t\tSize      : " + Integer.toString(si.getColumn_size()) + "\n";
			s = s + "\t\tNullable  : " + si.getNullable() + "\n";
			s = s + "\t\tRemarks   : " + si.getRemarks() + "\n";
			s = s + "\t\tColumn def: " + si.getColumn_def() + "\n\n";
		    }			
		}
	    }
	}
	return s;

	}

    public Vector getPk(String table) {
	Vector v = new Vector();
	try {
	    ResultSet rs = dmd.getPrimaryKeys(null,null,table);
	    while (rs.next()) {
		PrimaryKey pk = new PrimaryKey(rs.getString(3),rs.getString(4),rs.getShort(5),rs.getString(6));
		v.addElement((PrimaryKey)pk);
	    }
	} catch(SQLException sqe) { 
		System.out.println("\nError while reading Catalogs from MetaData\n" + 
		                   "Exception: \n"+ sqe.getMessage() + 
		                   "\nError-Code: " + sqe.getErrorCode() +
		                   "\nSQL-State: " + sqe.getSQLState() + "\n");
       }       
	return v;	    
    }


    public String getTableDescription(String table, String leadingCharacter) {

	        String s = "";
		Vector pkv = new Vector();
	        pkv = getPk(table);
		s = s + leadingCharacter + "\t\tPrimarykeys: ";
		for (int m=0;m < pkv.size();m++) {
		    PrimaryKey pk = new PrimaryKey();
		    pk = (PrimaryKey)pkv.elementAt(m);
		    s = s + "   " + pk.getColumnName();
		}
		s = s + "\n" + leadingCharacter + "\n";    

		Vector v1 = new Vector();
		v1 = getColumnNames(table);
		for (int k = 0; k < v1.size();k++) {
		    SingleColumnInfo si = new SingleColumnInfo(getDmd(),getDatabase(),table,(String)v1.elementAt(k));
		    s = s + leadingCharacter + "\t\tColumnname: " + si.getColumn_name() + "\n";
		    s = s + leadingCharacter + "\t\tType name : " + si.getType_name()   + "\n";
		    s = s + leadingCharacter + "\t\tSize      : " + Integer.toString(si.getColumn_size()) + "\n";
		    s = s + leadingCharacter + "\t\tNullable  : " + si.getNullable() + "\n";
		    s = s + leadingCharacter + "\t\tRemarks   : " + si.getRemarks() + "\n";
		    s = s + leadingCharacter + "\t\tColumn def: " + si.getColumn_def() + "\n" + leadingCharacter + "\n";
		}		

		return s;
    }



	public Vector getColumnNames(String table) {

	    Vector v = new Vector();
	    try {
		ResultSet rs = dmd.getColumns(null,null,table,"%");
		while (rs.next()) {
		    v.addElement((String)rs.getString(4));
		}
	    } catch(SQLException sqe) { 
		System.out.println("\nError while reading Catalogs from MetaData\n");
		System.out.println("Exception: \n"+ sqe.getMessage());
		System.out.println("\nError-Code: " + sqe.getErrorCode());
		System.out.println("\nSQL-State: " + sqe.getSQLState() + "\n");
	    }       

	    return v;
	}
	public static void main(String args[]) {

	    if (args.length !=4) {

		System.out.println("Syntax: java sql.fredy.admin.DbInfo host user password DB");
		System.exit(0);
	    }
	    String db = args[3];
	    if (db.toLowerCase() == "null") db = null;
	    DbInfo dbi = new DbInfo(args[0],args[1],args[2],db);
	    System.out.println(dbi.getAllInfo());
	    dbi.close();

	}


}    
	
    
