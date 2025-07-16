package applications.basics;

/**

  ...Reads a file and it's content can be retrieved by getText()
     or a Vector containing every single line by getLines()

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
import java.util.Vector;


public class ReadFile {


    private String text;
    private Vector v;


    public Vector getLines() { return v;}

    
    /**
       * Get the value of text.
       * @return Value of text.
       */
    public String getText() {return text;}
    
    /**
       * Set the value of text.
       * @param v  Value to assign to text.
       */
    public void setText(String  v) {this.text = v;}
    

    public ReadFile (String fileName) {
        text="";
	v = new Vector();
	String s;

	// open the file and read it in
	try
	{
	    DataInputStream ipstr = new DataInputStream(
					new BufferedInputStream(
					new FileInputStream(fileName)));

	    BufferedReader bufrd = new BufferedReader(
				   new InputStreamReader(ipstr));
	    while ((s = bufrd.readLine()) != null) {
		text = text + s + "\n";
		v.addElement(s);
	    }				
	    ipstr.close();
	} catch(IOException exep) {
	    System.out.println("IO Fehler");
	}
    }



    public static void main(String args[]) {

	if (args.length != 1) {
	    System.out.println("Syntax: java file");
	    System.exit(0);
	}
	ReadFile rf = new ReadFile(args[0]);
        System.out.println(rf.getText());
    }
}
