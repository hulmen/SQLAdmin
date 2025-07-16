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
package sql.fredy.infodb;

import java.awt.SystemTray;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.joda.time.DateTime;
import sql.fredy.connection.DataSource;
import sql.fredy.share.TConnectProperties;
import sql.fredy.share.TableFindResult;
import sql.fredy.ui.FieldNameType;
import sql.fredy.ui.RunningJobsSystemTray;

/**
 *
 * @author fredy
 */
public class DbTable extends AbstractTableModel {

    private static Level LOGLEVEL = Level.FINE;

    private int maxRows = 10000;

    private Connection con = null;

    public void setCon(Connection c) {
        con = c;
        setConnectionEstablished(true);
    }

    /**
     * @return the startTime
     */
    public DateTime getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the EndTime
     */
    public DateTime getEndTime() {
        return endTime;
    }

    /**
     * @param EndTime the EndTime to set
     */
    public void setEndTime(DateTime EndTime) {
        this.endTime = EndTime;
    }

    private Logger logger = Logger.getLogger(getClass().getName());
    private boolean connectionEstablished;
    private String[] columnNames;
    private String[] fieldNames;
    private ArrayList<FieldNameType> fieldNameTypes;
    private int[] columnTypes;
    private boolean[] columnIsSigned;
    private ResultSetMetaData rsmd;
    private Vector rows;
    private int counter;
    private boolean standalone = false;
    private String SQLError;
    private SimpleDateFormat sdf;
    private SimpleDateFormat sdt;
    //private int maxRows = 10000;
    private String nullTxt;
    private boolean useNullTxt = false;
    private String query;

    private boolean tablePanel;
    private int rowsExecuted;
    private DateTime startTime, endTime;

    /*
    JdbcTable will be instantiated via the connection pool 
     */
    public DbTable(Connection c) {
        setCon(c);

    }

    public int getColumnType(int col) {
        try {
            return columnTypes[col];
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception {0}", e.getMessage());
        }
        return 0;
    }

    /*
      close the Statement and the connection, use with care not to close your connection by fault
 
     */
    public void close() {

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
                        rowSorter = new TableRowSorter<>(this);
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

    private boolean isProcedure(String query) {
        boolean procedure = false;
        if (query.toLowerCase().trim().startsWith("{call")) {
            procedure = true;
        }
        if (query.toLowerCase().trim().startsWith("exec")) {
            procedure = true;
        }
        if (query.toLowerCase().trim().startsWith("call")) {
            procedure = true;
        }
        return procedure;
    }

    /*   
      execute the query if a connection exists
      @param query the SQL statement to be run
     */
    public boolean executeQuery(String query) {

        Statement stmt = null;

        ResultSet rs = null;

        // the begininning , this variable is public to be interpreted
        startTime = new DateTime();

        /*      
        if (con == null) {
            con = getCon();
        }
         */
        if (query
                != null) {
            setQuery(query);
        }

        RunningJobsSystemTray runningJob = null;

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

            stmt = getCon().createStatement();

            // SystemTray
            if (SystemTray.isSupported()) {
                runningJob = new RunningJobsSystemTray(query);
                runningJob.setStmt(stmt);
                Thread runningJobThread = new Thread(runningJob);
                runningJobThread.start();
            }

            // we send the query to the DB
            //setTablePanel(stmt.execute(query));
            //setRowsExecuted(stmt.getUpdateCount());
                        
            boolean isResultSet = stmt.execute(query);
            
            while (true) {
                if ( isResultSet ) {

                    rs = stmt.getResultSet();

                    if (runningJob != null) {
                        runningJob.setStmt(stmt);
                    }

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
                                    //newRow.add((String) "OTHER Type not supported");
                                    newRow.add((String) rs.getString(i));
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
                                    //newRow.add((Integer) rs.getInt(i));
                                    newRow.add((Byte) rs.getByte(i));
                                    //newRow.add( rs.getShort(i));
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
                    // Changed 2022-05-19, because of closed connection below
                    createFieldList();
                } else {
                    int updateCount = stmt.getUpdateCount();
                    if (updateCount == - 1) {
                        break;
                    }
                }
                //setTablePanel(stmt.getMoreResults());
                isResultSet = stmt.getMoreResults();
            }

        } catch (SQLException sqlex) {

            //if (sqlex.getErrorCode() != 0) {
            endTime = new DateTime();
            logSqlException("executeQuery()", Level.WARNING, sqlex);

            // we remove the tray-icon for this query
            if (runningJob != null) {
                runningJob.remove();
            }
            return false;
            //}
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException ex) {
                logger.log(Level.WARNING, "SQLException while closing " + ex.getMessage());
            }
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException ex) {
                logger.log(Level.WARNING, "SQLException while closing " + ex.getMessage());
            }

        }
        // we remove the tray-icon for this query
        if (runningJob
                != null) {
            runningJob.remove();
        }
        endTime = new DateTime();

        return true;
    }

    /*
       if a value is not a String, it will be converted to a String
       whereby Date and Time are converted to a String by local Date and Time representation
       @param pattern the part of the text contained in a cell
       @return Array of Type TabelFindResult
    
     */
    public ArrayList<TableFindResult> search(String pattern) {
        ArrayList<TableFindResult> tfr = new ArrayList();
        DateFormat dateFormat = DateFormat.getDateInstance();
        DateFormat timeFormat = DateFormat.getTimeInstance();
        DateFormat datetimeFormat = DateFormat.getDateTimeInstance();;
        boolean numeric = isNumeric(pattern);
        String found = "";

        // we Loop to find....
        int contains = 0;
        for (int r = 0; r < getRowCount(); r++) {
            for (int c = 0; c < getColumnCount(); c++) {
                contains = -1;
                int type = columnTypes[c];
                try {
                    switch (type) {
                        case Types.ARRAY:
                            break;
                        case Types.BIGINT:
                            // Long
                            if (!numeric) {
                                break;
                            }
                            long l = Long.parseLong(pattern);
                            if (Long.valueOf(l).equals((Long) getValueAt(r, c))) {
                                contains = 1;
                            }
                            found = String.format("%,d", l);
                            //contains = String.format("%,f", getValueAt(r, c)).indexOf(pattern);
                            break;
                        case Types.BINARY:
                            break;
                        case Types.BIT:
                            // Boolean
                            if (((Boolean) getValueAt(r, c)) && (pattern.equalsIgnoreCase("true"))) {
                                found = "true";
                                contains = 1;
                            }
                            if ((!(Boolean) getValueAt(r, c)) && (pattern.equalsIgnoreCase("false"))) {
                                found = "false";
                                contains = 1;
                            }
                            break;
                        case Types.BLOB:
                            break;
                        case Types.BOOLEAN:
                            // Boolean
                            if (((Boolean) getValueAt(r, c)) && (pattern.equalsIgnoreCase("true"))) {
                                found = "true";
                                contains = 1;
                            }
                            if ((!(Boolean) getValueAt(r, c)) && (pattern.equalsIgnoreCase("false"))) {
                                found = "false";
                                contains = 1;
                            }
                            break;
                        case Types.CHAR:
                            contains = ((String) getValueAt(r, c)).toLowerCase().indexOf(pattern.toLowerCase());
                            if (contains >= 0) {
                                found = (String) getValueAt(r, c);
                            }
                            break;
                        case Types.CLOB:
                            break;
                        case Types.DATALINK:
                            break;
                        case Types.DATE:
                            // Date
                            contains = dateFormat.format((Date) getValueAt(r, c)).indexOf(pattern);
                            if (contains >= 0) {
                                found = dateFormat.format((Date) getValueAt(r, c));
                            }
                            break;
                        case Types.DECIMAL:
                            // BigDecimal  
                            if (!numeric) {
                                break;
                            }
                            l = Long.parseLong(pattern);
                            BigDecimal bd = BigDecimal.valueOf(l);
                            if (bd.equals((BigDecimal) getValueAt(r, c))) {
                                contains = 1;
                                found = String.format("%,f", (BigDecimal) getValueAt(r, c));
                            }
                            //contains = String.format("%,f", getValueAt(r, c)).indexOf(pattern);
                            break;
                        case Types.DISTINCT:
                            break;
                        case Types.DOUBLE:

                            if (!numeric) {
                                break;
                            }
                            double d = Double.parseDouble(pattern);
                            if (Double.valueOf(d).equals((Double) getValueAt(r, c))) {
                                contains = 1;
                                found = String.format("%,f", (Double) getValueAt(r, c));
                            }
                            //contains = String.format("%,f", getValueAt(r, c)).indexOf(pattern);
                            break;
                        case Types.FLOAT:
                            if (!numeric) {
                                break;
                            }
                            float f = Float.parseFloat(pattern);
                            if (Float.valueOf(f).equals((Float) getValueAt(r, c))) {
                                contains = 1;
                                found = String.format("%,f", (Float) getValueAt(r, c));
                            }
                            //contains = String.format("%,f", getValueAt(r, c)).indexOf(pattern);
                            break;
                        case Types.INTEGER:
                            if (!numeric) {
                                break;
                            }
                            int integer = Integer.parseInt(pattern);
                            if (Integer.valueOf(integer).equals((Integer) getValueAt(r, c))) {
                                contains = 1;
                                found = ((Integer) getValueAt(r, c)).toString();
                            }
                            break;
                        case Types.JAVA_OBJECT:
                            break;
                        case Types.LONGNVARCHAR:
                            contains = ((String) getValueAt(r, c)).toLowerCase().indexOf(pattern.toLowerCase());
                            if (contains >= 0) {
                                found = (String) getValueAt(r, c);
                            }
                            break;
                        case Types.LONGVARBINARY:
                            break;
                        case Types.LONGVARCHAR:
                            contains = ((String) getValueAt(r, c)).toLowerCase().indexOf(pattern.toLowerCase());
                            if (contains >= 0) {
                                found = (String) getValueAt(r, c);
                            }
                            break;
                        case Types.NCHAR:
                            contains = ((String) getValueAt(r, c)).toLowerCase().indexOf(pattern.toLowerCase());
                            if (contains >= 0) {
                                found = (String) getValueAt(r, c);
                            }
                            break;
                        case Types.NCLOB:
                            break;
                        case Types.NULL:
                            break;
                        case Types.NUMERIC:
                            if (!numeric) {
                                break;
                            }
                            l = Long.parseLong(pattern);
                            bd = BigDecimal.valueOf(l);
                            if (bd.equals((BigDecimal) getValueAt(r, c))) {
                                contains = 1;
                                found = String.format("%,f", (BigDecimal) getValueAt(r, c));
                            }
                            //contains = String.format("%,f", getValueAt(r, c)).indexOf(pattern);
                            break;
                        case Types.NVARCHAR:
                            contains = ((String) getValueAt(r, c)).toLowerCase().indexOf(pattern.toLowerCase());
                            if (contains >= 0) {
                                found = (String) getValueAt(r, c);
                            }
                            break;
                        case Types.OTHER:
                            break;
                        case Types.REAL:
                            // Float
                            contains = String.format("%,f", getValueAt(r, c)).indexOf(pattern);
                            if (contains >= 0) {
                                found = String.format("%,f", (Float) getValueAt(r, c));
                            }
                            break;
                        case Types.REF:
                            break;
                        case Types.REF_CURSOR:
                            break;
                        case Types.ROWID:
                            break;
                        case Types.SMALLINT:
                            if (!numeric) {
                                break;
                            }
                            integer = Integer.parseInt(pattern);
                            if (integer == (int) getValueAt(r, c)) {
                                contains = 1;
                                found = ((Integer) getValueAt(r, c)).toString();
                            }
                            break;
                        case Types.SQLXML:
                            contains = ((String) getValueAt(r, c)).toLowerCase().indexOf(pattern.toLowerCase());
                            if (contains >= 0) {
                                found = (String) getValueAt(r, c);
                            }
                            break;
                        case Types.STRUCT:
                            break;
                        case Types.TIME:
                            contains = timeFormat.format((Time) getValueAt(r, c)).indexOf(pattern);
                            if (contains >= 0) {
                                found = timeFormat.format((Time) getValueAt(r, c));
                            }
                            break;
                        case Types.TIMESTAMP:
                            contains = datetimeFormat.format((Timestamp) getValueAt(r, c)).indexOf(pattern);
                            break;
                        case Types.TIMESTAMP_WITH_TIMEZONE:
                            contains = datetimeFormat.format((Time) getValueAt(r, c)).indexOf(pattern);
                            if (contains >= 0) {
                                found = datetimeFormat.format((Time) getValueAt(r, c));
                            }
                            break;
                        case Types.TIME_WITH_TIMEZONE:
                            contains = timeFormat.format((Time) getValueAt(r, c)).indexOf(pattern);
                            if (contains >= 0) {
                                found = timeFormat.format((Time) getValueAt(r, c));
                            }
                            break;
                        case Types.TINYINT:
                            if (!numeric) {
                                break;
                            }
                            integer = Integer.parseInt(pattern);
                            if (Integer.valueOf(integer).equals((Integer) getValueAt(r, c))) {
                                contains = 1;
                                found = ((Integer) getValueAt(r, c)).toString();
                            }
                            break;
                        case Types.VARBINARY:
                            break;
                        case Types.VARCHAR:
                            contains = ((String) getValueAt(r, c)).toLowerCase().indexOf(pattern.toLowerCase());
                            if (contains >= 0) {
                                found = (String) getValueAt(r, c);
                            }
                            break;
                        default:
                            contains = ((String) getValueAt(r, c)).toLowerCase().indexOf(pattern.toLowerCase());
                            if (contains >= 0) {
                                found = (String) getValueAt(r, c);
                            }
                            break;
                    }
                } catch (Exception e) {

                }
                if (contains != -1) {
                    TableFindResult t = new TableFindResult(r, c, pattern, found);
                    tfr.add(t);
                }
            }
        }

        return tfr;
    }

    public static boolean isNumeric(String str) {
        NumberFormat numberformatter = NumberFormat.getInstance();
        ParsePosition position = new ParsePosition(0);
        numberformatter.parse(str, position);
        return str.length() == position.getIndex();
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

    private Connection getCon() {
        return con;
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
            columnIsSigned = new boolean[noCols];
            for (int i = 0; i < noCols; i++) {
                columnNames[i] = rsmd.getColumnLabel(i + 1);
                fieldNames[i] = rsmd.getColumnName(i + 1);
                columnTypes[i] = rsmd.getColumnType(i + 1);
                columnIsSigned[i] = rsmd.isSigned(i + 1);
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
     * @return the tablePanel
     */
    public boolean isTablePanel() {
        return tablePanel;
    }

    /**
     * @param tablePanel the tablePanel to set
     */
    public void setTablePanel(boolean tablePanel) {
        this.tablePanel = tablePanel;
    }

    /**
     * @return the rowsExecuted
     */
    public int getRowsExecuted() {
        return rowsExecuted;
    }

    /**
     * @param rowsExecuted the rowsExecuted to set
     */
    public void setRowsExecuted(int rowsExecuted) {
        this.rowsExecuted = rowsExecuted;
    }

    public ArrayList<FieldNameType> getFieldNameTypes() {
        if (fieldNameTypes == null) {
            createFieldList();
        }
        return fieldNameTypes;
    }

    private void createFieldList() {
        fieldNameTypes = new ArrayList();
        try {
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                FieldNameType fnt = new FieldNameType();
                fnt.setName(firstLower(spaceFixer(rsmd.getColumnLabel(i))));
                fnt.setType(getColumnClassAsString(i));
                fnt.setColumnDataType(rsmd.getColumnType(i));
                fnt.setOriginalColumnName(rsmd.getColumnLabel(i));
                fnt.setLength(rsmd.getColumnDisplaySize(i));
                fnt.setSigned(rsmd.isSigned(i));
                try {
                    fnt.setTable(rsmd.getTableName(i).toLowerCase());
                    fnt.setLength(rsmd.getPrecision(i));
                    fnt.setScale(rsmd.getScale(i));
                } catch (Exception e1) {
                    fnt.setTable("unknonwTable");
                }
                if (fnt.getTable().length() < 1) {
                    fnt.setTable("table");
                }
                try {
                    fnt.setDb(spaceFixer(rsmd.getCatalogName(1).toLowerCase()));
                } catch (Exception e2) {
                    fnt.setDb("canNotDetectDB");
                }
                if (fnt.getDb().length() < 1) {
                    fnt.setDb("db");
                }
                fieldNameTypes.add(fnt);
            }
        } catch (SQLException sqlex) {
            // trage hier deine Fehlermeldung ein
            logger.log(Level.WARNING, "Error while collecting info for  codegeneration : {0}", sqlex.getMessage());
            logger.log(Level.INFO, "SQL Status : {0}", sqlex.getSQLState());
            logger.log(Level.INFO, "SQL Fehler : {0}", sqlex.getErrorCode());
        } catch (Exception e) {
            logger.log(Level.WARNING, "probably no result generated. {0}", e.getMessage());
        }

    }

    private String spaceFixer(String s) {
        s = s.trim();
        s = s.replaceAll(" ", "_");
        return s;
    }

    private String nameFixer(String s) {
        s = s.trim();
        if (s.indexOf(' ') > 0) {
            s = "'" + s + "'";
        }
        return s;
    }

    private String firstUpper(String s) {
        s = s.substring(0, 1).toUpperCase() + s.substring(1);
        return s;
    }

    private String firstLower(String s) {
        try {
            s = s.substring(0, 1).toLowerCase() + s.substring(1);
        } catch (Exception e) {

        }
        return s;
    }

    public String getColumnClassAsString(int column) {
        int type;
        try {
            type = rsmd.getColumnType(column);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Exception in class \ngetColumnClass. Exception is:\n" + e.getMessage() + "\n\n");
            //e.printStackTrace();

            return "String";
        }

        switch (type) {
            case Types.CHAR:
                return "String";
            case Types.VARCHAR:
                return "String";
            case Types.LONGVARCHAR:
                return "String";
            case Types.BIT:
                return "boolean";
            case Types.BOOLEAN:
                return "boolean";
            case Types.TINYINT:
                return "int";
            case Types.SMALLINT:
                return "short";
            case Types.INTEGER:
                return "int";
            case Types.BIGINT:
                return "long";
            case Types.NUMERIC:
                return "BigDecimal";
            case Types.FLOAT:
                return "float";
            case Types.DECIMAL:
                return "float";
            case Types.DOUBLE:
                return "double";
            case Types.REAL:
                return "float";
            case Types.DATE:
                return "java.sql.Date";
            case Types.TIMESTAMP:
                return "java.sql.Timestamp";
            case Types.TIME:
                return "java.sql.Time";
            default:
                return "String";
        }
    }
}
