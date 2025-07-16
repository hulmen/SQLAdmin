/*
 * This class is reading the JDBC Type Mapping File from resources/config
 * it is creating the values from the file if they haven't been loading yet
 *
 * The MIT License
 *
 * Copyright 2025 fredy.
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
package sql.fredy.tools;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import sql.fredy.infodb.DBconnectionPool;

/**
 *
 * @author fredy
 */
public class ReadJdbcTypeMapping {

    private Logger logger = Logger.getLogger("sql.fredy.tools");
    private String database;

    public ReadJdbcTypeMapping(String database) {
        setDatabase(database);
        checkTable();
        hasContent();

    }

    private boolean hasContent() {
        boolean hascontent = false;

        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            stmt = connection.prepareStatement("""
                                               select 
                                                count(*)
                                               from APP.JDBCMAPPING where PRODUCTNAME = ?""");
            stmt.setString(1, getDatabase());
            rs = stmt.executeQuery();
            int t = 0;
            if (rs.next()) {
                t = rs.getInt(1);
            }
            if (t == 0) {

                logger.log(Level.INFO, "Loading JDBC Type Mapping for {0}", getDatabase());

                HashMap<String, JdbcTypeMapping> map = readProperties();

                if (!map.isEmpty()) {

                    stmt = connection.prepareStatement("""
                                                       INSERT INTO APP.JDBCMAPPING (
                                                        PRODUCTNAME,
                                                        JDBCTYPENAME,
                                                        PRODUCTTYPENAME,                                             
                                                        HASLENGTH,
                                                        HASPRECISION,
                                                        MAXLENGTH,
                                                        MAXLENGTHTEXT
                                                       ) values ( ?,?,?,?,?,?,? )
                                                       """);
                    for (Map.Entry entry : map.entrySet()) {
                        JdbcTypeMapping jt = (JdbcTypeMapping) entry.getValue();
                        stmt.setString(1, jt.getProductName());
                        stmt.setString(2, jt.getJdbcTypeName());
                        stmt.setString(3, jt.getDbProductTypeName());
                        stmt.setBoolean(4, jt.isHasLength());
                        stmt.setBoolean(5, jt.isHasPrecision());
                        stmt.setInt(6, jt.getMaxLength());
                        stmt.setString(7, jt.getBiggerMaxLengthText());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }
        } catch (SQLException | IOException | PropertyVetoException e) {
            logger.log(Level.SEVERE, "Exception while checking JDBC Mapping table {0}", e.getMessage());
        } finally {
            if (rs != null) 
              try {
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing Derby Object");
            }
            if (stmt != null) 
              try {
                stmt.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing Derby Object");
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing Derby Object");
            }
        }

        return hascontent;
    }

    private HashMap<String, JdbcTypeMapping> readProperties() {
        //List<Path> result;
        //String fileExtension = "props";
        Properties props = null;

        HashMap<String, JdbcTypeMapping> types = new HashMap<>();

        try {          

            URL url = this.getClass().getResource("/resources/config/" + getDatabase().toLowerCase() + ".props");
            InputStream input = url.openStream();
           
            if (input != null) {
                props = new Properties();
                props.load(input);

                // now, let us add all the values
                for (Entry<Object, Object> e : props.entrySet()) {
                    //System.out.println(e.getKey() + " = " + e.getValue());

                    JdbcTypeMapping jtm = new JdbcTypeMapping();

                    switch (e.getKey().toString().toUpperCase()) {
                        case "ARRAY":

                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.ARRAY);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "BIGINT":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.BIGINT);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "BINARY":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.BINARY);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "BIT":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.BIT);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "BLOB":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.BLOB);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "BOOLEAN":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.BOOLEAN);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "CHAR":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.CHAR);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "CLOB":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.CLOB);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "DATALINK":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.DATALINK);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "DATE":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.DATE);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "DECIMAL":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.DECIMAL);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(true);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "DISTINCT":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.DISTINCT);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "DOUBLE":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.DOUBLE);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "FLOAT":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.FLOAT);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "INTEGER":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.INTEGER);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "JAVA_OBJECT":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.JAVA_OBJECT);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "LONGNVARCHAR":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.LONGNVARCHAR);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "LONGVARBINARY":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.LONGVARBINARY);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "LONGVARCHAR":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.LONGVARCHAR);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(true);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(65635);
                            jtm.setBiggerMaxLengthText("max");
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "NCHAR":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.NCHAR);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(1000);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "NCLOB":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.NCLOB);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(65635);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "NULL":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.NULL);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "NUMERIC":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.NUMERIC);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(true);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "NVARCHAR":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.NVARCHAR);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(4000);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "OTHER":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.OTHER);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "REAL":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.REAL);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "REF":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.REF);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "REF_CURSOR":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.REF_CURSOR);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "ROWID":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.ROWID);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "SMALLINT":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.SMALLINT);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "SQLXML":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.SQLXML);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "STRUCT":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.STRUCT);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "TIME":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.TIME);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "TIMESTAMP":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.TIMESTAMP);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "TIMESTAMP WITH TIMEZONE":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "TIME WITH TIMEZONE":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "TINYINT":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.TINYINT);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(false);
                            jtm.setHasMaxLengthText(false);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "VARBINARY":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.VARBINARY);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(true);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(0);
                            jtm.setBiggerMaxLengthText(null);
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        case "VARCHAR":
                            jtm.setProductName(getDatabase());
                            jtm.setJdbcTypeNumber(java.sql.Types.VARCHAR);
                            jtm.setJdbcTypeName(e.getKey().toString().toUpperCase());
                            jtm.setDbProductTypeName(e.getValue().toString());
                            jtm.setHasLength(true);
                            jtm.setHasMaxLengthText(true);
                            jtm.setHasPrecision(false);
                            jtm.setMaxLength(8000);
                            jtm.setBiggerMaxLengthText("max");
                            types.put(e.getKey().toString().toUpperCase(), jtm);
                            break;
                        default:
                            break;
                    }
                }

            } else {
                logger.log(Level.INFO, "Configfile {0}.props not found. Using standard values.", getDatabase().toLowerCase());
            }
        } catch ( IOException ex) {
            logger.log(Level.WARNING, "Exception while reading config file for {0}", getDatabase());
        } finally {

        }
        return types;
    }

    private void checkTable() {
        Connection connection = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery("select count(*) from SYS.SYSTABLES  WHERE TABLENAME = 'JDBCMAPPING' AND TABLETYPE = 'T'");
            int t = 0;
            if (rs.next()) {
                t = rs.getInt(1);
            }
            if (t == 0) {
                stmt.executeUpdate("""
                                   create table APP.JDBCMAPPING (
                                   ID INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY(Start with 1, Increment by 1),
                                   PRODUCTNAME     varchar(128),
                                   JDBCTYPENAME    varchar(128),
                                   PRODUCTTYPENAME varchar(128),
                                   HASLENGTH       boolean,
                                   HASPRECISION    boolean,
                                   MAXLENGTH       int,
                                   MAXLENGTHTEXT   varchar(32)                                   
                                   )""");
            }
        } catch (SQLException | IOException | PropertyVetoException e) {
            logger.log(Level.SEVERE, "Exception while checking JDBC Mapping table {0}", e.getMessage());
        } finally {
            if (rs != null) 
              try {
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exceptin whil closing Derby Object");
            }
            if (stmt != null) 
              try {
                stmt.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exceptin whil closing Derby Object");
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exceptin whil closing Derby Object");
            }
        }

    }

    public static void main(String[] args) {
        ReadJdbcTypeMapping rtm = new ReadJdbcTypeMapping("oracle");
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }
}
