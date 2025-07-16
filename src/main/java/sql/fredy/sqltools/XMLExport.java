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

import java.beans.PropertyVetoException;
import java.sql.*;
import java.io.*;
import java.util.ArrayList;
import java.util.logging.*;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import sql.fredy.connection.DataSource;

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
     * this is the SQL-query to be executed
     */
    private String query = null;

    private File fileName = null;

    private PrintWriter out = null;

    private String encoding = "ISO-8859-1";

    private Logger logger = Logger.getLogger("sql.fredy.sqltools");

    public XMLExport(java.io.File file, java.lang.String query) {

        this.setFileName(file);
        this.setQuery(query);
        export();
    }

    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    private Connection con = null;

    public Connection getCon() {

        try {
            if ((null == con) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {

        }
        return con;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(java.lang.String v) {
        //logger.log(Level.INFO, "Query is : " + v);
        this.query = v;
    }

    public void setFileName(java.io.File v) {
        logger.log(Level.INFO, "Setting Filename to : " + v.getName());
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

    private String cleanBadCharacters(String v) {
        v = v.replaceAll(" ", "_");
        v = v.replaceAll("\t", "_");
        v = v.replaceAll("\r", "_");
        v = v.replaceAll("\n", "_");
        return v;
    }

    private void execsubQuery(String q) {
        Connection con = getCon();
        Statement stmt = null;
        try {
            stmt = con.createStatement();
            if (q.toLowerCase().startsWith("select")
                    | q.toLowerCase().startsWith("show")
                    | q.toLowerCase().startsWith("desc")
                    | q.toLowerCase().startsWith("declare")
                    | q.toLowerCase().startsWith("values")
                    | q.toLowerCase().startsWith("with")) {
                ResultSet rs = stmt.executeQuery(q);
            } else {
                stmt.executeUpdate(q);
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQLExcetpion {0}", sqlex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "SQLExcpetion on close Statement [0]", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "SQLExcpetion on close Connection [0]", e.getMessage());
                }
            }
        }
    }

    /**
     *
     */
    public void export() {

        if ((getQuery() != null) && (getFileName() != null)) {

            ResultSet rs = null;
            ResultSetMetaData rsmd = null;
            Statement stmt = null;
            Connection con = null;
            int lineCounter = 0;
            // execute the query and fetch metadata of the result
            try {

                /*
                if there are several statements, divided by ; we execute them ,
                we expect, the last part contains the result to display.
                
                To have a clean querylist, we remove all the empty lines
                 */
                String[] subQuery = getQuery().split(";");
                ArrayList<String> queries = new ArrayList();

                for (int i = 0; i < subQuery.length; i++) {
                    if (subQuery[i].length() > 5) {
                        queries.add(subQuery[i]);
                    }
                }

                // now we iterate the cleaned up queries to execute subqueries, if any
                for (int i = 0; i < queries.size() - 1; i++) {
                    execsubQuery((String) queries.get(i));
                }

                con = getCon();
                if (con == null) {
                    logger.log(Level.SEVERE, "Attention no connection established");
                }
                stmt = con.createStatement();
                rs = stmt.executeQuery(queries.get(queries.size() - 1));
                rsmd = rs.getMetaData();
            } catch (SQLException sqle1) {
                logger.log(Level.WARNING, "can not execute query");
                logger.log(Level.FINE, "Message: {0}", sqle1.getMessage());
            }

            // we create the xml-document
            Document document = new Document();
            Element  root = new Element("Data");
            root.addContent(new Element("DataExport").addContent("Generated by Fredy's XMLExporter part of SqlAdmin"));
            root.addContent(new Element("Query").addContent(getQuery()));            
            
            try {
                while (rs.next()) {
                    
                    Element row = new Element("row");


                    // we have now to loop every single row to get its attributes
                    for (int i = 0; i < rsmd.getColumnCount(); i++) {
                        
                        Element column = new Element(rsmd.getColumnName(i+1).replaceAll(" ","_"));

                        column.setAttribute(new Attribute("Table", rsmd.getTableName(i+1).isBlank() ? "undectetable" : rsmd.getTableName(i+1) ));
                        column.setAttribute(new Attribute("Type",  rsmd.getColumnTypeName(i + 1)));
                        
                                    
                        String value;
                        try {
                            // depending on the type, export the data
                            switch (rsmd.getColumnType(i + 1)) {
                                case java.sql.Types.INTEGER:
                                    value = Integer.toString(rs.getInt(i + 1));
                                    break;
                                case java.sql.Types.FLOAT:
                                    value = Float.toString(rs.getFloat(i + 1));
                                    break;
                                case java.sql.Types.DOUBLE:
                                    value = Double.toString(rs.getDouble(i + 1));
                                    break;
                                case java.sql.Types.DECIMAL:
                                    value = Float.toString(rs.getFloat(i + 1));
                                    break;
                                case java.sql.Types.NUMERIC:
                                    value = Float.toString(rs.getFloat(i + 1));
                                    break;
                                case java.sql.Types.BIGINT:
                                    value = Integer.toString(rs.getInt(i + 1));
                                    break;
                                case java.sql.Types.TINYINT:
                                    value = Integer.toString(rs.getInt(i + 1));
                                    break;
                                case java.sql.Types.SMALLINT:
                                    value = Integer.toString(rs.getInt(i + 1));
                                    break;
                                case java.sql.Types.DATE:
                                    value = rs.getDate(i + 1).toString();
                                    break;
                                case java.sql.Types.TIMESTAMP:
                                    value = rs.getTimestamp(i + 1).toString();
                                    break;
                                default:
                                    value = rs.getString(i + 1);
                                    break;
                            }
                            column.addContent(value);
                        } catch (Exception e) {
                            //logger.log(Level.WARNING, "unknown type, ignoring attribute " +  e.getMessage().toString() + " Type: " + rsmd.getColumnType(i + 1));
                            //e.printStackTrace();
                            //logger.log(Level.FINE,"Message: " + e.getMessage().toString());
                        }
                       row.addContent(column);
                    }
                    lineCounter++;
                    root.addContent(row);
                }
                document.setRootElement(root);
                XMLOutputter writer = new XMLOutputter();
                writer.setFormat(Format.getPrettyFormat());
                writer.output(document, new FileWriter(getFileName()));
                
                logger.log(Level.INFO, "Exported {0} Lines", Integer.toString(lineCounter));
                
            } catch (SQLException sqle2) {
                logger.log(Level.WARNING, "can not loop  data");
                logger.log(Level.FINE, "Message: {0}", sqle2.getMessage());
            } catch (Exception e) {
                logger.log(Level.WARNING, " Exception while collecting XML-Data: {0}", e.getMessage());
            } finally {
                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING, "SQLException on close Statement {0}", e.getMessage());
                    }
                }
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING, "SQLException on close ResultSet {0}", e.getMessage());
                    }
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING, "SQLException on close Connection {0}", e.getMessage());
                    }
                }
            }
        } else {
            logger.log(Level.WARNING, "Export not successfull");
        }
    }

    private String replacer(String v) {
        try {
            v = v.replaceAll("&", "&amp;");
            v = v.replaceAll("<", "&lt;");
            v = v.replaceAll(">", "&gt;");
            v = v.replaceAll("\"", "&quot;");
            v = v.replaceAll("'", "&apos;");
            v = v.replaceAll(" ","_");
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
                + "          xe.setFileName(java.io.File);  // this is the target export file\n"
                + "          xe.setQuery(java.lang.String); // this is your query to the DB\n"
                + "          xe.export(); // this writes your File\n\n"
                + "       2) XMLExport xe = new XMLExport(java.io.File,\n"
                + "                                       java.lang.String); // does it in one step\n\n";

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
                + "        Parameters: -q Query\n"
                + "                    -Q Filename of the file containing the Query\n"
                + "                    -f File to write into (.xml)\n");

        int i = 0;
        while (i < args.length) {
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

        XMLExport xe = new XMLExport(new File(file), query);
    }

    /**
     * @return the encoding
     */
    public String getEncoding() {
        return encoding;
    }

    /**
     * @param encoding the encoding to set
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

}
