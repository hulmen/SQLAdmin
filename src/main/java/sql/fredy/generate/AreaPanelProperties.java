package sql.fredy.generate;

/** AreaPanelProperties is to modify the Properties of the
 *  TextAreaFields within the
 *  CodeGenerater and is a part of Admin...
 *  Version 1.2 16. March 2002
 *  Fredy Fischer 
 *
 *  this has been created to query mySQL-Databases
 *  it only returns a JPanel, so it can easily been 
 *  used in different kind of windows
 
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.border.*;


public class AreaPanelProperties extends JPanel {

    private JTextField label       = new JTextField("",15);
    private JTextArea  defaultText = new JTextArea(4,15);
    private JTextField title       = new JTextField("",15);
  

    private JSpinner     cols,rows;
    private JCheckBox    titled   = new JCheckBox("yes");
    private JCheckBox    lineWrap = new JCheckBox("true");

    private JRadioButton layoutStandard = new JRadioButton("Standard");
    private JRadioButton layoutRaised   = new JRadioButton("Raised");
    private JRadioButton layoutLowered  = new JRadioButton("Lowered");


    FieldTypeParameters param = new FieldTypeParameters();
    
    /**
       * Get the value of param.
       * @return value of param.
       */
    public FieldTypeParameters getParam() {

	param.setCols((int)((Integer)cols.getValue()).intValue());
	param.setRows((int)((Integer)rows.getValue()).intValue());

	param.setLabel(label.getText());
	param.setText(defaultText.getText());
	if (titled.isSelected() )  {
	    param.setTitled(true);
	} else {
	    param.setTitled(false);
	}
	
	if (lineWrap.isSelected() )  {
	    param.setLinewrap(true);
	} else {
	    param.setLinewrap(false);
	}
	
	param.setTitle(title.getText());

	String s = "0";
	if (layoutRaised.isSelected())  s = "1";
	if (layoutLowered.isSelected()) s = "2";
	param.setLayout(s);

	return param;
    }


    public void setParam(FieldTypeParameters  v) {
	this.param = v;
	
	label.setText(param.getLabel());

	cols.setValue(param.getCols());
	rows.setValue(param.getRows());

	defaultText.setText(param.getText());
	if (param.isTitled() ) {
	    titled.setSelected(true);
	    title.setEditable(true);
	} else {
	    titled.setSelected(false);	
	    title.setEditable(false);
	}
	
	if (param.isLinewrap() ) {
	    lineWrap.setSelected(true);
	    defaultText.setLineWrap(true);
	} else {
	    lineWrap.setSelected(false);	
	    defaultText.setLineWrap(false);
	}

	title.setText(param.getTitle());
	if ( param.getLayout().startsWith("0") ) layoutStandard.setSelected(true);
	if ( param.getLayout().startsWith("1") ) layoutRaised.setSelected(true);
	if ( param.getLayout().startsWith("2") ) layoutLowered.setSelected(true);

    }
        

    


    public AreaPanelProperties() {
	init();
    }

    /**
       * Initialises with the type of the component
       * @param v Type of the component.
       */

    public AreaPanelProperties(String v) {

	init();   
    }

    public AreaPanelProperties(FieldTypeParameters v) {
	init();
	setParam(v);
    }




    private void init() {

	this.setName("AreaPanelProperties");

	this.setBorder(new TitledBorder(new EtchedBorder(),this.getName()));


	this.setLayout(new GridBagLayout());
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
	
	this.add(new JLabel("Label"),gbc);
	
	gbc.gridy = 1;
	this.add(new JLabel("Rows"),gbc);
	gbc.gridx = 2 ;
        gbc.anchor= GridBagConstraints.EAST;
	this.add(new JLabel("Cols"),gbc);
	gbc.gridx = 0;

	gbc.gridy = 2;
        gbc.anchor= GridBagConstraints.NORTHWEST;
	this.add(new JLabel("Default Text"),gbc);

        gbc.anchor= GridBagConstraints.WEST;

	gbc.gridy = 6;
	this.add(new JLabel("Linewrap"),gbc);

	gbc.gridy = 6;
	gbc.gridx = 2 ;	
	this.add(new JLabel("Titled"),gbc);

	gbc.gridy = 7;
	gbc.gridx = 0;
	this.add(new JLabel("Title"),gbc);

	gbc.gridy = 8;
	this.add(new JLabel("Layout"),gbc);

	gbc.gridx = 1;
	gbc.gridy = 0;
	gbc.gridwidth = 3;
	this.add(label,gbc);


	gbc.gridy = 1;
	gbc.gridwidth = 1;
        gbc.fill  = GridBagConstraints.NONE;
        rows   = new JSpinner(new SpinnerNumberModel(1,50,1,1));	
	this.add(rows,gbc);

	gbc.gridx = 3;
        gbc.fill  = GridBagConstraints.NONE;

        cols   =  new JSpinner(new SpinnerNumberModel(1,50,1,1));	
	this.add(cols,gbc);


	gbc.gridy = 2;
	gbc.gridx = 1;
	gbc.gridheight = 4;
	gbc.gridwidth = 3;

        gbc.fill  = GridBagConstraints.HORIZONTAL;

	this.add(new JScrollPane(defaultText),gbc);	


	gbc.gridy = 6;
	gbc.gridheight = 1;
	gbc.gridwidth = 1;
	this.add(lineWrap,gbc);
	
	lineWrap.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (lineWrap.isSelected()) {
			defaultText.setLineWrap(true) ;
		    } else {
			defaultText.setLineWrap(false) ;	
		    }
		    }});
	

	
	gbc.gridy = 6;
	gbc.gridx = 3;
	gbc.gridwidth = 1;
	this.add(titled,gbc);
	titled.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (titled.isSelected() ) {
			title.setEditable(true);
		    } else {
			title.setEditable(false);
		    }
		}});

	gbc.gridy = 7;
	gbc.gridx = 1;
	gbc.gridwidth = 3;
	this.add(title,gbc);

	gbc.gridy = 8;
	this.add(layoutPanel(),gbc);

    }

    private JPanel layoutPanel() {

	ButtonGroup group = new ButtonGroup();
	group.add(layoutStandard);
	group.add(layoutRaised);
	group.add(layoutLowered);

	JPanel radioPanel = new JPanel();
	radioPanel.setLayout(new GridLayout(1, 0));
	radioPanel.add(layoutStandard);
	radioPanel.add(layoutRaised);
	radioPanel.add(layoutLowered);

	return radioPanel;

    }



    public static void main(String a[]) {
	CloseableFrame cf = new CloseableFrame("AreaPanelProps");
	AreaPanelProperties app = new AreaPanelProperties();
	cf.getContentPane().add(app);
	cf.pack();
	cf.setVisible(true);

    }
}
