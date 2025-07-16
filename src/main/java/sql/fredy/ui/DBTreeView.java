package sql.fredy.ui;

/** 

   DBTreeView is a part of Admin, it displays a DB in atree

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
import javax.swing.*;
import sql.fredy.metadata.DBTree;


public class DBTreeView extends JPanel {


// Fredy's make Version
private static String fredysVersion = "Version 1.4  2. Jan. 2002 ";

public String getVersion() {return fredysVersion; }

    public JButton cancel;

    public DBTreeView(String host, String user, String password, String database) {
	DBTree dbTree = new DBTree(database);	
        init(dbTree);
    }

    public DBTreeView() {
	DBTree dbTree = new DBTree();	
        init(dbTree);
    }

    public void init(DBTree dbTree) {

	this.setLayout(new BorderLayout());
	

	this.add("Center",dbTree);
	this.add("South",buttonPanel());


    }

    private JPanel buttonPanel() {

	JPanel panel = new JPanel();
	cancel = new JButton("Cancel");
	panel.add(cancel);
	return panel;

    }

}
