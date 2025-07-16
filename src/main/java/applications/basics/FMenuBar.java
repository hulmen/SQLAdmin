package applications.basics;

/**

   

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

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JSeparator;

public class FMenuBar extends JMenuBar {


    public ImageMenuItem insert, update, delete, clear, cancel, search;


    public FMenuBar() {

	insert   = new ImageMenuItem("Insert","insert.gif","Inserts a new row");
	update   = new ImageMenuItem("Update","update.gif","Update existing row");
	delete   = new ImageMenuItem("Delete","delete.gif","removes this row");
	clear    = new ImageMenuItem("Clear","clear.gif","clear the fields");
	cancel   = new ImageMenuItem("Cancel","exit.gif","leave...");
	//search   = new ImageMenuItem("Search","search.gif","search");

	ImageMenu  fileMenu = new ImageMenu("File","open.gif",null);
	ImageMenu  editMenu = new ImageMenu("Edit","edit.gif",null);	
	
	fileMenu.add(cancel);
	editMenu.add(insert);
	editMenu.add(update);
	editMenu.add(new JSeparator());	
	editMenu.add(clear);
	//editMenu.add(search);

	this.add(fileMenu);
	this.add(editMenu);

    }
}
