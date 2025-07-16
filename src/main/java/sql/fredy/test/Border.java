package sql.fredy.test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;


public class Border extends JFrame {


    public Border() {

	this.addWindowListener(new WindowAdapter() {
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {System.exit(0);}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}});
       

	JPanel a = new JPanel();
	a.setLayout(new FlowLayout());
	a.add(new JLabel("Empty "));
	a.setBorder(new EmptyBorder(1,1,1,1));

	JPanel c = new JPanel();
	c.setLayout(new FlowLayout());
	c.add(new JLabel("BevelBorder lowered"));
	c.setBorder(new BevelBorder(BevelBorder.LOWERED));

	JPanel d = new JPanel();
	d.setLayout(new FlowLayout());
	d.add(new JLabel("BevelBorder raised"));
	d.setBorder(new BevelBorder(BevelBorder.RAISED));

	JPanel e = new JPanel();
	e.setLayout(new FlowLayout());
	e.add(new JLabel("Line"));
	e.setBorder(new LineBorder(Color.black,1));

	JPanel e2 = new JPanel();
	e2.setLayout(new FlowLayout());
	e2.add(new JLabel("Line"));
	e2.setBorder(new LineBorder(Color.black,2));

	JPanel e3 = new JPanel();
	e3.setLayout(new FlowLayout());
	e3.add(new JLabel("Line"));
	e3.setBorder(new LineBorder(Color.black,3));

	this.getContentPane().setLayout(new FlowLayout());
	this.getContentPane().add(a);
	this.getContentPane().add(c);
	this.getContentPane().add(d);
	this.getContentPane().add(e);
	this.getContentPane().add(e2);
	this.getContentPane().add(e3);

	this.pack();
	this.setVisible(true);

    }
    public static void main(String a[]) {

	Border b = new Border();
    }
}
