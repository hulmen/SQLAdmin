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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIDefaults;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 *
 * @author fredy
 */
public class RunningJob implements Runnable {

    private RSyntaxTextArea runningQuery;
    private JDialog runningJob;
    private String query;
    private Statement stmt = null;
    private Logger logger = Logger.getLogger("sql.fredy.ui");

    public RunningJob(String query) {
        setQuery(query);
    }

    @Override
    public void run() {
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
        JPanel stopPanel = new JPanel();
        stopPanel.setLayout(new FlowLayout());
        ImageButton stopButton = new ImageButton(null, "stop.gif", "Cancel selected query");
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (getStmt() != null) {
                    try {
                        stmt.cancel();
                        runningJob.setVisible(false);
                    } catch (SQLException ex) {
                        logger.log(Level.SEVERE, "SQL Exception, can not cancel job{0}", ex.getMessage());
                    }
                }
            }
        });
        stopPanel.add(stopButton);
        runningJob.getContentPane().add(stopPanel, BorderLayout.SOUTH);
        runningJob.pack();
        runningJob.setVisible(true);
    }

    public void setVisible(boolean b) {
        runningJob.setVisible(b);
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

}
