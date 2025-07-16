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
package sql.fredy.sqltools;

/**
 *
 * @author sql@hulmen.ch
 */
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SqlMonitorSplash extends Window implements Runnable {

    private Logger logger = Logger.getLogger("sql.fredy.sqltools");
    public JLabel message;
    private JLabel l1, l2;

    public SqlMonitorSplash() {
        super(new Frame());
        JPanel panel = new JPanel();
        l1 = new JLabel("SQL Monitor");
        l1.setFont(l1.getFont().deriveFont(36.0f));

        l2 = new JLabel(" ");
        l2.setFont(l2.getFont().deriveFont(24.0f));

        panel.setLayout(new BorderLayout());

        EtchedBorder bb = new EtchedBorder();

        panel.setBorder(bb);
        panel.add(BorderLayout.NORTH, l1);
        panel.add(BorderLayout.CENTER, l2);
        message = new JLabel("");
        message.setFont(message.getFont().deriveFont(14.0f));

        panel.add(BorderLayout.SOUTH, message);

        panel.setBackground(Color.CYAN);
        panel.setForeground(Color.YELLOW);
        add(panel);

        pack();
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        
        Dimension WindowSize = getSize();                
        setBounds((width - WindowSize.width) / 2,
                 (height -  WindowSize.height) / 2, WindowSize.width,
                 WindowSize.height);
        Thread t = new Thread(this, "SQL");
        t.start();
    }

    public void setMessage(String m) {
        message.setText(m);
        //waitAMoment(100);
        message.updateUI();;
        try {
            this.update(this.getGraphics());
        } catch (Exception e) {
            logger.log(Level.WARNING, "Graphic Exception: " + e.getMessage());
        }
    }

    public void close() {
        this.setVisible(false);
        defaultCursor();
        this.dispose();
    }

    private void defaultCursor() {
        this.setCursor(Cursor.getDefaultCursor());
        waitAMoment(100);
    }

    private void busyCursor() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        waitAMoment(100);
    }

    private void waitAMoment(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }

    @Override
    public void run() {
        busyCursor();
        setVisible(true);
    }

}
