/*
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
package sql.fredy.test;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.derby.drda.NetworkServerControl;

/**
 *
 * @author fredy
 */
public class DerbyDataSource {
     
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
     * @return the derbyPort
     */
    public int getDerbyPort() {
        return derbyPort;
    }

    /**
     * @param derbyPort the derbyPort to set
     */
    public void setDerbyPort(int derbyPort) {
        this.derbyPort = derbyPort;
    }
    private Logger logger = Logger.getLogger("sql.fredy.test");

    private static BasicDataSource dataSource = null;
    private NetworkServerControl server = null;
    private String hostname = "localhost";
    private int derbyPort = 60606;

    private static DerbyDataSource derbyDataSource = null;

    private String dataBase = "D:/SQLAdminRessources/sqladmin/work/SQLAdminWorkDB";

    public DerbyDataSource() {
        dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:derby://localhost:60606/" + getDataBase() + ";create=True");
        checkServer();
    }

    public static DerbyDataSource getInstance() throws IOException, SQLException, PropertyVetoException {
        if (derbyDataSource == null) {
            derbyDataSource = new DerbyDataSource();
            Logger.getLogger("sql.fredy.connection").log(Level.INFO, "Datasource created");
            return derbyDataSource;
        } else {
            Logger.getLogger("sql.fredy.connection").log(Level.INFO, "Datasource reused");
            return derbyDataSource;
        }
    }
   

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "can not get connection {0}", ex.getMessage());
            return null;
        }
    }

    private void checkServer() {
        if (server == null) {
          Thread thread = new Thread();
          thread.start();
        } else {
            logger.log(Level.INFO,"Server already running");
        }
    }

    public void run() {
         try {
                server = new NetworkServerControl(InetAddress.getByName(getHostname()), getDerbyPort());
                server.start(null);
                logger.log(Level.INFO, "Derby Server started on Host {0} on port {1} ", new Object[]{getHostname(), Integer.toString(getDerbyPort())});
                logger.log(Level.INFO, server.getRuntimeInfo());
                logger.log(Level.INFO, server.getSysinfo());

            } catch (UnknownHostException uhe) {
                logger.log(Level.WARNING, "Host ''localhost'' not found.  Exception {0} ", uhe.getMessage());
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Exception while starting Derby Server on localhost {} ", ex.getMessage());
                ex.printStackTrace();
            }
    }
    
    public void stop() {
        if (server != null) {
            try {
                DriverManager.getConnection("jdbc:derby://localhost:" + Integer.toString(getDerbyPort()) + "/;shutdown=true");
            } catch (SQLException se) {
                if (se.getSQLState().equals("XJ015")) {
                    logger.log(Level.INFO, "Derby shut down correctly");
                } else {
                    logger.log(Level.INFO, "SQLException when shuttng down: {0} State: {1}", new Object[]{se.getMessage(), se.getSQLState()});
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Something went wrong when shutting down derby {0}", e.getMessage());
            }
        }
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
    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

}
