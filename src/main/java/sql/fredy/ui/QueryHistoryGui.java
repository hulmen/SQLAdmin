/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 *
 */
package sql.fredy.ui;

import sql.fredy.tools.QueryHistory;
import sql.fredy.tools.QueryHistoryEntryTable;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Timestamp;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 *
 * @author sql@hulmen.ch
 */
public class QueryHistoryGui extends JPanel {

    private RSyntaxTextArea query;
    private QueryHistory queryHistory;
    private String dataBase;
    public ImageButton getQuery, cancel, refresh;

    public QueryHistoryGui(String dataBase) {
        setDataBase(dataBase);

        queryHistory = new QueryHistory();
        this.setLayout(new BorderLayout());

        JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, liste(), details());

        this.add(BorderLayout.CENTER, splitPanel);
        this.add(BorderLayout.NORTH, buttonPanel());
    }

    public void close() {

    }

    public void fillAll() {
        qhet.clearData();
        if (getDataBase().equals("%")) {
            qhet.addBunch(queryHistory.readAll());
        } else {
            qhet.addBunch(queryHistory.read(getDataBase()));
        }
        fixColumns();
    }

    public String getText() {
        if (txt.getText().length() > 1) {
            return txt.getText();
        } else {
            return "";
        }
    }

    private void mouseThings() {
        tableView.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {

                System.out.println("Click Count:" + e.getClickCount() + " Button: " + e.getButton());

                int centerButton = e.BUTTON2;
                if (MouseInfo.getNumberOfButtons() < 3) {
                    centerButton = 0;
                }

                int leftButton = e.BUTTON3;
                if (MouseInfo.getNumberOfButtons() < 3) {
                    leftButton = e.BUTTON2;
                }

                int rightButton = e.BUTTON2;

                // add popup if once center click
                if (e.getClickCount() == 1) {
                    if (e.getButton() == centerButton) {
                        //pm.show(e.getComponent(), e.getX(), e.getY());
                    }
                    if (e.getButton() == rightButton) {
                        //pm.show(e.getComponent(), e.getX(), e.getY());
                    }

                }

                if (e.getClickCount() == 2) {
                    if (e.getButton() == leftButton) {
                        getQuery.doClick();
                    }

                }
                if (e.getButton() == centerButton) {
                    //pm.show(e.getComponent(), e.getX(), e.getY());
                } else {

                    if (e.getClickCount() == 2) {
                        e.consume();

                    }
                }
            }

        }
        );
    }

    private JTextField searchPattern;
    private JCheckBox casesensitive;

    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(new EtchedBorder());

        casesensitive = new JCheckBox("Case sensitive", false);

        ImageButton cleanUp = new ImageButton(null, "clear.gif", "delete all entries older than the one selected");
        getQuery = new ImageButton(null, "documentdraw.gif", "get this query");
        ImageButton delete = new ImageButton(null, "delete.gif", "remove this query");
        ImageButton search = new ImageButton(null, "binocular.gif", "Search this pattern");

        search.addActionListener((ActionEvent e) -> {
            txt.setText("");
            busyCursor();
            qhet.addBunch(queryHistory.findIt(getDataBase(), searchPattern.getText(), casesensitive.isSelected()));
            fixColumns();
            defaultCursor();
        });

        cleanUp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (tableView.getSelectedRow() >= 0) {
                    if (!getDataBase().equalsIgnoreCase("%")) {
                        queryHistory.cleanup((Timestamp) qhet.getValueAt(tableView.getSelectedRow(), qhet.RUNAT), getDataBase());
                        txt.setText("");
                        fillAll();
                    }
                }
            }
        });

        delete.addActionListener((ActionEvent e) -> {
            if (tableView.getSelectedRow() >= 0) {
                busyCursor();
                queryHistory.delete((Timestamp) qhet.getValueAt(tableView.getSelectedRow(), qhet.RUNAT), getDataBase());
                qhet.removeRow(tableView.getSelectedRow());
                txt.setText("");
                fixColumns();
                defaultCursor();
            }
        });

        cancel = new ImageButton(null, "exit.gif", null);
        cancel.setToolTipText("Quit");

        refresh = new ImageButton(null, "refresh.gif", "refresh");
        refresh.addActionListener((ActionEvent e) -> {
            busyCursor();
            fillAll();
            defaultCursor();
        });

        searchPattern = new JTextField(30);
        searchPattern.addActionListener((ActionEvent e) -> {
            txt.setText("");
            busyCursor();
            qhet.addBunch(queryHistory.findIt(getDataBase(), searchPattern.getText(), casesensitive.isSelected()));
            fixColumns();
            defaultCursor();
        });       

        panel.add(casesensitive);
        panel.add(searchPattern);
        panel.add(search);

        panel.add(refresh);
        panel.add(getQuery);
        panel.add(cleanUp);
        panel.add(delete);
        panel.add(cancel);

        return panel;
    }

    private void defaultCursor() {
        this.setCursor(Cursor.getDefaultCursor());

    }

    private void busyCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    }

    public JTable tableView;
    private QueryHistoryEntryTable qhet;

    private void fixColumns() {
        TableColumn column = null;
        column = tableView.getColumnModel().getColumn(0);
        column.setPreferredWidth(150);

        column = tableView.getColumnModel().getColumn(1);
        column.setPreferredWidth(300);

        column = tableView.getColumnModel().getColumn(2);
        column.setPreferredWidth(600);

        tableView.updateUI();
    }

    private JPanel liste() {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        qhet = new QueryHistoryEntryTable();

        tableView = new JTable(qhet);

        tableView.getTableHeader().setReorderingAllowed(false);
        tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableView.setAutoCreateRowSorter(true);

        ListSelectionModel listSelectionModel = tableView.getSelectionModel();
        listSelectionModel.setValueIsAdjusting(true);

        fillAll();

        // fixColumnSize();
        tableView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if (lsm.isSelectionEmpty()) {
                        ;
                    } else {
                        int selectedRow = tableView.getSelectedRow();
                        txt.setText((String) tableView.getValueAt(selectedRow, qhet.QUERY));
                    }
                }
            }
        });
        fixColumns();

        // reduce the hieght of thes crollpane to a maximum of 10 rows
        JScrollPane scrollPane = new JScrollPane(tableView);
        Dimension d = tableView.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension(d.width, tableView.getRowHeight() * 10 + 1));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    TextEditor txt;

    private JPanel details() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        txt = new TextEditor();
        txt.setSyntaxStyle(SyntaxConstants.SYNTAX_STYLE_SQL);

        // we remove the text editors button panel, because two of them are disturbing
        Component[] components = txt.getComponents();
        for (int i = 0; i < components.length; i++) {
            String name = components[i].getName();
            if ((name != null) && (name.equalsIgnoreCase("buttonpanel"))) {
                txt.remove(components[i]);
                break;
            }
        }

        panel.add(BorderLayout.CENTER, txt);
        panel.setPreferredSize(new Dimension(1020, 200));
        return panel;
    }

    public static void main(String args[]) {
        final CloseableFrame cf = new CloseableFrame();
        cf.getContentPane().setLayout(new BorderLayout());
        final QueryHistoryGui gui = new QueryHistoryGui("%");

        gui.cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gui.close();
                System.exit(0);
            }
        });

        cf.getContentPane().add(BorderLayout.CENTER, gui);

        cf.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                gui.close();
                System.exit(0);
            }
        });

        cf.pack();
        cf.setVisible(true);
    }

    /**
     * @return the dataBase
     */
    public String getDataBase() {
        return dataBase;
    }

    /**
     * @param dataBase the dataBase to set
     */
    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

}
