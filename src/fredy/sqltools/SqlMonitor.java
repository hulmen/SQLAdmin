/**
   SQL-Monitor is a part of Admin ...
 
   This software is part of the Admin-Framework 
 
   Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
   DB-Administrations, as create / delete / alter and query tables it also
   creates indices and generates simple Java-Code to access DBMS-tables and
   exports data into various formats
 
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
 
   Thanks to David Good for his contribution to this tool
  
 
 */
package sql.fredy.sqltools;

import sql.fredy.ui.CodeCompletionForm;
import sql.fredy.ui.SearchReplaceGui;
import sql.fredy.ui.FileDrop;
import sql.fredy.ui.QueryHistoryGui;
import sql.fredy.ui.ImageButton;
import sql.fredy.share.TConnectProperties;
import sql.fredy.share.t_connect;
import sql.fredy.metadata.DbInfo;
import sql.fredy.metadata.SqlTree;
import sql.fredy.io.MyFileFilter;
import sql.fredy.io.ReadFile;
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

import sql.fredy.tools.DbAutoCompletionProvider;
import sql.fredy.tools.PropertiesInDerby;
import sql.fredy.tools.QueryHistory;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Timer;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.autocomplete.*;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SqlMonitor extends JPanel implements Runnable {

    final static int NUMBER_OF_AUTOSAVE = 25;  // we keep 25 files back.....

    // Search and Replace Stuff
    SearchContext context;
    SearchReplaceGui srg;

    private QueryHistory queryHistory;
    private SqlTree sqlTree;

    boolean hasLastDirectorychanged = false;
    private ArrayList sqlWords;
    private UIDefaults defaults;
    /**
     * @return the standAlone
     */
    public static boolean isStandAlone() {
        return standAlone;
    }

    /**
     * @param aStandAlone the standAlone to set
     */
    public static void setStandAlone(boolean aStandAlone) {
        standAlone = aStandAlone;
    }

    //private JTextArea response;
    private JTextPane response;
    public RSyntaxTextArea query;

    private String lastDirectoryPath = null;

    public JTabbedPane tabbedPane;
    public JPanel upperPanel;
    public JScrollPane scp;

    public Toolkit tk;
    public ImageButton exit;
    public JTable tableView;
    public JScrollPane scrollpane;

    boolean retainTabs = false;
    boolean retainStatus = false;
    String sqlSeparators = ";\r";
    final static int TABLE = 0;
    final static int SINGLE_TEXT = 1;
    final static int MULTIPLE_TEXT = 2;
    int resultType = TABLE;
    Container parentFrame;
    private static boolean standAlone = false;

    private int tabIndex = 0;

    t_connect con = null;

    /**
     * Get the value of con.
     *
     * @return value of con.
     */
    public t_connect getCon() {
        if (con == null) {
            con = new t_connect(getHost(),
                    getUser(),
                    getPassword(),
                    getDatabase());
            if ((!con.acceptsConnection()) || (con.isClosed())) {
                con = null;
            }
        }
        return con;
    }

    /**
     * Set the value of con.
     *
     * @param v Value to assign to con.
     */
    public void setCon(t_connect v) {
        this.con = v;
        setUser(con.getUser());
        setHost(con.getHost());
        setPassword(con.getPassword());
        setDatabase(con.getDatabase());
    }

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

    String database = null;

    /**
     * Get the value of database.
     *
     * @return Value of database.
     */
    public String getDatabase() {

        // if the Database ist not set, we first try to get it from Admin
        // if SQLMonitor is not called by Admin, there might be thrown an exception
        if (database == null) {
            try {
                final Container tla = SqlMonitor.this.getTopLevelAncestor();
                setDatabase(((sql.fredy.admin.Admin) tla).logOn.getDatabase());
            } catch (Exception e) {
                database = null;
            }
        }

        // if we still do not know the database, we take the Product-Name as Database
        if (database == null) {
            try {
                DatabaseMetaData dmd = getCon().con.getMetaData();
                database = dmd.getDatabaseProductName();
            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "Exception while SQL operation: {0}", sqlex.getMessage());
                logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
            }

        }

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

    public String getText() {
        return query.getText();
    }

    public void setText(String v) {
        query.setText(v);
    }

    private String tableNamePattern = "%";
    private int maxRowCount = 10000;
    private Logger logger = Logger.getLogger("sql.fredy.sqltools");

    SqlPickList s = null;

    ImageButton pickList;

    private JToolBar toolbar() {

        JToolBar tb = new JToolBar();
        tb.setOrientation(JToolBar.HORIZONTAL);

        final JTextField tablePattern = new JTextField(5);

        // the Story with the max number of rows to read
        final JTextField rowCount = new JTextField(5);
        rowCount.setToolTipText("this is the max. number of rows going to be read. 0 means no restriction");
        rowCount.setHorizontalAlignment(JTextField.RIGHT);

        final TConnectProperties props = new TConnectProperties();
        try {
            rowCount.setText(props.getDefaultMaxRowCount());
        } catch (Exception propException) {
            logger.log(Level.INFO, "Error reading properties ''maxRowCount, using standard Values. {0}", propException.getMessage());
            rowCount.setText("10000");
        }

        ImageButton exec = new ImageButton(null, "binocular.gif", null);
        exec.setToolTipText("Execute query");
        exec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                execQuery2();
                defaultCursor();
            }
        });
        tb.add(exec);

        ImageButton clear = new ImageButton(null, "sheet.gif", null);
        clear.setToolTipText("Clear");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = query.getText();
                StringSelection ss = new StringSelection(s);
                tk.getSystemClipboard().setContents(ss, ss);
                query.setText("");
            }
        });
        tb.add(clear);

        ImageButton copy = new ImageButton(null, "copy.gif", null);
        copy.setToolTipText("Copy all to clipboard");
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = query.getText();
                StringSelection ss = new StringSelection(s);
                tk.getSystemClipboard().setContents(ss, ss);
            }
        });
        tb.add(copy);

        ImageButton paste = new ImageButton(null, "paste.gif", null);
        paste.setToolTipText("Paste");
        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Clipboard c = tk.getSystemClipboard();
                Transferable t = c.getContents(this);
                try {
                    String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                    //query.setText(s);
                    // query.append(s);

                    // at this position, the Text will be added
                    if (query.getText().length() < 1) {
                        query.append(s);
                    } else {
                        int caretPosition = query.getCaretPosition();
                        query.insert(s, caretPosition);
                    }

                } catch (Exception eexc) {
                    tk.beep();
                }
            }
        });
        tb.add(paste);

        final ImageButton export = new ImageButton(null, "export.gif", null);
        export.setToolTipText("Export result");
        export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = tabbedPane.getSelectedIndex();
                if (tabbedPane.getTitleAt(index).startsWith("Result")) {
                    DataExportGui eq = new DataExportGui(getCon().con,
                            ((SqlPanel) (tabbedPane.getComponentAt(index))).getQuery(),
                            true, false);
                    eq.setLocationRelativeTo(export);
                }
            }
        });
        tb.add(export);

        ImageButton xls = new ImageButton(null, "xls.gif", "Export query into xls-file (needs POI)");
        xls.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final int index = tabbedPane.getSelectedIndex();
                if (tabbedPane.getTitleAt(index).startsWith("Result")) {
                    final JFileChooser chooser = new JFileChooser(getLastDirectoryPath());
                    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                    chooser.setDialogTitle("Select XLS-File");
                    chooser.setFileFilter(new MyFileFilter(new String[]{"xls", "XLS", "xlsx", "XLSX"}));

                    int returnVal = chooser.showSaveDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        setLastDirectoryPath(chooser.getCurrentDirectory().getAbsolutePath());
                        busyCursor();
                        waitAMoment(500);
                        Thread thread = new Thread() {
                            public void run() {
                                Calendar start = Calendar.getInstance();
                                XLSExport xe = new XLSExport(getCon().con);
                                JPanel panel = (JPanel) tabbedPane.getComponentAt(index);
                                xe.setQuery(((SqlPanel) panel.getComponent(0)).getQuery());
                                xe.createXLS(chooser.getCurrentDirectory()
                                        + java.io.File.separator
                                        + chooser.getSelectedFile().getName());
                                Calendar fertig = Calendar.getInstance();
                                // we only display a message if the export takes longer than one minute
                                if (((fertig.getTimeInMillis() - start.getTimeInMillis()) / 1000 / 60) > 1) {
                                    JOptionPane.showMessageDialog(null, chooser.getSelectedFile().getName() + " created ");
                                }
                            }
                        };
                        thread.start();
                        defaultCursor();
                    }
                }
            }
        });
        tb.add(xls);

        ImageButton saveQuery = new ImageButton(null, "save.gif", "Save Query to file");
        saveQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter();
                fw.setStartingPath(getLastDirectoryPath());
                fw.setFilter(new String[]{"sql", "SQL", "txt", "TXT"});
                fw.setContent(query.getText());
                fw.setFileName("?");
                fw.setSwitch("a");
                fw.write();
                setLastDirectoryPath(fw.getStartingPath());
            }
        });
        tb.add(saveQuery);

        ImageButton loadQuery = new ImageButton(null, "load.gif", "Load query from file");
        loadQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ReadFile rf = new ReadFile();
                rf.setFilter(new String[]{"sql", "SQL", "txt", "TXT"});
                rf.setStartingPath(getLastDirectoryPath());
                rf.setFileName("?");
                if (query.getText().length() > 0) {
                    query.append(";\n");
                }
                query.append("\r\n-- ");
                query.append(rf.getFileName());
                query.append(" ;\r\n");
                query.append(rf.getText());
                query.updateUI();
                setLastDirectoryPath(rf.getStartingPath());
            }
        });
        tb.add(loadQuery);

        ImageButton preferences = new ImageButton(null, "user.gif", null);
        preferences.setToolTipText("Preferences");
        preferences.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                SqlPrefForm sp = new SqlPrefForm();

            }
        });
        tb.add(preferences);

        ImageButton helpButton = new ImageButton(null, "help.gif", "Display Help");
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        });
        tb.add(helpButton);

        // Picklist Stuff
        pickList = new ImageButton(null, "data.gif", null);
        pickList.setToolTipText("Pick List");
        pickList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                parentFrame = getParentFrame();
                final Container tla = SqlMonitor.this.getTopLevelAncestor();
                Container PickListFrame;

                ActionListener a = new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        if (!isStandAlone()) {
                            try {
                                ((JInternalFrame) parentFrame).setSelected(true);
                            } catch (PropertyVetoException ex) {
                                ex.printStackTrace();
                            }
                        }
                        parentFrame.requestFocus();
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
                if (tablePattern.getText().length() > 0) {
                    setTableNamePattern(tablePattern.getText());
                }

                logger.log(Level.INFO, "collecting info for picklist with pattern " + getTableNamePattern() + " and Schema " + getSchema());
                busyCursor();
                s = new SqlPickList(getCon(), a, getSchema(), getTableNamePattern());
                s.setProductName(dbInfo.getProductName());
                defaultCursor();

                //Open Pick List as Dialog if standalone, another internal frame if within admin
                if (isStandAlone()) {
                    //PickListFrame = new JDialog((Frame)(parentFrame),"Pick List");
                    PickListFrame = new JDialog((Frame) (parentFrame), "Pick from: " + getTableNamePattern());
                    ((RootPaneContainer) PickListFrame).getContentPane().add(s);
                    ((JDialog) PickListFrame).pack();

                } else {

                    PickListFrame = new JInternalFrame("Pick from: " + getTableNamePattern(), true, true);

                    ((RootPaneContainer) PickListFrame).getContentPane().add(s);
                    ((JInternalFrame) PickListFrame).pack();
                    ((sql.fredy.admin.Admin) tla).lc.add(PickListFrame, -1);

                }
                Point p = parentFrame.getLocationOnScreen();
                p.translate(parentFrame.getWidth(), 0);

                // Sometimes the list is display somewhere out of the picture....
                int xCoord = 0;
                int yCoord = 0;
                if (p.x > (tla.getX() + tla.getWidth())) {
                    xCoord = parentFrame.getX();
                    //logger.log(Level.INFO, "X coordinate out of range {0}", p.x);
                } else {
                    xCoord = p.x;
                }
                if (p.y > tla.getHeight()) {
                    yCoord = parentFrame.getY();
                    //logger.log(Level.INFO, "Y coordinate out of range {0}", p.y);
                } else {
                    yCoord = p.y;
                }
                p.setLocation(xCoord, yCoord);

                PickListFrame.setLocation(p);
                PickListFrame.setVisible(true);
            }
        });

        tb.add(pickList);

        // add FontMenu here....
        // das Pattern           
        tablePattern.setText(getTableNamePattern());
        tablePattern.setToolTipText(
                "set the Tablename Pattern for the Picklist, %=all");
        tablePattern.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setTableNamePattern(tablePattern.getText());
                try {
                    if (!s.equals(null)) {
                        s.refreshTable();
                    }
                    pickList.doClick(50);
                } catch (Exception ex) {

                }
            }
        }
        );
        tb.add(tablePattern);

        // modify numbers of Rows to read
        rowCount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    setMaxRowCount(Integer.parseInt(rowCount.getText()));
                } catch (Exception ex) {

                }
            }
        });
        tb.add(rowCount);

        exit = new ImageButton(null, "exit.gif", null);
        exit.setToolTipText("Quit");
        tb.add(exit);

        return tb;
    }

    private Color background;
    private Color foreground;

    private void editorExtensions() {
        // add Editor Stuff here
        setBackground(query.getBackground());
        setForeground(query.getForeground());

        // Font-size
        JMenu fontMenu = new JMenu("Fontsize");

        JMenuItem font8 = new JMenuItem("8");
        JMenuItem font10 = new JMenuItem("10");
        JMenuItem font12 = new JMenuItem("12");
        JMenuItem font14 = new JMenuItem("14");
        JMenuItem font16 = new JMenuItem("16");
        JMenuItem font18 = new JMenuItem("18");

        JMenuItem zoomPlus = new JMenuItem("bigger");  // not used now
        JMenuItem zoomMinus = new JMenuItem("smaller"); // not used now

        fontMenu.add(font8);
        fontMenu.add(font10);
        fontMenu.add(font12);
        fontMenu.add(font14);
        fontMenu.add(font16);
        fontMenu.add(font18);

        font8.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                query.setFont(query.getFont().deriveFont(8.0f));
            }
        });
        font10.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                query.setFont(query.getFont().deriveFont(10.0f));
            }
        });
        font12.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                query.setFont(query.getFont().deriveFont(12.0f));
            }
        });
        font14.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                query.setFont(query.getFont().deriveFont(14.0f));
            }
        });
        font16.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                query.setFont(query.getFont().deriveFont(16.0f));
            }
        });
        font18.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                query.setFont(query.getFont().deriveFont(18.0f));
            }
        });

        JMenuItem codeCompletor = new JMenuItem("Code Completion");
        JMenuItem queryHistory = new JMenuItem("Query history");
        JMenuItem quickSave = new JMenuItem("Quick save");
        JMenuItem snr = new JMenuItem("Search & Replace");
        JMenuItem doQuery = new JMenuItem("Execute");
        JPopupMenu qpm = query.getPopupMenu();

        qpm.addSeparator();
        qpm.add(codeCompletor);
        qpm.add(queryHistory);
        qpm.addSeparator();
        qpm.add(doQuery);
        qpm.addSeparator();
        qpm.add(quickSave);
        qpm.add(snr);
        qpm.addSeparator();
        qpm.add(fontMenu);

        // a bigger Font has been selected: DOES NOT WORK
        zoomPlus.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Fontsize: " + fontP.getSize());                                      
                query.setFont(query.getFont().deriveFont(query.getFont().getStyle(), query.getFont().getSize2D() + 2.0f));
                query.updateUI();
            }
        });

        // a smallerFont has been selected: DOES NOT WORK
        zoomMinus.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {

                Font fontM = query.getFont();
                if (fontM.getSize2D() < 2.0) {
                    query.setFont(fontM.deriveFont(fontM.getSize2D() - 2.0f));
                    query.updateUI();
                }
            }
        });

        snr.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                srg.setLocation(p.x, p.y);
                srg.setVisible(true);
            }
        });

        doQuery.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                execQuery2();
                defaultCursor();
            }
        });
        quickSave.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                busyCursor();
                saveQuery();
                defaultCursor();
            }
        });

        codeCompletor.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                codeCompletion();
            }
        });

        queryHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                queryHistory();
            }
        });

    }
    CodeCompletionForm codeForm;
    JDialog codeCompletionDialog = null;

    private void codeCompletion() {
        if (codeCompletionDialog == null) {
            codeCompletionDialog = new JDialog();
            codeCompletionDialog.setTitle("Code Templates");
            codeCompletionDialog.getContentPane().setLayout(new BorderLayout());
            codeForm = new CodeCompletionForm((DbAutoCompletionProvider) provider);
            codeCompletionDialog.getContentPane().add(codeForm, BorderLayout.CENTER);
            codeCompletionDialog.pack();
            codeForm.cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //codeForm.close();
                    codeCompletionDialog.setVisible(false);
                }
            });
            // add the windowlistener here to avoid mess in Derby DB
            codeCompletionDialog.addWindowListener(new WindowAdapter() {
                public void windowActivated(WindowEvent e) {
                }

                public void windowClosed(WindowEvent e) {
                    //codeForm.close();
                    codeCompletionDialog.setVisible(false);
                }

                public void windowClosing(WindowEvent e) {
                    //codeForm.close();
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

            codeForm.cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //codeForm.close();
                    codeCompletionDialog.setVisible(false);
                    //fixDriver();
                }
            });
        }
        codeCompletionDialog.setModal(true);
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        codeCompletionDialog.setLocation(pointerInfo.getLocation());
        codeCompletionDialog.setVisible(true);

    }

    QueryHistoryGui history;
    JDialog historyDialog = null;

    private void queryHistory() {
        if (historyDialog == null) {
            historyDialog = new JDialog();
            historyDialog.setTitle("Query History");
            historyDialog.getContentPane().setLayout(new BorderLayout());
            history = new QueryHistoryGui(getDatabase());
            historyDialog.getContentPane().add(history, BorderLayout.CENTER);
            historyDialog.pack();
            history.cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    //codeForm.close();
                    historyDialog.setVisible(false);
                }
            });
            // add the windowlistener here to avoid mess in Derby DB
            historyDialog.addWindowListener(new WindowAdapter() {
                public void windowActivated(WindowEvent e) {
                    history.fillAll();
                }

                public void windowClosed(WindowEvent e) {
                    //codeForm.close();
                    historyDialog.setVisible(false);
                }

                public void windowClosing(WindowEvent e) {
                    //codeForm.close();
                }

                public void windowDeactivated(WindowEvent e) {
                }

                public void windowDeiconified(WindowEvent e) {
                    history.fillAll();
                }

                public void windowIconified(WindowEvent e) {
                }

                public void windowOpened(WindowEvent e) {
                    history.fillAll();
                }
            });

            history.cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    historyDialog.setVisible(false);
                }
            });
        }

        history.getQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                if (query.getText().length() > 0 ) {
                    query.append(";\n\n");
                }
                query.append(history.getText());
            }
        });

        historyDialog.setModal(false);
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        historyDialog.setLocation(pointerInfo.getLocation());
        historyDialog.setVisible(true);

    }

    private void searchNreplace() {
        //Search & Replace: look here http://www.fifesoft.com/rsyntaxtextarea/examples/example4.php

        srg.next.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (srg.getSearchFor().length() > 0) {
                    context.setSearchFor(srg.getSearchFor());
                    context.setMatchCase(srg.isMatchCase());
                    context.setRegularExpression(srg.isRegExp());
                    context.setSearchForward(true);
                    context.setWholeWord(srg.isWholeWord());

                    SearchResult sr = SearchEngine.find(query, context);
                    DocumentRange dr = sr.getMatchRange();

                    SearchEngine.markAll(query, context);

                    int startPos = query.getCaretPosition();

                    int lng = SearchEngine.getNextMatchPos(srg.getSearchFor(), query.getText().substring(query.getCaretPosition()), true, srg.isMatchCase(), srg.isWholeWord());

                    try {
                        query.setCaretPosition(startPos + lng);
                    } catch (Exception stupidException) {

                    }
                    query.updateUI();

                }
            }
        });
        srg.prev.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                if (srg.getSearchFor().length() > 0) {
                    context.setSearchFor(srg.getSearchFor());
                    context.setMatchCase(srg.isMatchCase());
                    context.setRegularExpression(srg.isRegExp());
                    context.setSearchForward(false);
                    context.setWholeWord(srg.isWholeWord());

                    SearchResult sr = SearchEngine.find(query, context);
                    DocumentRange dr = sr.getMatchRange();

                    SearchEngine.markAll(query, context);

                    int startPos = query.getCaretPosition();
                    int lng = SearchEngine.getNextMatchPos(srg.getSearchFor(), query.getText().substring(0, query.getCaretPosition()), false, srg.isMatchCase(), srg.isWholeWord());

                    if (lng < 0) {
                        lng = 0;
                    }
                    query.setCaretPosition(lng);
                    query.updateUI();

                }
            }
        });

        srg.replace.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                if ((srg.getSearchFor().length() > 0) && (srg.getReplaceWith().length() > 0)) {
                    context.setSearchFor(srg.getSearchFor());
                    context.setReplaceWith(srg.getReplaceWith());
                    context.setMatchCase(srg.isMatchCase());
                    context.setRegularExpression(srg.isRegExp());
                    context.setSearchForward(true);
                    context.setWholeWord(srg.isWholeWord());

                    SearchResult sr = SearchEngine.find(query, context);
                    DocumentRange dr = sr.getMatchRange();

                    SearchEngine.markAll(query, context);
                    SearchEngine.replace(query, context);

                    int startPos = query.getCaretPosition();
                    int lng = SearchEngine.getNextMatchPos(srg.getSearchFor(), query.getText().substring(query.getCaretPosition()), true, srg.isMatchCase(), srg.isWholeWord());

                    query.setCaretPosition(startPos + lng);
                    query.updateUI();

                }

            }
        });

        srg.replaceAll.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                if ((srg.getSearchFor().length() > 0) && (srg.getReplaceWith().length() > 0)) {
                    context.setSearchFor(srg.getSearchFor());
                    context.setReplaceWith(srg.getReplaceWith());
                    context.setMatchCase(srg.isMatchCase());
                    context.setRegularExpression(srg.isRegExp());
                    context.setSearchForward(true);
                    context.setWholeWord(srg.isWholeWord());

                    SearchEngine.replaceAll(query, context);
                }
            }
        });

        srg.unselect.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                context.setSearchFor("");
                context.setMarkAll(false);
                SearchEngine.markAll(query, context);

                context.setMarkAll(true);
                query.doLayout();
            }
        });

    }

    private void defaultCursor() {
        query.setCursor(Cursor.getDefaultCursor());
        waitAMoment(100);
    }

    private void busyCursor() {
        query.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        waitAMoment(100);
    }

    private void fileDrops() {
        new FileDrop(query, new FileDrop.Listener() {
            public void filesDropped(java.io.File[] files) {

                for (int i = 0; i < files.length; i++) {
                    try {
                        ReadFile rf = new ReadFile(files[i].getCanonicalPath());
                        query.append("\r\n-- ");
                        query.append(files[i].getCanonicalPath());
                        query.append(" ;\r\n");
                        query.append(rf.getText());
                        query.append("\r\n;\r\n");  // we add new lines and a semicolon to execute this query
                        query.updateUI();
                        setLastDirectoryPath(rf.getStartingPath());

                        //text.append(files[i].getCanonicalPath() + "\n");
                    } // end try
                    catch (java.io.IOException e) {
                    }
                }   // end for: through each dropped file
            }   // end filesDropped
        }); // end FileDrop.Listener

    }

    /*
     This adds Key-Listener to the query-Window. 
     Primarily it is done to execute the query by pressing F5-Key
     */
    private void doKeyListener() {

        int inFocus = JComponent.WHEN_IN_FOCUSED_WINDOW;
        //int inFocus = JComponent.WHEN_FOCUSED;
        InputMap iMap = this.getInputMap(inFocus);
        ActionMap aMap = this.getActionMap();

        KeyStroke ctrlS = KeyStroke.getKeyStroke("control S");
        KeyStroke ctrlF = KeyStroke.getKeyStroke("control F");
        KeyStroke ctrlH = KeyStroke.getKeyStroke("control H");
        KeyStroke f5 = KeyStroke.getKeyStroke("F5");

        iMap.put(ctrlS, "ctrl-S");
        iMap.put(ctrlF, "ctrl-F");
        iMap.put(ctrlH, "ctrl-H");
        iMap.put(f5, "F5");

        Action executeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                execQuery2();
                defaultCursor();
            }
        };

        Action searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                srg.setLocation(p.x, p.y);
                srg.setVisible(true);
            }
        };

        Action showHelpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showHelp();
            }
        };

        aMap.put("F5", executeAction);
        aMap.put("ctrl-S", searchAction);
        aMap.put("ctrl-F", searchAction);
        aMap.put("ctrl-H", showHelpAction);

    }

    public void showHelp() {
        String msg = help();
        JOptionPane.showMessageDialog(null, msg);

    }

    public String help() {
        StringBuilder help = new StringBuilder();
        help.append("Tipps\n");
        help.append("Click right for menu\n");
        help.append("Click CTRL-H for this window\n");
        help.append("F5 executes query ");
        help.append("(if there is a selection, only the selected text will be sent to the DB)\n");
        help.append("open the DB-Tree View on the left by clicking the small arrows\n");
        help.append("Type ALT-1 to get the previous command\n");
        help.append("Type ALT-A followed by a text terminated by <Enter> to create select statement using the text typed as table alias\n");
        help.append("Type ALT B to paste the first letter of the tablename as alias\n");
        help.append("Type ALT C to paste just the leaf");
        help.append("Type ALT-D in a column-leaf to get a \"select distinct {columnName} from {tableName}\"\n");
        help.append("Type ALT-E inside a table branch to get a select-Statement created using the first letter of the table as alias\n");
        help.append("Type ALT F to paste fully qualified names\n");
        help.append("Type ALT-H for this helpscreen\n");
        help.append("Type ALT N to paste tablename.columnname\n");
        help.append("Type ALT-S inside a table branch to get a select-Statement created\n");
        help.append("Type ALT X to walk the tree down\n");
        help.append("Type ALT Y to walk the tree up\n");
        help.append("Type ALT Insert to paste just plain values (same as doubleclick within the tree)\n");

        help.append("Type Control S to open the Search Window\n");
        help.append("Type Control F to open the Search Window\n");
        help.append("Do code completion with CTRL-SPACE, the SQL 2000 standard is already there. \nExtend on your own: right click and select 'Code Completion'");

        return help.toString();
    }

    /*
     Do autocompletion for SQL 2003 Standard
     */
    CompletionProvider provider;
    AutoCompletion ac;

    private void sql2003Completion() {
        //logger.log(Level.INFO, "adding code completion for SQL2003");

        sms.setMessage("creating SQL 2003 code completion");
        query.setCodeFoldingEnabled(true);
        query.setAntiAliasingEnabled(true);
        provider = createCompletionProvider();

        ac = new AutoCompletion(provider);
        ac.install(query);

    }

    private CompletionProvider createCompletionProvider() {

        DbAutoCompletionProvider provider = new DbAutoCompletionProvider();

        provider.addCompletion(new BasicCompletion(provider, "ADD"));
        provider.addCompletion(new BasicCompletion(provider, "ALL"));
        provider.addCompletion(new BasicCompletion(provider, "ALLOCATE"));
        provider.addCompletion(new BasicCompletion(provider, "ALTER"));
        provider.addCompletion(new BasicCompletion(provider, "AND"));
        provider.addCompletion(new BasicCompletion(provider, "ANY"));
        provider.addCompletion(new BasicCompletion(provider, "ARE"));
        provider.addCompletion(new BasicCompletion(provider, "ARRAY"));
        provider.addCompletion(new BasicCompletion(provider, "AS"));
        provider.addCompletion(new BasicCompletion(provider, "ASENSITIVE"));
        provider.addCompletion(new BasicCompletion(provider, "ASYMMETRIC"));
        provider.addCompletion(new BasicCompletion(provider, "AT"));
        provider.addCompletion(new BasicCompletion(provider, "ATOMIC"));
        provider.addCompletion(new BasicCompletion(provider, "AUTHORIZATION"));
        provider.addCompletion(new BasicCompletion(provider, "BEGIN"));
        provider.addCompletion(new BasicCompletion(provider, "BETWEEN"));
        provider.addCompletion(new BasicCompletion(provider, "BIGINT"));
        provider.addCompletion(new BasicCompletion(provider, "BINARY"));
        provider.addCompletion(new BasicCompletion(provider, "BLOB"));
        provider.addCompletion(new BasicCompletion(provider, "BOOLEAN"));
        provider.addCompletion(new BasicCompletion(provider, "BOTH"));
        provider.addCompletion(new BasicCompletion(provider, "BY"));
        provider.addCompletion(new BasicCompletion(provider, "CALL"));
        provider.addCompletion(new BasicCompletion(provider, "CALLED"));
        provider.addCompletion(new BasicCompletion(provider, "CASCADED"));
        provider.addCompletion(new BasicCompletion(provider, "CASE"));
        provider.addCompletion(new BasicCompletion(provider, "CAST"));
        provider.addCompletion(new BasicCompletion(provider, "CHAR"));
        provider.addCompletion(new BasicCompletion(provider, "CHARACTER"));
        provider.addCompletion(new BasicCompletion(provider, "CHECK"));
        provider.addCompletion(new BasicCompletion(provider, "CLOB"));
        provider.addCompletion(new BasicCompletion(provider, "CLOSE"));
        provider.addCompletion(new BasicCompletion(provider, "COLLATE"));
        provider.addCompletion(new BasicCompletion(provider, "COLUMN"));
        provider.addCompletion(new BasicCompletion(provider, "COMMIT"));
        provider.addCompletion(new BasicCompletion(provider, "CONDITION"));
        provider.addCompletion(new BasicCompletion(provider, "CONNECT"));
        provider.addCompletion(new BasicCompletion(provider, "CONSTRAINT"));
        provider.addCompletion(new BasicCompletion(provider, "CONTINUE"));
        provider.addCompletion(new BasicCompletion(provider, "CORRESPONDING"));
        provider.addCompletion(new BasicCompletion(provider, "CREATE"));
        provider.addCompletion(new BasicCompletion(provider, "CROSS"));
        provider.addCompletion(new BasicCompletion(provider, "CUBE"));
        provider.addCompletion(new BasicCompletion(provider, "CURRENT"));
        provider.addCompletion(new BasicCompletion(provider, "CURRENT_DATE"));
        provider.addCompletion(new BasicCompletion(provider, "CURRENT_DEFAULT_TRANSFORM_GROUP"));
        provider.addCompletion(new BasicCompletion(provider, "CURRENT_PATH"));
        provider.addCompletion(new BasicCompletion(provider, "CURRENT_ROLE"));
        provider.addCompletion(new BasicCompletion(provider, "CURRENT_TIME"));
        provider.addCompletion(new BasicCompletion(provider, "CURRENT_TIMESTAMP"));
        provider.addCompletion(new BasicCompletion(provider, "CURRENT_TRANSFORM_GROUP_FOR_TYPE"));
        provider.addCompletion(new BasicCompletion(provider, "CURRENT_USER"));
        provider.addCompletion(new BasicCompletion(provider, "CURSOR"));
        provider.addCompletion(new BasicCompletion(provider, "CYCLE"));
        provider.addCompletion(new BasicCompletion(provider, "DATE"));
        provider.addCompletion(new BasicCompletion(provider, "DAY"));
        provider.addCompletion(new BasicCompletion(provider, "DEALLOCATE"));
        provider.addCompletion(new BasicCompletion(provider, "DEC"));
        provider.addCompletion(new BasicCompletion(provider, "DECIMAL"));
        provider.addCompletion(new BasicCompletion(provider, "DECLARE"));
        provider.addCompletion(new BasicCompletion(provider, "DEFAULT"));
        provider.addCompletion(new BasicCompletion(provider, "DELETE"));
        provider.addCompletion(new BasicCompletion(provider, "DEREF"));
        provider.addCompletion(new BasicCompletion(provider, "DESCRIBE"));
        provider.addCompletion(new BasicCompletion(provider, "DETERMINISTIC"));
        provider.addCompletion(new BasicCompletion(provider, "DISCONNECT"));
        provider.addCompletion(new BasicCompletion(provider, "DISTINCT"));
        provider.addCompletion(new BasicCompletion(provider, "DO"));
        provider.addCompletion(new BasicCompletion(provider, "DOUBLE"));
        provider.addCompletion(new BasicCompletion(provider, "DROP"));
        provider.addCompletion(new BasicCompletion(provider, "DYNAMIC"));
        provider.addCompletion(new BasicCompletion(provider, "EACH"));
        provider.addCompletion(new BasicCompletion(provider, "ELEMENT"));
        provider.addCompletion(new BasicCompletion(provider, "ELSE"));
        provider.addCompletion(new BasicCompletion(provider, "ELSEIF"));
        provider.addCompletion(new BasicCompletion(provider, "END"));
        provider.addCompletion(new BasicCompletion(provider, "ESCAPE"));
        provider.addCompletion(new BasicCompletion(provider, "EXCEPT"));
        provider.addCompletion(new BasicCompletion(provider, "EXEC"));
        provider.addCompletion(new BasicCompletion(provider, "EXECUTE"));
        provider.addCompletion(new BasicCompletion(provider, "EXISTS"));
        provider.addCompletion(new BasicCompletion(provider, "EXIT"));
        provider.addCompletion(new BasicCompletion(provider, "EXTERNAL"));
        provider.addCompletion(new BasicCompletion(provider, "FALSE"));
        provider.addCompletion(new BasicCompletion(provider, "FETCH"));
        provider.addCompletion(new BasicCompletion(provider, "FILTER"));
        provider.addCompletion(new BasicCompletion(provider, "FLOAT"));
        provider.addCompletion(new BasicCompletion(provider, "FOR"));
        provider.addCompletion(new BasicCompletion(provider, "FOREIGN"));
        provider.addCompletion(new BasicCompletion(provider, "FREE"));
        provider.addCompletion(new BasicCompletion(provider, "FROM"));
        provider.addCompletion(new BasicCompletion(provider, "FULL"));
        provider.addCompletion(new BasicCompletion(provider, "FUNCTION"));
        provider.addCompletion(new BasicCompletion(provider, "GET"));
        provider.addCompletion(new BasicCompletion(provider, "GLOBAL"));
        provider.addCompletion(new BasicCompletion(provider, "GRANT"));
        provider.addCompletion(new BasicCompletion(provider, "GROUP"));
        provider.addCompletion(new BasicCompletion(provider, "GROUPING"));
        provider.addCompletion(new BasicCompletion(provider, "HANDLER"));
        provider.addCompletion(new BasicCompletion(provider, "HAVING"));
        provider.addCompletion(new BasicCompletion(provider, "HOLD"));
        provider.addCompletion(new BasicCompletion(provider, "HOUR"));
        provider.addCompletion(new BasicCompletion(provider, "IDENTITY"));
        provider.addCompletion(new BasicCompletion(provider, "IF"));
        provider.addCompletion(new BasicCompletion(provider, "IMMEDIATE"));
        provider.addCompletion(new BasicCompletion(provider, "IN"));
        provider.addCompletion(new BasicCompletion(provider, "INDICATOR"));
        provider.addCompletion(new BasicCompletion(provider, "INNER"));
        provider.addCompletion(new BasicCompletion(provider, "INOUT"));
        provider.addCompletion(new BasicCompletion(provider, "INPUT"));
        provider.addCompletion(new BasicCompletion(provider, "INSENSITIVE"));
        provider.addCompletion(new BasicCompletion(provider, "INSERT"));
        provider.addCompletion(new BasicCompletion(provider, "INT"));
        provider.addCompletion(new BasicCompletion(provider, "INTEGER"));
        provider.addCompletion(new BasicCompletion(provider, "INTERSECT"));
        provider.addCompletion(new BasicCompletion(provider, "INTERVAL"));
        provider.addCompletion(new BasicCompletion(provider, "INTO"));
        provider.addCompletion(new BasicCompletion(provider, "IS"));
        provider.addCompletion(new BasicCompletion(provider, "ITERATE"));
        provider.addCompletion(new BasicCompletion(provider, "JOIN"));
        provider.addCompletion(new BasicCompletion(provider, "LANGUAGE"));
        provider.addCompletion(new BasicCompletion(provider, "LARGE"));
        provider.addCompletion(new BasicCompletion(provider, "LATERAL"));
        provider.addCompletion(new BasicCompletion(provider, "LEADING"));
        provider.addCompletion(new BasicCompletion(provider, "LEAVE"));
        provider.addCompletion(new BasicCompletion(provider, "LEFT"));
        provider.addCompletion(new BasicCompletion(provider, "LIKE"));
        provider.addCompletion(new BasicCompletion(provider, "LOCAL"));
        provider.addCompletion(new BasicCompletion(provider, "LOCALTIME"));
        provider.addCompletion(new BasicCompletion(provider, "LOCALTIMESTAMP"));
        provider.addCompletion(new BasicCompletion(provider, "LOOP"));
        provider.addCompletion(new BasicCompletion(provider, "MATCH"));
        provider.addCompletion(new BasicCompletion(provider, "MEMBER"));
        provider.addCompletion(new BasicCompletion(provider, "MERGE"));
        provider.addCompletion(new BasicCompletion(provider, "METHOD"));
        provider.addCompletion(new BasicCompletion(provider, "MINUTE"));
        provider.addCompletion(new BasicCompletion(provider, "MODIFIES"));
        provider.addCompletion(new BasicCompletion(provider, "MODULE"));
        provider.addCompletion(new BasicCompletion(provider, "MONTH"));
        provider.addCompletion(new BasicCompletion(provider, "MULTISET"));
        provider.addCompletion(new BasicCompletion(provider, "NATIONAL"));
        provider.addCompletion(new BasicCompletion(provider, "NATURAL"));
        provider.addCompletion(new BasicCompletion(provider, "NCHAR"));
        provider.addCompletion(new BasicCompletion(provider, "NCLOB"));
        provider.addCompletion(new BasicCompletion(provider, "NEW"));
        provider.addCompletion(new BasicCompletion(provider, "NO"));
        provider.addCompletion(new BasicCompletion(provider, "NONE"));
        provider.addCompletion(new BasicCompletion(provider, "NOT"));
        provider.addCompletion(new BasicCompletion(provider, "NULL"));
        provider.addCompletion(new BasicCompletion(provider, "NUMERIC"));
        provider.addCompletion(new BasicCompletion(provider, "OF"));
        provider.addCompletion(new BasicCompletion(provider, "OLD"));
        provider.addCompletion(new BasicCompletion(provider, "ON"));
        provider.addCompletion(new BasicCompletion(provider, "ONLY"));
        provider.addCompletion(new BasicCompletion(provider, "OPEN"));
        provider.addCompletion(new BasicCompletion(provider, "OR"));
        provider.addCompletion(new BasicCompletion(provider, "ORDER"));
        provider.addCompletion(new BasicCompletion(provider, "OUT"));
        provider.addCompletion(new BasicCompletion(provider, "OUTER"));
        provider.addCompletion(new BasicCompletion(provider, "OUTPUT"));
        provider.addCompletion(new BasicCompletion(provider, "OVER"));
        provider.addCompletion(new BasicCompletion(provider, "OVERLAPS"));
        provider.addCompletion(new BasicCompletion(provider, "PARAMETER"));
        provider.addCompletion(new BasicCompletion(provider, "PARTITION"));
        provider.addCompletion(new BasicCompletion(provider, "PRECISION"));
        provider.addCompletion(new BasicCompletion(provider, "PREPARE"));
        provider.addCompletion(new BasicCompletion(provider, "PRIMARY"));
        provider.addCompletion(new BasicCompletion(provider, "PROCEDURE"));
        provider.addCompletion(new BasicCompletion(provider, "RANGE"));
        provider.addCompletion(new BasicCompletion(provider, "READS"));
        provider.addCompletion(new BasicCompletion(provider, "REAL"));
        provider.addCompletion(new BasicCompletion(provider, "RECURSIVE"));
        provider.addCompletion(new BasicCompletion(provider, "REF"));
        provider.addCompletion(new BasicCompletion(provider, "REFERENCES"));
        provider.addCompletion(new BasicCompletion(provider, "REFERENCING"));
        provider.addCompletion(new BasicCompletion(provider, "RELEASE"));
        provider.addCompletion(new BasicCompletion(provider, "REPEAT"));
        provider.addCompletion(new BasicCompletion(provider, "RESIGNAL"));
        provider.addCompletion(new BasicCompletion(provider, "RESULT"));
        provider.addCompletion(new BasicCompletion(provider, "RETURN"));
        provider.addCompletion(new BasicCompletion(provider, "RETURNS"));
        provider.addCompletion(new BasicCompletion(provider, "REVOKE"));
        provider.addCompletion(new BasicCompletion(provider, "RIGHT"));
        provider.addCompletion(new BasicCompletion(provider, "ROLLBACK"));
        provider.addCompletion(new BasicCompletion(provider, "ROLLUP"));
        provider.addCompletion(new BasicCompletion(provider, "ROW"));
        provider.addCompletion(new BasicCompletion(provider, "ROWS"));
        provider.addCompletion(new BasicCompletion(provider, "SAVEPOINT"));
        provider.addCompletion(new BasicCompletion(provider, "SCOPE"));
        provider.addCompletion(new BasicCompletion(provider, "SCROLL"));
        provider.addCompletion(new BasicCompletion(provider, "SEARCH"));
        provider.addCompletion(new BasicCompletion(provider, "SECOND"));
        provider.addCompletion(new BasicCompletion(provider, "SELECT"));
        provider.addCompletion(new BasicCompletion(provider, "SENSITIVE"));
        provider.addCompletion(new BasicCompletion(provider, "SESSION_USER"));
        provider.addCompletion(new BasicCompletion(provider, "SET"));
        provider.addCompletion(new BasicCompletion(provider, "SIGNAL"));
        provider.addCompletion(new BasicCompletion(provider, "SIMILAR"));
        provider.addCompletion(new BasicCompletion(provider, "SMALLINT"));
        provider.addCompletion(new BasicCompletion(provider, "SOME"));
        provider.addCompletion(new BasicCompletion(provider, "SPECIFIC"));
        provider.addCompletion(new BasicCompletion(provider, "SPECIFICTYPE"));
        provider.addCompletion(new BasicCompletion(provider, "SQL"));
        provider.addCompletion(new BasicCompletion(provider, "SQLEXCEPTION"));
        provider.addCompletion(new BasicCompletion(provider, "SQLSTATE"));
        provider.addCompletion(new BasicCompletion(provider, "SQLWARNING"));
        provider.addCompletion(new BasicCompletion(provider, "START"));
        provider.addCompletion(new BasicCompletion(provider, "STATIC"));
        provider.addCompletion(new BasicCompletion(provider, "SUBMULTISET"));
        provider.addCompletion(new BasicCompletion(provider, "SYMMETRIC"));
        provider.addCompletion(new BasicCompletion(provider, "SYSTEM"));
        provider.addCompletion(new BasicCompletion(provider, "SYSTEM_USER"));
        provider.addCompletion(new BasicCompletion(provider, "TABLE"));
        provider.addCompletion(new BasicCompletion(provider, "TABLESAMPLE"));
        provider.addCompletion(new BasicCompletion(provider, "THEN"));
        provider.addCompletion(new BasicCompletion(provider, "TIME"));
        provider.addCompletion(new BasicCompletion(provider, "TIMESTAMP"));
        provider.addCompletion(new BasicCompletion(provider, "TIMEZONE_HOUR"));
        provider.addCompletion(new BasicCompletion(provider, "TIMEZONE_MINUTE"));
        provider.addCompletion(new BasicCompletion(provider, "TO"));
        provider.addCompletion(new BasicCompletion(provider, "TRAILING"));
        provider.addCompletion(new BasicCompletion(provider, "TRANSLATION"));
        provider.addCompletion(new BasicCompletion(provider, "TREAT"));
        provider.addCompletion(new BasicCompletion(provider, "TRIGGER"));
        provider.addCompletion(new BasicCompletion(provider, "TRUE"));
        provider.addCompletion(new BasicCompletion(provider, "UNDO"));
        provider.addCompletion(new BasicCompletion(provider, "UNION"));
        provider.addCompletion(new BasicCompletion(provider, "UNIQUE"));
        provider.addCompletion(new BasicCompletion(provider, "UNKNOWN"));
        provider.addCompletion(new BasicCompletion(provider, "UNNEST"));
        provider.addCompletion(new BasicCompletion(provider, "UNTIL"));
        provider.addCompletion(new BasicCompletion(provider, "UPDATE"));
        provider.addCompletion(new BasicCompletion(provider, "USER"));
        provider.addCompletion(new BasicCompletion(provider, "USING"));
        provider.addCompletion(new BasicCompletion(provider, "VALUE"));
        provider.addCompletion(new BasicCompletion(provider, "VALUES"));
        provider.addCompletion(new BasicCompletion(provider, "VARCHAR"));
        provider.addCompletion(new BasicCompletion(provider, "VARYING"));
        provider.addCompletion(new BasicCompletion(provider, "WHEN"));
        provider.addCompletion(new BasicCompletion(provider, "WHENEVER"));
        provider.addCompletion(new BasicCompletion(provider, "WHERE"));
        provider.addCompletion(new BasicCompletion(provider, "WHILE"));
        provider.addCompletion(new BasicCompletion(provider, "WINDOW"));
        provider.addCompletion(new BasicCompletion(provider, "WITH"));
        provider.addCompletion(new BasicCompletion(provider, "WITHIN"));
        provider.addCompletion(new BasicCompletion(provider, "WITHOUT"));
        provider.addCompletion(new BasicCompletion(provider, "YEAR"));

        // additional things to add
        provider.addCompletion(new ShorthandCompletion(provider, "nl", "with (nolock)"));

        // add the personal completion stuff 
        sms.setMessage("loading personal code completion");

        CodeCompletionForm ccf = new CodeCompletionForm(provider);
        ccf.loadCompletion();
        ccf.close();

        fixDriver();

        return provider;
    }

    private void fixDriver() {
        try {
            Class.forName(getCon().getDriver());
        } catch (ClassNotFoundException cnfe) {

        }
    }

    private JPanel queryPanel() {
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

        //  query = new RSyntaxTextArea(20, 80);  // we do this earlier, because of the JPopUpMenu 
        query.setFont(new Font("Monospaced", Font.PLAIN, 13));
        //query.setFont(new Font("Lucida Console", Font.PLAIN, 12));

        query.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        //query.setCodeFoldingEnabled(true);
        sql2003Completion(); // adds a completion provider for SQL2003
        editorExtensions();
        fileDrops();  // we add Robert Harders filedrop Stuff to the SQL-Monitor

        RTextScrollPane sp = new RTextScrollPane(query);

        //panel.add(new JScrollPane(query), gbc);
        //panel.add(query, gbc);
        panel.add(sp, gbc);

        return panel;

    }

    private String getSavePath() {

        String directory = System.getProperty("admin.work");
        if (directory == null) {
            directory = System.getProperty("user.home");
        }

        directory = directory + File.separator + "sqladmin";
        File dir = new File(directory);
        if (!dir.exists()) {
            dir.mkdir();
        }

        return directory;
    }

    private String lastAutoSaveContent = "";

    // rename a file and if the desitnation exists, delete the destination file before renaming
    private boolean renameFile(String origName, String newName) {
        boolean success = false;

        //logger.log(Level.INFO, "renaming {0} to {1}", new Object[]{origName, newName});
        File f = new File(origName);
        if (f.exists()) {
            File f2 = new File(newName);
            if (f2.exists()) {
                f2.delete();
            }
            success = f.renameTo(new File(newName));
        }
        return success;
    }

    private void autoSave() {

        StringBuilder fileName = new StringBuilder();
        StringBuilder filePath = new StringBuilder();
        filePath.append(getSavePath()).append(File.separator).append("autosave");

        File dir = new File(filePath.toString());
        if (!dir.exists()) {
            dir.mkdir();
        }

        if ((query.getText().length() > 1) && (!getLastAutoSaveContent().equals(query.getText()))) {

            fileName.append(File.separator).append("SQL");
            String datenbankName = dbInfo.getDatabase();
            try {
                if (datenbankName == null) {
                    datenbankName = "db-AutoSave";
                }
                datenbankName = datenbankName.replaceAll("[:/\\\\]", "_");  // to avoid conflict with the Filesystem
            } catch (NullPointerException npe) {
                datenbankName = "db-autosave";
                logger.log(Level.WARNING, "Error whil detecting DB-name: {0}", npe.getMessage());
            }
            if (datenbankName.length() > 1) {
                fileName.append("-").append(datenbankName);
            }

            // does this file exist?
            File f1 = new File(filePath.toString() + fileName.toString() + ".sql");
            String renameThis = "";

            if (f1.exists()) {
                for (int i = NUMBER_OF_AUTOSAVE; i > 1; i--) {

                    String ext = "-" + Integer.toString(i) + ".sql";
                    String exd = "-" + Integer.toString(i - 1) + ".sql";

                    // delete this file, if it exists
                    File delete = new File(filePath.toString() + fileName.toString() + ext);
                    if (delete.exists()) {
                        delete.delete();
                    }

                    File f2 = new File(filePath.toString() + fileName.toString() + exd);
                    f2.renameTo(new File(filePath.toString() + fileName.toString() + ext));
                    renameThis = filePath.toString() + fileName.toString() + exd;

                }
            }
            // do the first file
            f1.renameTo(new File(renameThis));

            // and now, save it to the latest version           
            try {
                File outputFile = new File(filePath.toString() + fileName.toString() + ".sql");
                PrintWriter out = new PrintWriter(new FileOutputStream(outputFile));
                out.print(query.getText());
                out.flush();
                out.close();
                setLastAutoSaveContent(query.getText());
                logger.log(Level.INFO, "autosaved your query content to file {0}", outputFile.getAbsolutePath());
            } catch (FileNotFoundException e) {
                logger.log(Level.WARNING, "exception while saving query {0}", e.getMessage());
                JOptionPane.showMessageDialog(null, "exception while saving query " + e.getMessage());
            }

        }
    }

    // we save the query
    public void saveQuery() {

        if (query.getText().length() > 1) {
            String directory = System.getProperty("admin.work");
            StringBuffer fileName = new StringBuffer();
            if (directory == null) {
                directory = System.getProperty("user.home");
            }

            directory = directory + File.separator + "sqladmin";
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdir();
            }

            Calendar cal = Calendar.getInstance();
            fileName.append(directory).append(File.separator).append("SQL");

            String dbName = dbInfo.getDatabase();
            try {
                if (dbName == null) {
                    dbName = "db-AutoSave";
                }
                dbName = dbName.replaceAll("[:/\\\\]", "_");  // to avoid conflict with the Filesystem
                fileName.append("-").append(dbName);
            } catch (NullPointerException npe) {
                dbName = "db-autosave";
                logger.log(Level.WARNING, "Error whil detecting DB-name: {0}", npe.getMessage());
            }

            SimpleDateFormat sdg = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            fileName.append("_").append(sdg.format(cal.getTimeInMillis()));
            /*
            fileName.append("-").append(Integer.toString(cal.get(cal.YEAR)));
            int month = cal.get(cal.MONTH) + 1;
            fileName.append("-").append(Integer.toString(month));
            fileName.append("-").append(Integer.toString(cal.get(cal.DAY_OF_MONTH)));
            fileName.append("--").append(Integer.toString(cal.get(cal.HOUR_OF_DAY)));
            fileName.append("-").append(Integer.toString(cal.get(cal.MINUTE)));
            fileName.append("-").append(Integer.toString(cal.get(cal.SECOND)));
             */
            fileName.append(".sql");

            try {
                File outputFile = new File(fileName.toString());
                PrintWriter out = new PrintWriter(new FileOutputStream(outputFile));
                out.print(query.getText());
                out.flush();
                out.close();
                logger.log(Level.INFO, "Saved your query content to file {0}", fileName.toString());
            } catch (FileNotFoundException e) {
                logger.log(Level.WARNING, "exception while saving query {0}", e.getMessage());
            }
        }

    }

    protected void finalize() {
        close();
        saveQuery();
    }

    public void close() {
        queryHistory.close();
        codeForm.close();
        pid.close();
    }

    private JPanel displayPanel() {

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

        /*
         response = new JTextArea(20, 80);
         response.setFont(new Font("Monospaced", Font.PLAIN, 12));
         */
        //panel.add(new JScrollPane(response), gbc);
        panel.add(response, gbc);

        return panel;

    }

    Container getParentFrame() {
        Container c = new Container();

        if (isStandAlone()) {
            c = getTopLevelAncestor();
        } else {
            try {
                c = SwingUtilities.getAncestorOfClass(Class.forName("javax.swing.JInternalFrame"), (java.awt.Component) this); //get Internal frame when opened in Admin
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
        init();
    }

    public SqlMonitor(t_connect con) {
        setCon(con);
        setSchema("%");
        init();

    }

    public SqlMonitor(t_connect con, String schema) {
        setCon(con);
        setSchema(schema);
        init();
    }
    private DbInfo dbInfo;
    private String dbProductName = "";
    private SqlMonitorSplash sms;
    public Timer timer;
    private long autoSaveTime = 15;  // auto saving time in minutes
    private PropertiesInDerby pid;
    private JPanel sqlMonitorPanel;

    private void init() {
        createSqlWords();

        defaults = javax.swing.UIManager.getDefaults();
        sqlMonitorPanel = new JPanel();
        sqlMonitorPanel.setLayout(new GridBagLayout());

        sms = new SqlMonitorSplash();
        sms.setMessage("loading and preparing SQLMonitor");

        logger.log(Level.INFO, "loading and preparing SQLMonitor");

        queryHistory = new QueryHistory();

        tk = this.getToolkit();

        // Search and Replace Functionality
        context = new SearchContext();
        srg = new SearchReplaceGui();

        searchNreplace();

        dbInfo = new DbInfo(getCon());

        this.setLayout(new BorderLayout());

        //logger.log(Level.INFO, "Message for Debugpurpose");
        tabbedPane = new JTabbedPane();

        //response = new JTextArea(20, 80);
        response = new JTextPane();
        EmptyBorder eb = new EmptyBorder(new Insets(10, 10, 10, 10));
        response.setBorder(eb);
        response.setMargin(new Insets(5, 5, 5, 5));

        response.setEditable(false);
        response.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        response.setForeground(defaults.getColor("TextArea.foreground"));
        response.setBackground(defaults.getColor("TextArea.background"));
        
        query = new RSyntaxTextArea(20, 80);
        query.setForeground(defaults.getColor("TextArea.foreground"));
        query.setBackground(defaults.getColor("TextArea.background"));
        //query.setBackground(defaults.getColor("Label.background"));
        
        sms.setMessage("loading code completion");

        doKeyListener();

        sms.setMessage("loading last directory");
        pid = new PropertiesInDerby();
        lastDirectoryPath = pid.readParameter("SQLMonitorLastDirectoryPath");
        //pid.close();
        //fixDriver();

        sms.setMessage("preparing GUI");

        /**
         * upperPanel = new JPanel(); upperPanel.add(queryPanel());
         *
         */
        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        sqlMonitorPanel.add(toolbar(), gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 1;

        //tabbedPane.add("SQL", queryPanel());
        tabbedPane.addTab("SQL", null, (JPanel) queryPanel(), "Type CTRL-<SPACE> for SQL2003 completion");

        sqlMonitorPanel.add(tabbedPane, gbc);

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

        // we set up a timer, that saves the content every 15 Minutes
        long saveTime = getAutoSaveTime() * 60 * 1000; // 900000 = 15 Minutes

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // logger.log(Level.INFO,"autosave timer startet");                
                autoSave();
            }
        }, 0, saveTime);

        sms.close();

        // the actionListener listening on changes in the SqlTree       
        ActionListener a = new ActionListener() {
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

        sqlTree = new SqlTree(getCon().getCon(), getDatabase(), (DbAutoCompletionProvider) provider, a);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sqlTree, sqlMonitorPanel);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(0.0d);
        splitPane.setToolTipText("DB Objects list");

        this.add(BorderLayout.CENTER, splitPane);

    }

    public void update() {
        this.updateUI();
    }

    private void waitAMoment(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    public void execQuery2() {
        Thread t = new Thread(this, "SQL");
        t.start();
    }

    public void execQuery() {

        /**
         * if it is a query send it to the table, otherwise execute the command
         * and display the result of the command in the status tab
         */
        busyCursor();

        String queryText = "";
        String fullQueryText;

        JPanel lastPanel = new JPanel();
        JPanel statusPanel = new JPanel();

        statusPanel.setLayout(new GridBagLayout());

        boolean bError = false;

        //get query	
        if (query.getSelectionStart() == query.getSelectionEnd()) {
            fullQueryText = query.getText();
        } else {
            fullQueryText = query.getSelectedText();
        }

        if (fullQueryText == "" | fullQueryText == null) {
            defaultCursor();
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
            //response.append(queryText + "\n");
            appendToPane(queryText + "\n", Color.BLACK);

            SqlTab tabPanel = new SqlTextArea();

            // if a query starts with these words, it will return a ResultSet
            if (queryText.toLowerCase().startsWith("select")
                    | queryText.toLowerCase().startsWith("show")
                    | queryText.toLowerCase().startsWith("desc")
                    | queryText.toLowerCase().startsWith("declare")
                    | queryText.toLowerCase().startsWith("values")
                    | queryText.toLowerCase().startsWith("with")) {

                if (resultType != TABLE) {
                    tabPanel = new SqlTextArea(getCon(), queryText);
                } else {
                    tabPanel = new SqlPanel(getCon(), queryText);
                }

                if (tabPanel.getSQLError() != null) {
                    //response.append(tabPanel.getSQLError() + "\n\n");
                    appendToPane(tabPanel.getSQLError() + "\n\n", Color.RED);
                    tk.beep();
                    bError = true;
                } else {
                    tabIndex++;
                    tabbedPane.addTab("Results " + tabIndex, null, (JPanel) tabPanel, queryText);
                    fitSize(tabbedPane.getSize(), tabPanel);
                    //response.append(tabPanel.getNumRows() + " rows returned in Results " + tabIndex + "\n\n");
                    appendToPane(tabPanel.getNumRows() + " rows returned in Results " + tabIndex + "\n\n", Color.BLUE);
                    lastPanel = (JPanel) tabPanel;
                }
            } else {
                getCon();
                if (con.getError() != null) {
                    tk.beep();
                    logger.log(Level.INFO, con.getError());
                } else {
                    try {
                        int records = con.stmt.executeUpdate(queryText);
                        //response.append(Integer.toString(records) + " rows affected\n\n");
                        appendToPane(Integer.toString(records) + " rows affected\n\n", Color.BLUE);
                        lastPanel = statusPanel;
                    } catch (Exception excpt) {
                        //response.append(excpt.toString());
                        appendToPane(excpt.toString(), Color.BLUE);
                        tk.beep();
                        bError = true;
                    }
                }
                con.close();
                logger.log(Level.INFO, "connection closed");
            }
        }

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
        statusPanel.add(new JScrollPane(response), gbc);

        tabbedPane.add("Status", statusPanel);

        tabbedPane.repaint();

        if (bError) {
            lastPanel = statusPanel;
        }
        try {
            tabbedPane.setSelectedComponent(lastPanel);
            defaultCursor();
        } catch (Exception anException) {
            //response.append(anException.toString());
            appendToPane(anException.toString(), Color.BLUE);
            tk.beep();
            bError = true;
            defaultCursor();
        }

    }

    public void fitSize(Dimension d, SqlTab st) {

        Integer intx = new Integer(d.width);
        Integer inty = new Integer(d.height);
        double fx = intx.floatValue() * 0.95;
        double fy = inty.floatValue() * 0.80;
        Double dfx = new Double(fx);
        Double dfy = new Double(fy);
        d.setSize(dfx.intValue(), dfy.intValue());

        st.setViewPortSize(d);

    }

    public static void main(String args[]) {

        System.out.println("Fredy's SQL-Monitor\n"
                + "-------------------- Version 2\n"
                + "is a part of Admin and free software (MIT-License)\n"
                + "(Contribution by David Good)");

        setStandAlone(true);
        if (args.length != 5) {
            System.out.println("Syntax: java SqlMonitor host user password database schema\nWhere schema is avalid schema-pattern (incl. wildcards)");
            System.exit(0);
        }
        JFrame f = new JFrame("SQL-Monitor");
        f.getContentPane().setLayout(new GridBagLayout());
        final SqlMonitor g = new SqlMonitor(args[0], args[1], args[2], args[3], args[4]);

        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        f.getContentPane().add(g, gbc);
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
     * @return the maxRowCount
     */
    public int getMaxRowCount() {
        return maxRowCount;
    }

    /**
     * @param maxRowCount the maxRowCount to set
     */
    public void setMaxRowCount(int maxRowCount) {
        this.maxRowCount = maxRowCount;

        TConnectProperties props = new TConnectProperties();
        props.setDefaultMaxRowCount(Integer.toString(maxRowCount));
        props.save();
    }

    /**
     * @return the lastDirectoryPath
     */
    public String getLastDirectoryPath() {
        return lastDirectoryPath;
    }

    /**
     * @param lastDirectoryPath the lastDirectoryPath to set
     */
    private String memDirPath = null;

    public void setLastDirectoryPath(String lastDirectoryPath) {
        if ((lastDirectoryPath != null) && (memDirPath != lastDirectoryPath)) {
            this.lastDirectoryPath = lastDirectoryPath;
            memDirPath = lastDirectoryPath;
            // saving last position for fileLoader getLastDirectoryPath()
            //PropertiesInDerby pid = new PropertiesInDerby();
            pid.saveParameter("SQLMonitorLastDirectoryPath", getLastDirectoryPath());
            //pid.close();
            //fixDriver();
        }

    }

    /**
     * @return the dbProductName
     */
    public String getDbProductName() {
        return dbProductName;
    }

    /**
     * @param dbProductName the dbProductName to set
     */
    public void setDbProductName(String dbProductName) {
        this.dbProductName = dbProductName;
    }

    /**
     * @return the background
     */
    public Color getBackground() {
        return background;
    }

    /**
     * @param background the background to set
     */
    public void setBackground(Color background) {
        this.background = background;
    }

    /**
     * @return the foreground
     */
    public Color getForeground() {
        return foreground;
    }

    /**
     * @param foreground the foreground to set
     */
    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    @Override
    public void run() {
        /**
         * if it is a query send it to the table, otherwise execute the command
         * and display the result of the command in the status tab
         */
        busyCursor();

        String queryText = "";
        String fullQueryText;

        JPanel lastPanel = new JPanel();
        JPanel statusPanel = new JPanel();

        statusPanel.setLayout(new GridBagLayout());

        boolean bError = false;

        //get query	
        if (query.getSelectionStart() == query.getSelectionEnd()) {
            fullQueryText = query.getText();
        } else {
            fullQueryText = query.getSelectedText();
        }

        if (fullQueryText == "" | fullQueryText == null) {
            defaultCursor();
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

        // write query to history
        queryHistory.write(getDatabase(), fullQueryText);

        while (fqt.hasMoreTokens()) {

            queryText = fqt.nextToken().trim();
            if (queryText.length() > 0) {

                //response.append(queryText + "\n");
                appendToPane(queryText + "\n", Color.BLACK);

                logger.log(Level.FINE, "executing Query on " + getDatabase());

                /*
                 if ((!isComment(queryText))) {
                 queryHistory.write(getDatabase(), queryText);  // this writes every single query to the history
                 }
                 */
                SqlTab tabPanel = new SqlTextArea();

                if (queryText.toLowerCase().startsWith("select")
                        | queryText.toLowerCase().startsWith("set")
                        | queryText.toLowerCase().startsWith("show")
                        | queryText.toLowerCase().startsWith("desc")
                        | queryText.toLowerCase().startsWith("declare")
                        | queryText.toLowerCase().startsWith("values")
                        | queryText.toLowerCase().startsWith("with")
                        | queryText.toLowerCase().startsWith("sp_")
                        | queryText.toLowerCase().startsWith("exec")) {

                    if (resultType != TABLE) {
                        tabPanel = new SqlTextArea(getCon(), queryText);
                    } else {
                        tabPanel = new SqlPanel(getCon(), queryText);
                    }

                    if (tabPanel.getSQLError() != null) {
                        //response.append(tabPanel.getSQLError() + "\n\n");
                        appendToPane(tabPanel.getSQLError() + "\n\n", Color.RED);
                        tk.beep();
                        bError = true;
                    } else {
                        tabIndex++;
                        JPanel resultPanel = new JPanel();
                        resultPanel.setLayout(new BorderLayout());
                        resultPanel.add(BorderLayout.CENTER, (JPanel) tabPanel);
                        JPanel resultStatusPanel = new JPanel();
                        resultStatusPanel.setLayout(new FlowLayout());
                        resultStatusPanel.setBackground(Color.WHITE);
                        resultStatusPanel.setForeground(Color.LIGHT_GRAY);
                        resultStatusPanel.setFont(resultStatusPanel.getFont().deriveFont(10f));

                        JLabel statusLabel = new JLabel(tabPanel.getNumRows() + " rows returned " + tabPanel.getStatusLine());
                        statusLabel.setFont(statusLabel.getFont().deriveFont(10f));
                        resultStatusPanel.add(statusLabel);
                        resultPanel.add(BorderLayout.SOUTH, resultStatusPanel);

                        //tabbedPane.addTab("Results " + tabIndex, null, (JPanel) tabPanel, queryText);
                        tabbedPane.addTab("Results " + tabIndex, null, (JPanel) resultPanel, queryText);
                        fitSize(tabbedPane.getSize(), tabPanel);
                        //response.append(tabPanel.getNumRows() + " rows returned in Results " + tabIndex + "\n\n");
                        appendToPane(tabPanel.getNumRows() + " rows returned in Results " + tabIndex + "\n\n", Color.BLUE);
                        //lastPanel = (JPanel) tabPanel;
                        lastPanel = (JPanel) resultPanel;
                    }
                } else {
                    getCon();
                    if (con.getError() != null) {
                        tk.beep();
                    } else {
                        DateTime startTime = new DateTime();
                        try {
                            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

                            int records = con.stmt.executeUpdate(queryText);
                            DateTime endTime = new DateTime();
                            Duration dur = new Duration(startTime, endTime);
                            //response.append(Integer.toString(records) + " rows affected in " + dur.getMillis()+ " ms\n\n");
                            appendToPane(Integer.toString(records) + " rows affected in " + dur.getMillis() + " ms\n\n", Color.BLUE);

                            lastPanel = statusPanel;
                        } catch (Exception excpt) {
                            //response.append(excpt.toString());
                            appendToPane(excpt.toString(), Color.RED);
                            DateTime endTime = new DateTime();
                            Duration dur = new Duration(startTime, endTime);
                            //response.append("\n in " + dur.getMillis() + " ms");
                            appendToPane("\nin " + dur.getMillis() + " ms", Color.BLUE);
                            tk.beep();
                            bError = true;
                        }
                    }
                    con.close();
                }
            }
        }

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
        statusPanel.add(new JScrollPane(response), gbc);

        tabbedPane.add("Status", statusPanel);

        tabbedPane.repaint();

        if (bError) {
            lastPanel = statusPanel;
        }
        try {
            tabbedPane.setSelectedComponent(lastPanel);
            defaultCursor();
        } catch (Exception anException) {
            if (!anException.toString().equalsIgnoreCase("java.lang.IllegalArgumentException: component not found in tabbed pane")) {
                //response.append(anException.toString());
                appendToPane(anException.toString(), Color.RED);
                tk.beep();
                bError = true;
            }
            defaultCursor();
        }

    }

    /*
     Find out if the query starts with a comment
     */
    private boolean isComment(String q) {
        if ((q.trim().startsWith("--"))
                || (q.trim().startsWith("# "))
                || (q.trim().startsWith("/*--"))) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @return the lastAutoSaveContent
     */
    public String getLastAutoSaveContent() {
        return lastAutoSaveContent;
    }

    /**
     * @param lastAutoSaveContent the lastAutoSaveContent to set
     */
    public void setLastAutoSaveContent(String lastAutoSaveContent) {
        this.lastAutoSaveContent = lastAutoSaveContent;
    }

    /**
     * @return the autoSaveTime
     */
    public long getAutoSaveTime() {
        return autoSaveTime;
    }

    /**
     * @param autoSaveTime the autoSaveTime to set
     */
    public void setAutoSaveTime(long autoSaveTime) {
        this.autoSaveTime = autoSaveTime;
        logger.log(Level.INFO, "Changed autosave time to {0} Minutes", autoSaveTime);
        // we set up a timer, that saves the content every 15 Minutes
        long saveTime = getAutoSaveTime() * 60 * 1000; // 900000 = 15 Minutes
        timer.cancel();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // logger.log(Level.INFO,"autosave timer startet");                
                autoSave();
            }
        }, 0, saveTime);

    }

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
        sqlWords = new ArrayList();
        sqlWords.add("ADD");
        sqlWords.add("ALL");
        sqlWords.add("ALLOCATE");
        sqlWords.add("ALTER");
        sqlWords.add("AND");
        sqlWords.add("ANY");
        sqlWords.add("ARE");
        sqlWords.add("ARRAY");
        sqlWords.add("AS");
        sqlWords.add("ASENSITIVE");
        sqlWords.add("ASYMMETRIC");
        sqlWords.add("AT");
        sqlWords.add("ATOMIC");
        sqlWords.add("AUTHORIZATION");
        sqlWords.add("BEGIN");
        sqlWords.add("BETWEEN");
        sqlWords.add("BIGINT");
        sqlWords.add("BINARY");
        sqlWords.add("BLOB");
        sqlWords.add("BOOLEAN");
        sqlWords.add("BOTH");
        sqlWords.add("BY");
        sqlWords.add("CALL");
        sqlWords.add("CALLED");
        sqlWords.add("CASCADED");
        sqlWords.add("CASE");
        sqlWords.add("CAST");
        sqlWords.add("CHAR");
        sqlWords.add("CHARACTER");
        sqlWords.add("CHECK");
        sqlWords.add("CLOB");
        sqlWords.add("CLOSE");
        sqlWords.add("COLLATE");
        sqlWords.add("COLUMN");
        sqlWords.add("COMMIT");
        sqlWords.add("CONDITION");
        sqlWords.add("CONNECT");
        sqlWords.add("CONSTRAINT");
        sqlWords.add("CONTINUE");
        sqlWords.add("CORRESPONDING");
        sqlWords.add("CREATE");
        sqlWords.add("CROSS");
        sqlWords.add("CUBE");
        sqlWords.add("CURRENT");
        sqlWords.add("CURRENT_DATE");
        sqlWords.add("CURRENT_DEFAULT_TRANSFORM_GROUP");
        sqlWords.add("CURRENT_PATH");
        sqlWords.add("CURRENT_ROLE");
        sqlWords.add("CURRENT_TIME");
        sqlWords.add("CURRENT_TIMESTAMP");
        sqlWords.add("CURRENT_TRANSFORM_GROUP_FOR_TYPE");
        sqlWords.add("CURRENT_USER");
        sqlWords.add("CURSOR");
        sqlWords.add("CYCLE");
        sqlWords.add("DATE");
        sqlWords.add("DAY");
        sqlWords.add("DEALLOCATE");
        sqlWords.add("DEC");
        sqlWords.add("DECIMAL");
        sqlWords.add("DECLARE");
        sqlWords.add("DEFAULT");
        sqlWords.add("DELETE");
        sqlWords.add("DEREF");
        sqlWords.add("DESCRIBE");
        sqlWords.add("DETERMINISTIC");
        sqlWords.add("DISCONNECT");
        sqlWords.add("DISTINCT");
        sqlWords.add("DO");
        sqlWords.add("DOUBLE");
        sqlWords.add("DROP");
        sqlWords.add("DYNAMIC");
        sqlWords.add("EACH");
        sqlWords.add("ELEMENT");
        sqlWords.add("ELSE");
        sqlWords.add("ELSEIF");
        sqlWords.add("END");
        sqlWords.add("ESCAPE");
        sqlWords.add("EXCEPT");
        sqlWords.add("EXEC");
        sqlWords.add("EXECUTE");
        sqlWords.add("EXISTS");
        sqlWords.add("EXIT");
        sqlWords.add("EXTERNAL");
        sqlWords.add("FALSE");
        sqlWords.add("FETCH");
        sqlWords.add("FILTER");
        sqlWords.add("FLOAT");
        sqlWords.add("FOR");
        sqlWords.add("FOREIGN");
        sqlWords.add("FREE");
        sqlWords.add("FROM");
        sqlWords.add("FULL");
        sqlWords.add("FUNCTION");
        sqlWords.add("GET");
        sqlWords.add("GLOBAL");
        sqlWords.add("GRANT");
        sqlWords.add("GROUP");
        sqlWords.add("GROUPING");
        sqlWords.add("HANDLER");
        sqlWords.add("HAVING");
        sqlWords.add("HOLD");
        sqlWords.add("HOUR");
        sqlWords.add("IDENTITY");
        sqlWords.add("IF");
        sqlWords.add("IMMEDIATE");
        sqlWords.add("IN");
        sqlWords.add("INDICATOR");
        sqlWords.add("INNER");
        sqlWords.add("INOUT");
        sqlWords.add("INPUT");
        sqlWords.add("INSENSITIVE");
        sqlWords.add("INSERT");
        sqlWords.add("INT");
        sqlWords.add("INTEGER");
        sqlWords.add("INTERSECT");
        sqlWords.add("INTERVAL");
        sqlWords.add("INTO");
        sqlWords.add("IS");
        sqlWords.add("ITERATE");
        sqlWords.add("JOIN");
        sqlWords.add("LANGUAGE");
        sqlWords.add("LARGE");
        sqlWords.add("LATERAL");
        sqlWords.add("LEADING");
        sqlWords.add("LEAVE");
        sqlWords.add("LEFT");
        sqlWords.add("LIKE");
        sqlWords.add("LOCAL");
        sqlWords.add("LOCALTIME");
        sqlWords.add("LOCALTIMESTAMP");
        sqlWords.add("LOOP");
        sqlWords.add("MATCH");
        sqlWords.add("MEMBER");
        sqlWords.add("MERGE");
        sqlWords.add("METHOD");
        sqlWords.add("MINUTE");
        sqlWords.add("MODIFIES");
        sqlWords.add("MODULE");
        sqlWords.add("MONTH");
        sqlWords.add("MULTISET");
        sqlWords.add("NATIONAL");
        sqlWords.add("NATURAL");
        sqlWords.add("NCHAR");
        sqlWords.add("NCLOB");
        sqlWords.add("NEW");
        sqlWords.add("NO");
        sqlWords.add("NONE");
        sqlWords.add("NOT");
        sqlWords.add("NULL");
        sqlWords.add("NUMERIC");
        sqlWords.add("OF");
        sqlWords.add("OLD");
        sqlWords.add("ON");
        sqlWords.add("ONLY");
        sqlWords.add("OPEN");
        sqlWords.add("OR");
        sqlWords.add("ORDER");
        sqlWords.add("OUT");
        sqlWords.add("OUTER");
        sqlWords.add("OUTPUT");
        sqlWords.add("OVER");
        sqlWords.add("OVERLAPS");
        sqlWords.add("PARAMETER");
        sqlWords.add("PARTITION");
        sqlWords.add("PRECISION");
        sqlWords.add("PREPARE");
        sqlWords.add("PRIMARY");
        sqlWords.add("PROCEDURE");
        sqlWords.add("RANGE");
        sqlWords.add("READS");
        sqlWords.add("REAL");
        sqlWords.add("RECURSIVE");
        sqlWords.add("REF");
        sqlWords.add("REFERENCES");
        sqlWords.add("REFERENCING");
        sqlWords.add("RELEASE");
        sqlWords.add("REPEAT");
        sqlWords.add("RESIGNAL");
        sqlWords.add("RESULT");
        sqlWords.add("RETURN");
        sqlWords.add("RETURNS");
        sqlWords.add("REVOKE");
        sqlWords.add("RIGHT");
        sqlWords.add("ROLLBACK");
        sqlWords.add("ROLLUP");
        sqlWords.add("ROW");
        sqlWords.add("ROWS");
        sqlWords.add("SAVEPOINT");
        sqlWords.add("SCOPE");
        sqlWords.add("SCROLL");
        sqlWords.add("SEARCH");
        sqlWords.add("SECOND");
        sqlWords.add("SELECT");
        sqlWords.add("SENSITIVE");
        sqlWords.add("SESSION_USER");
        sqlWords.add("SET");
        sqlWords.add("SIGNAL");
        sqlWords.add("SIMILAR");
        sqlWords.add("SMALLINT");
        sqlWords.add("SOME");
        sqlWords.add("SPECIFIC");
        sqlWords.add("SPECIFICTYPE");
        sqlWords.add("SQL");
        sqlWords.add("SQLEXCEPTION");
        sqlWords.add("SQLSTATE");
        sqlWords.add("SQLWARNING");
        sqlWords.add("START");
        sqlWords.add("STATIC");
        sqlWords.add("SUBMULTISET");
        sqlWords.add("SYMMETRIC");
        sqlWords.add("SYSTEM");
        sqlWords.add("SYSTEM_USER");
        sqlWords.add("TABLE");
        sqlWords.add("TABLESAMPLE");
        sqlWords.add("THEN");
        sqlWords.add("TIME");
        sqlWords.add("TIMESTAMP");
        sqlWords.add("TIMEZONE_HOUR");
        sqlWords.add("TIMEZONE_MINUTE");
        sqlWords.add("TO");
        sqlWords.add("TRAILING");
        sqlWords.add("TRANSLATION");
        sqlWords.add("TREAT");
        sqlWords.add("TRIGGER");
        sqlWords.add("TRUE");
        sqlWords.add("UNDO");
        sqlWords.add("UNION");
        sqlWords.add("UNIQUE");
        sqlWords.add("UNKNOWN");
        sqlWords.add("UNNEST");
        sqlWords.add("UNTIL");
        sqlWords.add("UPDATE");
        sqlWords.add("USER");
        sqlWords.add("USING");
        sqlWords.add("VALUE");
        sqlWords.add("VALUES");
        sqlWords.add("VARCHAR");
        sqlWords.add("VARYING");
        sqlWords.add("WHEN");
        sqlWords.add("WHENEVER");
        sqlWords.add("WHERE");
        sqlWords.add("WHILE");
        sqlWords.add("WINDOW");
        sqlWords.add("WITH");
        sqlWords.add("WITHIN");
        sqlWords.add("WITHOUT");
        sqlWords.add("YEAR");

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
        JLabel jLabelAT = new JLabel("Autosave interval in min.");
        JTextField autoSaveTime = new JTextField(2);

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

            JPanel panel3 = new JPanel();
            panel3.setLayout(new FlowLayout());
            panel3.add(txtSeparator);
            panel3.add(jLabel1);

            autoSaveTime.setText(((Long) SqlMonitor.this.getAutoSaveTime()).toString());
            autoSaveTime.addActionListener(new ActionListener() {
                // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
                public void actionPerformed(ActionEvent e) {
                    SqlMonitor.this.setAutoSaveTime((new Long(autoSaveTime.getText())).longValue());
                }
            });

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
                    SqlMonitor.this.setAutoSaveTime((new Long(autoSaveTime.getText())).longValue());

                    if (rbText.isSelected()) {
                        SqlMonitor.this.resultType = SqlMonitor.this.MULTIPLE_TEXT;
                    } else {
                        SqlMonitor.this.resultType = SqlMonitor.this.TABLE;
                    }

                    SqlPrefForm.this.dispose();

                }
            });

            JPanel panel4 = new JPanel();
            panel4.setLayout(new FlowLayout());
            panel4.add(cbOK);
            panel4.add(cbCancel);

            //jPanel1.add(txtSeparator, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 10, 0));
            //jPanel1.add(jLabel1, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
            jPanel1.add(panel3, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 10), 15, 0));
            jPanel1.add(cbRetainStatus, new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
            jPanel1.add(cbRetainTabs, new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
            jPanel1.add(buttonPanel, new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 20, 0, 0), 0, 0));
            //jPanel1.add(cbCancel, new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 10, 10, 20), 0, 0));
            //jPanel1.add(cbOK, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 10), 15, 0));
            jPanel1.add(panel4, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 10), 15, 0));
            JPanel panel2 = new JPanel();
            panel2.setLayout(new FlowLayout());
            panel2.setBorder(new EtchedBorder());
            panel2.add(jLabelAT);
            panel2.add(autoSaveTime);
            jPanel1.add(panel2, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 10), 15, 0));

        }
    }

    /*
      got this from here: http://stackoverflow.com/questions/9650992/how-to-change-text-color-in-the-jtextarea
     */
    private void appendToPane(String msg, Color c) {

        response.setEditable(true);

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = response.getDocument().getLength();
        response.setCaretPosition(len);
        response.setCharacterAttributes(aset, false);
        response.replaceSelection(msg);

        response.updateUI();
        response.setVisible(true);

        response.setEditable(false);
    }

}
