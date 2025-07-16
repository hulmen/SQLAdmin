package sql.fredy.metadata;



/** 
 * Columns is a wrapper about columns in a given Table
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 * and exports data into various formats
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

**/


public class Columns {

// Fredy's make Version
private static String fredysVersion = "Version 1.4   2. Jan.2002";

public String getVersion() {return fredysVersion; }

 
    int number;
    
    /**
       * Get the value of number.
       * @return Value of number.
       */
    public int getNumber() {return number;}
    
    /**
       * Set the value of number.
       * @param v  Value to assign to number.
       */
    public void setNumber(int  v) {this.number = v;}
    

    
    String name;
    
    /**
       * Get the value of name.
       * @return Value of name.
       */
    public String getName() {return name;}
    
    /**
       * Set the value of name.
       * @param v  Value to assign to name.
       */
    public void setName(String  v) {this.name = v;}
    


    int lng;
    
    /**
       * Get the value of lng.
       * @return Value of lng.
       */
    public int getLng() {return lng;}
    
    /**
       * Set the value of lng.
       * @param v  Value to assign to lng.
       */
    public void setLng(int  v) {this.lng = v;}
    
 
    
    int decDigits;
    
    /**
       * Get the value of decDigits.
       * @return Value of decDigits.
       */
    public int getDecDigits() {return decDigits;}
    
    /**
       * Set the value of decDigits.
       * @param v  Value to assign to decDigits.
       */
    public void setDecDigits(int  v) {this.decDigits = v;}
    


   
    String  type;
    
    /**
       * Get the value of type.
       * @return Value of type.
       */
    public String  getType() {return type;}
    
    /**
       * Set the value of type.
       * @param v  Value to assign to type.
       */
    public void setType(String   v) {this.type = v;}
    
    
    String table;
    
    /**
       * Get the value of table.
       * @return Value of table.
       */
    public String getTable() {return table;}
    
    /**
       * Set the value of table.
       * @param v  Value to assign to table.
       */
    public void setTable(String  v) {this.table = v;}
    
    public Columns() {

    }

    public  Columns(int no, String name, String type, int lng, int decDigits, String table) {


	setTable(table);
	setNumber(no);
	setName(name);
	setType(type);
	setLng(lng);
	setDecDigits(decDigits);

    }


}

