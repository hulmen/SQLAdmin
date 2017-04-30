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
 * RSyntaxTextArea and AutoComplete is from www.fifesoft.com 
 * ---------------- Start fifesoft License ------------------------------------- 
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
 *
 *
 *
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
import sql.fredy.sqltools.Import;
import sql.fredy.sqltools.AutoForm;
import sql.fredy.sqltools.ChangeTable;
import sql.fredy.sqltools.CreateIndex;
import sql.fredy.share.t_connect;
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
import com.bulenkov.darcula.DarculaLaf;
import sql.fredy.tools.DerbyInfoServer;
import sql.fredy.tools.Encryption;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicLookAndFeel;
import javax.swing.text.*;

//public class Admin extends JFrame implements ActionListener, MouseListener {
public class Admin extends JFrame implements ActionListener {

    private static String fredysVersion = "Version 3.0  2017-04-01";
    LoadImage loadImage = new LoadImage();

    /**
     * It's time for a new version. Because of these lots of changing in the
     * SQL-Monitor
     */
    public String getVersion() {
        return fredysVersion;
    }

    private Logger logger;
    private sql.fredy.ui.SplashScreen Splash;
    private JLabel statusLine;
    ImageIcon genImgIcon;
    JPopupMenu popup;
    public JLayeredPane lc;
    //JScrollableDesktopPane  lc;
    JInternalFrame desktop;
    JList tableList;
    String[] table;
    String newdb;
    private Properties prop;
    private Qbe qbePanel;
    private AutoForm formPanel;
    private JCheckBoxMenuItem m30;
    private Color defaultColor;
    private ChooseColor cg;
    private JMenuBar menubar;
    private JInternalFrame msgFrame, qbeFrame, createFrame, createIdxFrame, formFrame, dropperFrame, aboutFrame, colorFrame, licenseFrame, payFrame;
    private JTable tableView;
    public JCheckBox[] userChoice;
    public JTextField hostHost, hostDB, userHost, userDb, userUser, dbHost, dbUser, dbDb, User, propsHost, propsDB, propsmySQL;
    public JTextArea dbInfo, tableInfo, msgBoard;
    // public JPasswordField password,userPassword;
    public JTextField password, userPassword;
    private CreateTable crTa;
    public DataBaseLogin logOn;
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
        try {
            t_connect tc = logOn.getCon();
            if (tc.acceptsConnection()) {
                DbInfo dbi = new DbInfo(logOn.getCon());
                return dbi.getProductName();
            } else {
                return "connection failed";
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while accessing db: {0}", e.getMessage());
            e.initCause(new Throwable("connection failed"));
            //stackTracer(e);

            return " no info available ";
        }

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

        System.out.println(
                "\nThis is Fredy's admin-tool for SQL-Databases"
                + "\n--------------------------------------------\n"
                + "Admin is free Software\n\n"
                + "This is " + System.getProperty("os.name") + "\n\n"
                + "Systemproperties (to add with java -D<option> -D<option> sql.fredy.admin.Admin)\n"
                + "----------------\n"
                + "Generic Properties:\n"
                + "admin.work=<directory> Default: " + System.getProperty("user.home") + "\n"
                + "           this is the location of the config-files (admin.props, t_connect.props)\n"
                + "           I'm using: " + workDir + "\n\n"
                + "admin.image=<directory or URL> Default: sql.fredy.images \n"
                + "           this is the location where admin fetches the images from\n"
                + "           I'm using: " + imgDir + "\n"
                + "\n");

        if ((args.length > 0) && (args[0].equalsIgnoreCase("-h"))) {
            System.out.println(
                    "\nLogging Properties: (Admin uses java.util.logging-API)\n"
                    + "admin.logging.extended=YES activates Admin-Logging via SimpleLogServer\n"
                    + "                           (your System needs to support TCP/IP)\n"
                    + "                           only for Admin-Main Logs\n"
                    + "admin.logging.host=<hostname or IP-Address> Default: localhost\n"
                    + "admin.logging.port=<PortNumber>  Default: 5237\n"
                    + "admin.logging.level=<Log-Level>  Default: INFO\n"
                    + "                                 Allowed: ALL, CONFIG, FINE, FINEST, FINER, INFO, OFF, SEVERE, WARNING\n\n");
        }

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Admin a = new Admin();
            }
        });

        //Admin a = new Admin();
    }

    private void initLogging() {
        logger = Logger.getLogger("sql.fredy.admin");

    }

    /*
     This is the Derby-Server to store internal Information
     */
    private DerbyInfoServer derby;

    private ImageScaler scaler;
    sql.fredy.tools.Console console;

    public Admin() {

        //super("Fredy's SQL Admintool ");
        super("SQL Admintool ");

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
        console = new sql.fredy.tools.Console();

        // init the StackTracer
        stackTrace = new StackTracer(this, "Exception", true);
        stackTrace.cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                stackTrace.setVisible(false);
            }
        });

        // handle Window-Events
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent evt) {
                goodbye();
            }

            public void windowActivated(WindowEvent evt) {
                ;
            }

            public void windowDeactivated(WindowEvent evt) {
                ;
            }

            public void windowIconified(WindowEvent evt) {
                // mb.frame.setState(Frame.ICONIFIED);
            }

            public void windowDeiconified(WindowEvent evt) {
                ;
            }

            public void windowOpened(WindowEvent evt) {
                ;
            }

            public void windowClosed(WindowEvent evt) {
                goodbye();
            }
        });

        // setting IconImage
        try {
            this.setIconImage(loadImage.getImage("icon.gif").getImage());
        } catch (Exception whatException) {
            logger.log(Level.INFO, "Error loading Image for Frame. Exception: " + whatException.getMessage());
            whatException.initCause(new Throwable("Error loading Image for Frame"));
            //stackTracer(whatException);
            //System.exit(0);
        }

        // resize listener
        this.addComponentListener(new ComponentListener() {
            public void componentResized(ComponentEvent e) {
                if (getBackGroundImage() != null) {
                    lc.setBorder(new CentredBackgroundBorder(scaler.scaleImage(getBackGroundImage(), e.getComponent().getSize())));
                    lc.updateUI();
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

        // display Splash-Screen
       // splash();

        propertyChecks();

        // starting up the internal Derby-Server
        derby = new DerbyInfoServer();

        genImgIcon = loadImage.getImage("generated.gif");

        lc = new JDesktopPane();
        //lc = new JScrollableDesktopPane();
        lc.setOpaque(false);
        defaultColor = lc.getBackground();

        // file drops
        fileDrops();

        lc.add(login(), JLayeredPane.PALETTE_LAYER);

        JScrollPane mc = new JScrollPane(lc);
        //mc.getViewport().add(lc);

        this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(mc, BorderLayout.CENTER);

        this.getContentPane().add(statusPanel(), BorderLayout.SOUTH);

        this.setSize(Integer.parseInt(prop.getProperty("width")), Integer.parseInt(prop.getProperty("heigth")));

       // Splash.close();

        lnfchange(prop.getProperty("Look-n-Feel", "default"));

        //mb.frame.setVisible(true);
        this.setVisible(true);
    }

    public void actionPerformed(ActionEvent evt) {
        String command = evt.getActionCommand();
        if (command.equals("exit")) {
            goodbye();
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
        lc.setBorder(new EmptyBorder(0, 0, 0, 0));
        setBackGroundImageFile(null);
    }

    private void setBackgroundImage(File file) {
        try {
            //URL url = new URL("file://" + file.getPath());
            backGroundImage = ImageIO.read(file);

            backGroundImage = scaler.scaleImage(backGroundImage, this.getSize());

            setBackGroundImageFile(file.getAbsolutePath());
            CentredBackgroundBorder cbb = new CentredBackgroundBorder(backGroundImage);
            lc.setBorder(new EmptyBorder(0, 0, 0, 0));
            lc.setBorder(cbb);
            lc.updateUI();

        } catch (MalformedURLException ex) {
            logger.log(Level.INFO, "Malformed URL Exception: {0}", ex.getMessage());
        } catch (IOException iox) {
            logger.log(Level.INFO, "IO Exception: {0}", iox.getMessage());
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

            FileInputStream fip = new FileInputStream(admin_dir + File.separator + "admin.props");
            prop.load(fip);
            fip.close();
            if (prop.getProperty("JDBCdriver") == null) {
                setDefaultProperties();
            }

            try {
                setBackGroundImageFile(prop.getProperty("background", null));
            } catch (Exception e) {
                setBackGroundImageFile(null);
            }

            logger.log(Level.INFO, "Using property-file: " + admin_dir + File.separator + "admin.props");
        } catch (Exception ioex) {

            logger.log(Level.WARNING, "can not lod properties file admin.props: " + ioex.getMessage().toString());

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

        logOn = new DataBaseLogin(prop.getProperty("RDBMS"));
        logOn.setPropertiesFileLocation(prop.getProperty("PropertiesFileLocation"));

        setLogOnValues();

        contentPane.add(logOn);

        logOn.lPassword.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                // close the connections if new connection
                if (isConnectionEstablished()) {
                    logOn.closeConnections();
                }

                if (logOn.getCon() != null) {
                    try {
                        prop.put("PropertiesFileLocation", logOn.getPropertiesFileLocation());
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
                        logger.log(Level.WARNING, "can not proceed: {0}", ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });

        logOn.connect.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // close the connections if new connection
                if (isConnectionEstablished()) {
                    logOn.closeConnections();
                }

                if (logOn.getCon() != null) {

                    try {
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
                        logger.log(Level.WARNING, "can not proceed: {0}", ex.getMessage());
                        //ex.printStackTrace();
                    }
                }
            }
        });

        logOn.cancel.addActionListener(new ActionListener() {

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
                    logOn.closeConnections();
                    System.exit(0);
                }
            }
        });

        Dimension dim = new Dimension(logOn.getPreferredSize());
        log.pack();//setBounds(getMouseX(), getMouseY(), dim.width, dim.height+30);
        log.setVisible(true);
        return log;
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

                t_connect con = new t_connect(logOn.getHost(), logOn.getUser(), logOn.getPassword(), "mysql");
                if (con.getError() != null) {
                    message(con.getError());
                } else {
                    try {
                        // first insert into user-Table of mysql
                        pos = "user";
                        int records = con.stmt.executeUpdate("insert into user "
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
                        int records1 = con.stmt.executeUpdate("insert into db "
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
                    } catch (Exception excpt) {
                        String ms = "affected: " + pos + "\n" + excpt.getMessage().toString();
                        message(ms);
                        excpt.initCause(new Throwable(ms));
                        stackTracer(excpt);
                    }
                }
                //con.close();
            }
        });

        delete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                t_connect con = new t_connect(logOn.getHost(), logOn.getUser(), logOn.getPassword(), "mysql");
                if (con.getError() != null) {
                    message(con.getError());
                } else {
                    try {
                        // first delete from user-Table of mysql
                        int records = con.stmt.executeUpdate("delete from user where User = '" + userUser.getText() + "'");
                        // then delete from DB
                        int records1 = con.stmt.executeUpdate("delete from db where User = '" + userUser.getText() + "'");
                        //con.close();
                    } catch (Exception excpt) {
                        message(excpt.getMessage().toString());
                        excpt.initCause(new Throwable(excpt.getMessage().toString()));
                        stackTracer(excpt);
                    }
                }
                //con.close();
            }
        });

        list.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sqlTable sqlt = new sqlTable(logOn.getHost(), logOn.getUser(), logOn.getPassword(), "mysql", "select * from user");
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

        cg = new ChooseColor(defaultColor);
        contentPane.add("Center", cg);
        cg.select.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                lc.setBackground(cg.getColor());
                lc.updateUI();
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
        return colorFrame;

    }

    private void qbe() {

        logger.log(Level.FINEST, "launching QBE by user " + logOn.getUser());

        t_connect aConnection = logOn.getCon();
        if ((aConnection.acceptsConnection()) && (aConnection.getError() == null)) {
            qbePanel = new Qbe(aConnection, logOn.getSchema());
            qbePanel.execQuery.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    sqlTable sqlt = new sqlTable(qbePanel.getCon(), qbePanel.getQuery());
                    Dimension dim = sqlt.getPreferredSize();
                    newFrame((JPanel) sqlt, "Generated Query", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, null);
                }
            });

            Dimension dim = qbePanel.getPreferredSize();
            newFrame((JPanel) qbePanel, "Guided Query", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, qbePanel.cancel);
        }

    }

    private JInternalFrame form() {

        try {
            logger.log(Level.FINEST, "launching AutoForm by user " + logOn.getUser());

            Container contentPane;

            formFrame = new JInternalFrame("Form", true, true, true, true);
            contentPane = formFrame.getContentPane();
            contentPane.setLayout(new BorderLayout());
            formFrame.setFrameIcon(genImgIcon);

            formPanel = new AutoForm(logOn.getCon(), logOn.getSchema());

            formFrame.addComponentListener(new ComponentAdapter() {

                public void componentHidden(ComponentEvent e) {
                }

                public void componentMoved(ComponentEvent e) {
                }

                public void componentResized(ComponentEvent e) {
                    Dimension d = new Dimension();
                    d = formFrame.getSize();
                    Integer intx = new Integer(d.width);
                    Integer inty = new Integer(d.height);
                    double fx = intx.floatValue() * 0.75;
                    double fy = inty.floatValue() * 0.65;
                    Double dfx = new Double(fx);
                    Double dfy = new Double(fy);

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
            logger.log(Level.FINEST, "launching DropTable by user " + logOn.getUser());

            DropTable panel = new DropTable(logOn.getCon(), logOn.getSchema());
            Dimension dim = panel.getPreferredSize();
            newFrame((JPanel) panel, "Drop Table", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, panel.cancel);
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
                    final SqlMonitor sm = new SqlMonitor(logOn.getCon(), logOn.getSchema());

                    sm.setDatabase(logOn.getDatabase());
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

                    Dimension dim = sm.getPreferredSize();
                    newNonScrollableFrame((JPanel) sm, "SQL-Monitor", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, sm.exit, ifl);
                }
            };
            t.start();

            //newNonScrollableFrame((JPanel) sm, "SQL-Monitor", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, sm.exit);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }
    }

    private void dataSelect() {
        try {
            SelectionGui selGui = new SelectionGui(logOn.getCon());
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
            demoEnv.setCon(logOn.getCon().getCon());

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
        XMLImportGUI imp = new XMLImportGUI(logOn.getCon());
        Dimension dim = imp.getPreferredSize();
        newFrame(imp, "XML Import", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, imp.cancel);

    }

    private void iViewer() {
        ReportViewer viewer = new ReportViewer(logOn.getCon().con);
        Dimension dim = viewer.getPreferredSize();
        newFrame(viewer, "Report Viewer", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, viewer.cancel);
    }

    private void importDelimiter() {

        try {
            logger.log(Level.FINEST, "launching Import by user " + logOn.getUser());

            Import imp = new Import(logOn.getCon(), logOn.getSchema());
            Dimension dim = imp.getPreferredSize();
            newFrame(imp, "Import", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, imp.cancel);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }

    }

    private void cpTable() {

        logger.log(Level.FINEST, "launching TableCopy by user " + logOn.getUser());

        TableCopyGUI tcg = new TableCopyGUI();
        Dimension dim = tcg.getPreferredSize();
        newFrame(tcg, "Import", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, tcg.cancel);

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
                    ChangeTable ctbl = new ChangeTable(logOn.getCon(), logOn.getSchema());
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
                    DBTreeView dbtv = new DBTreeView(logOn.getCon());
                    ;
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
                databaseInfo(logOn.getDatabase());
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

        logger.log(Level.FINEST, "launching GenerateCode by user " + logOn.getUser());
        try {

            GenerateTool gt = new GenerateTool(logOn.getCon());
            gt.setPassword(logOn.getPassword());
            gt.setHost(logOn.getHost());
            gt.setUser(logOn.getUser());
            gt.setDatabase(logOn.getDatabase());
            gt.setSchema(logOn.getSchema());
            gt.askXMLFile.setText(System.getProperty("user.home")
                    + java.io.File.separator
                    + dbNameVerifier(logOn.getDatabase().toLowerCase())
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
                lc.add(setColor(), 4);
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
        m47.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                dataExport();
            }
        });

        m4.add(m47);
        if (isSocketHandler() && (sls != null)) {
            JMenuItem m48 = new JMenuItem("Simple Log Server", loadImage.getImage("logserver.gif"));
            m48.setToolTipText("makes only sense, if you let Admin use the right logging.properties");
            m48.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    sls.frame.setVisible(true);
                }
            });

            m4.add(m48);
            m4.add(new JSeparator());
        }

        if (logOn.getCon().getProductName().toLowerCase().startsWith("mysql")) {
            JMenuItem m49 = new JMenuItem("MySQL Dashboard", loadImage.getImage("dashboard.gif"));
            m49.setToolTipText("displays information about the running MySQL");
            m49.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    if (dash == null) {
                        dash = new sql.fredy.ui.MySQLconnections(logOn.getCon());
                        dash.setLocal(false);
                    } else {
                        dash.setVisible(true);
                    }

                }
            });
            m4.add(m49);
            m4.add(new JSeparator());

        }
        JMenuItem m40 = new JMenuItem("Execute Command", loadImage.getImage("enter.gif"));
        m40.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                execCMD();
            }
        });
        m4.add(m40);

        JMenuItem m41 = new JMenuItem("Import delimiter file", loadImage.getImage("documentin.gif"));
        m41.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                importDelimiter();
            }
        });
        m4.add(m41);

        JMenuItem m41x = new JMenuItem("Import XML file", loadImage.getImage("documentdraw.gif"));
        m41x.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                importXML();
            }
        });
        m4.add(m41x);

        JMenuItem m42 = new JMenuItem("Copy Table", loadImage.getImage("move.gif"));
        m42.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cpTable();
            }
        });
        m4.add(m42);

        JMenuItem m43 = new JMenuItem("Report Viewer", loadImage.getImage("document.gif"));
        m43.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                iViewer();
            }
        });

        m4.add(m43);

        JMenuItem m44 = new JMenuItem("DB-Model Visualizer", loadImage.getImage("popup.gif"));
        m44.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                generateGraph();
            }
        });
        m4.add(m44);

        JMenuItem m45 = new JMenuItem("Editor", loadImage.getImage("document.gif"));
        m45.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sql.fredy.ui.TextEditor te = new sql.fredy.ui.TextEditor();
                Dimension dim = te.getPreferredSize();
                newNonScrollableFrame((JPanel) te, "Editor", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, te.cancel, null);
            }
        });
        m4.add(m45);

        JMenuItem m46 = new JMenuItem("Notes", loadImage.getImage("documentin.gif"));
        m46.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sql.fredy.ui.Notes note = new sql.fredy.ui.Notes();
                Dimension dim = note.getPreferredSize();
                newNonScrollableFrame((JPanel) note, "Notes", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, null, null);
            }
        });
        m4.add(m46);

        JMenuItem m56 = new JMenuItem("Charset", loadImage.getImage("properties.gif"));
        m56.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                CharsetSelector sel = new CharsetSelector();
                Dimension dim = sel.getPreferredSize();
                newNonScrollableFrame((JPanel) sel, "Charset", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, sel.cancel, null);
            }
        });
        m4.add(m56);

        return m4;

    }

    private JMenuItem aboutMenu() {

        JMenuItem m5 = new JMenuItem("About", loadImage.getImage("bulb.gif"));
        m5.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                about();
            }
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

        // und schreibe die Statuszeile mit neuem Label und im titel auf welcher DB du als wer bist
        statusLine.setText("SQL-Admin-Tool" + getVersion() + " Running now on " + System.getProperty("os.name") + " Connected to database " + logOn.getDatabase() + " on server " + logOn.getHost() + " as " + logOn.getUser());
        super.setTitle("SQL Admintool  is connected to " + logOn.getDatabase() + " as User " + logOn.getUser());

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
            } else {
                if (lnf.equalsIgnoreCase("darcula")) {
                    BasicLookAndFeel darcula = new DarculaLaf();
                    try {
                        UIManager.setLookAndFeel(darcula);
                    } catch (UnsupportedLookAndFeelException ex) {
                        logger.log(Level.WARNING, "Look and Feel Exception: " + ex.getMessage());
                    }
                } else {
                    UIManager.setLookAndFeel(lnf);
                    SwingUtilities.updateComponentTreeUI(this);
                }

                // and we set the BackgroundImage
                if (getBackGroundImageFile() != null) {
                    setBackgroundImage(new File(getBackGroundImageFile()));
                }
                setLnF(lnf);
            }
        } catch (Exception exc) {
            message("can not load look & feel: " + lnf);
            exc.initCause(new Throwable("can not load look & feel: " + lnf));
            ;
            stackTracer(exc);
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
            dbi = new DbInfo(logOn.getCon());

            tableNamePattern.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    tableList.removeAll();
                    String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};
                    tableList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes, tableNamePattern.getText()));
                    tableList.updateUI();

                    tablesList.removeAll();
                    String[] tableTypes2 = {"TABLE"};
                    tablesList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes2, tableNamePattern.getText()));
                    tablesList.updateUI();

                    viewsList.removeAll();
                    String[] tableTypes3 = {"VIEW"};
                    viewsList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes3, tableNamePattern.getText()));
                    viewsList.updateUI();

                    aliasList.removeAll();
                    String[] tableTypes4 = {"ALIAS"};
                    aliasList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes4, tableNamePattern.getText()));
                    aliasList.updateUI();

                    synonymList.removeAll();
                    String[] tableTypes5 = {"SYNONYM"};
                    synonymList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes5, tableNamePattern.getText()));
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
                                dbi.createDotFile(chooser.getSelectedFile().getPath(), tableNamePattern.getText(), digIntoIt.isSelected(), logOn.getSchema());

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
                                dbi.createSchemaFile(chooser.getSelectedFile().getPath(), tableNamePattern.getText(), logOn.getSchema());

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
                    "JDBC Driver: \t" + dbi.getDriverName()
                    + dbi.getDriverVersion() + "\n");
            dbInfo.append(
                    "Database: \t" + db + "\n");
            dbInfo.append(
                    "Schema: \t" + logOn.getSchema() + "\n");
            dbInfo.append(
                    "Driver: \t" + logOn.getCon().getDriver() + "\n");
            dbInfo.append(
                    "URL: \t" + logOn.getCon().getURL());

            tableList.setListData(dbi.getTables(db, logOn.getSchema()));

            /*
             Add Mouselistener to all JList within the tabbed pane. So Detailas about a Object can be fetched from  every List.
             */
            MouseListener mouseListener = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {

                    if ((SwingUtilities.isRightMouseButton(e)) && (e.getClickCount() == 1)) {
                        //popUpMenu.show(e.getComponent(), e.getX(), e.getY());
                        String newSchema = JOptionPane.showInputDialog("Change Schema", logOn.getSchema());
                        if (newSchema != null) {

                            logOn.setSchema(newSchema);

                            tableList.removeAll();
                            String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};
                            tableList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes, tableNamePattern.getText()));
                            tableList.updateUI();

                            tablesList.removeAll();
                            String[] tableTypes2 = {"TABLE"};
                            tablesList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes2, tableNamePattern.getText()));
                            tablesList.updateUI();

                            viewsList.removeAll();
                            String[] tableTypes3 = {"VIEW"};
                            viewsList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes3, tableNamePattern.getText()));
                            viewsList.updateUI();

                            aliasList.removeAll();
                            String[] tableTypes4 = {"ALIAS"};
                            aliasList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes4, tableNamePattern.getText()));
                            aliasList.updateUI();

                            synonymList.removeAll();
                            String[] tableTypes5 = {"SYNONYM"};
                            synonymList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes5, tableNamePattern.getText()));
                            synonymList.updateUI();

                            int noTables = tableList.getModel().getSize();
                            infoLabel.setText("Schema: " + logOn.getSchema() + " Objects (" + Integer.toString(noTables) + ")");
                            infoLabel.updateUI();

                        }
                    }

                    //if (e.getClickCount() == 2) {
                    if ((SwingUtilities.isLeftMouseButton(e)) && (e.getClickCount() == 2)) {
                        int index = tableList.locationToIndex(e.getPoint());
                        TableMetaData tmd = new TableMetaData(logOn.getCon(),
                                logOn.getSchema(),
                                tableList.getModel().getElementAt(index).toString());
                        newFrame(tmd, "Info on: " + tableList.getModel().getElementAt(index).toString(), true, true, true, true, getMouseX(), getMouseY(), 500, 215, tmd.cancel);
                    }
                }
            };

            MouseListener mouseListener2 = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = tablesList.locationToIndex(e.getPoint());
                        TableMetaData tmd = new TableMetaData(logOn.getCon(),
                                logOn.getSchema(),
                                tablesList.getModel().getElementAt(index).toString());
                        newFrame(tmd, "Info on: " + tablesList.getModel().getElementAt(index).toString(), true, true, true, true, getMouseX(), getMouseY(), 500, 215, tmd.cancel);
                    }
                }
            };
            MouseListener mouseListener3 = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = viewsList.locationToIndex(e.getPoint());
                        TableMetaData tmd = new TableMetaData(logOn.getCon(),
                                logOn.getSchema(),
                                viewsList.getModel().getElementAt(index).toString());
                        newFrame(tmd, "Info on: " + viewsList.getModel().getElementAt(index).toString(), true, true, true, true, getMouseX(), getMouseY(), 500, 215, tmd.cancel);
                    }
                }
            };

            MouseListener mouseListener4 = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = aliasList.locationToIndex(e.getPoint());
                        TableMetaData tmd = new TableMetaData(logOn.getCon(),
                                logOn.getSchema(),
                                aliasList.getModel().getElementAt(index).toString());
                        newFrame(tmd, "Info on: " + aliasList.getModel().getElementAt(index).toString(), true, true, true, true, getMouseX(), getMouseY(), 500, 215, tmd.cancel);
                    }
                }
            };

            MouseListener mouseListener5 = new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = synonymList.locationToIndex(e.getPoint());
                        TableMetaData tmd = new TableMetaData(logOn.getCon(),
                                logOn.getSchema(),
                                synonymList.getModel().getElementAt(index).toString());
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
                    System.out.println("got focus " + index);
                    if (index == 1) {
                        tablesList.removeAll();
                        String[] tableTypes2 = {"TABLE"};
                        tablesList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes2, tableNamePattern.getText()));
                        tablesList.updateUI();
                    }

                    if (index == 2) {
                        viewsList.removeAll();
                        String[] tableTypes3 = {"VIEW"};
                        viewsList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes3, tableNamePattern.getText()));
                        viewsList.updateUI();
                    }

                    if (index == 3) {
                        aliasList.removeAll();
                        String[] tableTypes4 = {"ALIAS"};
                        aliasList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes4, tableNamePattern.getText()));
                        aliasList.updateUI();
                    }

                    if (index == 4) {
                        synonymList.removeAll();
                        String[] tableTypes5 = {"SYNONYM"};
                        synonymList.setListData(dbi.getTables(db, logOn.getSchema(), tableTypes5, tableNamePattern.getText()));
                        synonymList.updateUI();
                    }
                }
            };

            tabbedPane.addChangeListener(changeListener);

            JPanel panel2 = new JPanel(); // all Tables            

            panel2.setLayout(
                    new BorderLayout());
            int noTables = tableList.getModel().getSize();

            infoLabel = new JLabel("Schema: " + logOn.getSchema() + " Objects (" + Integer.toString(noTables) + ")");
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

                    lc.add(frame, 4);
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

                    lc.add(frame, 4);
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
        logOn.setHost(prop.getProperty("host"));
        logOn.setUser(prop.getProperty("user"));
        logOn.setUsePassword(prop.getProperty("usePassword"));
        logOn.setDatabase(prop.getProperty("db"));
        logOn.setDriver(prop.getProperty("JDBCdriver"));
        logOn.setUrl(prop.getProperty("JDBCurl"));
        logOn.setPort(prop.getProperty("DatabasePort"));
        logOn.setSchema(prop.getProperty("schema"));
        logOn.setConnections(Integer.parseInt(prop.getProperty("connections")));
        try {
            String p = encryption.decrypt(prop.getProperty("password"), encryptionOffset);
            logOn.setPassword(p);
            if (p.length() > 0) {
                logOn.savePassword.setSelected(true);
            }
        } catch (Exception e) {

        }

    }

    private void defaultCursor() {
        this.setCursor(Cursor.getDefaultCursor());
        waitAMoment(1);
    }

    private void busyCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        waitAMoment(1);
    }

    public void goodbye() {
        busyCursor();
        derby.stop();

        try {
            if (m30.getState()) {
                saveProperties();
            }
            logOn.closeConnections();
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
        prop.put("host", logOn.getHost());
        prop.put("user", logOn.getUser());
        prop.put("db", logOn.getDatabase());
        prop.put("usePassword", logOn.getUsePassword());
        if (logOn.isSavepwd()) {
            prop.put("password", encryption.encrypt(logOn.getPassword(), encryptionOffset));
        }
        prop.put("JDBCdriver", logOn.getDriver());
        prop.put("JDBCurl", logOn.getUrl());
        prop.put("DatabasePort", logOn.getPort());
        prop.put("Look-n-Feel", getLnF());
        prop.put("schema", logOn.getSchema());
        prop.put("PropertiesFileLocation", logOn.getPropertiesFileLocation());

        String rdbms = getRDBMS();
        if (!" no info available ".equals(rdbms)) {

            prop.put("RDBMS", rdbms);
            prop.put("connections", Integer.toString(logOn.getConnections()));

        } else {
            logger.log(Level.INFO, "Can not connect to DB");
        }

        if (getBackGroundImageFile() != null) {
            prop.put("background", getBackGroundImageFile());
        } else {
            prop.remove("background");
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
        lc.add(msgFrame, 4);
        msgFrame.moveToFront();
        msgFrame.setVisible(true);
    }

    private void createTable() {
        try {
            crTa = new CreateTable(logOn.getCon(), logOn.getSchema());

            Dimension dim = crTa.getPreferredSize();
            newFrame(crTa, "Create Table", true, true, true, true, getMouseX(), getMouseY(), dim.width, dim.height + 20, crTa.cancel);
        } catch (Exception e) {
            e.initCause(new Throwable(e.getMessage()));
            stackTracer(e);
        }
    }

    private void createTable2() {
        try {
            CreateTable2 crTa2 = new CreateTable2(logOn.getCon(), logOn.getSchema());

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
            CreateIndex crIdx = new CreateIndex(logOn.getCon(), logOn.getSchema());
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
        try {
            int records = logOn.getCon().stmt.executeUpdate(sqlQuery);
            message(Integer.toString(records) + " rows affected");
        } catch (Exception excpt) {
            message(excpt.getMessage().toString());
            excpt.initCause(new Throwable(excpt.getMessage()));
            stackTracer(excpt);
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
                +"\n\n"
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
        lc.add(aboutFrame, 4);
        aboutFrame.setVisible(true);

    }

    private JSplitPane statusPanel() {

        statusLine = new JLabel();
        statusLine.setText("SQL-Admin-Tool" + getVersion() + " Running now on " + System.getProperty("os.name"));
        statusLine.setFont(new Font("Monospaced", Font.PLAIN, 10));
        statusLine.setOpaque(true);
        statusLine.setBackground(Color.yellow);
        statusLine.setForeground(Color.blue);
        JSplitPane panel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, statusLine, console);
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
        lc.add(payFrame, 4);
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
        lc.add(readme, 4);
        readme.moveToFront();
        readme.setVisible(true);

    }

    private String getReadMe() {
        //java.net.URL url = null;
        try {
            //url = new java.net.URL(Admin.class.getResource(".." + File.separator + ".." + File.separator + ".." + File.separator + "COPYRIGHT").toString());
            //url = this.getClass().getResource("/sql/fredy/README");
            //sql.fredy.io.ReadFile rf = new sql.fredy.io.ReadFile(url.getFile());
            
            InputStream input = getClass().getResourceAsStream("/sql/fredy/README");
            sql.fredy.io.ReadFile rf = new sql.fredy.io.ReadFile(input);

            return "\n" + rf.getText();
           
        } catch (Exception e) {
            return "see http://www.hulmen.ch/admin\r\nCan not load file: " + this.getClass().getResource(File.separator + "sql" + File.separator + "fredy" + File.separator + "COPYRIGHT\r\n") + e.getMessage();
        }

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
        close.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                licenseFrame.dispose();
            }
        });

        panel1.add(close);
        contentPane.add("South", panel1);

        contentPane.add("Center", scrollpane);
        licenseFrame.pack();
        lc.add(licenseFrame, 4);
        licenseFrame.moveToFront();
        licenseFrame.setVisible(true);

    }

    private String getLicense() {
        //java.net.URL url = null;
        try {
            //url = new java.net.URL(Admin.class.getResource(".." + File.separator + ".." + File.separator + ".." + File.separator + "COPYRIGHT").toString());
            //url = this.getClass().getResource("/sql/fredy/COPYRIGHT");
            //sql.fredy.io.ReadFile rf = new sql.fredy.io.ReadFile(url.getFile());
            
            InputStream input = getClass().getResourceAsStream("/sql/fredy/COPYRIGHT");
            sql.fredy.io.ReadFile rf = new sql.fredy.io.ReadFile(input);

            return "\n" + rf.getText();
     
        } catch (Exception e) {
            return "see https://opensource.org/licenses/MIT\r\nCan not load file: " + this.getClass().getResource(File.separator + "sql" + File.separator + "fredy" + File.separator + "COPYRIGHT\r\n") + e.getMessage();
        }

    }

    protected void finalize() {
        logOn.closeConnections();
        derby.stop();
    }

    private void dataExport() {
        DataExportGui deg = new DataExportGui(logOn.getCon().con, false);
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
