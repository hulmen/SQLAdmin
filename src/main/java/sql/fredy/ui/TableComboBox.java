package sql.fredy.ui;


/**  
   this is a JComboBox with all tables of a rdbms-schema
 
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
import sql.fredy.metadata.DbInfo;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JFrame;

public class TableComboBox extends JComboBox {


// Fredy's make Version
private static String fredysVersion = "Version 2.0  25. Dec. 2020, uses DataSource ";

public String getVersion() {return fredysVersion; }

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
    

    public TableComboBox(String database, String schema) {
  	
	setDatabase(database);
	setSchema(schema);
	DbInfo dbi = new DbInfo();
	Vector v = new Vector();
	v = dbi.getTables(getDatabase(),getSchema());
	for (int i=0;i < v.size();i++) this.addItem((String)v.elementAt(i));
        this.setSelectedIndex(0);
        dbi.close();
    }

    public static void main(String args[]) {

	if (args.length !=2 ) {
	    System.out.println("TableCombox\n" +
			       "------------\n" +
			       "Syntax: java sql.fredy.admin.TableComboBox database schema\nWhere schema is a valid schema-name or %");
	} else {
	    TableComboBox tbc = new TableComboBox(args[0], args[1]);
	    JFrame frame = new JFrame("TableCombo");
	    frame.getContentPane().add(tbc);
	    frame.pack();
	    frame.addWindowListener(new WindowAdapter() {
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {System.exit(0);}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}});
	    frame.setVisible(true); 
	}


    }

}
