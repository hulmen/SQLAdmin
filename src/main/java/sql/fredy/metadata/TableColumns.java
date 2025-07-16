package sql.fredy.metadata;

/**
 * Tables lists the tables found in the Database. It is very often used inside
 * Admin.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
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
 *
 */
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.*;
import java.util.logging.*;
import java.sql.*;
import java.util.logging.Level;
import sql.fredy.connection.DataSource;

public class TableColumns {

// Fredy's make Version
    private static String fredysVersion = "Version 2.0   23. Dec. 2020";

    public String getVersion() {
        return fredysVersion;
    }

    private Logger logger = Logger.getLogger("sql.fredy.metadata");
    
    
    Vector allCols;
    Vector allNames;
    Vector columnInfo;
    String Table;

    /**
     * Get the value of Table.
     *
     * @return Value of Table.
     */
    public String getTable() {
        return Table;
    }

    /**
     * Set the value of Table.
     *
     * @param v Value to assign to Table.
     */
    public void setTable(String v) {
        this.Table = v;
    }

    /**
     * Get the value of allCols.
     *
     * @return Value of allCols.
     */
    public Vector getAllCols() {
        return allCols;
    }

    /**
     * Get the value of columnInfo.
     *
     * @return Value of columnInfo.
     */
    public Vector getColumnInfo() {
        return columnInfo;
    }

    /**
     * Set the value of columnInfo.
     *
     * @param v Value to assign to columnInfo.
     */
    public void setColumnInfo(Vector v) {
        this.columnInfo = v;
    }

    /**
     * get the all Column Names.
     *
     * @return Value of allNames.
     */
    public Vector getAllNames() {
        return allNames;
    }

 
    private String database;

    /**
     * Get the value of database.
     *
     * @return Value of database.
     */
    public String getDatabase() {
        return database;
    }

    /**
     * Set the value of database.
     *
     * @param v Value to assign to database.
     */
    public void setDatabase(String v) {
        this.database = v;
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

    public SingleColumnInfo getColumn(int columnNo) {

        try {
            return (SingleColumnInfo) columnInfo.elementAt(columnNo);
        } catch (ArrayIndexOutOfBoundsException aiob) {
            System.out.println("\nClass TableColumn, Method getColumn(" + columnNo + ") Error: " + aiob);
            return null;
        }

    }

    public Object getColumnInfo(int columnNo, int columnDescription) {

        SingleColumnInfo sci = new SingleColumnInfo();
        sci = getColumn(columnNo);
        Vector v = new Vector();
        v = sci.getDataVector();
        return v.elementAt(columnDescription);

    }

    public int getDecDigits(int columnNo) {

        SingleColumnInfo sci = new SingleColumnInfo();
        sci = getColumn(columnNo);
        return sci.getDecimal_digits();

    }

    int NumberOfColumns;

    /**
     * Get the value of NumberOfColumns.
     *
     * @return Value of NumberOfColumns.
     */
    public int getNumberOfColumns() {
        return NumberOfColumns;
    }

    /**
     * Set the value of NumberOfColumns.
     *
     * @param v Value to assign to NumberOfColumns.
     */
    public void setNumberOfColumns(int v) {
        this.NumberOfColumns = v;
    }
  

    public TableColumns(String table) {        
        setStandAlone(false);
        setTable(table);

        inits();
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
    
    private void inits() {

        allCols = new Vector();
        allNames = new Vector();
        columnInfo = new Vector();
        Connection con = null;
        
            try {
                DatabaseMetaData md = getConnection().getMetaData();
                ResultSet cols = md.getColumns(null, null, getTable(), "%");
                int i = 0;
                while (cols.next()) {

                    Columns col = new Columns(i, cols.getString(4), cols.getString(5), cols.getInt(7), cols.getInt(9), getTable());
                    allCols.addElement(col);
                    allNames.addElement(cols.getString(4));

                    try {
                        // doing complete ColumnInfo
                        Vector colTemp = new Vector();
                        colTemp.addElement((String)  cols.getString(1));
                        colTemp.addElement((String)  cols.getString(2));
                        colTemp.addElement((String)  cols.getString(3));
                        colTemp.addElement((String)  cols.getString(4));
                        colTemp.addElement((Short)   cols.getShort(5));
                        colTemp.addElement((String)  cols.getString(6));
                        colTemp.addElement((Integer) cols.getInt(7));
                        colTemp.addElement((Integer) 0);
                        colTemp.addElement((Integer) cols.getInt(9));
                        colTemp.addElement((Integer) cols.getInt(10));
                        colTemp.addElement((Integer) cols.getInt(11));
                        colTemp.addElement((String)  cols.getString(12));
                        colTemp.addElement((String)  cols.getString(13));
                        colTemp.addElement((Integer) cols.getInt(14));
                        colTemp.addElement((Integer) cols.getInt(15));
                        colTemp.addElement((Integer) cols.getInt(16));
                        colTemp.addElement((Integer) cols.getInt(17));
                        colTemp.addElement((String)  cols.getString(18));
                        SingleColumnInfo sctt = new SingleColumnInfo(colTemp);                        
                        columnInfo.addElement(sctt);
                    } catch (Exception excp1) {
                        System.out.println("Exception in TableColumns, Table: " + cols.getString(4));
                        excp1.printStackTrace();
                    }

                    i++;
                }
                setNumberOfColumns(i - 1);
            } catch (SQLException exception) {
                System.out.println("TableColumns: " + exception.getMessage().toString());
            } finally {
                if ( con != null ) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING,"Exception whil closing connection {0}",e.getMessage());
                    }
                }
            }

        
    }

}
