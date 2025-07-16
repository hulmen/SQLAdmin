package applications.basics;


/** 
    FfAlignerLayoutPanel is a Panel that allows me to arrange
    the components inside the Panels for Admin much more easy
    


    Admin is a Tool around SQL Databases to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and  other usefull things in DB-arena

   
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



import java.awt.Component;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;

public class FfAlignerLayoutPanel extends JPanel {


    private int actPos = 1;
    private GridBagConstraints gbc;
    public int numberOfRows = 2;

    
    /**
       * Get the value of numberOfRows.
       * @return Value of numberOfRows.
       */
    public int getNumberOfRows() {return numberOfRows;}
    
    /**
       * Set the value of numberOfRows.
       * @param v  Value to assign to numberOfRows.
       */
    public void setNumberOfRows(int  v) {this.numberOfRows = v;}



    Insets insets;
    
    /**
       * Get the value of insets.
       * @return Value of insets.
       */
    public Insets getInsets() {return insets;}
    
    /**
       * Set the value of insets.
       * @param top    = the inset from the top
       * @param left   = the inset from the left
       * @param bottom = the inset from the bottom
       * @param right  = the inset from the right
       */
    public void setInsets(int top, int left, int bottom, int right) {
	Insets i = new Insets(top, left, bottom, right);
	this.insets = i;
    }
    


    public FfAlignerLayoutPanel() {
	insets = new Insets(5,5,5,5);
	gbc = new GridBagConstraints();
	this.setLayout(new GridBagLayout());
    }

    /**
       * Adds a component to the Panel
       * @param c = Component to add
       */


    public void addComponent(Component c) {
	gbc.insets = getInsets();
	if ( actPos < getNumberOfRows() ) {
	    gbc.anchor    = GridBagConstraints.NORTHEAST;
	    gbc.gridwidth = GridBagConstraints.RELATIVE;
	} else {	    
 	    gbc.anchor    = GridBagConstraints.WEST;
	    gbc.gridwidth = GridBagConstraints.REMAINDER;   
	}
	this.add(c,gbc);
	actPos = actPos + 1;
        if ( actPos > numberOfRows ) actPos = 1;
    }
}
