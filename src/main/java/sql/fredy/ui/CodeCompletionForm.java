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

 you need to make sure, a Derby-DB instance is running on the server to connect to.
 So please make sure, you launch sql.fredy.tools.DerbyInfoServer in advance and
 make sure, these tables exists:
 
 create table CodeCompletion (
 ToolUser VARCHAR(128) not null,
 TemplateName VARCHAR(25) NOT NULL ,
 Abbreviation VARCHAR(25) ,
 CodeTemplate VARCHAR(4096) ,
 Description VARCHAR(4096)  ,
 PRIMARY KEY (ToolUser,TemplateName)
 )       
 create  index ccIdx on CodeCompletion (Abbreviation, ToolUser)

 create table AdminParameter (ToolUser VARCHAR(128), Name VARCHAR(255), Content VARCHAR(4096) );
 insert into AdminParameter(Name, Content) values ('alive','yes')



 */
package sql.fredy.ui;

import sql.fredy.tools.DerbyInfoServer;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

/**
 *
 * @author sql@hulmen.ch
 */
public class CodeCompletionForm extends JPanel {

    private static Level LOGLEVEL = Level.FINE;
    public ImageButton cancel;

    private RSyntaxTextArea codeTemplate;
    private JTextArea description;
    private JTextField abbreviation;
    private JTextField name;
    private Connection con;
    private String dbName;
    private CodeCompletionList ccl;
    private JTable tableView;
    private PreparedStatement insert;
    private PreparedStatement update;
    private DefaultCompletionProvider provider;
    private int activeRow = -1;

    String connectionURL;
    String driver;
    int loopCounter = 0;

    private Logger logger = Logger.getLogger("sql.fredy.ui");

    public CodeCompletionForm() {
        provider = new DefaultCompletionProvider();
        doIt();

    }

    public CodeCompletionForm(DefaultCompletionProvider p) {
        setProvider(p);
        doIt();

    }

    private void doIt() {
        initDB();

        this.setLayout(new BorderLayout());

        JSplitPane jsp = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listPanel(), infoPanel());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(jsp, BorderLayout.CENTER);
        panel.add(buttonPanel(), BorderLayout.SOUTH);
        this.add(panel, BorderLayout.CENTER);
        this.setOpaque(true);
    }

    private JPanel infoPanel() {
        JPanel panel = new JPanel();

        panel.setLayout(new BorderLayout());

        name = new JTextField(25);
        abbreviation = new JTextField(25);
        description = new JTextArea(10, 80);
        codeTemplate = new RSyntaxTextArea(10, 80);

        codeTemplate.setFont(new Font("Monospaced", Font.PLAIN, 12));
        codeTemplate.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);

        abbreviation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (abbreviationExist(abbreviation.getText())) {
                    JOptionPane.showMessageDialog(null, "Abbreviation already exists");
                    abbreviation.setText("");
                }
            }
        });

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new FlowLayout());
        textPanel.setAlignmentX(LEFT_ALIGNMENT);      
        
        textPanel.add(new JLabel("Name"));
        textPanel.add(name);

        textPanel.add(new JLabel("Abbreviation"));
        textPanel.add(abbreviation);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel ccPanel = new JPanel();
        ccPanel.setLayout(new BorderLayout());
        ccPanel.add(BorderLayout.CENTER, new RTextScrollPane(codeTemplate));
        tabbedPane.add("Code Template", ccPanel);

        JPanel descPanel = new JPanel();
        descPanel.setLayout(new BorderLayout());
        descPanel.add(BorderLayout.CENTER, new JScrollPane(description));
        tabbedPane.add("Description", descPanel);

        panel.add(textPanel, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private void fixColumnSize() {
        TableColumn column = null;
        column = tableView.getColumnModel().getColumn(ccl.NAME);
        column.setPreferredWidth(100);

        column = tableView.getColumnModel().getColumn(ccl.ABBREVIATION);
        column.setPreferredWidth(100);

        column = tableView.getColumnModel().getColumn(ccl.DESCRIPTION);
        column.setPreferredWidth(340);

        column = tableView.getColumnModel().getColumn(ccl.CODETEMPLATE);
        column.setPreferredWidth(340);

        tableView.updateUI();
    }

    private JPanel listPanel() {

        ListSelectionModel listSelectionModel;

        JPanel panel = new JPanel();
        //panel.setLayout(new FlowLayout());
        panel.setLayout(new GridLayout(1, 0));
        panel.setBorder(new EtchedBorder());
        ccl = new CodeCompletionList();
        ccl.addBunch(readTemplates());
        tableView = new JTable(ccl);

        tableView.getTableHeader().setReorderingAllowed(false);
        tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableView.setAutoCreateRowSorter(true);

        listSelectionModel = tableView.getSelectionModel();
        listSelectionModel.setValueIsAdjusting(true);

        fixColumnSize();

        tableView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if (lsm.isSelectionEmpty()) {
                        ;
                    } else {
                        int selectedRow = tableView.getSelectedRow();
                        activeRow = selectedRow;
                        CodeCompletionRow ccr = ccl.getRowAt(selectedRow);
                        name.setText(ccr.getName());
                        description.setText(ccr.getDescription());
                        codeTemplate.setText(ccr.getCodeTemplate());
                        abbreviation.setText(ccr.getAbbreviation());
                    }
                }
            }
        });

        // reduce the hieght of thes crollpane to a maximum of 10 rows
        JScrollPane scrollPane = new JScrollPane(tableView);
        Dimension d = tableView.getPreferredSize();
        scrollPane.setPreferredSize(new Dimension(d.width, tableView.getRowHeight() * 10 + 1));

        panel.add(scrollPane);

        return panel;
    }

    private String getUser() {
        try {
            return System.getProperty("user.name");
        } catch (Exception e) {
            return "unknown";
        }
    }

    private ArrayList<CodeCompletionRow> readTemplates() {
        ArrayList<CodeCompletionRow> templates = new ArrayList();
        try {
           
            PreparedStatement stmt = con.prepareStatement("select TemplateName, Abbreviation, CodeTemplate, Description from CodeCompletion where ToolUser = ?");
            stmt.setString(1, getUser());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                CodeCompletionRow ccr = new CodeCompletionRow();
                ccr.setName(rs.getString("TemplateName"));
                ccr.setAbbreviation(rs.getString("Abbreviation"));
                ccr.setCodeTemplate(rs.getString("CodeTemplate"));
                ccr.setDescription(rs.getString("Description"));

                templates.add(ccr);
            }
            rs.close();
            stmt.close();

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while fetching the CodeCompletion DB {0}", sqlex.getMessage());
        }

        return templates;
    }

    private JPanel buttonPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(new EtchedBorder());

        ImageButton clear = new ImageButton(null, "clear.gif", "clear fields");
        ImageButton save = new ImageButton(null, "save.gif", "Save this template");
        ImageButton delete = new ImageButton(null, "delete.gif", "remove this template");

        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRow();
            }
        });

        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRow(name.getText());
            }
        });

        cancel = new ImageButton(null, "exit.gif", null);
        cancel.setToolTipText("Quit");

        /*
        JLabel usedas = new JLabel("used as " + getUser());        
        panel.add(usedas);
        panel.add(new JLabel("  "));
        */
        
        panel.add(clear);
        panel.add(save);
        panel.add(delete);
        panel.add(cancel);

        return panel;
    }

    private boolean checkEmpty() {
        boolean empty = false;
        if ((name.getText().length() == 0)
                || (abbreviation.getText().length() == 0)
                || (codeTemplate.getText().length() == 0)) {
            empty = true;
        }
        return empty;
    }

    private boolean doesThisNameExist(String v) {
        boolean exists = false;

        String q = "select TemplateName from CodeCompletion where TemplateName = ? and ToolUser = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(q);
            stmt.setString(1, v);
            stmt.setString(2, getUser());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                exists = true;
                break;
            }
            rs.close();
            stmt.close();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while checking the CodeCompletion DB {0}", sqlex.getMessage());
        }
        return exists;
    }

    private boolean abbreviationExist(String v) {
        boolean exists = false;

        String q = "select Abbreviation from CodeCompletion where Abbreviation = ? and ToolUser = ?";
        try {
            PreparedStatement stmt = con.prepareStatement(q);
            stmt.setString(1, v);
            stmt.setString(2, getUser());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                exists = true;
                break;
            }
            rs.close();
            stmt.close();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while checking the CodeCompletion DB {0}", sqlex.getMessage());
        }
        return exists;
    }

    private void addRow() {
        /*
         insert = con.prepareStatement("insert into CodeCompletion ( ToolUser, TemplateName, Abbreviation, CodeTemplate, Description) values (?,?,?,?,?)");
          update = con.prepareStatement("update CodeCompletion set  Abbreviation = ?, CodeTemplate = ?, Description = ? where TemplateName = ? and ToolUser = ?");
         */
        if (checkEmpty()) {
            JOptionPane.showMessageDialog(null, "I need a Name and an Abbreviation and a CodeTemplate to continue");

        } else {

            try {
                if (!doesThisNameExist(name.getText())) {
                    insert.setString(1, getUser());
                    insert.setString(2, name.getText());
                    insert.setString(3, abbreviation.getText());
                    insert.setString(4, codeTemplate.getText());
                    insert.setString(5, description.getText());
                    insert.executeUpdate();
                    CodeCompletionRow ccr = new CodeCompletionRow();
                    ccr.setName(name.getText());
                    ccr.setAbbreviation(abbreviation.getText());
                    ccr.setCodeTemplate(codeTemplate.getText());
                    ccr.setDescription(description.getText());
                    ccl.addTableRowObject(ccr);
                    fixColumnSize();

                } else {
                    update.setString(1, abbreviation.getText());
                    update.setString(2, codeTemplate.getText());
                    update.setString(3, description.getText());
                    update.setString(4, name.getText());
                    update.setString(5, getUser());
                    update.executeUpdate();
                    provider.removeCompletion(new ShorthandCompletion(provider, abbreviation.getText(), codeTemplate.getText()));
                    if (activeRow >= 0) {
                        ccl.setValueAt((String) name.getText(), activeRow, ccl.NAME);
                        ccl.setValueAt((String) abbreviation.getText(), activeRow, ccl.ABBREVIATION);
                        ccl.setValueAt((String) codeTemplate.getText(), activeRow, ccl.CODETEMPLATE);
                        ccl.setValueAt((String) description.getText(), activeRow, ccl.DESCRIPTION);
                        fixColumnSize();
                    }
                }
                provider.addCompletion(new ShorthandCompletion(provider, abbreviation.getText(), codeTemplate.getText()));
            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "Exception while saving the CodeCompletion DB for User {0} Exception: {1}", new Object[]{getUser(), sqlex.getMessage()});
                sqlex.printStackTrace();
            }
        }
    }

    private boolean reallyDoIt(String v) {
        boolean doIt = false;
        String string1 = "Yes";
        String string2 = "No";
        Object[] options = {string1, string2};
        int n = JOptionPane.showOptionDialog(null,
                v,
                null,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, //don't use a custom Icon
                options, //the titles of buttons
                string2); //the title of the default button
        if (n == JOptionPane.YES_OPTION) {
            doIt = true;
        }
        return doIt;
    }

    private void deleteRow(String v) {

        if (checkEmpty()) {
            JOptionPane.showMessageDialog(null, "don't know what to delete");
        } else {
            if (reallyDoIt("Delete this entry?")) {
                String q = "delete from CodeCompletion where TemplateName = ? and ToolUser = ?";
                try {
                    PreparedStatement stmt = con.prepareStatement(q);
                    stmt.setString(1, v);
                    stmt.setString(2, getUser());
                    stmt.executeUpdate();
                    stmt.close();

                    provider.removeCompletion(new ShorthandCompletion(provider, abbreviation.getText(), codeTemplate.getText()));

                    if (activeRow >= 0) {
                        try {
                            ccl.removeRow(activeRow);
                            activeRow = -1;
                        } catch (Exception e) {

                        }
                    }
                    fixColumnSize();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while deleting from the CodeCompletion DB {0}", sqlex.getMessage());                    
                }
                clear();
            }
        }
    }

    private void clear() {
        name.setText("");
        abbreviation.setText("");
        codeTemplate.setText("");
        description.setText("");

    }

    private DerbyInfoServer derby;

    private void initDB() {

        /*
         get the derby-thinsgs without starting up the server
         */
        derby = DerbyInfoServer.getInstance();

        try {
            Class.forName(derby.getJDBCDriver()).newInstance();
        } catch (ClassNotFoundException cnfe) {

        } catch (Exception e) {

        }

        Statement stmt = null;
        ResultSet metaDataRs = null;
        String[] types = {"TABLE", "VIEW", "SYSTEM TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS", "SYNONYM"};

        try {
            con = DriverManager.getConnection(derby.getJDBCUrl());

            // we check if the latest version
            DatabaseMetaData dmd = con.getMetaData();
            metaDataRs = dmd.getTables(derby.getDbName(), "APP", "CODECOMPLETION", types);
            if (metaDataRs.next()) {
                metaDataRs.close();

                // check the latest version must have the column TOOLUSER VARCHAR(128)
                metaDataRs = dmd.getColumns(derby.getDbName(), "APP", "CODECOMPLETION", "TOOLUSER");
                if (!metaDataRs.next()) {
                    stmt = con.createStatement();
                    stmt.executeUpdate("ALTER TABLE APP.CODECOMPLETION ADD TOOLUSER VARCHAR(128)");
                    PreparedStatement ps = con.prepareStatement("UPDATE APP.CODECOMPLETION SET TOOLUSER = ?");
                    ps.setString(1, getUser());
                    ps.executeUpdate();
                    ps.close();
                    logger.log(Level.INFO, "Derby Table APP.CODECOMPLETION actualized");
                }
                
                metaDataRs.close();
            } else {
                stmt = con.createStatement();
                stmt.executeUpdate("create table APP.CodeCompletion (\n"
                        + " ToolUser VARCHAR(128) not null,\n"
                        + "	TemplateName VARCHAR(25) NOT NULL ,\n"
                        + "	Abbreviation VARCHAR(25) ,\n"
                        + "	CodeTemplate VARCHAR(4096) ,\n"
                        + "	Description VARCHAR(4096)  ,\n"
                        + "	PRIMARY KEY (ToolUser,TemplateName)\n"
                        + ")");
                stmt.executeUpdate("create unique index ccIdx on APP.CodeCompletion (Abbreviation, ToolUser)");
            }
        } catch (SQLException sqlex) {
            if (sqlex.getErrorCode() != 30000) {
                logger.log(Level.WARNING, "Exception while creating the CodeCompletion DB {0}", sqlex.getMessage());
                logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception while closing the CREATE TABLE Statement {0}", e.getMessage());
                }
            }
            if (metaDataRs != null) {
                try {
                    metaDataRs.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception while closing the MetaDataReader {0}", e.getMessage());
                }
            }
        }

        try {

            insert = con.prepareStatement("insert into CodeCompletion ( ToolUser, TemplateName, Abbreviation, CodeTemplate, Description) values (?,?,?,?,?)");
            update = con.prepareStatement("update CodeCompletion set  Abbreviation = ?, CodeTemplate = ?, Description = ? where TemplateName = ? and ToolUser = ?");

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while initialising the CodeCompletion DB {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
            logger.log(Level.INFO, "Mostly this is because derby is not up and running. So start the derby-server in advance by launching sql.fredy.tools.DerbyInfoServer");

        }

    }

    public void close() {
        try {
            con.close();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while initialising the CodeCompletion DB {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
            logger.log(Level.INFO, "Mostly this is because derby is not up and running. So start the derby-server in advance by launching sql.fredy.tools.DerbyInfoServer");

        }

        try {
            con.close();

            // we do not need to shut down the derby db, because we are using a DB server startet in advance
            //DriverManager.getConnection("jdbc:derby:" + dbName + ";shutdown=true");
            //DriverManager.getConnection("jdbc:derby:;shutdown=true");
        } catch (SQLException sqlex) {

            //   use this if you only shutdown the actual DB
            // this error will not appear...
            if ((sqlex.getErrorCode() == 45000) && ("08006".equals(sqlex.getSQLState()))) {

                // if ((sqlex.getErrorCode() == 50000) && ("XJ015".equals(sqlex.getSQLState()))) {
                //logger.log(Level.INFO, "DB closed");
            } else {
                logger.log(Level.WARNING, "Closing connection to SQLAdmin Work DerbyDB {0}", sqlex.getMessage());
                logger.log(Level.INFO, "SQLState: {0}", sqlex.getSQLState());
                logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
            }
        }
    }

    public static void main(String a[]) {
        JFrame f = new JFrame();
        final CodeCompletionForm ccf = new CodeCompletionForm();
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(BorderLayout.CENTER, ccf);

        f.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                ccf.close();
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

        ccf.cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ccf.close();
                System.exit(0);
            }
        });

        f.pack();
        f.setVisible(true);

    }

    /**
     * @return the provider
     */
    public CompletionProvider getProvider() {
        return provider;
    }

    /**
     * @param provider the provider to set
     */
    public void setProvider(DefaultCompletionProvider provider) {
        this.provider = provider;
    }

    public void loadCompletion() {
        logger.log(LOGLEVEL, "Loading codecompletion...");
        ArrayList<CodeCompletionRow> completions = new ArrayList();
        completions = readTemplates();
        for (int i = 0; i < completions.size(); i++) {
            CodeCompletionRow ccr = completions.get(i);
            provider.addCompletion(new ShorthandCompletion(provider, ccr.getAbbreviation(), ccr.getCodeTemplate()));
        }
    }

}
