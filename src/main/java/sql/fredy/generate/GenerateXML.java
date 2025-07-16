package sql.fredy.generate;

/**
 * This generates a XML-File out of the MetaData of a DB. It adds also standard
 * values for GUI generation. The generated file can then be edited by XMLEditor
 *
 * This software is part of Fredy's Admin-Framework
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
import java.util.Vector;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Attribute;
import org.jdom2.output.XMLOutputter;
import org.jdom2.Namespace;

// Swing-things
public class GenerateXML extends BasicAdmin {

    private Document doc;
    private Namespace ns;
    private DbInfo dbi;
    private Element mainPanel;

    public String INSETS = "2,2,2,2";

    public void setINSETS(String v) {
        INSETS = v;
    }

    public String getINSETS() {
        return INSETS;
    }

    int numberOfComponentsPerLine = 2;

    /**
     * Get the value of numberOfComponentsPerLine.
     *
     * @return value of numberOfComponentsPerLine.
     */
    public int getNumberOfComponentsPerLine() {
        return numberOfComponentsPerLine;
    }

    /**
     * Set the value of numberOfComponentsPerLine.
     *
     * @param v Value to assign to numberOfComponentsPerLine.
     */
    public void setNumberOfComponentsPerLine(int v) {
        this.numberOfComponentsPerLine = v;
    }

    int gridY;

    /**
     * Get the value of gridY.
     *
     * @return value of gridY.
     */
    public int getGridY() {
        return gridY;
    }

    int gridX = -1;

    /**
     * Get the value of gridX.
     *
     * @return value of gridX.
     */
    public int getGridX() {
        gridX = gridX + 1;
        if (gridX == getNumberOfComponentsPerLine()) {
            gridX = 0;
            gridY = gridY + getGridHeight();
            gridHeight = 1;
        }
        return gridX;
    }

    /**
     * Set the value of gridX.
     *
     * @param v Value to assign to gridX.
     */
    public void setGridX(int v) {
        this.gridX = v;
    }

    int gridHeight = 1;

    /**
     * Get the value of gridHeight.
     *
     * @return value of gridHeight.
     */
    public int getGridHeight() {
        return gridHeight;
    }

    /**
     * Set the value of gridHeight.
     *
     * @param v Value to assign to gridHeight.
     */
    public void setGridHeight(int v) {
        if (v > gridHeight) {
            this.gridHeight = v;
        }
    }

    int maxCols = 25;

    /**
     * Get the maximum length of a TextField before turning into a TextArea
     *
     * @return value of maxCols.
     */
    public int getMaxCols() {
        return maxCols;
    }

    /**
     * Set the maximum length of a TextField before turning into a TextArea
     *
     * @param v Value to assign to maxCols.
     */
    public void setMaxCols(int v) {
        this.maxCols = v;
    }

    int noCols = 25;

    /**
     * Get the number of Columns a TextArea has.
     *
     * @return value of noCols.
     */
    public int getNoCols() {
        return noCols;
    }

    /**
     * Set the number of Columns a TextArea has.
     *
     * @param v Value to assign to noCols.
     */
    public void setNoCols(int v) {
        this.noCols = v;
    }

    int noRows = 5;

    /**
     * Get the number of rows a TextArea has
     *
     * @return value of noRows.
     */
    public int getNoRows() {
        return noRows;
    }

    /**
     * Set the number of rows a TextArea has
     *
     * @param v Value to assign to noRows.
     */
    public void setNoRows(int v) {
        this.noRows = v;
    }

    String file;

    /**
     * Get the value of file.
     *
     * @return value of file.
     */
    public String getFile() {
        return file;
    }

    /**
     * Set the value of file.
     *
     * @param v Value to assign to file.
     */
    public void setFile(String v) {
        this.file = v;
    }

    public GenerateXML(String table, String schema, String file) {

        super(table, schema);
        setFile(file);

    }

    public void init() {

        exec();

    }

    public void exec() {
        dbi = new DbInfo();

        ns = Namespace.getNamespace("admin", "Fredys-Admintool");
        Element database = new Element("database", ns);
        database.setAttribute(new Attribute("name", getDatabase()));

        // now we do all Tables
        Vector v = new Vector();
        v = dbi.getTables(getDatabase(), getSchema());
        //                                          TableType
        //                                           |   TableNamePattern
        //v = dbi.getTables(getDatabase(),getSchema(),"%",getTable());

        for (int i = 0; i < v.size(); i++) {
            database.addContent((Element) table((String) v.elementAt(i)));
        }
        doc = new Document(database);
        XMLOutputter fmt = new XMLOutputter();
        try {

            DataOutputStream outputstr = new DataOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(getFile())));

            fmt.output(doc, outputstr);
        } catch (IOException e) {;
        }

    }

    private String getPrimaryKey(String table) {
        String s = "";
        Vector pk = new Vector();

        pk = dbi.getPk(getTable());
        for (int i = 0; i < pk.size(); i++) {
            if (i > 0) {
                s = s + ";";
            }
            PrimaryKey primkey = new PrimaryKey();
            primkey = (PrimaryKey) pk.elementAt(i);
            s = s + (String) primkey.getColumnName();
        }
        return s;
    }

    private Element table(String table) {
        Element tableElement = new Element("table", ns);
        tableElement.setAttribute(new Attribute("name", table));
        setTable(table);
        tableElement.setAttribute(new Attribute("primaryKeys", getPrimaryKey(table)));

        tableElement.addContent(panel("mainPanel"));
        return tableElement;
    }

    private String isJava(String s) {

        if (!Character.isJavaIdentifierStart(s.charAt(0))) {
            s = "adm_" + s;
        }
        return s;

    }

    private Element panel(String name) {

        name = isJava(name);
        mainPanel = new Element("panel", ns);
        mainPanel.setAttribute(new Attribute("name", name));
        mainPanel.addContent((Element) gridBagConstraints(INSETS,
                "GridBagConstraints.CENTER",
                "GridBagConstraints.BOTH",
                "1.0",
                "1.0",
                "1",
                "1",
                "0",
                "0"));

        // now we are adding other Panel related info
        // Title
        Element title = new Element("title", ns);
        title.addContent(firstUpper(getTable()));

        //Border
        Element border = new Element("border", ns);
        border.addContent("BevelBorder.LOWERED");

        mainPanel.addContent(title);
        mainPanel.addContent(border);

        // now add each single Column of this table....
        doColumns(getTable());

        return mainPanel;
    }

    private void doColumns(String table) {

        Vector v = new Vector();

        v = dbi.getColumnNames(table);
        gridX = -1;
        gridY = 0;
        for (int i = 0; i < v.size(); i++) {
            Element comp = new Element("component", ns);

            SingleColumnInfo sci = new SingleColumnInfo();
            sci = dbi.getColumnInfo(getDatabase(), table, (String) v.elementAt(i));

            comp.setAttribute("name", isJava((String) v.elementAt(i)));
            comp.setAttribute("type", javaType(sci.getData_type()));
            comp.setAttribute("fieldType", fieldType(sci));

            // add the labelConstraints here
            comp.addContent((Element) labelConstraints(INSETS,
                    "GridBagConstraints.NORTHEAST",
                    "GridBagConstraints.NONE",
                    "0.0",
                    "0.0",
                    "1",
                    "1",
                    Integer.toString(getGridX()),
                    Integer.toString(getGridY())));

            // add the component here
            comp.addContent((Element) parameter(sci));

            String fill = "GridBagConstraints.HORIZONTAL";
            int gridheight = 1;
            if (fieldType(sci) == "AreaPanel") {
                //gridheight = Integer.parseInt(getAreaCols(sci.getColumn_size()));
                //if (gridheight >= getNoCols()) fill = "GridBagConstraints.BOTH";

                gridheight = getNoRows();
                if (gridheight >= getNoRows()) {
                    fill = "GridBagConstraints.BOTH";
                }
            }
            if (fieldType(sci) == "DateButtonPanel") {
                fill = "GridBagConstraints.NONE";
            }

            comp.addContent((Element) gridBagConstraints(INSETS,
                    "GridBagConstraints.WEST",
                    fill,
                    "1.0",
                    "1.0",
                    Integer.toString(gridheight),
                    "1",
                    Integer.toString(getGridX()),
                    Integer.toString(getGridY())));
            setGridHeight(gridheight);
            mainPanel.addContent((Element) comp);
        }
    }

    private Element parameter(SingleColumnInfo sci) {
        Element elt = new Element("parameter", ns);
        elt.setAttribute("label", firstUpper(sci.getColumn_name()));
        if (fieldType(sci) == "FieldPanel") {
            elt.setAttribute("length", Integer.toString(sci.getColumn_size()));
            elt.setAttribute("text", "");
            //if (sci.getData_type() == java.sql.Types.CHAR)          elt.setAttribute("filter","JTextFieldFilter.ALPHA_NUMERIC"); 
            if (sci.getData_type() == java.sql.Types.CHAR) {
                elt.setAttribute("filter", "null");
            }
            if (sci.getData_type() == java.sql.Types.VARCHAR) {
                elt.setAttribute("filter", "null");
            }
            if (sci.getData_type() == java.sql.Types.BINARY) {
                elt.setAttribute("filter", "null");
            }
            if (sci.getData_type() == java.sql.Types.LONGVARBINARY) {
                elt.setAttribute("filter", "null");
            }
            if (sci.getData_type() == java.sql.Types.VARBINARY) {
                elt.setAttribute("filter", "null");
            }
            if (sci.getData_type() == java.sql.Types.INTEGER) {
                elt.setAttribute("filter", "JTextFieldFilter.NUMERIC");
            }
            if (sci.getData_type() == java.sql.Types.NUMERIC) {
                elt.setAttribute("filter", "JTextFieldFilter.FLOAT");
            }
            if (sci.getData_type() == java.sql.Types.DOUBLE) {
                elt.setAttribute("filter", "JTextFieldFilter.FLOAT");
            }
            if (sci.getData_type() == java.sql.Types.FLOAT) {
                elt.setAttribute("filter", "JTextFieldFilter.FLOAT");
            }
            if (sci.getData_type() == java.sql.Types.BIGINT) {
                elt.setAttribute("filter", "JTextFieldFilter.NUMERIC");
            }
            if (sci.getData_type() == java.sql.Types.BIT) {
                elt.setAttribute("filter", "\"01\"");
            }
            if (sci.getData_type() == java.sql.Types.DECIMAL) {
                elt.setAttribute("filter", "JTextFieldFilter.FLOAT");
            }
            if (sci.getData_type() == java.sql.Types.OTHER) {
                elt.setAttribute("filter", "null");
            }

            elt.setAttribute("titled", "false");
            elt.setAttribute("title", "");
            elt.setAttribute("layout", "0");
        }
        if (fieldType(sci) == "AreaPanel") {
            elt.setAttribute("rows", Integer.toString(getNoRows()));
            elt.setAttribute("cols", getAreaCols(sci.getColumn_size()));
            elt.setAttribute("lineWrap", "true");
            elt.setAttribute("titled", "false");
            elt.setAttribute("title", "");
            elt.setAttribute("layout", "0");
            elt.setAttribute("text", "");

        }

        if (fieldType(sci) == "DateButtonPanel") {
            elt.setAttribute("text", "");
            elt.setAttribute("titled", "false");
            elt.setAttribute("title", "");
            elt.setAttribute("layout", "0");
        }

        return elt;

    }

    private String getAreaCols(int l) {
        String s = Integer.toString(l);
        if (l > getNoCols()) {
            Float L =  Float.valueOf(l); // length
            Float C =  Float.valueOf(getNoCols()); // cols
            Float R =  Float.valueOf(getNoRows()); // rows
            float l1 = L / C;

            s = Integer.toString(getNoCols());
            if (l1 > R) {
                tNoRows = Integer.toString(getNoRows());
            } else {
                tNoRows = Float.toString(l1);
            }

        }
        return s;
    }

    private String tNoRows = "1";

    private String thisNoRows() {
        return tNoRows;
    }

    //Data-Type things  
    private String fieldType(SingleColumnInfo sci) {

        String s = "FieldPanel";
        if (sci.getColumn_size() > getMaxCols()) {
            s = "AreaPanel";
        }
        if (sci.getData_type() == java.sql.Types.DATE) {
            s = "DateButtonPanel";
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

    private Element gridBagConstraints(String insets,
            String anchor,
            String fill,
            String weightx,
            String weighty,
            String gridheight,
            String gridwidth,
            String gridx,
            String gridy) {
        Element elt = new Element("gridBagConstraints", ns);
        elt.setAttribute("insets", insets);
        elt.setAttribute("anchor", anchor);
        elt.setAttribute("fill", fill);
        elt.setAttribute("weightx", weightx);
        elt.setAttribute("weighty", weighty);
        elt.setAttribute("gridheight", gridheight);
        elt.setAttribute("gridwidth", gridwidth);
        elt.setAttribute("gridx", gridx);
        elt.setAttribute("gridy", gridy);

        return elt;
    }

    private Element labelConstraints(String insets,
            String anchor,
            String fill,
            String weightx,
            String weighty,
            String gridheight,
            String gridwidth,
            String gridx,
            String gridy) {
        Element elt = new Element("labelConstraints", ns);
        elt.setAttribute("insets", insets);
        elt.setAttribute("anchor", anchor);
        elt.setAttribute("fill", fill);
        elt.setAttribute("weightx", weightx);
        elt.setAttribute("weighty", weighty);
        elt.setAttribute("gridheight", gridheight);
        elt.setAttribute("gridwidth", gridwidth);
        elt.setAttribute("gridx", gridx);
        elt.setAttribute("gridy", gridy);

        return elt;
    }

    private String firstUpper(String s) {
        s = s.substring(0, 1).toUpperCase() + s.substring(1);
        return s;
    }

    public static void main(String args[]) {

        String host = "localhost";
        String user = System.getProperty("user.name");
        String password = "";
        String database = "";
        String table = "%";
        String file = System.getProperty("user.home") + File.separator + database + ".xml";
        String schema = "%";
        boolean help = false;
        boolean verbose = false;

        int i = 0;

        while (i < args.length) {
            if ((args[i].equals("-help")) || (args[i].equals("--h"))) {
                help = true;
            }

            if ((args[i].equals("-h")) || (args[i].equals("-host"))) {
                i++;
                host = args[i];
            }
            if ((args[i].equals("-v")) || (args[i].equals("-verbose"))) {
                verbose = true;
            }
            if ((args[i].equals("-u")) || (args[i].equals("-user"))) {
                i++;
                user = args[i];
            }
            if ((args[i].equals("-p")) || (args[i].equals("-password"))) {
                i++;
                password = args[i];
            }
            if ((args[i].equals("-d")) || (args[i].equals("-db"))) {
                i++;
                database = args[i];
                file = System.getProperty("user.home") + File.separator + database + ".xml";
            }
            if ((args[i].equals("-t")) || (args[i].equals("-table"))) {
                i++;
                table = args[i];
            }
            if ((args[i].equals("-f")) || (args[i].equals("-file"))) {
                i++;
                file = args[i];
            }
            if ((args[i].equals("-s")) || (args[i].equals("-schema"))) {
                i++;
                schema = args[i];
            }
            i++;
        }

        if ((help) || (args.length == 0)) {
            System.out.println("\nFredy's Generate XML\n"
                    + "--------------------\n"
                    + "Generates a XML-file out of the MetaData of a Database\n"
                    + "The primary use of this XML-file is to generate Java-Code\n"
                    + "Just feed this file into the GenerateForm to get all of it.\n\n"
                    + "Syntax: GenerateXML -s schema       [default: %]\n"                    
                    + "                    -t table        [default: %]\n"
                    + "                    -f file         [default: "
                    + System.getProperty("user.home") + File.separator + "database.xml]\n"
                    + "                    -v verbose\n"
                    + "                    -help this screen");

        } else {

            if (verbose) {
                System.out.println("\nhost = " + host + "\n"
                        + "user = " + user + "\n"
                        + "schema = " + schema + "\n"
                        + "database = " + database + "\n"
                        + "table = " + table + "\n"
                        + "file = " + file);
            }

            GenerateXML xml = new GenerateXML( table, schema,file);
            xml.init();
        }
    }
}
