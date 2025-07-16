/*
 * CriteriaPanel.java
 *
 * Created on November 20, 2003
 * Erstellt aus einer Datei eine Checkbox, damit
 * der Benutzer die Werte auswaehlen kann, die in der
 * WHERE-Condition einer SQL-Query verwendet wird. 
 * erstellt wird ein IN-Fragment.
 *
 * Verzeichnis: resourcen
 * Format     : Jede Zeile entspricht einer CheckBox
 *              [Ausgabe Text];[Rueckgabewert]           
 */


/*
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

package sql.fredy.datadrill;

import sql.fredy.ui.ImageButton;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import java.util.logging.*;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

/**
 *
 * @author  Fredy Fischer, Daniel Krebs
 */
public class CriteriaPanel extends javax.swing.JPanel implements PanelInterface {

    String filePath = null;
    String dateiName = null;
    Vector text=null;
    Vector wert=null;
    JPanel box;

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
	initVector();
    }
    
    String prefix="";
    
    /**
     * Get the value of prefix.
     * @return value of prefix.
     */
    public String getPrefix() {
	return prefix;
    }
    
    /**
     * Set the value of prefix.
     * @param v  Value to assign to prefix.
     */
    public void setPrefix(String  v) {
	this.prefix = v;
    }
    

    private void initVector() {
	ReadFile rf = new ReadFile(getDateiName());
	Vector v = rf.getLines();
	wert = new Vector();
	text = new Vector();
	for (int i=0;i < v.size();i++) {	
	    String s = (String) v.elementAt(i);
	    StringTokenizer st = new StringTokenizer((String)s,";");
	    try {
		text.addElement((String)st.nextToken());
		wert.addElement((String)st.nextToken());
	    } catch (NoSuchElementException nse) {
		logger.log(Level.INFO, "No such element exception when reading file {0}", getDateiName());
		logger.log(Level.FINE, "Error: {0}", nse.getMessage());
	    }
	}
    }


    private String getPath() {
	if ( filePath != null) return filePath;
        
        Properties prop = new Properties();
        
        
        /**
         * this is the same dir as the class: Admin.class.getResourceAsStream
         * but we put the props-File in the user's home-dir if there is not -D parameter for
         * place given at start
         **/
        try {
            
            String admin_dir = System.getProperty("admin.work");
            if (admin_dir  == null ) admin_dir = System.getProperty("user.home");
            
            try {
                FileInputStream fip = new FileInputStream(admin_dir + File.separator+"admin.selgui.props");
                prop.load(fip);
                fip.close();
            } catch (Exception fipEx) {
                logger.log(Level.INFO,"need to create properties for the first time");
            }
            
            if (prop.getProperty("selection.path") == null )  {
                filePath = admin_dir + File.separator + "datadrill" + File.separator + "resources" + File.separator;
                prop.put("selection.path",filePath);
                
                
                
                // write back properties
                try {
                    FileOutputStream fops = new FileOutputStream(admin_dir+File.separator+"admin.selgui.props");
                    prop.store(fops,"Selection properties Fredy's SqlAdmin");
                    fops.close();
                } catch (Exception propE) {
                    logger.log(Level.WARNING,"Can not save properties " + propE.getMessage());
                }
                
            } else {
                filePath = prop.getProperty("selection.path");
            }
            
            
            
        } catch (Exception ioex) {
            logger.log(Level.WARNING,"Something went wrong. " + ioex.getMessage());
        }
        return filePath;

    }


    // Logger
    Logger logger = Logger.getLogger("sql.fredy.datadrill");


    public CriteriaPanel(String componentName,String dateiName) {
	setDateiName(dateiName);
	this.setName(componentName);
	this.setLayout(new BorderLayout());
	this.add(BorderLayout.CENTER,checkboxpanel());
    }

    public CriteriaPanel(String componentName,String prefix,String dateiName) {
	setDateiName(dateiName);
	this.setName(componentName);
	this.setPrefix(prefix);
	this.setLayout(new BorderLayout());
	this.add(BorderLayout.CENTER,checkboxpanel());
	this.add(BorderLayout.SOUTH,buttonPanel());
    }
    private JPanel checkboxpanel() {
	box = new JPanel();
	box.setLayout(new GridLayout(13,0));

	for (int i=0;i < text.size();i++) 
	    box.add(new JCheckBox((String)text.elementAt(i),false));

	return box;
    }
    private JPanel buttonPanel() {

	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());

	ImageButton save = new ImageButton(null,"save.gif","Save data to file");
	ImageButton load = new ImageButton(null,"load.gif","Load data from file");

	panel.setBorder(new EtchedBorder());

	save.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    saveData();
		}
	    });
	load.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    loadData();
		}
	    });
	
	panel.add(load);
	panel.add(save);

        //it allows to select or unselect all
        final JCheckBox selectAll = new JCheckBox("select all");
        selectAll.setSelected(false);
        selectAll.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		Component c[] = box.getComponents();
		for (int i=0;i < c.length;i++) {
		    JCheckBox jcb = (JCheckBox)c[i];
		    jcb.setSelected(selectAll.isSelected());
		}
		}
	    });

        panel.add(selectAll);



	return panel;


    }    
    public String getData() {
	StringBuffer sb = new StringBuffer();

	Component c[] = box.getComponents();
        boolean first = false;
	for (int i=0;i < c.length;i++) {     
	    if (i == 0 ) {
		sb.append(" (");
	    }
		JCheckBox jcb = (JCheckBox)c[i];
		if ( jcb.isSelected() ) {
		    if (first) sb.append( ",");
		    first = true;
		    sb.append((String) wert.elementAt(i));
		}		       
	}
	sb.append(")");
	String r =  sb.toString();
	r = r.trim();
        if ( r.startsWith("()") ){
	    r="";
	} else {
	    r= getPrefix() + " " + sb.toString();
	}
	    
	return r;
    }

    // getting Information as XML-Element
    public Element getXML() {

	Element element = new Element("panel");

	Element name = new Element("name");
	name.setText(this.getName());
	element.addContent(name);

	Element type = new Element("type");
	type.setText("criteriapanel");
	element.addContent(type);

	Element boxes = new Element("checkbox");	

	Component c[] = box.getComponents();
	for (int i=0;i < c.length;i++) {     
		JCheckBox jcb = (JCheckBox)c[i];
		if ( jcb.isSelected() ) {
		    Element chk = new Element("selected");
		    chk.setText((String)text.elementAt(i));
		    boxes.addContent(chk);
		}		      
	}
	element.addContent(boxes);

	return element;

    }


    public void loadData() {
	Document doc = null;
	
	String fileName = getFileFromChooser();
	if ( fileName != null ) {
	    try {
		SAXBuilder builder = new SAXBuilder();
		doc = builder.build(new File(fileName));
	    } catch (JDOMException jde) {
	    	logger.log(Level.WARNING,"Could not read file");
	    	logger.log(Level.FINE,jde.getMessage());
	    } catch (IOException ioex) {
	    	logger.log(Level.WARNING,"Could not read file");
	    	logger.log(Level.FINE,ioex.getMessage());
	    }
	    if ( doc != null ) setAllValues(doc.getRootElement());
	}
    }

    public void setAllValues(Element elt) {
        
	try {
	    String type = elt.getChild("type").getText();

	    if ( type.equals("criteriapanel") ) {
		
		Element elt1 = elt.getChild("checkbox");
		Iterator iter = (elt1.getChildren("selected")).iterator();

		// first we clear all the selected CheckBoxes
		Component c[] = box.getComponents();
		for (int i=0;i < c.length;i++) {     
		    JCheckBox jcb = (JCheckBox)c[i];
		    jcb.setSelected(false);
		}


		// then we select all the ones, that are marked as selected
		while ( iter.hasNext() ) {
		    Element e = (Element) iter.next();
		    String v = e.getText();

		    for (int i=0;i < c.length;i++) {     
			JCheckBox jcb = (JCheckBox)c[i];
			if ( jcb.getText().equals(v) ) jcb.setSelected(true);
		    }
		}
	    }
	} catch (Exception e) {
	    logger.log(Level.WARNING,"Could not set data");
	    logger.log(Level.FINE,e.getMessage());
	}
    }


    
    /**
     *   This saves the data into a XML-File,
     *   his file is stored within the users home-directory
     *  
     **/
    public void saveData() {
	Document doc = new Document(getXML());
	XMLOutputter outputter = new XMLOutputter();
	
	String fileName = getFileFromChooser();
	if ( fileName != null) {
	    try {
		DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
		outputter.output(doc,out);
		logger.log(Level.FINEST,"Writing into file: " + fileName);       
	    } catch ( IOException ioex) {
		logger.log(Level.WARNING,"Could not write data");
		logger.log(Level.INFO,ioex.getMessage());
	    }
	}
    }



    private String getFileFromChooser() {
	String fileName = null;
	GetFileName gfn = new GetFileName(new String[] { "xml","XML"} );       
	return gfn.getFileName();
    }   



    public static void main(String a[]) {
	if (a.length >= 1) {
	    JFrame frame = new JFrame("CriteriaPanel");
	    frame.getContentPane().setLayout(new BorderLayout());
	    final CriteriaPanel cbp = new CriteriaPanel("test",a[0]);
	    frame.getContentPane().add(BorderLayout.CENTER,cbp);
	    frame.addWindowListener(new WindowAdapter() {
                public void windowActivated(WindowEvent e) {}
                public void windowClosed(WindowEvent e) {}
                public void windowClosing(WindowEvent e) {
		    System.out.println(cbp.getData());
		    System.exit(0);
		}
                public void windowDeactivated(WindowEvent e) {}
                public void windowDeiconified(WindowEvent e) {}
                public void windowIconified(WindowEvent e) {}
                public void windowOpened(WindowEvent e) {}});

	    frame.pack();
	    frame.setVisible(true);
	}
    }
}
