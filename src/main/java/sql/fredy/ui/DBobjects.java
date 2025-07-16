/*


   This class is a JPanel representing the children of a DB-OBject
   It is implemented as a Closeable Frame

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
package sql.fredy.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.MouseInfo;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import org.apache.commons.lang3.SystemUtils;
import sql.fredy.metadata.SqlTree;

public class DBobjects extends JFrame {

    private Logger logger = Logger.getLogger("sql.fredy.ui");

    private List<ObjectChildren> showChildren = new ArrayList<ObjectChildren>();
    private DBobjects.ChildrenTableModel childrenTableModel = null;
    private List<ObjectChildren> objectChildren;
    private JTable childrenTable;
    private JScrollPane childrenTableScrollPane;

    public DBobjects() {
        super("Children of");
        initGui();
    }

    public DBobjects(List<ObjectChildren> children, String title) {
        super("Children of " + title);
        showChildren = children;
        objectChildren = children;
        initGui();
    }

    public void close() {
        this.dispose();
    }

    public void doPack() {
        this.pack();
    }

    private void initGui() {
        this.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                close();
            }

            public void windowDeactivated(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowOpened(WindowEvent e) {
            }
        });

        //objectChildren = new ArrayList<>();
        this.getContentPane().setLayout(new BorderLayout());
        JPanel southpanel = new JPanel();
        southpanel.setLayout(new FlowLayout());
        ImageButton close = new ImageButton(null, "exit.gif", "Close");
        close.addActionListener((ActionEvent e) -> {
            this.setVisible(false);
            this.dispose();
        });

        ImageButton copy = new ImageButton(null, "copy.gif", "Copy all to clipboard");
        Toolkit tk = this.getToolkit();
        copy.addActionListener((ActionEvent e) -> {

            StringBuilder sb = new StringBuilder();
            for (ObjectChildren o : showChildren) {
                  sb.append(o.getColumnname()).append("\n");
                  /*
                  if ( SystemUtils.IS_OS_WINDOWS) {
                      sb.append("\r");
                  }
                  */
            }
            StringSelection ss = new StringSelection(sb.toString());
            tk.getSystemClipboard().setContents(ss, ss);
        });

        southpanel.add(copy);
        southpanel.add(close);
        
        southpanel.setBorder(new EtchedBorder());

        JPanel northpanel = new JPanel();
        northpanel.setLayout(new FlowLayout());

        String[] whatDisplay = {"all", "Name and column type", "Entity, Table, Name and Column Type", "Database, Table Name and Column Type"};
        JComboBox displayKids = new JComboBox(whatDisplay);
        displayKids.setSelectedIndex(1);
        displayKids.addActionListener((ActionEvent e) -> {
            childrenContent(displayKids.getSelectedIndex());

        });
        northpanel.add(displayKids);

        JTextField filterField = new JTextField(20);

        northpanel.add(new JLabel("Filter Displayname"));
        northpanel.add(filterField);

        try {
            childrenContent(displayKids.getSelectedIndex());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Exception while getting Children content {0}", e.getMessage());
        }

        filterField.addActionListener((ActionEvent e) -> {
            try {
                if (filterField.getText().isEmpty()) {
                    showChildren = objectChildren;
                } else {
                    showChildren = new ArrayList<ObjectChildren>();
                    for (ObjectChildren o : objectChildren) {
                        if (o.getDisplayablename().toLowerCase().contains(filterField.getText().toLowerCase())) {
                            showChildren.add(o);
                        }
                    }
                }
                childrenContent(displayKids.getSelectedIndex());
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Exception {0}", ex.getMessage());
            }
        });

        childrenTable = new JTable(childrenTableModel);
        //childrenTable.setName("childrentable");
        childrenTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        childrenTable.setColumnSelectionAllowed(true);
        childrenTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnAdjuster tca = new TableColumnAdjuster(childrenTable);
        tca.adjustColumns();
        childrenTableScrollPane = new JScrollPane(childrenTable);
        childrenTableScrollPane.setName("childrentablescrollpane");

        this.getContentPane().add(BorderLayout.NORTH, northpanel);
        this.getContentPane().add(BorderLayout.CENTER, childrenTableScrollPane);
        this.getContentPane().add(BorderLayout.SOUTH, southpanel);
        this.pack();
        this.setLocation(MouseInfo.getPointerInfo().getLocation());
        this.setVisible(true);

    }

    private void childrenContent(int displayType) {
        /*
        The way to display the children
        0 = all
        1 = Display Name and Column Type and Length
        2 = Entity Type, Table, Display Name, Column Type and length
        3 = Database, Table, Displayable Name, Column Type and length
         */
        if ((childrenTableModel == null) || (displayType != childrenTableModel.getDisplayType())) {
            childrenTableModel = new DBobjects.ChildrenTableModel(displayType);
        }
        Object[][] content;

        Iterator iter = showChildren.iterator();
        int line = 0;

        switch (displayType) {
            default:
                content = new Object[showChildren.size()][8];
                line = 0;
                while (iter.hasNext()) {
                    ObjectChildren oj = (ObjectChildren) iter.next();
                    content[line][0] = oj.getType();
                    content[line][1] = oj.getDatabase();
                    content[line][2] = oj.getSchema();
                    content[line][3] = oj.getTable();
                    content[line][4] = oj.getDisplayablename();
                    content[line][5] = oj.getColumnname();
                    content[line][6] = oj.getColumntype();
                    content[line][7] = oj.getLenght();
                    line++;
                }
                break;
            case 1:

                content = new Object[showChildren.size()][3];
                line = 0;
                while (iter.hasNext()) {
                    ObjectChildren oj = (ObjectChildren) iter.next();
                    content[line][0] = oj.getDisplayablename();
                    content[line][1] = oj.getColumntype();
                    content[line][2] = oj.getLenght();
                    line++;
                }
                break;
            case 2:

                content = new Object[showChildren.size()][5];
                line = 0;
                while (iter.hasNext()) {
                    ObjectChildren oj = (ObjectChildren) iter.next();
                    content[line][0] = oj.getType();
                    content[line][1] = oj.getTable();
                    content[line][2] = oj.getDisplayablename();
                    content[line][3] = oj.getColumntype();
                    content[line][4] = oj.getLenght();
                    line++;
                }
                break;
            case 3:
                content = new Object[showChildren.size()][5];
                line = 0;
                while (iter.hasNext()) {
                    ObjectChildren oj = (ObjectChildren) iter.next();
                    content[line][0] = oj.getDatabase();
                    content[line][1] = oj.getTable();
                    content[line][2] = oj.getDisplayablename();
                    content[line][3] = oj.getColumntype();
                    content[line][4] = oj.getLenght();
                    line++;
                }
                break;
        }

        childrenTableModel.setContent(content);
        childrenTable.setModel(childrenTableModel);
        TableColumnAdjuster tca = new TableColumnAdjuster(childrenTable);
        tca.adjustColumns();
        childrenTable.updateUI();
        this.revalidate();
        this.repaint();

    }

    public static void main(String[] args) {
        DBobjects dbo = new DBobjects();
    }

    private static class ChildrenTableModel extends AbstractTableModel {

        private int displayType = 1;
        String[] childrenheader;

        /*
        @param displayType if set to 0 display the fields Database,Type, Schema,Table, Displayable Name, Column name, Column type
                           if set to 1 display the fields Displayable Name, Column type
                           if set to 2 display the fields Type, Table, Displayable Name,  Column type
                           if set to 3 display the fields Database, Table, Displayable Name, Column type
         */
        public ChildrenTableModel(int displayType) {

            setDisplayType(displayType);

            childrenheader = switch (displayType) {
                case 1 ->
                    new String[]{
                        "Displayable name",
                        "Column type",
                        "Length"
                    };
                case 2 ->
                    new String[]{
                        "Type",
                        "Table",
                        "Displayable name",
                        "Column type",
                        "Length"
                    };
                case 3 ->
                    new String[]{
                        "Database",
                        "Table",
                        "Displayable name",
                        "Column type",
                        "Length"
                    };
                default ->
                    new String[]{
                        "Type",
                        "Database",
                        "Schema",
                        "Table",
                        "Displayable name",
                        "Column name",
                        "Column type",
                        "Length"
                    };
            };

        }

        Object[][] content;

        @Override
        public String getColumnName(int col) {
            return childrenheader[col];
        }

        @Override
        public int getRowCount() {
            return content.length;
        }

        @Override
        public int getColumnCount() {
            return childrenheader.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return content[rowIndex][columnIndex];
        }

        public void setContent(Object[][] content) {
            this.content = content;
            fireTableDataChanged();
        }

        /**
         * @return the displayType
         */
        public int getDisplayType() {
            return displayType;
        }

        /**
         * @param displayType the displayType to set
         */
        public void setDisplayType(int displayType) {
            this.displayType = displayType;
        }

    }
}
