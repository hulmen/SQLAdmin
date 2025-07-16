/*
 *
 * This class is representing a column in the JDBC Type mapping to DB Product data types
 *
 * It contains 
 *  ProductName            String
 *  JDBC Type Name         String
 *  JDBC Type Number       java.sql.Types integer
 *  DB Product Type Name   String
 *  Max Lenght             int
 *  bigger than MaxLength  String (mainly max)
 *  hasLength              boolean
 *  hasPrecision           boolean
 *  has maxLenght Text     boolean
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

/**
 *
 * @author fredy
 */
public class JdbcTypeMapping {

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return the jdbcTypeName
     */
    public String getJdbcTypeName() {
        return jdbcTypeName;
    }

    /**
     * @param jdbcTypeName the jdbcTypeName to set
     */
    public void setJdbcTypeName(String jdbcTypeName) {
        this.jdbcTypeName = jdbcTypeName;
    }

    /**
     * @return the jdbcTypeNumber
     */
    public int getJdbcTypeNumber() {
        return jdbcTypeNumber;
    }

    /**
     * @param jdbcTypeNumber the jdbcTypeNumber to set
     */
    public void setJdbcTypeNumber(int jdbcTypeNumber) {
        this.jdbcTypeNumber = jdbcTypeNumber;
    }

    /**
     * @return the dbProductTypeName
     */
    public String getDbProductTypeName() {
        return dbProductTypeName;
    }

    /**
     * @param dbProductTypeName the dbProductTypeName to set
     */
    public void setDbProductTypeName(String dbProductTypeName) {
        this.dbProductTypeName = dbProductTypeName;
    }

    /**
     * @return the maxLength
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * @param maxLength the maxLength to set
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return the biggerMaxLengthText
     */
    public String getBiggerMaxLengthText() {
        return biggerMaxLengthText;
    }

    /**
     * @param biggerMaxLengthText the biggerMaxLengthText to set
     */
    public void setBiggerMaxLengthText(String biggerMaxLengthText) {
        this.biggerMaxLengthText = biggerMaxLengthText;
    }

    /**
     * @return the hasLength
     */
    public boolean isHasLength() {
        return hasLength;
    }

    /**
     * @param hasLength the hasLength to set
     */
    public void setHasLength(boolean hasLength) {
        this.hasLength = hasLength;
    }

    /**
     * @return the hasPrecision
     */
    public boolean isHasPrecision() {
        return hasPrecision;
    }

    /**
     * @param hasPrecision the hasPrecision to set
     */
    public void setHasPrecision(boolean hasPrecision) {
        this.hasPrecision = hasPrecision;
    }

    /**
     * @return the hasMaxLengthText
     */
    public boolean isHasMaxLengthText() {
        return hasMaxLengthText;
    }

    /**
     * @param hasMaxLengthText the hasMaxLengthText to set
     */
    public void setHasMaxLengthText(boolean hasMaxLengthText) {
        this.hasMaxLengthText = hasMaxLengthText;
    }
       
    private String   productName;
    private String   jdbcTypeName;
    private int      jdbcTypeNumber;
    private String   dbProductTypeName;
    private int      maxLength;
    private String   biggerMaxLengthText;
    private boolean  hasLength;
    private boolean  hasPrecision;
    private boolean  hasMaxLengthText;
    
}
