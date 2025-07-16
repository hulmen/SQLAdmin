package sql.fredy.ui;

/**
 * DBcomboBox creates a combobox out of the 1st Field a SQL-Query returns.
 *
 *
 * Admin is a Tool around mySQL to do basic jobs for DB-Administrations, like: -
 * create/ drop tables - create indices - perform sql-statements - simple form -
 * a guided query and a other usefull things in DB-arena
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
 *
 *
 */
import java.beans.PropertyVetoException;
import javax.swing.*;
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.*;
import java.util.logging.Level;
import sql.fredy.connection.DataSource;

public class DBcomboBox extends JComboBox {

    private Logger logger = Logger.getLogger("sql.fredy.ui");

    private ResultSet sqlresult;
    private ResultSetMetaData metaData;

    private String dbgMessage = "";

    public String getMessage() {
        return dbgMessage;
    }

    String query;

    /**
     * Get the value of query.
     *
     * @return Value of query.
     */
    public String getQuery() {
        return query;
    }

    /**
     * Set the value of query.
     *
     * @param v Value to assign to query.
     */
    public void setQuery(String v) {
        this.query = v;
    }

    public void setText(String t) {
        try {
            this.setSelectedItem((String) t);
        } catch (Exception e) {;
        }
    }

    public String getText() {
        return (String) this.getSelectedItem();
    }

    public void clear() {
        try {
            this.setSelectedIndex(0);
        } catch (Exception e) {;
        }
    }

    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    public Connection getConnection() {

        Connection con = null;
        try {
            con = DataSource.getInstance().getConnection();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {
            return con;
        }

    }

    boolean standAlone = true;

    /**
     * Get the value of standAlone.
     *
     * @return value of standAlone.
     */
    public boolean isStandAlone() {
        return standAlone;
    }

    /**
     * Set the value of standAlone.
     *
     * @param v Value to assign to standAlone.
     */
    public void setStandAlone(boolean v) {
        this.standAlone = v;
    }

    public DBcomboBox(String query) {
        setQuery(query);
        setItems();
        try {
            this.setSelectedIndex(0);
        } catch (Exception excp) {;
        }

    }

    private void message(String msg) {
        dbgMessage = dbgMessage + "\n" + msg;
    }

    public void reload() {
        removeItems();
        setItems();
    }

    public void removeItems() {
        try {
            this.removeAllItems();
        } catch (Exception excp) {;
        }
    }

    public void setItems() {

        Connection con = null;
        Statement stmt = null;
        
        // execute this query
        try {
            con = getConnection();
            stmt = con.createStatement();
            sqlresult = stmt.executeQuery(getQuery());
            metaData = sqlresult.getMetaData();
        
            while (sqlresult.next()) {
                switch (metaData.getColumnType(1)) {
                    case java.sql.Types.INTEGER:
                        this.addItem(Integer.toString(sqlresult.getInt(1)));
                        break;
                    case java.sql.Types.FLOAT:
                        this.addItem(Float.toString(sqlresult.getFloat(1)));
                        break;
                    case java.sql.Types.DOUBLE:
                        this.addItem(Double.toString(sqlresult.getDouble(1)));
                        break;
                    case java.sql.Types.DATE:
                        this.addItem(sqlresult.getDate(1).toString());
                        break;
                    default:
                        this.addItem(sqlresult.getString(1));
                        break;
                }
            }
            sqlresult.close();
        } catch (SQLException sqle) {
            message("Exception while reading: " + sqle.getMessage().toString() + "\n\tQuery = " + getQuery());
        } finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch ( SQLException e) {
                    logger.log(Level.WARNING,"Exception while closing statement {0}", e.getMessage());
                }
            }
            if ( con != null ) {
                try {
                    con.close();
                } catch ( SQLException e) {
                    logger.log(Level.WARNING,"Exception while closing connection {0}", e.getMessage());
                }
            }
        }
       

    }
}
