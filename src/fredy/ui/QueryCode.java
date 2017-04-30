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
 * POI is from the Apache Foundation
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
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author sql@hulmen.ch
 */
public class QueryCode extends JDialog {

    private String query;
    private ResultSetMetaData rsmd;
    private JTextField packageNameBean, packageNameTable, packageNameManagedBean;
    private JTextField classNameBean, classNameTable, classNameTableBean;
    private JCheckBox managedBean;
    private JComboBox beanType, beanTypeManagedBean;
    private JTextField searchFields;
    private RSyntaxTextArea beanCode, tableBeanCode, tableCode, sqlQuery, phpcode, managedBeanCode;
    private Logger logger = Logger.getLogger("sql.fredy.ui.QueryCode");
    private ArrayList<FieldNameType> fieldNameTypes;
    private String dbName;
    private String tableName;
    private String schema = "";

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

        StringBuilder className = new StringBuilder();
        className.append("Bean");

        className.append(firstUpper(getTableName()));

        classNameBean.setText(spaceFixer(className.toString()));

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

        String[] beanScope = {"@RequestScoped", "@NoneScoped", "@ViewScoped", "@SessionScoped", "@ApplicationScoped"};
        beanType = new JComboBox(beanScope);
        beanType.setEnabled(managedBean.isSelected());
        panel2.add(beanType);

        ImageButton codeGen = new ImageButton(null, "opendb.gif", "Generate Code");
        codeGen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                beanCode.setText(getBean());
            }
        });
        panel2.add(codeGen);

        RTextScrollPane sp = new RTextScrollPane(beanCode);

        panel.add(new JScrollPane(panel2), BorderLayout.NORTH);
        panel.add(sp, BorderLayout.CENTER);

        return panel;
    }

    private JList list;

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
        final JTextField classNameTxt = new JTextField(20);
        classNameTxt.setText(spaceFixer(className.toString()));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 3;
        gbc.gridy = 0;
        panel2.add(classNameTxt, gbc);

        String[] beanScope = {"@RequestScoped", "@NoneScoped", "@ViewScoped", "@SessionScoped", "@ApplicationScoped"};
        beanTypeManagedBean = new JComboBox(beanScope);
        gbc.gridx = 4;
        panel2.add(beanTypeManagedBean, gbc);

        ImageButton codeGen = new ImageButton(null, "opendb.gif", "Generate Code");
        codeGen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                managedBeanCode.setText(getManagedBean("", packageNameManagedBean.getText(), classNameTxt.getText(), getQuery()));
            }
        });
        gbc.gridx = 5;
        panel2.add(codeGen, gbc);

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

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
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

    Set<String> imports;

    private String getBean() {
        StringBuilder top = new StringBuilder();
        StringBuilder sb = new StringBuilder();

        imports = new HashSet();

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

        // Am I a managed bean
        if (managedBean.isSelected()) {
            sb.append("@ManagedBean\n").append((String) beanType.getSelectedItem()).append("\n");
            imports.add("import javax.faces.bean.ManagedBean;");
            imports.add("import javax.faces.bean." + ((String) beanType.getSelectedItem()).substring(1) + ";");
            imports.add("import java.io.Serializable;");
            sb.append("public class ").append(classNameBean.getText()).append(" implements Serializable {\n\n");
        } else {
            sb.append("public class ").append(classNameBean.getText()).append(" {\n\n");
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
            sb.append("\n\tthis.").append(firstLower(spaceFixer(fnt.getName()))).append(" = v;\n}");
            sb.append("\n\npublic ").append(fnt.getType()).append(" get").append(firstUpper(spaceFixer(fnt.getName()))).append("(){");
            sb.append("\n\t return ").append(firstLower(spaceFixer(fnt.getName()))).append(";\n}\n");
        }

        if (managedBean.isSelected()) {
            sb.append(getAjaxFunction());
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
      Creates  update for each field to be used with ajax calls for a JSF managed bean representing this table
     */
    private StringBuilder getAjaxFunction() {
        StringBuilder ajax = new StringBuilder();
        String objectType = "String";

        // we do the update Methode        
        ArrayList<FieldNameType> a = getFieldNameTypes();
        ArrayList<FieldNameType> a2 = getFieldNameTypes();
        ArrayList<FieldNameType> a3 = getFieldNameTypes();
        ajax.append("/*\n"
                + "   Remember to add the context parameter 'jdbcConnection' to your web.xml containing the JDBC-Datasourcename\n"
                + "   because it is needed to establish a connection to the database.\n"
                + "*/");

        imports.add("import java.sql.Connection;");
        imports.add("import java.sql.PreparedStatement;");
        imports.add("import java.sql.SQLException;");
        imports.add("import javax.faces.component.UIOutput;");
        imports.add("import javax.faces.context.FacesContext;");
        imports.add("import javax.faces.event.AjaxBehaviorEvent;");
        imports.add("import javax.naming.Context;");
        imports.add("import javax.naming.InitialContext;");
        imports.add("import javax.naming.NamingException;");
        imports.add("import javax.sql.DataSource;");

        // first we add a delete methode
        ajax.append("\n"
                + "public void delete() {\n"
                + "\ttry {\n"
                + "\t\tContext ctx = new InitialContext();\n"
                + "\t\tFacesContext fctx = FacesContext.getCurrentInstance();\n"
                + "\t\tDataSource ds = (DataSource) ctx.lookup(fctx.getExternalContext().getInitParameter(\"jdbcConnection\"));\n"
                + "\t\tConnection con = ds.getConnection();\n"
                + "\t\tPreparedStatement ps = con.prepareStatement(\"delete from ");
        if (getSchema().length() > 0) {
            ajax.append(getSchema()).append(".");
        }
        ajax.append(getTableName()).append(" where ");
        // we find the primary keys

        boolean nextValue1 = false;
        for (int i = 0; i < a3.size(); i++) {
            FieldNameType ft = a3.get(i);
            if (ft.isPrimaryKey()) {
                if (nextValue1) {
                    ajax.append(" and ");
                }
                ajax.append(ft.getOriginalColumnName()).append(" = ? ");
                nextValue1 = true;
            }
        }
        ajax.append("\");\n");

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
        ajax.append("\t\tps.executeUpdate();\n"
                + "\t\tps.close();\n"
                + "\t\tcon.close();\n"
                + "\t} catch (SQLException sqle) {\n"
                + "\t\tSystem.out.println(getClass().getName() + \".delete() SQL Exception: \" + sqle.getMessage());\n"
                + "\t} catch (NamingException nme) {\n"
                + "\t\tSystem.out.println(getClass().getName() + \".delete() NamingException: \" + nme.getMessage());\n"
                + "\t}\n"
                + "}\n");

        // we start with the Ajax Methods to update each single field
        Iterator iter = a.iterator();
        while (iter.hasNext()) {

            FieldNameType f = (FieldNameType) iter.next();
            if (!f.isPrimaryKey()) {
                ajax.append("\n\npublic void ").append(f.getOriginalColumnName().toLowerCase()).append("Listener(AjaxBehaviorEvent event) {\n");

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

                //ajax.append("\t").append(objectType).append(" value = (").append(objectType).append(") ((UIOutput) event.getSource()).getValue();\n");
                ajax.append("\t").append(objectType).append(" value = (").append(objectType).append(") get").append(firstUpper(spaceFixer(f.getName()))).append("();\n");
                ajax.append("\ttry {\n"
                        + "\t\tContext ctx = new InitialContext();\n"
                        + "\t\tFacesContext fctx = FacesContext.getCurrentInstance();\n"
                        + "\t\tDataSource ds = (DataSource) ctx.lookup(fctx.getExternalContext().getInitParameter(\"jdbcConnection\"));\n"
                        + "\t\tConnection con = ds.getConnection();\n");
                ajax.append("\t\tPreparedStatement ps = con.prepareStatement(\"update ");
                if (getSchema().length() > 0) {
                    ajax.append(getSchema()).append(".");
                }
                ajax.append(getTableName()).append(" set ").append(f.getOriginalColumnName()).append(" = ? where ");

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
                ajax.append("\t\tps.executeUpdate();\n"
                        + "\t\tps.close();\n"
                        + "\t\tcon.close();\n"
                        + "\t} catch (SQLException sqle) {\n"
                        + "\t\tSystem.out.println(getClass().getName() + \".").append(f.getOriginalColumnName().toLowerCase()).append("Listener() SQL Exception: \" + sqle.getMessage());\n"
                        + "\t} catch (NamingException nme) {\n"
                        + "\t\tSystem.out.println(getClass().getName() + \".").append(f.getOriginalColumnName().toLowerCase()).append("Listener() NamingException: \" + nme.getMessage());\n"
                        + "\t}\n"
                        + "}\n");
            }
        }
        return ajax;
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

        Set<String> imports = new HashSet<String>();

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

        top.append("After you have correctly configured your JDBC-connection pool, add the name of this connection to your web.xml:\n\n");
        top.append("\t<context-param>\n");
        top.append("\t<param-name>jdbcConnection</param-name>\n");
        top.append("\t<param-value>jdbc/myconnection</param-value>\n");
        top.append("\t</context-param>\n\n");

        top.append("\n Add your comments, a Description, your license or whatever here\n"
                + "\n"
                + "\n"
                + "*/\n");
        top.append("\npackage ").append(packagename).append("; \n\n");

        // these import are used anyway
        imports.add("java.io.Serializable");
        imports.add("java.sql.Connection");
        imports.add("java.sql.PreparedStatement");
        imports.add("java.sql.ResultSet");
        imports.add("java.sql.SQLException");
        imports.add("java.util.ArrayList");
        imports.add("java.util.List");
        imports.add("javax.faces.bean.ManagedBean");
        imports.add("javax.faces.bean.ManagedProperty");
        imports.add("javax.annotation.PostConstruct");
        imports.add("javax.inject.Named");
        imports.add("javax.naming.Context");
        imports.add("javax.naming.InitialContext");
        imports.add("javax.naming.NamingException");
        imports.add("javax.servlet.ServletContext");
        imports.add("javax.faces.context.FacesContext");
        imports.add("javax.faces.application.FacesMessage");
        imports.add("javax.faces.event.PhaseId");
        imports.add("javax.sql.DataSource");
        imports.add("java.sql.Statement;");

        sb.append("@ManagedBean\n").append((String) beanTypeManagedBean.getSelectedItem()).append("\n");
        imports.add("javax.faces.bean." + ((String) beanTypeManagedBean.getSelectedItem()).substring(1));

        sb.append("public class ").append(classname).append(" implements Serializable {\n\n");

        if (!packageNameBean.getText().equals(packagename)) {
            imports.add(packageNameBean.getText() + "." + classNameBean.getText());
        }

        // the Connection 
        sb.append("\t// you need to change this to the correct name of your connection\n");
        sb.append("\tprivate String connectionName = \"to be set in web.xml\";\n");

        // the Bean we manage
        sb.append("\n\t@ManagedProperty(value=\"#{").append(classNameBean.getText().toLowerCase()).append("}\")\n");
        sb.append("\tprivate ").append(classNameBean.getText()).append(" ").append(classNameBean.getText().toLowerCase()).append(";\n");
        sb.append("\n\t@PostConstruct\n\tprivate void init() {\n");
        sb.append("\t\tFacesContext ctx = FacesContext.getCurrentInstance();\n");
        sb.append("\t\tconnectionName = ctx.getExternalContext().getInitParameter(\"jdbcConnection\");\n");
        sb.append("\t\t").append(classNameBean.getText().toLowerCase()).append(" = new ").append(classNameBean.getText()).append("();\n");
        sb.append("\t\t").append(classNameBean.getText().toLowerCase()).append("List = new ArrayList();\n");
        sb.append("\t}\n\n");

        sb.append("\tprivate ArrayList<").append(classNameBean.getText()).append("> ").append(classNameBean.getText().toLowerCase()).append("List;\n");

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
                    sb.append("\tpublic void set").append(firstUpper(spaceFixer(ft.getName()))).append("(").append(ft.getType()).append(" v) {;\n\t\tthis.").append(spaceFixer(ft.getName()).toLowerCase()).append(" = v ;\n\t}\n");
                    sb.append("\tpublic ").append(ft.getType()).append(" get").append(firstUpper(spaceFixer(ft.getName()))).append("() {\n").append("\t\treturn this.").append(spaceFixer(ft.getName()).toLowerCase()).append(";\n\t}\n");
                }
            }
        }

        // the constructor
        sb.append("\n\npublic ").append(classname).append("() {\n");
        sb.append("\t").append(classNameBean.getText().toLowerCase()).append(" = new ").append(classNameBean.getText()).append("();\n");
        sb.append("\t").append(classNameBean.getText().toLowerCase()).append("List = new ArrayList();\n");
        sb.append("}\n");

        // getter and setter for the Bean itself
        // sb.append("\tprivate ").append(classNameBean.getText()).append(" ").append(classNameBean.getText().toLowerCase()).append(";\n");
        sb.append("\npublic void set").append(firstUpper(classNameBean.getText().toLowerCase())).append("(").append(classNameBean.getText()).append(" v) {\n").append("\tthis.").append(classNameBean.getText().toLowerCase()).append(" = v;\n}\n");
        sb.append("\npublic ").append(classNameBean.getText()).append(" get").append(firstUpper(classNameBean.getText().toLowerCase())).append("() {\n\treturn this.").append(classNameBean.getText().toLowerCase()).append(";\n}\n");

        // getter and setter for the connectionname
        sb.append("\npublic void setConnectionName(String connectionName) {\n\tthis.connectionName = connectionName;\n}\n");
        sb.append("\npublic String getConnectionName() {\n\treturn this.connectionName;\n}\n");

        // get the List to display in a Table
        sb.append("\npublic List<").append(classNameBean.getText()).append("> get").append(firstUpper(classNameBean.getText().toLowerCase())).append("List() {\n");
        sb.append("\n\t// we only create the list at the appropriate pahase\n");
        sb.append("\tFacesContext context = FacesContext.getCurrentInstance();\n\tif (context.getCurrentPhaseId() != PhaseId.RENDER_RESPONSE) {\n\t\treturn ").append(classNameBean.getText().toLowerCase()).append("List;\n\t};\n");
        sb.append("\t").append(classNameBean.getText().toLowerCase()).append("List = new ArrayList();\n");
        sb.append("\tConnection con;\n").append("\ttry {\n").append("\t\tContext ctx = new InitialContext();\n").append("\t\tDataSource ds = (DataSource) ctx.lookup(getConnectionName());\n").append("\t\tcon = ds.getConnection();\n");
        sb.append("\t\tPreparedStatement ps = con.prepareStatement(query);\n").append("\t\tResultSet rs = ps.executeQuery();\n").append("\t\twhile (rs.next()) {\n");
        sb.append("\t\t\t").append(classNameBean.getText()).append(" row = new ").append(classNameBean.getText()).append("();\n");

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
                sb.append("String(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("boolean")) {
                sb.append("Boolean(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("int")) {
                sb.append("Int(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("short")) {
                sb.append("Short(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("long")) {
                sb.append("Long(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("BigDecimal")) {
                sb.append("BigDecimal(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("float")) {
                sb.append("Float(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("double")) {
                sb.append("Double(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("java.sql.Date")) {
                sb.append("Date(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("java.sql.Timestamp")) {
                sb.append("Timestamp(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (f.getType().equalsIgnoreCase("java.sql.Time")) {
                sb.append("Time(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
                read = true;
            }
            if (!read) {
                sb.append("String(\"").append(f.getOriginalColumnName()).append("\"").append("));\n");
            }
        }
        sb.append("\t\t\t").append(classNameBean.getText().toLowerCase()).append("List.add(row);\n");

        // hier loopen wir das Resultat
        sb.append("\t\t}\n");

        // and close all
        sb.append("\t\trs.close();\n\t\tps.close();\n\t\tcon.close();\n").append("\t} catch (NamingException nmex) {\n\t\tSystem.out.println(getClass().getName() + \" Nameing Exception while reading operation: \" + nmex.getMessage());\n");
        sb.append("\n\t} catch (SQLException sqlex) {\n\t\tSystem.out.println(getClass().getName() + \" Database/SQL Exception while reading: \" + sqlex.getMessage());\n\t}\n\treturn ").append(classNameBean.getText().toLowerCase()).append("List;\n}\n");

        // we do the insert Methode
        sb.append("public void insert() {\n");
        sb.append("\tConnection con;\n");
        sb.append("\ttry {\n");
        sb.append("\t\tContext ctx = new InitialContext();\n");
        sb.append("\t\tDataSource ds = (DataSource) ctx.lookup(getConnectionName());\n");
        sb.append("\t\tcon = ds.getConnection();\n");
        sb.append("\t\tPreparedStatement ps = con.prepareStatement(\"insert into ");
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

                sb.append(Integer.toString(counter)).append(",").append(classNameBean.getText().toLowerCase()).append(".get").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("());\n");
            }
        }
        sb.append("\t\tps.executeUpdate();\n");

        // if the Key was generated automatically, we have to fetch it and but it back into our bean
        if (autoIncrement) {
            sb.append("\n\t\t// the key has been generated automtically, so we have to put it back into the bean\n");
            sb.append("\t\tResultSet generatedKey = ps.getGeneratedKeys();\n\t\tgeneratedKey.next();\n");
            a = getFieldNameTypes();
            iter = a.iterator();
            counter = 0;
            while (iter.hasNext()) {
                FieldNameType f = (FieldNameType) iter.next();
                if (f.isAutoIncrement()) {
                    sb.append("\t\tget").append(firstUpper(classNameBean.getText().toLowerCase())).append("().set").append(firstUpper(f.getName())).append("(");
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
            sb.append("\t\tgeneratedKey.close();\n");
        }

        sb.append("\t\tps.close();\n\t\tcon.close();\n");
        sb.append("\t} catch (NamingException nmex) {\n").append("\t\tSystem.out.println(getClass().getName() + \" Nameing Exception while write operation: \" + nmex.getMessage());");
        sb.append("\n\t} catch (SQLException sqlex) {\n").append("\t\tSystem.out.println(getClass().getName() + \" Database/SQL Exception while writing: \" + sqlex.getMessage());\n").append("\t}\n}");

        // we do the update Methode
        sb.append("\n\npublic void update() {\n");
        sb.append("\tConnection con;\n");
        sb.append("\ttry {\n");
        sb.append("\t\tContext ctx = new InitialContext();\n");
        sb.append("\t\tDataSource ds = (DataSource) ctx.lookup(getConnectionName());\n");
        sb.append("\t\tcon = ds.getConnection();\n");
        sb.append("\t\tPreparedStatement ps = con.prepareStatement(\"update ");
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

                sb.append(Integer.toString(counter)).append(",").append(classNameBean.getText().toLowerCase()).append(".get").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("());\n");
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

                sb.append(Integer.toString(counter)).append(",").append(classNameBean.getText().toLowerCase()).append(".get").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("());\n");
            }
        }

        sb.append("\t\tps.executeUpdate();\n");
        sb.append("\t\tps.close();\n\t\tcon.close();\n");
        sb.append("\t} catch (NamingException nmex) {\n").append("\t\tSystem.out.println(getClass().getName() + \" Nameing Exception while write operation: \" + nmex.getMessage());");
        sb.append("\n\t} catch (SQLException sqlex) {\n").append("\t\tSystem.out.println(getClass().getName() + \" Database/SQL Exception while writing: \" + sqlex.getMessage());\n").append("\t}\n}\n");

        // we do the delete Methode
        sb.append("\npublic void delete() {\n");
        sb.append("\tConnection con;\n");
        sb.append("\ttry {\n");
        sb.append("\t\tContext ctx = new InitialContext();\n");
        sb.append("\t\tDataSource ds = (DataSource) ctx.lookup(getConnectionName());\n");
        sb.append("\t\tcon = ds.getConnection();\n");
        sb.append("\t\tPreparedStatement ps = con.prepareStatement(\"delete from ");
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

                sb.append(Integer.toString(counter)).append(",").append(classNameBean.getText().toLowerCase()).append(".get").append(firstUpper(spaceFixer(f.getOriginalColumnName()).toLowerCase())).append("());\n");
            }
        }

        sb.append("\t\tps.executeUpdate();\n");
        sb.append("\t\tps.close();\n\t\tcon.close();\n");
        sb.append("\t} catch (NamingException nmex) {\n").append("\t\tSystem.out.println(getClass().getName() + \" Nameing Exception while write operation: \" + nmex.getMessage());");
        sb.append("\n\t} catch (SQLException sqlex) {\n").append("\t\tSystem.out.println(getClass().getName() + \" Database/SQL Exception while writing: \" + sqlex.getMessage());\n").append("\t}\n}");

        // we add the imports to the top
        Iterator iterator = imports.iterator();
        while (iterator.hasNext()) {
            String i = (String) iterator.next();
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

        Set<String> imports = new HashSet<String>();

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

        sb.append("public class ").append(classNameTableBean.getText()).append(" extends ").append(classNameBean.getText()).append(" {\n\n");

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
        sb.append(packageNameBean.getText()).append(".").append(classNameBean.getText()).append("> a);\n");
        sb.append("  add");
        sb.append(classNameBean.getText()).append("(").append(classNameBean.getText()).append(" row);\n");
        sb.append("  addRow(Vector v);  // where v is the vector called from ").append(classNameBean.getText()).append(".getVector()\n");
        sb.append("  addTableRowObject(").append(classNameBean.getText()).append(" v); \n");
        sb.append("*/\n");
        sb.append("\npackage ").append(packageNameTable.getText()).append("; \n\n");
        sb.append("import java.util.ArrayList;\n");
        sb.append("import java.util.Vector;\n");
        sb.append("import javax.swing.table.AbstractTableModel;\n");
        sb.append("import ").append(packageNameBean.getText()).append(".").append(classNameBean.getText()).append(";\n\n");

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
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

}
