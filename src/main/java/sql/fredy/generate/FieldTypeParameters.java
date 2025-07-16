package sql.fredy.generate;

/** FieldTypeParameters
 *  this delivers parameters for all possible fields
 *  - type (GUI-Type)
 *  - javaType
 *  - label
 *  - length
 *  - text
 *  - filter
 *  - titled
 *  - title
 *  - layout (0,1,2)
 *  - rows
 *  - cols
 *  - linewrap
 *  - query


/** XMLEditor edits the XML-file used for Generate Code
 *  and is a part of Admin...
 *  Version 1.0  23. March 2002
 *  Fredy Fischer 
 *
 **/


/**  
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


import org.jdom2.Element;
import org.jdom2.Namespace;


public class FieldTypeParameters {

    
    String type="";
    
    /**
       * Get the value of type.
       * @return value of type.
       */
    public String getType() {return type;}
    
    /**
       * Set the value of type.
       * @param v  Value to assign to type.
       */
    public void setType(String  v) {this.type = v;}
    

    
    String javaType;
    
    /**
       * Get the value of javaType.
       * @return value of javaType.
       */
    public String getJavaType() {return javaType;}
    
    /**
       * Set the value of javaType.
       * @param v  Value to assign to javaType.
       */
    public void setJavaType(String  v) {this.javaType = v;}
    

    
    String label="";
    
    /**
       * Get the value of label.
       * @return value of label.
       */
    public String getLabel() {return label;}
    
    /**
       * Set the value of label.
       * @param v  Value to assign to label.
       */
    public void setLabel(String  v) {this.label = v;}
    
    
    int length=25;
    
    /**
       * Get the value of length.
       * @return value of length.
       */
    public int getLength() {return length;}
    
    /**
       * Set the value of length.
       * @param v  Value to assign to length.
       */
    public void setLength(int  v) {this.length = v;}
    
    
    String text="";
    
    /**
       * Get the value of text.
       * @return value of text.
       */
    public String getText() {return text;}
    
    /**
       * Set the value of text.
       * @param v  Value to assign to text.
       */
    public void setText(String  v) {this.text = v;}
        
    String filter="";
    
    /**
       * Get the value of filter.
       * @return value of filter.
       */
    public String getFilter() {return filter;}
    
    /**
       * Set the value of filter.
       * @param v  Value to assign to filter.
       */
    public void setFilter(String  v) {
       if (v == null ) v = "null";	  
      this.filter = v;

    }
    
    
    boolean titled=false;
    
    /**
       * Get the value of titled.
       * @return value of titled.
       */
    public boolean isTitled() {return titled;}
    
    /**
       * Set the value of titled.
       * @param v  Value to assign to titled.
       */
    public void setTitled(boolean  v) {this.titled = v;}
    
    
    String layout="0";
    
    /**
       * Get the value of layout.
       * @return value of layout.
       */
    public String getLayout() {return layout;}
    
    /**
       * Set the value of layout.
       * @param v  Value to assign to layout.
       */
    public void setLayout(String  v) {this.layout = v;}
    
    private String getBoolean(boolean v) {
	if (v) {
	    return "true";
	} else {
	    return "false";
	}
    }
   
    boolean linewrap=false;
    
    /**
       * Get the value of linewrap.
       * @return value of linewrap.
       */
    public boolean isLinewrap() {return linewrap;}
    
    /**
       * Set the value of linewrap.
       * @param v  Value to assign to linewrap.
       */
    public void setLinewrap(boolean  v) {this.linewrap = v;}
    
    
    String title;
    
    /**
       * Get the value of title.
       * @return value of title.
       */
    public String getTitle() {return title;}
    
    /**
       * Set the value of title.
       * @param v  Value to assign to title.
       */
    public void setTitle(String  v) {this.title = v;}
    
    
    int rows=10;
    
    /**
       * Get the value of rows.
       * @return value of rows.
       */
    public int getRows() {return rows;}
    
    /**
       * Set the value of rows.
       * @param v  Value to assign to rows.
       */
    public void setRows(int  v) {this.rows = v;}
    
    
    int cols=25;
    
    /**
       * Get the value of cols.
       * @return value of cols.
       */
    public int getCols() {return cols;}
    
    /**
       * Set the value of cols.
       * @param v  Value to assign to cols.
       */
    public void setCols(int  v) {this.cols = v;}
    
    
    String query="select ";
    
    /**
       * Get the value of query.
       * @return value of query.
       */
    public String getQuery() {return query;}
    
    /**
       * Set the value of query.
       * @param v  Value to assign to query.
       */
    public void setQuery(String  v) {
      if (v == null ) v = "select ";
      this.query = v;
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


    Element elt = new Element("parameter",getNs());

    public Element getElt() {
	elt = new Element("parameter",getNs());
	elt.setAttribute("label",getLabel()); 
	elt.setAttribute("length",getI2S(getLength()));
	elt.setAttribute("rows",  getI2S(getRows()));
        elt.setAttribute("cols",  getI2S(getCols()));
	elt.setAttribute("text",getText());
	elt.setAttribute("filter",getFilter());
	elt.setAttribute("title",getTitle());
	elt.setAttribute("query",getQuery());
	elt.setAttribute("layout",getLayout());
	elt.setAttribute("titled",getBoolean(isTitled()));
	elt.setAttribute("lineWrap",getBoolean(isLinewrap()));

	return elt;
    }

    private String getI2S(int i) { 
	try {
	    return Integer.toString(i); 
	} catch (NumberFormatException nfe) {
	    return "0";
	}
    }
    private int getS2I(String s) { 
	try {
	    return Integer.parseInt(s); 
	} catch (NumberFormatException nfe) {
	    return 0;
	}
    }
    private boolean setBoolean(String s) {
	if (s == null ) s = "false";
	if ( s.toLowerCase().startsWith("true")) {
	    return true;
	} else {
	    return false;
	}
    }

    public void setElt(Element v) {
	elt = new Element("parameter",getNs());
	setLabel((String)v.getAttributeValue("label"));
	setLength((int)getS2I((String)v.getAttributeValue("length")));
	setRows((int)getS2I((String)v.getAttributeValue("rows")));
	setCols((int)getS2I((String)v.getAttributeValue("cols")));
	setText((String)v.getAttributeValue("text"));
	setFilter((String)v.getAttributeValue("filter"));
	setTitle((String)v.getAttributeValue("title"));
	setQuery((String)v.getAttributeValue("query"));
	setLayout((String)v.getAttributeValue("layout"));
	setTitled((boolean)setBoolean((String)v.getAttributeValue("titled")));
	setLinewrap((boolean)setBoolean((String)v.getAttributeValue("linewrap")));
    }

    public FieldTypeParameters(String guiType,String javaType,Element e) {
	this.setType(guiType);
	this.setJavaType(javaType);
	this.setElt(e);
    }

    public FieldTypeParameters() {
    }
}

