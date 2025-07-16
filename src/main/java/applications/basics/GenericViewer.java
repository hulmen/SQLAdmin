package applications.basics;

/** 
    GenericViewer spawns an external Viewer based onto :
    .mailcap and .mime.types in the user.home-directory

    this first version supports only one entry per line

    Admin is a Tool around SQL Databases to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and other usefull things in DB-arena

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


import java.io.*;
import java.util.*;

public class GenericViewer {

  String appl = "";
  String param = "";
  
  String mimeTypes=".mime.types";
  public void setMimeTypes(String v) { this.mimeTypes = v; }
  public String getMimeTypes() { return mimeTypes; }

  String mailcap=".mailcap";
  public void setMailcap(String v) { this.mailcap = v; }
  public String getMailcap() { return mailcap; }


  Vector caps,types;
  private void setViewers() {
    caps = new Vector();
    types= new Vector();

    // Read .mime.types
    ReadFile mt = new ReadFile(getMimeTypes());
    ReadFile mc = new ReadFile(getMailcap());

    Vector vt = new Vector();
    Vector vm = new Vector();

    vt = mt.getLines();
    vm = mc.getLines();

    // set fileTyp and its extension out of .mime.types
    for (int i = 0; i < vt.size(); i++ ) {
      String s1 = (String)vt.elementAt(i);
      if ( ! s1.startsWith("#") ) {
	StringTokenizer st = new StringTokenizer(s1);
	ViewerTypes vy = new ViewerTypes();
	vy.setTyp(st.nextToken());
	vy.setExtension(st.nextToken());

	//System.out.println(vy.getTyp() + " " + vy.getExtension());

	types.addElement((ViewerTypes)vy);
      }
    }
    
    // set Applications 
    for (int j = 0; j < vm.size(); j++ ) {
      String s2 = (String)vm.elementAt(j);
      if ( ! s2.startsWith("#") ) {
	StringTokenizer st = new StringTokenizer(s2,";");
	ViewerExtension ve = new ViewerExtension();
	ve.setExtension(st.nextToken());
	ve.setViewer(st.nextToken());

	//System.out.println(ve.getExtension() + " " + ve.getViewer());

	caps.addElement((ViewerExtension)ve);
      }
    }
  }

  public void launchViewer(String file) {
    StringTokenizer st = new StringTokenizer(file,".");
    String extension = "";

    // the extension is the last token....
    while (st.hasMoreTokens() ) {
      extension = st.nextToken();
    }
    //System.out.println("Extension = " + extension);

    // now find the Viewer
    // for this we first have to find the applicationtyp
    String app=null;
    for (int i = 0; i < types.size(); i++) {
      ViewerTypes vt = new ViewerTypes();
      vt = (ViewerTypes) types.elementAt(i);
      if (vt.getExtension().endsWith(extension) ) {      
	app = vt.getTyp();
	//System.out.println("Application = " + app);
	i = types.size()+1;
      }
    }

    // now we search the Viewer
    String viewer = null;
    for (int j = 0; j < caps.size(); j++) {
      ViewerExtension ve = new ViewerExtension();
      ve = (ViewerExtension) caps.elementAt(j);
      if (ve.getExtension().endsWith(app) ) {
	viewer = ve.getViewer();
	//System.out.println("Viewer is " + viewer);
	j = caps.size()+1;
      }
    }

    // launch the Viewer
    if ( viewer != null ) {
      execCmd(viewer,file);
    }

  }

  private void execCmd(String v, String c) {
    appl = v;
    param = c;
	Thread t = new Thread() {
	    public void run() {

		try {
		    Runtime rt = Runtime.getRuntime();
		    Process prcs = rt.exec(appl + " " + param);
		    DataInputStream d = new DataInputStream(
							    prcs.getInputStream());
			
		}
		catch (IOException ioe) {
		   System.out.println("IO-Exception: " + ioe);
		}
	    }
	};
	t.start();
  }

  public GenericViewer() {
    setViewers();
  }

  public GenericViewer(String f) {
    setViewers();
    launchViewer(f);
  }

  public static void main(String args[] ) {

    GenericViewer g = new GenericViewer(args[0]);


  }

}
