package sql.fredy.generate;

/** XMLTreeNode
 *  Represents a single Node within the XMLEdtior's tree
 *  This class is implemented by:
 *  - DbTreeNode
 *  - TableTreeNode
 *  - PanelTreeNode
 *  - ComponentTreeNode
 * 
 * it has these methods:
 * - setName / getName   the Name of the Object
 * - setText / getText   the Text to be displayed by the renderer
 * - setType / getType   what object does it contain (DB,TABLE,Panel,Component)
 * - setNode / getNode   the type-related object used 
 *
 * related classes:
 * - DbObject
 * - TableObject
 * - PanelObject
 * - ComponentObject
 *
 **/




/** XMLEditor edits the XML-file used for Generate Code
 *  and is a part of Admin...
 *  Version 1.0  23. March 2002
 *  Fredy Fischer 
 *
 **/


/* 
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

import javax.swing.*;

public class XMLTreeNode {
    
    String name="";
    
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
   


    String text;
    
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
    
    
    Object node;
    
    /**
       * Get the value of node.
       * @return value of node.
       */
    public Object getNode() {return node;}
    
    /**
       * Set the value of node.
       * @param v  Value to assign to node.
       */
    public void setNode(Object  v) {this.node = v;}
    

}
