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
 */

 /*
This class is to import a simple excelfile into the actual db.
To load, one has to provide the tablename of the destination table.  
This tablename must include the schema, the tool does not care of the schema

It expects, the first line of the excelfile contains the fieldnames, if fieldnames
appears more then once, it just adds a number to it.

if there is no header, it enumerates the columsn with Field_nnn

to load, it uses apache poi libraries

 */
package sql.fredy.sqltools;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.DefaultParser;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sql.fredy.connection.DataSource;

/**
 *
 * @author fredy
 */
public class ExcelImport {

    /**
     * @return the rewriteColumnNames
     */
    public boolean isRewriteColumnNames() {
        return rewriteColumnNames;
    }

    /**
     * @param rewriteColumnNames the rewriteColumnNames to set
     */
    public void setRewriteColumnNames(boolean rewriteColumnNames) {
        this.rewriteColumnNames = rewriteColumnNames;
    }

    /**
     * @return the dropDestinationTable
     */
    public boolean isDropDestinationTable() {
        return dropDestinationTable;
    }

    /**
     * @param dropDestinationTable the dropDestinationTable to set
     */
    public void setDropDestinationTable(boolean dropDestinationTable) {
        this.dropDestinationTable = dropDestinationTable;
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

    public ExcelImport(File file, String tablename, boolean header, boolean rewriteColumnNames, boolean dropDestinationTable) {

        setRewriteColumnNames(rewriteColumnNames);
        setDropDestinationTable(dropDestinationTable);

        if (file.getPath().toLowerCase().endsWith("xls")) {
            setXlsType(XLS);
        }
        if (file.getPath().toLowerCase().endsWith("xlsx")) {
            setXlsType(XLSX);
        }
        if (0 == getXlsType()) {
            logger.log(Level.SEVERE, "Invalid file extension, use .xls or .xlsx to load it");
            return;
        }
        setTablename(tablename);
        setHeader(header);
        fields = new HashMap<>();
        readExcel(file);

    }

    private int xlsType = 0;

    private String tablename = null;
    private HashMap<String, String> fields = null;

    private Logger logger = Logger.getLogger("sql.fredy.sqltools");
    private boolean header = true;

    private static final int XLSX = 1;
    private static final int XLS = 2;

    private boolean rewriteColumnNames = true;
    private boolean dropDestinationTable = true;

    private Connection con = null;

    /**
     * @return the tablename
     */
    public String getTablename() {
        return tablename;
    }

    /**
     * @param tablename the tablename to set
     */
    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    private String checkHeaderName(String name) {

        // let us first check the name of the column
        name = fixColumnName(name);

        // second we check, if this field is already here, if not, we add it and return the name
        if (!fields.containsKey(name)) {
            fields.put(name.trim(), name.trim());
        } else {
            int i = 0;
            while (true) {
                i++;
                name = name + Integer.toString(i);
                if (!fields.containsKey(name)) {
                    fields.put(name.trim(), name.trim());
                    break;
                }
            }

        }
        return name.trim();
    }

    /*
    This methode fixes the column name by removing these characters from the name
    [], ' "      
    
     */
    private String fixColumnName(String column) {
        if (isRewriteColumnNames()) {
            column = column.replaceAll("[\"'\\[\\]*]", "_");
            column = column.replaceAll("ç", "c");
        }
        return column;
    }

    private String createTable(File file) {
        Workbook workbook = null;
        StringBuilder sb = new StringBuilder();
        StringBuilder insertStmt = new StringBuilder();

        try {
            if (getXlsType() == XLSX) {
                workbook = new XSSFWorkbook(new FileInputStream(file));
            }
            if (getXlsType() == XLS) {
                workbook = new HSSFWorkbook(new FileInputStream(file));
            }

            Sheet sheet = workbook.getSheetAt(0);

            int rownum = 0;
            int cellNum = 0;

            // find the Header lines in Line 1
            Row row = sheet.getRow(0);
            int lastColumn = Math.max(row.getLastCellNum(), 1);
            String[] headers = new String[lastColumn];

            for (int ci = 0; ci < lastColumn; ci++) {
                if (isHeader()) {
                    Cell cell = row.getCell(ci);
                    try {
                        headers[ci] = checkHeaderName(cell.getStringCellValue());
                    } catch (Exception e) {

                    }
                    //System.out.println(Integer.toString(ci) + " - " + cell.getStringCellValue());
                } else {
                    try {
                        headers[ci] = "Field_" + Integer.toString(ci);
                    } catch (Exception e) {

                    }
                }
            }

            // we read the first data row to guss the field type
            int firstDataRow = 1;
            if (!isHeader()) {
                firstDataRow = 0;
            }

            row = sheet.getRow(firstDataRow);
            FieldDefinition[] fieldList = new FieldDefinition[lastColumn];
            for (int ci = 0; ci < lastColumn; ci++) {
                fieldList[ci] = new FieldDefinition();
                fieldList[ci].setName((String) headers[ci]);
                Cell cell = row.getCell(ci);
                if ((cell != null) && (cell.getCellType() == CellType.NUMERIC)) {

                    if (DateUtil.isCellDateFormatted(cell)) {
                        String cellFormat = cell.getCellStyle().getDataFormatString();
                        fieldList[ci].setType("DATETIME");

                        if ((cellFormat.contains("yyyy")) && (cellFormat.contains("hh:mm"))) {
                            fieldList[ci].setType("DATETIME");
                        }
                        if ((cellFormat.contains("yyyy")) && (!cellFormat.contains("hh:mm"))) {
                            fieldList[ci].setType("DATE");
                        }
                        if ((!cellFormat.contains("yyyy")) && (cellFormat.contains("hh:mm"))) {
                            fieldList[ci].setType("TIME");
                        }
                        SimpleDateFormat sf = new SimpleDateFormat(cell.getCellStyle().getDataFormatString());
                        //System.out.print(sdfDT.format(cell.getDateCellValue())+ "\t");
                    } else {
                        fieldList[ci].setType("FLOAT");
                        //System.out.print(cell.getNumericCellValue()+ "\t");
                    }
                } else {
                    fieldList[ci].setLength(8000);
                    fieldList[ci].setType("VARCHAR");
                }
            }

            // we create the Create Table Statement and the InserStatement;
            sb.append("CREATE TABLE ");
            sb.append(getTablename()).append(" (\n");

            insertStmt.append("insert into ");
            insertStmt.append(getTablename()).append(" (\n");

            for (int i = 0; i < fieldList.length; i++) {
                FieldDefinition fd = fieldList[i];
                sb.append("\t").append(getOpenBracket()).append(headers[i]).append(getClosedBracket()).append(" ").append(fd.getType());
                if (fd.getType().equalsIgnoreCase("VARCHAR")) {  // (fd.getLength() > 0)   
                    if (getDatabaseProduct().toLowerCase().contains("mysql")) {
                        sb.append("(4096)");
                    } else {
                        sb.append("(max)");
                    }
                }
                if (fd.getType().equalsIgnoreCase("FLOAT")) {
                    if (getDatabaseProduct().toLowerCase().contains("mysql")) {
                        sb.append("(32)");
                    }
                }
                insertStmt.append("\t").append(getOpenBracket()).append(headers[i]).append(getClosedBracket());

                if (i < fieldList.length - 1) {
                    insertStmt.append(",");
                    sb.append(",\n");
                }
            }
            sb.append("\n)");
            insertStmt.append(") values (");

            for (int i = 0; i < fieldList.length; i++) {
                insertStmt.append("?");
                if (i < fieldList.length - 1) {
                    insertStmt.append(",");
                }
            }
            insertStmt.append(")\n");

        } catch (IOException ex) {
            logger.log(Level.WARNING, "Exception while preparing table {0}", ex.getMessage());
        }

        Statement stmt = null;
        ResultSet rs = null;

        //System.out.println("Create Table Statement:\n" + sb.toString() + "\n");
        try {

            stmt = getCon().createStatement();

            // first we drop the table, if it exists and if we set it to yes
            DatabaseMetaData dmd = con.getMetaData();

            // does this table exist?
            String[] tableNamer = getTablename().split("\\.");
            String theSchema = null;
            String theName = null;
            if (tableNamer.length > 1) {
                theSchema = tableNamer[0];
                theName = tableNamer[1];
            } else {
                theName = getTablename();
            }
            rs = dmd.getTables(null, theSchema, theName, new String[]{"TABLE"});
            if ((rs.next()) && (isDropDestinationTable())) {
                stmt.executeUpdate("DROP TABLE " + getTablename());
            }
            stmt.executeUpdate(sb.toString());

        } catch (SQLException ex) {
            logger.log(Level.WARNING, "SQL Exception {0}", ex.getMessage());
            if (getErrorMessage() == null) {
                setErrorMessage(ex.getMessage());
            } else {
                setErrorMessage(getErrorMessage() + "\n" + ex.getMessage());
            }
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {

                }
            }

            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {

                }
            }

        }
        return insertStmt.toString();
    }

    private int readExcel(File file) {

        logger.log(Level.INFO, "processing file {0}", file.getAbsolutePath());
        setErrorMessage(null);

        FileInputStream fis = null;
        Workbook workbook = null;
        PreparedStatement writer = null, fileUpdater = null;
        String insertStmt = null;
        int rownum = 0;

        try {
            getCon();
            insertStmt = createTable(file);
            writer = con.prepareStatement(insertStmt);

            if (getXlsType() == XLSX) {
                workbook = new XSSFWorkbook(new FileInputStream(file));
            }
            if (getXlsType() == XLS) {
                workbook = new HSSFWorkbook(new FileInputStream(file));
            }

            Sheet sheet = workbook.getSheetAt(0);

            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

            int rowStart = Math.min(15, sheet.getFirstRowNum());
            int rowEnd = Math.max(1400, sheet.getLastRowNum());
            System.out.println(getClass().getName() + ".readExcel(" + file.getAbsolutePath() + ") Expecting to load " + String.format("%,d", rowEnd) + " Rows");

            Row row = sheet.getRow(0);
            int lastColumn = Math.max(row.getLastCellNum(), 1);

            for (int rowNum = rowStart; rowNum <= rowEnd; rowNum++) {
                row = (Row) sheet.getRow(rowNum);
                if (row == null) {
                    continue;
                }

                for (int i = 0; i < lastColumn; i++) {
                    Cell cell = row.getCell(i);

                    // header?
                    if ((isHeader()) && (rownum == 0)) {

                    } else {

                        if (cell == null) {
                            writer.setNull(i + 1, java.sql.Types.VARCHAR);
                            continue;
                        }

                        if (cell.getCellType().equals(CellType.STRING)) {
                            writer.setString(i + 1, cell.getStringCellValue());
                        } else {
                            if (cell.getCellType().equals(CellType.NUMERIC)) {
                                if (DateUtil.isCellDateFormatted(cell)) {
                                    writer.setTimestamp(i + 1, new java.sql.Timestamp(cell.getDateCellValue().getTime()));
                                } else {
                                    try {
                                        double d = cell.getNumericCellValue();
                                        writer.setDouble(i + 1, d);
                                    } catch (Exception e) {
                                        writer.setNull(i + 1, java.sql.Types.DOUBLE);
                                    }
                                }
                            } else {
                                if (cell.getCellType().equals(CellType.BOOLEAN)) {
                                    writer.setBoolean(i + 1, cell.getBooleanCellValue());
                                } else {
                                    if (cell.getCellType().equals(CellType.FORMULA)) {
                                        try {
                                            DataFormatter formatter = new DataFormatter();
                                            Cell newCell = formulaEvaluator.evaluateInCell(cell);
                                            writer.setString(i + 1, formatter.formatCellValue(newCell));
                                        } catch (Exception e) {
                                            DataFormatter formatter = new DataFormatter();
                                            writer.setString(i + 1, formatter.formatCellValue(cell));
                                        }
                                    } else {
                                        if (!cell.getCellType().equals(CellType.ERROR)) {
                                            writer.setString(i + 1, cell.getStringCellValue());
                                        } else {
                                            writer.setNull(i + 1, java.sql.Types.VARCHAR);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (rownum > 0) {
                    if (rownum % 1000 == 0) {
                        logger.log(Level.INFO, "Processed {0}", rownum);

                    }
                    writer.addBatch();
                }
                rownum++;
                if (rownum % 5000 == 0) {
                    writer.executeBatch();
                }
            }
            writer.executeBatch();

            logger.log(Level.INFO, "{0} successfully loaded", file.getAbsolutePath());

        } catch (SQLException ex) {
            logger.log(Level.WARNING, "SQL Exception {0}", ex.getMessage());
            ex.printStackTrace();
            //System.out.println("\n" + insertStmt);
            if (getErrorMessage() == null) {
                setErrorMessage(ex.getMessage());
            } else {
                setErrorMessage(getErrorMessage() + "\n" + ex.getMessage());
            }
            //ex.printStackTrace();
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception {0}", ex.getMessage());
            if (getErrorMessage() == null) {
                setErrorMessage(ex.getMessage());
            } else {
                setErrorMessage(getErrorMessage() + "\n" + ex.getMessage());
            }
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(ExcelImport.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (SQLException ex) {
                    logger.log(Level.WARNING, "SQL Exception {0}", ex.getMessage());
                }
            }
            if (fileUpdater != null) {
                try {
                    fileUpdater.close();
                } catch (SQLException ex) {
                    logger.log(Level.WARNING, "SQL Exception {0}", ex.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    logger.log(Level.WARNING, "SQL Exception {0}", ex.getMessage());
                }
            }
        }

        return rownum;
    }

    /**
     * @return the xlsType
     */
    public int getXlsType() {
        return xlsType;
    }

    /**
     * @param xlsType the xlsType to set
     */
    public void setXlsType(int xlsType) {
        this.xlsType = xlsType;
    }

    /**
     * @return the header
     */
    public boolean isHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(boolean header) {
        this.header = header;
    }

    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    private Connection getCon() {
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

    private String errorMessage;

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private String databaseProduct;
    private String openBracket = "\"";
    private String closedBracket = "\"";

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
        System.out.println("Load an Excel file into the DB\nFredy Fischer, October 2022\n");
        String tableName = null;
        File file = null;
        boolean header = false;
        boolean rewriteNames = true;
        boolean dropDestination = true;

        Options options = new Options();
        options.addOption("f", true, "fully qualified name of the Excel file to load");
        options.addOption("t", true, "the name of the table to load into");
        options.addOption("d", false, "do not drop the destination table if it exists");
        options.addOption("r", false, "do not replace special characters in the column name");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("ExcelImport", options);

        if (args.length > 0) {
            try {
                CommandLine cmd = parser.parse(options, args);

                if (cmd.hasOption("h")) {
                    header = true;
                }
                if (cmd.hasOption("t")) {
                    tableName = cmd.getOptionValue("f");
                }
                if (cmd.hasOption("f")) {
                    file = new File(cmd.getOptionValue("f"));
                }

                if (cmd.hasOption("d")) {
                    dropDestination = false;
                }
                if (cmd.hasOption("r")) {
                    rewriteNames = false;
                }

            } catch (ParseException pe) {
                System.out.println("Invalid parameters " + pe.getMessage());
                System.exit(0);
            }

        } else {
            String filename = readFromPrompt("\nFully qualified name of the Excelfile (q=quit)", "q");
            if (filename.equalsIgnoreCase("q")) {
                System.exit(0);
            }
            file = new File(filename);

            tableName = readFromPrompt("Tablename to import into (q=quit)", "q");
            if (tableName.equalsIgnoreCase("q")) {
                System.exit(0);
            }

            String hasHeader = readFromPrompt("First line is header: (Y/n)", "y");
            switch (hasHeader.toLowerCase()) {
                case "q":
                    System.exit(0);
                case "y":
                    header = true;
                    break;
                case "n":
                    header = false;
                    break;
                default:
                    System.out.println("Invalid value");
                    System.exit(0);

            }
            String rewrite = readFromPrompt("replace special characters in column name: (y/N)", "n");
            switch (rewrite.toLowerCase()) {
                case "q":
                    System.exit(0);
                case "y":
                    rewriteNames = true;
                    break;
                case "n":
                    rewriteNames = false;
                    break;
                default:
                    System.out.println("Invalid value");
                    System.exit(0);
            }
            String dropTable = readFromPrompt("drop destination table if exist: (Y/n", "y");
            switch (dropTable.toLowerCase()) {
                case "q":
                    System.exit(0);
                case "y":
                    dropDestination = true;
                    break;
                case "n":
                    dropDestination = false;
                    break;
                default:
                    System.out.println("Invalid value");
                    System.exit(0);

            }

        }

        ExcelImport excelImport = new ExcelImport(file, tableName, header, rewriteNames, dropDestination);

    }
}
