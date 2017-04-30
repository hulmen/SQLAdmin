package sql.fredy.share;

/** 
    SelectDriver is a part of Admin and done for loggin in to it...
    It creates a ComboBox out of a file called rdbms.dat in the
    Admin InstallDir

 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 * and exports data into various formats
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


import java.util.*;
import javax.swing.ImageIcon;
import java.io.File;
import sql.fredy.ui.LoadImage;
import java.util.logging.*;

public class JdbcStuff {

// Fredy's make Version
private static String fredysVersion = "Version 1.4  2. Jan. 2002";
Logger logger = Logger.getLogger("sql.fredy.share");
 
public String getVersion() {return fredysVersion; }

  LoadImage loadImage = new LoadImage();

  public String name, jdbcDriver, dbUrl, port, gif;
 
    public JdbcStuff() { ; }

    public void setObject(String line) {

      try {
	  StringTokenizer st = new StringTokenizer(line,"^");
	  setName(st.nextToken());
	  setJDBCDriver(st.nextToken());
	  setDbUrl(st.nextToken());
	  setPort(st.nextToken());
	  setGif(st.nextToken());
      } catch (NoSuchElementException nse) { ; }

  }


    public void setName(String name) { 
        logger.log(Level.FINEST,"Loading JDBC Driver: " + name);
        this.name=name; 
    }
    public void setJDBCDriver(String jdbcDriver) { this.jdbcDriver = jdbcDriver; }
    public void setDbUrl(String dbUrl) { this.dbUrl = dbUrl; }
    public void setPort(String port ) { this.port = port; }
    public void setGif(String gif) { this.gif = gif; }

    public String getName() { return name; }
    public String getJDBCDriver() { return jdbcDriver; }
    public String getDbUrl() { return dbUrl; }
    public String getPort() { return port; }
    public int getPortAsInt() {
	int i=0;
	try {
	    i = Integer.parseInt(port);
	} catch ( NumberFormatException nfe) { ; }
	return i;
    }
    public String getGif() { return gif; }
    public ImageIcon getImage() {
	String image = getGif();
	ImageIcon img = null;
	if ( image != null ) {
	    img = loadImage.getImage(image);
         
	}
        return img;
    }


}
