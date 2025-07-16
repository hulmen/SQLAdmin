package sql.fredy.sqltools;

/**
 *
 * This software is part of David Good's contribution to the Admin-Framework *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
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
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import sql.fredy.ui.QueryCode;
import sql.fredy.ui.FieldNameType;
import sql.fredy.metadata.DbInfo;
import java.sql.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import javax.swing.tree.*;
import java.io.*;

import sql.fredy.tools.DateHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import sql.fredy.connection.DataSource;

public class SqlPickList extends JPanel {

    Logger logger = Logger.getLogger("sql.fredy.sqltools");

    private String productName = null;

    private String tableNamePattern = "%";

    /**
     * Get the value of con.
     *
     * @return value of con.
     */
    String host = "localhost";

    /**
     * Get the value of host.
     *
     * @return Value of host.
     */
    public String getHost() {
        return host;
    }

    /**
     * Set the value of host.
     *
     * @param v Value to assign to host.
     */
    public void setHost(String v) {
        this.host = v;
    }

    String user;

    /**
     * Get the value of user.
     *
     * @return Value of user.
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the value of user.
     *
     * @param v Value to assign to user.
     */
    public void setUser(String v) {
        this.user = v;
    }

    String password;

    /**
     * Get the value of password.
     *
     * @return Value of password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password.
     *
     * @param v Value to assign to password.
     */
    public void setPassword(String v) {
        this.password = v;
    }

    String database;

    /**
     * Get the value of database.
     *
     * @return Value of database.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Set the value of database.
     *
     * @param v Value to assign to database.
     */
    public void setDatabase(String v) {
        this.database = v;
    }

    String schema = "%";

    /**
     * Get the value of schema.
     *
     * @return Value of schema.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Set the value of schema.
     *
     * @param v Value to assign to schema.
     */
    public void setSchema(String v) {
        this.schema = v;
    }

    private DatabaseMetaData dmd;
    ResultSet rsTables;
    Vector vTables = new Vector(10, 10);
    JTree jtTables;
    JButton cbPaste;
    Container jPanel1 = this;

    private java.sql.Connection sqlCon = null;
    private Connection con = null;

    public Connection getCon() {

        try {
            if ((null == con) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
                setDatabase(DataSource.getInstance().getDataBase());
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection {0}", ex.getMessage());
        }

        return con;
    }

    SqlPickList(ActionListener paste) {
        init(paste);
    }

    SqlPickList(ActionListener paste, String schema) {
        setSchema(schema);
        init(paste);
    }

    SqlPickList(ActionListener paste, String schema, String tablePattern) {

        /*
         do we have the schema included in the TabelNamePattern?
         */
        String[] s = tablePattern.split("\\.");
        if (s.length > 1) {
            schema = s[0];
            tablePattern = s[1];
        }

        setSchema(schema);
        setTableNamePattern(tablePattern);
        init(paste);
    }

    TreePath selPath;

    public void refreshTable() {

    }

    public void init(ActionListener paste) {
        popUp();

        Connection con = getCon();

        try {

            //getCon();
            dmd = con.getMetaData();

            /*
             rsTables = dmd.getTables(null,
             schema,
             null,
             new String[]{"TABLE", "VIEW"});
             */
            rsTables = dmd.getTables(getDatabase(),
                    getSchema(),
                    getTableNamePattern(),
                    new String[]{"TABLE", "VIEW"});

            getTableList(rsTables);

            jPanel1.setLayout(new BorderLayout());

            cbPaste = new JButton("Paste");
            cbPaste.addActionListener(paste);
            cbPaste.setActionCommand("");

            jPanel1.add(cbPaste, BorderLayout.SOUTH);

            jtTables = new JTree(vTables);

            jtTables.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent e) {
                    selPath = jtTables.getPathForLocation(e.getX(), e.getY());
                    if (selPath != null) {

                        int centerButton = e.BUTTON2;
                        if (MouseInfo.getNumberOfButtons() < 3) {
                            centerButton = 0;
                        }

                        // add popup if once center click
                        if (e.getButton() == centerButton) {
                            pm.show(e.getComponent(), e.getX(), e.getY());
                        } else {
                            cbPaste.setActionCommand(" " + ((PickListItem) ((DefaultMutableTreeNode) (selPath.getLastPathComponent())).getUserObject()).getName() + " ");
                            if (e.getClickCount() == 2) {
                                e.consume();
                                cbPaste.doClick();

                            }
                        }
                    }
                }
            }
            );
            doKeyListener();
            JScrollPane scp = new JScrollPane(jtTables);
            scp.setName("jtTables");

            scp.setSize(
                    100, 80);

            jPanel1.add(scp);

        } catch (SQLException e) {
            //e.printStackTrace(); 
            logger.log(Level.WARNING, "SQLException: {0}", e.getMessage());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception: {0}", e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "SQLException while closing connection: {0}", e.getMessage());
                }
            }
        }
    }

    private void getTableList(ResultSet rs) throws Exception {
        while (rs.next()) {
            vTables.add(new ColumnList(rs.getString(3)));
        }
    }

    /**
     * @return the tableNamePattern
     */
    public String getTableNamePattern() {
        return tableNamePattern;
    }

    /**
     * @param tableNamePattern the tableNamePattern to set
     */
    public void setTableNamePattern(String tableNamePattern) {
        this.tableNamePattern = tableNamePattern;
    }

    /**
     * @return the producName
     */
    DbInfo dbInfo;

    public String getProductName() {
        if (productName == null) {
            dbInfo = new DbInfo();
            setProductName(dbInfo.getProductName());
        }

        return productName;
    }

    /**
     * @param producName the producName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public class ColumnList extends Vector implements PickListItem {

        private String tableName;

        public ColumnList(String TableName) throws Exception {
            super(10, 5);
            tableName = TableName;
            ResultSet rs = dmd.getColumns(null, null, tableName, null);
            while (rs.next()) {
                add(new Column(rs.getString(4)));
            }
        }

        public String toString() {
            return tableName;
        }

        public String getName() {
            return tableName;
        }

        public class Column implements PickListItem {

            private String columnName;

            public Column(String columnName) {
                this.columnName = columnName;
            }

            public String toString() {
                return columnName;
            }

            public String getName() {
                return ColumnList.this.tableName + "." + columnName;
            }
        }
    }

    /**
     * Getter for property sqlCon.
     *
     * @return Value of property sqlCon.
     */
    public java.sql.Connection getSqlCon() {
        return sqlCon;
    }

    /**
     * Setter for property sqlCon.
     *
     * @param sqlCon New value of property sqlCon.
     */
    public void setSqlCon(java.sql.Connection sqlCon) {
        this.sqlCon = sqlCon;
    }

    JPopupMenu pm;

    private void popUp() {
        pm = new JPopupMenu("Helper");
        pm.add(tablePopUp());
        pm.add(rowPopUp());

        // this is MS SQL specific
        if (getProductName().toLowerCase().startsWith("microsoft")) {
            pm.add(mssqlHelper());
        }

        pm.add(codePopUp());

        /*
         final JPanel a = this;

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
         pm.show(a, e.getX(), e.getY());
         }
         }
         }
         };
         this.addMouseListener(mouseListener);
         */
    }

    private String spaceFixer(String s) {
        s = s.trim();
        if (s.indexOf(' ') > 0) {
            s = "'" + s + "'";
        }
        return s;
    }

    private String firstUpper(String s) {
        s = s.substring(0, 1).toUpperCase() + s.substring(1);
        return s;
    }

    private String firstLower(String s) {
        s = s.substring(0, 1).toLowerCase() + s.substring(1);
        return s;
    }

    private ArrayList<FieldNameType> getFieldList() {
        ArrayList<FieldNameType> fieldNameTypes = new ArrayList();
        String table = getTableName();
        Connection con = getCon();
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = con.getMetaData();
            rs = dmd.getColumns(getDatabase(), getSchema(), table, "%");
            while (rs.next()) {
                FieldNameType fnt = new FieldNameType();
                fnt.setDb(getDatabase());
                fnt.setTable(table);
                fnt.setName(firstLower(spaceFixer(rs.getString(4))));
                fnt.setLength(rs.getInt(7));

                int typ = rs.getInt(5);
                String type = "";
                switch (typ) {
                    case Types.CHAR:
                        type = "String";
                        break;
                    case Types.VARCHAR:
                        type = "String";
                        break;
                    case Types.LONGVARCHAR:
                        type = "String";
                        break;
                    case Types.BIT:
                        type = "boolean";
                        break;
                    case Types.BOOLEAN:
                        type = "boolean";
                        break;
                    case Types.TINYINT:
                        type = "int";
                        break;
                    case Types.SMALLINT:
                        type = "short";
                        break;
                    case Types.INTEGER:
                        type = "int";
                        break;
                    case Types.BIGINT:
                        type = "long";
                        break;
                    case Types.NUMERIC:
                        type = "BigDecimal";
                        break;
                    case Types.FLOAT:
                        type = "float";
                        break;
                    case Types.DECIMAL:
                        type = "float";
                        break;
                    case Types.DOUBLE:
                        type = "double";
                        break;
                    case Types.REAL:
                        type = "float";
                        break;
                    case Types.DATE:
                        type = "java.sql.Date";
                        break;
                    case Types.TIMESTAMP:
                        type = "java.sql.Timestamp";
                        break;
                    case Types.TIME:
                        type = "java.sql.Time";
                        break;
                    default:
                        type = "String";
                        break;
                }
                fnt.setType(type);
                fieldNameTypes.add(fnt);
            }
        } catch (SQLException sqlex) {
            // trage hier deine Fehlermeldung ein
            logger.log(Level.WARNING, "Error while collecting info for  codegeneration : {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
        } catch (Exception e) {
            logger.log(Level.WARNING, "probably no result generated. {0}", e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on close. {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on close. {0}", e.getMessage());
                }
            }
        }
        return fieldNameTypes;
    }

    private JMenu codePopUp() {
        JMenu popup = new JMenu("Code");

        JMenuItem code = new JMenuItem("Code for Programmers");
        code.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (getSchema().length() > 1) {
                    QueryCode qc = new QueryCode("select * from " + getSchema() + "." + getTableName(), getFieldList());
                } else {
                    QueryCode qc = new QueryCode("select * from " + getTableName(), getFieldList());
                }
            }
        });
        popup.add(code);
        return popup;
    }

    private JMenu tablePopUp() {
        JMenu popup = new JMenu("Table Helper");

        JMenuItem selStm = new JMenuItem("Create Select Statement");
        selStm.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createSelectStatement());
                cbPaste.doClick();
            }
        });

        JMenu selectStatements = new JMenu("select statements");

        JMenuItem selAll = new JMenuItem("Create Select * from [Table]");
        selAll.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createSelectAllStatement());
                cbPaste.doClick();
            }
        });

        JMenuItem selAll10 = new JMenuItem("Select top 10");
        selAll10.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createTop10SelectAllStatement());
                cbPaste.doClick();
            }
        });

        JMenuItem selAll100 = new JMenuItem("Select top 100");
        selAll100.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createTop100SelectAllStatement());
                cbPaste.doClick();
            }
        });

        JMenuItem selAll1000 = new JMenuItem("Select top 1000");
        selAll1000.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(createTop1000SelectAllStatement());
                cbPaste.doClick();
            }
        });

        selectStatements.add(selAll);
        selectStatements.add(selAll10);
        selectStatements.add(selAll100);
        selectStatements.add(selAll1000);

        popup.add(selectStatements);
        popup.add(selStm);

        return popup;

    }

    private JMenu mssqlHelper() {

        JMenu popup = new JMenu("MS SQL  Helper");
        JMenuItem dateConversion = new JMenuItem("add DateConversion based on seconds");
        dateConversion.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" [").append(v).append("] =  dateadd(second,").append(v).append(",'1970-01-01 00:00:00.000') , ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }

            }
        });
        popup.add(dateConversion);

        JMenuItem dateConvGMT = new JMenuItem("add DateConversion based on seconds and GMT-offset for Europe/Zurich per today");
        dateConvGMT.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    DateHelper dh = new DateHelper();
                    sb.append(" [").append(v).append("] =  dateadd(second,").append(v).append(" + ").append(Integer.toString(dh.getGmtOffset() / 1000)).append(",'1970-01-01 00:00:00.000') , ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });
        popup.add(dateConvGMT);
        return popup;
    }

    private String getSelectedItem() {
        StringBuilder sl = new StringBuilder();
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        sl.append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" ,\n");
        return sl.toString();
    }

    private String getShort() {
        StringBuilder sl = new StringBuilder();
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            DefaultMutableTreeNode dmt2 = (DefaultMutableTreeNode) dmt.getParent();
            sl.append(fixSpaces(((PickListItem) dmt2.getUserObject()).toString()).substring(0, 1)).append(".").append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" ,\n");
        } else {
            // the Tablename has been selected, so continue with the abbreviation
            sl.append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append("  ").append(fixSpaces(((PickListItem) dmt.getUserObject()).toString()).substring(0, 1));
        }

        return sl.toString();
    }

    /**
     * Adding KeyListener to the JTree
     * <CTRL>+M display the PopUpMenu
     * <CTRL>-A pastes the ColumnName without trailing dot and Tablename
     * <CTRL>-B pastes the first Letter of the tablenamen plus a dot Insert =
     * CTRL-A
     */
    private void doKeyListener() {

        int inFocus = JComponent.WHEN_FOCUSED;
        InputMap iMap = this.getInputMap(inFocus);
        ActionMap aMap = this.getActionMap();

        KeyStroke insert = KeyStroke.getKeyStroke("insert");
        KeyStroke ctrlA = KeyStroke.getKeyStroke("control A");
        KeyStroke ctrlM = KeyStroke.getKeyStroke("control M");
        KeyStroke ctrlB = KeyStroke.getKeyStroke("control B");
        KeyStroke ctrlH = KeyStroke.getKeyStroke("control H");

        iMap.put(insert, "insert");
        iMap.put(ctrlA, "ctrl-A");
        iMap.put(ctrlM, "ctrl-M");
        iMap.put(ctrlB, "ctrl-B");
        iMap.put(ctrlH, "ctrl-H");

        Action insertAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(getSelectedItem());
                cbPaste.doClick();
            }
        };

        Action aliasAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cbPaste.setActionCommand(getShort());
                cbPaste.doClick();
            }
        };

        Action showMenuAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point p = jtTables.getLocation();
                pm.show(jtTables, p.x, p.y);
            }
        };

        Action showHelpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        };

        aMap.put("insert", insertAction);
        aMap.put("ctrl-A", insertAction);
        aMap.put("ctrl-B", aliasAction);
        aMap.put("ctrl-H", showHelpAction);
        aMap.put("ctrl-M", showMenuAction);

    }

    private void showHelp() {
        String msg = "Quick Tipps about Keys\n"
                + "<CTRL>-H  this infobox\n"
                + "<CTRL>-M  displays the Popup-Menu\n"
                + "<CTRL>-A  pastes just the column name without trailing . and tablename\n"
                + "<Insert>  the same as <CTRL>-A\n"
                + "<CTRL>-B  pastes the first letter of the table, a dot and the column name e.g. c.CustomerName";

        JOptionPane.showMessageDialog(null, msg);

    }

    private String getSelectedRow() {
        String leaf = null;

        // only if  a Leaf has been selected
        if (((DefaultMutableTreeNode) (selPath.getLastPathComponent())).isLeaf()) {
            leaf = ((PickListItem) ((DefaultMutableTreeNode) (selPath.getLastPathComponent())).getUserObject()).getName();
        }

        return leaf;
    }

    private String getSelectedTable() {
        String t = null;
        if (!((DefaultMutableTreeNode) (selPath.getLastPathComponent())).isLeaf()) {
            t = ((PickListItem) ((DefaultMutableTreeNode) (selPath.getLastPathComponent())).getUserObject()).getName();
        }
        return t;
    }

    // if one uses Spaces in a field- or table name, it has to be surrounded by "
    private String fixSpaces(String v) {
        if (v != null) {
            if (v.trim().contains(" ")) {
                v = "\"" + v + "\"";
            }
        }
        return v;
    }

    private String getTable() {
        StringBuilder sl = new StringBuilder();
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            dmt = (DefaultMutableTreeNode) dmt.getParent();
        }

        sl.append(fixSpaces(((PickListItem) dmt.getUserObject()).toString()));

        return sl.toString();
    }

    private boolean isSchema() {
        if (getSchema().equals("%")) {
            return false;
        }
        if (getSchema() == null) {
            return false;
        }
        return true;
    }

    private String getSelectedTableObject() {
        StringBuilder sl = new StringBuilder();
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            dmt = (DefaultMutableTreeNode) dmt.getParent();
        }

        if (isSchema()) {
            sl.append(getSchema()).append(".").append(fixSpaces(((PickListItem) dmt.getUserObject()).toString()));
        } else {
            sl.append(fixSpaces(((PickListItem) dmt.getUserObject()).toString()));
        }
        return sl.toString();
    }

    private String getTableName() {

        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            dmt = (DefaultMutableTreeNode) dmt.getParent();
        }
        return fixSpaces(((PickListItem) dmt.getUserObject()).toString());
    }

    private String createSelectAllStatement() {
        StringBuilder sl = new StringBuilder();
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            dmt = (DefaultMutableTreeNode) dmt.getParent();
        }

        if (isSchema()) {
            sl.append("select * from ").append(getSchema()).append(".").append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append("\n");
        } else {
            sl.append("select * from ").append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append("\n");
        }
        return sl.toString();
    }

    private String createTop10SelectAllStatement() {
        StringBuilder sl = new StringBuilder();
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            dmt = (DefaultMutableTreeNode) dmt.getParent();
        }

        String schemaPrefix = "";
        if (isSchema()) {
            schemaPrefix = getSchema() + ".";
        }

        if (getProductName().toLowerCase().startsWith("mysql")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" limit 10").append("\n");
        }
        if (getProductName().toLowerCase().startsWith("oracle")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" where ROWNUM <= 10").append("\n");
        }
        if (getProductName().toLowerCase().startsWith("microsoft")) {
            sl.append("select top 10 * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append("\n");
        }
        if (getProductName().toLowerCase().startsWith("apache derby")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" FETCH FIRST 10 ROWS ONLY").append("\n");
        }

        if (getProductName().toLowerCase().startsWith("postgresql")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" LIMIT 10").append("\n");
        }

        //sl.append("select top 10 * from ").append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append("\n");
        return sl.toString();
    }

    private String createTop100SelectAllStatement() {
        StringBuilder sl = new StringBuilder();
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            dmt = (DefaultMutableTreeNode) dmt.getParent();
        }

        String schemaPrefix = "";
        if (isSchema()) {
            schemaPrefix = getSchema() + ".";
        }

        if (getProductName().toLowerCase().startsWith("mysql")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" limit 100").append("\n");
        }
        if (getProductName().toLowerCase().startsWith("oracle")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" where ROWNUM <= 100").append("\n");
        }
        if (getProductName().toLowerCase().startsWith("microsoft")) {
            sl.append("select top 100 * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append("\n");
        }
        if (getProductName().toLowerCase().startsWith("apache derby")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" FETCH FIRST 100 ROWS ONLY").append("\n");
        }

        if (getProductName().toLowerCase().startsWith("postgresql")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" LIMIT 100").append("\n");
        }

        //sl.append("select top 100 * from ").append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append("\n");
        return sl.toString();
    }

    private String createTop1000SelectAllStatement() {
        StringBuilder sl = new StringBuilder();
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            dmt = (DefaultMutableTreeNode) dmt.getParent();
        }

        String schemaPrefix = "";
        if (isSchema()) {
            schemaPrefix = getSchema() + ".";
        }

        if (getProductName().toLowerCase().startsWith("mysql")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" limit 1000").append("\n");
        }
        if (getProductName().toLowerCase().startsWith("oracle")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" where ROWNUM <= 1000").append("\n");
        }
        if (getProductName().toLowerCase().startsWith("microsoft")) {
            sl.append("select top 1000 * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append("\n");
        }

        if (getProductName().toLowerCase().startsWith("apache derby")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" FETCH FIRST 1000 ROWS ONLY").append("\n");
        }

        if (getProductName().toLowerCase().startsWith("postgresql")) {
            sl.append("select * from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(" LIMIT 1000").append("\n");
        }
        return sl.toString();
    }

    private String createSelectStatement() {
        StringBuilder sl = new StringBuilder();
        DefaultMutableTreeNode dmt = (DefaultMutableTreeNode) selPath.getLastPathComponent();
        if (dmt.isLeaf()) {
            dmt = (DefaultMutableTreeNode) dmt.getParent();
        }

        String schemaPrefix = "";
        if (isSchema()) {
            schemaPrefix = getSchema() + ".";
        }

        sl.append("select ");

        for (int i = 0; i < dmt.getChildCount(); i++) {
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode) dmt.getChildAt(i);

            // make sure, the last Object does not have a ,
            if (i == dmt.getChildCount() - 1) {
                sl.append(" ").append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(".").append(fixSpaces(((PickListItem) tn.getUserObject()).toString())).append(" \n");
            } else {
                sl.append(" ").append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append(".").append(fixSpaces(((PickListItem) tn.getUserObject()).toString())).append(", \n");
            }
        }

        sl.append("from ").append(schemaPrefix).append(fixSpaces(((PickListItem) dmt.getUserObject()).toString())).append("\n");

        return sl.toString();
    }

    private StringBuilder sb;

    private JMenu rowPopUp() {

        sb = new StringBuilder();
        JMenu popup = new JMenu("Row Filters");

        JMenuItem filterMenuItem = new JMenuItem("select distinct [field]");
        JMenu whereMenu = new JMenu("where conditions");
        JMenu andMenu = new JMenu("and conditions");
        JMenuItem orderBy = new JMenuItem("Order by");

        filterMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append("\nselect distinct ").append(v).append("\nfrom ").append(getTable()).append("\norder by ").append(v);
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }

            }
        });

        orderBy.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" order by ").append(v);
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }

            }
        });

        JMenuItem whereLike = new JMenuItem("add 'where like'");
        whereLike.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" where ").append(v).append(" like ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }

            }
        });

        JMenuItem whereEqual = new JMenuItem("add 'where ='");
        whereEqual.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" where ").append(v).append(" = ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem whereLess = new JMenuItem("add 'where <'");
        whereLess.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" where ").append(v).append(" < ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem whereBigger = new JMenuItem("add 'where >'");
        whereBigger.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" where ").append(v).append(" > ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem whereBetween = new JMenuItem("add 'where between'");
        whereBetween.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" where ").append(v).append(" between ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem whereIn = new JMenuItem("add 'where in'");
        whereIn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" where ").append(v).append(" in ( ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem whereIsNull = new JMenuItem("add 'where is Null'");
        whereIsNull.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" where ").append(v).append(" IS NULL ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem andLike = new JMenuItem("add 'and like'");
        andLike.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" and ").append(v).append(" like ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }

            }
        });

        JMenuItem andEqual = new JMenuItem("add 'and ='");
        andEqual.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" and ").append(v).append(" = ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem andLess = new JMenuItem("add 'and <'");
        andLess.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" and ").append(v).append(" < ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem andBigger = new JMenuItem("add 'and >'");
        andBigger.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" and ").append(v).append(" > ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem andBetween = new JMenuItem("add 'and between'");
        andBetween.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" and ").append(v).append(" between ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem andIn = new JMenuItem("add 'and in'");
        andIn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" and ").append(v).append(" in ( ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        JMenuItem andIsNull = new JMenuItem("add 'and is Null'");
        andIsNull.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sb = new StringBuilder();
                String v = null;
                v = fixSpaces(getSelectedRow());
                if (v != null) {
                    sb.append(" and ").append(v).append(" IS NULL ");
                    cbPaste.setActionCommand(sb.toString());
                    cbPaste.doClick();
                }
            }
        });

        whereMenu.add(whereLike);
        whereMenu.add(whereEqual);
        whereMenu.add(whereLess);
        whereMenu.add(whereBigger);
        whereMenu.add(whereBetween);
        whereMenu.add(whereIsNull);

        andMenu.add(andLike);
        andMenu.add(andEqual);
        andMenu.add(andLess);
        andMenu.add(andBigger);
        andMenu.add(andBetween);
        andMenu.add(andIsNull);

        popup.add(filterMenuItem);
        popup.add(whereMenu);
        popup.add(andMenu);
        popup.add(orderBy);

        return popup;
    }

}
