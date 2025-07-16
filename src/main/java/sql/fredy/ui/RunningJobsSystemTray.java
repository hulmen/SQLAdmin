/*
 * The MIT License
 *
 * Copyright 2019 fredy.
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
 */
package sql.fredy.ui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.CheckboxMenuItem;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MenuItem;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.UIDefaults;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 *
 * @author fredy
 */
public class RunningJobsSystemTray implements Runnable {

    private String query;
    private Logger logger = Logger.getLogger("sql.fredy.ui");
    private RSyntaxTextArea runningQuery;
    private JDialog runningJob, timerDialog;
    private Statement stmt = null;

    public RunningJobsSystemTray() {

        if (!SystemTray.isSupported()) {
            logger.log(Level.WARNING, "SystemTray is not supported");
        }

    }

    public RunningJobsSystemTray(String query) {

        if (!SystemTray.isSupported()) {
            logger.log(Level.WARNING, "SystemTray is not supported");
        }
        setQuery(query);
    }

    private SystemTray tray;
    private TrayIcon trayIcon;

    @Override
    public void run() {
        Calendar startTime = Calendar.getInstance();
        LoadImage loadImage = new LoadImage();
        final PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(loadImage.getImage("dataextract.gif").getImage());
        trayIcon.setImageAutoSize(true);
        setTray(SystemTray.getSystemTray());

        MenuItem aboutItem = new MenuItem("SQL Admin: long running query");
        MenuItem timerItem = new MenuItem("Time past by...");
        MenuItem queryItem = new MenuItem("Display Query");
        MenuItem cancelItem = new MenuItem("Cancel Query");

        queryItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayQuery();
            }
        });

        timerItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeElapsed(startTime);
            }
        });
        cancelItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getStmt() != null) {
                    try {
                        stmt.cancel();
                        getTray().remove(trayIcon);
                    } catch (SQLException ex) {
                        logger.log(Level.SEVERE, "SQL Exception, can not cancel job{0}", ex.getMessage());
                    }
                }
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Fredy's SQL Admin Tool, www.hulmen.ch");
            }
        });

        //Add components to pop-up menu
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(timerItem);
        popup.add(queryItem);
        popup.add(cancelItem);

        trayIcon.setPopupMenu(popup);

        try {
            getTray().add(trayIcon);
        } catch (AWTException e) {
            logger.log(Level.WARNING, "TrayIcon could not be added.");
        }

    }

    public void remove() {
        try {
            if (runningJob != null) {
                runningJob.setVisible(false);
            }
            if (timerDialog != null) {
                timerDialog.setVisible(false);
            }

            getTray().remove(trayIcon);
        } catch (Exception e) {
            logger.log(Level.FINE, "Exception while remove: {0}", e.getMessage());
        }
    }

    /**
     * @return the query
     */
    public String getQuery() {
        return query;
    }

    /**
     * @param query the query to set
     */
    public void setQuery(String query) {
        this.query = query;
    }

    private void displayQuery() {
        UIDefaults defaults = javax.swing.UIManager.getDefaults();
        setRunningQuery(new RSyntaxTextArea(10, 40));
        getRunningQuery().setText(getQuery());
        getRunningQuery().setForeground(defaults.getColor("TextArea.foreground"));
        getRunningQuery().setBackground(defaults.getColor("TextArea.background"));
        getRunningQuery().setFont(new Font("Monospaced", Font.PLAIN, 13));
        getRunningQuery().setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);

        getRunningQuery().setEditable(true);

        runningJob = new JDialog();
        int lng = getQuery().length();
        if (lng > 30) {
            runningJob.setTitle(getQuery().substring(0, 30) + "...");
        } else {
            runningJob.setTitle(getQuery());
        }
        runningJob.getContentPane().setLayout(new BorderLayout());
        runningJob.getContentPane().add(new JScrollPane(getRunningQuery()), BorderLayout.CENTER);
        runningJob.pack();
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point punkt = new Point(pointerInfo.getLocation().x - runningJob.getWidth(), pointerInfo.getLocation().y - runningJob.getHeight());

        runningJob.setLocation(pointerInfo.getLocation().x - runningJob.getWidth(), pointerInfo.getLocation().y - runningJob.getHeight());
        runningJob.setVisible(true);
    }

    private void timeElapsed(Calendar startTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdt = new SimpleDateFormat("HH:mm:ss");
        timerDialog = new JDialog();
        timerDialog.setTitle("Query Time elapsed");
        JLabel startLabel = new JLabel("started at:" + sdf.format(startTime.getTime()));
        JLabel timerLabel = new JLabel("..");
        timerDialog.getContentPane().setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        Insets insets = new Insets(1, 1, 1, 1);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        timerDialog.getContentPane().add(startLabel, gbc);
        gbc.gridy = 1;
        timerDialog.getContentPane().add(timerLabel, gbc);
        timerDialog.pack();
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        timerDialog.setLocation(pointerInfo.getLocation().x - timerDialog.getWidth(), pointerInfo.getLocation().y - timerDialog.getHeight());
        timerDialog.setVisible(true);

        Calendar now = Calendar.getInstance();
        long difference = (now.getTimeInMillis() - startTime.getTimeInMillis()) / 1000;

        now.set(Calendar.HOUR_OF_DAY, (int) difference / 3600);
        now.set(Calendar.MINUTE, (int) (difference / 60));
        now.set(Calendar.SECOND, (int) difference);

        Thread timer = new Thread() {
            public void run() {
                while (true) {
                    //now.add(Calendar.SECOND, 1);
                    long difference = (now.getTimeInMillis() - startTime.getTimeInMillis()) / 1000;
                    now.set(Calendar.HOUR_OF_DAY, (int) difference / 3600);
                    now.set(Calendar.MINUTE, (int) (difference / 60));
                    now.set(Calendar.SECOND, (int) difference);
                    
                    timerLabel.setText(sdt.format(now.getTime()));
                    timerLabel.updateUI();
                    timerDialog.update(timerDialog.getGraphics());
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RunningJobsSystemTray.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                }
            }
        };
        timer.start();
    }

    /**
     * @return the runningQuery
     */
    public RSyntaxTextArea getRunningQuery() {
        return runningQuery;
    }

    /**
     * @param runningQuery the runningQuery to set
     */
    public void setRunningQuery(RSyntaxTextArea runningQuery) {
        this.runningQuery = runningQuery;
    }

    /**
     * @return the stmt
     */
    public Statement getStmt() {
        return stmt;
    }

    /**
     * @param stmt the stmt to set
     */
    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    /**
     * @return the tray
     */
    public SystemTray getTray() {
        return tray;
    }

    /**
     * @param tray the tray to set
     */
    public void setTray(SystemTray tray) {
        this.tray = tray;
    }

}
