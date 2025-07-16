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

 */

/*

This class represents a foreign-key construct of two tables. 
It contains the tablename and the attribute-name of both tables, 
named as 1 and 2

*/

package sql.fredy.metadata;

/**
 *
 * @author sql@hulmen.ch
 */
public class ForeignKeyConstraints {
    private String table1;
    private String field1;
    private String table2;
    private String field2;

    /**
     * @return the table1
     */
    public String getTable1() {
        return table1;
    }

    /**
     * @param table1 the table1 to set
     */
    public void setTable1(String table1) {
        this.table1 = table1;
    }

    /**
     * @return the field1
     */
    public String getField1() {
        return field1;
    }

    /**
     * @param field1 the field1 to set
     */
    public void setField1(String field1) {
        this.field1 = field1;
    }

    /**
     * @return the table2
     */
    public String getTable2() {
        return table2;
    }

    /**
     * @param table2 the table2 to set
     */
    public void setTable2(String table2) {
        this.table2 = table2;
    }

    /**
     * @return the field2
     */
    public String getField2() {
        return field2;
    }

    /**
     * @param field2 the field2 to set
     */
    public void setField2(String field2) {
        this.field2 = field2;
    }
        
}
