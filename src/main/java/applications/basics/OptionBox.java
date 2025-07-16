/**
 * This OptionBox creates a HTML-Form Select-Element
 * where the element and values are read out of the DB
 *
 * the connection to the DB is done by the name of a datasource
 * therefore OptionBox has to run inside a container delivering
 * the capability to lookup up JNDI
 *
 * call the element as follows:
 * getOption(String datasourcename, String HTML-Name of the Element,
 *           String default element to select,
 *           String SQL-Query)
 * the SQL-query must return 2 attributes, while the first attribute
 * is put as the value-key of the HTML-Select-Element and the second
 * attribute is displayed in the select-box
 *
 *
 * Fredy Fischer
 * Hulmenweg 36
 * 8405 Winterthur
 * Switzerland
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


 *
 **/
package applications.basics;
import java.sql.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import applications.basics.*;
import javax.sql.DataSource;
import java.util.logging.*;

public class OptionBox {
    
    private Logger logger=Logger.getLogger("applications.basics");
    
    public OptionBox() {
        
    }
    
    // returns field 1 while displaying field 2
    public String getOption(String datasource, String name,String def, String query) {
        Connection con = null;
        Statement stmt = null;
        StringBuffer s = new StringBuffer("<select name=\"" + name +"\">\n");
        try {
            InitialContext initCtx = new InitialContext();
            DataSource ds = (javax.sql.DataSource) initCtx.lookup(datasource);
            con  = ds.getConnection();
            stmt = con.createStatement();
        }  catch (javax.naming.NamingException nme) {
            logger.log(Level.WARNING,"can not get datasource");
            logger.log(Level.FINE,"Namingexception: " + nme.getMessage());
        }  catch (SQLException sqle) {
            logger.log(Level.WARNING,"can not connect to datasource");
            logger.log(Level.FINE,"SQLException: " + sqle.getMessage());
        }
        try {
            ResultSet rs   = stmt.executeQuery(query);
            while (rs.next() ) {
                s.append("<option value=\""+ rs.getString(1) + "\"");
                if ( rs.getString(1).equals(def) ) s.append(" SELECTED");
                s.append(">" + rs.getString(2) + "\n");
            }
            stmt.close();
            con.close();
        } catch (SQLException e) {
          logger.log(Level.WARNING,"can not get SELECT-Element");
          logger.log(Level.FINE,"SQL-Exception: " + e.getMessage());
        }
        s.append("</select>\n");
        
        return s.toString();
    }
}
