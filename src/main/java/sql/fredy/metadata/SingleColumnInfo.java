package sql.fredy.metadata;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.*;

/**
 * SingleColumnInfo contains info about a column in a DB-Table delivered by
 * MetaData
 *
 * 1.TABLE_CAT String => table catalog (may be null) 2.TABLE_SCHEM String =>
 * table schema (may be null) 3.TABLE_NAME String => table name 4.COLUMN_NAME
 * String => column name 5.DATA_TYPE short => SQL type from java.sql.Types
 * 6.TYPE_NAME String => Data source dependent type name, for a UDT the type
 * name is fully qualified 7.COLUMN_SIZE int => column size. For char or date
 * types this is the maximum number of characters, for numeric or decimal types
 * this is precision. 8.BUFFER_LENGTH is not used. 9.DECIMAL_DIGITS int => the
 * number of fractional digits 10.NUM_PREC_RADIX int => Radix (typically either
 * 10 or 2) 11.NULLABLE int => is NULL allowed? columnNoNulls - might not allow
 * NULL values columnNullable - definitely allows NULL values
 * columnNullableUnknown - nullability unknown 12.REMARKS String => comment
 * describing column (may be null) 13.COLUMN_DEF String => default value (may be
 * null) 14.SQL_DATA_TYPE int => unused 15.SQL_DATETIME_SUB int => unused
 * 16.CHAR_OCTET_LENGTH int => for char types the maximum number of bytes in the
 * column 17.ORDINAL_POSITION int => index of column in table (starting at 1)
 * 18.IS_NULLABLE String => "NO" means column definitely does not allow NULL
 * values; "YES" means the column might allow NULL values. An empty string means
 * nobody knows.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
 *
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
 *
 */
import java.util.Vector;
import java.util.logging.*;
import sql.fredy.connection.DataSource;

public class SingleColumnInfo {

    // Fredy's make Version
    private static String fredysVersion = "Version 1.4   2. Jan.2002";

    public String getVersion() {
        return fredysVersion;
    }

    private Logger logger = Logger.getLogger("sql.fredy.metadata");

    private String schema = null;

    public void setSchema(String v) {
        this.schema = v;
    }

    public String getSchema() {
        return this.schema;
    }

    String table_cat;

    /**
     * Get the value of table_cat.
     *
     * @return Value of table_cat.
     */
    public String getTable_cat() {
        return table_cat;
    }

    /**
     * Set the value of table_cat.
     *
     * @param v Value to assign to table_cat.
     */
    public void setTable_cat(String v) {
        this.table_cat = v;
    }

    String table_schem;

    /**
     * Get the value of table_schem.
     *
     * @return Value of table_schem.
     */
    public String getTable_schem() {
        return table_schem;
    }

    /**
     * Set the value of table_schem.
     *
     * @param v Value to assign to table_schem.
     */
    public void setTable_schem(String v) {
        this.table_schem = v;
    }

    String table_name;

    /**
     * Get the value of table_name.
     *
     * @return Value of table_name.
     */
    public String getTable_name() {
        return table_name;
    }

    /**
     * Set the value of table_name.
     *
     * @param v Value to assign to table_name.
     */
    public void setTable_name(String v) {
        this.table_name = v;
    }

    String column_name;

    /**
     * Get the value of column_name.
     *
     * @return Value of column_name.
     */
    public String getColumn_name() {
        return column_name;
    }

    /**
     * Set the value of column_name.
     *
     * @param v Value to assign to column_name.
     */
    public void setColumn_name(String v) {
        this.column_name = v;
    }

    short data_type;

    /**
     * Get the value of data_type.
     *
     * @return Value of data_type.
     */
    public short getData_type() {
        return data_type;
    }

    /**
     * Set the value of data_type.
     *
     * @param v Value to assign to data_type.
     */
    public void setData_type(short v) {
        this.data_type = v;
    }

    String type_name;

    /**
     * Get the value of type_name.
     *
     * @return Value of type_name.
     */
    public String getType_name() {

        return type_name;

    }

    /**
     * Set the value of type_name.
     *
     * @param v Value to assign to type_name.
     */
    public void setType_name(String v) {
        this.type_name = v;
    }

    int column_size;

    /**
     * Get the value of column_size.
     *
     * @return Value of column_size.
     */
    public int getColumn_size() {
        return column_size;
    }

    /**
     * Set the value of column_size.
     *
     * @param v Value to assign to column_size.
     */
    public void setColumn_size(int v) {
        this.column_size = v;
    }

    int buffer_length;

    /**
     * Get the value of buffer_length.
     *
     * @return Value of buffer_length.
     */
    public int getBuffer_length() {
        return buffer_length;
    }

    /**
     * Set the value of buffer_length.
     *
     * @param v Value to assign to buffer_length.
     */
    public void setBuffer_length(int v) {
        this.buffer_length = v;
    }

    int decimal_digits;

    /**
     * Get the value of decimal_digits.
     *
     * @return Value of decimal_digits.
     */
    public int getDecimal_digits() {
        return decimal_digits;
    }

    /**
     * Set the value of decimal_digits.
     *
     * @param v Value to assign to decimal_digits.
     */
    public void setDecimal_digits(int v) {
        this.decimal_digits = v;
    }

    int num_prec_radix;

    /**
     * Get the value of num_prec_radix.
     *
     * @return Value of num_prec_radix.
     */
    public int getNum_prec_radix() {
        return num_prec_radix;
    }

    /**
     * Set the value of num_prec_radix.
     *
     * @param v Value to assign to num_prec_radix.
     */
    public void setNum_prec_radix(int v) {
        this.num_prec_radix = v;
    }

    int nullable;

    /**
     * Get the value of nullable.
     *
     * @return Value of nullable.
     */
    public int getNullable() {
        return nullable;
    }

    public boolean isNullable() {
        if (getIs_nullable() != null) {
            if (getIs_nullable().equalsIgnoreCase("NO")) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * Set the value of nullable.
     *
     * @param v Value to assign to nullable.
     */
    public void setNullable(int v) {
        this.nullable = v;
    }

    String remarks;

    /**
     * Get the value of remarks.
     *
     * @return Value of remarks.
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Set the value of remarks.
     *
     * @param v Value to assign to remarks.
     */
    public void setRemarks(String v) {
        this.remarks = v;
    }

    String column_def;

    /**
     * Get the value of column_def.
     *
     * @return Value of column_def.
     */
    public String getColumn_def() {
        return column_def;
    }

    /**
     * Set the value of column_def.
     *
     * @param v Value to assign to column_def.
     */
    public void setColumn_def(String v) {
        this.column_def = v;
    }

    int sql_data_type;

    /**
     * Get the value of sql_data_type.
     *
     * @return Value of sql_data_type.
     */
    public int getSql_data_type() {
        return sql_data_type;
    }

    /**
     * Set the value of sql_data_type.
     *
     * @param v Value to assign to sql_data_type.
     */
    public void setSql_data_type(int v) {
        this.sql_data_type = v;
    }

    int sql_datetime_sub;

    /**
     * Get the value of sql_datetime_sub.
     *
     * @return Value of sql_datetime_sub.
     */
    public int getSql_datetime_sub() {
        return sql_datetime_sub;
    }

    /**
     * Set the value of sql_datetime_sub.
     *
     * @param v Value to assign to sql_datetime_sub.
     */
    public void setSql_datetime_sub(int v) {
        this.sql_datetime_sub = v;
    }

    int char_octet_length;

    /**
     * Get the value of char_octet_length.
     *
     * @return Value of char_octet_length.
     */
    public int getChar_octet_length() {
        return char_octet_length;
    }

    /**
     * Set the value of char_octet_length.
     *
     * @param v Value to assign to char_octet_length.
     */
    public void setChar_octet_length(int v) {
        this.char_octet_length = v;
    }

    int ordinal_position;

    /**
     * Get the value of ordinal_position.
     *
     * @return Value of ordinal_position.
     */
    public int getOrdinal_position() {
        return ordinal_position;
    }

    /**
     * Set the value of ordinal_position.
     *
     * @param v Value to assign to ordinal_position.
     */
    public void setOrdinal_position(int v) {
        this.ordinal_position = v;
    }

    String is_nullable;

    /**
     * Get the value of is_nullable.
     *
     * @return Value of is_nullable.
     */
    public String getIs_nullable() {
        return is_nullable;
    }

    /**
     * Set the value of is_nullable.
     *
     * @param v Value to assign to is_nullable.
     */
    public void setIs_nullable(String v) {
        this.is_nullable = v;
    }

    java.util.Vector dataVector;

    /**
     * Get the value of dataVector.
     *
     * @return Value of dataVector.
     */
    public Vector getDataVector() {
        return dataVector;
    }

    /**
     * Set the value of dataVector.
     *
     * @param v Value to assign to dataVector.
     */
    public void setDataVector(Vector v) {
        this.dataVector = v;
    }
    boolean autoIncrement = false;
    boolean primaryKey = false;

    public void setPrimaryKey() {

        Connection con = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            DatabaseMetaData dmd = con.getMetaData();
            rs = dmd.getPrimaryKeys(null, null, getTable_name());
            ResultSetMetaData rsmd = rs.getMetaData();

            while (rs.next()) {
                if (rs.getString(4).equalsIgnoreCase(getColumn_name())) {
                    primaryKey = true;
                }
            }

        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "Exception while reading PrimaryKey from MetaData {0}", sqe.getMessage());
            /*
            System.out.println("\nError while reading PrimaryKey from MetaData\n" +
            "Exception: "+ sqe.getMessage() +
            "\nError-Code: " + sqe.getErrorCode() +
            "\nSQL-State: " + sqe.getSQLState());
             */
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on closing resultset {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on closing connection {0}", e.getMessage());
                }
            }
        }

    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setData() {

        setTable_cat((String) dataVector.elementAt(0));
        setTable_schem((String) dataVector.elementAt(1));
        setTable_name((String) dataVector.elementAt(2));
        setColumn_name((String) dataVector.elementAt(3));
        setData_type(((Short) dataVector.elementAt(4)).shortValue());
        setType_name((String) dataVector.elementAt(5));
        setColumn_size(((Integer) dataVector.elementAt(6)).intValue());
        //setBuffer_length( ((Integer) dataVector.elementAt(7)).intValue()); //not used
        setDecimal_digits(((Integer) dataVector.elementAt(8)).intValue());
        setNum_prec_radix(((Integer) dataVector.elementAt(9)).intValue());
        setNullable(((Integer) dataVector.elementAt(10)).intValue());

        setRemarks((String) dataVector.elementAt(11));
        setColumn_def((String) dataVector.elementAt(12));
        //setSql_data_type(  ((Integer) dataVector.elementAt(13)).intValue()); // not used
        //setSql_datetime_sub(((Integer) dataVector.elementAt(14)).intValue()) ; // not used
        setChar_octet_length(((Integer) dataVector.elementAt(15)).intValue());
        setOrdinal_position(((Integer) dataVector.elementAt(16)).intValue());
        setIs_nullable((String) dataVector.elementAt(17));

    }

    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    public Connection getConnection() {

        Connection con = null;
        try {
            con = DataSource.getInstance().getConnection();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {
            return con;
        }

    }

    public void setValues(String db, String schema, String table, String column) {
        
        //logger.log(Level.INFO,"Setting values DB= {0} , Schema = {1} , Table = {2} , Column = {3}", new Object[]{db,schema,table,column});
                
        setTable_name(table);
        setTable_schem(schema);
        setColumn_name(column);
        
        setSchema(schema);
        int position = 0;
        String type;
        Vector v = new Vector();
        Connection con = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            DatabaseMetaData dmd = con.getMetaData();
            rs = dmd.getColumns(db, schema, table, column);
            while (rs.next()) {
                position = 1;
                type = "String";
                v = processVector(rs, v, type, position);
                position = 2;
                type = "String";
                v = processVector(rs, v, type, position);
                position = 3;
                type = "String";
                v = processVector(rs, v, type, position);
                position = 4;
                type = "String";
                v = processVector(rs, v, type, position);
                position = 5;
                type = "Short";
                v = processVector(rs, v, type, position);
                position = 6;
                type = "String";
                v = processVector(rs, v, type, position);
                position = 7;
                type = "Integer";
                v = processVector(rs, v, type, position);
                position = 8;
                type = "Integer";
                v = processVector(rs, v, type, position);
                position = 9;
                type = "Integer";
                v = processVector(rs, v, type, position);
                position = 10;
                type = "Integer";
                v = processVector(rs, v, type, position);
                position = 11;
                type = "Integer";
                v = processVector(rs, v, type, position);
                position = 12;
                type = "String";
                v = processVector(rs, v, type, position);
                position = 13;
                type = "String";
                v = processVector(rs, v, type, position);
                position = 14;
                type = "Integer";
                v = processVector(rs, v, type, position);
                position = 15;
                type = "Integer";
                v = processVector(rs, v, type, position);
                position = 16;
                type = "Integer";
                v = processVector(rs, v, type, position);
                position = 17;
                type = "Integer";
                v = processVector(rs, v, type, position);
                position = 18;
                type = "String";
                v = processVector(rs, v, type, position);
                setDataVector(v);
                setData();
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "Exception whil reading columns from MetaData {0} at position {1}", new Object[]{sqe.getMessage(), position});
            /*
            System.out.println("Error while reading Columns from MetaData (Position: " + position );
            System.out.println("Exception: "+ sqe.getMessage());
            System.out.println("Error-Code: " + sqe.getErrorCode());
            System.out.println("SQL-State: " + sqe.getSQLState() );
             */
        } finally {
            if ( rs != null ) {
                try {
                    rs.close();
                } catch (SQLException e ) {
                    logger.log(Level.WARNING,"Exception while closing ResultSet {0} ", e.getMessage());
                }
            }
            if ( con != null ) {
                try {
                    con.close();
                } catch (SQLException e ) {
                    logger.log(Level.WARNING,"Exception while closing Connection {0} ", e.getMessage());
                }
            }
        }

        setPrimaryKey();
        setForeignKeys(table, column);
    }

    String importedKeys = null;
    String exportedKeys = null;

    public String getImportedKeys() {
        return importedKeys;
    }

    public String getExportedKeys() {
        return exportedKeys;
    }

    private void setForeignKeys(String table, String column) {
        try {
            ForeignKeys fk = new ForeignKeys();
            fk.setSchema(getSchema());
            importedKeys = fk.getImportedKeys(table, column);
            exportedKeys = fk.getExportedKeys(table, column);
        } catch (java.lang.AbstractMethodError ame) {
            logger.log(Level.INFO, "JDBC-Driver does not support DatabaseMetaData.getConnection()");
        } catch (Exception anotherException) {
            logger.log(Level.WARNING, "Exception: " + anotherException.getMessage() + " probably driver does not support function");
        }
    }

    private Vector processVector(ResultSet rs, Vector v, String type, int position) {
        try {
            if (type.equals("String")) {
                v.addElement((String) rs.getString(position));
            }
            if (type.equals("Integer")) {
                v.addElement(rs.getInt(position));
            }
            if (type.equals("Short")) {
                v.addElement(rs.getShort(position));
            }
        } catch (SQLException e) {
            if (type.equals("String")) {
                v.addElement((String) " ");
            }
            if (type.equals("Integer")) {
                v.addElement(0);
            }
            if (type.equals("Short")) {
                v.addElement(Short.valueOf("0.0"));
            }
        }
        return v;
    }

    public SingleColumnInfo() {
        dataVector = new java.util.Vector();
    }

    /**
     * @param v Vector contains: String,
     * String,String,String,Short,String,Integer,Integer,Integer,Integer,Integer,
     * String,String,Integer,Integer,Integer,String
     *
     */
    public SingleColumnInfo(java.util.Vector v) {

        dataVector = new java.util.Vector();
        setDataVector(v);
        setData();

    }

    public SingleColumnInfo(String db, String schema, String table, String column) {
        setSchema(schema);
        setValues(db, schema, table, column);
    }

    public SingleColumnInfo(String db, String table, String column) {
        setValues(db, "%", table, column);
    }

}
