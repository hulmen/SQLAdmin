package sql.fredy.sqltools;

/**
   Import imports CSV-Files into a Table
   Import is part of my Admin Framework and free software (MIT-License)
 
   This software is part of the Admin-Framework 
  
   Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
   for DB-Administrations, as create / delete / alter and query tables
   it also creates indices and generates simple Java-Code to access DBMS-tables
   and exports data into various formats
 
  
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
   
 **/
import sql.fredy.ui.CloseableFrame;
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.FfAlignerLayoutPanel;
import sql.fredy.ui.Tables;
import sql.fredy.share.t_connect;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;

import java.util.logging.*;

public class Import extends JPanel {

// Fredy's make Version
    private static String fredysVersion = "Version 1.7  21. July. 2015 ";
    private Logger logger = Logger.getLogger("sql.fredy.sqltools");

    public String getVersion() {
        return fredysVersion;
    }
    private JTextField fieldSeparator;
    private JTextField textFieldSeparator;
    private JCheckBox useHeader;
    private JLabel fileName;
    private File file = null;
    private Tables table;
    private String sqlHeader;
    public ImageButton cancel;
    private JProgressBar progressBar;
    t_connect con = null;

    /**
     * Get the value of con.
     * @return value of con.
     */
    public t_connect getCon() {
        if (con == null) {
            con = new t_connect(getHost(),
                    getUser(),
                    getPassword(),
                    getDatabase());
            if (!con.acceptsConnection()) {
                con = null;
            }
        }

        return con;
    }

    /**
     * Set the value of con.
     * @param v  Value to assign to con.
     */
    public void setCon(t_connect v) {
        this.con = v;
        setHost(con.getHost());
        setUser(con.getUser());
        setPassword(con.getPassword());
        setDatabase(con.getDatabase());
    }
    String host;

    /**
     * Get the value of host.
     * @return Value of host.
     */
    public String getHost() {
        return host;
    }

    /**
     * Set the value of host.
     * @param v  Value to assign to host.
     */
    public void setHost(String v) {
        this.host = v;
    }
    String user;

    /**
     * Get the value of user.
     * @return Value of user.
     */
    public String getUser() {
        return user;
    }

    /**
     * Set the value of user.
     * @param v  Value to assign to user.
     */
    public void setUser(String v) {
        this.user = v;
    }
    String password;

    /**
     * Get the value of password.
     * @return Value of password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the value of password.
     * @param v  Value to assign to password.
     */
    public void setPassword(String v) {
        this.password = v;
    }
    String database;

    /**
     * Get the value of database.
     * @return Value of database.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Set the value of database.
     * @param v  Value to assign to database.
     */
    public void setDatabase(String v) {
        this.database = v;
    }
    String schema;

    /**
     * Get the value of schema.
     * @return value of schema.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Set the value of schema.
     * @param v  Value to assign to schema.
     */
    public void setSchema(String v) {
        this.schema = v;
    }

    public void close() {
        con.close();
    }

    private JPanel editPanel() {
        FfAlignerLayoutPanel panel = new FfAlignerLayoutPanel();

        fieldSeparator = new JTextField(";", 2);
        textFieldSeparator = new JTextField("\"", 2);

        panel.addComponent(new JLabel("Fieldseparator"));
        panel.addComponent(fieldSeparator);

        panel.addComponent(new JLabel("Textfields enclosed by: "));
        panel.addComponent(textFieldSeparator);

        panel.setBorder(BorderFactory.createEtchedBorder());

        return panel;
    }

    private JPanel buttonPanel() {

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEtchedBorder());

        useHeader = new JCheckBox("use header");
        useHeader.setSelected(true);
        useHeader.setToolTipText("use first line as header=attribut names");

        ImageButton selectFile = new ImageButton(null, "open.gif", null);
        selectFile.setToolTipText("open file for import");
        selectFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                readFile();
            }
        });

        ImageButton importFile = new ImageButton(null, "documentin.gif", null);
        importFile.setToolTipText("import");
        importFile.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                doImport();
            }
        });

        cancel = new ImageButton(null, "exit.gif", null);
        cancel.setToolTipText("Exit");

        panel.add(useHeader);
        panel.add(selectFile);
        panel.add(importFile);
        panel.add(cancel);

        return panel;
    }

    public String getTable() {
        return table.getSelectedValue().toString();
    }

    private JPanel tablePanel() {

        table = new Tables(getCon(), getSchema());

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.setBorder(BorderFactory.createEtchedBorder());

        fileName = new JLabel("Import");
        panel.add("North", fileName);

        JScrollPane scp = new JScrollPane(table);
        panel.add("Center", scp);


        progressBar = new JProgressBar();
        panel.add("South", progressBar);


        return panel;


    }

    private void readFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Select Importfile");

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            fileName.setText(file.getPath() + file.separator + file.getName());

        } else {
            file = null;
        }

    }

    private void doImport() {
        if (file != null) {
            String s;
            boolean first = true;


            if (useHeader.isSelected()) {
                first = true;
            } else {
                setSimpleSqlHeader();
                first = false;
            }


            // initialize ProgressBar
            progressBar.setMinimum(0);
            progressBar.setMaximum(getNumberOfLines(file));
            progressBar.setValue(0);
            progressBar.updateUI();


            // open the file and read it in
            try {
                DataInputStream ipstr = new DataInputStream(
                        new BufferedInputStream(
                        new FileInputStream(file)));

                BufferedReader bufrd = new BufferedReader(
                        new InputStreamReader(ipstr));
                while ((s = bufrd.readLine()) != null) {

                    if (first) {
                        setExtendedSqlHeader(s);
                        first = false;
                        updatePgb();
                    }
                    insert(s);
                }
                ipstr.close();
            } catch (IOException exep) {
                fileName.setText(getClass().getName() + " IO Error");
            }

        }

    }

    private void insert(String s) {

        String query = "";
        String fields[] = s.split(fieldSeparator.getText().trim());
        StringTokenizer st = new StringTokenizer(s, fieldSeparator.getText().trim());
        char x = '\'';

        boolean first = true;

        //while (st.hasMoreTokens()) {
        for (int fieldItr = 0; fieldItr < fields.length; fieldItr++) {
            try {

                if (first) {
                    first = false;
                } else {
                    query = query + ",";
                }
                //query = query + st.nextToken().replace(textFieldSeparator.getText().charAt(0),x);
                if (textFieldSeparator.getText().trim().length() > 0) {
                    query = query + fields[fieldItr].replace(textFieldSeparator.getText().charAt(0), x);
                } else {
                    String singleField = fields[fieldItr];
                    if ( singleField.length() < 1) singleField = "";
                    //query = query + "'" + fields[fieldItr] + "'";
                    singleField.replaceAll("'", " ");
                    query = query + "'" + singleField + "'";
                }
            } catch (Exception excp) {
               logger.log(Level.WARNING,"Exception while creating query. " +excp.getMessage());
            }

        }
        if ( s.endsWith(fieldSeparator.getText())) query = query + ",''";
        query = sqlHeader + query + ")";
        try {
            int records = con.stmt.executeUpdate(query);            
            updatePgb();
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception on insert query " + query + "\nException is:" + e.getMessage());
        }

    }

    private void updatePgb() {

        progressBar.setValue(progressBar.getValue() + 1);
        progressBar.updateUI();
    }

    private int getNumberOfLines(File f) {
        int nl = 0;
        String s;
        try {
            DataInputStream ipstr = new DataInputStream(
                    new BufferedInputStream(
                    new FileInputStream(f)));

            BufferedReader bufrd = new BufferedReader(
                    new InputStreamReader(ipstr));
            while ((s = bufrd.readLine()) != null) {
                nl++;

            }
            ipstr.close();
        } catch (IOException exep) {
            ;
        }
        return nl;

    }

    private void setSimpleSqlHeader() {
        sqlHeader = "insert into " + getTable() + " values (";
    }
        
    private void setExtendedSqlHeader(String s) {

        //StringTokenizer st = new StringTokenizer(s, fieldSeparator.getText());
        
        String fields[] = s.split(fieldSeparator.getText().trim());
        sqlHeader = "insert into " + getTable() + " ( ";
        boolean first = true;

        for (int fItr = 0; fItr < fields.length; fItr++) {
            try {
                if (first) {
                    first = false;
                } else {
                    sqlHeader = sqlHeader + ",";
                }
                //sqlHeader = sqlHeader + st.nextToken().replace(textFieldSeparator.getText().charAt(0), ' ');
                if (textFieldSeparator.getText().trim().length() > 0) {
                    sqlHeader = sqlHeader + fields[fItr].replace(textFieldSeparator.getText().charAt(0), ' ');
                } else {
                    sqlHeader = sqlHeader + fields[fItr];
                }
               
            } catch (Exception excp) {
                logger.log(Level.INFO, "Exception while creating header. " + excp.getMessage());
            }

        }
        sqlHeader = sqlHeader + ") values (";

    }

    public Import(String host,
            String user,
            String password,
            String database,
            String schema) {

        setHost(host);
        setUser(user);
        setPassword(password);
        setDatabase(database);
        setSchema(schema);
        getCon();
        init();
    }

    public Import(t_connect con, String schema) {
        setCon(con);
        setSchema(schema);
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());

        if (con.getError() != null) {
            this.add("North", new JLabel("Connection Error"));
        }

        this.add("West", editPanel());
        this.add("Center", tablePanel());
        this.add("South", buttonPanel());

    }

    public static void main(String args[]) {

        String host = "localhost";
        String user = System.getProperty("user.name");
        String schema = "%";
        String database = null;
        String password = null;

        System.out.println("CreateIndex\n"
                + "-----------\n"
                + "Syntax: java sql.fredy.sqltools.Import\n"
                + "        Parameters: -h Host (default: localhost)\n"
                + "                    -u User (default: "
                + System.getProperty("user.name") + ")\n"
                + "                    -p Password\n"
                + "                    -d database\n"
                + "                    -s Schema (default: %)\n");

        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-h")) {
                i++;
                host = args[i];
            }
            if (args[i].equals("-u")) {
                i++;
                user = args[i];
            }
            if (args[i].equals("-p")) {
                i++;
                password = args[i];
            }
            if (args[i].equals("-d")) {
                i++;
                database = args[i];
            }

            if (args[i].equals("-s")) {
                i++;
                schema = args[i];
            }
            i++;
        }
        ;

        if (database == null) {
            System.out.println("No database provided. Program stopped!");
            System.exit(0);
        }


        t_connect con = new t_connect(host, user, password, database);
        if (!con.acceptsConnection()) {
            System.out.println("I'm not able to connect. Error:\n" + con.getError());
            System.exit(0);
        }


        CloseableFrame cf = new CloseableFrame("Fredy's Data Import");
        Import imp = new Import(con, schema);
        cf.getContentPane().setLayout(new BorderLayout());
        cf.getContentPane().add(BorderLayout.CENTER, imp);
        imp.cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });


        cf.pack();
        cf.setVisible(true);


    }

    protected void finalize() {
        //close();
    }
}
