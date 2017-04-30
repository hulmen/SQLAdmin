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
    
}
