package sql.fredy.ui;

/**
 * This is a GUI around the XMLImport-Tool.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
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
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.border.*;

/**
 *
 * @author sql@hulmen.ch
 */
public class XMLImportGUI extends JPanel {

    public ImageButton cancel;
    public JCheckBox extendedSettings;
    private JTextField controlFileName;
    private JTextField dataFileName;
    private JTextField jdbcDriver;
    private JTextField connectionURL;
    private JLabel infoBar;
    private String controlFile;
    private String dataFile;

    private Connection con = null;

    public XMLImportGUI() {
        initGui();
    }

    public XMLImportGUI(java.sql.Connection c) {
        setCon(c);
        initGui();
    }

    private void initGui() {
        this.setLayout(new BorderLayout());

        this.add(BorderLayout.CENTER, centerPanel());
        this.add(BorderLayout.SOUTH, lowerPanel());
        this.add(BorderLayout.NORTH, infoPanel());
    }

    private JPanel centerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(filePanel(), gbc);

        // if there is no existing connection then display the values for the JDBC-Stuff
        boolean addCon = false;

        if (getCon() != null) {
            addCon = false;
        }

        if (addCon) {
            gbc.gridy = 1;
            panel.add(jdbcSetting(), gbc);

        }
        return panel;
    }

    private JPanel filePanel() {

        controlFileName = new JTextField(30);
        dataFileName = new JTextField(30);

        JPanel panel = new JPanel();

        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("XML Definition File"), gbc);

        gbc.gridx = 1;
        panel.add(controlFileName, gbc);

        gbc.gridx = 2;
        JButton filer1 = new JButton("...");
        panel.add(filer1, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("XML Data File"), gbc);

        gbc.gridx = 1;
        panel.add(dataFileName, gbc);

        gbc.gridx = 2;
        JButton filer2 = new JButton("...");
        panel.add(filer2, gbc);

        filer1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                chooser.setDialogTitle("Select control File");
                chooser.setFileFilter(new sql.fredy.io.MyFileFilter("xml"));

                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //setFile(chooser.getSelectedFile());
                    controlFileName.setText(chooser.getCurrentDirectory()
                            + java.io.File.separator
                            + chooser.getSelectedFile().getName());
                }
            }
        });
        filer2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogType(JFileChooser.OPEN_DIALOG);
                chooser.setDialogTitle("Select data File");
                chooser.setFileFilter(new sql.fredy.io.MyFileFilter("xml"));

                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    //setFile(chooser.getSelectedFile());
                    dataFileName.setText(chooser.getCurrentDirectory()
                            + java.io.File.separator
                            + chooser.getSelectedFile().getName());
                }
            }
        });

        panel.setBorder(new TitledBorder("File Settings"));
        return panel;
    }

    private JPanel jdbcSetting() {
        JPanel panel = new JPanel();
        jdbcDriver = new JTextField(30);
        connectionURL = new JTextField(30);

        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        extendedSettings = new JCheckBox("Use JDBC Driver from control file");
        panel.add(extendedSettings, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("JDBC Driver"), gbc);

        gbc.gridx = 1;
        panel.add(jdbcDriver, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("JDBC Connection URL"), gbc);

        gbc.gridx = 1;
        panel.add(connectionURL, gbc);

        panel.setBorder(new TitledBorder("JDBC Settings"));
        return panel;
    }

    private JPanel infoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        infoBar = new JLabel("Fredy's XML Import");
        panel.add(infoBar);
        return panel;

    }

    private JPanel lowerPanel() {
        // this is the Panel hosting the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());

        ImageButton load = new ImageButton(null, "createdb.gif", "Load data from file");
        cancel = new ImageButton(null, "exit.gif", "Cancel");

        panel.add(load);
        panel.add(cancel);

        panel.setBorder(new EtchedBorder());

        load.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                infoBar.setText(startImport() + " Rows imported");
            }
        });

        ImageButton help = new ImageButton(null, "help.gif", "how it works");
        help.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                howTo();
            }
        });

        panel.add(help);

        return panel;
    }

    private int startImport() {

        sql.fredy.sqltools.XMLImport xmlimport = new sql.fredy.sqltools.XMLImport(controlFileName.getText(), dataFileName.getText());
        return xmlimport.getNoOfRowsInserted();

        /*
        sql.fredy.sqltools.XMLImport xmlimport = new sql.fredy.sqltools.XMLImport();

        if (!extendedSettings.isSelected()) {
            xmlimport.setJdbcDriver(jdbcDriver.getText());
            xmlimport.setConnectionURL(connectionURL.getText());
        }
        xmlimport.doImport(controlFileName.getText(), dataFileName.getText());

        return xmlimport.getNoOfRowsInserted();
         */
    }

    private void howTo() {
        JDialog dialog = new JDialog();
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.setTitle("XMLImport How To");

        JTextPane info = new JTextPane();
        info.setText((new sql.fredy.sqltools.XMLImport()).getInfo());
        info.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(info);
        dialog.getContentPane().add(BorderLayout.CENTER, scrollPane);

        dialog.setModal(true);
        dialog.setPreferredSize(new Dimension(500, 250));
        dialog.pack();
        dialog.setVisible(true);

    }

    public static void main(String args[]) {
        JFrame frame = new JFrame("XML Import GUI");
        frame.getContentPane().setLayout(new BorderLayout());
        XMLImportGUI xgui = new XMLImportGUI();
        frame.add(BorderLayout.CENTER, xgui);

        frame.addWindowListener(new WindowAdapter() {

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

        xgui.cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        frame.pack();
        frame.setVisible(true);

    }

    /**
     * @return the controlFile
     */
    public String getControlFile() {
        return controlFile;
    }

    /**
     * @param controlFile the controlFile to set
     */
    public void setControlFile(String controlFile) {
        this.controlFile = controlFile;
    }

    /**
     * @return the dataFile
     */
    public String getDataFile() {
        return dataFile;
    }

    /**
     * @param dataFile the dataFile to set
     */
    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }

    /**
     * @return the con
     */
    public Connection getCon() {
        return con;
    }

    /**
     * @param con the con to set
     */
    public void setCon(Connection con) {
        this.con = con;
    }
}
