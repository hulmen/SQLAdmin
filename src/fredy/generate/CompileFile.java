package sql.fredy.generate;


/** 
    This javac's a File into the destination it's source is

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
import java.io.File;


public class CompileFile {

    private Logger logger;

    public CompileFile(String sourceDirectory,
		       String targetDirectory,
		       String [] files) {
	
	//com.sun.tools.javac.Main javac = new com.sun.tools.javac.Main();
	
	logger = Logger.getLogger("sql.fredy.generate");
	logger.log(Level.INFO,"Source Dir: " + sourceDirectory );
	logger.log(Level.INFO,"Target Dir: " + targetDirectory );
	for (int i = 0;i < files.length;i++) {
	    String[] args = new String[] {
		"-d",
		targetDirectory,
		sourceDirectory + File.separator + files[i]
	    };
            int status = 1;
	    // status = javac.compile(args);
	    logger.log(Level.INFO,"Compile Status of " + files[i] + " = " +
		       Integer.toString(status));
	}
    }
    
}
