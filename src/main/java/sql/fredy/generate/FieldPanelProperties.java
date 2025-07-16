package sql.fredy.generate;

/** FieldPanelProperties is to modify the Properties of the
 *  TextFields within the
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


public class FieldPanelProperties extends JPanel {

    private JTextField label       = new JTextField("",15);
    private JTextField defaultText = new JTextField("",15);
    private JTextField title       = new JTextField("",15);
  
 
    private String[] textFilterComponents =   { "JTextFieldFilter.ALPHA_NUMERIC",
					        "JTextFieldFilter.ALPHA",
					        "JTextFieldFilter.LOWERCASE",
					        "JTextFieldFiler.UPPERCASE",
					        "JTextFieldFilter.NUMERIC", 
					        "JTextFieldFilter.FLOAT",
						"null"
    };

    private String[] numberFilterComponents = { "JTextFieldFilter.NUMERIC", 
					        "JTextFieldFilter.FLOAT" };


    private JComboBox textFilter = new JComboBox();

    private JSpinner     length;
    private JCheckBox    titled   = new JCheckBox("yes");
    private JCheckBox    lineWrap = new JCheckBox("true");
    private JRadioButton layoutStandard = new JRadioButton("Standard");
    private JRadioButton layoutRaised   = new JRadioButton("Raised");
    private JRadioButton layoutLowered  = new JRadioButton("Lowered");


    public FieldPanelProperties() {
	setComponentType("char");
	init();

    }

    /**
       * Initialises with the type of the component
       * @param v Type of the component.
       */

    public FieldPanelProperties(FieldTypeParameters v) {
	init();   
	setParam(v);
    }


    
    FieldTypeParameters param = new FieldTypeParameters();
    
    /**
       * Get the value of param.
       * @return value of param.
       */
    public FieldTypeParameters getParam() {
	param.setLength((int)((Integer)length.getValue()).intValue());
	param.setLabel(label.getText());
	param.setText(defaultText.getText());
	if (titled.isSelected() )  {
	    param.setTitled(true);
	} else {
	    param.setTitled(false);	    
	}	
	if ( (! ((String)textFilter.getSelectedItem()).startsWith("JTextFieldFilter"))
	     &&
	     ( ! ((String)textFilter.getSelectedItem()).startsWith("null") )) {
	    param.setFilter("\"" + (String)textFilter.getSelectedItem() + "\"");
	} else {
	    param.setFilter((String)textFilter.getSelectedItem());
	}

	param.setTitle(title.getText());

	String s = "0";
	if (layoutRaised.isSelected())  s = "1";
	if (layoutLowered.isSelected()) s = "2";
	param.setLayout(s);

	return param;
    }
    
    /**
       * Set the value of param.
       * @param v  Value to assign to param.
       */
    public void setParam(FieldTypeParameters  v) {
	this.param = v;
	
	label.setText(param.getLabel());
	length.setValue((Integer)param.getLength());
	defaultText.setText(param.getText());
	if (param.isTitled() ) {
	    titled.setSelected(true);
	    title.setEditable(true);
	} else {
	    titled.setSelected(false);	
	    title.setEditable(false);
	}
	
	
	setComponentType(v.getJavaType());
        textFilter.setSelectedItem((String)param.getFilter());

	title.setText(param.getTitle());
	if ( param.getLayout().startsWith("0") ) layoutStandard.setSelected(true);
	if ( param.getLayout().startsWith("1") ) layoutRaised.setSelected(true);
	if ( param.getLayout().startsWith("2") ) layoutLowered.setSelected(true);

    }
    


    private void init() {
	this.setName("FieldPanelProperties");
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
	this.add(new JLabel("Length"),gbc);
	
	gbc.gridy = 2;
        gbc.anchor= GridBagConstraints.NORTHWEST;
	this.add(new JLabel("Default Text"),gbc);

        gbc.anchor= GridBagConstraints.WEST;
	gbc.gridy = 3;
	this.add(new JLabel("Titled"),gbc);
	
	gbc.gridy = 4;
	this.add(new JLabel("Filter"),gbc);


	gbc.gridy = 5;
	this.add(new JLabel("Title"),gbc);

	gbc.gridy = 6;
	this.add(new JLabel("Layout"),gbc);

	gbc.gridx = 1;
	gbc.gridy = 0;
	this.add(label,gbc);

	gbc.gridy = 1;
        gbc.fill  = GridBagConstraints.NONE;

        length   = new JSpinner(new SpinnerNumberModel(1, 500, 1, 1));	
	this.add(length,gbc);
		 
	gbc.gridy = 2;
	gbc.gridheight = 1;
        gbc.fill  = GridBagConstraints.HORIZONTAL;

	this.add(defaultText,gbc);

	gbc.gridy = 3;
	gbc.gridheight = 1;
	this.add(titled,gbc);
	titled.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (titled.isSelected() ) {
			title.setEditable(true);
		    } else {
			title.setEditable(false);
		    }
		}});

	gbc.gridy = 4;
	this.add(textFilter,gbc);

	gbc.gridy = 5;
	this.add(title,gbc);


	gbc.gridy = 6;
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

    String componentType;
    
    /**
       * Get the value of componentType.
       * @return value of componentType.
       */
    public String getComponentType() {return componentType;}
    
    /**
       * Set the value of componentType.
       * @param v  Value to assign to componentType.
       */
    public void setComponentType(String  v) {
	this.componentType = v;

	// according the type we set the FieldType and the Filter
	if ( v.toLowerCase().startsWith("string") ) {
	    setFilterType(textFilterComponents);
	}

	if ( v.toLowerCase().startsWith("int") ) {
	    setFilterType(numberFilterComponents);
	}
	if ( v.toLowerCase().startsWith("double") ) {
	    setFilterType(numberFilterComponents);
	}
	if ( v.toLowerCase().startsWith("float") )   { 
	    setFilterType(numberFilterComponents);
	}

	if ( v.toLowerCase().endsWith("date") ) {
	    textFilter.setEnabled(false);
	}

    }
    
    private void setFilterType(String[] s) {
	textFilter.setEnabled(true);
	textFilter.removeAllItems();
	for (int i=0; i < s.length;i++) {
	    textFilter.addItem(s[i]);
	}
	textFilter.setEditable(true);
	textFilter.setAlignmentX(Component.LEFT_ALIGNMENT);	
    }



    public static void main(String a[]) {
	CloseableFrame cf = new CloseableFrame("FieldPanelProps");
	FieldPanelProperties fpp = new FieldPanelProperties();
	cf.getContentPane().add(fpp);
	cf.pack();
	cf.setVisible(true);

    }
}
