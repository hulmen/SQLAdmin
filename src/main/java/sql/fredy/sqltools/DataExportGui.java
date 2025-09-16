/*
  DataExportGui.java
 
  Created on August 11, 2003, 7:39 PM
 
  This software is part of the Admin-Framework 
 
  Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
  for DB-Administrations, as create / delete / alter and query tables
  it also creates indices and generates simple Java-Code to access DBMS-tables
 
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

import sql.fredy.ui.ImageButton;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import sql.fredy.connection.DataSource;

/**
 *
 * @author sql@hulmen.ch
 */
public class DataExportGui extends javax.swing.JFrame {

    private Logger logger = Logger.getLogger("sql.fredy.sqltools");

    //private JTextArea query = null;
    private RSyntaxTextArea query = null;
    private java.io.File file = null;
    private Toolkit toolKit;
    private sql.fredy.ui.ImageButton close;
    private String[] filter = {"csv", "CSV", "txt", "TXT"};
    private JTextField fileName;

    private JComboBox exportStyle;

    private JTextField wordSeparator, wordSeparatorReplacer, characterEncoding;

    private JPanel pPanel;

    private java.sql.Connection con = null;

    private static int CSV = 1;

    private static int XLS = 2;

    private static int XML = 3;

    private int exportType = 1;

    private sql.fredy.ui.ImageButton connectButton;

    private boolean qEditable = true;

    private boolean local = false;

    private javax.swing.JLabel statusLine;

    /**
     * Creates a new instance of DataExportGui
     */
    public DataExportGui() {
        super("Data Export");
        query = new RSyntaxTextArea(12, 40);
        query.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        query.setCodeFoldingEnabled(true);

        init();
    }

    public DataExportGui(String q, boolean local) {
        super("Data Export");
        query = new RSyntaxTextArea(12, 40);
        query.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        query.setCodeFoldingEnabled(true);
        query.setText(q);
        setLocal(local);
        init();
    }

    public DataExportGui(java.lang.String q, boolean editable, boolean local) {
        super("Data Export");

        query = new RSyntaxTextArea(12, 40);
        query.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        query.setCodeFoldingEnabled(true);

        query.setText(q);
        setQEditable(editable);
        setLocal(local);
        init();
    }

    private void init() {

        toolKit = this.getToolkit();
        this.getContentPane().setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(infoPanel()),
                new JScrollPane(queryPanel()));
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(170);

        this.getContentPane().add(BorderLayout.CENTER, splitPane);
        this.getContentPane().add(BorderLayout.SOUTH, buttonPanel());

        this.pack();
        this.setVisible(true);

    }

    String getQuery() {
        return query.getText();
    }

    java.io.File getFile() {
        return file;
    }

    void setQuery(java.lang.String v) {
    }

    void setFile(java.io.File v) {
    }

    JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        close = new ImageButton(null, "exit.gif", "Exit");

        close.addActionListener((ActionEvent e) -> {
            close();
        });

        ImageButton export = new ImageButton(null, "export.gif", "Export Data");
        export.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!exportData()) {
                    setStatusLine(statusLine.getText() + " | Not exported!");
                } else {
                    setStatusLine("Data exporting!");
                }
                query.updateUI();
            }
        });

        panel.add(export);
        panel.add(close);

        panel.setBorder(new EtchedBorder());
        return panel;

    }

    JPanel paramPanel() {
        pPanel = new JPanel();
        pPanel.setLayout(new FlowLayout());

        JLabel label1 = new JLabel("Select exportstyle");
        exportStyle = new JComboBox(new String[]{"Default", "Excel", "Informix", "Informix_CSV", "Mongo_CSV", "Mongo_TSV", "MySQL", "Oracle", "PostgresSQL_CSV", "PostgresSQL_TEXT", "RFC4180", "TDF"});

        characterEncoding = new JTextField("ISO-8859-1", 10);

        JLabel label2 = new JLabel("Word Separator");
        label2.setAlignmentX(Component.LEFT_ALIGNMENT);

        pPanel.add(label1);
        pPanel.add(exportStyle);

        pPanel.add(new JLabel("Character Encoding for XML"));
        pPanel.add(characterEncoding);

        pPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Values for CSV-Type export"));
        return pPanel;

    }

    JPanel filePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        fileName = new JTextField(40);
        fileName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setDefaultLabelText();
            }
        });

        fileName.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent fe) {
                setDefaultLabelText();
            }

            public void focusLost(FocusEvent fe) {
            }
        });

        JButton search = new JButton("...");

        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setDefaultLabelText();
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                chooser.setDialogTitle("Select File");
                chooser.setFileFilter(new sql.fredy.io.MyFileFilter(filter));

                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    setFile(chooser.getSelectedFile());
                    fileName.setText(chooser.getCurrentDirectory()
                            + java.io.File.separator
                            + chooser.getSelectedFile().getName());
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(fileName, gbc);
        gbc.gridx = 1;
        panel.add(search);

        panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "File to save Export into"));
        return panel;
    }

    JPanel fileTypePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 1));

        JRadioButton csvButton = new JRadioButton("CSV Format");
        JRadioButton xlsButton = new JRadioButton("XLS Format");
        JRadioButton xmlButton = new JRadioButton("XML Format");

        ButtonGroup bg = new ButtonGroup();
        bg.add(csvButton);
        bg.add(xlsButton);
        bg.add(xmlButton);

        csvButton.setSelected(true);

        csvButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {                                
                filter = new String[]{"csv", "CSV", "txt", "TXT"};
                setExportType(CSV);

            }
        });

        xlsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                filter = new String[]{"xls", "XLS", "xlsx", "XLSX"};
                setExportType(XLS);
            }
        });

        xmlButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {             
                filter = new String[]{"xml", "XML"};
                setExportType(XML);
            }
        });
        panel.add(csvButton);
        panel.add(xlsButton);
        panel.add(xmlButton);

        panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Export Type"));
        return panel;

    }

    JPanel infoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        panel.add(fileTypePanel(), gbc);

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 4;
        panel.add(filePanel(), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 3;
        panel.add(paramPanel(), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.gridwidth = 5;
        panel.add(statusPanel(), gbc);

        panel.setBorder(new EtchedBorder());
        return panel;
    }

    JPanel queryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        query.setFont(new java.awt.Font("Monospaced", Font.PLAIN, 12));
        if (!isQEditable()) {
            query.setEditable(false);
        }

        panel.add(BorderLayout.CENTER, new RTextScrollPane(query));

        query.setDragEnabled(true);

        query.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent fe) {
                setDefaultLabelText();
            }

            public void focusLost(FocusEvent fe) {
            }
        });

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());

        ImageButton clear = new ImageButton(null, "clear.gif", null);
        clear.setToolTipText("clear query area");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                query.setText("");

            }
        });
        buttons.add(clear);

        ImageButton copy = new ImageButton(null, "copy.gif", null);
        copy.setToolTipText("Copy selection to clipboard");
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String s = query.getSelectedText();
                    if (s.length() > 0) {
                        StringSelection ss = new StringSelection(s);
                        toolKit.getSystemClipboard().setContents(ss, ss);
                    }
                } catch (Exception ec) {
                    toolKit.beep();
                }
            }
        });
        buttons.add(copy);

        ImageButton paste = new ImageButton(null, "paste.gif", null);
        paste.setToolTipText("Paste");
        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Clipboard c = toolKit.getSystemClipboard();
                Transferable t = c.getContents(this);
                try {
                    String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                    query.insert(s, query.getCaretPosition());
                } catch (Exception eexc) {
                    toolKit.beep();
                }
            }
        });
        buttons.add(paste);

        ImageButton cut = new ImageButton(null, "cut.gif", null);
        cut.setToolTipText("Cut");
        cut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = query.getSelectedText();
                StringSelection ss = new StringSelection(s);
                toolKit.getSystemClipboard().setContents(ss, ss);
                query.replaceRange(null, query.getSelectionStart(), query.getSelectionEnd());
            }
        });
        buttons.add(cut);

        ImageButton saveQuery = new ImageButton(null, "save.gif", "Save Query to file");
        saveQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter();
                fw.setFilter(new String[]{"sql", "SQL", "txt", "TXT"});
                fw.setContent(query.getText());
                fw.setFileName("?");
                fw.setSwitch("a");
                fw.write();
            }
        });
        buttons.add(saveQuery);

        ImageButton loadQuery = new ImageButton(null, "load.gif", "Load query from file");
        loadQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.ReadFile rf = new sql.fredy.io.ReadFile();
                rf.setFilter(new String[]{"sql", "SQL", "txt", "TXT"});
                rf.setFileName("?");
                if (query.getText().length() > 0) {
                    query.append(";\n");
                }
                query.append(rf.getText());
                query.updateUI();
            }
        });
        buttons.add(loadQuery);

        buttons.setBorder(new EtchedBorder());
        panel.add(BorderLayout.NORTH, buttons);

        panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Query"));
        return panel;

    }

    public void close() {
        if (isLocal()) {
            System.exit(0);
        } else {
            this.dispose();
        }
    }

    public static void main(String args[]) {
        DataExportGui deg = new DataExportGui();
    }

    public Connection getCon() {

        try {
            if ((null == con) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
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

    public boolean exportData() {

        if (fileName.getText().length() < 1) {
            setStatusLine("need to have a valid File for Export");
            return false;
        }
        if (getQuery().length() < 5) {
            setStatusLine("do not know, what to do, please enter a query");
            return false;
        }

        // what is to do?
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        this.setEnabled(false);

        switch (getExportType()) {
            case 1: // CSV
                /*
                Thread t = new Thread() {
                    public void run() {
                        DataExport dex = new DataExport();
                        CsvExport csvExport = new CsvExport();
                        csvExport.export((String)exportStyle.getSelectedItem(), query.getText(), fileName.getText());                        
                        setStatusLine("CSV Data exported into " +  fileName.getText());
                    }
                };
                t.start();
                 */
                Thread t = new Thread(new CsvExport((String) exportStyle.getSelectedItem(), query.getText(), fileName.getText()));
                t.start();
                break;
            case 2: //XLS
                Thread t1 = new Thread() {
                    public void run() {
                        XLSExport xle = new XLSExport();
                        xle.setQuery(getQuery());
                        String xlsFname = fileName.getText();
                        xle.createXLS(xlsFname);
                        setStatusLine("XLS Data exported into " + xlsFname);
                    }
                };
                t1.start();
                break;
            case 3: // XML
                Thread t2 = new Thread() {
                    public void run() {
                        XMLExport xex = new XMLExport();
                        xex.setEncoding(characterEncoding.getText());
                        String xmlFname = fileName.getText();
                        xex.setFileName(new File(xmlFname));
                        xex.setQuery(query.getText());
                        xex.export();
                        setStatusLine("XML Data exported into " + xmlFname);
                    }
                };
                t2.start();
                break;
            default:
                break;
        }
        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        this.setEnabled(true);
        return true;
    }

    public void setExportType(int v) {     
        this.exportType = v;
    }

    public int getExportType() {
        return exportType;
    }

    public void setQEditable(boolean v) {
        qEditable = v;
    }

    public boolean isQEditable() {
        return qEditable;
    }

    public void setLocal(boolean v) {
        local = v;
    }

    public boolean isLocal() {
        return local;
    }

    private JPanel statusPanel() {
        JPanel panel = new JPanel();
        statusLine = new JLabel();
        statusLine.setText("Fredy's Data Export Tool");
        statusLine.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusLine.setBackground(Color.yellow);
        statusLine.setForeground(Color.blue);
        panel.setBackground(Color.yellow);
        panel.setForeground(Color.blue);
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        panel.add(statusLine);
        return panel;
    }

    public void setStatusLine(java.lang.String v) {
        statusLine.setText(v);
        statusLine.setBackground(Color.red);
        statusLine.setForeground(Color.black);
        statusLine.setFont(new Font("Monospaced", Font.BOLD, 12));
        statusLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLine.updateUI();
        toolKit.beep();
    }

    private void setDefaultLabelText() {
        statusLine.setText("Fredy's Data Export Tool");
        statusLine.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusLine.setBackground(Color.yellow);
        statusLine.setForeground(Color.blue);

        statusLine.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLine.updateUI();
    }

}
