/*
 * ReadFileNet.java
 *
 * Created on August 21, 2007, 4:54 PM
 *
 * ReadFileNet is to: Rea a file from a website. I write this to create the demoenvironment
 *                    for the datadriller. I do it generic, so it can be used for whatever
 *                    idea you have.
 *
 *  explain here, what this class does...
 *
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
 
 */

package sql.fredy.io;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.*;


/**
 *
 * @author sql@hulmen.ch
 */
public class ReadFileNet {
    
    /** Creates a new instance of ReadFileNet */
    public ReadFileNet() {
    }
    
    public ReadFileNet(String src, String dst) {
        setSrc(src);
        setDst(dst);
        getFileFromNet();
    }
    
    public ReadFileNet(String src,
            String dst,
            String proxyHost,
            int proxyPort) {
        setSrc(src);
        setDst(dst);
        setProxyHost(proxyHost);
        setProxyPort(proxyPort);
        getFileFromNet();
    }
    
    private Logger logger = Logger.getLogger("sql.fredy.io");
    private String src = null;
    
    private String dst = null;
    
    private String proxyHost = "none";
    
    private int proxyPort = 0;
    
    public String getSrc() {
        return src;
    }
    
    public void setSrc(String src) {
        this.src = src;
    }
    
    public String getDst() {
        return dst;
    }
    
    public void setDst(String dst) {
        this.dst = dst;
    }
    
    public String getProxyHost() {
        return proxyHost;
    }
    
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
        System.setProperty("http.proxyHost",proxyHost);
    }
    
    public int getProxyPort() {
        return proxyPort;
    }
    
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
        System.setProperty("http.proxyPort",Integer.toString(proxyPort));
    }
    
    public String getFileFromNet() {
        if ( (getSrc() == null) || (getDst() == null)) return "nothing to do";
        
        String status = "ok";
        
        DataInputStream    in = null;
        //DataOutputStream  out = null;
        FileOutputStream file = null;
        
        try {
            
            URL remoteFile=new URL(getSrc());
            URLConnection fileStream=remoteFile.openConnection();
            
            // Open the input streams for the remote file
            file = new FileOutputStream(getDst());
            
            // Open the output streams for saving this file on disk
            //out = new DataOutputStream(file);
            
            in = new DataInputStream(fileStream.getInputStream());
            BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
            
            logger.log(Level.INFO,"getting: " + getSrc());
            
            
            // Read the remote on save save the file
            /* int data;
            while((data=in.read())!=-1){
                file.write(data);
            }
             */
            String line = null;
            do {
                line = inReader.readLine();
                
                if (line != null) {
                    if ( (System.getProperty("os.name")).toLowerCase().indexOf("windows") > 0 ) {
                        line += "\r\n";
                    } else {
                        line += "\n";
                    }
                    
                    file.write(line.getBytes());
                }
            } while ( line !=null);
            logger.log(Level.INFO,"wrote: " + getDst());
            
            
        } catch ( Exception e) {
            logger.log(Level.WARNING,"can not do this. Message: " + e.getMessage());
            status = "error:"  + e.getMessage();
            //e.printStackTrace();
        } finally {
            try {
                in.close();
                file.flush();
                file.close();
            } catch (Exception e) {
                logger.log(Level.WARNING,"Exception while closing streams: " + e.getMessage());
                status = "error:"  + e.getMessage();
            }
        }
        
        return status;
    }
    
    public String getInfo() {
        StringBuffer s = new StringBuffer();
        s.append("Fredy's tool to read a file from the net\n");
        s.append("Usage:\n\n");
        s.append(" -s SourceFileURL   (or -src)\n");
        s.append(" -d DestinationFile (or -dst)\n");
        s.append(" -h this output     (or -help)\n\n");
        s.append("Networksettings: -ph ProxyHost       (or -proxyhost)\n");
        s.append("                 -pp ProxyPort       (or -proxyport)\n");
        
        
        return s.toString();
    }
    
    
    public static void main(String args[]) {
        
        
        ReadFileNet rfn = new ReadFileNet();
        
        int i = 0;
        while ( i < args.length) {
            
            if ( (args[i].equals("-dst"))  || (args[i].equals("-d")) ) {
                i++;
                rfn.setDst(args[i]);
            }
            if ( (args[i].equals("-src"))  || (args[i].equals("-s")) ) {
                i++;
                rfn.setSrc(args[i]);
            }
            
            if ( (args[i].equals("-proxyhost"))  || (args[i].equals("-ph")) ) {
                i++;
                rfn.setProxyHost(args[i]);
            }
            if ( (args[i].equals("-proxyport"))  || (args[i].equals("-pp")) ) {
                i++;
                rfn.setProxyPort(Integer.parseInt(args[i]));
            }
            
            if ( (args[i].equals("-help"))  || (args[i].equals("-h")) ) {
                System.out.println(rfn.getInfo());
                System.exit(0);
            }
            i++;
        }
        
        String result = rfn.getFileFromNet();
        if ( ! result.startsWith("ok")) {
            System.out.print("ooops:  " + result + "\n\n" + rfn.getInfo());
        } else {
            System.out.println("copy successfull");
        }
        
        
    }
}
