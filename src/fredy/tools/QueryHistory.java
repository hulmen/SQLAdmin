/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
   This Class is part of Fredy's SQL-Admin Tool.
 
   Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
   DB-Administrations, like: - create/ drop tables - create indices - perform
   sql-statements - simple form - a guided query - Data Export and a other
   usefull things in DB-arena
 
   Admin (Version see below) Copyright (c) 1999 Fredy Fischer sql@hulmen.ch
 
   Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 
   The icons used in this application are from Dean S. Jones
 
   Icons Copyright(C) 1998 by Dean S. Jones dean@gallant.com
   www.gallant.com/icons.htm
 
   CalendarBean is Copyright (c) by Kai Toedter
 
   MSeries is Copyright (c) by Martin Newstead
 
   POI is from the Apache Foundation
 
 
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
package sql.fredy.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sql@hulmen.ch
 */
public class QueryHistory {

    private String dbName;
    Connection con;
    PreparedStatement insert, update, read, readAll, readLast, delete, cleanUp, find, previous, check;
    ResultSet queryHistory = null;
    String connectionURL;
    String driver;
    int loopCounter = 0;
    int CHUNKSIZE = 1;

    private Logger logger = Logger.getLogger("sql.fredy.tools.QueryHistory");
    private DerbyInfoServer derby;
    private boolean shutDownDerby = false;

    /**
     * @return the dbName
     */
    public String getDbName() {
        return dbName;
    }

    /**
     * @param dbName the dbName to set
     */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public QueryHistory() {
        /*
         get the derby-things without starting up the server
         */
        derby = new DerbyInfoServer(false);

        try {
            Class.forName(derby.getJDBCDriver()).newInstance();
        } catch (ClassNotFoundException cnfe) {

        } catch (Exception e) {

        }

        // Check if a DerbyServer is up and running
        try {
            con = DriverManager.getConnection(derby.getJDBCUrl());
        } catch (SQLException sqlex) {

            if (sqlex.getErrorCode() == 40000) {
                // we start the derby server
                derby = new DerbyInfoServer(true);
                shutDownDerby = true;
            } else {
                logger.log(Level.WARNING, "Exception when testing connection: {0}", sqlex.getMessage());
                logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
            }
        }

        try {
            con = DriverManager.getConnection(derby.getJDBCUrl());

            String createTable = "create table APP.QueryHistory (\n"
                    + "RunAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                    + "Database varchar(1024),\n"
                    + "Query VARCHAR(32672),\n"
                    + "HashCode INTEGER "
                    + ")";

            // we create the table and ignore the error, if it already exists
            Statement stmt = con.createStatement();
            stmt.executeUpdate(createTable);

            // and the Index
            stmt.executeUpdate("create INDEX APP.QUERYHISTORYIDX on APP.QueryHistory(HashCode)");
            stmt.close();

        } catch (SQLException sqlex) {

            // we expect the error X0Y32 if the table already exists
            if (!sqlex.getSQLState().equalsIgnoreCase("X0Y32")) {
                logger.log(Level.WARNING, "Exception while initialising the History DB-things {0}", sqlex.getMessage());
                logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
            }

        }

        try {
            insert = con.prepareStatement("insert into APP.QueryHistory ( Database, Query, HashCode) values (?,?,?)");
            readAll = con.prepareStatement("select RunAt, Database, Query from APP.QueryHistory order by RunAt desc");
            readLast = con.prepareStatement("select RunAt, Database, Query from APP.QueryHistory where Database = ? order by RunAt desc FETCH FIRST ROW ONLY");
            read = con.prepareStatement("select RunAt, Database, Query from APP.QueryHistory where Database = ? order by RunAt desc");
            delete = con.prepareStatement("delete from APP.QueryHistory where RunAt = ?");
            cleanUp = con.prepareStatement("delete from APP.QueryHistory where RunAt < ? and Database = ?");
            find = con.prepareStatement("select RunAt, Database, Query from APP.QueryHistory where query like ? and Database = ?");
            previous = con.prepareStatement("select query from APP.QueryHistory order by RunAt desc");
            check = con.prepareStatement("select count(*) from APP.QueryHistory where HashCode = ?");
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while preparing statements: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }

    }

    public ArrayList<QueryHistoryEntry> read(String database) {
        ArrayList<QueryHistoryEntry> list = new ArrayList();

        try {
            read.setString(1, database);
            ResultSet rs = read.executeQuery();
            while (rs.next()) {
                QueryHistoryEntry qhe = new QueryHistoryEntry();
                qhe.setRunat(rs.getTimestamp(1));
                qhe.setDatabase(rs.getString(2));
                qhe.setQuery(rs.getString(3));
                //logger.log(Level.INFO, "reading {0}", qhe.getDatabase());
                list.add(qhe);
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while SQL operation: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }
        return list;
    }

    public ArrayList<QueryHistoryEntry> findIt(String database, String pattern) {
        ArrayList<QueryHistoryEntry> list = new ArrayList();

        if (!pattern.contains("%")) {
            pattern = "%" + pattern + "%";
        }
        try {
            find.setString(1, pattern);
            find.setString(2, database);
            ResultSet rs = find.executeQuery();
            while (rs.next()) {
                QueryHistoryEntry qhe = new QueryHistoryEntry();
                qhe.setRunat(rs.getTimestamp(1));
                qhe.setDatabase(rs.getString(2));
                qhe.setQuery(rs.getString(3));
                //logger.log(Level.INFO, "reading {0}", qhe.getDatabase());
                list.add(qhe);
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while SQL operation: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }
        return list;
    }

    public ArrayList<QueryHistoryEntry> readAll() {
        ArrayList<QueryHistoryEntry> list = new ArrayList();

        try {
            ResultSet rs = readAll.executeQuery();
            while (rs.next()) {
                QueryHistoryEntry qhe = new QueryHistoryEntry();
                qhe.setRunat(rs.getTimestamp(1));
                qhe.setDatabase(rs.getString(2));
                qhe.setQuery(rs.getString(3));
                //logger.log(Level.INFO, "reading {0}", qhe.getDatabase());
                list.add(qhe);
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while SQL operation: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }
        return list;
    }

    public String getPreviousCommand(String db) {
        String pc = "";
        try {
            readLast.setString(1,db);
            ResultSet rs = readLast.executeQuery();
            while (rs.next()) {
                pc = rs.getString("Query");                
            }
            rs.close();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while SQL operation: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }
        return pc;
    }

    public void cleanup(Timestamp t, String db) {
        try {
            cleanUp.setTimestamp(1, t);
            cleanUp.setString(2, db);
            cleanUp.executeUpdate();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while cleaning up: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }

    }

    public void delete(Timestamp t) {
        try {
            delete.setTimestamp(1, t);

            delete.executeUpdate();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while deleting: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }

    }

    public void flush() {
        try {
            insert.executeBatch();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while flushing the History DB-things {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }
    }

    public void close() {
        try {
            flush();
            insert.close();
            read.close();
            readAll.close();
            delete.close();
            con.close();
            if (shutDownDerby) {
                derby.stop();
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while closing the History DB-things {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        }

    }

    /**
     * we write the history entry we need the name of the DB and the query we
     * only write a query once
     */
    String lastQuery = "";
    int counter = 0;

    public void write(String db, String query) {
        //logger.log(Level.INFO, "Writing history...");

        // first we check if the hash is here already
        boolean writeIt = true;

        /*
        no
        NO! we do not, ist not such a good idea to use a hashcode....
        try {
          check.setInt(1, query.hashCode());
          ResultSet wrs = check.executeQuery();
          while ( wrs.next()) {
              int c  = wrs.getInt(1);
              if ( c > 0 ) writeIt = false;
          }          
        } catch (SQLException sqlex) {
            // trage hier deine Fehlermeldung ein
            logger.log(Level.WARNING, "Exception when checking existense of query : {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Error : {0}", sqlex.getErrorCode());          
        }
        
         */
        // if not such hashcode exists, we write the query
        if (writeIt) {

            //logger.log(Level.FINE, "Writing history...");
            try {
                lastQuery = query;
                insert.setString(1, db);
                insert.setString(2, query);
                insert.setInt(3, query.hashCode());
                insert.addBatch();

                if (++counter % CHUNKSIZE == 0) {
                    flush();
                }
            } catch (SQLException sqlex) {
                logger.log(Level.WARNING, "Exception while while writing history {0}", sqlex.getMessage());
                logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
            }
        }
    }

}
