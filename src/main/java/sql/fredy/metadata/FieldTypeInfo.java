/*
 * FieldTypeInfo.java
 *
 * Created on January 25, 2004, 1:30 PM
 *
 * This software is part of the Admin-Framework and free software (MIT-License)
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 *
 *
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

package sql.fredy.metadata;

/**
 *
 * @author sql@hulmen.ch
 */
public class FieldTypeInfo {

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

    /**
     * @return the literal_prefix
     */
    public String getLiteral_prefix() {
        return literal_prefix;
    }

    /**
     * @param literal_prefix the literal_prefix to set
     */
    public void setLiteral_prefix(String literal_prefix) {
        this.literal_prefix = literal_prefix;
    }

    /**
     * @return the literal_suffix
     */
    public String getLiteral_suffix() {
        return literal_suffix;
    }

    /**
     * @param literal_suffix the literal_suffix to set
     */
    public void setLiteral_suffix(String literal_suffix) {
        this.literal_suffix = literal_suffix;
    }

    /**
     * @return the create_params
     */
    public String getCreate_params() {
        return create_params;
    }

    /**
     * @param create_params the create_params to set
     */
    public void setCreate_params(String create_params) {
        this.create_params = create_params;
    }

    /**
     * @return the nullable
     */
    public short getNullable() {
        return nullable;
    }

    /**
     * @param nullable the nullable to set
     */
    public void setNullable(short nullable) {
        this.nullable = nullable;
    }

    /**
     * @return the case_sensitive
     */
    public boolean isCase_sensitive() {
        return case_sensitive;
    }

    /**
     * @param case_sensitive the case_sensitive to set
     */
    public void setCase_sensitive(boolean case_sensitive) {
        this.case_sensitive = case_sensitive;
    }

    /**
     * @return the searchable
     */
    public short getSearchable() {
        return searchable;
    }

    /**
     * @param searchable the searchable to set
     */
    public void setSearchable(short searchable) {
        this.searchable = searchable;
    }

    /**
     * @return the unsigned_attribute
     */
    public boolean isUnsigned_attribute() {
        return unsigned_attribute;
    }

    /**
     * @param unsigned_attribute the unsigned_attribute to set
     */
    public void setUnsigned_attribute(boolean unsigned_attribute) {
        this.unsigned_attribute = unsigned_attribute;
    }

    /**
     * @return the fixed_rec_scale
     */
    public boolean isFixed_rec_scale() {
        return fixed_rec_scale;
    }

    /**
     * @param fixed_rec_scale the fixed_rec_scale to set
     */
    public void setFixed_rec_scale(boolean fixed_rec_scale) {
        this.fixed_rec_scale = fixed_rec_scale;
    }

    /**
     * @return the local_type_name
     */
    public String getLocal_type_name() {
        return local_type_name;
    }

    /**
     * @param local_type_name the local_type_name to set
     */
    public void setLocal_type_name(String local_type_name) {
        this.local_type_name = local_type_name;
    }

    /**
     * @return the minimum_scale
     */
    public short getMinimum_scale() {
        return minimum_scale;
    }

    /**
     * @param minimum_scale the minimum_scale to set
     */
    public void setMinimum_scale(short minimum_scale) {
        this.minimum_scale = minimum_scale;
    }

    /**
     * @return the sql_data_type
     */
    public int getSql_data_type() {
        return sql_data_type;
    }

    /**
     * @param sql_data_type the sql_data_type to set
     */
    public void setSql_data_type(int sql_data_type) {
        this.sql_data_type = sql_data_type;
    }

    /**
     * @return the sql_datetime_sub
     */
    public int getSql_datetime_sub() {
        return sql_datetime_sub;
    }

    /**
     * @param sql_datetime_sub the sql_datetime_sub to set
     */
    public void setSql_datetime_sub(int sql_datetime_sub) {
        this.sql_datetime_sub = sql_datetime_sub;
    }

    /**
     * @return the num_prec_radix
     */
    public int getNum_prec_radix() {
        return num_prec_radix;
    }

    /**
     * @param num_prec_radix the num_prec_radix to set
     */
    public void setNum_prec_radix(int num_prec_radix) {
        this.num_prec_radix = num_prec_radix;
    }
    
    private String typName = null;
    
    private boolean autoIncrement = false;
    
    /** Creates a new instance of FieldTypeInfo */
    public FieldTypeInfo() {
    }
    
    /** Getter for property autoIncrement.
     * @return Value of property autoIncrement.
     *
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }
    
    /** Setter for property autoIncrement.
     * @param autoIncrement New value of property autoIncrement.
     *
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }
    
    /** Getter for property typName.
     * @return Value of property typName.
     *
     */
    public java.lang.String getTypName() {
        return typName;
    }
    
    /** Setter for property typName.
     * @param typName New value of property typName.
     *
     */
    public void setTypName(java.lang.String typName) {
        this.typName = typName;
    }
    
    private int dataType;

    /**
     * @return the dataType
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }
    
     public String getJdbcTypeName() {
        String t ;
        switch ( getDataType()) {
            case java.sql.Types.ARRAY:
                t = "ARRAY";
                break;
            case java.sql.Types.BINARY:
                t = "BINARY";
                break;
            case java.sql.Types.BLOB:
                t = "BLOB";
                break;
            case java.sql.Types.CLOB:
                t = "CLOB";
                break;
            case java.sql.Types.INTEGER:
                t = "INTEGER";
                break;
            case java.sql.Types.FLOAT:
                t = "FLOAT";
                break;
            case java.sql.Types.DOUBLE:
                t = "DOUBLE";
                break;
            case java.sql.Types.DECIMAL:
                t = "DECIMAL";
                break;
            case java.sql.Types.NUMERIC:
                t = "NUMERIC";
                break;
            case java.sql.Types.BIGINT:
                t = "BIGINT";
                break;
            case java.sql.Types.TINYINT:
                t = "TINYINT";
                break;
            case java.sql.Types.SMALLINT:
                t = "SMALLINT";
                break;
            case java.sql.Types.DATE:
                t = "DATE";
                break;
            case java.sql.Types.TIMESTAMP:
                t = "TIMESTAMP";
                break;
            case java.sql.Types.TIME:
                t = "TIME";
                break;
            case java.sql.Types.BIT:
                t = "BIT";
                break;
            case java.sql.Types.BOOLEAN:
                t = "BOOLEAN";
                break;
            case java.sql.Types.CHAR:
                t = "CHAR";
                break;
            case java.sql.Types.NVARCHAR:
                t = "NVARCHAR";
                break;
            case java.sql.Types.VARCHAR:
                t = "VARCHAR";
                break;
            case java.sql.Types.LONGNVARCHAR:
                t = "LONGNVARCHAR";
                break;
            case java.sql.Types.LONGVARCHAR:
                t = "LONGVARCHAR";
                break;
            case java.sql.Types.LONGVARBINARY:
                t = "LONGVARBINARY";
                break;
            case java.sql.Types.NCHAR:
                t = "NCHAR";
                break;
            case java.sql.Types.NCLOB:
                t = "NCLOB";
                break;
            case java.sql.Types.OTHER:
                t = "OTHER";
                break;
            case java.sql.Types.REAL:
                t = "REAL";
                break;
            case java.sql.Types.REF:
                t = "REF";
                break;
            case java.sql.Types.SQLXML:
                t = "SQLXML";
                break;
            case java.sql.Types.STRUCT:
                t = "STRUCT";
                break;
            case java.sql.Types.JAVA_OBJECT:
                t = "JAVA_OBJECT";
                break;
            case java.sql.Types.DATALINK:
                t = "DATALINK";
                break;
            case java.sql.Types.DISTINCT:
                t = "DISTINCT";
                break;
            case java.sql.Types.NULL:
                t = "NULL";
                break;
            default:
                t = "unknown data type";
                break;
        }
        return t;
    }
    
    
    
    private int     precision;
    private String  literal_prefix;
    private String  literal_suffix;
    private String  create_params;
    private short   nullable;
    private boolean case_sensitive;
    private short   searchable;
    private boolean unsigned_attribute;
    private boolean fixed_rec_scale;
    private String  local_type_name;
    private short   minimum_scale;
    private int     sql_data_type;
    private int     sql_datetime_sub;
    private int     num_prec_radix;
}
