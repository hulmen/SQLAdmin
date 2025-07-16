package applications.basics;

/**
 * SQL-Monitor is a part of Admin ...
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, like: - create/ drop tables - create indices - perform
 * sql-statements - simple form - a guided query and a other usefull things in
 * DB-arena
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
 * This Tool has been developed by David Good as contribution to Fredy's
 * Admintool
 *
 *
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.beans.*;

public class SqlMonitor extends JPanel {

    private JTextArea query, response;
    public JTabbedPane tabbedPane;
    public JPanel upperPanel;
    public JScrollPane scp;

    public Toolkit tk;
    public JButton exit;
    public JTable tableView;
    public JScrollPane scrollpane;

    String host;
    boolean retainTabs = false;
    boolean retainStatus = false;
    String sqlSeparators = ";\r";
    final static int TABLE = 0;
    final static int SINGLE_TEXT = 1;
    final static int MULTIPLE_TEXT = 2;
    int resultType = TABLE;
    Container parentFrame;
    static boolean standAlone = false;

    private int tabIndex = 0;

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

    private JToolBar toolbar() {

        JToolBar tb = new JToolBar();
        tb.setOrientation(JToolBar.HORIZONTAL);

        JButton exec = new JButton(loadImage("binocular.gif"));
        exec.setToolTipText("Execute query");
        exec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                execQuery();
            }
        });
        tb.add(exec);

        JButton clear = new JButton(loadImage("sheet.gif"));
        clear.setToolTipText("Clear");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                query.setText("");
            }
        });
        tb.add(clear);

        JButton copy = new JButton(loadImage("copy.gif"));
        copy.setToolTipText("Copy all to clipboard");
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = query.getText();
                StringSelection ss = new StringSelection(s);
                tk.getSystemClipboard().setContents(ss, ss);
            }
        });
        tb.add(copy);

        JButton paste = new JButton(loadImage("paste.gif"));
        paste.setToolTipText("Paste");
        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Clipboard c = tk.getSystemClipboard();
                Transferable t = c.getContents(this);
                try {
                    String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                    query.setText(s);
                } catch (Exception eexc) {
                    tk.beep();
                }
            }
        });
        tb.add(paste);

        JButton export = new JButton(loadImage("export.gif"));
        export.setToolTipText("Export result");
        export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = tabbedPane.getSelectedIndex();
                if (tabbedPane.getTitleAt(index).startsWith("Result")) {
                    ExportQuery eq = new ExportQuery(getHost(), getUser(), getPassword(), getDatabase());
                    eq.setQuery(((SqlPanel) (tabbedPane.getComponentAt(index))).getQuery());
                    eq.selectFile();
                    eq.export();
                }
            }
        });
        tb.add(export);

        ImageButton xls = new ImageButton(null, "xls.gif", "Export query into xls-file (needs POI)");
        xls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = tabbedPane.getSelectedIndex();
                if (tabbedPane.getTitleAt(index).startsWith("Result")) {
                    JFileChooser chooser = new JFileChooser();
                    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                    chooser.setDialogTitle("Select XLS-File");
                    chooser.setFileFilter(new MyFileFilter(new String[]{"xls", "XLS"}));

                    int returnVal = chooser.showSaveDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        t_connect tc = new t_connect(getHost(),
                                getUser(),
                                getPassword(),
                                getDatabase());

                        XLSExport xe = new XLSExport(tc.con);
                        xe.setQuery(((SqlPanel) (tabbedPane.getComponentAt(index))).getQuery());
                        xe.createXLS(chooser.getCurrentDirectory()
                                + java.io.File.separator
                                + chooser.getSelectedFile().getName());
                        tc.close();
                    }
                }
            }
        });
        tb.add(xls);

        JButton preferences = new JButton(loadImage("user.gif"));
        preferences.setToolTipText("Preferences");
        preferences.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                SqlPrefForm sp = new SqlPrefForm();

            }
        });
        tb.add(preferences);

        JButton pickList = new JButton(loadImage("data.gif"));
        pickList.setToolTipText("Pick List");
        pickList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                parentFrame = getParentFrame();
                final Container tla = SqlMonitor.this.getTopLevelAncestor();
                Container PickListFrame;

                ActionListener a = new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        if (!standAlone) {
                            try {
                                ((JInternalFrame) parentFrame).setSelected(true);
                            } catch (PropertyVetoException ex) {
                                ex.printStackTrace();
                            }
                        }
                        parentFrame.requestFocus();
                        query.requestFocus();
                        query.append(ae.getActionCommand());

                    }

                };

                SqlPickList s = new SqlPickList(getHost(), getUser(), getPassword(), getDatabase(), a, getSchema());

                //Open Pick List as Dialog if standalone, another internal frame if within admin
                if (standAlone) {
                    PickListFrame = new JDialog((Frame) (parentFrame), "Pick List");
                    ((RootPaneContainer) PickListFrame).getContentPane().add(s);
                    ((JDialog) PickListFrame).pack();

                } else {

                    PickListFrame = new JInternalFrame("Pick List", true, true);

                    ((RootPaneContainer) PickListFrame).getContentPane().add(s);
                    ((JInternalFrame) PickListFrame).pack();
                    //((sql.fredy.admin.Admin)tla).lc.add(PickListFrame,-1);

                }
                Point p = new Point(1, 1);
                if (parentFrame.isVisible()) {
                    try {
                        p = parentFrame.getLocationOnScreen();
                        p.translate(parentFrame.getWidth(), 0);
                    } catch (Exception e2) {

                    }
                }

                PickListFrame.setLocation(p);

                PickListFrame.setVisible(true);
            }
        });

        tb.add(pickList);

        exit = new JButton(loadImage("exit.gif"));
        exit.setToolTipText("Quit");
        tb.add(exit);

        return tb;
    }

    private ImageIcon loadImage(String image) {
        String admin_img = System.getProperty("admin.image");
        ImageIcon img = null;
        if (admin_img == null) {
            img = new ImageIcon(SqlMonitor.class.getResource("images" + File.separator + image));
        } else {
            img = new ImageIcon(admin_img + File.separator + image);
        }

        return img;
    }

    private JScrollPane queryPanel() {
        //JPanel panel = new JPanel();
        query = new JTextArea(20, 80);
        query.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(query);
        //panel.add(scrollPane);
        return scrollPane;

    }

    private JScrollPane displayPanel() {

        //JPanel panel = new JPanel();
        JScrollPane scrollpane = new JScrollPane(response);
        //panel.add(scrollpane);
        return scrollpane;

    }

    Container getParentFrame() {
        Container c = new Container();

        if (standAlone) {
            c = getTopLevelAncestor();
        } else {
            try {
                c = SwingUtilities.getAncestorOfClass(Class.forName("javax.swing.JInternalFrame"), this); //get Internal frame when opened in Admin
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return c;
    }

    public SqlMonitor(String host, String user, String password, String database, String schema) {

        setHost(host);
        setUser(user);
        setPassword(password);
        setDatabase(database);
        setSchema(schema);
        tk = this.getToolkit();

        this.setLayout(new GridBagLayout());
        tabbedPane = new JTabbedPane();
        response = new JTextArea(20, 80);
        response.setEditable(false);
        response.setFont(new Font("Monospaced", Font.PLAIN, 12));

        upperPanel = new JPanel();
        upperPanel.add(queryPanel());

        GridBagConstraints gbc;
        Insets insets = new Insets(5, 5, 5, 5);
        gbc = new GridBagConstraints();
        gbc.insets = insets;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        this.add(toolbar(), gbc);

        this.add(tabbedPane, gbc);
        tabbedPane.add("SQL", upperPanel);

        //this.setRequestFocusEnabled(true);
        //this.requestFocus();
        //query.requestFocus();
        this.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                //fitSize();
                //tabbedPane.updateUI();

            }

        });

    }

    public void update() {
        this.updateUI();
    }

    public void execQuery() {

        /**
         * if it is a query send it to the table, otherwise execute the command
         * and display the result of the command in the status tab
         */
        String queryText = "";
        String fullQueryText;

        JPanel lastPanel = new JPanel();
        JPanel statusPanel = new JPanel();
        boolean bError = false;

        //get query	
        if (query.getSelectionStart() == query.getSelectionEnd()) {
            fullQueryText = query.getText();
        } else {
            fullQueryText = query.getSelectedText();
        }

        if (fullQueryText == "" | fullQueryText == null) {
            return;
        }

        //initialise based on Preferences
        int count = tabbedPane.getTabCount();

        if (!retainTabs) {
            tabIndex = 0;
            for (int i = (count - 1); i > 0; i--) {
                tabbedPane.remove(i);
            }
        } else {
            int stat = tabbedPane.indexOfTab("Status");
            if (stat > -1) {
                tabbedPane.remove(stat);
            }
        }

        if (!retainStatus) {
            response.setText("");
        }

        //parse query strings & iterate throught them
        StringTokenizer fqt = new StringTokenizer(fullQueryText, sqlSeparators);

        while (fqt.hasMoreTokens()) {
            queryText = fqt.nextToken().trim();
            response.append(queryText + "\n");

            SqlTab tabPanel = new SqlTextArea();

            if (queryText.toLowerCase().startsWith("select") | queryText.toLowerCase().startsWith("show")) {

                if (resultType != TABLE) {
                    tabPanel = new SqlTextArea(getHost(), getUser(), getPassword(), getDatabase(), queryText);
                } else {
                    tabPanel = new SqlPanel(getHost(), getUser(), getPassword(), getDatabase(), queryText);
                }

                if (tabPanel.getSQLError() != null) {
                    response.append(tabPanel.getSQLError() + "\n\n");
                    tk.beep();
                    bError = true;
                } else {
                    tabIndex++;
                    tabbedPane.addTab("Results " + tabIndex, null, (JPanel) tabPanel, queryText);
                    fitSize(tabbedPane.getSize(), tabPanel);
                    response.append(String.format("%,d",tabPanel.getNumRows()) + " rows returned in Results " + tabIndex + "\n\n");
                    lastPanel = (JPanel) tabPanel;
                }
            } else {
                t_connect con = new t_connect(getHost(), getUser(), getPassword(), getDatabase());
                if (con.getError() != null) {
                    tk.beep();
                } else {
                    try {
                        int records = con.stmt.executeUpdate(queryText);
                        response.append(String.format("%,d",records) + " rows affected\n\n");
                        con.close();
                        lastPanel = statusPanel;
                    } catch (Exception excpt) {
                        response.append(excpt.toString());
                        tk.beep();
                        bError = true;
                    }
                }

            }
        }

        statusPanel.add(displayPanel());
        tabbedPane.add("Status", statusPanel);

        tabbedPane.repaint();

        if (bError) {
            lastPanel = statusPanel;
        }

        tabbedPane.setSelectedComponent(lastPanel);
    }

    public void fitSize(Dimension d, SqlTab st) {

        Integer intx = d.width;
        Integer inty = d.height;
        double fx = intx.floatValue() * 0.95;
        double fy = inty.floatValue() * 0.80;
        Double dfx = fx;
        Double dfy = fy;
        d.setSize(dfx.intValue(), dfy.intValue());

        st.setViewPortSize(d);

    }

    public static void main(String args[]) {

        System.out.println("Fredy's SQL-Monitor\n"
                + "-------------------- Version 2\n"
                + "is a part of Admin and free Software\n"
                + "Developed and contributed by David Good");

        standAlone = true;
        if (args.length != 5) {
            System.out.println("Syntax: java SqlMonitor host user password database schema\nWhere schema is avalid schema-pattern (incl. wildcards)");
            System.exit(0);
        }
        JFrame f = new JFrame("SQL-Monitor");
        f.getContentPane().setLayout(new FlowLayout());
        final SqlMonitor g = new SqlMonitor(args[0], args[1], args[2], args[3], args[4]);
        f.getContentPane().add(g);
        f.pack();
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
        g.exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        f.addComponentListener(new ComponentAdapter() {
            public void componentHidden(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {

                g.tabbedPane.updateUI();

            }
        });

        f.setVisible(true);
    }

    public class SqlPrefForm extends JDialog {

        Container jPanel1 = getContentPane();
        GridBagLayout gridBagLayout1 = new GridBagLayout();
        JCheckBox cbRetainTabs = new JCheckBox();
        JCheckBox cbRetainStatus = new JCheckBox();
        JTextField txtSeparator = new JTextField(1);
        JLabel jLabel1 = new JLabel();
        JButton cbOK = new JButton();
        JButton cbCancel = new JButton();
        JRadioButton rbText = new JRadioButton("Text");
        JRadioButton rbTable = new JRadioButton("Table");
        JPanel buttonPanel = new JPanel();

        public SqlPrefForm() {

            try {
                jbInit();
                setTitle("SQL Monitor Preferences");
                setLocationRelativeTo(SqlMonitor.this);
                setModal(true);
                pack();
                show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void jbInit() throws Exception {
            jPanel1.setLayout(gridBagLayout1);

            cbRetainTabs.setText("Retain Results");
            cbRetainTabs.setSelected(retainTabs);

            cbRetainStatus.setText("Retain Status");
            cbRetainStatus.setSelected(retainStatus);

            txtSeparator.setText(sqlSeparators.substring(0, 1));
            jLabel1.setText("Statement Separator");

            if (SqlMonitor.this.resultType == SqlMonitor.this.TABLE) {
                rbTable.setSelected(true);
            } else {
                rbText.setSelected(true);
            }

            ButtonGroup bg = new ButtonGroup();

            bg.add(rbText);
            bg.add(rbTable);

            buttonPanel.add(rbText);
            buttonPanel.add(rbTable);
            buttonPanel.setBorder(new TitledBorder(new EtchedBorder(), "Results Type"));

            cbCancel.setText("Cancel");
            cbCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    SqlPrefForm.this.dispose();

                }
            });

            cbOK.setText("OK");
            cbOK.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    SqlMonitor.this.retainTabs = cbRetainTabs.isSelected();
                    SqlMonitor.this.retainStatus = cbRetainStatus.isSelected();
                    SqlMonitor.this.sqlSeparators = txtSeparator.getText().trim().substring(0, 1) + "\r";
                    if (rbText.isSelected()) {
                        SqlMonitor.this.resultType = SqlMonitor.this.MULTIPLE_TEXT;
                    } else {
                        SqlMonitor.this.resultType = SqlMonitor.this.TABLE;
                    }

                    SqlPrefForm.this.dispose();

                }
            });

            jPanel1.add(txtSeparator, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 10, 0));
            jPanel1.add(jLabel1, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
            jPanel1.add(cbRetainStatus, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
            jPanel1.add(cbRetainTabs, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
            jPanel1.add(buttonPanel, new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0,
                    GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
            jPanel1.add(cbCancel, new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 10, 10, 20), 0, 0));
            jPanel1.add(cbOK, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 10), 15, 0));
        }
    }

}
