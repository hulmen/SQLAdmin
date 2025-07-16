package sql.fredy.share;

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
 * helpfull.... Fredy Fischer, Admin Version 1.0, 1999 extended 21. Nov. 2002,
 * to be called with a t_connect
 *
 * added the management of NULL-Values
 *
 */
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Vector;
import java.sql.*;
import java.text.SimpleDateFormat;
import javax.swing.table.AbstractTableModel;
import java.util.logging.*;
import sql.fredy.connection.DataSource;

public class JDBCAdapter extends AbstractTableModel {

    /**
     * @return the connection
     */
    public Connection getConnection() {
        if (connection == null) {
            try {
                connection = DataSource.getInstance().getConnection();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "IO Exception while creating connection {0}", ex.getMessage());
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "SQL Exception while creating connection {0}", ex.getMessage());
            } catch (PropertyVetoException ex) {
                logger.log(Level.SEVERE, "Property Veto Exception while creating connection {0}", ex.getMessage());
            }
        }

        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return the statement
     */
    public Statement getStatement() {
        if ( statement == null ) {
            try {
                statement = getConnection().createStatement();
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "SQL Exception while creating statement {0}", ex.getMessage());
            }
        }
        return statement;
    }

    /**
     * @param statement the statement to set
     */
    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    /*
     if you set this value to 1, NULL-values will be returned as a String containing a text "null" , otherwise
     the corresponding objects will be created with a null-value, what means, all numbers will have 0 as value and not NULL
     */
    private static int NULLBEHAVIOUR = 1;
    private String nullText = "null";

    /**
     * @return the NULLBEHAVIOUR
     */
    public static int getNULLBEHAVIOUR() {
        return NULLBEHAVIOUR;
    }

    /**
     * @param aNULLBEHAVIOUR the NULLBEHAVIOUR to set
     */
    public static void setNULLBEHAVIOUR(int aNULLBEHAVIOUR) {
        NULLBEHAVIOUR = aNULLBEHAVIOUR;
    }

    private Logger logger = Logger.getLogger("sql.fredy.share");

    private boolean standalone = false;
    private Connection connection;
    private Statement statement;
    ResultSet resultSet;
    String[] columnNames = {};
    Class[] columnTpyes = {};
    Vector rows = new Vector();
    private ResultSetMetaData metaData;
    String SQLError;

    private int maxRowCount = 10000;

    public void initMaxRowCount() {

        /*
         The value of maximum Rows to be read is set here. If the Value is set zo 0 (Zero) it reads to the end
         The default Value is read from the t_connect.props File where it has been set as default to 10000
         If you provide the environment Variable sql.fredy.sqltools.jdbcadapter.maxRowCount with the value, this value has mor power then the t_connect.props-Value
         
         */
        TConnectProperties props = new TConnectProperties();
        try {
            setMaxRowCount(Integer.parseInt(props.getDefaultMaxRowCount()));
            if (getMaxRowCount() > 0) {
                logger.log(Level.INFO, "reading max. {0} rows with a query", getMaxRowCount());
            } else {
                logger.log(Level.INFO, "now restriction in the number of rows to read");
            }
        } catch (Exception propException) {
            logger.log(Level.INFO, "Error reading properties 'maxRowCount', using standard Values. {0}", propException.getMessage());
        }

        try {
            setMaxRowCount(Integer.parseInt(System.getenv("sql.fredy.sqltools.jdbcadapter.maxRowCount")));
        } catch (NumberFormatException e) {
            logger.log(Level.INFO, "environment variable sql.fredy.sqltools.jdbcadapter.maxRowCount not set, using standard value {0}", Integer.toString(getMaxRowCount()));
            //setMaxRowCount(10000);
        } catch (Exception badException) {
            logger.log(Level.INFO, "environment variable sql.fredy.sqltools.jdbcadapter.maxRowCount not set, using standard value {0}", Integer.toString(getMaxRowCount()));
            //setMaxRowCount(10000);
        }

    }

    public JDBCAdapter(String db) {

        initMaxRowCount();

    }

    public JDBCAdapter(Connection c) {
        connection = c;
        statement = null;
        try {
            statement = connection.createStatement();
        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, "Can not connect to database. " + sqlex.getMessage());
            connection = null;
        }
    }

    public JDBCAdapter(Connection c, boolean standAlone) {
        connection = c;
        statement = null;
        standalone = standAlone;
        try {
            statement = connection.createStatement();
        } catch (SQLException sqlex) {
            logger.log(Level.SEVERE, "Can not connect to database. " + sqlex.getMessage());
            connection = null;
        }
    }

    private String query;

    public void executeQuery(String query) {
        if (getConnection() == null || getStatement() == null) {
            System.err.println("There is no database to execute the query.");
            logger.log(Level.INFO, "There is no connection to a database to execute the query");
            return;
        }

        setQuery(query);
        try {
            resultSet = getStatement().executeQuery(query);
            setMetaData(resultSet.getMetaData());

            int numberOfColumns = getMetaData().getColumnCount();
            columnNames = new String[numberOfColumns];
            // Get the column names and cache them.
            // Then we can close the connection.
            for (int column = 0; column < numberOfColumns; column++) {
                columnNames[column] = getMetaData().getColumnLabel(column + 1);
            }
            logger.log(Level.FINE, "there are " + numberOfColumns + " in this result");

            // Get all rows.
            rows = new Vector();
            int counter = 0;

            while (resultSet.next()) {
                counter++;

                // changed 2014-04-04 by Fredy because of lots of data
                if ((counter > maxRowCount) && (maxRowCount > 0)) {
                    logger.log(Level.WARNING, "Max row count of {0} records exceeded", maxRowCount);
                    break;
                }

                logger.log(Level.FINEST, "looping row: " + counter);
                Vector newRow = new Vector();
                for (int i = 1; i <= getColumnCount(); i++) {
                    try {
                        CellObject cobject = getObject(i);
                        newRow.addElement(cobject.getPrintable());
                        //newRow.addElement(cobject.getObject());

                    } catch (Exception anException) {
                        newRow.addElement((String) " ");
                    }
                }
                rows.addElement(newRow);
            }
            fireTableChanged(null); // Tell the listeners a new table has arrived.
        } catch (SQLException ex) {
            System.err.println("Error in executeQuery: " + ex);
            SQLError = ex.toString();
            // ex.printStackTrace();
        }
    }

    public void close() throws SQLException {
        if (isStandalone()) {
            resultSet.close();
            getStatement().close();
            getConnection().close();
        }
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
    public CellObject getObject(int i) {
        CellObject co = new CellObject();
        boolean wasNull = false;

        try {
            Integer integer;
            String s;

            Object obj = resultSet.getObject(i);
            wasNull = resultSet.wasNull();

            if (wasNull) {
                //logger.log(Level.INFO,"NULL Wert aufgetreten: " + getMetaData().getColumnName(i));
                co.setPrintable(nullText);
                return co;
            }

            switch (getMetaData().getColumnType(i)) {
                case Types.CHAR:

                    co.setType(Types.CHAR);
                    co.setTypeText("CHAR");
                    co.setJavaType("String");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        co.setObject(resultSet.getString(i));
                        co.setPrintable(resultSet.getString(i));
                    }

                    break;
                case Types.VARCHAR:

                    co.setType(Types.VARCHAR);
                    co.setTypeText("VARCHAR");
                    co.setJavaType("String");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        co.setObject(resultSet.getString(i));
                        co.setPrintable(resultSet.getString(i));
                    }

                    break;
                case Types.LONGVARCHAR:

                    co.setType(Types.LONGVARCHAR);
                    co.setTypeText("LONGVARCHAR");
                    co.setJavaType("String");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        co.setObject(resultSet.getString(i));
                        co.setPrintable(resultSet.getString(i));
                    }

                    break;

                case Types.BIT:

                    co.setType(Types.BIT);
                    co.setTypeText("BIT");
                    co.setJavaType("boolean");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        boolean b = resultSet.getBoolean(i);
                        co.setObject(b);
                        if (b) {
                            co.setPrintable("true");
                        } else {
                            co.setPrintable("false");
                        }
                    }
                case Types.BOOLEAN:

                    co.setType(Types.BOOLEAN);
                    co.setTypeText("BOOLEAN");
                    co.setJavaType("boolean");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        boolean b = resultSet.getBoolean(i);
                        co.setObject(b);
                        if (b) {
                            co.setPrintable("true");
                        } else {
                            co.setPrintable("false");
                        }
                    }

                    break;
                case Types.TINYINT:

                    co.setType(Types.TINYINT);
                    co.setTypeText("TINYINT");
                    co.setJavaType("Integer");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        integer = resultSet.getInt(i);
                        co.setObject(integer);
                        co.setPrintable(integer.toString());
                    }

                    break;
                case Types.SMALLINT:

                    co.setType(Types.SMALLINT);
                    co.setTypeText("SMALLINT");
                    co.setJavaType("Integer");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        integer = resultSet.getInt(i);
                        co.setObject(integer);
                        co.setPrintable(integer.toString());
                    }

                    break;
                case Types.INTEGER:

                    co.setType(Types.INTEGER);
                    co.setTypeText("INTEGER");
                    co.setJavaType("Integer");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        integer = resultSet.getInt(i);
                        co.setObject(integer);
                        co.setPrintable(integer.toString());
                    }

                    break;
                case Types.BIGINT:

                    co.setType(Types.BIGINT);
                    co.setTypeText("BIGINT");
                    co.setJavaType("Long");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        Long lnog = resultSet.getLong(i);
                        co.setObject(lnog);
                        co.setPrintable(String.format("%d", lnog));
                    }

                    break;
                case Types.NUMERIC:

                    co.setType(Types.NUMERIC);
                    co.setTypeText("NUMERIC");
                    co.setJavaType("BigDecimal");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        BigDecimal bd = resultSet.getBigDecimal(i);
                        co.setObject(bd);
                        co.setPrintable(String.format("%f", bd));
                    }

                    break;
                case Types.DECIMAL:

                    co.setType(Types.DECIMAL);
                    co.setTypeText("DECIMAL");
                    co.setJavaType("Float");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        Float flo = resultSet.getFloat(i);
                        co.setObject(flo);
                        co.setPrintable(String.format("%f", flo));
                    }

                    break;
                case Types.FLOAT:

                    co.setType(Types.FLOAT);
                    co.setTypeText("FLOAT");
                    co.setJavaType("Float");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        Float flo2 = resultSet.getFloat(i);
                        co.setObject(flo2);
                        co.setPrintable(String.format("%f", flo2));
                    }

                    break;
                case Types.DOUBLE:

                    co.setType(Types.DOUBLE);
                    co.setTypeText("DOUBLE");
                    co.setJavaType("Double");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        Double dob = resultSet.getDouble(i);
                        co.setObject(dob);
                        co.setPrintable(String.format("%f", dob));
                    }

                    break;
                case Types.REAL:

                    co.setType(Types.REAL);
                    co.setTypeText("REAL");
                    co.setJavaType("Float");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        Float flo3 =  resultSet.getFloat(i);
                        co.setObject(flo3);
                        co.setPrintable(String.format("%f", flo3));
                    }

                    break;
                case Types.DATE:

                    co.setType(Types.DATE);
                    co.setTypeText("DATE");
                    co.setJavaType("Date");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date dt = resultSet.getDate(i);
                        co.setObject(dt);
                        co.setPrintable(sdf.format(dt));
                    }

                    break;
                case Types.TIMESTAMP:

                    co.setType(Types.TIMESTAMP);
                    co.setTypeText("TIMESTAMP");
                    co.setJavaType("java.sql.Timestamp");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        SimpleDateFormat sdg = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                        Timestamp ts = resultSet.getTimestamp(i);
                        co.setObject(ts);
                        co.setPrintable(sdg.format(new Date(ts.getTime())));
                    }

                    break;
                case Types.TIME:

                    co.setType(Types.TIME);
                    co.setTypeText("TIME");
                    co.setJavaType("java.sql.Time");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        SimpleDateFormat sdt = new SimpleDateFormat("HH:mm:ss:SSS");
                        Time tt = resultSet.getTime(i);
                        co.setObject(tt);
                        co.setPrintable(sdt.format(new Date(tt.getTime())));
                    }

                    break;
                default:

                    co.setType(getMetaData().getColumnType(i));
                    co.setTypeText("unknown");
                    co.setJavaType("String");

                    if (wasNull) {
                        co.setObject(null);
                        co.setPrintable(nullText);
                    } else {
                        co.setObject(resultSet.getString(i));
                        co.setPrintable(resultSet.getString(i));
                    }

                    break;
            }

            // the upperCase stuff is something because of Postgres...
            // as well as the startsWith-thing....
            //System.out.println(metaData.getColumnTypeName(i).toUpperCase());
            /*
            if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("INTEGER")) {
                Integer k = new Integer(resultSet.getInt(i));
                return k;
            }
            if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("INT")) {
                Integer k = new Integer(resultSet.getInt(i));
                return k;
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("VARCHAR")) {
                return resultSet.getString(i);
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("VARBINARY")) {
                return resultSet.getString(i);
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("LONGVARBINARY")) {
                return resultSet.getString(i);
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("BINARY")) {
                return resultSet.getString(i);
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("CHAR")) {
                return resultSet.getString(i);
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("LONGVARBINARY")) {
                return resultSet.getString(i);
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("BIT")) {
                resultSet.getBoolean(i);
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("DOUBLE")) {
                Double d = new Double(resultSet.getDouble(i));
                return d;
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("FLOAT")) {
                Float f = new Float(resultSet.getFloat(i));
                return f;
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("DATETIME")) {
                try {
                    Timestamp f = new Timestamp(System.currentTimeMillis());
                    f = (Timestamp) resultSet.getObject(i);
                    return f;
                } catch (ClassCastException cce) {
                    return resultSet.getString(i);
                    //return null;
                }
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("DATE")) {
                return resultSet.getDate(i);
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().endsWith("TIMESTAMP")) {
                try {
                    Timestamp f = new Timestamp(System.currentTimeMillis());
                    f = (Timestamp) resultSet.getObject(i);
                    return f;
                } catch (ClassCastException cce) {
                    return resultSet.getString(i);
                    //return null;
                }
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().endsWith("NUMBER")) {
                Float f = new Float(resultSet.getFloat(i));
                //  { NUMBER f= new  NUMBER((NUMBER)resultSet.getObject(i));
                return f;
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().endsWith("NUMERIC")) {
                return resultSet.getBigDecimal(i);
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().endsWith("DECIMAL")) {
                return resultSet.getBigDecimal(i);
            } else if (getMetaData().getColumnTypeName(i).toUpperCase().startsWith("BLOB")) {
                return resultSet.getString(i);
            }

            // as of 2016-03-02 by Fredy
            //return resultSet.getObject(i);
            return resultSet.getObject(i).toString();
             */
        } catch (SQLException e) {
            co = new CellObject();
            return co;
        }

        return co;

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
        //return String.class;

        int type;
        try {
            type = getMetaData().getColumnType(column + 1);
        } catch (Exception e) {
            // System.err.println("This is JDBC-Adapter with a SQLException in class \ngetColumnClass. Exception is:\n"+e.getMessage() + "\n\n");
            //e.printStackTrace();

            return super.getColumnClass(column);
        }

        switch (type) {
            case Types.ARRAY:
                return Array.class;
            case Types.BIGINT:
                return long.class;
            case Types.BINARY:
                return byte.class;
            case Types.BIT:
                return boolean.class;
            case Types.BLOB:
                return byte.class;
            case Types.BOOLEAN:
                return boolean.class;
            case Types.CHAR:
                return String.class;
            case Types.CLOB:
                return String.class;
            case Types.DATALINK:
                return Object.class;
            case Types.DATE:
                return java.sql.Date.class;
            case Types.DECIMAL:
                return java.math.BigDecimal.class;
            case Types.DISTINCT:
                return Object.class;
            case Types.DOUBLE:
                return double.class;
            case Types.FLOAT:
                return float.class;
            case Types.INTEGER:
                return int.class;
            case Types.JAVA_OBJECT:
                return Object.class;
            case Types.LONGNVARCHAR:
                return String.class;
            case Types.LONGVARBINARY:
                return Object.class;
            case Types.LONGVARCHAR:
                return String.class;
            case Types.NCHAR:
                return String.class;
            case Types.NCLOB:
                return Object.class;
            case Types.NULL:
                return Object.class;
            case Types.NUMERIC:
                return java.math.BigDecimal.class;
            case Types.NVARCHAR:
                return String.class;
            case Types.OTHER:
                return String.class;
            case Types.REAL:
                return float.class;
            case Types.REF:
                return Object.class;
            case Types.REF_CURSOR:
                return Object.class;
            case Types.ROWID:
                return Object.class;
            case Types.SMALLINT:
                return int.class;
            case Types.SQLXML:
                return String.class;
            case Types.STRUCT:
                return Object.class;
            case Types.TIME:
                return java.sql.Time.class;
            case Types.TIMESTAMP:
                return java.sql.Timestamp.class;
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return java.sql.Timestamp.class;
            case Types.TIME_WITH_TIMEZONE:
                return java.sql.Timestamp.class;
            case Types.TINYINT:
                return int.class;
            case Types.VARBINARY:
                return String.class;
            case Types.VARCHAR:
                return String.class;
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
            type = getMetaData().getColumnType(column + 1);
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
            String tableName = getMetaData().getTableName(column + 1);
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
        try {
            Vector dataRow = (Vector) rows.elementAt(row);
            dataRow.setElementAt(value, column);
        } catch (Exception e) {

            // change per 27. July 2015
            Vector dataRow = (Vector) rows.elementAt(row);
            dataRow.setElementAt(null, column);
        }

    }

    /**
     * @return the maxRowCount
     */
    public int getMaxRowCount() {
        return maxRowCount;
    }

    /**
     * @param maxRowCount the maxRowCount to set
     */
    public void setMaxRowCount(int maxRowCount) {
        this.maxRowCount = maxRowCount;
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * @return the nullText
     */
    public String getNullText() {
        return nullText;
    }

    /**
     * @param nullText the nullText to set
     */
    public void setNullText(String nullText) {
        this.nullText = nullText;
    }

    /**
     * @return the metaData
     */
    public ResultSetMetaData getMetaData() {
        return metaData;
    }

    /**
     * @param metaData the metaData to set
     */
    public void setMetaData(ResultSetMetaData metaData) {
        this.metaData = metaData;
    }

    /**
     * @return the standalone
     */
    public boolean isStandalone() {
        return standalone;
    }

    /**
     * @param standalone the standalone to set
     */
    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }
}
