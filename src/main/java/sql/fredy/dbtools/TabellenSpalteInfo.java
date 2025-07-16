 package sql.fredy.dbtools;

/**
 *
 * @author Fredy Fischer
 */

public class TabellenSpalteInfo {

    private String tabelle;
    private String name;
    private String type;
    private int length;
    private boolean nullAllowed;
    private String key;
    private String exportedKeys;
    private int dataType;
    private boolean autoIncrement;
    private String database;
    private String schema;
    private int decimalDigits;
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the nullAllowed
     */
    public boolean isNullAllowed() {
        return nullAllowed;
    }

    /**
     * @param nullAllowed the nullAllowed to set
     */
    public void setNullAllowed(boolean nullAllowed) {
        this.nullAllowed = nullAllowed;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the exportedKeys
     */
    public String getExportedKeys() {
        return exportedKeys;
    }

    /**
     * @param exportedKeys the exportedKeys to set
     */
    public void setExportedKeys(String exportedKeys) {
        this.exportedKeys = exportedKeys;
    }

    /**
     * @return the tabelle
     */
    public String getTabelle() {
        return tabelle;
    }

    /**
     * @param tabelle the tabelle to set
     */
    public void setTabelle(String tabelle) {
        this.tabelle = tabelle;
    }

    /**
     * @return the dataType
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the autoIncrement
     */
    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    /**
     * @param autoIncrement the autoIncrement to set
     */
    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    /**
     * @return the database
     */
    public String getDatabase() {
        return database;
    }

    /**
     * @param database the database to set
     */
    public void setDatabase(String database) {
        this.database = database;
    }

    /**
     * @return the schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @return the decimalDigits
     */
    public int getDecimalDigits() {
        return decimalDigits;
    }

    /**
     * @param decimalDigits the decimalDigits to set
     */
    public void setDecimalDigits(int decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

}
