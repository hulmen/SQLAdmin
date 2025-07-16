/**
 * This Class is part of Fredy's SQL-Admin Tool.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, like: - create/ drop tables - create indices - perform
 * sql-statements - simple form - a guided query - Data Export and a other
 * usefull things in DB-arena
 *
 * Admin (Version see below) Copyright (c) 1999 Fredy Fischer sql@hulmen.ch
 *
 * Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 *
 * The icons used in this application are from Dean S. Jones
 *
 * Icons Copyright(C) 1998 by Dean S. Jones dean@gallant.com
 * www.gallant.com/icons.htm
 *
 * CalendarBean is Copyright (c) by Kai Toedter
 *
 * MSeries is Copyright (c) by Martin Newstead
 *
 * POI is from the Apache Foundation
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
 *
 * Changes
 * =======
 * Fredy, 2025-03-20  Line 366, changed the Typename for the Tooltip to the typename provided by the 
 *                              database and not standard JDBC Type names.
 * 
 */
package sql.fredy.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import sql.fredy.metadata.FunctionColumnMetaData;
import sql.fredy.metadata.FunctionMetaData;

/**
 *
 * @author sql@hulmen.ch
 */
public class DbTreeNodeRenderer implements TreeCellRenderer {

    private final Logger logger = Logger.getLogger("sql.fredy.ui.DbTreeNodeRenderer");

    private final ImageIcon db;
    private final ImageIcon schema;
    private final ImageIcon table;
    private final ImageIcon view;
    private final ImageIcon alias;
    private final ImageIcon synonym;
    private final ImageIcon column;
    private final ImageIcon function;
    private final ImageIcon pk;
    private final ImageIcon textIcon,dateIcon,timeIcon,numberIcon,intIcon,booleanIcon,blobIcon,javaIcon;
    private final JLabel label;
         
    private final UIDefaults defaults;
    private final Color foreground;
    private final Color background;

    public DbTreeNodeRenderer() {
        LoadImage li = new LoadImage();
        db = li.getImage("opendb.gif");
        schema = li.getImage("frames.gif");
        table = li.getImage("table.gif");
        view = li.getImage("binocular.gif");
        alias = li.getImage("newsheet.gif");
        synonym = li.getImage("newsheet.gif");
        column = li.getImage("column.gif");
        function = li.getImage("edit.gif");
        pk = li.getImage("key.gif");
        textIcon = li.getImage("documentdraw.gif");
        dateIcon = li.getImage("documentmag.gif");
        timeIcon = li.getImage("clock.gif");
        numberIcon = li.getImage("calculator.gif");
        intIcon    = li.getImage("circle_0.gif");
        booleanIcon = li.getImage("plusminus.gif");
        blobIcon = li.getImage("box.gif");
        javaIcon = li.getImage("datastore.gif");
                
        label = new JLabel();

        defaults = javax.swing.UIManager.getDefaults();
        foreground = defaults.getColor("Tree.textForeground");
        background = defaults.getColor("Tree.textBackground");
        Color treebackground = defaults.getColor("Tree.background");
        Color treeforeground = defaults.getColor("Tree.foreground");

    }

    public Component getTreeCellRendererComponent(JTree tree,
            Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {

        DbTreeNode currentTreeNode = (DbTreeNode) value;

       
        // no brackets or " in Names
        currentTreeNode.setFIXSPACES(false);

        label.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_DATABASE)) {
            label.setIcon(db);
            label.setText(" "  + currentTreeNode.getDbName());
            label.setToolTipText(currentTreeNode.getProduct() + " " + currentTreeNode.getProductVersion() + " on " + currentTreeNode.getServerName());
            
            

            // I do not want to see the whole path of the DERBY D            
            if (currentTreeNode.getProduct().toLowerCase().contains("derby")) {
                String pattern = File.separator;
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    pattern = "\\\\";
                }
                String[] s = currentTreeNode.getDbName().split(pattern);
                if (s.length > 0) {
                    label.setText(" " + s[s.length - 1]);
                }
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < s.length - 1; i++) {
                    sb.append(s[i]).append(File.separator);
                }
                label.setToolTipText(currentTreeNode.getProduct() + " " + currentTreeNode.getProductVersion() + " DB-location: " + sb.toString());

            }

        }

        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_SCHEMA)) {
            label.setIcon(schema);
            label.setText(" " + currentTreeNode.getSchemaName());
            label.setToolTipText("Schema, open to see tables, views, synonyms, aliases and functions");
        }

        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_ALIAS)) {
            label.setIcon(alias);
            label.setText(" ALIAS");
            label.setToolTipText("all tables of type alias");
        }
        
        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_FUNCTION)) {
            label.setIcon(function);
            label.setText(" Functions");
            label.setToolTipText("functions in this schema");
        }

        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_SINGLE_FUNCTION)) {
            label.setIcon(function);
            try {
                String functionName[] = currentTreeNode.getFunctionName().split(";");
                StringBuilder fName = new StringBuilder();                
                fName.append(functionName[0]);
                FunctionMetaData fmd = currentTreeNode.getFunctionMetaData();
                boolean firstRound = true;
                HashMap<String,FunctionColumnMetaData> myColumns = new HashMap();
                myColumns = fmd.getColumnMetaData();
                for (Iterator<String> it = myColumns.keySet().iterator(); it.hasNext();) {
                    String funcName = it.next();
                    FunctionColumnMetaData fcmd = new FunctionColumnMetaData();
                    fcmd = (FunctionColumnMetaData) myColumns.get(funcName);
                    if (fcmd.getColumnType() == 1) {
                        if (firstRound) {
                            fName.append("(");
                            firstRound = false;
                        } else {
                            fName.append(",");
                        }                        
                        fName.append(fcmd.getColumnName());
                    }
                }
                if (!firstRound) {
                    fName.append(")");
                }
                label.setText(" " + fName.toString());
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception on FunctionMetaData: " + e.getMessage());
                //e.printStackTrace();
                String functionName[] = currentTreeNode.getFunctionName().split(";");
                label.setText(" " + functionName[0]);
            }
            label.setToolTipText("all Functions");
        }

        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_SYNONYM)) {
            label.setIcon(table);
            label.setText(" SYNONYMS");
            label.setToolTipText("all tables of type synonym");
        }
        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_TABLE)) {
            label.setIcon(table);
            //label.setText(currentTreeNode.getType());
            label.setText(" TABLES");
            label.setToolTipText("all tables");
        }

        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_SYSTEMTABLE)) {
            label.setIcon(table);
            label.setText(" System Tables ");
            label.setToolTipText(" ");
        }
        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_GLOBAL_TEMPORARY)) {
            label.setIcon(table);
            label.setText(" Global Temporary Tables ");
            label.setToolTipText(" ");
        }
        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_LOCAL_TEMPORARY)) {
            label.setIcon(table);
            label.setText(" Local Temporary Tables ");
            label.setToolTipText(" ");
        }

        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_VIEW)) {
            label.setIcon(synonym);
            label.setText(" VIEWS");
            label.setToolTipText("all tables of type view");
        }

        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_ENTITY)) {
            label.setIcon(table);
            label.setText(" " + currentTreeNode.getTableName());
            label.setToolTipText("click to see columns");
        }

        if (currentTreeNode.getType().equalsIgnoreCase(DbTreeNode.TYPE_COLUMN)) {
            label.setIcon(column);
            if (currentTreeNode.isPrimaryKey()) {
                label.setIcon(pk);
            } 
            /*
            else {
                switch ( currentTreeNode.getColumnDataType()) {
                    case java.sql.Types.BINARY:
                        label.setIcon(blobIcon);
                        break;
                    case java.sql.Types.BIT:
                        label.setIcon(booleanIcon);
                        break;
                    case java.sql.Types.BLOB:
                        label.setIcon(blobIcon);
                        break;
                    case java.sql.Types.BOOLEAN:
                        label.setIcon(booleanIcon);
                        break;
                    case java.sql.Types.CHAR:
                        label.setIcon(textIcon);
                        break;
                    case java.sql.Types.CLOB:
                        label.setIcon(blobIcon);
                        break;
                    case java.sql.Types.DATE:
                        label.setIcon(dateIcon);
                        break;
                    case java.sql.Types.DECIMAL:
                        label.setIcon(numberIcon);
                        break;
                    case java.sql.Types.DOUBLE:
                        label.setIcon(numberIcon);
                        break;
                    case java.sql.Types.FLOAT:
                        label.setIcon(numberIcon);
                        break;
                    case java.sql.Types.INTEGER:
                        label.setIcon(intIcon);
                        break;
                    case java.sql.Types.JAVA_OBJECT:
                        label.setIcon(javaIcon);
                        break;
                    case java.sql.Types.LONGNVARCHAR:
                        label.setIcon(textIcon);
                        break;
                    case java.sql.Types.LONGVARBINARY:
                        label.setIcon( blobIcon );
                        break;
                    case java.sql.Types.LONGVARCHAR:
                        label.setIcon(   textIcon );
                        break;
                    case java.sql.Types.NCHAR:
                        label.setIcon( textIcon);
                        break;
                    case java.sql.Types.NCLOB:
                        label.setIcon( blobIcon );
                        break;
                    case java.sql.Types.NUMERIC:
                        label.setIcon( numberIcon );
                        break;
                    case java.sql.Types.NVARCHAR:
                        label.setIcon( textIcon );
                        break;
                   case java.sql.Types.SMALLINT:
                        label.setIcon( intIcon );
                        break;
                    case java.sql.Types.TIME:
                        label.setIcon( timeIcon );
                        break;
                    case java.sql.Types.TIMESTAMP:
                        label.setIcon( timeIcon );
                        break;
                    case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
                        label.setIcon( timeIcon );
                        break;
                    case java.sql.Types.TIME_WITH_TIMEZONE:
                        label.setIcon( timeIcon );
                        break;
                    case java.sql.Types.TINYINT:
                        label.setIcon( intIcon );
                        break;
                    case java.sql.Types.VARBINARY:
                        label.setIcon( blobIcon );
                        break;
                    case java.sql.Types.VARCHAR:
                        label.setIcon( textIcon );
                        break;
                    default :
                         label.setIcon(column);
                         break;                         
                }
            }
            */
            label.setText(" " + currentTreeNode.getColumnName());
            StringBuilder sb = new StringBuilder();
            sb.append(currentTreeNode.getColumnName()).append(" : ");
            if (currentTreeNode.isPrimaryKey()) {
                sb.append("PRRIMARY KEY ; ");
            }

            // SQLite is challenging with datatypes            
            if (currentTreeNode.getProduct().toLowerCase().startsWith("sqlite")) {
                sb.append(currentTreeNode.getColumnType());
            } else {
                //sb.append(getTypeName(currentTreeNode.getColumnDataType()));
                sb.append(currentTreeNode.getColumnType());
            }

            if (displayLength(currentTreeNode.getColumnDataType())) {
                //sb.append("(").append(currentTreeNode.getLength()).append(")");
                sb.append(formattedLength(currentTreeNode));
            }
            sb.append(" ; ");

            if (currentTreeNode.isAutoIncrement()) {
                sb.append("AUTO INCREMENT ; ");
            }
            if (currentTreeNode.isNullable()) {
                sb.append("nullable ");
            } else {
                sb.append("not nullable ");
            }

            if ((currentTreeNode.getColumnDefault() != null) && (currentTreeNode.getColumnDefault().length() >= 1)) {
                sb.append(" Default: " + currentTreeNode.getColumnDefault());
            }

            label.setToolTipText(sb.toString());
        }

        if (sel) {
            //logger.log(Level.INFO, "{0} selected", label.getText());

            label.setOpaque(true);
            //label.setForeground(defaults.getColor("List.selectionForeground"));
            //label.setBackground(defaults.getColor("Tree.selectionBackground")); 
            label.setForeground(Color.WHITE);
            label.setBackground(Color.BLUE);

            label.updateUI();

        } else {
            //logger.log(Level.INFO, "{0} unselected", label.getText());

            label.setOpaque(false);
            /*
            label.setForeground(Color.BLACK);
            label.setBackground(Color.WHITE);
             */
            label.setForeground(foreground);
            label.setBackground(background);

            label.updateUI();
        }

        // are we on top?
        if (currentTreeNode.isRoot()) {
            label.setForeground(foreground);
            label.setBackground(background);
        }

        return label;

    }

    private String formattedLength(DbTreeNode currentTreeNode) {
        String l = "";
        int type = currentTreeNode.getColumnDataType();

        switch (type) {
            case java.sql.Types.ARRAY:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.BINARY:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.BLOB:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.CLOB:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.INTEGER:
                l = "";
                break;
            case java.sql.Types.FLOAT:
                //l = "(" + Integer.toString(currentTreeNode.getLength()) +"," + Integer.toString(currentTreeNode.getNumPrec()) + ")";            
                l = "";
                break;
            case java.sql.Types.DOUBLE:
                //l = "(" + Integer.toString(currentTreeNode.getLength()) +"," + Integer.toString(currentTreeNode.getNumPrec()) + ")";            
                l = "";
                break;
            case java.sql.Types.DECIMAL:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + "," + Integer.toString(currentTreeNode.getNumPrec()) + ")";
                break;
            case java.sql.Types.NUMERIC:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + "," + Integer.toString(currentTreeNode.getNumPrec()) + ")";
                break;
            case java.sql.Types.BIGINT:
                l = "";
                break;
            case java.sql.Types.TINYINT:
                l = "";
                break;
            case java.sql.Types.SMALLINT:
                l = "";
                break;
            case java.sql.Types.DATE:
                l = "";
                break;
            case java.sql.Types.TIMESTAMP:
                l = "";
                break;
            case java.sql.Types.TIME:
                l = "";
                break;
            case java.sql.Types.BIT:
                l = "";
                break;
            case java.sql.Types.BOOLEAN:
                l = "";
                break;
            case java.sql.Types.CHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.NVARCHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.VARCHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.LONGNVARCHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.LONGVARCHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.LONGVARBINARY:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.NCHAR:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.NCLOB:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.OTHER:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.REAL:
                l = "";
                break;
            case java.sql.Types.REF:
                l = "";
                break;
            case java.sql.Types.SQLXML:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.STRUCT:
                l = "";
                break;
            case java.sql.Types.JAVA_OBJECT:
                l = "(" + Integer.toString(currentTreeNode.getLength()) + ")";
                break;
            case java.sql.Types.DATALINK:
                l = "";
                break;
            case java.sql.Types.DISTINCT:
                l = "";
                break;
            case java.sql.Types.NULL:
                l = "";
                break;
            default:
                l = "";
                break;
        }

        // no Length if TEXT as Name
        if (currentTreeNode.getColumnType().equalsIgnoreCase("TEXT")) {
            l = "";
        }

        // SQLite messes with the DataTypes therefore I need to manually find TEXT and remove length
        if (currentTreeNode.getProduct().toLowerCase().startsWith("sqlite")) {
            if (currentTreeNode.getColumnType().equalsIgnoreCase("TEXT")) {
                l = "";
            }
        }

        // also MS SQL has a TEXT Type with no length parameter
        if (currentTreeNode.getProduct().toLowerCase().contains("Microsoft SQL Server".toLowerCase())) {
            if (currentTreeNode.getLength() >= 2147483647) {
                l = "(max)";
            }
            if (currentTreeNode.getColumnType().equalsIgnoreCase("TEXT")) {
                l = "";
            }
            if (currentTreeNode.getColumnType().equalsIgnoreCase("IMAGE")) {
                l = "";
            }

        }

        return l;
    }

    private boolean displayLength(int type) {
        boolean dl = false;

        switch (type) {
            case java.sql.Types.ARRAY:
                dl = true;
                break;
            case java.sql.Types.BINARY:
                dl = true;
                break;
            case java.sql.Types.BLOB:
                dl = true;
                break;
            case java.sql.Types.CLOB:
                dl = true;
                break;
            case java.sql.Types.INTEGER:
                dl = false;
                break;
            case java.sql.Types.FLOAT:
                dl = false;
                break;
            case java.sql.Types.DOUBLE:
                dl = false;
                break;
            case java.sql.Types.DECIMAL:
                dl = true;
                break;
            case java.sql.Types.NUMERIC:
                dl = true;
                break;
            case java.sql.Types.BIGINT:
                dl = false;
                break;
            case java.sql.Types.TINYINT:
                dl = false;
                break;
            case java.sql.Types.SMALLINT:
                dl = false;
                break;
            case java.sql.Types.DATE:
                dl = false;
                break;
            case java.sql.Types.TIMESTAMP:
                dl = false;
                break;
            case java.sql.Types.TIME:
                dl = false;
                break;
            case java.sql.Types.BIT:
                dl = false;
                break;
            case java.sql.Types.BOOLEAN:
                dl = false;
                break;
            case java.sql.Types.CHAR:
                dl = true;
                break;
            case java.sql.Types.NVARCHAR:
                dl = true;
                break;
            case java.sql.Types.VARCHAR:
                dl = true;
                break;
            case java.sql.Types.LONGNVARCHAR:
                dl = true;
                break;
            case java.sql.Types.LONGVARCHAR:
                dl = true;
                break;
            case java.sql.Types.LONGVARBINARY:
                dl = true;
                break;
            case java.sql.Types.NCHAR:
                dl = true;
                break;
            case java.sql.Types.NCLOB:
                dl = true;
                break;
            case java.sql.Types.OTHER:
                dl = true;
                break;
            case java.sql.Types.REAL:
                dl = false;
                break;
            case java.sql.Types.REF:
                dl = false;
                break;
            case java.sql.Types.SQLXML:
                dl = true;
                break;
            case java.sql.Types.STRUCT:
                dl = false;
                break;
            case java.sql.Types.JAVA_OBJECT:
                dl = true;
                break;
            case java.sql.Types.DATALINK:
                dl = false;
                break;
            case java.sql.Types.DISTINCT:
                dl = false;
                break;
            case java.sql.Types.NULL:
                dl = false;
                break;
            default:
                dl = false;
                break;
        }

        return dl;
    }

    private String getTypeName(int type) {
        String t = "";
        switch (type) {
            case java.sql.Types.ARRAY:
                t = "ARRAY";
                break;
            case java.sql.Types.BINARY:
                t = "BINARY";
                break;
            case java.sql.Types.BLOB:
                t = "BLOB";
                break;
            case java.sql.Types.CLOB:
                t = "CLOB";
                break;
            case java.sql.Types.INTEGER:
                t = "INTEGER";
                break;
            case java.sql.Types.FLOAT:
                t = "FLOAT";
                break;
            case java.sql.Types.DOUBLE:
                t = "DOUBLE";
                break;
            case java.sql.Types.DECIMAL:
                t = "DECIMAL";
                break;
            case java.sql.Types.NUMERIC:
                t = "NUMERIC";
                break;
            case java.sql.Types.BIGINT:
                t = "BIGINT";
                break;
            case java.sql.Types.TINYINT:
                t = "TINYINT";
                break;
            case java.sql.Types.SMALLINT:
                t = "SMALLINT";
                break;
            case java.sql.Types.DATE:
                t = "DATE";
                break;
            case java.sql.Types.TIMESTAMP:
                t = "TIMESTAMP";
                break;

            case java.sql.Types.TIME:
                t = "TIME";
                break;

            case java.sql.Types.BIT:
                t = "BIT";
                break;
            case java.sql.Types.BOOLEAN:
                t = "BOOLEAN";
                break;
            case java.sql.Types.CHAR:
                t = "CHAR";
                break;
            case java.sql.Types.NVARCHAR:
                t = "NVARCHAR";
                break;
            case java.sql.Types.VARCHAR:
                t = "VARCHAR";
                break;
            case java.sql.Types.LONGNVARCHAR:
                t = "LONGNVARCHAR";
                break;
            case java.sql.Types.LONGVARCHAR:
                t = "LONGVARCHAR";
                break;
            case java.sql.Types.LONGVARBINARY:
                t = "LONGVARBINARY";
                break;
            case java.sql.Types.NCHAR:
                t = "NCHAR";
                break;
            case java.sql.Types.NCLOB:
                t = "NCLOB";
                break;
            case java.sql.Types.OTHER:
                t = "OTHER";
                break;
            case java.sql.Types.REAL:
                t = "REAL";
                break;
            case java.sql.Types.REF:
                t = "REF";
                break;
            case java.sql.Types.SQLXML:
                t = "SQLXML";
                break;
            case java.sql.Types.STRUCT:
                t = "STRUCT";
                break;
            case java.sql.Types.JAVA_OBJECT:
                t = "JAVA_OBJECT";
                break;
            case java.sql.Types.DATALINK:
                t = "DATALINK";
                break;
            case java.sql.Types.DISTINCT:
                t = "DISTINCT";
                break;
            case java.sql.Types.NULL:
                t = "NULL";
                break;
            default:
                t = "oops, do not know this datatype";
                break;
        }
        return t;
    }

    private Color getTextSelectionColor() {
        System.out.print("fetching Selection Foreground as " + defaults.getColor("Tree.selectionForeground").toString());
        return defaults.getColor("Tree.selectionForeground");
    }

    private Color getSelectionBackground() {
        System.out.print("fetching Selection Background as " + defaults.getColor("Tree.selectionBackground").toString());
        return defaults.getColor("Tree.selectionBackground");
    }

    private Color getDefaultBG() {
        //return UIManager.getColor("List.background");
        return background;
    }

    private Color getDefaultFG() {
        //return UIManager.getColor("List.foreground");
        return foreground;
    }
}
