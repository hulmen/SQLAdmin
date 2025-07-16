package sql.fredy.sqltools;

/**
 * Import imports CSV-Files into a Table Import is part of my Admin Framework
 * and free software (MIT-License)
 *
 * This software is part of the Admin-Framework  *
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
 *
 *
 */
import sql.fredy.ui.CloseableFrame;
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.FfAlignerLayoutPanel;
import sql.fredy.ui.Tables;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.util.logging.*;
import sql.fredy.connection.DataSource;

public class Import extends JPanel {

// Fredy's make Version
    private static String fredysVersion = "Version 1.7  21. July. 2015 ";
    private Logger logger = Logger.getLogger("sql.fredy.sqltools");

    public String getVersion() {
        return fredysVersion;
    }
    private JTextField fieldSeparator;
    private JTextField textFieldSeparator;
    private JCheckBox useHeader;
    private JLabel fileName;
    private File file = null;
    private Tables table;
    private String sqlHeader;
    public ImageButton cancel;
    private JProgressBar progressBar;

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
    String schema;

    /**
     * Get the value of schema.
     *
     * @return value of schema.
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

    public void close() {

    }

    private JPanel editPanel() {
        FfAlignerLayoutPanel panel = new FfAlignerLayoutPanel();

        fieldSeparator = new JTextField(";", 2);
        textFieldSeparator = new JTextField("\"", 2);

        panel.addComponent(new JLabel("Fieldseparator"));
        panel.addComponent(fieldSeparator);

        panel.addComponent(new JLabel("Textfields enclosed by: "));
        panel.addComponent(textFieldSeparator);

        panel.setBorder(BorderFactory.createEtchedBorder());

        return panel;
    }

    private JPanel buttonPanel() {

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());

        useHeader = new JCheckBox("use header");
        useHeader.setSelected(true);
        useHeader.setToolTipText("use first line as header=attribut names");

        ImageButton selectFile = new ImageButton(null, "open.gif", null);
        selectFile.setToolTipText("open file for import");
        selectFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                readFile();
            }
        });

        ImageButton importFile = new ImageButton(null, "documentin.gif", null);
        importFile.setToolTipText("import");
        importFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doImport();
            }
        });

        cancel = new ImageButton(null, "exit.gif", null);
        cancel.setToolTipText("Exit");

        panel.add(useHeader);
        panel.add(selectFile);
        panel.add(importFile);
        panel.add(cancel);

        return panel;
    }

    public String getTable() {
        return table.getSelectedValue().toString();
    }

    private JPanel tablePanel() {

        table = new Tables(getSchema());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.setBorder(BorderFactory.createEtchedBorder());

        fileName = new JLabel("Import");
        panel.add("North", fileName);

        JScrollPane scp = new JScrollPane(table);
        panel.add("Center", scp);

        progressBar = new JProgressBar();
        panel.add("South", progressBar);

        return panel;

    }

    private void readFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Select Importfile");

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            fileName.setText(file.getPath() + file.separator + file.getName());

        } else {
            file = null;
        }

    }

    private void doImport() {
        if (file != null) {
            String s;
            boolean first = true;

            if (useHeader.isSelected()) {
                first = true;
            } else {
                setSimpleSqlHeader();
                first = false;
            }

            // initialize ProgressBar
            progressBar.setMinimum(0);
            progressBar.setMaximum(getNumberOfLines(file));
            progressBar.setValue(0);
            progressBar.updateUI();

            // open the file and read it in
            try {
                DataInputStream ipstr = new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(file)));

                BufferedReader bufrd = new BufferedReader(
                        new InputStreamReader(ipstr));
                while ((s = bufrd.readLine()) != null) {

                    if (first) {
                        setExtendedSqlHeader(s);
                        first = false;
                        updatePgb();
                    }
                    insert(s);
                }
                ipstr.close();
            } catch (IOException exep) {
                fileName.setText(getClass().getName() + " IO Error");
            }

        }

    }

    private void insert(String s) {

        String query = "";
        String fields[] = s.split(fieldSeparator.getText().trim());
        StringTokenizer st = new StringTokenizer(s, fieldSeparator.getText().trim());
        Connection con = null;
        Statement stmt = null;

        char x = '\'';

        boolean first = true;

        //while (st.hasMoreTokens()) {
        for (int fieldItr = 0; fieldItr < fields.length; fieldItr++) {
            try {

                if (first) {
                    first = false;
                } else {
                    query = query + ",";
                }
                //query = query + st.nextToken().replace(textFieldSeparator.getText().charAt(0),x);
                if (textFieldSeparator.getText().trim().length() > 0) {
                    query = query + fields[fieldItr].replace(textFieldSeparator.getText().charAt(0), x);
                } else {
                    String singleField = fields[fieldItr];
                    if (singleField.length() < 1) {
                        singleField = "";
                    }
                    //query = query + "'" + fields[fieldItr] + "'";
                    singleField.replaceAll("'", " ");
                    query = query + "'" + singleField + "'";
                }
            } catch (Exception excp) {
                logger.log(Level.WARNING, "Exception while creating query. " + excp.getMessage());
            }

        }
        if (s.endsWith(fieldSeparator.getText())) {
            query = query + ",''";
        }
        query = sqlHeader + query + ")";
        try {
            con = getConnection();
            stmt = con.createStatement();
            int records = stmt.executeUpdate(query);
            updatePgb();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Exception on insert query " + query + "\nException is:" + e.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception whil closing statement {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception whil closing statement {0}", e.getMessage());
                }
            }
        }

    }

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

    private void updatePgb() {

        progressBar.setValue(progressBar.getValue() + 1);
        progressBar.updateUI();
    }

    private int getNumberOfLines(File f) {
        int nl = 0;
        String s;
        try {
            DataInputStream ipstr = new DataInputStream(
                    new BufferedInputStream(
                            new FileInputStream(f)));

            BufferedReader bufrd = new BufferedReader(
                    new InputStreamReader(ipstr));
            while ((s = bufrd.readLine()) != null) {
                nl++;

            }
            ipstr.close();
        } catch (IOException exep) {
            ;
        }
        return nl;

    }

    private void setSimpleSqlHeader() {
        sqlHeader = "insert into " + getTable() + " values (";
    }

    private void setExtendedSqlHeader(String s) {

        //StringTokenizer st = new StringTokenizer(s, fieldSeparator.getText());
        String fields[] = s.split(fieldSeparator.getText().trim());
        sqlHeader = "insert into " + getTable() + " ( ";
        boolean first = true;

        for (int fItr = 0; fItr < fields.length; fItr++) {
            try {
                if (first) {
                    first = false;
                } else {
                    sqlHeader = sqlHeader + ",";
                }
                //sqlHeader = sqlHeader + st.nextToken().replace(textFieldSeparator.getText().charAt(0), ' ');
                if (textFieldSeparator.getText().trim().length() > 0) {
                    sqlHeader = sqlHeader + fields[fItr].replace(textFieldSeparator.getText().charAt(0), ' ');
                } else {
                    sqlHeader = sqlHeader + fields[fItr];
                }

            } catch (Exception excp) {
                logger.log(Level.INFO, "Exception while creating header. " + excp.getMessage());
            }

        }
        sqlHeader = sqlHeader + ") values (";

    }

    public Import(String database,String schema) {

        setDatabase(database);
        setSchema(schema);
        
        init();
    }

    public Import(String schema) {
        setSchema(schema);
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());        

        this.add("West", editPanel());
        this.add("Center", tablePanel());
        this.add("South", buttonPanel());

    }

    public static void main(String args[]) {

        String host = "localhost";
        String user = System.getProperty("user.name");
        String schema = "%";
        String database = null;
        String password = null;

        System.out.println("CreateIndex\n"
                + "-----------\n"
                + "Syntax: java sql.fredy.sqltools.Import\n"
                + "        Parameters: -d database\n"
                + "                    -s Schema (default: %)\n");

        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-d")) {
                i++;
                database = args[i];
            }

            if (args[i].equals("-s")) {
                i++;
                schema = args[i];
            }
            i++;
        }
        ;

        if (database == null) {
            System.out.println("No database provided. Program stopped!");
            System.exit(0);
        }
        

        CloseableFrame cf = new CloseableFrame("Fredy's Data Import");
        Import imp = new Import(schema);
        cf.getContentPane().setLayout(new BorderLayout());
        cf.getContentPane().add(BorderLayout.CENTER, imp);
        imp.cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        cf.pack();
        cf.setVisible(true);

    }

 }
