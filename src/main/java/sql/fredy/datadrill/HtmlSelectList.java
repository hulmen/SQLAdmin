/*
 * HtmlSelectList.java
 *
 * Created on August 23, 2006, 5:14 PM
 *
 * HtmlSelectList is to:
 *
 *  Creates the CodeFragements to get a SelectList out of a SQL-Query
 *  This is used within the WebVersion of datadrill.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 * and exports data into various formats
 *
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


 */

package sql.fredy.datadrill;

import java.sql.*;
import java.util.logging.*;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import javax.naming.Context;
/**
 *
 * @author sql@hulmen.ch
 */
public class HtmlSelectList {
    
    /** Creates a new instance of HtmlSelectList */
    public HtmlSelectList() {
    }
    
    private Logger logger = Logger.getLogger("sql.fredy.datadrill");
    
    private String tabName = null;
    
    private String sqlQuery = null;
    
    private int size = 10;
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    
    
    
    
    private java.sql.Connection connection = null;
    
    public static void main(String args[]) {
        System.out.println("Fredy's HTML-SelectList");
        System.out.println("---------------------");
        System.out.println("Version 0.1");
        
        boolean help    = false;
        String host     = "localhost";
        String user     = System.getProperty("user.name");
        String password = "cher0kee";
        String schema   = "%";
        String query    = "select distinct brand.brand from brand";
        String db       = "car";
        String tabName  = "Test";
        
        int i = 0;
        
        while ( i < args.length) {
            if ((args[i].equals("-help") ) || (args[i].equals("--h")) ) {
                help=true;
            }
            
            if ((args[i].equals("-h") ) || (args[i].equals("-host")) ) {
                i++;
                host=args[i];
            }
            
            if ((args[i].equals("-u") ) || (args[i].equals("-user")) ) {
                i++;
                user=args[i];
            }
            
            
            if ((args[i].equals("-p") ) || (args[i].equals("-password")) ) {
                i++;
                password=args[i];
            }
            
            if ((args[i].equals("-q") ) || (args[i].equals("-query")) ) {
                i++;
                query=args[i];
            }
            
            if ((args[i].equals("-d") ) || (args[i].equals("-db")) ) {
                i++;
                query=args[i];
            }
            
            if ((args[i].equals("-n") ) || (args[i].equals("-tabname")) ) {
                i++;
                tabName=args[i];
            }
            
            i++;
        }
        
        if ( help ) {
            System.out.println("\nFredy's HTML-Selectlist\n" +
                    "----------------------\n" +
                    "Generates a cool HTMl-Selectlist out of a query\n" +
                    "Syntax: HtmlPickList -help this screen\n" +
                    "                     -h    host\n" +
                    "                     -u    user\n" +
                    "                     -p    password\n" +
                    "                     -d    database\n" +
                    "                     -q    query\n");
            System.exit(0);
        }
        
        
        
        
        
        HtmlSelectList hsl = new HtmlSelectList();
        hsl.setTabName(tabName);              
             
        hsl.setSqlQuery(query);
        System.out.println("HTML-Part");
        System.out.println("----- cut between here -----");
        System.out.println(hsl.getHtml());
        System.out.println("----- cut and here     -----");
                
        System.exit(0);
    }
    
    
    public Connection getConnection() {
        return connection;
    }
    
    public String getTabName() {
        return tabName;
    }
    
    public String getSqlQuery() {
        return sqlQuery;
    }
    
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
    
    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }
    
    public void setTabName(String tabName) {
        this.tabName = tabName;
    }
    
    
    public void setDataSource(String ds) {
        try {
            InitialContext initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            DataSource datasource = (javax.sql.DataSource) envCtx.lookup(ds);
            setConnection(datasource.getConnection());            
        }  catch (javax.naming.NamingException nme) {
            logger.log(Level.WARNING,"can not get datasource " + ds + " -> " + nme.getMessage());
            logger.log(Level.FINE,"Namingexception: " + nme.getMessage());
        }  catch (SQLException sqle) {
            logger.log(Level.WARNING,"can not connect to datasource " + ds);
            logger.log(Level.FINE,"SQLException: " + sqle.getMessage());
        }
        
    }
    
    public HtmlSelectList(String tabName, String sqlQuery, java.sql.Connection connection) {
        setTabName(tabName);
        setSqlQuery(sqlQuery);
        setConnection(connection);
    }
    
    public HtmlSelectList(String tabName, String sqlQuery, String dataSource) {
        setTabName(tabName);
        setSqlQuery(sqlQuery);
        setDataSource(dataSource);        
    }
    
    
    private String firstUpper(String s) {
        // I do not want to have spaces within the name
        s = s.replace(' ','_');
        s = s.substring(0,1).toUpperCase() + s.substring(1);
        return s;
    }
    
    public String getHtml() {
        StringBuffer html = new StringBuffer();
        html.append("  <div id=\"" + firstUpper(getTabName().toLowerCase()) + "Pane\">\n");
        html.append("    <table>\n");
        html.append("    <tr>\n");
        html.append("    <td>\n");
        html.append("        <select NAME=\"" + firstUpper(getTabName().toLowerCase()) + "SelectList\" ID=\""  + firstUpper(getTabName().toLowerCase()) + "SelectList\" SIZE =\"" + Integer.toString(getSize()) + "\" multiple=\"multiple\" style=\"width: 150px\">\n");
        
        html.append(addOptions());
        
        html.append("        </select>\n");
        html.append("   </td>\n"); 
        html.append("   </tr>\n");
        html.append("   <tr><TD>&nbsp;&nbsp;&nbsp;</TD></tr>\n");
        html.append("   </table>\n");
        html.append(" </div>\n");
        
        
        return html.toString();
    }
    
    /*
     *   This methode adds every single option to the select-fragment,
     *   needed by the HTMLPart of this story
     *   It will need an empty thing, if something goes wrong
     *   and also log the message within the logging, so keep an eye on your logfiles
     **/
    private String addOptions() {
        StringBuffer options = new StringBuffer();
        
        try {
            Statement stmt             = getConnection().createStatement();
            ResultSet rs               = stmt.executeQuery(getSqlQuery());
            ResultSetMetaData metaData = rs.getMetaData();
            
            /*
             *  need to know, how many columns are returned
             *
             *  if only one column is given back, I will put the same value into
             *  the VALUE-Section of the command as the displayed value
             *
             *  if more then one column is returned, I will display the first value
             *  and return the second value
             **/
            int columnCount = metaData.getColumnCount();
            
            while (rs.next()) {
                options.append("              <option VALUE=\"");
                
                if (columnCount > 1) {
                    options.append(rs.getString(2));
                } else {
                    options.append(rs.getString(1));
                }
                
                options.append("\">" + rs.getString(1) + "</option>\n");
                
            }
            stmt.close();
        } catch (SQLException e)  {
            logger.log(Level.WARNING,"SQL-Exception: " + e.getMessage());
        }
        
        
        return options.toString();
    }
    
}
