/*
   FileExec.java
  
   Created on October 1, 2007, 3:57 PM
  
   FileExec is to send a file to he DBMS. For example a dataexport can
   so simply be sent to the dbms to reload a database.  Commands are separated
   by ';' and lines starting with '#', '--' or '/*' are marked as comments and not sent to the DB
 
  
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

package sql.fredy.sqltools;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.logging.*;
import sql.fredy.connection.DataSource;

import sql.fredy.io.ReadFile;

/**
 *
 * @author sql@hulmen.ch
 */
public class FileExec {
    
    /** Creates a new instance of FileExec */
    public FileExec() {
    }
    
    private String fileName = null;;    
    
    private Logger logger = Logger.getLogger("sql.fredy.sqltools");
    
    public String getFileName() {
        return fileName;
    }
    
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
        
    public FileExec(String fileName) {
        setFileName(fileName);
 
    }
    
    
    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    public Connection getConnection() {

        Connection con = null;
        try {
            con = DataSource.getInstance().getConnection();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {
            return con;
        }

    }

    
    
    /*
     * I'm only able to do the job if I have a filename and a connection
     * so this is checked here. And only executed if they exist. They are
     * not validate, just checking if they have been initialised
     */
    
    public void doIt() {   
            exec();
    }
    
    
    /*
     * try to execute the file:  I'm reading it in, eliminating all the lines starting
     * with one of the defined comment-signs '#', '--' or '/*' . Every line is added to a Stringbuffer
     * this Stringbuffer is then parsed by a StringTokenizer with the delimiter ';'. So I'm finally
     * getting all the commands.
     */
    private void exec() {
        ReadFile rf = new ReadFile(getFileName());
        
        // these are all lines
        Vector lines = rf.getLines();
        StringBuffer cmd = new StringBuffer();
        
        for (int i = 0; i < lines.size(); i++) {
            String s = (String)lines.elementAt(i);
            if ( ( s.trim().startsWith("#")  )  ||
                    ( s.trim().startsWith("--") )  ||
                    ( s.trim().startsWith("/*") )
                    ) {
                logger.log(Level.FINEST,"leaving alone: " + s);
            } else {
                cmd.append(s + "\n");
            }
        }
        
        StringTokenizer st = new StringTokenizer(cmd.toString(),";");
        while (st.hasMoreTokens()) {
            sqlCmd(st.nextToken());
        }
    }
    
    
    private void sqlCmd(String sql) {
        if (sql.trim().length() > 2) {
            Connection con = null;
            Statement stmt = null;            
            try {
                con = getConnection();
                stmt = con.createStatement();
                int i = stmt.executeUpdate(sql);
                logger.log(Level.FINE,sql);
            } catch (SQLException sqle) {
                logger.log(Level.WARNING,"error while executing: " + sql);
                logger.log(Level.WARNING,"Message is:" + sqle.getMessage());
            } catch (Exception e) {
                logger.log(Level.SEVERE,"Something went wrong while doing SQL: " + e.getMessage());
            } finally {
                if ( stmt != null ) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING,"Exception while closing statement {0}",e.getMessage());
                    }
                }
                if ( con != null ) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING,"Exception while closing connection {0}",e.getMessage());
                    }
                }
            }
        }
    }
    
  
    public String getInfo() {
        StringBuffer s = new StringBuffer();
        s.append("Fredy's tool to execute a file into a DB\n");
        s.append("Usage:\n\n");
        s.append(" -f file   (or -file)\n");
        s.append(" -h host   (or -host)\n");
        s.append(" -u user   (or -user)\n");
        s.append(" -p pwd    (or -pwd)\n");
        s.append(" -d db     (or -db)\n");
        s.append(" -help this output    \n\n");
        
        
        return s.toString();
    }
    
    
    public static void main(String args[]) {
             
        String file = "";
        
        int i = 0;
        while ( i < args.length) {
            if ( (args[i].equals("-f"))  || (args[i].equals("-file")) ) {
                i++;
                file = args[i];
            }
            
            if ( args[i].equals("-help") ) {
                
                StringBuffer s = new StringBuffer();
                s.append("Fredy's tool to execute a file into a DB\n");
                s.append("Usage:\n\n");
                s.append(" -f file   (or -file)\n");
                s.append(" -help this output    \n\n");
                System.out.println(s.toString());
                System.exit(0);
            }
            i++;
        }
        
        FileExec fe  = new FileExec(file);
        fe.doIt();              
    }
    
}
