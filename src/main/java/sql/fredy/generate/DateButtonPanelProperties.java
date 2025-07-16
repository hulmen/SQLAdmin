package sql.fredy.generate;

/** DateButtonPanelProperties is to modify the Properties of the
 *  DateFields within the
 *  CodeGenerater and is a part of Admin...
 *  Version 1.2 16. March 2002
 *  Fredy Fischer 
 *
 *  this has been created to query mySQL-Databases
 *  it only returns a JPanel, so it can easily been 
 *  used in different kind of windows
 */


/**  
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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.border.*;

public class DateButtonPanelProperties extends JPanel {

    private JTextField label       = new JTextField("",15);
    private JTextField defaultText = new JTextField("",15);
    private JTextField title       = new JTextField("",15);
  
 
    private JRadioButton layoutStandard = new JRadioButton("Standard");
    private JRadioButton layoutRaised   = new JRadioButton("Raised");
    private JRadioButton layoutLowered  = new JRadioButton("Lowered");

    private JCheckBox    titled   = new JCheckBox("yes");


    FieldTypeParameters param = new FieldTypeParameters();
    
    /**
       * Get the value of param.
       * @return value of param.
       */
    public FieldTypeParameters getParam() {

	param.setLabel(label.getText());
	param.setText(defaultText.getText());
	if (titled.isSelected() )  {
	    param.setTitled(true);
	} else {
	    param.setTitled(false);
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
	defaultText.setText(param.getText());
	if (param.isTitled() ) {
	    titled.setSelected(true);
	    title.setEditable(true);
	} else {
	    titled.setSelected(false);	
	    title.setEditable(false);
	}     

	title.setText(param.getTitle());
	if ( param.getLayout().startsWith("0") ) layoutStandard.setSelected(true);
	if ( param.getLayout().startsWith("1") ) layoutRaised.setSelected(true);
	if ( param.getLayout().startsWith("2") ) layoutLowered.setSelected(true);

    }
        
    




    public DateButtonPanelProperties() {
	init();
    }

    /**
       * Initialises with the type of the component
       * @param v Type of the component.
       */

     public DateButtonPanelProperties(FieldTypeParameters v) {
	init();
	setParam(v);
    }

    private void init() {
	
	this.setName("DateButtonPanelProperties");
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
        gbc.anchor= GridBagConstraints.WEST;
	this.add(new JLabel("Default Value"),gbc);

        gbc.anchor= GridBagConstraints.WEST;
	gbc.gridy = 2;
	this.add(new JLabel("Titled"),gbc);

	gbc.gridy = 3;
	this.add(new JLabel("Title"),gbc);

	gbc.gridy = 4;
	this.add(new JLabel("Layout"),gbc);

	gbc.gridx = 1;
	gbc.gridy = 0;
	this.add(label,gbc);

	gbc.gridy = 1;
	this.add(defaultText,gbc);

	gbc.gridy = 2;
	this.add(titled,gbc);
	titled.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (titled.isSelected() ) {
			title.setEditable(true);
		    } else {
			title.setEditable(false);
		    }
		}});

	gbc.gridy = 3;
	this.add(title,gbc);


	gbc.gridy = 4;
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
	CloseableFrame cf = new CloseableFrame("DateButtonPanelProps");
	DateButtonPanelProperties dpp = new DateButtonPanelProperties();
	cf.getContentPane().add(dpp);
	cf.pack();
	cf.setVisible(true);

    }
}
