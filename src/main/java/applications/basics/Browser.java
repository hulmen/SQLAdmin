package applications.basics;


/** Admin is a Tool around mySQL to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

    Admin  (Version see below)

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

/**
 * Browser.java
 *
 *
 * Created: Tue Jan  2 12:37:37 2001
 *
 * @author Fredy Fischer
 * @version
 */
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;


public class Browser extends JPanel {
    
      
    private JTextField url;
    private JEditorPane editorPane;
    public JButton cancel;
    private Stack urlStack = new Stack();
    private String homeUrl=null;
    private ImageButton home;

    public Browser() {
	this.setLayout(new BorderLayout());
	this.add(BorderLayout.SOUTH,buttonPanel());
	this.add(BorderLayout.CENTER,showPanel());
	
    }

    private JPanel buttonPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());

	home = new ImageButton(null,"home.gif","Home");
	home.setEnabled(false);
	home.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setURL(homeUrl);
		}});
	
	ImageButton left = new ImageButton(null,"left.gif","Back");
	left.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (urlStack.size() <= 1) return;
		try { 
		    urlStack.pop();		
		    String urlString = (String)urlStack.peek();
		    url.setText(urlString);                 
		    editorPane.setPage(urlString);
		} catch(IOException e1) {
		  editorPane.setText("Fehler: " + e1);		    
	        }
	    }});
	
	

	cancel = new JButton();
	ImageButton imb = new ImageButton();
	ImageIcon i1 = imb.getImageIcon("exit.gif");
	ImageIcon i2 = imb.grayed(i1.getImage());
	cancel.setIcon(i2);
	cancel.setRolloverIcon(i1);
	cancel.setRolloverEnabled(true);
	cancel.setToolTipText("Exit");

	panel.add(home);
	panel.add(left);
	panel.add(cancel);
	panel.add(new JLabel("URL:"));
	url = new JTextField(25);
	url.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		setURL(url.getText());
	    }});
	
	panel.add(url);

	return panel;	   
    }

    public void setURL(String s) {
		try {
		     urlStack.push(s);
		     editorPane.setPage(s);
		     url.setText(s);
		     if (homeUrl == null) {
			 homeUrl = url.getText();
			 home.setEnabled(true);
		     }
		} catch (IOException ex) {
		    editorPane.setText("Error: " + ex);
		}
    }


    private JScrollPane showPanel() {
      editorPane = new JEditorPane();
      editorPane.setEditable(false);
      editorPane.addHyperlinkListener(new HyperlinkListener()
         {  public void hyperlinkUpdate(HyperlinkEvent event)
            {  if (event.getEventType() 
                 == HyperlinkEvent.EventType.ACTIVATED)
              { 
		  setURL(event.getURL().toString());
	      }
            }
         });
       JScrollPane scrollpane = new JScrollPane(editorPane);
       return scrollpane;
    }

    
    public static void main(String args[]) {
	JFrame frame = new JFrame("Fredy's Browser");
	frame.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent e) {}
	    public void windowClosed(WindowEvent e) {}
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	    public void windowDeactivated(WindowEvent e) {}
	    public void windowDeiconified(WindowEvent e) {}
	    public void windowIconified(WindowEvent e) {}
	    public void windowOpened(WindowEvent e) {}});
	
	Browser browser = new Browser();
	browser.cancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
		}});
	frame.getContentPane().add(browser);
	if (args.length > 0) browser.setURL(args[0]);
	frame.pack();
	frame.setVisible(true);
    }


} // Browser
