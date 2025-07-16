/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * This Class is part of Fredy's SQL-Admin Tool.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, like: - create/ drop tables - create indices - perform
 * sql-statements - simple form - a guided query - Data Export and a other
 * usefull things in DB-arena
 *
 * Admin (Version see below) Copyright (c) 1999 Fredy Fischer sql@hulmen.ch
 *
 * Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 * The icons used in this application are from Dean S. Jones
 *
 * Icons Copyright(C) 1998 by Dean S. Jones dean@gallant.com
 * www.gallant.com/icons.htm
 *
 * CalendarBean is Copyright (c) by Kai Toedter
 *
 * MSeries is Copyright (c) by Martin Newstead
 *
 * POI is from the Apache Foundation
 *
 *
 * Copyright (c) 2017 Fredy Fischer, sql@hulmen.ch
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
/*
All Queries sent to the Database are stored within a ApacheDerbyDB.
To do so, this class starts a Derby-Networkservice on Port 606060. 
If there is already a Derby running, there will be no Derby-Server started.

 */
package sql.fredy.tools;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import sql.fredy.infodb.DBconnectionPool;

/**
 *
 * @author sql@hulmen.ch
 */
public class QueryHistory {

    private String dbName;
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

    }

    public ArrayList<QueryHistoryEntry> read(String database) {

        ArrayList<QueryHistoryEntry> list = new ArrayList();

        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement read = null;
        try {

            connection = DBconnectionPool.getInstance().getConnection();

            read = connection.prepareStatement("select RunAt, Database, Query from APP.QueryHistory where Database = ? and ToolUser = ? order by RunAt desc");
            read.setString(1, database);
            read.setString(2, getUser());
            rs = read.executeQuery();
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
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
            if (read != null) 
              try {
                read.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (rs != null) 
              try {
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
        }
        return list;
    }

    public ArrayList<QueryHistoryEntry> findIt(String database, String pattern, boolean caseSensitive) {

        ArrayList<QueryHistoryEntry> list = new ArrayList();

        Connection connection = null;
        PreparedStatement find = null;
        ResultSet rs = null;
        if (!pattern.contains("%")) {
            pattern = "%" + pattern + "%";
        }

        try {

            connection = DBconnectionPool.getInstance().getConnection();

            if (caseSensitive) {
                find = connection.prepareStatement("select RunAt, Database, Query from APP.QueryHistory where query like ? and Database = ? and ToolUser = ? order by RunAt desc");
                find.setString(1, pattern);
                find.setString(2, database);
                find.setString(3, getUser());
                rs = find.executeQuery();
            } else {

                find = connection.prepareStatement("select RunAt, Database, Query from APP.QueryHistory where LOWER(query) like ? and Database = ? and ToolUser = ? order by RunAt desc");
                find.setString(1, pattern.toLowerCase());
                find.setString(2, database);
                find.setString(3, getUser());
                rs = find.executeQuery();
            }

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
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
            if (find != null) 
              try {
                find.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (rs != null) 
              try {
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
        }
        return list;
    }

    public ArrayList<QueryHistoryEntry> readAll() {

        ArrayList<QueryHistoryEntry> list = new ArrayList();

        Connection connection = null;
        PreparedStatement readAll = null;
        ResultSet rs = null;

        try {

            connection = DBconnectionPool.getInstance().getConnection();
            readAll = connection.prepareStatement("select RunAt, Database, Query from APP.QueryHistory where ToolUser = ? order by RunAt desc");
            readAll.setString(1, getUser());
            rs = readAll.executeQuery();
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
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
            if (readAll != null) 
              try {
                readAll.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (rs != null) 
              try {
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
        }
        return list;
    }

    public String getPreviousCommand(String db) {
        String pc = "";
        Connection connection = null;
        PreparedStatement readLast = null;
        ResultSet rs = null;

        try {

            connection = DBconnectionPool.getInstance().getConnection();
            readLast = connection.prepareStatement("select RunAt, Database, Query from APP.QueryHistory where Database = ? and ToolUser = ? order by RunAt desc FETCH FIRST ROW ONLY");
            readLast.setString(1, db);
            readLast.setString(2, getUser());
            rs = readLast.executeQuery();
            while (rs.next()) {
                pc = rs.getString("Query");
            }
            rs.close();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while SQL operation: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
            if (readLast != null) 
              try {
                readLast.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (rs != null) 
              try {
                rs.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
        }
        return pc;
    }

    public void cleanup(Timestamp t, String db) {

        PreparedStatement cleanUp = null;
        Connection connection = null;

        try {
            connection = DBconnectionPool.getInstance().getConnection();
            cleanUp = connection.prepareStatement("delete from APP.QueryHistory where RunAt < ? and Database = ? and ToolUser = ?");

            cleanUp.setTimestamp(1, t);
            cleanUp.setString(2, db);
            cleanUp.setString(3, getUser());
            cleanUp.executeUpdate();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while cleaning up: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
            if (cleanUp != null) 
              try {
                cleanUp.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
        }

    }

    public void delete(Timestamp t, String db) {
        PreparedStatement delete = null;
        Connection connection = null;

        try {
            connection = DBconnectionPool.getInstance().getConnection();
            delete = connection.prepareStatement("delete from APP.QueryHistory where RunAt = ? and DataBase = ?  and ToolUser = ? ");

            delete.setTimestamp(1, t);
            delete.setString(2, db);
            delete.setString(3, getUser());
            delete.executeUpdate();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while deleting: {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
            if (delete != null) 
              try {
                delete.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
        }

    }

    /**
     * we write the history entry we need the name of the DB and the query we
     * only write a query once
     */
    String lastQuery = "";

    public void write(String db, String query) {
        
        if ( query.equalsIgnoreCase(lastQuery) ) {
            return;
        }
        Connection connection = null;
        PreparedStatement insert = null;
        
        try {
            connection = DBconnectionPool.getInstance().getConnection();
            insert = connection.prepareStatement("insert into APP.QueryHistory ( Database ,ToolUser, Query, HashCode) values (?,?,?,?)");
            
            lastQuery = query;
            insert.setString(1, db);
            insert.setString(2, getUser());
            insert.setString(3, query);
            insert.setInt(4, query.hashCode());
            insert.executeUpdate();

  
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Exception while while writing history {0}", sqlex.getMessage());
            logger.log(Level.INFO, "Error Code: {0}", sqlex.getErrorCode());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "IO Exception : {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.WARNING, "Property Veto Exception: {0}", ex.getMessage());
        } finally {
            if (insert != null) 
              try {
                insert.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing DB objects {0}", e.getMessage());
            }
        }

    }

    private String getUser() {
        try {
            return System.getProperty("user.name");
        } catch (Exception e) {
            return "unknown";
        }
    }

}
