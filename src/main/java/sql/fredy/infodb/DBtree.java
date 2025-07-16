/**
 * this class is derived out of SqlTree to be used in a reduced function SqlMonitor
 *
 * SqlTree is a part of Admin it displays the MetaData of a RDBMS as a tree.
 *
 * Collection of all metadata might take long and might consume a lot of memory
 * when used with professional highend RDBMS. But it's worth to see...
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
 *
 *
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
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES  MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 */
package sql.fredy.infodb;

import sql.fredy.tools.DbAutoCompletionProvider;
import sql.fredy.tools.QueryHistory;
import sql.fredy.ui.DbTreeNode;
import sql.fredy.ui.DbTreeNodeRenderer;
import sql.fredy.ui.FieldNameType;
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.LoadImage;
import sql.fredy.ui.QueryCode;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
//import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultTreeModel;
//import javax.swing.tree.TreeModel;
//import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.fife.ui.autocomplete.BasicCompletion;
import sql.fredy.metadata.FieldTypeInfo;
import sql.fredy.metadata.FunctionColumnMetaData;
import sql.fredy.metadata.FunctionMetaData;
import sql.fredy.sqltools.SqlPanel;
import sql.fredy.tools.JdbcTypeMapping;
import sql.fredy.ui.CloseableFrame;
//import org.fife.ui.autocomplete.DefaultCompletionProvider;
//import org.fife.ui.autocomplete.ShorthandCompletion;
//import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import sql.fredy.ui.SqlWords;
import sql.fredy.ui.TableColumnAdjuster;
//import sql.fredy.ui.TextEditor;

/**
 *
 * @author sql@hulmen.ch
 */
public class DBtree extends JPanel implements Runnable, FocusListener {

    private static boolean CLOSECON = false;

    private static Level LOGLEVEL = Level.FINE;

    /*
    private ImageIcon db;
    private ImageIcon schema;
    private ImageIcon table;
    private ImageIcon column;
    private ImageIcon pk;   
     */
    private Connection con;
    private DatabaseMetaData dmd;

    private HashMap<Integer, FieldTypeInfo> fieldTypes = null;
    private String database;
    private JTree tree;
    private LoadImage loadImage;
    private DbTreeNode top = null;
    private static final Logger logger = Logger.getLogger("sql.fredy.infodb.DBtree");
    private boolean autoscan = false;
    private Thread thread;
    private JScrollPane scp;

    public JButton cbPaste;
    private DbAutoCompletionProvider provider = null;

    private String databaseProductVersion = "";

    private String quote = "";
    private Color defaultBackGround;

    public DBtree(DbAutoCompletionProvider provider, String schema, ActionListener paste, Connection con) {

        logger.log(LOGLEVEL, "initialising DB Tree for Schema {0}", schema);

        if (con == null) {
            logger.log(LOGLEVEL, "DBtree Connection is null");
        }

        try {
            setCon(con);
            createSqlWords();
            setProvider(provider);
            this.setLayout(new BorderLayout());
            setDefaultBackGround(this.getBackground());
            /*
            this button is never used. It is externally triggered to paste the results of the PopUpMenu to the Query
             */
            cbPaste = new JButton("Paste");
            cbPaste.addActionListener(paste);
            cbPaste.setActionCommand("");
            loadImage = new LoadImage();
            UIManager.put("Tree.openIcon", loadImage.getImage("opendb.gif"));
            UIManager.put("Tree.leafIcon", loadImage.getImage("column.gif"));

            columnFilter = new JTextField(10);
            //schemaFilter = new JTextField(10);
            tableFilter = new JTextField(10);
            columnFilter.setText("%");
            //schemaFilter.setText("%");
            tableFilter.setText("%");
            try {

                // we find the schemas
                Vector v = new Vector();
                v.add((String) "%");
                getDmd();
                if (dmd != null) {
                    String schemaTerm = dmd.getSchemaTerm();
                    if (dmd.getSchemaTerm().length() > 0) {
                        ResultSet rs = dmd.getSchemas();
                        while (rs.next()) {
                            v.add((String) rs.getString(1));
                        }
                    }
                } else {
                    v.add("%");
                }

                ComboBoxModel cbm = new DefaultComboBoxModel(v);
                schemaFilter = new JComboBox(cbm);
                schemaFilter.setSelectedItem(schema);

            } catch (SQLException sqlex) {
                // trage hier deine Fehlermeldung ein
                logger.log(Level.WARNING, "SQL Exception while gattering metadata: {0}", sqlex.getMessage());
                logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
                logger.log(Level.INFO, "SQL Error  : {0}", sqlex.getErrorCode());

                // add just the % if the length of the combobox is 0
                if (schemaFilter == null) {
                    Vector v = new Vector();
                    v.add((String) "%");
                    ComboBoxModel cbm = new DefaultComboBoxModel(v);
                    schemaFilter = new JComboBox(cbm);
                }

            } finally {

            }

            //top = new DbTreeNode(dmd.getDatabaseProductName() + "." + getDatabase());
            top = new DbTreeNode(getDatabaseProduct() + "." + getDatabase());
            top.setType(DbTreeNode.TYPE_DATABASE);
            top.setDbName(getDatabase());
            top.setProduct(getDatabaseProduct());
            top.setProductVersion(getDatabaseProductVersion());
            top.setQuote(getQuote());

            //createTopNodes();
            thread = new Thread(this, "TREE");
            thread.start();
            tree = new JTree(top);

            //addTreeListener();
            tree.setCellRenderer(new DbTreeNodeRenderer());
            // tree.add(new TreeExpansionListenerImpl());
            javax.swing.ToolTipManager.sharedInstance().registerComponent(tree);
            //tree.setDragEnabled(true);
            scp = new JScrollPane(tree);
            scp.setName("TREE");
            doMouseThings();
            this.add(BorderLayout.CENTER, scp);
            this.add(BorderLayout.NORTH, filterPanel());
            doKeyListener();

        } catch (Exception ex) {
            logger.log(Level.WARNING, "289 Exception {0}", ex.getMessage());
            ex.printStackTrace();
        }
    }

    JTextField tableFilter, columnFilter;
    ImageButton filter;
    JComboBox schemaFilter = null;

    private JPanel filterPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(new JLabel("Schema: "), gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(schemaFilter, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Table: "), gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(tableFilter, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 2;

        filter = new ImageButton(null, "magnify.gif", "apply filter");
        filter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                applyFilter();
            }
        });

        schemaFilter.addItemListener(new ItemListener() {
            public void actionPerformed(ActionEvent e) {

            }

            @Override
            public void itemStateChanged(ItemEvent event) {
                if (event.getStateChange() == ItemEvent.SELECTED) {
                    applyFilter();
                }
            }
        });

        panel.add(filter, gbc);

        /*
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        ImageButton helpButton = new ImageButton(null, "help.gif", "Display Help");
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });

        panel.add(helpButton, gbc);

         */
        panel.setBorder(new TitledBorder("Filter"));

        return panel;
    }

    String databaseProduct = "";

    public void setDatabaseProduct(String product) {
        databaseProduct = product;

    }

    public String getDatabaseProduct() {
        if (databaseProduct == null) {
            setDbValues();
        }
        return databaseProduct;
    }

    private void addTreeListener() {
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DbTreeNode node = (DbTreeNode) tree.getLastSelectedPathComponent();

                //System.out.println("node selected " + node.getType() + " " + node.getDbName() + "." + node.getSchemaName() + "." + node.getTableName());
                if (node == null) {
                    return;
                }

                if (node.getChildCount() == 0) {
                    if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_SCHEMA)) {
                        addTables(node.getSchemaName(), node);
                        addFunctions(node.getSchemaName(), node);
                    }
                    if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
                        addColumns(node.getSchemaName(), node.getEntityType(), node.getTableName(), node);
                    }
                }

            }
        });
    }

    private void applyFilter() {

        //setAutoscan(true);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        schemaFilter.setEnabled(false);
        this.updateUI();
        //this.update(this.getGraphics());

        top.removeAllChildren();

        try {
            con = getCon();
            top = new DbTreeNode(dmd.getDatabaseProductName() + "." + getDatabase());
            top.setType(DbTreeNode.TYPE_DATABASE);
            top.setDbName(getDatabase());
            top.setProduct(getDatabaseProduct());
            top.setProductVersion(getDatabaseProductVersion());
            top.setQuote(getQuote());

        } catch (SQLException sqlex) {
            // trage hier deine Fehlermeldung ein
            logger.log(Level.WARNING, "SQL Exception while gattering metadata: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Error  : {0}", sqlex.getErrorCode());

            top = new DbTreeNode(getDatabase());
            top.setType(DbTreeNode.TYPE_DATABASE);
            top.setDbName(getDatabase());
            top.setQuote(getQuote());
        } finally {

        }

        this.remove(scp);

        tree.removeAll();
        tree = new JTree(top);
        tree.setCellRenderer(new DbTreeNodeRenderer());

        //addTreeListener();
        javax.swing.ToolTipManager.sharedInstance().registerComponent(tree);
        doMouseThings();
        scp = new JScrollPane(tree);
        scp.setName("TREE");
        this.add(BorderLayout.CENTER, scp);

        this.updateUI();
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(DBtree.class.getName()).log(Level.SEVERE, null, ex);
        }
        thread = new Thread(this, "TREE");
        thread.start();

        this.setCursor(Cursor.getDefaultCursor());
        schemaFilter.setEnabled(true);
        //this.update(this.getGraphics());
        this.updateUI();
    }

    private void waitAMoment(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    private void createTopNodes() {
        addSchemas();
    }

    private DatabaseMetaData getDmd() {
        if (dmd != null) {
            try {
                con = getCon();
                if (!con.isClosed()) {
                    dmd = con.getMetaData();
                    setDatabaseProduct(dmd.getDatabaseProductName());
                    setDatabaseProductVersion(dmd.getDatabaseProductVersion());
                }
            } catch (SQLException e) {
                logger.log(Level.INFO, "Connection closed {0}", e.getMessage());
                con = null;
                dmd = null;
            } finally {

            }
        }
        return dmd;
    }

    private void addSchemas() {
        busyCursor();
        schemaFilter.setEnabled(false);
        boolean hasSchema = false;
        DbTreeNode dmt = null;

        String schemaText = (String) schemaFilter.getSelectedItem();
        ResultSet rs = null;
        //logger.log(Level.INFO, " Default Schema: {0}", schemaText);
        if (schemaText.length() == 0) {
            schemaText = "%";
        }

        try {

            con = getCon();
            dmd = getDmd();
            if (dmd != null) {
                if (schemaText.equals("%")) {
                    rs = dmd.getSchemas(getDatabase(), schemaText);
                    //rs = dmd.getSchemas();
                    boolean processed = false;
                    while (rs.next()) {
                        hasSchema = true;
                        dmt = new DbTreeNode(rs.getString(1));
                        dmt.setType(DbTreeNode.TYPE_SCHEMA);
                        dmt.setSchemaName(rs.getString(1));
                        dmt.setDbName(getDatabase());
                        dmt.setProduct(getDatabaseProduct());
                        dmt.setQuote(getQuote());

                        //logger.log(Level.INFO, "Schema : {0}", rs.getString(1));
                        if (isAutoscan()) {
                            dmt = addTables(rs.getString(1), dmt);
                            dmt = addFunctions(rs.getString(1), dmt);
                        }

                        top.add(dmt);

                        processed = true;
                    }

                } else {
                    // only one schema
                    hasSchema = true;
                    dmt = new DbTreeNode(schemaText);
                    dmt.setType(DbTreeNode.TYPE_SCHEMA);
                    dmt.setSchemaName(schemaText);
                    dmt.setDbName(getDatabase());
                    dmt.setProduct(getDatabaseProduct());
                    dmt = addTables(schemaText, dmt);
                    dmt = addFunctions(schemaText, dmt);
                    dmt.setQuote(getQuote());
                    top.add(dmt);
                }

                if (!hasSchema) {
                    logger.log(LOGLEVEL, "(Info) no schema");
                    top = addTables("%", top);
                    top = addFunctions("%", top);
                }
            }
        } catch (SQLException sqlex) {
            // trage hier deine Fehlermeldung ein
            logger.log(Level.WARNING, "Error when gathering schemas : {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Error  : {0}", sqlex.getErrorCode());
            top = addTables("%a", top);
        } catch (Exception ex) {
            logger.log(Level.WARNING, "(non SQL Exception) Error when gathering schemas : {0}", ex.getMessage());
            logger.log(Level.INFO, "Database: {0}\nSchema: {1}", new Object[]{getDatabase(), (String) schemaFilter.getSelectedItem()});
            logger.log(Level.INFO, "(Exception) no schema");
            top = addTables("%", top);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    logger.log(Level.WARNING, "SQL Exception while closing connection : {0}", ex.getMessage());
                    logger.log(Level.INFO, "Database: {0}\nSchema: {1}", new Object[]{getDatabase(), (String) schemaFilter.getSelectedItem()});

                }
            }

        }
        schemaFilter.setEnabled(true);
        normalCursor();

    }

    private DbTreeNode addTables(String schema, DbTreeNode dmt) {

        busyCursor();
        String[] tableTypes = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};
        DbTreeNode type = null;
        DbTreeNode table = null;
        boolean first = true;
        ResultSet rs = null;

        try {
            dmd = getDmd();
            if (dmd != null) {
                for (int i = 0; i < tableTypes.length; i++) {

                    rs = dmd.getTables(database, schema, tableFilter.getText(), new String[]{tableTypes[i]});
                    first = true;
                    type = null;
                    while (rs.next()) {
                        if (first) {
                            type = new DbTreeNode(tableTypes[i]);
                            type.setDbName(database);
                            type.setSchemaName(schema);
                            //type.setType(DbTreeNode.TYPE_TABLE);
                            type.setTtype(tableTypes[i]);
                            type.setProduct(getDatabaseProduct());
                            type.setProductVersion(getDatabaseProductVersion());
                            type.setQuote(getQuote());
                            first = false;
                        }
                        logger.log(Level.FINE, "Schema: {0} Type: {1} Table: {2}", new Object[]{schema, tableTypes[i], rs.getString(3)});
                        table = new DbTreeNode(rs.getString(3));
                        table.setDbName(database);
                        table.setSchemaName(schema);
                        table.setTableName(rs.getString(3));
                        table.setType(DbTreeNode.TYPE_ENTITY);
                        table.setEntityType(tableTypes[i]);
                        table.setProduct(getDatabaseProduct());
                        table.setProductVersion(getDatabaseProductVersion());
                        table.setQuote(getQuote());

                        if (isAutoscan()) {
                            table = addColumns(schema, tableTypes[i], rs.getString(3), table);
                        }

                        type.add(table);
                    }
                    if (type != null) {
                        dmt.add(type);
                    }
                }
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQLException when gathering meta data: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    logger.log(Level.WARNING, "SQL Exception while closing resultset : {0}", ex.getMessage());
                    logger.log(Level.INFO, "Database: " + getDatabase() + "\nSchema: " + (String) schemaFilter.getSelectedItem());

                }
            }
        }
        normalCursor();
        logger.log(Level.FINE, "tables added");
        return dmt;
    }

    private DbTreeNode addColumns(String schema, String type, String table, DbTreeNode dmt) {
        //logger.log(Level.INFO, "Adding column for Database : {0} Schema {1} Table {2}", new Object[]{getDatabase(), schema, table});
        DbTreeNode column;
        ResultSet rs = null;
        busyCursor();
        /*
         first we create a list of columns beeing part of the primary key
         */
        ArrayList<String> primaryKey = new ArrayList();

        try {
            con = getCon();
            dmd = getDmd();
            rs = dmd.getPrimaryKeys(getDatabase(), schema, table);
            while (rs.next()) {
                primaryKey.add(rs.getString(4));
                logger.log(Level.FINE, "Found Primary Key: " + table + "." + rs.getString(4));
            }
            //rs.close();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQLException when gathering meta data primary keys: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    logger.log(Level.WARNING, "SQL Exception while closing resultset : {0}", ex.getMessage());
                    logger.log(Level.INFO, "Database: {0}\nSchema: {1}", new Object[]{getDatabase(), (String) schemaFilter.getSelectedItem()});

                }
            }

            try {
                getDmd();
                HashMap<String, JdbcTypeMapping> mapping = sql.fredy.admin.Admin.getJdbctypemapping();
                rs = dmd.getColumns(getDatabase(), schema, table, columnFilter.getText());
                while (rs.next()) {
                    //logger.log(Level.INFO, "Column : {0}",rs.getString("COLUMN_NAME"));

                    column = new DbTreeNode(rs.getString("COLUMN_NAME"));

                    column.setProduct(getDatabaseProduct());
                    column.setProductVersion(getDatabaseProductVersion());

                    column.setDbName(database);
                    column.setSchemaName(schema);
                    column.setTableName(table);
                    column.setType(DbTreeNode.TYPE_COLUMN);

                    column.setColumnName(rs.getString("COLUMN_NAME"));
                    column.setColumnDataType(rs.getInt("DATA_TYPE"));

                    JdbcTypeMapping jtm = (JdbcTypeMapping) mapping.get(column.getJdbcTypeName());
                    column.setColumnType(jtm.getDbProductTypeName());
                    column.setProductTypeName(rs.getString("TYPE_NAME"));

                    column.setLength(rs.getInt("COLUMN_SIZE"));
                    column.setDecimalDigits(rs.getInt("DECIMAL_DIGITS"));
                    column.setNumPrec(rs.getInt("NUM_PREC_RADIX"));
                    column.setColumnDefault(rs.getString("COLUMN_DEF"));
                    column.setQuote(getQuote());

                    /*
                  there is a speciality with SQLlite and Fieldlength
                  as CHARACTER,VARCHAR,VARYING CHARACTER,NCHAR,NATIVE CHARACTER,NVARCHAR,TEXT and CLOB 
                  are simply stored as TEXT and the Description goes into the Type...                
                     */
                    if ((dmd.getDatabaseProductName().toLowerCase().startsWith("sqlite")) && ((column.getColumnType().toLowerCase().contains("char")))) {
                        int openBracket = column.getColumnType().indexOf("(");
                        int closedBracket = column.getColumnType().indexOf(")");
                        try {
                            column.setLength(Integer.parseInt(column.getColumnType().substring(openBracket + 1, closedBracket)));
                        } catch (Exception convertException) {
                            logger.log(Level.INFO, "(SQLite) Error when fiddeling out the fieldlenght for " + column.getColumnType() + " " + column.getColumnType().substring(openBracket + 1, closedBracket));
                        }
                        column.setColumnType(column.getColumnType().substring(0, openBracket));
                    }

                    if (rs.getString("IS_NULLABLE").equalsIgnoreCase("YES")) {
                        column.setNullable(true);
                    } else {
                        column.setNullable(false);
                    }
                    try {
                        if (rs.getString("IS_AUTOINCREMENT").equalsIgnoreCase("YES")) {
                            column.setAutoIncrement(true);
                        } else {
                            column.setAutoIncrement(false);
                        }
                    } catch (Exception e) {
                        column.setAutoIncrement(false);
                    }

                    // is this column part of the primary key?
                    column.setPrimaryKey(false);
                    for (String t : primaryKey) {
                        if (t.equals(column.getColumnName().replace("\"", ""))) {
                            column.setPrimaryKey(true);
                            break;
                        }
                    }

                    dmt.add(column);
                }
            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "SQLException when gathering meta data: {0}", sqlex.getMessage());
                logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
                logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException ex) {
                        logger.log(Level.WARNING, "SQL Exception while closing resultset : {0}", ex.getMessage());
                        logger.log(Level.INFO, "Database: {0}\nSchema: {1}", new Object[]{getDatabase(), (String) schemaFilter.getSelectedItem()});

                    }
                }
            }
        }
        normalCursor();
        return dmt;
    }

    private DbTreeNode addFunctions(String schema, DbTreeNode dmt) {
        busyCursor();
        DbTreeNode function = null;
        boolean first = true;
        ResultSet rs = null;
        //logger.log(Level.INFO, " adding Functions");
        ArrayList<FunctionMetaData> functions = new ArrayList();

        DbTreeNode funktionen = new DbTreeNode("Functions");
        funktionen.setDbName(database);
        funktionen.setSchemaName(schema);
        funktionen.setType(DbTreeNode.TYPE_FUNCTION);
        funktionen.setFunctionName("Functions");
        funktionen.setProduct(getDatabaseProduct());
        funktionen.setProductVersion(getDatabaseProductVersion());
        funktionen.setQuote(getQuote());

        try {
            dmd = getDmd();
            rs = dmd.getFunctions(getDatabase(), schema, "%");
            while (rs.next()) {
                FunctionMetaData fmd = new FunctionMetaData();
                fmd.setFunctionCatalog(rs.getString(1));
                fmd.setFunctionSchema(rs.getString(2));
                fmd.setFunctionName(rs.getString(3));
                fmd.setRemarks(rs.getString(4));
                fmd.setFunctionType(rs.getShort(5));
                fmd.setSpecificName(rs.getString(6));
                fmd.setColumnMetaData(getFunctionColumnMetaData(fmd));

                function = new DbTreeNode(fmd.getFunctionName());
                function.setFunctionMetaData(fmd);
                function.setDbName(getDatabase());
                function.setSchemaName(schema);
                function.setFunctionName(fmd.getFunctionName());
                function.setType(DbTreeNode.TYPE_SINGLE_FUNCTION);
                function.setProduct(getDatabaseProduct());
                function.setProductVersion(getDatabaseProductVersion());
                function.setQuote(getQuote());

                // we do not add the columsn because we can not detected ( or do not kow how to... )                
                //addFunctionColumns(fmd, function);               
                funktionen.add(function);

            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQLException when gathering meta data: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException ex) {
                    logger.log(Level.WARNING, "SQL Exception while closing resultset : {0}", ex.getMessage());
                    logger.log(Level.INFO, "Database: {0}\nSchema: {1}", new Object[]{getDatabase(), (String) schemaFilter.getSelectedItem()});

                }
            }
        }
        dmt.add(funktionen);
        normalCursor();
        return dmt;
    }

    private HashMap<String, FunctionColumnMetaData> getFunctionColumnMetaData(FunctionMetaData fmd) {
        HashMap<String, FunctionColumnMetaData> detailMap = new HashMap();
        ResultSet rsDet = null;

        try {
            con = getCon();
            dmd = getDmd();
            rsDet = dmd.getFunctionColumns(fmd.getFunctionCatalog(), fmd.getFunctionSchema(), fmd.getFunctionName(), "%");
            while (rsDet.next()) {
                FunctionColumnMetaData fcmd = new FunctionColumnMetaData();
                fcmd.setFunctionCatalog(rsDet.getString(1));
                fcmd.setFunctionSchema(rsDet.getString(2));
                fcmd.setFunctionName(rsDet.getString(3));
                fcmd.setColumnName(rsDet.getString(4));
                fcmd.setColumnType(rsDet.getShort(5));
                fcmd.setDataType(rsDet.getInt(6));
                fcmd.setTypeName(rsDet.getString(7));
                fcmd.setPrecision(rsDet.getInt(8));
                fcmd.setLength(rsDet.getInt(9));
                fcmd.setScale(rsDet.getShort(10));
                fcmd.setRadix(rsDet.getShort(10));
                fcmd.setNullable(rsDet.getShort(12));
                fcmd.setRemarks(rsDet.getString(13));
                fcmd.setCharOctetLength(rsDet.getInt(14));
                fcmd.setOrdinalPosition(rsDet.getInt(15));
                fcmd.setIsNullable(rsDet.getString(16));
                fcmd.setSpecificName(rsDet.getString(17));
                detailMap.put(fcmd.getColumnName(), fcmd);
            }

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQLException when gathering meta data: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
        } finally {
            if (rsDet != null) {
                try {
                    rsDet.close();
                } catch (SQLException ex) {
                    logger.log(Level.WARNING, "SQLException when closing ResultSet on gathering FunctionMetaData: {0}", ex.getMessage());
                    logger.log(Level.INFO, "SQL Status : {0}", ex.getSQLState());
                    logger.log(Level.INFO, "SQL Fehler : {0}", ex.getErrorCode());
                }
            }
        }
        return detailMap;

    }

    private DbTreeNode addFunctionColumns(FunctionMetaData fmd, DbTreeNode dmt) {
        DbTreeNode column;

        for (String functionName : fmd.getColumnMetaData().keySet()) {

            FunctionColumnMetaData fcmd = (FunctionColumnMetaData) fmd.getColumnMetaData().get(functionName);
            column = new DbTreeNode(fcmd.getFunctionName());
            column.setProduct(getDatabaseProduct());
            column.setProductVersion(getDatabaseProductVersion());

            column.setDbName(database);
            column.setSchemaName(fmd.getFunctionSchema());
            column.setTableName(fcmd.getFunctionName());
            column.setFunctionName(fcmd.getFunctionName());
            column.setType(DbTreeNode.TYPE_COLUMN);

            column.setColumnName(fcmd.getColumnName());
            column.setColumnType(fcmd.getTypeName());
            column.setColumnDataType(fcmd.getDataType());
            column.setLength(fcmd.getLength());
            column.setDecimalDigits(fcmd.getPrecision());
            column.setNumPrec(fcmd.getRadix());
            column.setColumnDefault("");
            column.setNullable(false);

            column.setAutoIncrement(false);
            column.setPrimaryKey(false);
            column.setQuote(getQuote());

            //System.out.println("Function " +fcmd.getFunctionName() + " Column-Type: "  + fcmd.getColumnType());
            dmt.add(column);
        }

        return dmt;
    }

    /**
     * @return the con
     */
    public Connection getCon() {
        return con;
    }

    public void setCon(Connection con) {
        if (con != null) {
            ResultSet rs = null;
            this.con = con;
            try {
                dmd = con.getMetaData();
                setDatabase(con.getCatalog());
                setDatabaseProduct(dmd.getDatabaseProductName());
                setDatabaseProductVersion(Integer.toString(dmd.getDatabaseMajorVersion()) + "." + Integer.toString(dmd.getDatabaseMinorVersion()));
                String catalog = con.getCatalog();

                // Derby does not support Catalog entries
                if (catalog == null) {
                    catalog = getDatabaseProduct();
                }
                setDatabase(catalog);

                setQuote(dmd.getIdentifierQuoteString());

                rs = dmd.getTypeInfo();
                fieldTypes = new HashMap<>();
                while (rs.next()) {
                    FieldTypeInfo fti = new FieldTypeInfo();
                    fti.setTypName(rs.getString(1));
                    fti.setAutoIncrement(rs.getBoolean(12));
                    fti.setDataType(rs.getInt(2));
                    fieldTypes.put(fti.getDataType(), fti);
                }

            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "can not read DataBaseMetaData {0}", sqlex.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqlex) {
                        logger.log(Level.WARNING, "Exception on close {0}", sqlex.getMessage());
                    }
                }
            }
        }
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        if (database == null) {
            setDbValues();
        }
        return database;
    }

    /**
     * @param dataase the database to set
     */
    public void setDatabase(String database) {

        this.database = database;

        // in case we are running derby, we try to find out the latest item of the filepath,
        /*
        if (getDatabaseProduct().toLowerCase().contains("derby")) {
            String[] s;
            if (database.contains("/")) {
                s = database.split("/");
            } else {
                s = database.split(File.separator);
            }
            if (s.length > 0) {
                this.database = s[s.length - 1];
            }
        }
         */
    }

    private ImageIcon loadImage(String image) {
        return loadImage.getImage(image);

    }

    private void busyCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    private void normalCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    public void doCompletionProvider() {

        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        TreePath[] paths = tree.getSelectionPaths();
        DbTreeNode node = (DbTreeNode) tree.getLastSelectedPathComponent();

        if (node.isLeaf()) {
            node = (DbTreeNode) node.getParent();
        }

        walk(node);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    }

    private void walk(DbTreeNode node) {

        addCompletion(node);

        Enumeration children = node.children();
        if (children != null) {
            while (children.hasMoreElements()) {
                walk((DbTreeNode) children.nextElement());
            }
        } else {
            node = (DbTreeNode) node.getLastLeaf();
            addCompletion(node);
        }

        //provider.addCompletion(new ShorthandCompletion(provider, completion.toString(), completion.toString()));
    }

    HashMap<String, String> completions = new HashMap();

    private void addCompletion2(String c) {
        if (!completions.containsKey(c)) {
            provider.addCompletion(new BasicCompletion(provider, c));
            completions.put(c, "0");
        }
    }

    private void addCompletion(DbTreeNode node) {
        //StringBuilder sb;

        if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_SCHEMA)) {
            //provider.addCompletion(new ShorthandCompletion(provider, node.getSchemaName(), node.getSchemaName()));
            addCompletion2(node.getSchemaName());
        }

        if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
            addCompletion2(node.getSchemaName() + "." + node.getTableName());
            addCompletion2(node.getTableName());
        }
        if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_TABLE)) {
            addCompletion2(node.getSchemaName() + "." + node.getTableName());
            addCompletion2(node.getTableName());
        }
        if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_VIEW)) {
            addCompletion2(node.getSchemaName() + "." + node.getTableName());
            addCompletion2(node.getTableName());
        }

        if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
            /*
             sb = new StringBuilder();
             sb.append(node.getSchemaName()).append(".").append(node.getTableName()).append(".").append(node.getColumnName());
             sb.append(node.getTableName()).append(".").append(node.getColumnName());
             provider.addCompletion(new ShorthandCompletion(provider, sb2.toString(), sb.toString()));
             */
            addCompletion2(node.getTableName() + "." + node.getColumnName());
            List l = provider.getCompletionByInputText(node.getColumnName());
            try {
                if ((l == null) || (l.isEmpty())) {
                    addCompletion2(node.getColumnName());
                }
            } catch (Exception e) {
                addCompletion2(node.getColumnName());
            }
        }

    }

    private DbTreeNode getSelectedTreeNode() {
        return (DbTreeNode) tree.getLastSelectedPathComponent();
    }

    private ArrayList<FieldNameType> getFieldNameTypes(DbTreeNode dmt) {
        ArrayList<FieldNameType> fieldNameTypes = new ArrayList();

        // the Tree Pointer is already on a Table Niveau so we go from here
        int noChildren = dmt.getChildCount();
        for (int i = 0; i < noChildren; i++) {
            DbTreeNode d = (DbTreeNode) dmt.getChildAt(i);
            if (d.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                FieldNameType f = new FieldNameType();
                f.setDb(d.getDbName());
                f.setName(d.getColumnName());
                f.setSchema(d.getSchemaName());
                f.setOriginalColumnName(d.getColumnName());
                f.setTable(d.getTableName());
                f.setAutoIncrement(d.isAutoIncrement());
                f.setPrimaryKey(d.isPrimaryKey());
                f.setLength(d.getLength());
                switch (d.getColumnDataType()) {
                    case Types.CHAR:
                        f.setType("String");
                        break;
                    case Types.VARCHAR:
                        f.setType("String");
                        break;
                    case Types.LONGVARCHAR:
                        f.setType("String");
                        break;
                    case Types.BIT:
                        f.setType("boolean");
                        break;
                    case Types.BOOLEAN:
                        f.setType("boolean");
                        break;
                    case Types.TINYINT:
                        f.setType("int");
                        break;
                    case Types.SMALLINT:
                        f.setType("short");
                        break;
                    case Types.INTEGER:
                        f.setType("int");
                        break;
                    case Types.BIGINT:
                        f.setType("long");
                        break;
                    case Types.NUMERIC:
                        f.setType("BigDecimal");
                        break;
                    case Types.DECIMAL:
                        f.setType("float");
                        break;
                    case Types.FLOAT:
                        f.setType("float");
                        break;
                    case Types.DOUBLE:
                        f.setType("double");
                        break;
                    case Types.REAL:
                        f.setType("float");
                        break;
                    case Types.DATE:
                        f.setType("java.sql.Date");
                        break;
                    case Types.TIMESTAMP:
                        f.setType("java.sql.Timestamp");
                        break;
                    case Types.TIME:
                        f.setType("java.sql.Time");
                        break;
                    default:
                        f.setType("String");
                        break;
                }

                fieldNameTypes.add(f);
            }
        }

        return fieldNameTypes;
    }

    private TreePath selPath;

    private JPopupMenu popUpMenu;
    private JMenuItem countRows;

    private void doMouseThings() {
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        popUpMenu = new JPopupMenu();

        JMenuItem jm1 = new JMenuItem("Code Completion");
        jm1.setToolTipText("adds this branch to the code completion. On big DBs this might take long and consume much memory");
        popUpMenu.add(jm1);
        jm1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doCompletionProvider();
            }
        });

        JMenu msSql = new JMenu("MS SQL Server");
        JMenuItem code = new JMenuItem("Code");

        code.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // make sure we are on a table level
                DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
                if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
                    QueryCode qc = new QueryCode(createSelectNames(false, null), getFieldNameTypes(dmt));
                }
                if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                    dmt = (DbTreeNode) dmt.getParent();
                    QueryCode qc = new QueryCode(createSelectStatement(0), getFieldNameTypes(dmt));
                }

            }
        });

        countRows = new JMenuItem("# rows in Table");
        countRows.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                noRows();
            }
        });

        popUpMenu.add(tablePopUp());
        popUpMenu.add(rowFilters());
        popUpMenu.add(dmlMenu());
        popUpMenu.add(ddlMenu());
        popUpMenu.add(definitionMenu());
        try {
            con = getCon();
            dmd = getDmd();
            if (dmd != null) {
                if (dmd.getDatabaseProductName().toLowerCase().startsWith("microsoft")) {
                    popUpMenu.add(msSql);
                }
            }
        } catch (SQLException s) {
            logger.log(Level.WARNING, "SQL Exception while getting productname : {0}", s.getMessage());
        } finally {

        }

        // collapse
        JMenuItem collapse = new JMenuItem("Collapse");
        collapse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DbTreeNode dmta = (DbTreeNode) selPath.getLastPathComponent();
                    DbTreeNode dmt = (DbTreeNode) dmta.getParent();

                    tree.collapsePath(selPath.getParentPath());
                    //tree.collapsePath(selPath);

                } catch (Exception treeex) {
                    logger.log(Level.WARNING, "Exception while collapse tree {0}", treeex.getMessage());
                }
            }
        });

        // Expand
        JMenuItem expand = new JMenuItem("Expand");
        expand.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DbTreeNode dmta = (DbTreeNode) selPath.getLastPathComponent();
                    DbTreeNode dmt = (DbTreeNode) dmta.getParent();

                    tree.expandPath(selPath);
                    tree.updateUI();

                } catch (Exception treeex) {

                }
            }
        });

        // refresh
        JMenuItem refresh = new JMenuItem("Refresh");
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    DbTreeNode node = (DbTreeNode) selPath.getLastPathComponent();
                    //DbTreeNode node = (DbTreeNode) dmta.getParent();

                    node.removeAllChildren();

                    if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_SCHEMA)) {
                        addTables(node.getSchemaName(), node);
                        addFunctions(node.getSchemaName(), node);
                        tree.updateUI();
                    }
                    if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
                        addColumns(node.getSchemaName(), node.getEntityType(), node.getTableName(), node);
                        tree.updateUI();
                    }
                    if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_DATABASE)) {
                        createTopNodes();
                        tree.updateUI();
                    }

                } catch (Exception treeex) {

                }
            }
        });

        // Tree Walker
        JMenuItem johnny = new JMenuItem("Tree Walker");
        johnny.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    walkTree();
                } catch (Exception treeex) {

                }
            }
        });

        popUpMenu.add(new JSeparator());
        popUpMenu.add(code);
        popUpMenu.add(new JSeparator());
        popUpMenu.add(jm1);
        popUpMenu.add(new JSeparator());
        popUpMenu.add(countRows);
        popUpMenu.add(new JSeparator());
        popUpMenu.add(expand);
        popUpMenu.add(collapse);
        popUpMenu.add(new JSeparator());
        popUpMenu.add(refresh);
        //popUpMenu.add(johnny);

        JMenuItem objectDetails = new JMenuItem("Object Children");
        objectDetails.setToolTipText("a list of all children of this object");
        popUpMenu.add(new JSeparator());
        popUpMenu.add(objectDetails);
        objectDetails.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getChildren();
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {

                selPath = tree.getPathForLocation(e.getX(), e.getY());
                if (selPath != null) {

                    // row counter only for tables, views and entities
                    DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
                    addChildren(dmt);

                    if ((dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_TABLE))
                            || (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_VIEW))
                            || (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY))) {
                        countRows.setEnabled(true);
                    } else {
                        countRows.setEnabled(false);
                    }

                    int centerButton = e.BUTTON2;
                    if (MouseInfo.getNumberOfButtons() < 3) {
                        centerButton = 0;
                    }

                    // add popup if once center click  removed.
                    if ((e.getButton() == centerButton) && (e.getClickCount() == 1)) {
                        //    popUpMenu.show(e.getComponent(), e.getX(), e.getY());
                    }

                    // 1 click with the left Button
                    if ((SwingUtilities.isLeftMouseButton(e)) && (e.getClickCount() == 1)) {
                        // we mark the leaf
                        dmt = (DbTreeNode) selPath.getLastPathComponent();
                    }
                    // 1 click with the right Button shows popup menu
                    if ((SwingUtilities.isRightMouseButton(e)) && (e.getClickCount() == 1)) {
                        popUpMenu.show(e.getComponent(), e.getX(), e.getY());
                    }

                    // 1 click with the Center Button
                    /*
                    if ((SwingUtilities.isMiddleMouseButton(e)) && (e.getClickCount() == 1)) {
                        popUpMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                     */
                    // 2 clicks with the left Button
                    if ((SwingUtilities.isLeftMouseButton(e)) && (e.getClickCount() == 2)) {
                        pasteSelectedLeaf();
                    }

                    // 2 clicks with the right Button                    
                    // if ((SwingUtilities.isRightMouseButton(e)) && (e.getClickCount() == 2)) {
                    // }
                    // 2 clicks with the Center Button
                    if ((SwingUtilities.isMiddleMouseButton(e)) && (e.getClickCount() == 1)) {
                        pasteSelectedLeafQualified();
                    }

                }
            }
        }
        );
    }

    /*
     This adds Key-Listener to the JTree. 
     */
    boolean waitforkey = false;
    String textToWaitFor = "";

    public void doKeyListener() {

        int inFocus = JComponent.WHEN_IN_FOCUSED_WINDOW;
        //int inFocus = JComponent.WHEN_FOCUSED;
        InputMap iMap = this.getInputMap(inFocus);
        ActionMap aMap = this.getActionMap();

        KeyStroke ctrlS = KeyStroke.getKeyStroke("control S");
        KeyStroke ctrlD = KeyStroke.getKeyStroke("control D");
        KeyStroke ctrlA = KeyStroke.getKeyStroke("control A");
        KeyStroke ctrlB = KeyStroke.getKeyStroke("control B");
        KeyStroke ctrlF = KeyStroke.getKeyStroke("control F");
        KeyStroke ctrlN = KeyStroke.getKeyStroke("control N");
        KeyStroke ctrlH = KeyStroke.getKeyStroke("control H");

        KeyStroke alt1 = KeyStroke.getKeyStroke("alt 1");
        KeyStroke altA = KeyStroke.getKeyStroke("alt A");
        KeyStroke altB = KeyStroke.getKeyStroke("alt B");
        KeyStroke altC = KeyStroke.getKeyStroke("alt C");
        KeyStroke altD = KeyStroke.getKeyStroke("alt D");
        KeyStroke altE = KeyStroke.getKeyStroke("alt E");
        KeyStroke altF = KeyStroke.getKeyStroke("alt F");
        KeyStroke altG = KeyStroke.getKeyStroke("alt G");
        KeyStroke altN = KeyStroke.getKeyStroke("alt N");
        KeyStroke altH = KeyStroke.getKeyStroke("alt H");
        KeyStroke altQ = KeyStroke.getKeyStroke("alt Q");
        KeyStroke altS = KeyStroke.getKeyStroke("alt S");
        KeyStroke downArrow = KeyStroke.getKeyStroke("alt X");
        KeyStroke upArrow = KeyStroke.getKeyStroke("alt Y");

        //KeyStroke downArrow = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,InputEvent.CTRL_DOWN_MASK);
        //KeyStroke altArrow = KeyStroke.getKeyStroke(KeyEvent.VK_X,InputEvent.ALT_MASK);
        KeyStroke ins = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.ALT_MASK);

        /*
        iMap.put(ctrlS, "ctrl-s");
        iMap.put(ctrlD, "ctrl-d");
        iMap.put(ctrlA, "ctrl-a");
        iMap.put(ctrlB, "ctrl-b");
        iMap.put(ctrlF, "ctrl-f");
        iMap.put(ctrlN, "ctrl-n");
        iMap.put(ctrlH, "ctrl-h");
        
         */
        iMap.put(alt1, "alt-1");
        iMap.put(altS, "alt-s");
        iMap.put(altD, "alt-d");
        iMap.put(altA, "alt-a");
        iMap.put(altB, "alt-b");
        iMap.put(altC, "alt-c");
        iMap.put(altF, "alt-f");
        iMap.put(altN, "alt-n");
        iMap.put(altH, "alt-h");
        iMap.put(altQ, "alt-q");
        iMap.put(ins, "ins");
        iMap.put(downArrow, "arrow-down");
        iMap.put(upArrow, "arrow-up");
        iMap.put(altE, "alt-e");

        Action createSelectStatement = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createSelectNames(true, null));
                cbPaste.doClick();
            }
        };

        Action createSelectStmt = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createSelectStatement(0));
                cbPaste.doClick();
            }
        };

        Action distinct = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterDistinct();
            }
        };

        Action insert = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteSelectedLeaf();
            }
        };

        Action pasteAlias = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteSelectedLeafAlias();
            }
        };

        Action pasteFullyQualified = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteSelectedLeafQualified();
            }
        };

        Action pasteStandard = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteSelectedLeafStandard();
            }
        };

        Action showhelp = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        };

        Action arrowDown = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectNext();
            }
        };

        Action arrowUp = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectPrev();
            }
        };

        Action altCaction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pasteSelectedLeaf();
            }
        };

        Action alt1action = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                QueryHistory qh = new QueryHistory();
                cbPaste.setActionCommand(qh.getPreviousCommand(getDatabase()));
                cbPaste.doClick();
            }
        };

        Action specialAlias = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String myAlias = JOptionPane.showInputDialog(null, "Enter Alias:",
                        "Table Alias",
                        JOptionPane.PLAIN_MESSAGE);
                if (myAlias != null) {
                    cbPaste.setActionCommand(createSelectNames(true, myAlias));
                    cbPaste.doClick();
                }

            }
        };

        /*
        iMap.put(ctrlS, "ctrl-s");
        iMap.put(ctrlD, "ctrl-d");
        iMap.put(ctrlA, "ctrl-a");
        iMap.put(ctrlB, "ctrl-b");
        iMap.put(ctrlF, "ctrl-f");
        iMap.put(ctrlN, "ctrl-n");
        iMap.put(ctrlH, "ctrl-h");
        iMap.put(altS, "alt-s");
        iMap.put(altD, "alt-d");
        iMap.put(altA, "alt-a");
        iMap.put(altB, "alt-b");
        iMap.put(altF, "alt-f");
        iMap.put(altN, "alt-n");
        iMap.put(altH, "alt-h");
        iMap.put(ins,"ins");
         */
        //aMap.put("ctrl-s", createSelectStatement);
        //aMap.put("ctrl-d", distinct);
        //aMap.put("ctrl-a", specialAlias);
        //aMap.put("ctrl-b", pasteAlias);
        //aMap.put("ctrl-f", pasteFullyQualified);
        //aMap.put("ctrl-n", pasteStandard);
        //aMap.put("ctrl-h", showhelp);
        aMap.put("alt-1", alt1action);
        aMap.put("alt-e", createSelectStatement);
        aMap.put("alt-s", createSelectStmt);
        aMap.put("alt-d", distinct);
        aMap.put("alt-a", specialAlias);
        aMap.put("alt-b", pasteAlias);
        aMap.put("alt-c", altCaction);
        aMap.put("alt-f", pasteFullyQualified);
        aMap.put("alt-n", pasteStandard);
        aMap.put("alt-q", pasteStandard);
        aMap.put("alt-h", showhelp);
        aMap.put("arrow-down", arrowDown);
        aMap.put("arrow-up", arrowUp);
        aMap.put("ins", insert);

    }

    public void showHelp() {
        String msg = help();
        //JOptionPane.showMessageDialog(null, msg);

        JOptionPane optionPane = new JOptionPane(msg, JOptionPane.INFORMATION_MESSAGE);
        JDialog rowsDialog = optionPane.createDialog("SQL Monitor tipps");
        rowsDialog.setLocation(MouseInfo.getPointerInfo().getLocation());
        rowsDialog.setVisible(true);

    }

    public String help() {
        StringBuilder help = new StringBuilder();
        help.append("Tipps\n");
        help.append("Click right in tree for menu\n");

        help.append("Type ALT-1 to get the previous command\n");
        help.append("Type ALT-ENTER to to run the last command\n");
        help.append("Type ALT-R to run the command enclosed by ; at current position\n");
        help.append("Type ALT-A followed by a text terminated by <Enter> to create select statement using the text typed as table alias\n");
        help.append("Type ALT B to paste the first letter of the tablename as alias\n");
        help.append("Type ALT C to paste just the leaf\n");
        help.append("Type ALT-D in a column-leaf to get a \"select distinct {columnName} from {tableName}\"\n");
        help.append("Type ALT-E inside a table branch to get a select-Statement created using the first letter of the table as alias\n");
        help.append("Type ALT F to paste fully qualified names\n");
        help.append("Type ALT-H for this helpscreen\n");
        help.append("Type ALT N to paste tablename.columnname\n");
        help.append("Type ALT-S inside a table branch to get a select-Statement created\n");
        help.append("Type ALT X to walk the tree down\n");
        help.append("Type ALT Y to walk the tree up\n");
        help.append("Type ALT Insert to paste just plain values (same as doubleclick within the tree)\n");

        return help.toString();
    }

    @Override
    public void run() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        logger.log(LOGLEVEL, "Collect Metadata ...");
        createTopNodes();
        this.updateUI();

        //setAutoscan(false);
        // because of out of Memory this is not a good idea
        // doCompletionProvider(null);
        logger.log(LOGLEVEL, "Metadata collection finished");
        this.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void focusGained(FocusEvent e) {
        //this.updateUI();
    }

    @Override
    public void focusLost(FocusEvent e) {
        // nothing to do
    }

    /**
     * @return the provider
     */
    public DbAutoCompletionProvider getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(DbAutoCompletionProvider provider) {
        this.provider = provider;
    }

    private JMenu definitionMenu() {
        JMenu definitons = new JMenu("Save Definitions");

        JMenuItem tableDefinition = new JMenuItem("Tables");
        JMenuItem viewDefinition = new JMenuItem("Views");

        definitons.add(tableDefinition);
        definitons.add(viewDefinition);

        return definitons;
    }

    /*
     this Menu creates Data Manipulation thins like update, insert, delete
     this is availabile as Standard SQL script or as a prepared Statement
     */
    private JMenu dmlMenu() {
        JMenu dml = new JMenu("DML");

        JMenuItem insertSql = new JMenuItem("SQL: insert");
        JMenuItem updateSql = new JMenuItem("SQL: update");
        JMenuItem deleteSql = new JMenuItem("SQL: delete");

        JMenuItem insertPrep = new JMenuItem("Prepared Statement: insert");
        JMenuItem updatePrep = new JMenuItem("Prepared Statement: update");
        JMenuItem deletePrep = new JMenuItem("Prepared Statement: delete");

        dml.add(insertSql);
        dml.add(updateSql);
        dml.add(deleteSql);
        dml.add(new JSeparator());
        dml.add(insertPrep);
        dml.add(updatePrep);
        dml.add(deletePrep);

        insertSql.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createInsertStatement(false));
                cbPaste.doClick();
            }
        });
        insertPrep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createInsertStatement(true));
                cbPaste.doClick();
            }
        });

        updateSql.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createUpdateStatement(false));
                cbPaste.doClick();
            }
        });
        updatePrep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createUpdateStatement(true));
                cbPaste.doClick();
            }
        });

        deleteSql.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createDeleteStatement(false));
                cbPaste.doClick();
            }
        });

        deletePrep.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createDeleteStatement(true));
                cbPaste.doClick();
            }
        });

        return dml;
    }

    private JMenu ddlMenu() {
        JMenu ddl = new JMenu("DDL");
        JMenuItem createCmd = new JMenuItem("CREATE TABLE");

        createCmd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(getCreateStatement());
                cbPaste.doClick();
            }
        });

        ddl.add(createCmd);

        JMenuItem dropTable = new JMenuItem("DROP TABLE");
        dropTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(getDropStatement());
                cbPaste.doClick();
            }
        });
        ddl.add(dropTable);

        return ddl;
    }

    private JMenu tablePopUp() {
        JMenu popup = new JMenu("Select data");

        JMenuItem selStm = new JMenuItem("Create Select Statement");
        selStm.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createSelectNames(false, null));
                cbPaste.doClick();
            }
        });
        JMenuItem selStm2 = new JMenuItem("Create Select Statement with Alias");
        selStm2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createSelectNames(true, null));
                cbPaste.doClick();
            }
        });

        JMenuItem selAll = new JMenuItem("Create Select * from [Table]");
        selAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createSelectStatement(0));
                cbPaste.doClick();
            }
        });

        JMenuItem selAll10 = new JMenuItem("Select first 10 rows");
        selAll10.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createSelectStatement(10));
                cbPaste.doClick();
            }
        });

        JMenuItem selAll100 = new JMenuItem("Select first 100 rows");
        selAll100.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createSelectStatement(100));
                cbPaste.doClick();
            }
        });

        JMenuItem selAll1000 = new JMenuItem("Select first 1000 rows");
        selAll1000.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createSelectStatement(1000));
                cbPaste.doClick();
            }
        });

        popup.add(selStm);
        popup.add(selStm2);
        popup.add(selAll);

        /*
         implemented for MySQL, Oracle, DB2, ApacheDerby, PostgeSQL, Microsoft SQL Server
         */
        if (isRowsLimitable()) {
            popup.add(selAll10);
            popup.add(selAll100);
            popup.add(selAll1000);
        }

        return popup;

    }

    private void noRows() {

        Thread thread = new Thread() {
            public void run() {
                DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();

                if ((dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_TABLE))
                        || (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_VIEW))
                        || (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY))) {

                    Statement stmt = null;
                    ResultSet rs = null;
                    try {
                        getCon();
                        stmt = con.createStatement();
                        StringBuilder schemaPlusTable = new StringBuilder();
                        String s = dmt.getSchemaName();
                        if (!dmt.getSchemaName().equalsIgnoreCase("%")) {
                            schemaPlusTable.append(fixSpaces(dmt.getSchemaName())).append(".");
                        }
                        schemaPlusTable.append(fixSpaces(dmt.getTableName()));
                        rs = stmt.executeQuery("select count(*) from " + schemaPlusTable.toString());
                        int noRows = 0;
                        if (rs.next()) {
                            noRows = rs.getInt(1);
                            JOptionPane optionPane = new JOptionPane(String.format("%,d", noRows) + " Rows in " + dmt.getTableName(), JOptionPane.INFORMATION_MESSAGE);
                            JDialog rowsDialog = optionPane.createDialog(" Rows in " + dmt.getTableName());
                            rowsDialog.setLocation(MouseInfo.getPointerInfo().getLocation());
                            rowsDialog.setVisible(true);
                            //JOptionPane.showMessageDialog(null, String.format("%,d", noRows) + " Rows in " + dmt.getTableName());
                        }
                    } catch (SQLException sqlex) {
                        logger.log(Level.INFO, "Exception while counting rows: {0} on Table {1}", new Object[]{sqlex.getMessage(), dmt.getTableName()});
                    } finally {
                        if (rs != null) {
                            try {
                                rs.close();
                            } catch (SQLException sqlex) {
                                logger.log(Level.INFO, "Exception on close while counting rows: {0} on Table {1}", new Object[]{sqlex.getMessage(), dmt.getTableName()});
                            }
                        }
                        if (stmt != null) {
                            try {
                                stmt.close();
                            } catch (SQLException sqlex) {
                                logger.log(Level.INFO, "Exception on close while counting rows: {0} on Table {1}", new Object[]{sqlex.getMessage(), dmt.getTableName()});
                            }
                        }
                        if (con != null) {
                            try {
                                con.close();
                            } catch (SQLException sqlex) {
                                logger.log(Level.INFO, "Exception on close while counting rows: {0} on Table {1}", new Object[]{sqlex.getMessage(), dmt.getTableName()});
                            }
                        }
                    }
                }
            }
        };
        thread.start();

    }

    private StringBuilder sb;

    private JMenu rowFilters() {

        sb = new StringBuilder();
        JMenu popup = new JMenu("Row Filters");

        JMenuItem fieldContent = new JMenuItem("Display field content");
        fieldContent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayDistinctList();
            }
        });

        JMenuItem filterMenuItem = new JMenuItem("select distinct [field]");
        JMenu whereMenu = new JMenu("where conditions");
        JMenu andMenu = new JMenu("and conditions");
        JMenuItem orderBy = new JMenuItem("Order by");

        filterMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                filterDistinct();
            }
        });

        orderBy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
                if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                    cbPaste.setActionCommand("\norder by " + fixSpaces(dmt.getColumnName()) + "\n");
                    cbPaste.doClick();
                }
            }
        }
        );

        JMenuItem whereLike = new JMenuItem("add 'where like'");

        whereLike.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("where", "like"));
                cbPaste.doClick();
            }
        });

        JMenuItem whereEqual = new JMenuItem("add 'where ='");
        whereEqual.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("where", "="));
                cbPaste.doClick();
            }
        });

        JMenuItem whereLess = new JMenuItem("add 'where <'");
        whereLess.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("where", "<"));
                cbPaste.doClick();
            }
        });

        JMenuItem whereLessOrEqual = new JMenuItem("add 'where <='");
        whereLessOrEqual.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("where", "<="));
                cbPaste.doClick();
            }
        });

        JMenuItem whereBigger = new JMenuItem("add 'where >'");
        whereBigger.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("where", ">"));
                cbPaste.doClick();
            }
        });

        JMenuItem whereBiggerOrEqual = new JMenuItem("add 'where >='");
        whereBiggerOrEqual.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("where", ">="));
                cbPaste.doClick();
            }
        });

        JMenuItem whereBetween = new JMenuItem("add 'where between'");
        whereBetween.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("where", "between"));
                cbPaste.doClick();
            }
        });

        JMenuItem whereIn = new JMenuItem("add 'where in'");
        whereIn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("where", "in"));
                cbPaste.doClick();
            }
        });

        JMenuItem whereIsNull = new JMenuItem("add 'where is null'");
        whereIsNull.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("where", "is null"));
                cbPaste.doClick();
            }
        });

        JMenuItem whereIsNotNull = new JMenuItem("add 'where is not null'");
        whereIsNotNull.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("where", "is not null"));
                cbPaste.doClick();
            }
        });

        JMenuItem andLike = new JMenuItem("add 'and like'");
        andLike.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("and", "like"));
                cbPaste.doClick();
            }
        });

        JMenuItem andEqual = new JMenuItem("add 'and ='");
        andEqual.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("and", "="));
                cbPaste.doClick();
            }
        });

        JMenuItem andLess = new JMenuItem("add 'and <'");
        andLess.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("and", "<"));
                cbPaste.doClick();
            }
        });

        JMenuItem andLessOrEqual = new JMenuItem("add 'and <='");
        andLessOrEqual.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("and", "<="));
                cbPaste.doClick();
            }
        });

        JMenuItem andBigger = new JMenuItem("add 'and >'");
        andBigger.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("and", ">"));
                cbPaste.doClick();
            }
        });

        JMenuItem andBiggerOrEqual = new JMenuItem("add 'and >='");
        andBiggerOrEqual.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("and", ">="));
                cbPaste.doClick();
            }
        });

        JMenuItem andBetween = new JMenuItem("add 'and between'");
        andBetween.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("and", "between"));
                cbPaste.doClick();
            }
        });

        JMenuItem andIn = new JMenuItem("add 'and in'");
        andIn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("and", "in"));
                cbPaste.doClick();
            }
        });

        JMenuItem andIsNull = new JMenuItem("add 'and is Null'");
        andIsNull.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("and", "is null"));
                cbPaste.doClick();
            }
        });

        JMenuItem andIsNotNull = new JMenuItem("add 'and is not null'");
        andIsNotNull.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(doRowFilter("and", "is not null"));
                cbPaste.doClick();
            }
        });

        whereMenu.add(whereLike);
        whereMenu.add(whereEqual);
        whereMenu.add(whereLess);
        whereMenu.add(whereLessOrEqual);
        whereMenu.add(whereBigger);
        whereMenu.add(whereBiggerOrEqual);
        whereMenu.add(whereBetween);
        whereMenu.add(whereIn);
        whereMenu.add(whereIsNull);
        whereMenu.add(whereIsNotNull);

        andMenu.add(andLike);
        andMenu.add(andEqual);
        andMenu.add(andLess);
        andMenu.add(andLessOrEqual);
        andMenu.add(andBigger);
        andMenu.add(andBiggerOrEqual);
        andMenu.add(andBetween);
        andMenu.add(andIn);
        andMenu.add(andIsNull);
        andMenu.add(andIsNotNull);

        popup.add(fieldContent);
        popup.add(filterMenuItem);
        popup.add(whereMenu);
        popup.add(andMenu);
        popup.add(orderBy);

        return popup;
    }

    // if one uses Spaces in a field- or table name, it has to be surrounded by "
    private String fixSpaces(String v) {
        if ((v.startsWith(getQuote())) && (v.endsWith(getQuote()))) {
            return v;
        }
        sqlWordsIterator = sqlWords.iterator();
        if (v != null) {
            CharSequence charSequence = "-+*\\/ /.()[]{}%&@#?!,;='^´:<>¦|";

            if ((v.trim().contains(" "))
                    || (v.trim().contains("-"))
                    || (v.trim().contains("+"))
                    || (v.trim().contains("*"))
                    || (v.trim().contains("\\"))
                    || (v.trim().contains("/"))
                    || (v.trim().contains("("))
                    || (v.trim().contains(")"))
                    || (v.trim().contains("["))
                    || (v.trim().contains("]"))
                    || (v.trim().contains("{"))
                    || (v.trim().contains("}"))
                    || (v.trim().contains("%"))
                    || (v.trim().contains("@"))
                    || (v.trim().contains("?"))
                    || (v.trim().contains("!"))
                    || (v.trim().contains(","))
                    || (v.trim().contains(";"))
                    || (v.trim().contains(":"))
                    || (v.trim().contains("<"))
                    || (v.trim().contains(">"))
                    || (v.trim().contains("|"))
                    || (v.trim().contains("¦"))
                    || (v.trim().contains("'"))
                    || (v.trim().contains("^"))
                    || (v.trim().contains("`"))
                    || (v.trim().contains("."))) {

                v = inBrackets(v);
            } else {

                /*
                while (sqlWordsIterator.hasNext()) {
                    if (v.equalsIgnoreCase((String) sqlWordsIterator.next())) {
                        v = inBrackets(v);
                        break;
                    }
                }
                 */
                // everything is in brackets
                v = inBrackets(v);
            }
        }
        return v;
    }

    private boolean isRowsLimitable() {
        boolean yesOrNo = false;

        if ((getDatabaseProduct().toLowerCase().contains("mysql"))
                || (getDatabaseProduct().toLowerCase().contains("microsoft"))
                || (getDatabaseProduct().toLowerCase().contains("oracle"))
                || (getDatabaseProduct().toLowerCase().contains("postgresql"))
                || (getDatabaseProduct().toLowerCase().contains("db2"))
                || (getDatabaseProduct().toLowerCase().contains("derby"))) {
            yesOrNo = true;
        }
        return yesOrNo;
    }

    public void pasteSelectedLeaf() {
        try {
            DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();

            /*
            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_SCHEMA)) {
                if ((dmt.getSchemaName().equalsIgnoreCase("%")) || (dmt.getSchemaName().length() < 1)) {

                } else {
                    cbPaste.setActionCommand(dmt.getSchemaName());
                }
                cbPaste.doClick();
            }
             */
            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
                if ((dmt.getSchemaName().equalsIgnoreCase("%")) || (dmt.getSchemaName().length() < 1)) {
                    cbPaste.setActionCommand(fixSpaces(dmt.getTableName()));
                } else {
                    cbPaste.setActionCommand(dmt.getSchemaName() + "." + fixSpaces(dmt.getTableName()));
                }
                cbPaste.doClick();
            }
            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_SINGLE_FUNCTION)) {
                String fNames[] = dmt.getFunctionName().split(";");
                if ((dmt.getSchemaName().equalsIgnoreCase("%")) || (dmt.getSchemaName().length() < 1)) {
                    cbPaste.setActionCommand(fixSpaces(fNames[0]));
                } else {
                    cbPaste.setActionCommand(dmt.getSchemaName() + "." + fixSpaces(fNames[0]));
                }
                cbPaste.doClick();
            }
            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                cbPaste.setActionCommand(fixSpaces(dmt.getColumnName()) + " ,\n");
                cbPaste.doClick();
            }
        } catch (Exception e) {

        }
    }

    private void pasteSelectedLeafStandard() {
        StringBuilder sl = new StringBuilder();

        try {
            DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();

            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
                sl.append(fixSpaces(dmt.getTableName()));
                cbPaste.setActionCommand(sl.toString());
                cbPaste.doClick();
            }
            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                sl.append(fixSpaces(dmt.getTableName())).append(".").append(fixSpaces(dmt.getColumnName())).append(" ,\n");
                cbPaste.setActionCommand(sl.toString());
                cbPaste.doClick();
            }

            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_SINGLE_FUNCTION)) {
                String fNames[] = dmt.getFunctionName().split(";");
                if ((dmt.getSchemaName().equalsIgnoreCase("%")) || (dmt.getSchemaName().length() < 1)) {
                    cbPaste.setActionCommand(fixSpaces(fNames[0]));
                } else {
                    cbPaste.setActionCommand(dmt.getSchemaName() + "." + fixSpaces(fNames[0]));
                }
                cbPaste.doClick();
            }

        } catch (Exception e) {

        }
    }

    public void pasteSelectedLeafAlias() {
        try {
            StringBuilder sl = new StringBuilder();
            DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
            String schema = "";
            String table = "";
            String column = "";

            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
                sl.append(fixSpaces(dmt.getTableName())).append(" ").append(dmt.getTableName().substring(0, 1)).append(" ");
                cbPaste.setActionCommand(sl.toString());
                cbPaste.doClick();
            }

            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                sl.append(dmt.getTableName().substring(0, 1)).append(".").append(fixSpaces(dmt.getColumnName())).append(" ,\n");
                cbPaste.setActionCommand(sl.toString());
                cbPaste.doClick();
            }
        } catch (Exception e) {

        }
    }

    public void pasteSelectedLeafQualified() {
        try {
            StringBuilder sl = new StringBuilder();
            DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
            String schema = "";
            String table = "";
            String column = "";

            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
                sl.append(fixSpaces(dmt.getSchemaName())).append(".").append(fixSpaces(dmt.getTableName())).append("\n");
                cbPaste.setActionCommand(sl.toString());
                cbPaste.doClick();
            }
            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                sl.append(fixSpaces(dmt.getSchemaName())).append(".").append(fixSpaces(dmt.getTableName())).append(".").append(fixSpaces(dmt.getColumnName())).append(" ,\n");
                cbPaste.setActionCommand(sl.toString());
                cbPaste.doClick();
            }
        } catch (Exception e) {
            logger.log(Level.INFO, "Exception: {0}", e.getMessage());
        }
    }

    public void getChildren() {
        StringBuilder sb = new StringBuilder();
        DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
        Enumeration children = dmt.children();
        if (children != null) {
            boolean further = false;
            while (children.hasMoreElements()) {
                if (further) {
                    sb.append(System.getProperty("line.separator"));
                }
                further = true;
                DbTreeNode dbtreenode = (DbTreeNode) children.nextElement();
                sb.append(dbtreenode.getDisplayableName());
            }
        }
        /*
        JDialog infodialog = new JDialog();
        infodialog.setTitle("Children of " + dmt.getDisplayableName());
        infodialog.setModal(true);
         */
        CloseableFrame infodialog = new CloseableFrame("Children of " + dmt.getDisplayableName(), false);
        infodialog.getContentPane().setLayout(new BorderLayout());

        JTextArea msgArea = new JTextArea(15, 50);
        msgArea.setEditable(false);
        //msgArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
        msgArea.setLineWrap(false);
        msgArea.setText(sb.toString());

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        ImageButton close = new ImageButton(null, "exit.gif", "Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                infodialog.setVisible(false);
                infodialog.dispose();
            }
        });

        ImageButton copy = new ImageButton(null, "copy.gif", "Copy all to clipboard");
        Toolkit tk = this.getToolkit();
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StringSelection ss = new StringSelection(sb.toString());
                tk.getSystemClipboard().setContents(ss, ss);
            }
        });

        panel.add(close);
        panel.add(copy);
        panel.setBorder(new EtchedBorder());

        infodialog.getContentPane().add(BorderLayout.CENTER, new JScrollPane(msgArea));
        infodialog.getContentPane().add(BorderLayout.SOUTH, panel);
        infodialog.pack();
        infodialog.setLocation(MouseInfo.getPointerInfo().getLocation());
        infodialog.setVisible(true);

    }

    public void selectNext() {
        try {
            DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
            DbTreeNode d = (DbTreeNode) dmt.getNextLeaf();
            List<DbTreeNode> nodes = new ArrayList();
            if (d != null) {
                nodes.add(d);
                d = (DbTreeNode) d.getParent();
                while (d != null) {
                    nodes.add(0, d);
                    d = (DbTreeNode) d.getParent();
                }
            }
            tree.setSelectionPath(new TreePath(nodes.toArray()));
            selPath = new TreePath(nodes.toArray());
        } catch (Exception e) {

        }
    }

    public void selectPrev() {
        try {
            DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();;
            DbTreeNode d = (DbTreeNode) dmt.getPreviousLeaf();
            List<DbTreeNode> nodes = new ArrayList();
            if (d != null) {
                nodes.add(d);
                d = (DbTreeNode) d.getParent();
                while (d != null) {
                    nodes.add(0, d);
                    d = (DbTreeNode) d.getParent();
                }
            }
            tree.setSelectionPath(new TreePath(nodes.toArray()));
            selPath = new TreePath(nodes.toArray());
        } catch (Exception e) {

        }
    }

    /*
     Create a select statement of the selected table
     @param rows Number of lines to from top of the query to display
     */
    private String createSelectStatement(int rows) {
        try {
            StringBuilder sl = new StringBuilder();
            DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();

            if (dmt.isLeaf()) {
                addChildren(dmt);
            }

            /*
         I use prefix and postfix to fiddle out the numbers of rows to read
             */
            String prefix = "";
            String postfix = "";

            if (rows > 0) {
                if (getDatabaseProduct().toLowerCase().contains("microsoft")) {
                    prefix = " top " + Integer.toString(rows) + " ";
                }

                if (getDatabaseProduct().toLowerCase().contains("mysql")) {
                    postfix = "\nLIMIT " + Integer.toString(rows) + " ";
                }

                if (getDatabaseProduct().toLowerCase().contains("oracle")) {
                    postfix = "\nwhere ROWNUM <= " + Integer.toString(rows) + " ";
                }
                if (getDatabaseProduct().toLowerCase().contains("derby")) {
                    postfix = " FETCH FIRST " + Integer.toString(rows) + " ROWS ONLY";
                }

                if (getDatabaseProduct().toLowerCase().contains("db2")) {
                    postfix = " FETCH FIRST " + Integer.toString(rows) + " ROWS ONLY";
                }

                if (getDatabaseProduct().toLowerCase().contains("postgres")) {
                    postfix = "\nLIMIT " + Integer.toString(rows) + " ";
                }

            }
            /*
         if we are on a table/view/alias or a synonynm we create a select * statement
         if we are on a column we just do select columnname from table          
             */
            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
                sl.append("select ").append(prefix).append(" * from ");
                if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                    sl.append(dmt.getSchemaName().trim()).append(".");
                }
                sl.append(fixSpaces(dmt.getTableName()));
                sl.append(postfix);
            }

            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                sl.append("select ").append(prefix).append(fixSpaces(dmt.getColumnName())).append(" from ");
                if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                    sl.append(dmt.getSchemaName().trim()).append(".");
                }
                sl.append(fixSpaces(dmt.getTableName()));
                sl.append(postfix).append(";");
            }

            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_SINGLE_FUNCTION)) {
                sl.append("select * from ").append(prefix);
                if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                    sl.append(dmt.getSchemaName().trim()).append(".");
                }
                String[] fName = dmt.getFunctionName().split(";");
                sl.append(fixSpaces(fName[0]));

                HashMap<String, FunctionColumnMetaData> myColumns = new HashMap();
                myColumns = dmt.getFunctionMetaData().getColumnMetaData();
                boolean firstRound = true;
                for (Iterator<String> it = myColumns.keySet().iterator(); it.hasNext();) {
                    String funcName = it.next();
                    FunctionColumnMetaData fcmd = new FunctionColumnMetaData();
                    fcmd = (FunctionColumnMetaData) myColumns.get(funcName);
                    if (firstRound) {
                        sl.append("(");
                        firstRound = false;
                    } else {
                        sl.append(",");
                    }
                    sl.append(fcmd.getColumnName());
                }

                sl.append(postfix).append(");");
            }

            return sl.toString();
        } catch (Exception e) {
            return null;
        }

    }

    /*
     Create a select distinct field 
     */
    private void filterDistinct() {

        StringBuilder sl = new StringBuilder();
        DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();

        // this only works for columns
        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
            sl.append("\nselect distinct ").append(fixSpaces(dmt.getColumnName())).append(" from ");

            if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                sl.append(dmt.getSchemaName().trim()).append(".");
            }

            sl.append(fixSpaces(dmt.getTableName())).append(";");
            cbPaste.setActionCommand(sl.toString());
            cbPaste.doClick();
        }

    }

    /*
    If no children, add them
     */
    private void addChildren(DbTreeNode node) {

        //System.out.println(getClass().getName() + ".addChildren(" + node.getType() + "," + node.getEntityType() + ") Leaf ? " + node.isLeaf());
        if (!node.isLeaf()) {
            return;
        }
        if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_SCHEMA)) {
            addTables(node.getSchemaName(), node);
            addFunctions(node.getSchemaName(), node);

            tree.expandPath(selPath);
            tree.updateUI();
        }
        if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
            addColumns(node.getSchemaName(), node.getEntityType(), node.getTableName(), node);

            //tree.expandPath(selPath);
            tree.updateUI();
        }
        if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_DATABASE)) {
            createTopNodes();

            tree.expandPath(selPath);
            tree.updateUI();
        }

    }

    /*
     Create a select statement of the selected table          
     @tablealias if this is true, the aliasTxt and a . will be set ahead the columnname.     
     @aliasTxt the text that will be set as tablealias. If this is NULL or empty it will take the first lette of the tablename
     
     */
    private String createSelectNames(boolean tableAlias, String aliasTxt) {
        StringBuilder sl = new StringBuilder();
        try {
            DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
            if (dmt.isLeaf()) {
                addChildren(dmt);
            }

            String dot = "";

            //logger.log(Level.INFO," special alias " + tableAlias + " Text: " + aliasTxt + " Type: " + dmt.getType() + " Feld: " + dmt.getDbName() + "." + dmt.getSchemaName() + "." + dmt.getTableName() + "." + dmt.getColumnName());
            /*
         if we are on a COLUMN item we jump one up to get the entity
             */
            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                TreePath tp = selPath.getParentPath();
                dmt = (DbTreeNode) tp.getLastPathComponent();

            }

            if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {

                /* 
             the TableName and AliasName to be used in the Statement
        
                 */
                if (tableAlias) {
                    if ((aliasTxt == null) || (aliasTxt.length() < 1)) {
                        aliasTxt = dmt.getTableName().trim().substring(0, 1);
                    }
                    dot = ".";
                } else {
                    aliasTxt = "";
                    dot = "";
                }

                sl.append("select \n");

                // we traverse this node to get all children --> column Names
                int noChildren = dmt.getChildCount();
                for (int i = 0; i < noChildren; i++) {
                    DbTreeNode d = (DbTreeNode) dmt.getChildAt(i);
                    d.setFIXSPACES(false);
                    if (i > 0) {
                        sl.append(",\n");
                    }
                    if (d.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                        sl.append(" ").append(aliasTxt).append(dot).append(fixSpaces(d.getColumnName()));
                    }
                }

                sl.append("\nfrom ");
                if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                    sl.append(dmt.getSchemaName().trim()).append(".");
                }
                sl.append(fixSpaces(dmt.getTableName()));
                sl.append(" ").append(aliasTxt); //.append(";");
            }
        } catch (Exception e) {

        }

        return sl.toString();
    }

    /*
     we create a sql-insert Statement  of the selected table
     the switch prepStatement indicates wheter a prepared Statement or a SQL Statement should be generated
     */
    private String createInsertStatement(boolean prepStatement) {
        StringBuilder sb = new StringBuilder();
        StringBuilder fieldList = new StringBuilder();
        StringBuilder valuesList = new StringBuilder();

        DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            addChildren(dmt);
        }

        /*
         if we are on a COLUMN item we jump one up to get the entity
         */
        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
            TreePath tp = selPath.getParentPath();
            dmt = (DbTreeNode) tp.getLastPathComponent();

        }

        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {

            sb.append("insert into ");

            if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                sb.append(dmt.getSchemaName().trim()).append(".");
            }
            sb.append(fixSpaces(dmt.getTableName())).append(" (\n");

            // we traverse this node to get all children --> column Names
            boolean linePrinted = false;
            int noChildren = dmt.getChildCount();
            for (int i = 0; i < noChildren; i++) {
                DbTreeNode d = (DbTreeNode) dmt.getChildAt(i);
                if ((d.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) && (!d.isAutoIncrement())) {
                    if (linePrinted) {
                        fieldList.append(",\n");
                        if (prepStatement) {
                            valuesList.append(", ");
                        } else {
                            valuesList.append(",\n");
                        }

                    }

                    // we do not add the autoincrement Field
                    fieldList.append(" ").append(fixSpaces(d.getColumnName()));
                    if (prepStatement) {
                        valuesList.append("?");
                    } else {
                        valuesList.append(apostrophe(d.getColumnDataType())).append("{").append(d.getColumnName()).append("}").append(apostrophe(d.getColumnDataType()));
                    }
                    linePrinted = true;
                }
            }
            sb.append(fieldList).append("\n)\nvalues (\n");
            sb.append(valuesList).append("\n)");
        }

        return sb.toString();
    }

    /*
     we create the rowFilters
     @param type  must be either 'where' or 'and'
     @param filter the filter criteriea (e.g like, >= <= != between  is null is not null
     */
    public String doRowFilter(String type, String filter) {
        StringBuilder sb = new StringBuilder();

        DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
            sb.append("\n").append(type).append(" ").append(fixSpaces(dmt.getColumnName())).append(" ").append(filter).append(" ");
            if (filter.equalsIgnoreCase("in")) {
                sb.append("( ");
            }
            sb.append(apostrophe(dmt.getColumnDataType())).append("{...}").append(apostrophe(dmt.getColumnDataType()));
            if (filter.equalsIgnoreCase("in")) {
                sb.append(", ").append(apostrophe(dmt.getColumnDataType())).append("{...}").append(apostrophe(dmt.getColumnDataType())).append(" )");
            }
            if (filter.equalsIgnoreCase("between")) {
                sb.append(" and ").append(apostrophe(dmt.getColumnDataType())).append("{...}").append(apostrophe(dmt.getColumnDataType()));
            }

        }

        return sb.toString();
    }

    /*
     this displays a list with the values of select distinct field from table
     */
    public void displayDistinctList() {

        Thread distinctThread = new Thread() {
            public void run() {

                StringBuilder sl = new StringBuilder();
                StringBuilder title = new StringBuilder();
                DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();

                // this only works for columns
                if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                    sl.append("\nselect distinct ").append(fixSpaces(dmt.getColumnName())).append(" from ");

                    if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                        sl.append(dmt.getSchemaName().trim()).append(".");
                    }
                    sl.append(fixSpaces(dmt.getTableName())).append(" order by ").append(fixSpaces(dmt.getColumnName()));

                    // the title
                    if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                        title.append(dmt.getSchemaName().trim()).append(".");
                    }
                    title.append(fixSpaces(dmt.getTableName())).append(".");
                    title.append(fixSpaces(dmt.getColumnName()));
                    logger.log(Level.INFO, "Search for Field {0} startet, will be displayed, when finished", title.toString());
                    /*
            JDialog distinctDialog = new JDialog();
            distinctDialog.setTitle(title.toString());
                     */
                    CloseableFrame distinctDialog = new CloseableFrame(title.toString(), false);

                    distinctDialog.getContentPane().setLayout(new BorderLayout());

                    JPanel buttonPanel = new JPanel();
                    buttonPanel.setLayout(new FlowLayout());
                    ImageButton close = new ImageButton(null, "exit.gif", "close dialog");
                    close.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            distinctDialog.setVisible(false);
                            distinctDialog.dispose();
                        }
                    });
                    buttonPanel.add(close);

                    JMenuBar menuBar = new JMenuBar();
                    JMenu fileMenu = new JMenu("File");
                    JMenuItem menuClose = new JMenuItem("Close");
                    menuClose.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            distinctDialog.setVisible(false);
                            distinctDialog.dispose();
                        }
                    });
                    fileMenu.add(menuClose);
                    menuBar.add(fileMenu);
                    distinctDialog.setJMenuBar(menuBar);

                    // we add the query
                    SqlPanel tabPanel = new SqlPanel(sl.toString());
                    tabPanel.setCon(getCon());
                    tabPanel.tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    TableColumnAdjuster tca = new TableColumnAdjuster(tabPanel.tableView);
                    tca.adjustColumns();

                    distinctDialog.getContentPane().add(BorderLayout.CENTER, new JScrollPane((Component) tabPanel));
                    distinctDialog.getContentPane().add(BorderLayout.SOUTH, buttonPanel);
                    distinctDialog.pack();

                    Point p = MouseInfo.getPointerInfo().getLocation();
                    distinctDialog.setLocation(p);

                    GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

                    int width = gd.getDisplayMode().getWidth();
                    int height = gd.getDisplayMode().getHeight();

                    Dimension distinctDialogDim = distinctDialog.getPreferredSize();

                    if (distinctDialogDim.getWidth() + p.x > width) {
                        width = width - p.x;
                    } else {
                        width = distinctDialogDim.width;
                    }
                    if (distinctDialogDim.getHeight() + p.y > height) {
                        height = height - p.y;
                    } else {
                        height = distinctDialogDim.height;
                    }

                    if (width < 50) {
                        width = 50;
                    }
                    if (height < 50) {
                        height = 50;
                    }

                    distinctDialog.setSize(width, height);
                    distinctDialog.setVisible(true);
                    //normalCursor();
                }
            }

        };
        distinctThread.start();

    }

    /*
     we create an Update Statement Statement for the selected field
     it only works, when beeing on a COLUMN.
     @param prepStatement  if this ist set to true it will create a prepared statement to be used in Java Code, 
     if set to false, just DML is created     
     */
    public String createUpdateStatement(boolean prepStatement) {
        StringBuilder sb = new StringBuilder();
        StringBuilder pk = new StringBuilder();

        DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();

        /*
         if we are on a COLUMN item we update only this column
         */
        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
            sb.append("update ");
            if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                sb.append(dmt.getSchemaName().trim()).append(".");
            }
            sb.append(fixSpaces(dmt.getTableName())).append(" set ").append(fixSpaces(dmt.getColumnName())).append(" = ");
            if (prepStatement) {
                sb.append("? ");
            } else {
                sb.append(apostrophe(dmt.getColumnDataType())).append("{....}").append(apostrophe(dmt.getColumnDataType()));
            }


            /*
             now we add the where clause by finding the primarky key 
             if there is no primary key, we leave the where close open
             */
            sb.append("\nwhere ");

            // we go one up to find the primary keys from the table node and then we loop the whole table
            DbTreeNode parent = (DbTreeNode) dmt.getParent();
            int noChildren = parent.getChildCount();
            for (int i = 0; i < noChildren; i++) {
                DbTreeNode d = (DbTreeNode) parent.getChildAt(i);
                if (d.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                    if (d.isPrimaryKey()) {
                        if (pk.length() > 0) {
                            pk.append("\n and  ");
                        }
                        if (prepStatement) {
                            pk.append(d.getColumnName()).append(" = ?");
                        } else {
                            pk.append(d.getColumnName()).append(" = ").append(apostrophe(d.getColumnDataType())).append("{...}").append(apostrophe(d.getColumnDataType()));
                        }
                    }
                }
            }
            if (pk.length() < 1) {
                pk.append(" {add your where condition here} ");
            }
            sb.append(pk.toString());
        }

        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {

        }

        return sb.toString();
    }

    /*
    we create a DROP TABLE Command for the selected Table
     */
    public String getDropStatement() {

        StringBuilder sb = new StringBuilder();

        // we go one up, if we are on a column
        DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
            dmt = (DbTreeNode) dmt.getParent();
        }
        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
            sb.append("DROP TABLE ");

            if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                sb.append(dmt.getSchemaName().trim()).append(".");
            }
            sb.append(fixSpaces(dmt.getTableName())).append(" \n");

            if (dmt.getEntityType().equalsIgnoreCase("view")) {
                sb = new StringBuilder();
                sb.append("DROP VIEW ");
                if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                    sb.append(dmt.getSchemaName().trim()).append(".");
                }
                sb.append(fixSpaces(dmt.getTableName())).append(" \n");
            }

            if (dmt.getEntityType().equalsIgnoreCase("alias")) {
                sb = new StringBuilder();
                sb.append("DROP ALIAS ");
                if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                    sb.append(dmt.getSchemaName().trim()).append(".");
                }
                sb.append(fixSpaces(dmt.getTableName())).append(" \n");
            }
        }
        return sb.toString();
    }

    /*
      we create a CREATE TABLE statement for the selected Table
     */
    public String getCreateStatement() {
        StringBuilder sb = new StringBuilder();

        // we go one up, if we are on a column
        DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
            dmt = (DbTreeNode) dmt.getParent();
        }

        logger.log(Level.FINE, "Entity Type :{0}", dmt.getType()); // Fredy

        if ((dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) && (dmt.getEntityType().equalsIgnoreCase("table"))) {
            ArrayList<String> primaryKeys = new ArrayList();

            sb.append("CREATE TABLE ");
            if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                sb.append(dmt.getSchemaName().trim()).append(".");
            }
            sb.append(fixSpaces(dmt.getTableName())).append(" (\n");

            HashMap<String, JdbcTypeMapping> mapping = sql.fredy.admin.Admin.getJdbctypemapping();

            int noChildren = dmt.getChildCount();
            for (int i = 0; i < noChildren; i++) {
                DbTreeNode d = (DbTreeNode) dmt.getChildAt(i);
                if (d.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {

                    JdbcTypeMapping jtm = mapping.get(d.getJdbcTypeName());

                    if (i > 0) {
                        sb.append(" , \n");
                    }

                    // NAME
                    sb.append(" ").append(fixSpaces(d.getColumnName()));

                    // TYPE  MS SQL Server has a own type 'int identity' if it is an autoincrement column
                    if ((d.isAutoIncrement()) && (d.getColumnType().toLowerCase().contains("int identity"))) {
                        sb.append(" int");
                    } else {
                        sb.append(" ").append(d.getColumnType());
                    }

                    // LENGHT                  
                    sb.append(formattedLength(d));


                    // NULLABLE
                    if (!d.isNullable()) {
                        sb.append(" NOT NULL");
                    }

                    // AUTOINCREMENT                    
                    if (d.isAutoIncrement()) {
                        sb.append(" ").append(getDbSpecificAutoIncrement(getDatabaseProduct()));
                    }

                    // PRIMARY KEY?
                    if (d.isPrimaryKey()) {
                        //sb.append(" PRIMARY KEY");
                        primaryKeys.add(d.getColumnName());
                    }

                    // Default
                    if (d.getColumnDefault() != null) {
                        sb.append(" DEFAULT ").append(d.getColumnDefault()).append(" ");
                    }

                }
            }

            // there are PrimaryKeys to add
            if (!primaryKeys.isEmpty()) {
                sb.append(" ,\n PRIMARY KEY (");
                Iterator iter = primaryKeys.iterator();
                boolean firstRound = true;
                while (iter.hasNext()) {
                    if (firstRound) {
                        firstRound = false;
                    } else {
                        sb.append(" , ");
                    }
                    sb.append(iter.next());
                }
                sb.append(" )");
            }

            sb.append("\n)\n");
        }

        // and now for the views
        if ((dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) && (dmt.getEntityType().equalsIgnoreCase("view"))) {
            PreparedStatement viewReader = null;
            ResultSet viewReaderSet = null;
            sb = new StringBuilder();

            try {
                getCon();

                // if Microsoft                
                if (dmd.getDatabaseProductName().toLowerCase().startsWith("microsoft")) {
                    viewReader = getCon().prepareStatement("SELECT definition, uses_ansi_nulls, uses_quoted_identifier, is_schema_bound  FROM sys.sql_modules  WHERE object_id = OBJECT_ID(?)");
                    viewReader.setString(1, fixSpaces(dmt.getSchemaName()) + "." + fixSpaces(dmt.getTableName()));
                    viewReaderSet = viewReader.executeQuery();
                    if (viewReaderSet.next()) {
                        sb.append(viewReaderSet.getString("Definition"));
                    }
                }
                if (dmd.getDatabaseProductName().toLowerCase().startsWith("mysql")) {
                    viewReader = con.prepareStatement("SELECT  VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? and TABLE_NAME = ?");
                    viewReader.setString(1, fixSpaces(dmt.getDbName()));
                    viewReader.setString(2, fixSpaces(dmt.getTableName()));
                    viewReaderSet = viewReader.executeQuery();
                    if (viewReaderSet.next()) {
                        sb.append("CREATE OR REPLACE VIEW ").append(fixSpaces(dmt.getTableName())).append(" AS (\n").append(viewReaderSet.getString("VIEW_DEFINITION")).append("\n)");
                    }
                }
                if (dmd.getDatabaseProductName().toLowerCase().startsWith("postgresql")) {
                    viewReader = con.prepareStatement("select definition from pg_views where schemaname = ? and viewname = ?");
                    viewReader.setString(1, fixSpaces(dmt.getSchemaName()));
                    viewReader.setString(2, fixSpaces(dmt.getTableName()));
                    viewReaderSet = viewReader.executeQuery();
                    if (viewReaderSet.next()) {
                        sb.append("CREATE OR REPLACE VIEW ").append(fixSpaces(dmt.getTableName())).append(" AS (\n").append(viewReaderSet.getString("definition").replaceAll(";", "")).append("\n)");
                    }
                }

            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "Exception while reading Data to get the create statment {0}", sqlex.getMessage());
                //sqlex.printStackTrace();
            } finally {
                if (viewReaderSet != null) {
                    try {
                        viewReaderSet.close();
                    } catch (SQLException sqlex) {
                        logger.log(Level.WARNING, "Exception while closing MetaDataReader {0}", sqlex.getMessage());
                    }
                }
                if (viewReader != null) {
                    try {
                        viewReader.close();
                    } catch (SQLException sqlex) {
                        logger.log(Level.WARNING, "Exception while closing MetaDataReader {0}", sqlex.getMessage());
                    }
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException sqlex) {
                        logger.log(Level.WARNING, "Exception while closing MetaDataReader {0}", sqlex.getMessage());
                    }
                }
            }
        }

        if ((dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_SINGLE_FUNCTION)) || (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_FUNCTION))) {
            PreparedStatement viewReader = null;
            ResultSet viewReaderSet = null;
            sb = new StringBuilder();

            try {
                getCon();

                // if Microsoft                
                if (dmd.getDatabaseProductName().toLowerCase().startsWith("microsoft")) {
                    viewReader = getCon().prepareStatement("SELECT definition, uses_ansi_nulls, uses_quoted_identifier, is_schema_bound  FROM sys.sql_modules  WHERE object_id = OBJECT_ID(?)");
                    String functionName = dmt.getFunctionName().substring(0, dmt.getFunctionName().indexOf(";"));
                    viewReader.setString(1, fixSpaces(dmt.getSchemaName()) + "." + fixSpaces(functionName));
                    viewReaderSet = viewReader.executeQuery();
                    if (viewReaderSet.next()) {
                        sb.append(viewReaderSet.getString("Definition"));
                    }
                }
                if (dmd.getDatabaseProductName().toLowerCase().startsWith("mysql")) {
                    viewReader = con.prepareStatement("show create procedure ?");
                    viewReader.setString(2, fixSpaces(dmt.getTableName()));
                    viewReaderSet = viewReader.executeQuery();
                    if (viewReaderSet.next()) {
                        sb.append(viewReaderSet.getString("Create Procedure")).append("\n)");
                    }
                }

            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "Exception while reading Data to get the create statment {0}", sqlex.getMessage());
                sqlex.printStackTrace();
            } finally {
                if (viewReaderSet != null) {
                    try {
                        viewReaderSet.close();
                    } catch (SQLException sqlex) {
                        logger.log(Level.WARNING, "Exception while closing MetaDataReader {0}", sqlex.getMessage());
                    }
                }
                if (viewReader != null) {
                    try {
                        viewReader.close();
                    } catch (SQLException sqlex) {
                        logger.log(Level.WARNING, "Exception while closing MetaDataReader {0}", sqlex.getMessage());
                    }
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException sqlex) {
                        logger.log(Level.WARNING, "Exception while closing MetaDataReader {0}", sqlex.getMessage());
                    }
                }
            }
        }

        return sb.toString();
    }

    private String getDbSpecificAutoIncrement(String dbProduct) {
        //logger.log(Level.INFO, "DB Product: " + dbProduct);

        String autoIncrement = "";
        if (dbProduct.toLowerCase().contains("mysql")) {
            autoIncrement = "AUTO_INCREMENT";
        }
        if (dbProduct.toLowerCase().contains("microsoft")) {
            autoIncrement = "IDENTITY(1,1)";
        }
        if (dbProduct.toLowerCase().contains("derby")) {
            autoIncrement = "GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)";
        }
        if (dbProduct.toLowerCase().contains("db2")) {
            autoIncrement = "GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1))";
        }

        // SAP Hana
        if (dbProduct.toLowerCase().startsWith("hdb")) {
            autoIncrement = "GENERATED BY DEFAULT AS IDENTITY";
        }

        return autoIncrement;
    }

    private String formattedLength(DbTreeNode currentTreeNode) {
        String l = "";

        HashMap<String, JdbcTypeMapping> mapping =  sql.fredy.admin.Admin.getJdbctypemapping(); 
        JdbcTypeMapping jtm = mapping.get(currentTreeNode.getJdbcTypeName());
        
        FieldTypeInfo fti = getFieldTypes().get(currentTreeNode.getColumnDataType());

        StringBuilder sb = new StringBuilder();
        sb.append(jtm.getDbProductTypeName());
        
        if ( jtm.isHasLength()) {            
            if ( (jtm.getMaxLength() > 0 )  && (currentTreeNode.getLength() >= jtm.getMaxLength())) {
                sb.append("(").append(jtm.getBiggerMaxLengthText());
            } else {
                sb.append("(").append(Integer.toString(currentTreeNode.getLength())).append(")");
            }
        }
        
        /*
        
        switch (currentTreeNode.getColumnDataType()) {
            case java.sql.Types.ARRAY:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.BINARY:
                l = "BLOB(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.BLOB:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.CLOB:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.INTEGER:
                l = fti.getTypName();
                break;
            case java.sql.Types.FLOAT:
                l = fti.getTypName();
                break;
            case java.sql.Types.DOUBLE:
                l = fti.getTypName();
                break;
            case java.sql.Types.DECIMAL:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + "," + Integer.toString(currentTreeNode.getDecimalDigits()) + ")";
                break;
            case java.sql.Types.NUMERIC:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + "," + Integer.toString(currentTreeNode.getDecimalDigits()) + ")";
                break;
            case java.sql.Types.BIGINT:
                l = fti.getTypName();
                break;
            case java.sql.Types.TINYINT:
                l = fti.getTypName();
                break;
            case java.sql.Types.SMALLINT:
                l = fti.getTypName();
                break;
            case java.sql.Types.DATE:
                l = fti.getTypName();
                break;
            case java.sql.Types.TIMESTAMP:
                l = fti.getTypName();
                break;
            case java.sql.Types.TIME:
                l = fti.getTypName();
                break;
            case java.sql.Types.BIT:
                l = fti.getTypName();
                break;
            case java.sql.Types.BOOLEAN:
                l = fti.getTypName();
                break;
            case java.sql.Types.CHAR:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.NVARCHAR:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.VARCHAR:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.LONGNVARCHAR:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.LONGVARCHAR:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.LONGVARBINARY:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.NCHAR:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.NCLOB:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.OTHER:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.REAL:
                l = fti.getTypName();
                break;
            case java.sql.Types.REF:
                l = fti.getTypName();
                break;
            case java.sql.Types.SQLXML:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.STRUCT:
                l = fti.getTypName();
                break;
            case java.sql.Types.JAVA_OBJECT:
                l = fti.getTypName() + "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.DATALINK:
                l = fti.getTypName();
                break;
            case java.sql.Types.DISTINCT:
                l = fti.getTypName();
                break;
            case java.sql.Types.NULL:
                l = fti.getTypName();
                break;
            default:
                l = fti.getTypName();
                break;
        }
        */

        return sb.toString();
    }

    /*
    private String formattedLength(DbTreeNode currentTreeNode) {
        String l = "";
        int type = currentTreeNode.getColumnDataType();

        switch (type) {
            case java.sql.Types.ARRAY:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.BINARY:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.BLOB:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.CLOB:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.INTEGER:
                l = "";
                break;
            case java.sql.Types.FLOAT:
                //l = "(" + Integer.toString(currentTreeNode.getLength()) + "," + Integer.toString(currentTreeNode.getNumPrec()) + ")";
                l = "";
                break;
            case java.sql.Types.DOUBLE:
                //l = "(" + Integer.toString(currentTreeNode.getLength()) + "," + Integer.toString(currentTreeNode.getNumPrec()) + ")";
                l = "";
                break;
            case java.sql.Types.DECIMAL:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + "," + Integer.toString(currentTreeNode.getNumPrec()) + ")";
                break;
            case java.sql.Types.NUMERIC:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + "," + Integer.toString(currentTreeNode.getNumPrec()) + ")";
                break;
            case java.sql.Types.BIGINT:
                l = "";
                break;
            case java.sql.Types.TINYINT:
                l = "";
                break;
            case java.sql.Types.SMALLINT:
                l = "";
                break;
            case java.sql.Types.DATE:
                l = "";
                break;
            case java.sql.Types.TIMESTAMP:
                l = "";
                break;
            case java.sql.Types.TIME:
                l = "";
                break;
            case java.sql.Types.BIT:
                l = "";
                break;
            case java.sql.Types.BOOLEAN:
                l = "";
                break;
            case java.sql.Types.CHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.NVARCHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.VARCHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.LONGNVARCHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.LONGVARCHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.LONGVARBINARY:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.NCHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.NCLOB:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.OTHER:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.REAL:
                l = "";
                break;
            case java.sql.Types.REF:
                l = "";
                break;
            case java.sql.Types.SQLXML:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.STRUCT:
                l = "";
                break;
            case java.sql.Types.JAVA_OBJECT:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.DATALINK:
                l = "";
                break;
            case java.sql.Types.DISTINCT:
                l = "";
                break;
            case java.sql.Types.NULL:
                l = "";
                break;
            default:
                l = "";
                break;
        }

        // SQLite messes with the DataTypes therefore I need to manually find TEXT and remove length
        if (currentTreeNode.getProduct().toLowerCase().startsWith("sqlite")) {
            if (currentTreeNode.getColumnType().equalsIgnoreCase("TEXT")) {
                l = "";
            }
        }

        // also MS SQL has a TEXT Type with no length parameter
        if (currentTreeNode.getProduct().toLowerCase().contains("Microsoft SQL Server".toLowerCase())) {
            if (currentTreeNode.getLength() >= 2147483647) {
                l = "(max)";
            }
            if (currentTreeNode.getColumnType().equalsIgnoreCase("TEXT")) {
                l = "";
            }
            if (currentTreeNode.getColumnType().equalsIgnoreCase("IMAGE")) {
                l = "";
            }

        }
        return l;
    }
     */
 /*
     we create a Delete Statement Statement for the selected table
      
     @param prepStatement  if this ist set to true it will create a prepared statement to be used in Java Code, 
     if set to false, just DML is created     
     */
    public String createDeleteStatement(boolean prepStatement) {
        StringBuilder sb = new StringBuilder();
        StringBuilder pk = new StringBuilder();

        DbTreeNode dmt = (DbTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            addChildren(dmt);
        }

        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
            dmt = (DbTreeNode) dmt.getParent();
        }

        if (dmt.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
            sb.append("delete from ");

            if (!dmt.getSchemaName().trim().equalsIgnoreCase("%")) {
                sb.append(dmt.getSchemaName().trim()).append(".");
            }

            sb.append(fixSpaces(dmt.getTableName())).append("\nwhere ");

            // we add the primary key
            int noChildren = dmt.getChildCount();
            for (int i = 0; i < noChildren; i++) {
                DbTreeNode d = (DbTreeNode) dmt.getChildAt(i);
                if (d.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
                    if (d.isPrimaryKey()) {
                        if (pk.length() > 0) {
                            pk.append("\n and  ");
                        }
                        if (prepStatement) {
                            pk.append(fixSpaces(d.getColumnName())).append(" = ?");
                        } else {
                            pk.append(fixSpaces(d.getColumnName())).append(" = ").append(apostrophe(d.getColumnDataType())).append("{...}").append(apostrophe(d.getColumnDataType()));
                        }
                    }
                }
            }
            if (pk.length() < 1) {
                pk.append(" {add your where condition here} ");
            }
            sb.append(pk.toString());
        }

        return sb.toString();
    }

    private String apostrophe(int dataType) {
        String a = "";
        switch (dataType) {
            case java.sql.Types.ARRAY:
                a = "";
                break;
            case java.sql.Types.BIGINT:
                a = "";
                break;
            case java.sql.Types.BINARY:
                a = "";
                break;
            case java.sql.Types.BIT:
                a = "";
                break;
            case java.sql.Types.BLOB:
                a = "";
                break;
            case java.sql.Types.BOOLEAN:
                a = "";
                break;
            case java.sql.Types.CHAR:
                a = "'";
                break;
            case java.sql.Types.CLOB:
                a = "'";
                break;
            case java.sql.Types.DATALINK:
                a = "";
                break;
            case java.sql.Types.DATE:
                a = "'";
                break;
            case java.sql.Types.DECIMAL:
                a = "";
                break;
            case java.sql.Types.DISTINCT:
                a = "";
                break;
            case java.sql.Types.DOUBLE:
                a = "";
                break;
            case java.sql.Types.FLOAT:
                a = "";
                break;
            case java.sql.Types.INTEGER:
                a = "";
                break;
            case java.sql.Types.JAVA_OBJECT:
                a = "";
                break;
            case java.sql.Types.LONGNVARCHAR:
                a = "'";
                break;
            case java.sql.Types.LONGVARBINARY:
                a = "'";
                break;
            case java.sql.Types.LONGVARCHAR:
                a = "'";
                break;
            case java.sql.Types.NCHAR:
                a = "'";
                break;
            case java.sql.Types.NCLOB:
                a = "";
                break;
            case java.sql.Types.NULL:
                a = "";
                break;
            case java.sql.Types.NUMERIC:
                a = "";
                break;
            case java.sql.Types.NVARCHAR:
                a = "'";
                break;
            case java.sql.Types.OTHER:
                a = "";
                break;
            case java.sql.Types.REAL:
                a = "";
                break;
            case java.sql.Types.REF:
                a = "";
                break;
            case java.sql.Types.ROWID:
                a = "";
                break;
            case java.sql.Types.SMALLINT:
                a = "";
                break;
            case java.sql.Types.SQLXML:
                a = "'";
                break;
            case java.sql.Types.STRUCT:
                a = "";
                break;
            case java.sql.Types.TIME:
                a = "'";
                break;
            case java.sql.Types.TIMESTAMP:
                a = "'";
                break;
            case java.sql.Types.TINYINT:
                a = "";
                break;
            case java.sql.Types.VARBINARY:
                a = "";
                break;
            case java.sql.Types.VARCHAR:
                a = "'";
                break;
            default:
                a = "";
                break;
        }

        return a;
    }

    /**
     * @return the databaseProductVersion
     */
    public String getDatabaseProductVersion() {
        if (databaseProductVersion == null) {
            setDbValues();
        }
        return databaseProductVersion;
    }

    private void setDbValues() {
        try {
            dmd = con.getMetaData();
            setDatabase(con.getCatalog());
            setDatabaseProduct(dmd.getDatabaseProductName());
            setDatabaseProductVersion(Integer.toString(dmd.getDatabaseMajorVersion()) + "." + Integer.toString(dmd.getDatabaseMinorVersion()));
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "can not read DataBaseMetaData {0}", sqlex.getMessage());
        } catch (Exception e) {

        } finally {

        }
    }

    /**
     * @param databaseProductVersion the databaseProductVersion to set
     */
    public void setDatabaseProductVersion(String databaseProductVersion) {
        this.databaseProductVersion = databaseProductVersion;
    }

    private ArrayList<String> sqlWords;
    private Iterator sqlWordsIterator;

    /**
     * @return the sqlWords
     */
    public ArrayList getSqlWords() {
        return sqlWords;
    }

    /**
     * @param sqlWords the sqlWords to set
     */
    public void setSqlWords(ArrayList sqlWords) {
        this.sqlWords = sqlWords;
    }

    private void createSqlWords() {
        SqlWords s = new SqlWords();
        sqlWords = s.getSqlWords();
        sqlWordsIterator = sqlWords.iterator();
    }

    private String inBrackets(String v) {
        // if there are already some ", we remove them as a workaround from DbTreeNode
        v = v.replaceAll("\"", "");
        return getQuote() + v + getQuote();
    }

    private void walkTree() {
        /*
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        DbTreeNode root = (DbTreeNode) model.getRoot();
         */
        DbTreeNode node = (DbTreeNode) tree.getLastSelectedPathComponent();
        printNode(node);

    }

    private void printNode(DbTreeNode dmt) {
        int children = dmt.getChildCount();
        System.out.println(getClass().getName() + ".printNode(" + dmt.getType() + ") " + children);
        for (int i = 0; i < children; i++) {
            DbTreeNode dtn = (DbTreeNode) dmt.getChildAt(i);
            if (dtn.getChildCount() > 0) {
                printNode(dtn);
            } else {
                //StringBuilder sb = new StringBuilder();                
                System.out.println(dtn.getType() + ": " + dtn.getDbName() + "." + dtn.getSchemaName() + "." + dtn.getTableName() + "." + dtn.getColumnName());

            }
        }

    }

    private static class TreeExpansionListenerImpl implements TreeExpansionListener {

        public TreeExpansionListenerImpl() {
        }

        @Override
        public void treeExpanded(TreeExpansionEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void treeCollapsed(TreeExpansionEvent event) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    /**
     * @return the defaultBackGround
     */
    public Color getDefaultBackGround() {
        return defaultBackGround;
    }

    /**
     * @param defaultBackGround the defaultBackGround to set
     */
    public void setDefaultBackGround(Color defaultBackGround) {
        this.defaultBackGround = defaultBackGround;
    }

    /**
     * @return the autoscan
     */
    public boolean isAutoscan() {
        return autoscan;
    }

    /**
     * @param autoscan the autoscan to set
     */
    public void setAutoscan(boolean autoscan) {
        this.autoscan = autoscan;
    }

    public void refreshTree() {
        logger.log(Level.INFO, "Refreshing Tree");

        try {
            DbTreeNode node = (DbTreeNode) selPath.getLastPathComponent();
            //DbTreeNode node = (DbTreeNode) dmta.getParent();

            node.removeAllChildren();

            if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_SCHEMA)) {
                addTables(node.getSchemaName(), node);
                addFunctions(node.getSchemaName(), node);
                tree.updateUI();
            }
            if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
                addColumns(node.getSchemaName(), node.getEntityType(), node.getTableName(), node);
                tree.updateUI();
            }
            if (node.getType().equalsIgnoreCase(DbTreeNode.TYPE_DATABASE)) {
                createTopNodes();
                tree.updateUI();
            }

        } catch (Exception treeex) {

        }
    }

    /**
     * @return the fieldTypes
     */
    public HashMap<Integer, FieldTypeInfo> getFieldTypes() {
        if (fieldTypes == null) {

        }

        return fieldTypes;
    }

    /**
     * @param fieldTypes the fieldTypes to set
     */
    public void setFieldTypes(HashMap<Integer, FieldTypeInfo> fieldTypes) {
        this.fieldTypes = fieldTypes;
    }

    /**
     * @return the quote
     */
    public String getQuote() {
        return quote;
    }

    /**
     * @param quote the quote to set
     */
    public void setQuote(String quote) {
        this.quote = quote;
    }
}
