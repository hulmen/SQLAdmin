/*
 * This class is to control the internal Derby Server.
 * Apache Derby DB is used to save several information for SQLAdmin
 *
 * SqlMonitor
 * - your saved code templates  
 * - every query you send to the DB you are working on
 * - 


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
package sql.fredy.infodb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.Policy;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.derby.drda.NetworkServerControl;

/**
 *
 * @author fredy
 */
public class DBserver implements Runnable {

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
    private Logger logger = Logger.getLogger("sql.fredy.infodb");

    // to start Derby, we need to know the port and the hostname to listen
    private int port = 60606;
    private String hostname = "localhost";
    private Thread thread = null;
    private boolean running = false;
    private NetworkServerControl server = null;

    public DBserver() {
        // just the standard constructor
    }

    public DBserver(String hostname, int port) {
        setHostname(hostname);
        setPort(port);
    }

    public boolean serverPing() {
        boolean ok = true;
        try {
            server.ping();
        } catch (SocketException e) {
            logger.log(Level.FINE, "{0} Derby Server not ready", e.getMessage());
            ok = false;
        } catch (Exception ex) {
           logger.log(Level.FINE, "{0} Derby Server not ready", ex.getMessage());
        }
        return ok;
    }

    @Override
    public void run() {
        if (server == null) {
            try {

                System.out.println(getClass().getName() + ". Hostname: " + InetAddress.getByName(getHostname()) + " Port: "  +getPort());
                      
                
                server = new NetworkServerControl((InetAddress) InetAddress.getByName(getHostname()), getPort()); 
                server.start(new PrintWriter(System.out));
                
                logger.log(Level.INFO, "Derby Server started on Host {0} on port {1} ", new Object[]{getHostname(), Integer.toString(getPort())});
                logger.log(Level.FINE, server.getRuntimeInfo());
                logger.log(Level.FINE, server.getSysinfo());
                setRunning(true);
            } catch (UnknownHostException uhe) {
                logger.log(Level.WARNING, "Host ''localhost'' not found.  Exception {0} ", uhe.getMessage());
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Exception while starting Derby Server on localhost {} ", ex.getMessage());
                //ex.printStackTrace();
            }
        }

    }

    public void startUp() {
        if (thread == null) {
            thread = new Thread(this, "SQLadmin Derby Server");
            thread.start();
        }
    }

    public void stop() {
        if (isRunning()) {
            boolean cantShutdown = false;
            try {
                DriverManager.getConnection("jdbc:derby://" + getHostname() + ":" + Integer.toString(getPort()) + "/;shutdown=true");

            } catch (SQLException se) {
                if (se.getSQLState().equals("XJ015")) {
                    cantShutdown = true;
                } else {
                    logger.log(Level.INFO, "SQLException when shuttng down: {0} State: {1}", new Object[]{se.getMessage(), se.getSQLState()});
                }

            } catch (Exception e) {
                logger.log(Level.WARNING, "Something went wrong when shutting down derby {0}", e.getMessage());
                //e.printStackTrace();
            }

            if (!cantShutdown) {
                logger.log(Level.WARNING, "Derby DB on port {0} did not shut down properly", Integer.toString(getPort()));
            } else {
                logger.log(Level.INFO, "Derby DB on port {0} properly shut down", Integer.toString(getPort()));
            }
        } else {
            logger.log(Level.INFO, "I'm ,not the server, so I do not shut down the Derby-DB Server");
        }
    }

    public static void main(String[] args) {
        DBserver dbserver = new DBserver();
        dbserver.startUp();

        String shd = readFromPrompt("ShutDown ?", "y");

        dbserver.stop();

    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @param running the running to set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /* read from commandline
     *
     * 1st parameter as displaytext
     * 2nd parameter as defaultvalue
     */
    public static String readFromPrompt(String text, String defValue) {
        String fromPrompt = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(text + " (Default: " + defValue + ") ");
        try {
            fromPrompt = br.readLine();
            if (fromPrompt.length() < 1) {
                fromPrompt = defValue;
            }
        } catch (IOException ioe) {
            fromPrompt = defValue;
        }
        return fromPrompt;
    }

}
