/*
 * Fredy Fischer, fredy.fischer@hulmen.ch, July 2024
 *
 * this tool exports fa query into an existing 'other' database
 * it needs a connection to the source database and all the JDBC Parameters to connect to the destination DB
 *
 * JDBC-Driver
 * JDBC URL
 * JDBC Username
 * JDBC Password
 *
 * the query will be run on the source and written into the destination. 
 * the destination Table can be newly created or truncated before writing
 * this tool creates a prepared statement for writing into the destination
 *
 * The MIT License
 *
 * Copyright 2024 fredy.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sql.fredy.sqltools;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import sql.fredy.connection.DataSource;
import sql.fredy.ui.FieldNameType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import sql.fredy.metadata.FieldTypeInfo;
import sql.fredy.tools.JdbcTypeMapping;
import sql.fredy.tools.ReadJdbcTypeMapping;

/**
 *
 * @author fredy
 */
public class DataBaseExport {

    /**
     * @return the dstProductName
     */
    public String getDstProductName() {
        return dstProductName;
    }

    /**
     * @param dstProductName the dstProductName to set
     */
    public void setDstProductName(String dstProductName) {
        this.dstProductName = dstProductName;
    }

    /**
     * @return the dstTypeMappinge
     */
    public HashMap<String, JdbcTypeMapping> getDstTypeMappinge() {

        return dstTypeMappinge;
    }

    /**
     * @param dstTypeMappinge the dstTypeMappinge to set
     */
    public void setDstTypeMappinge(HashMap<String, JdbcTypeMapping> dstTypeMappinge) {
        this.dstTypeMappinge = dstTypeMappinge;
    }

    /**
     * @return the standardCreate
     */
    public String getStandardCreate() {
        return standardCreate;
    }

    /**
     * @param standardCreate the standardCreate to set
     */
    public void setStandardCreate(String standardCreate) {
        this.standardCreate = standardCreate;
    }

    /**
     * @return the manualCreate
     */
    public String getManualCreate() {
        return manualCreate;
    }

    /**
     * @param manualCreate the manualCreate to set
     */
    public void setManualCreate(String manualCreate) {
        this.manualCreate = manualCreate;
    }

    /**
     * @return the maxTextLength
     */
    public int getMaxTextLength() {
        return maxTextLength;
    }

    /**
     * @param maxTextLength the maxTextLength to set
     */
    public void setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }

    /**
     * @return the maxBinaryLength
     */
    public int getMaxBinaryLength() {
        return maxBinaryLength;
    }

    /**
     * @param maxBinaryLength the maxBinaryLength to set
     */
    public void setMaxBinaryLength(int maxBinaryLength) {
        this.maxBinaryLength = maxBinaryLength;
    }

    /**
     * @return the dstPort
     */
    public int getDstPort() {
        return dstPort;
    }

    /**
     * @param dstPort the dstPort to set
     */
    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    /**
     * @return the dstHostName
     */
    public String getDstHostName() {
        return dstHostName;
    }

    /**
     * @param dstHostName the dstHostName to set
     */
    public void setDstHostName(String dstHostName) {
        this.dstHostName = dstHostName;
    }

    /**
     * @return the dstDatabase
     */
    public String getDstDatabase() {
        return dstDatabase;
    }

    /**
     * @param dstDatabase the dstDatabase to set
     */
    public void setDstDatabase(String dstDatabase) {
        this.dstDatabase = dstDatabase;
    }

    /**
     * @return the createDestination
     */
    public boolean isCreateDestination() {
        return createDestination;
    }

    /**
     * @param createDestination the createDestination to set
     */
    public void setCreateDestination(boolean createDestination) {
        this.createDestination = createDestination;
    }

    /**
     * @return the clearDestination
     */
    public boolean isClearDestination() {
        return clearDestination;
    }

    /**
     * @param clearDestination the clearDestination to set
     */
    public void setClearDestination(boolean clearDestination) {
        this.clearDestination = clearDestination;
    }

    /**
     * @return the jdbcDriver
     */
    public String getJdbcDriver() {
        return jdbcDriver;
    }

    /**
     * @param jdbcDriver the jdbcDriver to set
     */
    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    /**
     * @return the jdbcUrl
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    /**
     * @param jdbcUrl the jdbcUrl to set
     */
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    private final static boolean PRINTTYPES = false;
    private String databaseProduct;
    private String quote;
    private HashMap<Integer, FieldTypeInfo> dstFieldTypes;
    private DatabaseMetaData dstDmd = null;
    private int chunksize = 1000;

    private int dstPort = 0;
    private String dstHostName = null;
    private String dstDatabase = null;
    private String dstAdditionalParameter = null;
    private boolean buildConnection = false;
    private boolean useDBQuery = true;

    private String standardCreate, manualCreate;

    private int maxTextLength, maxBinaryLength;

    private String srcQuote;

    /**
     * @return the srcCon
     */
    public Connection getSrcCon() {

        logger.log(Level.FINE, "connection is {0}", srcCon == null ? "null" : "established");

        try {
            if ((srcCon == null) || (srcCon.isClosed())) {
                srcCon = DataSource.getInstance().getConnection();
                DatabaseMetaData dmd = srcCon.getMetaData();
                setDatabaseProduct(dmd.getDatabaseProductName());
                setSrcQuote(dmd.getIdentifierQuoteString());
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection {0}", ex.getMessage());
        }

        return srcCon;
    }

    /**
     * @param srcCon the srcCon to set
     */
    public void setSrcCon(Connection srcCon) {
        this.srcCon = srcCon;
    }

    /**
     * @return the writer
     */
    public PreparedStatement getWriter() {
        return writer;
    }

    /**
     * @param writer the writer to set
     */
    public void setWriter(PreparedStatement writer) {
        this.writer = writer;
    }

    /**
     * @return the clear
     */
    public PreparedStatement getClear() {
        return clear;
    }

    /**
     * @param clear the clear to set
     */
    public void setClear(PreparedStatement clear) {
        this.clear = clear;
    }

    /**
     * @return the creator
     */
    public PreparedStatement getCreator() {
        return creator;
    }

    /**
     * @param creator the creator to set
     */
    public void setCreator(PreparedStatement creator) {
        this.creator = creator;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    private Logger logger = Logger.getLogger(getClass().getName());

    private String jdbcDriver = null;
    private String jdbcUrl = null;
    private String user = null;
    private String password = null;
    private String dstTable = null;

    private Connection srcCon = null;
    private Connection dstCon = null;

    private PreparedStatement writer = null;
    private PreparedStatement clear = null;
    private PreparedStatement creator = null;
    private ResultSetMetaData rsmd = null;
    private ResultSet reader = null;

    private boolean createDestination = false;
    private boolean clearDestination = false;

    private String query;

    private ArrayList<FieldNameType> fieldNameTypes;

    public DataBaseExport() {
        // the standard constructor
    }

    public DataBaseExport(Connection con) {
        setSrcCon(con);
    }

    public DataBaseExport(Connection con, String query) {
        setSrcCon(con);
        setQuery(query);
    }

    public DataBaseExport(Connection srcCon, Connection dstCon, String dstTable, String query, boolean createDestination, boolean clearDestination) {
        setSrcCon(srcCon);
        setDstCon(dstCon);
        setDstTable(dstTable);
        setQuery(query);
        setCreateDestination(createDestination);
        setClearDestination(clearDestination);

        exportData();
    }

    public DataBaseExport(Connection con, String query, String dstTable, String jdbcDriver, String jdbcUrl, String user, String password, boolean createDestination, boolean clearDestination, int chunksize) {
        setChunksize(chunksize);
        setSrcCon(con);
        setDstTable(dstTable);
        setQuery(query);
        setJdbcDriver(jdbcDriver);
        setJdbcUrl(jdbcUrl);
        setUser(user);
        setPassword(password);
        setDstTable(dstTable);
        setCreateDestination(createDestination);
        setClearDestination(clearDestination);
        exportData();
    }

    public int exportData() {

        int job = 0;
        String writerQuery = "";

        // oops, no connection to destination
        if (getDstCon() == null) {
            logger.log(Level.WARNING, "No connection to destination, not exporting");
            return 0;
        }

        // do we have as source?
        if ((getSrcCon() == null) || (getQuery() == null)) {
            logger.log(Level.INFO, "I need a connection to a source DB and a query to export");
            return 0;
        }

        // let us start with it 
        int rows = 0;
        Statement stmt = null;
        FieldNameType fnt = null;
        try {

            stmt = getSrcCon().createStatement();
            reader = stmt.executeQuery(getQuery());
            rsmd = reader.getMetaData();

            // do we need to create the destination table?
            if (isCreateDestination()) {
                job = 1;
                dropTableIfExists();

                job = 2;
                String creatorQuery = isUseDBQuery() ? getManualCreate() : getStandardCreate();
                creator = getDstCon().prepareStatement(creatorQuery);
                creator.executeUpdate();
                logger.log(Level.INFO, "Destination table created");
            }

            // does the destination table be cleaned up?
            if (isClearDestination()) {
                job = 3;
                clear = getDstCon().prepareStatement("truncate table " + getDstTable());
                clear.executeUpdate();
                logger.log(Level.INFO, "Destination table cleaned");
            }

            // now, we create the prepared statement to write the data
            writerQuery = createWriterQuery();
            job = 4;
            writer = getDstCon().prepareStatement(createWriterQuery());

            // let us write
            while (reader.next()) {

                for (int j = 0; j < rsmd.getColumnCount(); j++) {
                    switch (rsmd.getColumnType(j + 1)) {
                        case java.sql.Types.ARRAY:
                            writer.setArray(j + 1, reader.getArray(j + 1));
                            break;
                        case java.sql.Types.BINARY:
                            writer.setBinaryStream(j + 1, reader.getBinaryStream(j + 1));
                            break;
                        case java.sql.Types.BLOB:
                            writer.setBlob(j + 1, reader.getBlob(j + 1));
                            break;
                        case java.sql.Types.CLOB:
                            writer.setClob(j + 1, reader.getClob(j + 1));
                            break;
                        case java.sql.Types.INTEGER:
                            writer.setInt(j + 1, reader.getInt(j + 1));
                            break;
                        case java.sql.Types.FLOAT:
                            writer.setFloat(j + 1, reader.getFloat(j + 1));
                            break;
                        case java.sql.Types.DOUBLE:
                            writer.setDouble(j + 1, reader.getDouble(j + 1));
                            break;
                        case java.sql.Types.DECIMAL:
                            writer.setBigDecimal(j + 1, reader.getBigDecimal(j + 1));
                            break;
                        case java.sql.Types.NUMERIC:
                            writer.setBigDecimal(j + 1, reader.getBigDecimal(j + 1));
                            break;
                        case java.sql.Types.BIGINT:
                            writer.setLong(j + 1, reader.getLong(j + 1));
                            break;
                        case java.sql.Types.TINYINT:
                            writer.setShort(j + 1, reader.getShort(j + 1));
                            break;
                        case java.sql.Types.SMALLINT:
                            short wert = reader.getShort(j + 1);
                            writer.setShort(1, reader.getShort(j + 1));
                            break;
                        case java.sql.Types.DATE:
                            writer.setDate(j + 1, reader.getDate(j + 1));
                            break;
                        case java.sql.Types.TIMESTAMP:
                            writer.setTimestamp(j + 1, reader.getTimestamp(j + 1));
                            break;
                        case java.sql.Types.TIME:
                            writer.setTime(j + 1, reader.getTime(j + 1));
                            break;
                        case java.sql.Types.BIT:
                            writer.setBoolean(j + 1, reader.getBoolean(j + 1));
                            break;
                        case java.sql.Types.BOOLEAN:
                            writer.setBoolean(j + 1, reader.getBoolean(j + 1));
                            break;
                        case java.sql.Types.CHAR:
                            writer.setString(j + 1, reader.getString(j + 1));
                            break;
                        case java.sql.Types.NVARCHAR:
                            writer.setNString(j + 1, reader.getNString(j + 1));
                            break;
                        case java.sql.Types.VARCHAR:
                            writer.setString(j + 1, reader.getString(j + 1));
                            break;
                        case java.sql.Types.LONGNVARCHAR:
                            writer.setNString(j + 1, reader.getNString(j + 1));
                            break;
                        case java.sql.Types.LONGVARCHAR:
                            writer.setString(j + 1, reader.getString(j + 1));
                            break;
                        case java.sql.Types.LONGVARBINARY:
                            writer.setBytes(j + 1, reader.getBytes(j + 1));
                            break;
                        case java.sql.Types.NCHAR:
                            writer.setNString(j + 1, reader.getNString(j + 1));
                            break;
                        case java.sql.Types.NCLOB:
                            writer.setNClob(j + 1, reader.getNCharacterStream(j + 1));
                            break;
                        case java.sql.Types.OTHER:
                            writer.setObject(j + 1, reader.getObject(j + 1));
                            break;
                        case java.sql.Types.REAL:
                            writer.setFloat(j + 1, reader.getFloat(j + 1));
                            break;
                        case java.sql.Types.REF:
                            writer.setRef(j + 1, reader.getRef(j + 1));
                            break;
                        case java.sql.Types.SQLXML:
                            writer.setSQLXML(j + 1, reader.getSQLXML(j + 1));
                            break;
                        case java.sql.Types.STRUCT:
                            writer.setString(j + 1, reader.getString(j + 1));
                            break;
                        case java.sql.Types.JAVA_OBJECT:
                            writer.setObject(j + 1, reader.getObject(j + 1));
                            break;
                        case java.sql.Types.DATALINK:
                            writer.setURL(j + 1, reader.getURL(j + 1));
                            break;
                        case java.sql.Types.DISTINCT:
                            writer.setObject(j + 1, reader.getObject(j + 1));
                            break;
                        case java.sql.Types.NULL:
                            writer.setNull(j + 1, java.sql.Types.NULL);
                            break;
                        default:
                            writer.setString(j + 1, reader.getString(j + 1));
                            break;
                    }
                    if (reader.wasNull()) {
                        writer.setNull(j + 1, rsmd.getColumnType(j + 1));
                    }
                }
                writer.addBatch();
                if (rows++ % getChunksize() == 0) {
                    writer.executeBatch();
                    logger.log(Level.INFO, "copied {0} rows", String.format("%,d", rows));
                }
            }
            writer.executeBatch();
            logger.log(Level.INFO, "finished, copied {0} rows", String.format("%,d", rows));
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQL Exception while exporting: {0}", sqlex.getMessage());
            logger.log(Level.WARNING, "SQL State {0}", sqlex.getSQLState());

            switch (job) {
                case 1:
                    logger.log(Level.INFO, "Exception while dropping table before create ");
                    break;
                case 2:
                    logger.log(Level.INFO, "Create Table Query:\n{0}", isUseDBQuery() ? getManualCreate() : getStandardCreate());
                    break;
                case 3:
                    logger.log(Level.INFO, "Exception while read/write {0}", writerQuery);
                    break;
                default:
                    break;
            }

            sqlex.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQL Exception while closing: {0}", sqlex.getMessage());
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQL Exception while closing: {0}", sqlex.getMessage());
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQL Exception while closing: {0}", sqlex.getMessage());
                }
            }
            if (clear != null) {
                try {
                    clear.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQL Exception while closing: {0}", sqlex.getMessage());
                }
            }
            if (creator != null) {
                try {
                    creator.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQL Exception while closing: {0}", sqlex.getMessage());
                }
            }
            if (getDstCon() != null) {
                try {
                    getDstCon().close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQL Exception while closing: {0}", sqlex.getMessage());
                }
            }
            if (getSrcCon() != null) {
                try {
                    getSrcCon().close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQL Exception while closing: {0}", sqlex.getMessage());
                }
            }

        }
        return rows;
    }

    private String createWriterQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(getDstTable()).append(" (");

        StringBuilder parameter = new StringBuilder();

        if ((fieldNameTypes == null) || (fieldNameTypes.isEmpty())) {
            createFieldList();
        }

        Iterator iter = fieldNameTypes.iterator();
        boolean firstRound = true;
        while (iter.hasNext()) {
            FieldNameType fnt = (FieldNameType) iter.next();

            if (!firstRound) {
                sb.append(",");
                parameter.append(",?");
            } else {
                firstRound = false;
                parameter.append("?");
            }
            //sb.append((fnt.getName().contains(" ") ? "\"" : "")).append(fnt.getName()).append((fnt.getName().contains(" ") ? "\"" : ""));
            sb.append(getQuote()).append(fnt.getName()).append(getQuote());
        }
        sb.append(")\n").append("values (").append(parameter).append(")");

        return sb.toString();
    }

    /*
    private void createDstConnection() {

        // we need in minimum the JDBC Driver and the URL
        if ((getJdbcDriver() == null) || (getJdbcUrl() == null)) {
            logger.log(Level.WARNING, "I need a JDBC Driver and an URL");
            return;
        }

        // the different types of connections
        // 1. use URL, Username and Password
        if ((getUser() != null) && (getPassword() != null)) {
            try {
                dstCon = DriverManager.getConnection(getJdbcUrl(), getUser(), getPassword());
            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "SQL Exception while trying a connection to the destination DB: {0}", sqlex.getMessage());
                logger.log(Level.WARNING, "SQL State {0}", sqlex.getSQLState());
            }
        }

        // 2 we only have an URL
        try {
            dstCon = DriverManager.getConnection(getJdbcUrl());
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQL Exception while trying a connection to the destination DB: {0}", sqlex.getMessage());
            logger.log(Level.WARNING, "SQL State {0}", sqlex.getSQLState());
        }

        if (dstCon != null) {
            logger.log(Level.INFO, "connection to destination DB established");
        }
        // create the create-table-statement
        createTableStatement();
    }
     */
 /*
    This is creating the list of the fields in the source database    
     */
    private void createFieldList() {
        Statement stmt = null;
        setFieldNameTypes((ArrayList<FieldNameType>) new ArrayList());
        try {

            if (rsmd == null) {
                stmt = getSrcCon().createStatement();
                reader = stmt.executeQuery(getQuery());
                rsmd = reader.getMetaData();
                setQuote(getDstCon().getMetaData().getIdentifierQuoteString());
            }

            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                FieldNameType fnt = new FieldNameType();
                fnt.setName(rsmd.getColumnLabel(i));
                fnt.setVendorTypeName(rsmd.getColumnTypeName(i));
                fnt.setType(getColumnClass(i));
                fnt.setColumnDataType(rsmd.getColumnType(i));

                fnt.setOriginalColumnName(rsmd.getColumnLabel(i));
                fnt.setLength(rsmd.getColumnDisplaySize(i));
                fnt.setMaxLength(rsmd.getPrecision(i));
                try {
                    fnt.setTable(rsmd.getTableName(i).toLowerCase());
                    fnt.setLength(rsmd.getPrecision(i));
                    fnt.setScale(rsmd.getScale(i));
                } catch (SQLException e1) {
                    fnt.setTable("unknonwTable");
                }
                if (fnt.getTable().length() < 1) {
                    fnt.setTable("table");
                }
                try {
                    fnt.setDb(rsmd.getCatalogName(1));
                } catch (SQLException e2) {
                    fnt.setDb("canNotDetectDB");
                }
                if (fnt.getDb().length() < 1) {
                    fnt.setDb("db");
                }
                getFieldNameTypes().add(fnt);
            }
        } catch (SQLException sqlex) {
            // trage hier deine Fehlermeldung ein
            logger.log(Level.WARNING, "Error while collecting info for  codegeneration : {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
        } catch (Exception e) {
            logger.log(Level.WARNING, "probably no result generated. {0}", e.getMessage());
            //e.printStackTrace();
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Error while collecting info for  codegeneration : {0}", sqlex.getMessage());
                    logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
                    logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
                }
            }
        }

    }

    public String getColumnClass(int column) {
        int type;
        try {
            type = rsmd.getColumnType(column);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception : {0}", e.getMessage());

            return "String";
        }

        switch (type) {
            case Types.CHAR:
                return "String";
            case Types.VARCHAR:
                return "String";
            case Types.LONGVARCHAR:
                return "String";
            case Types.BIT:
                return "boolean";
            case Types.BOOLEAN:
                return "boolean";
            case Types.TINYINT:
                return "int";
            case Types.SMALLINT:
                return "short";
            case Types.INTEGER:
                return "int";
            case Types.BIGINT:
                return "long";
            case Types.NUMERIC:
                return "BigDecimal";
            case Types.FLOAT:
                return "float";
            case Types.DECIMAL:
                return "float";
            case Types.DOUBLE:
                return "double";
            case Types.REAL:
                return "float";
            case Types.DATE:
                return "java.sql.Date";
            case Types.TIMESTAMP:
                return "java.sql.Timestamp";
            case Types.TIME:
                return "java.sql.Time";
            default:
                return "String";
        }
    }

    public void createTableStatement() {

        StringBuilder standardQuery = new StringBuilder();
        standardQuery.append("create table ").append(getDstTable()).append(" (");

        StringBuilder destinationQuery = new StringBuilder();
        destinationQuery.append("create table ").append(getDstTable()).append(" (");

        if (fieldNameTypes == null) {
            createFieldList();
        }
        Iterator<FieldNameType> iter = fieldNameTypes.iterator();
        int round = 0;
        while (iter.hasNext()) {
            FieldNameType f = iter.next();

            if (round == 0) {
                standardQuery.append("\n");
                destinationQuery.append(" \n");
            } else {
                standardQuery.append(",\n");
                destinationQuery.append(", \n");
            }

            round++;
            //destinationQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" ").append(formattedLength(f));

            switch (f.getColumnDataType()) {
                case java.sql.Types.ARRAY:
                    // Array is not supported
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" ARRAY()");
                    break;
                case java.sql.Types.BINARY:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" BINARY(").append(f.getLength()).append(")");
                    break;
                case java.sql.Types.BLOB:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" BLOB(").append(f.getLength()).append(")");
                    break;
                case java.sql.Types.CLOB:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" CLOB(").append(f.getLength()).append(")");
                    break;
                case java.sql.Types.INTEGER:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" INT");
                    break;
                case java.sql.Types.FLOAT:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" FLOAT");
                    break;
                case java.sql.Types.DOUBLE:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" DOUBLE");
                    break;
                case java.sql.Types.DECIMAL:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" DECIMAL(").append(Integer.toString(f.getMaxLength())).append(",").append(f.getPrecision()).append(")");
                    break;
                case java.sql.Types.NUMERIC:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" NUMERIC(").append(Integer.toString(f.getMaxLength())).append(",").append(f.getPrecision()).append(")");
                    break;
                case java.sql.Types.BIGINT:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" BIGINT");
                    break;
                case java.sql.Types.TINYINT:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" TINYINT");
                    break;
                case java.sql.Types.SMALLINT:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" SMALLINT");
                    break;
                case java.sql.Types.DATE:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" ").append(formattedLength(f));
                    break;
                case java.sql.Types.TIMESTAMP:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" ").append(formattedLength(f));
                    break;
                case java.sql.Types.TIME:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" ").append(formattedLength(f));
                    break;
                case java.sql.Types.BIT:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" BIT");
                    break;
                case java.sql.Types.BOOLEAN:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" BOOLEAN");
                    ;
                    break;
                case java.sql.Types.CHAR:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" CHAR(").append(Integer.toString(f.getMaxLength())).append(")");
                    break;
                case java.sql.Types.NVARCHAR:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" NVARCHAR(").append(f.getMaxLength()).append(")");
                    break;
                case java.sql.Types.VARCHAR:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" VARCHAR(").append(Integer.toString(f.getMaxLength())).append(")");
                    break;
                case java.sql.Types.LONGNVARCHAR:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" LONGNVARCHAR(").append(Integer.toString(f.getMaxLength())).append(")");
                    break;
                case java.sql.Types.LONGVARCHAR:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" LONGVARCHAR(").append(f.getMaxLength()).append(")");
                    break;
                case java.sql.Types.LONGVARBINARY:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" LONGVARBINARY(").append(f.getMaxLength()).append(")");
                    break;
                case java.sql.Types.NCHAR:
                    standardQuery.append("\t").append(".").append(getQuote()).append(f.getName()).append(getQuote()).append(" NCHAR(").append(f.getMaxLength()).append(")");
                    break;
                case java.sql.Types.NCLOB:
                    standardQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" NCLOB(").append(f.getMaxLength()).append(")");
                    break;
                case java.sql.Types.OTHER:

                    break;
                case java.sql.Types.REAL:

                    break;
                case java.sql.Types.REF:

                    break;
                case java.sql.Types.SQLXML:

                    break;
                case java.sql.Types.STRUCT:

                    break;
                case java.sql.Types.JAVA_OBJECT:

                    break;
                case java.sql.Types.DATALINK:

                    break;
                case java.sql.Types.DISTINCT:

                    break;
                case java.sql.Types.NULL:

                    break;
                default:
                    standardQuery.append("\t").append(f.getSchema()).append(".").append(getQuote()).append(f.getName()).append(getQuote()).append(" VARCHAR(").append(f.getMaxLength()).append(")");
                    break;
            }

            //} else {            
            destinationQuery.append("\t").append(getQuote()).append(f.getName()).append(getQuote()).append(" ").append(formattedLength(f));
            //}

        }
        destinationQuery.append("\n)\n");
        standardQuery.append("\n)\n");
        setManualCreate(destinationQuery.toString());
        setStandardCreate(standardQuery.toString());

    }

    /*
    the destinatin is dropped, if it exists
     */
    private void dropTableIfExists() {
        ResultSet rs = null;
        Statement stmt = null;
        String catalog = null;
        String schema = null;
        String table = getDstTable();
        try {

            String[] schemaTable = getDstTable().split("\\.");

            if (schemaTable.length == 3) {
                catalog = schemaTable[0];
                schema = schemaTable[1];
                table = schemaTable[2];
            }
            if (schemaTable.length == 2) {
                schema = schemaTable[0];
                table = schemaTable[1];
            }

            String[] tableTypes = {"TABLE"};
            rs = getDstDmd().getTables(catalog, schema, table, tableTypes);
            if (rs.next()) {
                stmt = getDstCon().createStatement();
                String query = "drop table " + (catalog != null ? catalog + "." : "") + (schema != null ? schema + "." : "") + getQuote() + table + getQuote();
                stmt.executeUpdate(query);
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while deleting destination table {0}", sqlex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception on closing object {0}", sqlex.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception on closing object {0}", sqlex.getMessage());
                }
            }
        }
    }

    private String formattedLength(FieldNameType f) {

        // new Try with JdbcTypeMapping thing
        JdbcTypeMapping jtm = dstTypeMappinge.get(f.getJdbcTypeName());

        StringBuilder sb = new StringBuilder();
        //sb.append(jtm.getDbProductTypeName());
        
        sb.append(" ").append(jtm.getDbProductTypeName()).append(" ");

        if (jtm.isHasLength()) {
            if ((jtm.getMaxLength() > 0) && (f.getLength() >= jtm.getMaxLength())) {
                sb.append("(").append(jtm.getBiggerMaxLengthText()).append(")");
            } else {
                sb.append("(").append(Integer.toString(f.getLength())).append(")");
            }
        }

        return sb.toString();

        /*
        String l = "";
        try {

            if (dstFieldTypes == null) {
                getDstCon();
            }

            FieldTypeInfo fti = dstFieldTypes.get(f.getColumnDataType());

            int lng = vendorDependentMaxLength(f);
            int type = f.getColumnDataType();
            switch (type) {
                case java.sql.Types.ARRAY:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName()) + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.BINARY:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName()) + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.BLOB:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName()) + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.CLOB:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName()) + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.INTEGER:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.FLOAT:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.DOUBLE:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.DECIMAL:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName()) + "(" + Integer.toString(f.getMaxLength()) + "," + Integer.toString(f.getScale()) + ")";
                    break;
                case java.sql.Types.NUMERIC:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName()) + "(" + Integer.toString(f.getMaxLength()) + "," + Integer.toString(f.getScale()) + ")";
                    break;
                case java.sql.Types.BIGINT:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.TINYINT:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.SMALLINT:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.DATE:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.TIMESTAMP:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.TIME:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.BIT:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.BOOLEAN:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName());
                    break;
                case java.sql.Types.CHAR:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName()) + "(" + Integer.toString(lng) + ")";
                    break;
                case java.sql.Types.NVARCHAR:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName()) + "(" + Integer.toString(lng) + ")";
                    break;
                case java.sql.Types.VARCHAR:
                    l = (fti != null ? fti.getTypName() : f.getVendorTypeName()) + "(" + Integer.toString(lng) + ")";
                    break;
                case java.sql.Types.LONGNVARCHAR:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName() + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.LONGVARCHAR:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName() + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.LONGVARBINARY:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName() + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.NCHAR:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName() + "(" + Integer.toString(lng) + ")";
                    break;
                case java.sql.Types.NCLOB:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName() + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.OTHER:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName() + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.REAL:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName();
                    break;
                case java.sql.Types.REF:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName();
                    break;
                case java.sql.Types.SQLXML:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName() + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.STRUCT:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName();
                    break;
                case java.sql.Types.JAVA_OBJECT:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName() + "(" + Integer.toString(f.getMaxLength()) + ")";
                    break;
                case java.sql.Types.DATALINK:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName();
                    break;
                case java.sql.Types.DISTINCT:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName();
                    break;
                case java.sql.Types.NULL:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName();
                    break;
                default:
                    l = fti != null ? fti.getTypName() : f.getVendorTypeName();
                    break;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception {0}", e.getMessage());
        }
        return l;
         */
    }

    private int vendorDependentMaxLength(FieldNameType fti) {
        int lng = fti.getMaxLength();

        try {
            String dstDBProductName = dstDmd.getDatabaseProductName();
            switch (fti.getColumnDataType()) {
                case java.sql.Types.CHAR:
                    if (dstDBProductName.toLowerCase().contains("microsoft access")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("microsoft sql server")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("mysql")) {
                        lng = lng > 255 ? 255 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("maria")) {
                        lng = lng > 255 ? 255 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("postgres")) {
                        lng = lng > 255 ? 255 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("derby")) {
                        lng = lng > 255 ? 255 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("oracle")) {
                        lng = lng > 255 ? 255 : lng;
                    }
                    break;
                case java.sql.Types.VARCHAR:
                    if (dstDBProductName.toLowerCase().contains("microsoft access")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("microsoft sql server")) {

                    }
                    if (dstDBProductName.toLowerCase().contains("mysql")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("maria")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("postgres")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("derby")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("oracle")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    break;
                case java.sql.Types.NCHAR:
                    if (dstDBProductName.toLowerCase().contains("microsoft access")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("microsoft sql server")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("mysql")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("maria")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("postgres")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("derby")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("oracle")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    break;
                case java.sql.Types.NVARCHAR:
                    if (dstDBProductName.toLowerCase().contains("microsoft access")) {
                        lng = lng > 4000 ? 4000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("microsoft sql server")) {
                        lng = lng > 4000 ? 4000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("mysql")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("maria")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("postgres")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("derby")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    if (dstDBProductName.toLowerCase().contains("oracle")) {
                        lng = lng > 8000 ? 8000 : lng;
                    }
                    break;
                default:
                    break;
            }
        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, "SQL Exception {0}", sqlex.getMessage());
        }
        return lng;
    }

    private boolean recreateDstCon() {
        boolean v = true;
        try {
            v = (dstCon == null) || (dstCon.isClosed());

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while verifying destination connection {0}", sqlex.getMessage());
        }
        return v;
    }

    /**
     * @return the dstCon
     */
    private String dstProductName = null;
    private HashMap<String, JdbcTypeMapping> dstTypeMappinge = null;

    public Connection getDstCon() {

        if (recreateDstCon()) {
            logger.log(Level.INFO, "Creating connection to {0}:{1}:{2}//{3}", new Object[]{getJdbcUrl(), getDstHostName(), String.format("%d", getDstPort()), getDstDatabase()});

            ResultSet rs = null;
            try {
                // this is the simple way of creating the connection
                if (!isBuildConnection()) {
                    if (getUser() == null) {
                        dstCon = DriverManager.getConnection(getJdbcUrl());
                    } else {
                        Properties connProps = new Properties();
                        connProps.put("user", getUser());
                        connProps.put("password", getPassword());
                        dstCon = DriverManager.getConnection(getJdbcUrl(), connProps);
                    }

                } else {
                    // we build the connection out of JDBCURL, Hostname, DatabaseName, Usesrname, Password, Port and additional parameters as it is done in Datatasource

                    // Oracle 
                    if (getJdbcUrl().equalsIgnoreCase("jdbc:oracle:thin:@")) {
                        // we try the thin way
                        try {
                            dstCon = DriverManager.getConnection(getJdbcUrl() + "//" + getDstHostName() + ":" + Integer.toString(getDstPort()) + "/" + getDstDatabase(), getUser(), getPassword());
                        } catch (SQLException sqlex) {
                            // and now we try it with the TNSname
                            StringBuilder sb = new StringBuilder();
                            sb.append(getJdbcUrl()).append("(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)")
                                    .append("(HOST=")
                                    .append(getDstHostName())
                                    .append(")(PORT=").append(Integer.toString(getDstPort())).append("))")
                                    .append("(CONNECT_DATA=(SERVICE_NAME=").append(getDstDatabase())
                                    .append(")))");
                            dstCon = DriverManager.getConnection(sb.toString(), getUser(), getPassword());
                        }
                    }

                    // MS SQL Server
                    if (getJdbcUrl().equalsIgnoreCase("jdbc:sqlserver://")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(getJdbcUrl())
                                .append(getDstHostName())
                                .append(":").append(Integer.toString(getDstPort()))
                                .append(";databaseName=").append(getDstDatabase())
                                .append(((getDstAdditionalParameter() != null) || (getDstAdditionalParameter().trim().length() > 0)) ? getDstAdditionalParameter() : "");
                        dstCon = DriverManager.getConnection(sb.toString(), getUser(), getPassword());
                    }

                    // Apache Derby
                    if (getJdbcUrl().equalsIgnoreCase("jdbc:derby:")) {
                        // embedded
                        if (getDstPort() == 0) {
                            StringBuilder sb = new StringBuilder();
                            sb.append(getJdbcUrl())
                                    .append(getDstDatabase())
                                    .append(((getDstAdditionalParameter() != null) || (getDstAdditionalParameter().trim().length() > 0)) ? ";" + getDstAdditionalParameter() : "");
                            dstCon = DriverManager.getConnection(sb.toString());
                        } else {
                            // networked
                            StringBuilder sb = new StringBuilder();
                            sb.append(getJdbcUrl())
                                    .append("//").append(getDstHostName())
                                    .append(":").append(Integer.toString(getDstPort()))
                                    .append("/").append(getDstDatabase())
                                    .append(((getDstAdditionalParameter() != null) || (getDstAdditionalParameter().trim().length() > 0)) ? ";" + getDstAdditionalParameter() : "");
                            if ((getUser() == null) || (getUser().length() < 1)) {
                                dstCon = DriverManager.getConnection(sb.toString());
                            } else {
                                dstCon = DriverManager.getConnection(sb.toString(), getUser(), getPassword());
                            }
                        }
                    }

                    // SQLite
                    if (getJdbcUrl().equalsIgnoreCase("jdbc:sqlite:")) {
                        dstCon = DriverManager.getConnection(getJdbcUrl() + "//" + getDstDatabase());
                    }

                    // ucanaccess
                    if (getJdbcUrl().equalsIgnoreCase("jdbc:ucanaccess://")) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(getJdbcUrl())
                                .append(getDstDatabase())
                                .append(((getDstAdditionalParameter() != null) || (getDstAdditionalParameter().trim().length() > 0)) ? ";" + getDstAdditionalParameter() : "");

                        System.out.println(sb.toString());

                        if ((getUser() == null) || (getUser().length() < 1)) {
                            dstCon = DriverManager.getConnection(sb.toString());
                        } else {
                            dstCon = DriverManager.getConnection(sb.toString(), getUser(), getPassword());
                        }

                    }

                    // all others
                    if (dstCon == null) {
                        StringBuilder sb = new StringBuilder();
                        sb.append(getJdbcUrl())
                                .append(getDstHostName())
                                .append(getDstPort() > 0 ? ":" + Integer.toString(getDstPort()) : "")
                                .append("/").append(getDstDatabase())
                                //.append(((getDstAdditionalParameter() != null) || (getDstAdditionalParameter().trim().length() > 0)) ? "?" + getDstAdditionalParameter() : "");
                                .append(((getDstAdditionalParameter() == null) || (getDstAdditionalParameter().trim().length() == 0)) ? "" : "?" + getDstAdditionalParameter());
                        if ((getUser() == null) || (getUser().trim().length() < 1)) {
                            dstCon = DriverManager.getConnection(sb.toString());
                        } else {
                            dstCon = DriverManager.getConnection(sb.toString(), getUser(), getPassword());
                        }
                    }
                }

                // now let us create the FieldTypeNames because there are differences in between the different products
                dstDmd = dstCon.getMetaData();
                setQuote(dstDmd.getIdentifierQuoteString());

                ReadJdbcTypeMapping rtm = new ReadJdbcTypeMapping(dstDmd.getDatabaseProductName());
                TypeMapping tm = new TypeMapping(dstDmd.getDatabaseProductName());
                dstTypeMappinge = tm.getMapping();

                rs = dstDmd.getTypeInfo();
                dstFieldTypes = new HashMap<>();

                if (PRINTTYPES) {
                    System.out.println(fixedLength("Type Name", 20)
                            + " "
                            + fixedLength("Data Type", 9)
                            + " "
                            + fixedLength("Auto Increment", 15)
                            + " "
                            + fixedLength("Local Type Name", 20)
                            + " "
                            + fixedLength("SQL Data Type", 15)
                    );
                }
                while (rs.next()) {
                    FieldTypeInfo fti = new FieldTypeInfo();
                    fti.setTypName(rs.getString(1));
                    fti.setDataType(rs.getInt(2));
                    fti.setPrecision(rs.getInt(3));
                    fti.setLiteral_prefix(rs.getString(4));
                    fti.setLiteral_suffix(rs.getString(5));
                    fti.setCreate_params(rs.getString(6));
                    fti.setNullable(rs.getShort(7));
                    fti.setCase_sensitive(rs.getBoolean(8));
                    fti.setSearchable(rs.getShort(9));
                    fti.setUnsigned_attribute(rs.getBoolean(10));
                    fti.setFixed_rec_scale(rs.getBoolean(11));
                    fti.setAutoIncrement(rs.getBoolean(12));
                    fti.setLocal_type_name(rs.getString(13));
                    fti.setMinimum_scale(rs.getShort(14));
                    fti.setMinimum_scale(rs.getShort(15));
                    fti.setSql_data_type(rs.getInt(16));
                    fti.setSql_datetime_sub(rs.getInt(17));
                    fti.setNum_prec_radix(rs.getInt(18));

                    if (!fti.isAutoIncrement()) {
                        dstFieldTypes.put(fti.getDataType(), fti);
                    }

                    if (PRINTTYPES) {
                        System.out.println(fixedLength(fti.getTypName(), 20)
                                + " "
                                + fixedLength(String.format("%d", fti.getDataType()), 9)
                                + " "
                                + fixedLength(String.format("%b", fti.isAutoIncrement()), 15)
                                + " "
                                + fixedLength(fti.getLocal_type_name(), 20)
                                + " "
                                + fixedLength(String.format("%d", fti.getSql_data_type()), 15)
                        );
                    }
                }

                // some other values
                setMaxBinaryLength(dstDmd.getMaxBinaryLiteralLength());
                setMaxTextLength(dstDmd.getMaxCharLiteralLength());

            } catch (SQLException sqlex) {
                logger.log(Level.SEVERE, "Can not establish connection to destination DB {0}", sqlex.getMessage());
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException sqlex) {
                        logger.log(Level.WARNING, "Exception while closing resultset {0}", sqlex.getMessage());
                    }
                }
            }
        }
        return dstCon;
    }

    private static String fixedLength(String value, int lng) {
        return value + "\t";
        //return String.format("%1$" + lng + "s", value);
    }

    /**
     * @param dstCon the dstCon to set
     */
    public void setDstCon(Connection dstCon) {
        this.dstCon = dstCon;

        // we do the metadata things
        ResultSet rs = null;
        try {
            dstDmd = getDstCon().getMetaData();
            setQuote(dstDmd.getIdentifierQuoteString());

            /*
            rs = dstDmd.getTypeInfo();
            dstFieldTypes = new HashMap<>();
            while (rs.next()) {
                FieldTypeInfo fti = new FieldTypeInfo();
                fti.setTypName(rs.getString(1));
                fti.setDataType(rs.getInt(2));
                fti.setPrecision(rs.getInt(3));
                fti.setLiteral_prefix(rs.getString(4));
                fti.setLiteral_suffix(rs.getString(5));
                fti.setCreate_params(rs.getString(6));
                fti.setNullable(rs.getShort(7));
                fti.setCase_sensitive(rs.getBoolean(8));
                fti.setSearchable(rs.getShort(9));
                fti.setUnsigned_attribute(rs.getBoolean(10));
                fti.setFixed_rec_scale(rs.getBoolean(11));
                fti.setAutoIncrement(rs.getBoolean(12));
                fti.setLocal_type_name(rs.getString(13));
                fti.setMinimum_scale(rs.getShort(14));
                fti.setMinimum_scale(rs.getShort(15));
                fti.setSql_data_type(rs.getInt(16));
                fti.setSql_datetime_sub(rs.getInt(17));
                fti.setNum_prec_radix(rs.getInt(18));

                dstFieldTypes.put(fti.getDataType(), fti);
            }
             */
            // some other values
            setMaxBinaryLength(dstDmd.getMaxBinaryLiteralLength());
            setMaxTextLength(dstDmd.getMaxCharLiteralLength());
        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, "Exception while reading metadata {0}", sqlex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing resultset {0}", sqlex.getMessage());
                }
            }
        }

    }

    /**
     * @return the dstTable
     */
    public String getDstTable() {
        return dstTable;
    }

    /**
     * @param dstTable the dstTable to set
     */
    public void setDstTable(String dstTable) {
        this.dstTable = dstTable;
    }

    /**
     * @return the fieldNameTypes
     */
    public ArrayList<FieldNameType> getFieldNameTypes() {
        return fieldNameTypes;
    }

    /**
     * @param fieldNameTypes the fieldNameTypes to set
     */
    public void setFieldNameTypes(ArrayList<FieldNameType> fieldNameTypes) {
        this.fieldNameTypes = fieldNameTypes;
    }

    /**
     * @return the databaseProduct
     */
    public String getDatabaseProduct() {
        return databaseProduct;
    }

    /**
     * @param databaseProduct the databaseProduct to set
     */
    public void setDatabaseProduct(String databaseProduct) {
        this.databaseProduct = databaseProduct;
    }

    public static void main(String[] args) {
        try {
            Options options = new Options();

            options.addOption("d", true, "destination DB JDBC Driver (might be null");
            options.addOption("URL", true, "destination DB connection URL ");
            options.addOption("user", true, "destination DB username");
            options.addOption("p", true, "destination DB user password");
            options.addOption("q", true, "Source DB query");
            options.addOption("t", true, "destination DB destination tablename");
            options.addOption("clear", false, "destination DB clear destination table before run  (default=false)");
            options.addOption("create", false, "destination DB destination table before run (default=false)");
            options.addOption("b", true, "destination DB writer batchsize ( default=1000)");
            options.addOption("h", false, "Print this help and exit");

            HelpFormatter formatter = new HelpFormatter();
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if ((args.length == 0) || (cmd.hasOption("h"))) {
                System.out.println("""
                                   sql.fredy.sqltools.DataBaseExport is reading data from a source database and writes into a destination DB.
                                   The source DB when called from the commandline is read from the defaultDBpool.props as described in sql.fredy.connection.DataSource.
                                   
                                   When you are using this class from within your own application, you can set the connection while calling one of the constructors, if it is null
                                   it is using the connection from sql.fredy.connection.DataSource.
                                   
                                   To use this tool, you have to provide these parameters:
                                   - SQL query to run on the source DB
                                   - the name of the destination table, evtl. you need to provide the schema as part of the tablename
                                   - the connection URL to connect to the destination DB 
                                   - the JDBC-Driver must be part of the CLASSPATH
                                   
                                   all other parameter, see below.
                                   
                                   """);
                formatter.printHelp("DataBaseExport", options);
                System.exit(0);
            }

            DataBaseExport dbe = new DataBaseExport();

            // the JDBC driver
            if (cmd.hasOption("d")) {
                dbe.setJdbcDriver(cmd.getOptionValue("d"));
            }

            // the JDBC URL
            if (cmd.hasOption("URL")) {
                dbe.setJdbcUrl(cmd.getOptionValue("URL"));
            } else {
                System.out.println("please provide the JDBC URL to connect to the destination DB (use the -h option to display help");
                System.exit(0);
            }

            // the destination table
            if (cmd.hasOption("t")) {
                dbe.setDstTable(cmd.getOptionValue("t"));
            } else {
                System.out.println("please provide the destination table (use the -h option to display help");
                System.exit(0);
            }

            // the query to export
            if (cmd.hasOption("q")) {
                dbe.setQuery(cmd.getOptionValue("q"));
            } else {
                System.out.println("please provide the query to export (use the -h option to display help");
                System.exit(0);
            }

            // batch size
            if (cmd.hasOption("b")) {
                try {
                    dbe.setChunksize(Integer.parseInt(cmd.getOptionValue("b")));
                } catch (NumberFormatException nfe) {
                    System.out.println("Invalid batch size, using standard value of 1000");
                }
            }

            // the username
            if (cmd.hasOption("user")) {
                dbe.setUser(cmd.getOptionValue("user"));
            }

            // the password
            if (cmd.hasOption("p")) {
                dbe.setPassword(cmd.getOptionValue("p"));
            }

            // create the table
            if (cmd.hasOption("create")) {
                dbe.setCreateDestination(true);
            } else {
                dbe.setCreateDestination(false);
            }
            // clean the destination table
            if (cmd.hasOption("clean")) {
                dbe.setClearDestination(true);
            } else {
                dbe.setClearDestination(false);
            }

            dbe.exportData();

        } catch (ParseException pex) {
            System.out.println("Something went wrong: " + pex.getMessage());
        }

    }

    /**
     * @return the quote
     */
    public String getQuote() {
        return quote.trim();
    }

    /**
     * @param quote the quote to set
     */
    public void setQuote(String quote) {
        this.quote = quote;
    }

    /**
     * @return the dstFieldTypes
     */
    public HashMap<Integer, FieldTypeInfo> getDstFieldTypes() {
        return dstFieldTypes;
    }

    /**
     * @param dstFieldTypes the dstFieldTypes to set
     */
    public void setDstFieldTypes(HashMap<Integer, FieldTypeInfo> dstFieldTypes) {
        this.dstFieldTypes = dstFieldTypes;
    }

    /**
     * @return the dstDmd
     */
    public DatabaseMetaData getDstDmd() {
        return dstDmd;
    }

    /**
     * @param dstDmd the dstDmd to set
     */
    public void setDstDmd(DatabaseMetaData dstDmd) {
        this.dstDmd = dstDmd;
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

    /**
     * @return the buildConnection
     */
    public boolean isBuildConnection() {
        return buildConnection;
    }

    /**
     * @param buildConnection the buildConnection to set
     */
    public void setBuildConnection(boolean buildConnection) {
        this.buildConnection = buildConnection;
    }

    /**
     * @return the dstAdditionalParameter
     */
    public String getDstAdditionalParameter() {
        return dstAdditionalParameter;
    }

    /**
     * @param dstAdditionalParameter the dstAdditionalParameter to set
     */
    public void setDstAdditionalParameter(String dstAdditionalParameter) {
        this.dstAdditionalParameter = dstAdditionalParameter;
    }

    /**
     * @return the srcQuote
     */
    public String getSrcQuote() {
        return srcQuote;
    }

    /**
     * @param srcQuote the srcQuote to set
     */
    public void setSrcQuote(String srcQuote) {
        this.srcQuote = srcQuote;
    }

    /**
     * @return the useDBQuery
     */
    public boolean isUseDBQuery() {
        return useDBQuery;
    }

    /**
     * @param useDBQuery the useDBQuery to set
     */
    public void setUseDBQuery(boolean useDBQuery) {
        this.useDBQuery = useDBQuery;
    }

}
