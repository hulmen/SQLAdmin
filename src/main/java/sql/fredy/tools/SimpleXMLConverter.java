package sql.fredy.tools;
/**
 
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
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory; 
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.util.logging.*;

public class SimpleXMLConverter extends DefaultHandler {

    private Logger   logger;

    public SimpleXMLConverter() {

	logger = Logger.getLogger("sql.fredy.tools");	
	

    }
    public LogRecord convert(byte[] byts )  throws SAXException {

	//efaultHandler handler = new LogMsgHandler();
       LogMsgHandler handler = new LogMsgHandler();
       SAXParserFactory factory = SAXParserFactory.newInstance();

       int j = 0;
       
       for (int i=0;i< byts.length; i++) {
	   if ( byts[i] == 00 ) continue;
	   j++;
       }
       
       ByteArrayInputStream bais = new ByteArrayInputStream( byts,0,j);
       
       try {
	   
	   SAXParser saxParser = factory.newSAXParser();
	   saxParser.parse( bais, handler );
 
       } catch (IOException e) {
	   logger.log(Level.SEVERE,"IOException, " + e.getMessage().toString());
       } catch (IllegalArgumentException e) {
	   logger.log(Level.WARNING,"InputStream is null, " + e.getMessage().toString());
       } catch (SAXException e) {
	   // logger.log(Level.WARNING,"SAX Exception, " + e.getMessage().toString());
       } catch (ParserConfigurationException e) {
	   logger.log(Level.WARNING,"Parser Configuration Exception, " + e.getMessage().toString());
       }
        
       return handler.getLogRecord();
   }
}
    
