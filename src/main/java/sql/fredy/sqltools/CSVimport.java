/*
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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import sql.fredy.connection.DataSource;

/**
 *
 * @author fredy
 */
public class CSVimport {

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return the fileyType
     */
    public String getFileyType() {
        return fileyType;
    }

    /**
     * @param fileyType the fileyType to set
     */
    public void setFileyType(String fileyType) {
        this.fileyType = fileyType;
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
     * @return the hasHeader
     */
    public boolean isHasHeader() {
        return hasHeader;
    }

    /**
     * @param hasHeader the hasHeader to set
     */
    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
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
     * @return the cleanTable
     */
    public boolean isCleanTable() {
        return cleanTable;
    }

    /**
     * @param cleanTable the cleanTable to set
     */
    public void setCleanTable(boolean cleanTable) {
        this.cleanTable = cleanTable;
    }

    /**
     * @return the runFinisher
     */
    public boolean isRunFinisher() {
        return runFinisher;
    }

    /**
     * @param runFinisher the runFinisher to set
     */
    public void setRunFinisher(boolean runFinisher) {
        this.runFinisher = runFinisher;
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
     * @return the cleaner
     */
    public PreparedStatement getCleaner() {
        return cleaner;
    }

    /**
     * @param cleaner the cleaner to set
     */
    public void setCleaner(PreparedStatement cleaner) {
        this.cleaner = cleaner;
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
     * @return the dropper
     */
    public PreparedStatement getDropper() {
        return dropper;
    }

    /**
     * @param dropper the dropper to set
     */
    public void setDropper(PreparedStatement dropper) {
        this.dropper = dropper;
    }

    /**
     * @return the finisher
     */
    public CallableStatement getFinisher() {
        return finisher;
    }

    /**
     * @param finisher the finisher to set
     */
    public void setFinisher(CallableStatement finisher) {
        this.finisher = finisher;
    }

    /**
     * @return the finisherProcedure
     */
    public String getFinisherProcedure() {
        return finisherProcedure;
    }

    /**
     * @param finisherProcedure the finisherProcedure to set
     */
    public void setFinisherProcedure(String finisherProcedure) {
        this.finisherProcedure = finisherProcedure;
    }

    /**
     * @return the con
     */
    public Connection getCon() {
        logger.log(Level.FINE, "connection is {0}", con == null ? "null" : "established");

        try {
            if ((con == null) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
                DatabaseMetaData dmd = con.getMetaData();
                setDatabaseProduct(dmd.getDatabaseProductName());
                setQuote(dmd.getIdentifierQuoteString());
                vendorDependendencies();
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection {0}", ex.getMessage());
        }

        return con;
    }

    /**
     * @param con the con to set
     */
    public void setCon(Connection con) {
        this.con = con;
    }

    /**
     * @return the batchSize
     */
    public int getBatchSize() {
        return batchSize;
    }

    /**
     * @param batchSize the batchSize to set
     */
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    private Logger logger = Logger.getLogger(getClass().getName());
    private String fileName = null;

    private String fileyType = "EXCEL";
    private String tableName = null;

    private boolean hasHeader = false;
    private boolean dropTable = false;
    private boolean createTable = true;
    private boolean cleanTable = false;
    private boolean runFinisher = false;

    private PreparedStatement writer = null;
    private PreparedStatement cleaner = null;
    private PreparedStatement creator = null;
    private PreparedStatement dropper = null;
    private CallableStatement finisher = null;

    private String finisherProcedure = null;
    private Connection con = null;
    private int batchSize = 1000;

    private String quote;

    private String databaseProduct;
    private String fieldLength;
    private String fieldType;

    private void vendorDependendencies() {
        //logger.log(Level.INFO,"DB Product: " + getDatabaseProduct());
        if (getDatabaseProduct().toLowerCase().contains("microsoft")) {
            setFieldLength("max");
            setFieldType("VARCHAR");
        } else {
            if ((getDatabaseProduct().toLowerCase().contains("mysql")) || (getDatabaseProduct().toLowerCase().contains("maria"))) {
                setFieldLength("32768");
                setFieldType("TEXT");
            } else {
                setFieldLength("32768");
                setFieldType("VARCHAR");
            }
        }
    }

    private String inQuotes(String v) {
        // if there are already some ", we remove them as a workaround from DbTreeNode
        v = v.replaceAll("\"", "");
        return getQuote() + v + getQuote();
    }

    public CSVimport() {
        // Standard constructor....
    }

    public long readCsvFile() {

        // first we check the DB connection
        getCon();
        long numberOfRecords = 0;
        try {
            Reader reader = new InputStreamReader(new FileInputStream(getFileName()));
            Iterable<CSVRecord> records = null;

            switch (getFileyType().toUpperCase()) {
                case "DEFAULT":
                    records = CSVFormat.DEFAULT.parse(reader);
                    break;
                case "EXCEL":
                    records = CSVFormat.EXCEL.parse(reader);
                    break;
                case "INFORMIX":
                    records = CSVFormat.INFORMIX_UNLOAD.parse(reader);
                    break;
                case "INFORMIX_CSV":
                    records = CSVFormat.INFORMIX_UNLOAD_CSV.parse(reader);
                    break;
                case "MONGO_CSV":
                    records = CSVFormat.MONGODB_CSV.parse(reader);
                    break;
                case "MONGO_TSV":
                    records = CSVFormat.MONGODB_TSV.parse(reader);
                    break;
                case "MYSQL":
                    records = CSVFormat.MYSQL.parse(reader);
                    break;
                case "ORACLE":
                    records = CSVFormat.ORACLE.parse(reader);
                    break;
                case "POSTGRESQL_CSV":
                    records = CSVFormat.EXCEL.parse(reader);
                    break;
                case "POSTGRESQL_TEXT":
                    records = CSVFormat.POSTGRESQL_TEXT.parse(reader);
                    break;
                case "RFC4180":
                    records = CSVFormat.RFC4180.parse(reader);
                    break;
                case "TDF":
                    records = CSVFormat.TDF.parse(reader);
                    break;
                default:
                    records = CSVFormat.DEFAULT.parse(reader);
                    break;
            };

            // now we loop the records, if there is a header, we take the first line to generate the fields
            String[] headerFields = null;
            for (CSVRecord record : records) {

                numberOfRecords = record.getRecordNumber();
                if (headerFields == null) {
                    headerFields = new String[record.size()];
                    for (int i = 0; i < record.size(); i++) {
                        if (isHasHeader()) {
                            headerFields[i] = record.get(i);
                        } else {
                            headerFields[i] = "Field" + Integer.toString(i);
                        }
                    }
                    createWriter(headerFields);

                    if (!isHasHeader()) {
                        writeLine(record.getRecordNumber(), record);
                    }

                } else {
                    writeLine(record.getRecordNumber(), record);
                }
            }

            // we are finished
            getWriter().executeBatch();
            logger.log(Level.INFO, "I have loaded {0} Rows into {1}", new Object[]{String.format("%,d", numberOfRecords), getTableName()});

            // now the finisher
            if (getFinisherProcedure() != null) {
                setFinisher(getCon().prepareCall(getFinisherProcedure()));
                getFinisher().execute();
            }

        } catch (FileNotFoundException fne) {
            logger.log(Level.SEVERE, "file not foeund {0}", fne.getMessage());
        } catch (IOException iox) {
            logger.log(Level.SEVERE, "IO Exception {0}", iox.getMessage());
        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, "SQL Exception {0}", sqlex.getMessage());
        } finally {
            if (getCleaner() != null) {
                try {
                    getCleaner().close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing {0}", sqlex.getMessage());
                }
            }
            if (getCreator() != null) {
                try {
                    getCreator().close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing {0}", sqlex.getMessage());
                }
            }
            if (getDropper() != null) {
                try {
                    getDropper().close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing {0}", sqlex.getMessage());
                }
            }
            if (getWriter() != null) {
                try {
                    getWriter().close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing {0}", sqlex.getMessage());
                }
            }
            if (getFinisher() != null) {
                try {
                    getFinisher().close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing {0}", sqlex.getMessage());
                }
            }

            if (getCon() != null) {
                try {
                    getCon().close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "Exception while closing {0}", sqlex.getMessage());
                }
            }
        }
        return numberOfRecords;
    }

    private boolean dstTableExists() {
        boolean dstTbl = false;
        ResultSet rs = null;
        try {
            DatabaseMetaData dmd = con.getMetaData();
            String[] schemaTable = getTableName().split("\\.");

            String catalog = null;
            String schema = null;
            String table = getTableName();

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
            rs = dmd.getTables(catalog, schema, table, tableTypes);
            if (rs.next()) {
                dstTbl = true;
            }

        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, "SQL Exception {0}", sqlex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.SEVERE, "SQL Exception {0}", sqlex.getMessage());
                }
            }
        }
        return dstTbl;
    }

    private void createWriter(String[] headerFields) {
        StringBuilder createQuery = new StringBuilder();
        StringBuilder writeQuery = new StringBuilder();
        StringBuilder writerParam = new StringBuilder();

        createQuery.append("create table ").append(getTableName()).append(" (\n ");
        writeQuery.append("insert into ").append(getTableName()).append("\n(");
        writerParam.append("\nVALUES (");

        boolean firstRound = true;
        for (String field : headerFields) {

            if (!firstRound) {
                createQuery.append("\n,");
                writeQuery.append("\n,");
                writerParam.append(",");
            }
            createQuery.append(fixField(field)).append(getFieldType()).append("(").append(getFieldLength()).append(")");
            writeQuery.append(fixField(field));
            writerParam.append("?");

            if (firstRound) {
                firstRound = false;
            }
        }
        createQuery.append(")");
        writeQuery.append(")\n").append(writerParam).append(")");

        int resultCounter = 0;

        if (getCon() != null) {
            try {

                if ((isDropTable()) && (dstTableExists())) {
                    try {
                        setDropper(getCon().prepareStatement("drop table " + getTableName()));
                        resultCounter = getDropper().executeUpdate();
                        if (resultCounter > 0) {
                            logger.log(Level.INFO, " Table {0} dropped", getTableName());
                        }
                    } catch (SQLException dropperEx) {
                        logger.log(Level.WARNING, "Table {0} can not be dropped, {1}", new Object[]{getTableName(), dropperEx.getMessage()});
                    }
                }

                if (isCreateTable()) {
                    //first we drop the table
                    if (dstTableExists()) {
                        try {
                            setDropper(getCon().prepareStatement("drop table " + getTableName()));
                            resultCounter = getDropper().executeUpdate();
                            if (resultCounter > 0) {
                                logger.log(Level.INFO, " Table {0} dropped", getTableName());
                            }
                        } catch (SQLException dropperEx) {
                            logger.log(Level.WARNING, "Table {0} can not be dropped, {1}", new Object[]{getTableName(), dropperEx.getMessage()});
                        }
                    }
                    // then we create the table
                    if (!dstTableExists()) {
                        try {
                            setCreator(getCon().prepareStatement(createQuery.toString()));
                            resultCounter = getCreator().executeUpdate();
                            if (resultCounter > 0) {
                                logger.log(Level.INFO, " Table {0} created", getTableName());
                            }
                        } catch (SQLException creatorException) {
                            logger.log(Level.WARNING, "Table {0} can not be created, {1}", new Object[]{getTableName(), creatorException.getMessage()});
                            System.out.println("Create Query\n" + createQuery.toString());
                        }
                    }
                }

                try {
                    if ((isCleanTable()) && (dstTableExists())) {
                        setCleaner(getCon().prepareStatement("truncate table " + getTableName()));
                        resultCounter = getCleaner().executeUpdate();
                        if (resultCounter > 0) {
                            logger.log(Level.INFO, "Table {0} cleaned", getTableName());
                        }
                    }
                } catch (SQLException cleanerException) {
                    logger.log(Level.WARNING, "Table {0} can not be truncated, {1}", new Object[]{getTableName(), cleanerException.getMessage()});
                }

                setWriter(getCon().prepareStatement(writeQuery.toString()));

            } catch (SQLException sqlex) {
                logger.log(Level.SEVERE, "SQL Exception {0}", sqlex.getMessage());
            }
        } else {
            logger.log(Level.FINE, "createQuery:\n{0}\ncreateWriter:\n{1}", new Object[]{createQuery.toString(), writeQuery.toString()});
        }
    }

    private void writeLine(long recordNumber, CSVRecord record) {

        if (getCon() == null) {
            System.out.print(String.format("%,d", recordNumber) + "\t");
            for (int i = 0; i < record.size(); i++) {
                System.out.print(record.get(i) + "\t");
            }
            System.out.println();
            return;
        }
        try {
            for (int i = 0; i < record.size(); i++) {
                getWriter().setString(i + 1, record.get(i));
            }

            // we add to batch
            getWriter().addBatch();

            if (recordNumber > getBatchSize()) {
                getWriter().executeBatch();
                logger.log(Level.INFO, "{0} written", String.format("%,d", recordNumber));
            }

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQL Exception {0}", sqlex.getMessage());
        }
    }

    private String fixField(String field) {
        return getQuote() + field + getQuote();
    }

    public static void main(String[] args) {

        try {
            Options options = new Options();
            options.addOption("f", true, "fully qualified name of the CSV-file to load");
            options.addOption("t", true, "schema (if there is) and name of the table to load the data into");
            options.addOption("p", true, "finisher procedure to run right after the load ( default null)");
            options.addOption("bulk", false, "perform a bulk load");
            options.addOption("dir", true, "(bulkload) directory to load from");
            options.addOption("pattern", true, "(bulkload) pattern for files (Java regular expressions, case insensitive");

            options.addOption("header", false, "this CSV file has a header (default false)");
            options.addOption("drop", false, "drop the destination table if it exists before loading (default false)");
            options.addOption("create", false, "create the destination table if i does not exist (default false)");
            options.addOption("truncate", false, "truncate the destination table if it exists (default false)");
            options.addOption("help", false, "print this help and exit");
            options.addOption("h", false, "print this help and exit");

            HelpFormatter formatter = new HelpFormatter();
            CommandLineParser parser = new DefaultParser();
            CommandLine cmd = parser.parse(options, args);

            if (args.length == 0) {
                System.out.println("""                          
                           CSVimport is importing a CSV-file into a table. It is part of Fredy's SQL Admin.
                           If it is running stand alone, it is using the defaultDBpool.props as described in sql.fredy.connection.DataSource to connect to the destination DB.
                                   
                           See the following option on how to use it.
                           If it has to  creates a table, it uses for all fields text type:
                           - SQL Server     --> VARCHAR(max)
                           - MySQL/Maria DB --> TEXT(32768)
                           - all others     --> VARCHAR(32768)
                           
                           there might be some limitations, so it is mostly best to create the destination table before
                           be aware, that I considere all fields as String, so it makes sense to first load your CSV-file into
                           a stating area and from there on fill your production table. To do so, you can set a finisher procedure.
                               
                           """);
                formatter.printHelp("CSVImport", options);
                System.exit(0);
            }
            if ((cmd.hasOption("help")) || (cmd.hasOption("h"))) {
                System.out.println("""
                           option help
                           CSVimport is importing a CSV-file into a table. It is part of Fredy's SQL Admin.
                           If it is running stand alone, it is using the defaultDBpool.props as described in sql.fredy.connection.DataSource to connect to the destination DB.
                                   
                           See the following option on how to use it.
                           If it has to  creates a table, it uses for all fields text type:
                           - SQL Server     --> VARCHAR(max)
                           - MySQL/Maria DB --> TEXT(32768)
                           - all others     --> VARCHAR(32768)
                                   
                           there might be some limitations, so it is mostly best to create the destination table before
                           be aware, that I considere all fields as String, so it makes sense to first load your CSV-file into
                           a stating area and fro there on fill your production table. To do so, you can set a finisher procedure.
                               
                           """);
                formatter.printHelp("CSVImport", options);
                System.exit(0);
            }

            CSVimport loader = new CSVimport();

            if (cmd.hasOption("f")) {
                loader.setFileName(cmd.getOptionValue("f"));
            } else {
                System.out.println("Please provide the file to load");
                System.exit(0);
            }
            if (cmd.hasOption("t")) {
                loader.setTableName(cmd.getOptionValue("t"));
            } else {
                System.out.println("Please provide the table to load into");
                System.exit(0);
            }

            if (cmd.hasOption("p")) {
                loader.setFinisherProcedure(cmd.getOptionValue("p"));
            } else {
                loader.setFinisherProcedure(null);
            }

            if (cmd.hasOption("header")) {
                loader.setHasHeader(true);
            } else {
                loader.setHasHeader(false);
            }
            if (cmd.hasOption("drop")) {
                loader.setDropTable(true);
            } else {
                loader.setDropTable(false);
            }
            if (cmd.hasOption("create")) {
                loader.setCreateTable(true);
            }
            if (cmd.hasOption("truncate")) {
                loader.setCleanTable(true);
            } else {
                loader.setCleanTable(false);
            }

            if (cmd.hasOption("bulk")) {
                
                /*
                If a directory must be processed, we loop it here
                */
                String directory = null;
                String pattern = null;

                if (cmd.hasOption("dir")) {
                    directory = cmd.getOptionValue("dir");
                }
                if (cmd.hasOption("pattern")) {
                    pattern = cmd.getOptionValue("pattern");
                }

                if ((directory == null) || (pattern == null)) {
                    System.out.println("to bulk load files, please provide the directory and the file pattern");
                    System.exit(0);
                }

                try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directory))) {
                    for (Path path : stream) {
                        if (!Files.isDirectory(path)) {
                           
                            Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                            Matcher m = p.matcher(path.getFileName().toString());
                            if (m.matches()) {
                                System.out.println("Processing " + path.toString());
                                loader.setFileName(path.toString());
                                loader.readCsvFile();
                            }
                        }
                    }
                } catch (IOException iox) {
                    System.err.println(iox.getMessage());
                }

            } else {
                loader.readCsvFile();
            }

        } catch (ParseException pex) {
            System.out.println("Something went wrong: " + pex.getMessage());
        }

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

    /**
     * @return the fieldLength
     */
    public String getFieldLength() {
        return fieldLength;
    }

    /**
     * @param fieldLength the fieldLength to set
     */
    public void setFieldLength(String fieldLength) {
        this.fieldLength = fieldLength;
    }

    /**
     * @return the fieldType
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * @param fieldType the fieldType to set
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * @return the quote
     */
    public String getQuote() {
        return quote;
    }

    /**
     * @param quote the quote to set
     */
    public void setQuote(String quote) {
        this.quote = quote;
    }
}
