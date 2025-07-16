/*
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
package sql.fredy.ui;

import com.toedter.calendar.JDateChooser;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;
import sql.fredy.connection.DataSource;

/**
 *
 * @author Fredy Fischer
 *
 */
public final class ReportViewer extends JPanel {

    private Logger logger = Logger.getLogger("sql.fredy.ui");

    private String host;
    private String user;
    private String database;
    private String password;
    private String dataSource;
    public ImageButton cancel;
    private JTextField fileName;
    private JScrollPane viewer;
    private HashMap parameters = null;
    private HashMap<String, String> queryParameter = null;

    public ReportViewer() {
        initGui();
    }

    public ReportViewer(java.sql.Connection c) {
        setCon(c);
        initGui();
    }

    public ReportViewer(String host, String user, String database, String password) {
        initGui();
    }

    public void close() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {

            }
        }
    }

    private void initGui() {

        this.setLayout(new BorderLayout());
        this.add(BorderLayout.NORTH, labelPanel());
        //this.add(BorderLayout.CENTER, filePanel());
        //this.add(BorderLayout.CENTER, viewerPanel());
        this.add(BorderLayout.SOUTH, buttonPanel());

    }

    private JPanel labelPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("This is to display Jasper-Reports"));
        panel.setBorder(new EtchedBorder());
        return panel;
    }

    private JPanel buttonPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(filePanel());

        final ImageButton view = new ImageButton(null, "document.gif", "View selected report");
        view.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                displayReport(fileName.getText());
            }
        });
        panel.add(view);

        panel.add(new JSeparator());

        cancel = new ImageButton(null, "exit.gif", "Close this window");
        panel.add(cancel);

        panel.setBorder(new EtchedBorder());
        return panel;
    }

    private HashMap findParameters(String fileName) {
        HashMap<String, JRParameter> map = new HashMap<>();
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(new File(fileName));
            JRParameter[] params = jasperReport.getParameters();

            for (JRParameter param : params) {
                if (!param.isSystemDefined() && param.isForPrompting()) {
                    Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Parameter {0} found", param.getName());
                    map.put(param.getName(), param);
                }

            }

        } catch (JRException jre) {
            Logger.getLogger(ReportViewer.class.getName()).log(Level.WARNING, "Eexception on finding parameterss {0}", jre.getMessage());
        }
        return map;
    }

    private HashMap getParameters(String fileName) {
        HashMap map = findParameters(fileName);
        queryParameter = new HashMap();

        if (map.isEmpty()) {
            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no parameters found");
            return null;
        }

        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;

        parameters = new HashMap();
        for (Object key : map.keySet()) {

            /*
             Das ist in der Map
             JRParameter param
             param.getName();
             param.getDescription();
             param.getDefaultValueExpression();
             param.getNestedTypeName();

             */
            JRParameter param = (JRParameter) map.get((String) key);

            gbc.gridx = 0;

            String text = param.getDescription();
            try {
                if (text.length() < 2) {
                    text = param.getName();
                }
            } catch (NullPointerException npe) {
                text = param.getName();
            }

            JLabel label = new JLabel(text);
            label.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
            panel.add(label, gbc);

            gbc.gridx = 1;

            boolean def = true;

            queryParameter.put(param.getName(), param.getValueClassName());

            if ("java.util.Date".equals(param.getValueClassName())) {
                def = false;
                JDateChooser df = new JDateChooser();
                df.setName(param.getName());
                df.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
                panel.add(df, gbc);
            }
            if ("java.lang.Double".equals(param.getValueClassName())) {
                def = false;
                JTextField df = new JTextField(30);
                df.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
                df.setName(param.getName());
                df.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
                try {
                    df.setText(param.getDefaultValueExpression().getText());
                } catch (Exception e) {
                    Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no default value found for parameter");
                }
                panel.add(df, gbc);
            }
            if ("java.lang.Float".equals(param.getValueClassName())) {
                def = false;
                JTextField df = new JTextField(30);
                df.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
                df.setName(param.getName());
                df.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
                try {
                    df.setText(param.getDefaultValueExpression().getText());
                } catch (Exception e) {
                    Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no default value found for parameter");
                }
                panel.add(df, gbc);
            }
            if ("java.lang.Long".equals(param.getValueClassName())) {
                def = false;
                JTextField df = new JTextField(30);
                df.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
                df.setName(param.getName());
                df.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
                try {
                    df.setText(param.getDefaultValueExpression().getText());
                } catch (Exception e) {
                    Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no default value found for parameter");
                }
                panel.add(df, gbc);
            }
            if ("java.lang.Short".equals(param.getValueClassName())) {
                def = false;
                JTextField df = new JTextField(30);
                df.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
                df.setName(param.getName());
                df.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
                try {
                    df.setText(param.getDefaultValueExpression().getText());
                } catch (Exception e) {
                    Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no default value found for parameter");
                }
                panel.add(df, gbc);
            }
            if ("java.lang.Integer".equals(param.getValueClassName())) {
                def = false;
                JTextField df = new JTextField(30);
                df.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));
                df.setName(param.getName());
                df.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
                try {
                    df.setText(param.getDefaultValueExpression().getText());
                } catch (Exception e) {
                    Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no default value found for parameter");
                }
                panel.add(df, gbc);
            }
            if ("java.math.BigDecimal".equals(param.getValueClassName())) {
                def = false;
                JTextField df = new JTextField(30);
                df.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
                df.setName(param.getName());
                df.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
                try {
                    df.setText(param.getDefaultValueExpression().getText());
                } catch (Exception e) {
                    Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no default value found for parameter");
                }
                panel.add(df, gbc);
            }
            if ("java.lang.Number".equals(param.getValueClassName())) {
                def = false;
                JTextField df = new JTextField(30);
                df.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
                df.setName(param.getName());
                df.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
                try {
                    df.setText(param.getDefaultValueExpression().getText());
                } catch (Exception e) {
                    Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no default value found for parameter");
                }
                panel.add(df, gbc);
            }

            if ("java.lang.Boolean".equals(param.getValueClassName())) {
                def = false;
                JCheckBox df = new JCheckBox();
                df.setName(param.getName());
                df.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
                try {
                    if ("true".equals(param.getDefaultValueExpression().getText().toLowerCase())) {
                        df.setSelected(true);
                    }
                } catch (Exception e) {
                    Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no default value found for parameter");
                }
                panel.add(df, gbc);
            }

            if (def) {
                JTextField df = new JTextField(30);
                df.setName(param.getName());
                df.setToolTipText(param.getName().toString() + " is of class " + param.getValueClassName());
                try {
                    df.setText(param.getDefaultValueExpression().getText());
                } catch (Exception e) {
                    Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no default value found for parameter");
                }
                panel.add(df, gbc);
            }

            gbc.gridy++;

        }

        panel.setBorder(new EtchedBorder());
        final JDialog paras = new JDialog();
        paras.setModal(true);
        paras.setTitle("please enter parameters");
        JPanel bp = new JPanel();
        bp.setLayout(new FlowLayout());
        ImageButton ok = new ImageButton(null, "dataextract.gif", "run report");

        ok.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Parameter contains {0} components", panel.getComponentCount());
                for (int i = 0; i < panel.getComponentCount(); i++) {
                    Component component = panel.getComponent(i);
                    if (component.getName() != null) {
                        String parameterClass = queryParameter.get(component.getName());

                        /*
                         we loop all components and support Object classes listet below
                         in any other case (def = true) we use Sting as default
                         */
                        boolean def = true;

                        if ("java.util.Date".equals(parameterClass)) {
                            def = false;
                            JDateChooser df = (JDateChooser) component;
                            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Dialog Parameter {0} found", component.getName());
                            parameters.put(component.getName(), (java.util.Date) df.getDate());
                        }

                        if ("java.lang.Double".equals(parameterClass)) {
                            def = false;
                            JTextField tf = (JTextField) component;
                            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Dialog Parameter {0} found", component.getName());

                            try {
                                parameters.put(component.getName(), Double.valueOf(tf.getText()));
                            } catch (NumberFormatException dEx) {
                                Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no value found using 0");
                                double d = 0;
                                parameters.put(component.getName(), d);
                            }
                        }

                        if ("java.lang.Float".equals(parameterClass)) {
                            def = false;
                            JTextField tf = (JTextField) component;
                            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Dialog Parameter {0} found", component.getName());

                            try {
                                parameters.put(component.getName(), Float.valueOf(tf.getText()));
                            } catch (NumberFormatException dEx) {
                                Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no value found using 0");
                                float d = 0;
                                parameters.put(component.getName(), d);
                            }
                        }

                        if ("java.lang.Long".equals(parameterClass)) {
                            def = false;
                            JTextField tf = (JTextField) component;
                            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Dialog Parameter {0} found", component.getName());

                            try {
                                parameters.put(component.getName(), Long.valueOf(tf.getText()));
                            } catch (NumberFormatException dEx) {
                                Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no value found using 0");
                                long d = 0;
                                parameters.put(component.getName(), d);
                            }
                        }

                        if ("java.lang.Short".equals(parameterClass)) {
                            def = false;
                            JTextField tf = (JTextField) component;
                            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Dialog Parameter {0} found", component.getName());

                            try {
                                parameters.put(component.getName(), Short.valueOf(tf.getText()));
                            } catch (NumberFormatException dEx) {
                                Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no value found using 0");
                                short d = 0;
                                parameters.put(component.getName(), d);
                            }
                        }

                        if ("java.lang.Integer".equals(parameterClass)) {
                            def = false;
                            JTextField tf = (JTextField) component;
                            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Dialog Parameter {0} found", component.getName());

                            try {
                                parameters.put(component.getName(), (int) Integer.parseInt(tf.getText()));
                            } catch (NumberFormatException dEx) {
                                Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no value found using 0");
                                short d = 0;
                                parameters.put(component.getName(), d);
                            }
                        }

                        if ("java.math.BigDecimal".equals(parameterClass)) {
                            def = false;
                            JTextField tf = (JTextField) component;
                            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Dialog Parameter {0} found", component.getName());

                            try {
                                parameters.put(component.getName(), new java.math.BigDecimal(tf.getText()));
                            } catch (NumberFormatException dEx) {
                                Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no value found using 0");
                                parameters.put(component.getName(), new java.math.BigDecimal(0));
                            }
                        }

                        if ("java.lang.Number".equals(parameterClass)) {
                            def = false;
                            JTextField tf = (JTextField) component;
                            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Dialog Parameter {0} found", component.getName());

                            try {
                                parameters.put(component.getName(), Float.valueOf(tf.getText()));
                            } catch (NumberFormatException dEx) {
                                Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "no value found using 0");
                                short d = 0;
                                parameters.put(component.getName(), d);
                            }
                        }

                        if ("java.lang.Boolean".equals(parameterClass)) {
                            def = false;
                            JCheckBox cb = (JCheckBox) component;
                            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Dialog Parameter {0} found", component.getName());

                            if (cb.isSelected()) {
                                parameters.put(component.getName(), true);
                            } else {
                                parameters.put(component.getName(), false);
                            }

                        }
                        if (def) {
                            JTextField cf = (JTextField) component;
                            Logger.getLogger(ReportViewer.class.getName()).log(Level.INFO, "Dialog Parameter {0} found", component.getName());
                            parameters.put(component.getName(), (String) cf.getText());
                        }

                    }
                }
                paras.setVisible(false);
                paras.dispose();
            }
        });

        bp.add(ok);
        bp.setBorder(new EtchedBorder());

        paras.setLayout(new BorderLayout());
        paras.add(BorderLayout.CENTER, new JScrollPane(panel));
        paras.add(BorderLayout.SOUTH, bp);
        paras.pack();
        paras.setVisible(true);

        return parameters;
    }

    private void displayReport(String fileName) {

        JRViewer jrviewer;
        try {
            JasperPrint print = null;
            print = JasperFillManager.fillReport(fileName, getParameters(fileName), getCon());
            jrviewer = new JRViewer(print);

            /*
             This is if all is internally displaye
             it is commented out, because an own window is cooler
             */
 /*
             viewer.removeAll();
             viewer.setLayout(new BorderLayout());
             viewer.add(jrviewer);
             */
 /*
             this puts the viewer in its own window
             */
            CloseableFrame cf = new CloseableFrame("Report: " + fileName);
            cf.setExitOnClose(false);

            cf.getContentPane().setLayout(new BorderLayout());

            cf.getContentPane().add(jrviewer);
            cf.pack();
            cf.setVisible(true);

            Logger
                    .getLogger(ReportViewer.class
                            .getName()).log(Level.INFO, "Report created");
        } catch (JRException ex) {
            Logger.getLogger(ReportViewer.class
                    .getName()).log(Level.SEVERE, "Error while running Report", ex);
        }

    }

    private JPanel filePanel() {

        fileName = new JTextField(30);

        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Report Definition File"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(fileName, gbc);

        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        JButton filer1 = new JButton("...");
        panel.add(filer1, gbc);

        filer1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                chooser.setDialogTitle("Select control File");
                chooser.setFileFilter(new sql.fredy.io.MyFileFilter("jasper"));

                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //setFile(chooser.getSelectedFile());
                    fileName.setText(chooser.getCurrentDirectory()
                            + java.io.File.separator
                            + chooser.getSelectedFile().getName());
                }
            }
        });

        //panel.setBorder(new TitledBorder("File Settings"));
        panel.setBorder(new EtchedBorder());
        return panel;
    }

    private JScrollPane viewerPanel() {
        viewer = new JScrollPane();

        viewer.setBorder(new TitledBorder("Parameters"));

        return viewer;
    }

    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    public Connection getConnection() {

        try {
            if ((null == con) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
                setDatabase(DataSource.getInstance().getDataBase());
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {

        }
        return con;
    }

    Connection con;

    /**
     * @return the con
     */
    public Connection getCon() {
      
        return getConnection();
    }

    /**
     * @param con the con to set
     */
    public void setCon(Connection con) {
        this.con = con;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the dataSource
     */
    public String getDataSource() {
        return dataSource;
    }

    public static void main(String args[]) {

        String host = null;
        String user = null;
        String password = null;
        String db = null;
        String jdbcDriver = null;
        String jdbcURL = null;
        boolean cont = false;
        String selection = "";
        Connection con = null;

        System.out.println("This is Fredy's Report Viewer\n"
                + "-----------------------------\n"
                + "It displays Jasper Reports\n"
                + "To run it from the commandline, you have different launching options:\n\n"
                + "the first methode is to launch it by using the SQLAdmin environment, so start it as follows:\n"
                + "java sql.fredy.ui.Reportviewer\n\n"
                + "or by just telling the tool the JDBC-Driver and connection URL.\n"
                + "java sql.fredy.ui.Reportviewer -jdbc jdbc-driver (e.g. com.mysql.jdbc.Driver) -url connectionURL (e.g. jdbc:mysql://localhost/test?user=monty&password=greatsqldb\n\n"
        );

        if (args.length < 2) {
            System.out.println("No parameters provided\n\n"
                    + "please select:\n"
                    + "1 = I want to use SQLadmin way to connect\n"
                    + "2 = I want to provide the JDBC connection parameters\n"
                    + "3 = oh no, please quit\n"
            );

            selection = readFromPrompt("Please enter your selection", "3");
            if (selection.toUpperCase().startsWith("1")) {
                con = null;
                cont = true;
            }
            if (selection.toUpperCase().startsWith("2")) {
                jdbcDriver = readFromPrompt("JDBC Driver: ", "");
                jdbcURL = readFromPrompt("JDBC URL: ", "");
                try {
                    Class.forName(jdbcDriver);
                    con = DriverManager.getConnection(jdbcURL);
                } catch (SQLException sqe) {
                    System.out.println("SQLException: " + sqe.getMessage());
                    System.out.println("SQLState: " + sqe.getSQLState());
                    System.out.println("VendorError: " + sqe.getErrorCode());
                    System.exit(0);
                } catch (ClassNotFoundException cnfe) {
                    System.out.println("Class not found: " + jdbcDriver + ": " + cnfe.getMessage());
                    System.exit(0);
                } catch (Exception ex) {
                    System.out.println("Exception while creating connection: " + ex.getMessage());
                    System.exit(0);
                }
                cont = true;
            }

        } else {
            int i = 0;
            while (i < args.length) {
                if ((args[i].equals("-jdbc"))) {
                    i++;
                    jdbcDriver = args[i];
                }
                if ((args[i].equals("-url"))) {
                    i++;
                    jdbcURL = args[i];
                }

                i++;
            }

            // I know a JDBC Driver, a connection URL  is provided
            if ((jdbcDriver.length() > 1) && (jdbcURL.length() > 1)) {
                try {
                    Class.forName(jdbcDriver);
                    con = DriverManager.getConnection(jdbcURL);
                } catch (SQLException sqe) {
                    System.out.println("SQLException: " + sqe.getMessage());
                    System.out.println("SQLState: " + sqe.getSQLState());
                    System.out.println("VendorError: " + sqe.getErrorCode());
                    System.exit(0);
                } catch (ClassNotFoundException cnfe) {
                    System.out.println("Class not found: " + jdbcDriver + ": " + cnfe.getMessage());
                    System.exit(0);
                } catch (Exception ex) {
                    System.out.println("Exception while creating connection: " + ex.getMessage());
                    System.exit(0);
                }
                cont = true;
            }
        }

        if (!cont) {
            System.out.println("Good bye\n");
            System.exit(0);
        }
        JFrame cf = new JFrame("Fredy's Report Viewer");
        ReportViewer viewer = new ReportViewer(con);
        cf.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {

            }

            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                viewer.close();
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

        cf.setLayout(new BorderLayout());

        cf.add(BorderLayout.CENTER, viewer);

        viewer.cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                viewer.close();
                System.exit(0);
            }
        });
        cf.pack();
        cf.setVisible(true);

    }

    /* read from commandline
     *
     * 1st parameter as displaytext
     * 2nd parameter as defaultvalue
     */
    public static String readFromPrompt(String text, String defValue) {
        String fromPrompt = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(text + " (Default: " + defValue + ") ");
        try {
            fromPrompt = br.readLine();
            if (fromPrompt.length() < 1) {
                fromPrompt = defValue;
            }
        } catch (IOException ioe) {
            fromPrompt = defValue;
        }
        return fromPrompt;
    }
}
