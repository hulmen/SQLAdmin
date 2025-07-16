/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sql.fredy.infodb;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.TableCellRenderer;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import sql.fredy.share.JdbcTableDateRenderer;
import sql.fredy.share.JdbcTableIntegerRenderer;
import sql.fredy.share.JdbcTableNumberRenderer;
import sql.fredy.share.JdbcTableSmallIntRenderer;
import sql.fredy.share.JdbcTableStringRenderer;
import sql.fredy.share.JdbcTableTimeRenderer;
import sql.fredy.share.JdbcTableTimestampRenderer;
import sql.fredy.share.JdbcTableTinyIntRenderer;
import sql.fredy.tools.DbAutoCompletionProvider;
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.TableColumnAdjuster;

/**
 *
 * @author fredy
 */
public class DBquery extends JPanel {

    /**
     * @return the runAll
     */
    public boolean isRunAll() {
        return runAll;
    }

    /**
     * @param runAll the runAll to set
     */
    public void setRunAll(boolean runAll) {
        this.runAll = runAll;
    }

    /**
     * @return the runAtCaret
     */
    public boolean isRunAtCaret() {
        return runAtCaret;
    }

    /**
     * @param runAtCaret the runAtCaret to set
     */
    public void setRunAtCaret(boolean runAtCaret) {
        this.runAtCaret = runAtCaret;
    }

    private Logger logger = Logger.getLogger(getClass().getName());

    private boolean sqlAdmin = false;
    private boolean showLoginDialog = false;
    private boolean connectionEstablished = false;

    private RSyntaxTextArea query;
    public JMenuItem close;
    private JTextField jdbcURL, userName;
    private JPasswordField password;
    private CompletionProvider provider;

    private Connection connection = null;

    private boolean runAll = true;
    private boolean runAtCaret = false;

    private JTabbedPane mainPanel;
    private JSplitPane splitPane;
    private DBtree dbTree = null;
    //private DbTable dt;
    private JTabbedPane innerRightPanel;
    private int columnWidth;

    JPanel tablePanel;

    public DBquery() {
        setSqlAdmin(true);
        setShowLoginDialog(false);

        // we fetch the connection from the internal DB
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            logger.log(Level.FINE, "Connection to Internal DB established");

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Exception while connecting to internal Derby DB: {0}", e.getMessage());
        } catch (IOException | PropertyVetoException ex) {
            Logger.getLogger(DBverify.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }

        initGUI();
        dbtree();
    }

    public DBquery(boolean showLoginDialog) {
        setShowLoginDialog(showLoginDialog);
        setSqlAdmin(false);
        initGUI();
        mainPanel.setSelectedIndex(1);

    }

    private void initGUI() {
        this.setLayout(new BorderLayout());

        mainPanel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        innerRightPanel = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

        // Menubar only if stand alone
        if (isSqlAdmin()) {
            this.add(menuBar(), BorderLayout.NORTH);
        }

        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new BorderLayout());

        JPanel qPane = new JPanel();
        qPane.setLayout(new BorderLayout());
        query = new RSyntaxTextArea(20, 80);
        query.setFont(new Font("Monospaced", Font.PLAIN, 13));
        query.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        RTextScrollPane sp = new RTextScrollPane(query);
        //qPane.add(new JScrollPane(sp));
        qPane.add(sp,BorderLayout.CENTER);
        qPane.add(qButtonPanel(),BorderLayout.NORTH);
        innerRightPanel.addTab("SQL", qPane);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setToolTipText("DB Objects list");
        splitPane.setBottomComponent(innerRightPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(0.0d);

        mainPanel.addTab("Query", splitPane);

        if (isShowLoginDialog()) {
            mainPanel.addTab("Login", new JScrollPane(loginPanel()));
        }
        this.add(mainPanel, BorderLayout.CENTER);

        doKeyAction();
        columnsize();

    }

    private void dbtree() {

        // first, we remove the existing tree
        if (dbTree != null) {
            Component[] componentList = this.getComponents();
            for (Component tree : componentList) {
                if (tree instanceof JTree) {
                    this.remove(tree);
                }
            }

            this.revalidate();
            this.repaint();
        }

        // the actionListener listening on changes in the SqlTree       
        ActionListener a = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {

                query.requestFocus();

                // at this position, the Text will be added
                if (query.getText().length() < 1) {
                    query.append(ae.getActionCommand());
                } else {
                    String insertText = ae.getActionCommand();
                    int caretPosition = query.getCaretPosition();
                    query.insert(insertText, caretPosition);
                }

            }

        };

        dbTree = new DBtree((DbAutoCompletionProvider) provider, isSqlAdmin() ? "APP" : "%", a, getConnection());
        splitPane.setTopComponent(new JScrollPane(dbTree));
        //splitPane.setDividerLocation(0.0d);
        //splitPane.setOneTouchExpandable(true);
        splitPane.repaint();
        splitPane.updateUI();
    }

    /*
    we set the max column size
     */
    private void columnsize() {
        // Setting default max column Size
        String propertyMaxColumnWidht = System.getProperty("sql.fredy.sqltool.maxcolumnwidth");
        if (propertyMaxColumnWidht != null) {
            try {
                setColumnWidth(Integer.parseInt(propertyMaxColumnWidht));
            } catch (NumberFormatException e) {
                propertyMaxColumnWidht = null;
            }
        }

        if (propertyMaxColumnWidht == null) {
            propertyMaxColumnWidht = System.getenv("sql.fredy.sqltool.maxcolumnwidth");
        }

        propertyMaxColumnWidht = System.getProperty("sql.fredy.sqltools.maxcolumnwidth");
        if (propertyMaxColumnWidht != null) {
            try {
                setColumnWidth(Integer.parseInt(propertyMaxColumnWidht));
            } catch (NumberFormatException e) {
                propertyMaxColumnWidht = null;
            }
        }

        if (propertyMaxColumnWidht == null) {
            propertyMaxColumnWidht = System.getenv("sql.fredy.sqltools.maxcolumnwidth");
        }

        if (propertyMaxColumnWidht != null) {
            try {
                setColumnWidth(Integer.parseInt(propertyMaxColumnWidht));
            } catch (NumberFormatException e) {
                setColumnWidth(750);
            }
        }
    }

    private JPanel loginPanel() {
        Insets insets = new Insets(2, 2, 2, 2);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridwidth = 1;
        gbc.insets = insets;
        gbc.gridx = 0;

        gbc.gridy = 0;
        panel.add(new JLabel("JDBC URL"), gbc);

        gbc.gridy = 1;
        panel.add(new JLabel("Username"), gbc);

        gbc.gridy = 2;
        panel.add(new JLabel("Password"), gbc);

        jdbcURL = new JTextField(40);
        userName = new JTextField(16);
        password = new JPasswordField(32);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        gbc.gridx = 1;

        gbc.gridy = 0;
        panel.add(jdbcURL, gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(userName, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(password, gbc);

        ImageButton connect = new ImageButton(null, "plug.gif", "connect");
        ImageButton disconnect = new ImageButton(null, "unplug.gif", "disconnect");
        JPanel connectPanel = new JPanel();
        connectPanel.setLayout(new FlowLayout());
        connectPanel.setBorder(new EtchedBorder());
        connectPanel.add(connect);
        connectPanel.add(disconnect);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(connectPanel, gbc);

        password.addActionListener((ActionEvent e) -> {
            splitPane.getRightComponent().setMinimumSize(new Dimension());
            splitPane.setDividerLocation(0.0d);
            close();
            establishConnection();
            disconnect.setEnabled(true);
            connect.setEnabled(false);
            mainPanel.setSelectedIndex(0);
            
        });
        
        connect.addActionListener((ActionEvent e) -> {
            splitPane.getRightComponent().setMinimumSize(new Dimension());
            splitPane.setDividerLocation(0.0d);
            close();
            establishConnection();
            disconnect.setEnabled(true);
            connect.setEnabled(false);
            mainPanel.setSelectedIndex(0);
            
        });

        disconnect.addActionListener((ActionEvent e) -> {
            close();
            disconnect.setEnabled(false);
            connect.setEnabled(true);
        });

        return panel;
    }

    private void establishConnection() {

        if (!isSqlAdmin()) {
            try {
                setConnection(DriverManager.getConnection(jdbcURL.getText(), userName.getText(), String.valueOf(password.getPassword())));
                setConnectionEstablished(true);
            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "Can not establish a connection: {0}", sqlex.getMessage());
                setConnection(null);
            }
        } else {
            try {
                setConnection(DBconnectionPool.getInstance().getConnection());
                setConnectionEstablished(true);
            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "Can not establish a connection: {0}", sqlex.getMessage());
                setConnection(null);
            } catch (IOException iox) {
                logger.log(Level.WARNING, "IO Exception while establishing connection: {0}", iox.getMessage());
                setConnection(null);
            } catch (PropertyVetoException pex) {
                logger.log(Level.WARNING, "Proprerty Veto Exception while establishing connection: {0}", pex.getMessage());
                setConnection(null);
            }
        }
        if (getConnection() != null) {
            logger.log(Level.FINE, "Connection established");
            dbtree();
        }
    }

    private void defaultCursor() {
        query.setCursor(Cursor.getDefaultCursor());
        waitAMoment(100);
    }

    private void busyCursor() {
        query.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        waitAMoment(100);
    }

    private void waitAMoment(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    private void exec() {
        String fullQueryText = "";
        String sqlSeparators = ";";
        int startPos = 0;
        int endPos = 0;
        if (query.getSelectionStart() == query.getSelectionEnd()) {
            if (isRunAll()) {
                fullQueryText = query.getText();
            } else {
                if (isRunAtCaret()) {
                    try {
                        startPos = query.getText().lastIndexOf(sqlSeparators.trim(), query.getCaretPosition());
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Exception while finding start position {0}", e.getMessage());
                        //e.printStackTrace();
                    }
                    try {
                        endPos = query.getText().indexOf(sqlSeparators.trim(), query.getCaretPosition());
                    } catch (Exception e) {
                        logger.log(Level.WARNING, "Exception while finding end position {0}", e.getMessage());
                        //e.printStackTrace();
                    }
                    if (startPos > 0) {
                        if (endPos > 0) {
                            try {
                                fullQueryText = query.getText().substring(startPos + 1, endPos);
                                query.setSelectionStart(startPos + 1);
                                query.setSelectionEnd(endPos);
                            } catch (Exception e) {
                                logger.log(Level.WARNING, "Exception while calculating position: StartPosition = {0} EndPosition = {1}", new Object[]{startPos, endPos});
                            }
                        } else {
                            fullQueryText = query.getText().substring(startPos + 1);
                            query.setSelectionStart(startPos + 1);
                            query.setSelectionEnd(query.getText().length());
                        }
                    } else {
                        if (endPos > 0) {
                            fullQueryText = query.getText().substring(0, endPos);
                            query.setSelectionStart(0);
                            query.setSelectionEnd(endPos);
                        } else {
                            fullQueryText = query.getText();
                        }
                    }
                } else {
                    String[] pt = query.getText().split(sqlSeparators.trim());
                    fullQueryText = pt[pt.length - 1];
                }
                setRunAll(true);
                setRunAtCaret(false);
            }

        } else {
            fullQueryText = query.getSelectedText();
        }

        if (!("".equals(fullQueryText) | fullQueryText == null)) {
        } else {
            defaultCursor();
            return;
        }

        //initialise based on Preferences
        int count = innerRightPanel.getTabCount();

        int tabIndex = 0;
        for (int i = (count - 1); i > 0; i--) {
            innerRightPanel.remove(i);
        }

        //parse query strings & iterate throught them
        StringTokenizer fqt = new StringTokenizer(fullQueryText, sqlSeparators);

        while (fqt.hasMoreTokens()) {

            String queryText = fqt.nextToken().trim();
            logger.log(Level.FINE, "Query: {0}", queryText);
            if (queryText.length() > 0) {
                new Thread(() -> {
                    Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> {
                        logger.log(Level.FINE, "Exception {0}", e.getMessage()); 
                    });
                    executeQuery(queryText);
                }).start();
            }
        }

    }

    private void executeQuery(String queryText) {

        //establishConnection();
        logger.log(Level.FINE, "running query: {0}", queryText);

        ListSelectionModel listSelectionModel;

        DbTable dt = new DbTable(getConnection());
        dt.setStandalone(false);

        JPanel sqlPanel = new JPanel();
        sqlPanel.setLayout(new BorderLayout());

        if (dt.executeQuery(queryText)) {

            JTable tableView = new JTable(dt) {
                public TableCellRenderer getCellRenderer(int row, int column) {
                    if (dt.getColumnType(column) == java.sql.Types.TIMESTAMP) {
                        return new JdbcTableTimestampRenderer();
                    }
                    if (dt.getColumnType(column) == java.sql.Types.TIMESTAMP_WITH_TIMEZONE) {
                        return new JdbcTableTimestampRenderer();
                    }
                    if (dt.getColumnType(column) == java.sql.Types.DATE) {
                        return new JdbcTableDateRenderer();
                    }
                    if (dt.getColumnType(column) == java.sql.Types.TIME) {
                        return new JdbcTableTimeRenderer();
                    }
                    if (dt.getColumnType(column) == java.sql.Types.TIME_WITH_TIMEZONE) {
                        return new JdbcTableTimeRenderer();
                    }

                    if (dt.getColumnType(column) == java.sql.Types.INTEGER) {
                        return new JdbcTableIntegerRenderer();
                    }
                    if (dt.getColumnType(column) == java.sql.Types.BIGINT) {
                        return new JdbcTableIntegerRenderer();
                    }
                    if (dt.getColumnType(column) == java.sql.Types.SMALLINT) {
                        return new JdbcTableSmallIntRenderer();
                    }
                    if (dt.getColumnType(column) == java.sql.Types.TINYINT) {
                        boolean signedTinyInt = false;  // used to determine if a TINYInt is signed or not
                        try {
                            signedTinyInt = dt.getRsmd().isSigned(column + 1);
                        } catch (SQLException e) {
                            logger.log(Level.FINE, "Exception while finding out if number is signed or not {0}", e.getMessage());
                            signedTinyInt = false;
                        } catch (Exception e) {
                            logger.log(Level.WARNING, "Exception while finding out if number is signed or not {0}", e.getMessage());
                            logger.log(Level.INFO, "Column Number: {0}", column + 1);
                        }
                        return new JdbcTableTinyIntRenderer(signedTinyInt);
                    }
                    if (dt.getColumnType(column) == java.sql.Types.DECIMAL) {
                        return new JdbcTableNumberRenderer();
                    }
                    if (dt.getColumnType(column) == java.sql.Types.DOUBLE) {
                        return new JdbcTableNumberRenderer();
                    }
                    if (dt.getColumnType(column) == java.sql.Types.FLOAT) {
                        return new JdbcTableNumberRenderer();
                    }
                    if (dt.getColumnType(column) == java.sql.Types.NUMERIC) {
                        return new JdbcTableNumberRenderer();
                    }
                    if ((dt.getColumnType(column) == java.sql.Types.LONGNVARCHAR)
                            || (dt.getColumnType(column) == java.sql.Types.LONGVARCHAR)
                            || (dt.getColumnType(column) == java.sql.Types.NCHAR)
                            || (dt.getColumnType(column) == java.sql.Types.CHAR)
                            || (dt.getColumnType(column) == java.sql.Types.NVARCHAR)
                            || (dt.getColumnType(column) == java.sql.Types.VARCHAR)) {
                        return new JdbcTableStringRenderer();
                    }

                    //return super.getCellRenderer(row, column);
                    return new JdbcTableStringRenderer();

                }
            };

            tableView.getTableHeader().setReorderingAllowed(false);
            //tableView.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            //tableView.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS );
            tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            //tableView.setAutoCreateRowSorter(true);
            tableView.setRowSorter(dt.getRowSorter());

            tableView.setColumnSelectionAllowed(true);
            tableView.setCellSelectionEnabled(true);
            //tableView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            //tableView.getSelectionModel().addListSelectionListener(new SharedListSelectionHandler());
            listSelectionModel = tableView.getSelectionModel();

            listSelectionModel.setValueIsAdjusting(true);

            listSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            listSelectionModel.addListSelectionListener(tableView);
            tableView.setSelectionModel(listSelectionModel);

            TableColumnAdjuster tca = new TableColumnAdjuster(tableView);
            tca.adjustColumns();

            JScrollPane scrollpane = new JScrollPane(tableView);
            sqlPanel.add(scrollpane, BorderLayout.CENTER);

            try {
                innerRightPanel.addTab(queryText.substring(0, 15) + "...", sqlPanel);
                innerRightPanel.setSelectedComponent(sqlPanel);
                innerRightPanel.updateUI();
                innerRightPanel.repaint();
                this.updateUI();
            } catch (Exception ex) {

            }

        } else {
            logger.log(Level.INFO, "Query not executed. Error{0}", dt.getSQLError());

        }

    }

    private void doKeyAction() {

        int inFocus = JComponent.WHEN_IN_FOCUSED_WINDOW;

        InputMap iMap = this.getInputMap(inFocus);
        ActionMap aMap = this.getActionMap();

        KeyStroke f5 = KeyStroke.getKeyStroke("F5");
        KeyStroke altEnt = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.ALT_DOWN_MASK);

        iMap.put(f5, "F5");
        iMap.put(altEnt, "alt-Enter");

        Action executeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    exec();
                } catch (Exception ex) {

                }
            }
        };

        Action partialExecution = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRunAll(false);
                setRunAtCaret(false);
                exec();
            }
        };

        Action caretPositionExecution = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        };

        aMap.put("F5", executeAction);
        aMap.put("alt-Enter", partialExecution);
        aMap.put("alt-R", caretPositionExecution);
    }

    private String getQuery() {
        String q = null;
        q = query.getText();

        return q;
    }

    private JMenuBar menuBar() {
        JMenuBar menubar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem runQuery = new JMenuItem("Run");
        runQuery.addActionListener((ActionEvent e) -> {
            exec();
        });
        fileMenu.add(runQuery);

        close = new JMenuItem("Close");
        fileMenu.add(close);

        menubar.add(fileMenu);

        return menubar;
    }

    private JPanel qButtonPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder());
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        ImageButton runQueryButton = new ImageButton(null,"binocular.gif","run query");
        panel.add(runQueryButton);
        
        runQueryButton.addActionListener((ActionEvent e) -> {
            exec();
        });
        
        return panel;
    }
    
    /**
     * @return the sqlAdmin
     */
    public boolean isSqlAdmin() {
        return sqlAdmin;
    }

    /**
     * @param sqlAdmin the sqlAdmin to set
     */
    public void setSqlAdmin(boolean sqlAdmin) {
        this.sqlAdmin = sqlAdmin;
    }

    public void close() {
        if (getConnection() != null) {
            try {
                getConnection().close();
                setConnection(null);
            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "Exception while closing connection: {0}", sqlex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Options options = new Options();

        options.addOption("jdbc", true, "JDBC URL");
        options.addOption("user", true, "Username");
        options.addOption("password", true, "password");
        options.addOption("help", false, "print this message");

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("DBquery", options);

        String jdbcUrl = null;
        String userName = null;
        String password = null;

        DBquery query = new DBquery(true);

        JFrame frame = new JFrame("DB Query");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setJMenuBar(query.menuBar());
        query.close.addActionListener((e) -> {
            query.close();
            System.exit(0);
        });

        frame.getContentPane().add(query, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                query.close();
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }

        });

        frame.setLocationRelativeTo(
                null);
        frame.setVisible(
                true);
    }

    /**
     * @return the showLoginDialog
     */
    public boolean isShowLoginDialog() {
        return showLoginDialog;
    }

    /**
     * @param showLoginDialog the showLoginDialog to set
     */
    public void setShowLoginDialog(boolean showLoginDialog) {
        this.showLoginDialog = showLoginDialog;
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
     * @return the dt
     */
    /*
    public DbTable getDt() {
        return dt;
    }
    /*
    
     */
    /**
     * @param dt the dt to set
     */
    /*
    public void setDt(DbTable dt) {
        this.dt = dt;
    }
     */
    /**
     * @return the connection
     */
    public Connection getConnection() {

        if ((connection == null) && (isSqlAdmin())) {

        }

        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
        dbtree();
    }

    /**
     * @return the columnWidth
     */
    public int getColumnWidth() {
        return columnWidth;
    }

    /**
     * @param columnWidth the columnWidth to set
     */
    public void setColumnWidth(int columnWidth) {
        this.columnWidth = columnWidth;
    }

}
