package sql.fredy.ui;

/**
   DataBaseLogin is a part of Admin and done for loggin in to it...
  
   Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
   DB-Administrations, as create / delete / alter and query tables it also
   creates indices and generates simple Java-Code to access DBMS-tables and
   exports data into various formats

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
 */
import sql.fredy.share.TConnectProperties;
import sql.fredy.share.JdbcStuff;
import sql.fredy.share.t_connect;
import sql.fredy.share.SelectDriver;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.Vector;
import java.util.Properties;
import java.util.logging.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import sql.fredy.metadata.DBList;
import sql.fredy.tools.DerbyInfoServer;
import sql.fredy.tools.Encryption;
import groovy.lang.GroovySystem;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DataBaseLogin extends JPanel {

    // Fredy's make Version
    private static String fredysVersion = "Version 2.1  25. September 2016 ";

    int counter = 0;

    private Logger logger;

    public String getVersion() {
        return fredysVersion;
    }

    TConnectProperties prop;

    LoadImage loadImage = new LoadImage();

    int connections = 0;

    t_connect con = null;

    /**
     * Get the value of connections.
     *
     * @return value of connections.
     */
    public int getConnections() {
        connections = Integer.parseInt(noConns.getText());
        return connections;
    }

    /**
     * Set the value of connections.
     *
     * @param v Value to assign to connections.
     */
    public void setConnections(int v) {
        this.connections = v;
        //noConns.setText(Integer.toString(v));
    }

    /**
     * Get the value of con.
     *
     * @return value of con.
     */
    public t_connect getCon() {

        if ((con != null) && (con.isClosed())) {
            con = null;
        }

        if (con == null) {

            counter++;
            statusLine.setText("Connecting to " + getDatabase() + " ...");
            statusLine.updateUI();
            System.out.println("Create new connection:\n"
                    + "- User  : " + getUser() + "\n"
                    + "- Host  : " + getHost() + "\n"
                    + "- DB    : " + getDatabase());

            // update t_connect.props
            prop.setDriver(getDriver());
            prop.setUrl(getUrl());
            prop.setDatabasePort(getPort());
            prop.setUsePwd(lUsePassword.isSelected());
            prop.save();

            con = new t_connect(getHost(),
                    getUser(),
                    getPassword(),
                    getDatabase());

            if ((!con.acceptsConnection()) || (con.getError() != null)) {
                //con[activeConnection] = null;
                logger.log(Level.WARNING, "does not accept connections");
                System.out.println("- Status: does not accept connections\n\n");
                statusLine.setText("Can not connect to " + getDatabase() + " " + counter + " time(s)");
                statusLine.updateUI();
                con = null;

            } else {
                counter = 0;
                logger.log(Level.INFO, "accepts connections");
                System.out.println("- Status:  accepts connections\n\n");
                statusLine.setText("Connection to " + getDatabase() + " established");
                statusLine.updateUI();
            }
        }
        return con;
    }

    String host;

    /**
     * Get the value of host.
     *
     * @return Value of host.
     */
    public String getHost() {
        host = lHost.getText();
        return host;
    }

    /**
     * Set the value of host.
     *
     * @param v Value to assign to host.
     */
    public void setHost(String v) {
        this.host = v;
        lHost.setText(v);
    }

    String user;

    /**
     * Get the value of user.
     *
     * @return Value of user.
     */
    public String getUser() {
        user = lUser.getText();
        return user;
    }

    /**
     * Set the value of user.
     *
     * @param v Value to assign to user.
     */
    public void setUser(String v) {
        this.user = v;
        lUser.setText(v);
    }

    String password;

    /**
     * Get the value of password.
     *
     * @return Value of password.
     */
    public String getPassword() {
        //password = lPassword.getPassword().toString();
        password = String.valueOf(lPassword.getPassword());
        //password = lPassword.getText();
        return password;
    }

    /**
     * Set the value of password.
     *
     * @param v Value to assign to password.
     */
    public void setPassword(String v) {
        this.password = v;
        lPassword.setText(v);
    }

    String driver;

    /**
     * Get the value of JDBCdriver.
     *
     * @return Value of JDBCdriver.
     */
    public String getDriver() {
        driver = lJDBCDriver.getText();
        return driver;
    }

    /**
     * Set the value of JDBCdriver.
     *
     * @param v Value to assign to JDBCdriver.
     */
    public void setDriver(String v) {
        this.driver = v;
        lJDBCDriver.setText(v);
    }

    String url;

    /**
     * Get the value of JDBCurl.
     *
     * @return Value of JDBCurl.
     */
    public String getUrl() {
        url = lUrl.getText();
        return url;
    }

    /**
     * Set the value of JDBCurl.
     *
     * @param v Value to assign to JDBCurl.
     */
    public void setUrl(String v) {
        this.url = v;
        lUrl.setText(v);
    }

    String port;

    /**
     * Get the value of DatabasePort.
     *
     * @return Value of DatabasePort.
     */
    public String getPort() {
        port = lPort.getText();
        return port;
    }

    /**
     * Set the value of DatabasePort.
     *
     * @param v Value to assign to DatabasePort.
     */
    public void setPort(String v) {
        this.port = v;
        lPort.setText(v);
    }

    String database;

    /**
     * Get the value of database.
     *
     * @return Value of database.
     */
    public String getDatabase() {
        database = lDatabase.getText();
        return database;
    }

    /**
     * Set the value of database.
     *
     * @param v Value to assign to database.
     */
    public void setDatabase(String v) {
        this.database = v;
        lDatabase.setText(v);
    }

    String schema;

    /**
     * Get the value of schema.
     *
     * @return Value of schema.
     */
    public String getSchema() {
        this.schema = lSchema.getText();
        return schema;
    }

    /**
     * Set the value of schema.
     *
     * @param v Value to assign to schema.
     */
    public void setSchema(String v) {
        this.schema = v;
        lSchema.setText(v);
    }

    String usePassword;

    /**
     * Get the value of usePassword.
     *
     * @return Value of usePassword.
     */
    public String getUsePassword() {
        usePassword = "no";
        if (lUsePassword.isSelected()) {
            usePassword = "yes";
        }
        return usePassword;
    }

    /**
     * Set the value of usePassword.
     *
     * @param v Value to assign to usePassword.
     */
    public void setUsePassword(String v) {
        this.usePassword = v.toLowerCase();
        if (v.equalsIgnoreCase("yes")) {
            lUsePassword.setSelected(true);
        } else {
            lUsePassword.setSelected(false);
        }
    }

    public JButton cancel, connect;

    private JTextField lHost,
            lUser,
            lJDBCDriver,
            lUrl,
            lPort,
            lDatabase,
            lSchema,
            noConns;

    private JLabel statusLine;
    private ImageButton derbyServer;

    public JPasswordField lPassword;
    //public JTextField lPassword;
    public JCheckBox lUsePassword;
    public JCheckBox savePassword;
    private boolean savepwd = false;

    public DerbyInfoServer derby;

    /**
     * propFile is the File containing all the Properties
     *
     */
    public DataBaseLogin(String db) {

        logger = Logger.getLogger("sql.fredy.ui");

        prop = new TConnectProperties();

        this.setLayout(new BorderLayout());

        JPanel ffp = new JPanel();
        ffp.setLayout(new GridBagLayout());
        ffp.setBorder(BorderFactory.createEtchedBorder());

        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;

        lHost = new JTextField(20);
        lUser = new JTextField(20);
        lDatabase = new JTextField(15);
        lJDBCDriver = new JTextField(20);
        lUrl = new JTextField(20);
        lPort = new JTextField(5);
        lUsePassword = new JCheckBox("use Password");
        lPassword = new JPasswordField(20);
        lSchema = new JTextField(20);
        noConns = new JTextField(5);

        noConns.setToolTipText("max number of connections for this Session");
        noConns.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));

        //lPassword = new JTextField(20);
        gbc.gridx = 0;
        gbc.gridy = 0;
        ffp.add(new JLabel("Host"), gbc);
        gbc.gridy = 1;
        ffp.add(new JLabel("User"), gbc);
        gbc.gridy = 2;
        ffp.add(lUsePassword, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridy = 3;
        //ffp.add(new JLabel("Connections"), gbc);

        lHost.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pushNClearConnections();
            }
        });

        lDatabase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pushNClearConnections();
            }
        });

        lJDBCDriver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pushNClearConnections();

            }
        });

        lJDBCDriver.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changeUpdate");
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
                if (lJDBCDriver.getText().toLowerCase().startsWith("org.apache.derby.")) {
                    derbyServer.setEnabled(true);
                } else {
                    derbyServer.setEnabled(false);
                }
                  
                if ( lUrl.getText().toLowerCase().startsWith("jdbc:derby")) derbyServer.setEnabled(true);         
            }

        });

        lUrl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pushNClearConnections();
            }
        });

         lUrl.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                System.out.println("changeUpdate");
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
                if ( lUrl.getText().toLowerCase().startsWith("jdbc:derby")) {
                    derbyServer.setEnabled(true);
                } else {
                     derbyServer.setEnabled(false);
                }
            }

        });
        
        
        lPort.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                pushNClearConnections();
            }
        });

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridy = 0;
        ffp.add(lHost, gbc);
        gbc.gridy = 1;
        ffp.add(lUser, gbc);
        gbc.gridy = 2;
        ffp.add(lPassword, gbc);
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        noConns.setText("5");
        noConns.updateUI();
        //ffp.add(noConns, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;
        ffp.add(statusPanel(), gbc);

        FfAlignerLayoutPanel ffp2 = new FfAlignerLayoutPanel();
        ffp2.setBorder(BorderFactory.createEtchedBorder());
        ffp2.addComponent(new JLabel("JDBC-Driver"));
        ffp2.addComponent(lJDBCDriver);
        ffp2.addComponent(new JLabel("Database URL"));
        ffp2.addComponent(lUrl);
        ffp2.addComponent(new JLabel("Database Port"));
        ffp2.addComponent(lPort);
        ffp2.addComponent(new JLabel("Database"));
        ffp2.addComponent(lDatabase);
        ffp2.addComponent(new JLabel("Schema"));
        ffp2.addComponent(lSchema);

        final JComboBox ldb = new JComboBox();
        ldb.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setDatabase((String) ldb.getSelectedItem());
            }
        });

        final JComboBox loadSchema = new JComboBox();
        loadSchema.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSchema((String) loadSchema.getSelectedItem());
            }
        });

        ImageButton selectDB = new ImageButton("Select", "database.gif", "Select Database and Schema");
        selectDB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    DBList dblist = new DBList(getCon());
                    ldb.removeAllItems();
                    for (int i = 0; i < dblist.getDBs().size(); i++) {
                        ldb.addItem((String) dblist.getDBs().get(i));
                    }
                    ldb.setSelectedIndex(0);
                    ldb.updateUI();

                    loadSchema.removeAllItems();
                    for (int j = 0; j < dblist.getSchemas().size(); j++) {
                        loadSchema.addItem((String) dblist.getSchemas().get(j));
                    }
                    loadSchema.setSelectedIndex(0);
                    loadSchema.updateUI();

                } catch (Exception excpetion) {;
                }
            }
        });
        ffp2.addComponent(selectDB);
        ffp2.addComponent(ldb);
        ffp2.addComponent(new JLabel(""));
        ffp2.addComponent(loadSchema);

        FfAlignerLayoutPanel ffp3 = new FfAlignerLayoutPanel();
        ffp3.setBorder(BorderFactory.createEtchedBorder());
        ffp3.setNumberOfRows(1);

        JButton saveDef = new JButton("Save definition", loadImage.getImage("save.gif"));
        JButton loadDef = new JButton("Load definition", loadImage.getImage("load.gif"));
        savePassword = new JCheckBox("save Password", isSavepwd());

        derbyServer = new ImageButton("Derby Server", "datastore.gif", "launch Derby DB Server GUI (only if Derby is selected as JDBC)");
        derbyServer.setEnabled(false);
        derbyServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                derby = new DerbyInfoServer(false);
                derby.gui();
            }
        });

        savePassword.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setSavepwd(savePassword.isSelected());
            }
        });
        saveDef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveDefinition();
            }
        });
        loadDef.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadDefinition();
            }
        });

        ffp3.addComponent(savePassword);
        ffp3.addComponent(saveDef);
        ffp3.addComponent(loadDef);
        ffp3.addComponent(derbyServer);

        ffp3.addComponent(new JLabel("Select predefined"));
        final JLabel imgLabel = new JLabel(loadImage.getImage("sql.gif"));

        final SelectDriver sd = new SelectDriver(db);
        if (sd.getItemCount() > 0) {
            sd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    closeConnections();
                    JdbcStuff js = new JdbcStuff();
                    js = sd.getData(sd.getSelectedIndex());
                    logger.log(Level.INFO, "Changing JDBC-Driver to: "
                            + js.getJDBCDriver());
                    setDriver(js.getJDBCDriver());
                    setUrl(js.getDbUrl());
                    setPort(js.getPort());
                    imgLabel.setIcon(js.getImage());
                }
            });
        }
        ffp3.addComponent(sd);
        JScrollPane imgScroller = new JScrollPane(imgLabel);
        ffp3.addComponent(imgScroller);

        this.add("North", ffp);
        this.add("Center", ffp2);
        this.add("West", ffp3);

        lPassword.requestFocus();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        connect = new JButton("Connect", loadImage.getImage("plug.gif"));
        connect.setToolTipText("This stores the Info needed to connect to the Database");
        buttonPanel.add(connect);

        cancel = new JButton("Cancel", loadImage.getImage("unplug.gif"));
        cancel.setToolTipText("Exit");
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(cancel);

        this.add("South", buttonPanel);

    }

    public void pushNClearConnections() {
        if (con != null) {
            con.close();
            con = null;
        }

    }

    private boolean changeMessage() {
        boolean a = false;
        if (con != null) {
            Object[] options = {"Continue", "Cancel"};
            int n = JOptionPane.showOptionDialog(null,
                    "changeing of either\n"
                    + "host, port, database\n, URL or JDBC-Driver\n"
                    + "needs me to close the connection.\n"
                    + "Do you want to continue?",
                    "Connection changes!",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null, options, "Cancel");
            if (n == JOptionPane.YES_OPTION) {
                closeConnections();
                a = true;
            }
        }
        return a;
    }

    private ImageIcon loadImage(String image) {
        return loadImage.getImage(image);
    }

    private JPanel statusPanel() {
        JPanel panel = new JPanel();
        statusLine = new JLabel();
        statusLine.setText("Active Connection: none ");
        statusLine.setFont(new Font("Monospaced", Font.PLAIN, 10));
        statusLine.setBackground(Color.yellow);
        statusLine.setForeground(Color.blue);
        panel.setBackground(Color.yellow);
        panel.setForeground(Color.blue);
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        panel.add(statusLine);
        return panel;

    }

    public static void main(String args[]) {
        String db = null;
        if (args.length < 1) {
            db = readFromPrompt("please enter DB: ", "");
        } else {
            db = args[0];
        }

        if (db == null) {
            System.exit(0);
        }

        JFrame f = new JFrame("TestWindow");
        f.getContentPane().add(new DataBaseLogin(db));
        f.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            public void windowDeactivated(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowOpened(WindowEvent e) {
            }
        });
        f.pack();
        f.setVisible(true);
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

    protected void finalize() {
        closeConnections();
    }

    public void closeConnections() {
        if (con != null) {
            logger.log(Level.INFO, "Closing connections");
            try {
                con.closeCon();
                logger.log(Level.INFO, "Closing connection ");
            } catch (Exception e1) {
                logger.log(Level.INFO, "could not close all connections");
            }
        }

        // probably we have to shut down the derby server
        try {
            derby.shutdown();
        } catch (Exception e) {

        }
    }

    public void saveConnectProperties() {
        Properties tProps = new Properties();
        tProps.put("JDBCdriver", lJDBCDriver.getText());
        tProps.put("JDBCurl", lUrl.getText());
        tProps.put("DatabasePort", lPort.getText());
        tProps.put("usePassword", getUsePassword());

        tProps.put("DebugMode", "off");
        try {

            // Does this File exist?
            String admin_dir = System.getProperty("admin.work");
            if (admin_dir == null) {
                admin_dir = System.getProperty("user.home");
            }

            //File f = new File (admin_dir +File.separator+"t_connect.props");
            //boolean df = f.delete();
            FileOutputStream fops = new FileOutputStream(admin_dir + File.separator + "t_connect.props");
            //tProps.save(fops,"Properties for t_connect");
            tProps.store(fops, "Properties for t_connect");
            //fops.flush();
            fops.close();
        } catch (Exception e) {
            System.out.println("Error saving t_connect.props :" + e);
            e.printStackTrace();
        }

    }

    int offset = 31;

    private void saveDefinition() {
        Encryption encryption = new Encryption();
        Properties defProps = new Properties();
        defProps.put("host", lHost.getText());
        defProps.put("user", lUser.getText());
        defProps.put("connections", noConns.getText());
        defProps.put("jdbcdriver", lJDBCDriver.getText());
        defProps.put("dburl", lUrl.getText());
        defProps.put("dbport", lPort.getText());
        defProps.put("db", lDatabase.getText());
        defProps.put("schema", lSchema.getText());
        if (lUsePassword.isSelected()) {
            defProps.put("usePassword", "yes");
        } else {
            defProps.put("usePassword", "no");
        }

        if (isSavepwd()) {
            defProps.put("password", encryption.encrypt(getPassword(), offset));
        }
        String fileToSaveInto = "";
        try {

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogType(JFileChooser.SAVE_DIALOG);
            chooser.setDialogTitle("Select file to save into");
            chooser.setFileFilter(new sql.fredy.io.MyFileFilter("props"));

            int returnVal = chooser.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileToSaveInto = chooser.getSelectedFile().getPath();
                try (FileOutputStream fops = new FileOutputStream(fileToSaveInto)) {
                    defProps.store(fops, "Database Login parameter definition");
                    fops.flush();
                }
            }
        } catch (java.io.IOException e) {
            logger.log(Level.WARNING, "Can not save definition  into {0}" + " Message is: {1}", new Object[]{fileToSaveInto, e.getMessage()});
        }
    }

    private String propertiesFileLocation = "";

    private void loadDefinition() {
        Encryption encryption = new Encryption();
        Properties defProps = new Properties();

        JFileChooser chooser = new JFileChooser(getPropertiesFileLocation());

        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Select file to load from");
        chooser.setFileFilter(new sql.fredy.io.MyFileFilter("props"));

        String fileToLoadFrom = "";
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            FileInputStream fip = null;
            fileToLoadFrom = chooser.getSelectedFile().getPath();
            try {
                fip = new FileInputStream(fileToLoadFrom);
                setPropertiesFileLocation(chooser.getCurrentDirectory().getAbsolutePath());

            } catch (FileNotFoundException ex) {
                logger.log(Level.WARNING, "Can not load definition from {0}" + " Message is: {1}", new Object[]{fileToLoadFrom, ex.getMessage()});
            }
            try {
                defProps.load(fip);
                fip.close();
                lHost.setText((String) defProps.get("host"));
                lUser.setText((String) defProps.get("user"));
                noConns.setText((String) defProps.get("connections"));
                lJDBCDriver.setText((String) defProps.get("jdbcdriver"));
                lUrl.setText((String) defProps.get("dburl"));
                lPort.setText((String) defProps.get("dbport"));
                lDatabase.setText((String) defProps.get("db"));
                lSchema.setText((String) defProps.get("schema"));
                try {
                    if (((String) defProps.get("usePassword")).equalsIgnoreCase("yes")) {
                        lUsePassword.setSelected(true);
                        lPassword.setText(encryption.decrypt((String) defProps.get("password"), offset));
                        setSavepwd(true);
                    } else {
                        lUsePassword.setSelected(false);
                        setSavepwd(false);
                    }
                } catch (Exception e) {

                }

            } catch (IOException iox) {
                logger.log(Level.WARNING, "Can not get definition from {0}" + " Message is: {1}", new Object[]{fileToLoadFrom, iox.getMessage()});
            }
        }

    }

    /**
     * @return the propertiesFileLocation
     */
    public String getPropertiesFileLocation() {
        if ((propertiesFileLocation == null) || (propertiesFileLocation.length() < 2)) {
            propertiesFileLocation = System.getProperty("user.home");
        }
        return propertiesFileLocation;
    }

    /**
     * @param propertiesFileLocation the propertiesFileLocation to set
     */
    public void setPropertiesFileLocation(String propertiesFileLocation) {
        this.propertiesFileLocation = propertiesFileLocation;
    }

    /**
     * @return the savepwd
     */
    public boolean isSavepwd() {
        return savepwd;
    }

    /**
     * @param savepwd the savepwd to set
     */
    public void setSavepwd(boolean savepwd) {
        this.savepwd = savepwd;
    }

}
