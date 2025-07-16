package applications.basics;

/**
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.BorderFactory; 
import javax.swing.border.*;
import javax.swing.event.*;
import java.io.*;

public class ButtonPanel extends JPanel {


    public ImageButton  insert, update, delete, clear, cancel,nextRecord,prevRecord;

    public ButtonPanel() {

	this.setLayout(new FlowLayout());
	insert   = new ImageButton("Insert","insert.gif","Inserts a new row");
	update   = new ImageButton("Update","update.gif","Update existing row");
	delete   = new ImageButton("Delete","delete.gif",null);
	clear    = new ImageButton("Clear","clear.gif",null);
	cancel   = new ImageButton("Cancel","exit.gif",null);
	prevRecord = new ImageButton(null,"vcrback.gif","back");
	nextRecord = new ImageButton(null,"vcrforward.gif","next");

	//this.add(prevRecord);
	this.add(insert);
	this.add(update);
	this.add(delete);
	this.add(clear);
	this.add(cancel);
	//this.add(nextRecord);

	this.setBorder(BorderFactory.createEtchedBorder());
    }


    private ImageIcon loadImage(String image) {
	String admin_img = System.getProperty("admin.image");
	ImageIcon img = null;
	if (admin_img == null) {
	    img = new ImageIcon(ButtonPanel.class.getResource("images"+ File.separator + image));
	} else {
	    img = new ImageIcon(admin_img + File.separator + image);
	}
	    
        return img;
    }


    public static void main(String args[]) {

	JFrame frame = new JFrame("Button Panel");
	ButtonPanel bp = new ButtonPanel();
	frame.getContentPane().setLayout(new BorderLayout());
	frame.getContentPane().add("Center",bp);
	bp.cancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		System.exit(0);
		}});
	frame.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent e) {}
	    public void windowClosed(WindowEvent e) {}
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	    public void windowDeactivated(WindowEvent e) {}
	    public void windowDeiconified(WindowEvent e) {}
	    public void windowIconified(WindowEvent e) {}
	    public void windowOpened(WindowEvent e) {}});
	frame.pack();
	frame.setVisible(true);

	
    }


}
