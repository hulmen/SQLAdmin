/*
 * CreateTable2.java
 *
 * Created on September 13, 2003, 2:40 PM
 *
 * This software is part of the Admin-Framework and free software (MIT-License)
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 *
 *
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
package sql.fredy.sqltools;

import sql.fredy.ui.CloseableFrame;
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.ImageLabel;
import sql.fredy.ui.JTextFieldFilter;
import sql.fredy.metadata.DbInfo;
import sql.fredy.metadata.FieldTypeInfo;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import javax.swing.*;
import java.util.Vector;
import java.util.logging.*;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.swing.table.TableColumn;
import javax.swing.DefaultCellEditor;
import java.sql.*;
import sql.fredy.connection.DataSource;


/**
 *
 * @author sql@hulmen.ch
 */
public class CreateTable2 extends javax.swing.JPanel implements ActionListener {

    /**
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Creates a new instance of CreateTable2
     */
    public CreateTable2() {
    }

    public CreateTable2(String schema) {
        setSchema(schema);       
        init();
    }
 
    private void init() {
        splash = new CreateTable2Splash();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        splash.setMessage("fetching DB-Info from Metadata");
        dbi = new DbInfo();

        splash.setMessage("init field types");
        initFieldType();

        splash.setMessage("create GUI");
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.SOUTH, buttonPanel());

        this.add(BorderLayout.CENTER, tablePanel());

        this.add(BorderLayout.NORTH, infoPanel());

        splash.close();
        setCursor(Cursor.getDefaultCursor());
    }

    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        ImageButton create = new ImageButton(null, "newsheet.gif", "this will create the table");
        create.setActionCommand(CREATE);
        create.addActionListener(this);

        cancel = new ImageButton(null, "exit.gif", "Quit without create");

        ImageButton clear = new ImageButton(null, "clear.gif", "Clear row");
        clear.setActionCommand(CLEAR);
        clear.addActionListener(this);

        ImageButton addRows = new ImageButton(null, "plusplus.gif", "Add another 10 rows");
        addRows.setActionCommand(ADDROWS);
        addRows.addActionListener(this);

        ImageButton scandb = new ImageButton(null, "datastore.gif", "rescan DB, to display the new table(s) within the references-tree");
        scandb.setActionCommand(SCANDB);
        scandb.addActionListener(this);

        edit = new ImageButton(null, "document.gif", "Edit query. OK creates table directly");
        edit.setActionCommand(EDIT);
        edit.addActionListener(this);

        panel.add(create);
        panel.add(addRows);
        panel.add(edit);
        panel.add(clear);
        panel.add(cancel);

        panel.add(new ImageLabel(null, "blank-20.gif", null));
        panel.add(new ImageLabel(null, "blank-20.gif", null));

        panel.add(scandb);

        return panel;
    }

    // this Panel does the TableName
    private JPanel tablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        splash.setMessage("gathering Model");
        createTableModel = new CreateTableModel(dbi);
        createTable = new JTable(createTableModel);
        panel.add(BorderLayout.CENTER, new JScrollPane(createTable));
        panel.setBorder(new javax.swing.border.EtchedBorder());
        addRows(255);

        return panel;
    }

    // enter the Tablename here and get Info about the RDBMS
    private JPanel infoPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        panel.setLayout(new FlowLayout());

        panel.add(new JLabel("Tablename"));
        tableName = new JTextField(20);
        panel.add(tableName);

        JLabel rdbms = new JLabel("RDBMS: " + dbi.getProductName() + " " + dbi.getProductVersion());
        rdbms.setBorder(new javax.swing.border.EtchedBorder());
        panel.add(rdbms);

        JLabel jdbcDriver = new JLabel("JDBC-Driver: " + dbi.getDriverName() + " " + dbi.getDriverVersion());
        jdbcDriver.setBorder(new javax.swing.border.EtchedBorder());
        panel.add(jdbcDriver);

        return panel;

    }

    /*
     * this method creates the fieldtype selection combobox,
     * containing standard and RDBMS-specific values
     */
    private void initFieldType() {

        String[] items = {"CHAR", "VARCHAR", "INTEGER",
            "FLOAT", "DOUBLE", "BLOB",
            "DATE", "DATETIME", "TIMESTAMP",
            "ENUM", "SET",};
        fieldType = new JComboBox(items);
        //fieldType = new JComboBox();

        /**
         * to allow the use of RDBMS specific types all the types are stored
         * within a HashMap so we are able to detect, if Auto Increment is
         * allowed for a particular type
         *
         */
        splash.setMessage("field Types");
        fieldTypes = new HashMap();
        Vector v = dbi.getSQLWords();
        if (v.size() > 0) {
            fieldType.addItem("--- DB-Specific below this ---");
            for (int i = 0; i < v.size(); i++) {
                FieldTypeInfo fti = (FieldTypeInfo) v.elementAt(i);
                fieldTypes.put((String) fti.getTypName(), fti.isAutoIncrement());
                String s = (String) fti.getTypName();
                boolean add = true;
                for (int j = 0; j < fieldType.getItemCount(); j++) {
                    if (s.equalsIgnoreCase((String) fieldType.getItemAt(j))) {
                        add = false;
                    }
                }
                //String s2 = (String)fieldType.getItemAt(i);
                //if ( add ) fieldType.addItem((String) s2 );
                if (add) {
                    fieldType.addItem((String) s);
                }

            }
        }

        // we also initialize the Other-Field if MySQL we add AUTO_INCREMENT
        //if ( dbi.getProductName().toLowerCase().indexOf("mysql") >= 0 ) other.addItem((String)"AUTO_INCREMENT");
        splash.setMessage("other fields");
        other = new JComboBox();
        other.setEditable(true);
        other.addItem((String) " ");
        other.addItem((String) "AUTO_INCREMENT");
        if (dbi.getProductName().toLowerCase().indexOf("derby") >= 0) {
            other.addItem((String) "generated always as identity (START WITH 1, INCREMENT BY 1)");
            other.addItem((String) "generated by default as identity");
        }

        // this is AUTO_INCREMENT for MS SQL Server
        if (dbi.getProductName().toLowerCase().indexOf("microsoft") >= 0) {
            other.addItem((String) "IDENTITY(1,1)");

        }
        
        // this is AUTO_INCREMENT for SAP Hana
        if (dbi.getProductName().toLowerCase().startsWith("hdb")) {
            other.addItem((String) "GENERATED BY DEFAULT AS IDENTITY");
        }
        
        // this is AUTO_INCREMENT for DB2
        if (dbi.getProductName().toLowerCase().startsWith("db2")) {
            other.addItem((String) "GENERATED ALWAYS AS IDENTITY (START WITH 1 INCREMENT BY 1))");
        }
                  
    }

    private String user = System.getProperty("user.name");
    private String host = "localhost";
    private String password = null;
    private String database = null;
    private String schema = "%";   
    private Logger logger = Logger.getLogger("sql.fredy.sqltools");
    private JTextField tableName;
    private JComboBox fieldType;
    private JCheckBox notNull, defaultValue, autoIncrement, primaryKey, comment, referer, index;
    private JTextField fieldName, fieldLength, defaultText, commentText, indexName, indexColumns;
    private JComboBox references;
    private CreateTableModel createTableModel;
    private JTable createTable;
    private HashMap fieldTypes;
    private DbInfo dbi;
    private static final String EDIT = "EDIT";
    private static final String CLEAR = "CLEAR";
    private static final String ADDROWS = "ADDROWS";
    private static final String SAVE = "SAVE";
    private static final String CREATE = "CREATE";
    private ImageButton edit;
    public ImageButton cancel;

    private JComboBox other;

    public JMenuItem exitMenuItem;

    public static final String SCANDB = "SCANDB";

    private String CREATEEDITED = "CREATEEDITED";

    private JTextArea content;

    private CreateTable2Splash splash;

 
     /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    public Connection getConnection() {
        Connection con = null;
        try {
            con = DataSource.getInstance().getConnection();            
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {
            return con;
        }
    }
    
    String getSql() {
        String query = null;
        return query;
    }

    public static void main(String args[]) {
        System.out.println("CreateTable2\n"
                + "------------ Version 0.1\n"
                + "by Fredy Fischer, this free software (MIT-License)\n\n"
                + "-h host\n-u user\n-p password\n-d database\n-s schema");
        int i = 0;
        String host = "localhost";
        String user = System.getProperty("user.name");
        String db = null;
        String password = null;
        String schema = "%";

        while (i < args.length) {

            if ((args[i].equals("-h")) || (args[i].equals("-host"))) {
                i++;
                host = args[i];
            }

            if ((args[i].equals("-u")) || (args[i].equals("-user"))) {
                i++;
                user = args[i];
            }

            if ((args[i].equals("-s")) || (args[i].equals("-schema"))) {
                i++;
                schema = args[i];
            }

            if ((args[i].equals("-p")) || (args[i].equals("-password"))) {
                i++;
                password = args[i];
            }
            if ((args[i].equals("-d")) || (args[i].equals("-database"))) {
                i++;
                db = args[i];
            }
            i++;
        }
        CloseableFrame frame = new CloseableFrame("Create Table");
        final CreateTable2 cr = new CreateTable2(schema);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(BorderLayout.CENTER, cr);
        frame.setJMenuBar(cr.menuBar());
        frame.pack();
        frame.setVisible(true);
        cr.cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cr.close();
                System.exit(0);
            }
        });
        cr.exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cr.close();
                System.exit(0);
            }
        });
    }

    private void addRows(int number) {
        for (int i = 0; i < number; i++) {
            CreateTableRowObject ctro = new CreateTableRowObject();
            createTableModel.addRow(ctro.getVector());
        }
        doCellEditors();

    }

    public void doCellEditors() {

        // field-Type
        TableColumn typeColumn = createTable.getColumnModel().getColumn(createTableModel.TYPE);
        typeColumn.setCellEditor(new DefaultCellEditor(fieldType));

        // field-length
        TableColumn lengthColumn = createTable.getColumnModel().getColumn(createTableModel.LENGTH);
        //lengthColumn.setCellEditor(new TableSpinnerEditor(0,0,255,1));

        JTextField lc = new JTextField();
        lc.setHorizontalAlignment(JTextField.RIGHT);
        lc.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));

        lengthColumn.setCellEditor(new DefaultCellEditor(lc));

        // field defaults
        TableColumn defaultsColumn = createTable.getColumnModel().getColumn(createTableModel.DEFAULT);
        defaultsColumn.setCellEditor(new TextTableCellEditor("Default", 10, 25));

        // field Check Constraints
        TableColumn checkColumn = createTable.getColumnModel().getColumn(createTableModel.CHECKCONSTRAINT);
        checkColumn.setCellEditor(new TextTableCellEditor("Check", 10, 25));

        // field References
        splash.setMessage("Table references...");
        TableColumn referencesColumn = createTable.getColumnModel().getColumn(createTableModel.REFERENCES);
        referencesColumn.setCellEditor(new ReferencesTableCellEditor("References", getSchema(), getConnection()));

        // field Other
        TableColumn otherColumn = createTable.getColumnModel().getColumn(createTableModel.OTHER);
        otherColumn.setCellEditor(new DefaultCellEditor(other));

    }

    public String getSqlCommand() {
        StringBuffer cmd = new StringBuffer("create table ");
        String primKey = null;

        if (dbi.getProductName().toLowerCase().indexOf("derby") >= 0) {
                 cmd.append(getSchema()).append(".");
        }
        cmd.append(tableName.getText());
        cmd.append(" (\n");

        for (int i = 0; i < createTableModel.getRowCount(); i++) {
            CreateTableRowObject ctr = createTableModel.getRow(i);
            if ((ctr.getName().trim().length() > 0) && (ctr.getType().trim().length() > 0)) {
                if (i > 0) {
                    cmd.append(",\n");
                }
                cmd.append("\t");
                cmd.append(ctr.getName() + " ");
                cmd.append(ctr.getType());

                //if ( (ctr.getLength() > 0) && ("INTEGER".equals(ctr.getName().trim())) ) {
                if ((ctr.getLength() > 0) && (!ctr.getType().trim().toLowerCase().startsWith("int"))) {
                    cmd.append("(" + Integer.toString(ctr.getLength()) + ") ");
                } else {
                    cmd.append(" ");
                }
                if (ctr.isNotNull()) {
                    cmd.append("NOT NULL ");
                }

                // put any character oriented fields or time/date-oriented fields in '
                if (ctr.getDefaultValue().trim().length() > 0) {
                    String delim = "";
                    String dt = ctr.getType().toLowerCase();
                    if ((dt.indexOf("text") > -1) || (dt.indexOf("char") > -1) || (dt.indexOf("time") > -1) || (dt.indexOf("date") > -1)) {
                        if ((!dt.startsWith("'")) && (!dt.startsWith("'"))) {
                            delim = "'";
                        }
                    }
                    cmd.append("DEFAULT " + delim + ctr.getDefaultValue().trim() + delim + " ");
                }

                if (ctr.getCheckConstraint().trim().length() > 0) {
                    cmd.append("CHECK (" + ctr.getName() + " " + ctr.getCheckConstraint().trim() + ") ");
                }
                if (ctr.getReferences().trim().length() > 0) {
                    StringTokenizer st = new StringTokenizer(ctr.getReferences().trim(), ".");
                    cmd.append("REFERENCES " + st.nextToken() + " ");
                    cmd.append("(" + st.nextToken() + ") ");
                }

                if (ctr.getIndex().trim().length() > 0) {
                    cmd.append(ctr.getIndex() + " ");
                }
                if (ctr.isPrimaryKey()) {
                    if (primKey != null) {
                        primKey = primKey + ",";
                    } else {
                        primKey = "";
                    }
                    primKey = primKey + ctr.getName();
                }
            }
        }
        if (primKey != null) {
            cmd.append(",\n\tPRIMARY KEY (" + primKey + ")\n");
        }
        cmd.append(")");

        return cmd.toString();
    }

    private void message(String msg) {
        logger.log(Level.INFO, msg);
        JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.WARNING_MESSAGE);

    }

    public void editCommand(Component component) {
        final JDialog dialog = new JDialog();
        dialog.getContentPane().setLayout(new BorderLayout());
        content = new JTextArea(20, 50);
        content.setFont(new Font("Monospaced", Font.PLAIN, 12));
        content.setText(getSqlCommand());
        dialog.getContentPane().add(new JScrollPane(content));

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");
        JMenuItem execCmd = new JMenuItem("Create Table");
        JMenuItem openFile = new JMenuItem("Open");
        JMenuItem saveFile = new JMenuItem("Save");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem paste = new JMenuItem("Paste");
        final Toolkit tk = this.getToolkit();

        execCmd.setActionCommand(CREATEEDITED);
        execCmd.addActionListener(this);

        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Clipboard c = tk.getSystemClipboard();
                Transferable t = c.getContents(this);
                try {
                    String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                    content.setText(s);
                } catch (Exception eexc) {
                    tk.beep();
                }
            }
        }
        );
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = content.getText();
                StringSelection ss = new StringSelection(s);
                tk.getSystemClipboard().setContents(ss, ss);
            }
        }
        );

        saveFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter();
                fw.setFilter(new String[]{"sql", "SQL", "txt", "TXT"});
                fw.setContent(content.getText());
                fw.setFileName("?");
                fw.setSwitch("a");
                fw.write();
            }
        }
        );

        openFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.ReadFile rf = new sql.fredy.io.ReadFile();
                rf.setFilter(new String[]{"sql", "SQL", "txt", "TXT"});
                rf.setFileName("?");
                if (content.getText().length() > 0) {
                    content.append(";\n");
                }
                content.append(rf.getText());
                content.updateUI();
            }
        }
        );

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        }
        );

        fileMenu.add(execCmd);
        fileMenu.add(new JSeparator());
        fileMenu.add(openFile);
        fileMenu.add(saveFile);
        fileMenu.add(new JSeparator());
        fileMenu.add(exit);

        editMenu.add(copy);
        editMenu.add(paste);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        dialog.setJMenuBar(menuBar);

        dialog.setModal(true);
        dialog.setTitle("SQL-Command");
        dialog.setLocationRelativeTo(component);
        dialog.pack();
        dialog.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            // open Editor to edit query
            // create table when leaving editor with ok
            if (tableName.getText().length() > 0) {
                editCommand(edit);
            } else {
                message("I need a tablename");
            }
        }
        if (CREATE.equals(e.getActionCommand())) {
            if (tableName.getText().length() > 0) {
                createTable(getSqlCommand());
            } else {
                message("I need a tablename");
            }
        }
        if (CREATEEDITED.equals(e.getActionCommand())) {
            if ((tableName.getText().length() > 0) && (content.getText().length() > 10)) {
                createTable(content.getText());
            } else {
                message("SQL-Statement too short");
            }
        }

        if (ADDROWS.equals(e.getActionCommand())) {
            addRows(10);
        }
        if (CLEAR.equals(e.getActionCommand())) {
            createTableModel.clearData();
            tableName.setText("");
            addRows(255);
            createTable.updateUI();
        }
        if (SCANDB.equals(e.getActionCommand())) {
            rescanDB();
        }
    }

    private void createTable(String cmd) {
        Connection con = null;
        Statement stmt = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            int records = stmt.executeUpdate(cmd);
            logger.log(Level.INFO, "Table created:\n" + cmd);
        } catch (SQLException sqle) {
            logger.log(Level.SEVERE, "Exception while creating table: " + sqle.getMessage());
            logger.log(Level.SEVERE, "SQL-State: " + sqle.getSQLState());
            logger.log(Level.WARNING, "Vendor specific error-message: " + sqle.getErrorCode());
            message("Exception while creating table: " + sqle.getMessage());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Generic exception: " + e.getMessage());
            message("Generic exception: " + e.getMessage());
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch (SQLException e ) {
                    logger.log(Level.WARNING,"Exception while closing statement {0}", e.getMessage());
                }
            }
            if ( con != null ) {
                try {
                    con.close();
                } catch (SQLException e ) {
                    logger.log(Level.WARNING,"Exception while closing connection {0}", e.getMessage());
                }
            }
        }

    }

    public JMenuBar menuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenu editMenu = new JMenu("Edit");

        JMenuItem createItem = new JMenuItem("Create Table");
        JMenuItem addRowsItem = new JMenuItem("add 10 rows");
        JMenuItem editItem = new JMenuItem("Edit Query");
        JMenuItem clearItem = new JMenuItem("Clear");
        JMenuItem scanItem = new JMenuItem("Rescan Metadata");
        exitMenuItem = new JMenuItem("Exit");

        createItem.setActionCommand(CREATE);
        addRowsItem.setActionCommand(ADDROWS);
        editItem.setActionCommand(EDIT);
        clearItem.setActionCommand(CLEAR);
        scanItem.setActionCommand(SCANDB);

        createItem.addActionListener(this);
        addRowsItem.addActionListener(this);
        editItem.addActionListener(this);
        clearItem.addActionListener(this);
        scanItem.addActionListener(this);

        fileMenu.add(createItem);
        fileMenu.add(new JSeparator());
        fileMenu.add(exitMenuItem);

        editMenu.add(editItem);
        editMenu.add(addRowsItem);
        editMenu.add(editItem);
        editMenu.add(clearItem);
        editMenu.add(scanItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);

        return menuBar;
    }

    /*
     * I do not automatically rescan the DB to collect changed MetaInformstion
     * because on big DB's it migth take long to get back and this is only needed
     * for the references tree. So I leave it to the users decision...
     */
    public void rescanDB() {
        splash = new CreateTable2Splash();
        dbi = new DbInfo();
        doCellEditors();
        splash.close();
    }

    public void close() {
      
    }
    
  

}
