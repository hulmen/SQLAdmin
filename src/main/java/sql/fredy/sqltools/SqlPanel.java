package sql.fredy.sqltools;

/**
 * SqlPanel is part of David Good's contribution to Admin
 *
 *
 * This software is part of the Admin-Framework * Admin is a Tool around
 * JDBC-enabled SQL-Databases to do basic jobs for DB-Administrations, as create
 * / delete / alter and query tables it also creates indices and generates
 * simple Java-Code to access DBMS-tables and exports data into various formats
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
import sql.fredy.ui.CloseableFrame;
import sql.fredy.ui.QueryCode;
import sql.fredy.ui.TextEditor;
import sql.fredy.ui.FieldNameType;
import sql.fredy.ui.Transpose;
import sql.fredy.ui.TransposeRow;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
//import javax.swing.SwingUtilities;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.print.PrinterException;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.border.EtchedBorder;
import javax.swing.event.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import sql.fredy.connection.DataSource;
import sql.fredy.metadata.FieldTypeInfo;
import sql.fredy.share.FindResultTable;
import sql.fredy.share.JdbcTableIntegerRenderer;
import sql.fredy.share.JdbcTableNumberRenderer;
import sql.fredy.share.JdbcTableStringRenderer;
import sql.fredy.share.JdbcTableDateRenderer;
import sql.fredy.share.JdbcTableTimestampRenderer;
import sql.fredy.share.JdbcTable;
import sql.fredy.share.JdbcTableSmallIntRenderer;
import sql.fredy.share.JdbcTableTimeRenderer;
import sql.fredy.share.JdbcTableTinyIntRenderer;
import sql.fredy.share.TableFindResult;
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.TableColumnAdjuster;

//public class SqlPanel extends JPanel implements ActionListener, ListSelectionListener, SqlTab {
public class SqlPanel extends JPanel implements ListSelectionListener, SqlTab {

    /**
     * @return the startTime
     */
    public DateTime getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public DateTime getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    private boolean tablePanel;
    private int numRows;
    private DateTime startTime, endTime;
    private HashMap<Integer, FieldTypeInfo> fieldTypes = null;
    private String quote = "";

    /**
     * @return the dt
     */
    public JdbcTable getDt() {
        return dt;
    }

    /**
     * @param dt the dt to set
     */
    public void setDt(JdbcTable dt) {
        this.dt = dt;
    }
//public class SqlPanel extends JPanel implements ActionListener, SqlTab {

    public JTable tableView;
    public JScrollPane scrollpane;
    private int iRows;
    private String SQLError;

    private ResultSetMetaData rsmd;
    private Logger logger = Logger.getLogger("sql.fredy.sqltools");

    private ArrayList<FieldNameType> fieldNameTypes;

    private Connection con = null;

    ListSelectionModel listSelectionModel;
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

    private String password;

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

    private String query;

    /**
     * Get the value of query.
     *
     * @return Value of query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the value of dbTable.
     *
     * @param v Value to assign to dbTable.
     */
    public void setQuery(String v) {
        this.query = v;
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

    public SqlPanel() {

    }

    public SqlPanel(String host, String user, String password, String db, String query) {

        setHost(host);
        setUser(user);
        setPassword(password);
        setDatabase(db);
        setQuery(query);
        init();
    }

    public SqlPanel(String query) {

        setQuery(query);
        init();
    }

    public SqlPanel(String query, Connection con) {

        setCon(con);
        setQuery(query);
        init();
    }

    private void createFieldList() {
        fieldNameTypes = new ArrayList();
        try {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                FieldNameType fnt = new FieldNameType();
                fnt.setName(firstLower(spaceFixer(rsmd.getColumnLabel(i))));
                fnt.setType(getColumnClass(i));
                fnt.setColumnDataType(rsmd.getColumnType(i));
                fnt.setOriginalColumnName(rsmd.getColumnLabel(i));
                fnt.setLength(rsmd.getColumnDisplaySize(i));
                fnt.setVendorTypeName(rsmd.getColumnTypeName(i));
                try {
                    fnt.setTable(rsmd.getTableName(i).toLowerCase());
                    fnt.setLength(rsmd.getPrecision(i));
                    fnt.setScale(rsmd.getScale(i));
                } catch (Exception e1) {
                    fnt.setTable("unknonwTable");
                }
                if (fnt.getTable().length() < 1) {
                    fnt.setTable("table");
                }
                try {
                    fnt.setDb(spaceFixer(rsmd.getCatalogName(1).toLowerCase()));
                } catch (Exception e2) {
                    fnt.setDb("canNotDetectDB");
                }
                if (fnt.getDb().length() < 1) {
                    fnt.setDb("db");
                }
                fieldNameTypes.add(fnt);
            }
        } catch (SQLException sqlex) {
            // trage hier deine Fehlermeldung ein
            logger.log(Level.WARNING, "Error while collecting info for  codegeneration : {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
        } catch (Exception e) {
            logger.log(Level.WARNING, "probably no result generated. {0}", e.getMessage());
            //e.printStackTrace();
        }

    }

    private String statusLine = "";

    private JdbcTable dt;

    private void init() {

        this.setLayout(new GridBagLayout());

        DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter dtf1 = DateTimeFormat.forPattern("HH:mm:ss.sss");
        SimpleDateFormat sdt = new SimpleDateFormat("HH:mm:ss.sss");
        setStartTime(new DateTime());

        //JDBCAdapter dt = new JDBCAdapter(getCon());
        setDt(new JdbcTable());
        getDt().setStandalone(false);
        //getDt().setCon(getCon());

        if (getDt().executeQuery(getQuery())) { 

            setEndTime(new DateTime());

            setTablePanel(getDt().isTablePanel());
            setNumRows(getDt().getRowsExecuted());

            if (isTablePanel()) {
                rsmd = getDt().getMetaData();

                //createFieldList();
                fieldNameTypes = getDt().getFieldNameTypes();

                try {
                    iRows = getDt().getNumRows();
                } catch (Exception e) {
                    iRows = 0;
                }
                SQLError = getDt().getSQLError();
                tableView = new JTable(getDt()) {
                    public TableCellRenderer getCellRenderer(int row, int column) {
                        if (getDt().getColumnType(column) == java.sql.Types.TIMESTAMP) {
                            return new JdbcTableTimestampRenderer();
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.TIMESTAMP_WITH_TIMEZONE) {
                            return new JdbcTableTimestampRenderer();
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.DATE) {
                            return new JdbcTableDateRenderer();
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.TIME) {
                            return new JdbcTableTimeRenderer();
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.TIME_WITH_TIMEZONE) {
                            return new JdbcTableTimeRenderer();
                        }

                        if (getDt().getColumnType(column) == java.sql.Types.INTEGER) {
                            return new JdbcTableIntegerRenderer();
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.BIGINT) {
                            return new JdbcTableIntegerRenderer();
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.SMALLINT) {
                            return new JdbcTableSmallIntRenderer();
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.TINYINT) {
                            boolean signedTinyInt = false;  // used to determine if a TINYInt is signed or not
                            try {
                                signedTinyInt = getDt().getRsmd().isSigned(column + 1);
                            } catch (SQLException e) {
                                logger.log(Level.FINE, "Exception while finding out if number is signed or not {0}", e.getMessage());
                                signedTinyInt = false;
                            } catch (Exception e) {
                                logger.log(Level.WARNING, "Exception while finding out if number is signed or not {0}", e.getMessage());
                                logger.log(Level.INFO, "Column Number: {0}", column + 1);
                            }
                            return new JdbcTableTinyIntRenderer(signedTinyInt);
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.DECIMAL) {
                            return new JdbcTableNumberRenderer();
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.DOUBLE) {
                            return new JdbcTableNumberRenderer();
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.FLOAT) {
                            return new JdbcTableNumberRenderer();
                        }
                        if (getDt().getColumnType(column) == java.sql.Types.NUMERIC) {
                            return new JdbcTableNumberRenderer();
                        }
                        if ((getDt().getColumnType(column) == java.sql.Types.LONGNVARCHAR)
                                || (getDt().getColumnType(column) == java.sql.Types.LONGVARCHAR)
                                || (getDt().getColumnType(column) == java.sql.Types.NCHAR)
                                || (getDt().getColumnType(column) == java.sql.Types.CHAR)
                                || (getDt().getColumnType(column) == java.sql.Types.NVARCHAR)
                                || (getDt().getColumnType(column) == java.sql.Types.VARCHAR)) {
                            return new JdbcTableStringRenderer();
                        }

                        return super.getCellRenderer(row, column);

                    }
                };

                tableView.getTableHeader().setReorderingAllowed(false);;
                tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

                //tableView.setAutoCreateRowSorter(true);
                tableView.setRowSorter(getDt().getRowSorter());

                tableView.setColumnSelectionAllowed(true);
                tableView.setCellSelectionEnabled(true);
                //tableView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                //tableView.getSelectionModel().addListSelectionListener(new SharedListSelectionHandler());
                listSelectionModel = tableView.getSelectionModel();

                listSelectionModel.setValueIsAdjusting(true);

                listSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

                listSelectionModel.addListSelectionListener(this);
                tableView.setSelectionModel(listSelectionModel);

                scrollpane = new JScrollPane(tableView);

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

                this.add(scrollpane, gbc);
                //this.add(scrollpane, BorderLayout.CENTER);        

                Duration dur = new Duration(startTime, endTime);

                long millis = dur.getMillis();
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                        TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                        TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

                if (millis
                        <= 10000) {
                    hms = Long.toString(millis) + "ms";
                }

                setStatusLine(
                        " in " + hms + " |  " + startTime.toString(dtf) + " --> " + endTime.toString(dtf));

                mouseThings();

                this.add(popUpMenu());
                doKeyListener();

                TableColumnAdjuster tca = new TableColumnAdjuster(tableView);

                tca.adjustColumns();
            }
        } else {
            setSQLError(getDt().getSQLError());
        }
    }

    public int getNumRows() {
        return iRows;
    }

    public String getSQLError() {
        return SQLError;
    }

    private void setSQLError(String v) {
        SQLError = v;
    }

    public void setViewPortSize(Dimension d) {
        try {
            tableView.setPreferredScrollableViewportSize(d);
        } catch (Exception e) {

        }
    }

    private String spaceFixer(String s) {
        s = s.trim();
        s = s.replaceAll(" ", "_");
        return s;
    }

    private String nameFixer(String s) {
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
        try {
            s = s.substring(0, 1).toLowerCase() + s.substring(1);
        } catch (Exception e) {

        }
        return s;
    }

    private void getBean() {
        StringBuilder sb = new StringBuilder();

        sb.append("\n/*\n"
                + "  This Bean is generated by Fredy's SQL-Admin Tool visit http://www.hulmen.ch/admin\n"
                + " \n"
                + "  This is the query the Bean represents:\n");
        sb.append("\n").append(getQuery()).append("\n\n");
        sb.append("\n\n");
        sb.append("\n\n Add your comments, a Description, your license or whatever here\n"
                + "\n"
                + "\n"
                + "*/\n");
        sb.append("\npackage ADD-YOUR-PACKAGE-NAME-HERE\n\n");

        sb.append("import java.sql.Time;\n"
                + "import java.sql.Timestamp;\n"
                + "import java.sql.Date;\n"
                + "import java.math.BigDecimal;\n\n");
        sb.append("public class MY-Cool-Bean {\n\n");

        try {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                sb.append("\nprivate ").append(getColumnClass(i)).append(" ").append(firstLower(spaceFixer(rsmd.getColumnLabel(i)))).append(";");
                sb.append("\n\npublic void set").append(firstUpper(spaceFixer(rsmd.getColumnLabel(i)))).append("(").append(getColumnClass(i)).append(" v){");
                sb.append("\n\tthis.").append(firstLower(spaceFixer(rsmd.getColumnLabel(i)))).append(" = v;\n}");
                sb.append("\n\npublic ").append(getColumnClass(i)).append(" get").append(firstUpper(spaceFixer(rsmd.getColumnLabel(i)))).append("(){");
                sb.append("\n\t return ").append(firstLower(spaceFixer(rsmd.getColumnLabel(i)))).append(";\n}\n");
            }
            sb.append("\n}");
            StringSelection ss = new StringSelection(sb.toString());
            Toolkit tk = this.getToolkit();
            tk.getSystemClipboard().setContents(ss, ss);
        } catch (SQLException sqlex) {
            // trage hier deine Fehlermeldung ein
            logger.log(Level.WARNING, "Error while generating coder : {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());

        }

    }

    public String getColumnClass(int column) {
        int type;
        try {
            type = rsmd.getColumnType(column);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception in class \ngetColumnClass. Exception is:\n" + e.getMessage() + "\n\n");
            //e.printStackTrace();

            return "String";
        }

        switch (type) {
            case Types.CHAR:
                return "String";
            case Types.VARCHAR:
                return "String";
            case Types.LONGVARCHAR:
                return "String";
            case Types.BIT:
                return "boolean";
            case Types.BOOLEAN:
                return "boolean";
            case Types.TINYINT:
                return "int";
            case Types.SMALLINT:
                return "short";
            case Types.INTEGER:
                return "int";
            case Types.BIGINT:
                return "long";
            case Types.NUMERIC:
                return "BigDecimal";
            case Types.FLOAT:
                return "float";
            case Types.DECIMAL:
                return "float";
            case Types.DOUBLE:
                return "double";
            case Types.REAL:
                return "float";
            case Types.DATE:
                return "java.sql.Date";
            case Types.TIMESTAMP:
                return "java.sql.Timestamp";
            case Types.TIME:
                return "java.sql.Time";
            default:
                return "String";
        }
    }

    private void displayEditor(String s, String title) {
        TextEditor te = new TextEditor(s);

        final CloseableFrame frame = new CloseableFrame(title);
        frame.setExitOnClose(false);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.add(BorderLayout.CENTER, te);
        te.cancel.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        te.ok.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        te.ok.setEnabled(false);

        frame.pack();
        frame.setVisible(true);
    }

    private void zoom(String s) {
        TextEditor te = new TextEditor(s);

        final CloseableFrame frame = new CloseableFrame("Zoom Item");
        frame.setExitOnClose(false);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.add(BorderLayout.CENTER, te);
        te.cancel.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        te.ok.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        te.ok.setEnabled(false);

        frame.pack();
        frame.setVisible(true);
    }

    private JPopupMenu popup;

    private JPopupMenu popUpMenu() {
        popup = new JPopupMenu("Functions");

        JMenu fontMenu = new JMenu("Fontsize");
        JMenuItem font8 = new JMenuItem("8");
        JMenuItem font10 = new JMenuItem("10");
        JMenuItem font12 = new JMenuItem("12");
        JMenuItem font14 = new JMenuItem("14");
        JMenuItem font16 = new JMenuItem("16");
        JMenuItem font18 = new JMenuItem("18");

        fontMenu.add(font8);
        fontMenu.add(font10);
        fontMenu.add(font12);
        fontMenu.add(font14);
        fontMenu.add(font16);
        fontMenu.add(font18);

        font8.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                //tableView.setFont(new Font("Monospaced", Font.PLAIN, 8));
                tableView.setFont(tableView.getFont().deriveFont(8.0f));
            }
        });
        font10.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                //tableView.setFont(new Font("Monospaced", Font.PLAIN, 10));
                tableView.setFont(tableView.getFont().deriveFont(10.0f));
            }
        });
        font12.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                //tableView.setFont(new Font("Monospaced", Font.PLAIN, 12));
                tableView.setFont(tableView.getFont().deriveFont(12.0f));
            }
        });
        font14.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                //tableView.setFont(new Font("Monospaced", Font.PLAIN, 14));
                tableView.setFont(tableView.getFont().deriveFont(14.0f));
            }
        });
        font16.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                //tableView.setFont(new Font("Monospaced", Font.PLAIN, 16));
                tableView.setFont(tableView.getFont().deriveFont(16.0f));
            }
        });
        font18.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                //tableView.setFont(new Font("Monospaced", Font.PLAIN, 18));
                tableView.setFont(tableView.getFont().deriveFont(18.0f));
            }
        });
        JMenuItem reRun = new JMenuItem("run query again");
        JMenuItem zoomItem = new JMenuItem("Zoom");
        JMenuItem transposeItem = new JMenuItem("Transpose selected Rows");
        JMenuItem queryItem = new JMenuItem("Copy query to clipboard");
        JMenuItem queryItem2 = new JMenuItem("Display query");
        JMenuItem queryItem3 = new JMenuItem("Get create table Statement");
        JMenuItem queryItem4 = new JMenuItem("Find");

        queryItem4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchTable();
            }
        });

        reRun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                init();
            }
        });

        popup.add(zoomItem);
        popup.add(transposeItem);
        popup.add(queryItem);
        popup.add(queryItem2);
        popup.add(queryItem3);
        popup.add(queryItem4);
        popup.add(fontMenu);

        zoomItem.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                try {
                    zoom((String) tableView.getValueAt(tableView.getSelectedRow(), tableView.getSelectedColumn()));
                } catch (Exception e1) {
                    zoom((tableView.getValueAt(tableView.getSelectedRow(), tableView.getSelectedColumn())).toString());
                }
            }
        });

        transposeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                transpose();
            }
        });

        queryItem.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                // führe hier den Befehl aus
                StringSelection selection = new StringSelection(getQuery());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, selection);
            }
        });

        queryItem2.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                displayEditor(getQuery(), "Query");
            }
        });

        queryItem3.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                displayEditor(getCreateTableStatement(), "Create Table");
            }
        });

        JMenuItem bean = new JMenuItem("Code");
        bean.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                if (rsmd != null) {
                    QueryCode qc = new QueryCode(getQuery(), fieldNameTypes);
                }
            }
        });

        popup.add(new JSeparator());
        popup.add(bean);

        JMenuItem print = new JMenuItem("Print");
        print.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                try {
                    boolean printed = tableView.print();
                } catch (PrinterException ex) {
                    logger.log(Level.WARNING, "Exception while printing table: " + ex.getMessage());
                }
            }
        });

        popup.add(new JSeparator());
        popup.add(print);

        return popup;
    }

    JTable searchTable = null;
    TableColumn column = null;

    private void searchTable() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JPanel innerpanel = new JPanel();
        innerpanel.setLayout(new GridBagLayout());
        panel.setBorder(new EtchedBorder());

        GridBagConstraints gbc;

        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 0;

        innerpanel.add(new JLabel("Search Pattern"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        JTextField searchPattern = new JTextField(25);
        innerpanel.add(searchPattern, gbc);

        gbc.gridx = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        ImageButton magnify = new ImageButton(null, "magnify.gif", "find it");
        innerpanel.add(magnify, gbc);

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new FlowLayout());
        //ImageButton next = new ImageButton(null, "vcrforward.gif", "find next");
        //ImageButton previous = new ImageButton(null, "vcrback.gif", "find previous");
        ImageButton exit = new ImageButton(null, "exit.gif", "close");

        //lowerPanel.add(next);
        //lowerPanel.add(previous);
        lowerPanel.add(exit);

        panel.add(BorderLayout.NORTH, innerpanel);
        panel.add(BorderLayout.SOUTH, lowerPanel);
        JDialog searchDialog = new JDialog();
        searchDialog.setTitle("Find in Table");
        searchDialog.getContentPane().setLayout(new BorderLayout());
        searchDialog.getContentPane().add(BorderLayout.CENTER, panel);
        searchDialog.pack();
        searchDialog.setLocation(MouseInfo.getPointerInfo().getLocation());
        searchDialog.setVisible(true);

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchDialog.dispose();
            }
        });

        FindResultTable frt = new FindResultTable();

        ActionListener findListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (searchPattern.getText() != null) {

                    busyCursor();
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            ArrayList<TableFindResult> tfr = getDt().search(searchPattern.getText());

                            frt.clearData();

                            try {
                                Component[] components = panel.getComponents();
                                for (int i = 0; i < components.length; i++) {
                                    if (components[i].getName().equalsIgnoreCase("scrollPane")) {
                                        panel.remove(components[i]);
                                        panel.updateUI();
                                        break;
                                    }
                                }

                                /*
                                column = searchTable.getColumnModel().getColumn(0);
                                if (searchTable != null) {
                                    searchTable.updateUI();
                                    column.setPreferredWidth(300);
                                    column = searchTable.getColumnModel().getColumn(1);
                                    column.setPreferredWidth(15);
                                    column = searchTable.getColumnModel().getColumn(2);
                                    column.setPreferredWidth(15);
                                }
                                 */
                            } catch (Exception e) {

                            }
                            frt.setContent(tfr);
                            searchTable = new JTable(frt);

                            column = searchTable.getColumnModel().getColumn(0);
                            column.setPreferredWidth(300);
                            column = searchTable.getColumnModel().getColumn(1);
                            column.setPreferredWidth(15);
                            column = searchTable.getColumnModel().getColumn(2);
                            column.setPreferredWidth(15);
                            searchTable.updateUI();

                            JScrollPane scrollPane = new JScrollPane(searchTable);
                            searchTable.setFillsViewportHeight(true);
                            ListSelectionModel lsm = searchTable.getSelectionModel();
                            lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                            lsm.addListSelectionListener((ListSelectionEvent e1) -> {
                                try {
                                    int s = ((Integer) searchTable.getValueAt(searchTable.getSelectedRow(), 1));
                                    int c = ((Integer) searchTable.getValueAt(searchTable.getSelectedRow(), 2));
                                    //tableView.getSelectionModel().setSelectionInterval(s, c);
                                    //tableView.setRowSelectionInterval(s, s);
                                    tableView.changeSelection(s, c, false, false);
                                    //System.out.println("Select Row " + s + " Col : " + c);                                 
                                } catch (Exception e) {

                                }
                            });

                            searchTable.getModel().addTableModelListener(new TableModelListener() {
                                @Override
                                public void tableChanged(TableModelEvent e) {
                                    column = searchTable.getColumnModel().getColumn(0);
                                    column.setPreferredWidth(300);
                                    column = searchTable.getColumnModel().getColumn(1);
                                    column.setPreferredWidth(15);
                                    column = searchTable.getColumnModel().getColumn(2);
                                    column.setPreferredWidth(15);
                                    searchTable.updateUI();
                                }
                            });

                            scrollPane.setName("scrollPane");
                            panel.add(BorderLayout.CENTER, scrollPane);
                            searchDialog.setResizable(false);
                            searchDialog.pack();
                            searchDialog.validate();
                            searchDialog.repaint();
                            defaultCursor();
                        }
                    });
                } else {
                    tableView.clearSelection();
                }
            }
        };
        searchPattern.addActionListener(findListener);
        magnify.addActionListener(findListener);

    }

    private void waitAMoment(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    private void defaultCursor() {
        tableView.setCursor(Cursor.getDefaultCursor());

    }

    private void busyCursor() {
        tableView.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    }

    private void mouseThings() {

        //final Component component = this;
        final Component component = tableView;

        MouseListener mouseListener = new MouseAdapter() {

            // left mouse button
            public void mouseClicked(MouseEvent e) {
                int leftButton = e.BUTTON1;
                int rightButton = e.BUTTON3;
                int centerButton = e.BUTTON2;

                // 
                if (e.getButton() == leftButton) {
                    if (e.getClickCount() == 2) {
                        try {
                            String v = (String) tableView.getValueAt(tableView.getSelectedRow(), tableView.getSelectedColumn());
                            if (v == null) {
                                v = null;
                            }
                            zoom(v);
                        } catch (Exception e1) {
                            zoom((tableView.getValueAt(tableView.getSelectedRow(), tableView.getSelectedColumn())).toString());
                        }
                    }
                }
                if ((e.getButton() == rightButton) || (e.getButton() == centerButton)) {
                    popup.show(component, e.getX(), e.getY());
                }

            }
        };
        tableView.addMouseListener(mouseListener);

    }

    private void doKeyListener() {

        int inFocus = JComponent.WHEN_IN_FOCUSED_WINDOW;
        InputMap iMap = this.getInputMap(inFocus);
        ActionMap aMap = this.getActionMap();

        KeyStroke ctrlF = KeyStroke.getKeyStroke("control F");
        iMap.put(ctrlF, "ctrl-F");
        KeyStroke ctrlT = KeyStroke.getKeyStroke("control T");
        iMap.put(ctrlT, "ctrl-T");
        KeyStroke ctrlP = KeyStroke.getKeyStroke("control P");
        iMap.put(ctrlP, "ctrl-P");

        KeyStroke ctrlR = KeyStroke.getKeyStroke("control R");
        iMap.put(ctrlR, "ctrl-R");

        Action searchAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchTable();
            }
        };

        Action transposeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transpose();
            }
        };

        Action printAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    boolean printed = tableView.print();
                } catch (PrinterException ex) {
                    logger.log(Level.WARNING, "Exception while printing table: " + ex.getMessage());
                }
            }
        };

        Action rerunAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                init();
            }
        };

        aMap.put("ctrl-F", searchAction);
        aMap.put("ctrl-T", transposeAction);
        aMap.put("ctrl-P", printAction);
        // aMap.put("ctrl-R", rerunAction);

    }

    //Handling List Events
    public void valueChanged(ListSelectionEvent e) {;

        // I do not need this way, because this is with just one click and I need to zoom on doubleclick
        /*
         if (!e.getValueIsAdjusting()) {
         ListSelectionModel lsm = (ListSelectionModel) e.getSource();

         try {
         zoom((String) tableView.getValueAt(tableView.getSelectedRow(), tableView.getSelectedColumn()));
         } catch (Exception e1) {
         zoom((tableView.getValueAt(tableView.getSelectedRow(), tableView.getSelectedColumn())).toString());
         }

         //logger.log(Level.INFO, "Selected Object: " + tableView.getValueAt(tableView.getSelectedRow(), tableView.getSelectedColumn()));
         }
         */
        //setStatusLine(getStatusLine() + " " + String.format("%,d", tableView.getSelectedColumnCount())+ " Columns selected in " + String.format("%,d",tableView.getSelectedRowCount()) + " Rows");
        //this.updateUI();
        // display Selection
        // System.out.println("Selection " + String.format("%,d", tableView.getSelectedColumnCount())+ " / " + String.format("%,d",tableView.getSelectedRowCount()) );
    }

    // handling AWT-Events
    public void actionPerformed(ActionEvent evt) {
        logger.log(Level.INFO, "Selected Object: " + tableView.getValueAt(tableView.getSelectedRow(), tableView.getSelectedColumn()));
    }

    public static void main(String args[]) {
        if (args.length != 5) {
            System.out.println("Syntax: java SqlPanel host user password database query");
        } else {

            SqlPanel f = new SqlPanel(args[0], args[1], args[2], args[3], args[4]);
            JFrame frame = new JFrame("Table");
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add("Center", f);
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
            frame.setVisible(true);
        }

    }

    /**
     * @return the statusLine
     */
    public String getStatusLine() {
        return statusLine;
    }

    /**
     * @param statusLine the statusLine to set
     */
    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;

    }

    class SharedListSelectionHandler implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            //logger.log(Level.INFO,"Selection Handler");            
            int firstIndex = e.getFirstIndex();
            int lastIndex = e.getLastIndex();
            boolean isAdjusting = e.getValueIsAdjusting();

            String m = "Event for indexes "
                    + firstIndex + " - " + lastIndex
                    + "; isAdjusting is " + isAdjusting
                    + "; selected indexes:";
            //logger.log(Level.INFO,m);

            //logger.log(Level.INFO,"Selected Column: " + tableView.getSelectedColumn() + " selected Row: " + tableView.getSelectedRow());
            logger.log(Level.INFO, "Selected Object: " + tableView.getValueAt(tableView.getSelectedRow(), tableView.getSelectedColumn()));

            if (lsm.isSelectionEmpty()) {
                // System.out.println(" <none>");
            } else {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        // logger.log(Level.INFO," Selected Index " + i);
                    }
                }
            }

            // output.setCaretPosition(output.getDocument().getLength());
            setStatusLine(getStatusLine() + " " + String.format("%,d", tableView.getSelectedColumnCount()) + " Columns selected in " + String.format("%,d", tableView.getSelectedRowCount()) + " Rows");
        }
    }

    private void transpose() {
        ArrayList<TransposeRow> transpose;
        ArrayList<ArrayList> bunch = new ArrayList();

        //zoom((String) tableView.getValueAt(tableView.getSelectedRow(), tableView.getSelectedColumn()));
        NumberFormat numberFormat = new DecimalFormat("###,##0.00");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        boolean b;
        java.sql.Date date;
        java.sql.Timestamp timestamp;
        java.sql.Time time;

        // we know already the name and type of the fields
        Iterator<FieldNameType> iter;

        int rows[] = tableView.getSelectedRows();

        for (int i = 0; i < rows.length; i++) {
            transpose = new ArrayList();
            int row = rows[i];
            //logger.log(Level.INFO, "Processing row: {0}", row);
            TransposeRow tr;
            int col = 0;
            iter = fieldNameTypes.iterator();
            while (iter.hasNext()) {
                tr = new TransposeRow();
                FieldNameType f = iter.next();
                tr.setName(f.getOriginalColumnName());

                if (tableView.getValueAt(row, col) != null) {  // what a mess, if the value is NULL

                    //tr.setName(f.getName());
                    tr.setLength(f.getLength());

                    try {
                        switch (f.getColumnDataType()) {
                            case java.sql.Types.ARRAY:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.BIGINT:
                                try {
                                    tr.setContent(((Long) tableView.getValueAt(row, col)).toString());
                                    if (tr.getContent() != null) {
                                        tr.setLength(tr.getContent().length());
                                    } else {
                                        tr.setLength(0);
                                    }
                                } catch (Exception e) {
                                    tr.setContent("NULL");
                                    //tr.setContent(e.getMessage());
                                    tr.setLength(tr.getContent().length());
                                }
                                break;
                            case java.sql.Types.BINARY:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.BIT:
                                b = ((Boolean) tableView.getValueAt(row, col)).booleanValue();
                                if (b) {
                                    tr.setContent("true");
                                } else {
                                    tr.setContent("false");
                                }
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                ;
                                break;
                            case java.sql.Types.BLOB:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.BOOLEAN:
                                b = ((Boolean) tableView.getValueAt(row, col)).booleanValue();
                                if (b) {
                                    tr.setContent("true");
                                } else {
                                    tr.setContent("false");
                                }
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.CHAR:
                                tr.setContent((String) tableView.getValueAt(row, col));
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.CLOB:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.DATALINK:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.DATE:
                                date = (Date) tableView.getValueAt(row, col);
                                tr.setContent(dateFormat.format(date));
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.DECIMAL:
                                try {
                                    BigDecimal bd = (BigDecimal) tableView.getValueAt(row, col);
                                    tr.setContent(numberFormat.format(bd));
                                    if (tr.getContent() != null) {
                                        tr.setLength(tr.getContent().length());
                                    } else {
                                        tr.setLength(0);
                                    }
                                } catch (ClassCastException e) {
                                    String v = (String) tableView.getValueAt(row, col);
                                    //BigDecimal bds = new BigDecimal(v);
                                    tr.setContent(v);
                                    if (tr.getContent() != null) {
                                        tr.setLength(tr.getContent().length());
                                    } else {
                                        tr.setLength(0);
                                    }
                                } catch (Exception e) {
                                    tr.setContent("");
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.DISTINCT:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.DOUBLE:
                                try {
                                    double dv = ((Double) tableView.getValueAt(row, col)).doubleValue();
                                    tr.setContent(numberFormat.format(dv));
                                    if (tr.getContent() != null) {
                                        tr.setLength(tr.getContent().length());
                                    } else {
                                        tr.setLength(0);
                                    }
                                } catch (ClassCastException e) {
                                    float fl = ((Float) tableView.getValueAt(row, col)).floatValue();
                                    tr.setContent(numberFormat.format(fl));
                                    if (tr.getContent() != null) {
                                        tr.setLength(tr.getContent().length());
                                    } else {
                                        tr.setLength(0);
                                    }
                                } catch (Exception e) {
                                    tr.setContent("");
                                    tr.setLength(0);
                                }
                                break;

                            case java.sql.Types.FLOAT:
                                float fl = ((Float) tableView.getValueAt(row, col)).floatValue();
                                tr.setContent(numberFormat.format(fl));
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.INTEGER:
                                try {
                                    tr.setContent(((Integer) tableView.getValueAt(row, col)).toString());
                                    if (tr.getContent() != null) {
                                        tr.setLength(tr.getContent().length());
                                    } else {
                                        tr.setLength(0);
                                    }
                                } catch (ClassCastException e) {
                                    String v = (String) tableView.getValueAt(row, col);
                                    tr.setContent(v);
                                    if (tr.getContent() != null) {
                                        tr.setLength(tr.getContent().length());
                                    } else {
                                        tr.setLength(0);
                                    }
                                } catch (Exception e) {
                                    tr.setContent("");
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.JAVA_OBJECT:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.LONGNVARCHAR:
                                tr.setContent((String) tableView.getValueAt(row, col));
                                tr.setLength(tr.getContent().length());
                                break;
                            case java.sql.Types.LONGVARBINARY:
                                tr.setContent("not displayable content");
                                tr.setLength(tr.getContent().length());
                                break;
                            case java.sql.Types.LONGVARCHAR:
                                tr.setContent((String) tableView.getValueAt(row, col));
                                if (tr.getContent() != null) {
                                    try {
                                        tr.setLength(tr.getContent().length());
                                    } catch (Exception e) {
                                        tr.setLength(0);
                                    }
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.NCHAR:
                                tr.setContent((String) tableView.getValueAt(row, col));

                                try {
                                    tr.setLength(tr.getContent().length());
                                } catch (Exception e) {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.NCLOB:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.NULL:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.NUMERIC:
                                try {
                                    BigDecimal bd2 = (BigDecimal) tableView.getValueAt(row, col);
                                    tr.setContent(numberFormat.format(bd2));
                                    if (tr.getContent() != null) {
                                        tr.setLength(tr.getContent().length());
                                    } else {
                                        tr.setLength(0);
                                    }
                                } catch (Exception e) {
                                    tr.setContent("");
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.NVARCHAR:
                                tr.setContent((String) tableView.getValueAt(row, col));
                                try {
                                    tr.setLength(tr.getContent().length());
                                } catch (Exception e) {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.OTHER:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.REAL:
                                break;
                            case java.sql.Types.REF:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.REF_CURSOR:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.ROWID:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.SMALLINT:
                                tr.setContent(((Integer) tableView.getValueAt(row, col)).toString());
                                tr.setLength(tr.getContent().length());
                                break;
                            case java.sql.Types.SQLXML:
                                tr.setContent((String) tableView.getValueAt(row, col));
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.STRUCT:
                                tr.setContent("not displayable content");
                                tr.setLength(tr.getContent().length());
                                break;
                            case java.sql.Types.TIME:
                                time = (Time) tableView.getValueAt(row, col);
                                if (time != null) {
                                    tr.setContent(timeFormat.format(time));
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setContent(null);
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.TIMESTAMP:
                                timestamp = (Timestamp) tableView.getValueAt(row, col);
                                if (timestamp != null) {
                                    tr.setContent(dateTimeFormat.format(timestamp));
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setContent(null);
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
                                timestamp = (Timestamp) tableView.getValueAt(row, col);
                                if (timestamp != null) {
                                    tr.setContent(dateTimeFormat.format(timestamp));
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setContent(null);
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.TIME_WITH_TIMEZONE:
                                timestamp = (Timestamp) tableView.getValueAt(row, col);
                                if (timestamp != null) {
                                    tr.setContent(dateTimeFormat.format(timestamp));
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setContent(null);
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.VARBINARY:
                                tr.setContent("not displayable content");
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            case java.sql.Types.VARCHAR:
                                tr.setContent((String) tableView.getValueAt(row, col));
                                if (tr.getContent() != null) {
                                    tr.setLength(tr.getContent().length());
                                } else {
                                    tr.setLength(0);
                                }
                                break;
                            default:
                                break;
                        }
                    } catch (ClassCastException e) {
                        Object o = tableView.getValueAt(row, col);
                        String v = (String) o.toString();
                        tr.setContent(v);
                        if (tr.getContent() != null) {
                            tr.setLength(tr.getContent().length());
                        } else {
                            tr.setLength(0);
                        }
                    }
                } else {
                    tr.setContent("null");
                    tr.setLength(4);
                }
                col++;
                //System.out.println("Label: " + tr.getName() + " Wert: " + tr.getContent());
                transpose.add(tr);
            }
            bunch.add(transpose);
        }
        Transpose dialog = new Transpose(bunch);

    }

    private String getCreateTableStatement() {
        StringBuilder sb = new StringBuilder();
        sb.append("-- \n-- dependent of the JDBC driver, some values are not detectable\n-- you need to verify the correctness of this script on your own;\n--\n");
        sb.append("create table ");
        Iterator<FieldNameType> iter = fieldNameTypes.iterator();
        int round = 0;
        while (iter.hasNext()) {
            FieldNameType f = iter.next();
            if (round == 0) {
                sb.append(f.getTable()).append(" (\n");
            } else {
                sb.append(", \n");
            }
            round++;
            if ((f.getSchema().length() > 0) && (!f.getSchema().equalsIgnoreCase("unknown"))) {
                sb.append("\t").append(f.getSchema()).append(".").append(f.getName()).append(" ").append(formattedLength(f));
            } else {
                sb.append("\t").append(f.getName()).append(" ").append(formattedLength(f));
            }

        }
        sb.append("\n)\n");

        return sb.toString();
    }

    private String formattedLength(FieldNameType f) {
        String l = "";

        if (fieldTypes == null) {
            getCon();
            try {
                con.close();
            } catch (SQLException sqlex) {
                
            }
        }

        FieldTypeInfo fti = fieldTypes.get(f.getColumnDataType());

        int type = f.getColumnDataType();
        String columnTypeName = f.getVendorTypeName();
        switch (type) {
            case java.sql.Types.ARRAY:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.BINARY:
                l = "BLOB(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.BLOB:
                l  = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.CLOB:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.INTEGER:
                l = columnTypeName;
                break;
            case java.sql.Types.FLOAT:
                l = columnTypeName;
                break;
            case java.sql.Types.DOUBLE:
                l = columnTypeName;
                break;
            case java.sql.Types.DECIMAL:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + "," + Integer.toString(f.getScale()) + ")";
                break;
            case java.sql.Types.NUMERIC:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + "," + Integer.toString(f.getScale()) + ")";
                break;
            case java.sql.Types.BIGINT:
                l = columnTypeName;
                break;
            case java.sql.Types.TINYINT:
                l = columnTypeName;
                break;
            case java.sql.Types.SMALLINT:
                l = columnTypeName;
                break;
            case java.sql.Types.DATE:
                l = columnTypeName;
                break;
            case java.sql.Types.TIMESTAMP:
                l = columnTypeName;
                break;
            case java.sql.Types.TIME:
                l = columnTypeName;
                break;
            case java.sql.Types.BIT:
                l = columnTypeName;
                break;
            case java.sql.Types.BOOLEAN:
                l = columnTypeName;
                break;
            case java.sql.Types.CHAR:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.NVARCHAR:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.VARCHAR:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.LONGNVARCHAR:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.LONGVARCHAR:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.LONGVARBINARY:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.NCHAR:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.NCLOB:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.OTHER:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.REAL:
                l = columnTypeName;
                break;
            case java.sql.Types.REF:
                l = columnTypeName;
                break;
            case java.sql.Types.SQLXML:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.STRUCT:
                l = columnTypeName;
                break;
            case java.sql.Types.JAVA_OBJECT:
                l = columnTypeName + "(" + Integer.toString(f.getLength()) + ")";
                break;
            case java.sql.Types.DATALINK:
                l = columnTypeName;
                break;
            case java.sql.Types.DISTINCT:
                l = columnTypeName;
                break;
            case java.sql.Types.NULL:
                l = columnTypeName;
                break;
            default:
                l = columnTypeName;
                break;
        }

        return l;
    }

    /**
     * @return the tablePanel
     */
    public boolean isTablePanel() {
        return tablePanel;
    }

    /**
     * @param tablePanel the tablePanel to set
     */
    public void setTablePanel(boolean tablePanel) {
        this.tablePanel = tablePanel;
    }

    /**
     * @param numRows the numRows to set
     */
    public void setNumRows(int numRows) {
        this.numRows = numRows;
        this.iRows = numRows;
    }

    /**
     * @return the con
     */
    public Connection getCon() {
        ResultSet rs = null;
        try {
            if ((con == null) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
                DatabaseMetaData dmd = con.getMetaData();
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

            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection {0}", ex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing resultset {0}", sqlex.getMessage());
                }
            }
        }

        return con;
    }

    /**
     * @param con the con to set
     */
    public void setCon(Connection con) {
        this.con = con;
        ResultSet rs = null;

        try {
            DatabaseMetaData dmd = con.getMetaData();
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
            logger.log(Level.SEVERE, "Can not establish connection to DB {0}", sqlex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing resultset {0}", sqlex.getMessage());
                }
            }
        }

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
