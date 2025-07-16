package sql.fredy.generate;

/** XMLEditor edits the XML-file used for Generate Code
 *  and is a part of Admin...
 *  Version 1.0  23. March 2002
 *  Fredy Fischer 
 *
 **/


/**
   There will be a tree in this Form:
   - Database
     - Table
       - Panel
         - Component
         - Component
         - Component
       - Panel
         - Component
         - Component
         - Component
       - Panel
       :
       :
       :
     - Table
       - Panel
         - Component
         - Component
         - Component
       - Panel
         - Component
         - Component
         - Component
       - Panel
       :
       :
       :
       ....etc

     Then there will be an Editor, that allows the following functions:
     - Insert a Panel
     - Cut Component to Container
     - Paste Coponent into Panel
     - change / view GridBagConstraints of a component
     - change / view GridBagConstraints of a panel
     and a Preview Tool to visualize the changes in a JPanel

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
import sql.fredy.io.MyFileFilter;
import sql.fredy.io.MakeVersion;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.tree.*;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreeSelectionModel;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.Namespace;

public class XMLEditor extends JPanel {


    public FormInterpreter fi;
    public ImageButton exit, bestView;
    public JMenuItem quit,pack;
    private String toDay() {
	Calendar c = Calendar.getInstance();
	
	return  Integer.toString(c.get(Calendar.YEAR)) + "-" + 
	    Integer.toString(c.get(Calendar.MONTH)+1)+ "-" + 
	    Integer.toString(c.get(Calendar.DATE));
	
    }
    
    private Document doc;
    private DefaultMutableTreeNode top,tableNode,panelNode,componentNode;
    private JTree tree;
    private JPopupMenu popupPanel, popupAttr,tablePopup ;
    private JMenuItem cutItem,delItem;
    private JPanel    properties;
    private DefaultTreeModel model;
    private Vector componentClipBoard = new Vector();
    private Vector tableClipBoard = new Vector();
    private Vector panelClipBoard = new Vector();
    private JLabel statusLine;


    private ConnectionPanel connectionPanel = new ConnectionPanel();

    /**
       * Get the value of host.
       * @return Value of host.
       */
    public String getHost() {return connectionPanel.getHost();}
    public void setHost(String v) { connectionPanel.setHost(v); }

    /**
       * Get the value of user.
       * @return Value of user.
       */
    public String getUser() {return connectionPanel.getUser();}
    public void setUser(String v) { connectionPanel.setUser(v);}


    /**
       * Get the value of password.
       * @return Value of password.
       */
    public String getPassword() {return connectionPanel.getPassword();}
    public void setPassword(String v) { connectionPanel.setPassword(v);}
 
    

    TreePath selectedElement=null;
    
    /**
       * Get the value of selectedElement.
       * @return value of selectedElement.
       */
    public TreePath getSelectedElement() {return selectedElement;}
    
    /**
       * Set the value of selectedElement.
       * @param v  Value to assign to selectedElement.
       */
    public void setSelectedElement(TreePath  v) {
	this.selectedElement = v;
	tree.setSelectionPath(v);
    }
    


    public XMLEditor() {
	init();
	selectFile();
	doIt();

    }

    public XMLEditor(String fileName) {
	init();
	setFile(fileName) ;
	doIt();

    }

    public XMLEditor(File file) {
	init();
	setFile(file);
	doIt();

    }

    private JPanel statusPanel() {
	String s = "Fredy's XML-Editor";

    /**
	try {	
	    ResourceBundle rb=ResourceBundle.getBundle("build");
	    s = s + rb.getString("build.number") + " " + rb.getString("build.date");
	} catch(Exception e) {
	    System.out.println(e);
	}
    **/

	JPanel panel = new JPanel();
	statusLine = new JLabel();
	statusLine.setText(" ");
	statusLine.setFont(new Font("Monospaced", Font.PLAIN, 10));
	statusLine.setBackground(Color.yellow);
	statusLine.setForeground(Color.blue);
	panel.setBackground(Color.yellow);
	panel.setForeground(Color.blue);
	panel.setBorder(new BevelBorder(BevelBorder.LOWERED));

 	JLabel ver = new JLabel(s);
	ver.setFont(new Font("Monospaced", Font.PLAIN, 10));
	ver.setBackground(Color.yellow);
	ver.setForeground(Color.blue);
	panel.add(ver);

	panel.add(statusLine);
	return panel;

    }



   private void selectFile() {

	JFileChooser chooser = new JFileChooser(); 
	chooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
	chooser.setDialogTitle("Select XML file");

	MyFileFilter filter = new MyFileFilter();
	filter.addExtension("xml");
	filter.addExtension("XML");
	filter.setDescription("XML-files");
	chooser.setFileFilter(filter);

	int returnVal = chooser.showOpenDialog(this);
	if(returnVal == JFileChooser.APPROVE_OPTION)
	    setFile(chooser.getSelectedFile());

   }
	   
   
    File file;
    
    /**
       * Get the value of file.
       * @return value of file.
       */
    public File getFile() {return file;}
    
    /**
       * Set the value of file.
       * @param v  Value to assign to file.
       */
    public void setFile(File  v) {this.file = v;}
    

    public void setFile(String v) {
	file = new File(v);
    }


    
    String database;
    
    /**
       * Get the value of database.
       * @return value of database.
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
       * @return value of table.
       */
    public String getTable() {return table;}
    
    /**
       * Set the value of table.
       * @param v  Value to assign to table.
       */
    public void setTable(String  v) {
	this.table = v;
    }
    
    private void init() {
        properties = new JPanel();
	properties.setLayout(new BorderLayout());
   
	this.setLayout(new BorderLayout());
	this.add(BorderLayout.NORTH,menubar());
	this.add(BorderLayout.WEST,buttonPanel());
	this.add(BorderLayout.EAST,properties);
	this.updateUI();

	fi = new FormInterpreter();
	
    }



    private void doIt() {

 
	edit();

	JScrollPane scrollpane = new JScrollPane(tree);
	scrollpane.setName("tree");
	this.add(BorderLayout.CENTER,scrollpane);
	this.add(BorderLayout.SOUTH,statusPanel());
	tree.add(popupMenuPanel());
	tree.add(popupMenuAttribute());
	//treePanel.updateUI();

	tablePopUpMenuPanel();

	this.updateUI();

	this.setSize(new Dimension(560,475));

    }

    private void edit() {


	removeTree();

	try {
	    //DOMBuilder builder = new DOMBuilder("org.jdom.adapters.XercesDOMAdapter");
	    SAXBuilder builder = new SAXBuilder();
	    doc     = builder.build(getFile());
	    
	} catch (JDOMException e) {
	    e.printStackTrace();
	} catch (IOException ioex) {
    	ioex.printStackTrace();
    }
	Element database = doc.getRootElement();
	setDatabase(database.getAttributeValue("name"));
	
	// root Element of the JTree is the Database so its node is a DbTreeNodeObject
	XMLTreeNode xn = new XMLTreeNode();
	xn.setName(getDatabase());
	xn.setText(getDatabase());
	xn.setType("db");
	DbTreeNodeObject dbo = new DbTreeNodeObject(getDatabase());
	xn.setNode((DbTreeNodeObject)dbo);
	top = new DefaultMutableTreeNode(xn);
	
	java.util.List tables = database.getChildren();
	tableTreater(tables);
	
	//Create a tree that allows one selection at a time.
	tree = new JTree(top);
	tree.getSelectionModel().setSelectionMode
	    (TreeSelectionModel.SINGLE_TREE_SELECTION);
	
	tree.putClientProperty("JTree.lineStyle", "Angled");

	doTheTreeModelThings();

	tree.addTreeSelectionListener( new TreeSelectionListener() {
	  public void valueChanged(TreeSelectionEvent tse)
	    {
	      try {

		TreePath  t = tse.getOldLeadSelectionPath();
		if ( t !=null ) {
		    DefaultMutableTreeNode lastNode =  (DefaultMutableTreeNode)t.getLastPathComponent();
	
		    XMLTreeNode xn = new XMLTreeNode();
		    xn = (XMLTreeNode)lastNode.getUserObject();
		    
		    // is it a panel?
		    if (xn.getType().toLowerCase().startsWith("panel") ) {
			// 1st get the Component from the GUI
			PanelProperties pp = new PanelProperties();
			pp = (PanelProperties)getPanel("PanelProperties");
			
			// 2nd put the values back to the tree
			xn = pp.getXn();
			lastNode.setUserObject((XMLTreeNode)xn);
		    }
		    
		    // or is it a component              
		    if (xn.getType().toLowerCase().startsWith("component") ) {
			
			FieldProperties fp = new FieldProperties();
			fp = (FieldProperties)getPanel("FieldProperties");

			xn = fp.getXn();
			lastNode.setUserObject((XMLTreeNode)xn);
		    } 
		}
		} catch (Exception e) { }
		
		properties.removeAll();
		properties.updateUI();
	      


	    }});


	
	tree.addMouseListener( new MouseAdapter() {		
		public void mousePressed(MouseEvent e) 	{
	
		    int selRow = tree.getRowForLocation(e.getX(), e.getY());
		    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
		    //popUpEdit(selRow,selPath);
		    
		    if ( selRow != -1 ) {
			if ( selPath.getPathCount() > 1 ) {
			    popUpEdit(selRow,selPath);
			    
			    
			    if ( (e.getClickCount() == 1) && 
				 (e.isPopupTrigger() )  && 
				 ( selPath == getSelectedElement()) ) {

	
				// now show different Popup-Menues
				// this is the table-Level
				if ( selPath.getPathCount() == 2 ) {				   
				    tablePopup.show(tree,e.getX(), e.getY());	           			 
				}			      
				


				// this is the panel level
				if ( selPath.getPathCount() == 3 ) {
				    doForPopUpMenu(selPath);
				    popupPanel.show(tree,e.getX(), e.getY());	           			 
				}			      
				
				// this is the Attribute level
				if ( selPath.getPathCount() == 4 )  popupAttr.show(tree,e.getX(), e.getY());	
			    }
			    else if ( e.getClickCount() == 1) {  
				setSelectedElement(selPath); 
			    }
			    else if(e.getClickCount() == 2) {
				// click twice onto an element to prepare it for rightClick
				setSelectedElement(selPath);
			    }
			}
		    }
		}});

	// my tree cell renderer
	tree.setCellRenderer(new XMLTreeRenderer());

    }
    
    private void popUpEdit(int selRow, TreePath selPath) {
	    DefaultMutableTreeNode selNode =  (DefaultMutableTreeNode)selPath.getLastPathComponent();
	    
	    DefaultMutableTreeNode thisPanel = null;
	    DefaultMutableTreeNode thisTable = null;
	    
	    // make sure, component are not mixed over different tables
	    if ( selPath.getPathCount() > 1 ) {
		thisPanel =  (DefaultMutableTreeNode)selNode.getParent();
		thisTable =  (DefaultMutableTreeNode)thisPanel.getParent();
		if ( selPath.getPathCount() == 3 )  thisTable = thisPanel;
		if ( selPath.getPathCount() == 2 )  thisTable = selNode;
	    }
	    
	    if ( ( componentClipBoard.size() < 1 )  && ( panelClipBoard.size() < 1 ) ) {
		
		// compare TableNames
		XMLTreeNode xn = new XMLTreeNode();
		xn = (XMLTreeNode)thisTable.getUserObject();
		
		if (getTable() != (String)xn.getText() ) {
		    setTable( (String)xn.getText());
		}
	    }	    
	}


    private void displayPopUpMenues() {

	TreePath selPath = getSelectedElement();        
        int selRow = tree.getRowForPath(selPath);
	//popUpEdit(selRow,selPath);
		    
	if ( selRow != -1 ) {
	    if ( selPath.getPathCount() > 1 ) {
	
		java.awt.Point p = new Point();
                p = tree.getLocation();

		popUpEdit(selRow,selPath);
			    
		// now show different Popup-Menues
		// this is the panel level
		if ( selPath.getPathCount() == 3 ) {
		    doForPopUpMenu(selPath);
		    popupPanel.show(tree,p.x,p.y);	           			 
		}			      
		
		// this is the Attribute level
		if ( selPath.getPathCount() == 4 )  popupAttr.show(tree,p.x,p.y);	
	    }
	}
    }		    	      
    


    private void doForPopUpMenu( TreePath selPath) {

	DefaultMutableTreeNode selNode =  (DefaultMutableTreeNode)selPath.getLastPathComponent();
	updateStatus();
				    
	properties.removeAll();
	properties.updateUI();
				    
	XMLTreeNode xn = new XMLTreeNode();
	xn = (XMLTreeNode)selNode.getUserObject();
				    
	String s =  (String)xn.getText();
	if (s.toLowerCase().startsWith("mainpanel") ) {
	    cutItem.setEnabled(false); 
	    delItem.setEnabled(false);
	} else { 
	    cutItem.setEnabled(true); 
	    delItem.setEnabled(true);
	}

    }


    private void removeTree() {
	for (int i = 0; i<= this.getComponentCount(); i++ ) {
	    try {
		if (this.getComponent(i).getName().startsWith("tree") ) this.remove(this.getComponent(i));
	    }  catch (Exception e) { ; }
	}
    }

    /** 
     * here we treat each Table
     **/

   private void tableTreater(java.util.List tables) {
    Iterator itr = tables.iterator();
    while (itr.hasNext()) {
      Element table = (Element)itr.next();
      setTable(table.getAttributeValue("name"));
      
      // this is a TableTreeNodeObject
      TableTreeNodeObject ttno = new TableTreeNodeObject((Element)table);
      XMLTreeNode xn = new XMLTreeNode();
      xn.setName(getTable());
      xn.setText(getTable());
      xn.setType("table");
      xn.setNode((TableTreeNodeObject)ttno);

      top.add(panelTreater(xn,table.getChildren(),table.getNamespace()));
    }      
  }

    // do the panels
    private DefaultMutableTreeNode panelTreater(XMLTreeNode table,java.util.List panels, Namespace ns) {
	DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode((XMLTreeNode)table);
	Iterator itr = panels.iterator();
	while (itr.hasNext()) {
	    Element panel = (Element)itr.next();

	    PanelTreeNodeObject ptno = new PanelTreeNodeObject((Element)panel);
	    XMLTreeNode xn = new XMLTreeNode();
	    xn.setName(panel.getAttributeValue("name"));
	    xn.setText(panel.getAttributeValue("name"));
	    xn.setType("panel");
	    xn.setNode((PanelTreeNodeObject)ptno);

            tableNode.add(componentTreater(xn,panel.getChildren("component",ns),ns));
	}      
        return tableNode;
    }
   

    private DefaultMutableTreeNode componentTreater(XMLTreeNode panel,java.util.List components,Namespace ns) {
	DefaultMutableTreeNode panelNode = new DefaultMutableTreeNode((XMLTreeNode)panel);
	Iterator itr = components.iterator();
	while (itr.hasNext()) {
	    Element component = (Element)itr.next();
     
	    ComponentTreeNodeObject ctno = new ComponentTreeNodeObject((Element)component);
	    XMLTreeNode xn = new XMLTreeNode();
	    xn.setName(component.getAttributeValue("name"));
	    xn.setText(component.getAttributeValue("name"));
	    xn.setType("component");
	    xn.setNode((ComponentTreeNodeObject)ctno);

	    DefaultMutableTreeNode componentNode = new DefaultMutableTreeNode(xn);
	    panelNode.add(componentNode);
	}
	return panelNode;
    }


    private JMenuBar menubar() {
	JMenuBar mb = new JMenuBar();

	JMenu fileMenu = new JMenu("File");
	fileMenu.setMnemonic('F');
	
	JMenu editMenu = new JMenu("Edit");
	editMenu.setMnemonic('E');

	JMenu toolsMenu = new JMenu("Tools");
	toolsMenu.setMnemonic('T');

	JMenuItem open = new JMenuItem("Open");
	JMenuItem save = new JMenuItem("Save");
	          quit = new JMenuItem("Exit");

	JMenuItem cut  = new JMenuItem("Cut");
	JMenuItem paste= new JMenuItem("Paste");
	JMenuItem props= new JMenuItem("Properties");
	JMenuItem inspa= new JMenuItem("Insert Panel");
        JMenuItem eu   = new JMenuItem("Edit");

	JMenuItem prev = new JMenuItem("Preview");
	          pack = new JMenuItem("Pack");


	JMenuItem conn = new JMenuItem("Connection Parameter");	  

	setCtrlAccelerator(open,'o');
	setCtrlAccelerator(save,'S');
	setCtrlAccelerator(quit,'E');
	setStdAccelerator(cut,KeyEvent.VK_CUT);
	setStdAccelerator(paste,KeyEvent.VK_PASTE);
	setStdAccelerator(props,KeyEvent.VK_PROPS);

	setStdAccelerator(inspa,KeyEvent.VK_INSERT);
	setStdAccelerator(prev,KeyEvent.VK_F1);
	setStdAccelerator(pack,KeyEvent.VK_F2);
	setStdAccelerator(conn,KeyEvent.VK_F3);
	setStdAccelerator(eu,KeyEvent.VK_F4);
     
	fileMenu.add(open);
	fileMenu.add(save);
	fileMenu.add(new JSeparator());
	fileMenu.add(quit);

	editMenu.add(eu);
	editMenu.add(cut);
	editMenu.add(paste);
	editMenu.add(props);
	editMenu.add(inspa);
	
	toolsMenu.add(prev);
	toolsMenu.add(pack);
	toolsMenu.add(conn);

	mb.add(fileMenu);
	mb.add(editMenu);
	mb.add(toolsMenu);

	open.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selectFile();
		    doIt();
		    }});

	save.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    saveDoc();
		    }});
	
	eu.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		  displayPopUpMenues();

		}
	    });
	

	props.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    doProperties();
		    }});
	
	cut.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    doCut();
		    }});
	paste.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    doPaste();
		    }});
	inspa.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    insertPanel();
		    }});	
	
	conn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    connectionParameter();
		    }});
	prev.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    formPreview();
		    }});


	return mb;
    }

    private void connectionParameter() {
	properties.removeAll();
	properties.add(BorderLayout.CENTER,connectionPanel);
	properties.updateUI();		    
    }

    private void formPreview() {

	if ( ( getHost().length() < 2) ||
	     ( getUser().length() < 2)    ) {
	    beep();
	    connectionParameter();	    
	} else {	    	
	    fi.setHost(getHost());
	    fi.setUser(getUser());
	    fi.setPassword(getPassword());
	    fi.setDocument(getDocument(false));
	}
    }

    private void beep() {
	Toolkit tk = this.getToolkit();
	tk.beep();
    }

       private void setStdAccelerator(JMenuItem mi, int acc) {
	   KeyStroke ks = KeyStroke.getKeyStroke(
						 acc, 0
						 );
	   mi.setAccelerator(ks);

        }
       private void setCtrlAccelerator(JMenuItem mi, char acc) {
	   KeyStroke ks = KeyStroke.getKeyStroke(
						 acc, java.awt.Event.CTRL_MASK
						 );
	   mi.setAccelerator(ks);

        }


    private void saveDoc() {
	MakeVersion mv = new MakeVersion(getFile());
	XMLOutputter fmt=  new XMLOutputter();
	try {

	    DataOutputStream outputstr = new DataOutputStream(
				        new BufferedOutputStream(
				        new FileOutputStream(getFile())));

	    fmt.output((Document)getDocument(true),outputstr);
	} catch (IOException e) { ;}

    }



    private JToolBar buttonPanel() {
	JToolBar toolbar = new JToolBar(SwingConstants.VERTICAL);
	toolbar.setFloatable(false);

	ImageButton loadFile = new ImageButton(null,"opendoc.gif","load XML-File");
	ImageButton saveFile = new ImageButton(null,"save.gif","save XML-file");
	ImageButton preview  = new ImageButton(null,"magnify.gif","preview the Form");
	ImageButton props    = new ImageButton(null,"properties.gif","Properties");
        ImageButton popMenu  = new ImageButton(null,"popup.gif","Display Component Menu");
	ImageButton cut      = new ImageButton(null,"cut.gif","cut");
	ImageButton insPanel = new ImageButton(null,"insertpanel.gif","insert Panel");
	ImageButton paste    = new ImageButton(null,"paste.gif","paste");
	            exit     = new ImageButton(null,"exit.gif","exit");
		    bestView = new ImageButton(null,"pagesetup.gif","Best view");
	

       popMenu.addActionListener(new ActionListener() {
	       public void actionPerformed(ActionEvent e) {
		   displayPopUpMenues();
	       }
	   });
		    


	loadFile.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    selectFile();
		    doIt();
		    }});
	props.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    doProperties();
		    }});

	saveFile.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    saveDoc();
		    }});
	
	preview.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    formPreview();
		    }});
	


	
	cut.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    doCut();
		    }});
	paste.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    doPaste();
		    }});
	insPanel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    insertPanel();
		    }});
	

	toolbar.add(loadFile);
	toolbar.add(saveFile);
	toolbar.addSeparator();
	toolbar.add(preview);
	toolbar.add(props);
        toolbar.add(popMenu);
	toolbar.add(cut);
	toolbar.add(paste);
	toolbar.add(insPanel);
	toolbar.addSeparator();
	toolbar.add(bestView);
	toolbar.addSeparator();
	toolbar.add(exit);

	return toolbar;

    }

    private void tablePopUpMenuPanel() {
	tablePopup = new JPopupMenu();
	JMenuItem  delTable = new JMenuItem("Delete");
	tablePopup.add(delTable);
	delTable.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    TreePath  path = getSelectedElement();
		    DefaultMutableTreeNode node   = (DefaultMutableTreeNode) path.getLastPathComponent();
		    XMLTreeNode xn = new XMLTreeNode();
		    xn = (XMLTreeNode)node.getUserObject();	
		    tableClipBoard.addElement((MutableTreeNode)node);
		    model.removeNodeFromParent(node);	    
		}
	    });

	JMenuItem  undelTable = new JMenuItem("Undelete");
	tablePopup.add(undelTable);	
	undelTable.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    undeleteTable();
		}
	    });
	

    }


    private JPopupMenu popupMenuPanel() {
	popupPanel = new JPopupMenu();
	JMenuItem props = new JMenuItem("Properties");
	        cutItem = new JMenuItem("Cut");
	JMenuItem paste = new JMenuItem("Paste Panel");
	JMenuItem nPanel= new JMenuItem("Insert Panel");
	        delItem = new JMenuItem("Delete Panel");
	JMenuItem pastec= new JMenuItem("Paste Components");

	props.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    panelProperties();
		}});
	

	// cut a panel with its components to the internal clipboard
	cutItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    cutPanel();
		    updateStatus();
		    }});

	// delete a panel only if there are no components related
	delItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    deletePanel();
		    updateStatus();
		    }});

	// insert a panel from the clipboard
	paste.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    pastePanel();
		    updateStatus();
		    }});	
	// insert a new panel
	nPanel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    insertNewPanel();
		    updateStatus();
		    }});

	pastec.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    pasteComponentToPanel();
		    updateStatus();
		    }});
	

	
	popupPanel.add(props);
	popupPanel.add(new JSeparator());
	popupPanel.add(cutItem);
	popupPanel.add(new JSeparator());	
	popupPanel.add(paste);
	popupPanel.add(pastec);
	popupPanel.add(new JSeparator());
	popupPanel.add(nPanel);
	popupPanel.add(delItem);

	return popupPanel;
    }

    private void updateStatus() {
	statusLine.setText("Buffersize: " + Integer.toString(componentClipBoard.size()) + "|Table is " + getTable());
	statusLine.updateUI();
    }



    private void doProperties() {
	if (getNodeType().toLowerCase().startsWith("panel"))     panelProperties();
	if (getNodeType().toLowerCase().startsWith("component")) componentProperties();
    }

    private void doCut() {
	if (getNodeType().toLowerCase().startsWith("panel"))     cutPanel();
	if (getNodeType().toLowerCase().startsWith("component")) cutComponent();
    }
    private void doPaste() {
	if (getNodeType().toLowerCase().startsWith("panel"))     pastePanel();
	if (getNodeType().toLowerCase().startsWith("component")) pasteLast();
    }

    private void insertPanel() {
	if (getNodeType().toLowerCase().startsWith("panel")) insertNewPanel();
    }


    private String getNodeType() {
	try {
	    TreePath  t = getSelectedElement();
	    DefaultMutableTreeNode selNode =  (DefaultMutableTreeNode)t.getLastPathComponent();
	    
	    XMLTreeNode xn = new XMLTreeNode();
	    xn = (XMLTreeNode)selNode.getUserObject();
	    return xn.getType();
	} catch (Exception e) {
	    return null;
	}

    }

  private Object getPanel(String s) {
        Object o = null;
	for (int i = 0; i<= properties.getComponentCount(); i++ ) {
	    try {
		if (properties.getComponent(i).getName().startsWith(s) ) {
		  o = this.getComponent(i);
		  return properties.getComponent(i);
		}
	    }  catch (Exception e) { 
		//System.out.println("Error while trying to fetch component " + s + 
		//		  " at index " + i + "\n" + e.getMessage());
               return null; 
	    }
	}
	return o;
  }



    private void panelProperties() {

        properties.removeAll(); // important Change

	// need to protect the mainPanel 
	TreePath  t = getSelectedElement();
	DefaultMutableTreeNode selNode =  (DefaultMutableTreeNode)t.getLastPathComponent();
	
	XMLTreeNode xn = new XMLTreeNode();
	xn = (XMLTreeNode)selNode.getUserObject();
	String    s =(String)xn.getText();
	
	PanelProperties pp = new PanelProperties(xn);
	
	
	if ( s.startsWith("mainPanel") ) {
	    pp.name.setEditable(false);
	    pp.name.setText(s);
	}
	
	properties.add(BorderLayout.CENTER,pp);
	properties.updateUI();

    }

    private void componentProperties() {
        properties.removeAll(); // important Change
		    
	TreePath  t = getSelectedElement();
	DefaultMutableTreeNode selNode =  (DefaultMutableTreeNode)t.getLastPathComponent();
	
	XMLTreeNode xn = new XMLTreeNode();
	xn = (XMLTreeNode)selNode.getUserObject();
	FieldProperties fp = new FieldProperties(xn);
	
	properties.add(BorderLayout.CENTER,fp);
	properties.updateUI();
    }

    private JPopupMenu popupMenuAttribute() {
	popupAttr = new JPopupMenu();
	JMenuItem props   = new JMenuItem("Properties");
	JMenuItem paste   = new JMenuItem("Paste last");
	JMenuItem pastel  = new JMenuItem("Paste from Buffer");
	JMenuItem pastea  = new JMenuItem("Paste compl. Buffer");
	JMenuItem cut     = new JMenuItem("Cut to Buffer");


	props.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    componentProperties();
		}});
	

	cut.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    cutComponent();
		    updateStatus();
		    }});
	
	paste.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    pasteLast();
		    updateStatus();
		    }});
	pastea.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    pasteAll();
		    updateStatus();
		    }});
	



	popupAttr.add(props);
	popupAttr.add(new JSeparator());
	popupAttr.add(cut);
	popupAttr.add(new JSeparator());
	popupAttr.add(paste);
	popupAttr.add(pastel);
	popupAttr.add(pastea);



	return popupAttr;
    }

    private void doTheTreeModelThings() {
	model = (DefaultTreeModel) tree.getModel();
	model.addTreeModelListener(new TreeModelListener() {
		public void treeNodesInserted(TreeModelEvent e) {
		    // do something
		    updateStatus();
		}
		public void treeNodesRemoved(TreeModelEvent e) {
		    // do something

		    updateStatus();
		}
		public void treeNodesChanged(TreeModelEvent e) {		 
		    tree.updateUI();
		    updateStatus();

		}
		public void treeStructureChanged(TreeModelEvent e) {
		    // do something
		    updateStatus();
		}
	    });


    }

    private void insertNewPanel() {
	TreePath  path = getSelectedElement();

	MutableTreeNode parent = (MutableTreeNode) path.getLastPathComponent();
	MutableTreeNode node   = (MutableTreeNode) path.getLastPathComponent();
	DefaultMutableTreeNode tablen = (DefaultMutableTreeNode) parent.getParent();
	
	XMLTreeNode xn = new XMLTreeNode();
	xn = (XMLTreeNode)tablen.getUserObject();

	if (getTable().equals((String)xn.getText())) {           

	    String s = getUniqueName();
	    if ( s != null) {
		int index = parent.getParent().getIndex(node) + 1;

		XMLTreeNode xtn = new XMLTreeNode();
		xtn.setType("panel");
		xtn.setName(s);
		xtn.setText(xtn.getName());

		PanelTreeNodeObject ptno = new PanelTreeNodeObject(xtn.getName());
		xtn.setNode(ptno);
		DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(xtn);

		model.insertNodeInto(aNode, tablen, index);
		tree.scrollPathToVisible(new TreePath(aNode.getPath()));
	    }
	}

    }


    private String getUniqueName() {
	String s ="";
	s = JOptionPane.showInputDialog(XMLEditor.this,"Enter a unique name for this Panel",
					"insert Panel",
					JOptionPane.QUESTION_MESSAGE);

	return s;

    }


    private void cutPanel() {
	TreePath  path = getSelectedElement();
	DefaultMutableTreeNode node   = (DefaultMutableTreeNode) path.getLastPathComponent();
	XMLTreeNode xn = new XMLTreeNode();
	xn = (XMLTreeNode)node.getUserObject();	

	if ((getTable().equals((String)xn.getText()))  &&  
	    (! (xn.getText().startsWith("mainpanel"))) ){   
	    panelClipBoard.addElement((MutableTreeNode)node);
	    model.removeNodeFromParent(node);	    
	}
    }


    private void pastePanel() {
	if ( panelClipBoard.size() > 0) {
	    TreePath  path = getSelectedElement();
	    
	    MutableTreeNode        parent = (MutableTreeNode) path.getLastPathComponent();
	    DefaultMutableTreeNode node   = (DefaultMutableTreeNode) path.getLastPathComponent();
	    DefaultMutableTreeNode tablen = (DefaultMutableTreeNode) node.getParent();

	    XMLTreeNode xn = new XMLTreeNode();
	    xn = (XMLTreeNode)tablen.getUserObject();
	    if (getTable().equals((String)xn.getText())) { 	
		int index = parent.getParent().getIndex(node)+ 1;
		model.insertNodeInto( (MutableTreeNode) panelClipBoard.lastElement(),(MutableTreeNode) parent.getParent(), index);
		panelClipBoard.remove(panelClipBoard.size()-1);
		tree.scrollPathToVisible(new TreePath(node.getPath()));

	    }



	}
    }

    private void undeleteTable() {
	if ( tableClipBoard.size() > 0) {
	    TreePath  path = getSelectedElement();
	    
	    MutableTreeNode        parent = (MutableTreeNode) path.getLastPathComponent();
	    DefaultMutableTreeNode node   = (DefaultMutableTreeNode) path.getLastPathComponent();
	    DefaultMutableTreeNode tablen = (DefaultMutableTreeNode) node.getParent();

	    XMLTreeNode xn = new XMLTreeNode();
	    xn = (XMLTreeNode)tablen.getUserObject();	
	    int index = parent.getParent().getIndex(node)+ 1;
	    model.insertNodeInto( (MutableTreeNode) tableClipBoard.lastElement(),(MutableTreeNode) parent.getParent(), index);
	    tableClipBoard.remove(tableClipBoard.size()-1);
	    tree.scrollPathToVisible(new TreePath(node.getPath()));	    
	}
    }



    private void deletePanel() {
	TreePath  path = getSelectedElement();
	MutableTreeNode node   = (MutableTreeNode) path.getLastPathComponent();
	DefaultMutableTreeNode tablen = (DefaultMutableTreeNode) node.getParent();
	XMLTreeNode xn = new XMLTreeNode();
	xn = (XMLTreeNode)tablen.getUserObject();
	if (getTable().equals((String)xn.getText())) {    
	    if ( node.isLeaf() ) model.removeNodeFromParent(node);
	}
    }

	

    private void cutComponent() {
	try {
	    TreePath  path = getSelectedElement();
	    MutableTreeNode parent = (MutableTreeNode) path.getLastPathComponent();
	    MutableTreeNode node   = (MutableTreeNode) path.getLastPathComponent();
	    MutableTreeNode compon = (MutableTreeNode) parent.getParent();
	    DefaultMutableTreeNode tablen = (DefaultMutableTreeNode) compon.getParent();
	    XMLTreeNode xn = new XMLTreeNode();
	    xn = (XMLTreeNode)tablen.getUserObject();
	    if (getTable().equals((String)xn.getText())) {    		
		componentClipBoard.addElement((MutableTreeNode)node);
		model.removeNodeFromParent(node);	    
	    }
	} catch (Exception e) {}
    
    }



    private void pasteLast() {
	    
	if ( componentClipBoard.size() > 0) {
	    TreePath  path = getSelectedElement();
	    
	    MutableTreeNode parent = (MutableTreeNode) path.getLastPathComponent();
	    MutableTreeNode node   = (MutableTreeNode) path.getLastPathComponent();
	    DefaultMutableTreeNode compon = (DefaultMutableTreeNode) parent.getParent();
	    DefaultMutableTreeNode tablen = (DefaultMutableTreeNode) compon.getParent();

	    XMLTreeNode xn = new XMLTreeNode();
	    xn = (XMLTreeNode)tablen.getUserObject();
	    if (getTable().equals((String)xn.getText())) { 	
		//int index = parent.getParent().getIndex(node.getParent()) + 2;
		int index = parent.getParent().getIndex(node) + 1;
		model.insertNodeInto( (MutableTreeNode) componentClipBoard.lastElement(), compon, index);
		componentClipBoard.remove(componentClipBoard.size()-1);
		tree.scrollPathToVisible(new TreePath(compon.getPath()));

	    }
	}
    }

    private void pasteAll() {
	for (int i = componentClipBoard.size();i > -1; i--) pasteLast();
    }

    private void pasteComponentToPanel() {
	TreePath  path = getSelectedElement();
	DefaultMutableTreeNode parent = (DefaultMutableTreeNode) path.getLastPathComponent();
	DefaultMutableTreeNode tablen = (DefaultMutableTreeNode) parent.getParent();
	XMLTreeNode xn = new XMLTreeNode();
	xn = (XMLTreeNode)tablen.getUserObject();	

	if (getTable().equals((String)xn.getText())) {           	
	    if ( ( componentClipBoard.size() > 0)  && ( parent.isLeaf() ) ) {
		for (int i = 0;i <= componentClipBoard.size() -1;i++) {
		    parent.add( (MutableTreeNode) componentClipBoard.elementAt(i) );
		}
		componentClipBoard.removeAllElements();;
		model = (DefaultTreeModel) tree.getModel();
	    }
	}
    }


   
    public Document getDocument(boolean all) {

	String tableName = "%";
	TreePath selPath = tree.getSelectionPath();
	if ( selPath == null ) {
	    tree.setSelectionRow(0);
	    selPath = tree.getSelectionPath();	
	    all     = true;
	}

	int nodeIndex = tree.getLeadSelectionRow();
	if  ( nodeIndex == 0) all = true;

	// I only need the actual table if the cursor is on a Component, Panel or Table;
	if ( ! all) {
	    DefaultMutableTreeNode selNode =  (DefaultMutableTreeNode)selPath.getLastPathComponent();
	    XMLTreeNode xn = new XMLTreeNode();
	    xn = (XMLTreeNode)selNode.getUserObject();	   
	    String nodeType=xn.getType();
	    while ( ! nodeType.startsWith("table") ){	      
		selNode = (DefaultMutableTreeNode)selNode.getParent();
		xn = (XMLTreeNode)selNode.getUserObject();
		nodeType =  xn.getType();
	    }
	    TableTreeNodeObject tno =  (TableTreeNodeObject)xn.getNode();
	    tableName = tno.getTable();
	}


	tree.setSelectionRow(0);

	Namespace ns = Namespace.getNamespace("admin","Fredys-Admintool");
    
	TreePath  path = tree.getPathForRow(0);
	DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
	XMLTreeNode xn = new XMLTreeNode();
	xn = (XMLTreeNode)node.getUserObject();
	
	DbTreeNodeObject dbtn = new DbTreeNodeObject("a");
	dbtn = (DbTreeNodeObject)xn.getNode();
	//Element database = new Element(dbtn.getDatabase(),ns);
	Element database = dbtn.getElt();

	for (int i = 0;i < node.getChildCount();i++ ) {

	    // for all or for one?
	    DefaultMutableTreeNode dtn = (DefaultMutableTreeNode)node.getChildAt(i);
	    XMLTreeNode x = new XMLTreeNode();
	    x= (XMLTreeNode)dtn.getUserObject();
	    TableTreeNodeObject ttno =  (TableTreeNodeObject)x.getNode();
	    if ( (tableName.startsWith("%") ) || ( tableName.startsWith(ttno.getTable()) ) ) {		
		database.addContent((Element)getDocPanel((DefaultMutableTreeNode)node.getChildAt(i)));
	    }
	}
	doc = new Document(database);

	tree.setSelectionRow(nodeIndex);
	return doc;
    }


    private Element getDocPanel(DefaultMutableTreeNode node) {
	XMLTreeNode x = new XMLTreeNode();
	x= (XMLTreeNode)node.getUserObject();
	TableTreeNodeObject ttno = new TableTreeNodeObject("a","a");
	ttno = (TableTreeNodeObject)x.getNode();

	Element elt = ttno.getElt();
		
	for (int i = 0;i < node.getChildCount();i++ ) {
	    elt.addContent((Element)getDocComponent((DefaultMutableTreeNode)node.getChildAt(i)));
	}
	return elt;
    }

      private Element getDocComponent(DefaultMutableTreeNode node) {
	XMLTreeNode x = new XMLTreeNode();
	x= (XMLTreeNode)node.getUserObject();

	PanelTreeNodeObject ptno = new PanelTreeNodeObject();
	ptno = (PanelTreeNodeObject)x.getNode();
	Element elt = ptno.getElt();
	
	
	for (int i = 0;i < node.getChildCount();i++ ) {
	    DefaultMutableTreeNode n = new DefaultMutableTreeNode();
	    n = (DefaultMutableTreeNode)node.getChildAt(i);
	    XMLTreeNode xn = new XMLTreeNode();
	    xn= (XMLTreeNode)n.getUserObject();
	    ComponentTreeNodeObject ctno = new ComponentTreeNodeObject();
	    ctno = (ComponentTreeNodeObject) xn.getNode();
	    elt.addContent((Element)ctno.getElt());
	}
	return elt;
    }
  



    public static void main(String args[]) {

	final CloseableFrame cf = new CloseableFrame("XML-Tree");
	final XMLEditor panel;

	if (args.length == 0 ) {
	    panel = new XMLEditor();
	} else {
	    panel = new XMLEditor((String)args[0]);
	}

	panel.exit.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		    }});
	panel.quit.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		    }});

	panel.bestView.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    cf.pack();
		    }});
	panel.pack.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    cf.pack();
		    }});



	
	cf.getContentPane().setLayout(new BorderLayout());
	cf.getContentPane().add(BorderLayout.CENTER,panel);
	cf.pack();
	cf.setSize(new Dimension(560,475));
	cf.setVisible(true);

    }
}
