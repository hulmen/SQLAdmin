package applications.basics;


/** 
    sqlTable is the Grid you often meet in Admin, it was in fact the
    first stuff I wrote to learn more about meta-data.


    Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

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


    This has been contributed by David Good

**/

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.JFrame;
//import javax.swing.SwingUtilities;
import javax.swing.event.*;

public class SqlPanel extends JPanel implements  ActionListener, ListSelectionListener,SqlTab {

private String host, user, password, query;
public JTable tableView;
public JScrollPane scrollpane;
private int iRows;
private String SQLError;


public SqlPanel (String host, String user, String password,String db, String query) {
  
 this.host = host;
 this.user = user;
 this.password = password;
 this.query = query;
 this.setLayout(new FlowLayout());    

 JDBCAdapter dt = new JDBCAdapter(host, user,password,db);
 dt.executeQuery(query);
 iRows = dt.getNumRows();
 SQLError = dt.getSQLError();
 tableView = new JTable(dt);
 tableView.getSelectionModel().addListSelectionListener(this);
 tableView.getTableHeader().setReorderingAllowed(false);
 tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
 scrollpane = new JScrollPane(tableView);
 // scrollpane.getViewport().add(tableView);

  this.add(scrollpane);

  // this.pack();
  //this.show();
 
}
 
	public int getNumRows()
	{
		return iRows;
	}
	
	public String getQuery()
	{
		return query;
	}
	
	public String getSQLError()
	{
		return SQLError;
	}
	
	public void setViewPortSize(Dimension d)
	{
		tableView.setPreferredScrollableViewportSize(d);
	}
	
  //Handling List Events
  public void valueChanged(ListSelectionEvent e) {;}

  // handling AWT-Events
  public void actionPerformed(ActionEvent evt) {;}

  
  public static  void main(String args[]) {
    if (args.length != 5 ) {
     System.out.println("Syntax: java SqlPanel host user password database query");
     }
  else {
    SqlPanel f = new SqlPanel(args[0], args[1], args[2], args[3], args[4]);
    JFrame frame = new JFrame("Table");
    frame.getContentPane().setLayout(new BorderLayout());
    frame.getContentPane().add("Center",f);
    frame.addWindowListener(new WindowAdapter() {
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
        public void windowClosing(WindowEvent e) {System.exit(0);}
        public void windowDeactivated(WindowEvent e) {}
        public void windowDeiconified(WindowEvent e) {}
        public void windowIconified(WindowEvent e) {}
        public void windowOpened(WindowEvent e) {}});
    frame.setVisible(true);
   }
    
  }
  
}
