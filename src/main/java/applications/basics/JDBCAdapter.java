package applications.basics;

/*
 * @(#)JDBCAdapter.java	1.6 98/02/10
 *
 * Copyright (c) 1997 Sun Microsystems, Inc. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
/**
 * An adaptor, transforming the JDBC interface to the TableModel interface.
 *
 * @version 1.20 09/25/97
 * @author Philip Milne
 */
/**
 * I changed this a few to use it into my Admin-Stuff It is really very
 * helpfull.... Fredy Fischer, Admin Version 1.0, 1999
 */
import java.util.Vector;
import java.sql.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.event.TableModelEvent;

public class JDBCAdapter extends AbstractTableModel {

    Connection connection;
    Statement statement;
    ResultSet resultSet;
    String[] columnNames = {};
    Class[] columnTpyes = {};
    Vector rows = new Vector();
    ResultSetMetaData metaData;
    String SQLError;
    private String query;

    public JDBCAdapter(String host,
            String user, String password, String db) {

        t_connect c = new t_connect(host, user, password, db);
        if (c.getError() != null) {
            System.err.println("Cannot connect to the Database:" + c.getError());
        }
        statement = c.stmt;
        connection = c.con;

    }

    public void executeQuery(String query) {
        setQuery(query);
        if (connection == null || statement == null) {
            System.err.println("There is no database to execute the query.");
            return;
        }
        try {
            resultSet = statement.executeQuery(query);
            metaData = resultSet.getMetaData();

            int numberOfColumns = metaData.getColumnCount();
            columnNames = new String[numberOfColumns];
            // Get the column names and cache them.
            // Then we can close the connection.
            for (int column = 0; column < numberOfColumns; column++) {
                columnNames[column] = metaData.getColumnLabel(column + 1);
            }

            // Get all rows.
            rows = new Vector();
            while (resultSet.next()) {
                Vector newRow = new Vector();
                for (int i = 1; i <= getColumnCount(); i++) {
                    //System.out.println(metaData.getColumnLabel(i) + " = " +metaData.getColumnTypeName(i));
                    if (metaData.getColumnTypeName(i).toUpperCase().startsWith("BLOB")) {
                        newRow.addElement(resultSet.getString(i));
                    } else {
                        //newRow.addElement(resultSet.getObject(i));
                        newRow.addElement(getObject(i));

                    }
                    //newRow.addElement(getObject(i));
                }
                rows.addElement(newRow);
            }
            //  close(); Need to copy the metaData, bug in jdbc:odbc driver.
            fireTableChanged(null); // Tell the listeners a new table has arrived.
        } catch (SQLException ex) {
            System.err.println("Error in executeQuery: " + ex);
            SQLError = ex.toString();
        }
    }

    public void close() throws SQLException {
        //System.out.println("Closing db connection");
        resultSet.close();
        statement.close();
        connection.close();
    }

    public void clear() {
        rows.clear();
    }

    public void refresh() {
        clear();
        executeQuery(getQuery());
    }

     public int getNumRows() {
        return rows.size();
    }

    public String getSQLError() {
        return SQLError;
    }

    /**
     * getting the object from the resultSet, because there seems to be
     * something strange with the resultSet.getObject() of my JDBC-Driver
    *
     */
    public Object getObject(int i) {
        try {

            // the upperCase stuff is something because of Postgres...
            // as well as the startsWith-thing....
            if (metaData.getColumnTypeName(i).toUpperCase().startsWith("INTEGER")) {
                Integer k = resultSet.getInt(i);
                return k;
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("INT")) {
                Integer k = resultSet.getInt(i);
                return k;
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("VARCHAR")) {
                return resultSet.getString(i);
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("VARBINARY")) {
                return resultSet.getString(i);
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("LONGVARBINARY")) {
                return resultSet.getString(i);
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("BINARY")) {
                return resultSet.getString(i);
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("CHAR")) {
                return resultSet.getString(i);
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("LONGVARBINARY")) {
                return resultSet.getString(i);
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("BIT")) {
                resultSet.getBoolean(i);
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("DOUBLE")) {
                Double d = resultSet.getDouble(i);
                return d;
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("FLOAT")) {
                Float f = resultSet.getFloat(i);
                return f;
            } else if (metaData.getColumnTypeName(i).toUpperCase().startsWith("DATE")) {
                return resultSet.getDate(i);
            } else if (metaData.getColumnTypeName(i).toUpperCase().endsWith("TIMESTAMP")) {
                Timestamp f = new Timestamp(System.currentTimeMillis());
                f = (Timestamp) resultSet.getObject(i);
                return f;
            } else if (metaData.getColumnTypeName(i).toUpperCase().endsWith("NUMBER")) {
                Float f = resultSet.getFloat(i);
                return f;
            }

            return resultSet.getObject(i);
        } catch (SQLException e) {
            return null;
        }

    }

    //////////////////////////////////////////////////////////////////////////
    //
    //             Implementation of the TableModel Interface
    //
    //////////////////////////////////////////////////////////////////////////
    // MetaData
    public String getColumnName(int column) {
        if (columnNames[column] != null) {
            return columnNames[column];
        } else {
            return "";
        }
    }

    public Class getColumnClass(int column) {
        int type;
        try {
            type = metaData.getColumnType(column + 1);
        } catch (SQLException e) {
            System.out.println("This is JDBC-Adapter with an Exception in class \ngetColumnClass. Exception is:\n" + e.getMessage());
            return super.getColumnClass(column);
        }

        switch (type) {
            case Types.CHAR:
                return String.class;
            case Types.VARCHAR:
                return String.class;
            case Types.LONGVARCHAR:
                return String.class;

            case Types.BIT:
                return Boolean.class;

            case Types.TINYINT:
                return Integer.class;
            case Types.SMALLINT:
                return Integer.class;
            case Types.INTEGER:
                return Integer.class;

            case Types.BIGINT:
                return Long.class;
            case Types.NUMERIC:
                return Float.class;
            case Types.FLOAT:
                return Float.class;
            case Types.DOUBLE:
                return Double.class;

            case Types.DATE:
                return java.sql.Date.class;

            default:
                return Object.class;
        }
    }

    public boolean isCellEditable(int row, int column) {
        return false;

        /**
         * is always false, in this case I do not want to write.. try {
         *
         * return metaData.isWritable(column+1); } catch (SQLException e) {
         * return false; }
      *
         */
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    // Data methods
    public int getRowCount() {
        return rows.size();
    }

    public Object getValueAt(int aRow, int aColumn) {
        Vector row = (Vector) rows.elementAt(aRow);
        return (Object) row.elementAt(aColumn);
    }

    public String dbRepresentation(int column, Object value) {
        int type;

        if (value == null) {
            return "null";
        }

        try {
            type = metaData.getColumnType(column + 1);
        } catch (SQLException e) {
            System.out.println("This is JDBC-Adapter with an Exception in class \ndbRepresentation. Exception is:\n" + e.getMessage());
            return value.toString();
        }

        switch (type) {
            case Types.INTEGER:
            case Types.DOUBLE:
            case Types.FLOAT:
                return value.toString();
            case Types.BIT:
                return ((Boolean) value).booleanValue() ? "1" : "0";
            case Types.DATE:
                return value.toString(); // This will need some conversion.
            default:
                return "\"" + value.toString() + "\"";
        }

    }

    public void setValueAt(Object value, int row, int column) {
        try {
            String tableName = metaData.getTableName(column + 1);
            // Some of the drivers seem buggy, tableName should not be null.
            if (tableName == null) {
                System.out.println("Table name returned null.");
            }
            String columnName = getColumnName(column);
            String query
                    = "update " + tableName
                    + " set " + columnName + " = " + dbRepresentation(column, value)
                    + " where ";
            // We don't have a model of the schema so we don't know the
            // primary keys or which columns to lock on. To demonstrate
            // that editing is possible, we'll just lock on everything.
            for (int col = 0; col < getColumnCount(); col++) {
                String colName = getColumnName(col);
                if (colName.equals("")) {
                    continue;
                }
                if (col != 0) {
                    query = query + " and ";
                }
                query = query + colName + " = "
                        + dbRepresentation(col, getValueAt(row, col));
            }
            System.out.println(query);
            System.out.println("Not sending update to database");
            //statement.executeQuery(query);
        } catch (SQLException e) {
            //e.printStackTrace();
            System.err.println("Update failed");
        }
        Vector dataRow = (Vector) rows.elementAt(row);
        dataRow.setElementAt(value, column);

    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
