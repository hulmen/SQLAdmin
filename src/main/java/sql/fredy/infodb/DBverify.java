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

 /*
This is to verify and check the internal Derby Database 

It should contain these Tables:
APP.CODECOMPLETION   
    TEMPLATENAME VARCHAR(50)
    ABBREVIATION VARCHAR(50)
    CODETEMPLATE VARCHAR(8192)
    DESCRIPTION  VARCHAR(8192)
    TOOLUSER     VARCHAR(256)

APP.ADMINPARAMETER
    NAME     VARCHAR(510)
    CONTENT  VARCHAR(8192)
    TOOLUSER VARCHAR(256)

APP.NOTES
    ID    INTEGER      NOT NULL AUTOINCREMENT
    RUNAT TIMESTAMP    NOT NULL DEFAULT CURRENT_TIME
    TITLE VARCHAR(2048)
    NOTE  VARCHAR(65344)
    HASHCODE INTEGER
    TOOLUSER VARCHAR(256)
    
APP.QUERYHISTORY
    RUNAT  TIMESTAMP  NOT NULL DEFAULT CURRENT_TIME
    DATABASE VARCHAR(2048)
    QUERY    VARCHAR(65344)
    HASHCODE INTEGER
    TOOLUSER VARCHAR(256)


If the table does not exist, it will be created
If a field is missing, it will be added

 */
package sql.fredy.infodb;

import java.awt.Color;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fredy
 */
public class DBverify {

    private Logger logger = Logger.getLogger("sql.fredy.infodb");
    private static Level LOGLEVEL = Level.FINE;

    public DBverify() {
        Connection connection = null;

        try {
            connection = DBconnectionPool.getInstance().getConnection();
            checkAdminParameter(connection);
            checkCodecompletion(connection);
            checkNotes(connection);
            checkQueryhistory(connection);

            try {
                if (sql.fredy.admin.Admin.getLogOn().getUser() != null) {
                    checkBackgroundImage(connection);
                }
            } catch (Exception e) {

            }
            logger.log(LOGLEVEL, "Internal DB fine");

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Exception while verifying Derby DB: {0}", e.getMessage());
        } catch (IOException | PropertyVetoException ex) {
            Logger.getLogger(DBverify.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (connection != null) 
              try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Exception while closing Derby DB: {0}", e.getMessage());
            }
        }
    }

    private void checkAdminParameter(Connection con) {
        ResultSet rs = null;
        DatabaseMetaData dmd = null;
        Statement stmt = null;
        String tableName = "ADMINPARAMETER";

        try {
            dmd = con.getMetaData();
            stmt = con.createStatement();
            rs = dmd.getTables(null, "APP", tableName, new String[]{"TABLE"});
            if (rs.next()) {

                // we verify the columns;
                rs = dmd.getColumns(null, "APP", tableName, "NAME");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.ADMINPARAMETER ADD COLUMN NAME VARCHAR(256)");
                }
                rs = dmd.getColumns(null, "APP", tableName, "CONTENT");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.ADMINPARAMETER ADD COLUMN CONTENT VARCHAR(4096)");
                }
                rs = dmd.getColumns(null, "APP", tableName, "TOOLUSER");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.ADMINPARAMETER ADD COLUMN TOOLUSER VARCHAR(128)");
                }
            } else {
                stmt.executeUpdate("CREATE TABLE APP.ADMINPARAMETER ( NAME VARCHAR(256),CONTENT VARCHAR(4096), TOOLUSER VARCHAR(128) )");
                stmt.executeUpdate("CREATE INDEX APP.ADMINPARAMETER_IDX1 ON APP.ADMINPARAMETER(TOOLUSER)");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Exception while verifying Derby DB TABLE ADMINPARAMETER: {0}", e.getMessage());
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

        }
    }

    private void checkCodecompletion(Connection con) {
        ResultSet rs = null;
        DatabaseMetaData dmd = null;
        Statement stmt = null;
        String tableName = "CODECOMPLETION";

        try {
            dmd = con.getMetaData();
            stmt = con.createStatement();
            rs = dmd.getTables(null, "APP", tableName, new String[]{"TABLE"});
            if (rs.next()) {

                // we verify the columns;
                rs = dmd.getColumns(null, "APP", tableName, "TEMPLATENAME");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.CODECOMPLETION ADD COLUMN TEMPLATENAME VARCHAR(25)");
                }
                rs = dmd.getColumns(null, "APP", tableName, "ABBREVIATION");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.CODECOMPLETION ADD COLUMN ABBREVIATION VARCHAR(25)");
                }
                rs = dmd.getColumns(null, "APP", tableName, "CODETEMPLATE");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.CODECOMPLETION ADD COLUMN CODETEMPLATE VARCHAR(4096)");
                }

                rs = dmd.getColumns(null, "APP", tableName, "DESCRIPTION");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.CODECOMPLETION ADD COLUMN DESCRIPTION VARCHAR(4096)");
                }

                rs = dmd.getColumns(null, "APP", tableName, "TOOLUSER");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.CODECOMPLETION ADD TOOLUSER VARCHAR(128)");
                }
            } else {
                stmt.executeUpdate("create table CODECOMPLETION (\n"
                        + "                                    ToolUser VARCHAR(128) not null,\n"
                        + "                                    TemplateName VARCHAR(25) NOT NULL ,\n"
                        + "                                    Abbreviation VARCHAR(25) ,\n"
                        + "                                    CodeTemplate VARCHAR(4096) ,\n"
                        + "                                    Description VARCHAR(4096)  ,\n"
                        + "                                    PRIMARY KEY (ToolUser,TemplateName)\n"
                        + "                                    )");

                stmt.executeUpdate("CREATE INDEX APP.CODECOMPLETION_IDX1 ON APP.CODECOMPLETION(TOOLUSER)");

            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Exception while verifying Derby DB Table CODECOMPLETION: {0}", e.getMessage());
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
        }

    }

    private void checkNotes(Connection con) {
        ResultSet rs = null;
        DatabaseMetaData dmd = null;
        Statement stmt = null;
        String tableName = "NOTES";

        try {
            dmd = con.getMetaData();
            stmt = con.createStatement();
            rs = dmd.getTables(null, "APP", tableName, new String[]{"TABLE"});
            if (rs.next()) {

                // we verify the columns;
                rs = dmd.getColumns(null, "APP", tableName, "RUNAT");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.NOTES ADD COLUMN RUNAT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
                }
                rs = dmd.getColumns(null, "APP", tableName, "TITLE");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.NOTES ADD COLUMN TITLE VARCHAR(1024)");
                }
                rs = dmd.getColumns(null, "APP", tableName, "NOTE");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.NOTES ADD COLUMN NOTE VARCHAR(32672)");
                }

                rs = dmd.getColumns(null, "APP", tableName, "HASHCODE");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.NOTES ADD COLUMN HASHCODE INTEGER");
                }

                rs = dmd.getColumns(null, "APP", tableName, "TOOLUSER");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.NOTES ADD TOOLUSER CONTENT VARCHAR(128)");
                }
            } else {
                stmt.executeUpdate("create table APP.Notes (\n"
                        + "                                   ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),\n"
                        + "                                   ToolUser VARCHAR(128),\n"
                        + "                                   RunAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                        + "                                   Title varchar(1024),\n"
                        + "                                   Note VARCHAR(32672),\n"
                        + "                                   HashCode INTEGER )");

                stmt.executeUpdate("create INDEX APP.NotesIDX on APP.Notes(HashCode, ToolUser)");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Exception while verifying Derby DB Table NOTES: {0}", e.getMessage());
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
        }

    }

    private void checkQueryhistory(Connection con) {
        ResultSet rs = null;
        DatabaseMetaData dmd = null;
        Statement stmt = null;
        String tableName = "QUERYHISTORY";

        try {
            dmd = con.getMetaData();
            stmt = con.createStatement();
            rs = dmd.getTables(null, "APP", tableName, new String[]{"TABLE"});
            if (rs.next()) {

                // we verify the columns;
                rs = dmd.getColumns(null, "APP", tableName, "RUNAT");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.QUERYHISTORY ADD COLUMN RUNAT TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP");
                }
                rs = dmd.getColumns(null, "APP", tableName, "DATABASE");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.QUERYHISTORY ADD COLUMN DATABASE VARCHAR(1024)");
                }
                rs = dmd.getColumns(null, "APP", tableName, "QUERY");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.QUERYHISTORY ADD COLUMN QUERY VARCHAR(32672)");
                }

                rs = dmd.getColumns(null, "APP", tableName, "HASHCODE");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.QUERYHISTORY ADD COLUMN HASHCODE INTEGER");
                }

                rs = dmd.getColumns(null, "APP", tableName, "TOOLUSER");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.QUERYHISTORY ADD COLUMN TOOLUSER VARCHAR(128)");
                }
            } else {
                stmt.executeUpdate("create table APP.QueryHistory (\n"
                        + "                                   ToolUser VARCHAR(128) not null,\n"
                        + "                                   RunAt TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,\n"
                        + "                                   Database varchar(1024),\n"
                        + "                                   Query VARCHAR(32672),\n"
                        + "                                   HashCode INTEGER )");

                stmt.executeUpdate("create INDEX APP.QUERYHISTORY_IDX on APP.QueryHistory(HashCode, ToolUser)");
            }
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Exception while verifying Derby DB Table QUERYHISTORY: {0}", e.getMessage());
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
        }

    }

    public void checkBackgroundImage(Connection con) {
        ResultSet rs = null;
        DatabaseMetaData dmd = null;
        Statement stmt = null;
        PreparedStatement ps = null;
        String tableName = "BACKGROUNDIMAGE";

        try {
            dmd = con.getMetaData();
            stmt = con.createStatement();
            rs = dmd.getTables(null, "APP", tableName, new String[]{"TABLE"});
            if (!rs.next()) {
                stmt.executeUpdate("""
                                   create table APP.BACKGROUNDIMAGE (
                                                  userName     VARCHAR(32) ,
                                                  serverName   VARCHAR(256),
                                                  databaseName VARCHAR(1024),
                                                  pathToImage  VARCHAR(1024),
                                                  backGroundColor INT ,
                                                  PRIMARY KEY (userName,serverName,databaseName)
                                           )""");

                // now we add the value from the properties
                ps = con.prepareStatement("insert into APP.BACKGROUNDIMAGE ( userName,serverName,databaseName,pathToImage,BackGroundColor) values (?,?,?,?,?)");
                ps.setString(1, System.getProperty("user.name"));
                ps.setString(2, sql.fredy.admin.Admin.getLogOn().getHost());
                ps.setString(3, sql.fredy.admin.Admin.getLogOn().getDatabase());
                ps.setInt(4, Integer.parseInt(getValueFromProperties("background")));

                ps.setInt(5, Integer.getInteger(getValueFromProperties("backgroundcolor")));
                ps.executeUpdate();
            } else {
                // does backgroundcolor exist?
                rs = dmd.getColumns(null, "APP", tableName, "BACKGROUNDCOLOR");
                if (!rs.next()) {
                    stmt.executeUpdate("alter table APP.BACKGROUNDIMAGE ADD COLUMN backGroundColor INT");
                    ps = con.prepareStatement("update APP.BACKGROUNDIMAGE  set backGroundColor = ? where  userName = ?  and serverName = ?  and databaseName = ?");
                    ps.setInt(1, Integer.parseInt(getValueFromProperties("background")));
                    ps.setString(2, System.getProperty("user.name"));
                    ps.setString(3, sql.fredy.admin.Admin.getLogOn().getHost());
                    ps.setString(4, sql.fredy.admin.Admin.getLogOn().getDatabase());
                    
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            logger.log(Level.WARNING, "Exception while verifying Derby DB Table BACKGROUND: {0}", e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {

                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {

                }
            }
            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException e) {

                }
            }
        }
    }

    private String getValueFromProperties(String name) {
        String value = null;
        Properties prop = new Properties();

        try {

            String admin_dir = System.getProperty("admin.work");
            if (admin_dir == null) {
                admin_dir = System.getProperty("user.home");
            }

            //logger.log(Level.INFO, "Using properties file {0}{1}admin.props", new Object[]{admin_dir, File.separator});
            try (FileInputStream fip = new FileInputStream(admin_dir + File.separator + "admin.props")) {
                prop.load(fip);
            }

            try {
                value = prop.getProperty(name, null);
            } catch (Exception e) {
                logger.log(Level.FINE, "Exception while fetching proerty value {0} {1}", new Object[]{name, e.getMessage()});
            }
            logger.log(Level.INFO, "Using properties file {0}{1}admin.props to read {2} with value {3}", new Object[]{admin_dir, File.separator, name, value});
        } catch (IOException ioex) {
            logger.log(Level.WARNING, "cannot lod properties file admin.props: {0}", ioex.getMessage());
        }

        return value;
    }
}
