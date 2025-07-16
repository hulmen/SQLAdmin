package sql.fredy.datadrill;

/** this is a SQL-Parser for Fredy's Admin-Tool
    This parser replaces the content of a String,
    mostly a SQL-Statement, that contains values
    like @Name@ by the text it has been asked in a
    Grid.
    Example: select * from customer where name='@Name@'
             will open a window that asks for Name and
             feed this Info back to the SQl-Statement:
             select * from customer where name='fredy'

    (hope it will work)

    Version 1.0, 28. Dec. 1999
    Fredy Fischer

    HAS BEEN ADAPTED FOR DATADRILL to be a JPanel  

 *
 * This software is part of the Admin-Framework and free software
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


import sql.fredy.ui.TwinBox;
import sql.fredy.ui.DBcomboBox;
import sql.fredy.ui.DateButtonPanel;
import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.util.logging.*;
import java.net.*;
import java.io.*;


public class SqlParser extends JPanel {

    private Logger logger=Logger.getLogger("sql.fredy.datadrill");
    private Vector v;
    private String[] entry;
    private JPanel askPanel;
    private String query;
    private String shadowQuery;
    public JButton ok, cancel;
    public void setQuery(String s) { query=s;}
    public String getQuery() { return query; }
    public String getData() {
	parseIt();
	return getQuery(); 
    }
     

    String dateiName;
    /**
     * Get the value of dateiName, der zu ladenden Datei
     * @return value of dateiName.
     */
    public String getDateiName() {
	return dateiName;
    }
    
    /**
     * Set the value of dateiName, der zu ladenden Datei
     * @param v  Value to assign to dateiName.
     */
    public void setDateiName(String  v) {
	this.dateiName = getPath() + v;
	ReadFile rf = new ReadFile(getDateiName());
	setQuery(rf.getText());
	logger.log(Level.FINEST,"File read: " + this.dateiName);
	logger.log(Level.FINEST,"unparsed Query is: " + rf.getText());
    }
    

    private String getPath() {
	URL resource = SqlParser.class.getResource("." + java.io.File.separator + "resources");
        String top = resource.getFile().toString() + java.io.File.separator;
	try {
	    top = URLDecoder.decode(top,"UTF-8");
	} catch (UnsupportedEncodingException use) { 
	    logger.log(Level.WARNING,"Can not read directory");
	    logger.log(Level.INFO,"Error: " + use.getMessage());
	    top = "";
	}

	return top;

    }

    String parsed;
    
    /**
       * Get the value of parsed.
       * @return Value of parsed.
       */
    public String getParsed() {return parsed;}
    
    /**
       * Set the value of parsed.
       * @param v  Value to assign to parsed.
       */
    public void setParsed(String  v) {this.parsed = v;}
    



    public SqlParser(String fileName) {
 
	v = new Vector();      
	setDateiName(fileName);
	generateElements();
	this.setLayout(new BorderLayout());
	this.add("Center",ask());
	
    }
    
    
    
    private JPanel ask() {
	
	
	askPanel = new JPanel();
	askPanel.setLayout(new GridBagLayout());
	
	GridBagConstraints gbc;
	Insets insets = new Insets(2,2,2,2);
	gbc = new GridBagConstraints();
	gbc.weightx = 1.0;     
	gbc.insets = insets;
	
	int m = v.size();
	entry = new String[m];
	SqlParserObject s = new SqlParserObject();
	
	gbc.gridx = 0;
	gbc.gridy = -1;
	
	for (int i=0; i< v.size() ; i++ ) {
	    s = (SqlParserObject) v.elementAt(i);
	    gbc.gridy++;
	    gbc.gridx = 0;
	    gbc.anchor= GridBagConstraints.WEST;
	    gbc.fill  = GridBagConstraints.NONE;
	    askPanel.add(new JLabel(s.parameter),gbc);
	    
	    gbc.anchor= GridBagConstraints.WEST;
	    gbc.fill  = GridBagConstraints.NONE;
	    gbc.gridx = 1;

	    if ( s.getFieldType() == s.DATEFIELD ) 
                    askPanel.add((DateButtonPanel)s.getComponent(),gbc);

	    if ( s.getFieldType() == s.TEXTFIELD ) 
		askPanel.add((JTextField)s.getComponent(),gbc);

	    if ( s.getFieldType() == s.NUMBERFIELD ) 
		askPanel.add((JTextField)s.getComponent(),gbc);

	    if ( s.getFieldType() == s.COMBOBOX ) 
		askPanel.add((DBcomboBox)s.getComponent(),gbc);

	    if ( s.getFieldType() == s.TWINBOX ) 
		askPanel.add((TwinBox)s.getComponent(),gbc);
	}
	return askPanel;
    }
    
    
    private void generateElements() {
	
	StringTokenizer st = new StringTokenizer(getQuery(),"@@");
	
	while (st.hasMoreTokens()) {
	    try {
		st.nextToken();
		SqlParserObject spo = new SqlParserObject();
		String s = st.nextToken();
		spo.setOrigText(s);		
		logger.log(Level.FINEST,"Pattern: " + s);
		StringTokenizer sto = new StringTokenizer(s,";");
		String typ = sto.nextToken();
		logger.log(Level.FINEST,"Typ: " + typ);
		spo.parameter = sto.nextToken();  // Label
		String query = "";
		if (typ.toLowerCase().equals("combo") ) query = sto.nextToken();
		if (typ.toLowerCase().equals("twin")  ) query = sto.nextToken();
		logger.log(Level.FINEST,"Query: " + query);

		spo.setTyp(typ);
		spo.setQuery(query);
		spo.setComponent();
		
		v.addElement(spo);
	    } catch (NoSuchElementException excp) { ; }
	    
     }

    }


    public void parseIt() {
	
	SqlParserObject s = new SqlParserObject();
	for (int i=0; i< v.size() ; i++ ) {
	    s = (SqlParserObject) v.elementAt(i);
	    parse("@"+s.getOrigText()+"@",s.getText());
	}
	
    }
    
    public void parse(String e, String element) {
	String r1, r2;
        String line = getQuery();
	int i;
	i = line.indexOf(e);
        if ( i > 0 ) {
	    r1 = line.substring(0,i);
	    r2 = line.substring(i+e.length());
	    line=r1+element+r2;
	}
	logger.log(Level.FINEST,"Parsed line: " + line);
	setQuery(line);
	setParsed(line);
    }
    
}
