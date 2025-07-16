/*
 * This class lists the available Types a DB supports
 * it is taken from DatabaseMetaData.getTypeInfo


 * The MIT License
 *
 * Copyright 2025 fredy.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sql.fredy.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fredy
 */
public class AvailableDataTypes {

    /**
     * @return the jdbcurl
     */
    public String getJdbcurl() {
        return jdbcurl;
    }

    /**
     * @param jdbcurl the jdbcurl to set
     */
    public void setJdbcurl(String jdbcurl) {
        this.jdbcurl = jdbcurl;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    private String jdbcurl;
    private String user, password;

    private Logger logger = Logger.getLogger("sql.fredy.test");

    public AvailableDataTypes(String url, String usr, String pwd) {
        setJdbcurl(url);
        setUser(usr);
        setPassword(pwd);
        Connection con = null;
        ResultSet rs = null;
        try {
            con = DriverManager.getConnection(getJdbcurl(), getUser(), getPassword());
            DatabaseMetaData dmd = con.getMetaData();
            rs = dmd.getTypeInfo();
            while (rs.next()) {
                System.out.println("Type Name: " + rs.getString(1)
                        + " Local Type Name: " + rs.getString(13)
                        + " Data Type: " + rs.getInt(2)
                        + " Precision: " + rs.getInt(3)
                        + " Literal Prefix: " + rs.getString(4)
                        + " Literal Suffix: " + rs.getString(5)
                        + " Create Params: " + rs.getString(6)
                        + " Nullable: " + rs.getShort(7)
                        + " Case Sensitive: " + rs.getBoolean(8)
                        + " Searchable: " + rs.getInt(9)
                        + " Unsigned Attribute: " + rs.getBoolean(10)
                        + " Fixed prc scale: " + rs.getBoolean(11)
                        + " Auto Increment: " + rs.getBoolean(12)
                        + " Minimum Scale: " + rs.getShort(14)
                        + " Maximum Scale: " + rs.getShort(15)
                );
            }

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "SQL Exception while creating a connection to DB: {0}", sqlex.getMessage());
            logger.log(Level.WARNING, "SQL State {0}", sqlex.getSQLState());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQL Exception while trying to close the resultset: {0}", sqlex.getMessage());
                    logger.log(Level.WARNING, "SQL State {0}", sqlex.getSQLState());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException sqlex) {
                    logger.log(Level.WARNING, "SQL Exception while trying to close the connection: {0}", sqlex.getMessage());
                    logger.log(Level.WARNING, "SQL State {0}", sqlex.getSQLState());
                }
            }
        }
    }

    private static String fixedLength(String value, int lng) {
        return String.format("%1$" + lng + "s", value);
    }

    public static String readFromPrompt(String text, String defValue) {
        String fromPrompt = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(text + " (Default: " + defValue + ") ");
        try {
            fromPrompt = br.readLine();
            if (fromPrompt.length() < 1) {
                fromPrompt = defValue;
            }
        } catch (IOException ioe) {
            fromPrompt = defValue;
        }
        return fromPrompt;
    }

    public static void main(String[] args) {
        String url = readFromPrompt("JDBC URL (q = quit)", "q");
        if (url.equalsIgnoreCase("q")) System.exit(0);
        
        String userName = readFromPrompt("Username (q = quit", "q");
        if (userName.equalsIgnoreCase("q")) System.exit(0);
        
        String pwd = readFromPrompt("Password (q = quit", "q");
        if (pwd.equalsIgnoreCase("q")) System.exit(0);
        
        AvailableDataTypes adt = new AvailableDataTypes(url, userName, pwd);
        
    }
}
