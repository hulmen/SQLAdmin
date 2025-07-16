package sql.fredy.test;

import java.sql.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import sql.fredy.connection.DataSource;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class TestTypes extends JFrame {

    private JTextField host, user, password, database, schema, table, column;
    private JPanel output;
    private JFrame out;
    private Logger logger = Logger.getLogger("sql.fredy.test");

    public TestTypes() {

        super("Test JDBC-Types out of ResultSet");
        this.getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 2));

        host = new JTextField("");
        user = new JTextField("");
        password = new JTextField("");
        database = new JTextField("");
        schema = new JTextField("");
        table = new JTextField("");
        column = new JTextField("");

        panel.add(new JLabel("Host"));
        panel.add(host);
        panel.add(new JLabel("User"));
        panel.add(user);
        panel.add(new JLabel("Password"));
        panel.add(password);
        panel.add(new JLabel("Database"));
        panel.add(database);
        panel.add(new JLabel("Schema"));
        panel.add(schema);
        panel.add(new JLabel("Table"));
        panel.add(table);
        panel.add(new JLabel("Column"));
        panel.add(column);

        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new BevelBorder(BevelBorder.RAISED));

        JButton exec = new JButton("Execute");
        JButton close = new JButton("Close");

        exec.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                test();
            }
        });

        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(exec);
        buttonPanel.add(close);

        out = new JFrame("Result");
        output = new JPanel();
        output.setLayout(new GridLayout(0, 2));
        JScrollPane scrollpane = new JScrollPane(output);
        out.getContentPane().add(scrollpane);
        out.pack();
        out.setVisible(false);

        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        this.addWindowListener(new WindowAdapter() {
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

        this.pack();
        this.setVisible(true);

    }

    public static void main(String args[]) {
        TestTypes t = new TestTypes();
    }

    /**
     * Get the value of con.
     *
     * @return value of con.
     */
    private Connection con = null;

    public Connection getCon() {

        try {
            if ((null == con) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection {0}", ex.getMessage());
        }

        return con;
    }

    private void test() {

        output.removeAll();

        Connection con = null;
        ResultSet cols = null;
        try {
            con = getCon();
            DatabaseMetaData md = con.getMetaData();
            cols = md.getColumns(database.getText(), schema.getText(), table.getText(), column.getText());
            int i = 0;
            while (cols.next()) {
                output.add(new JLabel("Catalog"));
                output.add(new JLabel(cols.getString(1)));
                output.add(new JLabel("Schema"));
                output.add(new JLabel(cols.getString(2)));
                output.add(new JLabel("Table"));
                output.add(new JLabel(cols.getString(3)));
                output.add(new JLabel("Column"));
                output.add(new JLabel(cols.getString(4)));
                output.add(new JLabel("Data Type"));
                output.add(new JLabel(Short.toString(cols.getShort(5))));
                output.add(new JLabel("Type Name"));
                output.add(new JLabel(cols.getString(6)));
                output.add(new JLabel("Columnsize"));
                output.add(new JLabel(Integer.toString(cols.getInt(7))));
                output.add(new JLabel("Decimal Digits"));
                output.add(new JLabel(Integer.toString(cols.getInt(9))));
                output.add(new JLabel("Is nullable"));
                output.add(new JLabel(cols.getString(18)));
            }
        } catch (SQLException excp1) {
            System.out.println("Exception in TableColumns");
            excp1.printStackTrace();
        } finally {
            if (cols != null) {
                try {
                    cols.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception while closing resultset {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception while closing connection {0}", e.getMessage());
                }
            }
        }

        out.pack();
        out.setVisible(true);
    }
}
