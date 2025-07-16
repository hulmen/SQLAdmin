
package applications.basics;

/** 
    DBcomboBox creates a combobox out of the 1st Field a SQL-Query
    returns.


    Admin is a Tool around mySQL to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena


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
import javax.swing.*;
import java.io.*;
import java.util.*;

public class DatePanel extends JPanel {

   private JComboBox day, month, year;

   private String dbDate;
    public String getDbDate() {

	String dayDate    = (String)day.getSelectedItem();
        String monthDate  = (String)month.getSelectedItem();
        String yearDate   = (String)month.getSelectedItem();
        return dayDate + "-" + monthDate + "-" + yearDate;
    }
    public String getText() { return getDbDate(); }

    public void setDbDate(String dbd) {
    
    }

    public void setText(String dbd) { setDbDate(dbd);}

    public DatePanel() {

	this.setLayout(new FlowLayout());
	day = new JComboBox();
	for (int i=1;i <=31; i++) {
            day.addItem(Integer.toString(i));
	}
    
	month = new JComboBox();
	month.addItem("Jan");
	month.addItem("Feb");
	month.addItem("Mar");
	month.addItem("April");
	month.addItem("May");
	month.addItem("June");
	month.addItem("July");
	month.addItem("Aug");
	month.addItem("Sept");
	month.addItem("Oct");
	month.addItem("Nov");
	month.addItem("Dec");

	year = new JComboBox();
	for (int i=1990;i <=2036; i++) {
            year.addItem(Integer.toString(i));
	}
    	

	this.add(day);
	this.add(month);
        this.add(year);

    }

    public static void main(String args[]){

	DatePanel dp = new DatePanel();
	JFrame frame = new JFrame("Date");
	frame.getContentPane().setLayout(new BorderLayout());
	frame.getContentPane().add("Center",dp);
	frame.addWindowListener(new WindowAdapter() {
	    public void windowActivated(WindowEvent e) {}
	    public void windowClosed(WindowEvent e) {}
	    public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeactivated(WindowEvent e) {}
            public void windowDeiconified(WindowEvent e) {}
            public void windowIconified(WindowEvent e) {}
            public void windowOpened(WindowEvent e) {}});
	
	frame.pack();
    }
}
