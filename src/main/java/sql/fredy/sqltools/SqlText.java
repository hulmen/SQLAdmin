package sql.fredy.sqltools;

/*
   This software is part of tDavid Good's contribution to he Admin-Framework 
 
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
import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.util.concurrent.TimeUnit;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class SqlText extends JPanel implements SqlTab {

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
     * @return the endTime
     */
    public DateTime getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(DateTime endTime) {
        this.endTime = endTime;
    }

    public JTextArea TextArea = new JTextArea(20, 80);
    private String query;
    String szError;
    int iNumRows = 0;   
    JScrollPane scrollpane;
    private Statement stmt;
    private DateTime startTime,endTime;
    private boolean tablePanel;
    
    public SqlText() {
    }

    public SqlText(Connection con, Statement stmt, String query) {
        this.stmt = stmt;
        setLayout(new FlowLayout());
        this.query = query;
        TextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        execQuery(query);
        scrollpane = new JScrollPane(TextArea);
        add(scrollpane);
        // because of instantDB
        try {
            stmt.close();
            con.close();
        } catch (SQLException sqlexception) {;
        }
    }

    private String statusLine = "";

    public void setViewPortSize(Dimension d) {
        //scrollpane.setPreferredSize(d);
    }

    private void execQuery(String query) {
        try {
            DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.sss");
            setStartTime(new DateTime());
            
            setTablePanel(stmt.execute(query));
            if (isTablePanel()) {
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            setEndTime(new DateTime());

            Duration dur = new Duration(startTime, endTime);

            long millis = dur.getMillis();
            String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

            if (millis <= 10000) {
                hms = Long.toString(millis) + "ms";
            }
            setStatusLine("Query started " + startTime.toString(dtf) + " Query ended " + endTime.toString(dtf) + " Execution Time " + hms);

            SqlResults s = new SqlResults(rs, rsmd, rsmd.getColumnCount());
            s.loadText(TextArea);
            } else {
                iNumRows = stmt.getUpdateCount();
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    public String getSQLError() {
        return szError;
    }

    public int getNumRows() {
        return iNumRows;
    }

    public String getQuery() {
        return query;
    }

    /**
     * @return the statusLine
     */
    public String getStatusLine() {
        return statusLine;
    }

    /**
     * @param statusLine the statusLine to set
     */
    public void setStatusLine(String statusLine) {
        this.statusLine = statusLine;
    }

    class SqlColumn extends LinkedList {

        int maxLength = 0;

        SqlColumn(String colName, int width) {
            super();
            maxLength = width;
            add(colName);
            char[] underline = new char[colName.length()];
            Arrays.fill(underline, '-');
            add(new String(underline));
        }

        public Object get(int index) {
            String szData = (super.get(index)).toString();

            char[] filler = new char[maxLength - szData.length()];
            Arrays.fill(filler, ' ');

            return szData + new String(filler);

        }

        public boolean add(Object o) {
            super.add(o);
            if (o.toString().length() >= maxLength) {
                maxLength = o.toString().length() + 1;
            }

            return true;
        }
    }

    class SqlResults extends Vector {

        private int size;

        SqlResults(ResultSet rs, ResultSetMetaData rsmd, int size) {
            super(size);
            this.size = size;

            try {
                for (int y = 1; y <= size; y++) {
                    add(new SqlColumn(rsmd.getColumnName(y), rsmd.getColumnDisplaySize(y)));
                }

                while (rs.next()) {
                    for (int y = 0; y < size; y++) {
                        if (rs.getObject(y + 1) == null) {
                            ((SqlColumn) get(y)).add("null");
                        } else {
                            ((SqlColumn) get(y)).add(rs.getObject(y + 1));
                        }

                    }
                    iNumRows++;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        void loadText(JTextArea TextArea) {
            int listLength = iNumRows + 2;//takes account of row header

            for (int i = 0; i < listLength; i++) {
                for (int j = 0; j < size; j++) {
                    TextArea.append((((SqlColumn) get(j)).get(i)).toString());
                }
                TextArea.append("\n");
            }
        }
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

}
