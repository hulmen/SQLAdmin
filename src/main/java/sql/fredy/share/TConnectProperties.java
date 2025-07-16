package sql.fredy.share;

/**
 * *
 * TConnectProperties is used by t_connect and contains Information about the
 * JDBC-Connections. It looks as this: (in this example it is for MySQL
 *
 * #Properties for t_connect #Thu Jan 09 18:32:04 CET 2003 usePassword=yes <--
 * is the password send to the DB DatabasePort=3306 <-- TCP-Port where the RDBMS
 * is listening DebugMode=off <-- for the developer
 * JDBCdriver=org.gjt.mm.mysql.Driver <-- the JDBC-Driver JDBCurl=jdbc:mysql://
 * <-- JDBC-URL
 *
 *
 * This file is stored in one of the following locations: -
 * System.getProperty("user.home") - System.getProperty("admin.work")
 *
 * where the user.home in a Unix is the Home-Directory of the Unix-user
 * launching java. I do not know, where Windows-OSses are putting it, but it
 * works. You can set these values while starting the applications with the
 * following parameters: -Dadmin.work=adirectroy or -Duser.home=D:\java\share
 *
 * where admin.work has more weight then user.home, that means if admin.home is
 * set it ignores user.home
 *
 * Questions? send a E-Mail: sql@hulmen.ch
 *
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
 *
 * Copyright (c) 2017 Fredy Fischer, sql@hulmen.ch
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
import java.util.Properties;
import java.util.logging.*;
import java.io.*;

public class TConnectProperties {

    private Logger logger;
    private String defaultDriver = " ";
    private String defaultUrl = "jdbc:mysql://";
    private String defaultPort = "3306";
    private String defaultAuth = "yes";
    private String defaultMaxRowCount = "10000";
    private String additionalParameters = null;

    Properties prop;

    /**
     * Get the value of prop.
     *
     * @return value of prop.
     */
    public Properties getProp() {
        return prop;
    }

    /**
     * Set the value of prop.
     *
     * @param v Value to assign to prop.
     */
    public void setProp(Properties v) {
        this.prop = v;
    }

    public void init() {
        prop = new Properties();

        String directory = System.getProperty("user.home");
        if (System.getProperty("admin.work") != null) {
            directory = System.getProperty("admin.work");
        }

        try {
            FileInputStream input = new FileInputStream(directory
                    + File.separator
                    + "t_connect.props");
            prop.load(input);
        } catch (Exception ioex) {

            logger.log(Level.INFO, " can not load " + directory + File.separator
                    + "t_connect.props using standard values");

            prop.put("JDBCdriver", defaultDriver);
            prop.put("JDBCurl", defaultUrl);
            prop.put("DatabasePort", defaultPort);
            prop.put("usePassword", defaultAuth);
            try {
                prop.put("maxRowCount", getDefaultMaxRowCount());
            } catch (Exception e) {
                prop.put("maxRowCount", "10000");
            }
            if (getAdditionalParameters() != null) {
                try {
                    prop.put("additionalParameters", getAdditionalParameters());
                } catch (Exception e) {
                   
                }
            }
            ;
            prop.put("DebugMode", "off");
            save();
        }
    }

    public void save() {

        String directory = System.getProperty("user.home");
        if (System.getProperty("admin.work") != null) {
            directory = System.getProperty("admin.work");
        }

        try {
            FileOutputStream output = new FileOutputStream(directory
                    + File.separator
                    + "t_connect.props");
            prop.store(output, "Properties for t_connect");
            output.close();
        } catch (Exception ioex) {

            logger.log(Level.WARNING, " can not save " + directory + File.separator
                    + "t_connect.props");

        }
    }

    public void setDriver(String v) {
        prop.put("JDBCdriver", v);
    }

    public void setUrl(String v) {
        prop.put("JDBCurl", v);
    }

    public void setDatabasePort(String v) {
        prop.put("DatabasePort", v);
    }

    public void setUsePwd(boolean v) {
        if (v) {
            prop.put("usePassword", "yes");
        } else {
            prop.put("usePassword", "no");
        }
    }

    public void setDebug(boolean v) {
        if (v) {
            prop.put("DebugMode", "on");
        } else {
            prop.put("DebugMode", "off");
        }
    }

    public String getDriver() {
        return prop.getProperty("JDBCdriver");
    }

    public String getUrl() {
        return prop.getProperty("JDBCurl");
    }

    public String getDatabasePort() {
        return prop.getProperty("DatabasePort");
    }

    public String getUsePwd() {
        return prop.getProperty("usePassword");
    }

    public String getDebug() {
        return prop.getProperty("DebugMode");
    }

    public TConnectProperties() {
        logger = Logger.getLogger("sql.fredy.admin");
        init();
    }

    /**
     * @return the defaultMaxRowCount
     */
    public String getDefaultMaxRowCount() {
        return prop.getProperty("maxRowCount");
    }

    /**
     * @param defaultMaxRowCount the defaultMaxRowCount to set
     */
    public void setDefaultMaxRowCount(String defaultMaxRowCount) {
        this.defaultMaxRowCount = defaultMaxRowCount;
        prop.put("maxRowCount", defaultMaxRowCount);
        logger.log(Level.INFO, "setting maxRowcount to {0}", defaultMaxRowCount);
    }

    /**
     * @return the additionalParameters
     */
    public String getAdditionalParameters() {
         return prop.getProperty("additionalParameters");
    }

    /**
     * @param additionalParameters the additionalParameters to set
     */
    public void setAdditionalParameters(String additionalParameters) {
        this.additionalParameters = additionalParameters;
        prop.put("additionalParameters", additionalParameters);
    }

}
