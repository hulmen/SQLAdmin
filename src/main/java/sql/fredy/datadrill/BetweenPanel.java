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

package  sql.fredy.datadrill;

import sql.fredy.ui.ImageButton;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.text.*;
import javax.swing.border.*;
import java.util.logging.*;

import java.io.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;

public class BetweenPanel extends JPanel implements PanelInterface {

    JPanel box;
    BetweenPanelTableModel tableModel = new BetweenPanelTableModel();

    boolean und;
    
    /**
     * Get the value of und.
     * @return value of und.
     */
    public boolean isUnd() {
	return und;
    }
    
    /**
     * Set the value of und.
     * @param v  Value to assign to und.
     */
    public void setUnd(boolean  v) {
	this.und = v;
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
    
    boolean text;
    
    /**
     * Get the value of text.
     * @return value of text.
     */
    public boolean isText() {
	return text;
    }
    
    /**
     * Set the value of text.
     * @param v  Value to assign to text.
     */
    public void setText(boolean  v) {
	this.text = v;
    }
    

    // Logger
    Logger logger = Logger.getLogger("sql.fredy.datadrill");


    public BetweenPanel(String componentName,
			String prefix,
			int anzahlZeilen,
			boolean und,
			boolean text) {
	this.setName(componentName);
	this.setUnd(und);
	this.setText(text);
	this.setPrefix(prefix);
	this.setLayout(new BorderLayout());
	this.add(BorderLayout.CENTER,betweenpanel(anzahlZeilen));
	this.add(BorderLayout.SOUTH,buttonPanel());

    }
    private JScrollPane betweenpanel(int az) {
	Vector v ;
	for (int i = 0;i< az; i++) {
	    v = new Vector();
	    v.addElement((String)"");
	    v.addElement((String)"");
	    tableModel.addRow(v);
	}
	JTable table = new JTable(tableModel);
	table.getTableHeader().setReorderingAllowed(false);
	table.setPreferredScrollableViewportSize(new Dimension(200,200));

	return new JScrollPane(table);	
    }


    private JPanel buttonPanel() {

	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());

	ImageButton save = new ImageButton(null,"save.gif","Save data to file");
	ImageButton load = new ImageButton(null,"load.gif","Load data from file");
	ImageButton plus = new ImageButton(null,"plusplus.gif","Add another 50 rows to table");

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
	plus.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    for (int i = 0;i< 50; i++) {
			Vector v = new Vector();
			v.addElement((String)"");
			v.addElement((String)"");
			tableModel.addRow(v);
		    }
		}
	    });
	
	panel.add(load);
	panel.add(save);
	panel.add(plus);

	return panel;


    }
    
    public String getData() {
	StringBuffer sb = new StringBuffer();
	// iteriere durch die Tabelle
	for (int i = 0;i < tableModel.getRowCount();i++) {
	    if ( i == 0 ) sb.append("(    ");
	    // nimm nur die, welche an beiden Stellen einen Wert haben
	    if ( (((String)tableModel.getValueAt(i,0)).length() > 0 ) &&  
		 (((String)tableModel.getValueAt(i,0)).length() > 0 ) ) {
		if ( i > 0 ) {
		    if ( isUnd() ) {
			sb.append("\n AND ");
		    } else {
			sb.append("\n OR  ");
		    }
		}
		sb.append(getPrefix());
		sb.append(" BETWEEN ");
		if ( isText() ) sb.append("'");
		sb.append((String)tableModel.getValueAt(i,0));
		if ( isText() ) sb.append("'");
		sb.append(" AND ");
		if ( isText() ) sb.append("'");
		sb.append((String)tableModel.getValueAt(i,1));
		if ( isText() ) sb.append("'");
	    }
	}
	if ( sb.length() > 1 ) sb.append(" )");
	if ( sb.toString().equals("(     )")) sb.delete(0,"(     )".length());
	return sb.toString();
    }


    public static void main(String a[]) {
	System.out.println("BetWeenPanel prefix NoRowxs AND|OR TEXT|NUMBER");
	if (a.length >2) {
	    JFrame frame = new JFrame("BetWeenBoxPanel"); 	   
	    frame.getContentPane().setLayout(new BorderLayout());

	    String prefix = a[0];
	    boolean und   = false;
	    boolean text  = false;
	    if ( a[2].toLowerCase().equals("and") ) und = true;
	    if ( a[3].toLowerCase().equals("text")) text = true;
		

	    final BetweenPanel bp = new BetweenPanel("test",
						     prefix,
						     Integer.parseInt(a[1]),
						     und,text);
	    frame.getContentPane().add(BorderLayout.CENTER,bp);
	    frame.addWindowListener(new WindowAdapter() {
                public void windowActivated(WindowEvent e) {}
                public void windowClosed(WindowEvent e) {}
                public void windowClosing(WindowEvent e) {
		    System.out.println(bp.getData());
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


    // getting Information as XML-Element
    public Element getXML() {

	Element element = new Element("panel");

	Element name = new Element("name");
	name.setText(this.getName());
	element.addContent(name);

	Element type = new Element("type");
	type.setText("betweenpanel");
	element.addContent(type);

	for (int i = 0;i < tableModel.getRowCount();i++) {

	    // nimm nur die, welche an beiden Stellen einen Wert haben
	    if ( (((String)tableModel.getValueAt(i,0)).length() > 0 ) &&  
		 (((String)tableModel.getValueAt(i,0)).length() > 0 ) ) {

		Element row = new Element("row");

		Element left = new Element("left");
		left.setText((String)tableModel.getValueAt(i,0));
		row.addContent(left);

		Element right = new Element("right");
		right.setText((String)tableModel.getValueAt(i,1));
		row.addContent(right);

		element.addContent(row);
	    }
	}	


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

	    if ( type.equals("betweenpanel") ) {
		Iterator iter = elt.getChildren("row").iterator();
		
		// clear the existing data
		tableModel.clearData();

		while ( iter.hasNext() ) {
		    Element e = (Element) iter.next();

		    Vector v = new Vector();
		    v.addElement((String) e.getChild("left").getText());
		    v.addElement((String) e.getChild("right").getText());
		    tableModel.addRow(v);

		}
	    }
	} catch (Exception e) {
	    logger.log(Level.WARNING,"Could not set data");
	    logger.log(Level.FINE,e.getMessage());
	    //e.printStackTrace();
	}
    }


    
    /**
     *   This saves the data into a XML-File,
     *   his file is stored within the users home-directory
     *  
     **/
    public void saveData() {
	Document doc = new Document(getXML());
	//XMLOutputter outputter = new XMLOutputter("\t",true);
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


}
