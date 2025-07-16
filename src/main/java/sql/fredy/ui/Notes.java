/**
 * This Class is part of Fredy's SQL-Admin Tool.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, like: - create/ drop tables - create indices - perform
 * sql-statements - simple form - a guided query - Data Export and a other
 * usefull things in DB-arena
 *
 * Admin (Version see below) Copyright (c) 1999 Fredy Fischer sql@hulmen.ch
 *
 * Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 *
 * The icons used in this application are from Dean S. Jones
 *
 * Icons Copyright(C) 1998 by Dean S. Jones dean@gallant.com
 * www.gallant.com/icons.htm
 *
 * CalendarBean is Copyright (c) by Kai Toedter
 *
 * MSeries is Copyright (c) by Martin Newstead
 *
 * POI is from the Apache Foundation
 *
 *
 *   Copyright (c) 2017 Fredy Fischer, sql@hulmen.ch
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 *
 *
 * How does it work: * CTRL-H Help CTRL-Insert Save CTRL-DEL Delete
 * Control-Right Save & next Control-Left Save & Previous Control-PgUp Next
 * Control-PgDn Previous Control-t Set marked Text as Title Control-S Search
 * Text Control-Home findfirst Entry Control-End	find last Entry
 *
 */
package sql.fredy.ui;

import sql.fredy.tools.DerbyInfoServer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import sql.fredy.infodb.DBconnectionPool;
import sql.fredy.infodb.DBserver;
import sql.fredy.infodb.DBserverTest;
import sql.fredy.infodb.DBverify;

public class Notes extends JPanel implements FocusListener {

    private Logger logger = Logger.getLogger("sql.fredy.ui.Notes");
    private RSyntaxTextArea note;
    RTextScrollPane pane;
    private TitledBorder border;
    private DerbyInfoServer derby;

    private boolean shutDownDerby = false;  // we do not need to shutdown derby when we are not standalone
    private JLabel statusline;
    ArrayList<QueryBeanNotes> notesList;
    int positionInNotesList;

    String title;
    int id = 0;
    private Timestamp runAt = null;

    Color col = new Color(255, 255, 204, 255);

    String helpMessage = ("<ALT> H Help\n"
            + "<ALT> N New Document (without Save)\n"
            + "<ALT> S Save\n"
            + "<ALT> DEL Delete\n"
            + "<ALT> Insert Save and new\n"
            + "<ALT> P Save & Previous\n"
            + "<ALT> PgUp Next Document\n"
            + "<ALT> PgDn Previous Document\n"
            + "<ALT> T Set marked Text as Title\n"
            + "<ALT> F Find Text\n"
            + "<ALT> + Loop Searchresult forward\n"
            + "<ALT> - Loop Searchresult backwards\n"
            + "<ALT> Home find first Entry\n"
            + "<ALT> End find last Entry");
    //JDialog helpDialog, searchDialog;         
    JTabbedPane jtp;

    InputMap iMap;
    ActionMap aMap;

    public Notes() {

        /*
        int inFocus = JComponent.WHEN_FOCUSED;
        iMap  = this.getInputMap(inFocus);
        aMap  = this.getActionMap();
         */
        init();
        this.setLayout(new BorderLayout());

        /*
         this.setFocusable(true);
         this.addFocusListener(new FocusAdapter() {
         public void focusLost(FocusEvent e) {
         System.out.println("Focus lost");
         }

         public void focusGained(FocusEvent e) {
         System.out.println("Focus gained");
         }
         });
         */
        note = new RSyntaxTextArea(10, 150);

        note.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        note.setCodeFoldingEnabled(true);

        note.setBackground(col);
        note.setForeground(Color.BLACK);

        note.setRequestFocusEnabled(true);
        note.addFocusListener(this);

        pane = new RTextScrollPane(note);
        border = new TitledBorder("write note");
        border = colorSetter(border);
        pane.setBorder(border);
        doKeyListener();

        statusline = new JLabel("Type Alt-H for help", SwingConstants.CENTER);
        statusline.setBorder(new BevelBorder(BevelBorder.LOWERED));
        statusline.setBackground(col);

        jtp = new JTabbedPane();
        jtp.add("Note", pane);
        jtp.add("Info", findPanel());

        this.add(jtp, BorderLayout.CENTER);
        this.add(statusline, BorderLayout.SOUTH);
        //this.add(buttonPanel(), BorderLayout.NORTH);

        //helpwindow();
        //searchWindow();
        deleteWindow();
        last();

    }

    private Component colorSetter(Component c) {

        //c.setBackground(Color.YELLOW);
        c.setBackground(col);
        c.setForeground(Color.BLACK);
        c = fontSetter(c);
        return c;
    }

    private Component fontSetter(Component c) {
        c.setFont(c.getFont().deriveFont(10.0f));
        return c;
    }

    JButton no;
    JDialog deleteDialog;

    private void deleteWindow() {
        deleteDialog = new JDialog();
        deleteDialog.setModal(true);
        deleteDialog.setTitle("Delete?");
        deleteDialog.getContentPane().setLayout(new BorderLayout());
        deleteDialog.setResizable(false);
        deleteDialog.add(BorderLayout.CENTER, new ImageLabel("     Shall I really delete?", "stop.gif", "are you sure?"));
        JPanel panel = new JPanel();
        JButton yes = new JButton("YES");
        no = new JButton("NO");
        panel.setLayout(new FlowLayout());
        panel.add(yes);
        panel.add(no);
        yes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delete();
                deleteDialog.setVisible(false);
            }
        });
        no.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteDialog.setVisible(false);
            }
        });

        deleteDialog.add(BorderLayout.SOUTH, panel);

        deleteDialog.pack();
        deleteDialog.setSize(new Dimension(200, 100));
    }

    private JTextField search;

    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.YELLOW);
        panel.setForeground(Color.BLACK);
        panel.setLayout(new FlowLayout());
        JButton plus = new JButton("+");
        JButton minus = new JButton("-");
        JButton haeh = new JButton("?");
        JButton nxt = new JButton(">");
        JButton prv = new JButton("<");
        search = new JTextField(20);

        plus = (JButton) colorSetter(plus);
        minus = (JButton) colorSetter(minus);
        prv = (JButton) colorSetter(prv);
        haeh = (JButton) colorSetter(haeh);
        nxt = (JButton) colorSetter(nxt);
        search = (JTextField) colorSetter(search);

        panel.add(prv);
        panel.add(plus);
        panel.add(minus);
        panel.add(haeh);
        panel.add(search);
        panel.add(nxt);

        return panel;
    }

    private void doKeyListener() {

        Action showHelpAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtp.setSelectedIndex(1);
            }
        };
        Action newDocumentAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                note.setText("");
                prevText = "";
                setRunAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                setTitle(null);
                setId(0);
            }
        };

        Action saveAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                save();
            }
        };

        Action deleteAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                logger.log(Level.INFO, "Delete Dialog at " + p.toString());
                deleteDialog.setLocation(p);
                no.requestFocus();
                deleteDialog.setVisible(true);
            }
        };

        Action saveAndNewAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                save();
                note.setText("");
                prevText = "";
                setRunAt(new Timestamp(Calendar.getInstance().getTimeInMillis()));
                setTitle(null);
                setId(0);
            }
        };

        Action saveAndPreviousAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Point p = MouseInfo.getPointerInfo().getLocation();
                save();
                previous();
            }
        };

        Action nextDocumentAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                next();
            }
        };

        Action previousDocumentAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                previous();
            }
        };

        Action setTitleAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTitle();
            }
        };

        Action findTextAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jtp.setSelectedIndex(1);
                pattern.requestFocus();
            }
        };

        Action loopForwardAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((notesList != null) && (notesList.size() > 0)) {
                    nextFound();
                }
            }
        };

        Action loopBackwardsAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((notesList != null) && (notesList.size() > 0)) {
                    previousFound();
                }
            }
        };

        Action goToFirstEntryAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                first();
            }
        };

        Action goToLastEntryAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                last();
            }
        };

        int inFocus = JComponent.WHEN_IN_FOCUSED_WINDOW;
        iMap = note.getInputMap(inFocus);
        aMap = note.getActionMap();

        KeyStroke altH = KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.ALT_DOWN_MASK);
        KeyStroke altN = KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK);
        KeyStroke altS = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.ALT_DOWN_MASK);
        KeyStroke altDelete = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.ALT_DOWN_MASK);
        KeyStroke altInsert = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, InputEvent.ALT_DOWN_MASK);
        KeyStroke altP = KeyStroke.getKeyStroke("alt P");
        KeyStroke altPgUp = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, InputEvent.ALT_DOWN_MASK);
        KeyStroke altPgDown = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, InputEvent.ALT_DOWN_MASK);
        KeyStroke altT = KeyStroke.getKeyStroke("alt T");
        KeyStroke altF = KeyStroke.getKeyStroke("alt F");
        KeyStroke altPlus = KeyStroke.getKeyStroke("alt +");
        KeyStroke altMinus = KeyStroke.getKeyStroke("alt -");
        KeyStroke altHome = KeyStroke.getKeyStroke(KeyEvent.VK_HOME, InputEvent.ALT_DOWN_MASK);
        KeyStroke altEnd = KeyStroke.getKeyStroke(KeyEvent.VK_END, InputEvent.ALT_DOWN_MASK);

        iMap.put(altH, "alt-h");
        iMap.put(altN, "alt-n");
        iMap.put(altS, "alt-s");
        iMap.put(altDelete, "alt-del");
        iMap.put(altInsert, "alt-ins");
        iMap.put(altP, "alt-p");
        iMap.put(altPgUp, "alt-pgup");
        iMap.put(altPgDown, "alt-pgdown");
        iMap.put(altT, "alt-t");
        iMap.put(altF, "alt-f");
        iMap.put(altPlus, "alt-+");
        iMap.put(altMinus, "alt--");
        iMap.put(altHome, "alt-home");
        iMap.put(altEnd, "alt-end");

        aMap.put("alt-h", showHelpAction);
        aMap.put("alt-n", newDocumentAction);
        aMap.put("alt-s", saveAction);
        aMap.put("alt-del", deleteAction);
        aMap.put("alt-ins", saveAndNewAction);
        aMap.put("alt-p", saveAndPreviousAction);
        aMap.put("alt-pgup", nextDocumentAction);
        aMap.put("alt-pgdown", previousDocumentAction);
        aMap.put("alt-t", setTitleAction);
        aMap.put("alt-f", findTextAction);
        aMap.put("alt-+", loopForwardAction);
        aMap.put("alt--", loopBackwardsAction);
        aMap.put("alt-home", goToFirstEntryAction);
        aMap.put("alt-end", goToLastEntryAction);

    }
    private String prevText = "";

    private void save() {

        PreparedStatement dbUpdate = null;
        PreparedStatement dbInsert = null;
        PreparedStatement dbRunAt = null;
        Connection con = null;

        // first we check if the hash is here already
        boolean writeIt = true;

        if (prevText.equals(note.getText())) {
            writeIt = false;
        }

        if (writeIt) {

            // has this text alread been saved?
            if (getId() > 0) {

                //dbUpdate = con.prepareStatement("update APP.Notes set Title = ?, Note = ?, HashCode = ? where id = ?");
                //logger.log(Level.INFO, "Updating Note...");
                try {
                    con = getCon();
                    dbUpdate = con.prepareStatement("update APP.Notes set Title = ?, Note = ?, HashCode = ? where id = ?");
                    dbUpdate.setString(1, getTitle());
                    dbUpdate.setString(2, note.getText());
                    dbUpdate.setInt(3, note.hashCode());
                    dbUpdate.setInt(4, getId());
                    dbUpdate.executeUpdate();

                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while while updating note {0}", sqlex.getMessage());
                    logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
                } finally {
                    if (dbUpdate != null) {
                        try {
                            dbUpdate.close();
                        } catch (SQLException e) {
                             logger.log(Level.WARNING, "Exception while while updating note closing dbUpdate {0}", e.getMessage());
                        }
                    }
                    if (con != null) {
                        try {
                            con.close();                          
                        } catch (SQLException e) {
                            logger.log(Level.WARNING, "Exception while while updating note closing connection {0}", e.getMessage());
                        }
                    }

                }

            } else {
                logger.log(Level.INFO, "Writing Note...");
                try {
                    con = getCon();
                    dbInsert = con.prepareStatement("insert into APP.Notes ( ToolUser, Title, Note, HashCode) values (?, ?,?,?)", Statement.RETURN_GENERATED_KEYS);
                    dbRunAt = con.prepareStatement("select RunAt from APP.Notes where id = ?");

                    dbInsert.setString(1, getUser());
                    dbInsert.setString(2, getTitle());
                    dbInsert.setString(3, note.getText());
                    dbInsert.setInt(4, note.hashCode());
                    dbInsert.executeUpdate();

                    // and now fetch the inserted ID
                    ResultSet rsi = dbInsert.getGeneratedKeys();
                    while (rsi.next()) {
                        setId(rsi.getInt(1));
                        break;
                    }

                    // and now, we find the Timestamp generated by Derby
                    dbRunAt.setInt(1, getId());
                    rsi = dbRunAt.executeQuery();
                    while (rsi.next()) {
                        setRunAt(rsi.getTimestamp(1));
                        break;
                    }
                    //logger.log(Level.INFO,"Wrote " + getId() + " at " + getRunAt());
                    rsi.close();
                    sqt.addRow(getQueryBeanNotes());
                    fixColumnSize();

                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while while writing note {0}", sqlex.getMessage());
                    logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
                } finally {
                    if (dbInsert != null) {
                        try {
                            dbInsert.close();
                        } catch (SQLException e) {

                        }
                    }
                    if (dbRunAt != null) {
                        try {
                            dbRunAt.close();
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
            }
        }

    }

    private void delete() {

        Connection con = null;
        PreparedStatement dbDelete = null;

        String job = "delete";
        try {
            con = getCon();
            dbDelete = con.prepareStatement("delete from APP.Notes where ID = ?");

            job = "delete";
            if (getId() > 0) {
                dbDelete.setInt(1, getId());
                dbDelete.executeUpdate();
                note.setText("");
                setTitle("");
                setRunAt(null);
                findbyId(sqt.delete(getId()));
                fixColumnSize();
                //setId(0);                
            }

            // mach was immer eine SQLException ausloesen kann
        } catch (SQLException sqlex) {
            // trage hier deine Fehlermeldung ein
            logger.log(Level.WARNING, "Fehler beim was auch immmer : {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
            logger.log(Level.INFO, "Job : {0}", job);
        } finally {
            if (dbDelete != null) {
                try {
                    dbDelete.close();
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

    }

    private QueryBeanNotes getQueryBeanNotes() {
        QueryBeanNotes q = new QueryBeanNotes();
        q.setId(getId());
        q.setRunat(getRunAt());
        q.setTitle(getTitle());
        q.setNote(note.getText());
        q.setHashcode(note.getText().hashCode());
        return q;
    }

    private void next() {
        // dbNext = con.prepareStatement("select id,RunAt, Title, Note from APP.Notes where RunAt > ? order by RunAt asc");
        //logger.log(Level.INFO, "finding next");

        PreparedStatement dbNext = null;
        Connection con = null;
        ResultSet rs = null;
        try {
            con = getCon();
            dbNext = con.prepareStatement("select id,RunAt, Title, Note from APP.Notes where ToolUser = ? and  RunAt > ? order by RunAt asc");
            dbNext.setString(1, getUser());
            dbNext.setTimestamp(2, getRunAt());
            rs = dbNext.executeQuery();
            while (rs.next()) {
                setId(rs.getInt(1));
                setRunAt(rs.getTimestamp(2));
                setTitle(rs.getString(3));
                note.setText(rs.getString(4));
                prevText = note.getText();
                break;
            }

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while fetching next Note  {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {

                }
            }
            if (dbNext != null) {
                try {
                    dbNext.close();
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
    }

    private void previous() {
        // dbPrevious = con.prepareStatement("select id,RunAt, Title, Note from APP.Notes where RunAt < ? order by RunAt desc");
        //logger.log(Level.INFO, "finding previous " + getRunAt() + " ID " + getId());

        Connection con = null;
        PreparedStatement dbPrevious = null;
        ResultSet rs = null;

        try {
            con = getCon();
            dbPrevious = con.prepareStatement("select id,RunAt, Title, Note from APP.Notes where ToolUser = ? and  RunAt < ? order by RunAt desc");

            dbPrevious.setString(1, getUser());
            dbPrevious.setTimestamp(2, getRunAt());
            rs = dbPrevious.executeQuery();
            while (rs.next()) {
                setId(rs.getInt(1));
                setRunAt(rs.getTimestamp(2));
                setTitle(rs.getString(3));
                note.setText(rs.getString(4));
                prevText = note.getText();
                break;
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while fetching previous Note  {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {

                }
            }
            if (dbPrevious != null) {
                try {
                    dbPrevious.close();
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
    }

    private void setTitle() {
        title = note.getSelectedText();
        setTitle(title);
        prevText = "";
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private void setTitle(String v) {
        StringBuilder sb = new StringBuilder();
        if (getRunAt() != null) {
            sb.append(sdf.format(getRunAt().getTime()));
        }

        if (v != null) {
            title = v;
            sb.append("  ").append(title);
            prevText = "";
        }
        border.setTitle(sb.toString());
        pane.updateUI();

    }

    private String getTitle() {
        return title;
    }

    private void setId(int v) {
        id = v;
    }

    private int getId() {
        return id;
    }

    private void first() {
        // dbFirst = con.prepareStatement("select id,RunAt, Title, Note from APP.Notes order by RunAt asc FETCH FIRST 1 ROW ONLY");
        Connection con = null;
        PreparedStatement dbFirst = null;
        ResultSet rs = null;

        try {
            con = getCon();
            dbFirst = con.prepareStatement("select id,RunAt, Title, Note from APP.Notes where ToolUser = ? order by RunAt asc FETCH FIRST 1 ROW ONLY");
            dbFirst.setString(1, getUser());
            rs = dbFirst.executeQuery();
            while (rs.next()) {
                setId(rs.getInt(1));
                setRunAt(rs.getTimestamp(2));
                setTitle(rs.getString(3));
                note.setText(rs.getString(4));
                prevText = note.getText();
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while fetching first Note  {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {

                }
            }
            if (dbFirst != null) {
                try {
                    dbFirst.close();
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
    }

    private void last() {
        //dbLast = con.prepareStatement("select id,RunAt, Title, Note from APP.Notes order by RunAt desc FETCH FIRST 1 ROW ONLY");

        Connection con = null;
        PreparedStatement dbLast = null;
        ResultSet rs = null;

        try {
            con = getCon();
            dbLast = con.prepareStatement("select id,RunAt, Title, Note from APP.Notes where ToolUser = ? order by RunAt desc FETCH FIRST 1 ROW ONLY");
            dbLast.setString(1, getUser());
            rs = dbLast.executeQuery();
            while (rs.next()) {
                setId(rs.getInt(1));
                setRunAt(rs.getTimestamp(2));
                setTitle(rs.getString(3));

                note.setText(rs.getString(4));
                prevText = note.getText();

                //logger.log(Level.INFO, "ID = " + getId() + " at " + getRunAt());
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while fetching first Note  {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {

                }
            }
            if (dbLast != null) {
                try {
                    dbLast.close();
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
    }

    private void init() {

        // non DB things to be initialised
        notesList = new ArrayList();
        positionInNotesList = -1;

    }


    private Connection connection = null;     
    private Connection getCon() {
        
        try {
            connection = DBconnectionPool.getInstance().getConnection();
        } catch (SQLException e) {
        } catch (IOException | PropertyVetoException ex) {
            Logger.getLogger(DBserverTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return connection;
    }

    private void findbyId(int v) {
        Connection con = null;
        PreparedStatement dbFindID = null;
        try {
            con = getCon();
            dbFindID = con.prepareStatement("select id,RunAt, Title, Note from APP.Notes where id = ?");
            dbFindID.setInt(1, v);
            ResultSet rs = dbFindID.executeQuery();
            while (rs.next()) {
                setId(rs.getInt(1));
                setRunAt(rs.getTimestamp(2));
                setTitle(rs.getString(3));
                note.setText(rs.getString(4));
            }
        } catch (SQLException sqlex) {
            // trage hier deine Fehlermeldung ein
            logger.log(Level.WARNING, "Error when searching row : {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
        } finally {
            if (dbFindID != null) {
                try {
                    dbFindID.close();
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
    }

    private String getUser() {
        try {
            return System.getProperty("user.name");
        } catch (Exception e) {
            return "unknown";
        }
    }

    private void find(String f) {

        ResultSet rs = null;
        Connection con = null;
        PreparedStatement dbFind = null;

        f = "%" + f + "%";
        notesList = new ArrayList();
        positionInNotesList = 0;
        QueryBeanNotes n;
        try {
            con = getCon();
            dbFind = con.prepareStatement("select id,RunAt, Title, Note from APP.Notes where ToolUser = ? and (Title like ?  or  Note like ?) order by RunAt desc");

            dbFind.setString(1, getUser());
            dbFind.setString(2, f);
            dbFind.setString(3, f);
            rs = dbFind.executeQuery();
            while (rs.next()) {
                n = new QueryBeanNotes();
                n.setId(rs.getInt(1));
                n.setRunat(rs.getTimestamp(2));
                n.setTitle(rs.getString(3));
                n.setNote(rs.getString(4));
                notesList.add(n);
            }
            System.out.println("Found "+String.format("%,d",notesList.size()) + " Rows" );
            
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while searching notes {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } finally {
            if (dbFind != null) {
                try {
                    dbFind.close();
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
        sqt.addBunch(notesList);

        if (tableView != null) {
            fixColumnSize();
        }

        nextFound();
    }

    JPanel findPanel;
    JTable tableView = null;
    TableNotes sqt;
    JTextField pattern;

    private JPanel findPanel() {
        findPanel = new JPanel();
        findPanel.setLayout(new BorderLayout());

        findPanel.setBackground(col);
        sqt = new TableNotes();
        find("");
        sqt.addBunch(notesList);
        tableView = new JTable(sqt);
        tableView.setBackground(col);
        tableView.getTableHeader().setReorderingAllowed(false);
        tableView.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        tableView.setAutoCreateRowSorter(true);
        ListSelectionModel listSelectionModel = tableView.getSelectionModel();
        listSelectionModel.setValueIsAdjusting(true);

        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        tableView.setSelectionModel(listSelectionModel);

        tableView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if (lsm.isSelectionEmpty()) {
                        ;
                    } else {
                        int selectedRow = tableView.getSelectedRow();
                        setId(((Integer) tableView.getValueAt(selectedRow, 0)));
                        setRunAt((Timestamp) tableView.getValueAt(selectedRow, 1));
                        setTitle((String) tableView.getValueAt(selectedRow, 2));
                        note.setText((String) tableView.getValueAt(selectedRow, 3));

                        jtp.setSelectedIndex(0);
                    }
                }
            }
        });
        fixColumnSize();
        JScrollPane scrollpane = new JScrollPane(tableView);
        scrollpane.setBackground(col);;

        scrollpane.getViewport().setBackground(col);
        tableView.getTableHeader().setBackground(col);

        //fixColumnSize();
        findPanel.add(BorderLayout.CENTER, scrollpane);

        // and now the Help-Text
        JTextArea help = new JTextArea(helpMessage);
        help.setBorder(new TitledBorder("Help"));

        pattern = new JTextField(15);
        pattern.setBackground(col);
        pattern.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                find(pattern.getText());
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        JButton reset = new JButton("Reset");
        reset.setBackground(col);
        reset.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                find("");
                pattern.setText("");
            }
        });

        panel.add(reset);
        panel.add(new JLabel("Search Pattern"));
        panel.add(pattern);
        panel.setBackground(col);

        findPanel.add(BorderLayout.SOUTH, panel);

        help.setBackground(col);
        findPanel.add(BorderLayout.WEST, help);

        return findPanel;
    }

    private void fixColumnSize() {
        TableColumn column = null;
        TableNotes sq = new TableNotes();

        column = tableView.getColumnModel().getColumn(sq.ID);
        column.setPreferredWidth(30);

        column = tableView.getColumnModel().getColumn(sq.RUNAT);
        column.setPreferredWidth(200);

        column = tableView.getColumnModel().getColumn(sq.TITLE);
        column.setPreferredWidth(200);

        column = tableView.getColumnModel().getColumn(sq.NOTE);
        column.setPreferredWidth(400);

        tableView.updateUI();
    }

    private void nextFound() {

        //logger.log(Level.INFO,"Position: " + positionInNotesList);
        if ((positionInNotesList < notesList.size()) && (notesList.size() > 0)) {
            QueryBeanNotes n = (QueryBeanNotes) notesList.get(positionInNotesList);
            setId(n.getId());
            setRunAt(n.getRunat());
            setTitle(n.getTitle());
            note.setText(n.getNote());
            positionInNotesList++;
        }
        if (positionInNotesList > notesList.size()) {
            positionInNotesList = notesList.size();
        }
    }

    private void previousFound() {

        //logger.log(Level.INFO,"Position: " + positionInNotesList);
        if ((positionInNotesList > 0) && (notesList.size() > 0)) {
            positionInNotesList--;
            QueryBeanNotes n = (QueryBeanNotes) notesList.get(positionInNotesList);
            setId(n.getId());
            setRunAt(n.getRunat());
            setTitle(n.getTitle());
            note.setText(n.getNote());
        }
        if (positionInNotesList < 0) {
            positionInNotesList = 0;
        }
    }

    public void close() {
        save();
    }

    public static void main(String args[]) {
        DBserver dbserver = new DBserver();
        dbserver.startUp();
        DBverify dbVerify = new DBverify();

        JFrame frame = new JFrame("Notes");
        final Notes s = new Notes();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(s, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                s.close();
                dbserver.stop();
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
    }

    @Override
    public void focusGained(FocusEvent e) {
        //System.out.println("Focus gained");
    }

    @Override
    public void focusLost(FocusEvent e) {
        // Store changes when leving
        //System.out.println("Focus lost");
        save();
    }

    private TitledBorder colorSetter(TitledBorder border) {
        border.setTitleColor(Color.BLACK);
        //border.setTitleFont(border.getFont().deriveFont(10.0f));
        return border;
    }

    /**
     * @return the runAt
     */
    public Timestamp getRunAt() {
        return runAt;
    }

    /**
     * @param runAt the runAt to set
     */
    public void setRunAt(Timestamp runAt) {
        this.runAt = runAt;
    }

}
