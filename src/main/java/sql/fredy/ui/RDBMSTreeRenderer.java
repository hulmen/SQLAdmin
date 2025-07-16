package sql.fredy.ui;

/** 
 
  RDBMSTreeRenderer is a part of Admin and is used to display the RDBMS as a
  tree
 
  Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
  for DB-Administrations, as create / delete / alter and query tables
  it also creates indices and generates simple Java-Code to access DBMS-tables
  and exports data into various formats
 
 
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
import javax.swing.tree.*;


public class RDBMSTreeRenderer extends DefaultTreeCellRenderer {

    public Component getTreeCellRendererComponent( JTree tree, 
						    Object value, 
						    boolean selected,
						    boolean expanded,
						    boolean leaf,
						    int row,
						    boolean hasFocus ) {
	super.getTreeCellRendererComponent(tree,
					   value,
					   selected,
					   expanded,
					   leaf,
					   row,
					   hasFocus);

	if (row == 0 ) setIcon(loadImage.getImage("dbtreetop.gif"));

	//System.err.println(((DefaultMutableTreeNode) value).toString() + " row is "  + row   + " index is " +  tree.getIndex( (DefaultMutableTreeNode) value) );




	return this;
    }


     LoadImage loadImage = new LoadImage();

}

						    
