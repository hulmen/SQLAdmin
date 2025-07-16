package sql.fredy.generate;

/**
 * GenerateCode, generates Java-Code out of a table and is a part of Admin...
 * Version 1.1 01. June 1999 Fredy Fischer
 *
 * it only returns a JPanel, so it can easily been used in different kind of
 * windows
 *
 * old version, has been replaced in the framework by GenerateWrapper
 *
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
 */
import sql.fredy.ui.CloseableFrame;
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.Tables;
import sql.fredy.ui.LoadImage;
import sql.fredy.metadata.DbInfo;
import sql.fredy.metadata.SingleColumnInfo;
import sql.fredy.metadata.TableColumns;
import sql.fredy.metadata.Columns;
import sql.fredy.metadata.PrimaryKey;
import sql.fredy.io.FileInfo;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import java.io.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.datatransfer.*;
import java.beans.PropertyVetoException;
import java.lang.reflect.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import sql.fredy.connection.DataSource;

public class GenerateCode extends JPanel {

    private Logger logger = Logger.getLogger("sql.fredy.generate");
    
    boolean standAlone = true;
    LoadImage loadImage = new LoadImage();

    /**
     * Get the value of standAlone.
     *
     * @return value of standAlone.
     */
    public boolean isStandAlone() {
        return standAlone;
    }

    /**
     * Set the value of standAlone.
     *
     * @param v Value to assign to standAlone.
     */
    public void setStandAlone(boolean v) {
        this.standAlone = v;
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

    private ImageButton save, copy, copySwing, copyAWT, copyJTable, reGenerateSwing;
    private JSlider slW, slH;
    public JButton cancel;
    private ImageButton generate;
    private JList columns;
    private Tables table;
    private JTextArea code, codeSwing, codeAWT, codeJTable, messageBoard;
    private TableColumns colu;
    private Vector theTable;
    private Vector allColumns;
    private JTabbedPane jtp;

    String activeClass;

    /**
     * Get the value of activeClass.
     *
     * @return Value of activeClass.
     */
    public String getActiveClass() {
        return activeClass;
    }

    /**
     * Set the value of activeClass.
     *
     * @param v Value to assign to activeClass.
     */
    public void setActiveClass(String v) {
        this.activeClass = v;
    }

    private String swingFile;

    /**
     * Get the value of swingFile.
     *
     * @return Value of swingFile.
     */
    public String getSwingFile() {
        return swingFile;
    }

    /**
     * Set the value of swingFile.
     *
     * @param v Value to assign to swingFile.
     */
    public void setSwingFile(String v) {
        this.swingFile = v;
    }

    private String codeFile;

    /**
     * Get the value of codeFile.
     *
     * @return Value of codeFile.
     */
    public String getCodeFile() {
        return codeFile;
    }

    /**
     * Set the value of codeFile.
     *
     * @param v Value to assign to codeFile.
     */
    public void setCodeFile(String v) {
        this.codeFile = v;
    }

    private String tableFile;

    /**
     * Get the value of the file containing the JTable-Class.
     *
     * @return Value of the file containing the JTable-Class.
     */
    public String getTableFile() {
        return tableFile;
    }

    /**
     * Set the value of the file containing the JTable-Class.
     *
     * @param v Value to assign to the file containing the JTable-Class..
     */
    public void setTableFile(String v) {
        this.tableFile = v;
    }

    boolean oracle = false;

    /**
     * Get the value of oracle.
     *
     * @return Value of oracle.
     */
    public boolean getOracle() {
        return oracle;
    }

    /**
     * Set the value of oracle.
     *
     * @param v Value to assign to oracle.
     */
    public void setOracle(boolean v) {
        this.oracle = v;
    }

    public GenerateCode(String schema) {

        this.setSchema(schema);
        init();
    }

    public GenerateCode() {

        this.setDatabase(database);
        this.setSchema("%");
        init();
    }

    public static void main(String args[]) {

        final CloseableFrame cf = new CloseableFrame("GenerateCode");
        String schema = "%";

        System.out.println("\nFredy Fischer"
                + "\n-------------\n"
                + "Generate Code , Version 1.2\n"
                + "Syntax:\n"
                + "java sql.fredy.generate.GenerateCode {-Dimagedir=dir}\n\n"
                + "or direct-access via:\n"
                + "java sql.fredy.admin.GenerateCode {-Dimagedir=dir} -s Schema");
        if (args.length < 1) {
            GenerateCode gt = new GenerateCode();
        } else {
            int i = 0;
            while (i < args.length) {

                if (args[i].equals("-s")) {
                    i++;
                    schema = args[i];
                }
                i++;
            }
            GenerateCode gt = new GenerateCode(schema);

            gt.cancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            cf.getContentPane().add(gt);
            cf.pack();
            cf.setVisible(true);
        }

    }

    private void init() {

        DbInfo dbi;
        dbi = new DbInfo();

        if (dbi.getProductName().toLowerCase().indexOf("oracle") != -1) {
            setOracle(true);
        }

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 2.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(tablePanel(), gbc);

        gbc.gridy = 1;
        this.add(codePanel(), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridy = 2;
        this.add(buttonPanel(), gbc);

    }

    private JPanel tablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED), "Tables"));

        // the Tables
        table = new Tables(getSchema());

        // set the Listener onto the table-List
        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    setTable();
                }
                if (e.getClickCount() == 2) {
                    setTable();
                }
            }
        };
        table.addMouseListener(mouseListener);
        table.setToolTipText("these are the TABLES in DB " + getDatabase() + " within the Schema " + getSchema());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JScrollPane(table), gbc);

        // the Rows....
        columns = new JList();
        ListSelectionModel lsm = columns.getSelectionModel();
        columns.setSelectionMode(lsm.MULTIPLE_INTERVAL_SELECTION);
        columns.setToolTipText("here are the columns of the selected table"
                + " and its Primary Keys must be selected, "
                + "if possible it is done automatically");

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel.add(new JScrollPane(columns), gbc);

        return panel;
    }

    private JPanel codePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(textPanel(), gbc);

        return panel;
    }

    private JTabbedPane textPanel() {

        jtp = new JTabbedPane();
        jtp.addTab("SQL-Wrapper", loadImage.getImage("sqlwrapper.gif"), wrapperPanel());
        jtp.addTab("Swing-Code", loadImage.getImage("swing.gif"), swingPanel());
        jtp.addTab("JTableModel", loadImage.getImage("sheet.gif"), jtablePanel());

        jtp.addTab("Messages", null, msgPanel());
        return jtp;

    }

    private JPanel wrapperPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EtchedBorder());

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
        code = new JTextArea(10, 40);
        java.awt.Font f = new java.awt.Font("Monospaced", Font.PLAIN, 10);
        code.setFont(f);
        code.setEditable(false);
        code.setBackground(Color.lightGray);
        panel.add(new JScrollPane(code), gbc);

        final Toolkit tk = this.getToolkit();
        copy = new ImageButton(null, "copy.gif", "Copies the wrapper-code to the clipboard");
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = code.getText();
                StringSelection ss = new StringSelection(s);
                tk.getSystemClipboard().setContents(ss, ss);
                code.selectAll();
            }
        });
        copy.setEnabled(false);

        final JComboBox fontSize = new JComboBox(new String[]{"6", "8", "10", "12", "14", "16", "18", "20"});
        fontSize.setSelectedItem((String) "10");
        fontSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = Integer.parseInt((String) fontSize.getSelectedItem());
                java.awt.Font f = new java.awt.Font("Monospaced", Font.PLAIN, i);
                code.setFont(f);
            }
        });
        JPanel aPanel = new JPanel();
        aPanel.setLayout(new FlowLayout());
        aPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        aPanel.add(new JLabel("Fontsize:"));
        aPanel.add(fontSize);
        aPanel.add(copy);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(aPanel, gbc);

        return panel;
    }

    private JPanel swingPanel() {

        final JPanel swingPanel = new JPanel();
        swingPanel.setLayout(new GridBagLayout());
        swingPanel.setBorder(new EtchedBorder());

        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        slW = new JSlider(JSlider.HORIZONTAL, 5, 50, 25);
        slW.setToolTipText("Selects the width of max. JTextField,\nthen changes to JTextArea");
        //slW.createStandardLabels(1,5);
        slW.setMajorTickSpacing(5);
        slW.setMinorTickSpacing(1);
        slW.setPaintTicks(true);
        slW.setPaintLabels(true);
        slW.setSnapToTicks(true);
        slW.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                JSlider s = (JSlider) event.getSource();
                regenerateCode();
            }
        });
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        swingPanel.add(slW, gbc);

        slH = new JSlider(JSlider.VERTICAL, 2, 50, 10);
        slH.setToolTipText("Selects the max. heigth of a JTextArea");
        //slH.createStandardLabels(1,5);
        slH.setMajorTickSpacing(5);
        slH.setMinorTickSpacing(1);
        slH.setPaintTicks(true);
        slH.setPaintLabels(true);
        slH.setSnapToTicks(true);
        slH.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                JSlider s = (JSlider) event.getSource();
                regenerateCode();
            }
        });

        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        gbc.gridx = 1;
        gbc.gridy = 1;
        swingPanel.add(slH, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        codeSwing = new JTextArea(10, 40);
        java.awt.Font f = new java.awt.Font("Monospaced", Font.PLAIN, 10);
        codeSwing.setFont(f);
        codeSwing.setEditable(false);
        codeSwing.setBackground(Color.lightGray);
        swingPanel.add(new JScrollPane(codeSwing), gbc);

        final Toolkit tk = this.getToolkit();
        copySwing = new ImageButton(null, "copy.gif", "Copies the wrapper-code to the clipboard");
        copySwing.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = codeSwing.getText();
                StringSelection ss = new StringSelection(s);
                tk.getSystemClipboard().setContents(ss, ss);
                codeSwing.selectAll();
            }
        });
        copySwing.setEnabled(false);

        final JComboBox fontSize = new JComboBox(new String[]{"6", "8", "10", "12", "14", "16", "18", "20"});
        fontSize.setSelectedItem((String) "10");
        fontSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = Integer.parseInt((String) fontSize.getSelectedItem());
                java.awt.Font f = new java.awt.Font("Monospaced", Font.PLAIN, i);
                codeSwing.setFont(f);
            }
        });

        ImageButton refreshSwing = new ImageButton("Refresh", "refresh.gif", "Press this if the Sliders are not visible");
        refreshSwing.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                slH.updateUI();
                slW.updateUI();
                swingPanel.updateUI();
            }
        });

        reGenerateSwing = new ImageButton("Generate", "redo.gif", "press this to regenerate Swing-Interface");
        reGenerateSwing.setEnabled(false);
        reGenerateSwing.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                GenerateSwing gs = new GenerateSwing(theTable, getDatabase(), allColumns);
                gs.setOracle(getOracle());
                gs.setLng(slW.getValue());
                gs.setNoRows(slH.getValue());
                codeSwing.setText(gs.getSwingCode());
                codeSwing.updateUI();

            }
        });

        JPanel aPanel = new JPanel();
        aPanel.setLayout(new FlowLayout());
        aPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        aPanel.add(new JLabel("Fontsize:"));
        aPanel.add(fontSize);
        aPanel.add(copySwing);
        aPanel.add(refreshSwing);
        aPanel.add(reGenerateSwing);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        swingPanel.add(aPanel, gbc);

        return swingPanel;
    }

    private JPanel msgPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EtchedBorder());

        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        messageBoard = new JTextArea(10, 40);
        messageBoard.setEditable(false);
        messageBoard.setBackground(Color.red);
        messageBoard.setForeground(Color.yellow);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JScrollPane(messageBoard), gbc);

        ImageButton msgClear = new ImageButton("clear", "clear.gif", "Clear Message Board");
        msgClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                messageBoard.setText("");
            }
        });

        final JComboBox fontSize = new JComboBox(new String[]{"6", "8", "10", "12", "14", "16", "18", "20"});
        fontSize.setSelectedItem((String) "10");
        fontSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = Integer.parseInt((String) fontSize.getSelectedItem());
                java.awt.Font f = new java.awt.Font("Monospaced", Font.PLAIN, i);
                messageBoard.setFont(f);
            }
        });

        JPanel aPanel = new JPanel();
        aPanel.setLayout(new FlowLayout());
        aPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        aPanel.add(new JLabel("Fontsize:"));
        aPanel.add(fontSize);
        aPanel.add(msgClear);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(aPanel, gbc);
        return panel;
    }

    private JPanel jtablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EtchedBorder());

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
        codeJTable = new JTextArea(10, 40);
        java.awt.Font f = new java.awt.Font("Monospaced", Font.PLAIN, 10);
        codeJTable.setFont(f);
        codeJTable.setEditable(false);
        codeJTable.setBackground(Color.lightGray);
        panel.add(new JScrollPane(codeJTable), gbc);

        final Toolkit tk = this.getToolkit();
        copyJTable = new ImageButton(null, "copy.gif", "Copies the wrapper-code to the clipboard");
        copyJTable.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = codeJTable.getText();
                StringSelection ss = new StringSelection(s);
                tk.getSystemClipboard().setContents(ss, ss);
                codeJTable.selectAll();
            }
        });
        copyJTable.setEnabled(false);

        final JComboBox fontSize = new JComboBox(new String[]{"6", "8", "10", "12", "14", "16", "18", "20"});
        fontSize.setSelectedItem((String) "10");
        fontSize.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int i = Integer.parseInt((String) fontSize.getSelectedItem());
                java.awt.Font f = new java.awt.Font("Monospaced", Font.PLAIN, i);
                codeJTable.setFont(f);
            }
        });
        JPanel aPanel = new JPanel();
        aPanel.setLayout(new FlowLayout());
        aPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        aPanel.add(new JLabel("Fontsize:"));
        aPanel.add(fontSize);
        aPanel.add(copyJTable);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(aPanel, gbc);

        return panel;
    }

    private void setTable() {
        
        colu = new TableColumns(table.getSelectedValue().toString());

        theTable = colu.getAllCols();
        allColumns = colu.getColumnInfo();
        String[] s;
        s = new String[theTable.size()];
        for (int i = 0; i < theTable.size(); i++) {
            Columns c = new Columns();
            c = (Columns) theTable.elementAt(i);
            s[i] = c.getName();
        }
        columns.setListData(s);
        setPrimaryKeys();
        columns.setToolTipText("these are the columns of " + table.getSelectedValue().toString()
                + " and its Primary Keys must be selected, if possible it is done automatically");
        columns.updateUI();
    }

    private void setPrimaryKeys() {
        DbInfo dbi = null;        
        dbi = new DbInfo();
        
        Vector pkv = new Vector();
        pkv = dbi.getPk(table.getSelectedValue().toString());
        int idx[] = new int[pkv.size()];
        for (int m = 0; m < pkv.size(); m++) {
            PrimaryKey pk = new PrimaryKey();
            pk = (PrimaryKey) pkv.elementAt(m);
            ListModel lm = columns.getModel();
            for (int i = 0; i < lm.getSize(); i++) {
                String s1 = (String) lm.getElementAt(i);
                String s2 = (String) pk.getColumnName();
                if (s1.equals(s2)) {
                    idx[m] = i;
                }
            }
        }
        columns.setSelectedIndices(idx);
        if (isStandAlone()) {
            dbi.close();
        }

    }

    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

        generate = new ImageButton(null, "documentdraw.gif", "Generates the Java-Code");
        generate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (columns.getSelectedIndices().length > 0) {
                    generate();
                    save.setEnabled(true);
                    copy.setEnabled(true);
                    copySwing.setEnabled(true);
                    copyJTable.setEnabled(true);
                    reGenerateSwing.setEnabled(true);
                } else {
                    message("you need to selected a table and Primary-Key(s)");
                }
            }
        });

        ImageButton compile = new ImageButton(null, "hammer.gif", "compile");
        compile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setActiveClass("applications." + getDatabase() + "." + firstUpper(table.getSelectedValue().toString() + "Grid"));
                execCMD("javac " + getCodeFile());
                execCMD("javac " + getSwingFile());
            }
        });

        ImageButton runit = new ImageButton(null, "enter.gif", "Run it in a Frame");
        runit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runJava();
            }
        });

        cancel = new JButton();
        ImageButton imb = new ImageButton();
        ImageIcon i1 = imb.getImageIcon("exit.gif");
        ImageIcon i2 = imb.grayed(i1.getImage());
        cancel.setIcon(i2);
        cancel.setRolloverIcon(i1);
        cancel.setRolloverEnabled(true);
        cancel.setToolTipText("Exit");

        save = new ImageButton(null, "save.gif", "Stores the Code in the directory [dir]/applications/" + getDatabase());
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveCode();
                save.setEnabled(false);
            }
        });
        save.setEnabled(false);

        /**
         * final Toolkit tk = this.getToolkit(); copy = new
         * JButton(loadImage.getImage("copy.gif")); copy.setToolTipText("Copies
         * the code to the clipboard"); copy.addActionListener(new
         * ActionListener() { public void actionPerformed(ActionEvent e) {
         * String s = code.getText(); StringSelection ss = new
         * StringSelection(s); tk.getSystemClipboard().setContents(ss,ss); }});
         * copy.setEnabled(false);
	*
         */
        panel.add(generate);
        panel.add(cancel);
        panel.add(save);
        //panel.add(copy);
        panel.add(compile);
        panel.add(runit);
        return panel;

    }

    private void saveCode() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        String dir = "";
        chooser.setDialogTitle("Choose Directory");

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            dir = chooser.getCurrentDirectory() + File.separator + chooser.getSelectedFile().getName();

            FileInfo fi = new FileInfo(dir, null);
            if (fi.getTyp() == 1) {
                reallySave(dir);
            } else {
                message("This is not a directory");
            }
        } else {
            message("Did not save");
        }
    }

    private void reallySave(String dir) {

        setCodeFile(dir + File.separator + "applications" + File.separator + getDatabase() + File.separator + firstUpper(table.getSelectedValue().toString() + "Row.java"));

        setSwingFile(dir + File.separator + "applications" + File.separator + getDatabase() + File.separator + firstUpper(table.getSelectedValue().toString() + "Grid.java"));

        setTableFile(dir + File.separator + "applications" + File.separator + getDatabase() + File.separator + firstUpper(table.getSelectedValue().toString() + "TableModel.java"));

        writeFile(dir, getCodeFile(), code.getText());
        writeFile(dir, getSwingFile(), codeSwing.getText());
        writeFile(dir, getTableFile(), codeJTable.getText());

        setActiveClass("applications." + getDatabase() + "." + firstUpper(table.getSelectedValue().toString() + "Grid.java"));

    }

    private void writeFile(String dir, String file, String content) {

        // create directory if not existing
        File f = new File(dir + File.separator + "applications" + File.separator + getDatabase());
        f.mkdir();

        try {
            DataOutputStream outptstr = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(file)));
            outptstr.writeBytes(content);
            outptstr.flush();
            outptstr.close();

            message("Created: " + file + " \n ");

        } catch (IOException execp) {
            message(execp.toString());
        }

    }

    private void execCMD(String cmdx) {

        final String cmd = cmdx;
        Thread t = new Thread() {
            public void run() {

                Cursor cu = new Cursor(Cursor.WAIT_CURSOR);
                jtp.setCursor(cu);

                message(cmd + "\n");
                try {
                    Runtime rt = Runtime.getRuntime();
                    Process prcs = rt.exec(cmd);
                    DataInputStream d = new DataInputStream(
                            new BufferedInputStream(
                                    prcs.getInputStream()));

                    String line;
                    BufferedReader bufrd = new BufferedReader(
                            new InputStreamReader(d));
                    while ((line = bufrd.readLine()) != null) {
                        message(line);
                    }
                    message("Done: " + cmd + "\n");
                    Cursor cu1 = new Cursor(Cursor.DEFAULT_CURSOR);
                    jtp.setCursor(cu1);
                } catch (IOException ioe) {
                    message("IO-Exception: " + ioe);
                }
            }
        };
        t.start();
    }

    private void runJava() {
        Thread t = new Thread() {
            public void run() {

                try {
                    Class ca = Class.forName(getActiveClass());
                    Constructor cons[] = ca.getConstructors();                    
                    cons[0].newInstance();
                    final JFrame frame = new JFrame(firstUpper(table.getSelectedValue().toString() + "Grid"));
                    frame.getContentPane().add((JTabbedPane) cons[0].newInstance());
                    frame.addWindowListener(new WindowAdapter() {
                        public void windowActivated(WindowEvent e) {
                        }

                        public void windowClosed(WindowEvent e) {
                        }

                        public void windowClosing(WindowEvent e) {
                            frame.dispose();
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
                } catch (Exception cne) {
                    message(cne.toString());
                }
            }
        };
        t.start();
    }

    private void message(String msg) {

        messageBoard.append("\n" + msg);
        messageBoard.updateUI();
        jtp.setSelectedIndex(2);

    }

    private String firstUpper(String s) {
        s = s.substring(0, 1).toUpperCase() + s.substring(1);
        return s;
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
    
    
    
    private String getDBMS() {
 
        Connection con = null;
        
        try {
            DatabaseMetaData md = getConnection().getMetaData();
            if (isStandAlone()) {
                con.close();
            }
            return md.getDatabaseProductName();
        } catch (SQLException exception) {            
            return "not applicable";
        } finally {
            if ( con !=null ) {
                try {
                    con.close();
                } catch (SQLException  e) {
                    logger.log(Level.WARNING,"Exception while closing connection {0}", e.getMessage());
                }
            }
        }

    }

    private ImageIcon loadImage(String image) {
        return loadImage.getImage(image);
    }

    private String toDay() {

        Calendar c = Calendar.getInstance();

        return Integer.toString(c.get(Calendar.YEAR)) + "-"
                + Integer.toString(c.get(Calendar.MONTH) + 1) + "-"
                + Integer.toString(c.get(Calendar.DATE));

    }

    private String getPrimaryKey() {

        String pk = " (";
        int p[] = columns.getSelectedIndices();

        for (int i = 0; i < p.length; i++) {
            Columns c = new Columns();
            c = (Columns) theTable.elementAt(p[i]);

            if (i > 0) {
                pk = pk + " + \" and ";
            }
            pk = pk + c.getName() + " = ";
            if (getType(i).startsWith("int")) {
                pk = pk + " \" + Integer.toString(get" + firstUpper(c.getName()) + "()) ";
            }
            if (getType(i).startsWith("String")) {
                pk = pk + " \" + \"'\" + get" + firstUpper(c.getName()) + "() + \"'\" ";
            }
            if (getType(i).startsWith("java.sql.Date")) {
                pk = pk + " \" + \"'\" + get" + firstUpper(c.getName()) + "() + \"'\" ";
            }
            if (getType(i).startsWith("float")) {
                pk = pk + " \" + Float.toString(get" + firstUpper(c.getName()) + "()) ";
            }
            if (getType(i).startsWith("double")) {
                pk = pk + " \" + Double.toString(get" + firstUpper(c.getName()) + "()) ";
            }
            if (getType(i).endsWith("Number")) {
                pk = pk + " \" + get" + firstUpper(c.getName()) + "().stringValue() ";
            }

        }
        pk = pk + " + \")\"";
        return pk;

    }

    // Generate other-Code
    private void regenerateCode() {
        GenerateSwing gs = new GenerateSwing(theTable, getDatabase(), allColumns);
        gs.setOracle(getOracle());
        gs.setLng(slW.getValue());
        gs.setNoRows(slH.getValue());
        codeSwing.setText(gs.getSwingCode());
        codeSwing.updateUI();

        GenerateJTableModel gjtm = null;
        gjtm = new GenerateJTableModel(table.getSelectedValue().toString(),getSchema());        

        codeJTable.setText(gjtm.getTheCode());
        codeJTable.updateUI();
    }

    private String getType(int i) {
        String t = "String";
        String s;
        SingleColumnInfo sci = new SingleColumnInfo();
        sci = (SingleColumnInfo) allColumns.elementAt(i);

        if (sci.getType_name().toLowerCase().startsWith("char")) {
            t = "String";
        }
        if (sci.getType_name().toLowerCase().startsWith("varchar")) {
            t = "String";
        }
        if (sci.getType_name().toLowerCase().startsWith("longvarchar")) {
            t = "String";
        }
        if (sci.getType_name().toLowerCase().startsWith("binary")) {
            t = "String";
        }
        if (sci.getType_name().toLowerCase().startsWith("longvarbinary")) {
            t = "String";
        }
        if (sci.getType_name().toLowerCase().startsWith("varbinary")) {
            t = "String";
        }
        if (sci.getType_name().toLowerCase().startsWith("date")) {
            t = "java.sql.Date";
        }
        if (sci.getType_name().toLowerCase().startsWith("time")) {
            t = "java.sql.Time";
        }
        if (sci.getType_name().toLowerCase().startsWith("timestamp")) {
            t = "java.sql.Timestamp";
        }
        if (sci.getType_name().toLowerCase().startsWith("int")) {
            t = "int";
        }
        if (sci.getType_name().toLowerCase().startsWith("double")) {
            t = "double";
        }
        if (sci.getType_name().toLowerCase().startsWith("float")) {
            t = "float";
        }
        if (sci.getType_name().toLowerCase().startsWith("decimal")) {
            t = "float";
        }
        if (sci.getType_name().toLowerCase().startsWith("number")) {
            t = "float";
        }

        return t;
    }

    private String getInitValue(String s) {
        String t = "\"\"";
        if (s.startsWith("int")) {
            t = "0";
        }
        if (s.startsWith("double")) {
            t = "0";
        }
        if (s.startsWith("float")) {
            t = "0";
        }
        if (s.startsWith("decimal")) {
            t = "0";
        }
        //if (s.endsWith("Number"))   t = "new NUMBER(0)";
        if (s.startsWith("java.sql.Date")) {
            t = "java.sql.Date.valueOf(toDay())";
        }
        if (s.endsWith("Timestamp")) {
            t = "new java.sql.Timestamp(System.currentTimeMillis())";
        }

        return t;
    }

    //__________________________________________________________
    private void generate() {

        final Thread generateThread = new Thread() {
            public void run() {

                Cursor cu = new Cursor(Cursor.WAIT_CURSOR);
                jtp.setCursor(cu);
                String theCode;
                theCode = "package applications." + getDatabase() + ";\n\n"
                        + "/** this has been generated by Fredy's Admin-Tool for SQL-Databases\n"
                        + " *  Date: " + toDay() + "\n"
                        + " * \n"
                        + " *  RDBMS:    " + getDBMS() + " \n"
                        + " *  Database: " + getDatabase() + "\n"
                        + " *  Table:    " + table.getSelectedValue().toString() + "\n *\tDescription:\n";

                DbInfo dbi = null;               
                dbi = new DbInfo();
               
                theCode = theCode + " *\n" + dbi.getTableDescription(table.getSelectedValue().toString(), " * ");
                theCode = theCode + " *\n"
                        + " *  Admin is free software (MIT License)\n"
                        + " *\n"
                        + " *  Fredy Fischer\n"
                        + " *  Hulmenweg 36\n"
                        + " *  8405 Winterthur\n"
                        + " *  Switzerland\n"
                        + " *\n"
                        + " * sql@hulmen.ch\n"
                        + " *\n"
                        + " **/\n"
                        + "\n"
                        + "import java.sql.*;\n";

                theCode = theCode + "import java.util.Calendar;\n"
                        + "import applications.basics.t_connect;\n"
                        + "\n"
                        + "public class " + firstUpper(table.getSelectedValue().toString()) + "Row { \n";

                theCode = theCode
                        + "    private  ResultSet sqlresult;\n";

                theCode = theCode + "    private  t_connect con;\n"
                        + "    \n"
                        + "    String ahost;\n"
                        + "  \n"
                        + "    /**\n"
                        + "       * Get the value of host.\n"
                        + "       * @return Value of host.\n"
                        + "       */\n"
                        + "    public String getAHost() {return ahost;}\n"
                        + "    \n"
                        + "    /**\n"
                        + "       * Set the value of host.\n"
                        + "       * @param v  Value to assign to host\n"
                        + "       */\n"
                        + "    public void setAHost(String  v) {this.ahost = v;}\n"
                        + "    \n"
                        + "    String auser;\n"
                        + "    \n"
                        + "    /**\n"
                        + "       * Get the value of user.\n"
                        + "       * @return Value of user.\n"
                        + "       */\n"
                        + "    public String getAUser() {return auser;}\n"
                        + "    \n"
                        + "    /**\n"
                        + "       * Set the value of user.\n"
                        + "       * @param v  Value to assign to user.\n"
                        + "       */\n"
                        + "    public void setAUser(String  v) {this.auser = v;}\n"
                        + "    \n"
                        + "    String apassword;\n"
                        + "    \n"
                        + "    /**\n"
                        + "       * Get the value of password.\n"
                        + "       * @return Value of password.\n"
                        + "       */\n"
                        + "    public String getAPassword() {return apassword;}\n"
                        + "    \n"
                        + "    /**\n"
                        + "       * Set the value of password.\n"
                        + "       * @param v  Value to assign to password.\n"
                        + "       */\n"
                        + "    public void setAPassword(String  v) {this.apassword = v;}\n\n"
                        + "\n"
                        + "    String database;\n"
                        + "    \n"
                        + "    /**\n"
                        + "       * Get the value of database.\n"
                        + "       * @return Value of database.\n"
                        + "       */\n"
                        + "    public String getDatabase() {return database;}\n"
                        + "    \n"
                        + "    /**\n"
                        + "       * Set the value of database.\n"
                        + "       * @param v  Value to assign to database.\n"
                        + "       */\n"
                        + "    public void setDatabase(String  v) {this.database = v;}\n\n";

                // now we are creating the get and set Methods for each column
                for (int i = 0; i < theTable.size(); i++) {
                    Columns c = new Columns();
                    c = (Columns) theTable.elementAt(i);

                    theCode = theCode
                            + "       " + getType(i) + " " + c.getName() + ";\n\n"
                            + "    /**\n"
                            + "       * Set the value of " + c.getName() + ".\n"
                            + "       * @param v  Value to assign to " + c.getName() + " .\n"
                            + "       */\n"
                            + "       public void set" + firstUpper(c.getName()) + "(" + getType(i) + "  v) { this." + c.getName() + " = v;}\n\n"
                            + "    /**\n"
                            + "       * Get the value of " + c.getName() + ".\n"
                            + "       * @return  Value of " + c.getName() + " .\n"
                            + "       */\n"
                            + "       public " + getType(i) + " get" + firstUpper(c.getName()) + "() {return " + c.getName() + ";} \n\n";
                }

                // now we generate the query-Methodes
                theCode = theCode
                        + "    String query;\n"
                        + "    \n"
                        + "    /**\n"
                        + "       * Get the value of query.\n"
                        + "       * @return Value of query.\n"
                        + "       */\n"
                        + "    public String getQuery() {return query;}\n"
                        + "    \n"
                        + "    /**\n"
                        + "       * Set the value of query.\n"
                        + "       * @param v  Value to assign to query.\n"
                        + "       */\n"
                        + "    public void setQuery(String  v) {this.query = v;}\n\n"
                        + "    private String toDay() {\n\n"
                        + "	        Calendar c = Calendar.getInstance();\n\n"
                        + "    	return  Integer.toString(c.get(Calendar.YEAR)) + \"-\" + \n"
                        + "                 Integer.toString(c.get(Calendar.MONTH)+1)+ \"-\" + \n"
                        + "                 Integer.toString(c.get(Calendar.DATE));\n"
                        + "     }\n";

                // this is constructor 1
                theCode = theCode
                        + "    public " + firstUpper(table.getSelectedValue().toString()) + "Row(String ahost, String auser, String apassword) {\n"
                        + "\n"
                        + "	setDatabase(\"" + getDatabase() + "\");\n"
                        + "\n"
                        + "	setAHost(ahost);\n"
                        + "	setAUser(auser);\n"
                        + "	setAPassword(apassword);\n\n";

                // now there is to generate the inits
                for (int i = 0; i < theTable.size(); i++) {
                    Columns c = new Columns();
                    c = (Columns) theTable.elementAt(i);

                    theCode = theCode
                            + "	set" + firstUpper(c.getName()) + "(" + getInitValue(getType(i)) + ");\n";
                }
                theCode = theCode + "\n\n    }\n\n";

                // this is constructor 2
                theCode = theCode
                        + "    public " + firstUpper(table.getSelectedValue().toString()) + "Row(String ahost, String auser, String apassword";
                for (int i = 0; i < theTable.size(); i++) {
                    Columns c = new Columns();
                    c = (Columns) theTable.elementAt(i);
                    if (getType(i).startsWith("java.sql.Date")) {
                        theCode = theCode + ",String " + c.getName();
                    } else {
                        theCode = theCode + "," + getType(i) + " " + c.getName();
                    }
                }
                theCode = theCode + ") {\n"
                        + "\n"
                        + "	setDatabase(\"" + getDatabase() + "\");\n"
                        + "\n"
                        + "	setAHost(ahost);\n"
                        + "	setAUser(auser);\n"
                        + "	setAPassword(apassword);\n\n";

                // now there is to generate the inits
                for (int i = 0; i < theTable.size(); i++) {
                    Columns c = new Columns();
                    c = (Columns) theTable.elementAt(i);

                    if (getType(i).startsWith("java.sql.Date")) {
                        theCode = theCode
                                + "        set" + firstUpper(c.getName()) + "(java.sql.Date.valueOf(" + c.getName() + "));\n";
                    } else {
                        theCode = theCode
                                + "	set" + firstUpper(c.getName()) + "(" + c.getName() + ");\n";
                    }
                }
                theCode = theCode + "\n\n    }"
                        + "    private String execQuery() {\n"
                        + "\n"
                        + "	t_connect c = new t_connect(getAHost(), getAUser(), getAPassword(), getDatabase());\n"
                        + "	if (c.getError() != null) {\n"
                        + "	    return \"Connection Error: \"+ c.getError();\n"
                        + "	}	\n"
                        + "\n"
                        + "\n"
                        + "        try {\n"
                        + "	    int records = c.stmt.executeUpdate(getQuery());\n"
                        + "	    c.close(); 	\n"
                        + "	} catch (Exception e) {\n"
                        + "	    return e.getMessage().toString();\n"
                        + "	}\n"
                        + "	return \"ok\";\n"
                        + "    }\n"
                        + "\n"
                        + "    private String selectQuery() {\n"
                        + "\n"
                        + "	con = new t_connect(getAHost(), getAUser(), getAPassword(), getDatabase());\n"
                        + "	if (con.getError() != null) {\n"
                        + "	    return \"Connection Error: \"+ con.getError();\n"
                        + "	}\n"
                        + "\n"
                        + "	try {\n"
                        + "          sqlresult = con.stmt.executeQuery(getQuery());\n"
                        + "\n"
                        + "	} catch (Exception e) {\n"
                        + "	    return e.getMessage().toString();\n"
                        + "	}\n"
                        + "	return \"ok\";\n"
                        + "    }\n"
                        + "\n"
                        + "\n"
                        + "    public " + firstUpper(table.getSelectedValue().toString()) + "Row next() {\n"
                        + "\n"
                        + "	" + firstUpper(table.getSelectedValue().toString()) + "Row k = new " + firstUpper(table.getSelectedValue().toString()) + "Row(getAHost(),getAUser(),getAPassword());\n"
                        + "	try {\n"
                        + "	    if (sqlresult.next()) {\n";

                for (int i = 0; i < theTable.size(); i++) {
                    Columns c = new Columns();
                    c = (Columns) theTable.elementAt(i);
                    if (getType(i).startsWith("java.sql.Date")) {
                        theCode = theCode + "               k.set" + firstUpper(c.getName()) + "(sqlresult.getDate(\"" + c.getName() + "\"));\n";
                    } else {
                        theCode = theCode + "               k.set" + firstUpper(c.getName()) + "(sqlresult.get" + firstUpper(getType(i)) + "(\"" + c.getName() + "\"));\n";
                    }
                }
                theCode = theCode
                        + "		return k;\n"
                        + "	    } else \n"
                        + "		{ con.close();\n"
                        + "		  return null; \n"
                        + "		}\n"
                        + "	} catch  (Exception e) {\n"
                        + "          return null;\n"
                        + "	}\n"
                        + "    }\n";

                // now we are implementing the data manipulation methods
                // INSERT
                theCode = theCode
                        + "    public String insert() {\n"
                        + "\n"
                        + "      setQuery (\"insert into " + table.getSelectedValue().toString() + " (";
                for (int i = 0; i < theTable.size(); i++) {
                    Columns c = new Columns();
                    c = (Columns) theTable.elementAt(i);

                    if (i == 0) {
                        theCode = theCode + c.getName();
                    } else {
                        theCode = theCode + ", " + c.getName();
                    }
                }
                theCode = theCode + ") values (\" +";
                for (int i = 0; i < theTable.size(); i++) {
                    Columns c = new Columns();
                    c = (Columns) theTable.elementAt(i);
                    if (i > 0) {
                        theCode = theCode + " + \", \" + ";
                    }
                    if (getType(i).startsWith("int")) {
                        theCode = theCode + "Integer.toString(get" + firstUpper(c.getName()) + "())";
                    }
                    if (getType(i).startsWith("String")) {
                        theCode = theCode + "\"'\" + get" + firstUpper(c.getName()) + "() +\"'\"";
                    }
                    if (getType(i).startsWith("java.sql.Date")) {
                        theCode = theCode + "\" { d '\" + get" + firstUpper(c.getName()) + "() +\"'}\"";
                    }
                    if (getType(i).startsWith("float")) {
                        theCode = theCode + "Float.toString(get" + firstUpper(c.getName()) + "())";
                    }
                    if (getType(i).startsWith("double")) {
                        theCode = theCode + "Double.toString(get" + firstUpper(c.getName()) + "())";
                    }
                    if (getType(i).endsWith("Number")) {
                        theCode = theCode + "get" + firstUpper(c.getName()) + "().stringValue()";
                    }
                    if (getType(i).endsWith("Timestamp")) {
                        theCode = theCode + "get" + firstUpper(c.getName()) + "().toString()";
                    }

                }
                theCode = theCode + " + \")\");"
                        + "  \n"
                        + "       return execQuery();\n"
                        + " \n"
                        + "    }\n";

                // UPDATE
                theCode = theCode + "\n\n"
                        + "    public String update() {\n"
                        + "\n"
                        + "	setQuery(\"update " + table.getSelectedValue().toString() + " set ";
                for (int i = 0; i < theTable.size(); i++) {
                    Columns c = new Columns();
                    c = (Columns) theTable.elementAt(i);
                    if (i > 0) {
                        theCode = theCode + " + \", ";
                    }
                    theCode = theCode + c.getName() + " = \" + ";
                    if (getType(i).startsWith("int")) {
                        theCode = theCode + "Integer.toString(get" + firstUpper(c.getName()) + "())";
                    }
                    if (getType(i).startsWith("String")) {
                        theCode = theCode + "\"'\" + get" + firstUpper(c.getName()) + "() + \"'\"";
                    }
                    if (getType(i).startsWith("java.sql.Date")) {
                        theCode = theCode + "\" { d '\" + get" + firstUpper(c.getName()) + "() + \"' } \"";
                    }
                    if (getType(i).startsWith("float")) {
                        theCode = theCode + "Float.toString(get" + firstUpper(c.getName()) + "())";
                    }
                    if (getType(i).startsWith("double")) {
                        theCode = theCode + "Double.toString(get" + firstUpper(c.getName()) + "()) ";
                    }
                    if (getType(i).endsWith("Number")) {
                        theCode = theCode + "get" + firstUpper(c.getName()) + "().stringValue() ";
                    }
                    if (getType(i).endsWith("Timestamp")) {
                        theCode = theCode + "get" + firstUpper(c.getName()) + "().toString()";
                    }

                }
                theCode = theCode + " + \" where " + getPrimaryKey() + " );\n";
                theCode = theCode
                        + "        return execQuery();\n"
                        + "    }\n";

                // DELETE
                theCode = theCode + "\n\n    public String delete() {\n\n";
                theCode = theCode + " 	setQuery(\"delete from " + table.getSelectedValue().toString() + " where " + getPrimaryKey() + ");\n";
                theCode = theCode + "        return execQuery();\n    }";

                // let's generate a searchAll-Methode
                theCode = theCode + "\n\n    public String searchAll() {\n\n";
                theCode = theCode + " 	setQuery(\"select * from " + table.getSelectedValue().toString() + "\");\n";
                theCode = theCode + "        return selectQuery();\n    }";

                // now we generate the Code to find a row by all of its columns
                for (int i = 0; i < theTable.size(); i++) {
                    Columns c = new Columns();
                    c = (Columns) theTable.elementAt(i);

                    theCode = theCode + "\n\n    // if you set exact to true, it will return exact the corresponding row\n";
                    theCode = theCode + "    // if you set exact to false,all rows corresponding  like-Statement will be returned";

                    theCode = theCode + "\n\n    public String searchBy" + firstUpper(c.getName()) + "(" + getType(i) + " " + c.getName() + ", boolean exact) {\n\n";

                    theCode = theCode + "	String e;\n";
                    theCode = theCode + "	if ( exact ) { \n";
                    theCode = theCode + "	   e = \"=\"; } else {\n";
                    theCode = theCode + "	   e = \"like\"; }\n ";

                    theCode = theCode + "	setQuery(\"select * from " + table.getSelectedValue().toString() + " where " + c.getName() + " \" + e + \" \" + ";
                    if (getType(i).startsWith("int")) {
                        theCode = theCode + "Integer.toString(" + c.getName() + ") ";
                    }
                    if (getType(i).startsWith("String")) {
                        theCode = theCode + "\"'\" + " + c.getName() + " + \"'\"";
                    }
                    if (getType(i).startsWith("java.sql.Date")) {
                        theCode = theCode + "\"'\" + " + c.getName() + " + \"'\"";
                    }
                    if (getType(i).startsWith("float")) {
                        theCode = theCode + "Float.toString(" + c.getName() + ") ";
                    }
                    if (getType(i).startsWith("double")) {
                        theCode = theCode + "Double.toString(" + c.getName() + ") ";
                    }
                    if (getType(i).endsWith("Number")) {
                        theCode = theCode + " " + c.getName() + ".stringValue() ";
                    }
                    if (getType(i).endsWith("Timestamp")) {
                        theCode = theCode + c.getName() + ".toString() ";
                    }

                    theCode = theCode + " + \" order by " + c.getName() + "\");\n";
                    theCode = theCode
                            + "	return selectQuery();\n"
                            + "    }\n";
                }

                theCode = theCode + "\n\n}\n";
                code.setText(theCode);
                code.updateUI();

                regenerateCode();  // do generate Swing-Code

                Cursor cu1 = new Cursor(Cursor.DEFAULT_CURSOR);
                jtp.setCursor(cu1);

            }
        };
        generateThread.start();

    }
    //__________________________________________________________

}
