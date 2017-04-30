package sql.fredy.share;


/**  
 * Displays a message in a popup dialog. If it appears, something unwanted happened....
 * it is part of my tries  to make the exception handling better...
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 * and exports data into various formats
 *
 *
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
 * MsgDialog.java
 *
 *
 * Created:  Jan  4 7 2003
 *
 * @author Fredy Fischer
 * @version 1.0
 */
import sql.fredy.ui.ImageButton;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.*;




public class MsgDialog extends JDialog {

    public JTextArea msgArea;
    public ImageButton close;
    
    public MsgDialog () {

	this.getContentPane().setLayout(new BorderLayout());

	msgArea = new JTextArea(5,20);
	msgArea.setEditable(false);
	msgArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
	msgArea.setLineWrap(true);

	this.getContentPane().add(BorderLayout.CENTER,new JScrollPane(msgArea));
	this.getContentPane().add(BorderLayout.SOUTH,buttonPanel());
	this.pack();
    }

    public void setText(String v) {
	msgArea.setText(v);
	msgArea.updateUI();
    }

    public String getText() { return msgArea.getText(); }

    private JPanel buttonPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
	close = new ImageButton(null,"exit.gif","Close");
	panel.add(close);
	panel.setBorder(new EtchedBorder());
	return panel;
    }
}
