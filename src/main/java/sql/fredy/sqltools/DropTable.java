package sql.fredy.sqltools;

/**
 *
 * DropTable is the friendly person deleting tables inside Admin, so it is a
 * part of Fredy's Admin
 *
 * This software is part of the Admin-Framework  *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
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
 */
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.Tables;
import sql.fredy.ui.LoadImage;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.border.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import sql.fredy.connection.DataSource;

public class DropTable extends JPanel {

    private static String fredysVersion = "Version 1.4.3  16. Oct 2014";
    private Logger logger = Logger.getLogger("sql.fredy.sqltools");

    private JCheckBox askSillyQuestions;

    LoadImage loadImage = new LoadImage();

    public String getVersion() {
        return fredysVersion;
    }

    private JPanel mainPanel;
    private Tables tableList;

    public JButton cancel;

    /**
     * to find out, when the user wants to close this application, set a
     * listener onto (JButton)AutoForm.cancel
     *
     */
    private String user;

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

    private String database;

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

    private String dbTable;

    /**
     * Get the value of dbTable.
     *
     * @return Value of dbTable.
     */
    public String getDbTable() {
        dbTable = tableList.getSelectedValue().toString();
        System.out.println("DropTable: Table to Drop is: " + dbTable);
        return dbTable;
    }

    public int[] getSelectedTable() {
        return tableList.getSelectedIndices();
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

    public DropTable(String schema) {
        setSchema(schema);
        init();
    }

    private void init() {

        this.setLayout(new BorderLayout());
        this.add(BorderLayout.NORTH, patternPanel());
        this.add(BorderLayout.CENTER, tablePanel());
        this.add(BorderLayout.SOUTH, buttonPanel());

    }

    private JTextField pattern;

    private JPanel patternPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        pattern = new JTextField(25);
        panel.add(new JLabel("Pattern: "));
        pattern.setText("%");
        panel.add(pattern);
        pattern.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                tableList.setPattern(pattern.getText());
                tableList.updateUI();
            }
        });

        return panel;
    }

    private JPanel tablePanel() {

        tableList = new Tables( getSchema());
        JScrollPane scrollpane = new JScrollPane(tableList);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(BorderLayout.CENTER, scrollpane);
        return mainPanel;
    }

    private JPanel buttonPanel() {

        JPanel panel = new JPanel();
        ImageButton delete = new ImageButton(null, "delete.gif", "Delete this table");
        delete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!tableList.isSelectionEmpty()) {
                    ArrayList toDelete = new ArrayList();
                    int[] j = getSelectedTable();
                    for (int i = 0; i < j.length; i++) {
                        toDelete.add((String) tableList.getModel().getElementAt(j[i]));
                    }

                    /*
                    If more then 5 tables are selected for deletion, get a confirmation from the user
                    I think, if one is deleting 5 tables, she knows, what she is doing... (hopefully)
                     */
                    boolean cont = true;
                    if (toDelete.size() > 5) {
                        String string1 = "Yes";
                        String string2 = "No";
                        Object[] options = {string1, string2};
                        int n = JOptionPane.showOptionDialog(null,
                                "Do you really want to start the deletion of " + toDelete.size() + " tables?",
                                "Drop tables",
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
                        for (int i = 0; i < toDelete.size(); i++) {
                            if (!dropTable((String) toDelete.get(i))) {
                                break;
                            }
                        }
                    }
                    tableList.clearSelection();
                    tableList.updateUI();
                    askSillyQuestions.setSelected(true);
                }
            }
        });

        cancel = new JButton(loadImage.getImage("exit.gif"));
        cancel.setToolTipText("Cancel operation and close GUI");

        askSillyQuestions = new JCheckBox("Verify each deletion");
        askSillyQuestions.setSelected(true);

        panel.add(askSillyQuestions);
        panel.add(delete);
        panel.add(cancel);
        panel.setBorder(new EtchedBorder());
        return panel;

    }

    private boolean dropTable(String t) {
        boolean cont = true;

        if (askSillyQuestions.isSelected()) {

            String string1 = "Yes";
            String string2 = "No";
            String string3 = "Cancel";
            Object[] options = {string1, string2, string3};
            int n = JOptionPane.showOptionDialog(null,
                    "Do you really want to delete " + t + "?",
                    "Delete Table ",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, //don't use a custom Icon
                    options, //the titles of buttons
                    string2); //the title of the default button
            if (n == JOptionPane.YES_OPTION) {
                cont = dropThisTable(t);
            }
            if (n == JOptionPane.CANCEL_OPTION) {
                cont = false;
            }
        } else {
            cont = dropThisTable(t);
        }
        return cont;
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
    
    private boolean dropThisTable(String t) {
        boolean cont = true;
        Connection con = null;
        Statement stmt = null;
        try {
            con = getConnection();
            stmt = con.createStatement();
            int records = stmt.executeUpdate("drop table " + t);
            logger.log(Level.INFO, "Dropped table " + t);
            tableList.table.remove(t);
            tableList.updateUI();

            //message(Integer.toString(records) + " rows affected");
        } catch (SQLException excpt) {
            //System.out.println("DropTable:\n" + excpt);  
            message(excpt.getMessage().toString());
            cont = false;
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING,"Exception on close Statement [0]", e.getMessage());
                }
            }
            if ( con != null ) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING,"Exception on close Connection [0]", e.getMessage());
                }
            }
        }
        return cont;
    }

    private void message(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Message", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String args[]) {
        
        String schema = "%";

        System.out.println("DropTable\n"
                + "------------\n"
                + "Syntax: java sql.fredy.sqltools.DropTable\n"
                + "        Parameters: -s Schema (default: %)\n");

        int i = 0;
        while (i < args.length) {
             if (args[i].equals("-s")) {
                i++;
                schema = args[i];
            }
            i++;
        };

 
        DropTable dt = new DropTable( schema);
        JFrame frame = new JFrame("Fredy's Drop Table");
        frame.getContentPane().add(dt);
        frame.pack();
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
        dt.cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        frame.setVisible(true);
    }

}
