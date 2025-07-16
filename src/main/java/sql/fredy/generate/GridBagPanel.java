package sql.fredy.generate;

/** GridBagPanel is to modify the GridBagConstraints within the
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


import sql.fredy.ui.ImageButton;
import sql.fredy.ui.JTextFieldFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.JFrame;
import javax.swing.border.*;


public class GridBagPanel extends JPanel {

    public boolean debug = false;
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

    
    String title;
    
    /**
       * Get the value of title.
       * @return value of title.
       */
    public String getTitle() {return title;}
    
    /**
       * Set the value of title.
       * @param v  Value to assign to title.
       */
    public void setTitle(String  v) {this.title = v;}
    


    private String dblConv(double d) {
	return Double.toString(d);
    }

    private String intConv(int i) {
	return Integer.toString(i);
    }

    public void setGBC(GridBagConstraints gbc) {

	insetsField.setText(setIns(gbc.insets));

	weightx.setValue(   (Double) gbc.weightx);
	weighty.setValue(   (Double) gbc.weighty);

	gridheight.setValue((Integer) gbc.gridheight);
	gridwidth.setValue( (Integer) gbc.gridwidth);
	gridx.setValue(     (Integer) gbc.gridx);
        gridy.setValue(     (Integer) gbc.gridy);

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


    private Insets getIns() {
	int ins[] = new int[4];
	    StringTokenizer st = new StringTokenizer(insetsField.getText(),",");
	    for (int i=0;i <4;i++) {
		try {
		    ins[i] = Integer.parseInt(st.nextToken());
		} catch (Exception nfe) {
		    ins[i] = 2;
		    beep();
		}
	    }
    
	return new Insets(ins[0],ins[1],ins[2],ins[3]);
    }

    private void beep() {
		Toolkit tk = this.getToolkit();
		tk.beep();
    }

    private String setIns(Insets i) {
	String s ="";
	try {
	    s = s + Integer.toString(i.top) + ",";
	} catch (NumberFormatException nfe) {
	    beep();
	    s = s + "2,";
	}
	try {
	    s = s + Integer.toString(i.left) + ",";
	} catch (NumberFormatException nfe) {
	    beep();
	    s = s + "2,";
	}
	try {
	    s = s + Integer.toString(i.bottom) + ",";
	} catch (NumberFormatException nfe) {
	    beep();
	    s = s + "2,";
	}
	try {
	    s = s + Integer.toString(i.right);
	} catch (NumberFormatException nfe) {
	    beep();
	    s = s + "2";
	}
	return s;
    }


    public GridBagConstraints getGBC() {

	GridBagConstraints gbc = new GridBagConstraints() ;
	gbc.insets = getIns();

	gbc.gridx      =  (int)((Integer)gridx.getValue()).intValue();
	gbc.gridy      =  (int)((Integer)gridy.getValue()).intValue();
	gbc.gridheight =  (int)((Integer)gridheight.getValue()).intValue();
	gbc.gridwidth  =  (int)((Integer)gridwidth.getValue()).intValue();

	gbc.weightx = (double)((Double)weightx.getValue()).doubleValue();
	gbc.weighty = (double)((Double)weighty.getValue()).doubleValue();

	switch (anchor.getSelectedIndex() ) {
	case 0:
	    gbc.anchor =  GridBagConstraints.CENTER;
	    break;
	case 1:
	    gbc.anchor =  GridBagConstraints.NORTH;
	    break;
	case 2:
	    gbc.anchor =  GridBagConstraints.NORTHEAST;
	    break;
	case 3:
	    gbc.anchor =  GridBagConstraints.NORTHWEST;
	    break;
	case 4:
	    gbc.anchor =  GridBagConstraints.SOUTH;
	    break;
	case 5:
	    gbc.anchor =  GridBagConstraints.SOUTHEAST;
	    break;
	case 6:
	    gbc.anchor =  GridBagConstraints.SOUTHWEST;
	    break;
	case 7:
	    gbc.anchor =  GridBagConstraints.WEST;
	    break;
	case 8:
	    gbc.anchor =  GridBagConstraints.EAST;
	    break;
	default:
	    gbc.anchor =  GridBagConstraints.WEST;
	    break;
	}


	switch ( fill.getSelectedIndex() ) {
	case 0:
	    gbc.fill = GridBagConstraints.BOTH;
	    break;
	case 1:
	    gbc.fill = GridBagConstraints.NONE;
	    break;
	case 2:
	    gbc.fill = GridBagConstraints.HORIZONTAL;
	    break;
	case 3:
	    gbc.fill = GridBagConstraints.VERTICAL;
	    break;
	default:
	    gbc.fill = GridBagConstraints.NONE;
	    break;
	}
	if ( debug) {
	    System.out.println("---- GridBagPanel output as gbc ------------\n" +
			       "Anchor  : " + anchor.getSelectedItem() + "\n" +
			       "Fill    : " + fill.getSelectedItem()   + "\n" +
			       "insets  : " + gbc.insets.top + "," + gbc.insets.left + "," 
			       + gbc.insets.bottom + "," + gbc.insets.right +  "\n" +
			       "Weightx : " + gbc.weightx + "\n" +
			       "Weighty : " + gbc.weighty + "\n" +
			       "Height  : " + gbc.gridheight + "\n" +
			       "Width   : " + gbc.gridwidth + "\n" +
			       "X       : " + gbc.gridx + "\n" +
			       "Y       : " + gbc.gridy + "\n" +
			       "--------------------------------------------");
	}
	    

	return gbc;
    }

    private JPanel gridpanel() {

	JPanel panel = new JPanel();
	panel.setLayout(new GridBagLayout() );
	panel.setBorder(new TitledBorder(new EtchedBorder(),getTitle() )) ;

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

        gridwidth   = new JSpinner(new SpinnerNumberModel(1, 50, 1, 1));
	gridheight  = new JSpinner(new SpinnerNumberModel(1, 50, 1, 1));

	weightx = new JSpinner(new SpinnerNumberModel(1.0, 50.0, 1.0, 0.1));
	weighty = new JSpinner(new SpinnerNumberModel(1.0, 50.0, 1.0, 0.1));

        gbc.anchor= GridBagConstraints.WEST;
        gbc.fill  = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        gbc.gridx = 1;
        gbc.gridy = 0;

	panel.add(gridx,gbc);

        gbc.gridy = 1;
	panel.add(gridy,gbc);

        gbc.gridy = 2;

	insetsField = new JTextField("2,2,2,2",8);
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
 
    

    public GridBagPanel() {
	setTitle("GridBag-Constraints");
	init();
    }
    public GridBagPanel(String title) {
	setTitle(title);
	init();
    }

    private void init() {
	this.setLayout(new BorderLayout());
	this.add(BorderLayout.CENTER,gridpanel());
    }

    public static void main(String args[]) {

	JFrame cf = new JFrame("GridBagPanel");
	cf.getContentPane().setLayout(new BorderLayout());
	
	final GridBagPanel gbp = new GridBagPanel();
	JButton test = new JButton("Test");

	gbp.debug = true;
	test.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    gbp.getGBC();
		    }});
	

	cf.addWindowListener(new WindowAdapter() {
		public void windowActivated(WindowEvent e) {}
		public void windowClosed(WindowEvent e) {}
		public void windowClosing(WindowEvent e) {System.exit(0);}
		public void windowDeactivated(WindowEvent e) {}
		public void windowDeiconified(WindowEvent e) {}
		public void windowIconified(WindowEvent e) {}
		public void windowOpened(WindowEvent e) {}});
	

	cf.getContentPane().add(BorderLayout.CENTER,gbp);
	cf.getContentPane().add(BorderLayout.SOUTH,test);


	cf.pack();
	cf.setVisible(true);
    }
}	    
    




