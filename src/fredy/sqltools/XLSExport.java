package sql.fredy.sqltools;

/**
   XLSExport exports the result of a query into a XLS-file. To do this it is
   using HSSF from the Apache POI Project: http://jakarta.apache.org/poi
 
   Version 1.0 Date 7. aug. 2003 Author Fredy Fischer
 
   XLSExport is part of the Admin-Suite  
 
   Once instantiated there are the following steps to go to get a XLS-file out
   of a query
 
   XLSExport xe = new XLSExport(java.sql.Connection con)
   xe.setQuery(java.lang.String query) please set herewith the the query to get
   its results as XLS-file
 
   int xe.createXLS(java.lang.String fileName) this will then create the
   XLS-File. If this file already exists, it will be overwritten!
 
   it returns the number of rows written to the File
  
   2015-11-16 Creating an additional Worksheet containing the SQL-Query
 
 
   Admin is a Tool around SQL to do basic jobs for DB-Administrations, like: -
   create/ drop tables - create indices - perform sql-statements - simple form -
   a guided query and a other usefull things in DB-arena
 
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
import sql.fredy.share.t_connect;
import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.sql.*;
import java.util.logging.*;
import java.util.Date;

import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.hssf.record.*;
import org.apache.poi.hssf.model.*;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.*;

import java.util.ArrayList;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class XLSExport {

    private Logger logger;

    Connection con = null;

    /**
     * Get the value of con.
     *
     * @return value of con.
     */
    public Connection getCon() {
        return con;
    }

    /**
     * Set the value of con.
     *
     * @param v Value to assign to con.
     */
    public void setCon(Connection v) {
        this.con = v;
    }

    String query=null;

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
    private boolean xlsx = false;

    private void checkXlsx(String fileName) {
        String[] extension = fileName.split("\\.");
        int l = extension.length - 1;
        String fileType = extension[l].toLowerCase();
        if ("xlsx".equals(fileType)) {
            setXlsx(true);
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

            logger.log(Level.FINE, "Prefix: " + prefix + " Postfix: " + postfix);

            if ((postfix.equalsIgnoreCase("xlsx")) || (postfix.equalsIgnoreCase("xls"))) {
                // nothing to do                
            } else {
                postfix = "xlsx";
            }
            fixed = prefix + "." + postfix;
        }

        logger.log(Level.INFO, "Filename: " + fixed);
        return fixed;
    }

    /**
     * Create the XLS-File named fileName
     *
     * @param fileName is the Name (incl. Path) of the XLS-file to create
     *
     *
     */
    public int createXLS(String fileName) {

        // I need to have a query to process
        if ((getQuery() == null) && (getPstmt() == null)) {
            logger.log(Level.WARNING, "Need to have a query to process");
            return 0;
        }

        // I also need to have a file to write into
        if (fileName == null) {
            logger.log(Level.WARNING, "Need to know where to write into");
            return 0;
        }
        fileName = fixFileName(fileName);
        checkXlsx(fileName);

        // I need to have a connection to the RDBMS
        if (getCon() == null) {
            logger.log(Level.WARNING, "Need to have a connection to process");
            return 0;
        }

        //Statement stmt = null;
        ResultSet resultSet = null;
        ResultSetMetaData rsmd = null;
        try {

            // first we have to create the Statement
            if (getPstmt() == null) {               
                pstmt = getCon().prepareStatement(getQuery());
            }

            //stmt = getCon().createStatement();
        } catch (SQLException sqle1) {
            setException(sqle1);
            logger.log(Level.WARNING, "Can not create Statement. Message: "
                    + sqle1.getMessage().toString());
            return 0;
        }

        logger.log(Level.FINE, "FileName: " + fileName);
        logger.log(Level.FINE, "Query   : " + getQuery());

        logger.log(Level.FINE, "Starting export...");

        // create an empty sheet
        Workbook wb;
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
        sheet = wb.createSheet("Data Export");

        // create a second sheet just containing the SQL Statement
        sqlsheet = wb.createSheet("SQL Statement");
        Row sqlrow = sqlsheet.createRow(0);
        Cell sqltext = sqlrow.createCell(0);
        try {
           if ( getQuery() != null ) {
               sqltext.setCellValue(getQuery());
           } else {
               sqltext.setCellValue(pstmt.toString());
           }
        } catch (Exception lex) {
            
        }
         CellStyle style = wb.createCellStyle();
        style.setWrapText(true);

        sqltext.setCellStyle(style);

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
            logger.log(Level.WARNING, "Can not execute query. Message: "
                    + sqle2.getMessage().toString());
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

            for (int i = 0; i < columnCount; i++) {

                // we create the cell
                Cell cell = r.createCell(col);

                // set the value of the cell
                cell.setCellValue(rsmd.getColumnName(i + 1));
                head.add(rsmd.getColumnName(i + 1));

                // then we align center
                CellStyle cellStyle = wb.createCellStyle();
                cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

                // now we make it bold
                //HSSFFont f = wb.createFont();
                Font headerFont = wb.createFont();
                headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
                cellStyle.setFont(headerFont);

                //cellStyle.setFont(f);
                // adapt this font to the cell
                cell.setCellStyle(cellStyle);

                col++;
            }
        } catch (SQLException sqle3) {
            setException(sqle3);
            logger.log(Level.WARNING, "Can not create XLS-Header. Message: "
                    + sqle3.getMessage().toString());
            return 0;
        }

        // looping the resultSet
        int wbCounter = 0;
        try {
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
                        CellStyle cellStyle = wb.createCellStyle();
                        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

                        // now we make it bold
                        //HSSFFont f = wb.createFont();
                        Font headerFont = wb.createFont();
                        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
                        cellStyle.setFont(headerFont);

                        //cellStyle.setFont(f);
                        // adapt this font to the cell
                        cell.setCellStyle(cellStyle);

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
                    r = sheet.createRow(row);  // titlerow
                    for (int i = 0; i < head.size(); i++) {

                        // we create the cell
                        Cell cell = r.createCell(col);

                        // set the value of the cell
                        cell.setCellValue((String) head.get(i));

                        // then we align center
                        CellStyle cellStyle = wb.createCellStyle();
                        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

                        // now we make it bold
                        //HSSFFont f = wb.createFont();
                        Font headerFont = wb.createFont();
                        headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
                        cellStyle.setFont(headerFont);

                        //cellStyle.setFont(f);
                        // adapt this font to the cell
                        cell.setCellStyle(cellStyle);

                        col++;
                    }

                    row++;

                }

                col = 0; // put column counter back to 0 to start at the next row
                String previousMessage = "";
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
                                r.createCell(col).setCellValue(resultSet.getInt(i + 1));
                                break;
                            case java.sql.Types.SMALLINT:
                                r.createCell(col).setCellValue(resultSet.getInt(i + 1));
                                break;

                            case java.sql.Types.DATE:
                                // first we get the date
                                java.sql.Date dat = resultSet.getDate(i + 1);
                                java.util.Date date = new java.util.Date(dat.getTime());
                                r.createCell(col).setCellValue(date);
                                break;

                            case java.sql.Types.TIMESTAMP:
                                // first we get the date
                                java.sql.Timestamp ts = resultSet.getTimestamp(i + 1);

                                Cell c = r.createCell(col);
                                try {
                                    c.setCellValue(ts);
                                    // r.createCell(col).setCellValue(ts);

                                    // Date Format
                                    CellStyle cellStyle = wb.createCellStyle();
                                    cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd hh:mm:ss"));
                                    c.setCellStyle(cellStyle);
                                } catch (Exception e) {
                                    c.setCellValue(" ");
                                }
                                break;

                            case java.sql.Types.TIME:
                                // first we get the date
                                java.sql.Time time = resultSet.getTime(i + 1);
                                r.createCell(col).setCellValue(time);
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
                                r.createCell(col).setCellValue(resultSet.getString(i + 1));
                                break;
                            case java.sql.Types.NVARCHAR:
                                r.createCell(col).setCellValue(resultSet.getString(i + 1));
                                break;

                            case java.sql.Types.VARCHAR:
                                try {
                                    r.createCell(col).setCellValue(resultSet.getString(i + 1));
                                } catch (Exception e) {
                                    r.createCell(col).setCellValue(" ");
                                    logger.log(Level.WARNING, "Exception while writing column {0} row {3} type: {1} Message: {2}", new Object[]{col, rsmd.getColumnType(i + 1), e.getMessage(), row});
                                }
                                break;
                            default:
                                r.createCell(col).setCellValue(resultSet.getString(i + 1));
                                break;
                        }
                    } catch (Exception e) {
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
            }
            //pstmt.close();
        } catch (SQLException sqle3) {
            setException(sqle3);
            logger.log(Level.WARNING, "Exception while writing data into sheet. Message: "
                    + sqle3.getMessage().toString());
        }

        try {

            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            fileOut.close();

            logger.log(Level.INFO, "File created");
            logger.log(Level.INFO, "Wrote: {0} lines into XLS-File", Integer.toString(row));

        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while writing xls-File: " + e.getMessage().toString());
        }
        return row;

    }

    public XLSExport(Connection con) {
        logger = Logger.getLogger("sql.fredy.sqltools");
        setCon(con);
    }

    public XLSExport() {
        logger = Logger.getLogger("sql.fredy.sqltools");

    }

    public static void main(String args[]) {

        String host = "localhost";
        String user = System.getProperty("user.name");
        String schema = "%";
        String database = null;
        String password = null;
        String query = null;
        String file = null;

        System.out.println("XLSExport\n"
                + "----------\n"
                + "Syntax: java sql.fredy.sqltools.XLSExport\n"
                + "        Parameters: -h Host (default: localhost)\n"
                + "                    -u User (default: "
                + System.getProperty("user.name") + ")\n"
                + "                    -p Password\n"
                + "                    -q Query\n"
                + "                    -Q Filename of the file containing the Query\n"
                + "                    -d database\n"
                + "                    -f File to write into (.xls or xlsx)\n");

        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-h")) {
                i++;
                host = args[i];
            }
            if (args[i].equals("-u")) {
                i++;
                user = args[i];
            }
            if (args[i].equals("-p")) {
                i++;
                password = args[i];
            }
            if (args[i].equals("-d")) {
                i++;
                database = args[i];
            }

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

        t_connect tc = new t_connect(host, user, password, database);
        XLSExport xe = new XLSExport(tc.con);
        xe.setQuery(query);
        xe.createXLS(file);
        tc.close();
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
}
