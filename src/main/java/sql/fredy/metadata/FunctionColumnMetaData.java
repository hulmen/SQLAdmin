/**
 * *
 * FunctionColumnMetaData is Part of Admin and delivers lot of the Meta-Data-Info of a Database
 * it contains the the description of a column within a function
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


package sql.fredy.metadata;

/**
 *
 * @author tkfir
 */


public class FunctionColumnMetaData {

    /**
     * @return the functionCatalog
     */
    public String getFunctionCatalog() {
        return functionCatalog;
    }

    /**
     * @param functionCatalog the functionCatalog to set
     */
    public void setFunctionCatalog(String functionCatalog) {
        this.functionCatalog = functionCatalog;
    }

    /**
     * @return the functionSchema
     */
    public String getFunctionSchema() {
        return functionSchema;
    }

    /**
     * @param functionSchema the functionSchema to set
     */
    public void setFunctionSchema(String functionSchema) {
        this.functionSchema = functionSchema;
    }

    /**
     * @return the functionName
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * @param functionName the functionName to set
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the columnType
     */
    public Short getColumnType() {
        return columnType;
    }

    /**
     * @param columnType the columnType to set
     */
    public void setColumnType(Short columnType) {
        this.columnType = columnType;
    }

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

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
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

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the scale
     */
    public short getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(short scale) {
        this.scale = scale;
    }

    /**
     * @return the radix
     */
    public short getRadix() {
        return radix;
    }

    /**
     * @param radix the radix to set
     */
    public void setRadix(short radix) {
        this.radix = radix;
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
     * @return the remarks
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * @param remarks the remarks to set
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * @return the charOctetLength
     */
    public int getCharOctetLength() {
        return charOctetLength;
    }

    /**
     * @param charOctetLength the charOctetLength to set
     */
    public void setCharOctetLength(int charOctetLength) {
        this.charOctetLength = charOctetLength;
    }

    /**
     * @return the ordinalPosition
     */
    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    /**
     * @param ordinalPosition the ordinalPosition to set
     */
    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    /**
     * @return the isNullable
     */
    public String getIsNullable() {
        return isNullable;
    }

    /**
     * @param isNullable the isNullable to set
     */
    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    /**
     * @return the specificName
     */
    public String getSpecificName() {
        return specificName;
    }

    /**
     * @param specificName the specificName to set
     */
    public void setSpecificName(String specificName) {
        this.specificName = specificName;
    }
    private String functionCatalog;
    private String functionSchema;
    private String functionName;
    private String columnName;
    private Short  columnType;
    private int dataType;
    private String typeName;
    private int precision;
    private int length;
    private short scale;
    private short radix;
    private short nullable;
    private String remarks;
    private int charOctetLength;
    private int ordinalPosition;
    private String isNullable;
    private String specificName;
    
}
