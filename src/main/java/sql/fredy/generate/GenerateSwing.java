package sql.fredy.generate;

/** GenerateSwing, generates Java-Swing Code out of a table and is a part of Admin...
 *  Version 2.0 01. June 1999
 *  Fredy Fischer 
 *
 *  this has been created to query mySQL-Databases
 *  it only returns a JPanel, so it can easily been 
 *  used in different kind of windows
 *
 *  It uses Kai Toedter's JCalendar-Bean for displaying dates
 *
 * it has been replaced within the Framework by GenerateForm
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


import sql.fredy.metadata.SingleColumnInfo;
import sql.fredy.metadata.Columns;
import java.util.*;
import java.sql.*;




public class GenerateSwing {

// Fredy's make Version
private static String fredysVersion = "Version 1.4  2002-01-02";

public String getVersion() {return fredysVersion; }


    Vector v;
    
    String swingCode;
    
    /**
       * Set the value of swingCode.
       * @param v  Value to assign to swingCode.
       */
    public void setSwingCode(String  v) {this.swingCode = v;}
    
    private String firstUpper(String s) {
	s = s.substring(0,1).toUpperCase() + s.substring(1);
	return s;
    }

    private String toDay() {

	Calendar c = Calendar.getInstance();
   
	return  Integer.toString(c.get(Calendar.YEAR)) + "-" + 
	        Integer.toString(c.get(Calendar.MONTH)+1)+ "-" + 
                Integer.toString(c.get(Calendar.DATE));

    }

    /**
    private String getType(String s) {
	String t="String";

       if ( Integer.parseInt(s) ==  java.sql.Types.CHAR )          t = "String";
       if ( Integer.parseInt(s) ==  java.sql.Types.VARCHAR )       t = "String";
       if ( Integer.parseInt(s) ==  java.sql.Types.LONGVARCHAR )   t = "String";;
       if ( Integer.parseInt(s) ==  java.sql.Types.BINARY )        t = "String";;
       if ( Integer.parseInt(s) ==  java.sql.Types.LONGVARBINARY ) t = "String";
       if ( Integer.parseInt(s) ==  java.sql.Types.VARBINARY )     t = "String";
       if ( Integer.parseInt(s) ==  java.sql.Types.DATE )          t = "java.sql.Date";
       if ( Integer.parseInt(s) ==  java.sql.Types.TIME )          t = "String";
       if ( Integer.parseInt(s) ==  java.sql.Types.TIMESTAMP )     t = "String";
       if ( Integer.parseInt(s) ==  java.sql.Types.OTHER )         t = "String";
       if ( Integer.parseInt(s) ==  java.sql.Types.INTEGER )       t = "int";
       if ( Integer.parseInt(s) ==  java.sql.Types.DOUBLE )        t = "double";
       if ( Integer.parseInt(s) ==  java.sql.Types.FLOAT )         t = "float";
       if ( Integer.parseInt(s) ==  java.sql.Types.DECIMAL )       t = "float";
       if ( Integer.parseInt(s) ==  java.sql.Types.TIMESTAMP )     t = "java.sql.Timestamp";

       return t;
    }
    **/

     private String getType(int i) {
	String t="String";
        String s ;
	SingleColumnInfo sci = new SingleColumnInfo();
	sci = (SingleColumnInfo) allColumns.elementAt(i);
	

	if ( sci.getType_name().toLowerCase().startsWith("char") )          t = "String";
	if ( sci.getType_name().toLowerCase().startsWith("varchar") )       t = "String";
	if ( sci.getType_name().toLowerCase().startsWith("longvarchar") )   t = "String";
	if ( sci.getType_name().toLowerCase().startsWith("binary") )        t = "String";
	if ( sci.getType_name().toLowerCase().startsWith("longvarbinary") ) t = "String";
	if ( sci.getType_name().toLowerCase().startsWith("varbinary") )     t = "String";
	if ( sci.getType_name().toLowerCase().startsWith("date") )          t = "java.sql.Date";
	if ( sci.getType_name().toLowerCase().startsWith("time") )          t = "String";
	if ( sci.getType_name().toLowerCase().startsWith("timestamp") )     t = "String";
	if ( sci.getType_name().toLowerCase().startsWith("int") )           t = "int";
	if ( sci.getType_name().toLowerCase().startsWith("double") )        t = "double";
	if ( sci.getType_name().toLowerCase().startsWith("float") )         t = "float";
	if ( sci.getType_name().toLowerCase().startsWith("decimal") )       t = "float";
	if ( sci.getType_name().toLowerCase().startsWith("number") )        t = "float";

 
	   
       return t;
    }
  
    String database;
    
    /**
       * Get the value of database.
       * @return Value of database.
       */
    public String getDatabase() {return database;}
    
    /**
       * Set the value of database.
       * @param v  Value to assign to database.
       */
    public void setDatabase(String  v) {this.database = v;}
    
    
    String table;
    
    /**
       * Get the value of table.
       * @return Value of table.
       */
    public String getTable() {return table;}
    
    /**
       * Set the value of table.
       * @param v  Value to assign to table.
       */
    public void setTable(String  v) {this.table = v;}
    

    

    int lng;
    
    /**
       * Get the value of the length when to switch from TextField to TextArea.
       * @return Value of lng.
       */
    public int getLng() {return lng;}
    
    /**
       * Set the value of length when to switch from TextField to TextArea.
       * @param v  Value to assign to lng.
       */
    public void setLng(int  v) {this.lng = v;}
    

    int noRows;
    
    /**
       * Get the value of number of Rows in a TextArea.
       * @return Value of number of Rows in a TextArea.
       */
    public int getNoRows() {return noRows;}
    
    /**
       * Set the value of number of Rows in a TextArea.
       * @param v  Value to assign to number of Rows in a TextArea.
       */
    public void setNoRows(int  v) {this.noRows = v;}

    boolean oracle=false;
    
    /**
       * Get the value of oracle.
       * @return Value of oracle.
       */
    public boolean getOracle() {return oracle;}
    
    /**
       * Set the value of oracle.
       * @param v  Value to assign to oracle.
       */
    public void setOracle(boolean  v) {this.oracle = v;}

    private Vector theTable;
    private Vector allColumns;
    
    public GenerateSwing(Vector v, String db, Vector ac) {
	this.theTable = v;
	this.allColumns = ac;
	this.v = v;
	setDatabase(db);
	setLng(30);
	setNoRows(5);

    }

    public String getSwingCode() {

	Columns c = new Columns();
	c = (Columns) v.elementAt(0);
	setTable(c.getTable());

	swingCode = "package applications." + getDatabase() + ";\n\n" +
	    "/** this has been generated by Fredy's Admin-Tool for SQL-Databases\n" +
	    " *  this is a Swing-Form\n\n" +
	    " *  Date: " + toDay() + "\n" +
	    " * \n" +
	    " *  Database: " + getDatabase() + "\n" +
	    " *  Table:    " + getTable() + "\n" +
	    " *\n" +
	    " *  Admin is free software (MIT-License)\n *\n" +
	    " *  it uses Kai Toedter's JCalendar Bean\n *\n"+
	    " *\n" +
	    " *  Fredy Fischer\n" +
	    " *  Hulmenweg 36\n" +
	    " *  8405 Winterthur\n" +
	    " *  Switzerland\n" +
	    " *\n" +
	    " * sql@hulmen.ch\n" +
	    " *\n" +
	    " **/\n" +
	    "\n" +
	    "import java.awt.*;\n" +
	    "import java.awt.event.*;\n" +
	    "import java.util.*;\n" +
	    "import java.math.*;\n" +
	    "import javax.swing.*;\n" +
	    "import javax.swing.BorderFactory; \n" +
	    "import javax.swing.border.Border;\n" +
	    "import javax.swing.border.TitledBorder;\n" +
	    "import javax.swing.JLabel;\n" +
	    "import javax.swing.JPanel; \n" +
	    "import javax.swing.JFrame;\n" +
	    "import javax.swing.event.*;\n" +
	    "import javax.swing.text.*;\n" +
	    "import javax.swing.border.*;\n" +
	    "import java.awt.datatransfer.*;\n" +
	    "import applications.basics.ButtonPanel;\n" +
	    "import applications.basics.sqlTable;\n" +
	    "import applications.basics.CalendarField;\n" +
	    "import applications.basics.JTextFieldFilter;\n";

	swingCode = swingCode +
	    "\n" +
	    "public  class " + firstUpper(getTable()) + "Grid" + "  extends JTabbedPane {\n" +
	    "\n" +
	    "    public JTable list;\n" +
	    "    private JPanel     listPanel;\n" +
	    "    private JComboBox  search;\n" +
	    "    private JCheckBox  exact;\n" +
	    "    private JTextField searchString;\n" +
	    "\n";
	for (int i = 0; i < v.size(); i++) {
	    c = new Columns();
	    c = (Columns) v.elementAt(i);
	    if (getType(i).startsWith("String") && c.getLng() > getLng() ) {
		swingCode = swingCode + "    private JTextArea " + c.getName() + ";\n";
	    } else {
		if  (getType(i).startsWith("java.sql.Date")) {
		    	swingCode = swingCode + "    private CalendarField " + c.getName() + ";\n";
		} else {
		    swingCode = swingCode + "    private JTextField " + c.getName() + ";\n";
		}
	    }

	}
	    swingCode = swingCode + "    public ButtonPanel bp;\n" +
	    "\n" +
	    "\n" +
	    "\n" +
	    "    String ahost;\n" +
	    "  \n" +
	    "    /**\n" +
	    "       * Get the value of host.\n" +
	    "       * @return Value of host.\n" +
	    "       */\n" +
	    "    public String getAHost() {return ahost;}\n" +
	    "    \n" +
	    "    /**\n" +
	    "       * Set the value of host.\n" +
	    "       * @param v  Value to assign to host\n" +
	    "       */\n" +
	    "    public void setAHost(String  v) {this.ahost = v;}\n" +
	    "    \n" +
	    "    String auser;\n" +
	    "    \n" +
	    "    /**\n" +
	    "       * Get the value of user.\n" +
	    "       * @return Value of user.\n" +
	    "       */\n" +
	    "    public String getAUser() {return auser;}\n" +
	    "    \n" +
	    "    /**\n" +
	    "       * Set the value of user.\n" +
	    "       * @param v  Value to assign to user.\n" +
	    "       */\n" +
	    "    public void setAUser(String  v) {this.auser = v;}\n" +
	    "    \n" +
	    "    String apassword;\n" +
	    "    \n" +
	    "    /**\n" +
	    "       * Get the value of password.\n" +
	    "       * @return Value of password.\n" +
	    "       */\n" +
	    "    public String getAPassword() {return apassword;}\n" +
	    "    \n" +
	    "    /**\n" +
	    "       * Set the value of password.\n" +
	    "       * @param v  Value to assign to password.\n" +
	    "       */\n" +
	    "    public void setAPassword(String  v) {this.apassword = v;}\n" +
	    "\n" +
	    "    public " + firstUpper(getTable()) + "Grid (String ahost, String auser, String apassword) {\n" +
	    "\n" +
	    "	setAHost(ahost);\n" +
	    "	setAUser(auser);\n" +
	    "	setAPassword(apassword);\n" +
	    "\n" +
	    "	this.add(\"Form\",formPanel());\n" +
	    "	this.add(\"Search\",searchPanel());\n" +
	    "    }\n" + 
	    "\n" + 
	    "    private JPanel formPanel() {\n" + 
	    "\n";
	    int nowRows;
	    for (int i = 0; i < v.size(); i++) {
		c = new Columns();
		c = (Columns) v.elementAt(i);
	    if (getType(i).startsWith("String") && c.getLng() > getLng() ) { 
		nowRows = c.getLng()/getLng() + 1;
		if (nowRows > getNoRows()) nowRows = getNoRows();

	     	swingCode = swingCode + "    " + c.getName() + " = new JTextArea("+ Integer.toString(nowRows) + "," + Integer.toString(getLng()) +");\n";
		swingCode = swingCode + "    " + c.getName() + ".setLineWrap(true);\n";
	    } else {
		if  (getType(i).startsWith("java.sql.Date")) {
		    swingCode = swingCode + "    " + c.getName() + " = new CalendarField(\"" + firstUpper(c.getName()) +"\");\n";
		} else {
		swingCode = swingCode + "    " + c.getName() + " = new JTextField(\"\"," + Integer.toString(c.getLng()) +");\n";
		if ( getType(i).startsWith("int") || getType(i).startsWith("double") || getType(i).startsWith("float")  || getType(i).endsWith("Number") ) {
		    if  ( getType(i).startsWith("int") ) { 
			swingCode = swingCode + "    " + c.getName() + ".setDocument (new JTextFieldFilter(JTextFieldFilter.NUMERIC));\n";
		    } else {
			swingCode = swingCode + "    " + "JTextFieldFilter jtff" + Integer.toString(i) +" = new JTextFieldFilter(JTextFieldFilter.FLOAT);\n";
			swingCode = swingCode + "    " + "jtff" + Integer.toString(i) +".setNegativeAccepted(true);\n";
			swingCode = swingCode + "    " + c.getName() + ".setDocument(jtff" + Integer.toString(i) +");\n";
		    } 			

		}
		}
	    }
	    }
    
	    swingCode = swingCode +
	    "\n" + 
	    "	JPanel panel = new JPanel();\n" + 
	    "	panel.setLayout(new BorderLayout());\n" + 
	    "	bp = new ButtonPanel();	\n" + 
	    "	panel.add(\"South\",bp);\n" + 
	    "\n" + 
	    "	bp.insert.addActionListener(new ActionListener() {\n" + 
	    "	    public void actionPerformed(ActionEvent e) {\n" + 
	    "		insert(getRow());\n" + 
	    "		}});\n" + 
	    "	bp.delete.addActionListener(new ActionListener() {\n" + 
	    "	    public void actionPerformed(ActionEvent e) {\n" + 
	    "		delete(getRow());\n" + 
	    "		}});\n" + 
	    "	bp.update.addActionListener(new ActionListener() {\n" + 
	    "	    public void actionPerformed(ActionEvent e) {\n" + 
	    "		update(getRow());\n" + 
	    "		}});\n" + 
	    "	bp.clear.addActionListener(new ActionListener() {\n" + 
	    "	    public void actionPerformed(ActionEvent e) {\n" + 
	    "		clear();\n" + 
	    "		}});\n" + 
	    "	\n" + 
	    "\n" + 
	    "\n" + 
	    "	JPanel gridPanel = new JPanel();\n" + 
	    "	gridPanel.setLayout(new GridBagLayout());\n" + 
	    "\n" + 
	    "	GridBagConstraints gbc;\n" + 
	    "	Insets insets = new Insets(5,5,5,5);\n" + 
	    "        JScrollPane fScrollPane;\n" +
	    "\n\n"; 
	    for (int i = 0; i < v.size(); i++) {
		c = new Columns();
		c = (Columns) v.elementAt(i);
		swingCode = swingCode + 
	    "	gbc = new GridBagConstraints();\n" +
	    "	gbc.anchor=GridBagConstraints.NORTHEAST;\n" +
	    "	gbc.insets = insets;\n" +
	    "	gridPanel.add((JLabel) new JLabel(\"" + firstUpper(c.getName()) + "\"),gbc);\n" +
	    "	gbc = new GridBagConstraints ();\n" +
	    "	gbc.gridwidth = 0;\n" +
	    "	gbc.anchor=GridBagConstraints.WEST;\n" +
	    "	gbc.insets = insets;\n";
	    if (getType(i).startsWith("String") && c.getLng() > getLng() ) {
		swingCode = swingCode + "        fScrollPane = new JScrollPane(" +  c.getName() + ");\n";
		swingCode = swingCode + "        gridPanel.add(fScrollPane,gbc);\n";
	    } else {
		swingCode = swingCode + "        gridPanel.add(" + c.getName() + ",gbc);\n";
	    }
	    }
	    swingCode = swingCode + 
	    "\n" +
	    "	JScrollPane scrollpane = new JScrollPane(gridPanel);\n" +
	    "\n" +
	    "	panel.add(\"Center\",scrollpane);\n" +
	    "\n" +
	    "	return panel;\n" +
	    "\n" +
	    "    }\n" +
	    "\n" +
	    "\n" +
	    "    private JPanel searchPanel() {\n" +
	    "\n" +
	    "	final JPanel panel = new JPanel();\n" +
	    "	panel.setLayout(new BorderLayout());\n" +
	    "\n" +
	    "   final JPanel panel2 = new JPanel();\n" +
	    "	panel2.setLayout(new FlowLayout());\n" +
	    "\n" +
	    "	listPanel = new JPanel();\n" +
	    "\n" +
	    "	String[] items = {";

	    for (int i = 0; i < v.size(); i++) {
	        c = new Columns();
		c = (Columns) v.elementAt(i);
		swingCode = swingCode + "        \"" + c.getName() + "\",\n";
	    }
	    swingCode = swingCode + "	};\n" +
	    "	\n" +
	    "	search = new JComboBox(items);\n" +
	    "	exact  = new JCheckBox(\"exact search\");\n" +
	    "	exact.setSelected(true);\n" +
	    "	searchString = new JTextField(\"\",20);\n" +
	    "	JButton doIt = new JButton(\"Search\");\n" +
	    "	doIt.addActionListener(new ActionListener() {\n" +
	    "	    public void actionPerformed(ActionEvent e) {\n" +
	    "		String query = \"select * from " + getTable() + " where \";\n" +
	    "		String sel   = (String)search.getSelectedItem();\n" +
	    "		int    ind   = search.getSelectedIndex();\n" +
	    "		query = query + sel + \" \";\n" +
	    "		if (exact.isSelected()) {\n" +
	    "		   query = query + \"  = \";\n" +
	    "		} else { query  = query + \" like \"; }\n" +
	    "	        String s = searchString.getText();\n" +
	    "		switch (ind) {\n";
	    for (int i = 0; i < v.size(); i++) {
	        c = new Columns();
		c = (Columns) v.elementAt(i);
		String u = "\" \" ";
		if (getType(i).startsWith("String")) u = "\"'\"";
		swingCode = swingCode + 
	    "		case " + Integer.toString(i) + ":\n" +
	    "		    query = query + " + u + " + s + " + u + ";\n" +
	    "		    break;\n";
	    }
	    swingCode = swingCode + 
	    "		default:\n" +
	    "		    ;\n" +
	    "		}\n" +
	    "		query = query + \" order by \" + sel;\n" +
	    "		panel.remove(listPanel);\n" +
	    "           final sqlTable st = new sqlTable(getAHost(), getAUser(), getAPassword(),\"" + getDatabase() +"\",query);\n" +
	    "		JScrollPane scp = new JScrollPane(st);\n" +
	    "		Dimension d = new Dimension();\n" +
	    "		d = panel.getSize();\n" +
	    " 	        Integer intx = new Integer(d.width);\n" +
	    "	        Integer inty = new Integer(d.height);\n" +
	    "	        double fx = intx.floatValue() * 0.95;\n" +
	    "	        double fy = inty.floatValue() * 0.65;\n" +
	    "	        Double dfx = new Double(fx);\n" +
	    "	        Double dfy = new Double(fy);\n" +
	    "		d.setSize(dfx.intValue(),dfy.intValue());\n" +
	    "		st.tableView.setPreferredScrollableViewportSize(d);\n" +
	    "		listPanel = new JPanel();\n" +
	    "		listPanel.add(scp);\n" +
	    "		panel.add(\"South\",listPanel);\n" +
	    "		panel.updateUI();\n" +
	    "\n" +
	    "		// as well there is needed a List Selection Listener\n" +
	    "		st.tableView.getSelectionModel().addListSelectionListener(new ListSelectionListener() {\n" +
	    "		    public void valueChanged(ListSelectionEvent e) {\n";
	    swingCode = swingCode + 
	    "		" + firstUpper(getTable()) + "Row k = new " + firstUpper(getTable()) + "Row(getAHost(), getAUser(),getAPassword());\n" +
	    "\n" +
	    "			Integer i = new Integer(0);\n" +
	    "			Float f = new Float(0);\n" +
	    "			Double dbl = new Double(0);\n";

	    
	    for (int i = 0; i < v.size(); i++) {
	        c = new Columns();
		c = (Columns) v.elementAt(i);
		if (getType(i).startsWith("String")) swingCode = swingCode + "			k.set" + firstUpper(c.getName()) + "((String)st.tableView.getValueAt(st.tableView.getSelectedRow()," + Integer.toString(i) + "));\n";
		    
                if (getType(i).startsWith("int")) {
		    swingCode = swingCode + 
	    "			i = (Integer)st.tableView.getValueAt(st.tableView.getSelectedRow()," + Integer.toString(i) + ");\n" +
	    "			k.set" + firstUpper(c.getName()) + "(i.intValue());\n";
		}		   

                if (getType(i).startsWith("float")) {
		    swingCode = swingCode + 
	    "			f = (Float)st.tableView.getValueAt(st.tableView.getSelectedRow()," + Integer.toString(i) + ");\n" +
	    "			k.set" + firstUpper(c.getName()) + "(f.floatValue());\n";
		}

                if (getType(i).startsWith("double")) {
		    swingCode = swingCode + 
	    "			dbl = (Double)st.tableView.getValueAt(st.tableView.getSelectedRow()," + Integer.toString(i) + ");\n" +
	    "			k.set" + firstUpper(c.getName()) + "(dbl.doubleValue());\n";
		}

                if (getType(i).startsWith("java.sql.Date")) {
		    swingCode = swingCode +  "			k.set" + firstUpper(c.getName()) + "((java.sql.Date)st.tableView.getValueAt(st.tableView.getSelectedRow()," + Integer.toString(i) + "));\n";
		}
                if (getType(i).startsWith("java.sql.Timestamp")) {
		    swingCode = swingCode +  "			k.set" + firstUpper(c.getName()) + "((java.sql.Timestamp)st.tableView.getValueAt(st.tableView.getSelectedRow()," + Integer.toString(i) + "));\n";
		}		
	    }

	    swingCode = swingCode +
	    "\n" +
	    "\n" +
	    "			setRow(k);\n" +
	    "		    }});\n" +
	    "		}});\n" +
	    "	\n" +
	    "\n" +
	    "\n" +
	    "	panel2.add(search);\n" +
	    "	panel2.add(searchString);\n" +
	    "	panel2.add(exact);\n" +
	    "	panel2.add(doIt);\n" +
	    "	panel2.setBorder(BorderFactory.createEtchedBorder());\n" +
	    "\n" +
	    "	panel.add(\"North\",panel2);\n" +
	    "	panel.add(\"Center\",listPanel);\n" +
	    "	\n" +
	    "\n" +
	    "	return panel;\n" +
	    "\n" +
	    "    }\n" +
	    "\n" +
	    "\n" +
	    "    public " + firstUpper(getTable()) + "Row getRow() {\n\n" +
            "     " + firstUpper(getTable()) + "Row k = new " + firstUpper(getTable()) + "Row(getAHost(), getAUser(), getAPassword());\n\n";


	    for (int i = 0; i < v.size(); i++) {
	        c = new Columns();
		c = (Columns) v.elementAt(i);
		if (getType(i).startsWith("String")) swingCode = swingCode + "	k.set" + firstUpper(c.getName()) + "(" + c.getName() + ".getText());\n";
		    
                if (getType(i).startsWith("float")) {
		    swingCode = swingCode + 
            "	try {\n" +
            "	    k.set" + firstUpper(c.getName()) + "(Float.valueOf(" + c.getName() + ".getText()).floatValue());\n" +
            "	} catch (NumberFormatException e) { k.set" + firstUpper(c.getName()) + "(0); }\n" +
            "\n\n";

		}		   

                if (getType(i).startsWith("int")) {
		    swingCode = swingCode + 
            "	try {\n" +
            "	    k.set" + firstUpper(c.getName()) + "(Integer.parseInt(" + c.getName() + ".getText()));\n" +
            "	} catch (NumberFormatException e) { k.set" + firstUpper(c.getName()) + "(0); }\n" +
            "\n\n";

		}

                if (getType(i).startsWith("double")) {
		    swingCode = swingCode + 
            "	try {\n" +
            "	    k.set" + firstUpper(c.getName()) + "(Double.valueOf(" + c.getName() + ".getText()).doubleValue());\n" +
            "	} catch (NumberFormatException e) { k.set" + firstUpper(c.getName()) + "(0); }\n" +
            "\n\n";

		}

                if (getType(i).startsWith("java.sql.Date")) {
		    swingCode = swingCode + 
			"	try {\n" +
			"	    k.set" + firstUpper(c.getName()) + "(java.sql.Date.valueOf(" + c.getName() + ".getDate()));\n" + 
			"       } catch (Exception e) { " + c.getName() + ".setText(\"yyyy-mm-dd\");\n  }\n";
		}
		

	    }

	    swingCode = swingCode + "   return k;\n\n   }\n\n" +
            "\n" +
            "    public void insert(" + firstUpper(getTable()) + "Row k) { msg(k.insert()); }\n" +
            "    public void update(" + firstUpper(getTable()) + "Row k) { msg(k.update()); }\n" +
            "    public void delete(" + firstUpper(getTable()) + "Row k) { \n" +
            "	String string1 = \"Yes\";\n" +
            "	String string2 = \"No\";\n" +
            "	Object[] options = {string1, string2};\n" +
            "	int n = JOptionPane.showOptionDialog(null,\n" +
            "                              \"Do you really want to delete?\",\n " +
            "                              \"Delete  ?\",\n" +
            "                              JOptionPane.YES_NO_OPTION,\n" +
            "                              JOptionPane.QUESTION_MESSAGE,\n" +
            "                              null,     //don't use a custom Icon\n" +
            "                              options,  //the titles of buttons\n" +
            "                              string2); //the title of the default button\n" +
            "	     if (n == JOptionPane.YES_OPTION) {\n" +
            "		 String s = k.delete();\n" +
            "		 if (s.startsWith(\"ok\")) {\n" +
            "		     clear();\n" +
            "		 } else { msg(s);}\n" +
            "	     }\n" +
            "    }\n" +
            "\n" +
            "    private void msg(String s) {\n" +
            "	if (  ! s.startsWith(\"ok\") )  JOptionPane.showMessageDialog(null, s,\"Message\",JOptionPane.WARNING_MESSAGE);\n" +
            "    }\n\n";
	    swingCode = swingCode + 
            "    public void clear() {\n";
	    for (int i = 0; i < v.size(); i++) {
	        c = new Columns();
		c = (Columns) v.elementAt(i);
		swingCode = swingCode + "		" + c.getName() + ".setText(\"\");\n";
	    }
	    swingCode = swingCode +
            "    }\n\n" +
	    "    public void setRow(" + firstUpper(getTable()) + "Row k) {\n\n";
	    for (int i = 0; i < v.size(); i++) {
	        c = new Columns();
		c = (Columns) v.elementAt(i);
		if (getType(i).startsWith("String")) swingCode = swingCode + "	" + c.getName() + ".setText(k.get" + firstUpper(c.getName()) + "());\n";

		if (getType(i).startsWith("int")) swingCode = swingCode + "	" + c.getName() + ".setText(Integer.toString(k.get" + firstUpper(c.getName()) + "()));\n";

		if (getType(i).startsWith("float")) swingCode = swingCode + "	" + c.getName() + ".setText(Float.toString(k.get" + firstUpper(c.getName()) + "()));\n";

		if (getType(i).endsWith("Number")) swingCode = swingCode + "	" + c.getName() + ".setText(k.get" + firstUpper(c.getName()) + "().toString());\n";

		if (getType(i).startsWith("double")) swingCode = swingCode + "	" + c.getName() + ".setText(Double.toString(k.get" + firstUpper(c.getName()) + "()));\n";
	    
		if (getType(i).startsWith("java.sql.Date")) swingCode = swingCode + "	" + c.getName() + ".setDate(k.get" + firstUpper(c.getName()) + "().toString());\n";
	    }

	    swingCode = swingCode + "\n   }\n\n" +
	    "    public static void main(String args[]) {\n" +
	    "\n" +
	    "	if (args.length != 3) {\n" +
	    "	    System.out.println(\"Syntax: java applications." + getDatabase() + "." + firstUpper(getTable()) + "Grid host user password\");\n" +
	    "	    System.exit(0);\n" +
	    "	}\n" +
	    "	" + firstUpper(getTable()) + "Grid kg = new " + firstUpper(getTable()) + "Grid(args[0], args[1], args[2]);\n" +
	    "	JFrame frame = new JFrame(\"" + firstUpper(getTable()) + " Form\");\n" +
	    "	frame.getContentPane().setLayout(new BorderLayout());\n" +
	    "	frame.getContentPane().add(\"Center\",kg);\n" +
	    "	frame.addWindowListener(new WindowAdapter() {\n" +
	    "	    public void windowActivated(WindowEvent e) {}\n" +
	    "	    public void windowClosed(WindowEvent e) {}\n" +
	    "	    public void windowClosing(WindowEvent e) {System.exit(0);}\n" +
	    "            public void windowDeactivated(WindowEvent e) {}\n" +
	    "            public void windowDeiconified(WindowEvent e) {}\n" +
	    "            public void windowIconified(WindowEvent e) {}\n" +
	    "            public void windowOpened(WindowEvent e) {}});\n" +
	    "	kg.bp.cancel.addActionListener(new ActionListener() {\n" +
	    "	    public void actionPerformed(ActionEvent e) {\n" +
	    "		System.exit(0);\n" +
	    "		}});\n" +
	    "	frame.pack();\n" +
	    "	frame.setVisible(true);\n" +
	    "	\n" +
	    "    }\n" +
	    "}\n" ;
	    return swingCode;
    }
}
