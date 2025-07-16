package sql.fredy.sqltools;

/**
   Xml2db is to load a xml-file into a DB-table
   it reads th structure of an xml-file and creates  a table out of the
   structure and then adds the xml-file into the db.
   It creates a table with default textfields. You can add an XML-Attribute to each element
   indicating the type of the field.  Per default it creates a unique identifier as the primarykey
   of the table, this ist just the element-number processed.
 
   Paramters:
   - hostname (default localhost )
   - dbname
   - tablename (default is the filename)
   - user to connect to the db (default system-user)
   - this users password
   - switch to drop already existing table
 
 
 
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
  
 */
import java.io.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.DOMBuilder;
import org.jdom2.Namespace;

/**
 *
 * @author sql@hulmen.ch
 */
public class Xml2db {

    public static void main(String args[]) {
        String host = "localhost";
        String user = System.getProperty("user.name");
        String password = "";
        String db = "";
        String xmlFile = "";
        String dropTable = "y";
        String tableName = "";

        int i = 0;
        while (i < args.length) {

            if ((args[i].equals("-h")) || (args[i].equals("-host"))) {
                i++;
                host = args[i];
            }
            if ((args[i].equals("-u")) || (args[i].equals("-user"))) {
                i++;
                user = args[i];
            }

            if ((args[i].equals("-p")) || (args[i].equals("-password"))) {
                i++;
                password = args[i];
            }
            if ((args[i].equals("-d")) || (args[i].equals("-db"))) {
                i++;
                db = args[i];
            }

             if ((args[i].equals("-x")) || (args[i].equals("-xml"))) {
                i++;
                xmlFile = args[i];
            }
            if ((args[i].equals("-c")) || (args[i].equals("-drop"))) {
                i++;
                dropTable = args[i];
            }
            if ((args[i].equals("-t")) || (args[i].equals("-table"))) {
                i++;
                tableName = args[i];
            }
            if ((args[i].equals("-help")) || (args[i].equals("-h"))) {
                System.out.println("Fredy's XMLS to DB Tool\n"
                        + "--------------------\n"
                        + "Parameter:\n"
                        + "-h host      (or -host)\n"
                        + "-u user      (or -user)\n"
                        + "-p password  (or -password)\n"
                        + "-d database  (or -db)\n"
                        + "-x xmlfile   (or -xml)\n"
                        + "-c y¦n       (or -drop y¦n) drop destination table\n"
                        + "-t tablename (or -table)");

                System.exit(0);
            }
            i++;
        }

               if (args.length == 0) {
             System.out.println("Fredy's XMLS to DB Tool\n"
                        + "--------------------\n"
                        + "Parameter:\n"
                        + "-h host      (or -host)\n"
                        + "-u user      (or -user)\n"
                        + "-p password  (or -password)\n"
                        + "-d database  (or -db)\n"
                        + "-x xmlfile   (or -xml)\n"
                        + "-c y¦n       (or -drop y¦n) drop destination table");

            //System.exit(0);
            // no information provided, ask on commandline
           String cont = readFromPrompt("No information provided, continue? Y/n","Y");
           if ( ! cont.toUpperCase().startsWith("Y")) System.exit(0);
           host = readFromPrompt("Database host ", host);
           user = readFromPrompt("Database user ", user);
           password = readFromPrompt("Database password ", "");
           db = readFromPrompt("Database name ", db);
           xmlFile = readFromPrompt("XML-File", "");



        }

    }

    /* read from commandline
     *
     * 1st parameter as displaytext
     * 2nd parameter as defaultvalue
     */
    public static String readFromPrompt(String text, String defValue) {
        String fromPrompt = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(text + " (Default: " + defValue + ") ");
        try {
            fromPrompt = br.readLine();
            if (fromPrompt.length() < 1) fromPrompt = defValue;
        } catch (IOException ioe) {
            fromPrompt = defValue;
        }
        return fromPrompt;
    }
}
