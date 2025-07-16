package sql.fredy.generate;

/**
 * GenerateWrapper generates a Wrapper in between the RDBMS and Java and is a
 * part of Admin... Version 2.1 2. Jan. 2002
 *
 * Version 2.2 8. May 2005 IT also generates a Bean whitout any connections to
 * the DB and with no DB-operations at all. There are two additional methods
 * generated within the xxxRow-Class, these are: public xxBean getBean() and
 * public xxBean nextRow() getBean returns an instance of the xxBeanObject from
 * the xxRow-Object nextRow returns a xxBean while looping throug resultsets
 *
 * Fredy Fischer
 *
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
import sql.fredy.share.BasicAdmin;
import sql.fredy.metadata.DbInfo;
import sql.fredy.metadata.SingleColumnInfo;
import sql.fredy.metadata.PrimaryKey;
import java.io.*;
import java.util.Calendar;
import java.util.*;
import java.util.logging.*;

public class GenerateWrapper extends BasicAdmin {

    private Logger logger = Logger.getLogger("sql.fredy.generate");

    private DbInfo dbi;

    boolean standAlone = true;

    /**
     * Get the value of standAlone.
     *
     * @return value of standAlone.
     */
    public boolean isStandAlone() {
        return standAlone;
    }

    /**
     * Set the value of standAlone.
     *
     * @param v Value to assign to standAlone.
     */
    public void setStandAlone(boolean v) {
        this.standAlone = v;
    }

    String applicationsDirectory = "";

    public void setApplicationsDirectory(String v) {
        applicationsDirectory = v;
    }

    public String getApplicationsDirectory() {
        return applicationsDirectory;
    }

    String theCode;

    /**
     * Get the value of theCode.
     *
     * @return value of theCode.
     */
    public String getTheCode() {
        return theCode;
    }

    /**
     * Set the value of theCode.
     *
     * @param v Value to assign to theCode.
     */
    public void setTheCode(String v) {
        this.theCode = v;
    }

    private StringBuffer beanCode;

    public void setBeanCode(StringBuffer beanCode) {
        this.beanCode = beanCode;
    }

    public StringBuffer getBeanCode() {
        return beanCode;
    }

    public GenerateWrapper(String database, String directory, String schema) {
        super(database);
        setSchema(schema);
        setApplicationsDirectory(directory);
        init();
    }

    public GenerateWrapper(String database,
            String table,
            String directory,
            String schema) {
        super(database, table, schema);
        setSchema(schema);
        setApplicationsDirectory(directory);
        init();
        doIt(table);
    }

    public static void main(String args[]) {
        String host = "localhost";
        String user = "";
        String password = "";
        String database = "";
        String table = "";
        String directory = System.getProperty("user.home");
        String schema = "%";

        int i = 0;
        while (i < args.length) {

            if ((args[i].equals("-d")) || (args[i].equals("-db"))) {
                i++;
                database = args[i];
            }
            if ((args[i].equals("-t")) || (args[i].equals("-table"))) {
                i++;
                table = args[i];
            }
            if ((args[i].equals("-dir")) || (args[i].equals("-directory"))) {
                i++;
                directory = args[i];
            }
            if ((args[i].equals("-s")) || (args[i].equals("-schema"))) {
                i++;
                schema = args[i];
            }
            i++;
        }

        if (args.length > 5) {
            GenerateWrapper g = new GenerateWrapper(database,
                    table,
                    directory,
                    schema);
        } else {
            System.out.println("Fredy's GenerateWrapper\n\n"
                    + "generates a Bean and a connection-class to the DB\n\n"
                    + "Usage:\n"
                    + "java sql.fredy.generate.GenerateWrapper -h host -u user -p password -d database -t table -dir directory -s schema");
        }
        //System.out.println(g.getTheCode());

    }

    private void init() {
        dbi = new DbInfo();
    }

    private String firstUpper(String s) {
        // I do not want to have spaces within the name
        s = s.replace(' ', '_');
        s = s.substring(0, 1).toUpperCase() + s.substring(1);
        return s;
    }

    private String toDay() {

        Calendar c = Calendar.getInstance();
        return Integer.toString(c.get(Calendar.YEAR)) + "-"
                + Integer.toString(c.get(Calendar.MONTH) + 1) + "-"
                + Integer.toString(c.get(Calendar.DATE));
    }

    private void doIt(String table) {
        logger.log(Level.INFO, "generating for table: " + table);
        // upper Part
        theCode = "package applications." + getDatabase() + ";\n\n"
                + "/** this has been generated by Fredy's Admin-Tool for SQL-Databases\n"
                + " *  Date: " + toDay() + "\n"
                + " * \n"
                + " *  RDBMS:    " + dbi.getProductName() + " Version: " + dbi.getProductVersion() + " \n"
                + " *  Database: " + getDatabase() + "\n"
                + " *  Table:    " + table + "\n *\n *  Description:\n"
                + " *\n" + dbi.getTableDescription(table, " * ") + "\n"
                + " *  Admin is free software (MIT-License)\n"
                + " *\n"
                + " *  Fredy Fischer\n"
                + " *  Hulmenweg 36\n"
                + " *  8405 Winterthur\n"
                + " *  Switzerland\n"
                + " *\n"
                + " * sql@hulmen.ch\n"
                + " *\n"
                + " **/\n"
                + "\n";

        beanCode = new StringBuffer(theCode);
        beanCode.append("import java.io.Serializable;\n\n");
        beanCode.append("public class " + firstUpper(table) + "Bean implements Serializable { \n");
        beanCode.append(getSetMethods(table));
        beanCode.append(getBeanConstructor(table));

        theCode = theCode
                + "import java.sql.*;\n"
                + "import java.util.Calendar;\n"
                + "import applications.basics.t_connect;\n"
                + "import applications.basics.BasicWrapper;\n"
                + "\n"
                + "public class " + firstUpper(table) + "Row extends BasicWrapper { \n"
                + prepStmt(table)
                + getSetMethods(table)
                + getConstructor(table)
                + initPreparedStatement(table)
                + closePreparedStatements(table)
                + nextMethode(table)
                + insertUpdate(table)
                + searchBy(table)
                + updateBy(table)
                + "\n}\n";

        // write it....
        String dir = getApplicationsDirectory();
        String wrapperFile = dir + File.separator + "applications" + File.separator
                + extractName(getDatabase())
                + File.separator + firstUpper(getTable()) + "Row.java";
        String beanFile = dir + File.separator + "applications" + File.separator
                + extractName(getDatabase())
                + File.separator + firstUpper(getTable()) + "Bean.java";

        sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter(getTheCode(), wrapperFile);
        sql.fredy.io.FileWriter fw2 = new sql.fredy.io.FileWriter(getBeanCode().toString(), beanFile);
    }

    private String extractName(String v) {
        File f = new File(v);
        if (f.exists()) {
            if (f.isDirectory()) {
                v = f.getName();
            }
            if (f.isFile()) {
                v = f.getName();
            }
        }

        return v;
    }

    // getting where condition for PrimaryKey
    private String getWhereforPK() {
        String s = " where ";
        Vector pk = new Vector();

        pk = dbi.getPk(getTable());
        for (int i = 0; i < pk.size(); i++) {
            if (i > 0) {
                s = s + " and ";
            }
            PrimaryKey primkey = new PrimaryKey();
            primkey = (PrimaryKey) pk.elementAt(i);
            String b = (String) primkey.getColumnName();
            String komma = getKomma(b);
            //s = s + b + " = " + komma + "\" + get" + firstUpper(b) + "() +\"" + komma + "\"";
            s = s + b + " = ? ";
        }

        return s;
    }

    // Creating code-fragment for where condition for preparedStatements
    private String getPKstmt(String trailor, String table, int j) {

        String s = "";
        Vector pk = new Vector();

        pk = dbi.getPk(table);
        for (int i = 0; i < pk.size(); i++) {
            j = j + 1;

            PrimaryKey primkey = new PrimaryKey();
            primkey = (PrimaryKey) pk.elementAt(i);

            SingleColumnInfo sci = new SingleColumnInfo();
            sci = dbi.getColumnInfo(getDatabase(), table, (String) primkey.getColumnName());
            s = s + "      " + trailor + ".set" + sqlType(sci.getData_type())
                    + "(" + Integer.toString(j) + ",get" + firstUpper(sci.getColumn_name())
                    + "());\n";
        }

        return s;
    }

    public String getKomma(String column) {
        String s = "";
        SingleColumnInfo sci = new SingleColumnInfo();
        sci = dbi.getColumnInfo(getDatabase(), getTable(), column);

        if (sci.getData_type() == java.sql.Types.CHAR) {
            s = "'";
        }
        if (sci.getData_type() == java.sql.Types.VARCHAR) {
            s = "'";
        }
        if (sci.getData_type() == java.sql.Types.BINARY) {
            s = "'";
        }
        if (sci.getData_type() == java.sql.Types.LONGVARBINARY) {
            s = "'";
        }
        if (sci.getData_type() == java.sql.Types.VARBINARY) {
            s = "'";
        }
        if (sci.getData_type() == java.sql.Types.TIME) {
            s = "'";
        }
        if (sci.getData_type() == java.sql.Types.DATE) {
            s = "'";
        }
        if (sci.getData_type() == java.sql.Types.TIMESTAMP) {
            s = "'";
        }
        if (sci.getData_type() == java.sql.Types.INTEGER) {
            s = "";
        }
        if (sci.getData_type() == java.sql.Types.NUMERIC) {
            s = "";
        }
        if (sci.getData_type() == java.sql.Types.DOUBLE) {
            s = "";
        }
        if (sci.getData_type() == java.sql.Types.FLOAT) {
            s = "";
        }
        if (sci.getData_type() == java.sql.Types.BIGINT) {
            s = "";
        }
        if (sci.getData_type() == java.sql.Types.BIT) {
            s = "";
        }
        if (sci.getData_type() == java.sql.Types.BLOB) {
            s = "'";
        }
        if (sci.getData_type() == java.sql.Types.CLOB) {
            s = "";
        }
        if (sci.getData_type() == java.sql.Types.DECIMAL) {
            s = "";
        }
        if (sci.getData_type() == java.sql.Types.OTHER) {
            s = "'";
        }

        return s;
    }

    //Data-Type things
    private String javaType(short colType) {

        String s = "String";
        if (colType == java.sql.Types.CHAR) {
            s = "String";
        }
        if (colType == java.sql.Types.VARCHAR) {
            s = "String";
        }
        if (colType == java.sql.Types.BINARY) {
            s = "String";
        }
        if (colType == java.sql.Types.LONGVARBINARY) {
            s = "String";
        }
        if (colType == java.sql.Types.VARBINARY) {
            s = "String";
        }
        if (colType == java.sql.Types.TIME) {
            s = "java.sql.Time";
        }
        if (colType == java.sql.Types.DATE) {
            s = "java.sql.Date";
        }
        if (colType == java.sql.Types.TIMESTAMP) {
            s = "java.sql.Timestamp";
        }
        if (colType == java.sql.Types.INTEGER) {
            s = "int";
        }
        if (colType == java.sql.Types.NUMERIC) {
            s = "float";
        }
        if (colType == java.sql.Types.DOUBLE) {
            s = "double";
        }
        if (colType == java.sql.Types.FLOAT) {
            s = "float";
        }
        if (colType == java.sql.Types.BIGINT) {
            s = "int";
        }
        if (colType == java.sql.Types.BIT) {
            s = "int";
        }
        if (colType == java.sql.Types.BLOB) {
            s = "String";
        }
        if (colType == java.sql.Types.CLOB) {
            s = "String";
        }
        if (colType == java.sql.Types.DECIMAL) {
            s = "float";
        }
        if (colType == java.sql.Types.OTHER) {
            s = "String";
        }

        return s;

    }
    //SQL-Type things

    private String sqlType(short colType) {

        String s = "String";
        if (colType == java.sql.Types.CHAR) {
            s = "String";
        }
        if (colType == java.sql.Types.VARCHAR) {
            s = "String";
        }
        if (colType == java.sql.Types.BINARY) {
            s = "String";
        }
        if (colType == java.sql.Types.LONGVARBINARY) {
            s = "String";
        }
        if (colType == java.sql.Types.VARBINARY) {
            s = "String";
        }
        if (colType == java.sql.Types.TIME) {
            s = "Time";
        }
        if (colType == java.sql.Types.DATE) {
            s = "Date";
        }
        if (colType == java.sql.Types.TIMESTAMP) {
            s = "Timestamp";
        }
        if (colType == java.sql.Types.INTEGER) {
            s = "Int";
        }
        if (colType == java.sql.Types.NUMERIC) {
            s = "Float";
        }
        if (colType == java.sql.Types.DOUBLE) {
            s = "Double";
        }
        if (colType == java.sql.Types.FLOAT) {
            s = "Float";
        }
        if (colType == java.sql.Types.BIGINT) {
            s = "Int";
        }
        if (colType == java.sql.Types.BIT) {
            s = "Int";
        }
        if (colType == java.sql.Types.BLOB) {
            s = "String";
        }
        if (colType == java.sql.Types.CLOB) {
            s = "String";
        }
        if (colType == java.sql.Types.DECIMAL) {
            s = "Float";
        }
        if (colType == java.sql.Types.OTHER) {
            s = "String";
        }

        return s;

    }

    private String getBeanConstructor(String table) {
        String constructor = "";
        constructor = constructor
                + "\n   /**\n"
                + "    * this is just the default constructor\n"
                + "    * that is here just be be here\n"
                + "    **/\n"
                + "\n   public " + firstUpper(table) + "Bean () {\n"
                + "      // default constructor\n"
                + "      }\n\n"
                + "      public " + firstUpper(table) + "Bean (\n";

        // column related values
        Vector v = dbi.getColumnNames(table);
        String s3 = "";
        String s2 = "";

        for (int i = 0; i < v.size(); i++) {
            SingleColumnInfo sci = new SingleColumnInfo();
            sci = dbi.getColumnInfo(getDatabase(), table, (String) v.elementAt(i));

            // this generates the rest of the Full Constructor
            if (i > 0) {
                s2 = s2 + ",\n";
            }
            s2 = s2 + "                       " + javaType(sci.getData_type()) + " " + sci.getColumn_name();
            // this is the set-Methodes initializing with values from constructor
            s3 = s3 + "         this.set" + firstUpper(sci.getColumn_name()) + "("
                    + sci.getColumn_name() + ");\n";

        }

        s2 = s2 + ") {\n";
        s3 = s3 + "      }\n}\n\n";

        return constructor + s2 + s3;
    }

    private String getConstructor(String table) {

        // connection only constructor
        String cos = "";
        cos = "\n    public " + firstUpper(table) + "Row" + "(java.sql.Connection con) {\n\n"
                + "         super(con);\n\n";

        // datasource constructor
        String dsc = "";
        dsc = "\n/**\n"
                + " * call this constructor, if you know the name of the datasource\n"
                + " * and you have a reachable ConnectionPool running, e.g. Tomcat\n"
                + " **/\n"
                + "\n    public " + firstUpper(table) + "Row" + "(String datasource) {\n\n"
                + "         super(datasource);\n\n";

        // short Constructor
        String ss = "";
        ss = "\n    public " + firstUpper(table) + "Row" + "(String host,\n"
                + "                       String user,\n"
                + "                       String password) {\n\n"
                + "         super(host,user,password,\"" + getDatabase() + "\",\"" + table + "\",\"%\");\n\n";

        // Basic constructor with connection-object (t_con)
        String sc1 = "";
        sc1 = "\n    public " + firstUpper(table) + "Row" + "(t_connect con) {\n\n"
                + "         super(con);\n"
                + "         setSchema(\"%\");\n"
                + "         setTable(\"" + table + "\");\n\n";

        // Basic constructor with connection-object (t_con), table and schema
        String sc2 = "";
        sc2 = "\n    public " + firstUpper(table) + "Row" + "(t_connect con,\n"
                + "                       String table,\n"
                + "                       String schema) {\n\n"
                + "         super(con,table,schema);\n"
                + "         setSchema(schema);\n"
                + "         setTable(table);\n\n";

        //  generic Constructor
        String s1 = "";
        s1 = s1 + "\n    public " + firstUpper(table) + "Row" + "(String host,\n"
                + "                       String user,\n"
                + "                       String password,\n"
                + "                       String database,\n"
                + "                       String table,\n"
                + "                       String schema) {\n\n"
                + "         super(host,user,password,database,table,schema);\n\n";

        // Full Constructor
        String s2 = "";
        s2 = "\n    public " + firstUpper(getTable()) + "Row" + "(String host,\n"
                + "                       String user,\n"
                + "                       String password,\n"
                + "                       String database,\n"
                + "                       String table,\n"
                + "                       String schema";

        // column related values
        Vector v = dbi.getColumnNames(table);
        String s3 = "";
        String s4 = "";
        for (int i = 0; i < v.size(); i++) {
            SingleColumnInfo sci = new SingleColumnInfo();
            sci = dbi.getColumnInfo(getDatabase(), table, (String) v.elementAt(i));

            // this is the Set-Methodes with init-values
            s4 = "         this.set" + firstUpper(sci.getColumn_name()) + "("
                    + getInitValue(javaType(sci.getData_type())) + ");\n";
            cos = cos + s4;
            dsc = dsc + s4;
            s1 = s1 + s4;
            ss = ss + s4;
            sc1 = sc1 + s4;
            sc2 = sc2 + s4;

            // this generates the rest of the Full Constructor
            s2 = s2 + ",\n" + "                       " + javaType(sci.getData_type()) + " " + sci.getColumn_name();

            // this is the set-Methodes initializing with values from constructor
            s3 = s3 + "         this.set" + firstUpper(sci.getColumn_name()) + "("
                    + sci.getColumn_name() + ");\n";

        }
        cos = cos + "       }\n\n";
        dsc = dsc + "       }\n\n";
        s1 = s1 + "       }\n\n";
        s2 = s2 + ") {\n";
        s2 = s2 + "         super(host,user,password,database,table,schema);\n\n";
        s3 = s3 + "       }\n\n";
        ss = ss + "       }\n\n";
        sc1 = sc1 + "       }\n\n";
        sc2 = sc2 + "       }\n\n";

        return cos + dsc + ss + s1 + s2 + s3 + sc1 + sc2;
    }

    private String getInitValue(String s) {
        String t = "\"\"";
        if (s.startsWith("int")) {
            t = "0";
        }
        if (s.startsWith("double")) {
            t = "0.0";
        }
        if (s.startsWith("float")) {
            t = "0";
        }
        if (s.startsWith("decimal")) {
            t = "0";
        }
        if (s.startsWith("java.sql.Date")) {
            t = "java.sql.Date.valueOf(toDay())";
        }
        if (s.endsWith("Timestamp")) {
            t = "new java.sql.Timestamp(System.currentTimeMillis())";
        }

        return t;
    }

    private String prepStmt(String table) {
        String s = "\n"
                + "    // all the prepared Statements used to treat this table\n"
                + "    private PreparedStatement insertStatement;\n"
                + "    private PreparedStatement updateStatement;\n"
                + "    private PreparedStatement deleteStatement;\n";

        Vector v = dbi.getColumnNames(table);
        for (int i = 0; i < v.size(); i++) {
            s = s
                    + "    private PreparedStatement stmtSearch" + firstUpper((String) v.elementAt(i)) + ";\n"
                    + "    private PreparedStatement stmtUpdate" + firstUpper((String) v.elementAt(i)) + ";\n";
        }

        return s;
    }

    private String closePreparedStatements(String table) {
        String s = "    public void close() {\n"
                + "       try {\n"
                + "          insertStatement.close();\n"
                + "          updateStatement.close();\n"
                + "          deleteStatement.close();\n";
        Vector v = dbi.getColumnNames(table);
        for (int i = 0; i < v.size(); i++) {
            s = s + "          stmtUpdate" + firstUpper((String) v.elementAt(i)) + ".close();\n";
            s = s + "          stmtSearch" + firstUpper((String) v.elementAt(i)) + ".close();\n";
        }
        s = s + "          con.closeCon();\n";
        s = s + "       } catch (SQLException sqle) {\n"
                + "          System.out.println(\"SQL-Exception while creating prepared Statements!\"+ sqle.getMessage());\n "
                + "       }\n "
                + "    }\n\n";
        return s;
    }

    private String initPreparedStatement(String table) {
        String s = "\n   // this section creates the prepared Statements\n"
                + "    // for the DB-operations\n"
                + "    public void initPreparedStatements() { \n"
                + "     try {\n";

        String insert = "      insertStatement = con.con.prepareStatement(\"insert into " + table + "(\" + \n            \"";
        String insert2 = "      \"(";
        String delete = "      deleteStatement = con.con.prepareStatement(\"delete from " + table + getWhereforPK() + "\");\n";
        String update = "      updateStatement = con.con.prepareStatement(\"update " + table + " set \" + \n            \"";
        String otherSearch = "";
        String otherUpdate = "";

        Vector v = dbi.getColumnNames(table);
        for (int i = 0; i < v.size(); i++) {
            if (i > 0) {
                insert = insert + ",\" + \n            \"";
                update = update + ",\" + \n            \"";
                insert2 = insert2 + ",";
            }
            insert = insert + (String) v.elementAt(i);
            insert2 = insert2 + "?";
            update = update + (String) v.elementAt(i) + " = ? ";

            otherUpdate = otherUpdate + "      "
                    + "stmtUpdate" + firstUpper((String) v.elementAt(i)) + " = con.con.prepareStatement(\"update " + table + " set "
                    + (String) v.elementAt(i) + " = ? " + getWhereforPK() + "\");\n";

            otherSearch = otherSearch + "      "
                    + //	"stmtSearch" + firstUpper((String)v.elementAt(i)) + " = con.con.prepareStatement(\"select * from " + table + getWhereforPK() + "\");\n";
                    "stmtSearch" + firstUpper((String) v.elementAt(i)) + " = con.con.prepareStatement(\"select * from " + table + " where " + (String) v.elementAt(i) + " = ?\");\n";

        }
        insert = insert + ")\" +\n" + "          \" VALUES \" +\n      " + insert2 + ")\");\n\n";
        update = update + "\" +\n            \" " + getWhereforPK() + "\");\n\n";

        String c = "     } catch ( SQLException sqlException) {\n"
                + "          System.out.println(\"SQL-Exception while creating prepared Statements!\\n\"+"
                + " sqlException.getMessage());\n     }";
        return s + insert + update + delete + "\n" + otherUpdate + otherSearch + c + "\n    }\n\n";

    }

    private String getSetMethods(String table) {
        String s = "\n";
        String bean = "\n\n"
                + "    /**\n"
                + "       * get the value of the bean derived out of the table " + table + "\n"
                + "       * @return value of " + table + "Bean \n"
                + "       */\n"
                + "    public synchronized " + firstUpper(table) + "Bean getBean() {\n"
                + "       " + firstUpper(table) + "Bean " + table.toLowerCase() + "Bean = new " + firstUpper(table) + "Bean();\n";

        Vector v = dbi.getColumnNames(table);
        for (int i = 0; i < v.size(); i++) {
            SingleColumnInfo sci = new SingleColumnInfo();
            sci = dbi.getColumnInfo(getDatabase(), table, (String) v.elementAt(i));
            s = s + "\n    " + javaType(sci.getData_type()) + " " + sci.getColumn_name() + ";\n\n";
            s = s + "    /**\n"
                    + "       * get the value of the column " + sci.getColumn_name() + ";\n"
                    + "       * @return value of " + sci.getColumn_name() + ";\n"
                    + "       */\n"
                    + "    public synchronized " + javaType(sci.getData_type()) + " get"
                    + firstUpper(sci.getColumn_name()) + "() { return "
                    + sci.getColumn_name() + "; }\n\n";

            s = s + "    /**\n"
                    + "       * set the value of the column " + sci.getColumn_name() + ";\n"
                    + "       * @param v value to assign to " + sci.getColumn_name() + ";\n"
                    + "       */\n"
                    + "    public synchronized void set" + firstUpper(sci.getColumn_name()) + "("
                    + javaType(sci.getData_type()) + " v) { this." + sci.getColumn_name() + " = v; }\n\n";

            bean = bean
                    + "       " + table.toLowerCase() + "Bean.set" + firstUpper(sci.getColumn_name())
                    + "(get" + firstUpper(sci.getColumn_name()) + "());\n";

        }
        bean = bean + "       return " + table.toLowerCase() + "Bean;\n    }\n";
        return s + bean;
    }

    private String searchMethods(String table) {
        String s = "    /**\n"
                + "       * these are the search-Methods\n"
                + "       * divided up into different sections:\n"
                + "       * searchAll retrieves all data from the Table\n"
                + "       * searchBy[ColumnName]\n"
                + "       *   if you set exact to true, returns exact the corresponding row\n"
                + "       *   if you set exact to false, returns all rows corresponding like-Statement\n"
                + "       *   where the true-Version is treaten as a preparedStatement\n"
                + "       **/\n\n";
        Vector v = dbi.getColumnNames(table);
        for (int i = 0; i < v.size(); i++) {
            SingleColumnInfo sci = new SingleColumnInfo();
            sci = dbi.getColumnInfo(getDatabase(), table, (String) v.elementAt(i));
            s = s + "    public String searchBy" + firstUpper(sci.getColumn_name())
                    + "(" + javaType(sci.getData_type()) + " " + sci.getColumn_name() + ",boolean exact) }\n"
                    + "       String e = \"like\";\n"
                    + "       if ( exact ) e = \"=\";\n"
                    + "       setQuery(\"select * from " + table + " where " + sci.getColumn_name()
                    + " \" + e + \" \" + ";

            String s1 = "'" + sci.getColumn_name() + "'";
            if (javaType(sci.getData_type()).startsWith("int")) {
                s1 = "Integer.toString(" + sci.getColumn_name() + ")";
            }
            if (javaType(sci.getData_type()).startsWith("float")) {
                s1 = "Float.toString(" + sci.getColumn_name() + ")";
            }
            if (javaType(sci.getData_type()).startsWith("double")) {
                s1 = "Double.toString(" + sci.getColumn_name() + ")";
            }
            if (javaType(sci.getData_type()).startsWith("java.sql.Date")) {
                s1 = "\"'\" + " + sci.getColumn_name() + " + \"'\"";
            }

        }

        return s;
    }

    // this Methode creates generice retrieve part as the second step of a read-operation
    private String nextMethode(String table) {
        String n = "\n\n"
                + " /**\n"
                + "   * execute this Method to get the next row as a simple bean without any DB-methods in\n"
                + "   * of a resultSet after a query has been initiated\n"
                + "  **/";

        String s = "\n\n"
                + " /**\n"
                + "   * execute this Method to get the next row\n"
                + "   * of a resultSet after a query has been initiated\n"
                + "  **/";

        s = s + "\n" + "    public " + firstUpper(table) + "Row next() {\n"
                + "      " + firstUpper(table) + "Row k = new " + firstUpper(table)
                + "Row(super.getCon().con);\n"
                + "      try {\n"
                + "           if ( sqlresult.next()) { \n";

        n = n + "\n" + "    public " + firstUpper(table) + "Bean nextRow() {\n"
                + "      " + firstUpper(table) + "Bean k = new " + firstUpper(table) + "Bean();\n"
                + "      try {\n"
                + "           if ( sqlresult.next()) { \n";

        // the line  "Row(super.getCon());\n" +  has been changed to  "Row(super.getCon().con);\n" +
        // because better support of t_connect-less operation, especially when using connection pools
        // Fredy, 15. October 2003
        Vector v = dbi.getColumnNames(table);
        String k = "";
        for (int i = 0; i < v.size(); i++) {
            SingleColumnInfo sci = new SingleColumnInfo();
            sci = dbi.getColumnInfo(getDatabase(), table, (String) v.elementAt(i));
            k = k + "             k.set" + firstUpper(sci.getColumn_name())
                    + "( sqlresult.get" + sqlType(sci.getData_type()) + "(\"" + sci.getColumn_name() + "\") );\n";
        }
        k = k + "             return k;\n"
                + "           } else { return null; }\n";
        k = k + "      } catch  (Exception e) { return null; } \n    }\n";

        s = s + k;
        n = n + k + "\n\n";

        return s + n;
    }

    // here we create standard methods like insert, update, delete
    private String insertUpdate(String table) {

        String try1 = "     try { \n";
        String try2 = "     } catch (SQLException sqlexception ) { return sqlexception.getMessage(); }\n";
        String try3 = "     } catch (Exception exception ) { return exception.getMessage(); }\n";

        String insert = "\n\n"
                + " /**\n"
                + "   * this method inserts a row into the table\n"
                + "   * and returns ok if it was succesfull\n"
                + "   * \n"
                + "  **/";
        insert = insert + "\n" + "    public String insert() {\n" + try1;

        String update = "\n\n"
                + " /**\n"
                + "   * this method updates this row\n"
                + "   * and returns ok if it was succesfull \n"
                + "   * \n"
                + "  **/";
        update = update + "\n" + "    public String update() {\n" + try1;

        String delete = "\n\n"
                + " /**\n"
                + "   * this method deletes this row\n"
                + "   * and returns ok if it was succesfull \n"
                + "   * \n"
                + "  **/";
        delete = delete + "\n" + "    public String delete() {\n" + try1;
        delete = delete + getPKstmt("deleteStatement", table, 0);

        Vector v = dbi.getColumnNames(table);
        for (int i = 0; i < v.size(); i++) {
            SingleColumnInfo sci = new SingleColumnInfo();
            sci = dbi.getColumnInfo(getDatabase(), table, (String) v.elementAt(i));
            insert = insert + "      insertStatement.set" + sqlType(sci.getData_type())
                    + "(" + Integer.toString(i + 1) + ",get" + firstUpper(sci.getColumn_name())
                    + "());\n";
            update = update + "      updateStatement.set" + sqlType(sci.getData_type())
                    + "(" + Integer.toString(i + 1) + ",get" + firstUpper(sci.getColumn_name())
                    + "());\n";

        }

        // now we have to add the primarykey-thing for the update query
        update = update + getPKstmt("updateStatement", table, v.size());

        insert = insert + "      return execQuery(insertStatement);\n " + try2 + "   }\n\n";
        update = update + "      return execQuery(updateStatement);\n " + try2 + "   }\n\n";
        delete = delete + "      return execQuery(deleteStatement);\n " + try3 + "   }\n\n";

        return insert + update + delete;
    }

    // generate searchByFieldNAME Methods
    private String searchBy(String table) {
        String s = "\n\n"
                + " /**\n"
                + "   * the following methodes enables to search rows\n"
                + "   * by each Field.\n"
                + "   * Where you can select in between a \n"
                + "   * <b>equal to (=)</b> query or a <b>like</b> query \n"
                + "   * by setting the boolean to <b>true = equal</b> or \n"
                + "   * <b>false = like</b>\n"
                + "   **/\n\n";

        Vector v = dbi.getColumnNames(table);
        for (int i = 0; i < v.size(); i++) {
            SingleColumnInfo sci = new SingleColumnInfo();
            sci = dbi.getColumnInfo(getDatabase(), table, (String) v.elementAt(i));

            s = s
                    + "\n"
                    + "    public String searchBy" + firstUpper((String) v.elementAt(i))
                    + "(" + javaType(sci.getData_type()) + " v,boolean exact) throws SQLException {\n"
                    + "        if (exact) {\n"
                    + "           stmtSearch" + firstUpper((String) v.elementAt(i))
                    + ".set" + sqlType(sci.getData_type())
                    + "(1,v);\n"
                    + "           return selectQuery(stmtSearch"
                    + firstUpper((String) v.elementAt(i)) + ");\n"
                    + "        } else {\n"
                    + "           PreparedStatement p = tempStatement("
                    + "\"select * from " + table + " where "
                    + (String) v.elementAt(i) + " like ?\");\n"
                    + "           p.set" + sqlType(sci.getData_type())
                    + "(1,v);\n"
                    + "           return selectQuery(p);\n "
                    + "        }\n"
                    + "    }\n";
        }
        return s;
    }

    // generate updateBy FIELD_NAME Methods
    /**
     * IDEE:
     *
     * public String updateNotes() { stmtUpdateNotes.setString(this.getNotes());
     * stmtUpdateNotes.setInt(this.getId()); return execQuery(stmtUpdateNotes);
     * }
     *
     */
    private String updateBy(String table) {
        String s = "\n\n"
                + " /**\n"
                + "   * the following methodes enables to update every single Field \n"
                + "   **/\n\n";

        Vector v = dbi.getColumnNames(table);
        for (int i = 0; i < v.size(); i++) {
            SingleColumnInfo sci = new SingleColumnInfo();
            sci = dbi.getColumnInfo(getDatabase(), table, (String) v.elementAt(i));

            s = s
                    + "\n"
                    + "    public String update" + firstUpper((String) v.elementAt(i))
                    + "()  throws SQLException  {\n"
                    + "        stmtUpdate" + firstUpper((String) v.elementAt(i))
                    + ".set" + sqlType(sci.getData_type())
                    + "( 1, get" + firstUpper(sci.getColumn_name()) + "() );\n"
                    + "  " + getPKstmt("stmtUpdate" + firstUpper((String) v.elementAt(i)), table, 1)
                    + "        return execQuery(stmtUpdate" + firstUpper((String) v.elementAt(i)) + ");\n"
                    + "   }\n\n";
        }

        return s;
    }
}
