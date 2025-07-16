package sql.fredy.generate;

/** GridBagObject
 *  is used to represent a GridBagConstraint for the XMLEditor
 *
 *  XMLEditor edits the XML-file used for Generate Code
 *  and is a part of Admin...
 *  Version 1.0  23. March 2002
 *  Fredy Fischer 
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


import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.StringTokenizer;

import org.jdom2.Element;
import org.jdom2.Namespace;


public class GridBagObject {


    public boolean debug = false;
    
    String insets="2,2,2,2";
    
    /**
       * Get the value of insets.
       * @return value of insets.
       */
    public String getInsets() {return insets;}
    
    /**
       * Set the value of insets.
       * @param v  Value to assign to insets.
       */
    public void setInsets(String  v) {this.insets = v;}
    
    
    String anchor="";
    
    /**
       * Get the value of anchor.
       * @return value of anchor.
       */
    public String getAnchor() {return anchor;}
    
    /**
       * Set the value of anchor.
       * @param v  Value to assign to anchor.
       */
    public void setAnchor(String  v) {this.anchor = v;}
    
    
    String fill="";
    
    /**
       * Get the value of fill.
       * @return value of fill.
       */
    public String getFill() {return fill;}
    
    /**
       * Set the value of fill.
       * @param v  Value to assign to fill.
       */
    public void setFill(String  v) {this.fill = v;}
    
    
    String weightx="1.0";
    
    /**
       * Get the value of weightx.
       * @return value of weightx.
       */
    public String getWeightx() {return weightx;}
    
    /**
       * Set the value of weightx.
       * @param v  Value to assign to weightx.
       */
    public void setWeightx(String  v) {this.weightx = v;}
    
    
    String weighty="1.0";
    
    /**
       * Get the value of weighty.
       * @return value of weighty.
       */
    public String getWeighty() {return weighty;}
    
    /**
       * Set the value of weighty.
       * @param v  Value to assign to weighty.
       */
    public void setWeighty(String  v) {this.weighty = v;}
    
    
    String gridwidth="1";
    
    /**
       * Get the value of gridwidth.
       * @return value of gridwidth.
       */
    public String getGridwidth() {return gridwidth;}
    
    /**
       * Set the value of gridwidth.
       * @param v  Value to assign to gridwidth.
       */
    public void setGridwidth(String  v) {this.gridwidth = v;}
    
    
    String gridheight="1";
    
    /**
       * Get the value of gridheight.
       * @return value of gridheight.
       */
    public String getGridheight() {return gridheight;}
    
    /**
       * Set the value of gridheight.
       * @param v  Value to assign to gridheight.
       */
    public void setGridheight(String  v) {this.gridheight = v;}
    
    
    String gridx="1";
    
    /**
       * Get the value of gridx.
       * @return value of gridx.
       */
    public String getGridx() {return gridx;}
    
    /**
       * Set the value of gridx.
       * @param v  Value to assign to gridx.
       */
    public void setGridx(String  v) {this.gridx = v;}
    
    
    String gridy="1";
    
    /**
       * Get the value of gridy.
       * @return value of gridy.
       */
    public String getGridy() {return gridy;}
    
    /**
       * Set the value of gridy.
       * @param v  Value to assign to gridy.
       */
    public void setGridy(String  v) {this.gridy = v;}
    

    
    GridBagConstraints gbc = new GridBagConstraints();
    
    /**
       * Get the value of gbc.
       * @return value of gbc.
       */
    public GridBagConstraints getGbc() {
	gbc.insets = getInsetValue();
	int anchor = GridBagConstraints.CENTER;
	if ( getAnchor().startsWith("GridBagConstraints.NORTH" )     )   anchor = GridBagConstraints.NORTH;
	if ( getAnchor().startsWith("GridBagConstraints.SOUTH" )     )   anchor = GridBagConstraints.SOUTH;
	if ( getAnchor().startsWith("GridBagConstraints.WEST" )      )   anchor = GridBagConstraints.WEST;
	if ( getAnchor().startsWith("GridBagConstraints.EAST" )      )   anchor = GridBagConstraints.EAST;
	if ( getAnchor().startsWith("GridBagConstraints.NORTHWEST" ) )   anchor = GridBagConstraints.NORTHWEST;
	if ( getAnchor().startsWith("GridBagConstraints.NORTHEAST" ) )   anchor = GridBagConstraints.NORTHEAST;
	if ( getAnchor().startsWith("GridBagConstraints.SOUTHWEST" ) )   anchor = GridBagConstraints.SOUTHWEST;
	if ( getAnchor().startsWith("GridBagConstraints.SOUTHEAST" ) )   anchor = GridBagConstraints.SOUTHEAST;
	gbc.anchor = anchor;

	int fill      = GridBagConstraints.NONE;

	if ( getFill().startsWith("GridBagConstraints.HORIZONTAL" ) ) fill = GridBagConstraints.HORIZONTAL;
	if ( getFill().startsWith("GridBagConstraints.VERTICAL" )   ) fill = GridBagConstraints.VERTICAL;
	if ( getFill().startsWith("GridBagConstraints.BOTH" )       ) fill = GridBagConstraints.BOTH;
	
	gbc.fill = fill;
	
	gbc.weightx    = Double.parseDouble(getWeightx());
	gbc.weighty    = Double.parseDouble(getWeighty());
	gbc.gridheight = Integer.parseInt(getGridheight());
	gbc.gridwidth  = Integer.parseInt(getGridwidth());
	gbc.gridx      = Integer.parseInt(getGridx());
	gbc.gridy      = Integer.parseInt(getGridy());
   

	if (debug) {
	    System.out.println("---- GridBagObject output gbc -----------------\n" + 
			       "Anchor  : " + getAnchor() + "\n" +
			       "Fill    : " + getFill()   + "\n" +
			       "insets  : " + gbc.insets.top + "," + gbc.insets.left + "," 
			       + gbc.insets.bottom + "," + gbc.insets.right +  "\n" +
			       "Weightx : " + gbc.weightx + "\n" +
			       "Weighty : " + gbc.weighty + "\n" +
			       "Height  : " + gbc.gridheight + "\n" +
			       "Width   : " + gbc.gridwidth + "\n" +
			       "X       : " + gbc.gridx + "\n" +
			       "Y       : " + gbc.gridy + "\n" +
			       "-----------------------------------------------");
	}



	return gbc;
    }
    
    /**
       * Set the value of gbc.
       * @param v  Value to assign to gbc.
       */
    public void setGbc(GridBagConstraints  v) {
	this.gbc = v;
	if (gbc.anchor == GridBagConstraints.CENTER )    setAnchor("GridBagConstraints.CENTER");
	if (gbc.anchor == GridBagConstraints.NORTH )     setAnchor("GridBagConstraints.NORTH");
	if (gbc.anchor == GridBagConstraints.SOUTH )     setAnchor("GridBagConstraints.SOUTH");
	if (gbc.anchor == GridBagConstraints.WEST )      setAnchor("GridBagConstraints.WEST");
	if (gbc.anchor == GridBagConstraints.EAST )      setAnchor("GridBagConstraints.EAST");
	if (gbc.anchor == GridBagConstraints.NORTHWEST ) setAnchor("GridBagConstraints.NORTHWEST");
	if (gbc.anchor == GridBagConstraints.SOUTHWEST ) setAnchor("GridBagConstraints.SOUTHWEST");
	if (gbc.anchor == GridBagConstraints.NORTHEAST ) setAnchor("GridBagConstraints.NORTHEAST");
	if (gbc.anchor == GridBagConstraints.SOUTHEAST ) setAnchor("GridBagConstraints.SOUTHEAST");
	
	if ( gbc.fill == GridBagConstraints.NONE )       setFill("GridBagConstraints.NONE");
	if ( gbc.fill == GridBagConstraints.HORIZONTAL)	 setFill("GridBagConstraints.HORIZONTAL");
	if ( gbc.fill == GridBagConstraints.VERTICAL)    setFill("GridBagConstraints.VERTICAL");
	if ( gbc.fill == GridBagConstraints.BOTH)        setFill("GridBagConstraints.BOTH");


	setInsets(Integer.toString(gbc.insets.top) + "," +
		  Integer.toString(gbc.insets.left) + "," +
		  Integer.toString(gbc.insets.bottom) + "," +
		  Integer.toString(gbc.insets.right)  );

	setWeightx(Double.toString(gbc.weightx));
	setWeighty(Double.toString(gbc.weighty));
	setGridwidth(Integer.toString(gbc.gridwidth));
	setGridheight(Integer.toString(gbc.gridheight));
	setGridx(Integer.toString(gbc.gridx));
	setGridy(Integer.toString(gbc.gridy));

    }
    
    public Insets getInsetValue() {
	int ins[] = new int[4];
	    StringTokenizer st = new StringTokenizer(getInsets(),",");
	    for (int i=0;i <4;i++) {
		try {
		    ins[i] = Integer.parseInt(st.nextToken());
		} catch (Exception nfe) {
		    ins[i] = 2;
		}
	    }
    
	return new Insets(ins[0],ins[1],ins[2],ins[3]);
    }


    Namespace ns = Namespace.getNamespace("admin","Fredys-Admintool");
    
    /**
       * Get the value of ns.
       * @return value of ns.
       */
    public Namespace getNs() {return ns;}
    
    /**
       * Set the value of ns.
       * @param v  Value to assign to ns.
       */
    public void setNs(Namespace  v) {this.ns = v;}
    

    
    String name="gridBagConstraints";
    
    /**
       * Get the value of name.
       * @return value of name.
       */
    public String getName() {return name;}
    
    /**
       * Set the value of name.<
       * @param v  Value to assign to name.
       */
    public void setName(String  v) {this.name = v;}
    
    
    Element elt;
    
    /**
       * Get the value of  Element.
       * @return value of  Element.
       */
    public Element getElt() {

	elt = new Element(getName(),getNs());
        elt.setAttribute("insets",insets);
	elt.setAttribute("anchor",anchor);
	elt.setAttribute("fill",fill);
	elt.setAttribute("weightx",weightx);
	elt.setAttribute("weighty",weighty);
	elt.setAttribute("gridheight",gridheight);
	elt.setAttribute("gridwidth",gridwidth);
	elt.setAttribute("gridx",gridx);
	elt.setAttribute("gridy",gridy);

	return elt;
    }
    
    /**
       * Set the value of gElement.
       * @param v  Value to assign to gElement.
       */
    public void setElt(Element  v) {
	this.elt = v;

	setName(v.getName());
	insets    = v.getAttributeValue("insets");
	anchor    = v.getAttributeValue("anchor");
        fill      = v.getAttributeValue("fill"); 
        weightx   = v.getAttributeValue("weightx");
	weighty   = v.getAttributeValue("weighty");
        gridheight= v.getAttributeValue("gridheight");
        gridwidth = v.getAttributeValue("gridwidth");
	gridx     = v.getAttributeValue("gridx");
        gridy     = v.getAttributeValue("gridy");

    }
    

    public GridBagObject() {
	GridBagConstraints g = new GridBagConstraints();
	setGbc(g);
    }

    public GridBagObject(GridBagConstraints g) { 
	setGbc(g);
    }

    public GridBagObject(Element elt) {
	setElt(elt);
    }

    public GridBagObject(Namespace ns,Element elt) {
	setElt(elt);
	setNs(ns);
    }


}
