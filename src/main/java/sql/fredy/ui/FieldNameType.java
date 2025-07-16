/**
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
package sql.fredy.ui;

public class FieldNameType {

    private String name = "unknown";
    private String type = "unknown";
    private String table = "unknown";
    private String db = "unknown";
    private String schema = "unknown";
    private String vendorTypeName = "unknown";

    private String originalColumnName = "";

    private boolean autoIncrement = false;
    private boolean primaryKey = false;

    private int columnDataType;
    private int length = 0;
    private int scale = 0;
    private int maxLength = 0;
    private int precision = 0;
    private boolean signed = false;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name.toLowerCase();
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the db
     */
    public String getDb() {
        return db;
    }

    /**
     * @param db the db to set
     */
    public void setDb(String db) {
        this.db = db;
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the originalColumnName
     */
    public String getOriginalColumnName() {
        return originalColumnName;
    }

    /**
     * @param originalColumnName the originalColumnName to set
     */
    public void setOriginalColumnName(String originalColumnName) {
        this.originalColumnName = originalColumnName;
    }

    /**
     * @return the autoIncrement
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * @param autoIncrement the autoIncrement to set
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    /**
     * @return the primaryKey
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @return the columnDataType
     */
    public int getColumnDataType() {
        return columnDataType;
    }

    /**
     * @param columnDataType the columnDataType to set
     */
    public void setColumnDataType(int columnDataType) {
        this.columnDataType = columnDataType;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param lenth the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

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
     * @return the signed
     */
    public boolean isSigned() {
        return signed;
    }

    /**
     * @param signed the signed to set
     */
    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    /**
     * @return the vendorTypeName
     */
    public String getVendorTypeName() {
        return vendorTypeName;
    }

    /**
     * @param vendorTypeName the vendorTypeName to set
     */
    public void setVendorTypeName(String vendorTypeName) {
        this.vendorTypeName = vendorTypeName;
    }

    /**
     * @return the maxLength
     */
    public int getMaxLength() {

        // product dependent max length of VARCHAR/NVARCHAR/
        return maxLength;
    }

    /**
     * @param maxLength the maxLength to set
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * @param precision the precision to set
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String getJdbcTypeName() {
        switch (getColumnDataType()) {
            case java.sql.Types.ARRAY:
                return "ARRAY";
            case java.sql.Types.BIGINT:
                return "BIGINT";
            case java.sql.Types.BINARY:
                return "BINARY";
            case java.sql.Types.BIT:
                return "BIT";
            case java.sql.Types.BLOB:
                return "BLOB";
            case java.sql.Types.BOOLEAN:
                return "BOOLEAN";
            case java.sql.Types.CHAR:
                return "CHAR";
            case java.sql.Types.CLOB:
                return "CLOB";
            case java.sql.Types.DATALINK:
                return "DATALINK";
            case java.sql.Types.DATE:
                return "DATE";
            case java.sql.Types.DECIMAL:
                return "DECIMAL";
            case java.sql.Types.DISTINCT:
                return "DISTINCT";
            case java.sql.Types.DOUBLE:
                return "DOUBLE";
            case java.sql.Types.FLOAT:
                return "FLOAT";
            case java.sql.Types.INTEGER:
                return "INTEGER";
            case java.sql.Types.JAVA_OBJECT:
                return "JAVA_OBJECT";
            case java.sql.Types.LONGNVARCHAR:
                return "LONGNVARCHAR";
            case java.sql.Types.LONGVARBINARY:
                return "LONGVARBINARY";
            case java.sql.Types.LONGVARCHAR:
                return "LONGVARCHAR";
            case java.sql.Types.NCHAR:
                return "NCHAR";
            case java.sql.Types.NCLOB:
                return "NCLOB";
            case java.sql.Types.NULL:
                return "NULL";
            case java.sql.Types.NUMERIC:
                return "NUMERIC";
            case java.sql.Types.NVARCHAR:
                return "NVARCHAR";
            case java.sql.Types.OTHER:
                return "OTHER";
            case java.sql.Types.REAL:
                return "REAL";
            case java.sql.Types.REF:
                return "REF";
            case java.sql.Types.REF_CURSOR:
                return "REF_CURSOR";
            case java.sql.Types.ROWID:
                return "ROWID";
            case java.sql.Types.SMALLINT:
                return "SMALLINT";
            case java.sql.Types.SQLXML:
                return "SQLXML";
            case java.sql.Types.STRUCT:
                return "STRUCT";
            case java.sql.Types.TIME:
                return "TIME";
            case java.sql.Types.TIMESTAMP:
                return "TIMESTAMP";
            case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
                return "TIMESTAMP_WITH_TIMEZONE";
            case java.sql.Types.TIME_WITH_TIMEZONE:
                return "TIME_WITH_TIMEZONE";
            case java.sql.Types.TINYINT:
                return "TINYINT";
            case java.sql.Types.VARBINARY:
                return "VARBINARY";
            case java.sql.Types.VARCHAR:
                return "VARCHAR";
            default:
                return "unknown";
        }
    }

}
