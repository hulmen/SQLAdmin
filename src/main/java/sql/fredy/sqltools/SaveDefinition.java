/*
 * The MIT License
 *
 * Copyright 2022 fredy.
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
 
This class is fetching the definition of these objects ( only one at a time ) from a defined schema
and writes the result into a file given to this class.  It is fetching the connection from the connection pool of SQL Admin

These Objects of a schema are possible

- table
- view
- function

Fredy Fischer, 2023-03-28

 */
package sql.fredy.sqltools;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import sql.fredy.connection.DataSource;
import sql.fredy.ui.SqlWords;

/**
 *
 * @author a_tkfir
 */
public class SaveDefinition {

    /**
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @return the entityType
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @param entityType the entityType to set
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

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
     * @return the con
     */
    public Connection getCon() {
        logger.log(Level.FINEST, "connection is {0}", con == null ? "null" : "established");

        try {
            if ((con == null) || (con.isClosed())) {
                logger.log(Level.FINE, "fetching connection, because was null");
                con = DataSource.getInstance().getConnection();
                setDatabaseProduct(con.getMetaData().getDatabaseProductName());
                vendorDependendBrackets();
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
     * @return the deleteFile
     */
    public boolean isDeleteFile() {
        return deleteFile;
    }

    /**
     * @param deleteFile the deleteFile to set
     */
    public void setDeleteFile(boolean deleteFile) {
        this.deleteFile = deleteFile;
    }

    /**
     * @return the outputFile
     */
    public File getOutputFile() {
        return outputFile;
    }

    /**
     * @param outputFile the outputFile to set
     */
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
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
     * @return the openBracket
     */
    public String getOpenBracket() {
        return openBracket;
    }

    /**
     * @param openBracket the openBracket to set
     */
    public void setOpenBracket(String openBracket) {
        this.openBracket = openBracket;
    }

    /**
     * @return the closedBracket
     */
    public String getClosedBracket() {
        return closedBracket;
    }

    /**
     * @param closedBracket the closedBracket to set
     */
    public void setClosedBracket(String closedBracket) {
        this.closedBracket = closedBracket;
    }

    private DatabaseMetaData getDmd() {
        if (dmd == null) {
            try {
                con = getCon();
                dmd = con.getMetaData();
                setDatabaseProduct(dmd.getDatabaseProductName());
            } catch (SQLException e) {
                logger.log(Level.INFO, "Connection interrupted, recreating {0}", e.getMessage());
                con = null;
                dmd = null;
                try {
                    DataSource.getInstance().close();
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, "IO Exception while closing connection  {0}", ex.getMessage());
                } catch (SQLException ex) {
                    logger.log(Level.SEVERE, "SQL Exception while closing connection  {0}", ex.getMessage());
                } catch (PropertyVetoException ex) {
                    logger.log(Level.SEVERE, "Property Veto Exception while closting connection  {0}", ex.getMessage());
                } finally {

                }
            } finally {

            }
        }
        return dmd;
    }

    private Logger logger = Logger.getLogger("sql.fredy.sqltools");

    private String schema;
    private String entityType;
    private String fileName;
    private Connection con = null;
    private boolean deleteFile = false;
    private File outputFile;
    private String databaseProduct;
    private String openBracket = "\"", closedBracket = "\"";
    private DatabaseMetaData dmd = null;

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

    public static void main(String[] args) {
        System.out.println("Save the definitions of the tables or views or functions of a specified schema into a file.\nPart of sql.fred.admin by Fredy Fischer");

        String schema = null;
        String fileName = null;
        String entityType = null;
        boolean deleteFile = false;

        Options options = new Options();
        options.addOption("s", true, "the name of the schema to parse");
        options.addOption("f", true, "fully qualified name of the file to export into");
        options.addOption("e", true, "the type of the entity to treat use one of table, view, function ");
        options.addOption("x", false, "overwrite the file, if it already exists");
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("SaveDefinition", options);

        if (args.length > 0) {
            try {
                CommandLine cmd = parser.parse(options, args);

                if (cmd.hasOption("f")) {
                    File file = new File(cmd.getOptionValue("f"));

                    if (cmd.hasOption("x")) {
                        deleteFile = true;
                    }

                    if ((file.exists()) && (!deleteFile)) {
                        System.out.println("File exists and I'm not allowed to delete it, please provide the option -x or provide a file that does not exist");
                        System.exit(0);
                    }
                }

                if (cmd.hasOption("s")) {
                    schema = cmd.getOptionValue("s");
                }

                if (cmd.hasOption("e")) {
                    entityType = cmd.getOptionValue("e");

                    switch (entityType.toLowerCase()) {
                        case "table":
                            break;
                        case "view":
                            break;
                        case "function":
                            break;
                        default:
                            System.out.println("invalid entity type, please use either table, view or function");
                            System.exit(0);
                    }

                }
            } catch (ParseException pe) {
                System.out.println("Invalid parameters " + pe.getMessage());
                System.exit(0);
            }
        } else {
            schema = readFromPrompt("Schema (q = quit)", "q");
            if (schema.equalsIgnoreCase("q")) {
                System.exit(0);
            }

            entityType = readFromPrompt("Entity Type ( q|table|view|function )", "q");
            switch (entityType.toLowerCase()) {
                case "table":
                    break;
                case "view":
                    break;
                case "function":
                    break;
                default:
                    System.exit(0);
            }

            String delFile = readFromPrompt("Delete File if it exists (Y/n/q", "y");
            switch (delFile.toLowerCase()) {
                case "y":
                    deleteFile = true;
                    break;
                case "n":
                    deleteFile = false;
                case "q":
                    System.exit(0);
                default:
                    System.out.println("Wrong answer, assuming No");
                    deleteFile = false;
                    break;
            }

            fileName = readFromPrompt("Fully qualified name of the file to write into (q = quit)", "q");
            if (fileName.equalsIgnoreCase("q")) {
                System.exit(0);
            }

            File file = new File(fileName);

            if ((file.exists()) && (!deleteFile)) {
                System.out.println("File exists and I'm not allowed to delete it, please provide the option -x or provide a file that does not exist");
                System.exit(0);
            }
        }
        SaveDefinition svd = new SaveDefinition(fileName, schema, entityType, deleteFile);

    }
    
            

    private void vendorDependendBrackets() {
        //logger.log(Level.INFO,"DB Product: " + getDatabaseProduct());
        if (getDatabaseProduct().toLowerCase().contains("microsoft")) {
            setOpenBracket("[");
            setClosedBracket("]");
        }
        if (getDatabaseProduct().toLowerCase().contains("mysql")) {
            setOpenBracket("`");
            setClosedBracket("`");
        }
    }

    public SaveDefinition(String fileName, String schema, String entityType, boolean deleteFile) {
        setFileName(fileName);
        setSchema(schema);
        setEntityType(entityType);
        setDeleteFile(deleteFile);
        

        if (!checkFile()) {
            return;
        }

        processEntity();

    }

    private boolean checkFile() {
        boolean ok = true;
        setOutputFile(new File(getFileName()));
        if (getOutputFile().exists()) {
            if (isDeleteFile()) {
                getOutputFile().delete();
            } else {
                logger.log(Level.SEVERE, "File already exists and I'm not allowed to delete it");
                ok = false;
            }
        }
        return ok;
    }

    /*
    to process we loop the entities of entity type within the given schema
     */
    private void processEntity() {
        String[] tableTypes = {getEntityType()};
        createSqlWords();

        ResultSet rs = null;
        try {
            con = getCon();
            dmd = getDmd();
            rs = dmd.getTables(con.getCatalog(), getSchema(), "%", tableTypes);
            while (rs.next()) {
                String entityName = rs.getString(3);
                switch (getEntityType().toLowerCase()) {
                    case "table":
                        getTableDefinition(entityName);
                        break;
                    case "view":
                        appendDataToFile(getViewDefinition(entityName));
                        break;
                    case "function":
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQLException when gathering meta data: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
        } finally {
            if ( rs != null ) {
                try {
                    rs.close();                    
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING,"Exception while closing result set " + sqlex.getMessage());
                }
            }
            
            if ( con != null ) {
                try {
                    con.close();                    
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING,"Exception while closing connection set " + sqlex.getMessage());
                }
            }
            
        }
    }
    
    
    private boolean appendDataToFile(String query) {
        boolean ok = true;
       try {
        Files.write( Paths.get(getFileName()), query.getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
       } catch ( IOException iox) {
           logger.log(Level.SEVERE, "Can not write file {0} Exception :{1}", new Object[]{getFileName(), iox.getMessage()});
           ok = false;
       }
       return ok;
    }
    

    // if one uses Spaces in a field- or table name, it has to be surrounded by "
    private String fixSpaces(String v) {
        if ((v.startsWith(getOpenBracket())) && (v.endsWith(getClosedBracket()))) {
            return v;
        }
        sqlWordsIterator = sqlWords.iterator();
        if (v != null) {
            CharSequence charSequence = "-+*\\/ /.()[]{}%&@#?!,;='^´:<>¦|";

            if ((v.trim().contains(" "))
                    || (v.trim().contains("-"))
                    || (v.trim().contains("+"))
                    || (v.trim().contains("*"))
                    || (v.trim().contains("\\"))
                    || (v.trim().contains("/"))
                    || (v.trim().contains("("))
                    || (v.trim().contains(")"))
                    || (v.trim().contains("["))
                    || (v.trim().contains("]"))
                    || (v.trim().contains("{"))
                    || (v.trim().contains("}"))
                    || (v.trim().contains("%"))
                    || (v.trim().contains("@"))
                    || (v.trim().contains("?"))
                    || (v.trim().contains("!"))
                    || (v.trim().contains(","))
                    || (v.trim().contains(";"))
                    || (v.trim().contains(":"))
                    || (v.trim().contains("<"))
                    || (v.trim().contains(">"))
                    || (v.trim().contains("|"))
                    || (v.trim().contains("¦"))
                    || (v.trim().contains("'"))
                    || (v.trim().contains("^"))
                    || (v.trim().contains("`"))
                    || (v.trim().contains("."))) {

                v = inBrackets(v);
            } else {
                // if there is a reserved SQL-Keyword, we put it in brackets
                while (sqlWordsIterator.hasNext()) {
                    if (v.equalsIgnoreCase((String) sqlWordsIterator.next())) {
                        v = inBrackets(v);
                        break;
                    }
                }
            }
        }
        return v;
    }

    private String inBrackets(String v) {
        // if there are already some ", we remove them as a workaround from DbTreeNode
        v = v.replaceAll("\"", "");
        return getOpenBracket() + v + getClosedBracket();
    }
    private ArrayList<String> sqlWords;
    private Iterator sqlWordsIterator;

    /**
     * @return the sqlWords
     */
    public ArrayList getSqlWords() {
        return sqlWords;
    }

    /**
     * @param sqlWords the sqlWords to set
     */
    public void setSqlWords(ArrayList sqlWords) {
        this.sqlWords = sqlWords;
    }

    private void createSqlWords() {
        SqlWords s = new SqlWords();
        sqlWords = s.getSqlWords();
        sqlWordsIterator = sqlWords.iterator();
    }

    private String getTableDefinition(String table) {
        StringBuilder sb = new StringBuilder();
        sb.append(table).append(": ").append("Table Definition to be implemented");
        return sb.toString();
    }

    private String getViewDefinition(String table) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\n-- start View definition for ").append(table).append(";\n");

        ResultSet viewReaderSet = null;
        PreparedStatement viewReader = null;

        try {
            getCon();

            // if Microsoft                
            if (dmd.getDatabaseProductName().toLowerCase().startsWith("microsoft")) {
                viewReader = getCon().prepareStatement("SELECT definition, uses_ansi_nulls, uses_quoted_identifier, is_schema_bound  FROM sys.sql_modules  WHERE object_id = OBJECT_ID(?)");
                viewReader.setString(1, fixSpaces(getSchema()) + "." + fixSpaces(table));
                viewReaderSet = viewReader.executeQuery();
                if (viewReaderSet.next()) {
                    sb.append(viewReaderSet.getString("Definition"));
                    sb.append("\n;--- end View definition for ").append(table).append(" ;\n");
                }
            }
            
            // mySQL
            if (dmd.getDatabaseProductName().toLowerCase().startsWith("mysql")) {
                viewReader = con.prepareStatement("SELECT  VIEW_DEFINITION FROM INFORMATION_SCHEMA.VIEWS WHERE TABLE_SCHEMA = ? and TABLE_NAME = ?");
                viewReader.setString(1, fixSpaces(getCon().getCatalog()));
                viewReader.setString(2, fixSpaces(table));
                viewReaderSet = viewReader.executeQuery();
                if (viewReaderSet.next()) {
                    sb.append("CREATE OR REPLACE VIEW ").append(fixSpaces(table)).append(" AS (\n").append(viewReaderSet.getString("VIEW_DEFINITION")).append("\n)");
                }
            }
            
            // PostgreSQL
            if (dmd.getDatabaseProductName().toLowerCase().startsWith("postgresql")) {
                viewReader = con.prepareStatement("select definition from pg_views where schemaname = ? and viewname = ?");
                viewReader.setString(1, fixSpaces(getSchema()));
                viewReader.setString(2, fixSpaces(table));
                viewReaderSet = viewReader.executeQuery();
                if (viewReaderSet.next()) {
                    sb.append("CREATE OR REPLACE VIEW ").append(fixSpaces(table)).append(" AS (\n").append(viewReaderSet.getString("definition").replaceAll(";", "")).append("\n)");
                }
            }

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while reading Data to get the create statment {0}", sqlex.getMessage());            
        } finally {
        }

        logger.log(Level.INFO, "View definitions for schema {0} exportet to {1}", new Object[]{getSchema(), getFileName()});
        return sb.toString();
    }

}
