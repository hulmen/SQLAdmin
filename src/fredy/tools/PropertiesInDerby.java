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
       TemplateName VARCHAR(25) NOT NULL ,
       Abbreviation VARCHAR(25) ,
       CodeTemplate VARCHAR(4096) ,
       Description VARCHAR(4096)  ,
       PRIMARY KEY (TemplateName)
)       
create unique index ccIdx on CodeCompletion (Abbreviation)

create table AdminParameter ( Name VARCHAR(255), Content VARCHAR(4096) );
insert into AdminParameter(Name, Content) values ('alive','yes')



*/

package sql.fredy.tools;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Fredy Fischer
 */
public class PropertiesInDerby {

    private String dbName;
    Connection con;
    PreparedStatement insert, update, read, delete;
    String connectionURL;
    String driver;
    int loopCounter = 0;

    private Logger logger = Logger.getLogger("sql.fredy.tools");

    public static void main(String a[]) {
        PropertiesInDerby pid = new PropertiesInDerby();
        pid.close();
        System.out.println("if no errormessage apeared, everything is fine. Use Derbytools to manage this DB");
    }

    public PropertiesInDerby() {
        initDB();
    }

    public String readParameter(String name) {
        String value = null;
        try {
            read.setString(1, name);
            ResultSet rs = read.executeQuery();
            while (rs.next()) {
                value = rs.getString(1);
            }

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while reading a Parameter {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }  catch (Exception ex) {
              logger.log(Level.WARNING, "Exception while reading Parameter {0} Exception is: {1}", new Object[]{name, ex.getMessage()}); 
        }

        return value;
    }

    public void saveParameter(String name, String value) {

        // first we test, wheter this pair already exists
        String v = readParameter(name);
        PreparedStatement work = update;

        try {
            if (v == null) {
                work = insert;
                work.setString(1, name);
                work.setString(2, value);
            } else {
                work.setString(1, name);
                work.setString(2, value);
                work.setString(3, name);
            }
            work.executeUpdate();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while saving a Parameter {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQLState: {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }
    }

    public void deleteParamter(String name) {
        try {
            delete.setString(1, name);
            delete.executeUpdate();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while deleting a Parameter {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQLState: {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }

    }

    public void close() {               
                
        try {
            con.close();  
            
            // we do not need to shut down the derby db, because we are using a DB server startet in advance
            
            //DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
            //DriverManager.getConnection("jdbc:derby:;shutdown=true");

        } catch (SQLException sqlex) {
            
            //   use this if you only shutdown the actual DB
            // this error will not appear...
            
            if ((sqlex.getErrorCode() == 45000) && ("08006".equals(sqlex.getSQLState()))) {

                // if ((sqlex.getErrorCode() == 50000) && ("XJ015".equals(sqlex.getSQLState()))) {
                //logger.log(Level.INFO, "DB closed");
            } else {
                logger.log(Level.WARNING, "Closing connection to SQLAdmin Work DerbyDB {0}", sqlex.getMessage());
                logger.log(Level.INFO, "SQLState: {0}", sqlex.getSQLState());
                logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
            }
        }
    }

    protected void finalize() {
        close();
    }
    

   
    private DerbyInfoServer derby;
    private void initDB() {
        
       /*
        get the derby-things without starting up the server
        */     
       derby = new DerbyInfoServer(false);

       try {
            Class.forName(derby.getJDBCDriver()).newInstance();
        } catch (ClassNotFoundException cnfe) {

        } catch (Exception e) {

        }       

        try {
            con = DriverManager.getConnection(derby.getJDBCUrl());
      
            insert = con.prepareStatement("insert into AdminParameter ( Name, Content) values (?,?)");
            update = con.prepareStatement("update AdminParameter set  Name = ?, Content = ? where Name = ?");
            read = con.prepareStatement("select Content from AdminParameter where Name = ?");
            delete = con.prepareStatement("delete from AdminParameter where Name = ?");

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while initialising the CodeCompletion DB {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
            logger.log(Level.INFO, "Mostly this is because derby is not up and running. So start the derby-server in advance by launching sql.fredy.tools.DerbyInfoServer");          
        }

    }

}
