package sql.fredy.metadata;


/** 
 * It lists the available DB's of a RDBMS
 *
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
import java.util.*;
import javax.swing.*;

public class DBList  {


// Fredy's make Version
private static String fredysVersion = "Version 2.0   25. Dec. 2020, uses DataSource";
   

    public Vector databases;

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
    
    private DbInfo dbi;

    
    
    /**
       * Get the value of productName.
       * @return value of productName.
       */
    public String getProductName() {return dbi.getProductName();}
    
  


    public DBList (String database) {
       setDatabase(database);
       init();       
    }

    public DBList () {     
       init();       
    }

    private void init() {
       dbi = new DbInfo();
    }
    



    public JComboBox toComboBox() {
	JComboBox cb = new JComboBox();
	for (int i = 0; i < getDBs().size(); i++) {
	    cb.addItem((String)dbi.getCatalog().get(i));
	}
	cb.setSelectedIndex(0);
	return cb;
    }


    public JList toJList() {
	JList list = new JList();
	list.setListData(dbi.getCatalog());
	list.setSelectedIndex(0);
	return list;
    }

    public Vector getDBs() { return dbi.getCatalog(); }


    public Vector getSchemas() {
	return dbi.getSchemas();
       
    }



    public Vector getTables(String db,String schema, String[] type) {

	return dbi.getTables(db,schema,type);

    }



    public String toString() {
	
	String s = "";
        //System.out.println("Size: " + databases.size());
	try {
	for (int i = 0; i < dbi.getCatalog().size(); i++) {
	    s = s + (String)dbi.getCatalog() .get(i) + "\n";
	}
	} catch (Exception unexpectedException) { s="Empty"; }
	return s;
    }


    public static void main(String args[]) {


	if (args.length != 4 ) {
	    System.out.println("DB-List\n\nSyntax: java sql.fredy.metadata.DBList database");
	    System.exit(0);
	}
	DBList dbl = new DBList(args[0]);
	System.out.println(dbl.toString());

    }


}
