/*
 * The MIT License
 *
 * Copyright 2020 fredy.
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

This class realizes a ConnectionPool

It can be used like this:

        Connection connection = null;
        DatabaseMetaData dmd = null;
        try {
            connection = DataSource.getInstance().getConnection();
            System.out.println("Connection established to " + DataSource.getInstance().getDataBase() + " running on " +DataSource.getInstance().getHostName()  );           
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

The connection is established by either getting the values from 
1) sql.fredy.admin 
2) a Properties file
3) JVM -d parameters
4) OS Environment variables


 
By a Properties-File:
---------------------
Filename   : defaultDBpool.props
Folder     : either in 'user.home'/sqladmin/poolconfig
             or at a location defined in the JVM Environment variable 'admin.work'/sqladmin/poolconfig'

FileContent:

usePassword=yes
schema=%
dbport=1433
user=XY_PREPORTDWH01
jdbcdriver=com.microsoft.sqlserver.jdbc.SQLServerDriver
dburl=jdbc\:sqlserver\://
password=.u0rt9;64_4)^*
additionalParameters=;MultipleActiveResultSets\=True;
host=[HOSTNAME]
db=[DBNAME]
connections=5
 

The password is encrypted by a verry simple encryption (Caesar Encryption). Use the Class sql.fredy.tools.Encryption to encrypt and decrypt the password whereby the offset is 31

By Environment Variables:
------------------------
sql.fredy.connection.username
sql.fredy.connection.password
sql.fredy.connection.jdbcdriver
sql.fredy.connection.jdbcurl
sql.fredy.connection.hostname
sql.fredy.connection.dbport
sql.fredy.connection.db
sql.fredy.connection.additionalparameter

 They can be set as -d-Parameters to the JVM or as OS-Environment variables, whereby JVM-Parameters have priority

 

 */
package sql.fredy.connection;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.dbcp2.BasicDataSource;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.DatabaseMetaData;
import java.util.Properties;
import org.apache.commons.dbcp2.DelegatingConnection;
import sql.fredy.admin.Admin;
import sql.fredy.tools.Encryption;

public final class DataSource {

    private static Level LOGLEVEL = Level.FINE;

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

    /**
     * @return the dataBase
     */
    public String getDataBase() {
        return dataBase;
    }

    /**
     * @param dataBase the dataBase to set
     */
    public final void setDataBase(String dataBase) {
        this.dataBase = dataBase;
        if (dataBase == null) {
            logger.log(Level.WARNING, "Empty Database Name");
        } else {
            logger.log(LOGLEVEL, "Databasename {0}", this.dataBase);
        }

    }

    /**
     * @return the hostName
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param hostName the hostName to set
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the userPassword
     */
    public String getUserPassword() {
        return userPassword;
    }

    /**
     * @param userPassword the userPassword to set
     */
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    /**
     * @return the dbListenerPort
     */
    public int getDbListenerPort() {
        return dbListenerPort;
    }

    /**
     * @param dbListenerPort the dbListenerPort to set
     */
    public void setDbListenerPort(int dbListenerPort) {
        this.dbListenerPort = dbListenerPort;
    }

    private String connectionError;

    public String getConnectionError() {
        return connectionError;
    }

    public void setConnectionError(String v) {
        connectionError = v;
    }

    private static volatile DataSource datasource;  // 2022-10-27
    private BasicDataSource ds;

    private String jdbcDriver = null;
    private String dataBase = null;
    private String hostName = null;
    private String userName = null;
    private String userPassword = null;
    private String additionalParameters = null;
    private String url = null;
    private int dbListenerPort = 0;

    private Logger logger = Logger.getLogger("sql.fredy.connection");

    private DataSource() throws IOException, SQLException, PropertyVetoException {

        logger.log(Level.FINEST, "Initializing DataSource");

        // 1st we check if we are running inside SQL Admin
        if (!readFromAdmin()) {

            // Running Stand Alone
            logger.log(LOGLEVEL, "Running standalone");
            /*
              Do we have JVM environment variables;
             */
            if (!readFromJVMenv()) {

                // then OS
                if (!readFromOSenv()) {

                    // and finally from Configuration file
                    if (!readConfigFile()) {
                        logger.log(Level.SEVERE, "No connection information founnd, connection can not be established");
                    }
                }
            }
        }

        //System.out.println(getClass().getName() + ".init() " + getJdbcDriver() + " " + getUserName());
        ds = new BasicDataSource();
        ds.setDriverClassName(getJdbcDriver());
        if ((getUserName() == null) || (getUserName().length() == 0)) {
            // no username and no password
        } else {
            ds.setUsername(getUserName());
            ds.setPassword(getUserPassword());
        }

        // The URL mangler Depending on different Databases
        // we use networked connection
        if (getDbListenerPort() > 0) {

            boolean defaultDb = true;

            // Oracle
            if (getJdbcDriver().toLowerCase().startsWith("oracle")) {
                setUrl(url + "@" + getHostName()
                        + ":" + Integer.toString(getDbListenerPort())
                        + ":" + getDataBase());
                defaultDb = false;
            }

            // Empress
            if (getJdbcDriver().toLowerCase().startsWith("empress")) {
                setUrl(url + "/SERVER=" + getHostName()
                        + ";PORT="
                        + Integer.toString(getDbListenerPort())
                        + ";DATABASE=" + getDataBase());
                defaultDb = false;
            }

            // Microsft SQL Server
            if (getJdbcDriver().toLowerCase().indexOf("microsoft") > 0) {
                setUrl(url + getHostName() + ":"
                        + Integer.toString(getDbListenerPort()) + ";databaseName=" + getDataBase());
                defaultDb = false;
            }

            if (getJdbcDriver().toLowerCase().indexOf("postgresql") > 0) {
                setUrl(url + getHostName() + ":"
                        + Integer.toString(getDbListenerPort()) + "/" + getDataBase());
                defaultDb = false;
            }

            // all other DBs
            if (defaultDb) {
                setUrl(url + getHostName() + ":" + Integer.toString(getDbListenerPort()) + "/" + getDataBase());
            }
        } else {
            // non networked DB
            setUrl(url + getDataBase());
        }

        setUrl(url + ((getAdditionalParameters() != null) ? getAdditionalParameters() : ""));
        logger.log(LOGLEVEL, "JDBC URL: {0}", getUrl());

        ds.setUrl(getUrl());

        // the settings below are optional -- dbcp can work with defaults
        /*
        ds.setMinIdle(0);
        ds.setInitialSize(0);
        ds.setMaxIdle(8);        
        ds.setJmxName("org.apache.dbcp:DataSource=" + getDataBase());
         */
        if (ds != null) {
            setDmd();
            setProductName();
            setProductVersion();

        }
        logger.log(Level.INFO, "DataSource initialized, URL: {0}", getUrl());
    }

    /*
       the value every connection needs is the URL, if this is not empty, we take it
     */
    private boolean readFromAdmin() {
        boolean fromAdmin = false;
        try {
            setDataBase((Admin.getLogOn().getDatabase()));
            setHostName(Admin.getLogOn().getHost());
            setUserName(Admin.getLogOn().getUser());
            //setUserPassword(String.valueOf(Admin.getLogOn().lPassword.getPassword()));
            setUserPassword(Admin.getLogOn().getPassword());
            setDbListenerPort(Integer.parseInt(Admin.getLogOn().getPort()));
            setJdbcDriver(Admin.getLogOn().getDriver());
            setAdditionalParameters(Admin.getLogOn().getAdditionalParameters());
            setUrl(Admin.getLogOn().getUrl());
            if ((getUrl() != null) && (getUrl().length() > 1)) {
                fromAdmin = true;
                logger.log(LOGLEVEL, "Connection parameters from SqlAdmin used");
            }
            // info();
        } catch (Exception e) {
            fromAdmin = false;
        }

        if (!fromAdmin) {
            logger.log(LOGLEVEL, "SqlAdmin not available, going further..");
        }
        return fromAdmin;
    }

    private boolean readFromJVMenv() {
        boolean jvmEnv = false;
        try {
            setUserName(System.getProperty("sql.fredy.admin.connection.username"));
            setUserPassword(System.getProperty("sql.fredy.admin.connection.password"));
            setJdbcDriver(System.getProperty("sql.fredy.admin.connection.jdbcdriver"));
            setUrl(System.getProperty("sql.fredy.admin.connection.jdbcurl"));
            setHostName(System.getProperty("sql.fredy.admin.connection.hostname"));
            setDbListenerPort(Integer.parseInt(System.getProperty("sql.fredy.admin.connection.dbport")));
            setDataBase(System.getProperty("sql.fredy.admin.connection.db"));
            setAdditionalParameters(System.getProperty("sql.fredy.admin.connection.additionalparameter"));
            if ((getUrl() != null) && (getUrl().length() > 1)) {
                jvmEnv = true;
                logger.log(LOGLEVEL, "Concection parameters from JVM Environment used");
            }

        } catch (Exception e) {
            jvmEnv = false;
        }

        if (!jvmEnv) {
            logger.log(LOGLEVEL, "No connection parameters found in JVM environment, going further..");
        }
        return jvmEnv;
    }

    private boolean readFromOSenv() {
        boolean osEnv = false;
        try {
            setUserName(System.getenv("sql.fredy.admin.connection.username"));
            setUserPassword(System.getenv("sql.fredy.admin.connection.password"));
            setJdbcDriver(System.getenv("sql.fredy.admin.connection.jdbcdriver"));
            setUrl(System.getenv("sql.fredy.admin.connection.jdbcurl"));
            setHostName(System.getenv("sql.fredy.admin.connection.hostname"));
            setDbListenerPort(Integer.parseInt(System.getenv("sql.fredy.admin.connection.dbport")));
            setDataBase(System.getenv("sql.fredy.admin.connection.db"));
            setAdditionalParameters(System.getenv("sql.fredy.admin.connection.additionalparameter"));
            if ((getUrl() != null) && (getUrl().length() > 1)) {
                osEnv = true;
                logger.log(LOGLEVEL, "Concection parameters from OS Environment used");
            }
        } catch (Exception e) {
            osEnv = false;
        }

        if (!osEnv) {
            logger.log(LOGLEVEL, "No connection parameters found in OS environment, going further..");
        }

        return osEnv;
    }

    private boolean readConfigFile() {
        String directory = System.getenv("admin.work");
        if (directory == null) {
            directory = System.getProperty("admin.work");
            if (directory == null) {
                directory = System.getProperty("user.home");
            }
        }

        directory = directory + File.separator + "sqladmin" + File.separator + "poolconfig";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }

        String poolConfigFile = directory + File.separator + "defaultDBpool.props";
        
        

        boolean cfgFile = false;
        Properties config = new Properties();

        logger.log(Level.INFO, "Properties {0}", poolConfigFile);

        try {
            InputStream input = new FileInputStream(poolConfigFile);
            config.load(input);
            setUserName(config.getProperty("user").trim());

            // the Password is simply encrypted
            Encryption encryption = new Encryption();
            int encryptionOffset = 31;
            setUserPassword(encryption.decrypt(config.getProperty("password").trim(), encryptionOffset));
            setJdbcDriver(config.getProperty("jdbcdriver").trim());
            setUrl(config.getProperty("dburl").trim());
            setHostName(config.getProperty("host").trim());
            setDbListenerPort(Integer.parseInt(config.getProperty("dbport").trim()));
            setDataBase(config.getProperty("db").trim());
            setAdditionalParameters(config.getProperty("additionalParameters"));
            cfgFile = true;
            logger.log(LOGLEVEL, "Concection parameters from Configuration file used");

            /*  eEbugging only :-)
            System.out.println("JDBC Driver: " + getJdbcDriver() + "\n" 
                             + "JDBC URL   : " + getUrl() + "\n"
                             + "Hostname   : " + getHostName() + "\n"
                             + "Port       : " + String.format("%d",getDbListenerPort()) + "\n"
                             + "Additional : " + getAdditionalParameters() + "\n"
                             + "Username   : " + getUserName() + "\n"
                             + "Password   : " + getUserPassword()
             );
             */
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception {0}", ex.getMessage());
            //ex.printStackTrace();
            cfgFile = false;
        }

        if (!cfgFile) {
            logger.log(LOGLEVEL, "No connection parameters found in config file {0} it might not work :-(", poolConfigFile);
        }

        return cfgFile;
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
        logger.log(Level.INFO, "User: {0}\nPassword: {1}\nHost: {2}\nPort: {3}\nJDBC Driver: {4}\nJDBC URL: {5}", new Object[]{getUserName(), getUserPassword(), getHostName(), getDbListenerPort(), getJdbcDriver(), getUrl()});
    }

    private static final Object locked = new Object();

    public static DataSource getInstance() throws IOException, SQLException, PropertyVetoException {
        synchronized (locked) {
            if (null == datasource) {
                datasource = new DataSource();
                Logger.getLogger("sql.fredy.connection").log(LOGLEVEL, "Datasource created");
            } else {
                Logger.getLogger("sql.fredy.connection").log(LOGLEVEL, "Datasource reused");
            }
            return datasource;
        }

    }

    // changed by Fredy 2022-05-19
    public Connection getConnection() throws SQLException {
        if (ds == null) {
            return null;
        }
        datasource.setConnectionError(null);
        try {
            logger.log(Level.FINE, "Getting connection from Default Catalog: {0}", this.ds.getDefaultCatalog());

            // it is a shame, if the connection can not be established and to get over that, datasource must be set to null
            Connection con = this.ds.getConnection();
            Connection dconn = ((DelegatingConnection) con).getInnermostDelegate();
            if (null == con) {
                datasource = null;
                ds = null;
                logger.log(Level.INFO, "Connection not established to: {0}", this.getDataBase());
                return null;
            }
            return con; //this.ds.getConnection();
        } catch (SQLException e) {
            //ds = null;                        
            datasource = null;
            Logger.getLogger("sql.fredy.connection").log(Level.WARNING, "SQLException while getting connection {0}", e.getMessage());
            throw e;
        } catch (UnsupportedClassVersionError | Exception e) {
            Logger.getLogger("sql.fredy.connection").log(Level.WARNING, "Exception while getting connection {0}", e.getMessage());

            datasource = null;
            return null;
        }
    }

    /**
     * @return the additionalParameters
     */
    public String getAdditionalParameters() {
        return additionalParameters;
    }

    /**
     * @param additionalParameters the additionalParameters to set
     */
    public void setAdditionalParameters(String additionalParameters) {
        this.additionalParameters = additionalParameters;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
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
            if ((ds.getConnection() != null) && (ds.getConnection().isValid(60))) {
                dmd = ds.getConnection().getMetaData();
            } else {
                ds = null;
                datasource = null;
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, "Metadata not readable: {0}", ex.getMessage());
            ds = null;
            datasource = null;
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Metadata not readable: {0}", ex.getMessage());
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
            logger.log(Level.WARNING, "User {0} throws Exception while reading ProductName from MetaData  |JDBC-Driver: {1} |Database: {2} |Error-Code: {3} |SQL-State: {4} |Exception: {5}", new Object[]{getUserName(), getJdbcDriver(), getDataBase(), sqe.getErrorCode(), sqe.getSQLState(), sqe.getMessage().toString()});
        } catch (Exception sqe) {
            logger.log(Level.SEVERE, "User: {0} unexpected exception: {1}", new Object[]{getUserName(), sqe.getMessage()});
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
            logger.log(Level.WARNING, "User {0} throws Exception while reading ProductVersion from MetaData  |JDBC-Driver: {1} |Database: {2} |Error-Code: {3} |SQL-State: {4} |Exception: {5}", new Object[]{getUserName(), getJdbcDriver(), getDataBase(), sqe.getErrorCode(), sqe.getSQLState(), sqe.getMessage().toString()});
        } catch (Exception sqe) {
            logger.log(Level.SEVERE, "User: {0} unexpected exception: {1}", new Object[]{getUserName(), sqe.getMessage()});
        }
    }

    public int getNumberOfOpenConnections() {
        return ds.getNumActive();
    }

    public int getNumberOfIdleConnections() {
        return ds.getNumIdle();
    }

    public static void main(String[] args) {
        Connection con = null;
        try {
            con = DataSource.getInstance().getConnection();
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
                            + DataSource.getInstance().getDataBase()
                            + " on "
                            + DataSource.getInstance().getHostName()
                            + " DB Product "
                            + DataSource.getInstance().getProductName());
                } catch (IOException ex) {
                    System.out.println("Ooops.... IO Exception: " + ex);
                } catch (PropertyVetoException ex) {
                    System.out.println("Ooops.... Property Veto Exception: " + ex);
                }
                con.close();
            } catch (SQLException ex) {
                System.out.println("Ooops.... SQL Exception: " + ex);
            } catch (Exception ex) {

            }
        }
    }

    private boolean usePassword;

    /**
     * @return the usePassword
     */
    public boolean isUsePassword() {
        return usePassword;
    }

    /**
     * @param usePassword the usePassword to set
     */
    public void setUsePassword(boolean usePassword) {
        this.usePassword = usePassword;
    }

    public void close() {
        if (datasource == null) {
            return;
        }

        if (ds != null) {
            try {
                ds.close();

            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception whil closing datasource {0}", e.getMessage());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception whil closing datasource {0}", e.getMessage());
            }
        }
        datasource = null;
        //ds = null;
    }
}
