package sql.fredy.generate;

/**
 * This is a graphical userinterface to help you to use the CodeGeneration
 * classes from the Admin Framework
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
 */
import sql.fredy.ui.CloseableFrame;
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.JTextFieldFilter;
import sql.fredy.io.MyFileFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;

public class GenerateTool extends JPanel {

    private JTextField askUser, askHost, askDatabase, askSchema;
    public JTextField askTable, askDirectory, askXMLFile;
    private JPasswordField askPassword;
    private JLabel statusLine;

    public ImageButton cancel;

    /**
     * componentsPerLine : how many components on the same Line default: 2
     * insets default 5,5,5,5 maxCols : max number of columns in a JTextField
     * before turning into a JTextArea and number of columns in a JTextArea
     * default=25 maxRows: max. Number of Rows a JTextArea has default= 5
     *
     */
    private JTextField componentsPerLine, inset, maxCols, maxRows, noCols;

    String database = "aDataBase";

    /**
     * Get the value of database.
     *
     * @return value of database.
     */
    public String getDatabase() {
        return askDatabase.getText();
    }

    /**
     * Set the value of database.
     *
     * @param v Value to assign to database.
     */
    public void setDatabase(String v) {
        this.database = v;
        askDatabase.setText(v);
    }

    String schema = "%";

    /**
     * Get the value of schema.
     *
     * @return value of schema.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Set the value of schema.
     *
     * @param v Value to assign to schema.
     */
    public void setSchema(String v) {
        this.schema = v;
        askSchema.setText(v);

    }

    String table = "%";

    /**
     * Get the value of table.
     *
     * @return value of table.
     */
    public String getTable() {
        return table;
    }

    /**
     * Set the value of table.
     *
     * @param v Value to assign to table.
     */
    public void setTable(String v) {
        this.table = v;
        askTable.setText(v);
    }

    private void initGUI() {
        askDatabase = new JTextField(15);
        askSchema = new JTextField(15);
        askTable = new JTextField(15);
        askHost = new JTextField(15);
        askUser = new JTextField(15);
        askPassword = new JPasswordField(15);
        askXMLFile = new JTextField(40);

    }

    private JPanel userPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(5, 5, 5, 5);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = insets;

        panel.setBorder(BorderFactory.createEtchedBorder());

        return panel;

    }

    private JPanel dbPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(5, 5, 5, 5);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = insets;

        askDatabase.setText(getDatabase());
        askSchema.setText(getSchema());
        askTable.setText(getTable());

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Database"), gbc);
        gbc.gridx = 1;
        panel.add(askDatabase, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Schema"), gbc);
        gbc.gridx = 1;
        panel.add(askSchema, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Table"), gbc);
        gbc.gridx = 1;
        panel.add(askTable, gbc);

        panel.setBorder(BorderFactory.createEtchedBorder());

        return panel;
    }

    private JPanel directoryPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        askDirectory = new JTextField(40);
        askDirectory.setText(System.getProperty("user.home"));
        askDirectory.setToolTipText("here is where it starts to save [this].applications.[database] ");

        panel.add(askDirectory);

        ImageButton folder = new ImageButton(null, "folderin.gif", "top level folder of the applications");
        folder.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectDirectory();
            }
        });

        panel.add(folder);

        panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Directory for Java-Code"));

        return panel;
    }

    /**
     * Some RDBMS use true Filenames as the DB-Name this behaviour confuses the
     * Generator, as it is based onto files as well.
     *
     * To bypass this, this class uses the FileName independently of the if the
     * DB_name is a File
     *
     *
     */
    public String dbNameVerifier(String v) {

        File f = new File(v);
        if (f.exists()) {
            if (f.isDirectory()) {
                v = f.getName() + "_dbdesc";
            }
            if (f.isFile()) {
                v = f.getName() + "_dbdesc";
            }
        }

        return v;
    }

    private JPanel XMLFile() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "XML-File"));

        askXMLFile.setText(System.getProperty("user.home")
                + java.io.File.separator
                + dbNameVerifier(askDatabase.getText().toLowerCase())
                + ".xml");

        askXMLFile.setToolTipText("this is the XML-file generated out of the DB to generate the code");

        ImageButton select = new ImageButton(null, "document.gif", "select the XML-file");
        select.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });

        //panel.add(new JLabel("XML-File"));
        panel.add(askXMLFile);
        panel.add(select);

        return panel;

    }

    private JPanel buttonPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        ImageButton generateForms = new ImageButton(null, "frames.gif", "generate Code");
        ImageButton generateXML = new ImageButton(null, "newxml.gif", "generate XML");
        cancel = new ImageButton(null, "exit.gif", "quit");

        ImageButton editForms = new ImageButton(null, "magnify.gif", "Form preview ");
        ImageButton editXML = new ImageButton(null, "pagesetup.gif", "edit XML-file");

        
        editForms.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final FormInterpreter fi = new FormInterpreter(askXMLFile.getText());
                fi.addWindowListener(new WindowAdapter() {
                    public void windowActivated(WindowEvent e) {
                    }

                    public void windowClosed(WindowEvent e) {
                    }

                    public void windowClosing(WindowEvent e) {
                        fi.dispose();
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
            }
        });

        editXML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                XMLEditor xed = new XMLEditor(askXMLFile.getText());

                final CloseableFrame cf = new CloseableFrame("Editor");
                cf.setExitOnClose(false);
                cf.getContentPane().setLayout(new BorderLayout());
                cf.getContentPane().add(xed, BorderLayout.CENTER);
                cf.addWindowListener(new WindowAdapter() {
                    public void windowActivated(WindowEvent e) {
                    }

                    public void windowClosed(WindowEvent e) {
                    }

                    public void windowClosing(WindowEvent e) {
                        cf.dispose();
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
                cf.pack();
                cf.setVisible(true);
                xed.exit.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cf.dispose();
                    }
                });
            }
        });

        ImageButton defaults = new ImageButton(null, "default.gif", "set defaults");
        defaults.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setDefaultValues();
            }
        });

        generateXML.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                genXML();
            }
        });

        generateForms.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                genForms();
            }
        });
     

        GridBagConstraints gbc;
        Insets insets = new Insets(5, 5, 5, 5);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;

        panel.add(generateXML, gbc);

        gbc.gridx = 1;
        panel.add(generateForms, gbc);

        gbc.gridx = 3;
        panel.add(defaults, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(editXML, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(editForms, gbc);

        gbc.gridx = 2; 

        gbc.gridx = 3;
        gbc.gridy = 1;
        panel.add(cancel, gbc);

        panel.setBorder(BorderFactory.createEtchedBorder());

        return panel;

    }

    private JPanel parameterPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        componentsPerLine = new JTextField(3);
        inset = new JTextField(8);
        maxCols = new JTextField(4);
        maxRows = new JTextField(4);
        noCols = new JTextField(4);

        componentsPerLine.setFont(new Font("Monospaced", Font.PLAIN, 12));
        inset.setFont(new Font("Monospaced", Font.PLAIN, 12));
        maxCols.setFont(new Font("Monospaced", Font.PLAIN, 12));
        maxRows.setFont(new Font("Monospaced", Font.PLAIN, 12));
        noCols.setFont(new Font("Monospaced", Font.PLAIN, 12));

        componentsPerLine.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));
        inset.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC + ","));
        maxCols.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));
        maxRows.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));
        noCols.setDocument(new JTextFieldFilter(JTextFieldFilter.NUMERIC));

        componentsPerLine.setToolTipText("how many components on one gridline in the form");
        inset.setToolTipText("insets as they are in java.awt.insets");
        maxCols.setToolTipText("max. number of cols in a JTextField before turnin into a JTextArea");
        maxRows.setToolTipText("max. number of rows in a JTextArea");
        noCols.setToolTipText("max. number of columns a JTextArea has");

        setDefaultValues();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Components per line"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(componentsPerLine, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Insets"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(inset, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("max. Columns"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(maxCols, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("max. Rows"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(maxRows, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("max. Cols"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(noCols, gbc);
        return panel;

    }

    private JPanel statusPanel() {
        JPanel panel = new JPanel();
        statusLine = new JLabel();
        statusLine.setText("Fredy's GenerateTool is part of admin and free software (MIT-License)");
        statusLine.setFont(new Font("Monospaced", Font.PLAIN, 10));
        statusLine.setBackground(Color.yellow);
        statusLine.setForeground(Color.blue);
        panel.setBackground(Color.yellow);
        panel.setForeground(Color.blue);
        panel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        panel.add(statusLine);
        return panel;

    }

    private void setDefaultValues() {
        componentsPerLine.setText("2");
        inset.setText("2,2,2,2");
        maxCols.setText("50");
        maxRows.setText("05");
        noCols.setText("50");

        askDirectory.setText(System.getProperty("user.home"));
        askDatabase.setText(getDatabase());
        askSchema.setText(getSchema());
        askTable.setText(getTable());

        askXMLFile.setText(System.getProperty("user.home")
                + java.io.File.separator
                + askDatabase.getText().toLowerCase()
                + ".xml");

    }

    private void selectDirectory() {

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select Directory");

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            askDirectory.setText(chooser.getCurrentDirectory() + java.io.File.separator + chooser.getSelectedFile().getName());
        }

    }

    private void selectFile() {

        JFileChooser chooser = new JFileChooser();
        //chooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
        chooser.setDialogTitle("Select XML file");

        MyFileFilter filter = new MyFileFilter();
        filter.addExtension("xml");
        filter.setDescription("XML-files");
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            askXMLFile.setText(chooser.getCurrentDirectory() + java.io.File.separator + chooser.getSelectedFile().getName());
        }

    }

    public GenerateTool() {
        initGUI();
        init();
    }

    private void init() {

        this.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(userPanel(), gbc);

        gbc.gridx = 1;
        gbc.gridheight = 1;
        this.add(dbPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        gbc.gridwidth = 2;
        this.add(XMLFile(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 2;
        gbc.gridwidth = 2;
        this.add(directoryPanel(), gbc);

        gbc.gridheight = 2;
        gbc.gridy = 1;
        gbc.gridx = 2;
        gbc.gridwidth = 1;
        this.add(buttonPanel(), gbc);

        gbc.gridy = 0;
        gbc.gridx = 2;
        gbc.gridheight = 1;
        this.add(parameterPanel(), gbc);

        gbc.gridy = 5;
        gbc.gridx = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 3;
        this.add(statusPanel(), gbc);

    }

    private void genXML() {
        GenerateXML gmx = null;

        gmx = new GenerateXML(askTable.getText(), askSchema.getText(), askXMLFile.getText());

        // set Values
        gmx.setINSETS(inset.getText());
        gmx.setNumberOfComponentsPerLine(Integer.parseInt(componentsPerLine.getText()));
        gmx.setMaxCols(Integer.parseInt(maxCols.getText()));
        gmx.setNoRows(Integer.parseInt(maxRows.getText()));
        gmx.setNoCols(Integer.parseInt(noCols.getText()));

        gmx.init();
    }

    private void genForms() {
        this.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        GenerateForm gf = new GenerateForm(askSchema.getText(), askXMLFile.getText(), askDirectory.getText());

        this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    }

    public static void main(String args[]) {

        CloseableFrame frame = new CloseableFrame("Generator Tool");
        GenerateTool gt = new GenerateTool();
        frame.getContentPane().add(gt);
        gt.cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);

    }

}
