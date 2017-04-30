package sql.fredy.tools;

/**

    Copyright (c) 2002 Fredy Fischer
                       fredy.fischer@hulmen.ch

		       Fredy Fischer
		       Hulmenweg 36
		       8405 Winterthur
		       Switzerland


   This is the SAX-Handler to parse Log-Messages of a LogMessage created
   by FxmlFormatter for java.util.logging.
   I do not know, where to put the logging.dtd, therefore I had to 
   change the Standard XMLFormatter from java.util.logging to eliminate
   the logging.dtd-Entry. If someone knows, where to put, please
   send a Mail to fredy.fischer@hulmen.ch.


   SimpleLogServer is a tool I did to enhance my sql-Admin package.
   http://www.hulmen.ch/admin

   SimpleLogServer is based onto java.util.logging and therefore
   needs to have in minimum jdk 1.4 underneath.

   SimpleLogServer is listenning onto a port ( default: 5237 )
   It receives java.util.logging.LogRecord and logs it into
   the default log-environment. To change file-names and LogHandlers
   see the  /usr/java/jre/lib/logging.properties file (or wherever
   this file in your os is), so you can easily change this by giving
   the VM the parameter -Djava.util.logging.config.file= [FILE]

   Additionally it allows to display the LogRecords within a GUI if
   you launch it via -GUI parameter.

   Parameters: -p   port to listen onto (default: 5237 )
               -GUI launch GUI
  
   
   I took the ideas to the server from David Flanagans 'Java in a Nutshell'

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





import java.io.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory; 
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.util.logging.*;

public class LogMsgHandler extends DefaultHandler {   

    private Logger logger;

    String eltName;
    
    /**
     * Get the value of eltName.
     * @return value of eltName.
     */
    public String getEltName() {
	return eltName;
    }
    
    /**
     * Set the value of eltName.
     * @param v  Value to assign to eltName.
     */
    public void setEltName(String  v) {
	this.eltName = v;
    }
    

    public LogMsgHandler() {
	logger =  Logger.getLogger("sql.fredy.tools");	
	logRecord = new LogRecord(Level.FINEST,"init");
    }

    StringBuffer document = new StringBuffer();
    private StringBuffer textBuffer;


    public String getDocument() { return document.toString(); }


    LogRecord logRecord;
    public LogRecord getLogRecord() { return logRecord; }


    private void emit(String s) throws SAXException {
	document.append(s);
    }
    private void nl() throws SAXException {
	document.append( System.getProperty("line.separator") );
    }

    public void startDocument() throws SAXException {
	emit("<?xml version='1.0' encoding='UTF-8'?>");
	nl();
    }

    public void endDocument() throws SAXException {
	nl();

    }
    public void startElement(String namespaceURI,
			     String sName, // simple name
			     String qName, // qualified name
			     Attributes attrs) throws SAXException     {

	process();
	String eName = sName; // element name
	if ("".equals(eName)) eName = qName; // not namespaceAware
	eltName = " ";

	emit("<"+eName);
	if (attrs != null) {
	    for (int i = 0; i < attrs.getLength(); i++) {
		String aName = attrs.getLocalName(i); // Attr name
		if ("".equals(aName)) aName = attrs.getQName(i);
		emit(" ");
		emit(aName+"=\""+attrs.getValue(i)+"\"");	
	    }
	}
	emit(">");
    }
 
    public void endElement(String namespaceURI,
			   String sName, // simple name
			   String qName  // qualified name

			   ) throws SAXException {
	String eName = sName; // element name
	if ("".equals(eName)) eName = qName; // not namespaceAware

	eltName =  eName;
	process();

	emit("</"+eName+">");

    }
    public void characters(char buf[], int offset, int len) throws SAXException{
	String s = new String(buf, offset, len);
	if (textBuffer == null) {
	    textBuffer = new StringBuffer(s);
	} else {
	    textBuffer.append(s);
	}
    }
    private void process() throws SAXException {
	if (textBuffer == null) return;
	String s = ""+ textBuffer;
	s = s.trim();
	emit(s);
	if ( s.length() < 1 ) return;
	try {

	    if ( eltName.toLowerCase().startsWith( "millis" ) ) {
		long millis = Long.parseLong( s  );
		logRecord.setMillis( millis );
	    }
	    if ( eltName.toLowerCase().startsWith( "sequence"))  logRecord.setSequenceNumber(Long.parseLong( s ));
	    if ( eltName.toLowerCase().startsWith( "logger" ) )  logRecord.setLoggerName(s);
	    if ( eltName.toLowerCase().startsWith( "level" )  ) {
		logRecord.setLevel(Level.FINEST);
		if ( s.equalsIgnoreCase("FINE" ) ) logRecord.setLevel(Level.FINE);
		if ( s.equalsIgnoreCase("CONFIG" ) ) logRecord.setLevel(Level.CONFIG);
		if ( s.equalsIgnoreCase("INFO" ) ) logRecord.setLevel(Level.INFO);
		if ( s.equalsIgnoreCase("WARNING" ) ) logRecord.setLevel(Level.WARNING);
		if ( s.equalsIgnoreCase("SEVERE" ) ) logRecord.setLevel(Level.SEVERE);
	    }
	    if ( getEltName().toLowerCase().startsWith("class" )   )  logRecord.setSourceClassName(s);
	    if ( getEltName().toLowerCase().startsWith( "method" ) )  logRecord.setSourceMethodName(s);
	    if ( getEltName().toLowerCase().startsWith("thread" )  )  logRecord.setThreadID(Integer.parseInt (s ));
	    if ( getEltName().toLowerCase().startsWith("message" ) ) {
		logRecord.setMessage(s);	
		this.logRecord = logRecord;
	    }
	} catch (Exception e) {	    
	    throw new SAXException("Converting Error", e);	    	    
	}
	textBuffer = null;
    }
}
