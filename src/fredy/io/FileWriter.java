package sql.fredy.io;

/**
   FileWriter writes a file (only for those who did not realize it)
   
   Created by:  Fredy Fischer
   Date      :  05. Dec. 2001
   Version   :  1.0
                1.1   2. Jan. 2002,  in own Package
		1.2  14. Sept 2002,  abilty to set FileFilter 


   Constructors:

   FileWriter()

   FileWriter(String content, String path, String FileName, String switch)
         Switch: x = overwrite
                 a = ask to overwrite
                 v = create versions  (default)
                 
   this tool is part of the Admin-Suite

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
                 
   
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.BorderFactory; 
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;


public class FileWriter {

    String fileName;
    
    /**
       * Get the value of fileName.
       * @return value of fileName.
       */
    public String getFileName() {return fileName;}
    
    /**
       * Set the value of fileName.
       * @param v  Value to assign to fileName.
       */
    public void setFileName(String  v) {
	if (v != "?" ) {
	    this.fileName = v;
	    this.file = new File(v);
	} else {
	    selectFile();
	}
	    

    }
    

    String title="Select File";
    
    /**
     * Get the value of title.
     * @return value of title.
     */
    public String getTitle() {
	return title;
    }
    
    /**
     * Set the value of title.
     * @param v  Value to assign to title.
     */
    public void setTitle(String  v) {
	this.title = v;
    }
    

    
    String path;
    
    /**
       * Get the value of path.
       * @return value of path.
       */
    public String getPath() {
	return getFile().getParent();
    }
    
    
    String content=null;
    
    /**
       * Get the value of content.
       * @return value of content.
       */
    public String getContent() {return content;}
    
    /**
       * Set the value of content.
       * @param v  Value to assign to content.
       */
    public void setContent(String  v) {this.content = v;}

    
    File file=null;
    
    /**
       * Get the value of file.
       * @return value of file.
       */
    public File getFile() {return file;}
    
    /**
       * Set the value of file.
       * @param v  Value to assign to file.
       */
    public void setFile(String v) {
        try {
	this.file = new File(v);
        } catch (Exception e) {
            
        }
    }

    public void setFile(File v) {
	this.file =v;
    }


    
    String swit="v"; // make version
    
    /**
       * Get the value of swit.
       * @return value of switch.
       */
    public String getSwitch() {return swit;}
    
    /**
       * Set the value of switch.
       * @param v  Value to assign to switch.
       */
    public void setSwitch(String  v) {this.swit = v;}
    
    
    
    String message="";
    
    /**
       * Get the value of message.
       * @return value of message.
       */
    public String getMessage() {return message;}
    
    /**
       * Set the value of message.
       * @param v  Value to assign to message.
       */
    public void setMessage(String  v) {this.message = v;}
    
    private String startingPath = null;
    
    public void selectFile() {	
	JFileChooser chooser = new JFileChooser(getStartingPath());
	chooser.setDialogType(JFileChooser.SAVE_DIALOG);
	chooser.setDialogTitle(getTitle());
	if (getFilter() != null) chooser.setFileFilter(getFilter());

	chooser.setVisible(true);
	int returnVal = chooser.showSaveDialog(null);
	if(returnVal == JFileChooser.APPROVE_OPTION) {
	    this.setFile(chooser.getSelectedFile());
	    setFileName(chooser.getSelectedFile().getParent()
			+ File.separator +
			chooser.getSelectedFile().getName());
            setStartingPath((chooser.getCurrentDirectory().getAbsolutePath()));
            
	    if (swit=="v") makeVersion();
	    if (swit=="a") verify();	    
	} else { setFile((String)null); }

    }   

    MyFileFilter filter = null;
    
    /**
     * Get the value of filter.
     * @return value of filter.
     */
    public MyFileFilter getFilter() {
	return filter;
    }
    
    /**
     * Set the Filefilter 
     * @param extensions are the desired File-extensions
     * @param description the description of the filter
     */
    public void setFilter(String f[]) {
	filter = new MyFileFilter(f);
    }
    



    public void verify() {

	if (file.exists() ) {
             String string1 = "Overwrite";
             String string2 = "Cancel";
             Object[] options = {string1, string2};
             int n = JOptionPane.showOptionDialog(null,
                              "This file already exists!",
                              "File exists",
                              JOptionPane.YES_NO_OPTION,
                              JOptionPane.QUESTION_MESSAGE,
                              null,     //don't use a custom Icon
                              options,  //the titles of buttons
                              string2); //the title of the default button
               if (n == JOptionPane.NO_OPTION)   file = null;

	}

    }


    public void makeVersion() {
	if ( file.exists() ) {
	    int i=0;
	    boolean v = false;
	    while ( ! v) {
		i++;
		File f = new File(getFile().getParent() + File.separator + 
				  getFile().getName() + "." + Integer.toString(i));
		if ( ! f.exists() )  v = getFile().renameTo(f);
	    }
	}
    }

    
  
    public void directoryCheck() {
        try {
	File dir = new File(this.getFile().getParent());
	if ( ! dir.exists() ) getFile().mkdirs();
        } catch (Exception e) {}
    }



    public void write() {
	if ( getContent() != null ) {
	    directoryCheck();
	    if ( swit != "x" ) {
		if (swit=="v") makeVersion();
		if (swit=="a") verify();	    
	    }
	
	    try {
		DataOutputStream outptstr = new DataOutputStream(
			         	    new BufferedOutputStream(
					    new FileOutputStream(file)));
		outptstr.writeBytes(getContent());
		outptstr.flush();
		outptstr.close();			
	    } catch(IOException execp) { 	 
		log("can not write this file " + getFile().getParent()+ File.separator + getFile().getName());
		log(execp.toString());       
	    }
	}
    }


    private void writeStream(InputStream is) {
	try {
	    FileOutputStream out = new FileOutputStream(getFileName());
	    int c;
	    while ((c = is.read()) != -1) {                    
		out.write(c);
	    } 
	    out.close();
	} catch (IOException ioex) { log(ioex.getMessage().toString());
	}
    }

    private void log(String msg) {

	message = message + msg + "\n";

    }


    public  FileWriter() {

    }

    public FileWriter(String fileName,InputStream is) {
	setFileName(fileName);
	makeVersion();
        writeStream( is );

    } 


    public  FileWriter(String content, 
		       String fileName,
		       String swit) {
	
	setContent(content);
	setFileName(fileName);
	setSwitch(swit);
	write();
    }

    public  FileWriter(String content, 
		       String fileName) {       
	setContent(content);
	setFileName(fileName);
	write();
    }

    public static void main(String args[]) {
	System.out.println("FileWriter Version 1.1\n" +
			   "---------- 2. Jan. 2002, Fredy Fischer\n" +
			   "is free software (MIT-License)\n\n" +
			   "Parameters: -f [Filename (? opens Dialog)\n" +
			   "            -c [content]\n\n" +
			   "            Choose none or one of these Switches:\n" +
			   "            -x overwrite\n" +
			   "            -v make a Version (default)\n" +
			   "            -a ask before overwrite");


	String f="?";
	String c=null;
	String s="v";
	int i = 0;
	while ( i < args.length) { 	    
	    if ((args[i].equals("-f") ) || (args[i].equals("-F")) ) {		
		i++;
		f=args[i];
	    }    
	    if  ((args[i].equals("-c")) || (args[i].equals("-c")) ) {		
		i++;
		c=args[i];
	    }
	    if ((args[i].equals("-x")) || (args[i].equals("-x")) ) {		
		i++;
		s="x";
	    }
	    if ((args[i].equals("-a")) || (args[i].equals("-A")) ) {		
		i++;
		s="a";
	    }
	    if ((args[i].equals("-v")) || (args[i].equals("-V")) ) {		
		i++;
		s="v";
	    }
	    i++;
	}

	FileWriter fw = new FileWriter(c,f,s);
	System.out.println(fw.getMessage());

    }

    /**
     * @return the startingPath
     */
    public String getStartingPath() {
        return startingPath;
    }

    /**
     * @param startingPath the startingPath to set
     */
    public void setStartingPath(String startingPath) {
        this.startingPath = startingPath;
    }
}
