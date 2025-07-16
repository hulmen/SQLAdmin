/**
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
 * POI is from the Apache FoundationM
 *
 *
 * Changes to Jakarta EE 10
 * ------------------------
 * Offer a selection to switch in between javax and jakarta.
 * Default is set to Jakarta
 * Fredy, 2024-03-10  ( Grandpa since today )
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
/**
 * This is to generate Java Code out of the result of a SQL-Query. It generates
 * a Bean-Class containing all Attributes of the result of the query and a
 * JTable-Object to be used in a SWING-Panel.
 */
package sql.fredy.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;
import sql.fredy.infodb.DBparameter;

/**
 *
 * @author sql@hulmen.ch
 */
public class QueryCode extends JFrame implements PropertyChangeListener {

    /**
     * @return the charactersTextfield
     */
    public int getCharactersTextfield() {
        return charactersTextfield;
    }

    /**
     * @param charactersTextfield the charactersTextfield to set
     */
    public void setCharactersTextfield(int charactersTextfield) {
        this.charactersTextfield = charactersTextfield;
    }

    /**
     * @return the columnsTextarea
     */
    public int getColumnsTextarea() {
        return columnsTextarea;
    }

    /**
     * @param columnsTextarea the columnsTextarea to set
     */
    public void setColumnsTextarea(int columnsTextarea) {
        this.columnsTextarea = columnsTextarea;
    }

    /**
     * @return the linesTextarea
     */
    public int getLinesTextarea() {
        return linesTextarea;
    }

    /**
     * @param linesTextarea the linesTextarea to set
     */
    public void setLinesTextarea(int linesTextarea) {
        this.linesTextarea = linesTextarea;
    }

    private String query;
    private ResultSetMetaData rsmd;
    private JTextField packageNameBean, packageNameTable, packageNameManagedBean;
    private JTextField classNameBean, classNameTable, classNameTableBean;
    private JTextField jsfTitle;
    private JCheckBox managedBean, jsfInclude;
    private JRadioButton jakarta;
    private JComboBox beanType, beanTypeManagedBean;
    private JTextField searchFields;
    private RSyntaxTextArea beanCode, tableBeanCode, tableCode, sqlQuery, phpcode, managedBeanCode, jsfPage;
    private Logger logger = Logger.getLogger("sql.fredy.ui.QueryCode");
    private ArrayList<FieldNameType> fieldNameTypes;
    private String dbName;
    private String tableName;
    private String schema = "";

    private JFormattedTextField paramTextFieldLength;
    private JFormattedTextField paramTextAreaLine;
    private JFormattedTextField paramTextAreaRows;
    DBparameter dbparams;

    public final String license = "";

    // from this on, it will generate a TextArea and if smaller just inputText
    private int charactersTextfield = 200;
    private int columnsTextarea = 200;
    private int linesTextarea = 10;

    private ImageButton codeGenBean;

    private JPanel beanPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new TitledBorder("Bean Code"));

        beanCode = new RSyntaxTextArea(20, 80);
        beanCode.setCodeFoldingEnabled(true);
        beanCode.setAntiAliasingEnabled(true);
        beanCode.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());

        panel2.add(new JLabel("Java Package:"));
        packageNameBean = new JTextField(20);
        classNameBean = new JTextField(20);

        StringBuilder packageName = new StringBuilder();
        packageName.append(System.getProperty("user.name")).append(".db.");
        packageName.append(getCleanedDbName()).append(".");

        packageName.append("beans");
        packageNameBean.setText(packageName.toString());
        packageNameBean.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                packageNameBean.setText(packageNameBean.getText().trim());
            }
        });

        StringBuilder className = new StringBuilder();
        className.append("Bean");

        className.append(firstUpper(getTableName()).trim());

        classNameBean.setText(spaceFixer(className.toString()));
        classNameBean.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                classNameBean.setText(classNameBean.getText().trim());
            }
        });

        panel2.add(packageNameBean);
        panel2.add(new JLabel("Classname:"));
        panel2.add(classNameBean);

        managedBean = new JCheckBox("use as managed bean", false);
        managedBean.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                beanType.setEnabled(managedBean.isSelected());
            }
        });
        panel2.add(managedBean);

        String[] beanScope = {"@RequestScoped", "@ViewScoped", "@SessionScoped", "@ApplicationScoped"};
        beanType = new JComboBox(beanScope);
        beanType.setEnabled(managedBean.isSelected());
        panel2.add(beanType);

        codeGenBean = new ImageButton(null, "opendb.gif", "Generate Code");
        codeGenBean.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                beanCode.setText(getBean());
            }
        });

        jakarta = new JRadioButton("Jakarta");
        jakarta.setToolTipText("generate code for Jakarta EE");
        jakarta.setSelected(true);

        if (jakarta.isSelected()) {
            codeGenBean.setToolTipText("Generate code for Jakarta EE");
        } else {
            codeGenBean.setToolTipText("Generate code for javax");
        }

        panel2.add(codeGenBean);

        ImageButton saveCode = new ImageButton(null, "save.gif", "Save to file");
        saveCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter();
                fw.setFilter(new String[]{"java", "TXT"});
                if (beanCode.getText().length() < 1) {
                    beanCode.setText(getBean());
                }
                fw.setContent(beanCode.getText());
                fw.setFileName("?");
                fw.setSwitch("a");
                fw.write();
            }
        });
        panel2.add(saveCode);

        RTextScrollPane sp = new RTextScrollPane(beanCode);

        panel.add(new JScrollPane(panel2), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JList list;

    private JTextField managedBeanName;  // the name of the managedBean

    private JPanel managedBeanPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new TitledBorder("JSF-ManagedBean Code"));

        managedBeanCode = new RSyntaxTextArea(20, 80);
        managedBeanCode.setCodeFoldingEnabled(true);
        managedBeanCode.setAntiAliasingEnabled(true);
        managedBeanCode.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

        // we need some more parameters to build the code
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());

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
        panel2.add(new JLabel("Java Package:"), gbc);

        packageNameManagedBean = new JTextField(20);
        StringBuilder packageName = new StringBuilder();
        packageName.append(System.getProperty("user.name")).append(".db.");
        packageName.append(getCleanedDbName()).append(".");
        packageName.append("beans");
        packageNameManagedBean.setText(packageName.toString());
        packageNameManagedBean.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                packageNameManagedBean.setText(packageNameManagedBean.getText().trim());
            }
        });

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 1;
        gbc.gridy = 0;
        panel2.add(packageNameManagedBean, gbc);

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 2;
        gbc.gridy = 0;
        panel2.add(new JLabel("Classname:"), gbc);

        StringBuilder className = new StringBuilder();
        className.append("ManagedBean");
        className.append(firstUpper(getTableName()));
        managedBeanName = new JTextField(20);
        managedBeanName.setText(spaceFixer(className.toString().trim()));
        managedBeanName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                managedBeanName.setText(managedBeanName.getText().trim());
            }
        });

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel2.add(managedBeanName, gbc);

        String[] beanScope = {"@RequestScoped", "@ViewScoped", "@SessionScoped", "@ApplicationScoped"};
        beanTypeManagedBean = new JComboBox(beanScope);
        gbc.gridx = 4;
        panel2.add(beanTypeManagedBean, gbc);

        ImageButton codeGen = new ImageButton(null, "opendb.gif", "Generate Code");
        jakarta.addActionListener((ActionEvent e) -> {
            if (jakarta.isSelected()) {
                codeGenBean.setToolTipText("Generate code for Jakarta EE");
                codeGen.setToolTipText("Generate code for Jakarta EE");
            } else {
                codeGenBean.setToolTipText("Generate code for javax");
                codeGen.setToolTipText("Generate code for javax");
            }
        });

        JRadioButton javax = new JRadioButton("javax");
        javax.setToolTipText("generate code for javax");

        javax.addActionListener((ActionEvent e) -> {
            if (javax.isSelected()) {
                codeGenBean.setToolTipText("Generate code for javax");
                codeGen.setToolTipText("Generate code for javax");
            } else {
                codeGenBean.setToolTipText("Generate code for Jakarta EE");
                codeGen.setToolTipText("Generate code for Jakarta EE");
            }
        });

        ButtonGroup group = new ButtonGroup();
        group.add(jakarta);
        group.add(javax);

        JPanel typePanel = new JPanel();
        typePanel.setLayout(new FlowLayout());
        typePanel.setBorder(new EtchedBorder());
        typePanel.add(jakarta);
        typePanel.add(javax);

        gbc.gridx = 5;
        panel2.add(typePanel, gbc);
        /*
        gbc.gridx = 6;
        panel2.add(javax, gbc);
         */
        codeGen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                managedBeanCode.setText(getManagedBean("", packageNameManagedBean.getText().trim(), managedBeanName.getText().trim(), getQuery()));
            }
        });

        gbc.gridx = 7;
        panel2.add(codeGen, gbc);

        ImageButton saveCode = new ImageButton(null, "save.gif", "Save to file");
        saveCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter();
                fw.setFilter(new String[]{"java", "TXT"});
                if (managedBeanCode.getText().length() < 1) {
                    managedBeanCode.setText(getManagedBean("", packageNameManagedBean.getText().trim(), managedBeanName.getText().trim(), getQuery()));
                }
                fw.setContent(managedBeanCode.getText());
                fw.setFileName("?");
                fw.setSwitch("a");
                fw.write();
            }
        });
        gbc.gridx = 8;
        panel2.add(saveCode, gbc);

        // for these Fields I generate Searchfields
        JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout());

        DefaultListModel m = new DefaultListModel();
        ArrayList<FieldNameType> a = getFieldNameTypes();
        for (int i = 0; i < a.size(); i++) {
            FieldNameType f = a.get(i);
            m.addElement((String) f.getOriginalColumnName());
        }
        list = new JList(m);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        panel3.add(new JLabel("select searchable Fields"), BorderLayout.NORTH);
        panel3.add(new JScrollPane(list), BorderLayout.CENTER);

        RTextScrollPane sp = new RTextScrollPane(managedBeanCode);

        panel.add(new JScrollPane(panel2), BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel3, sp);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(0.0d);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel phpPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new TitledBorder("PHP Code"));
        phpcode = new RSyntaxTextArea(20, 80);
        phpcode.setCodeFoldingEnabled(true);
        phpcode.setAntiAliasingEnabled(true);
        phpcode.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());
        ImageButton codeGen = new ImageButton(null, "opendb.gif", "Generate Code");
        codeGen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                phpcode.setText(getPhp());
            }
        });
        panel2.add(new JLabel("Click to generate PHP Code"));
        panel2.add(codeGen);

        RTextScrollPane sp = new RTextScrollPane(phpcode);
        panel.add(BorderLayout.CENTER, sp);

        panel.add(BorderLayout.NORTH, panel2);

        return panel;
    }

    private JPanel jsfPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new TitledBorder("JSF Page"));
        jsfPage = new RSyntaxTextArea(20, 80);
        jsfPage.setCodeFoldingEnabled(true);
        jsfPage.setAntiAliasingEnabled(true);
        jsfPage.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSP);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());

        panel2.add(new JLabel("Pagetitle"));
        jsfTitle = new JTextField(20);
        panel2.add(jsfTitle);

        jsfInclude = new JCheckBox("generate for import", false);
        panel2.add(jsfInclude);

        ImageButton codeGen = new ImageButton(null, "opendb.gif", "Generate Code");
        codeGen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jsfPage.setText(getJsfForm());
            }
        });

        codeGen.setToolTipText("Click to generate JSF Page");
        panel2.add(codeGen);

        ImageButton saveCode = new ImageButton(null, "save.gif", "Save to file");
        saveCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter();
                fw.setFilter(new String[]{"xhtml", "TXT"});
                if (jsfPage.getText().length() < 1) {
                    jsfPage.setText(getJsfForm());
                }
                fw.setContent(jsfPage.getText());
                fw.setFileName("?");
                fw.setSwitch("a");
                fw.write();
            }
        });
        panel2.add(saveCode);

        paramTextFieldLength = new JFormattedTextField(NumberFormat.INTEGER_FIELD);
        paramTextAreaLine = new JFormattedTextField(NumberFormat.INTEGER_FIELD);
        paramTextAreaRows = new JFormattedTextField(NumberFormat.INTEGER_FIELD);

        paramTextFieldLength.setColumns(10);
        paramTextAreaLine.setColumns(10);
        paramTextAreaRows.setColumns(10);

        paramTextFieldLength.setBorder(new TitledBorder("Textfield length"));
        paramTextAreaLine.setBorder(new TitledBorder("Textarea # lines"));
        paramTextAreaRows.setBorder(new TitledBorder("Textarea # rows"));

        paramTextFieldLength.addPropertyChangeListener("value", this);
        paramTextAreaLine.addPropertyChangeListener("value", this);
        paramTextAreaRows.addPropertyChangeListener("value", this);

        paramTextFieldLength.setValue(Integer.valueOf(dbparams.getParameter("generate.jsf.textfield.size", "200")));
        paramTextAreaLine.setValue(Integer.valueOf(dbparams.getParameter("generate.jsf.textarea.size", "200")));
        paramTextAreaRows.setValue(Integer.valueOf(dbparams.getParameter("generate.jsf.textarea.lines", "10")));

        JPanel parameterPanel = new JPanel();
        parameterPanel.setLayout(new GridBagLayout());
        Insets insets = new Insets(1, 1, 1, 1);
        GridBagConstraints gbc = new GridBagConstraints();

        /*
        gbc.anchor= GridBagConstraints.NORTHEAST;
        gbc.fill  = GridBagConstraints.NONE;
	gbc.gridwidth = 1;
	gbc.insets = insets;
	gbc.gridx = 0;
	gbc.gridy = 0;
        parameterPanel.add(new JLabel("max. Textfield length"),gbc);
         */
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 0;
        parameterPanel.add(paramTextFieldLength, gbc);

        /*
        gbc.anchor= GridBagConstraints.NORTHEAST;
        gbc.fill  = GridBagConstraints.NONE;
	gbc.gridwidth = 1;
	gbc.insets = insets;
	gbc.gridx = 0;
	gbc.gridy = 1;
        parameterPanel.add(new JLabel("Textarea # lines"),gbc);
         */
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 1;
        parameterPanel.add(paramTextAreaLine, gbc);

        /*
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 2;
        parameterPanel.add(new JLabel("Textarea # rows"), gbc);
         */
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 1;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 2;
        parameterPanel.add(paramTextAreaRows, gbc);

        panel.add(BorderLayout.WEST, new JScrollPane(parameterPanel));

        RTextScrollPane sp = new RTextScrollPane(jsfPage);
        panel.add(BorderLayout.CENTER, sp);

        panel.add(BorderLayout.NORTH, panel2);

        return panel;
    }

    public void propertyChange(PropertyChangeEvent evt) {

        Object src = evt.getSource();
        if (src == paramTextAreaLine) {
            Number n = ((Number) paramTextAreaLine.getValue()).intValue();
            dbparams.setParameter("generate.jsf.textarea.size", Integer.toString(n.intValue()));
        }
        if (src == paramTextFieldLength) {
            Number n = ((Number) paramTextFieldLength.getValue()).intValue();
            dbparams.setParameter("generate.jsf.textfield.size", Integer.toString(n.intValue()));
        }
        if (src == paramTextAreaRows) {
            Number n = ((Number) paramTextAreaRows.getValue()).intValue();
            dbparams.setParameter("generate.jsf.textarea.lines", Integer.toString(n.intValue()));
        }
    }

    private JPanel tableBeanPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new TitledBorder("Bean Code"));

        tableBeanCode = new RSyntaxTextArea(20, 80);
        tableBeanCode.setCodeFoldingEnabled(true);
        tableBeanCode.setAntiAliasingEnabled(true);
        tableBeanCode.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());

        classNameTableBean = new JTextField(20);

        StringBuilder className = new StringBuilder();
        className.append("TableBean");

        // FREDy
        className.append(firstUpper(getTableName()));

        classNameTableBean.setText(className.toString());

        //panel2.add(packageNameBean);
        panel2.add(new JLabel("Classname:"));
        panel2.add(classNameTableBean);

        ImageButton codeGen = new ImageButton(null, "opendb.gif", "Generate Code");
        codeGen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableBeanCode.setText(getTableBean());
            }
        });
        panel2.add(codeGen);

        RTextScrollPane sp = new RTextScrollPane(tableBeanCode);

        panel.add(new JScrollPane(panel2), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JPanel tablePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new TitledBorder("JTable Code"));

        tableCode = new RSyntaxTextArea(20, 80);
        tableCode.setCodeFoldingEnabled(true);
        tableCode.setAntiAliasingEnabled(true);
        tableCode.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout());
        panel2.add(new JLabel("Java Package:"));
        packageNameTable = new JTextField(20);
        classNameTable = new JTextField(20);

        panel2.add(packageNameTable);
        panel2.add(new JLabel("Classname:"));
        panel2.add(classNameTable);

        StringBuilder packageName = new StringBuilder();
        packageName.append(System.getProperty("user.name")).append(".gui.");

        packageName.append(getCleanedDbName().toLowerCase()).append(".");
        packageName.append(getTableName().toLowerCase()).append(".");

        packageName.append("tables");
        packageNameTable.setText(packageName.toString());

        StringBuilder className = new StringBuilder();
        className.append("Table");

        className.append(firstUpper(getTableName().toLowerCase()));

        classNameTable.setText(className.toString());

        ImageButton codeGen = new ImageButton(null, "opendb.gif", "Generate Code");
        codeGen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableCode.setText(getTable());
            }
        });
        panel2.add(codeGen);

        RTextScrollPane sp = new RTextScrollPane(tableCode);

        panel.add(panel2, BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JPanel queryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        sqlQuery = new RSyntaxTextArea(20, 80);
        sqlQuery.setText(getQuery());
        sqlQuery.setCodeFoldingEnabled(true);
        sqlQuery.setAntiAliasingEnabled(true);
        sqlQuery.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        sqlQuery.setEditable(false);
        panel.add(new RTextScrollPane(sqlQuery));
        return panel;
    }

    public QueryCode(String abfrage, ArrayList<FieldNameType> fnt) {

        this.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {

            }

            public void windowClosing(WindowEvent e) {
                close();
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

        dbparams = new DBparameter();
        try {
            setCharactersTextfield(Integer.parseInt(dbparams.getParameter("generate.jsf.textfield.size", "200")));
            setColumnsTextarea(Integer.parseInt(dbparams.getParameter("generate.jsf.textarea.size", "200")));
            setLinesTextarea(Integer.parseInt(dbparams.getParameter("generate.jsf.textarea.lines", "10")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setFieldNameTypes(fnt);
        setQuery(abfrage);

        try {
            FieldNameType ft = fnt.get(0);
            setSchema(ft.getSchema());
        } catch (Exception e) {

        }

        int lng = query.length();
        String title = "Java Code for:";
        if (lng > 30) {
            title = title + query.substring(0, 30) + "...";
        } else {
            title = title + query;
        }

        this.setTitle(title);

        this.getContentPane().setLayout(new BorderLayout());
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Java Bean", null, (JPanel) beanPanel(), "This contains the code for a Java Bean representing the Query");
        tabbedPane.addTab("JSF Managed Bean", null, (JPanel) managedBeanPanel(), "This contains the code for a JSF ManagedBean");
        tabbedPane.addTab("JSF Page", null, (JPanel) jsfPanel(), "This contains the code for a JSF Page");

        /*
           I think these are not so often used
         */
        tabbedPane.addTab("Java JTable Bean", null, (JPanel) tableBeanPanel(), "This contains the code for a Java Bean to be used by the JTable");
        tabbedPane.addTab("JTable", null, (JPanel) tablePanel(), "This contains the code for a JTable representing the Query");
        tabbedPane.addTab("PHP", null, (JPanel) phpPanel(), "This contains the php-code representing the field of the query");
        tabbedPane.addTab("Query", null, (JPanel) queryPanel(), "this is the originating query");

        this.getContentPane().add(BorderLayout.CENTER, tabbedPane);
        this.pack();

        Point p = MouseInfo.getPointerInfo().getLocation();
        this.setLocation(p);

        this.setVisible(true);
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    private void close() {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        query = query.trim();
        if (query.endsWith(";")) {
            query = query.substring(0, query.length() - 1);
        }

        this.query = query;
    }

    /**
     * @return the rsmd
     */
    public ResultSetMetaData getRsmd() {
        return rsmd;
    }

    /**
     * @param rsmd the rsmd to set
     */
    public void setRsmd(ResultSetMetaData rsmd) {
        this.rsmd = rsmd;
    }

    private String getPhp() {
        StringBuilder php = new StringBuilder();
        php.append("<?php");

        for (int i = 0; i < fieldNameTypes.size(); i++) {
            FieldNameType fnt = new FieldNameType();
            fnt = fieldNameTypes.get(i);

            if (i == 0) {
                php.append("\n").append("  ").append("class ").append(fnt.getTable()).append(" {");
            }
            php.append("\n").append("     public $").append(fnt.getName()).append(";");
        }

        // php.append("     public function __toString() ")
        php.append("\n").append("    }\n?>");
        return php.toString();
    }

    TreeSet<String> imports;

    private String getBean() {
        StringBuilder top = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        imports = new TreeSet<String>();

        top.append("/*\n");
        top.append("""
                      This code is generated by Fredy's SQL-Admin Tool visit http://www.hulmen.ch
                      SQL Admin is free software and licensed under MIT-License
                   
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
                   
                   
                    This is the query the Bean represents: 
                   """);

        top.append("\n").append(getQuery()).append("\n\n");
        top.append("""
                   
                    Add your comments, a Description, your license or whatever here
                                      
                   */
                   """);
        top.append("\npackage ").append(packageNameBean.getText()).append("; \n\n");

        // Am I a managed bean
        if (managedBean.isSelected()) {

            if (jakarta.isSelected()) {
                imports.add("import jakarta.faces.application.FacesMessage;");
            } else {
                imports.add("import javax.faces.application.FacesMessage;");
            }

            sb.append("@Named \n").append((String) beanType.getSelectedItem()).append("\n");;

            if (beanType.getSelectedItem().toString().toLowerCase().substring(1).startsWith("view")) {

                if (jakarta.isSelected()) {
                    imports.add("import jakarta.faces.view.ViewScoped;");
                } else {
                    imports.add("import org.omnifaces.cdi.ViewScoped;");
                }
            } else {
                if (jakarta.isSelected()) {
                    imports.add("import jakarta.enterprise.context." + ((String) beanType.getSelectedItem()).substring(1) + ";");
                } else {
                    imports.add("import javax.enterprise.context." + ((String) beanType.getSelectedItem()).substring(1) + ";");
                }
            }
            imports.add("import " + (jakarta.isSelected() ? "jakarta" : "javax") + ".inject.Named;");

            imports.add("import java.io.Serializable;");

            sb.append("public class ").append(classNameBean.getText().trim()).append(" implements Serializable {\n\n");
        } else {
            imports.add("import java.io.Serializable;");
            sb.append("public class ").append(classNameBean.getText().trim()).append(" implements Serializable {\n\n");
        }

        //top.append("import java.util.Vector;\n");
        for (int i = 0; i < fieldNameTypes.size(); i++) {

            FieldNameType fnt = new FieldNameType();
            fnt = fieldNameTypes.get(i);
            // fix the import stuff
            if (fnt.getType().equals("BigDecimal")) {
                imports.add("import java.math.BigDecimal;\n");
            }
            if (fnt.getType().equals("java.sql.Date")) {
                imports.add("import java.sql.Date;\n");
            }
            if (fnt.getType().equals("java.sql.Timestamp")) {
                imports.add("import java.sql.Timestamp;\n");
            }
            if (fnt.getType().equals("java.sql.Time")) {
                imports.add("import java.sql.Time;\n");
            }

            sb.append("\nprivate ").append(fnt.getType()).append(" ").append(firstLower(spaceFixer(fnt.getName()))).append(";");
            sb.append("\n\npublic void set").append(firstUpper(spaceFixer(fnt.getName()))).append("(").append(fnt.getType()).append(" v){");

            if ((fnt.getType().equals("java.sql.Date"))
                    || (fnt.getType().equals("java.sql.Timestamp"))
                    || (fnt.getType().equals("java.sql.Time"))) {
                sb.append("\n\tif ( v != null ) {");
                sb.append("\n\t\tthis.").append(firstLower(spaceFixer(fnt.getName()))).append(" = v;");
                sb.append("\n\t\tthis.").append(firstLower(spaceFixer(fnt.getName()))).append("_ = new java.util.Date(v.getTime());");
                sb.append("\n\t}");
            } else {
                sb.append("\n\tthis.").append(firstLower(spaceFixer(fnt.getName()))).append(" = v;");

            }

            sb.append("\n}\n");
            sb.append("\n\npublic ").append(fnt.getType()).append(" get").append(firstUpper(spaceFixer(fnt.getName()))).append("(){");
            sb.append("\n\t return ").append(firstLower(spaceFixer(fnt.getName()))).append(";\n}\n");

            /*
            There is a mess in between SQL-Dates and Util Dates and JSF Objects
            So we add for every SQL-Date or Timeobject a java.util.Date
             */
            if ((fnt.getType().equals("java.sql.Date"))
                    || (fnt.getType().equals("java.sql.Timestamp"))
                    || (fnt.getType().equals("java.sql.Time"))) {
                sb.append("\nprivate java.util.Date ").append(firstLower(spaceFixer(fnt.getName()))).append("_ ;");
                sb.append("\n\npublic void set").append(firstUpper(spaceFixer(fnt.getName()))).append("_(").append("java.util.Date v) {");
                sb.append("\n\tif ( v != null) { ");
                if ((fnt.getType().equals("java.sql.Date"))) {
                    sb.append("\n\t\tthis.").append(firstLower(spaceFixer(fnt.getName()))).append(" = new java.sql.Date(v.getTime());");
                }
                if ((fnt.getType().equals("java.sql.Timestamp"))) {
                    sb.append("\n\t\tthis.").append(firstLower(spaceFixer(fnt.getName()))).append(" = new java.sql.Timestamp(v.getTime());");
                }
                if ((fnt.getType().equals("java.sql.Time"))) {
                    sb.append("\n\t\tthis.").append(firstLower(spaceFixer(fnt.getName()))).append(" = new java.sql.Time(v.getTime());");
                }
                sb.append("\n\t\tthis.").append(firstLower(spaceFixer(fnt.getName()))).append("_ = v;\n\t}\n}");
                sb.append("\n\npublic ").append(" java.util.Date get").append(firstUpper(spaceFixer(fnt.getName()))).append("_(){");
                sb.append("\n\t return ").append(firstLower(spaceFixer(fnt.getName()))).append("_;\n}\n");
            }

        }

        if (managedBean.isSelected()) {
            sb.append(getAjaxFunction());
            sb.append("""
                       /*
                      
                       These methods are added to be used to sent messages to your application
                       Use the faces messagess- or growl Tag to make them appear on your browser
                       The messages contained within the parameter msg will be displayed.
                       
                       */
                      
                      public void info(String msg) {
                         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Info", msg));
                      }
                      
                      public void warn(String msg) {
                          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Warning!", msg));
                      }
                      
                      public void error(String msg) {
                          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error!", msg));
                      }
                      
                      public void fatal(String msg) {
                         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Fatal!", msg));
                      }
                      """);
        } else {
            sb.append(clearMethode());
        }

        Iterator<String> iterator = imports.iterator();
        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            top.append("\n").append(s);
        }

        sb.append("\n}\n");
        top.append("\n\n\n").append(sb);

        return top.toString();

    }

    private String firstUpper(String s) {
        s = s.substring(0, 1).toUpperCase() + s.substring(1);
        return s;
    }

    /*
      Create a JSF Form out of the FieldNameTypes
     */
    private String getJsfForm() {
        XhtmlHelper helper = new XhtmlHelper();

        StringBuilder jsf = new StringBuilder();
        ArrayList<FieldNameType> fields = getFieldNameTypes();

        if (jsfInclude.isSelected()) {
            jsf.append(helper.includeHead);
        } else {
            jsf.append(helper.getPageHead(jsfTitle.getText()));
        }

        // the Form
        jsf.append("\n\t<h:form id=\"form_").append(spaceFixer(getTableName())).append("\" >\n");
        jsf.append("\t\t<p:growl id=\"").append(spaceFixer(getTableName())).append("_").append("growl\"").append(" showDetail=\"true\" sticky=\"true\" />\n");
        jsf.append("\t\t<h:panelGrid id=\"").append(spaceFixer(getTableName())).append("_").append("grid\" columns=\"2\" border=\"0\" >\n");

        // we loop the fields
        Iterator iter = fields.iterator();
        while (iter.hasNext()) {
            FieldNameType fnt = (FieldNameType) iter.next();

            // the Label
            jsf.append("\n\t\t\t").append("<h:outputLabel for=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" value=\"").append(firstUpper(fnt.getName())).append("\" />\n");

            // what Type is it?
            boolean read = false;
            if (fnt.getType().equalsIgnoreCase("String")) {
                if (fnt.getLength() > getCharactersTextfield()) {
                    if (fnt.isPrimaryKey()) {
                        jsf.append("\t\t\t").append("<h:inputTextarea rows=\"").append(Integer.toString(getLinesTextarea())).append("\" cols=\"").append(Integer.toString(getColumnsTextarea())).append("\" readonly=\"true\" id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    } else {
                        jsf.append("\t\t\t").append("<h:inputTextarea rows=\"").append(Integer.toString(getLinesTextarea())).append("\" cols=\"").append(Integer.toString(getColumnsTextarea())).append("\" id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    }
                } else {
                    if (fnt.isPrimaryKey()) {
                        jsf.append("\t\t\t").append("<h:outputText id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    } else {
                        jsf.append("\t\t\t").append("<h:inputText id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    }
                }
                jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" />\n");

                read = true;
            }
            if (fnt.getType().equalsIgnoreCase("boolean")) {
                jsf.append("\t\t\t").append("<h:selectBooleanCheckbox id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                if (fnt.isPrimaryKey()) {
                    jsf.append(" readonly=\"true\" ");
                }
                jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" />\n");
                read = true;
            }
            if ((fnt.getType().equalsIgnoreCase("int"))
                    || (fnt.getType().equalsIgnoreCase("short"))
                    || (fnt.getType().equalsIgnoreCase("long"))) {

                jsf.append("\t\t\t").append("<h:inputText id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                if (fnt.isPrimaryKey()) {
                    jsf.append(" readonly=\"true\" ");
                }
                jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" >\n");
                jsf.append("\t\t\t\t").append("<f:convertNumber integerOnly=\"true\" />\n");
                jsf.append("\t\t\t").append("</h:inputText>\n");

                read = true;
            }
            if ((fnt.getType().equalsIgnoreCase("BigDecimal"))
                    || (fnt.getType().equalsIgnoreCase("float"))
                    || (fnt.getType().equalsIgnoreCase("double"))) {

                jsf.append("\t\t\t").append("<h:inputText id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                if (fnt.isPrimaryKey()) {
                    jsf.append(" readonly=\"true\" ");
                }
                jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" >\n");
                jsf.append("\t\t\t\t").append("<f:convertNumber pattern=\"#0.000\" />\n");
                jsf.append("\t\t\t").append("</h:inputText>\n");

                read = true;
            }

            if (fnt.getType().equalsIgnoreCase("java.sql.Date")) {
                if (fnt.isPrimaryKey()) {
                    jsf.append("\t\t\t").append("<h:outputText id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                    jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("_}\" style=\"float: left;text-align: left\" >\n");
                    jsf.append("\t\t\t\t").append("<f:convertDateTime  type=\"date\" />\n");
                    jsf.append("\t\t\t").append("</h:outputText>\n");
                } else {
                    jsf.append("\t\t\t").append("<p:calendar id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                    jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("_}\" style=\"float: left;text-align: left\" />\n");
                }

                read = true;
            }
            if (fnt.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                if (fnt.isPrimaryKey()) {
                    jsf.append("\t\t\t").append("<h:outputText id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                    jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("_}\" style=\"float: left;text-align: left\" >\n");
                    jsf.append("\t\t\t\t").append("<f:convertDateTime  type=\"datetime\" />\n");
                    jsf.append("\t\t\t").append("</h:outputText>\n");
                } else {
                    jsf.append("\t\t\t").append("<p:calendar id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                    jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("_}\" pattern=\"yyyy-MM-dd HH:mm:ss\" style=\"float: left;text-align: left\" />\n");
                }

                read = true;
            }
            if (fnt.getType().equalsIgnoreCase("java.sql.Time")) {
                if (fnt.isPrimaryKey()) {
                    jsf.append("\t\t\t").append("<h:outputText id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                    jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("_}\" style=\"float: left;text-align: left\" >\n");
                    jsf.append("\t\t\t\t").append("<f:convertDateTime  pattern=\"HH:mm\" />\n");
                    jsf.append("\t\t\t").append("</h:outputText>\n");
                } else {
                    jsf.append("\t\t\t").append("<p:calendar id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                    jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("_}\" pattern=\"HH:mm\" timeOnly=\"true\" style=\"float: left;text-align: left\" />\n");
                }

                read = true;
            }
            if (!read) {
                if (fnt.getLength() > getCharactersTextfield()) {
                    if (fnt.isPrimaryKey()) {
                        jsf.append("\t\t\t").append("<h:inputTextarea rows=\"").append(Integer.toString(getLinesTextarea())).append("\" cols=\"").append(Integer.toString(getColumnsTextarea())).append("\" readonly=\"true\" id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    } else {
                        jsf.append("\t\t\t").append("<h:inputTextarea rows=\"").append(Integer.toString(getLinesTextarea())).append("\" cols=\"").append(Integer.toString(getColumnsTextarea())).append("\" id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    }
                } else {
                    if (fnt.isPrimaryKey()) {
                        jsf.append("\t\t\t").append("<h:outputText id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    } else {
                        jsf.append("\t\t\t").append("<h:inputText id=\"").append(spaceFixer(getTableName())).append("_").append(spaceFixer(fnt.getName())).append("\" ");
                    }
                }
                jsf.append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase());
                jsf.append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" />\n");

                read = true;
            }

        }
        jsf.append("\n\t\t</h:panelGrid>\n");

        // and now the buttons
        jsf.append("\t\t<h:panelGrid columns=\"5\" border=\"0\" >\n");
        jsf.append("\t\t\t").append("<p:commandButton value=\"Update\" action=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".update}\" update=\"").append(spaceFixer(getTableName())).append("_").append("datalist ").append(spaceFixer(getTableName())).append("_").append("growl\" oncomplete=\"PF('").append(spaceFixer(getTableName())).append("_").append("datalistfilter').filter()\" />\n");
        jsf.append("\t\t\t").append("<p:commandButton value=\"Insert\" action=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".insert}\" update=\"").append(spaceFixer(getTableName())).append("_").append("datalist ").append(spaceFixer(getTableName())).append("_").append("growl\" oncomplete=\"PF('").append(spaceFixer(getTableName())).append("_").append("datalistfilter').filter()\" />\n");
        jsf.append("\t\t\t").append("<p:commandButton value=\"Clear\" action=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase()).append(".clear}\" update=\"").append(spaceFixer(getTableName())).append("_").append("grid\" oncomplete=\"PF('").append(spaceFixer(getTableName())).append("_").append("datalistfilter').filter()\"/>\n");
        jsf.append("\t\t\t").append("<p:commandButton value=\"Delete\" action=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".delete}\" update=\"").append(spaceFixer(getTableName())).append("_").append("datalist ").append(spaceFixer(getTableName())).append("_").append("grid ").append(spaceFixer(getTableName())).append("_").append("growl\" oncomplete=\"PF('").append(spaceFixer(getTableName())).append("_").append("datalistfilter').filter()\">\n");
        jsf.append("\t\t\t\t <p:confirm header=\"Delete?\" message=\"Are you sure?\" icon=\"ui-icon-alert\" />\n");
        jsf.append("\t\t\t").append("</p:commandButton>\n");
        jsf.append("\t\t\t").append("<p:commandButton value=\"Get XLSX\" action=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".downloadList}\" ajax=\"false\" />\n");
        jsf.append("\t\t</h:panelGrid>\n\n");

        jsf.append("\t\t<br/><br/>\n");

        // the Table
        jsf.append("\t\t").append("<p:dataTable ");
        jsf.append("\t").append(" id=\"").append(spaceFixer(getTableName())).append("_").append("datalist\"\n");
        jsf.append("\t\t\t\t\t").append(" var=\"v\"\n");
        jsf.append("\t\t\t\t\t").append(" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase()).append("List}\"\n");
        jsf.append("\t\t\t\t\t").append(" scrollable=\"true\"\n");
        jsf.append("\t\t\t\t\t").append(" scrollHeight=\"250\"\n");
        jsf.append("\t\t\t\t\t").append(" widgetVar=\"").append(spaceFixer(getTableName())).append("_").append("datalistfilter\"\n");
        jsf.append("\t\t\t").append(">\n");

        // we need to add a facet, to avoid lots of rows
        jsf.append("\t\t\t<f:facet name=\"header\">\n");
        jsf.append("\t\t\t\t<div align=\"left\">\n");
        jsf.append("\t\t\t\t\t<h:outputLabel for=\"rowstodisplay\" value=\"Rows to display (0 = all) \"  escape=\"false\" />\n");
        jsf.append("\t\t\t\t\t<p:spinner id=\"rowstodisplay\" value=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".rowsToDisplay}\" min=\"0\" stepFactor=\"10\"  />\n");
        jsf.append("\t\t\t\t\t<p:commandButton value=\"Refresh List\"  update=\"").append(spaceFixer(getTableName())).append("_").append("datalist\" oncomplete=\"PF('").append(spaceFixer(getTableName())).append("_").append("datalistfilter').filter()\" />\n");;
        jsf.append("\t\t\t\t</div>\n");
        jsf.append("\t\t\t</f:facet>\n");

        // we start with a select button to transport the record to the grid
        jsf.append("\t\t\t").append("<p:column style=\"width:80px\">\n");
        jsf.append("\t\t\t\t").append("<p:commandButton value=\"Sel\" update=\"form_").append(spaceFixer(getTableName())).append(":").append(spaceFixer(getTableName())).append("_grid\">\n");
        jsf.append("\t\t\t\t\t").append(" <f:setPropertyActionListener value=\"#{v}\" target=\"#{").append(firstLower(managedBeanName.getText().trim())).append(".").append(classNameBean.getText().trim().toLowerCase()).append("}\" />\n");
        jsf.append("\t\t\t\t").append("</p:commandButton>\n");
        jsf.append("\t\t\t").append("</p:column>\n");

        iter = fields.iterator();
        while (iter.hasNext()) {
            boolean read = false;

            FieldNameType fnt = (FieldNameType) iter.next();
            jsf.append("\t\t\t").append("<p:column headerText=\"").append(firstUpper(fnt.getName())).append("\" ");

            // we add a filter as long, as it is a text
            if (fnt.getType().equalsIgnoreCase("String")) {
                jsf.append(" filterBy=\"#{v.").append(spaceFixer(fnt.getName()).toLowerCase()).append("}\"").append(" filterMatchMode=\"contains\" >\n");
            } else {
                jsf.append(" >\n");
            }

            //jsf.append("\t\t\t\t<div align=\"left\" >");
            if (fnt.getType().equalsIgnoreCase("boolean")) {
                jsf.append("\t\t\t\t").append("<h:selectBooleanCheckbox id=\"").append(spaceFixer(fnt.getName())).append("\" readonly=\"true\" ");
                jsf.append(" value=\"#{v").append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" />\n");
                read = true;
            }
            if ((fnt.getType().equalsIgnoreCase("int"))
                    || (fnt.getType().equalsIgnoreCase("short"))
                    || (fnt.getType().equalsIgnoreCase("long"))) {
                jsf.append("\t\t\t\t").append("<h:outputText id=\"").append(spaceFixer(fnt.getName())).append("\" ");
                jsf.append(" value=\"#{v").append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" >\n");
                jsf.append("\t\t\t\t\t").append("<f:convertNumber integerOnly=\"true\" />\n");
                jsf.append("\t\t\t\t").append("</h:outputText>\n");
                //jsf.append("\t\t\t\t</div>\n");
                read = true;
            }
            if ((fnt.getType().equalsIgnoreCase("BigDecimal"))
                    || (fnt.getType().equalsIgnoreCase("float"))
                    || (fnt.getType().equalsIgnoreCase("double"))) {
                jsf.append("\t\t\t\t").append("<h:outputText id=\"").append(spaceFixer(fnt.getName())).append("\" ");
                jsf.append(" value=\"#{v").append(".").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" >\n");
                jsf.append("\t\t\t\t\t").append("<f:convertNumber pattern=\"#0.00\" />\n");
                jsf.append("\t\t\t\t").append("</h:outputText>\n");
                //jsf.append("\t\t\t\t</div>\n");

                read = true;
            }

            if (fnt.getType().equalsIgnoreCase("java.sql.Date")) {
                jsf.append("\t\t\t\t").append("<h:outputText id=\"").append(spaceFixer(fnt.getName())).append("\" ");
                jsf.append(" value=\"#{v.").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" >\n");
                jsf.append("\t\t\t\t\t").append("<f:convertDateTime  pattern=\"yyyy-MM-dd\"  />\n");
                jsf.append("\t\t\t\t").append("</h:outputText>\n");
                //jsf.append("\t\t\t\t</div>\n");
                read = true;
            }

            if (fnt.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                jsf.append("\t\t\t\t").append("<h:outputText id=\"").append(spaceFixer(fnt.getName())).append("\" ");
                jsf.append(" value=\"#{v.").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" >\n");
                jsf.append("\t\t\t\t\t").append("<f:convertDateTime  pattern=\"yyyy-MM-dd HH:mm:ss\"   />\n");
                jsf.append("\t\t\t\t").append("</h:outputText>\n");
                //jsf.append("\t\t\t\t</div>\n");
                read = true;
            }
            if (fnt.getType().equalsIgnoreCase("java.sql.Times")) {
                jsf.append("\t\t\t\t").append("<h:outputText id=\"").append(spaceFixer(fnt.getName())).append("\" ");
                jsf.append(" value=\"#{v.").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" >\n");
                jsf.append("\t\t\t\t\t").append("<f:convertDateTime  type=\"time\"  pattern=\"HH:mm\" timeOnly=\"true\" />\n");
                jsf.append("\t\t\t\t").append("</h:outputText>\n");
                //jsf.append("\t\t\t\t</div>\n");
                read = true;
            }

            if (!read) {
                jsf.append("\t\t\t\t").append("<h:outputText id=\"").append(spaceFixer(fnt.getName())).append("\" ");
                jsf.append(" value=\"#{v.").append(spaceFixer(fnt.getName().toLowerCase())).append("}\" style=\"float: left;text-align: left\" />\n");
                read = true;
            }
            jsf.append("\t\t\t").append("</p:column>\n");
        }

        // Footer with Number of rows
        jsf.append("\t\t\t").append("<f:facet name=\"footer\">\n");
        jsf.append("\t\t\t\t").append("<h:outputText value =\"#{").append(firstLower(managedBeanName.getText().trim())).append(".numberOfRows} Rows in List\" >\n");
        jsf.append("\t\t\t\t\t").append("<f:convertNumber integerOnly=\"true\" pattern=\"#0\" />\n");
        jsf.append("\t\t\t\t").append("</h:outputText>\n");
        jsf.append("\t\t\t").append("</f:facet>\n");

        jsf.append("\t\t").append("</p:dataTable>\n");

        jsf.append("\t</h:form>\n\n");

        // we add the DeleteVerifier in its own form
        jsf.append("\t").append("<ui:remove>\n\tThe comfirmation dialog must be in its own form\n\t</ui:remove>\n\n\t<h:form id=\"confirmForm\" >\n");
        jsf.append("\t\t").append("<p:confirmDialog global=\"true\" showEffect=\"fade\" hideEffect=\"fade\" >\n");
        jsf.append("\t\t\t").append("<p:commandButton value=\"No\" type=\"button\" styleClass=\"ui-confirmdialog-no\" icon=\"ui-icon-close\" />\n");
        jsf.append("\t\t\t").append("<p:commandButton value=\"Yes\" type=\"button\" styleClass=\"ui-confirmdialog-yes\" icon=\"ui-icon-check\" /> \n");
        jsf.append("\t\t").append("</p:confirmDialog>\n\t</h:form>\n");

        jsf.append(helper.license).append("\n\n");

        if (jsfInclude.isSelected()) {
            jsf.append(helper.includeFoot);
        } else {
            jsf.append(helper.pageFoot);
        }

        return jsf.toString();
    }

    /*
      Creates  update for each field to be used with ajax calls for a JSF managed bean representing this table
     */
    private StringBuilder getAjaxFunction() {
        StringBuilder ajax = new StringBuilder();
        String objectType = "String";

        // we do the update Methode        
        ArrayList<FieldNameType> a = getFieldNameTypes();
        ArrayList<FieldNameType> a2 = getFieldNameTypes();
        ArrayList<FieldNameType> a3 = getFieldNameTypes();
        ajax.append("""
                    /*
                       Remember to add the context parameter 'jdbcConnection' to your web.xml containing the JDBC-Datasourcename
                       because it is needed to establish a connection to the database.
                    */""");

        imports.add("import java.sql.Connection;");
        imports.add("import java.sql.PreparedStatement;");
        imports.add("import java.sql.SQLException;");

        if (jakarta.isSelected()) {
            imports.add("import jakarta.faces.context.FacesContext;");
            imports.add("import jakarta.faces.event.AjaxBehaviorEvent;");

        } else {
            imports.add("import javax.faces.context.FacesContext;");
            imports.add("import javax.faces.event.AjaxBehaviorEvent;");
        }
        imports.add("import javax.naming.Context;");
        imports.add("import javax.naming.InitialContext;");
        imports.add("import javax.naming.NamingException;");

        imports.add("import javax.sql.DataSource;");
        imports.add("import java.sql.Statement;");
        imports.add("import java.sql.ResultSet;");

        // first we add a delete methode
        ajax.append(
                "\n"
                + "public void delete() {\n"
                + "\tConnection con = null;\n"
                + "\tPreparedStatement ps = null;\n"
                + "\ttry {\n"
                + "\t\tContext ctx = new InitialContext();\n"
                + "\t\tFacesContext fctx = FacesContext.getCurrentInstance();\n"
                + "\t\tDataSource ds = (DataSource) ctx.lookup(fctx.getExternalContext().getInitParameter(\"jdbcConnection\"));\n"
                + "\t\tcon = ds.getConnection();\n"
                + "\t\tps = con.prepareStatement(\"delete from ");
        if (getSchema()
                .length() > 0) {
            ajax.append(getSchema()).append(".");
        }

        ajax.append(getTableName()).append(" where ");
        // we find the primary keys

        boolean nextValue1 = false;
        for (int i = 0;
                i < a3.size();
                i++) {
            FieldNameType ft = a3.get(i);
            if (ft.isPrimaryKey()) {
                if (nextValue1) {
                    ajax.append(" and ");
                }
                ajax.append(ft.getOriginalColumnName()).append(" = ? ");
                nextValue1 = true;
            }
        }

        ajax.append(
                "\");\n");

        // and now all the lines with the primary key
        // we add the setter for the PreparedStatement
        a3 = new ArrayList();
        a3 = getFieldNameTypes();
        Iterator iter3 = a3.iterator();
        int counter = 0;

        while (iter3.hasNext()) {
            FieldNameType ff = (FieldNameType) iter3.next();
            if (ff.isPrimaryKey()) {
                counter++;

                // what Type is it?
                boolean read = false;
                ajax.append("\t\t");
                if (ff.getType().equalsIgnoreCase("String")) {
                    ajax.append("ps.setString(");
                    read = true;
                }
                if (ff.getType().equalsIgnoreCase("boolean")) {
                    ajax.append("ps.setBoolean(");
                    read = true;
                }
                if (ff.getType().equalsIgnoreCase("int")) {
                    ajax.append("ps.setInt(");
                    read = true;
                }
                if (ff.getType().equalsIgnoreCase("short")) {
                    ajax.append("ps.setShort(");
                    read = true;
                }
                if (ff.getType().equalsIgnoreCase("long")) {
                    ajax.append("ps.setLong(");
                    read = true;
                }
                if (ff.getType().equalsIgnoreCase("BigDecimal")) {
                    ajax.append("ps.setBigDecimal(");
                    read = true;
                }
                if (ff.getType().equalsIgnoreCase("float")) {
                    ajax.append("ps.setFloat(");
                    read = true;
                }
                if (ff.getType().equalsIgnoreCase("double")) {
                    ajax.append("ps.setDouble(");
                    read = true;
                }
                if (ff.getType().equalsIgnoreCase("java.sql.Date")) {
                    ajax.append("ps.setDate(");
                    read = true;
                }
                if (ff.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                    ajax.append("ps.setTimestamp(");
                    read = true;
                }
                if (ff.getType().equalsIgnoreCase("java.sql.Time")) {
                    ajax.append("ps.setTime(");
                    read = true;
                }
                if (!read) {
                    ajax.append("ps.setString(");
                }
                ajax.append(Integer.toString(counter)).append(",").append("get").append(firstUpper(ff.getOriginalColumnName().toLowerCase())).append("());\n");

            }
        }

        // and now we finish this method
        ajax.append(
                "\t\tps.executeUpdate();\n"
                + "\t} catch (SQLException sqle) {\n"
                + "\t\tSystem.out.println(getClass().getName() + \".delete() SQL Exception: \" + sqle.getMessage());\n"
                + "\t} catch (NamingException nme) {\n"
                + "\t\tSystem.out.println(getClass().getName() + \".delete() NamingException: \" + nme.getMessage());\n"
                + "\t}"
                + "  finally {\n"
                + "\t\ttry {\n"
                + "\t\t\tif (ps != null) {\n"
                + "\t\t\t\tps.close();\n"
                + "\t\t\t}\n"
                + "\t\t\tif (con != null) {\n"
                + "\t\t\t\tcon.close();\n"
                + "\t\t\t}\n"
                + "\t\t} catch (SQLException s) {\n"
                + "\t\t\tSystem.out.println(getClass().getName() + \".delete() Database/SQL Exception while closeing: \" + s.getMessage());\n"
                + "\t\t}\n"
                + "\t}\n"
                + "}\n");

        // add a Clear Methode
        ajax.append(
                "\npublic void clear() {\n");
        ajax.append(
                "\t\tjava.util.Calendar cal = java.util.Calendar.getInstance();\n");
        a = getFieldNameTypes();
        Iterator iter = a.iterator();
        counter = 0;

        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            if (!f.isAutoIncrement()) {
                counter++;

                // what Type is it?
                boolean read = false;
                ajax.append("\t\t");
                if (f.getType().equalsIgnoreCase("String")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(\"\");\n");
                }
                if (f.getType().equalsIgnoreCase("boolean")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(false);\n");
                }
                if (f.getType().equalsIgnoreCase("int")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(0);\n");
                }
                if (f.getType().equalsIgnoreCase("short")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("((short)0);\n");
                }
                if (f.getType().equalsIgnoreCase("long")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(0.00l);\n");
                }
                if (f.getType().equalsIgnoreCase("BigDecimal")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(new BigDecimal(0));\n");
                }
                if (f.getType().equalsIgnoreCase("float")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(0.00f);\n");
                }
                if (f.getType().equalsIgnoreCase("double")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(0.00d);\n");
                }
                if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(new java.sql.Date(cal.getTimeInMillis()));\n");
                }
                if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(new java.sql.Timestamp(cal.getTimeInMillis()));\n");
                }
                if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                    ajax.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(new java.sql.Time(cal.getTimeInMillis()));\n");
                }
            }
        }

        ajax.append(
                "}\n");

        // we add an insert-Methode  
        ajax.append(
                "public void insert() {\n");
        ajax.append(
                "\tConnection con = null;\n");
        ajax.append(
                "\tPreparedStatement ps = null;\n");
        ajax.append(
                "\tResultSet generatedKey = null;\n");
        ajax.append(
                "\ttry {\n");
        ajax.append(
                "\t\tContext ctx = new InitialContext();\n");
        ajax.append(
                "\t\tFacesContext fctx = FacesContext.getCurrentInstance();\n");
        ajax.append(
                "\t\tDataSource ds = (DataSource) ctx.lookup(fctx.getExternalContext().getInitParameter(\"jdbcConnection\"));\n");
        ajax.append(
                "\t\tcon = ds.getConnection();\n");
        ajax.append(
                "\t\tps = con.prepareStatement(\"insert into ");
        if (getSchema()
                .length() > 0) {
            ajax.append(getSchema()).append(".");
        }

        ajax.append(getTableName()).append("(\\n\"\n");

        a = getFieldNameTypes();
        StringBuilder questionMarks = new StringBuilder();
        boolean firstLine = true;
        for (int i = 0;
                i < a.size();
                i++) {
            FieldNameType ft = a.get(i);
            if (!ft.isAutoIncrement()) {
                if ((!firstLine) && (i < a.size())) {
                    ajax.append(",\\n\"\n");
                    questionMarks.append(",");
                }
                ajax.append("\t\t+\"").append(fixStupidNames(ft.getOriginalColumnName()));
                questionMarks.append("?");
                firstLine = false;

            }
        }

        ajax.append(
                ") values (").append(questionMarks).append(")\"");

        // do we have an AUTO_INCREMENT Key? Then we have to figure out the inserted ID
        a = getFieldNameTypes();
        iter = a.iterator();
        boolean autoIncrement = false;
        counter = 0;

        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            if (f.isAutoIncrement()) {
                ajax.append(",Statement.RETURN_GENERATED_KEYS");
                autoIncrement = true;
            }
        }

        ajax.append(
                ");\n");

        // we add the setter for the PreparedStatement
        a = getFieldNameTypes();
        iter = a.iterator();
        counter = 0;

        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            if (!f.isAutoIncrement()) {
                counter++;

                // what Type is it?
                boolean read = false;
                ajax.append("\t\t");
                if (f.getType().equalsIgnoreCase("String")) {
                    ajax.append("ps.setString(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("boolean")) {
                    ajax.append("ps.setBoolean(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("int")) {
                    ajax.append("ps.setInt(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("short")) {
                    ajax.append("ps.setShort(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("long")) {
                    ajax.append("ps.setLong(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("BigDecimal")) {
                    ajax.append("ps.setBigDecimal(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("float")) {
                    ajax.append("ps.setFloat(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("double")) {
                    ajax.append("ps.setDouble(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                    ajax.append("ps.setDate(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                    ajax.append("ps.setTimestamp(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                    ajax.append("ps.setTime(");
                    read = true;
                }
                if (!read) {
                    ajax.append("ps.setString(");
                }

                ajax.append(Integer.toString(counter)).append(",").append("get").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("());\n");
            }
        }

        ajax.append(
                "\t\tps.executeUpdate();\n");

        // if the Key was generated automatically, we have to fetch it and but it back into our bean
        if (autoIncrement) {
            ajax.append("\n\t\t// the key has been generated automtically, so we have to put it back into the bean\n");
            ajax.append("\t\tgeneratedKey = ps.getGeneratedKeys();\n\t\tgeneratedKey.next();\n");
            a = getFieldNameTypes();
            iter = a.iterator();
            counter = 0;
            while (iter.hasNext()) {
                FieldNameType f = (FieldNameType) iter.next();
                if (f.isAutoIncrement()) {
                    ajax.append("\t\tset").append(firstUpper(f.getName())).append("(");
                    if (f.getType().equalsIgnoreCase("int")) {
                        ajax.append("generatedKey.getInt(1));\n");
                        break;
                    }
                    if (f.getType().equalsIgnoreCase("short")) {
                        ajax.append("generatedKey.getShort(1));\n");
                        break;
                    }
                    if (f.getType().equalsIgnoreCase("long")) {
                        ajax.append("generatedKey.getLong(1));\n");
                        break;
                    }
                    if (f.getType().equalsIgnoreCase("BigDecimal")) {
                        ajax.append("generatedKey.getBigDecimal(1));\n");
                        break;
                    }
                    if (f.getType().equalsIgnoreCase("float")) {
                        ajax.append("generatedKey.getFloat(1));\n");
                        break;
                    }
                    if (f.getType().equalsIgnoreCase("double")) {
                        ajax.append("generatedKey.getDouble(1));\n");
                        break;
                    }
                }
            }
            ajax.append("\t\tgeneratedKey.close();\n");
        }

        ajax.append(
                """
                    } catch (NamingException nmex) {
                    \t\tSystem.out.println(getClass().getName() + ".insert() Nameing Exception while write operation: " + nmex.getMessage());
                    \t\twarn("Naming exception while insert operation. " + nmex.getMessage() + "  Check your container configuration");
                    \t} catch (SQLException sqlex) {
                    \t\tSystem.out.println(getClass().getName() + ".insert() Database/SQL Exception while writing: " + sqlex.getMessage());
                    \t\twarn("SQL exception while insert operation. " + sqlex.getMessage() );
                    \t}
                      finally {
                    \t\ttry {
                    \t\t\tif (ps != null) {
                    \t\t\t\tps.close();
                    \t\t\t}
                    \t\t\tif (con != null) {
                    \t\t\t\tcon.close();
                    \t\t\t}
                    \t\t} catch (SQLException s) {
                    \t\t\tSystem.out.println(getClass().getName() + ".insert() Database/SQL Exception while closeing: " + s.getMessage());
                    \t\t}
                    \t}
                    }""");

        // we start with the Ajax Methods to update each single field
        iter = a.iterator();

        while (iter.hasNext()) {

            FieldNameType f = (FieldNameType) iter.next();
            if (!f.isPrimaryKey()) {
                ajax.append("\n\npublic void ").append(spaceFixer(f.getOriginalColumnName()).toLowerCase()).append("Listener(AjaxBehaviorEvent event) {\n");

                // what Type is it?
                objectType = "String";
                if (f.getType().equalsIgnoreCase("String")) {
                    objectType = "String";
                }
                if (f.getType().equalsIgnoreCase("boolean")) {
                    objectType = "Boolean";
                }
                if (f.getType().equalsIgnoreCase("int")) {
                    objectType = "Integer";
                }
                if (f.getType().equalsIgnoreCase("short")) {
                    objectType = "Short";
                }
                if (f.getType().equalsIgnoreCase("long")) {
                    objectType = "Long";
                }
                if (f.getType().equalsIgnoreCase("BigDecimal")) {
                    objectType = "BigDecimal";
                }
                if (f.getType().equalsIgnoreCase("float")) {
                    objectType = "Float";
                }
                if (f.getType().equalsIgnoreCase("double")) {
                    objectType = "Double";
                }
                if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                    objectType = "Date";
                }
                if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                    objectType = "Timestamp";
                }
                if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                    objectType = "Time";
                }

                ajax.append("\t").append(objectType).append(" value = (").append(objectType).append(") get").append(firstUpper(spaceFixer(f.getName()))).append("();\n");
                ajax.append("\tConnection con = null;\n");
                ajax.append("\tPreparedStatement ps = null;\n");
                ajax.append("""
                            \ttry {
                            \t\tContext ctx = new InitialContext();
                            \t\tFacesContext fctx = FacesContext.getCurrentInstance();
                            \t\tDataSource ds = (DataSource) ctx.lookup(fctx.getExternalContext().getInitParameter("jdbcConnection"));
                            \t\tcon = ds.getConnection();
                            """);
                ajax.append("\t\tps = con.prepareStatement(\"update ");
                if (getSchema().length() > 0) {
                    ajax.append(getSchema()).append(".");
                }
                ajax.append(getTableName()).append(" set ").append(fixStupidNames(f.getOriginalColumnName())).append(" = ? where ");

                // we find the primary keys
                a2 = getFieldNameTypes();
                boolean nextValue = false;
                for (int i = 0; i < a2.size(); i++) {
                    FieldNameType ft = a2.get(i);
                    if (ft.isPrimaryKey()) {
                        if (nextValue) {
                            ajax.append(" and ");
                        }
                        ajax.append(ft.getOriginalColumnName()).append(" = ? ");
                        nextValue = true;
                    }
                }
                ajax.append("\");\n");

                // and now we fill the prepared statement with values
                //ajax.append("\t\tps.set").append(objectType).append("(1, value");
                if (f.getType().equalsIgnoreCase("String")) {
                    ajax.append("\t\tps.setString(1, value);\n");
                }
                if (f.getType().equalsIgnoreCase("boolean")) {
                    ajax.append("\t\tps.setBoolean(1, value);\n");
                }
                if (f.getType().equalsIgnoreCase("int")) {
                    ajax.append("\t\tps.setInt(1, value);\n");
                }
                if (f.getType().equalsIgnoreCase("short")) {
                    ajax.append("\t\tps.setShort(1, value);\n");
                }
                if (f.getType().equalsIgnoreCase("long")) {
                    ajax.append("\t\tps.setLong(1, value);\n");
                }
                if (f.getType().equalsIgnoreCase("BigDecimal")) {
                    ajax.append("\t\tps.setBigDecimal(1, value);\n");
                }
                if (f.getType().equalsIgnoreCase("float")) {
                    ajax.append("\t\tps.setFloat(1, value);\n");
                }
                if (f.getType().equalsIgnoreCase("double")) {
                    ajax.append("\t\tps.setDouble(1, value);\n");
                }
                if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                    ajax.append("\t\tps.setDate(1, value);\n");
                }
                if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                    ajax.append("\t\tps.setTimestamp(1, value);\n");
                }
                if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                    ajax.append("\t\tps.setTime(1, value);\n");
                }

                // and now all the lines with the primary key
                // we add the setter for the PreparedStatement
                a2 = new ArrayList();
                a2 = getFieldNameTypes();
                Iterator iter2 = a2.iterator();
                counter = 1;
                while (iter2.hasNext()) {
                    FieldNameType ff = (FieldNameType) iter2.next();
                    if (ff.isPrimaryKey()) {
                        counter++;

                        // what Type is it?
                        boolean read = false;
                        ajax.append("\t\t");
                        if (ff.getType().equalsIgnoreCase("String")) {
                            ajax.append("ps.setString(");
                            read = true;
                        }
                        if (ff.getType().equalsIgnoreCase("boolean")) {
                            ajax.append("ps.setBoolean(");
                            read = true;
                        }
                        if (ff.getType().equalsIgnoreCase("int")) {
                            ajax.append("ps.setInt(");
                            read = true;
                        }
                        if (ff.getType().equalsIgnoreCase("short")) {
                            ajax.append("ps.setShort(");
                            read = true;
                        }
                        if (ff.getType().equalsIgnoreCase("long")) {
                            ajax.append("ps.setLong(");
                            read = true;
                        }
                        if (ff.getType().equalsIgnoreCase("BigDecimal")) {
                            ajax.append("ps.setBigDecimal(");
                            read = true;
                        }
                        if (ff.getType().equalsIgnoreCase("float")) {
                            ajax.append("ps.setFloat(");
                            read = true;
                        }
                        if (ff.getType().equalsIgnoreCase("double")) {
                            ajax.append("ps.setDouble(");
                            read = true;
                        }
                        if (ff.getType().equalsIgnoreCase("java.sql.Date")) {
                            ajax.append("ps.setDate(");
                            read = true;
                        }
                        if (ff.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                            ajax.append("ps.setTimestamp(");
                            read = true;
                        }
                        if (ff.getType().equalsIgnoreCase("java.sql.Time")) {
                            ajax.append("ps.setTime(");
                            read = true;
                        }
                        if (!read) {
                            ajax.append("ps.setString(");
                        }
                        ajax.append(Integer.toString(counter)).append(",").append("get").append(firstUpper(ff.getOriginalColumnName().toLowerCase())).append("());\n");

                    }
                }
                // and now we finish this method
                ajax.append("""
                            \t\tps.executeUpdate();
                            \t} catch (SQLException sqlex) {
                            
                            \t\twarn("SQL exception while update operation. " + sqlex.getMessage() );
                            \t\tSystem.out.println(getClass().getName() + ".""").append(spaceFixer(f.getOriginalColumnName()).toLowerCase()).append("Listener() SQL Exception: \" + sqlex.getMessage());\n"
                        + "\t} catch (NamingException nme) {\n"
                        + "\t\tSystem.out.println(getClass().getName() + \".").append(spaceFixer(f.getOriginalColumnName()).toLowerCase()).append("Listener() NamingException: \" + nme.getMessage());\n"
                        + "\n\t\twarn(\"Nameing exception while update operation. \" + nme.getMessage() );"
                        + "\t}"
                        + " finally {\n"
                        + "\t\ttry {\n"
                        + "\t\t\tif (ps != null) {\n"
                        + "\t\t\t\tps.close();\n"
                        + "\t\t\t}\n"
                        + "\t\t\tif (con != null) {\n"
                        + "\t\t\t\tcon.close();\n"
                        + "\t\t\t}\n"
                        + "\t\t} catch (SQLException s) {\n"
                        + "\t\t\tSystem.out.println(getClass().getName() + \".").append(spaceFixer(f.getOriginalColumnName()).toLowerCase()).append("Listener() Database/SQL Exception while closeing: \" + s.getMessage());\n"
                        + "\t\t}\n"
                        + "\t}\n"
                        + "}\n");
            }
        }
        return ajax;
    }

    private String clearMethode() {
        StringBuilder clear = new StringBuilder();

        // add a Clear Methode
        clear.append("\npublic void clear() {\n");
        clear.append("\t\tjava.util.Calendar cal = java.util.Calendar.getInstance();\n");
        ArrayList<FieldNameType> a = getFieldNameTypes();
        Iterator iter = a.iterator();
        int counter = 0;
        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            if (!f.isAutoIncrement()) {
                counter++;

                // what Type is it?
                boolean read = false;
                clear.append("\t\t");
                if (f.getType().equalsIgnoreCase("String")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(\"\");\n");
                }
                if (f.getType().equalsIgnoreCase("boolean")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(false);\n");
                }
                if (f.getType().equalsIgnoreCase("int")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(0);\n");
                }
                if (f.getType().equalsIgnoreCase("short")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("((short)0);\n");
                }
                if (f.getType().equalsIgnoreCase("long")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(0.00l);\n");
                }
                if (f.getType().equalsIgnoreCase("BigDecimal")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(new BigDecimal(0));\n");
                }
                if (f.getType().equalsIgnoreCase("float")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(0.00f);\n");
                }
                if (f.getType().equalsIgnoreCase("double")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(0.00d);\n");
                }
                if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(new java.sql.Date(cal.getTimeInMillis()));\n");
                }
                if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(new java.sql.Timestamp(cal.getTimeInMillis()));\n");
                }
                if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                    clear.append("set").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("(new java.sql.Time(cal.getTimeInMillis()));\n");
                }
            }
        }
        clear.append("}\n");
        return clear.toString();

    }

    /**
     * Creates a simple JSF managed Bean to be used together
     *
     * @param searchFields a comma separated List of Fields zo create
     * Searchcriteria
     * @param packagename the name of the package to be used
     * @param classname the name of the java class
     * @param query the sql query to be used, must be in the form of a prepared
     * statement
     * @return the Java Code
     */
    private String getManagedBean(String searchFields, String packagename, String classname, String query) {
        StringBuilder sb = new StringBuilder();
        StringBuilder top = new StringBuilder();

        boolean autoIncrement = false;

        imports = new TreeSet<String>();

        top.append("/*\n");
        top.append("""
                      This code is generated by Fredy's SQL-Admin Tool visit http://www.hulmen.ch
                      SQL Admin is free software and licensed under MIT-License
                   
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
                   
                   
                    This is the query the Bean represents: 
                   
                   """);
        top.append("\n").append(getQuery()).append("\n\n");

        top.append("After you have correctly configured your JDBC-connection pool, add the name of this connection to your web.xml:\n\n");
        top.append("\t<context-param>\n");
        top.append("\t<param-name>jdbcConnection</param-name>\n");
        top.append("\t<param-value>jdbc/myconnection</param-value>\n");
        top.append("\t</context-param>\n\n");
        top.append("\n\nyou need to add Apache POI to your CLASSPATH so the xlsx-generation works properly\n");
        top.append("\nyou can create an Excelfile by calling the methode downloadList(), make sure to disable Ajax at the button calling the file");
        top.append("\nalso you can determine if a list has content by the methode isListHasContent()\n");
        top.append("""
                   
                   Add your comments, a Description, your license or whatever here
                   
                   
                   */
                   """);
        top.append("\npackage ").append(packagename).append("; \n\n");

        if (jakarta.isSelected()) {

            // these are the standard imports for Jakarta EE
            imports.add("javax.naming.InitialContext");
            imports.add("javax.naming.NamingException");
            imports.add("javax.naming.Context");
            
            imports.add("jakarta.faces.annotation.ManagedProperty");
            imports.add("jakarta.faces.context.FacesContext");
            imports.add("jakarta.faces.application.FacesMessage");
            imports.add("jakarta.inject.Named");
            imports.add("jakarta.faces.context.ExternalContext");
            imports.add("jakarta.faces.event.PhaseId");
            imports.add("jakarta.annotation.PostConstruct");

            // SQL
            imports.add("java.sql.SQLException");
            imports.add("java.sql.Connection");
            imports.add("java.sql.ResultSet");
            imports.add("java.sql.Statement");
            imports.add("java.sql.PreparedStatement");
            imports.add("java.sql.ResultSetMetaData");
            imports.add("javax.sql.DataSource");

            imports.add("java.util.ArrayList");
            imports.add("java.util.Iterator");
            imports.add("java.util.List");

            imports.add("java.io.IOException");
            imports.add("java.io.Serializable");
            imports.add("java.io.OutputStream");

            // POI 
            imports.add("org.apache.poi.ss.usermodel.Sheet");
            imports.add("org.apache.poi.ss.usermodel.Cell");
            imports.add("org.apache.poi.ss.usermodel.Workbook");
            imports.add("org.apache.poi.xssf.usermodel.XSSFWorkbook");
            imports.add("org.apache.poi.ss.usermodel.CreationHelper");
            imports.add("org.apache.poi.ss.usermodel.Row");
            imports.add("org.apache.poi.ss.usermodel.CellStyle");

        } else {

            // these import are used anyway by javax
            imports.add("java.io.Serializable");
            imports.add("java.sql.Connection");
            imports.add("java.sql.PreparedStatement");
            imports.add("java.sql.ResultSet");
            imports.add("java.sql.SQLException");
            imports.add("java.util.ArrayList");
            imports.add("java.util.List");
            imports.add("javax.faces.bean.ManagedProperty");
            imports.add("javax.annotation.PostConstruct");
            imports.add("javax.inject.Named");
            imports.add("javax.naming.Context");
            imports.add("javax.naming.InitialContext");
            imports.add("javax.naming.NamingException");
            imports.add("javax.faces.context.FacesContext");
            imports.add("javax.faces.application.FacesMessage");
            imports.add("javax.faces.event.PhaseId");
            imports.add("javax.sql.DataSource");
            imports.add("java.sql.Statement");
            imports.add("java.sql.ResultSetMetaData");
            imports.add("org.apache.poi.ss.usermodel.Cell");
            imports.add("org.apache.poi.ss.usermodel.Row");
            imports.add("org.apache.poi.ss.usermodel.Sheet");
            imports.add("org.apache.poi.ss.usermodel.Workbook");
            imports.add("org.apache.poi.xssf.usermodel.XSSFWorkbook");
            imports.add("org.apache.poi.ss.usermodel.CreationHelper");
            imports.add("org.apache.poi.ss.usermodel.CellStyle");
            imports.add("javax.faces.context.ExternalContext");
            imports.add("java.io.OutputStream");
            imports.add("java.util.Iterator");
            imports.add("java.io.IOException");
            imports.add("javax.inject.Named");

        }
        sb.append("@Named\n").append((String) beanTypeManagedBean.getSelectedItem()).append("\n");

        if (beanTypeManagedBean.getSelectedItem().toString().toLowerCase().substring(1).startsWith("view")) {

            if (jakarta.isSelected()) {
                imports.add("jakarta.faces.view.ViewScoped");
            } else {
                imports.add("org.omnifaces.cdi.ViewScoped;");
            }
        } else {
            if (jakarta.isSelected()) {
                imports.add("jakarta.enterprise.context." + ((String) beanTypeManagedBean.getSelectedItem()).substring(1));
            } else {
                imports.add("javax.enterprise.context." + ((String) beanTypeManagedBean.getSelectedItem()).substring(1));
            }
        }

        sb.append("public class ").append(classname).append(" implements Serializable {\n\n");

        if (!packageNameBean.getText().equals(packagename)) {
            imports.add(packageNameBean.getText() + "." + classNameBean.getText().trim());
        }

        // the Connection 
        sb.append("\t// you need to change this to the correct name of your connection\n");
        sb.append("\tprivate String connectionName = \"to be set in web.xml\";\n");

        // the Bean we manage, only for javax        
        sb.append("\n\t@ManagedProperty(value=\"#{").append(classNameBean.getText().trim().toLowerCase()).append("}\")\n");

        sb.append("\tprivate ").append(classNameBean.getText().trim()).append(" ").append(classNameBean.getText().trim().toLowerCase()).append(";\n");
        sb.append("\n\t@PostConstruct\n\tprivate void init() {\n");
        sb.append("\t\tFacesContext ctx = FacesContext.getCurrentInstance();\n");
        sb.append("\t\tconnectionName = ctx.getExternalContext().getInitParameter(\"jdbcConnection\");\n");
        sb.append("\t\t").append(classNameBean.getText().trim().toLowerCase()).append(" = new ").append(classNameBean.getText().trim()).append("();\n");
        sb.append("\t\t").append(classNameBean.getText().trim().toLowerCase()).append("List = new ArrayList();\n");
        sb.append("\t}\n\n");

        sb.append("\tprivate ArrayList<").append(classNameBean.getText().trim()).append("> ").append(classNameBean.getText().trim().toLowerCase()).append("List;\n");
        sb.append("\tprivate ArrayList<String> columnNames;\n");
        sb.append("\tprivate int rowsToDisplay = 100;\n");
        sb.append("\tpublic void setRowsToDisplay(int v) {\n\t\trowsToDisplay = v;\n\t}\n");
        sb.append("\tpublic int  getRowsToDisplay() {\n\t\treturn rowsToDisplay;\n\t}\n");

        // we parse the query, because it mostly is mult lined        
        sb.append("\tprivate String query=\"\"\n");

        query = query.replaceAll("\"", "\\\\\"");
        //String[] ql = query.split(System.getProperty("line.separator"));
        String[] ql = query.split("\n");
        for (String ql1 : ql) {
            sb.append("\t\t+ \"").append(ql1).append(" \\n \"\n");
        }
        sb.append("\t;\n");

        // and now all the fields, we like to hava access to
        List selectedFields = list.getSelectedValuesList();
        if (!selectedFields.isEmpty()) {
            sb.append("// please take into consideration to change your query and set the preparedstatement accordingly \n");
            Iterator iter = selectedFields.iterator();
            while (iter.hasNext()) {
                FieldNameType ft = getFieldNameType((String) iter.next());
                if (ft != null) {
                    sb.append("\tprivate ").append(ft.getType()).append(" ").append(spaceFixer(ft.getName()).toLowerCase()).append(";\n");
                    sb.append("\tpublic void set").append(firstUpper(spaceFixer(ft.getName()))).append("(").append(ft.getType()).append(" v) {\n\t\tthis.").append(spaceFixer(ft.getName()).toLowerCase()).append(" = v ;\n\t}\n");
                    sb.append("\tpublic ").append(ft.getType()).append(" get").append(firstUpper(spaceFixer(ft.getName()))).append("() {\n").append("\t\treturn this.").append(spaceFixer(ft.getName()).toLowerCase()).append(";\n\t}\n");
                }
            }
        }

        // the constructor
        sb.append("\n\npublic ").append(classname).append("() {\n");
        sb.append("\t").append(classNameBean.getText().trim().toLowerCase()).append(" = new ").append(classNameBean.getText().trim()).append("();\n");
        sb.append("\t").append(classNameBean.getText().trim().toLowerCase()).append("List = new ArrayList();\n");
        sb.append("\tcolumnNames = new ArrayList();\n");
        sb.append("}\n");

        // getter and setter for the Bean itself
        // sb.append("\tprivate ").append(classNameBean.getText()).append(" ").append(classNameBean.getText().toLowerCase()).append(";\n");
        sb.append("\npublic void set").append(firstUpper(classNameBean.getText().trim().toLowerCase())).append("(").append(classNameBean.getText().trim()).append(" v) {\n").append("\tthis.").append(classNameBean.getText().toLowerCase()).append(" = v;\n}\n");
        sb.append("\npublic ").append(classNameBean.getText().trim()).append(" get").append(firstUpper(classNameBean.getText().trim().toLowerCase())).append("() {\n\treturn this.").append(classNameBean.getText().trim().toLowerCase()).append(";\n}\n");

        // getter and setter for the connectionname
        sb.append("\npublic void setConnectionName(String connectionName) {\n\tthis.connectionName = connectionName;\n}\n");
        sb.append("\npublic String getConnectionName() {\n\treturn this.connectionName;\n}\n");

        // the size of the List , or how many records are in it
        sb.append("\n// number of Rows in the list to display");
        sb.append("\npublic int getNumberOfRows() {");
        sb.append("\n\treturn ").append(classNameBean.getText().trim().toLowerCase()).append("List.size();\n");
        sb.append("}\n");

        // get the List to display in a Table
        sb.append("\npublic List<").append(classNameBean.getText().trim()).append("> get").append(firstUpper(classNameBean.getText().trim().toLowerCase())).append("List() {\n");
        sb.append("\n\t// we only create the list at the appropriate phase\n");
        sb.append("\tFacesContext context = FacesContext.getCurrentInstance();\n\tif (context.getCurrentPhaseId() != PhaseId.RENDER_RESPONSE) {\n\t\treturn ").append(classNameBean.getText().trim().toLowerCase()).append("List;\n\t}\n");
        sb.append("\t").append(classNameBean.getText().trim().toLowerCase()).append("List = new ArrayList();\n");
        sb.append("\tConnection con=null;\n").append("\tResultSet rs = null;\n").append("\tPreparedStatement ps = null;\n");
        sb.append("\ttry {\n").append("\t\tContext ctx = new InitialContext();\n").append("\t\tDataSource ds = (DataSource) ctx.lookup(getConnectionName());\n").append("\t\tcon = ds.getConnection();\n");
        sb.append("\t\tcolumnNames = new ArrayList();\n");
        sb.append("\t\tps = con.prepareStatement(query);\n").append("\t\trs = ps.executeQuery();\n");
        sb.append("\t\tResultSetMetaData rsmd = rs.getMetaData();\n").append("\t\tfor ( int i = 0; i < rsmd.getColumnCount();i++) {\n").append("\t\t\tcolumnNames.add((String) rsmd.getColumnLabel(i+1));\n").append("\t\t}\n");

        sb.append("\t\twhile (rs.next()) {\n");
        sb.append("\t\t\t").append(classNameBean.getText().trim()).append(" row = new ").append(classNameBean.getText().trim()).append("();\n");

        ArrayList<FieldNameType> a = getFieldNameTypes();
        Iterator iter = a.iterator();
        int counter = 0;
        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            counter++;
            sb.append("\t\t\trow.set").append(firstUpper(spaceFixer(f.getName()))).append("(rs.get");

            // what Type is it?
            boolean read = false;
            if (f.getType().equalsIgnoreCase("String")) {
                sb.append("String(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("String(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("boolean")) {
                sb.append("Boolean(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("int")) {
                sb.append("Int(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("Int(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("short")) {
                sb.append("Short(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("Short(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("long")) {
                sb.append("Long(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("Long(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("BigDecimal")) {
                sb.append("BigDecimal(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("BigDecimal(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("float")) {
                sb.append("Float(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("Float(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("double")) {
                sb.append("Double(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("Double(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                sb.append("Date(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("Date(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                sb.append("Timestamp(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("Timestamp(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                sb.append("Time(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("Time(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (!read) {
                sb.append("String(").append(fixName(f.getOriginalColumnName(), counter)).append("));\n");
                //sb.append("String(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
            }
        }
        sb.append("\t\t\t").append(classNameBean.getText().trim().toLowerCase()).append("List.add(row);\n");
        sb.append("\n\t\t\t").append("if ( ( getRowsToDisplay() >0 ) && (").append(classNameBean.getText().trim().toLowerCase()).append("List.size() == getRowsToDisplay()) ) break;\n");

        sb.append("\t\t}\n");

        // and close all
        sb.append("\t} catch (NamingException nmex) {\n\t\tSystem.out.println(getClass().getName() + \"").append(".get").append(firstUpper(classNameBean.getText().trim().toLowerCase())).append("List() Nameing Exception while reading operation: \" + nmex.getMessage());\n");
        sb.append("\n\t} catch (SQLException sqlex) {\n\t\tSystem.out.println(getClass().getName() + \"").append(".get").append(firstUpper(classNameBean.getText().trim().toLowerCase())).append("List() Database/SQL Exception while reading: \" + sqlex.getMessage());\n");
        sb.append("\t} finally {\n"
                + "\t\t try {\n"
                + "\t\t\tif (rs != null) rs.close();\n"
                + "\t\t\tif (ps != null) ps.close();\n"
                + "\t\t\tif (con != null) con.close();\n"
                + "\t\t} catch (SQLException s) {\n"
                + "\t\t\tSystem.out.println(getClass().getName() + \".").append(".get").append(firstUpper(classNameBean.getText().trim().toLowerCase())).append("List() Database/SQL Exception while closeing: \" + s.getMessage());\n"
                + "\t\t}\n"
                + "\t}\n");
        sb.append("\n\treturn ").append(classNameBean.getText().trim().toLowerCase()).append("List;\n}\n");

        // we add the Excel-Export function
        sb.append("\n\n/*\n\tUse this to determine if the list has content\n*/\n");
        sb.append("public boolean isListHasContent() {\n");
        sb.append("\treturn ! ").append(classNameBean.getText().trim().toLowerCase()).append("List.isEmpty();\n}\n");
        sb.append("\n/*\n\tIf the List is not empty, this will generate an Excel file having the name export.xlsx and sends it to the response of the webserver\n*/\n");
        sb.append("\n\npublic void downloadList() {\n");
        sb.append("\tif (isListHasContent() ) {\n");
        sb.append("\t\ttry {\n");
        sb.append("\t\t\tString fileName = \"export.xlsx\";\n");
        sb.append("\t\t\tString contentType = \"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\";\n");
        sb.append("\t\t\tFacesContext fc = FacesContext.getCurrentInstance();\n");
        sb.append("\t\t\tExternalContext ec = fc.getExternalContext();\n");
        sb.append("\t\t\tec.responseReset();\n");
        sb.append("\t\t\tec.setResponseContentType(contentType);\n");
        sb.append("\t\t\tec.setResponseHeader(\"Content-Disposition\", \"attachment; filename=\\\"\" + fileName + \"\\\"\");\n");
        sb.append("\t\t\tOutputStream output = ec.getResponseOutputStream();\n");
        sb.append("\t\t\tWorkbook wb = new XSSFWorkbook();\n");
        sb.append("\t\t\tCreationHelper createHelper = wb.getCreationHelper();\n");
        sb.append("\t\t\tCellStyle dateStyle = wb.createCellStyle();\n");
        sb.append("\t\t\tCellStyle dateTimeStyle = wb.createCellStyle();\n");
        sb.append("\t\t\tCellStyle timeStyle = wb.createCellStyle();\n");
        sb.append("\t\t\tdateStyle.setDataFormat(createHelper.createDataFormat().getFormat(\"yyyy-MM-dd\"));\n");
        sb.append("\t\t\tdateTimeStyle.setDataFormat(createHelper.createDataFormat().getFormat(\"yyyy-MM-dd HH:mm:ss\"));\n");
        sb.append("\t\t\ttimeStyle.setDataFormat(createHelper.createDataFormat().getFormat(\"HH:mm:ss\"));\n");
        sb.append("\t\t\tSheet sheet = wb.createSheet(\"Export\");\n");
        sb.append("\n\t\t\t// we create the header row\n");
        sb.append("\t\t\tRow row = sheet.createRow(0);\n");
        sb.append("\t\t\tfor  ( int i = 0; i < columnNames.size();i++) {\n");
        sb.append("\t\t\t\tCell cell = row.createCell(i);\n");
        sb.append("\t\t\t\tcell.setCellValue(columnNames.get(i));\n");
        sb.append("\t\t\t}\n");
        sb.append("\n\t\t\t//now we loop every line to add it to the Excelsheet\n");
        sb.append("\t\t\tIterator iter = ").append(classNameBean.getText().trim().toLowerCase()).append("List.iterator();\n");
        sb.append("\t\t\tint i = 0;\n");
        sb.append("\t\t\tCell cell;\n");
        sb.append("\t\t\twhile (iter.hasNext()) {\n");
        sb.append("\t\t\t\t").append(classNameBean.getText().trim()).append(" line =  (").append(classNameBean.getText().trim()).append(") iter.next();\n");
        sb.append("\t\t\t\ti++;\n");
        sb.append("\t\t\t\trow = sheet.createRow(i);\n");
        a = getFieldNameTypes();
        iter = a.iterator();
        counter = 0;
        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            sb.append("\t\t\t\tcell = row.createCell(").append(Integer.toString(counter)).append(");\n");
            sb.append("\t\t\t\tcell.setCellValue(line.get").append(firstUpper(spaceFixer(f.getName()))).append("());\n");
            if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                sb.append("\t\t\t\tcell.setCellStyle(dateStyle);\n");
            }
            if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                sb.append("\t\t\t\tcell.setCellStyle(dateTimeStyle);\n");
            }
            if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                sb.append("\t\t\t\tcell.setCellStyle(timeStyle);\n");
            }
            counter++;
        }
        sb.append("\t\t\t}\n");
        sb.append("\t\t\twb.write(output);\n");
        sb.append("\t\t\t((XSSFWorkbook)wb).close();\n");
        sb.append("\t\t\tfc.responseComplete();\n");
        sb.append("\t\t} catch (IOException iox) {\n");
        sb.append("\t\t\tSystem.out.println(getClass().getName() + \".downloadList() Exception: \" + iox.getMessage());\n");
        sb.append("\t\t}\n");
        sb.append("\t}\n");
        sb.append("}\n\n");

        // we do the insert Methode
        sb.append("public void insert() {\n");
        sb.append("\tConnection         con = null;\n");
        sb.append("\tPreparedStatement   ps = null;\n");
        sb.append("\tResultSet generatedKey = null;\n");
        sb.append("\ttry {\n");
        sb.append("\t\tContext ctx = new InitialContext();\n");
        sb.append("\t\tDataSource ds = (DataSource) ctx.lookup(getConnectionName());\n");
        sb.append("\t\tcon = ds.getConnection();\n");
        sb.append("\t\tps = con.prepareStatement(\"insert into ");
        if (getSchema().length() > 0) {
            sb.append(getSchema()).append(".");
        }
        sb.append(getTableName()).append("(\\n\"\n");

        a = getFieldNameTypes();
        StringBuilder questionMarks = new StringBuilder();
        boolean firstLine = true;
        for (int i = 0; i < a.size(); i++) {
            FieldNameType ft = a.get(i);
            if (!ft.isAutoIncrement()) {
                if ((!firstLine) && (i < a.size())) {
                    sb.append(",\\n\"\n");
                    questionMarks.append(",");
                }
                sb.append("\t\t+\"").append(fixStupidNames(ft.getOriginalColumnName()));
                questionMarks.append("?");
                firstLine = false;

            }
        }
        sb.append(") values (").append(questionMarks).append(")\"");

        // do we have an AUTO_INCREMENT Key? Then we have to figure out the inserted ID
        a = getFieldNameTypes();
        iter = a.iterator();
        counter = 0;
        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            if (f.isAutoIncrement()) {
                sb.append(",Statement.RETURN_GENERATED_KEYS");
                autoIncrement = true;
            }
        }

        sb.append(");\n");

        // we add the setter for the PreparedStatement
        a = getFieldNameTypes();
        iter = a.iterator();
        counter = 0;
        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            if (!f.isAutoIncrement()) {
                counter++;

                // what Type is it?
                boolean read = false;
                sb.append("\t\t");
                if (f.getType().equalsIgnoreCase("String")) {
                    sb.append("ps.setString(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("boolean")) {
                    sb.append("ps.setBoolean(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("int")) {
                    sb.append("ps.setInt(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("short")) {
                    sb.append("ps.setShort(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("long")) {
                    sb.append("ps.setLong(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("BigDecimal")) {
                    sb.append("ps.setBigDecimal(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("float")) {
                    sb.append("ps.setFloat(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("double")) {
                    sb.append("ps.setDouble(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                    sb.append("ps.setDate(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                    sb.append("ps.setTimestamp(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                    sb.append("ps.setTime(");
                    read = true;
                }
                if (!read) {
                    sb.append("ps.setString(");
                }

                sb.append(Integer.toString(counter)).append(",").append(classNameBean.getText().trim().toLowerCase()).append(".get").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("());\n");
            }
        }
        sb.append("\t\tps.executeUpdate();\n");

        // if the Key was generated automatically, we have to fetch it and but it back into our bean
        if (autoIncrement) {
            sb.append("\n\t\t// the key has been generated automtically, so we have to put it back into the bean\n");
            sb.append("\t\tgeneratedKey = ps.getGeneratedKeys();\n\t\tgeneratedKey.next();\n");
            a = getFieldNameTypes();
            iter = a.iterator();
            counter = 0;
            while (iter.hasNext()) {
                FieldNameType f = (FieldNameType) iter.next();
                if (f.isAutoIncrement()) {
                    sb.append("\t\tget").append(firstUpper(classNameBean.getText().trim().toLowerCase())).append("().set").append(firstUpper(f.getName())).append("(");
                    if (f.getType().equalsIgnoreCase("int")) {
                        sb.append("generatedKey.getInt(1));\n");
                        break;
                    }
                    if (f.getType().equalsIgnoreCase("short")) {
                        sb.append("generatedKey.getShort(1));\n");
                        break;
                    }
                    if (f.getType().equalsIgnoreCase("long")) {
                        sb.append("generatedKey.getLong(1));\n");
                        break;
                    }
                    if (f.getType().equalsIgnoreCase("BigDecimal")) {
                        sb.append("generatedKey.getBigDecimal(1));\n");
                        break;
                    }
                    if (f.getType().equalsIgnoreCase("float")) {
                        sb.append("generatedKey.getFloat(1));\n");
                        break;
                    }
                    if (f.getType().equalsIgnoreCase("double")) {
                        sb.append("generatedKey.getDouble(1));\n");
                        break;
                    }
                }
            }
            //sb.append("\t\tgeneratedKey.close();\n");
            sb.append("\t\tinfo(\"Data inserted\");\n");
        }

        //sb.append("\t\tps.close();\n\t\tcon.close();\n");
        sb.append("\t} catch (NamingException nmex) {\n").append("\t\tSystem.out.println(getClass().getName() + \".insert() Nameing Exception while write operation: \" + nmex.getMessage());");
        sb.append("\t\twarn(\"Nameing exception while insert. Check you container configuration \" + nmex.getMessage());\n");
        sb.append("\n\t} catch (SQLException sqlex) {\n").append("\t\tSystem.out.println(getClass().getName() + \".insert() Database/SQL Exception while writing: \" + sqlex.getMessage());\n");
        sb.append("\t\twarn(\"SQL exception while insert. \" + sqlex.getMessage());\n");
        sb.append("\t}");
        sb.append(" finally {\n"
                + "\t\ttry {\n"
                + "\t\t\tif (generatedKey != null) {\n"
                + "\t\t\t\tgeneratedKey.close();\n"
                + "\t\t\t}\n"
                + "\t\t\tif (ps != null) {\n"
                + "\t\t\t\tps.close();\n"
                + "\t\t\t}\n"
                + "\t\t\tif (con != null) {\n"
                + "\t\t\t\tcon.close();\n"
                + "\t\t\t}\n"
                + "\t\t} catch (SQLException s) {\n"
                + "\t\t\tSystem.out.println(getClass().getName() + \"").append(".insert() Database/SQL Exception while closeing: \" + s.getMessage());\n"
                        + " \t\t}\n"
                        + "\t}\n}");

        // we do the update Methode
        sb.append("\n\npublic void update() {\n");
        sb.append("\tConnection       con = null;\n");
        sb.append("\tPreparedStatement ps = null;\n");
        sb.append("\ttry {\n");
        sb.append("\t\tContext ctx = new InitialContext();\n");
        sb.append("\t\tDataSource ds = (DataSource) ctx.lookup(getConnectionName());\n");
        sb.append("\t\tcon = ds.getConnection();\n");
        sb.append("\t\tps = con.prepareStatement(\"update ");
        if (getSchema().length() > 0) {
            sb.append(getSchema()).append(".");
        }
        sb.append(getTableName()).append(" set \"\n");

        a = getFieldNameTypes();
        for (int i = 0; i < a.size(); i++) {
            FieldNameType ft = a.get(i);
            if (!ft.isAutoIncrement()) {
                sb.append("\t\t+ \"").append(fixStupidNames(ft.getOriginalColumnName())).append(" = ? ");
                if (i + 1 < a.size()) {
                    sb.append(",");
                }
                sb.append("\\n\"\n");
            }
        }
        sb.append("\t\t+ \"where ");

        // we find the primary keys
        a = getFieldNameTypes();
        boolean nextValue = false;
        for (int i = 0; i < a.size(); i++) {
            FieldNameType ft = a.get(i);
            if (ft.isPrimaryKey()) {
                if (nextValue) {
                    sb.append(" and ");
                }
                sb.append(fixStupidNames(ft.getOriginalColumnName())).append(" = ? ");
                nextValue = true;
            }
        }

        sb.append("\");\n");

        // we add the setter for the PreparedStatement
        a = getFieldNameTypes();
        iter = a.iterator();
        counter = 0;
        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            if (!f.isAutoIncrement()) {
                counter++;

                // what Type is it?
                boolean read = false;
                sb.append("\t\t");
                if (f.getType().equalsIgnoreCase("String")) {
                    sb.append("ps.setString(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("boolean")) {
                    sb.append("ps.setBoolean(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("int")) {
                    sb.append("ps.setInt(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("short")) {
                    sb.append("ps.setShort(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("long")) {
                    sb.append("ps.setLong(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("BigDecimal")) {
                    sb.append("ps.setBigDecimal(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("float")) {
                    sb.append("ps.setFloat(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("double")) {
                    sb.append("ps.setDouble(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                    sb.append("ps.setDate(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                    sb.append("ps.setTimestamp(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                    sb.append("ps.setTime(");
                    read = true;
                }
                if (!read) {
                    sb.append("ps.setString(");
                }

                sb.append(Integer.toString(counter)).append(",").append(classNameBean.getText().trim().toLowerCase()).append(".get").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("());\n");
            }
        }

        // we add the setter of the condition for the PreparedStatement 
        a = getFieldNameTypes();
        iter = a.iterator();
        //counter = 0;
        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            if (f.isPrimaryKey()) {
                counter++;

                // what Type is it?
                boolean read = false;
                sb.append("\t\t");
                if (f.getType().equalsIgnoreCase("String")) {
                    sb.append("ps.setString(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("boolean")) {
                    sb.append("ps.setBoolean(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("int")) {
                    sb.append("ps.setInt(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("short")) {
                    sb.append("ps.setShort(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("long")) {
                    sb.append("ps.setLong(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("BigDecimal")) {
                    sb.append("ps.setBigDecimal(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("float")) {
                    sb.append("ps.setFloat(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("double")) {
                    sb.append("ps.setDouble(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                    sb.append("ps.setDate(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                    sb.append("ps.setTimestamp(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                    sb.append("ps.setTime(");
                    read = true;
                }
                if (!read) {
                    sb.append("ps.setString(");
                }

                sb.append(Integer.toString(counter)).append(",").append(classNameBean.getText().trim().toLowerCase()).append(".get").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("());\n");
            }
        }

        sb.append("\t\tps.executeUpdate();\n");
        sb.append("\t\tinfo(\"Data updated\");\n");
        //sb.append("\t\tps.close();\n\t\tcon.close();\n");
        sb.append("\t} catch (NamingException nmex) {\n").append("\t\tSystem.out.println(getClass().getName() + \".update() Nameing Exception while write operation: \" + nmex.getMessage());\n");
        sb.append("\t\twarn(\"Nameing exception while update. Check you container configuration \" + nmex.getMessage());\n");
        sb.append("\n\t} catch (SQLException sqlex) {\n").append("\t\tSystem.out.println(getClass().getName() + \".update() Database/SQL Exception while writing: \" + sqlex.getMessage());\n");
        sb.append("\t\twarn(\"SQL exception while update. \" + sqlex.getMessage());\n");
        sb.append("\t}");
        sb.append(" finally {\n"
                + "\t\ttry {\n"
                + "\t\t\tif (ps != null) {\n"
                + "\t\t\t\tps.close();\n"
                + "\t\t\t}\n"
                + "\t\t\tif (con != null) {\n"
                + "\t\t\t\tcon.close();\n"
                + "\t\t\t}\n"
                + "\t\t} catch (SQLException s) {\n"
                + "\t\t\tSystem.out.println(getClass().getName() + \".update() Database/SQL Exception while closeing: \" + s.getMessage());\n"
                + "\t\t}\n"
                + "\t}\n");

        sb.append("}\n");

        // we do the delete Methode
        sb.append("\npublic void delete() {\n");
        sb.append("\tConnection con = null;\n");
        sb.append("\tPreparedStatement ps = null;\n");
        sb.append("\ttry {\n");
        sb.append("\t\tContext ctx = new InitialContext();\n");
        sb.append("\t\tDataSource ds = (DataSource) ctx.lookup(getConnectionName());\n");
        sb.append("\t\tcon = ds.getConnection();\n");
        sb.append("\t\tps = con.prepareStatement(\"delete from ");
        if (getSchema().length() > 0) {
            sb.append(getSchema()).append(".");
        }
        sb.append(getTableName()).append(" where ");

        // we find the primary keys
        a = getFieldNameTypes();
        nextValue = false;
        for (int i = 0; i < a.size(); i++) {
            FieldNameType ft = a.get(i);
            if (ft.isPrimaryKey()) {
                if (nextValue) {
                    sb.append("\n\t\tand ");
                }
                sb.append(fixStupidNames(ft.getOriginalColumnName())).append(" = ? ");
                nextValue = true;
            }
        }

        sb.append(" \");\n");

        // we add the setter of the condition for the PreparedStatement 
        a = getFieldNameTypes();
        iter = a.iterator();
        counter = 0;
        while (iter.hasNext()) {
            FieldNameType f = (FieldNameType) iter.next();
            if (f.isPrimaryKey()) {
                counter++;

                // what Type is it?
                boolean read = false;
                sb.append("\t\t");
                if (f.getType().equalsIgnoreCase("String")) {
                    sb.append("ps.setString(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("boolean")) {
                    sb.append("ps.setBoolean(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("int")) {
                    sb.append("ps.setInt(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("short")) {
                    sb.append("ps.setShort(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("long")) {
                    sb.append("ps.setLong(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("BigDecimal")) {
                    sb.append("ps.setBigDecimal(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("float")) {
                    sb.append("ps.setFloat(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("double")) {
                    sb.append("ps.setDouble(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                    sb.append("ps.setDate(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                    sb.append("ps.setTimestamp(");
                    read = true;
                }
                if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                    sb.append("ps.setTime(");
                    read = true;
                }
                if (!read) {
                    sb.append("ps.setString(");
                }

                sb.append(Integer.toString(counter)).append(",").append(classNameBean.getText().trim().toLowerCase()).append(".get").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("());\n");
            }
        }

        sb.append("\t\tps.executeUpdate();\n");
        sb.append("\t\t").append(classNameBean.getText().trim().toLowerCase()).append(".clear();\n");
        sb.append("\t\tinfo(\"Data deleted\");\n");
        sb.append("\t} catch (NamingException nmex) {\n").append("\t\tSystem.out.println(getClass().getName() + \".delete() Nameing Exception while write operation: \" + nmex.getMessage());\n");
        sb.append("\t\twarn(\"Nameing exception while delete. Check you container configuration \" + nmex.getMessage());\n");
        sb.append("\n\t} catch (SQLException sqlex) {\n").append("\t\tSystem.out.println(getClass().getName() + \".delete() Database/SQL Exception while writing: \" + sqlex.getMessage());\n");
        sb.append("\t\twarn(\"SQL exception while delete. \" + sqlex.getMessage());\n");
        sb.append("\t}");
        sb.append(" finally {\n");
        sb.append("""                  
                  \t\ttry {
                  \t\t\tif (ps != null) {
                  \t\t\t\tps.close();
                  \t\t\t}
                  \t\t\tif (con != null) {
                  \t\t\t\tcon.close();
                  \t\t\t}
                  \t\t} catch (SQLException s) {
                  \t\t\tSystem.out.println(getClass().getName() + ".delete() Database/SQL Exception while closeing: " + s.getMessage());
                  \t\t}
                  \t}
                  """);

        sb.append("}\n");
        // we add the imports to the top

        for (String i : imports) {
            top.append("\n import ").append(i).append(";");
        }

        // Standard Faces Messages 
        sb.append(" /*\n"
                + "\n"
                + " These methods are added to be used to sent messages to your application\n"
                + " Use the faces messagess- or growl Tag to make them appear on your browser\n"
                + " The messages contained within the parameter msg will be displayed.\n"
                + " \n"
                + " */\n"
                + "\n"
                + "public void info(String msg) {\n"
                + "   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, \"Info\", msg));\n"
                + "}\n"
                + "\n"
                + "public void warn(String msg) {\n"
                + "    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, \"Warning!\", msg));\n"
                + "}\n"
                + "\n"
                + "public void error(String msg) {\n"
                + "    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, \"Error!\", msg));\n"
                + "}\n"
                + "\n"
                + "public void fatal(String msg) {\n"
                + "   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, \"Fatal!\", msg));\n"
                + "}\n");

        // the end of the class
        top.append("\n\n");
        sb.append("\n}");

        top.append(sb);
        return top.toString();
    }

    private String getTableBean() {
        StringBuilder top = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        imports = new TreeSet<String>();

        top.append("/*\n");
        top.append("   This code is generated by Fredy's SQL-Admin Tool visit http://www.hulmen.ch/admin\n");
        top.append("   SQL Admin is free software and licensed under MIT-License\n\n");
        top.append("   Permission is hereby granted, free of charge, to any person obtaining a copy \n"
                + "   of this software and associated documentation files (the \"Software\"), to deal\n"
                + "   in the Software without restriction, including without limitation the rights\n"
                + "   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n"
                + "   copies of the Software, and to permit persons to whom the Software is\n"
                + "   furnished to do so, subject to the following conditions:\n");
        top.append("\n");
        top.append("  The above copyright notice and this permission notice shall be included in\n"
                + "  all copies or substantial portions of the Software.\n"
                + "\n"
                + "  THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n"
                + "  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n"
                + "  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n"
                + "  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n"
                + "  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n"
                + "  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n"
                + "  SOFTWARE.\n\n"
                + "\n This is the query the Bean represents:\n");
        top.append("\n").append(getQuery()).append("\n\n");
        top.append("\n Add your comments, a Description, your license or whatever here\n"
                + "\n"
                + "\n"
                + "*/\n");
        top.append("\npackage ").append(packageNameBean.getText()).append("; \n\n");

        sb.append("public class ").append(classNameTableBean.getText()).append(" extends ").append(classNameBean.getText().trim()).append(" {\n\n");

        top.append("import java.util.Vector;\n");

        sb.append("public Vector getVector() {\n");
        sb.append("\tVector v = new Vector();\n");
        for (int i = 0; i < fieldNameTypes.size(); i++) {
            FieldNameType fnt = new FieldNameType();
            fnt = fieldNameTypes.get(i);
            String id = firstUpper(spaceFixer(fnt.getName()));
            if (fnt.getType().equalsIgnoreCase("boolean")) {
                sb.append("\tv.add((Boolean) new Boolean(get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("int")) {
                sb.append("\tv.addElement((Integer) new Integer(get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("short")) {
                sb.append("\tv.addElement((Short) new Short(get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("long")) {
                sb.append("\tv.addElement((Long) new Long(get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("float")) {
                sb.append("\tv.addElement((Float) new Float(get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("double")) {
                sb.append("\tv.addElement((Double) new Double(get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("string")) {
                sb.append("\tv.addElement((String) get").append(id).append("());\n");
            }
            if (fnt.getType().equalsIgnoreCase("java.sql.Date")) {
                sb.append("\tv.addElement((java.sql.Date) get").append(id).append("());\n");
            }
            if (fnt.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                sb.append("\tv.addElement((java.sql.Timestamp) get").append(id).append("());\n");
            }
            if (fnt.getType().equalsIgnoreCase("java.sql.Time")) {
                sb.append("\tv.addElement((java.sql.Time) get").append(id).append("());\n");
            }
            if (fnt.getType().equalsIgnoreCase("BigDecimal")) {
                sb.append("\tv.addElement((BigDecimal) new BigDecimal(get").append(id).append("()));\n");
            }
        }

        sb.append("\n\treturn v;\n}\n\n}");

        top.append("\n\n\n").append(sb);
        return top.toString();

    }

    private FieldNameType getFieldNameType(String name) {
        Iterator iter = fieldNameTypes.iterator();
        while (iter.hasNext()) {
            FieldNameType ft = (FieldNameType) iter.next();
            if (ft.getName().equalsIgnoreCase(name)) {
                return ft;
            }
        }
        return null;
    }

    private String getTable() {
        StringBuilder sb = new StringBuilder();

        sb.append("/*\n");
        sb.append("  This code is generated by Fredy's SQL-Admin Tool visit http://www.hulmen.ch/admin\n");
        sb.append("  SQL Admin is free software and licensed under MIT License\n\n");
        sb.append("  Permission is hereby granted, free of charge, to any person obtaining a copy \n"
                + "  of this software and associated documentation files (the \"Software\"), to deal\n"
                + "  in the Software without restriction, including without limitation the rights\n"
                + "  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n"
                + "  copies of the Software, and to permit persons to whom the Software is\n"
                + "  furnished to do so, subject to the following conditions:\n"
                + "  \n"
                + "  The above copyright notice and this permission notice shall be included in\n"
                + "  all copies or substantial portions of the Software.\n"
                + "\n"
                + "  THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n"
                + "  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n"
                + "  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n"
                + "  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n"
                + "  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n"
                + "  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n"
                + "  SOFTWARE.\n");
        sb.append(" \n");
        sb.append("  This class represents a JTable for display only of the query printed below.\n");
        sb.append("  you can fill this table with the calling these methods:\n");
        sb.append("  addBunch(ArrayList<");
        sb.append(packageNameBean.getText()).append(".").append(classNameBean.getText().trim()).append("> a);\n");
        sb.append("  add");
        sb.append(classNameBean.getText().trim()).append("(").append(classNameBean.getText().trim()).append(" row);\n");
        sb.append("  addRow(Vector v);  // where v is the vector called from ").append(classNameBean.getText().trim()).append(".getVector()\n");
        sb.append("  addTableRowObject(").append(classNameBean.getText().trim()).append(" v); \n");
        sb.append("*/\n");
        sb.append("\npackage ").append(packageNameTable.getText()).append("; \n\n");
        sb.append("import java.util.ArrayList;\n");
        sb.append("import java.util.Vector;\n");
        sb.append("import javax.swing.table.AbstractTableModel;\n");
        sb.append("import ").append(packageNameBean.getText()).append(".").append(classNameBean.getText().trim()).append(";\n\n");

        sb.append("public class ").append(classNameTable.getText()).append("  extends AbstractTableModel  {\n");

        for (int i = 0; i < fieldNameTypes.size(); i++) {
            FieldNameType fnt = new FieldNameType();
            fnt = fieldNameTypes.get(i);
            sb.append("\tpublic static final int ").append(spaceFixer(fnt.getName()).toUpperCase()).append(" = ").append(Integer.toString(i)).append(";\n");
        }

        sb.append("\tprivate Vector data;\n");
        sb.append("\tprivate String[] columnNames = {\n");
        for (int i = 0; i < fieldNameTypes.size(); i++) {
            FieldNameType fnt = new FieldNameType();
            fnt = fieldNameTypes.get(i);
            sb.append("\t\t\"").append(fnt.getName()).append("\",\n");
        }
        sb.append("\t};\n\n");

        sb.append("public ").append(classNameTable.getText()).append("() {\n").append("\tdata = new Vector();\n}\n\n");

        sb.append("@Override\n"
                + "public int getRowCount() {\n"
                + "   return data.size();\n"
                + "}\n"
                + "\n"
                + "@Override\n"
                + "public int getColumnCount() {\n"
                + "    return columnNames.length;\n"
                + "}\n"
                + "\n"
                + "@Override\n"
                + "public String getColumnName(int column) {\n"
                + "        if (columnNames[column] != null) {\n"
                + "           return columnNames[column];\n"
                + "        } else {\n"
                + "            return \"\";\n"
                + "        }\n"
                + "}\n\n"
                + "@Override\n"
                + "public Object getValueAt(int rowIndex, int columnIndex) {\n"
                + "    Vector rowData = (Vector) data.elementAt(rowIndex);\n"
                + "    return rowData.elementAt(columnIndex);\n"
                + "}\n"
                + "\n"
                + "@Override\n"
                + "public void setValueAt(Object value, int row, int col) {\n"
                + "    Vector rowData = (Vector) data.elementAt(row);\n"
                + "    rowData.setElementAt(value, col);\n"
                + "    data.setElementAt(rowData, row);\n"
                + "\n"
                + "    fireTableCellUpdated(row, col);\n"
                + "}\n\n");

        sb.append("public ").append(classNameTableBean.getText()).append(" getRowAt(int row) {\n");
        sb.append("\t").append(classNameTableBean.getText()).append(" a = new ").append(classNameTableBean.getText()).append("();\n");

        for (int i = 0; i < fieldNameTypes.size(); i++) {
            FieldNameType fnt = new FieldNameType();
            fnt = fieldNameTypes.get(i);
            sb.append("\ta.set").append(firstUpper(spaceFixer(fnt.getName()))).append("(");
            sb.append(converter(fnt)).append(");\n");
        }
        sb.append("\treturn a;\n}\n");

        sb.append("public void addTableRowObject(").append(classNameTableBean.getText()).append(" v) {\n");
        sb.append("\taddRow(v.getVector());\n}\n");
        sb.append("@Override\n"
                + "public boolean isCellEditable(int row, int col) {\n"
                + "   return false;\n"
                + "}\n"
                + "\n"
                + "public void removeRow(int row) {\n"
                + "    data.removeElementAt(row);\n"
                + "    fireTableChanged(null);\n"
                + "}\n");

        sb.append("public void addRow(Vector v) {\n"
                + "\tdata.addElement(v);\n"
                + "\tfireTableChanged(null);\n"
                + "}\n"
                + "\n"
                + "public void addBunch(ArrayList a) {\n"
                + "\tclearData();\n"
                + "\tVector v;\n");
        sb.append("\t").append(classNameTableBean.getText()).append(" satz;\n");
        sb.append("\t").append("for (int i = 0; i < a.size(); i++) {\n");
        sb.append("\t\t").append("v = new Vector();\n");
        sb.append("\t\t").append("satz = new ").append(classNameTableBean.getText()).append("();\n");
        sb.append("\t\t").append("satz = (").append(classNameTableBean.getText()).append(") a.get(i);\n");

        for (int i = 0; i < fieldNameTypes.size(); i++) {
            FieldNameType fnt = new FieldNameType();
            fnt = fieldNameTypes.get(i);
            String id = firstUpper(spaceFixer(fnt.getName()));
            if (fnt.getType().equalsIgnoreCase("boolean")) {
                sb.append("\tv.addElement((Boolean) new Boolean(satz.get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("int")) {
                sb.append("\tv.addElement((Integer) new Integer(satz.get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("short")) {
                sb.append("\tv.addElement((Short) new Short(satz.get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("long")) {
                sb.append("\tv.addElement((Long) new Long(satz.get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("float")) {
                sb.append("\tv.addElement((Float) new Float(satz.get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("double")) {
                sb.append("\tv.addElement((Double) new Double(satz.get").append(id).append("()));\n");
            }
            if (fnt.getType().equalsIgnoreCase("string")) {
                sb.append("\tv.addElement((String) satz.get").append(id).append("());\n");
            }
            if (fnt.getType().equalsIgnoreCase("java.sql.Date")) {
                sb.append("\tv.addElement((java.sql.Date) satz.get").append(id).append("());\n");
            }
            if (fnt.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                sb.append("\tv.addElement((java.sql.Timestamp) satz.get").append(id).append("());\n");
            }
            if (fnt.getType().equalsIgnoreCase("java.sql.Time")) {
                sb.append("\tv.addElement((java.sql.Time) satz.get").append(id).append("());\n");
            }
            if (fnt.getType().equalsIgnoreCase("BigDecimal")) {
                sb.append("\tv.addElement((BigDecimal) new BigDecimal(satz.get").append(id).append("()));\n");
            }
        }
        sb.append("\taddRow(v);\n\t}\n}\n");

        sb.append("public void clearData() {\n"
                + "\tdata.removeAllElements();\n"
                + "\tfireTableDataChanged();\n"
                + "}\n"
                + "}");

        return sb.toString();
    }

    private String converter(FieldNameType fnt) {
        String v = "";

        /*
         converting 
         boolean
         int
         short
         long
         BigDecimal
         float
         double
         java.sql.Date
         java.sql.Timestamp
         java.sql.Time    
         */
        String id = spaceFixer(fnt.getName()).toUpperCase();
        if (fnt.getType().equalsIgnoreCase("boolean")) {
            v = "((Boolean) getValueAt(row," + id + ")).booleanValue()";
        }
        if (fnt.getType().equalsIgnoreCase("int")) {
            v = "((Integer) getValueAt(row," + id + ")).intValue()";
        }
        if (fnt.getType().equalsIgnoreCase("short")) {
            v = "((Short) getValueAt(row," + id + ")).shortValue()";
        }
        if (fnt.getType().equalsIgnoreCase("long")) {
            v = "((Long) getValueAt(row," + id + ")).longValue()";
        }
        if (fnt.getType().equalsIgnoreCase("float")) {
            v = "((Float) getValueAt(row," + id + ")).floatValue()";
        }
        if (fnt.getType().equalsIgnoreCase("double")) {
            v = "((Double) getValueAt(row," + id + ")).doubleValue()";
        }
        if (fnt.getType().equalsIgnoreCase("string")) {
            v = "(String) getValueAt(row," + id + ")";
        }
        if (fnt.getType().equalsIgnoreCase("java.sql.Date")) {
            v = "(java.sql.Date) getValueAt(row," + id + ")";
        }
        if (fnt.getType().equalsIgnoreCase("java.sql.Timestamp")) {
            v = "(java.sql.Timestamp) getValueAt(row," + id + ")";
        }
        if (fnt.getType().equalsIgnoreCase("java.sql.Time")) {
            v = "(java.sql.Time) getValueAt(row," + id + ")";
        }
        if (fnt.getType().equalsIgnoreCase("BigDecimal")) {
            v = "(BigDecimal) getValueAt(row," + id + ")";
        }

        return v;
    }

    private String spaceFixer(String s) {
        s = s.trim();
        s = s.replaceAll(" ", "_");
        return s;
    }

    private String fixStupidNames(String s) {
        if (s.contains(" ")) {
            s = "\\\"" + s + "\\\"";
        }
        return s;
    }

    private String fixName(String s, int number) {
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            return Integer.toString(number);
        } else {
            return "\"" + s + "\"";
        }
    }

    private String firstLower(String s) {
        s = s.substring(0, 1).toLowerCase() + s.substring(1);
        return s;
    }

    public String getColumnClass(int column) {
        int type;
        try {
            type = rsmd.getColumnType(column);
        } catch (Exception e) {
            // System.err.println("This is JDBC-Adapter with a SQLException in class \ngetColumnClass. Exception is:\n"+e.getMessage() + "\n\n");
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
            case Types.BOOLEAN:
                return "boolean";
            case Types.BIT:
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

    /**
     * @return the dbName
     */
    public String getDbName() {
        return dbName;
    }

    /*
     Derby contains PATH names, sometimes we do not want them to include, especially in a package name
     */
    public String getCleanedDbName() {
        String d = getDbName();

        // if Derby, we eliminate the path
        if (d.contains(File.separator)) {
            String pattern = File.separator;
            if (pattern.equals("\\")) {
                pattern = "\\\\";
            }
            String[] s = d.split(pattern);
            if (s.length > 0) {
                d = s[s.length - 1];
            }
        }
        return d;
    }

    /**
     * @param dbName the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * @return the fieldNameTypes
     */
    public ArrayList<FieldNameType> getFieldNameTypes() {
        return fieldNameTypes;
    }

    /**
     * @param fieldNameTypes the fieldNameTypes to set
     */
    public void setFieldNameTypes(ArrayList<FieldNameType> fieldNameTypes) {
        this.fieldNameTypes = fieldNameTypes;
        if (fieldNameTypes.size() > 0) {
            FieldNameType fnt = new FieldNameType();
            fnt = fieldNameTypes.get(0);
            setDbName(fnt.getDb().toLowerCase());
            setTableName(fnt.getTable().toLowerCase());
        }

    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the schema
     */
    public String getSchema() {
        if (schema.equals("%")) {
            return "";
        }
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

}
