package sql.fredy.ui;

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
 */
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import sql.fredy.metadata.DbInfo;
import javax.swing.JList;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import sql.fredy.connection.DataSource;

public class Tables extends JList {

    private Logger logger = Logger.getLogger("sql.fredy.ui");

// Fredy's make Version
    private static String fredysVersion = "Version 2.0  12. Dec. 2020 ";

    public String getVersion() {
        return fredysVersion;
    }

    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    private Connection con = null;

    public Connection getCon() {

        try {
            if ((null == con) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {

        }
        return con;
    }

    public Vector table;

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

    String schema;

    /**
     * Get the value of schema.
     *
     * @return Value of schema.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Set the value of schema.
     *
     * @param v Value to assign to schema.
     */
    public void setSchema(String v) {
        this.schema = v;
    }

    public void deleteElement(int i) {
        table.remove(i);
        this.setListData(table);
    }

    public Tables(String schema) {
        setSchema(schema);
        init();
    }

    public Tables(String database, String schema) {

        setDatabase(database);
        setSchema(schema);
    }

    DbInfo dbi;

    private void init() {

        //String[] tableTypes = { "TABLE", "VIEW","ALIAS","SYNONYM" };
        String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};

        dbi = new DbInfo();

        table = new Vector();
        table = dbi.getTables(getDatabase(), getSchema());
        this.setListData(table);
    }

    public void setPattern(String p) {
        String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};
        table.clear();
        table = dbi.getTables(getDatabase(), getSchema(), tableTypes, p);
        this.removeAll();
        this.setListData(table);
    }

}
