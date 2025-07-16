/*
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
import java.util.logging.*;
import java.util.*;
import java.sql.*;
import javax.xml.stream.*;
import java.io.*;
import java.text.*;

import java.math.BigDecimal;
import sql.fredy.connection.DataSource;

/**
 * This class imports a XML-File into a Database. These are the functions: -
 * create new Table - empty table before import - limit the number of rows to be
 * imported
 *
 * To do so it needs a xml-file to control it. This XML-file must have this
 * layout:
 * <?xml version="1.0"?>
 *
 *
 * Tabeldescription follows here
 * <table>
 *
 * the JDBC-Driver to use (optional)
 * <jdbcDriver>org.apache.derby.jdbc.EmbeddedDriver</jdbcDriver>
 *
 * the connection URL, you need to provide this, if you've provided the
 * JDBC-Driver
 * <connectionURL>jdbc:derby:H:\Databases\Derby\testdb</connectionURL>
 *
 * if you set this switch to yes, every error will make the import interrupt
 * <abortOnError>yes|no</abortOnError</>
 *
 * the Element-Name containing the dataset to be imported
 * <dataSetName>name of the dataset</dataSetName>
 *
 * the name of the table the data are going into
 * <tablename>enter the tablename her</tablename>
 *
 * if you say 'yes' the table will be created,
 * <create>yes¦no</create>
 *
 * if you say yes, the first statement executed will be delete from [tablename]
 * <clear>yes|no</clear>
 *
 *
 * the descirption of every single attribut used by the import is described here
 * <field>
 * <xmlfieldname>the name of the xml element</xmlfieldname>
 *
 * below is the description of the DB-table
 * <name>fieldname</name>
 * <type>fieldtype</type>
 * <length>fieldlength</length>
 *
 * add as many options as you like, options are added with a space to the create
 * statement
 * <options>options</options>
 * <options>options</options>
 * </field>
 * </table>
 *
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
 *
 */
public class XMLImport {

    private Logger logger = Logger.getLogger("sql.fredy.sqltools");
    // at local creation, we close the connection before exiting, this switch indicates this mode
    private boolean localExecution = false;
    // this makes it stop after 200 lines
    private boolean testMode = false;
    // values from XML
    private boolean exitOnError = true;
    private boolean clearTable = false;
    private boolean createTable = false;
    private boolean dropTable = false;
    private String jdbcDriver = null;
    private String connectionURL = null;
    private String dropTableCommand = null;
    private String tableName;
    private String createTableQuery;
    private String dataSet;
    private int chunksize = 10000;
    private int counter;
    // varibales used for XML
    private XMLStreamReader streamReader;
    private XMLStreamReader descReader;
    private Hashtable<String, TableField> tableFields;
    // variables used for JDBC
    private Connection connection = null;
    private Statement statement;
    private int noOfRowsInserted = 0;

    public static void main(String args[]) {

        String jdbcDriver = "";
        String connectionURL = "";
        String controlFile = "";
        String dataFile = "";

        XMLImport xmlImport = new XMLImport();

        int i = 0;
        while (i < args.length) {

            if ((args[i].equals("-c")) || (args[i].equals("-controlfile"))) {
                i++;
                controlFile = args[i];
            }
            if ((args[i].equals("-d")) || (args[i].equals("-datafile"))) {
                i++;
                dataFile = args[i];
            }

            if ((args[i].equals("-j")) || (args[i].equals("-jdbcdriver"))) {
                i++;
                jdbcDriver = args[i];
            }

            if ((args[i].equals("-u")) || (args[i].equals("-connectionurl"))) {
                i++;
                connectionURL = args[i];
            }
            if ((args[i].equals("-help")) || (args[i].equals("-h"))) {
                System.out.println("XML Import Utility V0.1\n"
                        + "Part of Fredy's SQL-Admin\n\n"
                        + "Start it with these Parameters:\n"
                        + "-c  Name of the controlling XML-File\n"
                        + "-d  Name of the XML-File to import\n\n"
                        + "-j  the JDBC-Driver\n"
                        + "-u  the JDBC-Connection URL");

                XMLImport xml = new XMLImport();
                System.out.println(xml.getInfo());

                System.exit(0);
            }

            i++;
        }

        if (args.length == 0) {
            System.out.println("XML Import Utility V0.1\n"
                    + "Part of Fredy's SQL-Admin\n\n"
                    + "Start it with these Parameters:\n"
                    + "-c  Name of the controlling XML-File\n"
                    + "-d  Name of the XML-File to import\n\n"
                    + "-j  the JDBC-Driver\n"
                    + "-u  the JDBC-Connection URL");

            //System.exit(0);
            // no information provided, ask on commandline
            String cont = readFromPrompt("No information provided, continue? Y/n", "Y");
            if (!cont.toUpperCase().startsWith("Y")) {
                System.exit(0);
            }
            controlFile = readFromPrompt("Controling XML-File ", "");
            dataFile = readFromPrompt("Data XML File ", "");
            jdbcDriver = readFromPrompt("JDBC Driver ", "");
            connectionURL = readFromPrompt("JDBC Connection ", "");
        }
        if ((controlFile.length() < 2) && (dataFile.length() < 2)) {
            System.out.println("Quit without action.  Not enough information");
            System.exit(1);
        }

        if ((jdbcDriver.length() > 2) && (connectionURL.length() > 2)) {
            xmlImport.createConnection(jdbcDriver, connectionURL);
        }
        if ((controlFile.length() > 2) && (dataFile.length() > 2)) {
            xmlImport.doImport(controlFile, dataFile);
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
            if (fromPrompt.length() < 1) {
                fromPrompt = defValue;
            }
        } catch (IOException ioe) {
            fromPrompt = defValue;
        }
        return fromPrompt;
    }

    /*
     * use this constructor if you have the JDBC-driver and connection-url
     * within hte XML-steering file
     */
    public XMLImport(String controlFileName, String dataFileName) {
        doImport(controlFileName, dataFileName);
    }

    /*
     * use this constructor, if you want to set the values by your own
     */
    public XMLImport() {
    }
      
    public void doImport(String controlFileName, String dataFileName) {

        counter = 0;

        XMLInputFactory f = XMLInputFactory.newInstance();
        XMLInputFactory factory = XMLInputFactory.newInstance();

        // this is the reader finding the Tabledescription
        try {
            descReader = factory.createXMLStreamReader(new FileReader(controlFileName));

        } catch (FileNotFoundException fne) {
            logger.log(Level.WARNING, "File not found: " + fne.getMessage());
        } catch (XMLStreamException x) {
            logger.log(Level.WARNING, "XML-Exception: " + x.getMessage());
        }

        // create the jdbc-stuff if values are provided
        if ((getJdbcDriver() != null) && (getConnectionURL() != null)) {
            createConnection(getJdbcDriver(), getConnectionURL());
        }

        // no conneciton provided, get it from the datasource
        if (getCon() == null) {
            try {
                connection = DataSource.getInstance().getConnection();
                statement = connection.createStatement();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
            } catch (PropertyVetoException ex) {
                logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
            } finally {

            }
        }

        createTable();

        f = XMLInputFactory.newInstance();
        factory = XMLInputFactory.newInstance();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(dataFileName);
            streamReader = factory.createXMLStreamReader(fis, getCharacterEncoding());
            logger.log(Level.INFO, "Encoding valid for this file: {0}", streamReader.getEncoding());

        } catch (FileNotFoundException fne) {
            logger.log(Level.WARNING, "File not found: " + fne.getMessage());
        } catch (XMLStreamException x) {
            logger.log(Level.WARNING, "XML-Exception: " + x.getMessage());
        }

        loopXML();
        close();

    }

    /*
     *  We close all connection if we are localy started
     */
    public void close() {

        try {
            statement.close();
            connection.close();
            logger.log(Level.INFO, "{0} Rows inserted", getNoOfRowsInserted());
        } catch (SQLException sqle) {
            logger.log(Level.WARNING, "SQL-Exception while closing connection to RDBMS: " + sqle.getMessage());
            logger.log(Level.SEVERE, "SQL-State: " + sqle.getSQLState());
            logger.log(Level.WARNING, "Vendor specific error-message: " + sqle.getErrorCode());
        } catch (Exception exc) {
            logger.log(Level.WARNING, "Exception while closing connection to RDBMS: " + exc.getMessage());
            exc.printStackTrace();
        }

    }

    private void createTable() {
        String actualFieldName = "";
        String actualFieldValue = "";
        TableField tf = new TableField();
        boolean start = true;
        int insertSequence = 1;

        setTableFields(new Hashtable<String, TableField>());

        try {
            while (getDescReader().hasNext()) {
                getDescReader().next();
                if (getDescReader().getEventType() == XMLStreamReader.START_ELEMENT) {
                    actualFieldName = getDescReader().getName().getLocalPart();
                    start = true;
                }

                if (getDescReader().getEventType() == XMLStreamReader.END_ELEMENT) {
                    start = false;
                    actualFieldName = getDescReader().getName().getLocalPart();
                    if (actualFieldName.equalsIgnoreCase("field")) {
                        getTableFields().put(tf.getXmlField(), tf);
                    }
                }

                if ((getDescReader().getEventType() == XMLStreamReader.CHARACTERS) && (start)) {

                    actualFieldValue = getDescReader().getText();

                    // set the Switch abortOnError
                    if (actualFieldName.equalsIgnoreCase("abortonerror")) {
                        if (actualFieldValue.equalsIgnoreCase("yes")) {
                            setExitOnError(true);
                        }
                        if (actualFieldValue.equalsIgnoreCase("no")) {
                            setExitOnError(false);
                        }
                    }

                    if (actualFieldName.equalsIgnoreCase("datasetname")) {
                        setDataSet(actualFieldValue);
                    }

                    if (actualFieldName.equalsIgnoreCase("characterencoding")) {
                        setCharacterEncoding(actualFieldValue);
                    }

                    if (actualFieldName.equalsIgnoreCase("jdbcdriver")) {
                        setJdbcDriver(actualFieldValue);
                    }

                    if (actualFieldName.equalsIgnoreCase("chunksize")) {
                        setChunksize(Integer.parseInt(actualFieldValue));
                    }

                    if (actualFieldName.equalsIgnoreCase("connectionurl")) {
                        setConnectionURL(actualFieldValue);
                    }

                    // set the Switch drop table
                    if (actualFieldName.equalsIgnoreCase("droptable")) {
                        if (actualFieldValue.equalsIgnoreCase("yes")) {
                            setDropTable(true);
                        }
                        if (actualFieldValue.equalsIgnoreCase("no")) {
                            setDropTable(false);
                        }
                    }
                    if (actualFieldName.equalsIgnoreCase("droptablecommand")) {
                        setDropTableCommand(actualFieldValue);
                    }

                    // set the Switch create table
                    if (actualFieldName.equalsIgnoreCase("create")) {
                        if (actualFieldValue.equalsIgnoreCase("yes")) {
                            setCreateTable(true);
                        }
                        if (actualFieldValue.equalsIgnoreCase("no")) {
                            setCreateTable(false);
                        }
                    }

                    // set the Switch clear table
                    if (actualFieldName.equalsIgnoreCase("clear")) {
                        if (actualFieldValue.equalsIgnoreCase("yes")) {
                            setClearTable(true);
                        }
                        if (actualFieldValue.equalsIgnoreCase("no")) {
                            setClearTable(false);
                        }
                    }

                    // set the tableName
                    if (actualFieldName.equalsIgnoreCase("tablename")) {
                        setTableName(actualFieldValue);
                    }

                    if (actualFieldName.equalsIgnoreCase("field")) {
                        tf = new TableField();
                        tf.setInsertSequence(insertSequence);
                        insertSequence++;
                    }

                    if (actualFieldName.equalsIgnoreCase("xmlfieldname")) {
                        tf.setXmlField(actualFieldValue);
                    }
                    if (actualFieldName.equalsIgnoreCase("name")) {
                        tf.setFieldName(actualFieldValue);
                    }
                    if (actualFieldName.equalsIgnoreCase("type")) {
                        tf.setFieldType(actualFieldValue);
                    }
                    if (actualFieldName.equalsIgnoreCase("length")) {
                        tf.setLength(actualFieldValue);
                    }
                    if (actualFieldName.equalsIgnoreCase("options")) {
                        tf.addOption(actualFieldValue);
                    }
                }
            }

        } catch (XMLStreamException x) {
            getLogger().log(Level.WARNING, "XML Parsing exception: " + x.getMessage());
            x.printStackTrace();
            if (isExitOnError()) {
                close();
                System.exit(1);
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "XML Parsing exception: " + e.getMessage());
            e.printStackTrace();
            if (isExitOnError()) {
                close();
                System.exit(1);
            }
        }

        // fiddle the drop table command        
        if (getDropTableCommand() == null) {
            setDropTableCommand("drop table " + getTableName());
        }

        // fiddle the create table command
        StringBuffer query = new StringBuffer();
        query.append("create table " + getTableName() + "(");
        for (Enumeration<TableField> e = getTableFields().elements(); e.hasMoreElements();) {
            TableField localTableField = e.nextElement();

            query.append(localTableField.getFieldName());
            query.append(" ");
            query.append(localTableField.getFieldType());
            if (localTableField.getLength() != null) {
                query.append("(");
                query.append(localTableField.getLength());
                query.append(")");
            }
            if (localTableField.optionsLength() > 0) {
                for (int i = 0; i < localTableField.optionsLength(); i++) {
                    query.append(localTableField.getOption(i));
                    query.append(" ");
                }
            }
            if (e.hasMoreElements()) {
                query.append(",\n\t");
            }
        }

        query.append("\n)");
        setCreateTableQuery(query.toString());

        // let's create the connection
        createConnection();

        // if we need to drop the table before, we do it now
        if (isDropTable()) {
            executeSQL(getDropTableCommand());
        }

        //  if the table should be created first
        if (isCreateTable()) {
            executeSQL(getCreateTableQuery());
        }

        // if we want the table to be cleared first
        if (isClearTable()) {
            executeSQL("delete from " + getTableName());
        }

    }

    public final static String getEventTypeString(int eventType) {
        switch (eventType) {
            case XMLStreamReader.START_ELEMENT:
                return "START_ELEMENT";
            case XMLStreamReader.END_ELEMENT:
                return "END_ELEMENT";
            case XMLStreamReader.PROCESSING_INSTRUCTION:
                return "PROCESSING_INSTRUCTION";
            case XMLStreamReader.CHARACTERS:
                return "CHARACTERS";
            case XMLStreamReader.COMMENT:
                return "COMMENT";
            case XMLStreamReader.START_DOCUMENT:
                return "START_DOCUMENT";
            case XMLStreamReader.END_DOCUMENT:
                return "END_DOCUMENT";
            case XMLStreamReader.ENTITY_REFERENCE:
                return "ENTITY_REFERENCE";
            case XMLStreamReader.ATTRIBUTE:
                return "ATTRIBUTE";
            case XMLStreamReader.DTD:
                return "DTD";
            case XMLStreamReader.CDATA:
                return "CDATA";
            case XMLStreamReader.SPACE:
                return "SPACE";
        }
        return "UNKNOWN_EVENT_TYPE " + "," + eventType;
    }

    private void loopXML() {

        String actualFieldName = "";
        String actualFieldValue = "";

        boolean writeRow = false;
        boolean writeAttribute = false;
        boolean isFirstAttributeV = false;
        boolean isFirstAttributeF = false;
        boolean isText = false;
        boolean isDate = false;
        boolean isNumber = false;

        StringBuffer insertQuery = new StringBuffer();
        StringBuffer insertValues = new StringBuffer();
        int counter = 1;
        TableField tf = new TableField();

        Hashtable<Integer, XMLinputField> attributes = new Hashtable<Integer, XMLinputField>();

        try {

            while (getStreamReader().hasNext()) {
                getStreamReader().next();

                if (getStreamReader().getEventType() == XMLStreamReader.START_ELEMENT) {
                    actualFieldName = getStreamReader().getName().getLocalPart();

                    // if the dataset starts, we can start creating our query
                    if (actualFieldName.equalsIgnoreCase(getDataSet())) {
                        writeRow = true;
                        insertQuery = new StringBuffer();
                        insertQuery.append("insert into " + getTableName() + "(");

                        //this is the part with the values
                        insertValues = new StringBuffer();
                        isFirstAttributeV = true;
                        isFirstAttributeF = true;
                    }

                    // let's check if there is a valid element
                    tf = tableFields.get(actualFieldName);
                    if (tf != null) {
                        writeAttribute = true;
                    } else {
                        writeAttribute = false;
                    }

                }

                if (getStreamReader().getEventType() == XMLStreamReader.END_ELEMENT) {
                    actualFieldName = getStreamReader().getName().getLocalPart();

                    // save the row if the last field of a dataset rushed by..
                    if (actualFieldName.equalsIgnoreCase(getDataSet())) {
                        insertQuery.append("\n) values (");
                        insertQuery.append(insertValues.toString());
                        insertQuery.append("\n)");
                        // let's write it                        
                        insertRow(attributes, insertQuery.toString());
                        writeRow = false;
                        counter = 1;
                        attributes = new Hashtable<Integer, XMLinputField>();
                        tf = null;
                    }
                }

                // now let's check if there is something to write
                if ((getStreamReader().getEventType() == XMLStreamReader.CHARACTERS) && (writeAttribute) && (tf != null)) {

                    actualFieldValue = getStreamReader().getText();

                    if (!getStreamReader().isWhiteSpace()) {

                        // we write the insert fieldnames part
                        if (!isFirstAttributeF) {
                            insertQuery.append(",\n\t");
                        } else {
                            isFirstAttributeF = false;
                        }
                        insertQuery.append(tf.getFieldName());

                        // we write the values part
                        if (!isFirstAttributeV) {
                            insertValues.append(",\n\t");
                        } else {
                            isFirstAttributeV = false;
                        }

                        //insertValues.append(actualFieldValue);
                        // this is for the prepared Statement
                        insertValues.append("?");
                        //tf.setValue(actualFieldValue);

                        // we write the value into the hashtable  so we are able to create the preparedStatement
                        XMLinputField inField = new XMLinputField(counter, tf.getFieldName(), actualFieldValue, tf.getFieldType());
                        attributes.put(counter, inField);
                        counter++;
                    }
                    writeAttribute = false;
                }
            }
        } catch (XMLStreamException x) {
            getLogger().log(Level.WARNING, "XML Reading exception: " + x.getMessage());
            if (isExitOnError()) {
                close();
                System.exit(1);
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "XML Reading exception: " + e.getMessage());
            if (isExitOnError()) {
                close();
                System.exit(1);
            }
        }
    }

    /*
     * we execute the insert command as a prepared Statement
     * to do so, we have a reference insert-Query with the ? prepared
     * before and we have put the value of the field as a text into a new
     * HahTable containing the sequence number as a key
     * and a double-valued object having a String with the value and a STring with the field
     * type.
     */
    private void insertRow(Hashtable<Integer, XMLinputField> row, String query) {
        boolean cont = true;
        XMLinputField inField = null;

        try {
            PreparedStatement pst = connection.prepareStatement(query);

            // lets loop the fields to add values to our prepared statement
            for (int i = 1; i < row.size() + 1; i++) {
                inField = (XMLinputField) row.get(i);

                // there are so many different fieldtypes :-(
                if (inField.getAttributeType().equalsIgnoreCase("BIT")) {
                    boolean bool = false;
                    if (inField.getAttributeValue().equalsIgnoreCase("yes")) {
                        bool = true;
                    }
                    if (inField.getAttributeValue().equalsIgnoreCase("oui")) {
                        bool = true;
                    }
                    if (inField.getAttributeValue().equalsIgnoreCase("ja")) {
                        bool = true;
                    }
                    if (inField.getAttributeValue().equalsIgnoreCase("si")) {
                        bool = true;
                    }
                    if (inField.getAttributeValue().equalsIgnoreCase("1")) {
                        bool = true;
                    }
                    pst.setBoolean(inField.getSequence(), bool);
                }
                if (inField.getAttributeType().equalsIgnoreCase("CHAR")) {
                    pst.setString(inField.getSequence(), inField.getAttributeValue());
                }
                if (inField.getAttributeType().equalsIgnoreCase("DATE")) {
                    DateFormat df = DateFormat.getDateInstance();
                    pst.setDate(inField.getSequence(), new java.sql.Date(df.parse(inField.getAttributeValue()).getTime()));
                }
                if (inField.getAttributeType().equalsIgnoreCase("DATETIME")) {
                    DateFormat df = DateFormat.getDateInstance();
                    pst.setDate(inField.getSequence(), new java.sql.Date(df.parse(inField.getAttributeValue()).getTime()));
                }
                if (inField.getAttributeType().equalsIgnoreCase("DECIMAL")) {
                    pst.setBigDecimal(inField.getSequence(), new BigDecimal(inField.getAttributeValue()));
                }
                if (inField.getAttributeType().equalsIgnoreCase("FLOAT")) {
                    pst.setFloat(inField.getSequence(), Float.valueOf(inField.getAttributeValue()));
                }
                if (inField.getAttributeType().equalsIgnoreCase("INTEGER")) {
                    pst.setInt(inField.getSequence(), Integer.valueOf(inField.getAttributeValue()));
                }
                if (inField.getAttributeType().equalsIgnoreCase("INT")) {
                    pst.setInt(inField.getSequence(),Integer.valueOf(inField.getAttributeValue()));
                }
                if (inField.getAttributeType().equalsIgnoreCase("LONGVARBINARY")) {
                    pst.setString(inField.getSequence(), inField.getAttributeValue());
                }
                if (inField.getAttributeType().equalsIgnoreCase("LONGVARCHAR")) {
                    pst.setString(inField.getSequence(), inField.getAttributeValue());
                }
                if (inField.getAttributeType().equalsIgnoreCase("NCHAR")) {
                    pst.setString(inField.getSequence(), inField.getAttributeValue());
                }
                if (inField.getAttributeType().equalsIgnoreCase("NUMERIC")) {
                    pst.setBigDecimal(inField.getSequence(), new BigDecimal(inField.getAttributeValue()));
                }
                if (inField.getAttributeType().equalsIgnoreCase("NTEXT")) {
                    pst.setString(inField.getSequence(), inField.getAttributeValue());
                }
                if (inField.getAttributeType().equalsIgnoreCase("NVARCHAR")) {
                    pst.setString(inField.getSequence(), inField.getAttributeValue());
                }
                if (inField.getAttributeType().equalsIgnoreCase("REAL")) {
                    pst.setFloat(inField.getSequence(),Float.valueOf(inField.getAttributeValue()));
                }
                if (inField.getAttributeType().equalsIgnoreCase("SMALLINT")) {
                    pst.setShort(inField.getSequence(), Float.valueOf(inField.getAttributeValue()).shortValue());
                }
                if (inField.getAttributeType().equalsIgnoreCase("TIME")) {
                    DateFormat df = DateFormat.getDateInstance();
                    java.util.Date d = df.parse(inField.getAttributeValue());
                    pst.setTime(inField.getSequence(), new java.sql.Time(d.getTime()));

                }
                if (inField.getAttributeType().equalsIgnoreCase("TIMESTAMP")) {
                    DateFormat df = DateFormat.getDateInstance();
                    java.util.Date d = df.parse(inField.getAttributeValue());
                    pst.setTimestamp(inField.getSequence(), new java.sql.Timestamp(d.getTime()));
                }
                if (inField.getAttributeType().equalsIgnoreCase("VARBINARY")) {
                    pst.setString(inField.getSequence(), inField.getAttributeValue());
                }
                if (inField.getAttributeType().equalsIgnoreCase("VARCHAR")) {
                    pst.setString(inField.getSequence(), inField.getAttributeValue());
                }
                if (inField.getAttributeType().equalsIgnoreCase("BLOB")) {
                    pst.setString(inField.getSequence(), inField.getAttributeValue());
                }
                if (inField.getAttributeType().equalsIgnoreCase("TEXT")) {
                    pst.setString(inField.getSequence(), inField.getAttributeValue());
                }
            }

            // ok, all is set
            // now let's write the row                        
            pst.executeUpdate();
            pst.close();

            if (counter++ % getChunksize() == 0) {
                logger.log(Level.INFO, "wrote {0} rows", counter);
            }

        } catch (SQLException sqle) {
            logger.log(Level.WARNING, "SQL Exception while inserting row " + counter + " : " + sqle.getMessage());
            logger.log(Level.SEVERE, "SQL-State: " + sqle.getSQLState());
            logger.log(Level.WARNING, "SQL Vendor specific error-message: " + sqle.getErrorCode());
            logger.log(Level.INFO, "Parameters: " + inField.getSequence() + ", " + inField.getAttributeName() + ", " + inField.getAttributeType() + ", " + inField.getAttributeValue());
            cont = false;
        } catch (Exception exc) {
            logger.log(Level.WARNING, "Non Exception for row " + counter + " while inserting data set: " + exc.getMessage());
            exc.printStackTrace();
            cont = false;
        }
        if (!cont) {
            logger.log(Level.INFO, "query: " + query);
        }

        if ((!cont) && (isExitOnError())) {
            close();
            System.exit(1);
        }

        // a public Counter of successfully inserted Rows
        noOfRowsInserted++;
    }

    // execute SQL-Statements
    private void executeSQL(String q) {
        boolean cont = true;
        try {
            statement.executeUpdate(q);
        } catch (SQLException sqle) {
            logger.log(Level.WARNING, "Exception while preparing SQL-stuff: " + sqle.getMessage());
            logger.log(Level.SEVERE, "SQL-State: " + sqle.getSQLState());
            logger.log(Level.WARNING, "Vendor specific error-message: " + sqle.getErrorCode());
            logger.log(Level.INFO, "Query was: \n" + q);
            cont = false;
        } catch (Exception exc) {
            logger.log(Level.WARNING, "Exception loading JDBC-Driver: " + exc.getMessage());
            cont = false;
        }

        if ((!cont) && (isExitOnError())) {
            System.exit(1);
        }

    }

    // if all JDBC-Values are provided within the XML-steering-file
    public void createConnection() {
        if ((getJdbcDriver() != null) && (getConnectionURL() != null) && (getCon() == null)) {
            createConnection(getJdbcDriver(), getConnectionURL());
        }
        setLocalExecution(true);
    }
  
    // use it, when you have an existing connection
    public void createConnection(java.sql.Connection c) {
        boolean cont = true;
        connection = c;
        try {
            statement = connection.createStatement();
        } catch (SQLException sqle) {
            logger.log(Level.WARNING, "Exception while preparing SQL-stuff: " + sqle.getMessage());
            logger.log(Level.SEVERE, "SQL-State: " + sqle.getSQLState());
            logger.log(Level.WARNING, "Vendor specific error-message: " + sqle.getErrorCode());
            cont = false;
        } catch (Exception exc) {
            logger.log(Level.WARNING, "Exception loading JDBC-Driver: " + exc.getMessage());
            cont = false;
        }

        if ((!cont) && (isExitOnError())) {
            close();
            System.exit(1);
        }

        setLocalExecution(false);
    }

    public void createConnection(String jdbcDriver, String connectionURL) {
        boolean cont = true;
        try {
            logger.log(Level.INFO, "jdbc-Driver: " + jdbcDriver);
            logger.log(Level.INFO, "JDBC-URL: " + connectionURL);
            Class.forName(jdbcDriver);
            connection = DriverManager.getConnection(connectionURL);
            logger.log(Level.INFO, "Connecting to " + connection.getMetaData().getDatabaseProductName());
            statement = connection.createStatement();
        } catch (SQLException sqle) {
            logger.log(Level.WARNING, "Exception while preparing SQL-stuff: " + sqle.getMessage());
            logger.log(Level.SEVERE, "SQL-State: " + sqle.getSQLState());
            logger.log(Level.WARNING, "Vendor specific error-message: " + sqle.getErrorCode());
            cont = false;
        } catch (Exception c) {
            logger.log(Level.WARNING, "Exception loading JDBC-Driver: " + c.getMessage());
            cont = false;
        }

        if ((!cont) && (isExitOnError())) {
            close();
            System.exit(1);
        }
        setLocalExecution(true);
    }

    /**
     * @return the logger
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * @return the exitOnError
     */
    public boolean isExitOnError() {
        return exitOnError;
    }

    /**
     * @param exitOnError the exitOnError to set
     */
    public void setExitOnError(boolean exitOnError) {
        this.exitOnError = exitOnError;
    }

    /**
     * @return the clearTable
     */
    public boolean isClearTable() {
        return clearTable;
    }

    /**
     * @param clearTable the clearTable to set
     */
    public void setClearTable(boolean clearTable) {
        this.clearTable = clearTable;
    }

    /**
     * @return the createTable
     */
    public boolean isCreateTable() {
        return createTable;
    }

    /**
     * @param createTable the createTable to set
     */
    public void setCreateTable(boolean createTable) {
        this.createTable = createTable;
    }

    /**
     * @return the testMode
     */
    public boolean isTestMode() {
        return testMode;
    }

    /**
     * @param testMode the testMode to set
     */
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    /**
     * @return the streamReader
     */
    public XMLStreamReader getStreamReader() {
        return streamReader;
    }

    /**
     * @param streamReader the streamReader to set
     */
    public void setStreamReader(XMLStreamReader streamReader) {
        this.streamReader = streamReader;
    }

    /**
     * @return the descReader
     */
    public XMLStreamReader getDescReader() {
        return descReader;
    }

    /**
     * @param descReader the descReader to set
     */
    public void setDescReader(XMLStreamReader descReader) {
        this.descReader = descReader;
    }

    /**
     * @return the tableFields
     */
    public Hashtable<String, TableField> getTableFields() {
        return tableFields;
    }

    /**
     * @param tableFields the tableFields to set
     */
    public void setTableFields(Hashtable<String, TableField> tableFields) {
        this.tableFields = tableFields;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the createTableQuery
     */
    public String getCreateTableQuery() {
        return createTableQuery;
    }

    /**
     * @param createTableQuery the createTableQuery to set
     */
    public void setCreateTableQuery(String createTableQuery) {
        this.createTableQuery = createTableQuery;
    }

    /**
     * @return the dataSet
     */
    public String getDataSet() {
        return dataSet;
    }

    /**
     * @param dataSet the dataSet to set
     */
    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * @return the connection
     */
    public Connection getCon() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setCon(Connection con) {
        this.connection = con;
    }

    /**
     * @return the jdbcDriver
     */
    public String getJdbcDriver() {
        return jdbcDriver;
    }

    String characterEncoding = System.getProperty("file.encoding");

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    /*
     @param set the default Characterencoding    
     */
    public void setCharacterEncoding(String v) {
        logger.log(Level.INFO, "Set characterencoding to: {0}", v);
        characterEncoding = v;
        System.setProperty("file.encoding", v);
    }

    /**
     * @param jdbcDriver the jdbcDriver to set
     */
    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    /**
     * @return the connectionURL
     */
    public String getConnectionURL() {
        return connectionURL;
    }

    /**
     * @param connectionURL the connectionURL to set
     */
    public void setConnectionURL(String connectionURL) {
        this.connectionURL = connectionURL;
    }

    /**
     * @return the localExecution
     */
    public boolean isLocalExecution() {
        return localExecution;
    }

    /**
     * @param localExecution the localExecution to set
     */
    public void setLocalExecution(boolean localExecution) {
        this.localExecution = localExecution;
    }

    /**
     * @return the noOfRowsInserted
     */
    public int getNoOfRowsInserted() {
        return noOfRowsInserted;
    }

    /**
     * @param noOfRowsInserted the noOfRowsInserted to set
     */
    public void setNoOfRowsInserted(int noOfRowsInserted) {
        this.noOfRowsInserted = noOfRowsInserted;
    }

    public String getInfo() {
        StringBuffer sb = new StringBuffer();

        InputStream rdbmsFile = null;

        String line = "";
        try {
            rdbmsFile = this.getClass().getResourceAsStream("/resources/doc/xmlimport.readme");
            BufferedReader in = new BufferedReader(new InputStreamReader(rdbmsFile));
            while (line != null) {
                line = in.readLine();
                if (line != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "something went wrong while reading xmlimport.readme: " + e.getMessage());
        }
        return sb.toString();
    }

    /**
     * @return the dropTable
     */
    public boolean isDropTable() {
        return dropTable;
    }

    /**
     * @param dropTable the dropTable to set
     */
    public void setDropTable(boolean dropTable) {
        this.dropTable = dropTable;
    }

    /**
     * @return the dropTableCommand
     */
    public String getDropTableCommand() {
        return dropTableCommand;
    }

    /**
     * @param dropTableCommand the dropTableCommand to set
     */
    public void setDropTableCommand(String dropTableCommand) {
        this.dropTableCommand = dropTableCommand;
    }

    /**
     * @return the chunksize
     */
    public int getChunksize() {
        return chunksize;
    }

    /**
     * @param chunksize the chunksize to set
     */
    public void setChunksize(int chunksize) {
        this.chunksize = chunksize;
    }
}
