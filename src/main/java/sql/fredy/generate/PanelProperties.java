package sql.fredy.generate;

/** PanelProperties is to modify the Properties of a Panel within the
 *  CodeGenerater and is a part of Admin...
 *  Version 1.2 16. March 2002
 *  Fredy Fischer 
 *
 *  it only returns a JPanel, so it can easily been 
 *  used in different kind of windows
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


import sql.fredy.ui.CloseableFrame;
import sql.fredy.ui.ImageButton;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.border.*;


public class PanelProperties extends JPanel {

   private String[] borderComponents =  { 
       "Empty Border",
       "Standard Border",
       "lowered",
       "raised",
       "thin line Border", 
       "medium line Border",
       "thick line Border"
   };


    
    PanelTreeNodeObject ptno = new  PanelTreeNodeObject();
    
    /**
       * Get the value of ptno.
       * @return value of ptno.
       */
    public PanelTreeNodeObject getPtno() {

	ptno.setName(name.getText());
	ptno.setTitle(title.getText());	
	ptno.setGbc(new GridBagObject(gbp.getGBC()));

	// the Border
	int ind = border.getSelectedIndex();
	if (ind == 0 ) ptno.setBorder("Empty");
	if (ind == 1 ) ptno.setBorder("EtchedBorder()");
	if (ind == 2 ) ptno.setBorder("BevelBorder.LOWERED");
	if (ind == 3 ) ptno.setBorder("BevelBorder.RAISED");
	if (ind == 4 ) ptno.setBorder("thin");
	if (ind == 5 ) ptno.setBorder("medium");
	if (ind == 6 ) ptno.setBorder("thick");

	xn.setNode((PanelTreeNodeObject)ptno);

	return ptno;
    }
    
    /**
       * Set the value of ptno.
       * @param v  Value to assign to ptno.
       */
    public void setPtno(PanelTreeNodeObject  v) {
	this.ptno = v;
       
	// the GridBagPanel
	GridBagObject gbo = new GridBagObject();
	gbo = ptno.getGbc();
	gbp.setGBC(gbo.getGbc());

	// the Border
	int ind = 0;

	if (ptno.getBorder().toLowerCase().startsWith("etched") )              ind = 1;
	if (ptno.getBorder().toLowerCase().startsWith("bevelborder(bevelborder.lowered)") ) ind = 2;
	if (ptno.getBorder().toLowerCase().startsWith("bevelborder(bevelborder.raised)") )  ind = 3;
	if (ptno.getBorder().toLowerCase().startsWith("thin") )                ind = 4;
	if (ptno.getBorder().toLowerCase().startsWith("medium") )              ind = 5;
	if (ptno.getBorder().toLowerCase().startsWith("thick") )               ind = 6;
	border.setSelectedIndex(ind);

	// the name
	name.setText(ptno.getName());
	if (ptno.getName().toLowerCase().startsWith("mainpanel")) name.setEditable(false);
	
	// the Title
	title.setText(ptno.getTitle());										   
    }
    

    
    XMLTreeNode xn = new XMLTreeNode();
    
    /**
       * Get the value of xn.
       * @return value of xn.
       */
    public XMLTreeNode getXn() {
      xn.setNode((PanelTreeNodeObject)getPtno());
      return xn;
    }
    
    /**
       * Set the value of xn.
       * @param v  Value to assign to xn.
       */
    public void setXn(XMLTreeNode  v) {
	this.xn = v;
	this.setPtno((PanelTreeNodeObject)xn.getNode());
    }
    


    public JComboBox  border = new JComboBox(borderComponents);
    public JTextField name,title;
    public GridBagPanel gbp;

    public PanelProperties() {
	init();
    }

    public PanelProperties(XMLTreeNode xn) {
	init();
	this.setXn(xn);
    }

    public void init() {
	
	this.setName("PanelProperties");

	this.setLayout(new GridBagLayout());
	GridBagConstraints gbc;
	this.setBorder(new TitledBorder(new EtchedBorder(),"Panel Properties"));

        Insets insets = new Insets(1,1,1,1);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.anchor= GridBagConstraints.WEST;
        gbc.fill  = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 0;
	this.add(upperPart(),gbc);
	
	gbc.gridy = 1;
	gbp = new GridBagPanel();
	this.add(gbp,gbc);

	//gbc.gridy = 2;
	//this.add(applyPanel(),gbc);
       
    }
    private JPanel upperPart() { 
	
	JPanel panel = new JPanel();

	panel.setBorder(new EtchedBorder());
	panel.setLayout(new GridBagLayout());
	title = new JTextField("null",15);
	name  = new JTextField("",15);
		
	GridBagConstraints gbc;

        Insets insets = new Insets(1,1,1,1);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.anchor= GridBagConstraints.WEST;
        gbc.fill  = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 0;

	panel.add(new JLabel("Name"),gbc);

	gbc.gridy = 1;
	panel.add(new JLabel("Title"),gbc);
	
	gbc.gridy = 2;
	panel.add(new JLabel("Border"),gbc);

	gbc.gridx = 1;
	gbc.gridy = 0;
	panel.add(name,gbc);

	gbc.gridy = 1;
	panel.add(title,gbc);
	
	gbc.gridy = 2;
	panel.add(border,gbc);

	return panel;
    }

    private JPanel applyPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
	panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
	
	ImageButton apply = new ImageButton("Apply","apply.gif","apply changes");
	panel.add(apply);
	apply.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    }});
	
	return panel;
    }

    public static void main(String args[]) {

	CloseableFrame cf = new CloseableFrame("TEST");
	PanelProperties pp = new PanelProperties();
	cf.getContentPane().add(pp);
	cf.pack();
	cf.setVisible(true);
    }
}	
	    
