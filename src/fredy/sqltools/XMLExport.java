/*
  XMLExport.java
 
  Created on August 13, 2003, 10:56 AM
 
  This software is part of the Admin-Framework 
 
  Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
  for DB-Administrations, as create / delete / alter and query tables
  it also creates indices and generates simple Java-Code to access DBMS-tables
 
 
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
 */
package sql.fredy.sqltools;

import sql.fredy.share.t_connect;
import java.sql.*;
import java.io.*;
import java.util.logging.*;

/**
 *
 * @author sql@hulmen.ch
 */
public class XMLExport {

    /**
     * Creates a new instance of XMLExport
     */
    public XMLExport() {
    }
    /**
     * this is the connection to the DB used for this class
     */
    private Connection con = null;

    /**
     * this is the SQL-query to be executed
     */
    private String query = null;

    private File fileName = null;

    private PrintWriter out = null;

    private Logger logger = Logger.getLogger("sql.fredy.sqltools");

    private java.sql.Statement stmt = null;

    public XMLExport(java.sql.Connection con, java.io.File file, java.lang.String query) {

        this.setCon(con);
        this.setFileName(file);
        this.setQuery(query);
        export();
    }

    /**
     * get the connection to the DB to use for queries
     */
    public Connection getCon() {
        return con;
    }

    public void setCon(java.sql.Connection v) {
        try {
            // Test the connection
            DatabaseMetaData dmd = v.getMetaData();
            logger.log(Level.INFO, "Connection to " + dmd.getDatabaseProductName());
            this.con = v;
        } catch (SQLException sqle) {
            logger.log(Level.WARNING, "Connection not established");
            logger.log(Level.INFO, "resetting connection to null");
            this.con = null;
        }
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(java.lang.String v) {
        //logger.log(Level.INFO, "Query is : " + v);
        this.query = v;
    }

    public void setFileName(java.io.File v) {
        logger.log(Level.INFO, "Seeting Filename to : " + v.getName());
        this.fileName = v;
    }

    public java.io.File getFileName() {
        return fileName;
    }

    public PrintWriter getOut() {
        if (getFileName() != null) {
            try {
                out = new PrintWriter(new FileOutputStream(getFileName()));
            } catch (FileNotFoundException fne) {
                logger.log(Level.WARNING, "Can not open file " + getFileName().getName());
                logger.log(Level.FINE, "Message: " + fne.getMessage().toString());
                out = null;
            }
        }
        return out;
    }

    public java.sql.Statement getStmt() {
        if (stmt == null) {
            try {
                stmt = getCon().createStatement();
                logger.log(Level.FINE, "Statement created");
            } catch (SQLException sqle) {
                logger.log(Level.WARNING, "can not create Statement");
                logger.log(Level.FINE, "Message: " + sqle.getMessage().toString());
            }
        }
        return stmt;
    }

    private String cleanBadCharacters(String v) {
        v = v.replaceAll(" ", "_");
        v = v.replaceAll("\t","_");
        v = v.replaceAll("\r","_");
        v = v.replaceAll("\n", "_");
        return v;                
    }
    
    
    /**
     *
     */
    public void export() {

        if ((getCon() != null)
                && (getStmt() != null)
                && (getQuery() != null)
                && (getFileName() != null)) {

            ResultSet rs = null;
            ResultSetMetaData rsmd = null;
            int lineCounter = 0;
            // execute the query and fetch metadata of the result
            try {
                rs = stmt.executeQuery(getQuery());
                rsmd = rs.getMetaData();
            } catch (SQLException sqle1) {
                logger.log(Level.WARNING, "can not execute query");
                logger.log(Level.FINE, "Message: " + sqle1.getMessage().toString());
            }

            // here we loop the result row by row
            getOut();

            // XML-Header
            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            out.println("<Data>");
            out.print("<DataExport>");
            out.print("File created by Fredy's XMLExporter. XMLExporter is free software (MIT-License)");
            out.println("</DataExport>");

            try {
                while (rs.next()) {

                    // we give out the ROW-Tag
                    out.println("<row>");

                    // we have now to loop every single row to get its attributes
                    for (int i = 0; i < rsmd.getColumnCount(); i++) {
                        out.print(getTab());
                        out.print("<");
                        out.print(cleanBadCharacters(rsmd.getColumnName(i + 1)));

                        out.print(" ");
                        out.print("Table=\"");

                        if ( (rsmd.getTableName(i + 1).length() < 1) || (rsmd.getTableName(i + 1) == null)) {
                            out.print("undetecable");
                        } else {
                            out.print(cleanBadCharacters(rsmd.getTableName(i + 1)));

                        }
                        out.print("\"");

                        out.print(" ");
                        out.print("Type=\"");
                        out.print(rsmd.getColumnTypeName(i + 1));
                        out.print("\"");

                        if (rsmd.isCurrency(i + 1)) {
                            out.print(" ");
                            out.print("Currency=\"");
                            out.print("yes");
                            out.print("\"");
                        }

                        out.print(" >");
                        try {
                            // depending on the type, export the data
                            switch (rsmd.getColumnType(i + 1)) {
                                case java.sql.Types.INTEGER:
                                    out.print(rs.getInt(i + 1));
                                    break;
                                case java.sql.Types.FLOAT:
                                    out.print(rs.getFloat(i + 1));
                                    break;
                                case java.sql.Types.DOUBLE:
                                    out.print(rs.getDouble(i + 1));
                                    break;
                                case java.sql.Types.DECIMAL:
                                    out.print(rs.getFloat(i + 1));
                                    break;
                                case java.sql.Types.NUMERIC:
                                    out.print(rs.getFloat(i + 1));
                                    break;
                                case java.sql.Types.BIGINT:
                                    out.print(rs.getInt(i + 1));
                                    break;
                                case java.sql.Types.TINYINT:
                                    out.print(rs.getInt(i + 1));
                                    break;
                                case java.sql.Types.SMALLINT:
                                    out.print(rs.getInt(i + 1));
                                    break;
                                case java.sql.Types.DATE:
                                    out.print(rs.getDate(i + 1).toString());
                                    break;
                                case java.sql.Types.TIMESTAMP:
                                    out.print(rs.getTimestamp(i + 1).toString());
                                    break;
                                default:
                                    out.print(replacer(rs.getString(i + 1)));
                                    break;
                            }
                        } catch (Exception e) {
                            //logger.log(Level.WARNING, "unknown type, ignoring attribute " +  e.getMessage().toString() + " Type: " + rsmd.getColumnType(i + 1));
                            //e.printStackTrace();
                            //logger.log(Level.FINE,"Message: " + e.getMessage().toString());
                        }
                        out.print("</");
                        out.print(cleanBadCharacters(rsmd.getColumnName(i + 1)));
                        out.println(">");
                    }
                    lineCounter++;
                    out.println("</row>");
                }
                out.println("</Data>");
                out.flush();
                out.close();
                logger.log(Level.INFO, "Exported " + Integer.toString(lineCounter) + " Lines");
            } catch (SQLException sqle2) {
                logger.log(Level.WARNING, "can not loop  data");
                logger.log(Level.FINE, "Message: " + sqle2.getMessage().toString());
            }
        } else {
            logger.log(Level.WARNING, "Export not successfull");
        }
    }

    private String replacer(String v) {
        try {
            v = v.replaceAll("&", "&amp;");
            v = v.replaceAll("<","&lt;");
            v = v.replaceAll(">","&gt;");
            v = v.replaceAll("\"","&quot;");
            v = v.replaceAll("'","&apos;");
        } catch (Exception e) {
            if (v == null) {
                v = "";
            }
        }
        return v;

    }

    public String getInfo() {
        return "This is Fredy's Query-XML-Exporter.\n"
                + "XMLExport is free software (MIT-License)\n\n"
                + "Usage: 1) XMLExport xe = new XMLExport();\n"
                + "          xe.setCon(java.sql.Connection); // this is the connection to your DB\n"
                + "          xe.setFileName(java.io.File);  // this is the target export file\n"
                + "          xe.setQuery(java.lang.String); // this is your query to the DB\n"
                + "          xe.export(); // this writes your File\n\n"
                + "       2) XMLExport xe = new XMLExport(java.sql.Connection,\n"
                + "                                         java.io.File,\n"
                + "                                         java.lang.String); // does it in one step\n\n";

    }

    public String getTab() {
        return "    ";
    }

    public static void main(String args[]) {

        String host = "localhost";
        String user = System.getProperty("user.name");
        String schema = "%";
        String database = null;
        String password = null;
        String query = null;
        String file = null;

        System.out.println("XMLExport\n"
                         + "---------\n"
                + "Syntax: java sql.fredy.sqltools.XMLExport\n"
                + "        Parameters: -h Host (default: localhost)\n"
                + "                    -u User (default: "
                + System.getProperty("user.name") + ")\n"
                + "                    -p Password\n"
                + "                    -q Query\n"
                + "                    -Q Filename of the file containing the Query\n"
                + "                    -d database\n"
                + "                    -f File to write into (.xml)\n");

        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-h")) {
                i++;
                host = args[i];
            }
            if (args[i].equals("-u")) {
                i++;
                user = args[i];
            }
            if (args[i].equals("-p")) {
                i++;
                password = args[i];
            }
            if (args[i].equals("-d")) {
                i++;
                database = args[i];
            }

            if (args[i].equals("-q")) {
                i++;
                query = args[i];
            }

            if (args[i].equals("-Q")) {
                i++;
                sql.fredy.io.ReadFile rf = new sql.fredy.io.ReadFile(args[i]);
                query = rf.getText();
            }
            
            
            if (args[i].equals("-f")) {
                i++;
                file = args[i];
            }
            i++;

        };

        t_connect tc = new t_connect(host, user, password, database);
        XMLExport xe = new XMLExport(tc.getCon(),new File(file),query);       
        tc.close();
    }

    
    
    
}
