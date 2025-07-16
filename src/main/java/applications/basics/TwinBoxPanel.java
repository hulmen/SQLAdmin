package applications.basics;

/** 



    TwinBoxPanel is an class representing a standard Column-GUI
    areound TwinBox consisting of:
    - JLabel
    - DBcomboBox
    - GridBagConstraints
    - Border
    - Foreground- and BackGroundColor
    

    it can be in the Form:
    1) Label: data
    2) a field having a titled Border with the Field-name


    Admin is a Tool around SQL-Databases to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

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


import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;


public class TwinBoxPanel extends JPanel {

    
    public TwinBox comboField;
    
    /**
       * Get the value of comboField.
       * @return value of comboField.
       */
    public TwinBox getComboField() {return comboField;}
    
    /**
       * Set the value of comboField.
       * @param v  Value to assign to comboField.
       */
    public void setComboField(TwinBox  v) {this.comboField = v;}
   

    /**
       * set the content of the comboField
       * @param v Value to set
       **/
    public void setText(String v) { 
	comboField.setText(v);
    }
    /**
       * get the content of the comboField
       **/   
    public String getText() { 
	    return comboField.getText(); 
    }
    
    public void clear() { comboField.setSelectedIndex(0); }


    public void setTitledBorder(String v) {   
	this.setBorder(new TitledBorder(v));
    }

    public void setRaisedTitledBorder(String v) {
	this.setBorder(new TitledBorder(new BevelBorder(BevelBorder.RAISED),v));
    }
    public void setLoweredTitledBorder(String v) {
	this.setBorder(new TitledBorder(new BevelBorder(BevelBorder.LOWERED),v));
    }

    
    public JLabel label;
    
    /**
       * Get the value of label.
       * @return value of label.
       */
    public String getLabel() {return label.getText();}
    
    /**
       * Set the value of label.
       * @param v  Value to assign to label.
       */
    public void setLabel(String  v) {this.label.setText(v);}



    GridBagConstraints gbcLabel;
    public GridBagConstraints getGbcLabel() { return gbcLabel; }
    public void setGbcLabel(GridBagConstraints v) { gbcLabel = v; }

    GridBagConstraints gbcText;
    public GridBagConstraints getGbcText() { return gbcText; }
    public void setGbcText(GridBagConstraints v) { gbcText = v; }


    public void setStandardConstraints() {
	Insets insets = new Insets(2,2,2,2);
	GridBagConstraints gbc = new GridBagConstraints();
	gbc.anchor= GridBagConstraints.EAST;
        gbc.fill  = GridBagConstraints.HORIZONTAL;
	gbc.weightx = 1.0;
	gbc.weighty = 1.0;
	gbc.gridwidth = 1;
	gbc.insets = insets;
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbcLabel = gbc;

	gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.BOTH;
	gbc.gridx  = 1;
	gbcText    = gbc;


  }



    /**
       * @param label    The Label text
       * @param text     The value to be selected
       * @param titled   true = display a titled border false no border
       * @param title    the title in the border
       * @param layout   0 = standard, 1 = RAISED, 2 = LOWERED)
       **/

    public TwinBoxPanel(String host, 
			String user,
			String password, 
			String database, 
			String query,
			String label,
			String text, 
			boolean titled, 
			String title,
			int layout) {
	init(host,user,password,database,query,label,text,titled,title,layout);
    }

    public TwinBoxPanel(String host, 
			String user,
			String password, 
			String database, 
			String query,
			String label) {
	init(host,user,password,database,query,label,null,false,null,0);
    }
    public TwinBoxPanel(String host, 
			String user,
			String password, 
			String database, 
			String query
			   ) {
	init(host,user,password,database,query,null,null,false,null,0);
     }
     
 
    private void init(String host, 
		      String user,
		      String password, 
		      String database, 
		      String query,
		      String label,
		      String text, 
		      boolean titled, 
		      String title,
		      int layout) {

	this.label = new JLabel(label);
	this.comboField = new TwinBox(host, user,password,database,query);

	if ( text  != null ) setText(text);

	this.setLayout(new GridBagLayout());
	gbcLabel = new GridBagConstraints();
	gbcText  = new GridBagConstraints();	

	if (getLabel() != null) this.add(this.label,gbcLabel);
	this.add(getComboField(),gbcText);

	if (titled) {
	    if ( layout == 1) setRaisedTitledBorder(title);
	    if ( layout == 2) setLoweredTitledBorder(title);
	    if ( layout == 0) setTitledBorder(title);
	}
    }
}
       
	    
	
    
