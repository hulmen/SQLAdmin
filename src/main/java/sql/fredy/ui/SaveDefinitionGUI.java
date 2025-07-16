
/*
 * The MIT License
 *
 * Copyright 2022 fredy.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.

This class is a GUI to launch SaveDefinition to export the definitions of Views, Tables and Procedures 
into a File

Fredy Fischer, 2023-03-30


 */
package sql.fredy.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import sql.fredy.io.MyFileFilter;
import sql.fredy.metadata.DbInfo;
import sql.fredy.sqltools.SaveDefinition;

/**
 *
 * @author a_tkfir
 */
public class SaveDefinitionGUI extends JPanel {

    private JComboBox schema;
    private JComboBox entityType;
    private JCheckBox deleteFile;
    private String fileName = null;
    private JLabel fileNameLabel;
    public JMenuItem exit;
    public ImageButton exitButton;

    public SaveDefinitionGUI() {
        exit = new JMenuItem("Exit");
        exitButton = new ImageButton(null, "exit.gif", "close");
        
        this.setLayout(new BorderLayout());
        this.add(centerPanel(), BorderLayout.CENTER);
        this.add(buttonPanel(), BorderLayout.SOUTH);
    }

    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        
        ImageButton loadButton = new ImageButton(null, "documentin.gif", "export to file");

        loadButton.addActionListener((ActionEvent e) -> {
            doExport();
        });

        JPanel innerPanel = new JPanel();
        innerPanel.setLayout(new FlowLayout());

        innerPanel.add(exitButton);
        innerPanel.add(loadButton);

        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new FlowLayout());
        fileNameLabel = new JLabel((String) fileName == null ? "..." : getFileName());
        lowerPanel.add(fileNameLabel);

        panel.add(innerPanel, BorderLayout.CENTER);
        panel.add(lowerPanel, BorderLayout.SOUTH);
        panel.setBorder(new EtchedBorder());
        return panel;

    }

    private void doExport() {

        busyCursor();
        
        // first we check the file
        File file = new File(getFileName());
        if ((file.exists()) && (!deleteFile.isSelected())) {
            JOptionPane.showMessageDialog(this, "File exists and I'm not allowed to delete it");
            defaultCursor();
            return;
        }

        SaveDefinition sde = new SaveDefinition(fileName, (String) schema.getSelectedItem(), (String) entityType.getSelectedItem(), deleteFile.isSelected());
        JOptionPane.showMessageDialog(this, "Export finished");

        defaultCursor();
    }

    private JPanel centerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        DbInfo dbInfo = new DbInfo();
        schema = new JComboBox(dbInfo.getSchemas());

        //entityType = new JComboBox(new String[]{"View", "Tables", "Functions"})
        entityType = new JComboBox(new String[]{"View"});

        deleteFile = new JCheckBox();
        deleteFile.setSelected(true);

        ImageButton selectFile = new ImageButton(null, "documentdraw.gif", "click to select file");
        selectFile.addActionListener((ActionEvent e) -> {
            setFileName(null);

            MyFileFilter filter = new MyFileFilter(new String[]{"sql", "csv"}, "SQL or Text files");
            JFileChooser chooser = new JFileChooser();
            chooser.setFileFilter(filter);
            chooser.setDialogType(JFileChooser.OPEN_DIALOG);
            chooser.setDialogTitle("Select Export File");

            int returnVal = chooser.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                setFileName(chooser.getSelectedFile().getPath());
            }
            fileNameLabel.setText(getFileName());
            fileNameLabel.updateUI();
        });

        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Schema"), gbc);

        gbc.gridy = 1;
        panel.add(new JLabel("Entity Type"), gbc);

        gbc.gridy = 2;
        panel.add(new JLabel("Delete File if exists"), gbc);

        gbc.gridy = 3;
        panel.add(new JLabel("File"), gbc);

        gbc.gridx = 1;

        gbc.gridy = 0;
        panel.add(schema, gbc);

        gbc.gridy = 1;
        panel.add(entityType, gbc);

        gbc.gridy = 2;
        panel.add(deleteFile, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(selectFile, gbc);

        return panel;
    }

    public JMenuBar menubar() {
        JMenuBar menubar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem export = new JMenuItem("Export");
        

        fileMenu.add(export);
        fileMenu.add(new JSeparator());
        fileMenu.add(exit);
        menubar.add(fileMenu);

        export.addActionListener((ActionEvent e) -> {
            doExport();
        });

        return menubar;
    }

    private void defaultCursor() {
        this.setCursor(Cursor.getDefaultCursor());
        waitMillis(200);
    }

    private void busyCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        waitMillis(200);
    }

    private void waitMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    public static void main(String[] args) {

        JFrame frame = new JFrame("Save Definition");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        SaveDefinitionGUI sd = new SaveDefinitionGUI();
        frame.setJMenuBar(sd.menubar());
        frame.getContentPane().add(sd, BorderLayout.CENTER);
        sd.exit.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        sd.exitButton.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
