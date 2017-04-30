package sql.fredy.metadata;

/** 
 * DBTree is a part of Admin  it displays the MetaData of a RDBMS as a tree.
 * 
 * Collection of all metadata might take longer and might consume a lot of memory
 * when used with professional highend RDBMS. But it's worth to see...
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


import sql.fredy.ui.RDBMSTreeRenderer;
import sql.fredy.ui.LoadImage;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.sql.*;
import java.util.Vector;
import java.io.*;

import sql.fredy.share.t_connect;

public class DBTree extends JPanel {


// Fredy's make Version
private static String fredysVersion = "Version 1.4.2   16. May 2007";

LoadImage loadImage = new LoadImage();

public String getVersion() {return fredysVersion; }

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
        setUser(v.getUser());
        setHost(v.getHost());
        setDatabase(v.getDatabase());
        setPassword(v.getPassword());
	this.con = v;
    }
    


    private DBList dbl;

    String[] tableTypes = { "TABLE", "VIEW", "SYNONYM" }; //,"ALIAS"};


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
 
    public DBTree (String host, String user, String password, String database, String[] tableTypes) {

	setHost(host);
	setUser(user);
	setPassword(password);
	setDatabase(database);

	this.tableTypes = tableTypes;

	DBTreeInit();


    }


   

    public DBTree (String host, String user, String password, String database) {

	setHost(host);
	setUser(user);
	setPassword(password);
	setDatabase(database);

	DBTreeInit();


    }
   public DBTree (t_connect con) {

       setCon(con);       
       DBTreeInit();


    }

   public DBTree (t_connect con, String[] tableType) {

       setCon(con);       
       this.tableTypes = tableTypes;
       DBTreeInit();


    }

    private void  DBTreeInit() {


	// doing the images
	//UIManager.put("Tree.closedIcon",loadImage.getImage("closeddb.gif"));
	UIManager.put("Tree.openIcon",loadImage.getImage("opendb.gif"));
	UIManager.put("Tree.leafIcon",loadImage.getImage("column.gif"));

		     
        if ( con == null ) {
	    dbl = new DBList(getHost(),getUser(),getPassword(),getDatabase());
	} else {
	    dbl = new DBList(getCon());
	}



        //Create the nodes.
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(dbl.getProductName());
        createNodes(top);

        //Create a tree that allows one selection at a time.
        final JTree tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);

	tree.putClientProperty("JTree.lineStyle", "Angled");

	tree.setCellRenderer(new RDBMSTreeRenderer());


	this.add(tree);

    }

    private void addSchemas(DefaultMutableTreeNode top, String db) {
	Vector schemas = new Vector();
	schemas = dbl.getSchemas();
	for (int i = 0; i < schemas.size(); i++) {
	    String s = "";
	    String sc = (String)schemas.get(i);
	    if ( sc.startsWith("%")) 	s="all schemas";
	    if ( sc.length() < 1 )      s="Standardschema";

	    DefaultMutableTreeNode schNode = new DefaultMutableTreeNode(s + schemas.get(i));
	    addTyp(schNode,db, (String)schemas.get(i));
	    top.add(schNode);



	}


    }



    private void createNodes(DefaultMutableTreeNode top) {
	Vector databases = new Vector();
	databases = dbl.getDBs();
	if (databases.size() < 1) databases.addElement((String)"%");
	for (int i = 0; i < databases.size(); i++) {
	    DefaultMutableTreeNode dbNode = new DefaultMutableTreeNode(databases.get(i));
	    addSchemas(dbNode,(String)databases.get(i));
	    top.add(dbNode);
	}

    }

    private void addTyp(DefaultMutableTreeNode top, String db,String schema) {


	for (int i = 0; i < tableTypes.length; i++) {
	    DefaultMutableTreeNode typNode = new DefaultMutableTreeNode(tableTypes[i]);
	    addTables(typNode,db,schema,tableTypes[i]);
	    if ( ! typNode.isLeaf() ) top.add(typNode);
	    //top.add(typNode);
	}

    }

    private void addTables(DefaultMutableTreeNode top, String db, String schema,String tableTyp) {
	Vector tables = new Vector();
	tables = dbl.getTables(db,schema,new String[] {tableTyp});
	for (int i = 0; i < tables.size(); i++) {
	    DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tables.get(i));
	    addRows(tableNode,db,(String)tables.get(i));
	    top.add(tableNode);
	}

    }
 

    private void addRows(DefaultMutableTreeNode top, String db, String table) {

	Vector rows = new Vector();

	/**
	TableColumns tableColumns = new TableColumns(getHost(), getUser(), getPassword(),db, table);
	rows = tableColumns.getAllNames();
	**/
        DbInfo dbi=null;
        if ( ( db == "" ) || ( db == null ) ) db ="%";
        dbi = new DbInfo(getHost(), getUser(), getPassword(),db);

	rows = dbi.getColumnNames(table);

	for (int i = 0; i < rows.size(); i++) {
	    DefaultMutableTreeNode rowNode = new DefaultMutableTreeNode(rows.get(i));
	    top.add(rowNode);
	}
        dbi.close();
    }

    private ImageIcon loadImage(String image) {
	return loadImage.getImage(image);

   }

    public static void main(String args[]) {

	   if (args.length != 4) {
	       System.out.println("Syntax: java sql.fredy.metadata.DBTree host user password database table");
	       System.exit(0);
	   }

	   JFrame f = new JFrame("RDBMS");
	   f.addWindowListener(new WindowAdapter() {
	       public void windowActivated(WindowEvent e) {}
	       public void windowClosed(WindowEvent e) {}
	       public void windowClosing(WindowEvent e) {System.exit(0);}
	       public void windowDeactivated(WindowEvent e) {}
	       public void windowDeiconified(WindowEvent e) {}
	       public void windowIconified(WindowEvent e) {}
	       public void windowOpened(WindowEvent e) {}});
	   DBTree dbt = new DBTree(args[0], args[1],args[2], args[3]);
	   JScrollPane scp = new JScrollPane(dbt);
	   f.getContentPane().add(scp);
	   f.pack();
	   f.setVisible(true);

    }
}
