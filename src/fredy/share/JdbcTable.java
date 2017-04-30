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
  this is a dynamic JTable created out of a SQL-Query 
  it can by called by  different constructors:
  - by a javax.sql.Connection
  - by a sql.fredy.share.t_connect
  - by hostname, username, userpassword and db so it will create a connecitn based of sql.fredy.share.t_connect
  - by a JDBC-Driver and the connection URL
 
 once instatiated without any error, you call the methode excuteQuery(String sqlquery) it will create the JTable and fill it with
 the result of the query
  
 to display this query add the JTable to a JPanel like

JFrame frame = new JFrame("My Query Result");
frame.getContentPane.setLayout(new BorderLayout());

JdbcTable table = new JdbcTable(connection);
table.executeQuery("select * from employee");

frame.getContentPane().add(BorderLayout.CENTER,new JScrollPane(table)); 
frame.pack();
frame.setVisible(true);


 */
package sql.fredy.share;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author fredy
 */
public class JdbcTable extends AbstractTableModel {

    private Connection con = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private Logger logger = Logger.getLogger("sql.fredy.share");
    private boolean connectionEstablished;
    private String[] columnNames;
    private String[] fieldNames;
    private int[] columnTypes;
    private ResultSetMetaData rsmd;
    private Vector rows;
    private int counter;
    private boolean standalone = false;
    private String SQLError;
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdt;
    private int maxRows = 10000;
    private String nullTxt;
    private boolean useNullTxt = true;

    /*
     instantiate JdbcTable with a java.sql.Connection
     */
    public JdbcTable(Connection con) {
        setNullTxt("NULL");
        setCon(con);
        setCounter(0);
        try {
            setStmt(con.createStatement());
        } catch (SQLException sqlex) {
            logSqlException("JdbcTable(Connection con)", Level.WARNING, sqlex);
            setConnectionEstablished(false);
        }
    }

    /*
      instantiate JdbcTable with a sql.fredy.share.t_connext Object
     */
    public JdbcTable(t_connect tcon) {
        setNullTxt("NULL");
        setCounter(0);
        setCon(tcon.getCon());
        setStmt(tcon.getStmt());
        setConnectionEstablished(true);
    }

    /*
      instantiate JdbcTable via t_connect but instantiate t_connect with host, user, password and the db name
      
     */
    public JdbcTable(String host, String user, String password, String db) {
        setNullTxt("NULL");
        setCounter(0);
        t_connect tcon = new t_connect(host, user, password, db);
        setCon(tcon.getCon());
        setStmt(tcon.getStmt());
        setConnectionEstablished(true);
    }

    /*
      instantiate JdbcTable by a JDBC-Driver and an URL
     */
    public JdbcTable(String jdbcDriver, String jdbcUrl) {
        setNullTxt("NULL");
        setCounter(0);
        try {
            Class.forName(jdbcDriver);
            setCon(DriverManager.getConnection(jdbcUrl));
            setConnectionEstablished(true);
        } catch (ClassNotFoundException ex) {
            logException("JdbcTable(String jdbcDriver, String jdbcUrl)", Level.SEVERE, "can not load drvier class " + ex.getMessage());
            setConnectionEstablished(false);
        } catch (SQLException sqlex) {
            logSqlException("JdbcTable(Connection con)", Level.WARNING, sqlex);
            setConnectionEstablished(false);
        }
    }

    public int getColumnType(int col) {
        return columnTypes[col];
    }

    /*
      close the Statement and the connection, use with care not to close your connection by fault
 
     */
    public void close() {
        logger.log(Level.INFO, "Closing connection");
        try {
            if (stmt != null) {
                stmt.close();
            }
            if (con != null) {
                con.close();
            }
        } catch (SQLException sqlex) {
            logSqlException("close() ", Level.WARNING, sqlex);
        }
    }

    public TableRowSorter<TableModel> getRowSorter() {
        TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(this);

        for (int i = 0; i < getColumnCount(); i++) {
            int type = columnTypes[i];
            switch (type) {
                case Types.ARRAY:
                    //newRow.add(rs.getArray(i));
                    break;
                case Types.BIGINT:
                    //newRow.add((Long) rs.getLong(i));
                    try {
                        rowSorter.setComparator(i, new Comparator<Long>() {
                            @Override
                            public int compare(Long o1, Long o2) {
                                return o1.compareTo(o2);
                            }
                        });
                    } catch (Exception e) {
                        rowSorter = new TableRowSorter<TableModel>(this);
                    }
                    break;
                case Types.BINARY:
                    //newRow.add((String) "BINARY not supported");
                    break;
                case Types.BIT:
                    //newRow.add(rs.getBoolean(i));
                    break;
                case Types.BLOB:
                    //newRow.add((String) "BLOB not supported");
                    break;
                case Types.BOOLEAN:
                    //newRow.add(rs.getBoolean(i));
                    break;
                case Types.CHAR:
                    //newRow.add((String) rs.getString(i));
                    break;
                case Types.CLOB:
                    //newRow.add((String) "CLOB not supported");
                    break;
                case Types.DATALINK:
                    //newRow.add((String) "DATALINK not supported");
                    break;
                case Types.DATE:
                    //newRow.add((java.sql.Date) rs.getDate(i));
                    try {
                        rowSorter.setComparator(i, new Comparator<Date>() {
                            @Override
                            public int compare(Date o1, Date o2) {
                                if (o1.before(o2)) {
                                    return -1;
                                }
                                if (o1.equals(o2)) {
                                    return 0;
                                }
                                if (o1.after(o2)) {
                                    return 1;
                                }
                                return 0;
                            }
                        });
                    } catch (Exception e) {
                        rowSorter = new TableRowSorter<TableModel>(this);
                    }
                    break;
                case Types.DECIMAL:
                    //newRow.add((java.math.BigDecimal) rs.getBigDecimal(i));
                    try {
                        rowSorter.setComparator(i, new Comparator<BigDecimal>() {
                            @Override
                            public int compare(BigDecimal o1, BigDecimal o2) {
                                return o1.compareTo(o2);
                            }
                        });
                    } catch (Exception e) {
                        rowSorter = new TableRowSorter<TableModel>(this);
                    }
                    break;
                case Types.DISTINCT:
                    //newRow.add((String) "DISTINCT not supported");
                    break;
                case Types.DOUBLE:
                    //newRow.add((Double) rs.getDouble(i));
                    try {
                        rowSorter.setComparator(i, new Comparator<Double>() {
                            @Override
                            public int compare(Double o1, Double o2) {
                                return o1.compareTo(o2);
                            }
                        });
                    } catch (Exception e) {
                        rowSorter = new TableRowSorter<TableModel>(this);
                    }
                    break;
                case Types.FLOAT:
                    //newRow.add((Float) rs.getFloat(i));
                    try {
                        rowSorter.setComparator(i, new Comparator<Float>() {
                            @Override
                            public int compare(Float o1, Float o2) {
                                return o1.compareTo(o2);
                            }
                        });
                    } catch (Exception e) {
                        rowSorter = new TableRowSorter<TableModel>(this);
                    }
                    break;
                case Types.INTEGER:
                    //newRow.add((Integer) rs.getInt(i));
                    try {
                        rowSorter.setComparator(i, new Comparator<Integer>() {
                            @Override
                            public int compare(Integer o1, Integer o2) {
                                return o1.compareTo(o2);
                            }
                        });
                    } catch (Exception e) {
                        rowSorter = new TableRowSorter<TableModel>(this);
                    }
                    break;
                case Types.JAVA_OBJECT:
                    //newRow.add((String) "JAVA_OBJECT not supported");
                    break;
                case Types.LONGNVARCHAR:
                    //newRow.add((String) rs.getString(i));
                    break;
                case Types.LONGVARBINARY:
                    //newRow.add((String) "LONGVARBINARY not supported");
                    break;
                case Types.LONGVARCHAR:
                    //newRow.add((String) rs.getString(i));
                    break;
                case Types.NCHAR:
                    //newRow.add((String) rs.getString(i));
                    break;
                case Types.NCLOB:
                    //newRow.add((String) "NCLOB not supported");
                    break;
                case Types.NULL:
                    //newRow.add((String) "NULL Type not supported");
                    break;
                case Types.NUMERIC:
                    //newRow.add((java.math.BigDecimal) rs.getBigDecimal(i));
                    break;
                case Types.NVARCHAR:
                    //newRow.add((String) rs.getString(i));
                    break;
                case Types.OTHER:
                    //newRow.add((String) "OTHER Type not supported");
                    break;
                case Types.REAL:
                    //newRow.add((Float) rs.getFloat(i));
                    break;
                case Types.REF:
                    //newRow.add((String) "REF Type not supported");
                    break;
                case Types.REF_CURSOR:
                    //newRow.add((String) "REF_CURSOR Type not supported");
                    break;
                case Types.ROWID:
                    //newRow.add((String) "ROWID Type not supported");
                    break;
                case Types.SMALLINT:
                    //newRow.add((Integer) rs.getInt(i));
                    break;
                case Types.SQLXML:
                    //newRow.add((String) rs.getString(i));
                    break;
                case Types.STRUCT:
                    //newRow.add((String) "STRUCT Type not supported");
                    break;
                case Types.TIME:
                    //newRow.add((java.sql.Time) rs.getTime(i));
                    try {
                        rowSorter.setComparator(i, new Comparator<java.sql.Time>() {
                            @Override
                            public int compare(java.sql.Time o1, java.sql.Time o2) {
                                if (o1.before(o2)) {
                                    return -1;
                                }
                                if (o1.equals(o2)) {
                                    return 0;
                                }
                                if (o1.after(o2)) {
                                    return 1;
                                }
                                return 0;
                            }
                        });
                    } catch (Exception e) {
                        rowSorter = new TableRowSorter<TableModel>(this);
                    }
                    break;
                case Types.TIMESTAMP:
                    //newRow.add((java.sql.Timestamp) rs.getTimestamp(i));
                    try {
                        rowSorter.setComparator(i, new Comparator<Timestamp>() {
                            @Override
                            public int compare(Timestamp o1, Timestamp o2) {
                                if (o1.before(o2)) {
                                    return -1;
                                }
                                if (o1.equals(o2)) {
                                    return 0;
                                }
                                if (o1.after(o2)) {
                                    return 1;
                                }
                                return 0;
                            }

                        });
                    } catch (Exception e) {
                        // might come from a possible null value within a result
                    }
                    break;
                case Types.TIMESTAMP_WITH_TIMEZONE:
                    //newRow.add((java.sql.Timestamp) rs.getTimestamp(i));
                    try {
                        rowSorter.setComparator(i, new Comparator<Timestamp>() {
                            @Override
                            public int compare(Timestamp o1, Timestamp o2) {
                                if (o1.before(o2)) {
                                    return -1;
                                }
                                if (o1.equals(o2)) {
                                    return 0;
                                }
                                if (o1.after(o2)) {
                                    return 1;
                                }
                                return 0;
                            }

                        });
                    } catch (Exception e) {
                        rowSorter = new TableRowSorter<TableModel>(this);
                    }
                    break;
                case Types.TIME_WITH_TIMEZONE:
                    //newRow.add((java.sql.Time) rs.getTime(i));
                    try {
                        rowSorter.setComparator(i, new Comparator<Time>() {
                            @Override
                            public int compare(Time o1, Time o2) {
                                if (o1.before(o2)) {
                                    return -1;
                                }
                                if (o1.equals(o2)) {
                                    return 0;
                                }
                                if (o1.after(o2)) {
                                    return 1;
                                }
                                return 0;
                            }
                        });
                    } catch (Exception e) {
                        rowSorter = new TableRowSorter<TableModel>(this);
                    }
                    break;
                case Types.TINYINT:
                    //newRow.add((Integer) rs.getInt(i));
                    break;
                case Types.VARBINARY:
                    //newRow.add((String) "VARBINARY Type not supported");
                    break;
                case Types.VARCHAR:
                    //newRow.add((String) rs.getString(i));
                    break;
                default:
                    //newRow.add((String) rs.getString(i));
                    break;
            }
        }
        return rowSorter;
    }

    /*
      execute the query if a connection exists
      @param query the SQL statement to be run
     */
    public boolean executeQuery(String query) {
        if (!isConnectionEstablished()) {
            return false;
        }

        // max Row Count
        TConnectProperties props = new TConnectProperties();
        setMaxRows(Integer.parseInt(props.getDefaultMaxRowCount()));

        //sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        //sdt = new SimpleDateFormat("HH:mm:ss.SSS");
        sdf = (SimpleDateFormat) DateFormat.getDateTimeInstance();
        sdt = (SimpleDateFormat) DateFormat.getTimeInstance();

        Timestamp timestamp = null;
        Time time = null;
        java.sql.Date date = null;

        try {
            // we send the query to the DB
            rs = getStmt().executeQuery(query);

            // then we need the ResultSetMetaData
            setRsmd(rs.getMetaData());

            // we loop the result and add each row to the Vector
            rows = new Vector();
            setCounter(0);
            while (rs.next()) {

                // this is just one Line
                Vector newRow = new Vector();

                // thanks to the ResultSetMetaData, we now the number of columns
                for (int i = 1; i <= getColumnCount(); i++) {

                    // Depending on the Type, we add every single value
                    int type = columnTypes[i - 1];
                    switch (type) {
                        case Types.ARRAY:
                            newRow.add(rs.getArray(i));
                            break;
                        case Types.BIGINT:
                            newRow.add((Long) rs.getLong(i));
                            break;
                        case Types.BINARY:
                            newRow.add((String) "BINARY not supported");
                            break;
                        case Types.BIT:
                            newRow.add(rs.getBoolean(i));
                            break;
                        case Types.BLOB:
                            newRow.add((String) "BLOB not supported");
                            break;
                        case Types.BOOLEAN:
                            newRow.add(rs.getBoolean(i));
                            break;
                        case Types.CHAR:
                            newRow.add((String) rs.getString(i));
                            break;
                        case Types.CLOB:
                            newRow.add((String) "CLOB not supported");
                            break;
                        case Types.DATALINK:
                            newRow.add((String) "DATALINK not supported");
                            break;
                        case Types.DATE:
                            newRow.add((java.sql.Date) rs.getDate(i));
                            break;
                        case Types.DECIMAL:
                            newRow.add((java.math.BigDecimal) rs.getBigDecimal(i));
                            break;
                        case Types.DISTINCT:
                            newRow.add((String) "DISTINCT not supported");
                            break;
                        case Types.DOUBLE:
                            newRow.add((Double) rs.getDouble(i));
                            break;
                        case Types.FLOAT:
                            newRow.add((Float) rs.getFloat(i));
                            break;
                        case Types.INTEGER:
                            newRow.add((Integer) rs.getInt(i));
                            break;
                        case Types.JAVA_OBJECT:
                            newRow.add((String) "JAVA_OBJECT not supported");
                            break;
                        case Types.LONGNVARCHAR:
                            newRow.add((String) rs.getString(i));
                            break;
                        case Types.LONGVARBINARY:
                            newRow.add((String) "LONGVARBINARY not supported");
                            break;
                        case Types.LONGVARCHAR:
                            newRow.add((String) rs.getString(i));
                            break;
                        case Types.NCHAR:
                            newRow.add((String) rs.getString(i));
                            break;
                        case Types.NCLOB:
                            newRow.add((String) "NCLOB not supported");
                            break;
                        case Types.NULL:
                            newRow.add((String) "NULL Type not supported");
                            break;
                        case Types.NUMERIC:
                            newRow.add((java.math.BigDecimal) rs.getBigDecimal(i));
                            break;
                        case Types.NVARCHAR:
                            newRow.add((String) rs.getString(i));
                            break;
                        case Types.OTHER:
                            newRow.add((String) "OTHER Type not supported");
                            break;
                        case Types.REAL:
                            newRow.add((Float) rs.getFloat(i));
                            break;
                        case Types.REF:
                            newRow.add((String) "REF Type not supported");
                            break;
                        case Types.REF_CURSOR:
                            newRow.add((String) "REF_CURSOR Type not supported");
                            break;
                        case Types.ROWID:
                            newRow.add((String) "ROWID Type not supported");
                            break;
                        case Types.SMALLINT:
                            newRow.add((Integer) rs.getInt(i));
                            break;
                        case Types.SQLXML:
                            newRow.add((String) rs.getString(i));
                            break;
                        case Types.STRUCT:
                            newRow.add((String) "STRUCT Type not supported");
                            break;
                        case Types.TIME:
                            //newRow.add((java.sql.Time) rs.getTime(i));
                            time = rs.getTime(i);
                            if (time != null) {
                                newRow.add(time);
                                //newRow.add((String) sdt.format(time.getTime()));
                            } else {
                                newRow.add((String) getNullTxt());
                            }
                            break;
                        case Types.TIMESTAMP:
                            newRow.add((java.sql.Timestamp) rs.getTimestamp(i));

                            // because JTable does format Timestmap as Date
                            /*
                            timestamp = rs.getTimestamp(i);
                            if (timestamp != null) {
                                newRow.add((String) sdf.format(timestamp.getTime()));
                            } else {
                                newRow.add((String) getNullTxt());
                            }
                             */
                            break;
                        case Types.TIMESTAMP_WITH_TIMEZONE:
                            //newRow.add((java.sql.Timestamp) rs.getTimestamp(i));
                            timestamp = rs.getTimestamp(i);
                            if (timestamp != null) {
                                newRow.add((String) sdf.format(timestamp.getTime()));
                            } else {
                                newRow.add((String) getNullTxt());
                            }
                            break;
                        case Types.TIME_WITH_TIMEZONE:
                            //newRow.add((java.sql.Time) rs.getTime(i));
                            time = rs.getTime(i);
                            if (time != null) {
                                newRow.add(time);
                                //newRow.add((String) sdt.format(time.getTime()));
                            } else {
                                newRow.add((String) getNullTxt());
                            }
                            break;
                        case Types.TINYINT:
                            newRow.add((Integer) rs.getInt(i));
                            break;
                        case Types.VARBINARY:
                            newRow.add((String) "VARBINARY Type not supported");
                            break;
                        case Types.VARCHAR:
                            newRow.add((String) rs.getString(i));
                            break;
                        default:
                            newRow.add((String) rs.getString(i));
                            break;
                    }

                    if (rs.wasNull()) {
                        newRow.remove(newRow.size() - 1);
                        if ((getColumnClass(newRow.size() - 1) != String.class) || (!isUseNullTxt())) {
                            newRow.add(null);
                        } else {
                            newRow.add(getNullTxt());
                        }
                    }

                }
                rows.add(newRow);

                /*
                   We limit the numbers of rows read to the value of maxRows.
                   This value is by default set to 10'000 if you set it to 0, 
                   there will be no limit.
                 */
                counter++;
                if ((getMaxRows() > 0) && (counter == getMaxRows())) {
                    break;
                }

            }

        } catch (SQLException sqlex) {
            logSqlException("executeQuery()", Level.WARNING, sqlex);
            return false;
        }

        return true;
    }

    /*
       log a  SQLexception by providing the methode and the Exception
     */
    private void logSqlException(String methode, Level level, SQLException sqlex) {
        logger.log(level, "SQL Exception in {0} : {1} SQLState: {2} SQL Error Code: {3}", new Object[]{methode, sqlex.getMessage(), sqlex.getSQLState(), sqlex.getErrorCode()});
        setSQLError("\tSQL Exception: " + sqlex.getMessage() + "\n\tSQL State:     " + sqlex.getSQLState() + "\n\tError-Code:    " + sqlex.getErrorCode() + "\n");
        //sqlex.printStackTrace();
    }

    /*
       log an exception by providing the methode and the Exception
     */
    private void logException(String methode, Level level, String message) {
        logger.log(level, "Exception in {0} :  Message: {1} ", new Object[]{methode, message});
    }

    @Override
    public int getRowCount() {
        return rows.size();
    }

    public int getNumRows() {
        return getRowCount();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

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
            type = getColumnType(column);
        } catch (Exception e) {
            if (column >= 0) {
                logException("getColumnClass(" + column + ")", Level.WARNING, "Error fetching column class type, using default String.class. Exception: " + e.getMessage());
            }
            //e.printStackTrace();
            return String.class;
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
                // TIME is not correctly rendered
                //return java.sql.Time.class;
                return String.class;
            case Types.TIMESTAMP:
                // Timestamp is not correctly rendered
                //return java.sql.Timestamp.class;
                return String.class;
            case Types.TIMESTAMP_WITH_TIMEZONE:
                // Timestamp is not correctly rendered
                //return java.sql.Timestamp.class;
                return String.class;
            case Types.TIME_WITH_TIMEZONE:
                // TIME is not correctly rendered
                //return java.sql.Time.class;
                return String.class;
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

        /*
         is always false, in this case I do not want to write.. try {
        
         */
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Vector v = (Vector) rows.elementAt(rowIndex);
        return (Object) v.elementAt(columnIndex);
    }

    /**
     * @return the con
     */
    public Connection getCon() {
        return con;
    }

    /**
     * @param con the con to set
     */
    public void setCon(Connection con) {
        this.con = con;
    }

    /**
     * @return the stmt
     */
    public Statement getStmt() {
        return stmt;
    }

    /**
     * @param stmt the stmt to set
     */
    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    /**
     * @return the connectionEstablished
     */
    public boolean isConnectionEstablished() {
        return connectionEstablished;
    }

    /**
     * @param connectionEstablished the connectionEstablished to set
     */
    public void setConnectionEstablished(boolean connectionEstablished) {
        this.connectionEstablished = connectionEstablished;
    }

    /**
     * @return the rsmd
     */
    public ResultSetMetaData getRsmd() {
        return rsmd;
    }

    public ResultSetMetaData getMetaData() {
        return rsmd;
    }

    /**
     * @param rsmd the rsmd to set
     */
    public void setRsmd(ResultSetMetaData rsmd) {
        this.rsmd = rsmd;

        try {
            // we also set the Values for the JTable Headers
            int noCols = rsmd.getColumnCount();

            // and now we set the Column Headers and the fieldNames
            columnNames = new String[noCols];
            fieldNames = new String[noCols];
            columnTypes = new int[noCols];
            for (int i = 0; i < noCols; i++) {
                columnNames[i] = rsmd.getColumnLabel(i + 1);
                fieldNames[i] = rsmd.getColumnName(i + 1);
                columnTypes[i] = rsmd.getColumnType(i + 1);
            }

        } catch (SQLException sqlex) {
            logSqlException("setRsmd(ResultSetMetaData rsmd", Level.WARNING, sqlex);
        }

    }

    /**
     * @return the counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * @param counter the counter to set
     */
    public void setCounter(int counter) {
        this.counter = counter;
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

    /**
     * @return the SQLError
     */
    public String getSQLError() {
        return SQLError;
    }

    /**
     * @param SQLError the SQLError to set
     */
    public void setSQLError(String SQLError) {
        this.SQLError = SQLError;
    }

    /**
     * @return the maxRows
     */
    public int getMaxRows() {
        return maxRows;
    }

    public int getMaxRowCount() {
        return maxRows;
    }

    /**
     * @param maxRows the maxRows to set
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    public void setMaxRowCount(int v) {
        this.maxRows = v;
    }

    /**
     * @return the nullTxt
     */
    public String getNullTxt() {
        return nullTxt;
    }

    /**
     * @param nullTxt the nullTxt to set
     */
    public void setNullTxt(String nullTxt) {
        this.nullTxt = nullTxt;
    }

    /**
     * @return the useNullTxt
     */
    public boolean isUseNullTxt() {
        return useNullTxt;
    }

    /**
     * @param useNullTxt the useNullTxt to set
     */
    public void setUseNullTxt(boolean useNullTxt) {
        this.useNullTxt = useNullTxt;
    }

}
