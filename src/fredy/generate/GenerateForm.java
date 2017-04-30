package sql.fredy.generate;
/*
 * This is a FormGenerator, generating a simple SwingForm around a Table
 * base is the XML-Document generated by GenerateXML and edited by XMLEditor
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
 */
import sql.fredy.share.t_connect;
import java.io.*;
import java.util.List;
import java.util.Iterator;
import java.util.Calendar;
import java.util.logging.Level;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.input.DOMBuilder;
import org.jdom.Namespace;

public class GenerateForm {

    String applicationsDirectory = "";
    public void setApplicationsDirectory(String v) { applicationsDirectory = v; }
    public String getApplicationsDirectory() { return applicationsDirectory; }

    private Document doc;
    private String masterPanel="";
    private String otherPanels="";
    private String listPanel  ="";
    private String dcls       ="";
    private String getRow     ="";
    private String setRow     ="";
    private String clearRow   ="";


    t_connect con=null;
    
    /**
     * Get the value of con.
     * @return value of con.
     */
    public t_connect getCon() {
        if ( con == null) {
	    con = new t_connect(getHost(),
				getUser(),
				getPassword(),
				getDatabase());
	    if ( ! con.acceptsConnection() ) con = null;
	}
	return con;
    }
    
    /**
     * Set the value of con.
     * @param v  Value to assign to con.
     */
    public void setCon(t_connect  v) {
	this.con = v;
	setUser(con.getUser());
	setHost(con.getHost());
	setPassword(con.getPassword());
	setDatabase(con.getDatabase());
    }
    
    
    String host;
    
    /**
       * Get the value of host.
       * @return value of host.
       */
    public String getHost() {return host;}
    
    /**
       * Set the value of host.
       * @param v  Value to assign to host.
       */
    public void setHost(String  v) {this.host = v;}
    
    
    String user;
    
    /**
       * Get the value of user.
       * @return value of user.
       */
    public String getUser() {return user;}
    
    /**
       * Set the value of user.
       * @param v  Value to assign to user.
       */
    public void setUser(String  v) {this.user = v;}
    
    
    String password;
    
    /**
       * Get the value of password.
       * @return value of password.
       */
    public String getPassword() {return password;}
    
    /**
       * Set the value of password.
       * @param v  Value to assign to password.
       */
    public void setPassword(String  v) {this.password = v;}
    

    
    String schema;
    
    /**
       * Get the value of schema.
       * @return value of schema.
       */
    public String getSchema() {return schema;}
    
    /**
       * Set the value of schema.
       * @param v  Value to assign to schema.
       */
    public void setSchema(String  v) {this.schema = v;}
    

    


    private void initPanels() {
	masterPanel="public JScrollPane formPanel() {\n"+
                             "   JPanel panel = new JPanel();\n" +
                             "   panel.setLayout(new GridBagLayout());\n" +
                             "   GridBagConstraints gbc = new GridBagConstraints();\n";

	listPanel="public JScrollPane listPanel() {\n";

	otherPanels="";
	dcls       ="";


    }


    private void initForRows() {
	

	/** 
	 * generating the getRow() and setRow()  Methods,
	 * getRow returns the [TableName]Row-Object from the Text
	 * setRow puts the content into the Form coming from the [TableName]Row-Object
	 **/
	getRow     = "   " + firstUpper(getTable()) + "Row row;\n\n" +
	    "    /**\n" +
	    "       * get the Value of the actual row as Row-Object\n" + 
	    "       * @return value of the Row-Object.\n" +
	    "       */\n" +
	    "    public " +  firstUpper(getTable()) + "Row getRow() {\n" +
	    "\n" +
	    "        row = new " +  firstUpper(getTable()) + "Row(getCon());\n";


	setRow     = 
	    "    /**\n" +
	    "       * set the form with values from the Row-Object\n" + 
	    "       * @param v Value of the Row-Object.\n" +
	    "       */\n" +
	    "    public void setRow(" + firstUpper(getTable()) + "Row v) {\n";


	clearRow  = 
	    "    /**\n" +
	    "       * Clear the form\n" + 
	    "       */\n" +
	    "    public void clear() {\n";

    }

  private String header;
  private String getHeader() {

      header = "package applications." + getDatabase() + ";\n\n" + 
	  "/** this has been generated by Fredy's Admin-Tool for SQL-Databases\n" +
	  " *  Date: " + toDay() + "\n" +
	  " * \n" +
	  " *  Database: " + getDatabase() + "\n" +
	  " *  Table:    " + getTable()  + "\n\n *\n" +
	  " *  Admin is free software (MIT License)\n" +
	  " *\n" +
	  " *  Fredy Fischer\n" +
	  " *  Hulmenweg 36\n" +
	  " *  8405 Winterthur\n" +
	  " *  Switzerland\n" +
	  " *\n" +
	  " *  sql@hulmen.ch\n" +
          " *  generated with Version: 16.02.2011\n" +
	  " *\n" +
	  " **/\n" +
	  "\n" +
	  "import applications.basics.*;\n\n" + 
	  "import java.awt.*;\n" +
	  "import java.awt.event.*;\n" +
	  "import java.awt.datatransfer.*;\n" +
	  "import java.util.*;\n" +
          "import java.util.logging.*;\n" +
	  "import javax.swing.*;\n" +
	  "import javax.swing.BorderFactory; \n" +
	  "import javax.swing.border.*;\n" +
	  "import javax.swing.event.*;\n" +
          "import javax.swing.event.ListSelectionEvent;\n" +
          "import javax.swing.event.ListSelectionListener;\n" +
	  "import javax.swing.text.*;\n" +
	  "\n" +
	  "public  class " + firstUpper(getTable()) + "Form" + "  extends BasicGrid {\n" +
	  "\n\n"+
          "       private Logger logger = Logger.getLogger(\"applications." + getDatabase() + "\");\n\n" ;
      return header;
  }

  private String getFooter() {
    String footer = "\npublic " + firstUpper(getTable()) + "Form(String h, String u, String p, String d, String t) {\n" +
                    "       super(h,u,p,d,t);\n" +
                    "}\n" +
                    "\npublic " + firstUpper(getTable()) + "Form(String d, String t) {\n" +
                    "       super(d,t);\n" +
                    "}\n" +
	            "\npublic " + firstUpper(getTable()) + "Form(t_connect con, String t) {\n" +
                    "       super(con,t);\n" +
                    "}\n" +

                    "\npublic static void main(String args[]) {\n" +
                    "       String host     = null;\n" +
                    "       String user     = null;\n" +
            	    "       String password = null;\n" + 
	            "       String table    = \"" + getTable() + "\";\n" +
 	            "       String database = \"" + getDatabase() + "\";\n" +
                    "       System.out.println(\"Syntax: java applications." + getDatabase() + "." + firstUpper(getTable()) + "Form" +
                    " -u [user] -p [password] -h [host]\");\n" +
                    "       final " + firstUpper(getTable()) + "Form form;\n" +
                    "       if (args.length < 6) { \n" +
	            "          form = new " +firstUpper(getTable()) + "Form(database,table);\n" +
         	    "       } else {\n" +
	            "          int i = 0;\n"  +
  		    "           while ( i < args.length) {\n" +
 		    "               if (args[i].equals(\"-h\")) {\n" +
		    " 	               i++;\n" +
		    " 	               host = args[i];\n" +
		    "               }\n" +
		    "               if (args[i].equals(\"-u\")) {\n" +
		    " 	               i++;\n" +
		    "	               user = args[i];\n" +
		    "              }\n" +
		    "              if (args[i].equals(\"-p\")) {\n" +
		    "	               i++;\n" +
		    "	               password = args[i];\n" +
		    "              }\n" +
		    "              i++;\n" +
		    "           }\n" +
		    "           form = new " + firstUpper(getTable()) + "Form(host,user,password,database,table);\n" +
	            "       }\n" +
                    "       CloseableFrame cf = new CloseableFrame(\"" + getTable() + "\");\n" +
                    "       cf.getContentPane().setLayout(new BorderLayout());\n" +
                    "       cf.getContentPane().add(form,BorderLayout.CENTER);\n" +
	            "       form.addMenuBar(cf,form.mb);\n" +
                    "       form.mb.cancel.addActionListener(new ActionListener() {\n" +
                    "                  public void actionPerformed(ActionEvent e) {\n" +
                    "                  System.exit(0);\n" +
                    "        }});\n" +
                    "       form.bp.cancel.addActionListener(new ActionListener() {\n" +
                    "             public void actionPerformed(ActionEvent e) {\n" +
                    "                System.exit(0);\n" +
                    "        }});\n\n" +
                    "       cf.pack();\n\n" +
                    "       cf.setVisible(true);\n" +
                    "}\n}";
       return footer;
  }


  String table;
  public void setTable(String v) { table = v; }
  public String getTable() { return table; }

  String primaryKeys;
  public void setPrimaryKeys(String v) { primaryKeys = v; }
  public String getPrimaryKeys() { return primaryKeys; }

  private String database;
  public void setDatabase(String v) { database = v; }
  public String getDatabase() { return database; }

    private String firstUpper(String s) {
      	// I do not want to have spaces within the name
        s = s.replace(' ','_');	
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
     *   @param file   Name of the XML-file containing the DB-description
     *   @param appDir TOP-Level Directory where to save the source files [directory]/applications/[DatabaseName]
     *
     **/

    public GenerateForm(String host, 
			String user, 
			String password, 
			String schema,
			String file, String appDir) {

	setHost(host);
	setUser(user);
	setPassword(password);
	setSchema(schema);
	init(file, appDir);
    }

    public GenerateForm(t_connect con,
			String schema,
			String file, String appDir) {

	setCon(con);
	setSchema(schema);
	init(file, appDir);
    }
    

    private void init(String file, String appDir) {  
 

      setApplicationsDirectory(appDir);
      initPanels();
      try {
	  //DOMBuilder builder = new DOMBuilder("org.jdom.adapters.XercesDOMAdapter");
	  SAXBuilder builder = new SAXBuilder();

	  doc     = builder.build(new File(file));
	  
      } catch (JDOMException e) {
	  e.printStackTrace();
      } catch (IOException ioex) {
	    	ioex.printStackTrace();
	    }
      Element database = doc.getRootElement();
      setDatabase(database.getAttributeValue("name"));
      
      List tables = database.getChildren();
      tableTreater(tables);
    }

    private String extractName(String v) {
	File f = new File(v);
	if ( f.exists() ) {
	    if ( f.isDirectory() ) v = f.getName();
	    if ( f.isFile()      ) v = f.getName();
	}
	    
	return v;
    }


    private void save() {

	dcls = "\n\n// *** DECLARATIONS ***\n\n" + 
	    "  public "  + firstUpper(getTable()) + "TableModel tableModel;\n\n"  + dcls;
	getRow   = getRow   + "       return row;\n    }\n"; 
	setRow   = setRow   + "    }\n";
	clearRow = clearRow + "    }\n";
	
	masterPanel = "\n\n// *** MASTER PANEL *** \n\n" + masterPanel;
	otherPanels = "\n\n// *** OTHER PANELS ***\n\n" + otherPanels;
	listPanel   = "\n\n// *** LIST PANEL ***\n\n" + listPanel;
	
	String code = getHeader();
	code        = code + 
	    dcls + 
	    getRow + 
	    setRow + 
	    clearRow +
	    listeners() +
	    masterPanel + 
	    otherPanels + 
	    listPanel + 
	    getFooter();
	String dir = getApplicationsDirectory();
	String formFile =dir + File.separator + "applications"+ File.separator + 
	    extractName(getDatabase()) + 
	    File.separator + firstUpper(getTable())+"Form.java"; 
	
	
	sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter(code,formFile);
	
	/**
	   create directory if not existing
	   File f = new File(dir + File.separator + "applications" + File.separator + getDatabase());
	   f.mkdir();
	   
	   try {
	   DataOutputStream outptstr = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(formFile)));
	   outptstr.writeBytes(code);
	   outptstr.flush();
	   outptstr.close();
	   } catch(IOException execp) { execp.printStackTrace(); }
	   
	   
	**/
	
	// so we do the Wrappers and the JTableModel
	GenerateWrapper gw=null;
	GenerateJTableModel gm=null;
	if (getCon() == null ) {
	    gw = new GenerateWrapper(getHost(),
				     getUser(),
				     getPassword(),
				     getDatabase(),
				     getTable(),
				     getApplicationsDirectory(),
				     getSchema());   
	    
	    gm = new GenerateJTableModel(getHost(),
					 getUser(),
					 getPassword(),
					 getDatabase(),
					 getTable(),
					 getSchema());
	} else {
	    gw = new GenerateWrapper(getCon(),
				     getTable(),
				     getApplicationsDirectory(),
				     getSchema());   
	    
	    gm = new GenerateJTableModel(getCon(),
					 getTable(),
					 getSchema());
	    
	}
	String tableFile = dir + File.separator + "applications"+ File.separator + 
	    extractName(getDatabase()) + 
	    File.separator + firstUpper(getTable())+"TableModel.java"; 

	sql.fredy.io.FileWriter fw2 = new sql.fredy.io.FileWriter(gm.getTheCode(),tableFile);
					     					  
	initPanels();
    }


  private void tableTreater(List tables) {
    Iterator itr = tables.iterator();
    while (itr.hasNext()) {
      Element table = (Element)itr.next();

      setTable(table.getAttributeValue("name"));
      generateListPanel(table.getAttributeValue("name"));
      initForRows();
      setPrimaryKeys(table.getAttributeValue("primaryKeys"));
      panelTreater(table.getChildren(),table.getNamespace());
      save();
    }      
  }

  private void panelTreater(List panels, Namespace ns) {
    Iterator itr = panels.iterator();
    while (itr.hasNext()) {
      Element panel = (Element)itr.next();
      masterPanel = masterPanel + gridBagTreater(panel.getChild("gridBagConstraints",ns));
      masterPanel = masterPanel + "   panel.add("+ panel.getAttributeValue("name") + "(),gbc);\n";
      otherPanels = otherPanels + "\nprivate JPanel " + panel.getAttributeValue("name") + "() {\n" +
                             "   JPanel panel = new JPanel();\n" +
                             "   panel.setLayout(new GridBagLayout());\n" +
                             "   GridBagConstraints gbc = new GridBagConstraints();\n\n";
      // now do the Border of this Panel
      otherPanels = otherPanels + borderTreater(panel.getChildText("border",ns),panel.getChildText("title",ns) );

                                  
      componentTreater(panel.getChildren("component",ns),ns);
      otherPanels = otherPanels + "\n  return panel;\n}";
    }      
    //masterPanel = masterPanel + "\n   setMySize(panel.getPreferredSize());\n";
    masterPanel = masterPanel + "\n   return new JScrollPane(panel);\n}";
  }

    private String borderTreater(String border, String title) {
	String b = "new EtchedBorder()";
	String c = "   panel.setBorder(";

	if (border.startsWith("BevelBorder")  ) b = "new BevelBorder(" + border + ")";
	if (border.startsWith("thin"        ) ) b = "new LineBorder(Color.black,1)";
	if (border.startsWith("medium"      ) ) b = "new LineBorder(Color.black,2)";
	if (border.startsWith("thick"       ) ) b = "new LineBorder(Color.black,3)";
	if (border.startsWith("Empty"       ) ) b = "new EmptyBorder(1,1,1,1)";

	if (title.length() > 0) {
	    c = c + "new TitledBorder(" + b + ",\"" + title + "\"));\n";
	} else {
	    c = c + b + ");\n";
	}
	return c;
    } 



  private void componentTreater(List components,Namespace ns) {
    Iterator itr = components.iterator();
    while (itr.hasNext()) {
      Element component = (Element)itr.next();
     
      // do the getRow and setRow Methods
      doRows(component.getAttributeValue("name"),component.getAttributeValue("type"));


      Element parameter=component.getChild("parameter",ns);

      labelTreater((Element)component.getChild("labelConstraints",ns),parameter.getAttributeValue("label"));


      if ( component.getAttributeValue("fieldType").equals("FieldPanel")) 
	  fieldPanel(parameter,component.getAttributeValue("name"));

      if ( component.getAttributeValue("fieldType").equals("AreaPanel") )      
	  areaPanel(parameter,component.getAttributeValue("name"));

      if ( component.getAttributeValue("fieldType").equals("DBcomboBoxPanel") ) 
	  DBcomboBoxPanel(parameter,component.getAttributeValue("name"));

      if ( component.getAttributeValue("fieldType").equals("TwinBoxPanel") )   
	  TwinBoxPanel(parameter,component.getAttributeValue("name"));

      if ( component.getAttributeValue("fieldType").equals("DateFieldPanel") ) 
	  dateFieldPanel(parameter,component.getAttributeValue("name"));

      if ( component.getAttributeValue("fieldType").equals("DateButtonPanel") )
	  dateButtonPanel(parameter,component.getAttributeValue("name"));


      otherPanels = otherPanels + gridBagTreater(component.getChild("gridBagConstraints",ns));
      otherPanels = otherPanels + "\n   panel.add(" + component.getAttributeValue("name") + ",gbc);\n";
    }
  }

      
    private void labelTreater(Element l,String text) {
	if ( text != null) {
	    otherPanels = otherPanels + gridBagTreater(l);
	    otherPanels = otherPanels + "\n   panel.add(new JLabel(\"" + text + "\"),gbc);\n";
	}
    }



      private void fieldPanel(Element parameter,String name) {

        otherPanels = otherPanels + "\n   " + name + " = new FieldPanel(null";
	otherPanels = otherPanels + "," +  parameter.getAttributeValue("length");
	otherPanels = otherPanels + "," + "\"" +  parameter.getAttributeValue("text") + "\"";
        otherPanels = otherPanels + "," +  parameter.getAttributeValue("filter");
        otherPanels = otherPanels + "," +  parameter.getAttributeValue("titled");
	otherPanels = otherPanels + "," +  "\"" +  parameter.getAttributeValue("title") + "\"";
	otherPanels = otherPanels + "," +  parameter.getAttributeValue("layout") + ");\n";

	dcls = dcls + "   public FieldPanel " + name + ";\n";

      }

      private void areaPanel(Element parameter, String name) {

	  otherPanels = otherPanels + "\n   " + name + " = new AreaPanel(null";	   
	  otherPanels = otherPanels + "," +  parameter.getAttributeValue("rows") ;
	  otherPanels = otherPanels + "," +  parameter.getAttributeValue("cols") ;     
	  otherPanels = otherPanels + "," +  parameter.getAttributeValue("lineWrap");
	  otherPanels = otherPanels + "," +  "\"" +  parameter.getAttributeValue("text") + "\"";
	  otherPanels = otherPanels + "," +  parameter.getAttributeValue("titled");
	  otherPanels = otherPanels + ", "+  "\"" +  parameter.getAttributeValue("title") + "\"";
	  otherPanels = otherPanels + "," +  parameter.getAttributeValue("layout") + ");\n";
	  dcls = dcls + "   public AreaPanel " + name + ";\n";

      }

    private void DBcomboBoxPanel(Element parameter, String name) {
        otherPanels = otherPanels + "\n   " + name + " = new DBcomboBoxPanel(getAHost(),getAUser(),getAPassword(),getADatabase()" ;
	otherPanels = otherPanels + "," + "\"" +  parameter.getAttributeValue("query") + "\"";
	//otherPanels = otherPanels + "," + "\"" +  parameter.getAttributeValue("label") + "\"";
	otherPanels = otherPanels + "," + "null";
	otherPanels = otherPanels + "," + "\"" +  parameter.getAttributeValue("text") + "\"";
	otherPanels = otherPanels + "," +  parameter.getAttributeValue("titled");
        otherPanels = otherPanels + ", "+  "\"" +  parameter.getAttributeValue("title") + "\"";
        otherPanels = otherPanels + "," +  parameter.getAttributeValue("layout") + ");\n";
	dcls = dcls + "   public DBcomboBoxPanel " + name + ";\n";
    }

    private void TwinBoxPanel(Element parameter, String name) {
        otherPanels = otherPanels + "\n   " + name + " = new TwinBoxPanel(getAHost(),getAUser(),getAPassword(),getADatabase()" ;
	otherPanels = otherPanels + "," + "\"" +  parameter.getAttributeValue("query") + "\"";
	//otherPanels = otherPanels + "," + "\"" +  parameter.getAttributeValue("label") + "\"";
	otherPanels = otherPanels + "," + "null";
	otherPanels = otherPanels + "," + "\"" +  parameter.getAttributeValue("text") + "\"";
	otherPanels = otherPanels + "," +  parameter.getAttributeValue("titled");
        otherPanels = otherPanels + ", "+  "\"" +  parameter.getAttributeValue("title") + "\"";
        otherPanels = otherPanels + "," +  parameter.getAttributeValue("layout") + ");\n";
	dcls = dcls + "   public TwinBoxPanel " + name + ";\n";
    }



    private void dateFieldPanel(Element parameter, String name) {
        otherPanels = otherPanels + "\n   " + name + " = new DateFieldPanel(null";
	otherPanels = otherPanels + "," + "\"" +  parameter.getAttributeValue("text") + "\"";
	otherPanels = otherPanels + "," +  parameter.getAttributeValue("titled");
        otherPanels = otherPanels + ", "+  "\"" +  parameter.getAttributeValue("title") + "\"";
        otherPanels = otherPanels + "," +  parameter.getAttributeValue("layout") + ");\n";
	dcls = dcls + "   public DateFieldPanel " + name + ";\n";

    }
    
    private void dateButtonPanel(Element parameter, String name) {
        otherPanels = otherPanels + "\n   " + name + " = new DateButtonPanel(null";
	otherPanels = otherPanels + "," + "\"" +  parameter.getAttributeValue("text") + "\"";
	otherPanels = otherPanels + "," +  parameter.getAttributeValue("titled");
        otherPanels = otherPanels + ", "+  "\"" +  parameter.getAttributeValue("title") + "\"";
        otherPanels = otherPanels + "," +  parameter.getAttributeValue("layout") + ");\n";
	dcls = dcls + "   public DateButtonPanel " + name + ";\n";


    }



  private String gridBagTreater(Element gbc) {
    String s = "\n";
    if ( gbc != null) { 
	s = s + "   gbc.insets    = new Insets(" +  gbc.getAttributeValue("insets") + ");\n";
	s = s + "   gbc.anchor    = " + gbc.getAttributeValue("anchor") + ";\n";
	s = s + "   gbc.fill      = " + gbc.getAttributeValue("fill") + ";\n";
	s = s + "   gbc.weightx   = " + gbc.getAttributeValue("weightx")+ ";\n";
	s = s + "   gbc.weighty   = " + gbc.getAttributeValue("weighty")+ ";\n";
	s = s + "   gbc.gridheight= " + gbc.getAttributeValue("gridheight") + ";\n";
	s = s + "   gbc.gridwidth = " + gbc.getAttributeValue("gridwidth") + ";\n";
	s = s + "   gbc.gridx     = " + gbc.getAttributeValue("gridx") + ";\n";
	s = s + "   gbc.gridy     = " + gbc.getAttributeValue("gridy") + ";\n";
    } 
    return s;
  }


    private void doRows(String name, String type) {      

	clearRow = clearRow + "        " + name + ".clear();\n";

	if (type.startsWith("int")) {
	    setRow = setRow + "        " + name + ".setText(Integer.toString(v.get" + firstUpper(name) + "()));\n";
            getRow = getRow + "        try {\n" +
		"            row.set" + firstUpper(name) + "(Integer.parseInt(" + name + ".getText()));\n"+
		"        } catch (NumberFormatException e) { row.set" + firstUpper(name) + "(0); } \n";
	}
	if ( (type.startsWith("float")) || (type.startsWith("decimal"))) {
	    setRow = setRow + "        " + name + ".setText(Float.toString(v.get" + firstUpper(name) + "()));\n";
            getRow = getRow + "        try {\n" +
		"            row.set" + firstUpper(name) + "(Float.valueOf(" + name + ".getText()).floatValue());\n"+
		"            } catch (NumberFormatException e) { row.set" + firstUpper(name) + "(0); } \n";
	}
	if (type.startsWith("double")) {
	    setRow = setRow + "        " + name + ".setText(Double.toString(v.get" + firstUpper(name) + "()));\n";
            getRow = getRow + "   try {\n" +
		"       row.set" + firstUpper(name) + "(Double.valueOf(" + name + ".getText()).doubleValue());\n"+
		"  } catch (NumberFormatException e) { row.set" + firstUpper(name) + "(0.0); } \n";
	}
	if (type.startsWith("java.sql.Date")) {
	    setRow = setRow + "        " + name + ".setText(v.get" + firstUpper(name) + "().toString());\n";
            getRow = getRow + "        try {\n" +
		"            row.set" + firstUpper(name) + "(java.sql.Date.valueOf(" + name + ".getDate()));\n"+
		"        } catch (Exception e) { ; } \n";
	}
	if (type.endsWith("java.sql.Time")) {
	    setRow = setRow + "        " + name + ".setText(v.get" + firstUpper(name) + "().toString());\n";
            getRow = getRow + "        try {\n" +
		"            row.set" + firstUpper(name) + "(java.sql.Time.valueOf(" + name + ".getText()));\n"+
		"        } catch (Exception e) { ; } \n";
	}
	if (type.endsWith("java.sql.Timestamp")) {
	    setRow = setRow + "        " + name + ".setText(v.get" + firstUpper(name) + "().toString());\n";
            getRow = getRow + "        try {\n" +
		"            row.set" + firstUpper(name) + "(java.sql.Timestamp.valueOf(" + name + ".getText()));\n"+
		"        } catch (Exception e) { ; } \n";
	}

	if (type.startsWith("String")) {
	    setRow = setRow + "        " + name + ".setText(v.get" + firstUpper(name) + "());\n";
	    getRow = getRow + "        row.set" + firstUpper(name) +"(" + name + ".getText());\n";
	}

    }



    private String listeners() {
	String s =
            "    /**\n" +
	    "       * do the listeners for this form\n" + 
	    "       */\n" +
	    "    public void doListeners() {\n " +
	    "       bp.clear.addActionListener(new ActionListener() {\n" +
            "          public void actionPerformed(ActionEvent e) {\n" +
	    "             clear();\n" +
            "       }});\n" +
            "       mb.clear.addActionListener(new ActionListener() {\n" +
            "          public void actionPerformed(ActionEvent e) {\n" +
	    "             clear();\n" +
            "       }});\n" +
	    "       bp.insert.addActionListener(new ActionListener() {\n" +
            "                 public void actionPerformed(ActionEvent e) {\n" +
            "                   row = getRow();\n" +
            "                   msg(row.insert());\n" +
            "                   refresh();\n" +
            "       }});\n" +
            "       mb.insert.addActionListener(new ActionListener() {\n" +
            "                 public void actionPerformed(ActionEvent e) {\n" +
            "                   row = getRow();\n" +
            "                   msg(row.insert());\n" +
            "                   refresh();\n" +
            "       }});\n" +
	    "       bp.update.addActionListener(new ActionListener() {\n" +
            "                 public void actionPerformed(ActionEvent e) {\n" +
            "                   row = getRow();\n" +
            "                   msg(row.update());\n" +
            "                   refresh();\n" +
            "       }});\n" +
            "       mb.update.addActionListener(new ActionListener() {\n" +
            "                 public void actionPerformed(ActionEvent e) {\n" +
            "                   row = getRow();\n" +
            "                   msg(row.update());\n" +
            "                   refresh();\n" +
            "       }});\n" +
	    "       bp.delete.addActionListener(new ActionListener() {\n" +
            "                 public void actionPerformed(ActionEvent e) {\n" +
	    "                   if (verifyDelete() ) {\n" +
            "                      row = getRow();\n" +
            "                      msg(row.delete());\n" +
	    "                      clear();\n" +
            "                      refresh();\n" +
            "                   }\n" +
            "       }});\n" +
            "       mb.delete.addActionListener(new ActionListener() {\n" +
            "                 public void actionPerformed(ActionEvent e) {\n" +
	    "                   if (verifyDelete() ) {\n" +
            "                      row = getRow();\n" +
            "                      msg(row.delete());\n" +
	    "                      clear();\n" +
            "                      refresh();\n" +
            "                   }\n" +
            "       }});\n" +
	    "     }\n\n" +
            "     public void refresh() {\n" +
            "             tableModel.clearData();\n"+
            "             tableModel.executeQuery();\n" +
            "     }\n\n";
	return s;

    }


    private void generateListPanel(String t) {
	//listPanel = listPanel + "     tableModel = new " + firstUpper(t) + "TableModel(getAHost(),getAUser(),getAPassword(),getADatabase(),getWorkingTable(),getASchema());\n";

        listPanel = listPanel + "     tableModel = new " + firstUpper(t) + "TableModel(getCon(),getWorkingTable(),getASchema());\n";

	listPanel = listPanel + "     tableModel.executeQuery();\n" +
            "     theTable = new JTable(tableModel);\n" +
            "     theTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);\n" +
            "     theTable.setAutoCreateRowSorter(true);\n" +
	    "     theTable.getTableHeader().setReorderingAllowed(false);\n" ;

	listPanel = listPanel + "     theTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {\n" +
	    "     public void valueChanged(ListSelectionEvent e) {\n" +
            "          int[] selection;\n" +
	    "          if ( ! e.getValueIsAdjusting()) {\n" +                    
	    "             ListSelectionModel lsm = (ListSelectionModel)e.getSource();\n" +
	    "             if (lsm.isSelectionEmpty()) {\n" +
	    "                ;\n" +
	    "             } else {\n" +
            "                 selection = theTable.getSelectedRows();\n" +
            "                 for (int i = 0; i < selection.length; i++) {\n" +
            "                           selection[i] = theTable.convertRowIndexToModel(selection[i]);\n" +
            "                 }\n" +
	    "                 setRow(tableModel.getRow(selection[0]));\n" +
            "                 setSelectedRow(selection[0]);\n" +
            "                 postListPanelAction();\n" +
	    "             }\n" +
	    "          }\n" +			
	    "     }});\n" +
            "	  generateQuery.search.addActionListener(new ActionListener() {\n" +
	    "  	    public void actionPerformed(ActionEvent e) {\n" +
	    "	       tableModel.setQuery(generateQuery.getQuery());\n" +
	    "               tableModel.executeQuery();\n" +
	    "	    }});\n" +
	    "     return new JScrollPane(theTable);\n" +
	    "  }\n\n";
	

    }


  public static void main(String args[]) {
    String uri="";
    String dir = "";
    boolean help = false;
    String host = "localhost";
    String user = System.getProperty("user.name");
    String password = "";
    String schema = "%";
     
    if ( args.length < 2) {
	help = true;
    }  else {


	int i = 0;

	while ( i < args.length) {
	    if ((args[i].equals("-help") ) || (args[i].equals("--h")) ) {		
		help=true;
	    }    

	    if ((args[i].equals("-dir") ) || (args[i].equals("-directory")) ) {		
		i++;
		dir=args[i];
	    }    
	    if ((args[i].equals("-f") ) || (args[i].equals("-file")) ) { 
		i++;
		uri=args[i];
	    }
	    if ((args[i].equals("-h") ) || (args[i].equals("-host")) ) { 
		i++;
		host=args[i];
	    }

	    if ((args[i].equals("-u") ) || (args[i].equals("-user")) ) { 
		i++;
		user=args[i];
	    }

	    if ((args[i].equals("-s") ) || (args[i].equals("-schema")) ) { 
		i++;
		schema=args[i];
	    }

	    if ((args[i].equals("-p") ) || (args[i].equals("-password")) ) { 
		i++;
		password=args[i];
	    }
	    i++;
	}
    }

    if ( help ) {
	System.out.println("\nFredy's Generate Form\n" +
			   "--------------------\n" +
			   "Generates the forms out of a XML file containing the DB-description\n" +
			   "Syntax: GenerateForm -help this screen\n" +
			   "                     -dir  top level directory where to store the code\n" +
			   "                           [this dir]/applications/[databasename]\n" +
			   "                     -f    the XML-file to use\n" +
			   "                     -h    host\n" +
			   "                     -u    user\n" +
			   "                     -p    password\n" +
			   "                     -s    schema [default % ]\n");
	System.exit(0);
    } 
    GenerateForm f = new GenerateForm(host,user,password,schema,uri,dir);
  }
}
