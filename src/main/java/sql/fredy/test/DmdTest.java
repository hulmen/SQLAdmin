package sql.fredy.test;

import sql.fredy.ui.ImageButton;
import sql.fredy.share.t_connect;
import java.sql.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.BorderFactory; 
import javax.swing.border.*;

public class DmdTest extends JFrame {

   
    private JTextField host, user, password, database, schema, table, column;
    private JTextArea output;   
    private t_connect con;
    
    private ImageButton cancel;	
    private ImageButton exec;
    private ImageButton open;
    private ImageButton close;


    private JPanel loginPanel() {

	
	JPanel panel = new JPanel();
	panel.setLayout(new GridLayout(0,2));

	host     = new JTextField("localhost");
	user     = new JTextField(System.getProperty("user.name"));
	password = new JTextField("");
	database = new JTextField("");
	schema   = new JTextField("%");
	table    = new JTextField("");
	column   = new JTextField("");
	
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

	return panel;
    }

    private JPanel outPanel() {

	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());
	
	output = new JTextArea(10,25);
	output.setFont(new Font("Courier", Font.PLAIN, 14));
	output.setEditable(false);

	JScrollPane scp = new JScrollPane(output);
	
	panel.add(BorderLayout.CENTER,scp);

	return panel;
    }

    private JPanel buttonPanel() {

	cancel = new ImageButton(null,"cancel.gif","Exit");	
	exec   = new ImageButton(null,"exec.gif","Run");
	open   = new ImageButton(null,"plug.gif","connect");
	close  = new ImageButton(null,"unplug.gif","disconnect");

	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());

	panel.add(open);
	panel.add(exec);
	exec.setEnabled(false);
	close.setEnabled(false);
	panel.add(close);
	panel.add(cancel);

	panel.setBorder(new EtchedBorder());

	
	open.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    connect();
		}
	    });
	
	close.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    disconnect();
		}
	    });
	
	
	exec.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    execute();
		}
	    });
	
	cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		}
	    });
	
	return panel;
    }

    private void connect() {
	con = new t_connect(host.getText(),
			    user.getText(),
			    password.getText(),
			    database.getText());
	if ( con.getError() != null ) {
	    output.setText("Connection not successfull!\n\n");
	    output.append(con.getError());
	} else {
	    output.setText("Connection successfull\n\n");
	    exec.setEnabled(true);
	    close.setEnabled(true);
	    open.setEnabled(false);
	}
    }
    private void disconnect() {
	con.close();
	open.setEnabled(true);
	exec.setEnabled(false);
    }

    private void execute() {

	try {
	       DatabaseMetaData md = con.con.getMetaData();
	       ResultSet rs = md.getTypeInfo() ;
	       while (rs.next()) {
		   output.append("\n- Type-Name:      " + rs.getString(1));
		   //output.append("\n- Data-Type:      " + rs.getShort(2));
		   //output.append("\n- Literal_Prefix: " + rs.getString(4));
		   //output.append("\n- Literal_Suffix: " + rs.getString(5));
		   output.append("\n- Create Params:  " + rs.getString(6));
		   output.append("\n- Nullable:       " + rs.getString(7));
		   output.append("\n- Auto Increment: " + rs.getBoolean(12));
		   output.append("\n\n");
	       }
	} catch (SQLException sqe ){
	    output.append("\n\n -> SQL-Exception: " + sqe.getMessage().toString());
	}
    }

    private JSplitPane display() {

	JSplitPane panel = new JSplitPane();
	panel.setLeftComponent(loginPanel());
	panel.setRightComponent(outPanel());
	return panel;
    }


    public DmdTest() {
	super("Test Data Types");
	
	this.getContentPane().setLayout(new BorderLayout());
	this.getContentPane().add(BorderLayout.SOUTH,buttonPanel());
	this.getContentPane().add(BorderLayout.CENTER,display());
	
	this.pack();
	this.setVisible(true);
	this.addWindowListener(new WindowAdapter() {
		public void windowActivated(WindowEvent e) {
		}
		public void windowClosed(WindowEvent e){
		    
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
    }
    public static void main(String a[]) {
	DmdTest d = new DmdTest();
    }
}
