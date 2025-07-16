
package sql.fredy.sqltools;

/**
 
   This class represents a Tablefield and is used by the XML-Importer
   to create the table
 
   Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
   for DB-Administrations, as create / delete / alter and query tables
   it also creates indices and generates simple Java-Code to access DBMS-tables
   and exports data into various formats
 
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
/**
 *
 * @author sql@hulmen.ch
 */
public class TableField {

    private String fieldName = null;
    private String fieldType = null;
    private String length = null;
    private String xmlField = null;   
    private String[] options;
    private int insertSequence = 0;


    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName the fieldName to set
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the fieldType
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * @param fieldType the fieldType to set
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * @return the length
     */
    public String getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(String length) {
        this.length = length;
    }

    /**
     * @return the options
     */
    public String[] getOptions() {
        return options;
    }

    /**
     * @param options the options to set
     */
    public void setOptions(String[] options) {
        this.options = options;
    }

    public void addOption(String v) {
        int n = options.length + 1;
        options[n] = v;
    }

    public int optionsLength() {
        try {
            return options.length;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getOption(int n) {
        return options[n];
    }

    /**
     * @return the xmlField
     */
    public String getXmlField() {
        return xmlField;
    }

    /**
     * @param xmlField the xmlField to set
     */
    public void setXmlField(String xmlField) {
        this.xmlField = xmlField;
    }

    /**
     * @return the insertSequence
     */
    public int getInsertSequence() {
        return insertSequence;
    }

    /**
     * @param insertSequence the insertSequence to set
     */
    public void setInsertSequence(int insertSquence) {
        this.insertSequence = insertSquence;
    }

 }
