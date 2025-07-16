package applications.basics;

/** 

    PrimaryKeys is a part of Admin and returns the Primary keys of a table

    Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
    for DB-Administrations, like:
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    and a other usefull things in DB-arena

   
		       Fredy Fischer
		       Hulmenweg 36
		       8405 Winterthur
		       Switzerland

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



**/




public class PrimaryKey   {


    public PrimaryKey() {


    }
 

    public PrimaryKey(String tableName, String columnName, short keySeq, String pkName) {
	this.setTableName(tableName);
	this.setColumnName(columnName);
	this.setKeySeq(keySeq);
	this.setPkName(pkName);
    }


    
    String tableName;
    
    /**
       * Get the value of tableName.
       * @return Value of tableName.
       */
    public String getTableName() {return tableName;}
    
    /**
       * Set the value of tableName.
       * @param v  Value to assign to tableName.
       */
    public void setTableName(String  v) {this.tableName = v;}
    
    
    String columnName;
    
    /**
       * Get the value of columnName.
       * @return Value of columnName.
       */
    public String getColumnName() {return columnName;}
    
    /**
       * Set the value of columnName.
       * @param v  Value to assign to columnName.
       */
    public void setColumnName(String  v) {this.columnName = v;}
    
    
    short keySeq;
    
    /**
       * Get the value of keySeq.
       * @return Value of keySeq.
       */
    public short getKeySeq() {return keySeq;}
    
    /**
       * Set the value of keySeq.
       * @param v  Value to assign to keySeq.
       */
    public void setKeySeq(short  v) {this.keySeq = v;}
    

    
    String pkName;
    
    /**
       * Get the value of pkName.
       * @return Value of pkName.
       */
    public String getPkName() {return pkName;}
    
    /**
       * Set the value of pkName.
       * @param v  Value to assign to pkName.
       */
    public void setPkName(String  v) {this.pkName = v;}

}
