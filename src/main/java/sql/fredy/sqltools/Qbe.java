package sql.fredy.sqltools;

/**
 * *
 * Qbe: Query by examples is a part of Admin... Version 1.0 24. January 1999
 *
 * Fredy Fischer * this has been created to query mySQL-Databases it only
 * returns a JPanel, so it can easily been used in different kind of windows
 * This software is part of the Admin-Framework * Admin is a Tool around
 * JDBC-enabled SQL-Databases to do basic jobs for DB-Administrations, as create
 * / delete / alter and query tables it also creates indices and generates
 * simple Java-Code to access DBMS-tables and exports data into various formats
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
import sql.fredy.ui.Tables;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import sql.fredy.connection.DataSource;

public class Qbe extends JPanel {

// Fredy's make Version
    private static String fredysVersion = "Version 1.6.1  24. Dec. 2020";

    public String getVersion() {
        return fredysVersion;
    }

    private Tables tableList;

    private JCheckBox[] columnName;
    private String[] columnType;
    private int[] columnLength;
    private JTextField[] conditionValue;
    private JComboBox[] conditionType;
    public String query;
    private JTextArea queryArea;
    private JLabel selTable;

    private JTabbedPane mainPanel;
    public JButton cancel, execQuery;
    private JPanel panel1, panel2, panel3, panel4;
    private JPanel panel2_1, panel3_1;
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

    String dbTable;

    /**
     * Get the value of dbTable.
     *
     * @return Value of dbTable.
     */
    public String getDbTable() {
        try {
            dbTable = tableList.getSelectedValue().toString();
        } catch (Exception e) {
            ListModel lm = tableList.getModel();
            if (lm.getSize() < 1) {
                dbTable = "";
            } else {
                dbTable = (String) lm.getElementAt(0);
            }
        }

        //return spaceFixer(dbTable);
        return dbTable;
    }

    private String spaceFixer(String s) {
        s = s.trim();
        if (s.indexOf(' ') > 0) {
            s = "'" + s + "'";
        }
        return s;
    }

    /**
     * Set the value of dbTable.
     *
     * @param v Value to assign to dbTable.
     */
    public void setDbTable(String v) {
        this.dbTable = v;
    }

    String schema;

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

    public Qbe(String database, String schema) {

        setDatabase(database);
        setSchema(schema);

        doInit();
    }

    public Qbe(String schema) {
        setSchema(schema);
        doInit();
    }

    private void doInit() {

        selTable = new JLabel(" ");
        selTable.setForeground(Color.blue);

        mainPanel = new JTabbedPane();

        mainPanel.add("Table", step1());
        mainPanel.add("Columns", step2());
        mainPanel.add("Conditions", step3());
        mainPanel.add("Query", step4());

        this.setLayout(new BorderLayout());
        this.add("South", buttonPanel());
        this.add("Center", mainPanel);
    }

    /**
     * within this panel the user can select the table he/she would like to
     * query from
     */
    private JPanel step1() {

        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());

        JTextArea info = new JTextArea(10, 20);
        info.setWrapStyleWord(true);
        info.setLineWrap(true);
        info.setEditable(false);
        info.setBackground(Color.lightGray);
        info.setText("Please select the table you would like to "
                + "see something out of...");

        JScrollPane infoPane = new JScrollPane();
        infoPane.getViewport().add(info);
        JPanel panel1_1 = new JPanel();
        panel1_1.setLayout(new BorderLayout());
        panel1_1.add("Center", infoPane);

        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        panel1_1.add("South", progressBar);

        panel1.add("Center", panel1_1);
        panel1.add("West", selectTable());
        return panel1;

    }

    /**
     * here the user selects the columns to display within the answer
     *
     */
    private JPanel step2() {

        panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());

        try {
            GridBagConstraints gbc;
            Insets insets = new Insets(5, 5, 5, 5);
            gbc = new GridBagConstraints();

            JTextArea info = new JTextArea(10, 20);
            info.setWrapStyleWord(true);
            info.setLineWrap(true);
            info.setEditable(false);
            info.setBackground(Color.lightGray);
            info.setText("These are the columns available in table "
                    + getDbTable()
                    + ". Please click all the ones you want to display");

            JScrollPane infoPane = new JScrollPane();
            infoPane.getViewport().add(info);

            JPanel infPanel = new JPanel();
            infPanel.setLayout(new FlowLayout());
            infPanel.add(infoPane);

            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = insets;
            gbc.gridx = 1;
            gbc.gridy = 0;
            panel2.add(infPanel, gbc);

            panel2_1 = new JPanel();
            panel2_1.setLayout(new BorderLayout());
            panel2_1.add("Center", selectColumns());

            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = insets;
            gbc.gridx = 0;
            gbc.gridy = 0;
            panel2.add(panel2_1, gbc);
        } catch (Exception e) {
            // throws an exception if there isn't any table at all...
            // or I can not read properly metadata
        }

        return panel2;
    }

    /**
     * here the user selects the columns to set conditions
     *
     */
    private JPanel step3() {

        panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());

        try {
            panel3_1 = new JPanel();
            panel3_1.setLayout(new BorderLayout());

            JPanel panel3_1_1 = new JPanel();
            panel3_1_1.setLayout(new GridLayout(columnName.length, 3));

            JLabel[] labels = new JLabel[columnName.length];
            for (int i = 0; i < columnName.length; i++) {
                labels[i] = new JLabel(columnName[i].getText());
            }

            String[] items = {"equal to",
                "not equal to",
                "smaller than or equal",
                "smaller than",
                "bigger than or equal",
                "bigger than",
                "starts with",
                "ends with",
                "contains"};
            for (int i = 0; i < conditionType.length; i++) {
                conditionType[i] = new JComboBox(items);
                conditionValue[i] = new JTextField();
                conditionValue[i].setText(null);
            }

            for (int i = 0; i < conditionType.length; i++) {
                panel3_1_1.add(labels[i]);
                panel3_1_1.add(conditionType[i]);
                panel3_1_1.add(conditionValue[i]);
            }

            JScrollPane scrollpane = new JScrollPane();
            scrollpane.getViewport().add(panel3_1_1);

            JPanel panel3_2 = new JPanel();
            panel3_2.setLayout(new FlowLayout());
            panel3_2.add(scrollpane);

            panel3_1.add("Center", panel3_2);
            panel3.add("West", panel3_1);
        } catch (Exception e) {
            // throws an exception if there isn't any table at all...
            // or I can not read properly metadata
        }

        return panel3;
    }

    private JPanel step4() {

        panel4 = new JPanel();
        panel4.setLayout(new BorderLayout());

        queryArea = new JTextArea(12, 50);
        queryArea.setWrapStyleWord(true);
        queryArea.setLineWrap(true);
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(queryArea);

        JPanel panel4_2 = new JPanel();
        panel4_2.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel4_2.add(scrollpane, gbc);

        JPanel panel4_1 = new JPanel();
        panel4_1.setBorder(BorderFactory.createEtchedBorder());

        JButton genQuery = new JButton("generate");
        genQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateQuery();
            }
        });

        panel4_1.add(genQuery);

        panel4.add("Center", panel4_2);
        panel4.add("South", panel4_1);

        return panel4;

    }

    public String getQuery() {
        return queryArea.getText();
    }

    private void generateQuery() {

        query = "Select ";

        // first fill in the fields to display
        for (int i = 0; i < columnName.length; i++) {
            if (columnName[i].isSelected()) {
                if (query.length() > 7) {
                    query = query + ",\n\t";
                }
                query = query + spaceFixer(getDbTable()) + "." + spaceFixer(columnName[i].getText());
            }
        }

        query = query + "\nfrom " + spaceFixer(getDbTable()) + "\n";
        if (checkConditions()) {
            query = query + "\nwhere\n\t " + generateConditions();
        }
        queryArea.setText(query);
        queryArea.updateUI();
        execQuery.setEnabled(true);
    }

    private boolean checkConditions() {
        boolean v = false;
        for (int i = 0; i < conditionValue.length; i++) {
            if (conditionValue[i].getText().length() > 0) {
                v = true;
            }
        }
        return v;
    }

    private String generateConditions() {
        String s = "";
        String cn = "";
        for (int i = 0; i < conditionValue.length; i++) {
            if (conditionValue[i].getText().length() > 0) {

                cn = spaceFixer(columnName[i].getText());

                if (s.length() > 0) {
                    s = s + "\nand\n\t ";
                }
                if (conditionType[i].getSelectedItem().toString().startsWith("equal to")) {
                    s = s + cn + " = "
                            + fieldType(i) + conditionValue[i].getText() + fieldType(i);
                }

                if (conditionType[i].getSelectedItem().toString().startsWith("not equal to")) {
                    s = s + cn + " != "
                            + fieldType(i) + conditionValue[i].getText() + fieldType(i);
                }

                if (conditionType[i].getSelectedItem().toString().startsWith("smaller than or equal")) {
                    s = s + cn + " <= "
                            + fieldType(i) + conditionValue[i].getText() + fieldType(i);
                }

                if (conditionType[i].getSelectedItem().toString().endsWith("smaller than")) {
                    s = s + cn + " < "
                            + fieldType(i) + conditionValue[i].getText() + fieldType(i);
                }

                if (conditionType[i].getSelectedItem().toString().startsWith("bigger than or equal")) {
                    s = s + cn + " >= "
                            + fieldType(i) + conditionValue[i].getText() + fieldType(i);
                }

                if (conditionType[i].getSelectedItem().toString().endsWith("bigger than")) {
                    s = s + cn + " > "
                            + fieldType(i) + conditionValue[i].getText() + fieldType(i);
                }

                if (conditionType[i].getSelectedItem().toString().startsWith("starts with")) {
                    s = s + cn + " like "
                            + fieldType(i) + conditionValue[i].getText() + "%" + fieldType(i);
                }

                if (conditionType[i].getSelectedItem().toString().startsWith("ends with")) {
                    s = s + cn + " like "
                            + fieldType(i) + "%" + conditionValue[i].getText() + fieldType(i);
                }

                if (conditionType[i].getSelectedItem().toString().startsWith("contains")) {
                    s = s + cn + " like "
                            + fieldType(i) + "%" + conditionValue[i].getText() + "%" + fieldType(i);
                }
            }
        }
        return s;
    }

    private String fieldType(int i) {

        String ft = " ";
        if (Integer.parseInt(columnType[i]) == java.sql.Types.CHAR) {
            ft = "'";
        }
        if (Integer.parseInt(columnType[i]) == java.sql.Types.VARCHAR) {
            ft = "'";
        }
        if (Integer.parseInt(columnType[i]) == java.sql.Types.LONGVARCHAR) {
            ft = "'";
        }
        if (Integer.parseInt(columnType[i]) == java.sql.Types.BINARY) {
            ft = "'";
        }
        if (Integer.parseInt(columnType[i]) == java.sql.Types.LONGVARBINARY) {
            ft = "'";
        }
        if (Integer.parseInt(columnType[i]) == java.sql.Types.VARBINARY) {
            ft = "'";
        }
        if (Integer.parseInt(columnType[i]) == java.sql.Types.DATE) {
            ft = "'";
        }
        if (Integer.parseInt(columnType[i]) == java.sql.Types.TIME) {
            ft = "'";
        }
        if (Integer.parseInt(columnType[i]) == java.sql.Types.TIMESTAMP) {
            ft = "'";
        }
        if (Integer.parseInt(columnType[i]) == java.sql.Types.OTHER) {
            ft = "'";
        }
        return ft;

    }

    /**
     * this is the List that contains the available tables in the Database
     */
    private JScrollPane selectTable() {
        String[] table;

        tableList = new Tables(getSchema());
        tableList.setSelectedIndex(0);

        // set the Listener onto the table-List
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    doUpdate();
                }
            }
        };
        tableList.addMouseListener(mouseListener);

        JScrollPane listPane = new JScrollPane(tableList);

        selTable.setText("Table is: " + getDbTable());
        return listPane;
    }

    /**
     * this is to update the whole stuff if the table changed
     *
     */
    private void doUpdate() {

        progressBar.setValue(1);
        progressBar.updateUI();
        Cursor W_cursor = new Cursor(Cursor.WAIT_CURSOR);
        Cursor D_cursor = new Cursor(Cursor.DEFAULT_CURSOR);
        super.setCursor(W_cursor);

        // Columns tab
        progressBar.setValue(5);
        mainPanel.remove(panel2);
        mainPanel.add("Columns", step2());
        progressBar.setValue(30);

        mainPanel.remove(panel3);
        progressBar.setValue(35);

        mainPanel.add("Conditions", step3());
        progressBar.setValue(60);

        mainPanel.remove(panel4);
        progressBar.setValue(70);

        mainPanel.add("Query", step4());
        progressBar.setValue(98);

        selTable.setText("Table is: " + getDbTable());
        progressBar.setValue(100);

        mainPanel.updateUI();

        super.setCursor(D_cursor);

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
            message(ex.getMessage().toString());
        } catch (SQLException ex) {
            message(ex.getMessage().toString());
        } catch (PropertyVetoException ex) {
            message(ex.getMessage().toString());
        } finally {
            return con;
        }

    }

    /**
     * this is to display all available columns of the selected table there are
     * shadow arrays that contains additional infos: - columnType = the SQL-Type
     * of the field - columnLength = the length of this field
     */
    private JScrollPane selectColumns() {
        String[] columns;
        Box box1 = Box.createVerticalBox();
        Connection con = null;
        ResultSet cc = null;
        ResultSet cols = null;

        int i = 0;

        try {

            con = getConnection();

            // this is to count the columns to create the array...
            DatabaseMetaData md = con.getMetaData();
            cc = md.getColumns(null, null, getDbTable(), "%");
            while (cc.next()) {
                i++;
            }

            cols = md.getColumns(null, null, getDbTable(), "%");
            columns = new String[i];
            columnType = new String[i];
            columnLength = new int[i];
            conditionValue = new JTextField[i];
            conditionType = new JComboBox[i];

            i = 0;
            columnName = new JCheckBox[columns.length];
            while (cols.next()) {
                columns[i] = cols.getString(4);
                columnType[i] = cols.getString(5);
                columnLength[i] = cols.getInt(7);
                columnName[i] = new JCheckBox(columns[i]);
                columnName[i].setSelected(true);
                box1.add(columnName[i]);
                i++;
            }
        } catch (SQLException exception) {
            message(exception.getMessage().toString());
        } finally {
            if (cc != null) {
                try {
                    cc.close();
                } catch (SQLException e) {
                    
                }
            }
             if (cols != null) {
                try {
                    cols.close();
                } catch (SQLException e) {
                    
                }
            }
              if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    
                }
            }
        }
      
        JScrollPane scrollpane = new JScrollPane();
        scrollpane.getViewport().add(box1);

        return scrollpane;
    }

    private void message(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.WARNING_MESSAGE);
    }

    private JPanel buttonPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        execQuery = new JButton("Execute query");
        execQuery.setToolTipText("As soon as you generated the query, it can be executed");
        execQuery.setEnabled(false);

        cancel = new JButton("Cancel");
        cancel.setToolTipText("immediately leave this...");

        panel.add(selTable);
        panel.add(execQuery);
        panel.add(cancel);
        panel.setBorder(BorderFactory.createEtchedBorder());
        return panel;

    }

    public static void main(String args[]) {
        if (args.length != 5) {
            System.out.println("Syntax: java sql.fredy.sqltools.Qbe db schema");
            System.exit(0);
        }

        final Qbe qbe = new Qbe( args[1]);
        final JFrame frame = new JFrame("QBE");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(qbe, BorderLayout.CENTER);

        qbe.cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        qbe.execQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sqlTable sqt = new sqlTable(qbe.getQuery());
                JFrame sqlframe = new JFrame("Result");
                sqlframe.getContentPane().setLayout(new BorderLayout());
                sqlframe.getContentPane().add(sqt.scrollpane, BorderLayout.CENTER);
                sqlframe.pack();
                sqlframe.setVisible(true);
            }
        });

        frame.addWindowListener(new WindowAdapter() {
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
        frame.pack();
        frame.setVisible(true);

    }

}
