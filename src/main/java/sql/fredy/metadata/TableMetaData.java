package sql.fredy.metadata;

/**
 *
 * TableMetaData is a part of Admin
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
 *
 *
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
 *
 * Added Functions to the Mouseclick:
 *
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.util.Vector;

import sql.fredy.ui.ImageButton;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class TableMetaData extends JPanel {

    // Fredy's make Version
    private static String fredysVersion = "Version 2.0   25. Dec 2020, uses DataSource";

    public String getVersion() {
        return fredysVersion;
    }

    private JTable info;
    public JButton cancel;
    private TableInfo ti;

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

    String table;

    /**
     * Get the value of table.
     *
     * @return Value of table.
     */
    public String getTable() {
        //return spaceFixer(table);
        return table;
    }

    private String spaceFixer(String s) {
        s = s.trim();
        if (s.indexOf(' ') > 0) {
            s = "'" + s + "'";
        }
        return s;
    }

    /**
     * Set the value of table.
     *
     * @param v Value to assign to table.
     */
    public void setTable(String v) {
        this.table = v;
    }

    private Vector name, type, size, isNull, remarks, defaults;
    private DatabaseMetaData dmd;

    public TableMetaData(String schema,String table) {
        
        setSchema(schema);
        setTable(table);
        inits();

    }

    public TableMetaData() {
        inits();
    }

    public TableMetaData(
            String database,
            String schema,
            String table) {
     
        setDatabase(database);
        setSchema(schema);
        setTable(table);
        inits();

    }

    public TableMetaData(String table) {
        setSchema("%");
        setTable(table);
        inits();
    }

    private void inits() {
        name = new Vector();
        type = new Vector();
        size = new Vector();
        isNull = new Vector();
        remarks = new Vector();
        defaults = new Vector();
        this.setLayout(new BorderLayout());
        this.add("North", descr());
        this.add("Center", listPanel());
        this.add("South", buttonPanel());
    }

    private void tableInit() {

        ti = new TableInfo();
        info = new JTable(ti);

        DbInfo dbi = null;     
        dbi = new DbInfo();
     

        Vector columns = new Vector();
        columns = dbi.getColumnNames(getTable());
        Vector pk = new Vector();
        pk = dbi.getPk(getTable());

        for (int i = 0; i < columns.size(); i++) {
            
            SingleColumnInfo sci = new SingleColumnInfo(getDatabase(),getSchema(),getTable(),(String) columns.elementAt(i));

            Vector v = new Vector();
            v.addElement((String) sci.getColumn_name());
            v.addElement((String) sci.getType_name());
            v.addElement((Integer) sci.getColumn_size());
            v.addElement((Boolean) sci.isNullable());

            String r = (sci.getRemarks());
            if (r == null) {
                r = " ";
            }
            v.addElement((String) r);

            String d = sci.getColumn_def();
            if (d == null) {
                d = " "; //This is a change for postgres
            }
            v.addElement(d);

            // is this a primary key?
            if (pk.size() < 1) {
                v.addElement(" ");
            } else {
                for (int j = 0; j < pk.size(); j++) {
                    try {
                        if (sci.getColumn_name().equals(((PrimaryKey) pk.elementAt(j)).getColumnName())) {
                            v.addElement("Primary");
                        } else {
                            v.addElement(" ");
                        }
                    } catch (Exception e) {
                        v.addElement(" ");
                    }
                }
            }

            // exported Foreign Keys
            v.addElement((String) sci.getExportedKeys());

            // imported Foreign Keys
            v.addElement((String) sci.getImportedKeys());

            ti.addRow(v);
        }

    }

    //private JSplitPane listPanel() {
    private JPanel listPanel() {

        tableInit();
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);

        gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        info.getTableHeader().setReorderingAllowed(false);
        info.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        info.setPreferredScrollableViewportSize(new Dimension(500, 160));
        info.setAutoCreateRowSorter(true);

        /*
         Fill the Clipboard with Information on Mouseclick:
         1 Mouseclick add s the Tablename to the Clipboard
         2 Clicks add tabelname.rowname to the Clipboard
         */
        MouseListener mouseListener = new MouseAdapter() {

            // left mouse button
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == e.BUTTON1) {
                    if (e.getClickCount() == 2) {
                        StringSelection selection = new StringSelection(" " + getTable() + "." + (String) info.getValueAt(info.getSelectedRow(), 0) + " ");

                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                    if (e.getClickCount() == 1) {
                        StringSelection selection = new StringSelection(" " + getTable() + " ");

                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }

                }
                // how many buttons does the mouse have?
                int rightButton = e.BUTTON2;
                if (MouseInfo.getNumberOfButtons() == 2) {
                    rightButton = e.BUTTON2;
                }

                // if clicked once  the right , it adds a SQL-Statement to see this table to the clipboard
                if ((MouseInfo.getNumberOfButtons() == 2) && (e.getButton() == rightButton)) {

                    if (e.getClickCount() == 1) {

                        StringBuilder sb = new StringBuilder();
                        sb.append("select \r\n");
                        for (int i = 0; i < info.getRowCount(); i++) {
                            sb.append("\t").append(info.getValueAt(i, 0));
                            if (i < info.getRowCount() - 1) {
                                sb.append(", \r\n");
                            } else {
                                sb.append("\r\n");
                            }
                        }
                        sb.append("from ").append(getTable()).append("\r\n");

                        StringSelection selection = new StringSelection(sb.toString());

                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                }

                // i just clicked twice button2, add something stupid
                if (e.getClickCount() == 2) {
                    StringSelection selection = new StringSelection(" where " + getTable() + "." + (String) info.getValueAt(info.getSelectedRow(), 0) + " like ");

                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, selection);
                }

            }
        };
        info.addMouseListener(mouseListener);

        panel.add(new JScrollPane(info), gbc);

        // and now, we add the SQL-Create-Statement for this table to the Panel
        /*
         JPanel panel2 = new JPanel();
         panel2.setLayout(new BorderLayout());
         JTextArea sqlCreate = new JTextArea(getCreateStatement(), 5, 30);
         sqlCreate.setToolTipText("This is the Create Table SQL-Statement");
         panel2.add(BorderLayout.CENTER, new JScrollPane(sqlCreate));

         JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, panel, panel2);
         splitPanel.setOneTouchExpandable(true);
         splitPanel.setDividerLocation(1.00);

         */
        //return splitPanel;
        return panel;
    }

    public String getInsertPreparedStatement() {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();
        
        sb.append("insert into ").append(getTable()).append("( \r\n");        
        for (int i = 0; i < ti.getRowCount(); i++) {
            sb.append(" ").append((String) ti.getValueAt(i, 0));  // Attribute Name        
            
           if (i < ti.getRowCount() - 1) {
                sb.append(" ,\r\n");
            } else {
                sb.append("\r\n");
            }

            if (i > 0) {
                sb1.append(",");
            }
            sb1.append("?");
        }
        sb.append(") values \r\n( ").append(sb1.toString()).append(" )");
        
        return sb.toString();
    }
    
    public String getJavaBeanCode() {
        StringBuilder sb = new StringBuilder();   
        
        
        return sb.toString();
    }
    
    public String getUpdatePreparedStatement() {
        StringBuilder sb = new StringBuilder();      
        
        sb.append("update ").append(getTable()).append(" set \r\n");        
        for (int i = 0; i < ti.getRowCount(); i++) {
            sb.append(" ").append((String) ti.getValueAt(i, 0)).append(" = ? ");  // Attribute Name                    
            if (i < ti.getRowCount()-1)  {
                sb.append(",\r\n");                
            }           
        }        
        sb.append(getPrimaryKey());
        
        return sb.toString();
        
    }
    
    private String getPrimaryKey() {
        StringBuilder sb = new StringBuilder() ;
        boolean found = false;
        for (int i = 0; i < ti.getRowCount(); i++) {
            if ( ((String)ti.getValueAt(i, 6)).equals("Primary")) {
                
                if (found) {
                    sb.append("\r\nand ");
                } else {
                   sb.append("\r\nwhere "); 
                   found = true;
                }
                sb.append( (String) ti.getValueAt(i, 0) ).append(" = ? ");
            }
        }
        
        if (!found) sb.append("where -- please add here your condition to identify this row exactly --");                
        return sb.toString();
    }
    
    
    public String getCreateStatement() {
        StringBuilder sb = new StringBuilder();
        String fieldType = "";
        sb.append("CREATE TABLE ").append(getTable()).append("( \r\n");
        for (int i = 0; i < ti.getRowCount(); i++) {
            sb.append("   ").append((String) ti.getValueAt(i, 0)).append(" ");  // Attribute Name        

            // Attribute Type and MS SQL Server AUTO_Increment speciality
            fieldType = (String) ti.getValueAt(i, 1);
            if (fieldType.equals("int identity")) {
                sb.append("INTEGER");
            } else {
                sb.append(fieldType);  // Attribute Type
            }

            boolean useLength = true;
            if ( fieldType.toLowerCase().equals("text"))     useLength = false;
            if ( fieldType.toLowerCase().equals("ntext"))    useLength = false;
            if ( fieldType.toLowerCase().equals("int"))      useLength = false;
            if ( fieldType.toLowerCase().equals("tinyint"))  useLength = false;
            if ( fieldType.toLowerCase().equals("bigint"))   useLength = false;
            if ( fieldType.toLowerCase().equals("integer"))  useLength = false;
            if ( fieldType.toLowerCase().equals("datetime")) useLength = false;
            if ( fieldType.toLowerCase().equals("date"))     useLength = false;
            if ( fieldType.toLowerCase().equals("time"))     useLength = false;
            if ( fieldType.toLowerCase().startsWith("int"))  useLength = false;
            if ( fieldType.toLowerCase().endsWith("int"))    useLength = false;
            if (((int) ti.getValueAt(i, 2)) <= 1)            useLength = false;
            
            if ( useLength ) {                
                sb.append("(").append(Integer.valueOf((int) ti.getValueAt(i, 2))).append(") "); // Size
            } else {
                sb.append(" ");
            }
            if (!(boolean) ti.getValueAt(i, 3)) {
                sb.append("NOT NULL");  // null not allowed
            }

            // if MS SQL Server Auto_increment
            if (fieldType.equals("int identity")) {
                sb.append(" IDENTITY(1,1)");
            }
            if (i < (ti.getRowCount() -1) )  {
                sb.append(" , \r\n");
            } else {
                sb.append(" ");
            }
        }

        // are there some Primary Keys to append?
        boolean pk = false;
        int pkCount = 0;
        for (int i = 0; i < ti.getRowCount(); i++) {
            if (((String) ti.getValueAt(i, 6)).equals("Primary")) {
                if (!pk) {
                    sb.append(",\r\n  PRIMARY KEY (");
                    pk = true;
                }
                if (pkCount > 0) {
                    sb.append(", ");
                }
                sb.append(ti.getValueAt(i, 0));
                pkCount++;
            }
        }
        if (pk) {
            sb.append(")");
        }

        // are there some FOREIGN KEY Constraints to create
        for (int i = 0; i < ti.getRowCount(); i++) {
            if (((String) ti.getValueAt(i, 7)).length() > 1) {
                String[] fKeys = ((String) ti.getValueAt(i, 7)).split(",");
                for (int j = 0; j < fKeys.length; j++) {

                    String t = fKeys[j].trim();
                    String[] fKeys2 = t.split("\\.");

                    sb.append(", \r\n  CONSTRAINT FK_").append(getTable().toUpperCase()).append("_").append(fKeys2[0].trim().toUpperCase()).append(" ");
                    sb.append("FOREIGN KEY (").append((String) ti.getValueAt(i, 0)).append(") REFERENCES ");

                    if (fKeys2.length > 0) {
                        sb.append(fKeys2[0].trim()).append("(").append(fKeys2[1].trim()).append(")");
                    }
                }
            }
        }

        sb.append("\r\n)\r\n");
        return sb.toString();
    }

    private JPanel buttonPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        // this adds the SQL-Statement to create this Table to the clipboard
        ImageButton createStatement = new ImageButton("SQL Stmt", "newsheet.gif",  "adds the SQL-Statement to create this table to the clipboard");
        createStatement.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String s = getCreateStatement();
                    if (s.length() > 0) {
                        StringSelection selection = new StringSelection(s);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                } catch (Exception ec) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        panel.add(createStatement);
        
        ImageButton createStmt = new ImageButton("insert Stmt", "altertable.gif",  "adds the SQL-code for a prepared statement to insert a row into this table to the clipboard");
        ImageButton updateStmt = new ImageButton("update Stmt", "updatecolumn.gif",  "adds the SQL-code for a prepared statement to update a row of this table to the clipboard");
        ImageButton beanCode   = new ImageButton("Java Bean","datastore.gif","adds the codefragment for a JavaBean to the clipboard");
        createStmt.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                 try {
                    String s = getInsertPreparedStatement();
                    if (s.length() > 0) {
                        StringSelection selection = new StringSelection(s);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                } catch (Exception ec) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        updateStmt.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                try {
                    String s = getUpdatePreparedStatement();
                    if (s.length() > 0) {
                        StringSelection selection = new StringSelection(s);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                } catch (Exception ec) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        
        beanCode.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werden soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                try {
                    String s = getJavaBeanCode();
                    if (s.length() > 0) {
                        StringSelection selection = new StringSelection(s);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(selection, selection);
                    }
                } catch (Exception ec) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        
        panel.add(createStmt);
        panel.add(updateStmt);
        //panel.add(beanCode);
        cancel = new JButton("Cancel");
        sql.fredy.ui.LoadImage loadImage = new sql.fredy.ui.LoadImage();
        cancel.setIcon(loadImage.getImage("exit.gif"));
        
        
        
        panel.add(cancel);
        
        return panel;
    }

    private JPanel descr() {

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
  

        JLabel l3 = new JLabel("Database:");
        JTextField t3 = new JTextField(getDatabase());
        t3.setEditable(false);
        t3.setBackground(Color.black);
        t3.setForeground(Color.white);

        JLabel l4 = new JLabel("Table:");
        JTextField t4 = new JTextField(getTable());
        t4.setEditable(false);
        t4.setBackground(Color.black);
        t4.setForeground(Color.white);
     
        panel.add(l3);
        panel.add(t3);
        panel.add(l4);
        panel.add(t4);
                
        
        return panel;

    }

    public static void main(String args[]) {

        if (args.length != 6) {
            System.out.println("Syntax: java TableMetaData database schema table");
            System.exit(0);
        }

        JFrame f = new JFrame("Table Info");
        f.addWindowListener(new WindowAdapter() {
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
        f.getContentPane().setLayout(new BorderLayout());
        TableMetaData tmd = new TableMetaData(args[0], args[1], args[2]);
        f.getContentPane().add("Center", tmd);
        tmd.cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        f.pack();
        f.setVisible(true);
    }
}
