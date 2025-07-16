package sql.fredy.generate;

/** FieldProperties is to modify the Properties of the Fields within the
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
import sql.fredy.ui.ImageButton;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.border.*;

public class FieldProperties extends JPanel {

    private String[] textFieldComponents =  {  "FieldPanel",
					       "AreaPanel",
					       "DBcomboBoxPanel",
					       "TwinBoxPanel" };

    private String[] numberFieldComponents =   { "FieldPanel",
						 "DBcomboBoxPanel",
						 "TwinBoxPanel" };

    private String[] dateFieldComponents   = { "DateButtonPanel","DateFieldPanel" };
    private JLabel       name           = new JLabel();
    private JLabel       type           = new JLabel();

    private JComboBox  fieldType   = new JComboBox();

    private ActionListener fieldTypeActionListener;


   // for test purpose

    private String[] dataTypeComponents = { "String",
					    "int",
					    "java.sql.Date",
					    
					    "float",
					    "double"
    };


    private JComboBox dataType = new JComboBox(dataTypeComponents);
  
    String componentName="none";
    
    /**
       * Get the value of componentName.
       * @return value of componentName.
       */
    public String getComponentName() {return componentName;}
    
    /**
       * Set the value of componentName.
       * @param v  Value to assign to componentName.
       */
    public void setComponentName(String  v) {
	this.componentName = v;
	name.setText(v);
	name.updateUI();
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
	int ind = 0;

	// according the type we set the FieldType and the Filter
	if ( v.toLowerCase().startsWith("string") ) {
	    setFieldType(textFieldComponents);
	    ind = 0;
	}
	if ( v.toLowerCase().startsWith("int") ) {
	    setFieldType(numberFieldComponents);
	    ind = 1;
	}
	if ( v.toLowerCase().startsWith("double") ) {
	    setFieldType(numberFieldComponents);
	    ind = 4;
	}
	if ( v.toLowerCase().startsWith("float") )   { 
	    setFieldType(numberFieldComponents);
	    ind = 3;
	}

	if ( v.toLowerCase().endsWith("date") ) {
	    setFieldType(dateFieldComponents);
	    ind = 2;
	}
	dataType.setSelectedIndex(ind);
    }
    

    private void setFieldType(String[] s) {
	fieldType.removeActionListener(fieldTypeActionListener);
	fieldType.removeAllItems();
	for (int i=0; i < s.length;i++) {
	    fieldType.addItem(s[i]);
	}
	fieldType.addActionListener(fieldTypeActionListener);
    }



    public GridBagPanel gbp,labelGbc;
    private JPanel middlePanel,fieldGBCpanel,labelGBCpanel;



    
    ComponentTreeNodeObject ctno = new ComponentTreeNodeObject();
    
    /**
       * Get the value of ctno.
       * @return value of ctno.
       */
    public ComponentTreeNodeObject getCtno() {

	ctno.setGuiType((String)fieldType.getSelectedItem());
	ctno.setLabelGbc(new GridBagObject(labelGbc.getGBC()));
	ctno.setGbc(new GridBagObject(gbp.getGBC()));
	ctno.setParameter(getFieldTypeParameter());

	return ctno;
    }
    
    /**
       * Set the value of ctno.
       * @param v  Value to assign to ctno.
       */
    public void setCtno(ComponentTreeNodeObject  v) {
	this.ctno = v;

	// first we do the Data-Type Properties
	setComponentName(ctno.getName());
	setComponentType(ctno.getType());

	// what GUI-Type is selected
	fieldType.setSelectedItem((String)ctno.getGuiType());
	  	
	// then we do the Label Properties
	labelGbc.setGBC((GridBagConstraints)ctno.getLabelGbc().getGbc());

	// then we do the Component GBC
	gbp.setGBC((GridBagConstraints)ctno.getGbc().getGbc());
    }
    

    
    XMLTreeNode xn = new XMLTreeNode();
    
    /**
       * Get the value of xn.
       * @return value of xn.
       */
    public XMLTreeNode getXn() {

	xn.setNode((ComponentTreeNodeObject) this.getCtno());
	return xn;
    }
    
    /**
       * Set the value of xn.
       * @param v  Value to assign to xn.
       */
    public void setXn(XMLTreeNode  v) {
	this.xn = v;
	setCtno((ComponentTreeNodeObject)xn.getNode());

    }
    



    public FieldProperties() {
	doIt();
    }


    public FieldProperties(XMLTreeNode xn) {
	doIt();
	this.setXn(xn);
	this.updateUI();
    }

    public void doIt() {
	this.setLayout(new GridBagLayout());
	//this.setBorder(new TitledBorder(new EtchedBorder(),"Component Properties"));


	this.setName("FieldProperties");

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

       
	// init all the things.....
	init();
	this.add(upperPart(),gbc);

	gbc.gridy =1;
	this.add(tabbedPane(),gbc);

	//	gbc.gridy = 2;
	//this.add(applyPanel(),gbc);
       
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
    

    private JTabbedPane tabbedPane() {
	JTabbedPane panel = new JTabbedPane();
	panel.setName("tabbedPane");
	panel.add("Field-Properties",middlePanel);
	panel.add("Field-Constraints",fieldGBCpanel);
	panel.add("Label-Constraints",labelGBCpanel);
	
	return panel;

    }


    private void init() {
	middlePart();
	
	fieldGBCpanel = new JPanel();
	fieldGBCpanel.setLayout(new BorderLayout());

	labelGBCpanel = new JPanel();
	labelGBCpanel.setLayout(new BorderLayout());

	gbp = new GridBagPanel("Field GBC");
	labelGbc = new GridBagPanel("LabelGBC");

	fieldGBCpanel.add(BorderLayout.CENTER,gbp);
	labelGBCpanel.add(BorderLayout.CENTER,labelGbc);



	fieldTypeActionListener  = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    String s = (String)fieldType.getSelectedItem();
		    //removeComponentPanel();
		    middlePanel.removeAll();
		    try {
			if (s.startsWith("FieldPanel") )      middlePanel.add(BorderLayout.CENTER,fieldPanel());
			if (s.startsWith("AreaPanel") )       middlePanel.add(BorderLayout.CENTER,areaPanel());
			if (s.startsWith("DBcomboBoxPanel") ) middlePanel.add(BorderLayout.CENTER,dbComboBoxPanel());
			if (s.startsWith("TwinBoxPanel") )    middlePanel.add(BorderLayout.CENTER,dbComboBoxPanel());
			if (s.startsWith("DateButtonPanel") ) middlePanel.add(BorderLayout.CENTER,dateFieldPanel());
			if (s.startsWith("DateFieldPanel") )  middlePanel.add(BorderLayout.CENTER,dateFieldPanel());
		    } catch (Exception strangeException) { 
			System.out.println("Strange exception at FieldTypeActionListener " + s);
			strangeException.printStackTrace();
		    }
	
		    middlePanel.updateUI();		   
		    update();
		    }};
	




    }


    private JPanel upperPart() { 
	
	JPanel panel = new JPanel();

	panel.setBorder(new TitledBorder(new EtchedBorder(),"Data-Type Properties"));
	panel.setName("Data-Type Properties");
	panel.setLayout(new GridBagLayout());
		
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
	panel.add(new JLabel("Data Type"),gbc);

	gbc.gridy = 2;
	panel.add(new JLabel("FieldType"),gbc);


	gbc.gridx = 1;
	gbc.gridy = 0;
	panel.add(name,gbc);
	
        gbc.gridy = 1;
	dataType.setEnabled(false);
	panel.add(dataType,gbc);
	dataType.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    setComponentType((String)dataType.getSelectedItem());
		    }});
	

	gbc.gridy = 2;
	panel.add(fieldType,gbc);
	fieldType.addActionListener(fieldTypeActionListener);
	return panel;
    }





    private void update() { this.updateUI(); }

    private void removeComponentPanel() {

	for (int i = 0; i<= this.getComponentCount(); i++ ) {
	    try {
		if (middlePanel.getComponent(i).getName().endsWith("PanelProperties") ) {
		    System.out.println("Removing " + middlePanel.getComponent(i).getName());
		    this.remove(middlePanel.getComponent(i));
		}
	    }  catch (Exception e) { ; }
	}

    }


    private void  middlePart() {
	middlePanel = new JPanel();
	//middlePanel.setBorder(new TitledBorder(new EtchedBorder(),"Field-Type Properties"));
	middlePanel.setName("Field-Type Properties");
	middlePanel.setLayout(new BorderLayout());
	middlePanel.add(BorderLayout.CENTER,fieldPanel());
    }

    private JPanel fieldPanel() {
	try {
	    FieldPanelProperties fpp = new FieldPanelProperties(ctno.getParameter());
	   
	    return fpp;
	} catch (Exception e) {
	    FieldPanelProperties fpp = new FieldPanelProperties();
	    return fpp;
	}

    }

    private JPanel areaPanel() {
	try {
	    AreaPanelProperties app = new AreaPanelProperties(ctno.getParameter());
	    return app;
	} catch (Exception e) {
	    AreaPanelProperties app = new AreaPanelProperties();
	    return app;
	}
    }

    private JPanel dbComboBoxPanel() {

	try {
	    DbBoxPanelProperties dpp = new DbBoxPanelProperties(ctno.getParameter());
	    return dpp;
	} catch (Exception e) {
	    DbBoxPanelProperties dpp = new DbBoxPanelProperties();
	    return dpp;
	}
    }


    private JPanel dateFieldPanel() {
	try {
	    DateButtonPanelProperties dbpp = new DateButtonPanelProperties(ctno.getParameter());
	    return dbpp;
	} catch (Exception e) {
	    DateButtonPanelProperties dbpp = new DateButtonPanelProperties();
	    return dbpp;
	}
    }




    public FieldTypeParameters getFieldTypeParameter() {

        FieldTypeParameters fp = new FieldTypeParameters();
 	for (int i = 0; i<= middlePanel.getComponentCount(); i++ ) {
	    try {	       
		if (middlePanel.getComponent(i).getName().startsWith("FieldPanelProperties") ) {
		    FieldPanelProperties fpp = (FieldPanelProperties)middlePanel.getComponent(i);
		    fp = fpp.getParam();
		}
		if (middlePanel.getComponent(i).getName().startsWith("AreaPanelProperties") ) {
		    AreaPanelProperties app = (AreaPanelProperties)middlePanel.getComponent(i);
		    fp = app.getParam();
		}
		if (middlePanel.getComponent(i).getName().startsWith("DateButtonPanelProperties") ) {
		    DateButtonPanelProperties dpp = (DateButtonPanelProperties)middlePanel.getComponent(i);
		    fp = dpp.getParam();
		}
		if (middlePanel.getComponent(i).getName().startsWith("DbBoxPanelProperties") ) {
		    DbBoxPanelProperties dbp = (DbBoxPanelProperties)middlePanel.getComponent(i);
		    fp = dbp.getParam();
		}
		
	    }  catch (Exception e) { 
		//e.printStackTrace();
	    }
	}      



	return fp;
    }




    public static void main(String args[]) {

	CloseableFrame  cf = new CloseableFrame("TEST");
	FieldProperties fp = new FieldProperties();
	cf.getContentPane().add(fp);
	cf.pack();
	cf.setVisible(true);
    }
}	
	    
