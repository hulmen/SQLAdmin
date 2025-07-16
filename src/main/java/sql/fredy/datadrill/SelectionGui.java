/*
 * SelectionGui.java
 *
 * Created on November 17, 2003
 */

/*
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
package sql.fredy.datadrill;

import sql.fredy.ui.ImageButton;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import java.util.logging.*;


import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;


/**
 *
 * @author sql@hulmen.ch
 */
public class SelectionGui extends javax.swing.JPanel {

    private Logger logger = Logger.getLogger("sql.fredy.datadrill");
    private String filePath = null;
    public JTabbedPane gui;
    Vector sqlStatement;
    private Vector sql = new Vector();
    public JComboBox liste;
    public ImageButton exit, save;

    String queryName;

    /**
     * Get the value of queryName.
     *
     * @return value of queryName.
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Set the value of queryName.
     *
     * @param v Value to assign to queryName.
     */
    public void setQueryName(String v) {
        this.queryName = v;
    }
    String selectionFile;

    /**
     * Get the value of selectionFile.
     *
     * @return value of selectionFile.
     */
    public String getSelectionFile() {
        return selectionFile;
    }

    /**
     * Set the value of selectionFile.
     *
     * @param v Value to assign to selectionFile.
     */
    public void setSelectionFile(String v) {
        this.selectionFile = v;
    }
   

    public SelectionGui() {
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.NORTH, selectListe());
        this.add(BorderLayout.SOUTH, buttonPanel());
        this.add(BorderLayout.CENTER, mainPanel());

    }

    // additional Constructor for recursive call
    public SelectionGui( String componentName, String fileName) {
        this.setName(componentName);
        sql = new Vector();
        sql.addElement((String) fileName);

        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER, mainPanel());
    }

    // mit true wird der Cursor eingeschaltet, false macht einen WAIT_CURSOR
    private void toggleCursor(boolean on) {
        if (on) {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        } else {
            this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        }
    }

    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        final ImageButton view = new ImageButton(null, "binocular.gif", "display result");
        view.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                toggleCursor(false);
                save.setEnabled(true);

                sql.fredy.sqltools.sqlTable sqt = new sql.fredy.sqltools.sqlTable(getQuery());
                final JDialog queryDialog = new JDialog();
                queryDialog.setTitle("Selection");
                queryDialog.setModal(false);
                queryDialog.getContentPane().setLayout(new BorderLayout());
                queryDialog.getContentPane().add(BorderLayout.CENTER, sqt);
                ImageButton closedialog = new ImageButton(null, "exit.gif", "close");
                JPanel anotherPanel = new JPanel();
                anotherPanel.setLayout(new FlowLayout());
                anotherPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                anotherPanel.add(closedialog);
                queryDialog.getContentPane().add(BorderLayout.SOUTH, anotherPanel);
                closedialog.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        queryDialog.dispose();
                        toggleCursor(true);
                        //closeCon();
                    }
                });

                queryDialog.addWindowListener(new WindowAdapter() {

                    public void windowActivated(WindowEvent e) {
                    }

                    public void windowClosed(WindowEvent e) {
                    }

                    public void windowClosing(WindowEvent e) {
                        //closeCon();
                        toggleCursor(true);
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

                queryDialog.pack();
                //queryDialog.setLocationRelativeTo(view);
                queryDialog.setVisible(true);
            }
        });
        panel.add(view);

        final ImageButton exec = new ImageButton(null, "save.gif", "save result without displaying it");

        exec.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                save.setEnabled(true);

                // fuehre die Query aus
                sql.fredy.sqltools.DataExportGui dataExportGui = new sql.fredy.sqltools.DataExportGui(getQuery(),false);
                                
                //dataExportGui.setLocationRelativeTo(exec);
            }
        });
        panel.add(exec);

        exit = new ImageButton(null, "exit.gif", "quit");
        panel.add(exit);

        // Der Button zur Anzeige der Query
        ImageButton displayQuery = new ImageButton(null, "documentdraw.gif", "display query");
        displayQuery.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                final JFrame frame = new JFrame("Query");
                sql.fredy.ui.TextEditor te = new sql.fredy.ui.TextEditor(getQuery());
                te.ok.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                    }
                });
                te.cancel.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                    }
                });

                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(BorderLayout.CENTER, te);
                frame.addWindowListener(new WindowAdapter() {

                    public void windowActivated(WindowEvent e) {
                    }

                    public void windowClosed(WindowEvent e) {
                    }

                    public void windowClosing(WindowEvent e) {

                        frame.dispose();
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
                frame.pack();
                frame.setVisible(true);
            }
        });
        panel.add(displayQuery);

        panel.setBorder(new EtchedBorder());
        return panel;
    }

    private String toDay() {

        Calendar c = Calendar.getInstance();

        String m = Integer.toString(c.get(Calendar.MONTH) + 1);
        if (m.length() < 2) {
            m = "0" + m;
        }

        String d = Integer.toString(c.get(Calendar.DATE));
        if (d.length() < 2) {
            d = "0" + d;
        }

        String hour = Integer.toString(c.get(Calendar.HOUR_OF_DAY));
        if (hour.length() < 2) {
            hour = "0" + hour;
        }

        String minute = Integer.toString(c.get(Calendar.MINUTE));
        if (minute.length() < 2) {
            minute = "0" + minute;
        }

        String second = Integer.toString(c.get(Calendar.SECOND));
        if (second.length() < 2) {
            second = "0" + second;
        }

        return Integer.toString(c.get(Calendar.YEAR)) + "-" + m + "-" + d + " " + hour + "-" + minute + "-" + second;

    }

    private JPanel selectListe() {

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        logger.log(Level.INFO, "reading selections.txt Path: " + getPath());
        ReadFile rf = new ReadFile(getPath() + "selections.txt");
        //System.out.println("This is the text I got: \n" + rf.getText());
        logger.log(Level.INFO, "Got file \n" + rf.getText());
        sql = new Vector();
        Vector v = rf.getLines();
        Vector data = new Vector();
        try {
            for (int i = 0; i < v.size(); i++) {
                StringTokenizer st = new StringTokenizer((String) v.elementAt(i), ";");
                while (st.hasMoreTokens()) {
                    data.addElement(st.nextToken());
                    sql.addElement(st.nextToken());
                }
            }
        } catch (Exception tokex) {
            logger.log(Level.WARNING,"Exception while loading file");
        }
        // to save all information
        setSelectionFile((String) sql.elementAt(0));
        setQueryName((String) data.elementAt(0));

        liste = new JComboBox(data);

        liste.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // set the name of the file
                setSelectionFile((String) sql.elementAt(liste.getSelectedIndex()));
                setQueryName((String) liste.getSelectedItem());

                // Selektionen koennen nicht gespeichert werden, bevor
                // sie einmal ausgefuehrt wurden
                save.setEnabled(false);
                parseFile((String) sql.elementAt(liste.getSelectedIndex()));
            }
        });

        save = new ImageButton(null, "save.gif", "Save this selection");
        ImageButton load = new ImageButton(null, "load.gif", "Load a previously saved selection");
        ImageButton origQ = new ImageButton(null, "box.gif", "Display original query");

        save.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                saveQuery();
            }
        });
        save.setEnabled(false);

        load.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                loadQuery();
            }
        });

        origQ.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                final JFrame frame = new JFrame("original query");
                sql.fredy.ui.TextEditor te = new sql.fredy.ui.TextEditor(getOrigQuery());
                te.ok.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                    }
                });
                te.cancel.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                    }
                });

                frame.getContentPane().setLayout(new BorderLayout());
                frame.getContentPane().add(BorderLayout.CENTER, te);
                frame.addWindowListener(new WindowAdapter() {

                    public void windowActivated(WindowEvent e) {
                    }

                    public void windowClosed(WindowEvent e) {
                    }

                    public void windowClosing(WindowEvent e) {

                        frame.dispose();
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
                frame.pack();
                frame.setVisible(true);
            }
        });

        panel.add(liste);
        panel.add(load);
        panel.add(save);
        panel.add(origQ);

        panel.setBorder(new EtchedBorder());
        return panel;
    }

    private JPanel mainPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        gui = new JTabbedPane();

        // first use create initial GUI
        parseFile((String) sql.elementAt(0));
        JScrollPane scp = new JScrollPane(gui);

        panel.add(BorderLayout.CENTER, scp);
        return panel;
    }

    private String getFileFromChooser() {
        String fileName = null;
        GetFileName gfn = new GetFileName(new String[]{"xml", "XML"});
        return gfn.getFileName();
    }

    private void saveQuery() {

        String[] panelTyp = {"#execute",
            "#checkbox",
            "#between",
            "#criteriabox",
            "#gui",
            "#selectiongui"};

        Element top = new Element("selection");
        Element name = new Element("name");
        Element benutzer = new Element("user");
        Element datei = new Element("file");
        Element erfasst = new Element("created");

        name.setText(getQueryName());
        benutzer.setText(System.getProperty("user.name"));
        datei.setText(getSelectionFile());
        erfasst.setText(toDay());

        top.addContent(name);
        top.addContent(datei);
        top.addContent(benutzer);
        top.addContent(erfasst);

        top = getXMLDataFromComponents(top);

        Document doc = new Document(top);
        XMLOutputter outputter = new XMLOutputter();

        String fileName = getFileFromChooser();
        if (fileName != null) {
            try {
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
                outputter.output(doc, out);
                logger.log(Level.FINEST, "Writing into file: " + fileName);
            } catch (IOException ioex) {
                logger.log(Level.WARNING, "Could not write data");
                logger.log(Level.INFO, ioex.getMessage());
            }
        }
    }

    private void loadQuery() {
        Document doc = null;

        String fileName = getFileFromChooser();
        if (fileName != null) {
            try {
                SAXBuilder builder = new SAXBuilder();
                doc = builder.build(new File(fileName));
            } catch (JDOMException jde) {
                logger.log(Level.WARNING, "Could not read file");
                logger.log(Level.FINE, jde.getMessage());
            } catch (IOException ioex) {
                logger.log(Level.WARNING, "Could not read file");
                logger.log(Level.FINE, ioex.getMessage());
            }
            if (doc != null) {
                Element element = doc.getRootElement();

                // let's set the selected query in the combobox
                liste.setSelectedItem(element.getChild("name").getText());

                /**
                 * there is no need to parse again, because the JComboBox is
                 * throwing an event and therefore parses the file by itself
                 *
                 */
                //parseFile( element.getChild("file").getText());
                setXMLData(element);
            }
        }

    }

    private Element getXMLDataFromComponents(Element top) {

        Component c[] = gui.getComponents();

        for (int i = 0; i < c.length; i++) {
            String componentName = c[i].getName();
            if (c[i] instanceof SelectionGui) {

                SelectionGui sg = (SelectionGui) c[i];
                top = sg.getXMLDataFromComponents(top);
            } else {
                if (c[i] instanceof BetweenPanel) {
                    BetweenPanel cbp = (BetweenPanel) c[i];
                    top.addContent(cbp.getXML());
                }
                if (c[i] instanceof CheckBoxPanel) {
                    CheckBoxPanel cbp = (CheckBoxPanel) c[i];
                    top.addContent(cbp.getXML());
                }
                if (c[i] instanceof CriteriaPanel) {
                    CriteriaPanel cbp = (CriteriaPanel) c[i];
                    top.addContent(cbp.getXML());
                }
                if (c[i] instanceof SelectPanel) {
                    SelectPanel cbp = (SelectPanel) c[i];
                    top.addContent(cbp.getXML());
                }

            }

        }
        return top;

    }

    public Vector getAllComponents() {
        Vector v = new Vector();
        Component c[] = gui.getComponents();
        for (int i = 0; i < c.length; i++) {
            if (c[i] instanceof SelectionGui) {
                SelectionGui sg = (SelectionGui) c[i];
                Vector v2 = sg.getAllComponents();
                for (int j = 0; j < v2.size(); j++) {
                    v.addElement(v2.elementAt(j));
                }
            } else {
                v.addElement(c[i]);
            }
        }

        return v;
    }

    private void setXMLData(Element element) {

        try {

            // get all components
            Vector components = getAllComponents();

            // get all panels
            Iterator iter = (element.getChildren("panel")).iterator();

            // then loop the panels
            while (iter.hasNext()) {

                // this is a panel
                Element elt = (Element) iter.next();

                // what name has it
                String name = elt.getChild("name").getText();

                // what type is it
                String type = elt.getChild("type").getText();
                logger.log(Level.FINE, "Name: " + name);
                logger.log(Level.FINE, "Type: " + type);

                for (int i = 0; i < components.size(); i++) {
                    Component c3 = (Component) components.elementAt(i);
                    String componentName = c3.getName();
                    if (componentName.equals(name)) {
                        if (type.equals("betweenpanel")) {
                            BetweenPanel bp = (BetweenPanel) components.elementAt(i);
                            bp.setAllValues(elt);
                        }
                        if (type.equals("checkboxpanel")) {
                            CheckBoxPanel cbp = (CheckBoxPanel) components.elementAt(i);
                            cbp.setAllValues(elt);
                        }
                        if (type.equals("criteriapanel")) {
                            CriteriaPanel cp = (CriteriaPanel) components.elementAt(i);
                            cp.setAllValues(elt);
                        }
                        if (type.equals("selectionpanel")) {
                            SelectPanel sp = (SelectPanel) components.elementAt(i);
                            sp.setAllValues(elt);
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "unable to set this panels values");
            logger.log(Level.FINE, "Exception is: " + e.getMessage());
        }
    }

    private JPanel selectionPanel(String name,
            String type,
            String prefix,
            String query) {

        SelectPanel sp = new SelectPanel(name, type, prefix, query);

        return sp;
    }

    private JPanel checkBoxPanel(String name,
            String file) {

        CheckBoxPanel cbp = new CheckBoxPanel(name, file);

        return cbp;
    }

    private JPanel criteriaPanel(String name,
            String prefix,
            String file) {

        CriteriaPanel cp = new CriteriaPanel(name, prefix, file);

        return cp;
    }

    private JPanel betweenPanel(String name,
            String prefix,
            int anzahlZeilen,
            boolean und,
            boolean zahl) {
        BetweenPanel bp = new BetweenPanel(name, prefix, anzahlZeilen, und, zahl);
        return bp;
    }

    public JPanel guiPanel(String name, String file) {
        SqlParser sqp = new SqlParser(file);
        sqp.setName(name);
        return sqp;
    }

    public String getData() {
        return getQuery();
    }

    public String getQuery() {
        StringBuffer sb = new StringBuffer();
        String panelTyp = "";
        for (int i = 0; i < sqlStatement.size(); i++) {

            // ist es ein ListPanel?
            panelTyp = "#execute";
            if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                String s = (String) sqlStatement.elementAt(i);
                s = s.substring(panelTyp.length() + 1);
                StringTokenizer st = new StringTokenizer(s, ";");
                logger.log(Level.FINE, "Execute: {0}", s);
                while (st.hasMoreTokens()) {
                    String n = (String) st.nextToken();
                    String t = (String) st.nextToken();
                    String p = (String) st.nextToken();
                    String q = (String) st.nextToken();
                    sb.append(getQueryFromComponent(n));
                    sb.append("\n");
                }
            }

            // ist es ein CheckBoxPanel ?
            panelTyp = "#checkbox";
            if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                logger.log(Level.FINE, "adding checkbox ");
                String s = (String) sqlStatement.elementAt(i);
                s = s.substring(panelTyp.length() + 1);
                StringTokenizer st = new StringTokenizer(s, ";");
                logger.log(Level.FINE, "CheckBox: " + s);
                while (st.hasMoreTokens()) {
                    String n = (String) st.nextToken();
                    String f = (String) st.nextToken();
                    sb.append(getQueryFromCheckBox(n));
                    sb.append("\n");
                }
            }

            // is es ein CriteriaPanel?
            panelTyp = "#criteriabox";
            if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                logger.log(Level.FINE, "adding criteriabox ");
                String s = (String) sqlStatement.elementAt(i);
                s = s.substring(panelTyp.length() + 1);
                StringTokenizer st = new StringTokenizer(s, ";");
                logger.log(Level.FINE, "CriteriaPanel: " + s);
                while (st.hasMoreTokens()) {
                    String n = (String) st.nextToken();
                    String p = (String) st.nextToken();
                    String f = (String) st.nextToken();
                    sb.append(getQueryFromCriteriaBox(n));
                    sb.append("\n");
                }
            }

            // ist es ein BetweenPanel?
            panelTyp = "#between";
            if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                logger.log(Level.FINE, "adding betweenpanel ");
                String s = (String) sqlStatement.elementAt(i);
                s = s.substring(panelTyp.length() + 1);
                StringTokenizer st = new StringTokenizer(s, ";");
                logger.log(Level.FINE, "BetweenPanel: " + s);
                while (st.hasMoreTokens()) {
                    String n = (String) st.nextToken(); // name
                    String p = (String) st.nextToken(); // prefix
                    int z = Integer.parseInt((String) st.nextToken()); // anzahl zeilen
                    boolean a = true;  // AND
                    boolean t = true;  // TEXT
                    if (((String) st.nextToken()).toLowerCase().equals("or")) {
                        a = false;
                    }
                    if (((String) st.nextToken()).toLowerCase().equals("zahl")) {
                        t = false;
                    }
                    sb.append(getQueryFromBetweenPanel(n));
                    sb.append("\n");
                }
            }

            // bauen wir ein GUI?
            panelTyp = "#gui";
            if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                logger.log(Level.FINE, "adding GUI-panel ");
                String s = (String) sqlStatement.elementAt(i);
                s = s.substring(panelTyp.length() + 1);
                StringTokenizer st = new StringTokenizer(s, ";");
                logger.log(Level.FINE, "GUI-Panel: " + s);
                while (st.hasMoreTokens()) {
                    String n = (String) st.nextToken(); // name
                    String q = (String) st.nextToken(); // query
                    sb.append(getQueryFromGuiPanel(n));
                    sb.append("\n");
                }
            }

            // ist es ein SelectionGUI ?
            panelTyp = "#selectiongui";
            if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                logger.log(Level.FINE, "adding selectiongui ");
                String s = (String) sqlStatement.elementAt(i);
                s = s.substring(panelTyp.length() + 1);
                StringTokenizer st = new StringTokenizer(s, ";");
                logger.log(Level.FINE, "SelectionGUI: " + s);
                while (st.hasMoreTokens()) {
                    String n = (String) st.nextToken();
                    String f = (String) st.nextToken();
                    sb.append(getQueryFromSelectionGUI(n));
                    sb.append("\n");
                }
            }

            // das ist ein normales Statement
            if (!((String) sqlStatement.elementAt(i)).toLowerCase().startsWith("#")) {

                sb.append((String) sqlStatement.elementAt(i));
                sb.append("\n");
            }

        }
        QueryChecker queryChecker = new QueryChecker();
        return queryChecker.check(sb.toString());
    }

    // find a component by its name
    private String getQueryFromComponent(String n) {
        String s = "";
        Component c[] = gui.getComponents();

        for (int i = 0; i < c.length; i++) {
            String componentName = c[i].getName();
            if (componentName.equals(n)) {
                SelectPanel sp = (SelectPanel) c[i];
                s = sp.getData();
            }
        }
        return s;
    }

    public Element getXMLfromComponent(String n, String type) {
        Element elt = null;
        Component c[] = gui.getComponents();

        for (int i = 0; i < c.length; i++) {
            String componentName = c[i].getName();
            if (componentName.equals(n)) {
                if (type.equals("#execute")) {
                    elt = ((SelectPanel) c[i]).getXML();
                }
                if (type.equals("#checkbox")) {
                    elt = ((CheckBoxPanel) c[i]).getXML();
                }
                if (type.equals("#criteriabox")) {
                    elt = ((CriteriaPanel) c[i]).getXML();
                }
                if (type.equals("#between")) {
                    elt = ((BetweenPanel) c[i]).getXML();
                }

            }
        }
        logger.log(Level.FINE, "Name " + n);
        logger.log(Level.FINE, "Type " + type);
        logger.log(Level.FINE, "Element " + elt.toString());

        return elt;
    }

    // lese die Query aus der CheckBox
    private String getQueryFromCheckBox(String n) {
        String s = "";
        Component c[] = gui.getComponents();

        for (int i = 0; i < c.length; i++) {
            String componentName = c[i].getName();
            if (componentName.equals(n)) {
                CheckBoxPanel sp = (CheckBoxPanel) c[i];
                s = sp.getData();
            }
        }
        return s;
    }

    // hole das Resultat aus der CriteriaBox
    private String getQueryFromCriteriaBox(String n) {
        String s = "";
        Component c[] = gui.getComponents();

        for (int i = 0; i < c.length; i++) {
            String componentName = c[i].getName();
            if (componentName.equals(n)) {
                CriteriaPanel cp = (CriteriaPanel) c[i];
                s = cp.getData();
            }
        }
        return s;
    }

    // hole das Resultat aus dem BetWeenPanel
    private String getQueryFromBetweenPanel(String n) {
        String s = "";
        Component c[] = gui.getComponents();

        for (int i = 0; i < c.length; i++) {
            String componentName = c[i].getName();
            if (componentName.equals(n)) {
                BetweenPanel bp = (BetweenPanel) c[i];
                s = bp.getData();
            }
        }
        return s;
    }

    // hole das Resultat aus dem GUIPanel
    private String getQueryFromGuiPanel(String n) {
        String s = "";
        Component c[] = gui.getComponents();

        for (int i = 0; i < c.length; i++) {
            String componentName = c[i].getName();
            if (componentName.equals(n)) {
                SqlParser sqp = (SqlParser) c[i];
                s = sqp.getData();
            }
        }
        return s;
    }

    // hole das Resultat aus dem SelectionGUI
    private String getQueryFromSelectionGUI(String n) {
        String s = "";
        Component c[] = gui.getComponents();

        for (int i = 0; i < c.length; i++) {
            String componentName = c[i].getName();
            if (componentName.equals(n)) {
                SelectionGui sg = (SelectionGui) c[i];
                s = sg.getData();
            }
        }
        return s;
    }
    String origQuery = "";

    /**
     * Get the value of origQuery.
     *
     * @return value of origQuery.
     */
    public String getOrigQuery() {
        return origQuery;
    }

    /**
     * Set the value of origQuery.
     *
     * @param v Value to assign to origQuery.
     */
    public void setOrigQuery(String v) {
        this.origQuery = v;
    }

    // Pattern #execute(name;typ;query)
    private void parseFile(String file) {

        gui.removeAll();
        logger.log(Level.INFO, "Parsing file: " + file);

        ReadFile rf = new ReadFile(getPath() + file);
        sqlStatement = rf.getLines();
        setOrigQuery(rf.getText());

        setSelectionFile(file);
        try {
            for (int i = 0; i < sqlStatement.size(); i++) {

                // zeige hier das ListenElement (SelectionPanel) f�r eine Auswahl aus einer Liste
                String panelTyp = "#execute";
                if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                    String s = (String) sqlStatement.elementAt(i);
                    s = s.substring(panelTyp.length() + 1);
                    StringTokenizer st = new StringTokenizer(s, ";");
                    logger.log(Level.FINE, "Execute: " + s);
                    while (st.hasMoreTokens()) {
                        String n = (String) st.nextToken(); // Name
                        String t = (String) st.nextToken(); // Typ
                        String p = (String) st.nextToken(); // Prefix
                        String q = (String) st.nextToken();  // Query
                        q = q.trim();
                        q = q.substring(0, q.length() - 1);
                        gui.add(n, selectionPanel(n, t, p, q));
                    }
                    gui.updateUI();
                }

                // zeige hier eine CheckBox an f�r die Selektion der Ausgabespalten
                panelTyp = "#checkbox";
                if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                    String s = (String) sqlStatement.elementAt(i);
                    s = s.substring(panelTyp.length() + 1);
                    StringTokenizer st = new StringTokenizer(s, ";");
                    logger.log(Level.FINE, "CheckBox: " + s);
                    while (st.hasMoreTokens()) {
                        String n = (String) st.nextToken();
                        String f = (String) st.nextToken();
                        f = f.trim();
                        f = f.substring(0, f.length() - 1);
                        gui.add(n, checkBoxPanel(n, f));
                    }
                    gui.updateUI();
                }

                // zeige hier ein CriteriaPanel an
                panelTyp = "#criteriabox";
                if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                    String s = (String) sqlStatement.elementAt(i);
                    s = s.substring(panelTyp.length() + 1);
                    StringTokenizer st = new StringTokenizer(s, ";");
                    logger.log(Level.FINE, "CriteriaBox: " + s);
                    while (st.hasMoreTokens()) {
                        String n = (String) st.nextToken();
                        String p = (String) st.nextToken();
                        String f = (String) st.nextToken();
                        f = f.substring(0, f.length() - 1);
                        gui.add(n, criteriaPanel(n, p, f));
                    }
                    gui.updateUI();
                }

                // zeige hier ein BetweenPanel an
                panelTyp = "#between";
                if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                    String s = (String) sqlStatement.elementAt(i);
                    s = s.substring(panelTyp.length() + 1);
                    StringTokenizer st = new StringTokenizer(s, ";");
                    logger.log(Level.FINE, "BetweenPanel: " + s);
                    while (st.hasMoreTokens()) {
                        String n = (String) st.nextToken(); // name
                        String p = (String) st.nextToken(); // prefix
                        int z = Integer.parseInt((String) st.nextToken()); // anzahl zeilen
                        boolean a = true;  // AND
                        boolean t = true;  // TEXT
                        if (((String) st.nextToken()).toLowerCase().equals("or")) {
                            a = false;
                        }
                        if (((String) st.nextToken()).toLowerCase().startsWith("zahl")) {
                            t = false;
                        }
                        gui.add(n, betweenPanel(n, p, z, a, t));
                    }
                    gui.updateUI();
                }

                // bauen wir ein GUI?
                panelTyp = "#gui";
                if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                    logger.log(Level.FINE, "adding GUI-panel ");
                    String s = (String) sqlStatement.elementAt(i);
                    s = s.substring(panelTyp.length() + 1);
                    StringTokenizer st = new StringTokenizer(s, ";");
                    logger.log(Level.FINE, "GUI-Panel: " + s);
                    while (st.hasMoreTokens()) {
                        String n = (String) st.nextToken(); // name
                        String f = (String) st.nextToken(); // File-Name containing the query
                        logger.log(Level.FINE, "Name=" + n);
                        logger.log(Level.FINE, "File=" + f);
                        f = f.trim();
                        f = f.substring(0, f.length() - 1);
                        gui.add(n, guiPanel(n, f));
                    }
                    gui.updateUI();
                }

                // rekursiver Aufruf des SelectionGUI
                panelTyp = "#selectiongui";
                if (((String) sqlStatement.elementAt(i)).toLowerCase().startsWith(panelTyp.toLowerCase())) {
                    logger.log(Level.FINE, "adding SelectionGUI ");
                    String s = (String) sqlStatement.elementAt(i);
                    s = s.substring(panelTyp.length() + 1);
                    StringTokenizer st = new StringTokenizer(s, ";");
                    logger.log(Level.FINE, "SelectionGUI-Panel: " + s);
                    while (st.hasMoreTokens()) {
                        String n = (String) st.nextToken(); // name
                        String f = (String) st.nextToken(); // File-Name containing the query
                        logger.log(Level.FINE, "Name=" + n);
                        f = f.trim();
                        f = f.substring(0, f.length() - 1);
                        logger.log(Level.FINE, "File=" + f);
                        gui.add(n, new SelectionGui( n, f));
                    }
                    gui.updateUI();
                }

            }

        } catch (Exception parseFileException) {
            logger.log(Level.WARNING, "Exception while parse file: " + parseFileException.getMessage());
            //parseFileException.printStackTrace();
        } 

    }

    /*
     private String getPath() {
     String top="";
     try {
     URL resource = SelectionGui.class.getResource("." + java.io.File.separator + "resources");
     top = resource.getFile().toString() + java.io.File.separator;
     top = URLDecoder.decode(top,"UTF-8");

     } catch (Exception use) {
     logger.log(Level.WARNING,"Can not read directory " + top);
     logger.log(Level.INFO,"Error: " + use.getMessage());
     top = getPathFromProperties();
     }

     return top;

     }
     */
    private String getPath() {
        // Load Properties

        if (filePath != null) {
            return filePath;
        }

        Properties prop = new Properties();

        /**
         * this is the same dir as the class: Admin.class.getResourceAsStream
         * but we put the props-File in the user's home-dir if there is not -D
         * parameter for place given at start
         *
         */
        try {

            String admin_dir = System.getProperty("admin.work");
            if (admin_dir == null) {
                admin_dir = System.getProperty("user.home");
            }

            try {
                FileInputStream fip = new FileInputStream(admin_dir + File.separator + "admin.selgui.props");
                prop.load(fip);
                fip.close();
            } catch (Exception fipEx) {
                logger.log(Level.INFO, "need to create properties for the first time");
            }

            if (prop.getProperty("selection.path") == null) {
                filePath = admin_dir + File.separator + "datadrill" + File.separator + "resources" + File.separator;
                prop.put("selection.path", filePath);

                // creating the directory structure under the users Home-dir and add examples
                createExampleEnvironment(admin_dir);

                // write back properties
                try {
                    FileOutputStream fops = new FileOutputStream(admin_dir + File.separator + "admin.selgui.props");
                    prop.store(fops, "Selection properties Fredy's SqlAdmin");
                    fops.close();
                } catch (Exception propE) {
                    logger.log(Level.WARNING, "Can not save properties " + propE.getMessage());
                }

            } else {
                filePath = prop.getProperty("selection.path");
            }

        } catch (Exception ioex) {
            logger.log(Level.WARNING, "Something went wrong. " + ioex.getMessage());
        }
        return filePath;
    }

    private void createExampleEnvironment(String startDirectory) {
        try {

            // this nis the directory
            File drillFile = new File(startDirectory + File.separator + "datadrill" + File.separator + "resources");
            drillFile.mkdirs();

            // now we get all the eamples out of the jar-Stream
            // happily we have os like windows, so we need to have other files with CR+LF terminated lines
            String os = (System.getProperty("os.name")).toLowerCase();
            String bs = "unix";
            if (os.indexOf("windows") > 0) {
                bs = "windows";
            }

            String p1 = startDirectory + File.separator + "datadrill" + File.separator + "resources" + File.separator;
            readFromJarAndWriteToFile("/resources/datadrillexamples/" + bs + "/car-list.display", p1 + "car-list.display");
            readFromJarAndWriteToFile("/resources/datadrillexamples/" + bs + "/car-list.other", p1 + "car-list.other");
            readFromJarAndWriteToFile("/resources/datadrillexamples/" + bs + "/car-list.sel", p1 + "car-list.sel");
            readFromJarAndWriteToFile("/resources/datadrillexamples/" + bs + "/employee-list.display", p1 + "employee-list.display");
            readFromJarAndWriteToFile("/resources/datadrillexamples/" + bs + "/employee-list.function", p1 + "employee-list.function");
            readFromJarAndWriteToFile("/resources/datadrillexamples/" + bs + "/employee-list.sel", p1 + "employee-list.sel");
            readFromJarAndWriteToFile("/resources/datadrillexamples/" + bs + "/selections.txt", p1 + "selections.txt");

        } catch (Exception createException) {
            logger.log(Level.WARNING, "error while creating directories and example environment for dataDrill " + createException.getMessage());
        }
    }

    private void readFromJarAndWriteToFile(String resource, String target) {

        InputStream is = null;

        try {
            is = this.getClass().getClassLoader().getResourceAsStream(resource);
            try {
                DataOutputStream outptstr = new DataOutputStream(
                        new BufferedOutputStream(
                                new FileOutputStream(target)));
                int c = 0;
                try {
                    while (c > -1) {
                        c = is.read();
                        outptstr.writeByte(c);
                    }
                } catch (IOException wex) {
                    logger.log(Level.WARNING, "Message: " + wex.getMessage());
                }
                outptstr.flush();
                outptstr.close();
                logger.log(Level.INFO, "read: " + resource + " and wrote: " + target);
            } catch (IOException execp) {
                logger.log(Level.WARNING, "can not write this file " + target);
                logger.log(Level.INFO, "Exception message: " + execp.getMessage());
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "could not create file " + resource + " Message: " + e.getMessage());
        }

    }

    public static void main(String args[]) {
        String host = "localhost";
        String user = System.getProperty("user.name");
        String password = "";
        String db = "";

        int i = 0;
        while (i < args.length) {

            if ((args[i].equals("-h")) || (args[i].equals("-host"))) {
                i++;
                host = args[i];
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
                db = args[i];
            }

            if ((args[i].equals("-help")) || (args[i].equals("-h"))) {
                System.out.println("Fredy's SelectionGUI\n"
                        + "--------------------\n"
                        + "Parameter:\n"
                        + "-h host      (or -host)\n"
                        + "-u user      (or -user)\n"
                        + "-p password  (or -password)\n"
                        + "-d database  (or -db)\n");

                System.exit(0);
            }
            i++;
        }

        JFrame cf = new JFrame("SelectionGUI");

        if (args.length == 0) {
            System.out.println("Fredy's SelectionGUI\n"
                    + "--------------------\n"
                    + "Parameter:\n"
                    + "-h host      (or -host)\n"
                    + "-u user      (or -user)\n"
                    + "-p password  (or -password)\n"
                    + "-d database  (or -db)\n");

            //System.exit(0);
            // no information provided, ask on commandline
            String cont = readFromPrompt("No information provided, continue? Y/n", "Y");
            if (!cont.toUpperCase().startsWith("Y")) {
                System.exit(0);
            }
            host = readFromPrompt("Database host ", host);
            user = readFromPrompt("Database user ", user);
            password = readFromPrompt("Database password ", "");
            db = readFromPrompt("Database name ", db);

        }
   
        final SelectionGui sel = new SelectionGui();

        sel.exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {               
                System.exit(0);
            }
        });

        cf.addWindowListener(new WindowAdapter() {

            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {            
                System.exit(0);
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

        cf.getContentPane().setLayout(new BorderLayout());
        cf.getContentPane().add(sel, BorderLayout.CENTER);
        cf.pack();
        cf.setVisible(true);
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
}
