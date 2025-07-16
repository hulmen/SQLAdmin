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
 *
 */
package sql.fredy.ui;

import java.awt.BorderLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author sql@hulmen.ch
 */
public class Transpose extends JFrame {

    private static int maxLength = 50;

    public Transpose(ArrayList<ArrayList> bunch) {

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

        // we set all the Content things....
        this.setTitle("Transpose");

        this.getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.insets = insets;

        // for the labels;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        int y = 0;
        int x = 0;

        Iterator iter = bunch.iterator();
        while (iter.hasNext()) {

            ArrayList<TransposeRow> rows = (ArrayList) iter.next();

            Iterator iterRow = rows.iterator();
            y = 0;
            while (iterRow.hasNext()) {

                TransposeRow tr = (TransposeRow) iterRow.next();

                if (x == 0) {
                    //JLabel label = new JLabel(tr.getName());
                    JLabel label = new JLabel(tr.getName());
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.fill = GridBagConstraints.NONE;
                    gbc.weightx = 1.0;
                    gbc.weighty = 1.0;
                    gbc.gridx = x;
                    gbc.gridy = y;
                    panel.add(label, gbc);
                }

                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.NONE;
                gbc.weightx = 1.0;
                gbc.weighty = 1.0;
                gbc.gridx = x + 1;
                gbc.gridy = y;

                if (tr.getLength() > maxLength) {
                    JTextArea textarea;
                    try {
                        textarea = new JTextArea(tr.getContent(), 10, maxLength);
                        //textarea.setLineWrap(true);
                        //textarea.setWrapStyleWord(true);                      
                    } catch (Exception e) {
                        textarea = new JTextArea("null", 1, 4);
                    }
                    textarea.setEditable(false);
                    panel.add(new JScrollPane(textarea), gbc);
                } else {
                    JTextField textfield;
                    try {
                        textfield = new JTextField(tr.getContent(), tr.getLength());
                    } catch (Exception e) {
                        textfield = new JTextField("null", 4);
                    }
                    textfield.setEditable(false);
                    panel.add(textfield, gbc);
                }
                y++;
            }
            x++;
        }
        this.getContentPane().add(BorderLayout.CENTER, new JScrollPane(panel));
        this.pack();

        Point p = MouseInfo.getPointerInfo().getLocation();
        this.setLocation(p);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        if (width < this.getWidth()) {
            width = width - p.x;
        } else {
            width = this.getWidth();
        }
        if (height - p.y < this.getHeight()) {
            height = height - p.y;
        } else {
            height = this.getHeight();
        }

        this.setSize(width, height);

        this.setVisible(true);
    }

    private void close() {
        this.setVisible(false);
        this.dispose();
    }

}
