package sql.fredy.test;

import java.io.*;
import java.net.URL;
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


public class JdbcTest extends JFrame {


    private JTextField jdbcDriver;
    private JTextField jdbcUrl;
    private JTextField user;
    private JTextField passwd;
    private JTextArea  mBox, qBox;
    private Connection con;
    private Statement stmt;
    private ResultSet sqlResult;


    public JdbcTest() {

      super("JDBC-Tester");

      this.addWindowListener(new WindowAdapter() {
	  public void windowActivated(WindowEvent e) {}
	  public void windowClosed(WindowEvent e) {}
	  public void windowClosing(WindowEvent e) {System.exit(0);}
	  public void windowDeactivated(WindowEvent e) {}
	  public void windowDeiconified(WindowEvent e) {}
	  public void windowIconified(WindowEvent e) {}
	  public void windowOpened(WindowEvent e) {}});
      



      this.getContentPane().setLayout(new BorderLayout());


      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(0,2));
      panel.add(new JLabel("JDBC-Driver"));
      jdbcDriver = new JTextField(15);
      jdbcDriver.setText("org.gjt.mm.mysql.Driver");
      panel.add(jdbcDriver);
      panel.add(new JLabel("URL"));
      jdbcUrl = new JTextField(15);
      jdbcUrl.setText("jdbc:mysql://192.168.0.10:3306/test");
      panel.add(jdbcUrl);

      panel.add(new JLabel("User"));
      user = new JTextField(15);
      panel.add(user);

      panel.add(new JLabel("Password"));
      passwd = new JTextField(15);
      panel.add(passwd);





      panel.setBorder(BorderFactory.createEtchedBorder());


      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new GridLayout(1,0));
      buttonPanel.setBorder(BorderFactory.createEtchedBorder());
      JButton connect = new JButton("Connect");
      JButton quit    = new JButton("Quit");
      JButton query   = new JButton("send SQL-Query");

      connect.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	      test();
	      }});
      quit.addActionListener(new ActionListener() {
	  public void actionPerformed(ActionEvent e) {
	      System.exit(0);
	      }});
      query.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
		  execQuery();
		  }});
      
      


      buttonPanel.add(connect);
      buttonPanel.add(query);
      buttonPanel.add(quit);


      qBox = new JTextArea(10,15);
      qBox.setEditable(true);
      qBox.setToolTipText("Type query in here");

      mBox = new JTextArea(10,15);
      mBox.setEditable(false);
      mBox.setToolTipText("OutputArea");
      mBox.setFont(new Font("Monospaced", Font.PLAIN, 12));

      JScrollPane scrollpane = new JScrollPane(qBox);
      JScrollPane scrollpane2 = new JScrollPane(mBox);	

      JPanel mqPanel = new JPanel();
      mqPanel.setLayout(new GridLayout(0,1));
      mqPanel.add(scrollpane);
      mqPanel.add(scrollpane2);


      this.getContentPane().add(BorderLayout.WEST,panel);
      this.getContentPane().add(BorderLayout.CENTER,buttonPanel);
      this.getContentPane().add(BorderLayout.SOUTH,mqPanel);
      this.pack();
      this.setVisible(true);

    }
    private void test() {
	try {
	    Class.forName(jdbcDriver.getText());
            con = DriverManager.getConnection(jdbcUrl.getText(),user.getText(),passwd.getText());
	    stmt = con.createStatement();
            mBox.append("\nConnection established");	    
	} catch (Exception e) { 
	    mBox.append("\n" + e.getMessage());
	    e.printStackTrace();}


    }


    private void execQuery() {
	test();
	String sqlQuery = qBox.getText().toLowerCase();
	if ( sqlQuery.startsWith("select") || sqlQuery.startsWith("show") ) {
	    SqlText sqt = new SqlText(con,stmt, qBox.getText());
	    mBox.setText(sqt.TextArea.getText());

	} else {
	    try {
		int records = stmt.executeUpdate(qBox.getText());
		mBox.append("\n" + records + " rows affected");
	    } catch (SQLException sqe) { 
		mBox.append("\nERROR!!!\n" + sqe.getMessage().toString());
	    }
	}


    
    }


    public static void main(String args[]) {
	JdbcTest j = new JdbcTest();

    }
}
