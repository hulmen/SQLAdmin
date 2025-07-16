/*
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

/*
 This class creates a HTML-combobox to be used within an HTML-form. 
 Just send a SQL-query returning two fields from the DB. Whereby the first field
 will be displayed within your HTML-page and the second value is the field beeing 
 returned from the form.
 */
package applications.html;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sql@hulmen.ch
 */
public class HTMLTwinCombo {

    private String query = null;
    private java.sql.Connection connection;
    private Statement stmt;
    private Logger logger;

    public  HTMLTwinCombo(java.sql.Connection c) {
        logger = Logger.getLogger("applications.html");
        setConnection(c);
    }

    /*
     @return this returns the HTML-Code for the query you have provided by setQuery(v)
     @param  name is the name of the HTML-Form-component        
     @param  size is the number of lines displayed before scrolling
     */
    public String getComboBox(String name, int size) {
        StringBuilder combo = new StringBuilder();
        combo.append("<select name=\"");
        combo.append(name).append("\" size=\"").append(Integer.toString(size)).append("\">\r\n");

        if (query != null) {
            try {
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(getQuery());
                while (rs.next()) {
                    combo.append("\t<option value=\"").append(rs.getString(2)).append("\">");
                    combo.append(rs.getString(1));
                    combo.append("</option>\r\n");
                }
            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "Exception while reading from DB: {0}", sqlex.getMessage());
            }

        }
        combo.append("</select>\r\n");
        return combo.toString();
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return the stmt
     */
    public Statement getStmt() {
        return stmt;
    }

    /**
     * @param stmt the stmt to set
     */
    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    /**
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}


