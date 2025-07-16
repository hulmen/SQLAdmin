/*
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


*/
package sql.fredy.datadrill;

import java.io.File;
import javax.swing.event.*;
import javax.swing.JFileChooser;


public class GetFileName extends JFileChooser {

    String fileName;
    
    /**
     * Get the value of fileName.
     * @return value of fileName.
     */
    public String getFileName() {
	return fileName;
    }
    
    MyFileFilter filter = null;
    
    /**
     * Get the value of filter.
     * @return value of filter.
     */
    public MyFileFilter getFilter() {
	return filter;
    }
    
    /**
     * Set the Filefilter 
     * @param extensions are the desired File-extensions
     * @param description the description of the filter
     */
    public void setFilter(String f[]) {
	filter = new MyFileFilter(f);
    }
    
    /**
     * Set the value of fileName.
     * @param v  Value to assign to fileName.
     */
    public void setFileName(String  v) {
	this.fileName = v;
    }
    
    public GetFileName(String[] filter) {
	this.setFilter(filter);
	this.setDialogType(JFileChooser.OPEN_DIALOG);
	this.setDialogTitle("Open File");
	if (getFilter() != null) this.setFileFilter(getFilter());

	this.setVisible(true);
	int returnVal = this.showOpenDialog(null);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	    setFileName(this.getSelectedFile().getParent()
			+ File.separator +
			this.getSelectedFile().getName());
	} else { setFileName(null); }
    }
}
