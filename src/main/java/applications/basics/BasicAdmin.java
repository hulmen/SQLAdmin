package applications.basics;

/**
 *
 * BasicAdmin is a class doing all the basic sets and gets
 *
 * Admin is a Tool around mySQL to do basic jobs
 * for DB-Administrations, like:
 * - create/ drop tables
 * - create  indices
 * - perform sql-statements
 * - simple form
 * - a guided query
 * and a other usefull things in DB-arena
 *
 * Admin Version see below
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


public class BasicAdmin {
    
    
    public t_connect con=null;
    
    /**
     * Get the value of con.
     * @return value of con.
     */
    public t_connect getCon() {
        
        if ( con == null) {
            con = new t_connect(getHost(),
            getUser(),
            getPassword(),
            getDatabase());
            if ( ! con.acceptsConnection() ) con = null;
        }
        return con;
    }
    
    /**
     * Set the value of con.
     * @param v  Value to assign to con.
     */
    public void setCon(t_connect  v) {
        this.con = v;
        setUser(con.getUser());
        setHost(con.getHost());
        setPassword(con.getPassword());
        setDatabase(con.getDatabase());
    }
    
    
    
    
    String host;
    
    /**
     * Get the value of host.
     * @return Value of host.
     */
    public String getHost() {return host;}
    
    /**
     * Set the value of host.
     * @param v  Value to assign to host.
     */
    public void setHost(String  v) {this.host = v;}
    
    String user;
    
    /**
     * Get the value of user.
     * @return Value of user.
     */
    public String getUser() {return user;}
    
    /**
     * Set the value of user.
     * @param v  Value to assign to user.
     */
    public void setUser(String  v) {this.user = v;}
    
    String password;
    
    /**
     * Get the value of password.
     * @return Value of password.
     */
    public String getPassword() {return password;}
    
    /**
     * Set the value of password.
     * @param v  Value to assign to password.
     */
    public void setPassword(String  v) {this.password = v;}
    
    String database;
    
    /**
     * Get the value of database.
     * @return Value of database.
     */
    public String getDatabase() {return database;}
    
    /**
     * Set the value of database.
     * @param v  Value to assign to database.
     */
    public void setDatabase(String  v) {this.database = v;}
    
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
    
    
    String schema;
    
    /**
     * Get the value of schema.
     * @return value of schema.
     */
    public String getSchema() {return schema;}
    
    /**
     * Set the value of schema.
     * @param v  Value to assign to schema.
     */
    public void setSchema(String  v) {this.schema = v;}
    
    
    
    
    public BasicAdmin(String host, String user, String password, String database, String table) {
        
        this.setHost(host);
        this.setUser(user);
        this.setPassword(password);
        this.setDatabase(database);
        this.setTable(table);
    }
    
    public BasicAdmin(String host, String user, String password, String database, String table, String schema) {
        
        this.setHost(host);
        this.setUser(user);
        this.setPassword(password);
        this.setDatabase(database);
        this.setTable(table);
        this.setSchema(schema);
    }
    
    
    
    public BasicAdmin(String host, String user, String password, String database) {
        
        this.setHost(host);
        this.setUser(user);
        this.setPassword(password);
        this.setDatabase(database);
        this.setTable("%");
        this.setSchema("%");
    }
    
    public BasicAdmin(t_connect con) {
        this.setHost(con.getHost());
        this.setUser(con.getUser());
        this.setPassword(con.getPassword());
        this.setDatabase(con.getDatabase());
        this.setCon(con);
        this.setTable("%");
        this.setSchema("%");
    }
    
    
    public BasicAdmin(t_connect con, String table) {
        this.setHost(con.getHost());
        this.setUser(con.getUser());
        this.setPassword(con.getPassword());
        this.setDatabase(con.getDatabase());
        this.setCon(con);
        this.setTable(table);
        this.setSchema("%");
        
    }
    public BasicAdmin(t_connect con, String table, String schema) {
        this.setHost(con.getHost());
        this.setUser(con.getUser());
        this.setPassword(con.getPassword());
        this.setDatabase(con.getDatabase());
        this.setCon(con);
        this.setTable(table);
        this.setSchema(schema);
        
    }
    
}
