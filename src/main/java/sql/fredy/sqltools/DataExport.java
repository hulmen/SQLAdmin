/*
  DataExport.java
 
  Created on August 9, 2003, 2:46 PM
 
  DataExport exports result of a SQL-query into csv
 
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
 */
package sql.fredy.sqltools;

import java.sql.*;
import java.io.*;
import java.util.logging.*;

public class DataExport {

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

    private String wordSeparator = ";";
    private String wordSeparatorReplacer = "@,@";

    private String stringSeparator = "\"";

    /**
     * Creates a new instance of DataExport
     */
    public DataExport() {
    }

    public DataExport(java.sql.Connection con, java.io.File file, java.lang.String query) {

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
        logger.log(Level.INFO, "Query is : " + v);
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

    /**
     *
     */
    public void export() {

        if ((getCon() != null)
                && (getStmt() != null)
                && (getQuery() != null)
                && (getFileName() != null)) {

            // Charset Mess...
            System.setProperty("file.encoding", "utf-8");

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
            try {

                // we print the head
                for (int ih = 0; ih < rsmd.getColumnCount(); ih++) {
                    if (ih > 0) {
                        out.print(getWordSeparator());
                    }
                    out.print(getStringSeparator());
                    out.print(rsmd.getColumnLabel(ih + 1));
                    out.print(getStringSeparator());
                }

                out.println();
                String value ;
                
                while (rs.next()) {
                    // we have now to loop every single row to get its attributes
                    for (int i = 0; i < rsmd.getColumnCount(); i++) {
                        if (i > 0) {
                            out.print(getWordSeparator());
                        }
                        try {

                            value = rs.getString(i + 1);
                            try {
                                if (value.equals(null)) {
                                    value = "";
                                }
                            } catch (Exception e) {
                                value = "";
                            }

                            // sometimes, the wordseparator is part of a text and needs to be replaced as well, otherwise the columns are bad....
                            if (value.indexOf(getWordSeparator()) > 0) {
                                
                                //logger.log(Level.INFO, rsmd.getColumnLabel(i + 1) + " " + value   + "before");
                                //logger.log(Level.INFO,"contains separator: " + getWordSeparator() + " " + getWordSeparatorReplacer());
                                value = value.replace(getWordSeparator(), getWordSeparatorReplacer());
                                //logger.log(Level.INFO, value + " after");
                            }

                            // depending on the type, export the data
                            switch (rsmd.getColumnType(i + 1)) {
                                case java.sql.Types.INTEGER:
                                    //out.print(rs.getInt(i + 1));
                                    out.print(value);
                                    break;
                                case java.sql.Types.FLOAT:
                                    //out.print(rs.getFloat(i + 1));
                                    out.print(value);
                                    break;
                                case java.sql.Types.DOUBLE:
                                    //out.print(rs.getDouble(i + 1));
                                    out.print(value);
                                    break;
                                case java.sql.Types.DECIMAL:
                                    //out.print(rs.getFloat(i + 1));
                                    out.print(value);
                                    break;
                                case java.sql.Types.NUMERIC:
                                    //out.print(rs.getFloat(i + 1));
                                    out.print(value);
                                    break;
                                case java.sql.Types.BIGINT:
                                    //out.print(rs.getInt(i + 1));
                                    out.print(value);
                                    break;
                                case java.sql.Types.TINYINT:
                                    //out.print(rs.getInt(i + 1));
                                    out.print(value);
                                    break;
                                case java.sql.Types.SMALLINT:
                                    //out.print(rs.getInt(i + 1));
                                    out.print(value);
                                    break;
                                case java.sql.Types.DATE:
                                    //out.print(rs.getDate(i + 1).toString());
                                    out.print(value);
                                    break;
                                default:
                                    out.print(getStringSeparator());
                                    //out.print(rs.getString(i + 1));
                                    out.print(value);
                                    out.print(getStringSeparator());
                                    break;
                            }
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "unknown type, ignoring attribute");
                            e.printStackTrace();
                            //logger.log(Level.FINE,"Message: " + e.getMessage().toString());
                        }
                    }
                    lineCounter++;
                    out.println();
                }
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

    public void setStringSeparator(java.lang.String v) {
        this.stringSeparator = v;
    }

    public String getStringSeparator() {
        return this.stringSeparator;
    }

    public void setWordSeparator(java.lang.String v) {
        this.wordSeparator = v;
    }

    public String getWordSeparator() {
        return this.wordSeparator;
    }

    public String getInfo() {
        return "This is Fredy's Query-CSV-Exporter.\n"
                + "DataExport is free software (MIT-License)\n\n"
                + "Usage: 1) DataExport de = new DataExport();\n"
                + "          de.setCon(java.sql.Connection); // this is the connection to your DB\n"
                + "          de.setFileName(java.io.File);  // this is the target export file\n"
                + "          de.setQuery(java.lang.String); // this is your query to the DB\n"
                + "          de.export(); // this writes your File\n\n"
                + "       2) DataExport de = new DataExport(java.sql.Connection,\n"
                + "                                         java.io.File,\n"
                + "                                         java.lang.String); // does it in one step\n\n"
                + "Other values: setStringSeparator(java.lang.String) this sets the String, \n"
                + "                                                   a Text-Field is enclosed by (default \") \n\n"
                + "              setWordSeparator(java.lang.String)   this is the String, \n"
                + "                                                   every Field is terminated by (default ;)";
    }

    /**
     * @return the wordSeparatorReplacer
     */
    public String getWordSeparatorReplacer() {
        return wordSeparatorReplacer;
    }

    /**
     * @param wordSeparatorReplacer the wordSeparatorReplacer to set
     */
    public void setWordSeparatorReplacer(String wordSeparatorReplacer) {
        this.wordSeparatorReplacer = wordSeparatorReplacer;
    }

}
