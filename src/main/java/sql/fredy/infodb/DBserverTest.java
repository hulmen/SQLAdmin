/*
 * The MIT License
 *
 * Copyright 2022 fredy.
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
package sql.fredy.infodb;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fredy
 */
public class DBserverTest {

    /* read from commandline
     *
     * 1st parameter as displaytext
     * 2nd parameter as defaultvalue
     */
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

    public DBserverTest() {
        DBserver dbserver = new DBserver();
        dbserver.startUp();
        DBverify dbv = new DBverify();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        while (true) {

            // we are getting a connection
            Connection connection = null;
            Statement stmt = null;
            DatabaseMetaData dmd = null;
            ResultSet rs = null;
            try {
                connection = DBconnectionPool.getInstance().getConnection();
                System.out.println(DBconnectionPool.getInstance().getProductName() + " " + DBconnectionPool.getInstance().getProductVersion() + " " + DBconnectionPool.getInstance().getSqladminDB());
                stmt = connection.createStatement();
                rs = stmt.executeQuery("Select RunAt from APP.QueryHistory order by RunAt desc fetch first 5 rows only ");
                while (rs.next()) {
                    System.out.println(sdf.format(new java.sql.Date(rs.getTimestamp(1).getTime())));
                }

            } catch (SQLException e) {
            } catch (IOException | PropertyVetoException ex) {
                Logger.getLogger(DBserverTest.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (SQLException e) {
                    }
                }

                if (stmt != null) {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                    }
                }
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                    }
                }

            }
            String t = readFromPrompt("Continue ? (Y/n)", "y");
            if (t.equalsIgnoreCase("n")) {
                dbserver.stop();
                break;
            }
        }
    }

    public static void main(String[] args) {
        DBserverTest d = new DBserverTest();
    }
}
