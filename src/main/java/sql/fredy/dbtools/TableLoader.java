/*

 This loader is capabble to read a table in a source Database, create an according Table in a destination database
 Then it reads the content of the source table and copies it to the newly created destination table

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
package sql.fredy.dbtools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sql@hulmen.ch
 */
public class TableLoader {

    private boolean TEST = false;
    private int CHUNKSIZE = 10000;
    //private int CHUNKSIZE = 1;  /// nimm das, wenn du doofe Fehler hast...
    private boolean initial = true;  // actually I can only load initially

    private Connection sourceConnection;
    private Connection destinationConnection;

    DatabaseMetaData sourceDmd;
    DatabaseMetaData destDmd;

    String readQuery;
    String destQuery;

    private String srcDb, destDb;
    private String srcSchema, destSchema;

    private String table;
    private String tableNamePattern;
    private ArrayList<TabellenSpalteInfo> spalten;
    private ArrayList<String> tableNames;
    private Logger logger = Logger.getLogger("loader");

    DatabaseMetaData dmd; // = c.getMetaData();

    public TableLoader(boolean test, String sourceDB, String destDB, String sourceSchema, String destinationSchema, Connection source, Connection dest) {
        setTEST(test);
        setSourceConnection(source);
        setDestinationConnection(dest);
        setSrcDb(sourceDB);
        setDestDb(destDB);
        setSrcSchema(sourceSchema);
        setDestSchema(destinationSchema);
    }

    public void loadIt(ArrayList<String> toLoad) {

        try {
            dmd = destinationConnection.getMetaData();
        } catch (SQLException ex) {
            Logger.getLogger(TableLoader.class.getName()).log(Level.SEVERE, "Error when fetching Destination MetaData", ex);
        }

        for (int i = 0; i < toLoad.size(); i++) {
            String t = toLoad.get(i);
            setTableNamePattern(t);
            createTableInfo();
            runImport();
        }

        close();

    }

    private int importLooper;
    private String aktuelleTabelle;
    /*
     Jede der Tabellen gem. TableNamePattern wird in einem eigenen Thread abgearbeitet
     Am Schluss der Verarbeitung muss gewartet werden, bis alle fertig sind...
     */

    public void runImport() {

        /*
         loope durch die Tabellen und importiere
       
         */
        for (importLooper = 0; importLooper < tableNames.size(); importLooper++) {
            aktuelleTabelle = tableNames.get(importLooper);

            processData(aktuelleTabelle);
        }

    }

    public static void main(String args[]) {

        String isTest = readFromPrompt("Test y/n", "y");
        boolean test = true;
        if (isTest.equalsIgnoreCase("n")) {
            test = false;
        }

        boolean initial = true;
        /* 
         String isInitial = readFromPrompt("Initial Load y/n", "n");
         if (isInitial.equalsIgnoreCase("n")) {
         initial = false;
         }
         */

        String tnp = readFromPrompt("Tablename Pattern: ", "");

        //GenericTableLoader gtl = new TableLoader(test, initial);
        //gtl.loadIt(tnp);
        System.exit(0);
    }

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

    private String getInfo() {
        return "Info";
    }

    /*
     private void print() {
     System.out.println("Generic Loader");
     System.out.println("==============");
     System.out.println(getInfo());
     for (int i = 0; i < tableNames.size(); i++) {
     setTable(tableNames.get(i));

     System.out.println("Tabelle: " + tableNames.get(i));
     System.out.println("-------");
     System.out.println("ITSM8-Select-Statement: " + getITSMreadStatement(tableNames.get(i)) + "\r\n");
     System.out.println("Create Table Statement: " + getCreateStatement(tableNames.get(i)) + "\r\n");
     System.out.println("Insert PreparedStatement: " + getInsertPreparedStatement(dwhConnection.getDatabase(), dwhConnection.getSchema(), tableNames.get(i), dwhConnection.getCon()));
     System.out.println("Update PreparedStatement: " + getUpdatePreparedStatement(dwhConnection.getDatabase(), dwhConnection.getSchema(), tableNames.get(i), dwhConnection.getCon()));
     }
     }

     */
    int colType;

    //private void processData(String table, DWHConnection dwhConnection, ITSM8ocrSnapshotConnection itsmConnection) {
    private void processData(String table) {

        System.out.println("Processing " + table);

        String updateOderInsert = "";

        PreparedStatement insert;
        PreparedStatement update;
        PreparedStatement check;
        PreparedStatement work; // dieses wird für den Ablauf benötigt

        int insertCounter = 0;
        int updateCounter = 0;

        // schreiben der Info
        //LoaderInfoWriter loaderInfoWriter = new LoaderInfoWriter(table);
        //LoaderErrorWriter loaderErrorWriter = new LoaderErrorWriter(table, loaderInfoWriter.loaderInfo.getLoadDatum());
        String aktuellerDatensatz = "";

        boolean isUpdate = false;

        // loesche die Tabelle, bei einem Initialen Load
        if (isInitial()) {
            dropTable(table);
        }

        // falls die Tabelle nicht da ist, wird sie nun erstellt
        createTable(table);
        colType = 0;

        try {

            // initalisieren der Prepared Statements Source
            insert = destinationConnection.prepareStatement(getInsertPreparedStatement(getDestDb(), getDestSchema(), table, destinationConnection));
            //update = destinationConnection.prepareStatement(getUpdatePreparedStatement(getDestDb(), getDestSchema(), table, destinationConnection));
            //check = destinationConnection.prepareStatement(getCheckStatement(table));

            work = insert;

            // die Abfrage ans ITSM8
            String sql = getReadStatement(table);
            ResultSet rs = sourceConnection.createStatement().executeQuery(sql);

            // Informationen zur Abfrage im ITSM
            ResultSetMetaData rsmd;
            rsmd = rs.getMetaData();
            int numberOfColumns = rsmd.getColumnCount();

            while (rs.next()) {

                // prüfen, ob der Datensatz bereits existiert
                // wird nicht benötigt, da wir eh einen Initial Load fahren
                /*
                 check.setString(1, rs.getString(1));
                 aktuellerDatensatz = rs.getString(1);

                 ResultSet res = check.executeQuery();
                 res.next();
                 if (res.getInt(1) > 0) {
                 updateOderInsert = "update";
                 isUpdate = true;
                 work = update;

                 if (++updateCounter % getCHUNKSIZE() == 0) {
                 System.out.print(table + ": Update der naechsten " + getCHUNKSIZE() + " Datensaetze ");
                 update.executeBatch();
                 System.out.println(table + ": " + updateCounter + " Datensaetze upgedated\r\n");
                 }

                 } else {
                 updateOderInsert = "insert";
                 isUpdate = false;
                 work = insert;

                 if (++insertCounter % getCHUNKSIZE() == 0) {
                 System.out.println(table + ": Insert der naechsten " + getCHUNKSIZE() + " Datensaetze ");
                 insert.executeBatch();
                 System.out.print(table + " " + insertCounter + " Datensaetze inserted\r\n");
                 }
                 }
                 */
                updateOderInsert = "insert";
                isUpdate = false;
                work = insert;

                if (++insertCounter % getCHUNKSIZE() == 0) {
                    System.out.println(table + ": Insert der naechsten " + getCHUNKSIZE() + " Datensaetze ");
                    insert.executeBatch();
                    System.out.print(table + " " + insertCounter + " Datensaetze inserted\r\n");
                }

                // Verarbeiten des Datensatzes
                int i = 0;
                int j = 0;
                int fieldLength = 0;
                boolean typFound = true;

                try {
                    for (j = 0; j < (numberOfColumns); j++) {
                        i++;

                        typFound = false;

                        // wie lange ist dieses Feld? Muss geprüft werden, damit das mit den Datenlänge passt
                        // welcher DatenTyp ist gefragt?                                        
                        colType = rsmd.getColumnType(i);

                        if (colType == java.sql.Types.CHAR) {
                            work.setString(i, rs.getString(i));
                            typFound = true;
                        }
                        if (colType == java.sql.Types.VARCHAR) {

                            try {
                                work.setString(i, rs.getString(i));
                                typFound = true;
                            } catch (SQLException e) {

                            }
                            typFound = true;
                        }

                        if (colType == java.sql.Types.BINARY) {
                            work.setString(i, rs.getString(i));
                            typFound = true;

                        }
                        if (colType == java.sql.Types.LONGVARBINARY) {
                            work.setString(i, rs.getString(i));
                            typFound = true;
                        }
                        if (colType == java.sql.Types.VARBINARY) {
                            work.setString(i, rs.getString(i));
                            typFound = true;
                        }
                        if (colType == java.sql.Types.TIME) {

                            // die Zeit wird als GMT gespeichert, wie sie aus dem ITSM kommt
                            work.setTimestamp(i, rs.getTimestamp(i));
                            typFound = true;

                            // vielleicht ein NULL Wert?
                            if ((rs.wasNull()) && (rsmd.isNullable(i) == ResultSetMetaData.columnNullable)) {
                                work.setNull(i, colType);
                            }
                        }
                        if (colType == java.sql.Types.DATE) {
                            work.setDate(i, rs.getDate(i));

                            // vielleicht ein NULL Wert?
                            if ((rs.wasNull()) && (rsmd.isNullable(i) == ResultSetMetaData.columnNullable)) {
                                work.setNull(i, colType);
                            }
                            typFound = true;
                        }
                        if (colType == java.sql.Types.TIMESTAMP) {
                            // die Zeit wird als GMT gespeichert, wie sie aus dem ITSM kommt
                            work.setTimestamp(i, rs.getTimestamp(i));
                            // vielleicht ein NULL Wert?
                            if ((rs.wasNull()) && (rsmd.isNullable(i) == ResultSetMetaData.columnNullable)) {
                                work.setNull(i, colType);
                            }

                            typFound = true;
                        }
                        if (colType == java.sql.Types.INTEGER) {
                            work.setInt(i, rs.getInt(i));

                            // vielleicht ein NULL Wert?
                            if ((rs.wasNull()) && (rsmd.isNullable(i) == ResultSetMetaData.columnNullable)) {
                                work.setNull(i, colType);
                            }

                            typFound = true;
                        }
                        if (colType == java.sql.Types.NUMERIC) {
                            work.setFloat(i, rs.getFloat(i));
                            // vielleicht ein NULL Wert?
                            if ((rs.wasNull()) && (rsmd.isNullable(i) == ResultSetMetaData.columnNullable)) {
                                work.setNull(i, colType);
                            }
                            typFound = true;
                        }
                        if (colType == java.sql.Types.DOUBLE) {
                            work.setDouble(i, rs.getDouble(i));
                            // vielleicht ein NULL Wert?
                            if ((rs.wasNull()) && (rsmd.isNullable(i) == ResultSetMetaData.columnNullable)) {
                                work.setNull(i, colType);
                            }
                            typFound = true;
                        }
                        if (colType == java.sql.Types.FLOAT) {
                            work.setFloat(i, rs.getFloat(i));
                            // vielleicht ein NULL Wert?
                            if ((rs.wasNull()) && (rsmd.isNullable(i) == ResultSetMetaData.columnNullable)) {
                                work.setNull(i, colType);
                            }
                            typFound = true;
                        }
                        if (colType == java.sql.Types.BIGINT) {
                            work.setInt(i, rs.getInt(i));
                            typFound = true;

                        }
                        if (colType == java.sql.Types.BIT) {
                            work.setInt(i, rs.getInt(i));
                            // vielleicht ein NULL Wert?
                            if ((rs.wasNull()) && (rsmd.isNullable(i) == ResultSetMetaData.columnNullable)) {
                                work.setNull(i, colType);
                            }
                            typFound = true;

                        }

                        if (colType == java.sql.Types.BLOB) {
                            work.setString(i, rs.getString(i));
                            typFound = true;

                        }
                        if (colType == java.sql.Types.CLOB) {
                            work.setString(i, rs.getString(i));
                            typFound = true;

                        }
                        if (colType == java.sql.Types.DECIMAL) {
                            work.setFloat(i, rs.getFloat(i));
                            // vielleicht ein NULL Wert?
                            if ((rs.wasNull()) && (rsmd.isNullable(i) == ResultSetMetaData.columnNullable)) {
                                work.setNull(i, colType);
                            }
                            typFound = true;

                        }
                        if (colType == java.sql.Types.OTHER) {
                            work.setString(i, rs.getString(i));
                            typFound = true;
                        }

                        if (!typFound) {
                            try {
                                work.setObject(i, rs.getObject(i));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }

                    // wir haben alle Felder geschafft und speichern nun den Datensatz
                    if (isUpdate) {
                        work.setString(rsmd.getColumnCount() + 1, rs.getString(1));
                    }
                    work.addBatch();

                } catch (SQLException einSatzAusnahme) {
                    logger.log(Level.INFO, "SQL Type: {0}", colType);
                    logger.log(Level.WARNING, "Error when processing table {0}  on {1} Message {2}", new Object[]{table, updateOderInsert, einSatzAusnahme.getMessage()});
                    logger.log(Level.INFO, " Row has not been processed");
                    //loaderErrorWriter.write(aktuellerDatensatz, einSatzAusnahme.getMessage(), "loader.TableLoader.processData(" + table + ")");
                }

            }

            // alle Datensaetze sind durch, schreiben des restlichen Batchlaufs
            // update.executeBatch();
            // System.out.print(table + ": " + updateCounter + " rows upgedated\r\n");
            insert.executeBatch();
            System.out.print(table + ": " + insertCounter + " rows inserted\r\n");

            //loaderInfoWriter.write(insertCounter, updateCounter);
            //loaderInfoWriter.close();
            insert.close();
            //update.close();

            //itsmConnection.close();
            //dwhConnection.close();
            System.out.println("table: " + table + " finished, DB things closed");

        } catch (SQLException sqlex) {
            logger.log(Level.INFO, "SQL Type: {0}", colType);
            logger.log(Level.WARNING, "Error when processing table {0}  at {1} message {2}", new Object[]{table, updateOderInsert, sqlex.getMessage()});
            sqlex.printStackTrace();
            ///loaderErrorWriter.write(aktuellerDatensatz, sqlex.getMessage(), "loader.TableLoader.processData(" + table + ")");
        }

    }

 
    private void close() {

        try {
            sourceConnection.close();
            destinationConnection.close();
        } catch (SQLException ex) {
            Logger.getLogger(TableLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private ArrayList<TabellenSpalteInfo> getTableInfo() {
        return spalten;
    }

    private void createTable(String table) {
        String sql = getCreateStatement(table);

        try {
            destinationConnection.createStatement().executeUpdate(sql);
            logger.log(Level.INFO, "Table " + table + " created.");
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Error when creating table {0} {1}", new Object[]{table, sqlex.getMessage()});
            logger.log(Level.INFO, sql);
        }

    }

    private void dropTable(String table) {
        String sql = "drop table " + table;

        try {

            //dwhConnection.getStmt().executeUpdate(sql);
            //logger.log(Level.INFO, "Tabelle " + table + " truncated.");            
            destinationConnection.createStatement().executeUpdate(sql);
            logger.log(Level.INFO, "Table " + table + " dropped.");

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Error while dropping table {0} {1}", new Object[]{table, sqlex.getMessage()});
            logger.log(Level.INFO, sql);
        }

    }

    /*
     Hier werden die Informationen zu denITSM-Tabellen abgeholt, die in der Variablen tableNamePattern enthalten sind.
     */
    private void createTableInfo() {
        spalten = new ArrayList();
        tableNames = new ArrayList();
        String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};
        String tableName = "";
        TabellenSpalteInfo tsi;

        try {

            // welche Tabellen suchen wir?
            sourceDmd = sourceConnection.getMetaData();
            ResultSet rs = sourceDmd.getTables(getSrcDb(), getSrcSchema(), getTableNamePattern(), tableTypes);

            while (rs.next()) {

                tableName = rs.getString(3);
                tableNames.add(tableName);

                // und jetzt die einzelnen Spalten dieser Tabelle
                ResultSet rsTable = sourceDmd.getColumns(getSrcDb(), getSrcSchema(), tableName, "%");
                while (rsTable.next()) {
                    tsi = new TabellenSpalteInfo();

                    tsi.setDatabase(rsTable.getString(1));
                    tsi.setSchema(rsTable.getString(2));
                    tsi.setTabelle(rsTable.getString(3));
                    tsi.setName("\"" + rsTable.getString(4) + "\"");
                    tsi.setDataType(rsTable.getInt(5));
                    tsi.setType(rsTable.getString(6));
                    if (tsi.getType().equalsIgnoreCase("datetime")) {
                        tsi.setType("TIMESTAMP");
                    }
                    if (tsi.getType().equalsIgnoreCase("text")) {
                        tsi.setType("CLOB");
                    }
                    if (tsi.getType().equalsIgnoreCase("nvarchar")) {
                        tsi.setType("varchar");
                    }
                    tsi.setLength(rsTable.getInt(7));
                    tsi.setDecimalDigits(rsTable.getInt(9));

                    if (rsTable.getString(18).equalsIgnoreCase("yes")) {
                        tsi.setNullAllowed(true);
                    } else {
                        tsi.setNullAllowed(false);
                    }

                    try {
                        if (rsTable.getString(23).equalsIgnoreCase("yes")) {
                            tsi.setAutoIncrement(true);
                        } else {
                            tsi.setAutoIncrement(false);
                        }
                    } catch (Exception e) {
                        tsi.setAutoIncrement(false);
                    }

                    tsi.setKey("");  // not used
                    tsi.setExportedKeys("");  // not used

                    spalten.add(tsi);

                }
                rsTable.close();
            }
            rs.close();
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Error while reading table information for {0} Exception: {1}", new Object[]{tableName, sqlex.getMessage()});
        }

    }

    private String getReadStatement(String table) {

        // falls wir keinen Inital Load durchführen, bereite das WhereStatement vor
        if (!isInitial()) {
            // prepareIncremental(table);
        }

        StringBuilder sb = new StringBuilder();
        String s = "  "; // der Abstand am Anfang der Zeile
        boolean first = true;

        sb.append("select ");
        if (isTEST()) {
            sb.append("top 1 ");  // im Testlauf werden nur 5 Datensaetze verarbeitet
        }
        sb.append("\r\n");

        TabellenSpalteInfo tsi = new TabellenSpalteInfo();
        Iterator<TabellenSpalteInfo> iterator = spalten.listIterator();
        String aktuelleTabelle = "";

        while (iterator.hasNext()) {
            tsi = iterator.next();

            // nur für die aktuelle Tabelle
            if (tsi.getTabelle().equalsIgnoreCase(table)) {

                // beim ersten Durchgang setzen wir die aktuelle Tabelle
                if (first) {
                    first = false;
                    aktuelleTabelle = tsi.getName();
                } else {
                    sb.append(", \r\n");
                }

                sb.append(s).append(tsi.getName());

            }
        }
        sb.append("\r\nfrom ").append(table);
        sb.append(getWhereStatement(table));
        return sb.toString();
    }

    /*
     Ueberschreibe diese Methode, falls du eine spezifische Tabelle mit einer spezifischen WHERE-Klausel versehen moechtest
     */
    String whereStatement = "";

    public String getWhereStatement(String table) {
        return whereStatement;
    }

    public void setWhereStatement(String v) {
        whereStatement = v;
    }

    private String getCreateStatement(String table) {
        StringBuilder sb = new StringBuilder();
        String fieldType = "";
        sb.append("CREATE TABLE ").append(table).append("( \r\n");

        // wir machen den DGI-OCR Key
        TabellenSpalteInfo tsi = new TabellenSpalteInfo();
        tsi = spalten.get(0);
        int pkLength = 15 + tsi.getLength();

        for (int i = 0; i < spalten.size(); i++) {

            if (i > 0) {
                sb.append(", \r\n");
            }

            tsi = new TabellenSpalteInfo();
            tsi = spalten.get(i);

            if (tsi.getTabelle().equalsIgnoreCase(table)) {
                sb.append("   ").append((String) tsi.getName()).append(" ");  // Attribute Name        

                // Attribute Type and MS SQL Server AUTO_Increment speciality
                fieldType = (String) tsi.getType();

                if (fieldType.equals("int identity")) {
                    sb.append("int");
                } else {
                    sb.append(fieldType);  // Attribute Type
                }

                boolean useLength = true;
                if (fieldType.toLowerCase().equals("text")) {
                    useLength = false;
                }
                if (fieldType.toLowerCase().equals("clob")) {
                    useLength = false;
                }
                
                if (fieldType.toLowerCase().equals("image")) {
                    useLength = false;
                }

                if (fieldType.toLowerCase().equals("int")) {
                    useLength = false;
                }
                if (fieldType.toLowerCase().equals("integer")) {
                    useLength = false;
                }
                if (fieldType.toLowerCase().equals("datetime")) {
                    fieldType = "TIMESTAMP";
                    useLength = false;
                }

                if (fieldType.toLowerCase().equals("timestamp")) {
                    useLength = false;
                }
                if (fieldType.toLowerCase().equals("date")) {
                    useLength = false;
                }
                if (fieldType.toLowerCase().startsWith("int")) {
                    useLength = false;
                }

                if (fieldType.toLowerCase().equalsIgnoreCase("tinyint")) {
                    useLength = false;
                }
                if (fieldType.toLowerCase().equalsIgnoreCase("smallint")) {
                    useLength = false;
                }
                
                 if (fieldType.toLowerCase().equalsIgnoreCase("float")) {
                    useLength = false;
                }
                if (fieldType.toLowerCase().equals("decimal")) {
                    useLength = false;
                    sb.append("(").append(Integer.toString(tsi.getLength())).append(",").append(Integer.toString(tsi.getDecimalDigits())).append(")");
                }

                 if (fieldType.toLowerCase().equalsIgnoreCase("time")) {
                    useLength = false;
                }
                
                
                if (((int) tsi.getLength()) < 1) {
                    useLength = false;
                }

                if (useLength) {
                    sb.append("(").append( Integer.toString(tsi.getLength())).append(") "); // Size
                } else {
                    sb.append(" ");
                }
                if (!(boolean) tsi.isNullAllowed()) {
                    sb.append("NOT NULL");  // null not allowed
                }

                // if MS SQL Server Auto_increment
                if (fieldType.equals("int identity")) {
                    //sb.append(" IDENTITY(1,1)");

                    // Derby specific
                    //sb.append(" GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1) ");
                }

                //sb.append(",\r\n");
            }
        }

        // und jetzt noch den Primary Key fuer DGI_Reporting_ID
        //sb.append(",\r\n");
        //sb.append("   ").append("PRIMARY KEY (DGI_Reporting_ID)");
        sb.append("\r\n)\r\n");
        return sb.toString();
    }

    private ArrayList<String> getColumns(String db, String schema, String table, Connection c) {
        ArrayList<String> tables = new ArrayList();
        String[] tableTypes = {"TABLE", "VIEW", "ALIAS", "SYNONYM"};

        try {

            String colPattern = "%";
            ResultSet rs = dmd.getColumns(null, null, table, null);
            while (rs.next()) {
                tables.add((String) rs.getString(4));
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Error while while fetching metadata : {0}", sqlex.getMessage());
        }
        return tables;
    }

    /*
     lies die Primary Keys dieser Tabelle 
     */
    private ArrayList<String> getPrimaryKeys(String db, String schema, String table, Connection c) {
        ArrayList<String> pk = new ArrayList();

        try {
            ResultSet rs = dmd.getPrimaryKeys(db, schema, table);
            while (rs.next()) {
                pk.add((String) rs.getString(4));
            }
        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Error while while fetching primary keys : {0}", sqlex.getMessage());
        }

        return pk;
    }

    private String getPrimaryKey(String db, String schema, String table, Connection c) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> pk = getPrimaryKeys(db, schema, table, c);

        boolean found = false;
        for (int i = 0; i < pk.size(); i++) {

            if (i == 0) {
                sb.append("\r\nwhere ").append((String) pk.get(i)).append(" = ? ");
                found = true;
            } else {
                sb.append("\r\nand ").append((String) pk.get(i)).append(" = ? ");
            }
        }

        if (!found) {
            sb.append("where -- please add here your condition to identify this row exactly --");
        }
        return sb.toString();
    }

    private String getInsertPreparedStatement(String db, String schema, String table, Connection c) {

        ArrayList<String> al = getColumns(db, schema, table, c);

        StringBuilder sb = new StringBuilder();
        StringBuilder sb1 = new StringBuilder();

        sb.append("insert into ").append(table).append(" ( \r\n");
        for (int i = 0; i < spalten.size(); i++) {
            TabellenSpalteInfo tsi = spalten.get(i);
            sb.append(" ").append((String) tsi.getName());  // Attribute Name        

            if (i < spalten.size() - 1) {
                sb.append(" ,\r\n");
            } else {
                sb.append("\r\n");
            }

            if (i > 0) {
                sb1.append(",");
            }
            sb1.append("?");
        }
        sb.append(") values \r\n( ").append(sb1.toString()).append(" )");
        return sb.toString();
    }

    public String getUpdatePreparedStatement(String db, String schema, String table, Connection c) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> al = getColumns(db, schema, table, c);

        sb.append("update ").append(table).append(" set \r\n");
        for (int i = 0; i < al.size(); i++) {
            sb.append(" ").append((String) al.get(i)).append(" = ? ");  // Attribute Name                    
            if (i < al.size() - 1) {
                sb.append(",\r\n");
            }
        }
        sb.append(getPrimaryKey(db, schema, table, c));

        return sb.toString();

    }

    public String getCheckStatement(String table) {
        return "";
    }

    /**
     * @return the table
     */
    public String getTable() {
        return table;
    }

    /**
     * @param table the table to set
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * @return the tableNamePattern
     */
    public String getTableNamePattern() {
        return tableNamePattern;
    }

    /**
     * @param tableNamePattern the tableNamePattern to set
     */
    public void setTableNamePattern(String tableNamePattern) {
        this.tableNamePattern = tableNamePattern;
    }

    /**
     * @return the TEST
     */
    public boolean isTEST() {
        return TEST;
    }

    /**
     * @param TEST the TEST to set
     */
    public void setTEST(boolean TEST) {
        this.TEST = TEST;
    }

    /**
     * @return the CHUNKSIZE
     */
    public int getCHUNKSIZE() {
        return CHUNKSIZE;
    }

    /**
     * @param CHUNKSIZE the CHUNKSIZE to set
     */
    public void setCHUNKSIZE(int CHUNKSIZE) {
        this.CHUNKSIZE = CHUNKSIZE;
    }

    /**
     * @return the initial
     */
    public boolean isInitial() {
        return initial;
    }

    /**
     * @param initial the initial to set
     */
    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    /**
     * @return the sourceConnection
     */
    public Connection getSourceConnection() {
        return sourceConnection;
    }

    /**
     * @param sourceConnection the sourceConnection to set
     */
    public void setSourceConnection(Connection sourceConnection) {
        this.sourceConnection = sourceConnection;
    }

    /**
     * @return the destinationConnection
     */
    public Connection getDestinationConnection() {
        return destinationConnection;
    }

    /**
     * @param destinationConnection the destinationConnection to set
     */
    public void setDestinationConnection(Connection destinationConnection) {
        this.destinationConnection = destinationConnection;
    }

    /**
     * @return the srcDb
     */
    public String getSrcDb() {
        return srcDb;
    }

    /**
     * @param srcDb the srcDb to set
     */
    public void setSrcDb(String srcDb) {
        this.srcDb = srcDb;
    }

    /**
     * @return the destDb
     */
    public String getDestDb() {
        return destDb;
    }

    /**
     * @param destDb the destDb to set
     */
    public void setDestDb(String destDb) {
        this.destDb = destDb;
    }

    /**
     * @return the srcSchema
     */
    public String getSrcSchema() {
        return srcSchema;
    }

    /**
     * @param srcSchema the srcSchema to set
     */
    public void setSrcSchema(String srcSchema) {
        this.srcSchema = srcSchema;
    }

    /**
     * @return the destSchema
     */
    public String getDestSchema() {
        return destSchema;
    }

    /**
     * @param destSchema the destSchema to set
     */
    public void setDestSchema(String destSchema) {
        this.destSchema = destSchema;
    }

}
