package sql.fredy.generate;

/*
 * This executes a Java-Application and is part of Fredy's Admin Framework
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

import java.util.logging.*;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


public class RunFile {   
    private Logger logger;

 

    public void  run(String file, String args[]) {

	logger = Logger.getLogger("sql.fredy.generate");
        logger.log(Level.INFO,"Trying to run:  " + file );
	try {            
	    Class cls = Class.forName(file);
	    Method main = cls.getMethod("main", new Class[] { String[].class });
	    main.invoke(null, new Object[] { args });
	    logger.log(Level.INFO,"Launched: " + file);
	} catch (InvocationTargetException ex) {
	    // Exception in the main method we just tried to run
	    showMsg("Exception in main: " + ex.getTargetException());
	    ex.getTargetException().printStackTrace();
	} catch (Exception ex) {
	    showMsg(ex.toString());
	}

    }

    private void showMsg(String m) {
	logger.log(Level.WARNING,m);
    }
    public static void main(String args[] ) {
	String f = args[0];
	String[] a = new String[ args.length -1 ];
	for (int i = 0; i< args.length -1;i++)
	     a[i] = args[i+1];

	RunFile rf = new RunFile();
	rf.run(f,a);
    }
        


}
