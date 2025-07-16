package sql.fredy.test;

import java.util.logging.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.BorderFactory; 
import javax.swing.border.*;


/**

   Das Logger-property File ist : /usr/java/jre/lib/logging.properties

**/


public class LoggerTest extends JFrame {

    private String[] logTypes = { "FINEST",
				  "FINE",
				  "CONFIG",
				  "INFO",
				  "WARNING",
				  "SEVERE",
                                  "ALL" };

    private JComboBox logLevel = new JComboBox(logTypes);
    private JTextArea mesg = new JTextArea(5,40);


    private void log ( ) {
	String msg  = mesg.getText();

	Logger logger = Logger.getLogger("sql.fredy.test.LoggerTest");


	if ( logLevel.getSelectedIndex() == 0 ) logger.log(Level.FINEST,msg);
	if ( logLevel.getSelectedIndex() == 1 ) logger.log(Level.FINE,msg);
	if ( logLevel.getSelectedIndex() == 2 ) logger.log(Level.CONFIG,msg);
	if ( logLevel.getSelectedIndex() == 3 ) logger.log(Level.INFO,msg);
	if ( logLevel.getSelectedIndex() == 4 ) logger.log(Level.WARNING,msg);
	if ( logLevel.getSelectedIndex() == 5 ) logger.log(Level.SEVERE,msg);
	if ( logLevel.getSelectedIndex() == 6 ) logger.log(Level.ALL,msg);
    }

    private JPanel buttonPanel () {
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
	
	JButton logIt = new JButton("log Message");
	JButton clear = new JButton("clear");
	JButton cancel= new JButton("cancel");

	logIt.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    log();
		}
	    });
	clear.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    mesg.setText("");
		}
	    });
	cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		}
	    });
	panel.add(logIt);
	panel.add(clear);
	panel.add(cancel);
	return panel;
    }
	
    private JScrollPane textPanel() {
	JScrollPane scp = new JScrollPane(mesg);
	return scp;
    }

    public LoggerTest() {
	super("Logger Test");



	this.getContentPane().setLayout(new BorderLayout());
	this.getContentPane().add(BorderLayout.NORTH,logLevel);
	this.getContentPane().add(BorderLayout.CENTER,textPanel());
	this.getContentPane().add(BorderLayout.SOUTH,buttonPanel());

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
	
	this.pack();
	this.setVisible(true);
    }
    public static void main(String a[]) {
	LoggerTest lt = new LoggerTest();
    }
}
