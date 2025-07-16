/*
 * The MIT License
 *
 * Copyright 2024 fredy.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sql.fredy.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import sql.fredy.sqltools.DataBaseExport;

/**
 *
 * @author fredy
 */
public class DataBaseExportGui extends JPanel {

    private JTextField jdbcdriver;
    private JTextField jdbcurl;
    private JTextField username;
    private JTextField port;
    private JTextField hostname;
    private JTextField database;
    private JTextField additionalParameter;

    private JPasswordField password;

    private JTextField destinationTable;
    private JCheckBox destinationClean;
    private JCheckBox destinationCreate;

    private JTextField batchsize;

    private RSyntaxTextArea exportQuery;
    LoadImage loadImage = new LoadImage();

    private String dataBaseProductName;
    private String connectionError;

    private Logger logger = Logger.getLogger(getClass().getName());

    public JButton cancel;

    public DataBaseExportGui() {
        dbe = new DataBaseExport();
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER, tabbedPane());
    }

    public void setQuery(String q) {
        exportQuery.setText(q);
    }

    private JTabbedPane tabbedPane() {
        JTabbedPane panel = new JTabbedPane();
        panel.addTab("Query", queryPanel());
        panel.addTab("DST DB", new JScrollPane(connectionPanel()));
        panel.addTab("Run it", runPanel());

        return panel;
    }

    private JPanel queryPanel() {
        JPanel queryPanel = new JPanel();
        queryPanel.setLayout(new BorderLayout());
        exportQuery = new RSyntaxTextArea(10, 40);
        exportQuery.setFont(new Font("Monospaced", Font.PLAIN, 12));
        exportQuery.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        exportQuery.setText("");
        exportQuery.setEditable(true);
        queryPanel.add(new JScrollPane(exportQuery), BorderLayout.CENTER);

        return queryPanel;
    }

    private RSyntaxTextArea standardQuery, dbstyleQuery;

    private JPanel runPanel() {
        JPanel mainpanel = new JPanel();
        mainpanel.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Destination table"), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridwidth = 5;
        destinationTable = new JTextField(40);
        panel.add(destinationTable, gbc);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Batchsize"), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridwidth = 5;
        batchsize = new JTextField(10);

        batchsize.setText("1000");

        panel.add(batchsize, gbc);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Create new destination table"), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        destinationCreate = new JCheckBox();
        destinationCreate.setSelected(false);
        destinationCreate.setToolTipText("""
                                         The automatic creation of a table might lead into errors
                                          because of many different SQL dialects.                                         
                                          Therefore it is recommended to create a table in the destination DB
                                          before running this or verfify the script below.""");

        panel.add(destinationCreate, gbc);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Clean destination table"), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        destinationClean = new JCheckBox();
        destinationClean.setSelected(false);
        panel.add(destinationClean, gbc);
        
        JRadioButton useStandard = new JRadioButton("Standard Query");
        JRadioButton useSql      = new JRadioButton("DB Product Query");
        useSql.setSelected(false);
        useStandard.setSelected(true);
        
        ButtonGroup group = new ButtonGroup();
        group.add(useStandard);
        group.add(useSql);
        
        JPanel radiopanel = new JPanel();
        radiopanel.setLayout(new GridLayout(1, 0));
        radiopanel.add(useStandard);
        radiopanel.add(useSql);
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Create table command"), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridwidth = 1;        
        panel.add(radiopanel, gbc);
        
        
        JPanel buttonpanel = new JPanel();
        buttonpanel.setLayout(new FlowLayout());
        buttonpanel.setBorder(new EtchedBorder());

        //JSplitPane createqueries = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JPanel createqueries = new JPanel();
        createqueries.setLayout(new BoxLayout(createqueries, BoxLayout.X_AXIS));
        createqueries.setBorder(new TitledBorder("Create Query"));
        


        standardQuery = new RSyntaxTextArea(10, 60);
        standardQuery.setFont(new Font("Monospaced", Font.PLAIN, 13));
        standardQuery.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);        
        
        
        JPanel stdPanel = new JPanel();
        stdPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        stdPanel.setBorder(new TitledBorder("SQL Java Types"));
        stdPanel.add(new JScrollPane(standardQuery), BorderLayout.CENTER);

        dbstyleQuery = new RSyntaxTextArea(10, 60);
        dbstyleQuery.setFont(new Font("Monospaced", Font.PLAIN, 13));
        dbstyleQuery.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);

        JPanel dbPanel = new JPanel();
        dbPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        dbPanel.setBorder(new TitledBorder("DB product related"));
        dbPanel.add(new JScrollPane(dbstyleQuery), BorderLayout.CENTER);

        createqueries.add(stdPanel);
        createqueries.add(dbPanel);

        /*
        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BorderLayout());

        innerPanel.add(panel, BorderLayout.CENTER);
        innerPanel.add(createqueries, BorderLayout.SOUTH);
         */
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridwidth = 6;
        panel.add(createqueries, gbc);

        mainpanel.add(new JScrollPane(panel), BorderLayout.CENTER);

        //mainpanel.add(innerPanel, BorderLayout.CENTER);
        JButton runit = new JButton("Export", loadImage.getImage("documentin.gif"));
        runit.setToolTipText("Run the data load");
        runit.addActionListener((ActionEvent e) -> {

            /*
            to check we need to have:
            - a valid connection
            - a query
            - a destination table
             */
            if ((jdbcurl.getText().trim().length() > 0) && (exportQuery.getText().trim().length() > 0) && (destinationTable.getText().trim().length() > 0) && (testConnection())) {
                setConnectionParameter();
                dbe.setManualCreate(dbstyleQuery.getText());
                dbe.setStandardCreate(standardQuery.getText());
                dbe.setUseDBQuery(useSql.isSelected());
                
                int rows = exportData();
                JOptionPane.showMessageDialog(null, String.format("%,d", rows) + " Rows exported", getDataBaseProductName(), JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "There is no connection or no query or no destination table", "Can not export", JOptionPane.WARNING_MESSAGE);
            }

        });

        buttonpanel.add(runit);

        cancel = new JButton("Exit", loadImage.getImage("exit.gif"));
        buttonpanel.add(cancel);

        mainpanel.add(buttonpanel, BorderLayout.SOUTH);

        destinationTable.addActionListener((ActionEvent e) -> {

            /*
            setConnectionParameter();
 
            if (destinationCreate.isSelected()) {
                dbe.createTableStatement();
                standardQuery.setText(dbe.getStandardCreate());
                dbstyleQuery.setText(dbe.getManualCreate());
            } else {
                standardQuery.setText("");
                dbstyleQuery.setText("");
            }

            standardQuery.updateUI();
            dbstyleQuery.updateUI();
            mainpanel.updateUI();
            */
        });

        destinationCreate.addActionListener((ActionEvent e) -> {

            setConnectionParameter();

            if (destinationCreate.isSelected()) {
                dbe.createTableStatement();
                standardQuery.setText(dbe.getStandardCreate());
                dbstyleQuery.setText(dbe.getManualCreate());
            } else {
                standardQuery.setText("");
                dbstyleQuery.setText("");
            }

            standardQuery.updateUI();
            dbstyleQuery.updateUI();
            mainpanel.updateUI();
        });
               
        
        return mainpanel;
    }

    private void setConnectionParameter() {
        
        dbe.setBuildConnection(true);
        dbe.setDstAdditionalParameter(additionalParameter.getText().length() > 0 ? additionalParameter.getText() : null);
        dbe.setDstHostName(hostname.getText() );
        dbe.setDstPort(getPort());
        dbe.setDstDatabase(database.getText());
        dbe.setDstTable(destinationTable.getText());
        dbe.setPassword((String.valueOf(password.getPassword()).length() > 0) ? String.valueOf(password.getPassword()) : null);
        dbe.setJdbcUrl(jdbcurl.getText());
        dbe.setQuery(exportQuery.getText());
        dbe.setUser((username.getText().length() > 0) ? username.getText() : null);

    }

    private JPanel connectionPanel() {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("JDBC URL"), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 0;
        jdbcurl = new JTextField(40);
        panel.add(jdbcurl, gbc);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 1;

        panel.add(new JLabel("Hostname"), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 1;

        hostname = new JTextField(40);
        panel.add(hostname, gbc);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 2;

        panel.add(new JLabel("Port"), gbc);

        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 2;
        port = new JTextField(10);
        panel.add(port, gbc);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 3;

        panel.add(new JLabel("Database"), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 3;
        database = new JTextField(20);
        panel.add(database, gbc);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        panel.add(new JLabel("Additional parameter"), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 1;
        gbc.gridy = 4;
        additionalParameter = new JTextField(20);
        panel.add(additionalParameter, gbc);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 5;

        panel.add(new JLabel("Username"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        username = new JTextField(16);
        panel.add(username, gbc);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 6;

        panel.add(new JLabel("Password"), gbc);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.gridx = 1;
        gbc.gridy = 6;
        password = new JPasswordField(16);
        panel.add(password, gbc);

        mainPanel.add(panel, BorderLayout.CENTER);

        JPanel selectPanel = new JPanel();
        selectPanel.setLayout(new FlowLayout());
        selectPanel.setBorder(new EtchedBorder());

        String[] wellKnown = {"select well known", "Apache Derby", "Maria DB", "MS Access", "MS SQL Server", "MySQL", "Oracle", "PostgreSQL", "SQLite"};
        JComboBox selectDB = new JComboBox(wellKnown);

        selectPanel.add(selectDB, LEFT_ALIGNMENT);

        selectDB.addActionListener((ActionEvent e) -> {
            jdbcurl.setText("");
            hostname.setText("");
            port.setText("");
            database.setText("");
            additionalParameter.setText("");
            username.setText("");
            password.setText("");

            jdbcurl.setEnabled(true);
            hostname.setEnabled(true);
            port.setEnabled(true);
            database.setEnabled(true);
            additionalParameter.setEnabled(true);
            username.setEnabled(true);
            password.setEnabled(true);

            JComboBox cb = (JComboBox) e.getSource();

            switch ((String) cb.getSelectedItem()) {
                case "Apache Derby":
                    jdbcurl.setText("jdbc:derby:");
                    additionalParameter.setText("create=true");
                    hostname.setText("localhost");
                    port.setText("1527");

                    break;
                case "Maria DB":
                    jdbcurl.setText("jdbc:mariadb://");
                    hostname.setText("localhost");
                    port.setText("3306");

                    break;
                case "MS Access":
                    jdbcurl.setText("jdbc:ucanaccess://");
                    additionalParameter.setText("newDatabaseVersion=V2010");
                    additionalParameter.setEnabled(true);
                    hostname.setEnabled(false);
                    port.setEnabled(false);
                    break;
                case "MS SQL Server":
                    jdbcurl.setText("jdbc:sqlserver://");
                    hostname.setText("localhost");
                    port.setText("1433");
                    additionalParameter.setText("MultipleActiveResultSets=True;encrypt=true;trustServerCertificate=true;");
                    additionalParameter.setEnabled(true);
                    break;
                case "MySQL":
                    jdbcurl.setText("jdbc:mysql://");
                    hostname.setText("localhost");
                    port.setText("3306");
                    break;
                case "Oracle":
                    jdbcurl.setText("jdbc:oracle:thin:@");
                    hostname.setText("localhost");
                    port.setText("1521");
                    break;
                case "PostgreSQL":
                    jdbcurl.setText("jdbc:postgresql://");
                    hostname.setText("localhost");
                    port.setText("5432");
                    break;
                case "SQLite":
                    jdbcurl.setText("jdbc:sqlite:");
                    port.setEnabled(false);
                    username.setEnabled(false);
                    password.setEnabled(false);
                    break;
                default:
                    jdbcurl.setText("");
                    hostname.setText("");
                    port.setText("");
                    database.setText("");
                    additionalParameter.setText("");
                    username.setText("");
                    password.setText("");

                    jdbcurl.setEnabled(true);
                    hostname.setEnabled(true);
                    port.setEnabled(true);
                    database.setEnabled(true);
                    additionalParameter.setEnabled(true);
                    username.setEnabled(true);
                    password.setEnabled(true);

                    break;
            }

            jdbcurl.updateUI();
            hostname.updateUI();
            port.updateUI();
            database.updateUI();
            additionalParameter.updateUI();
            username.updateUI();
            password.updateUI();
            panel.updateUI();
        });

        JButton testConnection = new JButton(null, loadImage.getImage("check.gif"));
        testConnection.setToolTipText("Test the connection");
        selectPanel.add(testConnection);

        testConnection.addActionListener((ActionEvent e) -> {
            //testConnection();
            boolean connectionEstablished = testConnection();
            if (connectionEstablished) {
                JOptionPane.showMessageDialog(null, getDataBaseProductName(), "Connection established ", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, getConnectionError(), "No connection to " + database.getText(), JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton runit = new JButton(null, loadImage.getImage("documentin.gif"));
        runit.setToolTipText("Run the data load");
        runit.addActionListener((ActionEvent e) -> {

        });

        mainPanel.add(selectPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private int getPort() {
        int portValue = 0;
        if (port.getText().trim().length() > 0) {
            try {
                portValue = Integer.parseInt(port.getText().trim());
            } catch (NumberFormatException nfe) {

            }
        }
        return portValue;
    }

    private int getChunksize() {
        int chunksize = 1;
        if (batchsize.getText().trim().length() > 0) {
            try {
                chunksize = Integer.parseInt(batchsize.getText().trim());
            } catch (NumberFormatException nfe) {

            }
        }
        return chunksize;
    }

    private DataBaseExport dbe;

    private int exportData() {

        dbe.setBuildConnection(true);
        dbe.setChunksize(Integer.parseInt(batchsize.getText()));
        dbe.setClearDestination(destinationClean.isSelected());
        dbe.setCreateDestination(destinationCreate.isSelected());
        dbe.setDstAdditionalParameter(additionalParameter.getText().length() > 0 ? additionalParameter.getText() : null);
        dbe.setDstHostName(hostname.getText().length() > 0 ? hostname.getText() : null);
        dbe.setDstPort(getPort());
        dbe.setDstDatabase(database.getText());
        dbe.setDstTable(destinationTable.getText());
        dbe.setPassword((String.valueOf(password.getPassword()).length() > 0) ? String.valueOf(password.getPassword()) : null);
        dbe.setJdbcUrl(jdbcurl.getText());
        dbe.setQuery(exportQuery.getText());
        dbe.setUser((username.getText().length() > 0) ? username.getText() : null);

        return dbe.exportData();

    }

    private boolean testConnection() {
        boolean connectionEstablished = false;
        setDataBaseProductName("");
        setConnectionError("");

        if (jdbcurl.getText().trim().length() < 1) {
            return false;
        }
        Connection con = null;
        try {
            if (jdbcurl.getText().equalsIgnoreCase("jdbc:oracle:thin:@")) {
                // we try the thin way
                try {
                    con = DriverManager.getConnection(jdbcurl.getText() + "//" + hostname.getText() + ":" + Integer.toString(getPort()) + "/" + database.getText(), username.getText(), String.valueOf(password.getPassword()));
                } catch (SQLException sqlex) {
                    // and now we try it with the TNSname
                    StringBuilder sb = new StringBuilder();
                    sb.append(jdbcurl.getText()).append("(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)")
                            .append("(HOST=")
                            .append(hostname.getText())
                            .append(")(PORT=").append(port.getText()).append("))")
                            .append("(CONNECT_DATA=(SERVICE_NAME=").append(database.getText())
                            .append(")))");
                    con = DriverManager.getConnection(sb.toString(), username.getText(), String.valueOf(password.getPassword()));
                    connectionEstablished = true;
                }
            }

            // MS SQL Server
            if (jdbcurl.getText().equalsIgnoreCase("jdbc:sqlserver://")) {
                StringBuilder sb = new StringBuilder();
                sb.append(jdbcurl.getText())
                        .append(hostname.getText())
                        .append(":").append(Integer.toString(getPort()))
                        .append(";databaseName=").append(database.getText())
                        .append(((additionalParameter.getText() != null) || (additionalParameter.getText().trim().length() > 0)) ? additionalParameter.getText() : "");
                con = DriverManager.getConnection(sb.toString(), username.getText(), String.valueOf(password.getPassword()));
                connectionEstablished = true;
            }

            // Apache Derby
            if (jdbcurl.getText().equalsIgnoreCase("jdbc:derby:")) {
                // embedded
                if (getPort() == 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(jdbcurl.getText())
                            .append(database.getText())
                            .append(((additionalParameter.getText() != null) || (additionalParameter.getText().trim().length() > 0)) ? ";" + additionalParameter.getText() : "");
                    con = DriverManager.getConnection(sb.toString());
                    connectionEstablished = true;
                } else {
                    // networked
                    StringBuilder sb = new StringBuilder();
                    sb.append(jdbcurl.getText())
                            .append("//").append(hostname.getText())
                            .append(":").append(Integer.toString(getPort()))
                            .append("/").append(database.getText())
                            .append(((additionalParameter.getText() != null) || (additionalParameter.getText().trim().length() > 0)) ? ";" + additionalParameter.getText() : "");
                    if ((username.getText() == null) || (username.getText().length() < 1)) {
                        con = DriverManager.getConnection(sb.toString());
                        connectionEstablished = true;
                    } else {
                        con = DriverManager.getConnection(sb.toString(), username.getText(), String.valueOf(password.getPassword()));
                        connectionEstablished = true;
                    }
                }
            }

            // SQLite
            if (jdbcurl.getText().equalsIgnoreCase("jdbc:sqlite:")) {
                con = DriverManager.getConnection(jdbcurl.getText() + "//" + database.getText());
                connectionEstablished = true;
            }

            // ucanaccess
            if (jdbcurl.getText().equalsIgnoreCase("jdbc:ucanaccess://")) {
                StringBuilder sb = new StringBuilder();
                sb.append(jdbcurl.getText())
                        .append(database.getText())
                        .append(((additionalParameter.getText() != null) || (additionalParameter.getText().trim().length() > 0)) ? ";" + additionalParameter.getText() : "");

                System.out.println(sb.toString());

                if ((username.getText() == null) || (username.getText().length() < 1)) {
                    con = DriverManager.getConnection(sb.toString());
                    connectionEstablished = true;
                } else {
                    con = DriverManager.getConnection(sb.toString(), username.getText(), String.valueOf(password.getPassword()));
                    connectionEstablished = true;
                }

            }

            // all others
            if (con == null) {
                StringBuilder sb = new StringBuilder();
                sb.append(jdbcurl.getText())
                        .append(hostname.getText())
                        .append(getPort() > 0 ? ":" + port.getText() : "")
                        .append("/").append(database.getText())
                        .append(((additionalParameter.getText() != null) || (additionalParameter.getText().trim().length() < 1)) ? "" : "?" + additionalParameter.getText());
                if ((username.getText() == null) || (username.getText().trim().length() < 1)) {
                    con = DriverManager.getConnection(sb.toString());
                    connectionEstablished = true;
                } else {
                    con = DriverManager.getConnection(sb.toString(), username.getText(), String.valueOf(password.getPassword()));
                    connectionEstablished = true;
                }

            }

            if (con != null) {
                DatabaseMetaData dmd = con.getMetaData();
                setDataBaseProductName(dmd.getDatabaseProductName() + " " + dmd.getDatabaseProductVersion());
            }

        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, "Can not establish connection to destination DB {0}", sqlex.getMessage());
            connectionEstablished = false;
            setConnectionError(sqlex.getLocalizedMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing resultset {0}", sqlex.getMessage());
                }
            }
        }

        return connectionEstablished;
    }

    public static void main(String[] args) {
        CloseableFrame closeableFrame = new CloseableFrame("DB Export/Import", true);
        closeableFrame.getContentPane().setLayout(new BorderLayout());
        DataBaseExportGui dbg = new DataBaseExportGui();

        closeableFrame.getContentPane().add(dbg, BorderLayout.CENTER);
        closeableFrame.pack();
        closeableFrame.setVisible(true);
        dbg.cancel.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

    }

    /**
     * @return the dataBaseProductName
     */
    public String getDataBaseProductName() {
        return dataBaseProductName;
    }

    /**
     * @param dataBaseProductName the dataBaseProductName to set
     */
    public void setDataBaseProductName(String dataBaseProductName) {
        this.dataBaseProductName = dataBaseProductName;
    }

    /**
     * @return the connectionError
     */
    public String getConnectionError() {
        return connectionError;
    }

    /**
     * @param connectionError the connectionError to set
     */
    public void setConnectionError(String connectionError) {
        this.connectionError = connectionError;
    }

}
