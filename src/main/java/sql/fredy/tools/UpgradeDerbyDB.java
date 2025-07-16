/**
 * This Class is part of Fredy's SQL-Admin Tool.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, like: - create/ drop tables - create indices - perform
 * sql-statements - simple form - a guided query - Data Export and a other
 * usefull things in DB-arena
 *
 * Admin (Version see below) Copyright (c) 1999 Fredy Fischer sql@hulmen.ch
 *
 * Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 *
 * The icons used in this application are from Dean S. Jones
 *
 * Icons Copyright(C) 1998 by Dean S. Jones dean@gallant.com
 * www.gallant.com/icons.htm
 *
 * CalendarBean is Copyright (c) by Kai Toedter
 *
 * MSeries is Copyright (c) by Martin Newstead
 *
 * POI is from the Apache Foundation
 *
 *
 *   Copyright (c) 2017 Fredy Fischer, sql@hulmen.ch
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *
 */
package sql.fredy.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

/*
 As of Version Version 4.4.2  2021-03-29 The Derby-DB structure has changed
 In a multiuser environment, the tools does not work, because of serving more than one DB by Derby running on the same server
 Therefore the structure of the Derby-DB has changed to contain the name of the User.  The DB is created/found based on these ruleS:

- if the environment variable sql.fredy.admin.configdb is set to contain the name of the DB
- if this is not set, it takes the same DB as until now, but the Tables are going to be changed

 An import goes as this 
1) read all fields into an internal Array
2) drop the table
3) create the new Table
4) write the content of the internal Array

for these Tables:
ADMINPARAMETER
CODECOMPLETION
NOTES
QUERYHISTORY


This Tool updates the DB
 */
public class UpgradeDerbyDB {

    private String user;
    private String oldDerbyFile;

    public UpgradeDerbyDB(String user, String derbyFile) {
        setUser(user);
        setOldDerbyFile(derbyFile);

        System.out.println("Processing file " + derbyFile + " for user " + user);
        upgradeAdminparameter();
        updgradeNotes();
        upgradeQueryhistory();
        upgradeCodeCompletion();

    }

    private void upgradeAdminparameter() {

        System.out.println("Updgrading Adminparameters..");

        ResultSet reader = null;
        Connection con = null;
        Statement stmt = null;
        PreparedStatement writer = null;
        PreparedStatement cleaner = null;

        ArrayList<DerbyAdminparameter> list = new ArrayList();

        try {
            con = DriverManager.getConnection("jdbc:derby:" + getOldDerbyDb());
            stmt = con.createStatement();
            reader = stmt.executeQuery("select NAME,CONTENT from ADMINPARAMETER");
            while (reader.next()) {
                DerbyAdminparameter ap = new DerbyAdminparameter();
                ap.setName(reader.getString("NAME"));
                ap.setContent(reader.getString("CONTENT"));
                list.add(ap);
            }
            con.close();
            stmt.close();
            reader.close();

            shutDownDerby(getOldDerbyDb());
            
            con = DriverManager.getConnection("jdbc:derby:" + getNewDerbyDb());
            stmt = con.createStatement();

            // Check if Table exists in the Destination            
            DatabaseMetaData dmd = con.getMetaData();
            reader = dmd.getTables(null, null, "ADMINPARAMETER", null);
            if (reader.next()) {
                // the Table exists, does it have the Field TOOLUSER
                reader.close();
                reader = dmd.getColumns(null, null, "ADMINPARAMETER", "TOOLUSER");
                if (reader.next()) {
                    // there is such a Column, do not change or delete      

                }

            } else {
                stmt.executeUpdate("create table APP.ADMINPARAMETER (ToolUser VARCHAR(128), Name VARCHAR(255), Content VARCHAR(4096) )");
            }

            cleaner = con.prepareStatement("delete from ADMINPARAMETER where TOOLUSER = ?");
            cleaner.setString(1, getUser());
            cleaner.executeUpdate();
            cleaner.close();

            reader.close();
            stmt.close();

            // now we fill the data in...
            writer = con.prepareStatement("insert into ADMINPARAMETER (TOOLUSER,NAME,CONTENT) values ( ?,?,? )");
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                DerbyAdminparameter ap = (DerbyAdminparameter) iter.next();
                writer.setString(1, getUser());
                writer.setString(2, ap.getName());
                writer.setString(3, ap.getContent());
                writer.addBatch();
            }
            writer.executeBatch();
            writer.close();
            con.close();

        } catch (SQLException sqlex) {
            System.out.println("upgradeAdminparameter() throws SQL-Exception: " + sqlex.getMessage());
            sqlex.printStackTrace();
        }
        System.out.println("done updgrading Adminparameters");
        shutDownDerby(getNewDerbyDb());
    }

    private void upgradeCodeCompletion() {

        System.out.println("Updgrading CodeCompletion...");

        ResultSet reader = null;
        Connection con = null;
        Statement stmt = null;
        PreparedStatement writer = null;
        PreparedStatement cleaner = null;

        ArrayList<DerbyCodeCompletion> list = new ArrayList();

        try {
            con = DriverManager.getConnection("jdbc:derby:" + getOldDerbyDb());
            stmt = con.createStatement();
            reader = stmt.executeQuery("select TEMPLATENAME,ABBREVIATION,CODETEMPLATE, DESCRIPTION  from CODECOMPLETION");
            while (reader.next()) {
                DerbyCodeCompletion dc = new DerbyCodeCompletion();
                dc.setTemplatename(reader.getString("TEMPLATENAME"));
                dc.setAbbreviation(reader.getString("ABBREVIATION"));
                dc.setCodetemplate(reader.getString("CODETEMPLATE"));
                dc.setDescription(reader.getString("DESCRIPTION"));
                list.add(dc);
            }
            con.close();
            stmt.close();
            reader.close();

            shutDownDerby(getOldDerbyDb());
            
            con = DriverManager.getConnection("jdbc:derby:" + getNewDerbyDb());
            stmt = con.createStatement();

            // Check if Table exists in the Destination            
            DatabaseMetaData dmd = con.getMetaData();
            reader = dmd.getTables(null, null, "CODECOMPLETION", null);
            if (reader.next()) {
                // the Table exists, does it have the Field TOOLUSER
                reader.close();
                reader = dmd.getColumns(null, null, "CODECOMPLETION", "TOOLUSER");
                if (reader.next()) {
                    // there is such a Column, do not change or delete                       
                }
            } else {
                stmt.executeUpdate("create table APP.CodeCompletion (\n"
                        + " ToolUser VARCHAR(128) not null,\n"
                        + "	TemplateName VARCHAR(25) NOT NULL ,\n"
                        + "	Abbreviation VARCHAR(25) ,\n"
                        + "	CodeTemplate VARCHAR(4096) ,\n"
                        + "	Description VARCHAR(4096)  ,\n"
                        + "	PRIMARY KEY (ToolUser,TemplateName)\n"
                        + ")");
                stmt.executeUpdate("create unique index ccIdx on APP.CodeCompletion (Abbreviation, ToolUser)");
            }

            // we delete already imported Rows
            cleaner = con.prepareStatement("delete from CODECOMPLETION where TOOLUSER = ?");
            cleaner.setString(1, getUser());
            cleaner.executeUpdate();
            cleaner.close();

            reader.close();
            stmt.close();

            // now we fill the data in...
            writer = con.prepareStatement("insert into CODECOMPLETION (TOOLUSER,TEMPLATENAME,ABBREVIATION,CODETEMPLATE,DESCRIPTION) values ( ?,?,?,?,?)");
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                DerbyCodeCompletion dc = (DerbyCodeCompletion) iter.next();
                writer.setString(1, getUser());
                writer.setString(2, dc.getTemplatename());
                writer.setString(3, dc.getAbbreviation());
                writer.setString(4, dc.getCodetemplate());
                writer.setString(5, dc.getDescription());
                writer.addBatch();
            }
            writer.executeBatch();
            writer.close();
            con.close();

        } catch (SQLException sqlex) {
            System.out.println("upgradeCodeCompletion() throws SQL-Exception: " + sqlex.getMessage());
        }
        System.out.println(list.size() + " Records imported into CODECOMPLETION");
        System.out.println("done updgrading CodeCompletion");    
        shutDownDerby(getNewDerbyDb());
    }

    private void updgradeNotes() {

        System.out.println("Updgrading Notes...");

        ResultSet reader = null;
        Connection con = null;
        Statement stmt = null;
        PreparedStatement writer = null;
        PreparedStatement cleaner = null;

        ArrayList<DerbyNotes> list = new ArrayList();

        try {
            con = DriverManager.getConnection("jdbc:derby:" + getOldDerbyDb());
            stmt = con.createStatement();
            reader = stmt.executeQuery("select RUNAT,TITLE,NOTE, HASHCODE  from NOTES");
            while (reader.next()) {
                DerbyNotes dn = new DerbyNotes();
                dn.setRunat(reader.getTimestamp("RUNAT"));
                dn.setTitle(reader.getString("TITLE"));
                dn.setNote(reader.getString("NOTE"));
                dn.setHashcode(reader.getInt("HASHCODE"));
                list.add(dn);
            }
            con.close();
            stmt.close();
            reader.close();

            shutDownDerby(getOldDerbyDb());
            
            con = DriverManager.getConnection("jdbc:derby:" + getNewDerbyDb());
            stmt = con.createStatement();

            // Check if Table exists in the Destination            
            DatabaseMetaData dmd = con.getMetaData();
            reader = dmd.getTables(null, null, "NOTES", null);
            if (reader.next()) {
                // the Table exists, does it have the Field TOOLUSER
                reader.close();
                reader = dmd.getColumns(null, null, "NOTES", "TOOLUSER");
                if (reader.next()) {
                    // there is such a Column, do not change or delete    

                }
            } else {
                stmt.executeUpdate("create table APP.NOTES (\n"
                        + "ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n"
                        + "ToolUser VARCHAR(128),\n"
                        + "RunAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                        + "Title varchar(1024),\n"
                        + "Note VARCHAR(32672),\n"
                        + "HashCode INTEGER "
                        + ")");
                stmt.executeUpdate("create unique index ccIdx on APP.CodeCompletion (Abbreviation, ToolUser)");
            }

            cleaner = con.prepareStatement("delete from NOTES where TOOLUSER = ?");
            cleaner.setString(1, getUser());
            cleaner.executeUpdate();
            cleaner.close();

            reader.close();
            stmt.close();

            // now we fill the data in...
            writer = con.prepareStatement("insert into NOTES (TOOLUSER,RUNAT,TITLE,NOTE,HASHCODE) values ( ?,?,?,?,? )");
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                DerbyNotes dn = (DerbyNotes) iter.next();
                writer.setString(1, getUser());
                writer.setTimestamp(2, dn.getRunat());
                writer.setString(3, dn.getTitle());
                writer.setString(4, dn.getNote());
                writer.setInt(5, dn.getHashcode());
                writer.addBatch();
            }
            writer.executeBatch();
            writer.close();
            con.close();

        } catch (SQLException sqlex) {
            System.out.println("updgradeNotes() throws SQL-Exception: " + sqlex.getMessage());
        }
        System.out.println(list.size() + " Records imported into NOTES");
        System.out.println("done updgrading Notes");     
        shutDownDerby(getNewDerbyDb());
    }

    private void upgradeQueryhistory() {

        System.out.println("Updgrading QueryHistory...");

        ResultSet reader = null;
        Connection con = null;
        Statement stmt = null;
        PreparedStatement writer = null;
        PreparedStatement cleaner = null;

        ArrayList<DerbyQueryhistory> list = new ArrayList();

        try {
            con = DriverManager.getConnection("jdbc:derby:" + getOldDerbyDb());
            stmt = con.createStatement();
            reader = stmt.executeQuery("select RUNAT,DATABASE,QUERY, HASHCODE  from QUERYHISTORY");
            while (reader.next()) {
                DerbyQueryhistory qh = new DerbyQueryhistory();
                qh.setRunat(reader.getTimestamp("RUNAT"));
                qh.setDatabase(reader.getString("DATABASE"));
                qh.setQuery(reader.getString("QUERY"));
                qh.setHashcode(reader.getInt("HASHCODE"));
                list.add(qh);
            }

            con.close();
            stmt.close();
            reader.close();

            shutDownDerby(getOldDerbyDb());
            
            con = DriverManager.getConnection("jdbc:derby:" + getNewDerbyDb());
            stmt = con.createStatement();

            // Check if Table exists in the Destination            
            DatabaseMetaData dmd = con.getMetaData();
            reader = dmd.getTables(null, null, "QUERYHISTORY", null);
            if (reader.next()) {
                // the Table exists, does it have the Field TOOLUSER
                reader.close();
                reader = dmd.getColumns(null, null, "QUERYHISTORY", "TOOLUSER");
                if (reader.next()) {
                    // there is such a Column, do not change or delete                     
                }
            } else {
                stmt.executeUpdate("create table APP.QueryHistory (\n"
                        + "RunAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                        + "ToolUser varchar(128),\n"
                        + "Database varchar(1024),\n"
                        + "Query VARCHAR(32672),\n"
                        + "HashCode INTEGER "
                        + ")");
                stmt.executeUpdate("create INDEX APP.QUERYHISTORYIDX on APP.QueryHistory(HashCode, ToolUser)");
                stmt.executeUpdate("create INDEX APP.UserIDX on APP.QueryHistory(ToolUser,Database)");
            }

            cleaner = con.prepareStatement("delete from QUERYHISTORY where TOOLUSER = ?");
            cleaner.setString(1, getUser());
            int z = cleaner.executeUpdate();
            System.out.println("Deleted " + z + " Rows from QUERYHISTORY for TOOLUSER=" + getUser());
            cleaner.close();

            reader.close();
            stmt.close();

            // now we fill the data in...
            writer = con.prepareStatement("insert into QUERYHISTORY (RUNAT,TOOLUSER,DATABASE,QUERY,HASHCODE) values ( ?,?,?,?,?)");
            Iterator iter = list.iterator();
            while (iter.hasNext()) {
                DerbyQueryhistory qh = (DerbyQueryhistory) iter.next();
                writer.setTimestamp(1, qh.getRunat());
                writer.setString(2, getUser());
                writer.setString(3, qh.getDatabase());
                writer.setString(4, qh.getQuery());
                writer.setInt(5, qh.getHashcode());
                writer.addBatch();
            }
            writer.executeBatch();
            writer.close();
            con.close();

        } catch (SQLException sqlex) {
            System.out.println("upgradeQueryhistory() throws SQL-Exception: " + sqlex.getMessage());
        }
        System.out.println(list.size() + " Records imported into QUERYHISTORY");
        System.out.println("done updgrading Queryhistory");   
        shutDownDerby(getNewDerbyDb());
    }

    private String getOldDerbyDb() {
        return getOldDerbyFile();

    }

    private String getNewDerbyDb() {
        String adminConfigDb = null;
        try {
            adminConfigDb = System.getenv("sql.fredy.admin.configdb");
            if (adminConfigDb != null) {
                return adminConfigDb;
            }
        } catch (Exception e) {
            System.out.println("Can not find Environment variable for config DB, using standard");
        }

        String directory = System.getProperty("admin.work");

        if (directory == null) {
            directory = System.getProperty("user.home");
        }

        directory = directory + File.separator + "sqladmin" + File.separator + "work";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return directory + File.separator + "SQLAdminWorkDB";
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    private void shutDownDerby(String db) {
        try {
            DriverManager.getConnection("jdbc:derby:" + db + ";shutdown=true");
        } catch (SQLException sqlex) {                        
            //System.out.println("Ecxeption whil shutting down " + db + " " + sqlex.getMessage() + " Status:" + sqlex.getSQLState());
        }
    }

    public static String readFromPrompt(String text, String defValue) {
        String fromPrompt = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(text + " (Default: " + defValue + ") ");
        try {
            fromPrompt = br.readLine();
            if (fromPrompt.length() < 1) {
                fromPrompt = defValue;
            }
        } catch (IOException ioe) {
            fromPrompt = defValue;
        }
        return fromPrompt;
    }

    public static void main(String[] args) {
        System.out.println("Upgrade the internal SQL Admin Derby DB\nrun it to get up to the version 4.4.2 or to merge personal Databases into the global one for the shared environment");
        String user = System.getProperty("user.name");

        String directory = System.getProperty("admin.work");

        if (directory == null) {
            directory = System.getProperty("user.home");
        }

        directory = directory + File.separator + "sqladmin" + File.separator + "work";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        directory = directory + File.separator + "SQLAdminWorkDB";

        while (true) {
            user = readFromPrompt("Enter user name ( q =quit", user);
            if (user.equalsIgnoreCase("q")) {
                System.exit(0);
            }
            directory = readFromPrompt("Path to users DerbyDB: ", directory);
            if (directory.equalsIgnoreCase("q")) {
                System.exit(0);
            }

            UpgradeDerbyDB derby = new UpgradeDerbyDB(user, directory);
        }
    }

    /**
     * @return the oldDerbyFile
     */
    public String getOldDerbyFile() {
        return oldDerbyFile;
    }

    /**
     * @param oldDerbyFile the oldDerbyFile to set
     */
    public void setOldDerbyFile(String oldDerbyFile) {
        this.oldDerbyFile = oldDerbyFile;
    }

}
