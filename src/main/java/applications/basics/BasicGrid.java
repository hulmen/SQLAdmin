package applications.basics;

/** 
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
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.border.*;
import java.awt.datatransfer.*;
import java.util.logging.*;

public class BasicGrid extends JPanel {

    public GenerateQuery generateQuery;
    public JTable theTable;
    private Dimension listPanelSize = new Dimension(0, 0);
    private Dimension mainPanelSize = new Dimension(0, 0);
    public JTabbedPane tabbedPane;
    public JSplitPane splitPane;
    /*
     * displayMode controls how the form is displayed:
     * 1 = in a single page, a splitpane on upper part is the list on the lower part is the form
     * 2 = in a tabbed pane, where tabs are on top.
     * 3 = in a tabbed pane, where tabs are on the left
     */
    private int displayMode = 2;
    Logger logger;
    private int selectedRow = 0;
    t_connect con = null;

    public t_connect getCon() {
        if (con == null) {
            con = new t_connect(getAHost(), getAUser(), getAPassword(), getADatabase());
        }
        return con;
    }

    public void setCon(t_connect v) {
        this.con = v;
    }
    public ImageButton connect;
    String aHost = null;

    /**
     * Get the value of host.
     * @return Value of host.
     */
    public String getAHost() {
        return aHost;
    }

    /**
     * Set the value of host.
     * @param v  Value to assign to host
     */
    public void setAHost(String v) {
        this.aHost = v;
    }
    String aUser = null;

    /**
     * Get the value of user
     * @return Value of user
     */
    public String getAUser() {
        return aUser;
    }

    /**
     * Set the Value of user
     * @param v Value to assign to user
     */
    public void setAUser(String v) {
        this.aUser = v;
    }
    String aPassword = null;

    /**
     * Get the value of password
     * @return Value of password
     */
    public String getAPassword() {
        return aPassword;
    }

    /**
     * Set the Value of password
     * @param v Value to assign to password
     */
    public void setAPassword(String v) {
        this.aPassword = v;
    }
    String aDatabase = null;

    /**
     * Get the value of database
     * @return Value of database
     */
    public String getADatabase() {
        return aDatabase;
    }

    /**
     * Set the Value of Database
     * @param v Value to assign to Database
     */
    public void setADatabase(String v) {
        this.aDatabase = v;
    }
    String aSchema = "%";

    /**
     * Get the value of schema
     * @return Value of schema
     */
    public String getASchema() {
        return aSchema;
    }

    /**
     * Set the Value of schema
     * @param v Value to assign to schema
     */
    public void setASchema(String v) {
        this.aSchema = v;
    }
    public ButtonPanel bp;
    public FMenuBar mb;
    public JTabbedPane workPane;

    private JPanel loginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        Insets insets = new Insets(5, 5, 5, 5);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = insets;

        final JTextField askUser, askHost, askDatabase, askSchema;
        final JPasswordField askPassword;

        askUser = new JTextField(15);
        askUser.setText(getAUser());

        askHost = new JTextField(15);
        askHost.setText(getAHost());

        askDatabase = new JTextField(15);
        askDatabase.setText(getADatabase());

        askSchema = new JTextField(15);
        askSchema.setText(getASchema());

        askPassword = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Host"), gbc);
        gbc.gridx = 1;
        panel.add(askHost, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("User"), gbc);
        gbc.gridx = 1;
        panel.add(askUser, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password"), gbc);
        gbc.gridx = 1;
        panel.add(askPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Database"), gbc);
        gbc.gridx = 1;
        panel.add(askDatabase, gbc);


        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Schema"), gbc);
        gbc.gridx = 1;
        panel.add(askSchema, gbc);

        JPanel button2Panel = new JPanel();
        //connect = new ImageButton(null,"plug.gif","Connect to database");
        connect.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                setAHost(askHost.getText());
                setAUser(askUser.getText());
                setAPassword(String.valueOf(askPassword.getPassword()));
                setADatabase(askDatabase.getText());
                setASchema(askSchema.getText());
                if (verify()) {
                    init();
                } else {
                    JOptionPane.showMessageDialog(null, "Connection failed", "Invalid Parameters", JOptionPane.WARNING_MESSAGE);
                    logger.log(Level.WARNING, "Connection refused: Host: " + getAHost() + " User: " + getAUser() + " DB: " + getADatabase());

                }
            }
        });

        button2Panel.add(connect);
        button2Panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        JPanel aPanel = new JPanel();
        aPanel.setLayout(new BorderLayout());
        aPanel.add(panel, BorderLayout.CENTER);
        aPanel.add(button2Panel, BorderLayout.SOUTH);


        // set default Parameters out of the environment
        if (System.getProperty("admin.db") != null) {
            askDatabase.setText(System.getProperty("admin.db"));
        }
        if (System.getProperty("admin.host") != null) {
            askHost.setText(System.getProperty("admin.host"));
        }
        if (System.getProperty("admin.schema") != null) {
            askSchema.setText(System.getProperty("admin.schema"));
        }
        if (System.getProperty("admin.user") != null) {
            askUser.setText(System.getProperty("admin.user"));
        }


        return aPanel;
    }

    // Test the connection
    public boolean verify() {
        boolean ok = true;
        String rdbms = " ";
        getCon();
        if (con.getError() != null) {
            logger.log(Level.WARNING, "Connection Error: {0}", con.getError());
            ok = false;
        } else {
            try {
                DatabaseMetaData md = con.con.getMetaData();
                rdbms = md.getDatabaseProductName();
                con.close();
            } catch (Exception exception) {
                ok = false;
                logger.log(Level.INFO, "Unable to get DatabaseMetaData{0}", exception.getMessage());
            }
        }


        return ok;
    }
    String workingTable = null;

    /**
     * Get the value of workingTable.
     * @return Value of workingTable.
     */
    public String getWorkingTable() {

        if (workingTable == null) {
            this.workingTable = JOptionPane.showInputDialog(this, "Table to work on: ");
        }
        return this.workingTable;
    }

    /**
     * Set the value of workingTable.
     * @param v  Value to assign to workingTable.
     */
    public void setWorkingTable(String v) {
        this.workingTable = v;
    }

    public boolean verifyDelete() {
        String string1 = "Yes";
        String string2 = "No";
        Object[] options = {string1, string2};
        int n = JOptionPane.showOptionDialog(this,
                "Do you really want to delete?",
                "Delete  ?",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, //don't use a custom Icon
                options, //the titles of buttons
                string2); //the title of the default button
        if (n == JOptionPane.YES_OPTION) {
            return true;
        } else {
            return false;
        }
    }
    Dimension mySize;

    public void setMySize(Dimension v) {
        this.mySize = v;
    }

    public Dimension getMySize() {
        return mySize;
    }

    public void doListeners() {
        ;
    }

    public BasicGrid() {
        beforeEverything();
        firstStep();
        atTheBeginning();
        secondStep();
        this.setLayout(new BorderLayout());
        this.add(loginPanel(), BorderLayout.CENTER);
    }

    public BasicGrid(String database, String table) {
        beforeEverything();
        firstStep();
        atTheBeginning();
        secondStep();
        this.setADatabase(database);
        this.setWorkingTable(table);
        this.setLayout(new BorderLayout());
        this.add(loginPanel(), BorderLayout.CENTER);
        init();
    }

    /**
     *   @param con this is the t_connect to reuse existing connections
     *   @param table the name of the table to work on
     *   @param schema the schema of this database (mostly fits by using "%")
     **/
    public BasicGrid(t_connect con,
            String table,
            String schema) {
        setCon(con);
        this.setAHost(con.getHost());
        this.setAUser(con.getUser());
        this.setAPassword(con.getPassword());
        this.setADatabase(con.getDatabase());
        this.setASchema(schema);
        this.setWorkingTable(table);
        beforeEverything();
        firstStep();
        atTheBeginning();
        secondStep();
        init();
    }

    /**
     *   @param con this is the t_connect to reuse existing connections
     *   @param table the name of the table to work on
     **/
    public BasicGrid(t_connect con,
            String table) {
        setCon(con);
        this.setAHost(con.getHost());
        this.setAUser(con.getUser());
        this.setAPassword(con.getPassword());
        this.setADatabase(con.getDatabase());
        this.setASchema("%");
        this.setWorkingTable(table);
        beforeEverything();
        firstStep();
        atTheBeginning();
        secondStep();

        init();
    }

    /**
     *   @param host the hostname where the RDBMS is
     *   @param user the userid to connect to the RDBMS
     *   @param password the password to connect under userid to the RDBMS
     *   @param database the databasename to connect to
     *   @param table the name of the table to work on
     *   @param schema the schema of this database (mostly fits by using "%")
     **/
    public BasicGrid(String host,
            String user,
            String password,
            String database,
            String table,
            String schema) {
        beforeEverything();
        firstStep();
        atTheBeginning();
        secondStep();
        this.setAHost(host);
        this.setAUser(user);
        this.setAPassword(password);
        this.setADatabase(database);
        this.setASchema(schema);
        this.setWorkingTable(table);
        init();
    }

    public BasicGrid(String host,
            String user,
            String password,
            String database,
            String table) {
        beforeEverything();
        firstStep();
        atTheBeginning();
        secondStep();
        this.setAHost(host);
        this.setAUser(user);
        this.setAPassword(password);
        this.setADatabase(database);
        this.setASchema("%");
        this.setWorkingTable(table);
        init();
    }

    public BasicGrid(String host,
            String user,
            String password,
            String database) {
        beforeEverything();
        firstStep();
        atTheBeginning();
        secondStep();
        this.setAHost(host);
        this.setAUser(user);
        this.setAPassword(password);
        this.setADatabase(database);
        this.setASchema("%");
        init();
    }

    public void beforeEverything() {
    }

    private void atTheBeginning() {
        bp = new ButtonPanel();
        mb = new FMenuBar();
        //workPane = new JTabbedPane();
        connect = new ImageButton("Connect", "plug.gif", "Connect to database");

    }

    public void addMenuBar(JMenuBar m) {
        this.add(m, BorderLayout.NORTH);
    }

    public void addMenuBar(JFrame f, JMenuBar m) {
        f.getRootPane().setJMenuBar(m);
        //f.setJMenuBar(m);
    }

    public void firstStep() {
        // overwrite this method for your own initis
    }

    public void secondStep() {
        // overwrite this method for your own initis
    }

    public void postListPanelAction() {
        // overwrite this method for your own purpose
    }

    private void splittedLayout() {
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPanel(), formPanel());
        splitPane.setOneTouchExpandable(true);
        this.add(BorderLayout.CENTER, splitPane);
        this.add(BorderLayout.SOUTH, buttonPanel());
    }

    private void tabbedLayoutTOP() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(BorderLayout.CENTER, formPanel());
        panel.add(BorderLayout.SOUTH, buttonPanel());

        tabbedPane.addTab("Data", panel);
        tabbedPane.addTab("Search", generateQuery);
        tabbedPane.addTab("List", listPanel());

        this.add(tabbedPane);
    }

    private void tabbedLayoutLEFT() {
        tabbedPane = new JTabbedPane(JTabbedPane.LEFT);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(BorderLayout.CENTER, formPanel());
        panel.add(BorderLayout.SOUTH, buttonPanel());

        tabbedPane.addTab("Data", panel);
        tabbedPane.addTab("Search", generateQuery);
        tabbedPane.addTab("List", listPanel());

        this.add(tabbedPane);
    }

    private void init() {
        logger = logger.getLogger("applications.basics");
        this.removeAll();
        generateQuery = new GenerateQuery(getAHost(), getAUser(), getAPassword(), getADatabase(), getWorkingTable());

        this.setLayout(new BorderLayout());

        if (getDisplayMode() == 1) {
            splittedLayout();
        } else {
            if (getDisplayMode() == 2) {
                tabbedLayoutTOP();
            } else {
                tabbedLayoutLEFT();
            }
        }

        doListeners();

        //this.add(completeFormPanel(),gbc);
        this.updateUI();
        doIt();

    }

    // overwrite this method with the right one
    public JScrollPane formPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("put the formpanel here"));
        return new JScrollPane(panel);
    }

    public JPanel searchPanel() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("put the searchpanel here"));
        return panel; 
    }

    public JScrollPane listPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        theTable = new JTable();

        return new JScrollPane(theTable);
    }

    private JPanel buttonPanel() {
        return bp;
    }

    public JPanel completeFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();

        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(formPanel(), gbc);

        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridheight = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridy = 1;
        panel.add(bp, gbc);


        return panel;
    }

    public void msg(String s) {
        if (!s.startsWith("ok")) {
            JOptionPane.showMessageDialog(null, s, "Message", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void doIt() {
    }

    public static void main(String args[]) {

        BasicGrid bg = new BasicGrid();

        final JFrame frame = new JFrame("Basic Grid");


        frame.getContentPane().add(bg);
        frame.pack();
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
        bg.bp.cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        bg.mb.cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        frame.setVisible(true);


    }

    /**
     * @return the listPanelSize
     */
    public Dimension getListPanelSize() {
        logger.log(Level.FINEST, "Getting Size:{0}", listPanelSize.toString());
        return listPanelSize;
    }

    /**
     * @param listPanelSize the listPanelSize to set
     */
    public void setListPanelSize(Dimension listPanelSize) {
        logger.log(Level.FINEST, "Setting Size:{0}", listPanelSize.toString());
        this.listPanelSize = listPanelSize;
    }

    /**
     * @return the mainPanelSize
     */
    public Dimension getMainPanelSize() {
        logger.log(Level.FINEST, "Getting Size:{0}", mainPanelSize.toString());
        return mainPanelSize;
    }

    /**
     * @param mainPanelSize the mainPanelSize to set
     */
    public void setMainPanelSize(Dimension mainPanelSize) {
        logger.log(Level.FINEST, "setting Size:{0}", mainPanelSize.toString());
        this.mainPanelSize = mainPanelSize;
    }

    /**
     * @return the displayMode
     */
    public int getDisplayMode() {
        return displayMode;
    }

    /**
     * @param displayMode the displayMode to set
     */
    public void setDisplayMode(int displayMode) {
        this.displayMode = displayMode;
    }

    /**
     * @return the selectedRow
     */
    public int getSelectedRow() {
        return selectedRow;
    }

    /**
     * @param selectedRow the selectedRow to set
     */
    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }
}
