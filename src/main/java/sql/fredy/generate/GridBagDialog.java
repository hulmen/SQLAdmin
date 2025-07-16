package sql.fredy.generate;

/** 
 *  GridBagDialog is to modify the GridBagConstraints within the
 *  CodeGenerater and is a part of Admin...
 *  Version 1.2 16. March 2002
 *  Fredy Fischer 
 *
 *  it only returns a JPanel, so it can easily been 
 *  used in different kind of windows
 *
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
import sql.fredy.ui.JTextFieldFilter;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.border.*;

public class GridBagDialog extends JDialog {


    private JTextField insetsField;
    private String[] anchorComponents =  { "CENTER",
					   "NORTH",
					   "NORTHEAST",
					   "NORTHWEST",
					   "SOUTH",  
					   "SOUTHEAST",
					   "SOUTHWEST",
					   "WEST", 
					   "EAST" };

    private JComboBox  anchor = new JComboBox(anchorComponents);

    private String[] fillComponents =  { "BOTH",
					 "NONE",
					 "HORIZONTAL",
					 "VERTICAL" };
					   
    private JComboBox fill  = new JComboBox(fillComponents);					     

    private JSpinner gridheight,gridwidth,gridx,gridy,weightx, weighty;
    public ImageButton ok, cancel;


    private String dblConv(double d) {
	return Double.toString(d);
    }

    private String intConv(int i) {
	return Integer.toString(i);
    }

    public void setGBC(GridBagConstraints gbc) {
	insetsField.setText(gbc.insets.toString() );

	/**
	weightx.setText(    dblConv(gbc.weightx) );
	weighty.setText(    dblConv(gbc.weighty) );
	gridheight.setText( dblConv(gbc.gridheight));
	gridwidth.setText(  dblConv(gbc.gridwidth));
	gridx.setText(      intConv(gbc.gridx) );
	gridy.setText(      intConv(gbc.gridy) );
	**/

	if ( gbc.anchor == GridBagConstraints.CENTER ) anchor.setSelectedIndex(0);
	if ( gbc.anchor == GridBagConstraints.NORTH ) anchor.setSelectedIndex(1);
	if ( gbc.anchor == GridBagConstraints.NORTHEAST ) anchor.setSelectedIndex(2);
	if ( gbc.anchor == GridBagConstraints.NORTHWEST ) anchor.setSelectedIndex(3);
	if ( gbc.anchor == GridBagConstraints.SOUTH ) anchor.setSelectedIndex(4);
	if ( gbc.anchor == GridBagConstraints.SOUTHEAST ) anchor.setSelectedIndex(5);
	if ( gbc.anchor == GridBagConstraints.SOUTHWEST ) anchor.setSelectedIndex(6);
	if ( gbc.anchor == GridBagConstraints.WEST ) anchor.setSelectedIndex(7);
	if ( gbc.anchor == GridBagConstraints.EAST ) anchor.setSelectedIndex(8);

	if ( gbc.fill == GridBagConstraints.BOTH )       fill.setSelectedIndex(0);
	if ( gbc.fill == GridBagConstraints.NONE )       fill.setSelectedIndex(1);
	if ( gbc.fill == GridBagConstraints.HORIZONTAL ) fill.setSelectedIndex(2);
	if ( gbc.fill == GridBagConstraints.VERTICAL )   fill.setSelectedIndex(3);

    }

    public GridBagConstraints getGBC() {

	GridBagConstraints gbc = new GridBagConstraints() ;


	return gbc;
    }

    private JPanel gridpanel() {

	JPanel panel = new JPanel();
	panel.setLayout(new GridBagLayout() );
	panel.setBorder(new EtchedBorder()  ) ;

	GridBagConstraints gbc;

        Insets insets = new Insets(1,1,1,1);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.anchor= GridBagConstraints.WEST;
        gbc.fill  = GridBagConstraints.BOTH;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 0;

	panel.add(new JLabel("Position X"),gbc);

	gbc.gridy = 1;
	panel.add(new JLabel("Position Y"),gbc);

	gbc.gridy = 2;
	panel.add(new JLabel("Insets"),gbc);

	gbc.gridy = 3;
	panel.add(new JLabel("Horizontal weight"),gbc);

	gbc.gridy = 4;
	panel.add(new JLabel("Vertical weight"),gbc);

	gbc.gridy = 5;
	panel.add(new JLabel("Gridheight"),gbc);

	gbc.gridy = 6;
	panel.add(new JLabel("Gridwidth"),gbc);

	gbc.gridy = 7;
	panel.add(new JLabel("Anchor"),gbc);

	gbc.gridy = 8;
	panel.add(new JLabel("Fill"),gbc);


        gridx   = new JSpinner(new SpinnerNumberModel(0, 500, 0, 1));
	gridy   = new JSpinner(new SpinnerNumberModel(0, 500, 0, 1));
	
        gridwidth   = new JSpinner(new SpinnerNumberModel(0, 50, 1, 1));
        gridheight  = new JSpinner(new SpinnerNumberModel(1, 50, 1, 1));
	
	weightx = new JSpinner(new SpinnerNumberModel(1.0, 50.0, 1.0, 0.1));
	weighty = new JSpinner(new SpinnerNumberModel(1.0, 50.0, 1.0, 0.1)); 

        gbc.anchor= GridBagConstraints.WEST;
        gbc.fill  = GridBagConstraints.VERTICAL;
        gbc.insets = insets;
        gbc.gridx = 1;
        gbc.gridy = 0;

	panel.add(gridx,gbc);

        gbc.gridy = 1;
	panel.add(gridy,gbc);

        gbc.gridy = 2;

	insetsField = new JTextField("",8);
	insetsField.setDocument (new JTextFieldFilter("0123456789,"));
	insetsField.setToolTipText("e.g. 2,2,2,2");
	panel.add(insetsField,gbc);

        gbc.gridy = 3;
	panel.add(weightx,gbc);

        gbc.gridy = 4;
	panel.add(weighty,gbc);

        gbc.gridy = 5;

	gridheight.setToolTipText("this components consumes \"n\" gridheights");
	panel.add(gridheight,gbc);

        gbc.gridy = 6;

	gridwidth.setToolTipText("this components consumes \"n\" gridwidth");
	panel.add(gridwidth,gbc);

        gbc.gridy = 7;
	anchor.setToolTipText("alignement");
	panel.add(anchor,gbc);

        gbc.gridy = 8;
	fill.setToolTipText("growth");
	panel.add(fill,gbc);

	return panel;
    }
 
    
    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
	ok = new ImageButton(null,"ok.gif",null);
	cancel = new ImageButton(null,"cancel.gif",null);
	panel.add(ok);
	panel.add(cancel);
	panel.setBorder(new EtchedBorder());
	return panel;
    }

    public GridBagDialog() {
	init();
    }


    public GridBagDialog(Frame frame,boolean modal) {
	super(frame,modal);
	init();


    }
    private void init() {
	this.setTitle("GridBagConstraints");
	this.getContentPane().setLayout(new BorderLayout());
	this.getContentPane().add(BorderLayout.CENTER,gridpanel());
	this.getContentPane().add(BorderLayout.SOUTH,buttonPanel());
	this.pack();
    }

    public static void main(String args[]) {

	CloseableFrame cf = new CloseableFrame("TEST");
	GridBagDialog gbd = new GridBagDialog(cf,true);
	gbd.cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		    }});
	gbd.setVisible(true);
    }
}	    
    




