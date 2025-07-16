/*
 * This class is preparing the type mapping for different Databases to JDBC.
 * It is taking the values from the internal Table APP.JDBCMAPPING
 * and prepares a HashMap<String,JdbcTypeMapping> whereby the key is the JDBC-Type

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
package sql.fredy.sqltools;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import sql.fredy.infodb.DBconnectionPool;
import sql.fredy.tools.JdbcTypeMapping;

/**
 *
 * @author fredy
 */
public class TypeMapping {

    private HashMap<String, JdbcTypeMapping> mapping;
    private Logger logger = Logger.getLogger("sql.fredy.sqltools");
    private String databaseProduct;

    public TypeMapping(String databaseproduct) {

        mapping = new HashMap<>();

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = DBconnectionPool.getInstance().getConnection();

            // we read the content;
            ps = connection.prepareStatement("""
                                             SELECT
                                             ID,
                                             PRODUCTNAME,
                                             JDBCTYPENAME,
                                             PRODUCTTYPENAME,                                             
                                             HASLENGTH,
                                             HASPRECISION,
                                             MAXLENGTH,
                                             MAXLENGTHTEXT
                                             FROM APP.JDBCMAPPING WHERE LOWER(PRODUCTNAME) = ?
                                             """);
            ps.setString(1, databaseproduct.toLowerCase());
            rs = ps.executeQuery();
            while (rs.next()) {
                JdbcTypeMapping jtm = new JdbcTypeMapping();
                jtm.setProductName(rs.getString("PRODUCTNAME"));
                jtm.setJdbcTypeName(rs.getString("JDBCTYPENAME"));
                jtm.setDbProductTypeName(rs.getString("PRODUCTTYPENAME"));
                jtm.setHasLength(rs.getBoolean("HASLENGTH"));
                jtm.setHasPrecision(rs.getBoolean("HASPRECISION"));
                jtm.setMaxLength(rs.getInt("MAXLENGTH"));
                jtm.setBiggerMaxLengthText(rs.getString("MAXLENGTHTEXT"));
                mapping.put((String) jtm.getJdbcTypeName(), jtm);
            }
        } catch (SQLException | IOException | PropertyVetoException e) {
            logger.log(Level.SEVERE, "Exception while checking JDBC Mapping table {0}", e.getMessage());
        } finally {
            if (rs != null) 
              try {
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exceptin while closing Derby Object");
            }
            if (ps != null) 
              try {
                ps.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exceptin while closing Derby Object");
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing Derby Object");
            }
        }

        // now we check, if  all fields are here, if not, we add them here
        String[] jdbcTypes = {
            "ARRAY",
            "BIGINT",
            "BINARY",
            "BIT",
            "BLOB",
            "BOOLEAN",
            "CHAR",
            "CLOB",
            "DATALINK",
            "DATE",
            "DECIMAL",
            "DISTINCT",
            "DOUBLE",
            "FLOAT",
            "INTEGER",
            "JAVA_OBJECT",
            "LONGNVARCHAR",
            "LONGVARBINARY",
            "LONGVARCHAR",
            "NCHAR",
            "NCLOB",
            "NULL",
            "NUMERIC",
            "NVARCHAR",
            "OTHER",
            "REAL",
            "REF",
            "REF_CURSOR",
            "ROWID",
            "SMALLINT",
            "SQLXML",
            "STRUCT",
            "TIME",
            "TIMESTAMP",
            "TIMESTAMP WITH TIMEZONE",
            "TIME WITH TIMEZONME",
            "TINYINT",
            "VARBINARY",
            "VARCHAR"
        };
        for ( String k : jdbcTypes) {
            if ( ! mapping.containsKey((String) k.toUpperCase())) { 
                mapping.put((String)k, fill(k));
            }
        }      
    }

    private JdbcTypeMapping fill(String key) {
        JdbcTypeMapping jtm = new JdbcTypeMapping();

        switch (key) {
            case "ARRAY":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.ARRAY);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "BIGINT":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.BIGINT);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "BINARY":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.BINARY);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "BIT":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.BIT);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "BLOB":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.BLOB);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "BOOLEAN":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.BOOLEAN);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "CHAR":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.CHAR);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "CLOB":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.CLOB);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "DATALINK":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.DATALINK);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "DATE":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.DATE);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "DECIMAL":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.DECIMAL);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(true);
                jtm.setMaxLength(0);
                break;
            case "DISTINCT":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.DISTINCT);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "DOUBLE":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.DOUBLE);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "FLOAT":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.FLOAT);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "INTEGER":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.INTEGER);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "JAVA_OBJECT":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.JAVA_OBJECT);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "LONGNVARCHAR":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.LONGNVARCHAR);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "LONGVARBINARY":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.LONGVARBINARY);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                break;
            case "LONGVARCHAR":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.LONGVARCHAR);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(true);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(65635);
                jtm.setBiggerMaxLengthText("max");
                break;
            case "NCHAR":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.NCHAR);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(1000);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "NCLOB":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.NCLOB);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(65635);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "NULL":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.NULL);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "NUMERIC":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.NUMERIC);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(true);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "NVARCHAR":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.NVARCHAR);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(4000);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "OTHER":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.OTHER);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "REAL":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.REAL);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "REF":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.REF);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "REF_CURSOR":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.REF_CURSOR);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "ROWID":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.ROWID);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "SMALLINT":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.SMALLINT);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "SQLXML":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.SQLXML);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "STRUCT":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.STRUCT);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "TIME":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.TIME);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "TIMESTAMP":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.TIMESTAMP);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "TIMESTAMP WITH TIMEZONE":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "TIME WITH TIMEZONE":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.TIMESTAMP_WITH_TIMEZONE);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "TINYINT":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.TINYINT);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(false);
                jtm.setHasMaxLengthText(false);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "VARBINARY":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.VARBINARY);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(true);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(0);
                jtm.setBiggerMaxLengthText(null);
                break;
            case "VARCHAR":
                jtm.setProductName(getDatabaseProduct());
                jtm.setJdbcTypeNumber(java.sql.Types.VARCHAR);
                jtm.setJdbcTypeName(key);
                jtm.setDbProductTypeName(key);
                jtm.setHasLength(true);
                jtm.setHasMaxLengthText(true);
                jtm.setHasPrecision(false);
                jtm.setMaxLength(4000);
                jtm.setBiggerMaxLengthText("max");
                break;
            default:
                break;
        }
        return jtm;
    }

    /**
     * @return the mapping
     */
    public HashMap<String, JdbcTypeMapping> getMapping() {
        return mapping;
    }

    /**
     * @param mapping the mapping to set
     */
    public void setMapping(HashMap<String, JdbcTypeMapping> mapping) {
        this.mapping = mapping;
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
  
    
}
