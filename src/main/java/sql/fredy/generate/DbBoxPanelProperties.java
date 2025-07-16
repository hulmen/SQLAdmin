package sql.fredy.generate;

/** DbBoxPanelProperties is to modify the Properties of the
 *  DBcomboBoxPanel and the TwinBoxPanel within the
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

public class DbBoxPanelProperties extends JPanel {

    private JTextField label       = new JTextField("",15);
    private JTextField defaultText = new JTextField("",15);
    private JTextField title       = new JTextField("",15);  
    private JTextArea    sqlStatement = new JTextArea(4,15);
    private JCheckBox    titled   = new JCheckBox("yes");

    private JRadioButton layoutStandard = new JRadioButton("Standard");
    private JRadioButton layoutRaised   = new JRadioButton("Raised");
    private JRadioButton layoutLowered  = new JRadioButton("Lowered");


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
	
	param.setQuery(sqlStatement.getText());
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

	sqlStatement.setText(param.getQuery());
	

    }
        
    


    public DbBoxPanelProperties() {

	init();

    }

    /**
       * Initialises with the type of the component
       * @param v Type of the component.
       */

    public DbBoxPanelProperties(String v) {

	init();   
    }


    public DbBoxPanelProperties(FieldTypeParameters v) {
	init();
	setParam(v);
    }





    private void init() {

	this.setName("DbBoxPanelProperties");
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
        gbc.anchor= GridBagConstraints.NORTHWEST;
	this.add(new JLabel("SQL-Query"),gbc);


	gbc.gridy = 5;
        gbc.anchor= GridBagConstraints.WEST;
	this.add(new JLabel("Default Text"),gbc);


	gbc.gridy = 6;
        gbc.anchor= GridBagConstraints.WEST;
	this.add(new JLabel("Titled"),gbc);
	
	gbc.gridy = 7;
	this.add(new JLabel("Title"),gbc);

	gbc.gridy = 8;
	this.add(new JLabel("Layout"),gbc);


	gbc.gridx = 1;
	gbc.gridy = 0;
	this.add(label,gbc);

	gbc.gridy = 1;
	gbc.gridheight = 4;
	this.add(new JScrollPane(sqlStatement),gbc);


	gbc.gridy = 5;
	gbc.gridheight = 1;
	this.add(defaultText,gbc);
	
	gbc.gridy = 6;
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
	CloseableFrame cf = new CloseableFrame("DbBoxPanelProps");
	DbBoxPanelProperties dpp = new DbBoxPanelProperties();
	cf.getContentPane().add(dpp);
	cf.pack();
	cf.setVisible(true);

    }
}
