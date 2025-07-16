package sql.fredy.admin;

/**
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, like: - create/ drop tables - create indices - perform
 * sql-statements - simple form - a guided query - Data Export and a other
 * usefull things in DB-arena
 *
 * Admin (Version see below)
 *
 * sql@hulmen.ch
 *
 * Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 *
 * The icons used in this application are from Dean S. Jones
 *
 * Icons Copyright(C) 1998 by Dean S. Jones dean@gallant.com
 * www.gallant.com/icons.htm
 *
 * CalendarBean is Copyright (c) by Kai Toedter
 *
 * MSeries is Copyright (c) by Martin Newstead
 *
 * POI is from the Apache Foundation
 *
 *
 * sql@hulmen.ch Postal: Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 *
 * Copyright (c) 2017 Fredy Fischer
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
 *
 * RSyntaxTextArea and AutoComplete is from www.fifesoft.com ----------------
 * Start fifesoft License -------------------------------------
 *
 * Copyright (c) 2012, Robert Futrell All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. * Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. * Neither the name of the author nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ----------------End fifesoft License -------------------------------------
 *
 * 2019-04-25 Changed the Properties Handling to Apache Commons Configuration
 * 2021-11-15 Change execution of SQL queries while taking stmt.execute into
 * consideration to easier find out if there was an update or a select
 * 2022-04-01 Fixed deprecated functions when going to JDK 17 2022-04-04 Fixed
 * program hang when a connection couldn't be established 2022-05-19 Fixed some
 * Apache Derby related things and DataSource-Issue, when connection fails
 *
 * 2023-07-14 creating a select-statement is setting the "['' -things for every
 * field ( SqlTree.java )
 *
 * 2024 Changed to maven and version 5 addedd a SQL Monitor for the internal
 * Derby DB, can be used stand alone for others addedd an own Backgroundimage
 * and color per Database and server and system.user printing the server and the
 * DB on the backgroundimage
 *
 * March 2024 Changed the CSV export to use apache.commons.csv
 *
 * Create a package with this command out of the target-directory: jpackage
 * --input . --main-jar SqlAdmin-jar-with-dependencies.jar --name SqlAdmin
 * --main-class sql.fredy.admin.Admin --app-version 5.2 --win-dir-chooser
 * --win-menu --win-shortcut --install-dir SqlAdmin --type msi --copyright
 * "Fredy Fischer" --description "JDBC based DB Administration"
 *
 */
import sql.fredy.ui.DBTreeView;
import sql.fredy.ui.FileDrop;
import sql.fredy.ui.CentredBackgroundBorder;
import sql.fredy.ui.ColumnLayout;
import sql.fredy.ui.StackTracer;
import sql.fredy.ui.ChooseColor;
import sql.fredy.ui.ReportViewer;
import sql.fredy.ui.CharsetSelector;
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.DataBaseLogin;
import sql.fredy.ui.TableCopyGUI;
import sql.fredy.ui.XMLImportGUI;
import sql.fredy.ui.LoadImage;
import sql.fredy.sqltools.DropTable;
import sql.fredy.sqltools.sqlTable;
import sql.fredy.sqltools.DataExportGui;
import sql.fredy.sqltools.Qbe;
import sql.fredy.sqltools.CreateTable;
import sql.fredy.sqltools.SqlMonitor;
import sql.fredy.sqltools.CreateTable2;
import sql.fredy.sqltools.AutoForm;
import sql.fredy.sqltools.ChangeTable;
import sql.fredy.sqltools.CreateIndex;
import sql.fredy.share.Browser;
import sql.fredy.metadata.DbInfo;
import sql.fredy.metadata.FindPatternTable;
import sql.fredy.metadata.FindPattern;
import sql.fredy.metadata.TableMetaData;
import sql.fredy.io.ImageScaler;
import sql.fredy.io.MakeVersion;
import sql.fredy.generate.GenerateTool;
import sql.fredy.datadrill.CreateDemoEnv;
import sql.fredy.datadrill.SelectionGui;
import sql.fredy.tools.Encryption;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.border.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import sql.fredy.connection.DataSource;
import sql.fredy.infodb.DBconnectionPool;
import sql.fredy.infodb.DBquery;
import sql.fredy.infodb.DBserver;
import sql.fredy.infodb.DBverify;
import sql.fredy.io.MyFileFilter;
import sql.fredy.sqltools.CSVimport;
import sql.fredy.sqltools.ExcelImport;
import sql.fredy.sqltools.TypeMapping;
import sql.fredy.tools.JdbcTypeMapping;
import sql.fredy.tools.MemoryCounter;
import sql.fredy.tools.ReadJdbcTypeMapping;
import sql.fredy.ui.DataBaseExportGui;
import sql.fredy.ui.JdbcTypeMappingGui;
import sql.fredy.ui.SaveDefinitionGUI;

//public class Admin extends JFrame implements ActionListener, MouseListener {
public class Admin extends JFrame implements ActionListener {

    /**
     * @return the adminPosX
     */
    public int getAdminPosX() {
        if (adminPosX < 1) {
            return 1;
        }
        return adminPosX;
    }

    /**
     * @param adminPosX the adminPosX to set
     */
    public void setAdminPosX(int adminPosX) {
        this.adminPosX = adminPosX;
    }

    /**
     * @return the adminPosY
     */
    public int getAdminPosY() {
        if (adminPosY < 1) {
            return 1;
        }
        return adminPosY;
    }

    /**
     * @param adminPosY the adminPosY to set
     */
    public void setAdminPosY(int adminPosY) {
        this.adminPosY = adminPosY;
    }

    private int waitForDerby = 500;

    /**
     * @return the defaultColor
     */
    public Color getDefaultColor() {
        String userName = System.getProperty("user.name");
        String serverName = getLogOn().getHost();
        String databaseName = getLogOn().getDatabase();

        Connection connection = null;
        PreparedStatement reader = null;
        ResultSet rs = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            reader = connection.prepareStatement("select BACKGROUNDCOLOR from  APP.BACKGROUNDIMAGE  where userName = ? and serverName = ? and databaseName = ?");

            reader.setString(1, userName);
            reader.setString(2, serverName);
            reader.setString(3, databaseName);
            rs = reader.executeQuery();

            if (rs.next()) {
                defaultColor = new Color(rs.getInt("BACKGROUNDCOLOR"));
            }

        } catch (SQLException | IOException | PropertyVetoException sqlex) {
            logger.log(Level.WARNING, "Can not read background.  Exception: {0}", sqlex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (SQLException e) {
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }

        }

        return defaultColor;
    }

    /**
     * @param defaultColor the defaultColor to set
     */
    public void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;

        lc.setOpaque(true);
        lc.setBackground(defaultColor);
        lc.updateUI();
        lc.update(lc.getGraphics());

    }

    private static final String ADMIN_VERSION = "Version 5.2.9 2025-03-20";
    LoadImage loadImage = new LoadImage();

    /**
     * It's time for a new version. Because of these lots of changing in the
     * SQL-Monitor
     *
     * @return the actual version
     */
    public String getVersion() {
        return ADMIN_VERSION;
    }

    private boolean encapsulateLogger = true;

    private static Logger logger = null; //Logger.getLogger(Admin.class.getName());
    private sql.fredy.ui.SplashScreen Splash;
    private JLabel statusLine;
    ImageIcon genImgIcon;
    JPopupMenu popup;
    public JLayeredPane lc;

    JList tableList;
    private String newdb;
    private Properties prop;
    private Qbe qbePanel;
    private AutoForm formPanel;
    private JCheckBoxMenuItem m30;
    private Color defaultColor;
    private ChooseColor cg;
    private JMenuBar menubar;
    private JInternalFrame msgFrame, formFrame, aboutFrame, colorFrame, licenseFrame, payFrame;
    public JCheckBox[] userChoice;
    public JTextField hostHost, hostDB, userHost, userDb, userUser, dbHost, dbUser, dbDb, User, propsHost, propsDB, propsmySQL;
    public JTextArea dbInfo, tableInfo, msgBoard;
    // public JPasswordField password,userPassword;
    public JTextField password, userPassword;
    private CreateTable crTa;
    private static DataBaseLogin logOn;
    private boolean connectionEstablished = false;
    private StackTracer stackTrace;
    //private MsgBoard mb;
    protected Action dbInfoAction;
    protected Action generateCodeAction;
    protected Action exitAction;
    protected Action qbeAction;
    protected Action formAction;
    protected Action sqlMonitorAction;
    protected Action dropTableAction;
    protected Action createTableAction;
    protected Action createIndexAction;
    protected Action createUserAction;
    protected Action createDBAction;
    protected Action dropDBAction;
    protected Action backgroundAction;
    protected Action execPrgAction;
    protected Action aboutAction;
    private sql.fredy.tools.SimpleLogServer sls = null;
    int mouseX;

    private String backGroundImageFile = null;
    private BufferedImage backGroundImage = null;
    private boolean localExecution = true;

    // we do a simple encryption of the password
    Encryption encryption = new Encryption();
    int encryptionOffset = 31;

    /**
     * Get the value of mouseX.
     *
     * @return Value of mouseX.
     */
    public int getMouseX() {
        if (mouseX < 1) {
            mouseX = 1;
        }
        return mouseX;
    }

    /**
     * Set the value of mouseX.
     *
     * @param v Value to assign to mouseX.
     */
    public void setMouseX(int v) {
        this.mouseX = v;
    }
    int mouseY;

    /**
     * Get the value of mouseY.
     *
     * @return Value of mouseY.
     */
    public int getMouseY() {

        if (mouseY < 1) {
            mouseY = 1;
        }
        return mouseY;
    }

    /**
     * Set the value of mouseY.
     *
     * @param v Value to assign to mouseY.
     */
    public void setMouseY(int v) {
        this.mouseY = v;
    }
    /**
     * Get the value of .
     *
     * @return Value of .
     */
    private String lnF = "default";
    private sql.fredy.ui.MySQLconnections dash = null;

    private void setLnF(String v) {
        lnF = v;
    }

    private String getLnF() {
        return lnF;
    }

    private String getRDBMS() {
        DbInfo dbi = new DbInfo();
        return dbi.getProductName();

    }

    private void stackTracer(Exception e) {

        stackTrace.setExcpt(e);
        stackTrace.setVisible(true);

    }

    public static void main(String args[]) {
        String imgDir = "";
        String workDir = "";
        try {
            imgDir = System.getProperty("admin.image");

            if (imgDir == null) {
                imgDir = Admin.class.getResource("..") + "images";
            }
            if (imgDir.startsWith("nullimages")) {
                imgDir = "the jar-file";
            }

            workDir = System.getProperty("admin.work");
            if (workDir == null) {
                workDir = System.getProperty("user.home");
            }

        } catch (Exception exc) {
            //
        }

        // Check if this working directory exists
        Path workingPath = Paths.get(workDir);
        if (Files.exists(workingPath) && Files.isDirectory(workingPath)) {

        } else {
            String msg = """
                         The workdirectory was not found.
                         I'm going to use the standard home directory.
                         Please set the property 'admin.work' correctly.
                         """;
            //JOptionPane.showMessageDialog(null, msg);
            JOptionPane optionPane = new JOptionPane(msg, JOptionPane.ERROR_MESSAGE);
            JDialog rowsDialog = optionPane.createDialog(workDir + " not found");
            rowsDialog.setLocation(MouseInfo.getPointerInfo().getLocation());
            rowsDialog.setVisible(true);
        }

        System.out.println(
                "\nThis is Fredy's admin-tool for SQL-Databases"
                + "\n--------------------------------------------\n"
                + "Admin " + ADMIN_VERSION + " is free Software\n\n"
                + "This is " + System.getProperty("os.name") + "\n\n"
                + "Systemproperties (to add with java -D<option> -D<option> sql.fredy.admin.Admin)\n"
                + "----------------\n"
                + "Generic Properties:\n"
                + "admin.work=<directory> Default: " + System.getProperty("user.home") + "\n"
                + "           this is the location of the config-files (admin.props, t_connect.props)\n"
                + "           I'm using: " + workDir + "\n\n"
                + "admin.image=<directory or URL> Default: sql.fredy.images \n"
                + "           this is the location where admin fetches the images from\n"
                + "           I'm using: " + imgDir + "\n\n"
                + "\n");

        if ((args.length > 0) && (args[0].equalsIgnoreCase("-h"))) {
            System.out.println("""
                               
                               Logging Properties: (Admin uses java.util.logging-API)
                               admin.logging.extended=YES activates Admin-Logging via SimpleLogServer
                                                          (your System needs to support TCP/IP)
                                                          only for Admin-Main Logs
                               admin.logging.host=<hostname or IP-Address> Default: localhost
                               admin.logging.port=<PortNumber>  Default: 5237
                               admin.logging.level=<Log-Level>  Default: INFO
                                                                Allowed: ALL, CONFIG, FINE, FINEST, FINER, INFO, OFF, SEVERE, WARNING
                               
                               sql.fredy.admin.encapsulate.logger=n to print logging messages to standardout and not to the admin-console,
                                                                    if this is not set all messages are redirected to the console window.
                               
                               sql.fredy.sqltool.maxcolumnwidth="500" to set the max columnwidth within the query result. Default 750                                
                               sql.fredy.admin.internaldb.upgrade=n the internal DB is upgraded automatically, if you do not want to do this, set this switch
                               sql.fredy.admin.internaldb=y to activate the view to the internal derby-DB
                               """);
        }

        javax.swing.SwingUtilities.invokeLater(() -> {
            Admin a = new Admin();
        });

        //Admin a = new Admin();
    }

    private void initLogging() {
        logger = Logger.getLogger("sql.fredy.admin");

    }

    /*
     This is the Derby-Server to store internal Information
     */
    private DBserver derby;

    private ImageScaler scaler;
    sql.fredy.tools.Console console;

    private int adminPosX = 1, adminPosY = 1;

    private static HashMap<String, JdbcTypeMapping> jdbctypemapping = null;

    public Admin() {

        //super("Fredy's SQL Admintool ");
        super("SQL Admintool ");

        this.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                //
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                setAdminPosX(e.getComponent().getX());
                setAdminPosY(e.getComponent().getY());
                //System.out.println(getAdminPosX() + " / " + getAdminPosY());
            }

            @Override
            public void componentShown(ComponentEvent e) {
                //
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                //
            }

        });

        // for debug purpose, we can set this switch to use standardout instead of the consolewindow to print messages
        setEncapsulateLogger(true);
        if ((System.getenv("sql.fredy.admin.encapsulate.logger") != null) && (System.getenv("sql.fredy.admin.encapsulate.logger").equalsIgnoreCase("n"))) {
            setEncapsulateLogger(false);
        }
        if ((System.getProperty("sql.fredy.admin.encapsulate.logger") != null) && (System.getProperty("sql.fredy.admin.encapsulate.logger").equalsIgnoreCase("n"))) {
            setEncapsulateLogger(false);
        }
        if (isEncapsulateLogger()) {
            console = new sql.fredy.tools.Console();
        }

        scaler = new ImageScaler();

        /**
         * The Logger is based onto Standard LogManager. See
         * jre.lib.logging.properties I wrote a Simple Log Server to use
         * SocketHandler to log zentralized.
         *
         * The default logger I use for all my stuff is: sql.fredy.admin
         *
         *
         */
        initLogging();

        internalDBthings();

        // init the StackTracer
        stackTrace = new StackTracer(this, "Exception", true);
        stackTrace.cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stackTrace.setVisible(false);
            }
        });

        // handle Window-Events
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent evt) {
                goodbye();
            }

            @Override
            public void windowActivated(WindowEvent evt) {
                ;
            }

            @Override
            public void windowDeactivated(WindowEvent evt) {
                ;
            }

            @Override
            public void windowIconified(WindowEvent evt) {
                // mb.frame.setState(Frame.ICONIFIED);
            }

            @Override
            public void windowDeiconified(WindowEvent evt) {
                ;
            }

            @Override
            public void windowOpened(WindowEvent evt) {
                ;
            }

            @Override
            public void windowClosed(WindowEvent evt) {
                goodbye();
            }
        });

        // setting IconImage
        try {
            this.setIconImage(loadImage.getImage("icon.gif").getImage());
        } catch (Exception whatException) {
            logger.log(Level.INFO, "Error loading Image for Frame. Exception: {0}", whatException.getMessage());
            whatException.initCause(new Throwable("Error loading Image for Frame"));
            //stackTracer(whatException);
            //System.exit(0);
        }

        // resize listener
        this.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                try {
                    if (getBackGroundImage() != null) {

                        /*
                    lc.setBorder(new CentredBackgroundBorder(scaler.scaleImage(getBackGroundImage(), e.getComponent().getSize())));
                    lc.updateUI();
                    lc.update(lc.getGraphics());
                         */
                        Component[] c = lc.getComponents();
                        for (int i = 0; i < c.length; i++) {

                            if ((c[i].getName() != null) && (c[i].getName().equalsIgnoreCase("background"))) {
                                lc.remove(c[i]);
                                break;
                            }
                        }
                        //ImageIcon icon = new ImageIcon(scaler.scaleImage(getBackGroundImage(), e.getComponent().getSize()));
                        ImageIcon icon = new ImageIcon(scaler.scaleImage(getBackGroundImage(), lc.getSize()));
                        JLabel label = new JLabel((Icon) icon, JLabel.CENTER);
                        label.setName("background");
                        label.setBounds(0, 0, lc.getSize().width, lc.getSize().height);
                        lc.add(label, Integer.MIN_VALUE);
                        // lc.update(lc.getGraphics());
                    }
                } catch (Exception exception) {

                }
            }

            @Override

            public void componentMoved(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void componentShown(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
        );

        // Start the Internal Derby Server aon Port 60606
        // display Splash-Screen
        // splash();
        propertyChecks();

        genImgIcon = loadImage.getImage("generated.gif");

        lc = new JDesktopPane();

        // file drops
        fileDrops();

        lc.add(login(), JLayeredPane.PALETTE_LAYER);

        JScrollPane mc = new JScrollPane(lc);
        //mc.getViewport().add(lc);

        this.getContentPane()
                .setLayout(new BorderLayout());

        this.getContentPane()
                .add(mc, BorderLayout.CENTER);

        this.getContentPane()
                .add(statusPanel(), BorderLayout.SOUTH);

        int width = Integer.parseInt(prop.getProperty("width"));
        int height = Integer.parseInt(prop.getProperty("heigth"));

        if (width
                < 250) {
            width = 250;
        }
        if (height
                < 150) {
            height = 150;
        }

        this.setSize(width, height);

        // Splash.close();
        lnfchange(prop.getProperty("Look-n-Feel", "default"));

        //mb.frame.setVisible(true);       
        lc.setOpaque(true);
        setDefaultBackground();
        this.setVisible(true);

    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        if (command.equals("exit")) {
            goodbye();
        }
    }

    private void internalDBthings() {
        // starting up the internal Derby-Server
        derby = new DBserver();
        derby.startUp();

        // per default, we try max 500 times to connect to derby, but this can be overwritten by the 
        // vaiable sql.fredy.admin.waitforderby
        String wfd;
        try {
            wfd = System.getProperty("sql.fredy.admin.waitforderby");
        } catch (Exception e) {
            wfd = null;
        }
        if (wfd == null) {
            try {
                wfd = System.getenv("sql.fredy.admin.waitforderby");
            } catch (Exception e) {
                wfd = null;
            }
        }
        if (wfd != null) {
            try {
                waitForDerby = Integer.parseInt(wfd);
            } catch (NumberFormatException nfe) {

            }
        }

        // verify if the server is running:
        boolean derbyRunning = false;
        try {
            for (int i = 0; i < waitForDerby; ++i) {
                if (derby.serverPing()) {
                    derbyRunning = true;
                    logger.log(Level.INFO, "Derby Server is running");
                    break;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException iex) {

        }
        if (!derbyRunning) {
            JOptionPane.showMessageDialog(null, "Internal Derby Server is not running, can not continue", "SQL Admin Warning", JOptionPane.WARNING_MESSAGE);
            System.exit(1);
        } else {
            DBverify dbv = new DBverify();
        }

    }

    public void processMouseEvent(MouseEvent e) {

        if ((popup != null) && e.isPopupTrigger()) {
            popup.show(this, e.getX(), e.getY());
            setMouseX(e.getX());
            setMouseY(e.getY());
        }

        // how many buttons does the mouse have?
        int leftButton = e.BUTTON3;
        if (MouseInfo.getNumberOfButtons() < 3) {
            leftButton = e.BUTTON2;
        }

        // if clicked once  the right , it adds a SQL-Statement to see this table to the clipboard
        if (e.getButton() == leftButton) {

            if (e.getClickCount() == 1) {
                popup.show(this, e.getX(), e.getY());
                setMouseX(e.getX());
                setMouseY(e.getY());
            }
        }

    }

    public void mouseClicked(MouseEvent e) {

        /*
         if ((popup != null) && e.isPopupTrigger()) {
         popup.show(this, e.getX(), e.getY());
         setMouseX(e.getX());
         setMouseY(e.getY());
         } else {
         super.processMouseEvent(e);
         }
         */
        // how many buttons does the mouse have?
        int leftButton = e.BUTTON3;
        if (MouseInfo.getNumberOfButtons() < 3) {
            leftButton = e.BUTTON2;
        }

        if (e.getButton() == leftButton) {
            if (e.getClickCount() == 1) {
                popup.show(this, e.getX(), e.getY());
                setMouseX(e.getX());
                setMouseY(e.getY());
            }
        } else {
            super.processMouseEvent(e);
        }

    }

    public void mousePressed(MouseEvent e) {
        ;
    }

    public void mouseReleased(MouseEvent e) {
        ;
    }

    public void mouseEntered(MouseEvent e) {
        ;
    }

    public void mouseExited(MouseEvent e) {
        ;
    }

    private void generateGraph() {

        final JTextField dotFile = new JTextField(20);
        dotFile.setToolTipText("Path might be in the Clipboard");
        final JTextField imageFile = new JTextField(20);
        final JTextField pathToGraphviz = new JTextField(20);
        pathToGraphviz.setText(prop.getProperty("pathtographviz"));

        JButton lDotFile = new JButton("...");
        JButton lImgFile = new JButton("...");
        JButton lGrPath = new JButton("...");

        final String[] fileFormats = {"gif", "jpg", "png", "bmp", "pdf", "canon", "cmap", "cmapx", "cmapx_np", "dot", "emf", "emfplus", "eps", "fig", "gd", "gd2", "gv", "imap", "imap_np", "ismap", "jpe", "jpeg", "metafile", "pic", "plain", "plain-ext", "pov", "ps", "ps2", "svg", "svgz", "tif", "tiff", "tk", "vml", "vmlz", "vrml", "wbmp", "xdot", "xdot1"};
        String[] layouter = {"dot", "neato", "circo", "sfdp", "fdp", "twopi"};
        final JComboBox fileFormat = new JComboBox(fileFormats);
        final JComboBox layout = new JComboBox(layouter);

        lDotFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                chooser.setFileFilter(new sql.fredy.io.MyFileFilter("dot"));
                chooser.setDialogTitle("Select DOT File");

                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //setFile(chooser.getSelectedFile());
                    dotFile.setText(chooser.getSelectedFile().getPath());
                }
            }
        });

        lImgFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                chooser.setDialogTitle("Select image File");

                //chooser.setFileFilter(new sql.fredy.io.MyFileFilter(fileFormats));            
                chooser.setFileFilter(new sql.fredy.io.MyFileFilter((String) fileFormat.getSelectedItem()));

                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    imageFile.setText(chooser.getSelectedFile().getPath());
                }
            }
        });

        lGrPath.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                chooser.setDialogTitle("Select path to GraphViz-binaries");

                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //setFile(chooser.getSelectedFile());
                    pathToGraphviz.setText(chooser.getSelectedFile().getPath());
                    prop.setProperty("pathtographviz", pathToGraphviz.getText());
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("DOT-File"), gbc);
        gbc.gridx = 1;
        panel.add(dotFile, gbc);
        gbc.gridx = 2;
        panel.add(lDotFile, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Image File"), gbc);
        gbc.gridx = 1;
        panel.add(imageFile, gbc);
        gbc.gridx = 2;
        panel.add(lImgFile, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Destination File Format"), gbc);
        gbc.gridx = 1;

        panel.add(fileFormat, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Path to Graphviz binaries"), gbc);
        gbc.gridx = 1;
        panel.add(pathToGraphviz, gbc);
        gbc.gridx = 2;
        panel.add(lGrPath, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Layout Engine"), gbc);
        gbc.gridx = 1;

        panel.add(layout, gbc);

        final ImageButton exec = new ImageButton(null, "palette.gif", "Create Graphik");
        final ImageButton cancel = new ImageButton(null, "exit.gif", "Close");

        exec.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                prop.setProperty("pathtographviz", pathToGraphviz.getText());

                MakeVersion mkv = new MakeVersion(imageFile.getText());

                String cmd = pathToGraphviz.getText() + File.separator + (String) layout.getSelectedItem() + " -T" + (String) fileFormat.getSelectedItem() + " -o " + imageFile.getText() + " " + dotFile.getText();

                runCMD(cmd);

                String graphType = (String) fileFormat.getSelectedItem();
                if (("jpg".equals(graphType))
                        || ("jpeg".equals(graphType))
                        || ("gif".equals(graphType))
                        || ("png".equals(graphType))
                        || ("tiff".equals(graphType))
                        || ("tif".equals(graphType))) {
                    ImageIcon imcn = new ImageIcon(imageFile.getText());
                    JLabel dp = new JLabel();
                    dp.setIcon(imcn);
                    JScrollPane scrp = new JScrollPane(dp);

                    JPanel dpanel = new JPanel();
                    dpanel.setLayout(new BorderLayout());
                    dpanel.add(BorderLayout.CENTER, scrp);

                    Dimension dim = lc.getSize();
                    int width = dpanel.getPreferredSize().width;
                    int height = dpanel.getPreferredSize().height;

                    if (dpanel.getPreferredSize().width > dim.width) {
                        width = dim.width - 20;
                    }
                    if (dpanel.getPreferredSize().height > dim.height) {
                        height = dim.height - 20;
                    }

                    newFrame(dpanel, imageFile.getText(), true, true, true, true, getMouseX(), getMouseY(),
                            width, height + 20, null);

                }

            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());
        buttonPanel.add(exec);
        buttonPanel.add(cancel);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 3;
        panel.add(buttonPanel, gbc);

        newFrame(panel, "Create Graphic out of DOT", true, true, true, true, getMouseX(), getMouseY(),
                panel.getPreferredSize().width, panel.getPreferredSize().height + 20, cancel);

    }

    private void fileDrops() {
        new FileDrop(lc, new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {

                // we loop through each file
                //logger.log(Level.INFO,files.length + " Files dropped");
                for (int i = 0; i < files.length; i++) {
                    String extension = getFileExtension(files[i]);

                    if (isExcel(extension)) {
                        loadExcel(files[i]);
                    }

                    if (extension.equalsIgnoreCase("csv")) {
                        loadCsvFile(files[i]);
                    }

                    if (isPicture(extension)) {
                        setBackgroundImage(files[i]);
                    }
                    if (isText(extension)) {
                        select(files[i]);
                    }
                }
            }
        });

    }

    private String getFileExtension(File file) {
        String name = file.getName();
        String[] parts = name.split(Pattern.quote("."));
        return parts[parts.length - 1];
    }

    private boolean isPicture(String ext) {
        if (ext.equalsIgnoreCase("jpg")) {
            return true;
        }
        if (ext.equalsIgnoreCase("jpeg")) {
            return true;
        }
        if (ext.equalsIgnoreCase("gif")) {
            return true;
        }
        if (ext.equalsIgnoreCase("tif")) {
            return true;
        }
        if (ext.equalsIgnoreCase("tiff")) {
            return true;
        }
        if (ext.equalsIgnoreCase("png")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isText(String ext) {
        if (ext.equalsIgnoreCase("sql")) {
            return true;
        }
        if (ext.equalsIgnoreCase("txt")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isExcel(String ext) {
        if (ext.equalsIgnoreCase("xlsx")) {
            return true;
        }
        if (ext.equalsIgnoreCase("xls")) {
            return true;
        } else {
            return false;
        }
    }

    private void excelLoad() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        MyFileFilter filter = new MyFileFilter(new String[]{"xls", "xlsx"}, "Excel files");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Select Excel File");

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            loadExcel(chooser.getSelectedFile());
        }
    }

    private void csvLoad() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        MyFileFilter filter = new MyFileFilter(new String[]{"csv", "txt"}, "CSV files");
        chooser.setFileFilter(filter);
        chooser.setDialogTitle("Select CSV File");

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            loadCsvFile(chooser.getSelectedFile());
        }
    }

    private void definitionExport() {
        JDialog saveDefDialog = new JDialog(this, "Save Definitions", true);
        saveDefDialog.getContentPane().setLayout(new BorderLayout());
        SaveDefinitionGUI sGui = new SaveDefinitionGUI();
        sGui.exit.addActionListener((ActionEvent e) -> {
            saveDefDialog.dispose();
        });
        sGui.exitButton.addActionListener((ActionEvent e) -> {
            saveDefDialog.dispose();
        });
        saveDefDialog.getContentPane().add(sGui, BorderLayout.CENTER);
        saveDefDialog.pack();
        saveDefDialog.setLocation(MouseInfo.getPointerInfo().getLocation());
        saveDefDialog.setVisible(true);
    }

    private void loadExcel(File file) {
        JDialog exceldialog = new JDialog(this, "Load " + file.getAbsolutePath(), true);
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder());
        panel.setLayout(new FlowLayout());
        JTextField tablename = new JTextField(30);

        JCheckBox hasHeader = new JCheckBox("first line is header", true);
        JCheckBox fixColumns = new JCheckBox("fix column names", true);
        JCheckBox dTable = new JCheckBox("drop table if exists", false);

        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new GridBagLayout());
        boxPanel.setBorder(new EtchedBorder());
        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        boxPanel.add(hasHeader, gbc);
        gbc.gridy = 1;
        boxPanel.add(fixColumns, gbc);
        gbc.gridy = 2;
        boxPanel.add(dTable, gbc);

        ImageButton load = new ImageButton(null, "load.gif", "load this file into the table");
        ImageButton exit = new ImageButton(null, "exit.gif", "do not load");

        load.addActionListener((ActionEvent e) -> {
            ExcelImport excelImport = new ExcelImport(file, tablename.getText(), hasHeader.isSelected(), fixColumns.isSelected(), dTable.isSelected());
            exceldialog.dispose();
        });

        tablename.addActionListener((ActionEvent e) -> {
            ExcelImport excelImport = new ExcelImport(file, tablename.getText(), hasHeader.isSelected(), fixColumns.isSelected(), dTable.isSelected());
            exceldialog.dispose();
        });

        exit.addActionListener((ActionEvent e) -> {
            exceldialog.dispose();
        });

        panel.add(new JLabel("Destination table"));
        panel.add(tablename);
        panel.add(boxPanel);
        panel.add(load);
        panel.add(exit);

        exceldialog.add(panel);
        exceldialog.pack();
        exceldialog.setLocation(MouseInfo.getPointerInfo().getLocation());
        exceldialog.setVisible(true);

    }

    private void loadCsvFile(File file) {
        JDialog csvdialog = new JDialog(this, "Load " + file.getAbsolutePath(), true);
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder());
        panel.setLayout(new FlowLayout());
        JTextField tablename = new JTextField(30);
        JTextField finisherprocedure = new JTextField(30);

        JComboBox filetype = new JComboBox(new String[]{"Default", "Excel", "Informix", "Informix_CSV", "Mongo_CSV", "Mongo_TSV", "MySQL", "Oracle", "PostgresSQL_CSV", "PostgresSQL_TEXT", "RFC4180", "TDF"});

        JCheckBox hasHeader = new JCheckBox("first line is header", true);
        JCheckBox cleanTable = new JCheckBox("clean destination table", true);
        JCheckBox dTable = new JCheckBox("drop table if exists", false);
        JCheckBox createTable = new JCheckBox("create table if not exists", false);

        JPanel boxPanel = new JPanel();
        boxPanel.setLayout(new GridBagLayout());
        boxPanel.setBorder(new EtchedBorder());
        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        boxPanel.add(hasHeader, gbc);
        gbc.gridy = 1;
        boxPanel.add(cleanTable, gbc);
        gbc.gridy = 2;
        boxPanel.add(dTable, gbc);
        gbc.gridy = 3;
        boxPanel.add(createTable, gbc);

        ImageButton load = new ImageButton(null, "load.gif", "load this file into the table");
        ImageButton exit = new ImageButton(null, "exit.gif", "do not load");

        load.addActionListener((ActionEvent e) -> {
            CSVimport csvLoader = new CSVimport();

            csvLoader.setFileName(file.getAbsolutePath());

            csvLoader.setCleanTable(cleanTable.isSelected());
            csvLoader.setCreateTable(createTable.isSelected());
            csvLoader.setDropTable(dTable.isSelected());
            csvLoader.setHasHeader(hasHeader.isSelected());
            if (finisherprocedure.getText().isBlank()) {
                csvLoader.setRunFinisher(false);
                csvLoader.setFinisherProcedure(null);
            } else {
                csvLoader.setRunFinisher(true);
                csvLoader.setFinisherProcedure(finisherprocedure.getText());
            }

            if (!tablename.getText().isBlank()) {
                csvLoader.setTableName(tablename.getText());
                long l = csvLoader.readCsvFile();
                JOptionPane.showMessageDialog(null, "Loaded " + String.format("%,d", hasHeader.isSelected() ? l - 1 : l) + " Rows into " + tablename.getText(), "Message", JOptionPane.INFORMATION_MESSAGE);
            }

            csvdialog.dispose();
        });

        tablename.addActionListener((ActionEvent e) -> {
            CSVimport csvLoader = new CSVimport();

            csvLoader.setFileName(file.getAbsolutePath());

            csvLoader.setCleanTable(cleanTable.isSelected());
            csvLoader.setCreateTable(createTable.isSelected());
            csvLoader.setDropTable(dTable.isSelected());
            csvLoader.setHasHeader(hasHeader.isSelected());
            if (finisherprocedure.getText().isBlank()) {
                csvLoader.setRunFinisher(false);
                csvLoader.setFinisherProcedure(null);
            } else {
                csvLoader.setRunFinisher(true);
                csvLoader.setFinisherProcedure(finisherprocedure.getText());
            }

            csvLoader.setFieldType((String) filetype.getSelectedItem());

            if (!tablename.getText().isBlank()) {
                csvLoader.setTableName(tablename.getText());
                long l = csvLoader.readCsvFile();
                JOptionPane.showMessageDialog(null, "Loaded " + String.format("%,d", hasHeader.isSelected() ? l - 1 : l) + " Rows into " + tablename.getText(), "Message", JOptionPane.INFORMATION_MESSAGE);
            }

            csvdialog.dispose();
            csvdialog.dispose();
        });

        exit.addActionListener((ActionEvent e) -> {
            csvdialog.dispose();
        });

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new GridBagLayout());
        textPanel.setBorder(new EtchedBorder());

        insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        textPanel.add(new JLabel("Destination table"), gbc);
        gbc.gridy = 1;
        textPanel.add(tablename, gbc);
        gbc.gridy = 2;
        textPanel.add(new JLabel("Finisher procedure"), gbc);
        gbc.gridy = 3;
        textPanel.add(finisherprocedure, gbc);
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        textPanel.add(new JLabel("CSV file type"), gbc);
        gbc.gridy = 4;
        gbc.gridx = 1;
        textPanel.add(filetype, gbc);

        panel.add(textPanel);
        panel.add(boxPanel);
        panel.add(load);
        panel.add(exit);

        csvdialog.add(panel);
        csvdialog.pack();
        csvdialog.setLocation(MouseInfo.getPointerInfo().getLocation());
        csvdialog.setVisible(true);

    }

    private void selectBackgroundImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Select Background Picture");
        chooser.setFileFilter(new sql.fredy.io.MyFileFilter(new String[]{"jpg", "JPG", "gif", "GIF", "PNG", "png", "tif", "TIF", "tiff", "TIFF", "jpeg", "JPEG"}, "Images"));

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            setBackgroundImage(chooser.getSelectedFile());
        }
    }

    private void removeBackgroundImage() {
        //logger.log(Level.INFO,"removing background");
        Component[] c = lc.getComponents();
        for (int i = 0; i < c.length; i++) {

            if ((c[i].getName() != null) && (c[i].getName().equalsIgnoreCase("background"))) {
                lc.remove(c[i]);
                break;
            }
        }
        lc.setBorder(new EmptyBorder(0, 0, 0, 0));
        lc.updateUI();
        lc.update(lc.getGraphics());
        setBackGroundImageFile(null);
    }

    private void setBackgroundImage(File file) {
        try {

            backGroundImage = ImageIO.read(file);
            backGroundImage = scaler.scaleImage(backGroundImage, lc.getSize()); // this.getSize());
            setBackGroundImageFile(file.getAbsolutePath());

            // we add some Information to the Image
            Graphics g = backGroundImage.getGraphics();
            g.setFont(g.getFont().deriveFont(30f));
            g.setColor(Color.WHITE);
            g.drawString(getLogOn().getHost() + " / " + getLogOn().getDatabase(), 50, 50);
            g.dispose();

            /*
            CentredBackgroundBorder cbb = new CentredBackgroundBorder(backGroundImage);
            lc.setOpaque(true);
            //lc.setBorder(new EmptyBorder(0, 0, 0, 0));
            lc.setBorder(cbb);
            lc.update(lc.getGraphics());
             */
            Component[] c = lc.getComponents();
            for (int i = 0; i < c.length; i++) {

                if ((c[i].getName() != null) && (c[i].getName().equalsIgnoreCase("background"))) {
                    lc.remove(c[i]);
                    break;
                }
            }
            ImageIcon icon = new ImageIcon(backGroundImage);
            JLabel label = new JLabel((Icon) icon, JLabel.CENTER);
            label.setName("background");
            label.setBounds(0, 0, lc.getSize().width, lc.getSize().height);
            lc.add(label, Integer.MIN_VALUE);

        } catch (MalformedURLException ex) {
            logger.log(Level.INFO, "Malformed URL Exception: {0}", ex.getMessage());
        } catch (IOException iox) {
            logger.log(Level.INFO, "IO Exception: {0}", iox.getMessage());
        } catch (Exception exception) {
            logger.log(Level.INFO, "Generic Exception: {0} ", exception.getMessage());
        }
    }

    Properties lnfProps;

    private void propertyChecks() {
        // Load Properties
        prop = new Properties();
        lnfProps = new Properties();

        /**
         * this is the same dir as the class: Admin.class.getResourceAsStream
         * but we put the props-File in the user's home-dir if there is not -D
         * parameter for place given at start
         *
         */
        String admin_dir = "";
        try {

            admin_dir = System.getProperty("admin.work");
            if (admin_dir == null) {
                admin_dir = System.getProperty("user.home");
            }

            logger.log(Level.FINE, "Using properties file {0}{1}admin.props", new Object[]{admin_dir, File.separator});

            FileInputStream fip = new FileInputStream(admin_dir + File.separator + "admin.props");
            prop.load(fip);
            fip.close();
            if (prop.getProperty("JDBCdriver") == null) {
                setDefaultProperties();
            }

            /*
            try {
                setBackGroundImageFile(prop.getProperty("background", null));
            } catch (Exception e) {
                setBackGroundImageFile(null);
            }
             */
        } catch (Exception ioex) {

            logger.log(Level.WARNING, "cannot lod properties file admin.props: " + ioex.getMessage().toString());

            System.out.println("Exception while loading props:\n" + ioex + System.getProperty("user.name")
                    + "\ndb=none\nwidth=600\nheigth=400\nmysqladmin=/usr/local/mysql/bin/mysqladmin\nusePassword=yes\nJDBCdriver=ocom.mysql.jdbc.Driver\nJDBCurl=jdbc:mysql://\nDatabasePort=3306\nRDBMS=mysql");

            setDefaultProperties();
        }

        try {
            FileInputStream fipLnf = new FileInputStream(admin_dir + File.separator + "adminlnf.props");
            lnfProps.load(fipLnf);

        } catch (IOException ioex) {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                lnfProps.put(info.getName(), info.getClassName());
            }
        }

    }

    private void verifyProperty(String property, String defaultValue) {
        String value = "";
        try {
            value = prop.getProperty(property);
        } catch (Exception e) {
            prop.put(property, defaultValue);
        }
    }

    private void setDefaultProperties() {

        logger.log(Level.INFO, "Creating default Property-File");

        prop.put("host", "localhost");
        prop.put("user", System.getProperty("user.name"));
        prop.put("db", "");
        prop.put("width", "800");
        prop.put("heigth", "600");
        prop.put("mysqladmin", "/usr/local/mysql/bin/mysqladmin");
        prop.put("usePassword", "yes");
        prop.put("password", " ");
        prop.put("JDBCdriver", "");
        prop.put("JDBCurl", "jdbc:mysql://");
        prop.put("DatabasePort", "3306");
        prop.put("RDBMS", "mysql");
        prop.put("Look-n-Feel", "default");
        prop.put("schema", "%");
        prop.put("connections", "25");

        String directory = System.getProperty("admin.work");
        StringBuffer fileName = new StringBuffer();
        if (directory == null) {
            directory = System.getProperty("user.home");
        }

        directory = directory + File.separator + "sqladmin" + File.separator + "work" + File.separator + "props";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        prop.put("PropertiesFileLocation", directory);

        saveConnectProperties();

    }

    private void splash() {

        int sleepMillis = 200;

        Splash = new sql.fredy.ui.SplashScreen(loadImage.getImage("admin.gif"));
        try {

            Splash.showStatus("Welcome to Fredy's");
            Thread.sleep(sleepMillis);
            Splash.showStatus("Admin-Tool for SQL " + getVersion());
            Thread.sleep(sleepMillis);
            Splash.showStatus("Fredy Fischer");
            Thread.sleep(sleepMillis);
            Splash.showStatus("Hulmenweg 36");
            Thread.sleep(sleepMillis);
            Splash.showStatus("CH-8405 Winterthur");
            Thread.sleep(sleepMillis);
        } catch (Exception e) {
            e.initCause(new Throwable("something went wrong with the Splash-Screen"));
            stackTracer(e);
        }

    }

    private void setLogFile() {
        /*
        if (mb.file != null) {
            try {
                System.setOut(new PrintStream(new FileOutputStream(mb.file)));
                mb.poller();
            } catch (FileNotFoundException fne) {
                fne.initCause(new Throwable("FileNotFound Exception for LogFile"));
                stackTracer(fne);
            }
        }
         */
    }

    private void checkPropertyFilesLocationProperty() {
        String directory = System.getProperty("admin.work");
        StringBuffer fileName = new StringBuffer();
        if (directory == null) {
            directory = System.getProperty("user.home");
        }

        directory = directory + File.separator + "sqladmin" + File.separator + "work" + File.separator + "props";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }
        verifyProperty("PropertiesFileLocation", directory);
    }

    private JInternalFrame login() {

        // because I've added a new property to make it easier to load the login-Props-Files
        checkPropertyFilesLocationProperty();

        Container contentPane;

        final JInternalFrame log = new JInternalFrame("Login", true, false, true, true);
        contentPane = log.getContentPane();
        contentPane.setLayout(new FlowLayout());

        setLogOn(new DataBaseLogin());
        getLogOn().setPropertiesFileLocation(prop.getProperty("PropertiesFileLocation"));

        setLogOnValues();

        contentPane.add(getLogOn());

        getLogOn().lPassword.addActionListener((ActionEvent e) -> {
            // close the connections if new connection
            if (isConnectionEstablished()) {
                getLogOn().closeConnections();
            }

            if (getConnection() != null) {
                try {
                    prop.put("PropertiesFileLocation", getLogOn().getPropertiesFileLocation());
                    saveProperties();
                    setConnectionEstablished(true);
                    lc.add(pm());
                    normalMenu();
                    try {
                        log.setIcon(true);
                    } catch (java.beans.PropertyVetoException pve) {
                        message(pve.getMessage());
                        pve.initCause(new Throwable(pve.getMessage()));
                        stackTracer(pve);
                    }
                } catch (Exception ex) {
                    logger.log(Level.WARNING, "cannot proceed: {0}", ex.getMessage());
                    //ex.printStackTrace();
                }

            } else {
                JOptionPane.showMessageDialog(null, connectionError, "no connection to DB " + getLogOn().getDatabase(), JOptionPane.WARNING_MESSAGE);
            }
        });

        getLogOn().connect.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // close the connections if new connection
                if (isConnectionEstablished()) {
                    getLogOn().closeConnections();
                }
                if (getConnection() != null) {
                    try {

                        DBverify dbv = new DBverify();

                        // verifying Data Type Mappings
                        ReadJdbcTypeMapping rtm = new ReadJdbcTypeMapping(getProductName());
                        
                        TypeMapping tm = new TypeMapping(getProductName());
                        jdbctypemapping = tm.getMapping();

                        setDefaultBackground();
                        saveProperties();
                        setConnectionEstablished(true);
                        lc.add(pm());
                        normalMenu();

                        /*
                        // set BackgroundImage
                        String bgImage = null;
                        try {
                            bgImage = prop.getProperty("background", null);
                        } catch (Exception exception) {
                        }
                        setBgImg(bgImage);
                         */
                        try {
                            log.setIcon(true);
                        } catch (java.beans.PropertyVetoException pve) {
                            message(pve.getMessage());
                            pve.initCause(new Throwable(pve.getMessage()));
                            stackTracer(pve);
                        }
                    } catch (Exception ex) {
                        logger.log(Level.WARNING, "cannot proceed: {0}", ex.getMessage());
                        //ex.printStackTrace();
                    }
                } else {
                    connectionError = "";
                    try {
                        connectionError = DataSource.getInstance().getConnectionError();
                        DataSource.getInstance().close();

                    } catch (SQLException | PropertyVetoException | IOException ex) {
                        Logger.getLogger(Admin.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }

                    JOptionPane.showMessageDialog(null, "no connection to DB " + getLogOn().getDatabase(), "Message", JOptionPane.WARNING_MESSAGE);
                }
            }

        });

        getLogOn().cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String string1 = "Yes";
                String string2 = "No";
                Object[] options = {string1, string2};
                int n = JOptionPane.showOptionDialog(null,
                        "Do you really want to leave?",
                        "Quit Admin?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null, //don't use a custom Icon
                        options, //the titles of buttons
                        string2); //the title of the default button
                if (n == JOptionPane.YES_OPTION) {
                    getLogOn().closeConnections();
                    System.exit(0);
                }
            }
        });

        Dimension dim = new Dimension(getLogOn().getPreferredSize());
        log.pack();//setBounds(getMouseX(), getMouseY(), dim.width, dim.height+30);
        log.setVisible(true);
        return log;
    }

    private static String productName = null;

    private static String getProductName() {
        if (productName == null) {
            Connection con = null;
            try {
                con = DataSource.getInstance().getConnection();
                productName = DataSource.getInstance().getProductName();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
            } catch (PropertyVetoException ex) {
                logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING, "Exception while closing connection {0}", e.getMessage());
                    }
                }
            }
        }
        return productName;
    }

    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    String connectionError = "";

    public Connection getConnection() {
        Connection con = null;
        try {
            con = DataSource.getInstance().getConnection();
            productName = DataSource.getInstance().getProductName();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
            connectionError = ex.getLocalizedMessage();
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
            connectionError = ex.getLocalizedMessage();
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
            connectionError = ex.getLocalizedMessage();
        } finally {
            return con;
        }
    }

    private JInternalFrame users() {

        Container contentPane;

        JInternalFrame frame = new JInternalFrame("User", true, true, true, true);
        contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel panel1 = new JPanel();
        ColumnLayout layout1 = new ColumnLayout(5, 5, 10, 2);
        panel1.setLayout(layout1);
        JLabel label = new JLabel();
        label.setText("Host");
        panel1.add(label);

        JLabel label3 = new JLabel("Database");
        panel1.add(label3);

        JLabel label1 = new JLabel();
        label1.setText("User");
        panel1.add(label1);

        JLabel label2 = new JLabel();
        label2.setText("Password");
        panel1.add(label2);

        JPanel panel2 = new JPanel();
        ColumnLayout layout2 = new ColumnLayout(5, 5, 5, 0);
        panel2.setLayout(layout2);

        userHost = new JTextField(15);
        userHost.setText("%");
        userUser = new JTextField(15);
        userDb = new JTextField(15);
        //userPassword = new JPasswordField(null,15);
        userPassword = new JTextField(null, 15);

        panel2.add(userHost);
        panel2.add(userDb);
        panel2.add(userUser);
        panel2.add(userPassword);

        JLabel l = new JLabel(loadImage.getImage("mysql-06.gif"));
        panel2.add(l);

        Box box1 = Box.createVerticalBox();
        String[] choices = {"Select Priv", "Insert Priv", "Update Priv",
            "Delete Priv", "Create Priv", "Drop Priv",
            "Reload Priv", "Shutdown Priv", "Process Priv", "File Priv", "Grant_priv", "References_priv", "Index_priv", "Alter_priv"};
        String[] tips = {"Select Privilegs", "Insert Privilegs", "Update Privilegs",
            "Delete Privilegs", "Create Privilegs", "Drop Privilegs",
            "Reload Privilegs", "Shutdown Privilegs", "Process Privilegs", "File Privilegs", "Grant", "Ref", "Index", "Alter"};

        userChoice = new JCheckBox[choices.length];
        for (int i = 0; i < choices.length; i++) {
            userChoice[i] = new JCheckBox(choices[i]);
            userChoice[i].setToolTipText(tips[i]);
            box1.add(userChoice[i]);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        JButton list = new JButton("List");
        JButton insert = new JButton("Insert");
        JButton delete = new JButton("Delete");

        insert.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String pos = " ";
                Connection con = null;
                Statement stmt = null;
                try {

                    con = getConnection();
                    stmt = con.createStatement();

                    // first insert into user-Table of mysql
                    pos = "user";
                    int records = stmt.executeUpdate("insert into user "
                            + "values ('" + userHost.getText() + "', '" + userUser.getText() + "',PASSWORD('" + userPassword.getText() + "')"
                            + ", '" + checkPriv(userChoice[0])
                            + "', '" + checkPriv(userChoice[1])
                            + "', '" + checkPriv(userChoice[2])
                            + "', '" + checkPriv(userChoice[3])
                            + "', '" + checkPriv(userChoice[4])
                            + "', '" + checkPriv(userChoice[5])
                            + "', '" + checkPriv(userChoice[6])
                            + "', '" + checkPriv(userChoice[7])
                            + "', '" + checkPriv(userChoice[8])
                            + "', '" + checkPriv(userChoice[9])
                            + "', '" + checkPriv(userChoice[10])
                            + "', '" + checkPriv(userChoice[11])
                            + "', '" + checkPriv(userChoice[12])
                            + "', '" + checkPriv(userChoice[13]) + "')");
                    // then insert into DB
                    pos = "db";
                    int records1 = stmt.executeUpdate("insert into db "
                            + "values ('" + userHost.getText() + "','" + userDb.getText() + "','" + userUser.getText()
                            + "', '" + checkPriv(userChoice[0])
                            + "', '" + checkPriv(userChoice[1])
                            + "', '" + checkPriv(userChoice[2])
                            + "', '" + checkPriv(userChoice[3])
                            + "', '" + checkPriv(userChoice[4])
                            + "', '" + checkPriv(userChoice[5])
                            + "', '" + checkPriv(userChoice[6])
                            + "', '" + checkPriv(userChoice[7])
                            + "', '" + checkPriv(userChoice[8])
                            + "', '" + checkPriv(userChoice[9]) + "')");
                    //con.close();
                } catch (SQLException excpt) {
                    String ms = "affected: " + pos + "\n" + excpt.getMessage().toString();
                    message(ms);
                    excpt.initCause(new Throwable(ms));
                    stackTracer(excpt);
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException sqle) {
                            logger.log(Level.WARNING, "Exception while closing statement {0}", sqle.getMessage());
                        }
                    }
                    if (con != null) {
                        try {
                            con.close();
                        } catch (SQLException sqle) {
                            logger.log(Level.WARNING, "Exception while closing connection {0}", sqle.getMessage());
                        }
                    }
                }
            }
        });

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Connection con = null;
                Statement stmt = null;
                try {

                    con = getConnection();
                    stmt = con.createStatement();
                    // first delete from user-Table of mysql
                    int records = stmt.executeUpdate("delete from user where User = '" + userUser.getText() + "'");
                    // then delete from DB
                    int records1 = stmt.executeUpdate("delete from db where User = '" + userUser.getText() + "'");
                } catch (SQLException excpt) {
                    message(excpt.getMessage().toString());
                    excpt.initCause(new Throwable(excpt.getMessage().toString()));
                    stackTracer(excpt);
                } finally {
                    if (stmt != null) {
                        try {
                            stmt.close();
                        } catch (SQLException sqle) {
                            logger.log(Level.WARNING, "Exception while closing statement {0}", sqle.getMessage());
                        }
                    }
                    if (con != null) {
                        try {
                            con.close();
                        } catch (SQLException sqle) {
                            logger.log(Level.WARNING, "Exception while closing connection {0}", sqle.getMessage());
                        }
                    }
                }

            }
        });

        list.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sqlTable sqlt = new sqlTable("select * from user");
                Dimension dim = sqlt.getPreferredSize();
                newFrame((JPanel) sqlt, "User-List", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, null);

            }
        });

        buttonPanel.add(list);
        buttonPanel.add(insert);
        buttonPanel.add(delete);

        panel.add("West", panel1);
        panel.add("Center", panel2);
        panel.add("East", box1);
        panel.add("South", buttonPanel);

        JScrollPane mc = new JScrollPane();
        mc.getViewport().add(panel);

        contentPane.add("Center", mc);
        frame.pack();//setBounds(getMouseX(), getMouseY(), 400, 300);
        frame.setVisible(true);

        return frame;
    }

    private String checkPriv(JCheckBox b) {
        if (b.isSelected()) {
            return "Y";
        } else {
            return "N";
        }

    }

    private void message(String msg) {
        //JOptionPane.showMessageDialog(this, msg);
        //JOptionPane.showMessageDialog(null, msg,"Message",JOptionPane.WARNING_MESSAGE);

        logger.log(Level.INFO, msg);
        //mb.addText(msg);
        //mb.frame.setState(Frame.NORMAL);
        Toolkit tk = this.getToolkit();
        tk.beep();

    }

    private JInternalFrame dbs() {

        Container contentPane;

        JInternalFrame frame = new JInternalFrame("Databases", true, false, true, true);
        contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        frame.pack();//setBounds(getMouseX(), getMouseY(), 400, 215);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel panel1 = new JPanel();
        ColumnLayout layout1 = new ColumnLayout(5, 5, 10, 2);
        panel1.setLayout(layout1);
        JLabel label = new JLabel();
        label.setText("Host");
        panel1.add(label);

        JLabel label1 = new JLabel();
        label1.setText("Db");
        panel1.add(label1);

        JLabel label2 = new JLabel();
        label2.setText("User");
        panel1.add(label2);

        JPanel panel2 = new JPanel();
        ColumnLayout layout2 = new ColumnLayout(5, 5, 5, 0);
        panel2.setLayout(layout2);

        dbHost = new JTextField(15);
        dbDb = new JTextField(15);
        dbUser = new JTextField(15);

        panel2.add(dbHost);
        panel2.add(dbDb);
        panel2.add(dbUser);

        JLabel l = new JLabel(loadImage.getImage("mysql-06.gi"));
        panel2.add(l);

        Box box1 = Box.createVerticalBox();
        String[] choices = {"Select Priv", "Insert Priv", "Update Priv",
            "Delete Priv", "Create Priv", "Drop Priv"};
        String[] tips = {"Select Privilegs", "Insert Privilegs", "Update Privilegs",
            "Delete Privilegs", "Create Privilegs", "Drop Privilegs"};
        for (int i = 0; i < choices.length; i++) {
            JCheckBox b = new JCheckBox(choices[i]);
            b.setToolTipText(tips[i]);
            box1.add(b);
        }

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEtchedBorder());

        JButton list = new JButton("List");
        JButton modify = new JButton("Modify");
        JButton insert = new JButton("Insert");
        JButton delete = new JButton("Delete");

        buttonPanel.add(list);
        buttonPanel.add(modify);
        buttonPanel.add(insert);
        buttonPanel.add(delete);

        panel.add("West", panel1);
        panel.add("Center", panel2);
        panel.add("East", box1);
        panel.add("South", buttonPanel);

        JScrollPane mc = new JScrollPane();
        mc.getViewport().add(panel);

        contentPane.add("Center", mc);

        return frame;
    }

    private JInternalFrame setColor() {
        Container contentPane;

        colorFrame = new JInternalFrame("Color", true, true, true, true);
        contentPane = colorFrame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        cg = new ChooseColor(getDefaultColor());
        contentPane.add("Center", cg);
        cg.select.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                lc.setOpaque(true);
                lc.setBackground(cg.getColor());
                lc.updateUI();
                lc.update(lc.getGraphics());
                saveDefaultColor(cg.getColor());
                setDefaultColor(cg.getColor());
                colorFrame.dispose();

            }
        });
        cg.cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                colorFrame.dispose();
            }
        });
        colorFrame.pack();
        colorFrame.setVisible(true);
        colorFrame.moveToFront();

        return colorFrame;

    }

    private void saveDefaultColor(Color color) {
        int c = color.getRGB();

        String userName = System.getProperty("user.name");
        String serverName = getLogOn().getHost();
        String databaseName = getLogOn().getDatabase();

        Connection connection = null;
        PreparedStatement writer = null;
        ResultSet rs = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            writer = connection.prepareStatement("update APP.BACKGROUNDIMAGE  set BACKGROUNDCOLOR = ? where userName = ? and serverName = ? and databaseName = ?");
            writer.setInt(1, c);
            writer.setString(2, userName);
            writer.setString(3, serverName);
            writer.setString(4, databaseName);
            writer.executeUpdate();

        } catch (SQLException | IOException | PropertyVetoException sqlex) {
            logger.log(Level.WARNING, "Can not update background.  Exception: {0}", sqlex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }

            if (writer != null) {
                try {
                    writer.close();
                } catch (SQLException e) {
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }

        }
    }

    private void qbe() {

        qbePanel = new Qbe(getLogOn().getSchema());
        qbePanel.execQuery.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sqlTable sqlt = new sqlTable(qbePanel.getQuery());
                Dimension dim = sqlt.getPreferredSize();
                newFrame((JPanel) sqlt, "Generated Query", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, null);
            }
        });

        Dimension dim = qbePanel.getPreferredSize();
        newFrame((JPanel) qbePanel, "Guided Query", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, qbePanel.cancel);

    }

    private JInternalFrame form() {

        try {
            logger.log(Level.FINEST, "launching AutoForm by user " + getLogOn().getUser());

            Container contentPane;

            formFrame = new JInternalFrame("Form", true, true, true, true);
            contentPane = formFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            formFrame.setFrameIcon(genImgIcon);

            formPanel = new AutoForm(getLogOn().getSchema());

            formFrame.addComponentListener(new ComponentAdapter() {

                public void componentHidden(ComponentEvent e) {
                }

                public void componentMoved(ComponentEvent e) {
                }

                public void componentResized(ComponentEvent e) {
                    Dimension d = new Dimension();
                    d = formFrame.getSize();
                    Integer intx = d.width;
                    Integer inty = d.height;
                    double fx = intx.floatValue() * 0.75;
                    double fy = inty.floatValue() * 0.65;
                    Double dfx = fx;
                    Double dfy = fy;

                    formPanel.setVSize(dfx.intValue(), dfy.intValue());
                    formPanel.setFormSize();
                }

                public void componentShown(ComponentEvent e) {
                }
            });

            formPanel.cancel.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    formPanel.close();
                    formFrame.dispose();
                }
            });

            JScrollPane mc = new JScrollPane();
            mc.getViewport().add(formPanel);

            contentPane.add("Center", mc);
            formFrame.pack();//setBounds(getMouseX(), getMouseY(), 560, 310);
            formFrame.setVisible(true);
            return formFrame;
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
            return null;
        }
    }

    private void dropTable() {

        try {
            logger.log(Level.FINEST, "launching DropTable by user {0}", getLogOn().getUser());

            DropTable panel = new DropTable(getLogOn().getSchema());
            Dimension dim = panel.getPreferredSize();
            newFrame((JPanel) panel, "Drop Table", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, panel.cancel);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }

    }

    private void internalDbMonitor() {
        try {

            Thread t = new Thread() {
                public void run() {

                    DBquery dbQuery = new DBquery();

                    InternalFrameListener ifl = new InternalFrameListener() {
                        @Override
                        public void internalFrameClosing(InternalFrameEvent e) {
                            dbQuery.close();
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameClosed(InternalFrameEvent e) {
                            ;
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameOpened(InternalFrameEvent e) {
                            ;
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameIconified(InternalFrameEvent e) {
                            ;
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameDeiconified(InternalFrameEvent e) {
                            ;
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameActivated(InternalFrameEvent e) {
                            ;
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameDeactivated(InternalFrameEvent e) {
                            ;
                        }
                    };

                    Dimension dim = dbQuery.getPreferredSize();
                    newNonScrollableFrame((JPanel) dbQuery, "internal DB", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, null, ifl);

                }
            };
            t.setUncaughtExceptionHandler(new GlobalExceptionHandler());
            t.start();

            //newNonScrollableFrame((JPanel) sm, "SQL-Monitor", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, sm.exit);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }
    }

    private void select(final File file) {

        try {
            //SqlMonitor sm = new SqlMonitor(logOn.getCon(), logOn.getSchema());

            //Dimension dim = sm.getPreferredSize();           
            Thread t = new Thread() {
                public void run() {

                    SqlMonitor sm = new SqlMonitor(getLogOn().getSchema());

                    sm.setDatabase(getLogOn().getDatabase());
                    if (file != null) {
                        try {
                            sql.fredy.io.ReadFile rf = new sql.fredy.io.ReadFile(file.getCanonicalPath());
                            sm.query.setText(rf.getText());
                        } catch (java.io.IOException e) {
                        }
                    }

                    InternalFrameListener ifl = new InternalFrameListener() {
                        @Override
                        public void internalFrameClosing(InternalFrameEvent e) {
                            sm.saveQuery();
                            sm.timer.cancel();
                            sm.timer.purge();
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameClosed(InternalFrameEvent e) {
                            sm.timer.cancel();
                            sm.timer.purge();
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameOpened(InternalFrameEvent e) {
                            ;
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameIconified(InternalFrameEvent e) {
                            e.getInternalFrame().moveToFront();
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameDeiconified(InternalFrameEvent e) {
                            ;
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameActivated(InternalFrameEvent e) {
                            ;
                        }

                        @Override
                        @SuppressWarnings("empty-statement")
                        public void internalFrameDeactivated(InternalFrameEvent e) {
                            ;
                        }
                    };

                    Dimension dim = sm.getPreferredSize();
                    newNonScrollableFrame((JPanel) sm, "SQL-Monitor", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, sm.exit, ifl);

                }
            };
            t.setUncaughtExceptionHandler(new GlobalExceptionHandler());
            t.start();

            //newNonScrollableFrame((JPanel) sm, "SQL-Monitor", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, sm.exit);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }
    }

    private void dataSelect() {
        try {
            SelectionGui selGui = new SelectionGui();
            Dimension dim = selGui.getPreferredSize();
            newFrame((JPanel) selGui, "Data Selection", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, selGui.exit);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }
    }

    private void dataSelectionDemoEnvironment() {

        String filePath = "";
        localAdminDir = System.getProperty("admin.work");
        final Properties prop = new Properties();
        try {

            if (localAdminDir == null) {
                localAdminDir = System.getProperty("user.home");
            }

            try {
                FileInputStream fip = new FileInputStream(localAdminDir + File.separator + "admin.selgui.props");
                prop.load(fip);
                fip.close();
            } catch (Exception fipEx) {
                logger.log(Level.INFO, "need to create properties for the first time");
            }

            if (prop.getProperty("selection.path") == null) {
                filePath = localAdminDir + File.separator + "datadrill" + File.separator + "resources" + File.separator;
                prop.put("selection.path", filePath);
            } else {
                filePath = prop.getProperty("selection.path");
            }

            final CreateDemoEnv demoEnv = new CreateDemoEnv(filePath);
            Dimension dim = demoEnv.getPreferredSize();

            Thread t = new Thread() {

                public void run() {
                    try {
                        Container contentPane;
                        final JInternalFrame frame = new JInternalFrame("Create Demo Environment",
                                true,
                                true,
                                false,
                                true);
                        contentPane = frame.getContentPane();
                        contentPane.setLayout(new BorderLayout());

                        JScrollPane scrollpane = new JScrollPane(demoEnv);

                        contentPane.add("Center", scrollpane);

                        demoEnv.undo.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                frame.dispose();
                            }
                        });

                        demoEnv.exitButton.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {

                                // update Properties path
                                prop.put("selection.path", demoEnv.getFileLocation());
                                // write back properties
                                try {
                                    FileOutputStream fops = new FileOutputStream(localAdminDir + File.separator + "admin.selgui.props");
                                    prop.store(fops, "Selection properties Fredy's SqlAdmin");
                                    fops.close();
                                } catch (Exception propE) {
                                    logger.log(Level.WARNING, "Can not save properties " + propE.getMessage());
                                }
                                frame.dispose();
                            }
                        });

                        lc.add(frame, 4);
                        frame.moveToFront();
                        logger.log(Level.INFO, "to front");
                        frame.pack();
                        logger.log(Level.INFO, "pack");
                        frame.setVisible(true);
                        logger.log(Level.INFO, "set visible true");
                    } catch (Exception foo) {
                        foo.initCause(new Throwable(foo.getMessage()));
                        stackTracer(foo);
                    }
                }
            };
            t.start();

        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }

    }

    private void importXML() {
        XMLImportGUI imp = new XMLImportGUI();
        Dimension dim = imp.getPreferredSize();
        newFrame(imp, "XML Import", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, imp.cancel);

    }

    private void iViewer() {
        ReportViewer viewer = new ReportViewer();
        Dimension dim = viewer.getPreferredSize();
        newFrame(viewer, "Report Viewer", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, viewer.cancel);
    }

    private void importDelimiter() {

        try {
            logger.log(Level.FINEST, "launching Import by user " + getLogOn().getUser());

            //Import imp = new Import(getLogOn().getCon(), getLogOn().getSchema());
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(new JLabel("Delimiter import is too weak and therefore not anymore available"));
            Dimension dim = panel.getPreferredSize();
            newFrame(panel, "Import", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, null);

        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }

    }

    private void cpTable() {

        logger.log(Level.FINEST, "launching TableCopy by user " + getLogOn().getUser());

        TableCopyGUI tcg = new TableCopyGUI();
        Dimension dim = tcg.getPreferredSize();
        newFrame(tcg, "Import", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, tcg.cancel);

    }

    private void exportToDb() {
        DataBaseExportGui exportgui = new DataBaseExportGui();
        Dimension dim = exportgui.getPreferredSize();
        newFrame(exportgui, "Export", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, exportgui.cancel);
    }

    private JMenu dbOpsMenu() {

        JMenu m0 = new JMenu("Database operations");

        JMenuItem m01 = new JMenuItem("Create Tables", loadImage.getImage("newsheet.gif"));
        m01.setActionCommand("createTables");
        m01.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createTable();
            }
        });
        JMenuItem m01aa = new JMenuItem("Create Tables (2)", loadImage.getImage("newsheet.gif"));
        m01aa.setActionCommand("createTables2");
        m01aa.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createTable2();
            }
        });

        JMenuItem m0a = new JMenuItem("Create Index", loadImage.getImage("updatecolumn.gif"));
        m0a.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createIndex();
            }
        });

        JMenuItem m02 = new JMenuItem("Drop Tables", loadImage.getImage("deletesheet.gif"));
        m02.setActionCommand("dropTables");
        m02.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dropTable();
            }
        });

        JMenuItem m03 = new JMenuItem("Create User", loadImage.getImage("user.gif"));
        m03.setToolTipText("must have acces to mysql-database");
        m03.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                lc.add(users(), 3);
            }
        });

        JMenuItem m04 = new JMenuItem("Create Database", loadImage.getImage("data.gif"));
        m04.setToolTipText("must have acces to mysql-database and File-Create Permissions");
        m04.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                createDB();
            }
        });

        JMenuItem m05 = new JMenuItem("Drop Database", loadImage.getImage("delete.gif"));
        m05.setToolTipText("must have acces to mysql-database and File-Create Permissions");
        m05.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                deleteDB();
            }
        });

        JMenuItem m06 = new JMenuItem("Alter Table", loadImage.getImage("altertable.gif"));
        m06.setToolTipText("change a table (update/add rows)");
        m06.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    ChangeTable ctbl = new ChangeTable(getLogOn().getSchema());
                    newFrame(ctbl, "ChangeTable", true, true, true, true, getMouseX(), getMouseY(), 0, 0, ctbl.exit);
                } catch (Exception e2) {
                    e2.initCause(new Throwable(e2.getMessage()));
                    stackTracer(e2);
                }
            }
        });

        m0.add(m01);
        m0.add(m01aa);
        m0.add(m06);
        m0.add(m02);
        m0.add(m0a);
        //m0.add(m03);
        //m0.add(m04);
        //m0.add(m05);

        return m0;

    }

    private JMenu dataOpsMenu() {
        JMenu m1 = new JMenu("Data operations");

        JMenuItem m10 = new JMenuItem("guided query", loadImage.getImage("binocular.gif"));
        m1.add(m10);
        m10.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                qbe();
            }
        });

        JMenuItem m11 = new JMenuItem("Form", loadImage.getImage("documentdraw.gif"));
        m1.add(m11);
        m11.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                lc.add(form(), 4);
            }
        });

        JMenuItem m12 = new JMenuItem("SQL Monitor", loadImage.getImage("computer.gif"));
        m1.add(m12);
        m12.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                select(null);
            }
        });

        JMenu m13Menu = new JMenu("Data Selection Tool");
        JMenuItem m14 = new JMenuItem("Create Demo environment", loadImage.getImage("hammer.gif"));
        m14.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dataSelectionDemoEnvironment();
            }
        });
        JMenuItem m13 = new JMenuItem("Data Selections", loadImage.getImage("dataextract.gif"));

        m13.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dataSelect();
            }
        });

        m13Menu.add(m13);
        m13Menu.add(m14);

        m1.add(m13Menu);

        return m1;

    }

    private JMenu dbInfoMenu() {

        JMenu dbIM = new JMenu("Database Info");

        JMenuItem m1 = new JMenuItem("RDBMS", loadImage.getImage("camera.gif"));
        m1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    busyCursor();
                    DBTreeView dbtv = new DBTreeView();
                    setCursor(Cursor.getDefaultCursor());
                    newFrame(dbtv, "RDBMS", true, true, true, true, getMouseX(), getMouseY(), 0, 0, dbtv.cancel);
                } catch (Exception e2) {
                    e2.initCause(new Throwable(e2.getMessage()));
                    stackTracer(e2);
                }
            }
        });

        JMenuItem m2 = new JMenuItem("this DB", loadImage.getImage("cameraflash.gif"));
        m2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                databaseInfo(getLogOn().getDatabase());
            }
        });

        dbIM.add(m1);
        dbIM.add(m2);
        return dbIM;
    }

    private JMenuItem generateCode() {

        JMenuItem m2 = new JMenuItem("Generate Java-Code", loadImage.getImage("hammer.gif"));
        m2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                gC();
            }
        });
        return m2;
    }

    private void gC() {

        logger.log(Level.FINEST, "launching GenerateCode by user " + getLogOn().getUser());
        try {

            GenerateTool gt = new GenerateTool();
            gt.setDatabase(getLogOn().getDatabase());
            gt.setSchema(getLogOn().getSchema());
            gt.askXMLFile.setText(System.getProperty("user.home")
                    + java.io.File.separator
                    + dbNameVerifier(getLogOn().getDatabase().toLowerCase())
                    + ".xml");
            Dimension dim = gt.getPreferredSize();
            newFrame(gt, "Generate Java-Code Tool", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, gt.cancel);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }
    }

    public String dbNameVerifier(String v) {

        File f = new File(v);
        if (f.exists()) {
            if (f.isDirectory()) {
                v = f.getName() + "_dbdesc";
            }
            if (f.isFile()) {
                v = f.getName() + "_dbdesc";
            }
        }

        return v;
    }

    private JMenu propsMenu() {

        JMenu m3 = new JMenu("Properties");
        m30 = new JCheckBoxMenuItem("Save on exit");
        m30.setState(true);
        m30.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
            }
        });

        m3.add(m30);

        JMenuItem m31 = new JMenuItem("Select background color", loadImage.getImage("palette.gif"));
        m31.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JInternalFrame frame = setColor();
                lc.add(frame, JLayeredPane.DEFAULT_LAYER);
                lc.updateUI();
            }
        });

        m3.add(m31);
        JMenu backGround = new JMenu("Background");
        JMenuItem m32 = new JMenuItem("Select background Image");
        m32.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                selectBackgroundImage();
            }
        });
        backGround.add(m32);

        JMenuItem m33 = new JMenuItem("remove background Image");
        m33.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                removeBackgroundImage();
            }
        });
        backGround.add(m33);

        m3.add(backGround);

        return m3;
    }

    private JMenu progMenu() {
        JMenu m4 = new JMenu("Tools");

        JMenuItem m47 = new JMenuItem("Data Export", loadImage.getImage("export.gif"));
        m47.addActionListener((ActionEvent e) -> {
            dataExport();
        });
        m4.add(m47);

        JMenuItem mcdb = new JMenuItem("Copy Query to DB", loadImage.getImage("sqlwrapper.gif"));
        mcdb.addActionListener((ActionEvent e) -> {
            exportToDb();
        });
        m4.add(mcdb);

        if (isSocketHandler() && (sls != null)) {
            JMenuItem m48 = new JMenuItem("Simple Log Server", loadImage.getImage("logserver.gif"));
            m48.setToolTipText("makes only sense, if you let Admin use the right logging.properties");
            m48.addActionListener((ActionEvent e) -> {
                sls.frame.setVisible(true);
            });

            m4.add(m48);
            m4.add(new JSeparator());
        }

        if (getProductName().toLowerCase().startsWith("mysql")) {
            JMenuItem m49 = new JMenuItem("MySQL Dashboard", loadImage.getImage("dashboard.gif"));
            m49.setToolTipText("displays information about the running MySQL");
            m49.addActionListener(new ActionListenerImpl());
            m4.add(m49);
            m4.add(new JSeparator());

        }
        JMenuItem m40 = new JMenuItem("Execute Command", loadImage.getImage("enter.gif"));
        m40.addActionListener((ActionEvent e) -> {
            execCMD();
        });
        m4.add(m40);

        JMenuItem m41 = new JMenuItem("Import delimiter file", loadImage.getImage("documentin.gif"));
        m41.addActionListener((ActionEvent e) -> {
            importDelimiter();
        });
        m4.add(m41);

        JMenuItem m4xls = new JMenuItem("Import Excel file", loadImage.getImage("documentdraw.gif"));
        m4xls.addActionListener((ActionEvent e) -> {
            excelLoad();
        });
        m4.add(m4xls);

        JMenuItem m4csv = new JMenuItem("Import CSV file", loadImage.getImage("documentdraw.gif"));
        m4csv.addActionListener((ActionEvent e) -> {
            csvLoad();
        });
        m4.add(m4csv);

        JMenuItem m41x = new JMenuItem("Import XML file", loadImage.getImage("documentdraw.gif"));
        m41x.addActionListener((ActionEvent e) -> {
            importXML();
        });
        m4.add(m41x);

        JMenuItem m45ex = new JMenuItem("Export Definitions", loadImage.getImage("dataextract.gif"));
        m45ex.addActionListener((ActionEvent e) -> {
            definitionExport();
        });
        m4.add(m45ex);

        JMenuItem m42 = new JMenuItem("Copy Table", loadImage.getImage("move.gif"));
        m42.addActionListener((ActionEvent e) -> {
            cpTable();
        });
        m4.add(m42);

        JMenuItem m43 = new JMenuItem("Report Viewer", loadImage.getImage("document.gif"));
        m43.addActionListener((ActionEvent e) -> {
            iViewer();
        });

        m4.add(m43);

        JMenuItem m44 = new JMenuItem("DB-Model Visualizer", loadImage.getImage("popup.gif"));
        m44.addActionListener((ActionEvent e) -> {
            generateGraph();
        });
        m4.add(m44);

        JMenuItem m45 = new JMenuItem("Editor", loadImage.getImage("document.gif"));
        m45.addActionListener((ActionEvent e) -> {
            sql.fredy.ui.TextEditor te = new sql.fredy.ui.TextEditor();
            Dimension dim = te.getPreferredSize();
            newNonScrollableFrame((JPanel) te, "Editor", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, te.cancel, null);
        });
        m4.add(m45);

        JMenuItem m46 = new JMenuItem("Notes", loadImage.getImage("documentin.gif"));
        m46.addActionListener((ActionEvent e) -> {
            sql.fredy.ui.Notes note = new sql.fredy.ui.Notes();
            Dimension dim = note.getPreferredSize();
            newNonScrollableFrame((JPanel) note, "Notes", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, null, null);
        });
        m4.add(m46);

        JMenuItem m56 = new JMenuItem("Charset", loadImage.getImage("properties.gif"));
        m56.addActionListener((ActionEvent e) -> {
            CharsetSelector sel = new CharsetSelector();
            Dimension dim = sel.getPreferredSize();
            newNonScrollableFrame((JPanel) sel, "Charset", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, sel.cancel, null);
        });
        m4.add(m56);

        JMenuItem m57 = new JMenuItem("Memory usage", loadImage.getImage("memory.gif"));
        m57.addActionListener((ActionEvent e) -> {
            final Dimension adminDim = Admin.super.getSize();

            Thread t = new Thread() {
                public void run() {
                    try {
                        Container contentPane;
                        final JInternalFrame frame = new JInternalFrame("Memory usage",
                                true,
                                true,
                                false,
                                true);
                        contentPane = frame.getContentPane();
                        contentPane.setLayout(new BorderLayout());

                        MemoryCounter mc = new MemoryCounter();
                        contentPane.add("Center", mc);
                        if (mc.close != null) {
                            mc.close.addActionListener(new ActionListener() {
                                public void actionPerformed(ActionEvent e) {
                                    mc.startStop.setSelected(false);
                                    mc.setRunning(localExecution);
                                    frame.dispose();
                                }
                            });
                        }

                        lc.add(frame, JLayeredPane.DEFAULT_LAYER);
                        frame.moveToFront();

                        frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                        frame.pack();
                        frame.setVisible(true);
                    } catch (Exception foo) {
                        foo.initCause(new Throwable(foo.getMessage()));
                        stackTracer(foo);
                    }
                }
            };
            t.start();
        });
        m4.add(m57);

        boolean showInternalDb = false;
        if ((System.getenv("sql.fredy.admin.internaldb") != null) && (System.getenv("sql.fredy.admin.internaldb").equalsIgnoreCase("y"))) {
            showInternalDb = true;
        }
        if ((System.getProperty("sql.fredy.admin.internaldb") != null) && (System.getProperty("sql.fredy.admin.internaldb").equalsIgnoreCase("y"))) {
            showInternalDb = true;
        }
        if (showInternalDb) {
            JMenuItem m58 = new JMenuItem("internal Derby DB", loadImage.getImage("datastore.gif"));
            m58.addActionListener((ActionEvent e) -> {
                internalDbMonitor();
            });
            m4.add(new JSeparator());
            m4.add(m58);
        }

        JMenuItem jdbctypemapping = new JMenuItem("JDBC TYpe mapping", loadImage.getImage("draw.gif"));
        jdbctypemapping.addActionListener((ActionEvent e) -> {
            jdbcTypeMapping();
        });
        m4.add(new JSeparator());
        m4.add(jdbctypemapping);

        return m4;

    }

    private JMenuItem aboutMenu() {

        JMenuItem m5 = new JMenuItem("About", loadImage.getImage("bulb.gif"));
        m5.addActionListener((ActionEvent e) -> {
            about();
        });

        return m5;
    }

    private void setOS(String os) {
        Properties p = System.getProperties();
        p.put("os.name", os);
        System.setProperties(p);
    }

    private JMenu laf() {

        JMenu lookfeel = new JMenu("Look & Feel");
        lookfeel.setName("lookandfeel");

        JMenuItem addLnf = new JMenuItem("Manage Look and Feel");
        addLnf.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addLookAndFeel();
            }
        });
        lookfeel.add(addLnf);
        lookfeel.add(new JSeparator());

        Set<Object> entries = lnfProps.keySet();
        for (Object k : entries) {

            JMenuItem mi = new JMenuItem((String) k);
            mi.setName((String) k);
            mi.setActionCommand((String) lnfProps.get(k));
            lookfeel.add(mi);
            mi.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    lnfchange(e.getActionCommand());
                }
            });
        }

        return lookfeel;

    }

    // Manage Look and Feel
    JMenu lookAndFeelMenu = null;
    ImageButton lookAndFeelMenuClose;

    private void addLookAndFeel() {

        // first we find the look-and-feel Menu
        Component[] components = menubar.getComponents();
        for (Component component : components) {
            if ((component.getName() != null) && (component.getName().equalsIgnoreCase("lookandfeel"))) {
                lookAndFeelMenu = (JMenu) component;
            }
        }

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new GridBagLayout());

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        Vector v = new Vector();
        Set<Object> entries = lnfProps.keySet();
        for (Object k : entries) {
            v.add((String) k);
        }
        final JList liste = new JList(v);
        listPanel.add(new JScrollPane(liste));
        ImageButton remove = new ImageButton(null, "delete.gif", "remove from list");

        final JTextField name = new JTextField(80);
        final JTextField lnfClass = new JTextField(80);
        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 0;
        innerPanel.add(new JLabel("Name"), gbc);
        gbc.gridx = 1;
        innerPanel.add(name, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        innerPanel.add(new JLabel("Look and Feel class"), gbc);
        gbc.gridx = 1;
        innerPanel.add(lnfClass, gbc);

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new FlowLayout());
        lowerPanel.add(remove);
        ImageButton save = new ImageButton(null, "insert.gif", "add to list");
        lowerPanel.add(save);

        lookAndFeelMenuClose = new ImageButton(null, "exit.gif", "close window");
        lowerPanel.add(lookAndFeelMenuClose);

        remove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // we remove this menu from the Look-and-Feel Menu
                Component[] components = lookAndFeelMenu.getMenuComponents();
                for (Component component : components) {
                    if ((component.getName() != null) && (component.getName().equalsIgnoreCase((String) liste.getSelectedValue()))) {
                        lookAndFeelMenu.remove((JMenuItem) component);
                    }
                }
                name.setText("");
                lnfClass.setText("");
                lnfProps.remove(liste.getSelectedValue());
                liste.removeAll();
                Vector v = new Vector();
                Set<Object> entries = lnfProps.keySet();
                for (Object k : entries) {
                    v.add((String) k);
                }
                liste.setListData((Vector) v);
            }
        });

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                lnfProps.put(name.getText(), lnfClass.getText());

                // we need to update the look and feel menu
                JMenuItem mi = new JMenuItem((String) name.getText());
                mi.setName((String) name.getText());
                mi.setActionCommand((String) lnfClass.getText());
                lookAndFeelMenu.add(mi);
                mi.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        lnfchange(e.getActionCommand());
                    }
                });

                liste.removeAll();
                Vector v = new Vector();
                Set<Object> entries = lnfProps.keySet();
                for (Object k : entries) {
                    v.add((String) k);
                }
                liste.setListData((Vector) v);
            }
        });

        MouseListener listChanger = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = liste.locationToIndex(e.getPoint());
                    String selectedLnf = (String) liste.getSelectedValue();
                    name.setText(selectedLnf);
                    lnfClass.setText(lnfProps.getProperty(selectedLnf));
                }
            }
        };
        liste.addMouseListener(listChanger);

        panel.add(listPanel, BorderLayout.WEST);
        panel.add(innerPanel, BorderLayout.CENTER);
        panel.add(lowerPanel, BorderLayout.SOUTH);
        Dimension dim = panel.getPreferredSize();
        newFrame(panel, "Look and feel", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, lookAndFeelMenuClose);

    }

    private JMenuItem exitMenu() {

        JMenuItem mx = new JMenuItem("Exit", loadImage.getImage("exit.gif"));
        mx.setActionCommand("exit");
        popup.add(mx);
        mx.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                goodbye();
            }
        });

        return mx;

    }

    private JMenuItem newWindow() {
        JMenuItem mi = new JMenuItem("New Window", loadImage.getImage("Computer.gif"));
        mi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Runnable r = new Runnable() {
                    public void run() {
                        Admin adm = new Admin();
                        adm.setLocalExecution(false);
                    }
                };
                Thread t = new Thread(r);
                t.start();
                // t.run();
            }
        });

        return mi;
    }

    private void normalMenu() {

        menubar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu helpMenu = new JMenu("Help");
        fileMenu.add(dbInfoMenu());
        fileMenu.add(new JSeparator());
        fileMenu.add(generateCode());
        fileMenu.add(new JSeparator());
        // fileMenu.add(newWindow());
        // fileMenu.add(new JSeparator());
        fileMenu.add(exitMenu());
        helpMenu.add(aboutMenu());

        menubar.add(fileMenu);
        menubar.add(dataOpsMenu());
        menubar.add(dbOpsMenu());
        menubar.add(propsMenu());
        menubar.add(progMenu());
        menubar.add(laf());
        menubar.add(Box.createHorizontalGlue());
        menubar.add(helpMenu);
        menubar.setEnabled(false);
        this.setJMenuBar(menubar);
        //this.getContentPane().add("North",menubar);
        //	this.pack();
        this.setSize(Integer.parseInt(prop.getProperty("width")) + 1, Integer.parseInt(prop.getProperty("heigth")) + 1);
        this.setSize(Integer.parseInt(prop.getProperty("width")), Integer.parseInt(prop.getProperty("heigth")));

        statusInformation();

        // Test with Mouslistener
        final Component component = super.getContentPane();
        MouseListener mouseListener = new MouseAdapter() {

            // left mouse button
            public void mouseClicked(MouseEvent e) {
                int leftButton = e.BUTTON3;
                if (MouseInfo.getNumberOfButtons() < 3) {
                    leftButton = e.BUTTON2;
                }

                // if clicked once  the right , it adds a SQL-Statement to see this table to the clipboard
                if (e.getButton() == leftButton) {
                    if (e.getClickCount() == 1) {
                        popup.show(lc, e.getX(), e.getY());
                        setMouseX(e.getX());
                        setMouseY(e.getY());
                    }
                }
            }
        };
        lc.addMouseListener(mouseListener);
        // end test with MouseListener
    }

    private void statusInformation() {
        // und schreibe die Statuszeile mit neuem Label und im titel auf welcher DB du als wer bist
        statusLine.setText("SQL-Admin-Tool " + getVersion() + " Running on " + System.getProperty("os.name") + " Connected to database " + getLogOn().getDatabase() + " on server " + getLogOn().getHost() + " as " + getLogOn().getUser() + " using Java " + System.getProperty("java.version"));
        statusLine.updateUI();
        super.setTitle("SQL Admintool (" + getVersion() + ") is connected to DB " + getLogOn().getDatabase() + " on Server " + getLogOn().getHost() + " as User " + getLogOn().getUser());
    }

    private JPopupMenu pm() {

        popup = new JPopupMenu("Main Menu");

        popup.add(aboutMenu());
        popup.add(dbInfoMenu());
        popup.add(generateCode());
        popup.add(dataOpsMenu());
        popup.add(dbOpsMenu());
        popup.add(propsMenu());
        popup.add(progMenu());
        popup.add(new JSeparator());
        popup.add(laf());
        popup.add(new JSeparator());
        // popup.add(newWindow());
        // popup.add(new JSeparator());
        popup.add(exitMenu());

        return popup;
    }

    private void browser() {
        Thread t = new Thread() {

            public void run() {
                Browser bro = new Browser();
                Dimension dim = bro.getPreferredSize();
                newFrame(bro, "Browser", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, bro.cancel);
            }
        };
        t.start();

    }

    private boolean isSocketHandler() {
        boolean v = false;
        Handler[] handler = logger.getHandlers();
        logger.log(Level.FINE, "Number of Handlers found:  " + handler.length);
        for (int i = 0; i < handler.length; i++) {
            if (handler[i] instanceof SocketHandler) {
                v = true;
            }
        }

        return v;
    }

    public void lnfchange(String lnf) {

        try {
            if (lnf.toLowerCase().startsWith("default")) {
                lnf = UIManager.getSystemLookAndFeelClassName();
                SwingUtilities.updateComponentTreeUI(this);
            } else {

                switch (lnf.toLowerCase()) {
                    //case "darcula" -> {

                    /*
                    case "darcula":
                        FlatDarculaLaf.setup();
                        SwingUtilities.updateComponentTreeUI(this);
                        break;

                    //case "flatlightlaf" -> {
                    case "flatlightlaf":
                        FlatLightLaf.setup();
                        SwingUtilities.updateComponentTreeUI(this);
                        break;

                    //case "flatdarklaf" -> {
                    case "flatdarklaf":
                        FlatDarkLaf.setup();
                        SwingUtilities.updateComponentTreeUI(this);
                        break;

                    //case "flatintellij" -> {
                    case "flatintellij":
                        FlatIntelliJLaf.setup();
                        SwingUtilities.updateComponentTreeUI(this);
                        break;
                     */
                    //case "crossplatform" -> {                      
                    case "crossplatform":
                        try {
                            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                            SwingUtilities.updateComponentTreeUI(this);
                        } catch (UnsupportedLookAndFeelException ex) {
                            logger.log(Level.WARNING, "Look and Feel Exception: {0}", ex.getMessage());
                        }
                        break;

                    //default -> {
                    default:
                        try {
                            UIManager.setLookAndFeel(lnf);
                            SwingUtilities.updateComponentTreeUI(this);
                        } catch (UnsupportedLookAndFeelException ex) {
                            logger.log(Level.WARNING, "Look and Feel Exception: {0}", ex.getMessage());
                        }
                        break;
                }
            }

            // and we set the BackgroundImage
            if (getBackGroundImageFile() != null) {
                setBackgroundImage(new File(getBackGroundImageFile()));
            }
            setLnF(lnf);

        } catch (Exception exc) {

            lnf = UIManager.getSystemLookAndFeelClassName();
            try {
                UIManager.setLookAndFeel(lnf);
                SwingUtilities.updateComponentTreeUI(this);

            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Admin.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (InstantiationException ex) {
                Logger.getLogger(Admin.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (IllegalAccessException ex) {
                Logger.getLogger(Admin.class
                        .getName()).log(Level.SEVERE, null, ex);

            } catch (UnsupportedLookAndFeelException ex) {
                Logger.getLogger(Admin.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
            message("can not load look & feel: " + lnf + " going back to default");
            //exc.initCause(new Throwable("can not load look & feel: " + lnf));
            ; //stackTracer(exc);
        }

        //this.setSize(Integer.parseInt(prop.getProperty("width")),Integer.parseInt(prop.getProperty("heigth")));
        //this.setVisible(true);
        //this.pack();
    }

    private JTextField tableNamePattern;

    private JPanel dbInfoTableNamePattern() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(new JLabel("Tablename pattern: "));
        tableNamePattern = new JTextField("%", 20);
        panel.add(tableNamePattern);

        return panel;
    }

    JLabel infoLabel = new JLabel();

    private void databaseInfo(final String db) {

        final JList tablesList = new JList();
        final JList viewsList = new JList();
        final JList aliasList = new JList();
        final JList synonymList = new JList();

        try {
            Container contentPane;

            tableList = new JList();

            //JPanel panel = new JPanel();
            //panel.setLayout(new BorderLayout());
            JPanel panel1 = new JPanel();
            panel1.setLayout(new ColumnLayout(5, 5, 5, 0));

            dbInfo = new JTextArea("", 5, 30);
            dbInfo.setEditable(false);
            JScrollPane scrollpane = new JScrollPane(dbInfo);
            //scrollpane.getViewport().add(dbInfo);

            panel1.add((JLabel) new JLabel("About"));
            panel1.add(scrollpane);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            JButton cancel = new JButton("Cancel", loadImage.getImage("exit.gif"));
            buttonPanel.add(cancel);

            ImageButton textInfo = new ImageButton(null, "magnify.gif", "export to csv (might take long)");
            buttonPanel.add(textInfo);

            final ImageButton info2file = new ImageButton("DOT File for Graphviz", "newdocument.gif", "export info to dot-file to be interpreted by http://www.graphiz.org");
            buttonPanel.add(info2file);

            final JCheckBox digIntoIt = new JCheckBox("scan all tables");
            digIntoIt.setToolTipText("By selecting this checkbox, I scan all tables to find relations sequentially. Might consume a lot of time and ressources");
            digIntoIt.setSelected(false);
            /*
             It is nod a good idea and consumes a lot of computing power because the way relationships are
             handeled are different to every DB designer. So we only take those relationships generated by 
             foreign key constraints in the DB.
             */
            //buttonPanel.add(digIntoIt);

            panel1.add(buttonPanel);

            panel1.add(dbInfoTableNamePattern());

            final DbInfo dbi;
            dbi = new DbInfo();

            tableNamePattern.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    tableList.removeAll();
                    String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};
                    tableList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes, tableNamePattern.getText()));
                    tableList.updateUI();

                    tablesList.removeAll();
                    String[] tableTypes2 = {"TABLE"};
                    tablesList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes2, tableNamePattern.getText()));
                    tablesList.updateUI();

                    viewsList.removeAll();
                    String[] tableTypes3 = {"VIEW"};
                    viewsList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes3, tableNamePattern.getText()));
                    viewsList.updateUI();

                    aliasList.removeAll();
                    String[] tableTypes4 = {"ALIAS"};
                    aliasList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes4, tableNamePattern.getText()));
                    aliasList.updateUI();

                    synonymList.removeAll();
                    String[] tableTypes5 = {"SYNONYM"};
                    synonymList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes5, tableNamePattern.getText()));
                    synonymList.updateUI();

                }
            });

            info2file.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {

                    // display a Message if mor than 500 tables to scan.
                    boolean cont = true;
                    int noTables = tableList.getModel().getSize();

                    if (noTables > 500) {
                        String string1 = "Yes";
                        String string2 = "No";
                        Object[] options = {string1, string2};
                        int n = JOptionPane.showOptionDialog(null,
                                "About to scan " + Integer.toString(noTables) + " tables",
                                "Continue?",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null, //don't use a custom Icon
                                options, //the titles of buttons
                                string2); //the title of the default button
                        if (n == JOptionPane.NO_OPTION) {
                            cont = false;
                        }
                    }
                    if (cont) {

                        JFileChooser chooser = new JFileChooser();
                        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                        chooser.setDialogTitle("Select export File");
                        chooser.setFileFilter(new sql.fredy.io.MyFileFilter("dot"));

                        int returnVal = chooser.showSaveDialog(null);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            try {
                                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                info2file.setToolTipText("export info to " + chooser.getSelectedFile().getPath());
                                //dbi.printAllInfo(chooser.getSelectedFile().getPath(),tableNamePattern.getText());
                                dbi.createDotFile(chooser.getSelectedFile().getPath(), tableNamePattern.getText(), digIntoIt.isSelected(), getLogOn().getSchema());

                                StringSelection selection = new StringSelection(chooser.getSelectedFile().getPath());
                                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                clipboard.setContents(selection, selection);
                                generateGraph();

                            } finally {
                                setCursor(Cursor.getDefaultCursor());
                            }
                        }
                    }
                }
            });

            textInfo.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    // display a Message if mor than 500 tables to scan.
                    boolean cont = true;
                    int noTables = tableList.getModel().getSize();

                    if (noTables > 500) {
                        String string1 = "Yes";
                        String string2 = "No";
                        Object[] options = {string1, string2};
                        int n = JOptionPane.showOptionDialog(null,
                                "About to scan " + Integer.toString(noTables) + " tables",
                                "Continue?",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE,
                                null, //don't use a custom Icon
                                options, //the titles of buttons
                                string2); //the title of the default button
                        if (n == JOptionPane.NO_OPTION) {
                            cont = false;
                        }
                    }
                    if (cont) {

                        JFileChooser chooser = new JFileChooser();
                        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                        chooser.setDialogTitle("Select export File");
                        chooser.setFileFilter(new sql.fredy.io.MyFileFilter("csv"));

                        int returnVal = chooser.showSaveDialog(null);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            try {
                                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                waitAMoment(1);  // wait  a sceond, to change the cursor
                                info2file.setToolTipText("export info to " + chooser.getSelectedFile().getPath());
                                //dbi.printAllInfo(chooser.getSelectedFile().getPath(),tableNamePattern.getText());
                                dbi.createSchemaFile(chooser.getSelectedFile().getPath(), tableNamePattern.getText(), getLogOn().getSchema());

                            } finally {
                                setCursor(Cursor.getDefaultCursor());
                            }
                        }
                    }
                }
            });

            dbInfo.append(
                    "DBMS: \t" + dbi.getProductName()
                    + dbi.getProductVersion() + "\n");
            dbInfo.append(
                    "JDBC Driver: \t" + dbi.getDriverName() + "  "
                    + dbi.getDriverVersion() + "\n");
            dbInfo.append(
                    "Database: \t" + db + "\n");
            dbInfo.append("Schema: \t" + getLogOn().getSchema() + "\n");
            dbInfo.append("Driver: \t" + getLogOn().getDriver() + "\n");
            dbInfo.append("URL: \t" + getLogOn().getUrl());

            tableList.setListData(dbi.getTables(db, getLogOn().getSchema()));

            /*
             Add Mouselistener to all JList within the tabbed pane. So Detailas about a Object can be fetched from  every List.
             */
            MouseListener mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {

                    if ((SwingUtilities.isRightMouseButton(e)) && (e.getClickCount() == 1)) {
                        //popUpMenu.show(e.getComponent(), e.getX(), e.getY());
                        String newSchema = JOptionPane.showInputDialog("Change Schema", getLogOn().getSchema());
                        if (newSchema != null) {

                            getLogOn().setSchema(newSchema);

                            tableList.removeAll();
                            String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};
                            tableList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes, tableNamePattern.getText()));
                            tableList.updateUI();

                            tablesList.removeAll();
                            String[] tableTypes2 = {"TABLE"};
                            tablesList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes2, tableNamePattern.getText()));
                            tablesList.updateUI();

                            viewsList.removeAll();
                            String[] tableTypes3 = {"VIEW"};
                            viewsList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes3, tableNamePattern.getText()));
                            viewsList.updateUI();

                            aliasList.removeAll();
                            String[] tableTypes4 = {"ALIAS"};
                            aliasList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes4, tableNamePattern.getText()));
                            aliasList.updateUI();

                            synonymList.removeAll();
                            String[] tableTypes5 = {"SYNONYM"};
                            synonymList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes5, tableNamePattern.getText()));
                            synonymList.updateUI();

                            int noTables = tableList.getModel().getSize();
                            infoLabel.setText("Schema: " + getLogOn().getSchema() + " Objects (" + Integer.toString(noTables) + ")");
                            infoLabel.updateUI();

                        }
                    }

                    //if (e.getClickCount() == 2) {
                    if ((SwingUtilities.isLeftMouseButton(e)) && (e.getClickCount() == 2)) {
                        int index = tableList.locationToIndex(e.getPoint());
                        busyCursor();
                        waitAMoment(1);
                        TableMetaData tmd = new TableMetaData(getLogOn().getDatabase(), getLogOn().getSchema(),
                                tableList.getModel().getElementAt(index).toString());
                        setCursor(Cursor.getDefaultCursor());
                        newFrame(tmd, "Info on: " + tableList.getModel().getElementAt(index).toString(), true, true, true, true, getMouseX(), getMouseY(), 500, 215, tmd.cancel);
                    }
                }
            };

            MouseListener mouseListener2 = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = tablesList.locationToIndex(e.getPoint());
                        busyCursor();
                        TableMetaData tmd = new TableMetaData(getLogOn().getDatabase(), getLogOn().getSchema(),
                                tablesList.getModel().getElementAt(index).toString());
                        setCursor(Cursor.getDefaultCursor());
                        newFrame(tmd, "Info on: " + tablesList.getModel().getElementAt(index).toString(), true, true, true, true, getMouseX(), getMouseY(), 500, 215, tmd.cancel);
                    }
                }
            };
            MouseListener mouseListener3 = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = viewsList.locationToIndex(e.getPoint());
                        busyCursor();
                        TableMetaData tmd = new TableMetaData(getLogOn().getDatabase(), getLogOn().getSchema(),
                                viewsList.getModel().getElementAt(index).toString());
                        setCursor(Cursor.getDefaultCursor());
                        newFrame(tmd, "Info on: " + viewsList.getModel().getElementAt(index).toString(), true, true, true, true, getMouseX(), getMouseY(), 500, 215, tmd.cancel);
                    }
                }
            };

            MouseListener mouseListener4 = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = aliasList.locationToIndex(e.getPoint());
                        busyCursor();
                        TableMetaData tmd = new TableMetaData(getLogOn().getDatabase(), getLogOn().getSchema(),
                                aliasList.getModel().getElementAt(index).toString());
                        setCursor(Cursor.getDefaultCursor());
                        newFrame(tmd, "Info on: " + aliasList.getModel().getElementAt(index).toString(), true, true, true, true, getMouseX(), getMouseY(), 500, 215, tmd.cancel);
                    }
                }
            };

            MouseListener mouseListener5 = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = synonymList.locationToIndex(e.getPoint());
                        busyCursor();
                        TableMetaData tmd = new TableMetaData(getLogOn().getDatabase(), getLogOn().getSchema(),
                                synonymList.getModel().getElementAt(index).toString());
                        setCursor(Cursor.getDefaultCursor());
                        newFrame(tmd, "Info on: " + synonymList.getModel().getElementAt(index).toString(), true, true, true, true, getMouseX(), getMouseY(), 500, 215, tmd.cancel);
                    }
                }
            };

            tableList.addMouseListener(mouseListener);
            tablesList.addMouseListener(mouseListener2);
            viewsList.addMouseListener(mouseListener3);
            aliasList.addMouseListener(mouseListener4);
            synonymList.addMouseListener(mouseListener5);

            JTabbedPane tabbedPane = new JTabbedPane();

            JScrollPane listPane = new JScrollPane(tableList);
            tabbedPane.add("All", listPane);
            tabbedPane.add("Tables", new JScrollPane(tablesList));
            tabbedPane.add("Views", new JScrollPane(viewsList));
            tabbedPane.add("Aliases", new JScrollPane(aliasList));
            tabbedPane.add("Synonyms", new JScrollPane(synonymList));

            ChangeListener changeListener = new ChangeListener() {
                public void stateChanged(ChangeEvent changeEvent) {
                    JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                    int index = sourceTabbedPane.getSelectedIndex();
                    //System.out.println("got focus " + index);
                    if (index == 1) {
                        tablesList.removeAll();
                        String[] tableTypes2 = {"TABLE"};
                        busyCursor();
                        tablesList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes2, tableNamePattern.getText()));
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        tablesList.updateUI();
                    }

                    if (index == 2) {
                        viewsList.removeAll();
                        String[] tableTypes3 = {"VIEW"};
                        busyCursor();
                        viewsList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes3, tableNamePattern.getText()));
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        viewsList.updateUI();
                    }

                    if (index == 3) {
                        aliasList.removeAll();
                        String[] tableTypes4 = {"ALIAS"};
                        busyCursor();
                        aliasList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes4, tableNamePattern.getText()));
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        aliasList.updateUI();
                    }

                    if (index == 4) {
                        synonymList.removeAll();
                        String[] tableTypes5 = {"SYNONYM"};
                        busyCursor();
                        synonymList.setListData(dbi.getTables(db, getLogOn().getSchema(), tableTypes5, tableNamePattern.getText()));
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        synonymList.updateUI();
                    }
                }
            };

            tabbedPane.addChangeListener(changeListener);

            JPanel panel2 = new JPanel(); // all Tables            

            panel2.setLayout(
                    new BorderLayout());
            int noTables = tableList.getModel().getSize();

            infoLabel = new JLabel("Schema: " + getLogOn().getSchema() + " Objects (" + Integer.toString(noTables) + ")");
            panel2.add(infoLabel, BorderLayout.NORTH);
            panel2.add(tabbedPane, BorderLayout.CENTER);

            // this is the panel to search all table-columns containing a specific value
            JPanel findColumnPanel = new JPanel();

            findColumnPanel.setLayout(
                    new FlowLayout());
            findColumnPanel.setBorder(
                    new TitledBorder("Find table-columns with value"));

            String[] qp = {"=", "like", ">", ">"};
            final JComboBox queryPattern = new JComboBox(qp);
            final JTextField findPattern = new JTextField(20);
            final ImageButton execFindPattern = new ImageButton(null, "magnify.gif", "find this value in all tables");

            findColumnPanel.add(findPattern);

            findColumnPanel.add(queryPattern);

            findColumnPanel.add(execFindPattern);

            execFindPattern.addActionListener(
                    new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e
                ) {
                    try {
                        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                        waitAMoment(1);
                        dbi.setRunFind(true);
                        ArrayList<FindPattern> list = dbi.findTableRow(findPattern.getText(), (String) queryPattern.getSelectedItem(), tableNamePattern.getText());

                        if (list.size() < 1) {
                            findPattern.setText("not found: " + findPattern.getText());
                        } else {
                            JPanel resultPanel = new JPanel();
                            resultPanel.setLayout(new BorderLayout());
                            JPanel resultPanelButtonPanel = new JPanel();
                            ImageButton resultPanelCancel = new ImageButton(null, "cancel.gif", "Close");
                            resultPanelButtonPanel.add(resultPanelCancel);

                            resultPanelButtonPanel.setBorder(BorderFactory.createEtchedBorder());
                            resultPanel.add(BorderLayout.SOUTH, resultPanelButtonPanel);
                            FindPatternTable fp = new FindPatternTable(dbi.findTableRow(findPattern.getText(), (String) queryPattern.getSelectedItem(), tableNamePattern.getText()));
                            resultPanel.add(BorderLayout.CENTER, new JScrollPane(new JTable(fp)));

                            setCursor(Cursor.getDefaultCursor());
                            newFrame(resultPanel, "Find Table and Column ", true, true, true, true, getMouseX(), getMouseY(),
                                    resultPanel.getPreferredSize().width, resultPanel.getPreferredSize().height + 20, resultPanelCancel);
                        }
                    } finally {
                        setCursor(Cursor.getDefaultCursor());
                    }
                }
            }
            );

            panel1.add(findColumnPanel);

            //panel.add("West", panel1);
            //panel.add("East", panel2);
            JSplitPane panel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel1, panel2);

            JPanel panel3 = new JPanel();

            panel3.setLayout(
                    new BorderLayout());
            panel3.add(BorderLayout.CENTER, panel);

            JScrollPane mc = new JScrollPane();

            mc.getViewport()
                    .add(panel3);

            Dimension dim = panel.getPreferredSize();

            newFrame(panel3,
                    "DB-Info ", true, true, true, true, getMouseX(), getMouseY(),
                    dim.width, dim.height + 20, cancel);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }
    }

    private void waitAMoment(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {

        }
    }

    private void waitMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    private void newFrame(final JPanel panel,
            final String title,
            final boolean resizable,
            final boolean closable,
            final boolean maximizable,
            final boolean iconifiable,
            final int x, final int y,
            final int width, final int height,
            final JButton cancel) {

        final Dimension adminDim = super.getSize();

        Thread t = new Thread() {

            public void run() {
                try {
                    Container contentPane;
                    final JInternalFrame frame = new JInternalFrame(title,
                            resizable,
                            closable,
                            maximizable,
                            iconifiable);
                    contentPane = frame.getContentPane();
                    contentPane.setLayout(new BorderLayout());

                    JScrollPane scrollpane = new JScrollPane(panel);

                    contentPane.add("Center", scrollpane);
                    if (cancel != null) {
                        cancel.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                frame.dispose();
                            }
                        });
                    }

                    //lc.add(frame, 4);
                    lc.add(frame, JLayeredPane.DEFAULT_LAYER);
                    frame.moveToFront();

                    // aboid having really large windows, came up when displaying Data Nodels
                    int w = width;
                    int h = height;
                    if (w > adminDim.width) {
                        w = adminDim.width - 20;
                    }
                    if (h > adminDim.height) {
                        h = adminDim.height - 20;
                    }

                    frame.reshape(x, y, w, h);
                    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);

                } catch (Exception foo) {
                    foo.initCause(new Throwable(foo.getMessage()));
                    stackTracer(foo);
                }
            }
        };
        t.start();

    }

    private void internalFrame(final JPanel panel,
            final String title,
            final boolean resizable,
            final boolean closable,
            final boolean maximizable,
            final boolean iconifiable,
            final int x, final int y,
            final int width, final int height,
            final JButton cancel,
            final InternalFrameListener ifl) {

        final Dimension adminDim = super.getSize();

        try {
            Container contentPane;
            final JInternalFrame frame = new JInternalFrame(title,
                    resizable,
                    closable,
                    maximizable,
                    iconifiable);
            contentPane = frame.getContentPane();
            contentPane.setLayout(new BorderLayout());

            contentPane.add("Center", panel);
            if (cancel != null) {
                cancel.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                    }
                });
            }

            if (ifl != null) {
                frame.addInternalFrameListener(ifl);
            }

            lc.add(frame, JLayeredPane.DEFAULT_LAYER);
            frame.moveToFront();

            // aboid having really large windows, came up when displaying Data Nodels
            int w = width;
            int h = height;
            if (w > adminDim.width) {
                w = adminDim.width - 20;
            }
            if (h > adminDim.height) {
                h = adminDim.height - 20;
            }

            frame.reshape(x, y, w, h);
            frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception foo) {
            foo.initCause(new Throwable(foo.getMessage()));
            stackTracer(foo);
        }
    }

    private void newNonScrollableFrame(final JPanel panel,
            final String title,
            final boolean resizable,
            final boolean closable,
            final boolean maximizable,
            final boolean iconifiable,
            final int x, final int y,
            final int width, final int height,
            final JButton cancel,
            final InternalFrameListener ifl) {

        final Dimension adminDim = super.getSize();

        Thread t = new Thread() {

            public void run() {
                try {
                    Container contentPane;
                    final JInternalFrame frame = new JInternalFrame(title,
                            resizable,
                            closable,
                            maximizable,
                            iconifiable);
                    contentPane = frame.getContentPane();
                    contentPane.setLayout(new BorderLayout());

                    contentPane.add("Center", panel);
                    if (cancel != null) {
                        cancel.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                frame.dispose();
                            }
                        });
                    }

                    if (ifl != null) {
                        frame.addInternalFrameListener(ifl);
                    }

                    lc.add(frame, JLayeredPane.DEFAULT_LAYER);
                    frame.moveToFront();

                    // aboid having really large windows, came up when displaying Data Nodels
                    int w = width;
                    int h = height;
                    if (w > adminDim.width) {
                        w = adminDim.width - 20;
                    }
                    if (h > adminDim.height) {
                        h = adminDim.height - 20;
                    }

                    frame.reshape(x, y, w, h);
                    frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                    frame.pack();
                    frame.setVisible(true);
                } catch (Exception foo) {
                    foo.initCause(new Throwable(foo.getMessage()));
                    stackTracer(foo);
                }
            }
        };
        t.start();

    }

    public void createDB() {

        if ((newdb = JOptionPane.showInputDialog(this, "Please enter Database-Name")) != null) {

            try {
                Runtime rt = Runtime.getRuntime();
                Process prcs = rt.exec(prop.getProperty("mysqladmin") + " create " + newdb);
                InputStreamReader isr = new InputStreamReader(prcs.getInputStream());
                BufferedReader d = new BufferedReader(isr);

                String line;
                while ((line = d.readLine()) != null) {
                    message(line);
                }
            } catch (IOException ioe) {
                message("IO-Exception: " + ioe);
                ioe.initCause(new Throwable("IO-Exception: " + ioe.getMessage()));
                stackTracer(ioe);
            }
        }

    }

    public void deleteDB() {

        if ((newdb = JOptionPane.showInputDialog(this, "Please enter Database-Name to delete")) != null) {

            try {
                Runtime rt = Runtime.getRuntime();
                Process prcs = rt.exec(prop.getProperty("mysqladmin") + " drop " + newdb);
                InputStreamReader isr = new InputStreamReader(prcs.getInputStream());
                BufferedReader d = new BufferedReader(isr);

                String line;
                while ((line = d.readLine()) != null) {
                    message(line);
                }
            } catch (IOException ioe) {
                message("IO-Exception: " + ioe);
                ioe.initCause(new Throwable("IO-Exception: " + ioe.getMessage()));
                stackTracer(ioe);
            }
        }

    }

    private void runCMD(final String cmd) {

        //messageBoard();
        logger.log(Level.INFO, "Executing command: {0}", cmd);
        try {
            Runtime rt = Runtime.getRuntime();
            Process prcs = rt.exec(cmd);
            InputStreamReader isr = new InputStreamReader(prcs.getInputStream());
            BufferedReader d = new BufferedReader(isr);

            String line;
            // msgBoard.setText("Command:\r\n");
            //msgBoard.append(cmd);
            //msgBoard.append("\r\n");
            while ((line = d.readLine()) != null) {
                //  msgBoard.append(line + "\n");
            }
        } catch (IOException ioe) {
            message("IO-Exception: " + ioe);
            ioe.initCause(new Throwable("IO-Exception: " + ioe.getMessage()));
            stackTracer(ioe);
        }

    }

    public void execCMD() {
        if ((newdb = JOptionPane.showInputDialog(this, "Please enter command")) != null) {

            Thread t = new Thread() {

                public void run() {
                    messageBoard();
                    try {
                        Runtime rt = Runtime.getRuntime();
                        Process prcs = rt.exec(newdb);
                        InputStreamReader isr = new InputStreamReader(prcs.getInputStream());
                        BufferedReader d = new BufferedReader(isr);

                        String line;
                        msgBoard.setText("");
                        while ((line = d.readLine()) != null) {
                            msgBoard.append(line + "\n");
                        }
                    } catch (IOException ioe) {
                        message("IO-Exception: " + ioe);
                        ioe.initCause(new Throwable("IO-Exception: " + ioe.getMessage()));
                        stackTracer(ioe);
                    }
                }
            };
            t.start();
        }

    }

    private void setLogOnValues() {
        getLogOn().setHost(prop.getProperty("host"));
        getLogOn().setUser(prop.getProperty("user"));
        getLogOn().setUsePassword(prop.getProperty("usePassword"));
        getLogOn().setDatabase(prop.getProperty("db"));
        getLogOn().setDriver(prop.getProperty("JDBCdriver"));
        getLogOn().setUrl(prop.getProperty("JDBCurl"));
        getLogOn().setPort(prop.getProperty("DatabasePort"));
        getLogOn().setSchema(prop.getProperty("schema"));
        //getLogOn().setConnections(Integer.parseInt(prop.getProperty("connections")));
        try {
            String p = encryption.decrypt(prop.getProperty("password"), encryptionOffset);
            //System.out.println("setting password from previous session... " + p);
            getLogOn().setPassword(p);
            if (p.length() > 0) {
                getLogOn().savePassword.setSelected(true);
            }
        } catch (Exception e) {

        }
        try {
            getLogOn().setAdditionalParameters(prop.getProperty("additionalParameters"));
        } catch (Exception e) {

        }

    }

    private void defaultCursor() {
        this.setCursor(Cursor.getDefaultCursor());
        waitAMoment(1);
    }

    private void busyCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        waitMillis(200);
    }

    public void goodbye() {
        logger.log(Level.INFO, "shutting down Derby and closing DB-connection...");
        busyCursor();
        derby.stop();

        try {
            DataSource.getInstance().close();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } catch (Exception e) {
        } finally {

        }
        try {
            if (m30.getState()) {
                saveProperties();
            }
            getLogOn().closeConnections();
            stackTrace.dispose();
        } catch (Exception e) {

        }
        if (isLocalExecution()) {
            defaultCursor();
            System.exit(0);
        } else {
            defaultCursor();
            this.setVisible(false);
            this.dispose();
        }

    }

    private void saveProperties() {

        Dimension dim = this.getSize();
        prop.put("width", Integer.toString(dim.width));
        prop.put("heigth", Integer.toString(dim.height));
        prop.put("host", getLogOn().getHost());
        prop.put("user", getLogOn().getUser());
        prop.put("db", getLogOn().getDatabase());
        prop.put("usePassword", getLogOn().getUsePassword());
        if (getLogOn().isSavepwd()) {
            prop.put("password", encryption.encrypt(getLogOn().getPassword(), encryptionOffset));
            //System.out.println("Encrypting password " + logOn.getPassword() + " -> "  + prop.get("password"));
        }
        prop.put("JDBCdriver", getLogOn().getDriver());
        prop.put("JDBCurl", getLogOn().getUrl());
        prop.put("DatabasePort", getLogOn().getPort());
        prop.put("Look-n-Feel", getLnF());
        prop.put("schema", getLogOn().getSchema());
        prop.put("PropertiesFileLocation", getLogOn().getPropertiesFileLocation());

        String rdbms = getRDBMS();
        if (!" no info available ".equals(rdbms)) {

            prop.put("RDBMS", rdbms);
            prop.put("connections", Integer.toString(getLogOn().getMaxConnection()));

        } else {
            logger.log(Level.INFO, "Can not connect to DB");
        }

        try {
            prop.put("additionalParameters", getLogOn().getAdditionalParameters());
        } catch (Exception e) {

        }
        saveAdminProps();
        saveConnectProperties();
    }

    private void saveAdminProps() {

        try {

            // Does this File exist?
            String admin_dir = System.getProperty("admin.work");
            if (admin_dir == null) {
                admin_dir = System.getProperty("user.home");
            }

            FileOutputStream fops = new FileOutputStream(admin_dir + File.separator + "admin.props");
            prop.store(fops, "Properties Fredy's SqlAdmin");
            fops.close();

            fops = new FileOutputStream(admin_dir + File.separator + "adminlnf.props");
            lnfProps.store(fops, "Look and Feel Properties Fredy's SqlAdmin");
            fops.close();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving admin.props :" + e.getMessage());
            e.initCause(new Throwable("Error saving admin.props : " + e.getMessage()));
            stackTracer(e);
        }

    }

    private void saveConnectProperties() {
        Properties tProps = new Properties();
        tProps.put("JDBCdriver", prop.getProperty("JDBCdriver"));
        tProps.put("JDBCurl", prop.getProperty("JDBCurl"));
        tProps.put("DatabasePort", prop.getProperty("DatabasePort"));
        tProps.put("usePassword", prop.getProperty("usePassword"));
        try {
            tProps.put("maxRowCount", prop.getProperty("maxRowCount"));
        } catch (Exception e5) {
            tProps.put("maxRowCount", "10000");
        }
        try {
            tProps.put("additionalParameters", getLogOn().getAdditionalParameters());
        } catch (Exception e) {

        }

        tProps.put("DebugMode", "off");
        try {

            // Does this File exist?
            String admin_dir = System.getProperty("admin.work");
            if (admin_dir == null) {
                admin_dir = System.getProperty("user.home");
            }

            Path workingPath = Paths.get(admin_dir);
            if (Files.exists(workingPath) && Files.isDirectory(workingPath)) {

            } else {
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
            logger.log(Level.WARNING, " Error saving t_connect.props " + e.getMessage());
            //mb.addText("Error saving t_connect.props :" + e);
            e.initCause(new Throwable("Error saving t_connect.props : " + e.getMessage()));
            stackTracer(e);
        }

    }

    public void messageBoard() {

        Container contentPane;

        msgFrame = new JInternalFrame("MessageBoard", true, true, true, true);
        contentPane = msgFrame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        msgBoard = new JTextArea("", 10, 30);
        msgBoard.setEditable(false);
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(msgBoard);

        contentPane.add("Center", scrollpane);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());

        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                msgFrame.dispose();
            }
        });

        panel1.add(close);
        contentPane.add("South", panel1);

        msgFrame.pack();
        lc.add(msgFrame, JLayeredPane.DEFAULT_LAYER);
        msgFrame.moveToFront();
        msgFrame.setVisible(true);
    }

    private void createTable() {
        try {
            crTa = new CreateTable();

            Dimension dim = crTa.getPreferredSize();
            newFrame(crTa, "Create Table", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, crTa.cancel);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }
    }

    private void jdbcTypeMapping() {
        try {
            JdbcTypeMappingGui jtg = new JdbcTypeMappingGui(getProductName());

            Dimension dim = jtg.getPreferredSize();
            newFrame(jtg, "JDBC Type Mapping", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, null);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }
    }

    private void createTable2() {
        try {
            CreateTable2 crTa2 = new CreateTable2(getLogOn().getSchema());

            Dimension dim = crTa2.getPreferredSize();
            newFrame(crTa2, "Create Table2", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, crTa2.cancel);
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error while initiating CreateTable2 {0}", e.getMessage());
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
            //e.printStackTrace();
        }
    }

    private void createIndex() {
        try {
            CreateIndex crIdx = new CreateIndex(getLogOn().getSchema());
            Dimension dim = crIdx.getPreferredSize();
            newFrame((JPanel) crIdx, "Create Index",
                    true,
                    true,
                    true,
                    true,
                    getMouseX(),
                    getMouseY(),
                    dim.width,
                    dim.height + 20,
                    crIdx.cancel);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }

    }

    private void execSQL(String sqlQuery) {
        Connection con = null;
        Statement stmt = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            int records = stmt.executeUpdate(sqlQuery);
            message(String.format("%,d", Integer.toString(records)) + " rows affected");
        } catch (SQLException excpt) {
            message(excpt.getMessage().toString());
            excpt.initCause(new Throwable(excpt.getMessage()));
            stackTracer(excpt);
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception while closing statement {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception while closing connection {0}", e.getMessage());
                }
            }
        }
    }

    public String getInfo() {

        return "Admin is a Tool around SQL-DBs to do basic jobs" + "\n"
                + "for DB-Administrations, like:" + "\n"
                + "- a powerfull SQL-Monitor\n"
                + "- create/ drop tables" + "\n"
                + "- create  indices" + "\n"
                + "- perform sql-statements" + "\n"
                + "- create Java-Code" + "\n"
                + "- simple form" + "\n"
                + "- a guided query" + "\n"
                + "- export data into various formats\n"
                + "and other usefull things in DB-arena" + "\n"
                + "" + "\n"
                + "Admin  " + getVersion() + "\n"
                + "Fredy Fischer\n"
                + "Hulmenweg 36\n"
                + "8405 Winterthur\n"
                + "Switzerland\n"
                + "\n\n"
                + "SQL-Admintool is free Software (MIT License)\n\n"
                + "Copyright (c) 2017 Fredy Fischer sql@hulmen.ch\n"
                + "\n"
                + "Permission is hereby granted, free of charge, to any person obtaining a copy \n"
                + "of this software and associated documentation files (the \"Software\"), to deal\n"
                + "in the Software without restriction, including without limitation the rights\n"
                + "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n"
                + "copies of the Software, and to permit persons to whom the Software is\n"
                + "furnished to do so, subject to the following conditions:\n"
                + "\n"
                + "The above copyright notice and this permission notice shall be included in\n"
                + "all copies or substantial portions of the Software.\n"
                + "\n"
                + "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n"
                + "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n"
                + "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n"
                + "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n"
                + "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n"
                + "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n"
                + "SOFTWARE."
                + "\n\n"
                + "Icons used are under the followings:\n"
                + "Icons Copyright(C) 1998 by Dean S.  Jones dean@gallant.com\n"
                + "www.gallant.com/icons.htm\n\n"
                + "CalendarBean Copyright (c) by Kai T?dter\n"
                + "MSeries Copyright (c) by Martin Newstead\n\n"
                + "\n"
                + "------------------------------------------------------------------------\n"
                + "Syntaxthighlighting is based onto RSyntaxTextArea available on\n"
                + "http://www.fifesoft.com and is under this modified BSD-license\n\n"
                + "Copyright (c) 2012, Robert Futrell\n"
                + "All rights reserved.\n"
                + "\n"
                + "Redistribution and use in source and binary forms, with or without\n"
                + "modification, are permitted provided that the following conditions are met:\n"
                + "    * Redistributions of source code must retain the above copyright\n"
                + "      notice, this list of conditions and the following disclaimer.\n"
                + "    * Redistributions in binary form must reproduce the above copyright\n"
                + "      notice, this list of conditions and the following disclaimer in the\n"
                + "      documentation and/or other materials provided with the distribution.\n"
                + "    * Neither the name of the author nor the names of its contributors may\n"
                + "      be used to endorse or promote products derived from this software\n"
                + "      without specific prior written permission.\n"
                + "\n"
                + "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND\n"
                + "ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\n"
                + "WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\n"
                + "DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY\n"
                + "DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES\n"
                + "(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;\n"
                + "LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\n"
                + "ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\n"
                + "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\n"
                + "SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\n"
                + "------------------------------------------------------------------------";

    }

    private void about() {

        Container contentPane;

        aboutFrame = new JInternalFrame("About", true, true, true, true);
        contentPane = aboutFrame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        //aboutFrame.setBounds(200,200, 450, 250);

        JTextArea a = new JTextArea(24, 80);
        a.setEditable(false);
        a.setFont(new Font("Monospaced", Font.PLAIN, 12));

        a.setText(getInfo());
        a.setCaretPosition(1);
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(a);

        contentPane.add("Center", scrollpane);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());

        JButton license = new JButton("License");
        license.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                displayLicense();
            }
        });

        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                aboutFrame.dispose();
            }
        });

        JButton pay = new JButton("Pay");
        pay.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                payFor();
            }
        });

        JButton readme = new JButton("Readme");
        readme.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                displayReadMe();
            }
        });

        panel1.add(readme);
        panel1.add(license);
        panel1.add(close);
        panel1.add(pay);
        contentPane.add("South", panel1);
        aboutFrame.pack();
        lc.add(aboutFrame, JLayeredPane.DEFAULT_LAYER);
        aboutFrame.setVisible(true);

    }

    private JSplitPane statusPanel() {

        statusLine = new JLabel();
        statusLine.setText("SQL-Admin-Tool" + getVersion() + " Running on " + System.getProperty("os.name"));
        statusLine.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusLine.setOpaque(true);
        statusLine.setBackground(Color.yellow);
        statusLine.setForeground(Color.blue);
        JSplitPane panel = null;
        if (isEncapsulateLogger()) {
            panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statusLine, console);
        } else {
            panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statusLine, new JPanel());
        }
        panel.setBackground(Color.yellow);
        panel.setForeground(Color.blue);
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        return panel;

    }

    /*
     * private ImageIcon loadImage(String image) {
     *
     *    return loadImage.getImage(image);
     *  }
     */
    private void payFor() {
        Container contentPane;

        payFrame = new JInternalFrame("Pay?", true, true, true, true);
        payFrame.setBackground(Color.blue);
        contentPane = payFrame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setBackground(Color.blue);
        panel.setLayout(new BorderLayout());
        JLabel l = new JLabel(loadImage.getImage("virago.gif"));
        panel.add("North", l);

        java.awt.Font f = new java.awt.Font("Helvetica", Font.BOLD, 24);
        JLabel np = new JLabel("Admin is FREE!!", SwingConstants.CENTER);
        np.setFont(f);
        np.setForeground(Color.yellow);
        panel.add("Center", np);

        JTextArea a = new JTextArea("", 10, 35);
        a.setEditable(false);
        a.setText("In fact this product is free software (MIT-License)\n\n"
                + "But, if you want to pay something for it, because you\n"
                + "think I did a good job and this must be paid....\n\n"
                + "then I'm happy to get a little amount I could invest\n"
                + "into cool tools for my workshop\n"
                + "http://www.schweissbar.ch");

        panel.add("South", a);

        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(panel);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());

        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                payFrame.dispose();
            }
        });

        panel1.add(close);
        contentPane.add("South", panel1);

        contentPane.add("Center", scrollpane);
        payFrame.pack();
        lc.add(payFrame, JLayeredPane.DEFAULT_LAYER);
        payFrame.moveToFront();
        payFrame.setVisible(true);
    }

    private void displayReadMe() {
        Container contentPane;

        final JInternalFrame readme = new JInternalFrame("ReadME", true, true, true, true);
        contentPane = readme.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JTextArea a = new JTextArea(24, 80);
        a.setEditable(false);
        a.setFont(new Font("Monospaced", Font.PLAIN, 12));

        a.setText(getReadMe());
        a.setCaretPosition(1);
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(a);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());

        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                readme.dispose();
            }
        });

        panel1.add(close);
        contentPane.add("South", panel1);

        contentPane.add("Center", scrollpane);
        readme.pack();
        lc.add(readme, JLayeredPane.DEFAULT_LAYER);
        readme.moveToFront();
        readme.setVisible(true);

    }

    private String resourceFileReader(String fileName, String defaultText) {
        InputStream input;
        String line = null;
        StringBuilder sb = new StringBuilder();
        try {

            URL url = this.getClass().getResource(fileName);
            input = url.openStream();

            if (input != null) {
                try (InputStreamReader isr = new InputStreamReader(input); BufferedReader br = new BufferedReader(isr);) {

                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    input.close();
                }
            } else {
                sb.append("can not load file ").append(fileName).append("\r\n").append(defaultText);
            }
        } catch (IOException e) {
            sb.append("Exception can not load file ").append(fileName).append("\r\n").append(defaultText).append("\n").append(e.getMessage());
        }
        return "\n" + sb.toString();
    }

    private String getReadMe() {
        return resourceFileReader("/resources/doc/README", "Please see https://www.hulmen.ch");

    }

    private void displayLicense() {
        Container contentPane;

        licenseFrame = new JInternalFrame("MIT", true, true, true, true);
        contentPane = licenseFrame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        JTextArea a = new JTextArea(24, 80);
        a.setEditable(false);
        a.setFont(new Font("Monospaced", Font.PLAIN, 12));

        a.setText(getLicense());
        a.setCaretPosition(1);
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(a);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());

        JButton close = new JButton("Close");
        close.addActionListener((ActionEvent e) -> {
            licenseFrame.dispose();
        });

        panel1.add(close);
        contentPane.add("South", panel1);

        contentPane.add("Center", scrollpane);
        licenseFrame.pack();
        lc.add(licenseFrame, JLayeredPane.DEFAULT_LAYER);
        licenseFrame.moveToFront();
        licenseFrame.setVisible(true);

    }

    private String getLicense() {

        return resourceFileReader("/resources/doc/COPYRIGHT", "Please see https://opensource.org/licenses/MIT");

    }

    private void dataExport() {
        DataExportGui deg = new DataExportGui();
        deg.setLocationRelativeTo(this);
    }
    private String localAdminDir = "";

    /**
     * @return the localExceution
     */
    public boolean isLocalExecution() {
        return localExecution;
    }

    /**
     * @param localExceution the localExceution to set
     */
    public void setLocalExecution(boolean localExecution) {
        this.localExecution = localExecution;

    }

    /*
    we set the default values for the background
    
     */
    private void setDefaultBackground() {

        String userName = System.getProperty("user.name");
        String serverName = getLogOn().getHost();
        String databaseName = getLogOn().getDatabase();

        Connection connection = null;
        PreparedStatement search = null;
        ResultSet rs = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            search = connection.prepareStatement("select pathToImage,backGroundColor from APP.BACKGROUNDIMAGE where userName = ? and serverName = ? and databaseName = ?");
            search.setString(1, userName);
            search.setString(2, serverName);
            search.setString(3, databaseName);
            rs = search.executeQuery();
            if (rs.next()) {
                //Integer.toString(getDefaultColor().getRGB()    
                Color color = new Color(rs.getInt("backGroundColor"));
                setDefaultColor(color);
                setBgImg(rs.getString("pathToImage"));
            }

        } catch (SQLException | IOException | PropertyVetoException sqlex) {
            logger.log(Level.WARNING, "Can not read background.  Exception: {0}", sqlex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }

            if (search != null) {
                try {
                    search.close();
                } catch (SQLException e) {
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }

        }
    }

    /**
     * @return the backGroundImageFile
     */
    public String getBackGroundImageFile() {
        return backGroundImageFile;
    }

    /**
     * @param backGroundImageFile the backGroundImageFile to set
     */
    public void setBackGroundImageFile(String backGroundImageFile) {
        this.backGroundImageFile = backGroundImageFile;

        String userName = System.getProperty("user.name");
        String serverName = getLogOn().getHost();
        String databaseName = getLogOn().getDatabase();

        if (backGroundImageFile == null) {
            // we remove the file from the background
            Connection connection = null;
            PreparedStatement search = null;
            PreparedStatement write = null;
            DatabaseMetaData dmd = null;
            ResultSet rs = null;
            try {
                connection = DBconnectionPool.getInstance().getConnection();
                //System.out.println(DBconnectionPool.getInstance().getProductName() + " " + DBconnectionPool.getInstance().getProductVersion() + " " + DBconnectionPool.getInstance().getSqladminDB());
                search = connection.prepareStatement("select count(*) n from APP.BACKGROUNDIMAGE where userName = ? and serverName = ? and databaseName = ?");
                search.setString(1, userName);
                search.setString(2, serverName);
                search.setString(3, databaseName);
                rs = search.executeQuery();
                if (rs.next()) {
                    if (rs.getInt(1) > 0) {

                        // delete
                        write = connection.prepareStatement("update APP.BACKGROUNDIMAGE set pathToImage = null  where userName = ? and serverName = ? and databaseName = ?");
                        write.setString(1, userName);
                        write.setString(2, serverName);
                        write.setString(3, databaseName);
                        write.executeUpdate();
                    }
                }

            } catch (SQLException | IOException | PropertyVetoException sqlex) {
                logger.log(Level.WARNING, "Can not delete Image File.  Exception: {0}", sqlex.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                    }
                }

                if (search != null) {
                    try {
                        search.close();
                    } catch (SQLException e) {
                    }
                }

                if (write != null) {
                    try {
                        write.close();
                    } catch (SQLException e) {
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                    }
                }

            }

        } else {

            /*
        We add the value to the internal DB
        To make it able, to have an own Backgroundimage
        - per User
        - per Server
        - per Database
        
        Table: 
        create table APP.BACKGROUNDIMAGE (
               userName     VARCHAR(32) ,
               serverName   VARCHAR(256),
               databaseName VARCHAR(1024),
               pathToImage  VARCHAR(1024),
               PRIMARY KEY (userName,serverName,databaseName)
        )
             */
            // the Database is checked while loading, so the table is here
            Connection connection = null;
            PreparedStatement search = null;
            PreparedStatement write = null;
            DatabaseMetaData dmd = null;
            ResultSet rs = null;
            try {
                connection = DBconnectionPool.getInstance().getConnection();
                //System.out.println(DBconnectionPool.getInstance().getProductName() + " " + DBconnectionPool.getInstance().getProductVersion() + " " + DBconnectionPool.getInstance().getSqladminDB());
                search = connection.prepareStatement("select count(*) n from APP.BACKGROUNDIMAGE where userName = ? and serverName = ? and databaseName = ?");
                search.setString(1, userName);
                search.setString(2, serverName);
                search.setString(3, databaseName);
                rs = search.executeQuery();
                if (rs.next()) {
                    if (rs.getInt(1) == 0) {
                        // insert
                        write = connection.prepareStatement("insert into APP.BACKGROUNDIMAGE ( pathToImage,userName,serverName,databaseName) values (?,?,?,?)");
                    } else {
                        // update
                        write = connection.prepareStatement("update APP.BACKGROUNDIMAGE set pathToImage = ? where userName = ? and serverName = ? and databaseName = ?");
                    }
                }
                if (write != null) {
                    write.setString(1, backGroundImageFile);
                    write.setString(2, userName);
                    write.setString(3, serverName);
                    write.setString(4, databaseName);
                    write.executeUpdate();
                }

            } catch (SQLException | IOException | PropertyVetoException sqlex) {
                logger.log(Level.WARNING, "Can not save Image File.  Exception: {0}", sqlex.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                    }
                }

                if (search != null) {
                    try {
                        search.close();
                    } catch (SQLException e) {
                    }
                }

                if (write != null) {
                    try {
                        write.close();
                    } catch (SQLException e) {
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                    }
                }

            }
        }
    }

    private void setBgImg(String image) {

        if (image == null) {
            return;
        }
        String userName = System.getProperty("user.name");
        String serverName = getLogOn().getHost();
        String databaseName = getLogOn().getDatabase();

        Connection connection = null;
        PreparedStatement search = null;

        String bgImage = null;

        ResultSet rs = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            //System.out.println(DBconnectionPool.getInstance().getProductName() + " " + DBconnectionPool.getInstance().getProductVersion() + " " + DBconnectionPool.getInstance().getSqladminDB());
            search = connection.prepareStatement("select pathToImage n from APP.BACKGROUNDIMAGE where userName = ? and serverName = ? and databaseName = ?");
            search.setString(1, userName);
            search.setString(2, serverName);
            search.setString(3, databaseName);
            rs = search.executeQuery();
            if (rs.next()) {
                bgImage = rs.getString(1);
            }

        } catch (SQLException | IOException | PropertyVetoException sqlex) {
            logger.log(Level.WARNING, "Can not load Image File.  Exception: {0}", sqlex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                }
            }

            if (search != null) {
                try {
                    search.close();
                } catch (SQLException e) {
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                }
            }
        }
        setBackgroundImage(new File(bgImage != null ? bgImage : image));
    }

    /**
     * @return the backGroundImage
     */
    public BufferedImage getBackGroundImage() {
        return backGroundImage;
    }

    /**
     * @param backGroundImage the backGroundImage to set
     */
    public void setBackGroundImage(BufferedImage backGroundImage) {
        this.backGroundImage = backGroundImage;
        lc.setBorder(new CentredBackgroundBorder(scaler.scaleImage(backGroundImage, this.getSize())));
        lc.updateUI();
        lc.update(lc.getGraphics());

    }

    /**
     * @return the connectionEstablished
     */
    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    /**
     * @param connectionEstablished the connectionEstablished to set
     */
    public void setConnectionEstablished(boolean connectionEstablished) {
        this.connectionEstablished = connectionEstablished;
    }

    /**
     * @return the logOn
     */
    public static DataBaseLogin getLogOn() {
        return logOn;
    }

    /**
     * @param logOn the logOn to set
     */
    public void setLogOn(DataBaseLogin logOn) {
        this.logOn = logOn;

    }

    /**
     * @return the encapsulateLogger
     */
    public boolean isEncapsulateLogger() {
        return encapsulateLogger;
    }

    /**
     * @param encapsulateLogger the encapsulateLogger to set
     */
    public final void setEncapsulateLogger(boolean encapsulateLogger) {
        this.encapsulateLogger = encapsulateLogger;

    }

    private class ActionListenerImpl implements ActionListener {

        public ActionListenerImpl() {
        }

        public void actionPerformed(ActionEvent e) {
            if (dash == null) {
                dash = new sql.fredy.ui.MySQLconnections(getLogOn().getHost());
                dash.setLocal(false);
            } else {
                dash.setVisible(true);
            }

        }
    }

    /**
     * @return the jdbctypemapping
     */
    public static HashMap<String, JdbcTypeMapping> getJdbctypemapping() {
        if (jdbctypemapping == null) {           
            TypeMapping tm = new TypeMapping(getProductName());
            jdbctypemapping = tm.getMapping();
        }
        return jdbctypemapping;
    }

    /**
     * @param jdbctypemapping the jdbctypemapping to set
     */
    public void setJdbctypemapping(HashMap<String, JdbcTypeMapping> jdbctypemapping) {
        this.jdbctypemapping = jdbctypemapping;
    }
}

class mScrollPane extends JScrollPane {

    public mScrollPane() {
        super();
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BorderLayout());
        getViewport().add(p);
    }

    public Dimension getMinimumSize() {
        return new Dimension(25, 25);
    }

    public boolean isOpaque() {
        return true;
    }
}
