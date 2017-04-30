/*
 
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


 * This Class is part of Fredy's SQL-Admin Tool.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, like: - create/ drop tables - create indices - perform
 * sql-statements - simple form - a guided query - Data Export and a other
 * usefull things in DB-arena
 *
 * Admin (Version see below) Copyright (c) 1999 Fredy Fischer sql@hulmen.ch
 *
 * Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 *
 * The icons used in this application are from Dean S. Jones
 *
 * Icons Copyright(C) 1998 by Dean S. Jones dean@gallant.com
 * www.gallant.com/icons.htm
 *
 * CalendarBean is Copyright (c) by Kai Toedter
 *
 * MSeries is Copyright (c) by Martin Newstead
 *
 * POI is from the Apache Foundation
 *
 
 */
package sql.fredy.ui;

import java.io.File;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author sql@hulmen.ch
 */
public class DbTreeNode extends DefaultMutableTreeNode {

    public static String DATABASENAME = "DATABASE_NAME";
    public static String SCHEMANAME = "SCHEMA_NAME";
    public static String TABLENAME = "TABLE_NAME";
    public static String COLUMNNAME = "COLUMN_NAME";

    public static String TYPE_DATABASE = "DATABASE";
    public static String TYPE_SCHEMA = "SCHEMA";
    public static String TYPE_ENTITY = "ENTITY";
    public static String TYPE_COLUMN = "COLUMN";

    public static String TYPE_TABLE = "TABLE";
    public static String TYPE_VIEW = "VIEW";
    public static String TYPE_ALIAS = "ALIAS";
    public static String TYPE_SYNONYM = "SYNONYM";
    public static String TYPE_SYSTEMTABLE = "SYSTEM Table";
    public static String TYPE_GLOBAL_TEMPORARY = "Global Temporary Table";
    public static String TYPE_LOCAL_TEMPORARY = "Local Temporary Table";

    private String dbName = "";
    private String schemaName = "";
    private String tableName = "";
    private String columnName = "";
    private String columnType = "";
    private String type = "";
    private int length;
    private boolean nullable;
    private boolean primaryKey;
    private boolean autoIncrement;
    private String product = "";
    private String productVersion = "";
    private int decimalDigits;
    private int numPrec;

    private String columnDefault;
    private int columnDataType = 0;

    private String entityType = "";
    private String closeBracket= "\"";
    private String openBracket = "\"";
   
    private Logger logger = Logger.getLogger("sql.fredy.ui");
    
    public DbTreeNode() {
        super();
        vendorDependendBrackets();
    }

    public DbTreeNode(Object userObject) {
        super(userObject);
        vendorDependendBrackets();
    }

    public DbTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
        vendorDependendBrackets();
    }

    /**
     * @return the dbName
     */
    public String getDbName() {

        return fixSpaces(dbName);
    }

    /**
     * @param dbName the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /**
     * @return the schemaName
     */
    public String getSchemaName() {
        return fixSpaces(schemaName);
    }

    /**
     * @param schemaName the schemaName to set
     */
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return fixSpaces(tableName);
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return fixSpaces(columnName);
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
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

    public void setTtype(String t) {

        if (t.equalsIgnoreCase("TABLE")) {
            setType(TYPE_TABLE);
        }
        if (t.equalsIgnoreCase("VIEW")) {
            setType(TYPE_VIEW);
        }
        if (t.equalsIgnoreCase("SYSTEM TABLE")) {
            setType(TYPE_SYSTEMTABLE);
        }
        if (t.equalsIgnoreCase("GLOBAL TEMPORARY")) {
            setType(TYPE_GLOBAL_TEMPORARY);
        }
        if (t.equalsIgnoreCase("LOCAL TEMPORARY")) {
            setType(TYPE_LOCAL_TEMPORARY);
        }
        if (t.equalsIgnoreCase("ALIAS")) {
            setType(TYPE_ALIAS);
        }
        if (t.equalsIgnoreCase("SYNONYM")) {
            setType(TYPE_SYNONYM);
        }
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
     * @return the nullable
     */
    public boolean isNullable() {
        return nullable;
    }

    /**
     * @param nullable the nullable to set
     */
    public void setNullable(boolean nullable) {
        this.nullable = nullable;
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
     * @return the product
     */
    public String getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(String product) {
        this.product = product;
    }

    /**
     * @return the productVersion
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * @param productVersion the productVersion to set
     */
    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    /**
     * @return the columnType
     */
    public String getColumnType() {
        return columnType;
    }

    /**
     * @param columnType the columnType to set
     */
    public void setColumnType(String columnType) {
        this.columnType = columnType;
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
     * @return the decimalDigits
     */
    public int getDecimalDigits() {
        return decimalDigits;
    }

    /**
     * @param decimalDigits the decimalDigits to set
     */
    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    /**
     * @return the numPrec
     */
    public int getNumPrec() {
        return numPrec;
    }

    /**
     * @param numPrec the numPrec to set
     */
    public void setNumPrec(int numPrec) {
        this.numPrec = numPrec;
    }

    /**
     * @return the entityType
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * @param entityType the entityType to set
     */
    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    /**
     * If one uses Spaces in object names, this might confuse the SQL command
     * therefore we fence the value with ". If in any cases, this is not wanted,
     * set the value NOFIXSPACES to true or use the method unfixSpaces
     *
     * @param v the name to be masked
     */
    private boolean FIXSPACES = true;

    /*
    public String fixSpaces(String v) {
        if (FIXSPACES) {
            if (v.indexOf(" ") >= 0) {
                v = "\"" + v + "\"";
            }
        }
        return v;
    }
     */
   
    private void vendorDependendBrackets() {        
        if (getProduct().toLowerCase().contains("microsoft")) {
            setOpenBracket("[");
            setCloseBracket("]");
        }
        if (getProduct().toLowerCase().contains("mysql")) {
            setOpenBracket("`");
            setCloseBracket("`");
        }
    }

    private String inBrackets(String v) {
        return getOpenBracket() + v + getCloseBracket();
    }
   
    SqlWords sqlWords = new SqlWords();
 /*
    private String fixSpaces(String v) {

        Iterator sqlWordsIterator = sqlWords.sqlWordsIterator;
        if (v != null) {
            if ((v.trim().contains(" ")) || (v.trim().contains("-")) || (v.trim().contains("+")) || (v.trim().contains("*")) || (v.trim().contains("\\")) || (v.trim().contains("/"))) {
                v = inBrackets(v);
            } else {
                // if there is a reserved SQL-Keyword, we put it in brackets
                while (sqlWordsIterator.hasNext()) {
                    if (v.equalsIgnoreCase((String) sqlWordsIterator.next())) {
                        v = inBrackets(v);
                        break;
                    }
                }
            }
        }
        return v;
    }
*/
    public String fixSpaces(String v) {
        if (FIXSPACES) {
            if (v.indexOf(" ") >= 0) {
                v = "\"" + v + "\"";
            }
        }
        return v;
    }
    
    public String unfixSpaces(String v) {
        if (v.indexOf("\"") >= 0) {
            v = v.replace("\"", "");
        }
        return v;
    }

    /**
     * @return the FIXSPACES
     */
    public boolean isFIXSPACES() {
        return FIXSPACES;
    }

    /**
     * @param FIXSPACES the FIXSPACES to set
     */
    public void setFIXSPACES(boolean FIXSPACES) {
        this.FIXSPACES = FIXSPACES;
    }

    /**
     * @return the columnDefault
     */
    public String getColumnDefault() {
        return columnDefault;
    }

    /**
     * @param columnDefault the columnDefault to set
     */
    public void setColumnDefault(String columnDefault) {
        this.columnDefault = columnDefault;
    }

    /**
     * @return the closedBracket
     */
    public String getCloseBracket() {
        return closeBracket;
    }

    /**
     * @param closeBracket the closedBracket to set
     */
    public void setCloseBracket(String closeBracket) {
        this.closeBracket = closeBracket;
    }

    /**
     * @return the openBracket
     */
    public String getOpenBracket() {
        return openBracket;
    }

    /**
     * @param openBracket the openBracket to set
     */
    public void setOpenBracket(String openBracket) {
        this.openBracket = openBracket;
    }

}
