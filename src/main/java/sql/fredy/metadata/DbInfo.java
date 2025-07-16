package sql.fredy.metadata;

/**
 * *
 * DbInfo is Part of Admin and delivers lot of the Meta-Data-Info of a Database
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
import java.sql.*;
import java.util.Vector;
import java.util.logging.*;
import sql.fredy.share.BasicAdmin;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import sql.fredy.connection.DataSource;

public class DbInfo extends BasicAdmin {

    private File fileName;
    private PrintWriter out = null;

    private Logger logger = Logger.getLogger("sql.fredy.admin");

    public DbInfo(String database) {
        super(database, null);
    }

    public DbInfo() {

    }

    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    private Connection con = null;

    public Connection getConnection() {

        try {
            if ((con == null) || (con.isClosed())) {
                con = DataSource.getInstance().getConnection();
                productName = DataSource.getInstance().getProductName();
                productVersion = DataSource.getInstance().getProductVersion();
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {
            return con;
        }

        
    }

    public void close() {

    }

    Vector catalog;

    /**
     * Get the value of catalogs.
     *
     * @return Value of catalogs.
     */
    public Vector getCatalog() {
        catalog = new Vector();
        ResultSet rs = null;
        //Connection con = null;
        DatabaseMetaData dmd = null;
        try {
            con = getCon();
            dmd = con.getMetaData();
            rs = dmd.getCatalogs();

            if (rs != null) {
                while (rs.next()) {
                    String s = rs.getString("TABLE_CAT");

                    // I'm struggeling around with PostgreSQL and its template_
                    if (!((getProductName().toLowerCase().startsWith("postgresql")) && (s.startsWith("template")))) {
                        catalog.addElement((String) s);
                    }
                }
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User " + getUser()
                    + " throws Exception while reading catalogs from MetaData "
                    + " |JDBC-Driver: " + getDriverName()
                    + " |Database: " + getDatabase()
                    + " |Error-Code: " + sqe.getErrorCode()
                    + " |SQL-State: " + sqe.getSQLState()
                    + " |Exception: " + sqe.getMessage().toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close ResultSet {0}", e.getMessage());
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        logger.log(Level.INFO, "Exception on close Connection {0}", e.getMessage());
                    }
                }
            }
            if (catalog.size() < 1) {
                catalog.addElement(getDatabase());
            }

            return catalog;
        }
    }

    Vector schemas;

    /**
     * Get the value of schemas.
     *
     * @return Value of schemas.
     */
    public Vector getSchemas() {
        schemas = new Vector();
        ResultSet rs = null;
        //Connection con = null;
        DatabaseMetaData dmd = null;
        try {
            con = getCon();
            dmd = con.getMetaData();
            rs = dmd.getSchemas();

            if (rs != null) {
                while (rs.next()) {
                    schemas.addElement((String) rs.getString(1));
                }
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User " + getUser()
                    + " throws Exception while schematas from MetaData "
                    + " |JDBC-Driver: " + getDriverName()
                    + " |Database: " + getDatabase()
                    + " |Error-Code: " + sqe.getErrorCode()
                    + " |SQL-State: " + sqe.getSQLState()
                    + " |Exception: " + sqe.getMessage().toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close ResultSet {0}", e.getMessage());
                }
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        logger.log(Level.INFO, "Exception on close Connection {0}", e.getMessage());
                    }
                }
            }
        }
        if (schemas.size() < 1) {
            schemas.addElement("%");
        }
        return schemas;
    }

    private String spaceFixer(String s) {
        s = s.trim();
        if (s.indexOf(' ') > 0) {
            s = "'" + s + "'";
        }
        return s;
    }

    Vector tables;

    /**
     * Get the value of tables.
     *
     * @return Value of tables.
     */
    public Vector getTables(String db) {
        tables = new Vector();
        String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};

        // Speciallity for Empress
        if (getProductName().toLowerCase().startsWith("empress")) {
            tableTypes = null;
        }

        //Connection con = null;
        DatabaseMetaData dmd = null;
        ResultSet rs = null;

        try {
            con = getCon();
            dmd = con.getMetaData();
            rs = dmd.getTables(db, "%", "%", tableTypes);
            while (rs.next()) {
                logger.log(Level.FINEST, "Fetching Metadata, Table: "
                        + rs.getString(3));
                tables.addElement(spaceFixer(rs.getString(3)));
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User " + getUser()
                    + " throws Exception while getTables(string) from MetaData "
                    + " |JDBC-Driver: " + getDriverName()
                    + " |Database: " + getDatabase()
                    + " |Error-Code: " + sqe.getErrorCode()
                    + " |SQL-State: " + sqe.getSQLState()
                    + " |Exception: " + sqe.getMessage().toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close ResultSet {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close Connection {0}", e.getMessage());
                }
            }
        }
        return tables;
    }

    public Vector getTables(String db, String schema) {
        tables = new Vector();
        String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};
        StringBuffer tt = new StringBuffer();;
        for (int i = 0; i < tableTypes.length; i++) {
            tt.append(tableTypes[i] + ",");
        }

        //Connection con = null;
        DatabaseMetaData dmd = null;
        ResultSet rs = null;

        try {
            con = getCon();
            dmd = con.getMetaData();
            rs = dmd.getTables(db, schema, "%", tableTypes);
            while (rs.next()) {
                logger.log(Level.FINEST, "Fetching Metadata (for Schema:"
                        + schema + ", TableTypes: " + tt + "), Table: "
                        + rs.getString(3));
                tables.addElement(rs.getString(3));
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User " + getUser()
                    + " throws Exception while getTables(string,string) from MetaData "
                    + " |JDBC-Driver: " + getDriverName()
                    + " |Database: " + getDatabase()
                    + " |Error-Code: " + sqe.getErrorCode()
                    + " |SQL-State: " + sqe.getSQLState()
                    + " |Exception: " + sqe.getMessage().toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close ResultSet {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close Connection {0}", e.getMessage());
                }
            }
        }

        return tables;
    }

    public Vector getTables(String db, String schema, String[] type) {
        tables = new Vector();
        String[] tableTypes = type;
        StringBuffer tt = new StringBuffer();;
        for (int i = 0; i < tableTypes.length; i++) {
            tt.append(tableTypes[i] + ",");
        }

        // Speciallity for Empress
        if (getProductName().toLowerCase().startsWith("empress")) {
            tableTypes = null;
        }
        //Connection con = null;
        DatabaseMetaData dmd = null;
        ResultSet rs = null;

        try {
            con = getCon();
            dmd = con.getMetaData();
            rs = dmd.getTables(db, schema, "%", tableTypes);
            while (rs.next()) {
                logger.log(Level.FINEST, "Fetching Metadata (for Schema:"
                        + schema + ", Tabletypes: " + tt + "), Table: "
                        + rs.getString(3));
                tables.addElement(rs.getString(3));
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User " + getUser()
                    + " throws Exception while getTables(string,string,string) from MetaData "
                    + " |JDBC-Driver: " + getDriverName()
                    + " |Database: " + getDatabase()
                    + " |Error-Code: " + sqe.getErrorCode()
                    + " |SQL-State: " + sqe.getSQLState()
                    + " |Exception: " + sqe.getMessage().toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close ResultSet {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close Connection {0}", e.getMessage());
                }
            }
        }
        return tables;
    }

    public Vector getTables(String db, String schema, String[] type, String tableNamePattern) {
        tables = new Vector();
        String[] tableTypes = type;

        // Speciallity for Empress
        if (getProductName().toLowerCase().startsWith("empress")) {
            tableTypes = null;
        }
        //Connection con = null;
        DatabaseMetaData dmd = null;
        ResultSet rs = null;

        try {
            con = getCon();
            dmd = con.getMetaData();
            rs = dmd.getTables(db, schema, tableNamePattern, tableTypes);
            while (rs.next()) {
                tables.addElement(rs.getString(3));
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User " + getUser()
                    + " throws Exception while getTables(string,string,string,string) from MetaData "
                    + " |JDBC-Driver: " + getDriverName()
                    + " |Database: " + getDatabase()
                    + " |Error-Code: " + sqe.getErrorCode()
                    + " |SQL-State: " + sqe.getSQLState()
                    + " |Exception: " + sqe.getMessage().toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close ResultSet {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close Connection {0}", e.getMessage());
                }
            }
        }
        return tables;
    }

    public ArrayList<FunctionMetaData> getFunctions(String db, String schema) {
        ArrayList<FunctionMetaData> functions = new ArrayList();
        //Connection con = null;
        DatabaseMetaData dmd = null;
        ResultSet rs = null;
        ResultSet rsDet = null;
        try {
            con = getCon();
            dmd = con.getMetaData();
            rs = dmd.getFunctions(db, schema, "%");
            while (rs.next()) {
                FunctionMetaData fmd = new FunctionMetaData();
                fmd.setFunctionCatalog(rs.getString(1));
                fmd.setFunctionSchema(rs.getString(2));
                fmd.setFunctionName(rs.getString(3));
                fmd.setRemarks(rs.getString(4));
                fmd.setFunctionType(rs.getShort(5));
                fmd.setSpecificName(rs.getString(6));

                // and now the details
                HashMap<String, FunctionColumnMetaData> detailMap = new HashMap();
                rsDet = dmd.getFunctionColumns(fmd.getFunctionCatalog(), fmd.getFunctionSchema(), fmd.getFunctionName(), "%");
                while (rsDet.next()) {
                    FunctionColumnMetaData fcmd = new FunctionColumnMetaData();
                    fcmd.setFunctionCatalog(rsDet.getString(1));
                    fcmd.setFunctionSchema(rsDet.getString(2));
                    fcmd.setFunctionName(rsDet.getString(3));
                    fcmd.setColumnName(rsDet.getString(4));
                    fcmd.setColumnType(rsDet.getShort(5));
                    fcmd.setDataType(rsDet.getInt(6));
                    fcmd.setTypeName(rsDet.getString(7));
                    fcmd.setPrecision(rsDet.getInt(8));
                    fcmd.setLength(rsDet.getInt(9));
                    fcmd.setScale(rsDet.getShort(10));
                    fcmd.setRadix(rsDet.getShort(10));
                    fcmd.setNullable(rsDet.getShort(12));
                    fcmd.setRemarks(rsDet.getString(13));
                    fcmd.setCharOctetLength(rsDet.getInt(14));
                    fcmd.setOrdinalPosition(rsDet.getInt(15));
                    fcmd.setIsNullable(rsDet.getString(16));
                    fcmd.setSpecificName(rsDet.getString(17));
                    detailMap.put(fcmd.getColumnName(), fcmd);
                }
                rsDet.close();
                fmd.setColumnMetaData(detailMap);
                functions.add(fmd);
            }

        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User " + getUser()
                    + " throws Exception while getFunctions(String db, String schema)  from MetaData "
                    + " |JDBC-Driver: " + getDriverName()
                    + " |Database: " + getDatabase()
                    + " |Error-Code: " + sqe.getErrorCode()
                    + " |SQL-State: " + sqe.getSQLState()
                    + " |Exception: " + sqe.getMessage().toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close ResultSet {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close Connection {0}", e.getMessage());
                }
            }
        }
        return functions;
    }

    /**
     * Set the value of tables.
     *
     * @param v Value to assign to tables.
     */
    public void setTables(Vector v) {
        this.tables = v;
    }

    /**
     * retrieve all SQL-Keywords, available for this DB
     *
     */
    public Vector getSQLWords() {
        Vector v = new Vector();
        //Connection con = null;
        DatabaseMetaData dmd = null;
        ResultSet rs = null;
        ResultSet rsDet = null;
        try {
            con = getCon();
            dmd = con.getMetaData();
            rs = dmd.getTypeInfo();
            while (rs.next()) {
                FieldTypeInfo fti = new FieldTypeInfo();
                fti.setTypName(rs.getString(1));
                fti.setAutoIncrement(rs.getBoolean(12));
                fti.setDataType(rs.getInt(2));
                v.addElement((FieldTypeInfo) fti);
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User " + getUser()
                    + " throws Exception on getting SQL-KeyWords "
                    + " |JDBC-Driver: " + getDriverName()
                    + " |Database: " + getDatabase()
                    + " |Error-Code: " + sqe.getErrorCode()
                    + " |SQL-State: " + sqe.getSQLState()
                    + " |Exception: " + sqe.getMessage().toString());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close ResultSet {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close Connection {0}", e.getMessage());
                }
            }
        }
        return v;
    }

    String productName;

    /**
     * Get the value of productName.
     *
     * @return Value of productName.
     */
    public String getProductName() {     
        if (productName == null) {
         
            try {           
                con = DataSource.getInstance().getConnection();
                productName = DataSource.getInstance().getProductName();
                productVersion = DataSource.getInstance().getProductVersion();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
            } catch (PropertyVetoException ex) {
                logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING, "Exception while closing connection {0}", e.getMessage());
                    }
                }
            }
        
        }

        return productName;
    }

    /**
     * Set the value of productName.
     *
     * @param v Value to assign to productName.
     */
    private void setProductName() {

    }

    String productVersion;

    /**
     * Get the value of productVersion.
     *
     * @return Value of productVersion.
     */
    public String getProductVersion() {
        if (productVersion == null) {
            //Connection con = null;
            try {
                con = DataSource.getInstance().getConnection();
                productVersion = DataSource.getInstance().getProductVersion();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
            } catch (PropertyVetoException ex) {
                logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        logger.log(Level.WARNING, "Exception while closing connection {0}", e.getMessage());
                    }
                }
            }

        }

        return productVersion;
    }

    String driverName = null;

    /**
     * Get the value of driverName.
     *
     * @return Value of driverName.
     */
    public String getDriverName() {
        if (driverName == null) {
            try {
                //con = DataSource.getInstance().getConnection();
                driverName = DataSource.getInstance().getDmd().getDriverName();
                driverVersion = DataSource.getInstance().getDmd().getDriverVersion();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
            } catch (PropertyVetoException ex) {
                logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
            } finally {

            }
        }

        return driverName;
    }

    String driverVersion = null;

    /**
     * Get the value of driverVersion.
     *
     * @return Value of driverVersion.
     */
    public String getDriverVersion() {
        if (driverVersion == null) {
            try {
                //con = DataSource.getInstance().getConnection();
                driverName = DataSource.getInstance().getDmd().getDriverName();
                driverVersion = DataSource.getInstance().getDmd().getDriverVersion();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
            } catch (SQLException ex) {
                logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
            } catch (PropertyVetoException ex) {
                logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
            } finally {

            }
        }
        return driverVersion;
    }

    public SingleColumnInfo getColumnInfo(String db, String table, String column) {
        //Connection con = null;
        DatabaseMetaData dmd = null;
        SingleColumnInfo s = null;
        productName = "";
        try {
            con = getConnection();
            dmd = con.getMetaData();
            s = new SingleColumnInfo(db, table, column);
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User " + getUser()
                    + " throws Exception while reading ColumnInfo from MetaData "
                    + " |JDBC-Driver: " + getDriverName()
                    + " |Database: " + getDatabase()
                    + " |Error-Code: " + sqe.getErrorCode()
                    + " |SQL-State: " + sqe.getSQLState()
                    + " |Exception: " + sqe.getMessage().toString());
        } catch (Exception sqe) {
            logger.log(Level.SEVERE, "unexpected exception: {0}", sqe.getMessage().toString());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close Connection {0}", e.getMessage());
                }
            }
        }
        return s;
    }

    /*
     This fixes the names of the DB. Because of filesystem-DB names with Derby
     having path names like d:\dbs\derydbs\mydb that DOT does not like, we replace
     : and / and \ with _
     */
    private String fixDotNames(String s) {
        s = s.replace(':', '_');
        s = s.replace('/', '_');
        s = s.replace('\\', '_');
        return s;
    }

    /*
     This method creates a schema-file into a csv:
     tablename;fieldname;fieldtype;fieldlength;PrimaryKey
     where the fieldseaparator is a Tabulator
     */
    public boolean createSchemaFile(String fileName, String tableNamePattern, String userSchema) {
        setSchema(userSchema);
        boolean ok = true;

        setFileName(new File(fileName));
        String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};
        //String[] tableTypes = {"VIEW","SYNONYM"};

        getOut();
        if (out == null) {
            return false;
        }

        StringBuilder content = new StringBuilder();

        content.append("Table\tFieldname\tFieldType\tFieldLength\tPrimaryKey\r\n");

        Vector v2 = new Vector();
        v2 = getTables(getDatabase(), getSchema(), tableTypes, tableNamePattern);

        //Connection con = null;
        DatabaseMetaData dmd = null;

        try {
            con = getCon();
            dmd = con.getMetaData();

            int tableCounter = 0;
            for (int l = 0; l < v2.size(); l++) {

                Vector v1 = new Vector();
                v1 = getColumnNames((String) v2.elementAt(l));
                String fkMgr = "";
                if (v1.size() > 0) {
                    for (int k = 0; k < v1.size(); k++) {
                        SingleColumnInfo si = new SingleColumnInfo(getDatabase(), getSchema(), (String) v2.elementAt(l), (String) v1.elementAt(k));

                        // table Name
                        content.append((String) v2.elementAt(l)).append("\t");

                        // field name
                        content.append(si.getColumn_name()).append("\t");

                        // field Type
                        content.append(si.getType_name()).append("\t");

                        // field length
                        content.append(Integer.toString(si.getColumn_size())).append("\t");

                        // if Primary Key           
                        if (si.isPrimaryKey()) {
                            content.append("yes\r\n");
                        } else {
                            content.append("\r\n");
                        }

                    }
                }
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "User " + getUser()
                    + " throws Exception while reading ColumnInfo from MetaData "
                    + " |JDBC-Driver: " + getDriverName()
                    + " |Database: " + getDatabase()
                    + " |Error-Code: " + sqe.getErrorCode()
                    + " |SQL-State: " + sqe.getSQLState()
                    + " |Exception: " + sqe.getMessage().toString());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.INFO, "Exception on close Connection {0}", e.getMessage());
                }
            }
        }

        out.print(content.toString());

        out.flush();
        out.close();

        return true;
    }

    public boolean createDotFile(String fileName, String tableNamePattern, boolean digIntoIt, String userSchema) {
        setSchema(userSchema);

        setFileName(new File(fileName));
        String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};

        getOut();
        if (out == null) {
            return false;
        }

        StringBuilder head = new StringBuilder();
        StringBuilder box = new StringBuilder();
        StringBuilder relations = new StringBuilder();
        StringBuilder foot = new StringBuilder();

        head.append("digraph \"").append(fixDotNames(getDatabase())).append("\" {\r\n");
        //head.append("graph [\r\nrankdir = \"TB\"\r\n];\r\n");
        head.append("graph [\r\nrankdir = \"LR\"\r\n];\r\n");

        foot.append("}\r\n");

        Vector v2 = new Vector();
        v2 = getTables(getDatabase(), getSchema(), tableTypes, tableNamePattern);

        int tableCounter = 0;
        for (int l = 0; l < v2.size(); l++) {

            box.append("\"").append((String) v2.elementAt(l)).append("\" [\r\n");

            box.append("shape = none\r\n");
            box.append("label =  <<table border=\"1\" cellspacing=\"0\">\r\n");
            box.append("<tr><td port=\"").append((String) v2.elementAt(l)).append("\" border = \"1\" bgcolor=\"#BFFF00\" >").append((String) v2.elementAt(l)).append("</td></tr>\r\n");

            // Send information in steps of 100, just to point out, still being alive
            tableCounter++;
            if (tableCounter == 100) {
                logger.log(Level.INFO, " Table: {0} is no. {1} out of {2}", new Object[]{(String) v2.elementAt(l), l, v2.size()});
                tableCounter = 0;
            }

            //relations.append(fixDotNames(getDatabase())).append(":").append(fixDotNames(getDatabase())).append(" -> ").append((String) v2.elementAt(l)).append(":").append((String) v2.elementAt(l)).append("\r\n");
            Vector v1 = new Vector();
            v1 = getColumnNames((String) v2.elementAt(l));
            String fkMgr = "";
            if (v1.size() > 0) {
                for (int k = 0; k < v1.size(); k++) {
                    SingleColumnInfo si = new SingleColumnInfo(getDatabase(), getSchema(), (String) v2.elementAt(l), (String) v1.elementAt(k));
                    //if ( k > 1) box.append("| ");
                    //box.append(" <").append(si.getColumn_name()).append("> ").append(si.getColumn_name());   

                    if (si.isPrimaryKey()) {
                        box.append("<tr><td port=\"").append(si.getColumn_name()).append("\" align=\"left\" border= \"1\" ><font color=\"red\">").append(si.getColumn_name()).append("</font></td></tr>\r\n");
                    } else {
                        box.append("<tr><td port=\"").append(si.getColumn_name()).append("\" align=\"left\" border= \"1\" >").append(si.getColumn_name()).append("</td></tr>\r\n");
                    }

                    // Foreign Key Constraints
                    if (si.getExportedKeys().length() > 3) {
                        String temp = si.getExportedKeys();
                        StringTokenizer st0 = new StringTokenizer(si.getExportedKeys(), ",");
                        while (st0.hasMoreTokens()) {
                            fkMgr = st0.nextToken();

                            StringTokenizer st = new StringTokenizer(fkMgr, ".");
                            try {
                                relations.append(v2.elementAt(l)).append(":").append(v1.elementAt(k)).append(" -> ").append(st.nextToken()).append(":").append(st.nextToken()).append(" [dir = none];\r\n");
                            } catch (NoSuchElementException nse) {
                                // probably there is no such element :-)
                            } catch (Exception exception) {

                            }
                        }
                    }
                }
            }
            box.append("</table>>\r\n]\r\n");
        }

        /*
         we try to find relationships within the database by comparing attribute-names.
         if one attribute in one table has the same name as one in the other table and one of the fields is
         held as primary keywe consider a relatinship
         in between these two tables. 
        
         As this might consum a lot of information his function needs to be accepted.
         */
        if (digIntoIt) {

            for (int tables = 0; tables < v2.size(); tables++) {

                Vector columns = new Vector();
                columns = getColumnNames((String) v2.elementAt(tables));

                for (int tables2 = tables + 1; tables2 < v2.size(); tables2++) {

                    Vector columns2 = new Vector();
                    columns2 = getColumnNames((String) v2.elementAt(tables2));

                    for (int cols = 0; cols < columns.size(); cols++) {
                        for (int cols2 = 0; cols2 < columns2.size(); cols2++) {

                            String col1 = (String) columns.elementAt(cols);
                            String col2 = (String) columns2.elementAt(cols2);

                            // only for testing purpose
                            //System.out.print(".");
                            SingleColumnInfo si1 = new SingleColumnInfo(getDatabase(), getSchema(), (String) v2.elementAt(tables), col1);
                            SingleColumnInfo si2 = new SingleColumnInfo(getDatabase(), getSchema(), (String) v2.elementAt(tables2), col2);

                            if ((col1.equals(col2)) && ((si1.isPrimaryKey()) || (si2.isPrimaryKey()))) {
                                relations.append((String) v2.elementAt(tables)).append(":").append(col1);
                                relations.append(" -> ");
                                relations.append((String) v2.elementAt(tables2)).append(":").append(col2);
                                relations.append("\r\n");
                            }

                        }

                    }

                }

            }

        }

        out.print(head.toString());
        out.print(box.toString());
        out.print(relations.toString());
        out.print(foot.toString());

        out.flush();
        out.close();

        return true;
    }

    public DatabaseMetaData getDmd() {
        DatabaseMetaData dmd = null;
        //Connection con = null;
        try {
            con = getCon();
            dmd = con.getMetaData();
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Can not fetch MetaData [0]", e.getMessage());
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on close Connection [0]", e.getMessage());
                }
            }
        }
        return dmd;
    }

    public void printAllInfo(String fileName, String tableNamePattern) {
        setFileName(new File(fileName));
        String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};

        getOut();
        if (out != null) {
            logger.log(Level.INFO, "writing to file: " + fileName);
            out.println("Productname:         " + getProductName());
            out.println("Productversion:      " + getProductVersion());
            out.println("JDBC-Driver:         " + getDriverName());
            out.println("JDBC-Driver Version: " + getDriverVersion());

            out.println("Database:              " + getDatabase());
            out.println("Schema:                " + getSchema());
            out.println("\n");

            Vector v2 = new Vector();
            v2 = getTables(getDatabase(), getSchema(), tableTypes, tableNamePattern);

            for (int l = 0; l < v2.size(); l++) {
                out.println("\nTable " + (String) v2.elementAt(l));
                Vector pkv = new Vector();
                pkv = getPk2((String) v2.elementAt(l));
                if (pkv.size() > 0) {
                    out.print("\tPrimarykeys: ");
                    //System.out.println("Schema: " +(String) v0.elementAt(j) + "\tTable:" + v2.elementAt(l));
                    for (int m = 0; m < pkv.size(); m++) {

                        PrimaryKey pk = new PrimaryKey();
                        pk = (PrimaryKey) pkv.elementAt(m);
                        out.print("   " + pk.getColumnName());
                    }
                    out.println("");
                }
                Vector v1 = new Vector();
                v1 = getColumnNames((String) v2.elementAt(l));
                if (v1.size() > 0) {
                    for (int k = 0; k < v1.size(); k++) {
                        SingleColumnInfo si = new SingleColumnInfo(getDatabase(), getSchema(), (String) v2.elementAt(l), (String) v1.elementAt(k));
                        out.println("\tColumnname: " + si.getColumn_name());
                        out.println("\t\tType name : " + si.getType_name());
                        out.println("\t\tSize      : " + Integer.toString(si.getColumn_size()));
                        out.println("\t\tNullable  : " + si.getNullable());
                        if (si.getRemarks() != null) {
                            out.println("\t\tRemarks   : " + si.getRemarks());
                        }
                        if (si.getColumn_def() != null) {
                            out.println("\t\tColumn def: " + si.getColumn_def() + "\n");
                        }
                    }
                }
            }
            out.flush();
            out.close();
        }
    }

    public PrintWriter getOut() {
        if (getFileName() != null) {
            try {
                out = new PrintWriter(new FileOutputStream(getFileName()));
            } catch (FileNotFoundException fne) {
                logger.log(Level.WARNING, "Can not open file " + getFileName().getName());
                logger.log(Level.WARNING, "Message: " + fne.getMessage().toString());
                out = null;
            }
        }
        return out;
    }

    public String getAllInfo() {

        String s = "";
        s = "Productname:         " + getProductName() + "\n";
        s = s + "Productversion:      " + getProductVersion() + "\n";
        s = s + "JDBC-Driver:         " + getDriverName() + "\n";
        s = s + "JDBC-Driver Version: " + getDriverVersion() + "\n";

        Vector v = new Vector();
        Vector v0 = new Vector();
        Vector v1 = new Vector();
        Vector v2 = new Vector();

        v = getCatalog();
        s = s + "\nCatalogs:            \n";

        if (v.size() < 1) {
            v.addElement(getDatabase());
        }

        for (int i = 0; i < v.size(); i++) {

            s = s + (String) v.elementAt(i) + "\n";

            v0 = getSchemas();
            for (int j = 0; j < v0.size(); j++) {
                s = s + "\n\nSchema: " + (String) v0.elementAt(j) + "\n";

                v2 = new Vector();
                v2 = getTables((String) v.elementAt(i), (String) v0.elementAt(j));

                for (int l = 0; l < v2.size(); l++) {
                    s = s + "\n\tTable " + (String) v2.elementAt(l) + "\n";
                    Vector pkv = new Vector();
                    pkv = getPk((String) v2.elementAt(l));
                    s = s + "\t\tPrimarykeys: ";
                    //System.out.println("Schema: " +(String) v0.elementAt(j) + "\tTable:" + v2.elementAt(l));
                    for (int m = 0; m < pkv.size(); m++) {

                        PrimaryKey pk = new PrimaryKey();
                        pk = (PrimaryKey) pkv.elementAt(m);
                        s = s + "   " + pk.getColumnName();
                    }
                    s = s + "\n\n";

                    v1 = new Vector();
                    v1 = getColumnNames((String) v2.elementAt(l));
                    for (int k = 0; k < v1.size(); k++) {
                        SingleColumnInfo si = new SingleColumnInfo((String) v.elementAt(i), (String) v0.elementAt(j), (String) v2.elementAt(l), (String) v1.elementAt(k));
                        s = s + "\t\tColumnname: " + si.getColumn_name() + "\n";
                        s = s + "\t\tType name : " + si.getType_name() + "\n";
                        s = s + "\t\tSize      : " + Integer.toString(si.getColumn_size()) + "\n";
                        s = s + "\t\tNullable  : " + si.getNullable() + "\n";
                        s = s + "\t\tRemarks   : " + si.getRemarks() + "\n";
                        s = s + "\t\tColumn def: " + si.getColumn_def() + "\n\n";
                    }
                }
            }
        }
        return s;

    }

    public Vector getPk(String table) {
        Vector v = new Vector();
        //Connection con = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            DatabaseMetaData dmd = con.getMetaData();
            rs = dmd.getPrimaryKeys(null, null, table);
            while (rs.next()) {
                PrimaryKey pk = new PrimaryKey(rs.getString(3), rs.getString(4), rs.getShort(5), rs.getString(6));
                v.addElement((PrimaryKey) pk);
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "Exception on reading MetaData {0}", sqe.getMessage());
            System.out.println("\nError while reading Catalogs from MetaData\n"
                    + "Exception: \n" + sqe.getMessage()
                    + "\nError-Code: " + sqe.getErrorCode()
                    + "\nSQL-State: " + sqe.getSQLState() + "\n");
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on closing resultset {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on closing connection {0}", e.getMessage());
                }
            }
        }
        return v;
    }

    public Vector getPk2(String table) {
        Vector v = new Vector();
        //Connection con = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            DatabaseMetaData dmd = con.getMetaData();
            rs = dmd.getPrimaryKeys(getDatabase(), getSchema(), table);
            while (rs.next()) {
                PrimaryKey pk = new PrimaryKey(rs.getString(3), rs.getString(4), rs.getShort(5), rs.getString(6));
                v.addElement((PrimaryKey) pk);
            }
        } catch (SQLException sqe) {
            System.out.println("Error while reading Catalogs from MetaData\n"
                    + "Exception: " + sqe.getMessage()
                    + "\nError-Code: " + sqe.getErrorCode()
                    + "\nSQL-State: " + sqe.getSQLState());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on closing resultset {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on closing connection {0}", e.getMessage());
                }
            }
        }
        return v;
    }

    public String getTableDescription(String table, String leadingCharacter) {

        String s = "";
        Vector pkv = new Vector();
        pkv = getPk(table);
        s = s + leadingCharacter + "\t\tPrimarykeys: ";
        for (int m = 0; m < pkv.size(); m++) {
            PrimaryKey pk = new PrimaryKey();
            pk = (PrimaryKey) pkv.elementAt(m);
            s = s + "   " + pk.getColumnName();
        }
        s = s + "\n" + leadingCharacter + "\n";

        Vector v1 = new Vector();
        v1 = getColumnNames(table);
        for (int k = 0; k < v1.size(); k++) {
            SingleColumnInfo si = new SingleColumnInfo(getDatabase(), table, (String) v1.elementAt(k));
            s = s + leadingCharacter + "\t\tColumnname: " + si.getColumn_name() + "\n";
            s = s + leadingCharacter + "\t\tType name : " + si.getType_name() + "\n";
            s = s + leadingCharacter + "\t\tSize      : " + Integer.toString(si.getColumn_size()) + "\n";
            s = s + leadingCharacter + "\t\tNullable  : " + si.getNullable() + "\n";
            s = s + leadingCharacter + "\t\tRemarks   : " + si.getRemarks() + "\n";
            s = s + leadingCharacter + "\t\tColumn def: " + si.getColumn_def() + "\n" + leadingCharacter + "\n";
        }

        return s;
    }

    public Vector getColumnNames(String table) {

        Vector v = new Vector();
        //Connection con = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            DatabaseMetaData dmd = con.getMetaData();
            rs = dmd.getColumns(null, null, table, "%");
            while (rs.next()) {
                v.addElement((String) rs.getString(4));
            }
        } catch (SQLException sqe) {
            logger.log(Level.WARNING, "Exception while reading metadata {0}", sqe.getMessage());
            /*
            System.out.println("Error while reading Catalogs from MetaData");
            System.out.println("Exception: " + sqe.getMessage());
            System.out.println("Error-Code: " + sqe.getErrorCode());
            System.out.println("SQL-State: " + sqe.getSQLState() );
             */
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on closing resultset {0}", e.getMessage());
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Exception on closing connection {0}", e.getMessage());
                }
            }
        }

        return v;
    }

    private boolean runFind = true;

    public void setRunFind(boolean b) {
        runFind = b;
    }

    public boolean isRunFind() {
        return runFind;
    }

    public ArrayList findTableRow(final String pattern, final String queryElement, final String tableNamePattern) {
        final ArrayList<FindPattern> list;
        list = new ArrayList<>();

        String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};
        Vector v1 = new Vector();
        v1 = getTables(getDatabase(), getSchema(), tableTypes, tableNamePattern);

        for (int l = 0; l < v1.size(); l++) {

            // break the loop 
            if (!isRunFind()) {
                break;
            }

            // fetching the Columns
            Vector v2 = new Vector();
            v2 = getColumnNames((String) v1.elementAt(l));

            // looping the columns
            for (int i = 0; i < v2.size(); i++) {

                // break the loop 
                if (!isRunFind()) {
                    break;
                }
                try {
                    logger.log(Level.FINEST, "getting Fieldtype of {0} in Table {1}", new Object[]{(String) v2.elementAt(i), v1.elementAt(l)});

                    SingleColumnInfo sci = getColumnInfo(getDatabase(), (String) v1.elementAt(l), (String) v2.elementAt(i));
                    String s = "select " + v2.elementAt(i) + " from " + (String) v1.elementAt(l)
                            + " where " + v2.elementAt(i) + " " + queryElement + " ";

                    if (("other".equals(sci.getType_name().toLowerCase()))
                            || ("char".equals(sci.getType_name().toLowerCase()))
                            || ("varchar".equals(sci.getType_name().toLowerCase()))
                            || ("longvarchar".equals(sci.getType_name().toLowerCase()))
                            || ("binary".equals(sci.getType_name().toLowerCase()))
                            || ("longvarbinary".equals(sci.getType_name().toLowerCase()))
                            || ("date".equals(sci.getType_name().toLowerCase()))
                            || ("time".equals(sci.getType_name().toLowerCase()))
                            || ("datetime".equals(sci.getType_name().toLowerCase()))
                            || ("timestamp".equals(sci.getType_name().toLowerCase()))) {
                        if ("like".equalsIgnoreCase(queryElement)) {
                            s = s + "'%" + pattern + "%'";
                        } else {
                            s = s + "'" + pattern + "'";
                        }
                    } else {
                        s = s + pattern;
                    }

                    logger.log(Level.FINEST, "Query: {0} Fieldtype: {1}", new Object[]{s, sci.getType_name()});
                    ArrayList<FindPattern> result = execFindQuery(s, getDatabase(), (String) v1.elementAt(l), (String) v2.elementAt(i), pattern);
                    //ArrayList<FindPattern> result = execFindQuery(s);
                    if (result.size() > 0) {
                        list.addAll(result);
                    }
                } catch (Exception wex) {
                    logger.log(Level.FINE, "Exception while finding typename of {0} in table {1}", new Object[]{v2.elementAt(i), v1.elementAt(l)});
                }
            }

        }

        return list;
    }

    private ArrayList<FindPattern> execFindQuery(String query, String fDb, String fTable, String fColumn, String pattern) {
        //private ArrayList<FindPattern> execFindQuery(String query) {
        final ArrayList<FindPattern> list = new ArrayList();
        FindPattern result = null;
        logger.log(Level.FINE, "Query: {0}", query);

        try {
            java.sql.Statement statement = getCon().createStatement();
            java.sql.ResultSet resultSet;
            resultSet = statement.executeQuery(query);
            java.sql.ResultSetMetaData metaData = resultSet.getMetaData();
            /*
             String l1 = metaData.getCatalogName(1);
             String l2 = metaData.getTableName(1);
             String l3 = metaData.getColumnName(1);
             */
            String l1 = fDb;
            String l2 = fTable;
            String l3 = fColumn;

            int counter = 0;

            while (resultSet.next()) {
                result = new FindPattern();

                result.setDatabase(l1);
                result.setTable(l2);
                result.setColumn(l3);
                result.setPattern(pattern);

                //result.setPattern(resultSet.getString(1));
                /*
                 String l1 = metaData.getCatalogName(1);
                 String l2 = metaData.getTableName(1);
                 String l3 = metaData.getColumnName(1);

                 result.setDatabase(fDb);
                 result.setTable(fTable);
                 result.setColumn(fColumn);
                 result.setPattern(resultSet.getString(1));
                 */
                logger.log(Level.FINEST, "Setting values: {0}, {1}, {2}", new Object[]{l1, l2, l3});
                counter++;
                list.add(result);
                break;

            }
            logger.log(Level.FINEST, "Found {0} occurencies of pattern", counter);

        } catch (SQLException sqlex) {
            logger.log(Level.FINE, "Exception while executing query: {0}\r\nExeption is: {1}", new Object[]{query, sqlex.getMessage()});
        }
        return list;
    }

    public static void main(String args[]) {

        if (args.length != 1) {
            System.out.println("Syntax: java sql.fredy.admin.DbInfo DB ");
            System.exit(0);
        }
        String db = args[0];
        if (db.toLowerCase() == "null") {
            db = null;
        }
        DbInfo dbi = new DbInfo(db);
        dbi.printAllInfo(args[4], "%");
        dbi.close();

    }

    /**
     * @return the fileName
     */
    public File getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(File fileName) {
        this.fileName = fileName;
    }

}
