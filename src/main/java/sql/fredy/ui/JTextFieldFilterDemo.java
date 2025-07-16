  package sql.fredy.ui;

  import java.awt.*; 
  import java.awt.event.*;
  import javax.swing.event.*;
  import javax.swing.*;   

   public class JTextFieldFilterDemo extends JFrame{
     JTextField tf1,tf1b,tf1c,tf2,tf3;
     JLabel l1,l1b,l1c,l2,l3;

   public JTextFieldFilterDemo() {
       this.addWindowListener(new WindowAdapter() {
	   public void windowActivated(WindowEvent e) {}
	   public void windowClosed(WindowEvent e) {}
	   public void windowClosing(WindowEvent e) {System.exit(0);}
	   public void windowDeactivated(WindowEvent e) {}
	   public void windowDeiconified(WindowEvent e) {}
	   public void windowIconified(WindowEvent e) {}
	   public void windowOpened(WindowEvent e) {}});
       

       getContentPane().setLayout(new FlowLayout());
       //
       l1 = new JLabel("only numerics");
       tf1 = new JTextField(10);
       getContentPane().add(l1);
       getContentPane().add(tf1);
       tf1.setDocument
          (new JTextFieldFilter(JTextFieldFilter.NUMERIC));

       //
       l1b = new JLabel("only float");
       tf1b = new JTextField(10);
       getContentPane().add(l1b);
       getContentPane().add(tf1b);
       tf1b.setDocument
          (new JTextFieldFilter(JTextFieldFilter.FLOAT));

       //
       l1c = new JLabel("only float(can be negative)");
       tf1c = new JTextField(10);
       getContentPane().add(l1c);
       getContentPane().add(tf1c);
       JTextFieldFilter jtff = new JTextFieldFilter(JTextFieldFilter.FLOAT);
       jtff.setNegativeAccepted(true);
       tf1c.setDocument(jtff);

       //
       l2 = new JLabel("only uppercase");
       tf2 = new JTextField(10);
       getContentPane().add(l2);
       getContentPane().add(tf2);
       tf2.setDocument
          (new JTextFieldFilter(JTextFieldFilter.UPPERCASE));

       //
       l3 = new JLabel("only 'abc123%$'");
       tf3 = new JTextField(10);
       getContentPane().add(l3);
       getContentPane().add(tf3);
       tf3.setDocument
          (new JTextFieldFilter("abc123%$"));
       this.pack();
       this.setVisible(true);
   }

   public static void main(String args[]) {

       JTextFieldFilterDemo jtfd = new JTextFieldFilterDemo();
       
   }
}
