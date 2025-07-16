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
 you need to make sure, a Derby-DB instance is running on the server to connect to.
 So please make sure, you launch sql.fredy.tools.DerbyInfoServer in advance and
 make sure, these tables exists:
 
 create table CodeCompletion (
       ToolUser varchar(128),
       TemplateName VARCHAR(25) NOT NULL ,
       Abbreviation VARCHAR(25) ,
       CodeTemplate VARCHAR(4096) ,
       Description VARCHAR(4096)  ,
       PRIMARY KEY (ToolUser,TemplateName)
)       
create unique index ccIdx on CodeCompletion (Abbreviation,ToolUser)

create table AdminParameter ( ToolUser varchar(128),Name VARCHAR(255), Content VARCHAR(4096) );
insert into AdminParameter(Name, Content) values ('user', 'alive','yes')



 */
package sql.fredy.tools;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sql.fredy.infodb.DBconnectionPool;

/**
 *
 * @author Fredy Fischer
 */
public class PropertiesInDerby {
 
    private Logger logger = Logger.getLogger("sql.fredy.tools");

    public static void main(String a[]) {
        PropertiesInDerby pid = new PropertiesInDerby();
        pid.close();
        System.out.println("if no errormessage apeared, everything is fine. Use Derbytools to manage this DB");
    }

    public PropertiesInDerby() {        
    }

    private String getUser() {
        try {
            return System.getProperty("user.name");
        } catch (Exception e) {
            return "unknown";
        }
    }

    public String readParameter(String name) {
        String value = null;
        Connection connection = null;
        PreparedStatement read = null;
        ResultSet rs = null;

        try {
            connection = DBconnectionPool.getInstance().getConnection();
            read = connection.prepareStatement("select Content from APP.ADMINPARAMETER where Name = ? and ToolUser = ?");
            read.setString(1, name);
            read.setString(2, getUser());
            rs = read.executeQuery();
            while (rs.next()) {
                value = rs.getString(1);
            }

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while reading a Parameter {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
            if (read != null) 
              try {
                read.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (rs != null) 
              try {
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
        }

        logger.log(Level.FINE, "found parameter {0} having value {1}", new Object[]{name, value});
        return value;
    }

    public void saveParameter(String name, String value) {

        // first we test, wheter this pair already exists
        String v = readParameter(name);

        Connection connection = null;
        PreparedStatement work = null;

        try {
            connection = DBconnectionPool.getInstance().getConnection();

            if (v == null) {

                work = connection.prepareStatement("insert into APP.ADMINPARAMETER ( ToolUser ,Name, Content) values (?, ?,?)");
                work.setString(1, getUser());
                work.setString(2, name);
                work.setString(3, value);
            } else {
                work = connection.prepareStatement("update APP.ADMINPARAMETER set  Name = ?, Content = ? where Name = ? and ToolUser = ?");
                work.setString(1, name);
                work.setString(2, value);
                work.setString(3, name);
                work.setString(4, getUser());
            }
            work.executeUpdate();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while saving a Parameter {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQLState: {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
            if (work != null) 
              try {
                work.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
        }
        
        logger.log(Level.FINE, "Saved parameter {0} with value {1}", new Object[]{name, value});
        
    }

    public void deleteParamter(String name) {

        Connection connection = null;
        PreparedStatement delete = null;

        try {

            connection = DBconnectionPool.getInstance().getConnection();
            delete = connection.prepareStatement("delete from APP.ADMINPARAMETER where Name = ? and ToolUser = ?");

            delete.setString(1, name);
            delete.setString(2, getUser());
            delete.executeUpdate();
       
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while saving a Parameter {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQLState: {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
            if (delete != null) 
              try {
                delete.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
        }

        logger.log(Level.INFO, "deleted parameter {0}", name);
    }

    public void close() {

       
    }

    private DerbyInfoServer derby;

   

}
