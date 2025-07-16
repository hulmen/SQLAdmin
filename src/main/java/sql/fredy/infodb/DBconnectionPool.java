/*
 * This class represents a connection pool for the internal apache derby db.
   refer sql.fredy.infodb.DBserver.java

 * The MIT License
 *
 * Copyright 2022 fredy.
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

 /*
It can be used like this:

        Connection connection = null;
        DatabaseMetaData dmd = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();           
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {        
            if (connection != null) 
              try {
                connection.close();
              } catch (SQLException e) {
                e.printStackTrace();
            }
        }       

 

Make sure to close the connection right after useing it.

 */
package sql.fredy.infodb;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.dbcp2.BasicDataSource;

/**
 *
 * @author fredy
 */
public final class DBconnectionPool {

    private static Level LOGLEVEL = Level.FINE;

    /**
     * @return the datasource
     */
    public static DBconnectionPool getDatasource() {
        return datasource;
    }

    /**
     * @param aDatasource the datasource to set
     */
    public static void setDatasource(DBconnectionPool aDatasource) {
        datasource = aDatasource;
    }

    /**
     * @return the ds
     */
    public BasicDataSource getDs() {
        return ds;
    }

    /**
     * @param ds the ds to set
     */
    public void setDs(BasicDataSource ds) {
        this.ds = ds;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the jdbcurl
     */
    public String getJdbcurl() {
        return jdbcurl;
    }

    /**
     * @param jdbcurl the jdbcurl to set
     */
    public void setJdbcurl(String jdbcurl) {        
        logger.log(Level.FINE, "setting JDBC URL: {0}", jdbcurl);
        this.jdbcurl = jdbcurl;
    }

    /**
     * @return the hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname the hostname to set
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }
        

    /**
     * @return the sqladminDB
     */
    public String getSqladminDB() {

        if (sqladminDB == null) {
            String adminConfigDb = null;
            try {
                adminConfigDb = System.getenv("sql.fredy.admin.configdb");
                if (adminConfigDb != null) {
                    logger.log(LOGLEVEL, "Config DB: {0}", adminConfigDb);
                    return adminConfigDb;
                }
            } catch (Exception e) {
                logger.log(LOGLEVEL, "Can not find Environment variable for config DB, using standard");
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
            sqladminDB = directory + File.separator + "SQLAdminWorkDB";
            
        }

        return sqladminDB;
    }

    /**
     * @param sqladminDB the sqladminDB to set
     */
    public void setSqladminDB(String sqladminDB) {
        this.sqladminDB = sqladminDB;
    }
    private String jdbcurl = null;
    private String hostname = "localhost";
    private int port = 60606;
    private String sqladminDB = null;
    private String username = null;
    private String password = null;

    private Logger logger = Logger.getLogger("sql.fredy.infodb");

    private static DBconnectionPool datasource = null;
    private BasicDataSource ds = null;
    private String jdbcDriver = "org.apache.derby.jdbc.ClientDriver";

    private DBconnectionPool() throws IOException, SQLException, PropertyVetoException {

        logger.log(LOGLEVEL, "Initializing DataSource");

        ds = new BasicDataSource();
        ds.setDriverClassName(getJdbcDriver());
        if ((getUsername() == null) || (getUsername().length() == 0)) {
            // no username and no password
        } else {
            ds.setUsername(getUsername());
            ds.setPassword(getPassword());
        }
        
        // if one does not want to perform an automatic upgrade of the Derby DB set this switch to no
        boolean upgradeInternalDb = true;
        if ((System.getenv("sql.fredy.admin.internaldb.upgrade") != null) && (System.getenv("sql.fredy.admin.internaldb.upgrade").equalsIgnoreCase("n"))) {
            upgradeInternalDb = false;
        }
        if ((System.getProperty("sql.fredy.admin.internaldb.upgrade") != null) && (System.getProperty("sql.fredy.admin.internaldb.upgrade").equalsIgnoreCase("n"))) {
            upgradeInternalDb = true;
        }
        
        setJdbcurl("jdbc:derby://" + getHostname() + ":" + Integer.toString(getPort()) + "/" + getSqladminDB() + ";create=true" + (upgradeInternalDb ? ";upgrade=true" : ""));

        //logger.log(Level.INFO, "jdbc:derby://{0}:{1}/{2};create=true{3}", new Object[]{getHostname(), Integer.toString(getPort()), getSqladminDB(), upgradeInternalDb ? ";upgrade=true" : ""});
        
        ds.setUrl(getJdbcurl());

        // the settings below are optional -- dbcp can work with defaults
        ds.setMinIdle(5);
        ds.setInitialSize(5);
        ds.setMaxIdle(20);
        ds.setMaxOpenPreparedStatements(180);
        //ds.setJmxName("internaldb");

        if (ds != null) {
            setDmd();
            setProductName();
            setProductVersion();
        }

    }

    public Connection getConnection() throws SQLException {
        try {
            //logger.log(Level.INFO, "Default Catalog: {0}", this.ds.getDefaultCatalog());

            // it is a shame, if the connection can not be established and to get over that, datasource must be set to null
            Connection con = this.ds.getConnection();
            if ( null == con) {
                datasource = null;
                //ds = null;
                logger.log(Level.INFO, "Connection not established to: {0}", this.getSqladminDB());
                return null;
            }
            return con; 
        } catch (SQLException e) {
            datasource = null;
            throw e;
        } catch (UnsupportedClassVersionError | Exception e) {
            logger.log(Level.WARNING, "Exception while getting connection {0}", e.getMessage());
            datasource = null;
            return null;
        }
    }

    private DatabaseMetaData dmd = null;

    public DatabaseMetaData getDmd() {
        if (dmd == null) {
            setDmd();
        }
        return dmd;
    }

    public void setDmd() {
        try {
            if (ds.getConnection() != null) {
                dmd = ds.getConnection().getMetaData();
            } else {
                //ds = null;
                datasource = null;
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "(SQL) Metadata not readable: {0} \nfor URL: {1}", new Object[]{ex.getMessage(), getJdbcurl()});            
            //ds = null;  Fredy 2024-08-23
            datasource = null;
            logger.log(Level.SEVERE, "SQL Error: {0}", ex.getSQLState());
            //ex.printStackTrace();
        } catch (Exception ex) {
            logger.log(Level.WARNING, "(System) Metadata not readable: {0}", ex.getMessage());
            ds = null;
            datasource = null;            
        }
    }

    private String productName, productVersion;

    /**
     * Get the value of productName.
     *
     * @return Value of productName.
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Set the value of productName.
     *
     * @param v Value to assign to productName.
     */
    public void setProductName() {

        productName = "";
        try {
            if (getDmd() != null) {
                productName = getDmd().getDatabaseProductName();
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User {0} throws Exception while reading ProductName from MetaData  |JDBC-Driver: {1} |Database: {2} |Error-Code: {3} |SQL-State: {4} |Exception: {5}", new Object[]{getUsername(), getJdbcDriver(), getSqladminDB(), sqe.getErrorCode(), sqe.getSQLState(), sqe.getMessage()});
        } catch (Exception sqe) {
            logger.log(Level.SEVERE, "User: {0} unexpected exception: {1}", new Object[]{getUsername(), sqe.getMessage()});
        }
    }

    /**
     * Get the value of productVersion.
     *
     * @return Value of productVersion.
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * Set the value of productVersion.
     *
     * @param v Value to assign to productVersion.
     */
    private void setProductVersion() {
        productVersion = "";
        try {
            if (getDmd() != null) {
                productVersion = getDmd().getDatabaseProductVersion();
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User {0} throws Exception while reading ProductVersion from MetaData  |JDBC-Driver: {1} |Database: {2} |Error-Code: {3} |SQL-State: {4} |Exception: {5}", new Object[]{getUsername(), getJdbcDriver(), getSqladminDB(), sqe.getErrorCode(), sqe.getSQLState(), sqe.getMessage()});
        } catch (Exception sqe) {
            logger.log(Level.SEVERE, "User: {0} unexpected exception: {1}", new Object[]{getUsername(), sqe.getMessage()});
        }
    }

    public int getNumberOfOpenConnections() {
        return ds.getNumActive();
    }

    public int getNumberOfIdleConnections() {
        return ds.getNumIdle();
    }

    /**
     * @return the jdbcDriver
     */
    public String getJdbcDriver() {
        return jdbcDriver;
    }

    /**
     * @param jdbcDriver the jdbcDriver to set
     */
    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    private void info() {
        /*
        System.out.println("User: "+ getUserName() + "\n"
                         + "Password: " + getUserPassword() + "\n"
                         + "Host: " + getHostName() + "\n"
                         + "Port: " + getDbListenerPort() + "\n"
                         + "JDBC Driver: " + getJdbcDriver() + "\n"
                         + "JDBC URL: " + getUrl() );
         */
        logger.log(Level.INFO, "User: {0}\nPassword: {1}\nHost: {2}\nPort: {3}\nJDBC Driver: {4}\nJDBC URL: {5}", new Object[]{getUsername(), getPassword(), getHostname(), getPort(), getJdbcDriver(), getJdbcurl()});
    }

    private static final Object locked = new Object();
    public static DBconnectionPool getInstance() throws IOException, SQLException, PropertyVetoException {
        
        synchronized (locked) {
            if ( null == datasource) {
                Logger.getLogger("sql.fredy.connection").log(LOGLEVEL, "Creating Datasource...");
                datasource = new DBconnectionPool();                
            } else {
                Logger.getLogger("sql.fredy.connection").log(LOGLEVEL, "Datasource reused");                
            }
            return datasource;
        }
    }

    public static void main(String[] args) {
        Connection con = null;
        try {
            con = DBconnectionPool.getInstance().getConnection();
        } catch (IOException ex) {
            System.out.println("Ooops.... IO Exception: " + ex);
        } catch (SQLException ex) {
            System.out.println("Ooops.... SQL Exception: " + ex);
        } catch (PropertyVetoException ex) {
            System.out.println("Ooops.... Property Veto Exception: " + ex);
        } finally {
            try {
                try {
                    System.out.println("Connectionpool established to "
                            + DBconnectionPool.getInstance().getSqladminDB()
                            + " on "
                            + DBconnectionPool.getInstance().getHostname()
                            + " DB Product "
                            + DBconnectionPool.getInstance().getProductName());
                } catch (IOException ex) {
                    System.out.println("Ooops.... IO Exception: " + ex);
                } catch (PropertyVetoException ex) {
                    System.out.println("Ooops.... Property Veto Exception: " + ex);
                }
                con.close();
            } catch (SQLException ex) {
                System.out.println("Ooops.... SQL Exception: " + ex);
            }
        }
    }

}
