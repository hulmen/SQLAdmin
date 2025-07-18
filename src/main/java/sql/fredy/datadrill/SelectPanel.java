/*
 * SelectPanel.java
 *
 * Created on September 20, 2003, 2:25 PM
 * Extended to set and get the content via a XMLS-Element
 * and to save the content into a file    March 8. 2004
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

import sql.fredy.connection.DataSource;
import sql.fredy.ui.ImageButton;
import java.util.Vector;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import java.sql.*;
import java.util.logging.*;
import java.util.Calendar;
import java.util.Iterator;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import java.beans.PropertyVetoException;

/**
 *
 * @author sql@hulmen.ch
 */
public class SelectPanel extends javax.swing.JPanel implements PanelInterface {

    String query;
    
    /**
     * Get the value of query.
     * @return value of query.
     */
    public String getQuery() {
	return query;
    }
    
    /**
     * Set the value of query.
     * @param v  Value to assign to query.
     */
    public void setQuery(String  v) {
	this.query = v;
    }
    
    
    String prefix;
    
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
    

    /** Creates new form SelectPanel */
    public SelectPanel() {
        initComponents();
    }
    
    public SelectPanel(String name,
		       String type,
		       String prefix,
		       String query) {
        this.setName(name);
	this.setType(type);
	this.setPrefix(prefix);      
        initComponents();
	setQuery(query);
        setElements(query);
        addListeners();
    }

    Logger logger = Logger.getLogger("sql.fredy.datadrill");
   
    String type = "IN";
    
    /**
     * Get the value of type.
     * @return value of type.
     */
    public String getType() {
	return type;
    }
    
    /**
     * Set the value of type.
     * @param v  Value to assign to type.
     */
    public void setType(String  v) {
	this.type = v;
    }
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        leftList = new javax.swing.JList();
        jScrollPane2 = new javax.swing.JScrollPane();
        rightList = new javax.swing.JList();
        jPanel3 = new javax.swing.JPanel();
        select = new ImageButton(null,"vcrforward.gif","select");
        unselect = new ImageButton(null,"vcrback.gif","remove");

	load = new ImageButton(null,"load.gif","Load document");
	save = new ImageButton(null,"save.gif","Save document");
	

        setLayout(new java.awt.BorderLayout());

        leftList.setPrototypeCellValue("01234567890123456789012345678901234567890123456789");
        leftList.setVisibleRowCount(15);
        jScrollPane1.setViewportView(leftList);

        jPanel1.add(jScrollPane1);

        rightList.setPrototypeCellValue("01234567890123456789012345678901234567890123456789");
        rightList.setVisibleRowCount(15);
        jScrollPane2.setViewportView(rightList);

        jPanel1.add(jScrollPane2);

        jPanel2.add(jPanel1);

        add(jPanel2, java.awt.BorderLayout.CENTER);

	jPanel3.add(load);
        jPanel3.add(select);
        jPanel3.add(unselect);
	jPanel3.add(save);

        add(jPanel3, java.awt.BorderLayout.SOUTH);

    }//GEN-END:initComponents

    public Vector getSelectedItems() {       
        return getRhs();
    }    

    public String getData() {
	Vector v = getSelectedItems();
	StringBuffer sb = new StringBuffer();
	for (int i = 0;i < v.size(); i++) {
	    if ( i == 0 ) sb.append( getPrefix() + " " );
	    if ( ( i == 0 )  && ( getType().toUpperCase().equals("IN") ) ) sb.append("(");

	    if ((i > 0 )  && ( ! getType().toUpperCase().equals("SIMPLE") ) ) sb.append(" , ");
	    
	    if (getType().toUpperCase().equals("IN")) sb.append("'");
	    if (getType().toUpperCase().equals("SIMPLE")) sb.append(" ");
	    sb.append(v.elementAt(i));
	    if (getType().toUpperCase().equals("IN"))sb.append("'");
	}
	if ((sb.length() > 1)  && ( ! getType().toUpperCase().equals("SIMPLE") ) ) sb.append(")");
	return sb.toString();
    }    

       /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    public Connection getConnection() {
        Connection con = null;
        try {
            con = DataSource.getInstance().getConnection(); 
        } catch (IOException ex) { 
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) { 
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {
            return con;
        }
    }
    
    /*
     * Make sure to send a query that only has 1 return Element
     *
     **/
    public void setElements(java.lang.String query) {
   	if (query != null ) {
	    Vector v = new Vector();
            Connection con = null;
            Statement stmt = null;
            try {
                con = getConnection();
                stmt = con.createStatement();
	 	ResultSet sqlresult = stmt.executeQuery(query);
		while ( sqlresult.next() ) {                    
		    v.addElement(sqlresult.getString(1));	       
		}
	    } catch (SQLException sqle) {
		logger.log(Level.WARNING,"Can not access this file!");
		logger.log(Level.INFO,"Query: " + query);
		logger.log(Level.INFO,"SQL-Exception: " + sqle.getMessage());
                //sqle.printStackTrace();
	    } catch (Exception e) {
                logger.log(Level.WARNING,"something went wrong:" + e.getMessage());
                logger.log(Level.WARNING,"while trying: " + query );
                //e.printStackTrace();
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch (SQLException e ) {
                        logger.log(Level.WARNING,"Exception while closing statement {0}", e.getMessage());
                    }
                }
                if ( con != null ) {
                    try {
                        con.close();
                    } catch (SQLException e ) {
                        logger.log(Level.WARNING,"Exception while closing connection {0}", e.getMessage());
                    }
                }
            }

            leftList.setListData(v);
	}
    }    
    
    public void setRightData(java.util.Vector v) {
        rightList.setListData(v);
        javax.swing.ListModel lm = leftList.getModel();
        Vector newData = new Vector();
        for (int i=0;i < lm.getSize();i++) {
            String r = (String) lm.getElementAt(i);
            boolean isIn = false;
            for (int j = 0; j < v.size(); j++) {
                String l = (String)v.elementAt(j);
                if ( r.equals(l) ) isIn = true;
            }
            if ( ! isIn ) newData.addElement((String)lm.getElementAt(i));
        }
        leftList.setListData(newData);
        leftList.updateUI();
    }    
    
    private Vector getLhs() {
       //  get all Data from the left hand side
        Vector lhs = new Vector();
        javax.swing.ListModel lm = leftList.getModel();
        for (int i=0;i < lm.getSize();i++) 
            lhs.addElement((String) lm.getElementAt(i));
        return lhs;
    }
    private Vector getRhs() {
       //  get all Data from the right hand side
        Vector rhs = new Vector();
        javax.swing.ListModel lm = rightList.getModel();
        for (int i=0;i < lm.getSize();i++) 
            rhs.addElement((String) lm.getElementAt(i));
        return rhs;
    }    
    public void selectField() {
        Vector lhs = getLhs();
        Vector rhs = getRhs();
        Vector tmp = new Vector();
        int[] selectedIndices = leftList.getSelectedIndices();
        for ( int i=0; i < selectedIndices.length;i++) 
            rhs.addElement(lhs.elementAt(selectedIndices[i]));
       for ( int j = 0; j < lhs.size(); j++ )        
            if ( ! leftList.isSelectedIndex(j) ) tmp.addElement(lhs.elementAt(j));
        
        leftList.setListData(tmp);
        rightList.setListData(rhs);
        
        leftList.updateUI();
        rightList.updateUI();
    }
    
    public void unselectField() {
        Vector lhs = getLhs();
        Vector rhs = getRhs();
        Vector tmp = new Vector();
        int[] selectedIndices = rightList.getSelectedIndices();
        for ( int i=0; i < selectedIndices.length;i++) 
            lhs.addElement(rhs.elementAt(selectedIndices[i]));
        
        for ( int j = 0; j < rhs.size(); j++ )        
            if ( ! rightList.isSelectedIndex(j) ) tmp.addElement(rhs.elementAt(j));
        
        leftList.setListData(lhs);
        rightList.setListData(tmp);
        
        leftList.updateUI();
        rightList.updateUI();
    }    
    
    public void addListeners() {
        select.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectField();
        }});
        unselect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                unselectField();
        }});

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
	
	
    }
    

    // getting Information as XML-Element
    public Element getXML() {

	Element element = new Element("panel");

	Element name = new Element("name");
	name.setText(this.getName());
	element.addContent(name);

	Element type = new Element("type");
	type.setText("selectionpanel");
	element.addContent(type);

	Element q = new Element("query");
	q.setText(getQuery());
	element.addContent(q);


	Vector v = getRhs();
	for (int i=0;i < v.size();i++) {
	    Element s = new Element("selected");
	    s.setText((String)v.elementAt(i));
	    element.addContent(s);
	}

	return element;

    }

    private String toDay() {

        Calendar c = Calendar.getInstance();

	String m =   Integer.toString(c.get(Calendar.MONTH)+1);
	if ( m.length() < 2) m = "0" + m;
	
	String d = Integer.toString(c.get(Calendar.DATE));
	if (d.length() < 2) d = "0" + d;

	String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
	if (hour.length() < 2) hour = "0" + hour;
	
	String minute = Integer.toString(c.get(Calendar.MINUTE));
	if (minute.length() < 2) minute = "0" + minute;
	 
	String second = Integer.toString(c.get(Calendar.SECOND));
	if (second.length() < 2) second = "0" + second;
				    

        return  Integer.toString(c.get(Calendar.YEAR)) + "-" + m + "-" + d + "_" + hour + "-" + minute + "-" + second;
       
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

	    if ( type.equals("selectionpanel") ) {
		setElements(elt.getChild("query").getText());
		Iterator iter = elt.getChildren("selected").iterator();
		Vector v = new Vector();
		
		while ( iter.hasNext() ) {
		    Element e = (Element) iter.next();
		    v.addElement(e.getTextTrim());
		}
		setRightData(v);
	    }
	} catch (Exception e) {
	    logger.log(Level.WARNING,"Could not set data");
	    logger.log(Level.FINE,e.getMessage());
	    //e.printStackTrace();
	}
    }



    private String getFileFromChooser() {
	String fileName = null;
	GetFileName gfn = new GetFileName(new String[] { "xml","XML"} );       
	return gfn.getFileName();
    }   



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList leftList;
    private javax.swing.JList rightList;
    private ImageButton select;
    private ImageButton unselect;
    private ImageButton load,save;
    // End of variables declaration//GEN-END:variables

    public static void main(String args[]) {

	String h = "localhost";
	String u = System.getProperty("user.name");
	String d = null;
	String p = null;
	String q = null;
	String t = "IN";
	String pre = null;

	System.out.println("SelectPanel mit SQL-Zugriff\n" +
			   "Version 1.0  Fredy Fischer, \n" +
			   "Summer 2006\n" +
			   "Syntax: java sql.fredy.SelectPanel  -T selection type DB -q query -pre prefix\n" +
			   "Default Values: type = IN [IN|SELECT]\n" );

	int i = 0;
	while ( i < args.length) { 
	    if ((args[i].equals("-t") ) || (args[i].equals("-type")) ) {		
		i++;
		t=args[i];
	    } 
	    if ((args[i].equals("-q") ) || (args[i].equals("-query")) ) {		
		i++;
	       q=args[i];
	    } 
	    if ((args[i].equals("-pre") ) || (args[i].equals("-prefix")) ) {		
		i++;
		pre=args[i];
	    } 
	    i++;
	}


	final SelectPanel selectPanel = new SelectPanel("test",t,pre,q);

	JFrame frame = new JFrame("SelectPanel");
	frame.getContentPane().setLayout(new BorderLayout());
	frame.getContentPane().add(BorderLayout.CENTER,selectPanel);

	frame.addWindowListener(new WindowAdapter() {
                public void windowActivated(WindowEvent e) {}
                public void windowClosed(WindowEvent e) {}
                public void windowClosing(WindowEvent e) {		    
		    System.out.println(selectPanel.getData());
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
