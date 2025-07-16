package sql.fredy.ui;

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

import java.awt.event.*;
import javax.swing.*;

public class CloseableFrame extends JFrame {

    boolean exitOnClose = true;

    /**
     * Get the value of exitOnClose.
     *
     * @return value of exitOnClose.
     */
    public boolean isExitOnClose() {
        return exitOnClose;
    }

    /**
     * Set the value of exitOnClose.
     *
     * @param v Value to assign to exitOnClose.
     */
    public void setExitOnClose(boolean v) {
        this.exitOnClose = v;
    }

    public CloseableFrame() {
        init();
    }

    public CloseableFrame(String title) {

        super(title);
        init();

    }

    public CloseableFrame(String title, boolean closeOnExit) {

        super(title);
        setExitOnClose(closeOnExit);
        init();

    }

    public void init() {
        this.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                if (isExitOnClose()) {
                    System.exit(0);
                } else {
                    close();
                }
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

    public void close() {
        this.dispose();
    }

    public void doPack() {
        this.pack();
    }

    public static void main(String args[]) {
        if (args.length > 0) {
            CloseableFrame c = new CloseableFrame(args[0]);
        } else {
            CloseableFrame c = new CloseableFrame();
        }
    }
}
