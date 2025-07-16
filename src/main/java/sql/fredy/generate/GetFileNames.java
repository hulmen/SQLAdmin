package sql.fredy.generate;


/**  *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 * and exports data into various formats
 *
 *
 *   Copyright (C)     2003, Fredy Fischer
 *                           sql@hulmen.ch
 *   Postal: Fredy Fischer
 *           Hulmenweg 36
 *           8405 Winterthur
 *           Switzerland
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

    This creates a list of the generated FileNames of a DB so I know, what files are to compile


**/

import sql.fredy.metadata.DbInfo;
import java.util.logging.*;
import java.util.Vector;
import java.io.File;

public class GetFileNames {

    private Logger logger;


    String baseDir;
    
    /**
     * Get the value of baseDir.
     * @return value of baseDir.
     */
    public String getBaseDir() {
	return baseDir;
    }
    
    /**
     * Set the value of baseDir.
     * @param v  Value to assign to baseDir.
     */
    public void setBaseDir(String  v) {
	this.baseDir = v;
    }
    public GetFileNames() { setBaseDir(""); }
    public GetFileNames(String bd) { setBaseDir(bd); }

    private String firstUpper(String s) {
	// I do not want to have spaces within the name
        s = s.replace(' ','_');
	s = s.substring(0,1).toUpperCase() + s.substring(1);
	return s;
    }

    public String[] getNames() {

	logger = Logger.getLogger("sql.fredy.generate");

	DbInfo dbi = new DbInfo();
	Vector v = dbi.getTables(dbi.getDatabase());

        // for each Table we have 3 files: Row, Form; TableModel
	String[] names = new String[v.size() * 3];

	int j = 0;
	for (int i=0;i < v.size();i ++ ) {
	    String s = getBaseDir() + 
		File.separator + 
		firstUpper((String)v.elementAt(i));
	    names[j] = s + "Row.java";
	    j++;
	    names[j] = s + "TableModel.java";
	    j++;
	    names[j] = s + "Form.java";
	    j++;
	    
	}

	return names;
    }

    public String[] getFormNames() {
	logger = Logger.getLogger("sql.fredy.generate");

	DbInfo dbi = new DbInfo();

	Vector v = dbi.getTables(dbi.getDatabase());

        // for each Table we have 3 files: Row, Form; TableModel
	String[] names = new String[v.size()];


	for (int i=0;i < v.size();i ++ ) {
	    String s = firstUpper((String)v.elementAt(i));
	    names[i] = "applications." + dbi.getDatabase()+ "." + s + "Form";
	    logger.log(Level.INFO,"Name is: " + names[i]);
	}
	return names;
    }
}


