package sql.fredy.test;


import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Output extends JFrame {


    

    public Output() {
    super("Redirect");

    this.addWindowListener(new WindowAdapter() {
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {System.exit(0);}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}});
    
    try {
	System.setOut(new PrintStream(new FileOutputStream(System.getProperty("user.home" + File.separator + "log.txt")))); 

    } catch (FileNotFoundException fne) { System.out.println("Exception " + fne); }

    final  JTextArea a = new JTextArea(20,20);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(BorderLayout.CENTER,a);
    
    JPanel panel = new JPanel();
    JButton close = new JButton("Close");
    close.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    System.exit(0);
	    }});
    JButton ex = new JButton("DO");
    ex.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	    System.out.println(a.getText());
	    }});

    panel.add(ex);
    panel.add(close);

    this.getContentPane().add(BorderLayout.SOUTH,panel);
    this.pack();
    this.setVisible(true);
        
    
    }

    public static void main(String args[]) {

	Output o = new Output();
	
    }
}
