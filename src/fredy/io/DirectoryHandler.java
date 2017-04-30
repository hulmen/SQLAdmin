package sql.fredy.io;

/**
 * This is a supporting class I need to handle directories within the GenerateTool environment
 * It is part of Fredy's Admin Framework
 *
 * *
 Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
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
 */

import java.io.File;

public class DirectoryHandler {

  private File dir;
  String directory;
    /**
       * Get the value of the Directory.
       * @return value of  the Directory.
       */
    public String getDirectory() {return directory;}
    
    /**
       * Set the value of the Directory.
       * @param v  Value to assign to the Directory.
       */
  public void setDirectory(String  v) {
     this.directory = v; 
     dir = new File(v);
     if ( ! dir.exists() ) dir.mkdir();
  }

  public void emptyDirectory() {
      try {
	  File f[];
	  f = dir.listFiles();
	  for (int i = 0; i < f.length; i++) f[i].delete();
      } catch (Exception exception) { }
  }

  public  DirectoryHandler(String name) {
     this.setDirectory(name) ;
  }
  public  DirectoryHandler() {
  }

}
