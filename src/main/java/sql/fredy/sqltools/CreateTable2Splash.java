/*
 * CreateTable2Splash.java
 *
 * Created on October 7, 2004, 8:47 AM
 *
 * This software is part of the Admin-Framework and free software (MIT-License)
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 *
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
package sql.fredy.sqltools;

import sql.fredy.ui.ImageButton;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 *
 * @author sql@hulmen.ch
 */
public class CreateTable2Splash extends Window implements Runnable {

    public JLabel message;
    private JLabel l1, l2;
    private ImageButton text ;

    /**
     * Creates a new instance of CreateTable2Splash
     */
    public CreateTable2Splash() {
        super(new Frame());

        JPanel panel = new JPanel();
        l1 = new JLabel("Create Table");
        l1.setFont(l1.getFont().deriveFont(36.0f));

        text = new ImageButton("Create Table","datastore.gif",null);
        text.setFont(l1.getFont().deriveFont(36.0f));
        text.setBackground(Color.LIGHT_GRAY);
        text.setForeground(Color.BLACK);
        text.setVerticalAlignment(JLabel.CENTER);
        l2 = new JLabel("fetching metadata");
        l2.setVerticalAlignment(JLabel.CENTER);
        l2.setFont(l2.getFont().deriveFont(24.0f));

        panel.setLayout(new BorderLayout());

        Border bb = BorderFactory.createLineBorder(Color.GRAY,5);
 

        panel.setBorder(bb);
        panel.add(BorderLayout.NORTH, text);
        panel.add(BorderLayout.CENTER, l2);
        message = new JLabel("patience please...");
        message.setVerticalAlignment(JLabel.CENTER);
        message.setFont(message.getFont().deriveFont(14.0f));

        panel.add(BorderLayout.SOUTH, message);

        panel.setBackground(Color.LIGHT_GRAY);
        panel.setForeground(Color.BLACK);
        add(panel);
        this.update(this.getGraphics());

        pack();
        Dimension WindowSize = getSize(),
                ScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((ScreenSize.width - WindowSize.width) / 2,
                (ScreenSize.height - WindowSize.height) / 2, WindowSize.width,
                WindowSize.height);
        Thread t = new Thread(this, "SQLCREATE");
        t.start();
    }

    public void close() {
        setVisible(false);
        dispose();
    }

    private void defaultCursor() {
        this.setCursor(Cursor.getDefaultCursor());
        waitAMoment(100);
    }

    private void busyCursor() {        
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        waitAMoment(100);
    }

    public void waitAMoment(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    public void setMessage(String m) {
        message.setText(m);
        waitAMoment(100);
        message.updateUI();;
        this.update(this.getGraphics());
    }

    @Override
    public void run() {
        busyCursor();
        setVisible(true);
        this.update(this.getGraphics());
    }

    public static void main(String a[]) {
        CreateTable2Splash c = new CreateTable2Splash();
        c.setMessage("1......");
        c.waitAMoment(1000);
        c.setMessage("2......");
        c.waitAMoment(1000);
        c.setMessage("3......");
        c.waitAMoment(1000);
        c.setMessage("4......");
        c.waitAMoment(1000);
        c.setMessage("5......");
        c.waitAMoment(1000);
        c.setMessage("6......");
        c.waitAMoment(1000);
        c.setMessage("8......");
        c.waitAMoment(1000);
        c.setMessage("9......");
        c.waitAMoment(1000);
        c.setMessage("... bye");
        c.waitAMoment(1000);
        c.close();
        System.exit(0);
    }

}
