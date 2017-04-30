package sql.fredy.ui;

/** 
    CalendarField is a CalendarField based on JCalendar from Kai Toedter
    The latest version of CalendarBean you find at www.geocities.com/SiliconValley/2123/calendarbean 
    it extends this to a setText and a getText-Method to integrate it into Admin-functions.


    Admin is a Tool around mySQL to do basic jobs
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
import CalendarBean.*;
import java.util.*;
import java.text.*;
import javax.swing.BorderFactory; 
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class CalendarField extends JButton {

    private JFrame    frame;
    public  JCalendar jcal;
 

    public void clear() { this.setDate(toDay()); }

    /** 
	this is to set the date in the form yyyy-mm-dd
    **/
    Calendar cal;

    public void setDate(String v) {
	StringTokenizer st = new StringTokenizer(v,"-");
	String y = " ";
	String m = " ";
	String d = " ";
	try {	    
	   y = st.nextToken();
	   m = st.nextToken();
	   d = st.nextToken();
	} catch (NoSuchElementException e) {
	    st = new StringTokenizer(toDay(),"-");
	    y = st.nextToken();
	    m = st.nextToken();
	    d = st.nextToken();	    
	}
	cal.set(Calendar.YEAR,Integer.parseInt(y));
	cal.set(Calendar.MONTH,Integer.parseInt(m)-1);
	cal.set(Calendar.DATE,Integer.parseInt(d));

     this.setText(v);
 
     jcal.setCalendar(cal);
    }


    public Calendar getCalendar() { return jcal.getCalendar(); }


    public String getDate() {

	Calendar cal1 = jcal.getCalendar();
	String y = Integer.toString(cal1.get(cal1.YEAR));
	String m = Integer.toString(cal1.get(cal1.MONTH)+1);
	m = trailing(m);
	String d = Integer.toString(cal1.get(cal1.DAY_OF_MONTH));
	d = trailing(d);

	this.setText( y + "-" + m + "-" + d);
	return y + "-" + m + "-" + d;

    }


    private String trailing(String v) {
	if (v.length() < 2 ) v = "0"+ v;
	return v;
    }

    private JPanel buttonPanel() {
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());

	ImageButton select = new ImageButton(null,"select.gif","Select");
	select.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		getDate();
		frame.dispose();
		}});
	ImageButton cancel = new ImageButton(null,"exit.gif","Cancel");
	cancel.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		frame.dispose();
		}});
	
	panel.add(select);
	panel.add(cancel);
	panel.setBorder(BorderFactory.createEtchedBorder());
	return panel;
    }

    private String toDay() {
	String y = Integer.toString(cal.get(Calendar.YEAR));
	String m = Integer.toString(cal.get(Calendar.MONTH)+1);
	m = trailing(m);
	String d = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
	d = trailing(d);
	
	return y + "-" + d + "-" + m;

    }

    public CalendarField(String v) {

     cal = Calendar.getInstance();
     this.setText(toDay());

     frame = new JFrame("Date of " + v);
     frame.addWindowListener(new WindowAdapter() {
	 public void windowActivated(WindowEvent e) {}
	 public void windowClosed(WindowEvent e) {}
	 public void windowClosing(WindowEvent e) {frame.dispose();}
	 public void windowDeactivated(WindowEvent e) {}
	 public void windowDeiconified(WindowEvent e) {}
	 public void windowIconified(WindowEvent e) {}
	 public void windowOpened(WindowEvent e) {}});
     
     jcal = new JCalendar();

     frame.getContentPane().setLayout(new BorderLayout());
     frame.getContentPane().add("Center",jcal);
     frame.getContentPane().add("South",buttonPanel());

     frame.pack();
     this.addActionListener(new ActionListener() {
	 public void actionPerformed(ActionEvent e) {	       
	     frame.setLocation(getPosition());
	     frame.setVisible(true);
	     }});
     
    }

    public Point getPosition() {
	Point p = new Point();
        p = this.getLocationOnScreen();
        return p;
    }

    public static void main(String args[]) {

	JFrame f = new JFrame();
	f.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent e) {}
	    public void windowClosed(WindowEvent e) {}
	    public void windowClosing(WindowEvent e) {System.exit(0);}
	    public void windowDeactivated(WindowEvent e) {}
	    public void windowDeiconified(WindowEvent e) {}
	    public void windowIconified(WindowEvent e) {}
	    public void windowOpened(WindowEvent e) {}});
	String v = "";
	if (args.length > 0) {
	    v = args[0];	    
	} else {
	    v = "a Test";
	}
	CalendarField cf = new CalendarField(v);
	f.getContentPane().add(cf);
	f.pack();
	f.setVisible(true);
    }
}
