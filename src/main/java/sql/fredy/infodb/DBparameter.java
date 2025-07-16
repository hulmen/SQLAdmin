/*
 * This tool is part of fredys SQLAdmin.  It is managing parameters stored in the Derby DB running
 * with SQLAdmin.
 * It allows:
 * - read  a Parameter ( getParameter  always as String )
 * - write a Parameter ( setParameter  always as String )
 * - to save, the object is serialized
 * - it is based on the DBConnection Pool to manage the connections to the internal DB
 * - it verifies, if the table APP.ADMINPARAMETER exists
 *  - if not, it creates the table
 *
 * The MIT License
 *
 * Copyright 2024 fredy.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sql.fredy.infodb;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fredy
 */
public class DBparameter {

    private Logger logger = Logger.getLogger("sql.fredy.infodb");

    public DBparameter() {
        checkTable();
    }
           
            
    
    private void checkTable() {
        DatabaseMetaData dmd = null;
        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        try {
            con = getConnection();
            dmd = con.getMetaData();
            stmt = con.createStatement();
            rs = dmd.getTables(null, "APP", "ADMINPARAMETERS", new String[]{"TABLE"});
            if (!rs.next()) {
                // The Table does not exist, we create it
                stmt.executeUpdate("""
                                   CREATE TABLE APP.ADMINPARAMETER (
                                    NAME     VARCHAR(255) , 
                                    CONTENT  VARCHAR(4096) , 
                                    TOOLUSER VARCHAR(128)
                                   )""");
            }

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while checking Table {0}", sqlex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing statement {0}", sqlex.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing resultset {0}", sqlex.getMessage());
                }
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing connection {0}", sqlex.getMessage());
                }
            }
        }
    }

    /*
    This methode tests, if a parameter is available
    
    @param  parameter the name of the parameter to be tested
    @return true if the parameter exists, false if it does not exist
     */
    private boolean testParameter(String parameter) {        
        boolean available = false;

        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("select count(*) from APP.ADMINPARAMETER  where NAME = ? and TOOLUSER = ?");
            ps.setString(1, parameter);
            ps.setString(2, getUser());
            rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) > 0) {
                    available = true;
                }
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while testing parameter {0}", sqlex.getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing statement {0}", sqlex.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing resultset {0}", sqlex.getMessage());
                }
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing connection {0}", sqlex.getMessage());
                }
            }
        }
        return available;
    }

    /*
    getting a Parameter by its name,
    if the parameter does not exist and the defaultValue is not null, the entry is created
    
    @param parameterName - the name of the parameter to be read
    @param defaultValue  - if the parameter does not exist, it returns the defaultvalue and creates the entry if the defaultvalue is not null
    @return the String value of the parameter paramName
     */
    public String getParameter(String parameterName, String defaultValue) {
        String parameterValue = defaultValue;

        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("select CONTENT from APP.ADMINPARAMETER  where NAME = ?  and TOOLUSER = ?");
            ps.setString(1, parameterName);
            ps.setString(2, getUser());
            rs = ps.executeQuery();
            if (rs.next()) {
                parameterValue = rs.getString("CONTENT");
            } else {
                if (defaultValue != null) {
                    ps = con.prepareStatement("""
                                          insert into APP.ADMINPARAMETER (
                                           NAME,
                                           CONTENT,
                                           TOOLUSER
                                          )
                                          values (?, ?, ?)""");
                    ps.setString(1, parameterName);
                    ps.setString(2, defaultValue);
                    ps.setString(3, getUser());
                    ps.executeUpdate();
                }
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while testing parameter {0}", sqlex.getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing statement {0}", sqlex.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing resultset {0}", sqlex.getMessage());
                }
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing connection {0}", sqlex.getMessage());
                }
            }
        }
        return parameterValue;
    }

    public void setParameter(String parameterName, String parameterValue) {
        logger.log(Level.INFO, "{0} {1}", new Object[]{parameterName, parameterValue});
        
        Connection con = null;        
        PreparedStatement insert = null;
        PreparedStatement update = null;

        boolean currentParameter = testParameter(parameterName);

        try {
            con = getConnection();
            if (currentParameter) {
               update = con.prepareStatement("""
                                             update APP.ADMINPARAMETER set CONTENT = ? 
                                             where  NAME = ? and TOOLUSER = ?
                                             """);
               update.setString(1,parameterValue);
               update.setString(2,parameterName);
               update.setString(3,getUser());
               update.executeUpdate();
            } else {
                insert = con.prepareStatement("""
                                          insert into APP.ADMINPARAMETER (
                                           NAME,
                                           CONTENT,
                                           TOOLUSER
                                          )
                                          values (?, ?, ?)""");
                insert.setString(1, parameterName);
                insert.setString(2, parameterValue);
                insert.setString(3, getUser());
                insert.executeUpdate();
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while testing parameter {0}", sqlex.getMessage());
        } finally {
            if (insert != null) {
                try {
                    insert.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing statement {0}", sqlex.getMessage());
                }
            }
            if (update != null) {
                try {
                    update.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing statement {0}", sqlex.getMessage());
               }
            }
            
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing connection {0}", sqlex.getMessage());
                }
            }
        }

    }
 

    private Connection getConnection() {
        Connection connection = null;   
        try {
            connection = DBconnectionPool.getInstance().getConnection();

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while while getting connection {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
        }
        return connection;
    }

    private String getUser() {
        try {
            return System.getProperty("user.name");
        } catch (Exception e) {
            return "unknown";
        }
    }
}
