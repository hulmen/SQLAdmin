package applications.basics;

/** 
    DataBaseLogin is a part of Admin and done for loggin in to it...

    Admin is a Tool around mySQL to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

    Admin
    
		       Fredy Fischer
		       Hulmenweg 36
		       8405 Winterthur
		       Switzerland

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


import sql.fredy.share.JdbcStuff;
import sql.fredy.share.SelectDriver;
import sql.fredy.metadata.DBList;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.BorderFactory; 
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.JFrame;

public class DataBaseLogin extends JPanel {

 // Fredy's make Version
private static String fredysVersion = "Version 1.2.1.a  2000-2-27 11:38 Build: 13";

public String getVersion() {return fredysVersion; }   
    String host;
    
    /**
       * Get the value of host.
       * @return Value of host.
       */
    public String getHost() {
	host = lHost.getText();
	return host;
    }
    
    /**
       * Set the value of host.
       * @param v  Value to assign to host.
       */
    public void setHost(String  v) {
	this.host = v;
	lHost.setText(v);
    }
    

    String user;
    
    /**
       * Get the value of user.
       * @return Value of user.
       */
    public String getUser() {
	user = lUser.getText();
	return user;
    }
    
    /**
       * Set the value of user.
       * @param v  Value to assign to user.
       */
    public void setUser(String  v) {
	this.user = v;
	lUser.setText(v);
    }
    


    String password;
    
    /**
       * Get the value of password.
       * @return Value of password.
       */
    public String getPassword() {
	//password = lPassword.getPassword().toString();
	password=String.valueOf(lPassword.getPassword());	
	    //password = lPassword.getText();
	return password;
    }
    
    /**
       * Set the value of password.
       * @param v  Value to assign to password.
       */
    public void setPassword(String  v) {
	this.password = v;
	lPassword.setText(v);
    }
    

    String driver;
    
    /**
       * Get the value of JDBCdriver.
       * @return Value of JDBCdriver.
       */
    public String getDriver() {
	driver = lJDBCDriver.getText();
	return driver;
    }
    
    /**
       * Set the value of JDBCdriver.
       * @param v  Value to assign to JDBCdriver.
       */
    public void setDriver(String  v) {
	this.driver = v;
	lJDBCDriver.setText(v);
    }
    

    String url;
    
    /**
       * Get the value of JDBCurl.
       * @return Value of JDBCurl.
       */
    public String getUrl() {
	url = lUrl.getText();
	return url;
    }
    
    /**
       * Set the value of JDBCurl.
       * @param v  Value to assign to JDBCurl.
       */
    public void setUrl(String  v) {
	this.url = v;
	lUrl.setText(v);
    }
    
    
    String port;
    
    /**
       * Get the value of DatabasePort.
       * @return Value of DatabasePort.
       */
    public String getPort() {
	port = lPort.getText();
	return port;
    }
    
    /**
       * Set the value of DatabasePort.
       * @param v  Value to assign to DatabasePort.
       */
    public void setPort(String  v) {
	this.port = v;
	lPort.setText(v);
    }
    
    
    String database;
    
    /**
       * Get the value of database.
       * @return Value of database.
       */
    public String getDatabase() {
	database = lDatabase.getText();
	return database;
    }
    
    /**
       * Set the value of database.
       * @param v  Value to assign to database.
       */
    public void setDatabase(String  v) {
	this.database = v;
	lDatabase.setText(v);
    }

    
    String schema;
    
    /**
       * Get the value of schema.
       * @return Value of schema.
       */
    public String getSchema() {
	this.schema = lSchema.getText();
	return schema;
    }
    
    /**
       * Set the value of schema.
       * @param v  Value to assign to schema.
       */
    public void setSchema(String  v) {
	this.schema = v;
	lSchema.setText(v);
    }
    


    String usePassword;
    
    /**
       * Get the value of usePassword.
       * @return Value of usePassword.
       */
    public String getUsePassword() {
	usePassword = "no";
	if ( lUsePassword.isSelected() ) usePassword = "yes";
	return usePassword;
    }
    
    /**
       * Set the value of usePassword.
       * @param v  Value to assign to usePassword.
       */
    public void setUsePassword(String  v) {
	this.usePassword = v.toLowerCase();
	if ( v.equalsIgnoreCase("yes") ) {
	    lUsePassword.setSelected(true); 
	} else {  lUsePassword.setSelected(false); }    
    }



    public JButton cancel, connect;

    private JTextField lHost, lUser, lJDBCDriver, lUrl, lPort,lDatabase, lSchema;
    public JPasswordField lPassword;
    //public JTextField lPassword;
    public JCheckBox lUsePassword;

    /** 
     * propFile is the File containing all the Properties
     **/
    public DataBaseLogin(String db) {

	this.setLayout(new BorderLayout());


	FfAlignerLayoutPanel ffp = new FfAlignerLayoutPanel();
	ffp.setBorder(BorderFactory.createEtchedBorder());

	lHost = new JTextField(20);
	lUser = new JTextField(20);
	lDatabase = new JTextField(15);
	lJDBCDriver = new JTextField(20);
	lUrl = new JTextField(20);
	lPort = new JTextField(5);
	lUsePassword = new JCheckBox("use Password");
	lPassword = new JPasswordField(20);
	lSchema   = new JTextField(20);
	//lPassword = new JTextField(20);

	ffp.addComponent(new JLabel("Host"));
	ffp.addComponent(lHost);
	ffp.addComponent(new JLabel("User"));
	ffp.addComponent(lUser);
	ffp.addComponent(lUsePassword);
	ffp.addComponent(lPassword);


	FfAlignerLayoutPanel ffp2 = new FfAlignerLayoutPanel();
	ffp2.setBorder(BorderFactory.createEtchedBorder());
	ffp2.addComponent(new JLabel("JDBC-Driver"));
	ffp2.addComponent(lJDBCDriver);
	ffp2.addComponent(new JLabel("Database URL"));
	ffp2.addComponent(lUrl);
        ffp2.addComponent(new JLabel("Database Port"));
	ffp2.addComponent(lPort);
	ffp2.addComponent(new JLabel("Database"));
	ffp2.addComponent(lDatabase);
	ffp2.addComponent(new JLabel("Schema"));
	ffp2.addComponent(lSchema);

	final JComboBox ldb = new JComboBox();
	ldb.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setDatabase((String)ldb.getSelectedItem());
		}});
	
	final JComboBox loadSchema = new JComboBox();
	loadSchema.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setSchema((String)loadSchema.getSelectedItem());
		}});
	


	ImageButton selectDB = new ImageButton("Select","database.gif","Select Database and Schema");
	selectDB.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		try {
		    DBList dblist = new DBList();
		    ldb.removeAllItems();
		    for (int i = 0; i < dblist.getDBs().size(); i++) {
			ldb.addItem((String) dblist.getDBs().get(i));
		    }
		    ldb.setSelectedIndex(0);
		    ldb.updateUI();

		    loadSchema.removeAllItems();
		    for (int j = 0; j < dblist.getSchemas().size(); j++) {
			loadSchema.addItem((String) dblist.getSchemas().get(j));
		    }
		    loadSchema.setSelectedIndex(0);
		    loadSchema.updateUI();

		} catch (Exception excpetion) {;}
	}});
	ffp2.addComponent(selectDB);
	ffp2.addComponent(ldb);
	ffp2.addComponent(new JLabel(""));
	ffp2.addComponent(loadSchema);
			  


	FfAlignerLayoutPanel ffp3 = new FfAlignerLayoutPanel();
	ffp3.setBorder(BorderFactory.createEtchedBorder());
	ffp3.setNumberOfRows(1);

	ffp3.addComponent(new JLabel("Select predefined"));
	//final JLabel imgLabel = new JLabel(new ImageIcon(DataBaseLogin.class.getResource("images"+ java.io.File.separator + "sql.gif")));
	final JLabel imgLabel = new JLabel(loadImage("sql.gif"));

	final SelectDriver sd = new SelectDriver(db);
	sd.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		JdbcStuff js = new JdbcStuff();
		js = sd.getData(sd.getSelectedIndex());
		setDriver(js.getJDBCDriver());
		setUrl(js.getDbUrl());
		setPort(js.getPort());
		imgLabel.setIcon(js.getImage());
		}});	
	ffp3.addComponent(sd);
	JScrollPane imgScroller = new JScrollPane(imgLabel);
	ffp3.addComponent(imgScroller);


	this.add("North",ffp);
	this.add("Center",ffp2);
	this.add("West",ffp3);

        lPassword.requestFocus();
	
	JPanel buttonPanel = new JPanel();
	buttonPanel.setLayout(new FlowLayout());
	buttonPanel.setBorder(BorderFactory.createEtchedBorder());

	connect = new JButton("Connect",loadImage("plug.gif"));
	connect.setToolTipText("This stores the Info needed to connect to the Database");
	buttonPanel.add(connect);

	cancel = new JButton("Cancel",loadImage("unplug.gif"));
	cancel.setToolTipText("Exit");
	buttonPanel.add(cancel);

	this.add("South",buttonPanel);
	
    }
	

    private ImageIcon loadImage(String image) {
	String admin_img = System.getProperty("admin.image");
	ImageIcon img = null;
	if (admin_img == null) {
	    img = new ImageIcon(DataBaseLogin.class.getResource("images"+ File.separator + image));
	} else {
	    img = new ImageIcon(admin_img + File.separator + image);
	}
	    
        return img;
    }



    public static void main(String args[]) {
	JFrame f = new JFrame("TestWindow");
	f.getContentPane().add(new DataBaseLogin(args[0]));
	f.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent e) {}
	    public void windowClosed(WindowEvent e) {}
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	    public void windowDeactivated(WindowEvent e) {}
	    public void windowDeiconified(WindowEvent e) {}
	    public void windowIconified(WindowEvent e) {}
	    public void windowOpened(WindowEvent e) {}});
	f.pack();
	f.setVisible(true);
    }
}
