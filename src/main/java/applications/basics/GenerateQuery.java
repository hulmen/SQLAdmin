package applications.basics;

/** GenerateQuery: generates a query around a Table and is used in Generated Applications
 *  Version 1.0 29. August 2001
 *  Fredy Fischer 
 *
 *  this has been created to query SQL-Databases
 *  it only returns a JPanel, so it can easily been 
 *  used in different kind of windows
 */


/** Admin is a Tool around SQL databases to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and  other usefull things in DB-arena    

		       Fredy Fischer
		       Hulmenweg 36
		       8405 Winterthur
		       Switzerland

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
import javax.swing.text.*;
import javax.swing.border.*;

public class GenerateQuery extends BasicPanel {

// Fredy's make Version
private static String fredysVersion = "Version 1.3  2001-8-29 10:7";

public String getVersion() {return fredysVersion; }
  

    private Vector columnName;
    private short[] columnType;
    private int[] columnLength;
    private JTextField[] conditionValue;
    private JComboBox[]  conditionType;

    public ImageButton search;

    String query;
    
    /**
       * Set the value of query.
       * @param v  Value to assign to query.
       */
    public void setQuery(String  v) {this.query = v;}
    
    /**
       * Get the value of query.
       * @return value of query.
       */
    public String getQuery() {
    
    	query = "Select ";
	
	// first fill in the fields to display
	for (int i = 0; i < columnName.size(); i++) {
	    if ( query.length() > 7 ) query = query + ",\n\t";
	    query = query + getTable() + "." + (String)columnName.elementAt(i);
	}
	

	query = query + "\nfrom " +getTable() + "\n";
	if (checkConditions()) {
	    query = query + "\nwhere\n\t " + generateConditions();
	}

	return query;
    }


    private boolean checkConditions() {
	boolean v=false;
	for (int i = 0; i < conditionValue.length; i++) {
	    if ( conditionValue[i].getText().length() > 0 ) v = true;
	}
	return v;	
    }

    private String generateConditions() {
	String s = "";
	for (int i = 0; i < conditionValue.length; i++) {
	    if ( conditionValue[i].getText().length() > 0 ) {
		if ( s.length() > 0 ) s = s + "\nand\n\t ";
	        if ( conditionType[i].getSelectedItem().toString().startsWith("equal to")) 
		    s = s + (String)columnName.elementAt(i) + " = " + fieldType(i) + 
			conditionValue[i].getText() + fieldType(i);


	        if ( conditionType[i].getSelectedItem().toString().startsWith("not equal to"))
		    s = s + (String)columnName.elementAt(i) + " != " + fieldType(i) + 
			conditionValue[i].getText() + fieldType(i); 

	        if ( conditionType[i].getSelectedItem().toString().startsWith("smaller than or equal"))
		    s = s + (String)columnName.elementAt(i) + " <= " + fieldType(i) + 
			conditionValue[i].getText() + fieldType(i);

	        if ( conditionType[i].getSelectedItem().toString().endsWith("smaller than"))
		    s = s + (String)columnName.elementAt(i) + " < " + fieldType(i) 
			+ conditionValue[i].getText() + fieldType(i); 

	        if ( conditionType[i].getSelectedItem().toString().startsWith("bigger than or equal"))
		    s = s + (String)columnName.elementAt(i) + " >= " + fieldType(i) +
			conditionValue[i].getText() + fieldType(i);

	        if ( conditionType[i].getSelectedItem().toString().endsWith("bigger than")) 
		    s = s +  (String)columnName.elementAt(i) + " > " + fieldType(i) + 
			conditionValue[i].getText() + fieldType(i);

	        if ( conditionType[i].getSelectedItem().toString().startsWith("starts with"))
		    s = s +  (String)columnName.elementAt(i) + " like " + fieldType(i) +
			conditionValue[i].getText() + "%" + fieldType(i);

	        if ( conditionType[i].getSelectedItem().toString().startsWith("ends with")) 
		    s = s + (String)columnName.elementAt(i) + " like %" + fieldType(i) +
			conditionValue[i].getText() + fieldType(i);

	        if ( conditionType[i].getSelectedItem().toString().startsWith("contains"))
		    s = s + (String)columnName.elementAt(i) + " like " + fieldType(i) +"%" +
			conditionValue[i].getText() + "%" + fieldType(i); 
	}
	}
	return s;
    }

    
    private String fieldType(int i) {

       String ft=" ";
       if ( columnType[i] ==  java.sql.Types.CHAR ) ft="'";
       if ( columnType[i] ==  java.sql.Types.VARCHAR ) ft="'";
       if ( columnType[i] ==  java.sql.Types.LONGVARCHAR )    ft="'";
       if ( columnType[i] ==  java.sql.Types.BINARY )  ft="'";
       if ( columnType[i] ==  java.sql.Types.LONGVARBINARY )   ft="'";
       if ( columnType[i] ==  java.sql.Types.VARBINARY ) ft="'";
       if ( columnType[i] ==  java.sql.Types.DATE ) ft="'";
       if ( columnType[i] ==  java.sql.Types.TIME ) ft ="'";
       if ( columnType[i] ==  java.sql.Types.TIMESTAMP ) ft = "'";
       if ( columnType[i] ==  java.sql.Types.OTHER ) ft = "'";
       return ft;

    }

     private String firstUpper(String s) {
        s = s.substring(0,1).toUpperCase() + s.substring(1);
        return s;
    }

    private String makeLength(String s, int l) {
        for (int i = s.length();i <=l;i++) s = s + " ";
        return s;
    }


    // first init the fields
    private void init() {

	DbInfo dbi = new DbInfo(getHost(),getUser(),getPassword(),getDatabase());
	
	columnName = new Vector();
	columnName = dbi.getColumnNames(getTable());
    
	columnType     = new short[columnName.size()];
	columnLength   = new int[columnName.size()];
	conditionValue = new JTextField[columnName.size()];
	conditionType  = new JComboBox[columnName.size()];


	for (int i=0;i < columnName.size(); i++ ) {	   
	    SingleColumnInfo sci = dbi.getColumnInfo(getDatabase(),getTable(),(String)columnName.elementAt(i));
	    columnType[i] = sci.getData_type();
	}

    }


    private JPanel selectionPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new GridBagLayout());
	panel.setBorder(new EtchedBorder());


	GridBagConstraints gbc;
	Insets insets = new Insets(5,5,5,5);
	gbc = new GridBagConstraints();
	gbc.insets = insets;


	// first we do all the Labels
	gbc.anchor= GridBagConstraints.EAST;
	gbc.fill  = GridBagConstraints.HORIZONTAL;
	gbc.gridx = 0;
	for (int i=0;i < columnName.size(); i++ ) {	 
	    gbc.gridy = i;
	    panel.add(new JLabel(firstUpper((String)columnName.elementAt(i))),gbc);
	}
	    
	// then we do the what?
	String[] items = { "equal to",
			   "not equal",
			   "smaller or equal",
			   "smaller",
			   "bigger or equal",
			   "bigger",
			   "starts with",
			   "ends with",
			   "contains" };
	  for (int j = 0; j < conditionType.length; j++) {
	       conditionType[j] = new JComboBox(items);	       
	       conditionValue[j] = new JTextField(10);
	       conditionValue[j].setText(null);
	       gbc.gridx = 1;
	       gbc.gridy = j;
	       gbc.fill  = GridBagConstraints.HORIZONTAL;
	       panel.add(conditionType[j],gbc);
	       gbc.gridx = 2;
	       gbc.fill  = GridBagConstraints.BOTH;
	       panel.add(conditionValue[j],gbc);
	  }

	  return panel;
    }

    
    private JPanel buttonPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new GridBagLayout());
	panel.setBorder(new BevelBorder(BevelBorder.LOWERED));

	GridBagConstraints gbc;
	Insets insets = new Insets(1,1,1,1);
	gbc = new GridBagConstraints();
	gbc.insets = insets;

	gbc.anchor= GridBagConstraints.CENTER;
	gbc.fill  = GridBagConstraints.HORIZONTAL;
	gbc.gridx = 0;	
	gbc.gridy = 0;

	search = new ImageButton(null,"search.gif","Search these data");
	panel.add(search,gbc);


	ImageButton extract = new ImageButton(null,"extractdata.gif","Export this query");
	extract.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    final ExportQuery eq = new ExportQuery(getHost(),getUser(),getPassword(),getDatabase(),getQuery(),"file.csv",true);
		    eq.selectFile();		    
		    eq.cancel.addActionListener(new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
				eq.frame.dispose();
			    }});	  
		    eq.frame.addWindowListener(new WindowAdapter() {
			    public void windowActivated(WindowEvent e) {}
			    public void windowClosed(WindowEvent e) {}
			    public void windowClosing(WindowEvent e) {eq.frame.dispose();}
			    public void windowDeactivated(WindowEvent e) {}
			    public void windowDeiconified(WindowEvent e) {}
			    public void windowIconified(WindowEvent e) {}
			    public void windowOpened(WindowEvent e) {}});	  
		}});
	
	gbc.gridx = 1;
	panel.add(extract,gbc);
	
       
			
	return panel;

    }

    public GenerateQuery(String host, String user, String password,String database,String table) {
	super(host,user,password,database,table);
	this.setLayout(new BorderLayout());
	init();

	this.add(BorderLayout.CENTER,selectionPanel());
	this.add(BorderLayout.SOUTH,buttonPanel());


    }
    public static void main(String args[] ) {
	CloseableFrame cf = new CloseableFrame("A Test");
	GenerateQuery gq = new GenerateQuery(args[0],args[1],args[2],args[3],args[4]);
	cf.getContentPane().add(gq);
	cf.pack();
	cf.setVisible(true);
    }
}
