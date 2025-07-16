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
package sql.fredy.ui;

/**
 *
 * @author a_tkfir
 */
public class ObjectChildren {

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

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the displayablename
     */
    public String getDisplayablename() {
        return displayablename;
    }

    /**
     * @param displayablename the displayablename to set
     */
    public void setDisplayablename(String displayablename) {
        this.displayablename = displayablename;
    }

    /**
     * @return the columnname
     */
    public String getColumnname() {
        return columnname;
    }

    /**
     * @param columnname the columnname to set
     */
    public void setColumnname(String columnname) {
        this.columnname = columnname;
    }

    /**
     * @return the columntype
     */
    public String getColumntype() {
        return columntype;
    }

    /**
     * @param columntype the columntype to set
     */
    public void setColumntype(String columntype) {
        this.columntype = columntype;
    }

    private String type;
    private String database;
    private String schema;
    private String table;
    private String displayablename;
    private String columnname;
    private String columntype;
    private String lenght;

    public ObjectChildren(String type, String database, String schema, String table, String displayablename, String columnname, String columntype, String lng) {
        setType(type);
        setDatabase(database);
        setSchema(schema);
        setTable(table);
        setDisplayablename(displayablename);
        setColumnname(columnname);
        setColumntype(columntype);
        setLenght(lng);
    }

    /**
     * @return the lenght
     */
    public String getLenght() {
        return lenght;
    }

    /**
     * @param lenght the lenght to set
     */
    public void setLenght(String lenght) {
        this.lenght = lenght;
    }
}
