/**
 * MemoryCounter is a part of Admin it displays the available Memory (guessed)
 *
 * Collection of all metadata might take long and might consume a lot of memory
 * when used with professional highend RDBMS. But it's worth to see...
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
 *
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
package sql.fredy.tools;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.lang.Thread.sleep;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sql.fredy.ui.Gauge;

/**
 *
 * @author tkfir
 */
public class MemoryCounter extends JPanel implements Runnable {

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * @param running the running to set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * @return the noCPUs
     */
    public int getNoCPUs() {
        return noCPUs;
    }

    /**
     * @param noCPUs the noCPUs to set
     */
    public void setNoCPUs(int noCPUs) {
        this.noCPUs = noCPUs;
    }

    /**
     * @return the availableMemory
     */
    public long getAvailableMemory() {
        return availableMemory;
    }

    /**
     * @param availableMemory the availableMemory to set
     */
    public void setAvailableMemory(long availableMemory) {
        this.availableMemory = availableMemory;
    }

    /**
     * @return the maxMemory
     */
    public long getMaxMemory() {
        return maxMemory;
    }

    /**
     * @param maxMemory the maxMemory to set
     */
    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    /**
     * @return the memoryInUse
     */
    public long getMemoryInUse() {
        return memoryInUse;
    }

    /**
     * @param memoryInUse the memoryInUse to set
     */
    public void setMemoryInUse(long memoryInUse) {
        this.memoryInUse = memoryInUse;
    }
    private int noCPUs;
    private long availableMemory;
    private long maxMemory;
    private long memoryInUse;
    private Gauge freeMemory;
    public  JCheckBox startStop;
    public JButton close;
    private Thread localThread = null;
    private boolean running = false;

    public MemoryCounter() {
        setNoCPUs(Runtime.getRuntime().availableProcessors());
        setMaxMemory(Runtime.getRuntime().maxMemory());

        startStop = new JCheckBox("Start");
        startStop.setSelected(running);
        close = new JButton("Close");
        this.setLayout(new BorderLayout());
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new FlowLayout());
        upperPanel.add(new JLabel("No CPUs: " + Integer.toString(getNoCPUs())));
        upperPanel.add(startStop);
        //upperPanel.add(close);

        startStop.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (isRunning()) {
                    setRunning(false);
                    freeMemory.setCurrent(0);                    
                } else {
                    setRunning(true);
                    localThread = new Thread(MemoryCounter.this);
                    localThread.start();
                }
            }
        });

        freeMemory = new Gauge();
        freeMemory.setLegend("Memory MB");
        freeMemory.setUnits("0");
        freeMemory.setMinimum(0);
        Double d = Double.valueOf(getMaxMemory());
        d = d / 10485760;  // we want it in MB
        freeMemory.setMaximum(d.intValue());

        this.add(BorderLayout.NORTH, upperPanel);
        this.add(BorderLayout.CENTER, freeMemory);

    }

    @Override
    public void run() {
        while (isRunning()) {
            memoryInUse = Runtime.getRuntime().totalMemory();
            memoryInUse = memoryInUse / 10485760;
            Long l = memoryInUse;
            freeMemory.setCurrent(l.intValue());
            freeMemory.setUnits(Long.toString(l));
            try {
                sleep(100);                
            } catch (InterruptedException ex) {
                Logger.getLogger(MemoryCounter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String args[]) {
        JFrame f = new JFrame();
        f.getContentPane().setLayout(new BorderLayout());
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
        MemoryCounter mc = new MemoryCounter();
        f.add(BorderLayout.CENTER, mc);
        f.pack();
        f.setVisible(true);
        //Thread thread = new Thread(mc);
        //thread.start();

    }
}
