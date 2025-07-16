package applications.basics;

/**
 *
 * BasicWrapper contains all common things of the Wrapper-Classes
 *
 * Admin is a Tool around SQL-Databases to do basic jobs
 * for DB-Administrations, like:
 * - create/ drop tables
 * - create  indices
 * - perform sql-statements
 * - simple form
 * - a guided query
 * and a other usefull things in DB-arena
 *
 * Admin Version see below
 
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


 *
 *
 **/
import java.sql.*;
import java.util.Calendar;
import java.util.logging.*;

public class BasicWrapper extends BasicAdmin {

    Logger logger;
    public ResultSet sqlresult;
    String query;
    public boolean useLookup = false;

    /**
     * Get the value of query.
     * @return value of query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the value of query.
     * @param v  Value to assign to query.
     */
    public void setQuery(String v) {
        this.query = v;
    }

    public String execQuery() {

        if (con == null) {
            openConnection();
        }


        try {
            int records = con.stmt.executeUpdate(getQuery());
        } catch (SQLException e) {
            return e.getMessage().toString();
        }
        return "ok";
    }

    public String execQuery(PreparedStatement stmt) {
        if (con == null) {
            openConnection();
        }
        try {
            int records = stmt.executeUpdate();
        } catch (SQLException e) {
            return e.getMessage().toString();
        }
        return "ok";
    }

    public String selectQuery() {

        if (con == null) {
            openConnection();
        }
        try {
            sqlresult = con.stmt.executeQuery(getQuery());
        } catch (SQLException e) {
            return e.getMessage().toString();
        }
        return "ok";
    }

    // only useful if running MySQL
    // returns the lates ID if using an auto increment tablekey
    public int getLastInsertID() {
        int lId = 0;
        try {
            ResultSet sqlresult = con.stmt.executeQuery("select last_insert_id()");
            sqlresult.next();
            if (sqlresult != null) {
                lId = sqlresult.getInt(1);
            }
        } catch (SQLException e) {
            lId = 0;
        }
        return lId;
    }

    public String selectQuery(PreparedStatement stmt) {

        try {
            sqlresult = stmt.executeQuery();
        } catch (SQLException e) {
            return e.getMessage().toString();
        }
        return "ok";
    }

    public PreparedStatement tempStatement(String q) {
        try {
            PreparedStatement pstmt = con.con.prepareStatement(q);
            return pstmt;
        } catch (SQLException sqlexception) {
            return null;
        }
    }

    public String toDay() {

        Calendar c = Calendar.getInstance();


        String month, day;

        if ( (c.get(Calendar.MONTH) + 1)  < 10 ) {
            month = "0" + Integer.toString(c.get(Calendar.MONTH) + 1) ;
        } else {
            month = Integer.toString(c.get(Calendar.MONTH) + 1) ;
        }
               
        if ( c.get(Calendar.DATE)  < 10 ) {
          day = "0" + Integer.toString(c.get(Calendar.DATE));
        } else {
            day = Integer.toString(c.get(Calendar.DATE));    
        }

        return Integer.toString(c.get(Calendar.YEAR)) + "-"
                + month + "-"
                + day;
    }

    private void openConnection() {
        logger.log(Level.INFO, "reopening connection");
        if (!useLookup) {
            getCon();
            if (con.getError() != null) {
                logger.log(Level.WARNING, "Can not connect to DB: " + con.getError());
                con = null;
            }
        }
    }

    public void initPreparedStatements() {
    }

    public void init() {
        logger = Logger.getLogger("applications.basics");
        if (con == null) {
            openConnection();
        }
        initPreparedStatements();


    }

   
    public BasicWrapper(t_connect con) {
        super(con);
        init();
    }

    public BasicWrapper(t_connect con,String schema) {
        super(con, schema);
        init();
    }

    public BasicWrapper(t_connect con,String table,String schema) {
        super(con,table,schema);
        setTable(table);
        init();
    }

    public BasicWrapper(String host, String user,String password,String database,String schema) {
        super(host, user, password, database, schema);
        init();
    }

    public BasicWrapper(String host,String user,String password,String database) {
        super(host, user, password, database, "%");
        init();
    }

    public BasicWrapper(String host,String user,String password,String database,String table,String schema) {

        super(host, user, password, database, schema);
        setTable(table);
        init();
    }

    public BasicWrapper(java.sql.Connection c) {
        super(new t_connect(c));
        init();
    }

    public BasicWrapper(java.lang.String datasource) {
        super(new t_connect(datasource));
        useLookup = true;
        init();
    }
}
