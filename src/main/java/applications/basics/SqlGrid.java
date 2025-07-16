package applications.basics;


/** 
    sqlTable is the Grid you often meet in Admin, it was in fact the
    first stuff I wrote to learn more about meta-data.


    Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.BevelBorder;
import javax.swing.JLabel;
import javax.swing.JPanel; 
import javax.swing.JFrame;
//import javax.swing.SwingUtilities;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.print.*;
import java.util.*;
import java.awt.Dimension;
import java.awt.geom.*;


public class SqlGrid implements ActionListener, ListSelectionListener, Printable {


// Fredy's make Version
private static String fredysVersion = "Version 1.2.1.a  2000-2-27 11:38 Build: 13";

public String getVersion() {return fredysVersion; }

private String host, user, password, query;
public JTable tableView;
public JScrollPane scrollpane;
public JPanel panel;
private JFrame frame;

public SqlGrid (String host, String user, String password,String db, String query) {
  
 this.host = host;
 this.user = user;
 this.password = password;
 this.query = query;
 
 JDBCAdapter dt = new JDBCAdapter(host, user,password,db);
 dt.executeQuery(query);
 tableView = new JTable(dt);
 tableView.getSelectionModel().addListSelectionListener(this);
 tableView.getTableHeader().setReorderingAllowed(false);
 tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
 scrollpane = new JScrollPane();
 scrollpane.getViewport().add(tableView);
  
  panel = new JPanel();
  BorderLayout layout = new BorderLayout();
  panel.setLayout(layout); 
  panel.add("Center",scrollpane);

  JPanel printPanel = new JPanel();
  printPanel.setLayout(new FlowLayout());
  JButton printButton = new JButton("Print");
  printButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

          try{ 
            PrinterJob pj=PrinterJob.getPrinterJob();
            pj.setPrintable(SqlGrid.this);
            pj.printDialog();
            pj.print();
          }catch (Exception PrintException ) {System.out.println("PrintException: " +PrintException); }
	  }});
  
  printPanel.add(printButton);
  panel.add("South",printPanel);


    frame = new JFrame("SqlGrid");
    frame.addWindowListener(new WindowAdapter() {
	public void windowActivated(WindowEvent e) {}
	public void windowClosed(WindowEvent e) {}
	public void windowClosing(WindowEvent e) {System.exit(0);}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}});
    

    BorderLayout layout1 = new BorderLayout();
    frame.getContentPane().setLayout(layout1);   
    frame.getContentPane().add("Center",panel);
    RepaintManager.currentManager(frame).setDoubleBufferingEnabled(true);
    frame.pack();
    frame.setVisible(true); 


}

    // the printing-Stuff this one works...
    /**
       public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
            if (pi >= 1) {
                return Printable.NO_SUCH_PAGE;
            }

            g.translate(100, 100);

            paint (g);
            return Printable.PAGE_EXISTS;
        }


    **/

     public int print(Graphics g, PageFormat pageFormat, 
        int pageIndex) throws PrinterException {
	Graphics2D  g2 = (Graphics2D) g;
     	g2.setColor(Color.black);
     	int fontHeight=g2.getFontMetrics().getHeight();
     	int fontDesent=g2.getFontMetrics().getDescent();

     	//leave room for page number
     	double pageHeight = pageFormat.getImageableHeight()-fontHeight;
     	double pageWidth = pageFormat.getImageableWidth();
     	double tableWidth = (double) tableView.getColumnModel().getTotalColumnWidth();
     	double scale = 1; 
     	if (tableWidth >= pageWidth) {
		scale =  pageWidth / tableWidth;
	}

     	double headerHeightOnPage=
                      tableView.getTableHeader().getHeight()*scale;
     	double tableWidthOnPage=tableWidth*scale;

     	double oneRowHeight=(tableView.getRowHeight()+
                      tableView.getRowMargin())*scale;
     	int numRowsOnAPage=
                      (int)((pageHeight-headerHeightOnPage)/oneRowHeight);
     	double pageHeightForTable=oneRowHeight*numRowsOnAPage;
     	int totalNumPages= (int)Math.ceil((
                      (double)tableView.getRowCount())/numRowsOnAPage);

	// Debug
        System.out.println("PageIndex: " + pageIndex + "  Number of Pages: " + totalNumPages);

     	if(pageIndex>=totalNumPages) {
                      return NO_SUCH_PAGE;
     	}

     	g2.translate(pageFormat.getImageableX(), 
                       pageFormat.getImageableY());
     	g2.drawString("Page: "+(pageIndex+1),(int)pageWidth/2-35,
                      (int)(pageHeight+fontHeight-fontDesent));//bottom center

     	g2.translate(0f,headerHeightOnPage);
     	g2.translate(0f,-pageIndex*pageHeightForTable);

     	//If this piece of the table is smaller than the size available,
     	//clip to the appropriate bounds.
     	if (pageIndex + 1 == totalNumPages) {
                     int lastRowPrinted = numRowsOnAPage * pageIndex;
                     int numRowsLeft = tableView.getRowCount() - lastRowPrinted;
                     g2.setClip(0, (int)(pageHeightForTable * pageIndex),
                       (int) Math.ceil(tableWidthOnPage),
                       (int) Math.ceil(oneRowHeight * numRowsLeft));
     	}
     	//else clip to the entire area available.
     	else{    
                     g2.setClip(0, (int)(pageHeightForTable*pageIndex), 
                     (int) Math.ceil(tableWidthOnPage),
                     (int) Math.ceil(pageHeightForTable));        
     	}

     	g2.scale(scale,scale);
     	tableView.paint(g2);
     	g2.scale(1/scale,1/scale);
     	g2.translate(0f,pageIndex*pageHeightForTable);
     	g2.translate(0f, -headerHeightOnPage);
     	g2.setClip(0, 0,(int) Math.ceil(tableWidthOnPage), 
                               (int)Math.ceil(headerHeightOnPage));
     	g2.scale(scale,scale);
     	tableView.getTableHeader().paint(g2);//paint header at top

     	return Printable.PAGE_EXISTS;
   }




  //Handling List Events
  public void valueChanged(ListSelectionEvent e) {;}

  // handling AWT-Events
  public void actionPerformed(ActionEvent evt) {;}

  
  public static  void main(String args[]) {
    if (args.length != 5 ) {
     System.out.println("Syntax: java sqlTable host user password database query");
     }
  else {
    SqlGrid f = new SqlGrid(args[0], args[1], args[2], args[3], args[4]);


  }
}
}
