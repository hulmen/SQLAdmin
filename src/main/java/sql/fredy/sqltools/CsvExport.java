/*
 * CsvExport is exporting a query into a file
 * it is using apache.commons.csv
 *
 * Created by Fredy, 2024-03-24 as part of SqlAdmin
 *
 * as of apache.commons csv please check https://commons.apache.org/proper/commons-csv/user-guide.html
 *
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import sql.fredy.connection.DataSource;
import sql.fredy.infodb.DBconnectionPool;

/**
 *
 * @author fredy
 */
public class CsvExport implements Runnable {

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
     * @return the exportStyle
     */
    public String getExportStyle() {
        return exportStyle;
    }

    /**
     * @param exportStyle the exportStyle to set
     */
    public void setExportStyle(String exportStyle) {
        this.exportStyle = exportStyle;
    }

    /**
     * @return the con
     */
    private Connection getCon() {

        logger.log(Level.FINE, "connection is {0}", con == null ? "null" : "established");

        try {
            if ((con == null) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
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

    private String fileName;
    private String exportStyle;
    private Connection con;
    private String query;

    private Logger logger = Logger.getLogger(getClass().getName());

    public CsvExport() {

    }

    public CsvExport(String style, String query, String filename) {

        setFileName(filename);
        setExportStyle(style);
        setQuery(query);
        setCon(con);

    }

    public void export(String style, String query, String filename) {

        setFileName(filename);
        setExportStyle(style);
        setQuery(query);
        setCon(con);

        ///exportQuery();
    }

    @Override
    public void run() {
        
        verifyFile();
        
        ResultSet rs = null;
        Statement stmt = null;
        BufferedWriter writer = null;
        try {

            stmt = getCon().createStatement();
            rs = stmt.executeQuery(getQuery());

            writer = new BufferedWriter(new FileWriter(getFileName()));

            CSVPrinter printer = null;
            printer = switch (getExportStyle().toUpperCase()) {
                case "DEFAULT" ->
                    CSVFormat.DEFAULT.withHeader(rs).print(writer);
                case "EXCEL" ->
                    CSVFormat.EXCEL.withHeader(rs).print(writer);
                case "INFORMIX" ->
                    CSVFormat.INFORMIX_UNLOAD.withHeader(rs).print(writer);
                case "INFORMIX_CSV" ->
                    CSVFormat.INFORMIX_UNLOAD_CSV.withHeader(rs).print(writer);
                case "MONGO_CSV" ->
                    CSVFormat.MONGODB_CSV.withHeader(rs).print(writer);
                case "MONGO_TSV" ->
                    CSVFormat.MONGODB_TSV.withHeader(rs).print(writer);
                case "MYSQL" ->
                    CSVFormat.MYSQL.withHeader(rs).print(writer);
                case "ORACLE" ->
                    CSVFormat.ORACLE.withHeader(rs).print(writer);
                case "POSTGRESSQL_CSV" ->
                    CSVFormat.POSTGRESQL_CSV.withHeader(rs).print(writer);
                case "POSTGRESSQL_TEXT" ->
                    CSVFormat.POSTGRESQL_TEXT.withHeader(rs).print(writer);
                case "RFC4180" ->
                    CSVFormat.RFC4180.withHeader(rs).print(writer);
                case "TDF" ->
                    CSVFormat.TDF.withHeader(rs).print(writer);
                default ->
                    CSVFormat.DEFAULT.withHeader(rs).print(writer);
            };

            printer.printRecords(rs);

        } catch (IOException iox) {
            logger.log(Level.SEVERE, "IO Exception :{0}", iox.getMessage());
        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, "SQL Exception :{0}", sqlex.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.SEVERE, "SQL Exception while closing ResultSet :{0}", sqlex.getMessage());
                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.SEVERE, "SQL Exception while closing Statement :{0}", sqlex.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.SEVERE, "SQL Exception while closing Connection :{0}", sqlex.getMessage());
                }
            }
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                    logger.log(Level.INFO, "Created file {0}", getFileName());
                } catch (IOException iox) {
                    logger.log(Level.SEVERE, "IO Exception while closing target file :{0}", iox.getMessage());
                }
            }

        }
    }

    /*
    We verify, if this file already exists,
    if yes, we number up the existing file not to overwrite it
     */
    private void verifyFile() {
        File file = new File(getFileName());

        if (file.exists()) {
            int i = 0;
            boolean v = false;
            while (!v) {
                i++;
                File f = new File(file.getParent() + File.separator  + file.getName() + "." + Integer.toString(i));
                if (!f.exists()) {
                    v = file.renameTo(f);
                    logger.log(Level.INFO, "Existing file renamed to {0}", f);
                }
            }
        }

    }

}
