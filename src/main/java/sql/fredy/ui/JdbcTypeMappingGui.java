/*
 *  This GUI maintains the JDBC Type Mapping for a particular Database
 *  It reads the inital values from  all the JDBC Types
 *  and overwrites it with the Product description file in ./resources/config/{DB}.props
 *  These values are going into the internal Derby Database jdbcmapping
 *
 * The MIT License
 *
 * Copyright 2025 fredy.
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
 /*
select count(*) from SYS.SYSTABLES  WHERE TABLENAME = 'JDBCMAPPING' AND TABLETYPE = 'T'
;
create table APP.JDBCMAPPING (
ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1),
PRODUCTNAME     varchar(128),
JDBCTYPENAME    varchar(128),
PRODUCTTYPENAME varchar(128),
HASLENGTH       boolean,
HASPRECISION    boolean,
MAXLENGTH       int,
MAXLENGTHTEXT   varchar(32)
)

 */
package sql.fredy.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import sql.fredy.infodb.DBconnectionPool;

/**
 *
 * @author fredy
 */
public class JdbcTypeMappingGui extends JPanel {

    private JTextField databaseproduct;
    private String product = "";
    private Logger logger = Logger.getLogger("sql.fredy.ui");

    public JdbcTypeMappingGui() {
        init();
    }

    public JdbcTypeMappingGui(String database) {
        setProduct(database);
        init();
    }

    private void init() {
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.NORTH, database());
        this.add(BorderLayout.CENTER, new JScrollPane(fieldspanel()));
        this.add(BorderLayout.SOUTH, new JScrollPane(listpanel()));
    }

    private JPanel database() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(new JLabel("Databaseproduct"));

        databaseproduct = new JTextField(40);
        databaseproduct.setText(getProduct());
        databaseproduct.setEditable(false);
        panel.add(databaseproduct);

        panel.setBorder(new EtchedBorder());

        return panel;
    }

    private JComboBox jdbcType;
    private JTextField productType;
    private JCheckBox hasLength;
    private JCheckBox hasPrecision;
    private JTextField maxLength;
    private JTextField maxLengthText;
    private int currentId;

    private JPanel fieldspanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        String[] jdbcTypes = {
            "ARRAY",
            "BIGINT",
            "BINARY",
            "BIT",
            "BLOB",
            "BOOLEAN",
            "CHAR",
            "CLOB",
            "DATALINK",
            "DATE",
            "DECIMAL",
            "DISTINCT",
            "DOUBLE",
            "FLOAT",
            "INTEGER",
            "JAVA_OBJECT",
            "LONGNVARCHAR",
            "LONGVARBINARY",
            "LONGVARCHAR",
            "NCHAR",
            "NCLOB",
            "NULL",
            "NUMERIC",
            "NVARCHAR",
            "OTHER",
            "REAL",
            "REF",
            "REF_CURSOR",
            "ROWID",
            "SMALLINT",
            "SQLXML",
            "STRUCT",
            "TIME",
            "TIMESTAMP",
            "TIMESTAMP WITH TIMEZONE",
            "TIME WITH TIMEZONME",
            "TINYINT",
            "VARBINARY",
            "VARCHAR"
        };

        jdbcType = new JComboBox(jdbcTypes);
        //jdbcType.setEditable(false);
        productType = new JTextField(20);
        hasLength = new JCheckBox();
        hasPrecision = new JCheckBox();
        maxLength = new JTextField(10);
        maxLengthText = new JTextField(10);

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;

        panel.add(new JLabel("JDBC Type"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(jdbcType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Product Type"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(productType, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("has length"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(hasLength, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("has precision"), gbc);
        gbc.gridx = 1;
        panel.add(hasPrecision, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("max length"), gbc);
        gbc.gridx = 1;
        panel.add(maxLength, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("max length text"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(maxLengthText, gbc);

        panel.setBorder(new TitledBorder("Types"));

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new BorderLayout());
        innerPanel.add(panel, BorderLayout.CENTER);

        // the ButtonPanel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBorder(new EtchedBorder());

        ImageButton update = new ImageButton(null, "update.gif", "update row");
        ImageButton insert = new ImageButton(null, "row.gif", "insert new row");
        ImageButton delete = new ImageButton(null, "delete.gif", "delete this row");

        buttonPanel.add(update);
        buttonPanel.add(insert);
        buttonPanel.add(delete);

        innerPanel.add(buttonPanel, BorderLayout.SOUTH);

        hasLength.addActionListener((ActionEvent e) -> {
            maxLength.setEditable(hasLength.isSelected());
            maxLength.setEnabled(hasLength.isSelected());
            maxLength.updateUI();

            maxLengthText.setEditable(hasLength.isSelected());
            maxLengthText.setEnabled(hasLength.isSelected());
            maxLengthText.updateUI();

            innerPanel.updateUI();
        });

        update.addActionListener((ActionEvent e) -> {
            updateRow();
        });

        delete.addActionListener((ActionEvent e) -> {
            if (reallyDelete()) {
                deleteRow();
            }
        });
        insert.addActionListener((ActionEvent e) -> {
            insertRow();
        });

        return innerPanel;
    }

    private void updateRow() {

        if (currentId == 0) {
            return;
        }

        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement("""
                                             UPDATE APP.JDBCMAPPING SET
                                             PRODUCTNAME     = ?,
                                             JDBCTYPENAME    = ?,
                                             PRODUCTTYPENAME = ?,
                                             HASLENGTH       = ?,
                                             HASPRECISION    = ?,
                                             MAXLENGTH       = ?,
                                             MAXLENGTHTEXT   = ?
                                             WHERE ID        = ?
                                             """);
            ps.setString(1, databaseproduct.getText());
            ps.setString(2, (String) jdbcType.getSelectedItem());
            ps.setString(3, productType.getText());
            ps.setBoolean(4, hasLength.isSelected());
            ps.setBoolean(5, hasPrecision.isSelected());

            if (hasLength.isSelected()) {
                try {
                    ps.setString(7, maxLengthText.getText());
                    ps.setInt(6, Integer.parseInt(maxLength.getText()));

                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Invalid number for maxLength {0}", maxLength.getText());
                    ps.setNull(6, java.sql.Types.INTEGER);
                }
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
                ps.setNull(7, java.sql.Types.VARCHAR);
            }

            ps.setInt(8, currentId);
            ps.executeUpdate();

            table.setModel(fillTable());
            fixTable();
            table.updateUI();

        } catch (SQLException | IOException | PropertyVetoException e) {
            logger.log(Level.SEVERE, "Exception while updating JDBC Mapping table {0}", e.getMessage());
        } finally {
            if (ps != null) 
              try {
                ps.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing Derby Object");
            }

            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exceptin while closing Derby Object");
            }
        }
    }

    private void insertRow() {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement("""
                                             INSERT INTO APP.JDBCMAPPING (
                                                PRODUCTNAME    ,
                                                JDBCTYPENAME   ,
                                                PRODUCTTYPENAME,
                                                HASLENGTH      ,
                                                HASPRECISION   ,
                                                MAXLENGTH      ,
                                                MAXLENGTHTEXT                                                  
                                             ) values (?,?,?,?,?,?,?)
                                             """);
            ps.setString(1, databaseproduct.getText());
            ps.setString(2, (String)jdbcType.getSelectedItem());
            ps.setString(3, productType.getText());
            ps.setBoolean(4, hasLength.isSelected());
            ps.setBoolean(5, hasPrecision.isSelected());

            if (hasLength.isSelected()) {
                try {
                    ps.setString(7, maxLengthText.getText());
                    ps.setInt(6, Integer.parseInt(maxLength.getText()));

                } catch (NumberFormatException | SQLException e) {
                    logger.log(Level.SEVERE, "Invalid number for maxLength {0}", maxLength.getText());
                    ps.setNull(6, java.sql.Types.INTEGER);
                }
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
                ps.setNull(7, java.sql.Types.VARCHAR);
            }

            ps.executeUpdate();

            table.setModel(fillTable());
            fixTable();
            table.updateUI();

        } catch (SQLException | IOException | PropertyVetoException e) {
            logger.log(Level.SEVERE, "Exception while inserting into JDBC Mapping table {0}", e.getMessage());
        } finally {
            if (ps != null) 
              try {
                ps.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing Derby Object");
            }

            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exceptin while closing Derby Object");
            }
        }
    }

    private boolean reallyDelete() {
        boolean doIt = false;
        String string1 = "Yes";
        String string2 = "No";
        Object[] options = {string1, string2};
        int n = JOptionPane.showOptionDialog(null,
                "Delete Entry?",
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

    private void deleteRow() {

        if (currentId == 0) {
            return;
        }

        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            ps = connection.prepareStatement("""
                                             DELETE FROM APP.JDBCMAPPING WHERE ID = ?
                                             """);
            ps.setInt(1, currentId);
            ps.executeUpdate();

            table.setModel(fillTable());
            fixTable();
            table.updateUI();

        } catch (SQLException | IOException | PropertyVetoException e) {
            logger.log(Level.SEVERE, "Exception while inserting into JDBC Mapping table {0}", e.getMessage());
        } finally {
            if (ps != null) 
              try {
                ps.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing Derby Object");
            }

            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exceptin while closing Derby Object");
            }
        }
    }

    private DefaultTableModel fillTable() {
        DefaultTableModel model = new DefaultTableModel();

        Connection connection = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("select count(*) from SYS.SYSTABLES  WHERE TABLENAME = 'JDBCMAPPING' AND TABLETYPE = 'T'");
            int t = 0;
            if (rs.next()) {
                t = rs.getInt(1);
            }
            if (t == 0) {
                stmt.executeUpdate("""
                                   create table APP.JDBCMAPPING (
                                   ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1),
                                   PRODUCTNAME     varchar(128),
                                   JDBCTYPENAME    varchar(128),
                                   PRODUCTTYPENAME varchar(128),
                                   HASLENGTH       boolean,
                                   HASPRECISION    boolean,
                                   MAXLENGTH       int,
                                   MAXLENGTHTEXT   varchar(32)
                                   )""");
            }

            // now we read the content;
            ps = connection.prepareStatement("""
                                             SELECT
                                             ID,
                                             PRODUCTNAME,
                                             JDBCTYPENAME,
                                             PRODUCTTYPENAME,                                             
                                             HASLENGTH,
                                             HASPRECISION,
                                             MAXLENGTH,
                                             MAXLENGTHTEXT
                                             FROM APP.JDBCMAPPING WHERE LOWER(PRODUCTNAME) = ?
                                             """);
            ps.setString(1, databaseproduct.getText().toLowerCase());
            rs = ps.executeQuery();

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Spaltennamen setzen
            for (int i = 1; i <= columnCount; i++) {
                model.addColumn(metaData.getColumnName(i));
            }
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                model.addRow(rowData);
            }
        } catch (SQLException | IOException | PropertyVetoException e) {
            logger.log(Level.SEVERE, "Exception while checking JDBC Mapping table {0}", e.getMessage());
        } finally {
            if (rs != null) 
              try {
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exceptin while closing Derby Object");
            }
            if (stmt != null) 
              try {
                stmt.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing Derby Object");
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing Derby Object");
            }
        }

        return model;
    }

    JTable table;

    private JPanel listpanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        table = new JTable();

        table.setModel(fillTable());
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setAutoCreateRowSorter(true);

        ListSelectionModel listSelectionModel = table.getSelectionModel();
        listSelectionModel.setValueIsAdjusting(true);

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()) {
                    int selectedRow = table.getSelectedRow();
                    currentId = (int) table.getValueAt(selectedRow, 0);

                    databaseproduct.setText((String) table.getValueAt(selectedRow, 1));
                    jdbcType.setSelectedItem((String) table.getValueAt(selectedRow, 2));
                    productType.setText((String) table.getValueAt(selectedRow, 3));
                    hasLength.setSelected((boolean) table.getValueAt(selectedRow, 4));
                    hasPrecision.setSelected((boolean) table.getValueAt(selectedRow, 5));
                    if (hasLength.isSelected()) {
                        try {
                            maxLength.setText(Integer.toString((int) table.getValueAt(selectedRow, 6)));
                            maxLength.setEnabled(true);
                            maxLengthText.setEnabled(true);
                        } catch (Exception ex) {
                            maxLength.setText("");
                        }
                        maxLengthText.setText((String) table.getValueAt(selectedRow, 7));
                    } else {
                        maxLength.setEnabled(false);
                        maxLengthText.setEnabled(false);
                    }
                }
            }
        });

        fixTable();
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void fixTable() {
        TableColumn column = null;
        column = table.getColumnModel().getColumn(0);
        column.setPreferredWidth(50);

        column = table.getColumnModel().getColumn(1);
        column.setPreferredWidth(100);

        column = table.getColumnModel().getColumn(2);
        column.setPreferredWidth(150);

        column = table.getColumnModel().getColumn(3);
        column.setPreferredWidth(150);

        column = table.getColumnModel().getColumn(4);
        column.setPreferredWidth(50);

        column = table.getColumnModel().getColumn(5);
        column.setPreferredWidth(50);

        column = table.getColumnModel().getColumn(6);
        column.setPreferredWidth(110);

        column = table.getColumnModel().getColumn(7);
        column.setPreferredWidth(100);

        table.updateUI();

    }

    public static void main(String[] args) {
        CloseableFrame cf = new CloseableFrame("JDBC Type Mapping", true);
        cf.getContentPane().setLayout(new BorderLayout());
        cf.getContentPane().add(new JdbcTypeMappingGui(), BorderLayout.CENTER);
        cf.pack();
        cf.setVisible(true);

    }

    /**
     * @return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(String product) {
        this.product = product;
    }

}
