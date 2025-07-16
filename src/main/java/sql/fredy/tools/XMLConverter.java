package sql.fredy.tools;

/**

    Copyright (c) 2002 Fredy Fischer
                       fredy.fischer@hulmen.ch

		       Fredy Fischer
		       Hulmenweg 36
		       8405 Winterthur
		       Switzerland

   This class is used by the SimpleLogServer to transform a ByteStream
   comeing in as XML-DOM into a LogRecord

   SimpleLogServer is a tool I did to enhance my sql-Admin package.
   http://www.hulmen.ch/admin

 
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

**/

import java.util.logging.*;
import java.io.*;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;


public class XMLConverter  {

    private Document doc;
    private Logger   logger;

    private LogRecord lr;

   
    public LogRecord convert(byte[] byts ) {
	logger = Logger.getLogger("sql.fredy.tools");	
        try {
	    SAXBuilder builder = new SAXBuilder();
	    builder.setValidation(false);
	    //ByteArrayInputStream bais = new ByteArrayInputStream( byts,0, byts.length);
            int j = 0;

	    for (int i=0;i< byts.length; i++) {
		if ( byts[i] == 00 ) continue;
		j++;
	    }
	    ByteArrayInputStream bais = new ByteArrayInputStream( byts,0,j);

	    doc = builder.build( bais );

	} catch (JDOMException e) {
	    logger.log(Level.SEVERE,"can not build document! " + e.getMessage().toString());
	    e.printStackTrace();
	} catch (Exception e ) {
	    logger.log(Level.SEVERE,"strange exception! " + e.getMessage().toString());
	    e.printStackTrace();
	}

	lr = new LogRecord(Level.FINEST," ");

	Element log      = doc.getRootElement();
	Element record   = log.getChild("record");
	lr.setMillis(getMillis(record));
	lr.setSequenceNumber(getSequence(record));
	lr.setLoggerName(getLoggerName(record));
	lr.setLevel(getLevel(record));
	lr.setSourceClassName(getSourceClassName(record));
	lr.setSourceMethodName(getSourceMethodName(record));
	lr.setThreadID(getThreadID(record));
	lr.setMessage(getMessage(record));	    
 
	return lr;
    }

    private long getMillis(Element record ) {

	long m = 0;
	try {
	    Element millis = record.getChild("millis");
	    String s =  millis.getTextTrim() ;
            m = Long.parseLong(  s );
	} catch ( NumberFormatException nfe) {
	    logger.log(Level.WARNING,"Can not convert MILLIS value, returning ZERO");
	}

	return m;

    }

    private long getSequence(Element record ) {

	long l = 0;
	try {
	    Element sequence = record.getChild("sequence");
	    String s =  sequence.getTextTrim();
	    l = Long.parseLong( s );
	} catch ( NumberFormatException nfe) {
	    logger.log(Level.WARNING,"Can not convert SEQUENCE value, returning ZERO");
	}
	return l;

    }
		
    private String getLoggerName(Element record ) {
	String s = "UNKNOWN";
	
	Element loggerName = record.getChild("logger");
	s = loggerName.getTextTrim() ;
	
	return s;
    }	


    private Level getLevel(Element record ) {
	String s = "FINEST";
	
	Element level = record.getChild("level");
	s =  level.getTextTrim();	

	if ( s.equalsIgnoreCase("FINE" ) ) return Level.FINE;
	if ( s.equalsIgnoreCase("CONFIG" ) ) return Level.CONFIG;
	if ( s.equalsIgnoreCase("INFO" ) ) return Level.INFO;
	if ( s.equalsIgnoreCase("WARNING" ) ) return Level.WARNING;
	if ( s.equalsIgnoreCase("SEVERE" ) ) return Level.SEVERE;

	return Level.FINEST;
    }
	
    private String getSourceClassName(Element record ) {
	String s = "UNKNOWN";
	Element srcName = record.getChild("class");
	s = srcName.getTextTrim() ;


	return s;
    }	

    private String getSourceMethodName(Element record ) {
	String s = "UNKNOWN";
	Element srcName = record.getChild("method");
	s = srcName.getTextTrim() ;

	return s;
    }	

    private int getThreadID(Element record ) {

	int i = 0;
	try {
	    Element threadId = record.getChild("thread");
            i = Integer.parseInt( threadId.getTextTrim() );
	} catch ( NumberFormatException nfe) {
	    logger.log(Level.WARNING,"Can not convert THREAD value, returning ZERO");
	} 

	return i;

    }
    private String getMessage(Element record ) {
	String s = "UNKNOWN";
	Element msg = record.getChild("message");
	s = msg.getTextTrim() ;
	return s;
    }	
}
