package sql.fredy.generate;

/** ComponentTreeNodeObject
 *  represents the Component for the XMLEditor
 *  it contains the following Info:
 *  - ComponentName
 *  - Java Type
 *  - FieldType
 *  - FieldTypeParameters
 *  - LabelConstraints
 *  - GridBagConstraints
 **/

/** FieldTypeParameters
 *  this delivers parameters for all possible fields
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


/**  * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
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


public class ComponentTreeNodeObject {

    
    String name;
    
    /**
       * Get the value of name.
       * @return value of name.
       */
    public String getName() {return name;}
    
    /**
       * Set the value of name.
       * @param v  Value to assign to name.
       */
    public void setName(String  v) {this.name = v;}
   

    
    String type;
    
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
    

    GridBagObject gbc;
    
    /**
       * Get the value of gbc.
       * @return value of gbc.
       */
    public GridBagObject getGbc() {return gbc;}
    
    /**
       * Set the value of gbc.
       * @param v  Value to assign to gbc.
       */
    public void setGbc(GridBagObject  v) {this.gbc = v;}
        
    GridBagObject labelGbc;
    
    /**
       * Get the value of labelGbc.
       * @return value of labelGbc.
       */
    public GridBagObject getLabelGbc() {
	return labelGbc;
    }
    
    /**
       * Set the value of labelGbc.
       * @param v  Value to assign to labelGbc.
       */
    public void setLabelGbc(GridBagObject  v) {this.labelGbc = v;}
    
 
    
    String guiType;
    
    /**
       * Get the value of guiType.
       * @return value of guiType.
       */
    public String getGuiType() {return guiType;}
    
    /**
       * Set the value of guiType.
       * @param v  Value to assign to guiType.
       */
    public void setGuiType(String  v) {this.guiType = v;}
    
   
    
    FieldTypeParameters parameter;
    
    /**
       * Get the value of parameter.
       * @return value of parameter.
       */
    public FieldTypeParameters getParameter() {return parameter;}
    
    /**
       * Set the value of parameter.
       * @param v  Value to assign to parameter.
       */
    public void setParameter(FieldTypeParameters  v) {this.parameter = v;}
    


    Element elt= new Element("component",getNs());
    
    /**
       * Get the value of elt.
       * @return value of elt.
       */
    public Element getElt() {
	elt = new Element("component",getNs());

	elt.setAttribute("name",getName());
	elt.setAttribute("type",getType());
	//elt.setAttribute("fieldType",parameter.getType());
	elt.setAttribute("fieldType",getGuiType());

	// LabelConstraints
	Element label = new Element("labelConstraints",getNs());
	labelGbc.setName("labelConstraints");
	label.addContent((Element)getLabelGbc().getElt());
	elt.addContent((Element)getLabelGbc().getElt());

	// Parameters
	elt.addContent((Element)parameter.getElt());

	// the GridBagConstraints
	elt.addContent((Element)getGbc().getElt());

	return elt;

    }
    
    /**
       * Set the value of elt.
       * @param v  Value to assign to elt.
       */
    public void setElt(Element v) {
	this.elt = v;
	setName((String)elt.getAttributeValue("name"));
        setType((String)elt.getAttributeValue("type"));
        setGuiType((String)elt.getAttributeValue("fieldType"));

	Element label = v.getChild("labelConstraints",getNs());
	GridBagObject go = new GridBagObject((Element)label);
	go.setName("labelConstraints");
        setLabelGbc(go);

	FieldTypeParameters pm = new FieldTypeParameters(getGuiType(),
							 getType(),
							 (Element) v.getChild("parameter",getNs()));
	setParameter(pm);

	setGbc(new GridBagObject((Element)v.getChild("gridBagConstraints",getNs())));

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
        
    public ComponentTreeNodeObject(Element v) {
	this.setElt(v);
    }
    public ComponentTreeNodeObject() {
    }

}
