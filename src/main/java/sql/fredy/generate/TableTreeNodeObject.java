package sql.fredy.generate;

/** TableTreeNodeObject
 *  represents the Table for the XMLEditor
 *  it contains the following Info:
 *  - TableName
 *  - PrimaryKeys
 **/




/** XMLEditor edits the XML-file used for Generate Code
 *  and is a part of Admin...
 *  Version 1.0  23. March 2002
 *  Fredy Fischer 
 *
 * 
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

import java.util.Vector;
import java.util.StringTokenizer;

import org.jdom2.Element;
import org.jdom2.Namespace;


public class TableTreeNodeObject {


    String table;
    
    /**
       * Get the value of table.
       * @return value of table.
       */
    public String getTable() {return table;}
    
    /**
       * Set the value of table.
       * @param v  Value to assign to table.
       */
    public void setTable(String  v) {this.table = v;}
    

    
    Vector primaryKey = new Vector();
    
    /**
       * Get the value of primaryKey.
       * @return value of primaryKey.
       */
    public Vector getPrimaryKey() {return primaryKey;}
    
    /**
       * Set the value of primaryKey.
       * @param v  Value to assign to primaryKey.
       */
    public void setPrimaryKey(Vector  v) {this.primaryKey = v;}
    
    public void addKey(String k) {
	primaryKey.addElement((String) k );
    }

    public void doPrimaryKey(String s) {
	StringTokenizer st = new StringTokenizer(s,";");
	while (st.hasMoreTokens()) addKey(st.nextToken());	    
    }


    public String getPK() {
	String s="";
	for (int i =0; i < primaryKey.size() ; i++) {
	    if (i == 0 ) {
		s = (String) primaryKey.elementAt(i);
	    } else {
		s = s + ";" + (String) primaryKey.elementAt(i);
	    }
	}
	return s;
    }

    
    Element elt= new Element("table",getNs());
    
    /**
       * Get the value of elt.
       * @return value of elt.
       */
    public Element getElt() {
	elt = new Element("table",getNs());
	elt.setAttribute("name",getTable());
	elt.setAttribute("primaryKeys",getPK());

	return elt;

    }
    
    /**
       * Set the value of elt.
       * @param v  Value to assign to elt.
       */
    public void setElt(Element  v) {
	this.elt = v;
	setTable((String)elt.getAttributeValue("name"));
        doPrimaryKey((String)elt.getAttributeValue("primaryKeys"));	   
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
    
    public TableTreeNodeObject(String t, String p) {
	setTable(t);
	doPrimaryKey(p);
    }

    public TableTreeNodeObject(Element e) {
	setElt(e);
    }

    public TableTreeNodeObject (Namespace ns, Element e) {
	setNs(ns);
	setElt(e);
    }
}
