package sql.fredy.ui;


/** 

    LoadImage is a part of Admin

    It tries to open a Image as a Icon from different locations:
    1) the directory given over with -Dadmin.image=[DIRECTORY]
    2) the directory ../images
    3) the directory ./images


  Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
  for DB-Administrations, as create / delete / alter and query tables
  it also creates indices and generates simple Java-Code to access DBMS-tables
  and exports data into various formats
 
 
    Copyright (C)     2003, Fredy Fischer
                            sql@hulmen.ch
    Postal: Fredy Fischer
            Hulmenweg 36
            8405 Winterthur
            Switzerland
 
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

**/


import java.io.*;
import javax.swing.ImageIcon;
import java.net.*;
import java.util.logging.*;


public class LoadImage {

    private File file=null;
    
    Logger logger = Logger.getLogger("sql.fredy.ui");
    
    String image=null;
    public void setImage(String v) {
	this.image = v;
    }
    
    public LoadImage() {
	image =null;
    }
    
    public LoadImage(String image) {
	this.setImage(image);
    }


    public  ImageIcon getImage(String image) {
	

	String admin_img = System.getProperty("admin.image");
        
        if ( System.getProperty("admin.image") == null ) {
        try {
            
            
            //logger.log(Level.INFO,"path is " + classLoader.getResource(image).getFile() );
            logger.log(Level.FINE, "Loading image {0}", image);
            
            ImageIcon imcn = new ImageIcon(this.getClass().getResource("/resources/images/" + image));
            //ImageIcon imcn = new ImageIcon(imageURL);
            //logger.log(Level.INFO,"found Icon: " + imcn.toString());
            return imcn;
            
        } catch ( Exception sde) {
            logger.log(Level.WARNING, "error while loading image: {0}", image);
            logger.log(Level.WARNING, "Exception thrown: {0}", sde.getMessage());
            logger.log(Level.WARNING,"Path description follows");
            logger.log(Level.WARNING, "Path is: {0}", this.getClass().getResource("/resources/images/" + image).getFile());
            //sde.printStackTrace();
            
            
            
            return null;
        }
       }
        
        
        /**
	 *  we try to find the image a three places:
	 *  1) at the location defined by the System-Property admin.image
         *  2) out of the jar
	 *  3) at the directory ../images
	 *  4) at the directory ./images
	 *  
	 *  new for this Version is the use of a URL, so images can be
	 *  loaded over the net
	*/

	String url1   = System.getProperty("admin.image") + File.separator + image;

	// did the user deliver a correct URL?
	if ( System.getProperty("admin.image") != null ) {
	    if (  ! isImageLoadable(url1) ) {
		try {
		    File f = new File(url1);
		    if ( f.exists() ) {
			url1 = f.toURL().toString();
		    } 
		} catch (Exception ioException ) { url1 = null ; }
	    }
	}



	String url2=null, url3=null;

        if ( System.getProperty("admin.image") == null ) {
	    try {
		url2   = LoadImage.class.getResource(".."
			+ File.separator + "images"
		        + File.separator + image).toString();

		url3   = LoadImage.class.getResource(
		          File.separator + "images" 
		        + File.separator + image).toString();
	    } catch (NullPointerException npe) {
		// not found
	    }
	}

	String url = null;

	// now we go the order and find out, if the image exists
	if ( isImageLoadable( url1 ) ) {
	    url = url1;
	} else {
	    if ( isImageLoadable( url2 )) {
		url = url2 ;
	    } else {
		if ( isImageLoadable( url3 )) {
		    url = url3; 
		}		
	    }
	}

	if (  url != null ) {
            
	    try {
		URL u = new URL(url);
		ImageIcon img = new ImageIcon(u);
		//addImage(img);
		return img;
	    } catch (Exception ecp1) {                                                 
		ecp1.printStackTrace(); 
	    }
            
	}
	return null;        
        
    }

    private static  boolean isImageLoadable(String u) {
	boolean loadable=true;
        try {
	    URL url        = new URL(u);
            InputStream is = url.openStream();
	} catch ( MalformedURLException mfue ) {	   
            loadable= false;
	} catch ( IOException ioex ) {
	    loadable = false;
	}
	return loadable;
    }
}
