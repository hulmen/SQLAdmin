/*
 
 DerbyInfoServer is a part of Admin ...

  DerbyInfoServer starts a Derby-DB listening on localhost only
  This Derby-Instance is used to save Parameters of the Admin-Tools
  as well as the CodeCompletion-Things of the SQL-Monitor
  it also stores the latest  file-location so if you open a file you are
  at the location you have been before... as long as the SW asks to be there...

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
package sql.fredy.tools;

import applications.basics.JTextFieldFilter;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.derby.drda.NetworkServerControl;

/**
 *
 * @author sql@hulmen.ch
 */
public class DerbyInfoServer implements Runnable {

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

    NetworkServerControl server;

    private String JDBCDriver = "org.apache.derby.jdbc.ClientDriver";
    private String JDBCUrl;
    private String dbLocation;
    private String dbName;
    private String hostname = "localhost";
    private Logger logger = Logger.getLogger("sql.fredy.tools");
    private Connection connection;

    private int derbyPort = 0;
    private static final int DERBYPORT = 60606;  /// this is the default derby port
    private boolean localExecution = false;
    private boolean running = false;
    private int runningPort = 0;

    // to indicate wheter this instance is the server, returns false if an error appeared by starting up the server
    private boolean iamTheServer = true;
    Thread t;

    public DerbyInfoServer() {
        t = new Thread(this, "Derby");
        t.start();
        checkServer();
    }

    /**
     *
     * @param startup if this is true, the server will start else it is just
     * ready to be used, e.g. use it to find out the default-DBname for the
     * connection by getJDBCDriver() and getJDBCUrl()
     */
    public DerbyInfoServer(boolean startup) {
        t = new Thread(this, "Derby");
        if (startup) {
            t.start();
            checkServer();
        }
    }

    private void refreshThread() {
        t = new Thread(this, "Derby");
    }

    public static void main(String args[]) {
        /*
         DerbyInfoServer derby = new DerbyInfoServer();
         readFromPrompt("Type something to stop DerbyServer:", "x");
         derby.stop();
        
         */
        DerbyInfoServer derby = new DerbyInfoServer(false);
        derby.setLocalExecution(true);
        derby.gui();

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

    public void start() {
        boolean isfine = false;
        if ((isRunning()) && (getRunningPort() == getDerbyPort())) {
            logger.log(Level.INFO, "Derby already running {0}", getJDBCUrl());
            return;
        }
        try {

            //server = new NetworkServerControl(InetAddress.getByName("localhost"), getDerbyPort());
            server = new NetworkServerControl(InetAddress.getByName(getHostname()), getDerbyPort());
            server.start(null);
            logger.log(Level.INFO, "Derby Server started on Host {0} on port {1} ", new Object[]{getHostname(), Integer.toString(getDerbyPort())});
            logger.log(Level.INFO, server.getRuntimeInfo());
            logger.log(Level.INFO, server.getSysinfo());
            setRunning(true);
            setRunningPort(getDerbyPort());

            // We do a test, to see if derby is running
            test();

        } catch (UnknownHostException uhe) {
            logger.log(Level.WARNING, "Host ''localhost'' not found.  Exception {0} ", uhe.getMessage());
            setIamTheServer(false);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "Exception while starting Derby Server on localhost {} ", ex.getMessage());
            setIamTheServer(false);
            ex.printStackTrace();
        }
    }

    /**
     * this is to check if the server could be started up or not if not, we
     * higher the port up to max 1537
     */
    private boolean checkServer() {
        //if ( isRunning() ) return true;

        String job = "";
        try {
            job = "Starting up the server";
            if (!isRunning()) {
                start();
            }
            logger.log(Level.INFO, getJDBCUrl());
            connection = DriverManager.getConnection(getJDBCUrl());

        } catch (SQLException sqlex) {
            if (sqlex.getSQLState().equalsIgnoreCase("08004")) {
                logger.log(Level.INFO, "Error 08004, creating DB");
                createDB();
            } else {
                logger.log(Level.INFO, "Error while starting up the server : {0}", sqlex.getMessage());
                logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
                logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
                logger.log(Level.INFO, "Job : {0}", job);
            }
        }
        return running;
    }

    public void stop() {
        if (isRunning()) {
            boolean cantShutdown = false;
            try {
                connection.close();
                DriverManager.getConnection("jdbc:derby://localhost:" + Integer.toString(getDerbyPort()) + "/;shutdown=true");
                //DriverManager.getConnection("jdbc:derby:;shutdown=true");
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
                logger.log(Level.WARNING, "Derby DB on port {0} did not shut down properly", getDerbyPort());
            } else {
                logger.log(Level.INFO, "Derby DB on port {0} properly shut down", getDerbyPort());
            }
        } else {
            logger.log(Level.INFO, "I',not the server, so I do not shut down the Derby-DB Server");
        }
    }

    public void initDB() {

        /*
        try {
            Class.forName(getJDBCDriver()).newInstance();
        } catch (ClassNotFoundException ex) {
            logger.log(Level.SEVERE, "Derby Driver not found ", ex);
        } catch (InstantiationException iex) {
            logger.log(Level.SEVERE, "Error when loading Derby driver ", iex);
        } catch (IllegalAccessException iax) {
            logger.log(Level.SEVERE, "Error when loading Derby driver ", iax);
        }
         */
        checkDB();

    }

    // find out, if the DerbyDB alread exists;
    private void checkDB() {
        try {
            connection = DriverManager.getConnection(getJDBCUrl());
            SQLWarning connectionWarning = connection.getWarnings();

            if (connectionWarning != null) {
                logger.log(Level.INFO, "Connection Warning: {0} SQLstate: {1}", new Object[]{connectionWarning.getMessage(), connectionWarning.getSQLState()});
                createDB();

                if (!"01J01".equalsIgnoreCase(connectionWarning.getSQLState())) {
                    while (connectionWarning != null) {

                        String warningMessage = connectionWarning.getMessage();
                        String warningSQLState = connectionWarning.getSQLState();
                        int warningErrorCode = connectionWarning.getErrorCode();
                        logger.log(Level.WARNING, "Connection warning: {0} Message: {1} SQLState: {2}", new Object[]{warningErrorCode, warningMessage, warningSQLState});

                        connectionWarning = connectionWarning.getNextWarning();
                    }
                }
                connection = DriverManager.getConnection(getJDBCUrl());
            }

            logger.log(Level.INFO, "Derby is listening on localhost and is ready to serve your requests for DB " + getDbName());
        } catch (SQLException sqlex) {
            createDB();
        }

    }

    private void test() {
        try {

            logger.log(Level.INFO, "Trying to connecto to derby with: {0}", getJDBCUrl());

            connection = DriverManager.getConnection(getJDBCUrl());
            SQLWarning connectionWarning = connection.getWarnings();
            if (connectionWarning != null) {
                logger.log(Level.INFO, "Connection Warning: {0} {1}", new Object[]{connectionWarning.getMessage(), connectionWarning.getSQLState()});
            }
            // now we try to get MetaData
            DatabaseMetaData dmd = connection.getMetaData();
            logger.log(Level.INFO, "DB Product Name: {0} {1} Driver: {2} {3}", new Object[]{dmd.getDatabaseProductName(), dmd.getDatabaseProductVersion(), dmd.getDriverName(), dmd.getDriverVersion()});

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Derby is not running " + sqlex.getMessage());
        }
    }


    /*
     This checks if the SqlAdmin internal DB is availabile
     if not, the DB and the tables used for SQLAdmin are created
     */
    public void verifySQLAdminDB() {
        boolean available = false;
        try {
            Statement stmt = connection.createStatement();
            ResultSet checkRs = stmt.executeQuery("select Name, Content from APP.AdminParameter where Name = 'alive'");
            String alive = "no";
            while (checkRs.next()) {
                alive = checkRs.getString(1);
                break;
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while SQL operation: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }
    }

    private String getUser() {
        try {
            return System.getProperty("user.name");
        } catch (Exception e) {
            return "unknown";
        }
    }

    private void createDB() {
        Statement stmt = null;
        try {
            logger.log(Level.INFO, "First usage, initialising DB {0}", getDbName());
            connection = DriverManager.getConnection(getJDBCUrl() + ";create=true");
            stmt = connection.createStatement();

            /*
            String createCommand = "create table APP.CodeCompletion (\n"
                    + " ToolUser VARCHAR(128) not null,\n"
                    + "	TemplateName VARCHAR(25) NOT NULL ,\n"
                    + "	Abbreviation VARCHAR(25) ,\n"
                    + "	CodeTemplate VARCHAR(4096) ,\n"
                    + "	Description VARCHAR(4096)  ,\n"
                    + "	PRIMARY KEY (ToolUser,TemplateName)\n"
                    + ")";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(createCommand);

            createCommand = "create table APP.AdminParameter (ToolUser VARCHAR(128), Name VARCHAR(255), Content VARCHAR(4096) )";
            stmt.executeUpdate(createCommand);

            String cmd = "insert into AdminParameter(Name, Content) values ('" + getUser() + "',alive','yes')";
            stmt.executeUpdate(cmd);

            String index = "create unique index ccIdx on APP.CodeCompletion (Abbreviation, ToolUser)";
            stmt.executeUpdate(index);

             */
            String createTable = "create table APP.QueryHistory (\n"
                    + "ToolUser VARCHAR(128) not null,\n"
                    + "RunAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                    + "Database varchar(1024),\n"
                    + "Query VARCHAR(32672),\n"
                    + "HashCode INTEGER "
                    + ")";

            // we create the table and ignore the error, if it already exists            
            stmt.executeUpdate(createTable);

            // and the Index
            stmt.executeUpdate("create INDEX APP.QUERYHISTORYIDX on APP.QueryHistory(HashCode, ToolUser)");

            // Code Completion Table
            stmt.executeUpdate("create table CODECOMPLETION (\n"
                    + " ToolUser VARCHAR(128) not null,\n"
                    + " TemplateName VARCHAR(25) NOT NULL ,\n"
                    + " Abbreviation VARCHAR(25) ,\n"
                    + " CodeTemplate VARCHAR(4096) ,\n"
                    + " Description VARCHAR(4096)  ,\n"
                    + " PRIMARY KEY (ToolUser,TemplateName)\n"
                    + " )");

            stmt.executeUpdate("create  index ccIdx on CODECOMPLETION (Abbreviation, ToolUser)");

            //stmt.executeUpdate("create table AdminParameter (ToolUser VARCHAR(128), Name VARCHAR(255), Content VARCHAR(4096) );");
            //stmt.executeUpdate("insert into AdminParameter(ToolUser, Name, Content) values ('" + getUser() + "',alive','yes')");
            connection = DriverManager.getConnection(JDBCUrl);
            stmt.close();
        } catch (SQLException sqlex2) {
            logger.log(Level.WARNING, "Exception while initialising the internal DB {0}", sqlex2.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex2.getErrorCode());
            logger.log(Level.INFO, "SQLState  : {0}", sqlex2.getSQLState());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception while closing the CREATE TABLE Statement {0}", e.getMessage());
                }
            }
        }
    }

    /**
     * @return the JDBCDriver
     */
    public String getJDBCDriver() {
        return JDBCDriver;
    }

    /**
     * @param JDBCDriver the JDBCDriver to set
     */
    public void setJDBCDriver(String JDBCDriver) {
        this.JDBCDriver = JDBCDriver;
    }

    /**
     * @return the JDBCUrl
     */
    public String getJDBCUrl() {
        return JDBCUrl = "jdbc:derby://localhost:" + Integer.toString(getDerbyPort()) + "/" + getDbName(); // + ";create=true";
    }

    /**
     * @return the dbLocation
     */
    public String getDbLocation() {
        return dbLocation;
    }

    /**
     * @param dbLocation the dbLocation to set
     */
    public void setDbLocation(String dbLocation) {
        this.dbLocation = dbLocation;
    }

    /**
     * @return the dbName
     */
    public String getDbName() {

        String adminConfigDb = null;
        try {
            adminConfigDb = System.getenv("sql.fredy.admin.configdb");
            if (adminConfigDb != null) {
                //logger.log(Level.INFO, "Config DB: {0}", adminConfigDb);
                return adminConfigDb;
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Can not find Environment variable for config DB, using standard");
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
        return directory + File.separator + "SQLAdminWorkDB";
    }

    /**
     * @param dbName the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    @Override
    public void run() {
        start();
        //initDB();
    }

    /**
     * @return the iamTheServer
     */
    public boolean isIamTheServer() {
        return iamTheServer;
    }

    /**
     * @param iamTheServer the iamTheServer to set
     */
    public void setIamTheServer(boolean iamTheServer) {
        this.iamTheServer = iamTheServer;
    }

    /**
     * @return the derbyPort
     */
    public int getDerbyPort() {
        if (derbyPort == 0) {
            setDefaultDerbyPort(DERBYPORT);  // Default Port is 60606
        }
        return derbyPort;

    }

    public void setDerbyPort(int port) {
        this.derbyPort = port;
    }

    /**
     * @param derbyPort the derbyPort to set
     */
    public void setDefaultDerbyPort(int dp) {
        derbyPort = dp;

        String port = System.getProperty("sql.fredy.sqltool.derbyport");
        if (port != null) {
            try {
                derbyPort = Integer.parseInt(port);
            } catch (Exception e) {
                derbyPort = dp;
            }
        }

        if (port == null) {
            port = System.getenv("sql.fredy.sqltool.derbyport");
            if (port != null) {
                try {
                    derbyPort = Integer.parseInt(port);
                } catch (Exception e) {
                    derbyPort = dp;
                }
            }
        }
    }

    public void shutdown() {
        boolean cantShutdown = false;
        try {
            DriverManager.getConnection("jdbc:derby://localhost:" + port.getText() + "/;shutdown=true");
        } catch (SQLException se) {
            if (se.getSQLState().equals("XJ015")) {
                cantShutdown = true;
            } else {
                logger.log(Level.INFO, "SQLException when shutting down: {0} State: {1}", new Object[]{se.getMessage(), se.getSQLState()});
            }
        }
        if (!cantShutdown) {
            logger.log(Level.WARNING, "Derby Database did not shut down properly");
        } else {
            logger.log(Level.INFO, "Derby DB properly shut down");
        }
    }

    String dbDirectory = "";
    private JTextField port, db;
    private JTextArea infoText;
    private JTextField host;
    private JButton newDB;
    private JToggleButton startUp;
    private static DerbyInfoServer derbyInfoServer = null;

    public static DerbyInfoServer getInstance() {
        if (derbyInfoServer == null) {
            derbyInfoServer = new DerbyInfoServer(false);
        }
        return derbyInfoServer;
    }

    public void gui() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        panel.add(new JLabel("Interface:"));
        host = new JTextField("localhost", 20);
        host.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setHostname(host.getText());
            }
        });

        host.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                //System.out.println("changeUpdate");
                check();
            }

            public void removeUpdate(DocumentEvent e) {
                //System.out.println("removeUpdate");
                check();
            }

            public void insertUpdate(DocumentEvent e) {
                //System.out.println("removeUpdate");
                check();
            }

            public void check() {
                //System.out.println(host.getText());
                setHostname(host.getText());
            }

        });

        panel.add(host);
        panel.add(new JLabel("Derby Port"));
        port = new JTextField(5);
        port.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));
        port.setText("1527");
        panel.add(port);
        startUp = new JToggleButton("start");
        panel.add(startUp);

        db = new JTextField(20);
        panel.add(new JLabel("Create Database: "));
        panel.add(db);

        final JButton selectFile = new JButton("...");
        selectFile.setToolTipText("select the directory, where I'm creating the database: ");
        panel.add(selectFile);
        selectFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Select Directory");

                int returnVal = chooser.showOpenDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    dbDirectory = chooser.getCurrentDirectory() + java.io.File.separator + chooser.getSelectedFile().getName();
                    //db.setText(dbDirectory);
                    selectFile.setToolTipText("the Derby DB will be created in " + dbDirectory);
                    infoText.setText("The Derby DB will by created in " + dbDirectory);
                }
            }
        });

        newDB = new JButton("Create DB");
        newDB.setToolTipText("Make sure the server has been start up otherwise I can not create a Database.");
        newDB.setEnabled(false);
        panel.add(newDB);
        newDB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if ((dbDirectory.length() > 1) && (db.getText().length() > 1)) {
                    try {
                        Connection c = DriverManager.getConnection("jdbc:derby://localhost:" + Integer.parseInt(port.getText()) + "/" + dbDirectory + File.separator + db.getText() + ";create=true");
                        SQLWarning connectionWarning = c.getWarnings();

                        if (connectionWarning != null) {

                            if (!"01J01".equalsIgnoreCase(connectionWarning.getSQLState())) {
                                while (connectionWarning != null) {

                                    String warningMessage = connectionWarning.getMessage();
                                    String warningSQLState = connectionWarning.getSQLState();
                                    int warningErrorCode = connectionWarning.getErrorCode();
                                    logger.log(Level.WARNING, "Connection warning: {0} Message: {1} SQLState: {2}", new Object[]{warningErrorCode, warningMessage, warningSQLState});

                                    connectionWarning = connectionWarning.getNextWarning();
                                }
                            }
                        }
                        logger.log(Level.INFO, "DB createtd: {0}{1}{2}", new Object[]{dbDirectory, File.separator, db.getText()});
                    } catch (SQLException s) {
                        logger.log(Level.WARNING, "Exception when creating DB: {0}{1}{2}  Message: {3}", new Object[]{dbDirectory, File.separator, db.getText(), s.getMessage()});
                    }
                }
            }
        });

        // to create a DB, I need to have a name and a directory
        db.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                //System.out.println("changeUpdate");
                check();
            }

            public void removeUpdate(DocumentEvent e) {
                //System.out.println("removeUpdate");
                check();
            }

            public void insertUpdate(DocumentEvent e) {
                //System.out.println("removeUpdate");
                check();
            }

            public void check() {
                if ((db.getText().length() > 0) && (startUp.getText().equalsIgnoreCase("stop"))) {
                    newDB.setEnabled(true);
                } else {
                    newDB.setEnabled(false);
                }
            }

        });

        final JButton close = new JButton("Close");
        if (!isLocalExecution()) {
            close.setEnabled(false);
        }

        close.setToolTipText("shut down and close");
        panel.add(close);

        startUp.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent ev) {

                // Start the server
                if (startUp.isSelected()) {
                    newDB.setEnabled(true);
                    close.setEnabled(false);
                    startUp.setText("Stop");
                    ///System.out.println("Start Derby!");
                    newDB.setToolTipText("Provide a directory and a DB Name to create a Derby Database.");

                    setDerbyPort(Integer.parseInt(port.getText()));
                    try {
                        t.start();
                    } catch (java.lang.IllegalThreadStateException i) {
                        refreshThread();
                        t.start();
                    }

                } else {

                    // shut the server down
                    newDB.setEnabled(false);
                    newDB.setToolTipText("Make sure the server has been start up otherwise I can not create a Database.");
                    startUp.setText("Start");

                    shutdown();
                    if (isLocalExecution()) {
                        close.setEnabled(true);
                    }
                }
            }
        });

        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isLocalExecution()) {
                    System.exit(0);
                }
            }
        });
        JFrame f = new JFrame();
        f.setTitle("Fredy's Derby Server");
        f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        Container c = f.getContentPane();
        f.setLayout(new BorderLayout());
        c.add(panel, BorderLayout.CENTER);

        infoText = new JTextArea(3, 80);
        infoText.setEditable(false);
        infoText.setText("To create a new Derby Database you can add the parameter';create=true' to the connection URL.\n"
                + "Or you can type in the name of the database within the upper textfield and you needd to select the directory with the directory-selection-button '...' \n"
                + "To create the DB, I need to now the directory and the name of the DB.");
        c.add(new JScrollPane(infoText), BorderLayout.SOUTH);

        f.pack();
        f.setVisible(true);

    }

    /**
     * @return the localExecution
     */
    public boolean isLocalExecution() {
        return localExecution;
    }

    /**
     * @param localExecution the localExecution to set
     */
    public void setLocalExecution(boolean localExecution) {
        this.localExecution = localExecution;
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

    /**
     * @return the runningPort
     */
    public int getRunningPort() {
        return runningPort;
    }

    /**
     * @param runningPort the runningPort to set
     */
    public void setRunningPort(int runningPort) {
        this.runningPort = runningPort;
    }

}
