/**
 * *
 * FunctionMetaData is Part of Admin and delivers lot of the Meta-Data-Info of a Database
 * it contains the Result of a Function-Query
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

import java.util.HashMap;

/**
 *
 * @author tkfir
 */
public class FunctionMetaData {

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
     * @return the functionType
     */
    public short getFunctionType() {
        return functionType;
    }

    /**
     * @param functionType the functionType to set
     */
    public void setFunctionType(short functionType) {
        this.functionType = functionType;
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
    private String remarks;
    private short functionType;
    private String specificName;
    private HashMap<String, FunctionColumnMetaData> columnMetaData;

    /**
     * @return the columnMetaData
     */
    public HashMap<String, FunctionColumnMetaData> getColumnMetaData() {
        return columnMetaData;
    }

    /**
     * @param columnMetaData the columnMetaData to set
     */
    public void setColumnMetaData(HashMap<String, FunctionColumnMetaData> columnMetaData) {
        this.columnMetaData = columnMetaData;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder() ;
        sb.append("DB = ").append(getFunctionCatalog());
        sb.append("Schema = ").append(getFunctionSchema());
        sb.append("Name = " ).append(getFunctionName());
        return sb.toString();
    }
}
