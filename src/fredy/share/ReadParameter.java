package sql.fredy.share;

/**
   ReadParameter is a part of Admin and done for reading in a bunch
   of Parameters out of a File

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



import java.io.*;
import java.util.*;


public class ReadParameter {

// Fredy's make Version
private static String fredysVersion = "Version 1.4  2. Jan. 2002";

public String getVersion() {return fredysVersion; }


    String param;
    
    /**
       * Get the value of param.
       * @return Value of param.
       */
    public String getParam() {return param;}
    
    /**
       * Set the value of param.
       * @param v  Value to assign to param.
       */
    public void setParam(String  v) {this.param = v;}
    


    public String[] getParameter() {

	int i = 0;
	StringTokenizer st = new StringTokenizer(param," ");
	String[] arg= new String[st.countTokens()];
	while (st.hasMoreTokens()) {
	    arg[i]=st.nextToken();
	    System.out.println("Argument " + i + " " + arg[i]);
	    i++;
	}
	return arg;

    }


    public ReadParameter(String file) {

	String line="";
	param ="";

	try
	{
	    DataInputStream ipstr = new DataInputStream(
					new BufferedInputStream(
					new FileInputStream(file)));

	    BufferedReader bufrd = new BufferedReader(
				   new InputStreamReader(ipstr));
	    while ((line = bufrd.readLine()) != null) {

		if ( ! line.startsWith("#") && (line.trim().length() != 0))
		  param = param + " " + line;
	    }  
	} catch(IOException exep) {
	   param = "Can no read file " + file;
	}
    }
}
