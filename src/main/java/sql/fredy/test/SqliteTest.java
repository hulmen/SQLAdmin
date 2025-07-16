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
package sql.fredy.test;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;

/**
 *
 * @author fredy
 */
public class SqliteTest {

    public static void main(String[] args) throws SQLException {
        Connection con = null;
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\fredy\\Documents\\DERBY\\Databases\\sqllite");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select * from test10");
            ResultSetMetaData rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();
            for (int i = 1; i < numberOfColumns; i++) {

                String type = "";
                switch (rsmd.getColumnType(i)) {
                    case Types.ARRAY:
                        type = "ARRAY";
                        break;
                    case Types.BIGINT:
                        type = "BIGINT";
                        break;
                    case Types.BINARY:
                        type = "BINARY";
                        break;
                    case Types.BIT:
                        type = "BIT";
                        break;
                    case Types.BLOB:
                        type = "BLOB";
                        break;
                    case Types.BOOLEAN:
                        type = "BOOLEAN";
                        break;
                    case Types.CHAR:
                        type = "CHAR";
                        break;
                    case Types.CLOB:
                        type = "CLOB";
                        break;
                    case Types.DATALINK:
                        type = "DATALINK";
                        break;
                    case Types.DATE:
                        type = "DATE";
                        break;
                    case Types.DECIMAL:
                        type = "DECIMAL";
                        break;
                    case Types.DISTINCT:
                        type = "DISTINCT";
                        break;
                    case Types.DOUBLE:
                        type = "DOUBLE";
                        break;
                    case Types.FLOAT:
                        type = "FLOAT";
                        break;
                    case Types.INTEGER:
                        type = "INTEGER";
                        break;
                    case Types.JAVA_OBJECT:
                        type = "JAVA_OBJECT";
                        break;
                    case Types.LONGNVARCHAR:
                        type = "LONGNVARCHAR";
                        break;
                    case Types.LONGVARBINARY:
                        type = "LONGVARBINARY";
                        break;
                    case Types.LONGVARCHAR:
                        type = "LONGVARCHAR";
                        break;
                    case Types.NCHAR:
                        type = "NCHAR";
                        break;
                    case Types.NCLOB:
                        type = "NCLOB";
                        break;
                    case Types.NULL:
                        type = "NULL";
                        break;
                    case Types.NUMERIC:
                        type = "NUMERIC";
                        break;
                    case Types.NVARCHAR:
                        type = "NVARCHAR";
                        break;
                    case Types.OTHER:
                        type = "OTHER";
                        break;
                    case Types.REAL:
                        type = "REAL";
                        break;
                    case Types.REF:
                        type = "REF";
                        break;
                    case Types.REF_CURSOR:
                        type = "REF_CURSOR";
                        break;
                    case Types.ROWID:
                        type = "ROWID";
                        break;
                    case Types.SMALLINT:
                        type = "SMALLINT";
                        break;
                    case Types.SQLXML:
                        type = "SQLXML";
                        break;
                    case Types.STRUCT:
                        type = "STRUCT";
                        break;
                    case Types.TIME:
                        type = "TIME";
                        break;
                    case Types.TIMESTAMP:
                        type = "TIMESTAMP";
                        break;
                    case Types.TIMESTAMP_WITH_TIMEZONE:
                        type = "TIMESTAMP_WITH_TIMEZONE";
                        break;
                    case Types.TIME_WITH_TIMEZONE:
                        type = "TIME_WITH_TIMEZONE";
                        break;
                    case Types.TINYINT:
                        type = "TINYINT";
                        break;
                    case Types.VARBINARY:
                        type = "VARBINARY";
                        break;
                    case Types.VARCHAR:
                        type = "VARCHAR";
                        break;
                    default:
                        type = "do not know";
                        break;
                }
                System.out.println(i + " " + rsmd.getColumnLabel(i + 1) + " " + rsmd.getColumnType(i) + " " + type + " " + rsmd.getColumnTypeName(i));                                
            }
            SimpleDateFormat sdt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while ( rs.next()) {
                
                System.out.println(rs.getString(6));
            }
            
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

    }
}
