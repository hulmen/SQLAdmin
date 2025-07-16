/*
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

 RSyntaxTextArea and AutoComplete is from www.fifesoft.com
 ---------------- Start fifesoft License ------------------------------------- 
       Copyright (c) 2012, Robert Futrell
      All rights reserved.
 
       Redistribution and use in source and binary forms, with or without
       modification, are permitted provided that the following conditions are met:
           * Redistributions of source code must retain the above copyright
             notice, this list of conditions and the following disclaimer.
           * Redistributions in binary form must reproduce the above copyright
             notice, this list of conditions and the following disclaimer in the
             documentation and/or other materials provided with the distribution.
           * Neither the name of the author nor the names of its contributors may
             be used to endorse or promote products derived from this software
             without specific prior written permission.
 
       THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
       ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
       WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
       DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
       DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
       (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
       LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
       ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
       (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
       SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ---------------- End fifesoft License -------------------------------------
*/


package sql.fredy.datadrill;

import sql.fredy.ui.ImageButton;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.JFrame;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.datatransfer.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
 

public class TextEditor extends JPanel {

  public ImageButton ok,cancel;
   
    //public JTextArea text;
    public RSyntaxTextArea text;
    
    public String getText() {
	return text.getText();

    }

    public void setText(String v) { 
	    text.setText(v);
    }

    public void setTitle(String t) {
	this.setTitle(t);
    }

    public TextEditor() {
	doIt();
    }
    public TextEditor(String text) {
	doIt();
	this.text.setText(text);
    }



    private void doIt() {

	initComponents();
	this.setLayout(new GridBagLayout());

	GridBagConstraints gbc;
	Insets insets = new Insets(5,5,5,5);

	gbc = new GridBagConstraints();
	gbc.anchor= GridBagConstraints.WEST;
        gbc.fill  = GridBagConstraints.HORIZONTAL;

	JPanel panel = new JPanel();


        panel = textPanel();

	gbc.gridx = 0;
	gbc.gridy = 0;	
	gbc.weightx = 0.8;	
	gbc.weighty = 0.2;
	this.add(panel,gbc);

	gbc.gridx = 0;
	gbc.gridy = 1;
        gbc.fill  = GridBagConstraints.BOTH;
	gbc.weightx = 0.8;	
	gbc.weighty = 0.2;
	this.add(buttonPanel(),gbc);		


    }

    private JPanel buttonPanel() {
	JPanel panel = new JPanel();
	panel.setBorder(new EtchedBorder());
	ok = new ImageButton(null,"ok.gif",null);
	cancel = new ImageButton(null,"exit.gif",null);
	panel.add(ok);
	panel.add(cancel);
	return panel;
    }



    private void initComponents() {
	//text = new JTextArea(10,20);
        text = new RSyntaxTextArea(10,20);
        text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        text.setCodeFoldingEnabled(true);
    }

	
    private JPanel textPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new GridLayout());

	GridBagConstraints gbc;
	Insets insets = new Insets(5,5,5,5);

	gbc = new GridBagConstraints();
	gbc.anchor= GridBagConstraints.WEST;
        gbc.fill  = GridBagConstraints.BOTH;

	//JScrollPane pane = new JScrollPane(text);
        RTextScrollPane pane = new RTextScrollPane(text);

	gbc.gridx = 0;
	gbc.gridy = 0;
	gbc.weightx = 1.0;	
	gbc.weighty = 1.0;
	panel.add(pane,gbc);

	return panel;

    }


    public static void main(String args[] ) {
	final TextEditor f = new TextEditor();
	JFrame e = new JFrame("Editor");
	e.getContentPane().add(f);
	e.pack();
	e.setVisible(true);
	e.addWindowListener(new WindowAdapter() {
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {
		    System.out.println(f.getText());
		    System.exit(0);
		}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}});
	f.cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		    }});
	


    }

}
