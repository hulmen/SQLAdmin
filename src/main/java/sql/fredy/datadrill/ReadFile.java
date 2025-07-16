package sql.fredy.datadrill;

/**
 * ReadFile reads a file (only for those who did not realize it)
 *
 * Created by: Fredy Fischer Date : 05. Dec. 2001 Version : 1.0 1.1 14. Sept.
 * 2002, added JFileChooser and FileFilter *
 *
 *
 * Constructors:
 *
 * ReadFile() this is just to instantiate it, so you can then set a Filer
 * ReadFile.setFilter(new String[]{ "xml", "XML"}); and then you can ReadFile
 * let display the filechooser: ReadFile.setFileName("?"); and finally get the
 * filecontent as text: ReadFile.getText();
 *
 *
 * ReadFile(String fileName) instantiate it and get the content as text:
 * ReadFile.getText();
 *
 *
 *
 * this tool is part of the Admin-Suite
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
 *
 *
 */
import java.io.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.util.logging.*;

public class ReadFile {

    Logger logger = Logger.getLogger("sql.fredy.datadrill");
    private String text;
    private Vector v;

    String path;

    /**
     * Get the value of path.
     *
     * @return value of path.
     */
    public String getPath() {
        return path;
    }

    /**
     * Set the the path.
     *
     * @param v Value to assign to path.
     */
    public void setPath(String v) {
        this.path = v;
    }

    String fileName;

    /**
     * Get the value of fileName.
     *
     * @return value of fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the value of fileName.
     *
     * @param v Value to assign to fileName.
     */
    public void setFileName(String v) {
        if (v != "?") {
            this.fileName = v;

            // set the path, for include
            setPath((new File(v)).getParent());
        } else {
            selectFile();
        }

    }

    public void selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("Open File");
        if (getFilter() != null) {
            chooser.setFileFilter(getFilter());
        }

        chooser.setVisible(true);
        int returnVal = chooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            setFileName(chooser.getSelectedFile().getParent()
                    + File.separator
                    + chooser.getSelectedFile().getName());
        } else {
            setFileName(null);
        }
        readIt();
    }

    MyFileFilter filter = null;

    /**
     * Get the value of filter.
     *
     * @return value of filter.
     */
    public MyFileFilter getFilter() {
        return filter;
    }

    /**
     * Set the Filefilter
     *
     * @param extensions are the desired File-extensions
     * @param description the description of the filter
     */
    public void setFilter(String f[]) {
        filter = new MyFileFilter(f);
    }

    public Vector getLines() {
        return v;
    }

    /**
     * Get the value of text.
     *
     * @return Value of text.
     */
    public String getText() {
        return text;
    }

    /**
     * Set the value of text.
     *
     * @param v Value to assign to text.
     */
    public void setText(String v) {
        this.text = v;
    }

    public ReadFile() {
    }

    public ReadFile(String fileName) {

        setFileName(fileName);
        readIt();

    }

    public ReadFile(InputStream is) {
        try {
            DataInputStream ipstr = new DataInputStream(new BufferedInputStream(is));
            BufferedReader bufrd = new BufferedReader(new InputStreamReader(ipstr));
            text = "";
            v = new Vector();
            String s;
            while ((s = bufrd.readLine()) != null) {

                // enable #include FILENAME
                // I assume that the file to include is in the same path as the caller
                if (s.trim().toLowerCase().startsWith("#include")) {
                    logger.log(Level.FINE, "Including file: " + s.substring(8).trim());
                    ReadFile rf = new ReadFile(getPath() + File.separator + s.substring(8).trim());
                    Vector include = rf.getLines();
                    for (int i = 0; i < include.size(); i++) {
                        text = text + (String) include.elementAt(i) + "\n";
                        v.addElement((String) include.elementAt(i));
                    }
                } else {
                    text = text + s + "\n";
                    v.addElement(s);
                }
            }
            ipstr.close();
        } catch (IOException exep) {
            logger.log(Level.WARNING, "IO Error for " + fileName + "|");
            logger.log(Level.INFO, "Exception: " + exep.getMessage());
        }
    }

    public void readIt() {
        if (getFileName() != null) {
            text = "";
            v = new Vector();
            String s;

            // open the file and read it in
            try {
                DataInputStream ipstr = new DataInputStream(
                        new BufferedInputStream(
                                new FileInputStream(getFileName())));

                BufferedReader bufrd = new BufferedReader(
                        new InputStreamReader(ipstr));
                while ((s = bufrd.readLine()) != null) {

                    // enable #include FILENAME
                    // I assume that the file to include is in the same path as the caller
                    if (s.trim().toLowerCase().startsWith("#include")) {
                        logger.log(Level.FINE, "Including file: " + s.substring(8).trim());
                        ReadFile rf = new ReadFile(getPath() + File.separator + s.substring(8).trim());
                        Vector include = rf.getLines();
                        for (int i = 0; i < include.size(); i++) {
                            text = text + (String) include.elementAt(i) + "\n";
                            v.addElement((String) include.elementAt(i));
                        }
                    } else {
                        text = text + s + "\n";
                        v.addElement(s);
                    }
                }
                ipstr.close();
            } catch (IOException exep) {
                logger.log(Level.WARNING, "IO Error for " + fileName + "|");
                logger.log(Level.INFO, "Exception: " + exep.getMessage());
            }
        }
    }

    public static void main(String args[]) {

        if (args.length != 1) {
            System.out.println("Syntax: java file");
            System.exit(0);
        }
        ReadFile rf = new ReadFile(args[0]);
        System.out.println(rf.getText());
    }
}
