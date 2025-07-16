package sql.fredy.sqltools;

/**
 * XLSExport exports the result of a query into a XLS-file. To do this it is
 * using HSSF from the Apache POI Project: http://jakarta.apache.org/poi
 *
 * Version 1.0 Date 7. aug. 2003 Author Fredy Fischer
 *
 * XLSExport is part of the Admin-Suite * Once instantiated there are the
 * following steps to go to get a XLS-file out of a query
 *
 * XLSExport xe = new XLSExport(java.sql.Connection con)
 * xe.setQuery(java.lang.String query) please set herewith the the query to get
 * its results as XLS-file
 *
 * int xe.createXLS(java.lang.String fileName) this will then create the
 * XLS-File. If this file already exists, it will be overwritten!
 *
 * it returns the number of rows written to the File
 *
 * 2015-11-16 Creating an additional Worksheet containing the SQL-Query
 *
 *
 * Admin is a Tool around SQL to do basic jobs for DB-Administrations, like: -
 * create/ drop tables - create indices - perform sql-statements - simple form -
 * a guided query and a other usefull things in DB-arena
 *
 * Copyright (c) 2017 Fredy Fischer, sql@hulmen.ch
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import java.beans.PropertyVetoException;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.sql.*;
import java.util.logging.*;
import org.apache.poi.hssf.usermodel.*;

import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.jsoup.Jsoup;
import sql.fredy.connection.DataSource;

public class XLSExport {

    /**
     * @return the dateStyle
     */
    public CellStyle getDateStyle() {
        return dateStyle;
    }

    /**
     * @param dateStyle the dateStyle to set
     */
    public void setDateStyle(CellStyle dateStyle) {
        this.dateStyle = dateStyle;
    }

    /**
     * @return the dateTimeStyle
     */
    public CellStyle getDateTimeStyle() {
        return dateTimeStyle;
    }

    /**
     * @param dateTimeStyle the dateTimeStyle to set
     */
    public void setDateTimeStyle(CellStyle dateTimeStyle) {
        this.dateTimeStyle = dateTimeStyle;
    }

    /**
     * @return the integerStyle
     */
    public CellStyle getIntegerStyle() {
        return integerStyle;
    }

    /**
     * @param integerStyle the integerStyle to set
     */
    public void setIntegerStyle(CellStyle integerStyle) {
        this.integerStyle = integerStyle;
    }

    /**
     * @return the flotingPointStyle
     */
    public CellStyle getFlotingPointStyle() {
        return floatingPointStyle;
    }

    /**
     * @param flotingPointStyle the flotingPointStyle to set
     */
    public void setFlotingPointStyle(CellStyle flotingPointStyle) {
        this.floatingPointStyle = flotingPointStyle;
    }

    private Logger logger;

    private boolean removeHTML = false;
    private Connection con = null;

    private CellStyle dateStyle;
    private CellStyle dateTimeStyle;
    private CellStyle integerStyle;
    private CellStyle floatingPointStyle;

    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    public Connection getCon() {

        try {
            if ((null == con) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {

        }

        return con;
    }

    String query = null;

    /**
     * Get the value of query.
     *
     * @return value of query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the value of query.
     *
     * @param v Value to assign to query.
     */
    public void setQuery(String v) {
        this.query = v;
    }

    java.sql.SQLException exception;

    /**
     * Get the value of exception.
     *
     * @return value of exception.
     */
    public java.sql.SQLException getException() {
        return exception;
    }

    /**
     * Set the value of exception.
     *
     * @param v Value to assign to exception.
     */
    public void setException(java.sql.SQLException v) {
        this.exception = v;
    }

    private PreparedStatement pstmt = null;

    /*
     is this file xlsx or xls?
     we detect this out of the filename extension
     */
    private boolean xlsx = true;

    private void checkXlsx(String fileName) {
        String[] extension = fileName.split("\\.");
        int l = extension.length - 1;
        String fileType = extension[l].toLowerCase();
        if ("xlsx".equals(fileType)) {
            setXlsx(true);
            org.apache.poi.openxml4j.util.ZipSecureFile.setMinInflateRatio(0.00000001);
        }
    }

    /*
     we need to check, if the extension of the Filename is either xls or xlsx if not,
     we set to xlsx as default
     */
    private String fixFileName(String f) {
        String prefix = "", postfix = "";
        String fixed = f;

        int i = f.lastIndexOf(".");

        // no postfix at all set
        if (i < 0) {
            fixed = f + ".xlsx";
        } else {
            prefix = f.substring(0, i);
            postfix = f.substring(i + 1);

            logger.log(Level.FINE, "Prefix: {0} Postfix: {1}", new Object[]{prefix, postfix});

            if ((postfix.equalsIgnoreCase("xlsx")) || (postfix.equalsIgnoreCase("xls"))) {
                // nothing to do                
            } else {
                postfix = "xlsx";
            }
            fixed = prefix + "." + postfix;
        }

        logger.log(Level.INFO, "Filename: {0}", fixed);
        return fixed;
    }

    private void execsubQuery(String q) {
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = con.createStatement();
            if (stmt.execute(q)) {
                rs = stmt.getResultSet();
            } else {
                stmt.executeUpdate(q);
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQLException in subQuery {0}", sqlex.getMessage());
        } finally {
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQLException in subQuery {0}", sqlex.getMessage());
                }
            }
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQLException in subQuery {0}", sqlex.getMessage());
                }
            }
        }
    }

    /**
     * Create the XLS-File named fileName
     *
     * @param fileName is the Name (incl. Path) of the XLS-file to create
     * @return number of Rows written
     *
     *
     */
    public int createXLS(String fileName) {
        // I also need to have a file to write into
        if (fileName == null) {
            logger.log(Level.WARNING, "Need to know where to write into");
            return 0;
        }
        fileName = fixFileName(fileName);
        checkXlsx(fileName);
        int row = 0;
        row = createWorkbook();
        try {

            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            fileOut.close();

            logger.log(Level.INFO, "File created");
            logger.log(Level.INFO, "Wrote {0} rows into {1}", new Object[]{String.format("%,d", row), fileName});

        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while writing xls-File: {0}", e.getMessage().toString());
            //e.printStackTrace();
        }

        return row;
    }

    public int createXLS(OutputStream output) {
        int row = 0;
        setXlsx(false);
        try {
            row = createWorkbook();
            System.out.println(getClass().getName() + ".createXLS(outputSstream) Start writing");
            wb.write(output);
            wb.close();
            System.out.println(getClass().getName() + ".createXLS(outputSstream) workbook closed");
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Exception while writing xls-File: " + ex.getMessage().toString());
        }
        return row;
    }

    public int createXLSX(OutputStream output) {
        int row = 0;
        //setXlsx(true);
        try {
            row = createWorkbook();
            wb.write(output);
            ((org.apache.poi.xssf.streaming.SXSSFWorkbook) wb).close();
            return row;
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Exception while writing xlsx-File: " + ex.getMessage().toString());
        }
        return row;
    }

    Workbook wb;

    private int createWorkbook() {

        // I need to have a query to process
        if ((getQuery() == null) && (getPstmt() == null)) {
            logger.log(Level.WARNING, "Need to have a query to process");
            return 0;
        }

        // I need to have a connection to the RDBMS
        if (getCon() == null) {
            logger.log(Level.WARNING, "Need to have a connection to process");
            return 0;
        }

        //Statement stmt = null;
        ResultSet resultSet = null;
        ResultSetMetaData rsmd = null;

        // I need to have a connection to the RDBMS
        if (con == null) {
            logger.log(Level.WARNING, "Need to have a connection to process");
            return 0;
        }

        try {

            // first we have to create the Statement
            if (getPstmt() == null) {

                /*
                if there are several statements, divided by ; we execute them ,
                we expect, the last part contains the result to display.
                
                To have a clean querylist, we remove all the empty lines
                 */
                String[] subQuery = getQuery().split(";");
                ArrayList<String> queries = new ArrayList();

                for (String subQuery1 : subQuery) {
                    if (subQuery1.length() > 5) {
                        queries.add(subQuery1);
                    }
                }

                // now we iterate the cleaned up queries to execute subqueries, if any
                for (int i = 0; i < queries.size() - 1; i++) {
                    execsubQuery((String) queries.get(i));
                }

                //pstmt = getCon().prepareStatement(getQuery());
                pstmt = con.prepareStatement(queries.get(queries.size() - 1));
            }

            //stmt = getCon().createStatement();
        } catch (SQLException sqle1) {
            setException(sqle1);
            logger.log(Level.WARNING, "Can not create Statement. Message: {0}", sqle1.getMessage().toString());
            sqle1.printStackTrace();
            return 0;
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQLException {0}", sqlex.getMessage());
                }
            }
        }

        logger.log(Level.FINE, "Query   : " + getQuery());
        logger.log(Level.FINE, "Starting export...");

        // create an empty sheet
        Sheet sheet;
        Sheet sqlsheet;
        CreationHelper createHelper = null;
        //XSSFSheet xsheet; 
        //HSSFSheet sheet;

        if (isXlsx()) {

            wb = new SXSSFWorkbook();
            createHelper = wb.getCreationHelper();

        } else {
            wb = new HSSFWorkbook();
            createHelper = wb.getCreationHelper();
        }

        // we create the styles
        dateStyle = wb.createCellStyle();
        dateTimeStyle = wb.createCellStyle();
        integerStyle = wb.createCellStyle();
        floatingPointStyle = wb.createCellStyle();

        sheet = wb.createSheet("Data Export");

        // create a second sheet just containing the SQL Statement
        sqlsheet = wb.createSheet("SQL Statement");
        Row sqlrow = null;
        Cell sqltext = null;//sqlrow.createCell(0);
        CellStyle style = wb.createCellStyle();
        style.setWrapText(true);
        try {
            if (getQuery() != null) {
                String[] parts = getQuery().split(";");
                for (int i = 0; i < parts.length; i++) {
                    sqlrow = sqlsheet.createRow(i);
                    sqltext = sqlrow.createCell(0);
                    sqltext.setCellValue(parts[i]);
                    sqltext.setCellStyle(style);
                    //sqltext.setCellValue(getQuery());
                }
            } else {
                sqlsheet.createRow(0);
                sqltext = sqlrow.createCell(0);
                sqltext.setCellValue(pstmt.toString());
                sqltext.setCellStyle(style);
            }
        } catch (Exception lex) {

        }

        Row r = null;

        int row = 0;  // row    number
        int col = 0;  // column number
        int columnCount = 0;

        try {
            //resultSet = stmt.executeQuery(getQuery());
            resultSet = pstmt.executeQuery();
            logger.log(Level.FINE, "query executed");
        } catch (SQLException sqle2) {
            setException(sqle2);
            logger.log(Level.WARNING, "Can not execute query. Message: {0}", sqle2.getMessage().toString());
            sqle2.printStackTrace();
            return 0;
        }

        // create Header in XLS-file
        ArrayList<String> head = new ArrayList();

        try {
            rsmd = resultSet.getMetaData();
            logger.log(Level.FINE, "Got MetaData of the resultset");

            columnCount = rsmd.getColumnCount();
            logger.log(Level.FINE, Integer.toString(columnCount)
                    + " Columns in this resultset");

            r = sheet.createRow(row);  // titlerow

            if ((!isXlsx()) && (columnCount > 255)) {
                columnCount = 255;
            }

            CellStyle cellStyle = wb.createCellStyle();
            cellStyle.setAlignment(HorizontalAlignment.CENTER);
            for (int i = 0; i < columnCount; i++) {

                // we create the cell
                Cell cell = r.createCell(col);

                // set the value of the cell
                cell.setCellValue(rsmd.getColumnName(i + 1));
                head.add(rsmd.getColumnName(i + 1));

                // then we align center
                // now we make it bold
                //HSSFFont f = wb.createFont();
                Font headerFont = wb.createFont();
                headerFont.setBold(true);
                cellStyle.setFont(headerFont);

                //cellStyle.setFont(f);
                // adapt this font to the cell
                cell.setCellStyle(cellStyle);

                col++;
            }
        } catch (SQLException sqle3) {
            setException(sqle3);
            logger.log(Level.WARNING, sqle3.getMessage().toString() + "Can not create XLS-Header. Message: ");
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {

                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {

                }
            }
            return 0;
        }

        dateStyle = wb.createCellStyle();
        dateTimeStyle = wb.createCellStyle();
        CellStyle timeStyle = wb.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        dateTimeStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
        timeStyle.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm:ss"));

        // looping the resultSet
        int wbCounter = 0;
        try {
            CellStyle[] cellStyle = new CellStyle[columnCount];
            while (resultSet.next()) {

                // this is the next row
                col = 0; // put column counter back to 0 to start at the next row
                row++;  // next row

                // create a new sheet if more then 60'000 Rows and xls file
                if ((!isXlsx()) && (row % 65530 == 0)) {
                    wbCounter++;
                    row = 0;

                    sheet = wb.createSheet("Data Export " + Integer.toString(wbCounter));
                    logger.log(Level.INFO, "created a further page because of a huge amount of data");

                    // create the head
                    r = sheet.createRow(row);  // titlerow
                    for (int i = 0; i < head.size(); i++) {

                        // we create the cell
                        Cell cell = r.createCell(col);

                        // set the value of the cell
                        cell.setCellValue((String) head.get(i));

                        // then we align center
                        cellStyle[i] = wb.createCellStyle();
                        cellStyle[i].setAlignment(HorizontalAlignment.CENTER);

                        // now we make it bold
                        //HSSFFont f = wb.createFont();
                        Font headerFont = wb.createFont();
                        headerFont.setBold(true);
                        cellStyle[i].setFont(headerFont);

                        //cellStyle.setFont(f);
                        // adapt this font to the cell
                        cell.setCellStyle(cellStyle[i]);

                        col++;
                    }

                    row++;
                }

                try {
                    r = sheet.createRow(row);
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error while creating row number " + row + " " + e.getMessage());

                    wbCounter++;
                    row = 0;

                    sheet = wb.createSheet("Data Export " + Integer.toString(wbCounter));
                    logger.log(Level.WARNING, "created a further page in the hope it helps...");

                    // create the head
                    cellStyle = new CellStyle[columnCount];
                    r = sheet.createRow(row);  // titlerow
                    for (int i = 0; i < head.size(); i++) {

                        // we create the cell
                        Cell cell = r.createCell(col);

                        // set the value of the cell
                        cell.setCellValue((String) head.get(i));

                        // then we align center
                        cellStyle[i] = wb.createCellStyle();
                        cellStyle[i].setAlignment(HorizontalAlignment.CENTER);

                        // now we make it bold
                        //HSSFFont f = wb.createFont();
                        Font headerFont = wb.createFont();
                        headerFont.setBold(true);
                        cellStyle[i].setFont(headerFont);

                        //cellStyle.setFont(f);
                        // adapt this font to the cell
                        cell.setCellStyle(cellStyle[i]);

                        col++;
                    }

                    row++;

                }

                col = 0; // put column counter back to 0 to start at the next row

                String previousMessage = "";
                String textContent = "";  // is used to not go over 32767 characters per cell 
                for (int i = 0; i < columnCount; i++) {
                    try {
                        // depending on the type, create the cell
                        switch (rsmd.getColumnType(i + 1)) {
                            case java.sql.Types.INTEGER:
                                r.createCell(col).setCellValue(resultSet.getInt(i + 1));
                                break;
                            case java.sql.Types.FLOAT:
                                r.createCell(col).setCellValue(resultSet.getFloat(i + 1));
                                break;
                            case java.sql.Types.DOUBLE:
                                r.createCell(col).setCellValue(resultSet.getDouble(i + 1));
                                break;
                            case java.sql.Types.DECIMAL:
                                r.createCell(col).setCellValue(resultSet.getFloat(i + 1));
                                break;
                            case java.sql.Types.NUMERIC:
                                r.createCell(col).setCellValue(resultSet.getFloat(i + 1));
                                break;
                            case java.sql.Types.BIGINT:
                                r.createCell(col).setCellValue(resultSet.getInt(i + 1));
                                break;
                            case java.sql.Types.TINYINT:
                                r.createCell(col).setCellValue(resultSet.getByte(i + 1));
                                break;
                            case java.sql.Types.SMALLINT:
                                r.createCell(col).setCellValue(resultSet.getInt(i + 1));
                                break;

                            case java.sql.Types.DATE:
                                // first we get the date
                                java.sql.Date dat = resultSet.getDate(i + 1);
                                java.util.Date date = null;
                                if (!resultSet.wasNull()) {
                                    date = new java.util.Date(dat.getTime());
                                }

                                Cell cell = r.createCell(col);
                                cell.setCellValue(date);
                                cell.setCellStyle(dateStyle);
                                break;

                            case java.sql.Types.TIMESTAMP:
                                // first we get the date
                                java.sql.Timestamp ts = resultSet.getTimestamp(i + 1);
                                if (resultSet.wasNull()) {
                                    ts = null;
                                }

                                Cell c = r.createCell(col);
                                try {
                                    c.setCellValue(ts);
                                    // r.createCell(col).setCellValue(ts);

                                    // Date Format
                                    if (cellStyle[i] == null) {
                                        cellStyle[i] = wb.createCellStyle();
                                        cellStyle[i].setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd hh:mm:ss"));
                                    }
                                    c.setCellStyle(cellStyle[i]);
                                } catch (Exception e) {
                                    c.setCellValue(" ");
                                }
                                break;

                            case java.sql.Types.TIME:
                                // first we get the date
                                java.sql.Time time = resultSet.getTime(i + 1);
                                if (resultSet.wasNull()) {
                                    time = null;
                                }

                                Cell timeCell = r.createCell(col);
                                timeCell.setCellValue(time);
                                timeCell.setCellStyle(timeStyle);
                                break;

                            case java.sql.Types.BIT:
                                boolean b1 = resultSet.getBoolean(i + 1);
                                r.createCell(col).setCellValue(b1);
                                break;
                            case java.sql.Types.BOOLEAN:
                                boolean b2 = resultSet.getBoolean(i + 1);
                                r.createCell(col).setCellValue(b2);
                                break;
                            case java.sql.Types.CHAR:
                                try {
                                    if (textContent != null) {
                                        textContent = removeHTMLtags(resultSet.getString(i + 1));
                                        r.createCell(col).setCellValue(textContent.length() < 32767 ? textContent : textContent.substring(1, 32760) + "...");
                                    }
                                } catch (Exception e) {
                                    r.createCell(col).setCellValue(" ");
                                }
                                break;

                            case java.sql.Types.NVARCHAR:
                                textContent = removeHTMLtags(resultSet.getString(i + 1));
                                try {
                                if (textContent != null) {
                                    r.createCell(col).setCellValue(textContent.length() < 32767 ? textContent : textContent.substring(1, 32760) + "...");
                                } else {
                                        r.createCell(col).setCellValue(" ");
                                    }
                                } catch (Exception e) {
                                    r.createCell(col).setCellValue(" ");
                                    logger.log(Level.WARNING, "Exception while writing column {0} row {3} type: {1} Message: {2}", new Object[]{col, rsmd.getColumnType(i + 1), e.getMessage(), row});
                                }
                                break;

                            case java.sql.Types.VARCHAR:
                                try {
                                    textContent = removeHTMLtags(resultSet.getString(i + 1));
                                    if (textContent != null) {
                                        r.createCell(col).setCellValue(textContent.length() < 32767 ? textContent : textContent.substring(1, 32760) + "...");
                                    } else {
                                        r.createCell(col).setCellValue(" ");
                                    }
                                } catch (Exception e) {
                                    r.createCell(col).setCellValue(" ");
                                    logger.log(Level.WARNING, "Exception while writing column {0} row {3} type: {1} Message: {2}", new Object[]{col, rsmd.getColumnType(i + 1), e.getMessage(), row});
                                }
                                break;

                            default:
                                try {
                                    if (textContent != null) {
                                        textContent = removeHTMLtags(resultSet.getString(i + 1));
                                        r.createCell(col).setCellValue(textContent.length() < 32767 ? textContent : textContent.substring(1, 32760) + "...");
                                    } else {
                                        r.createCell(col).setCellValue(" ");
                                    }
                                } catch (Exception e) {
                                    r.createCell(col).setCellValue(" ");
                                }
                                break;
                        }
                    } catch (SQLException e) {
                        //e.printStackTrace();
                        if (resultSet.wasNull()) {
                            r.createCell(col).setCellValue(" ");
                        } else {
                            logger.log(Level.WARNING, "Unhandled type at column {0}, row {3} type: {1}. Filling up with blank {2}", new Object[]{col, rsmd.getColumnType(i + 1), e.getMessage(), row});
                            r.createCell(col).setCellValue(" ");
                        }
                    }
                    col++;
                }
                if (row % 5000 == 0) {
                    logger.log(Level.INFO, "wrote {0} rows into Excel, still processing...", String.format("%,d", row));
                }
            }
            //pstmt.close();
        } catch (SQLException sqle3) {
            setException(sqle3);
            logger.log(Level.WARNING, "Exception while writing data into sheet. Message: {0}", sqle3.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception while close {0}", e.getMessage());
                }
            }
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while close {0}", e.getMessage());
            }

            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception while close {0}", e.getMessage());
                }
            }
        }

        return row;

    }

    private String removeHTMLtags(String s) {
        if (isRemoveHTML()) {
            s = s.replaceAll("#x20;", " ");
            s = s.replaceAll("&#x0D;", System.lineSeparator());
            s = s.replaceAll("&lt;", "<");
            s = s.replaceAll("&gt;", ">");
            s = Jsoup.parse(s).text();
        }
        return s;
    }

    public XLSExport() {
        logger = Logger.getLogger("sql.fredy.sqltools");

    }

    public static void main(String args[]) {
        String query = null;
        String file = null;

        System.out.println("XLSExport\n"
                + "----------\n"
                + "Syntax: java sql.fredy.sqltools.XLSExport\n"
                + "        Parameters: -q Query\n"
                + "                    -Q Filename of the file containing the Query\n"
                + "                    -f File to write into (.xls or xlsx)\n");

        int i = 0;
        while (i < args.length) {

            if (args[i].equals("-q")) {
                i++;
                query = args[i];
            }

            if (args[i].equals("-Q")) {
                i++;
                sql.fredy.io.ReadFile rf = new sql.fredy.io.ReadFile(args[i]);
                query = rf.getText();
            }

            if (args[i].equals("-f")) {
                i++;
                file = args[i];
            }
            i++;

        };

        XLSExport xe = new XLSExport();
        xe.setQuery(query);
        xe.createXLS(file);
    }

    /**
     * @return the xlsx
     */
    public boolean isXlsx() {
        return xlsx;
    }

    /**
     * @param xlsx the xlsx to set
     */
    public void setXlsx(boolean xlsx) {
        this.xlsx = xlsx;
    }

    /**
     * @return the pstmt
     */
    public PreparedStatement getPstmt() {
        return pstmt;
    }

    /**
     * @param pstmt the pstmt to set
     */
    public void setPstmt(PreparedStatement pstmt) {
        this.pstmt = pstmt;
    }

    /**
     * @return the removeHTML
     */
    public boolean isRemoveHTML() {
        return removeHTML;
    }

    /**
     * @param removeHTML the removeHTML to set
     */
    public void setRemoveHTML(boolean removeHTML) {
        this.removeHTML = removeHTML;
    }
}
