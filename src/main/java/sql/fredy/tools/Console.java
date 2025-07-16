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
package sql.fredy.tools;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

public class Console extends JPanel implements Runnable {

    private JTextArea textArea, zoomArea;
    private Thread reader;
    private Thread reader2;
    private boolean quit;

    private final PipedInputStream pin = new PipedInputStream();
    private final PipedInputStream pin2 = new PipedInputStream();

    Thread errorThrower; // just for testing (Throws an Exception at this Console

    public Console() {

        textArea = new JTextArea(4, 80);
        textArea.setEditable(false);

        zoomArea = new JTextArea(24, 80);
        zoomArea.setEditable(false);

        //JButton button = new JButton("clear");
        this.setLayout(new BorderLayout());
        this.add(new JScrollPane(textArea), BorderLayout.CENTER);
        //this.add(button, BorderLayout.SOUTH);
        this.setVisible(true);

        //button.addActionListener(this);
        try {
            PipedOutputStream pout = new PipedOutputStream(this.pin);
            System.setOut(new PrintStream(pout, true));
        } catch (java.io.IOException io) {
            textArea.append("Couldn't redirect STDOUT to this console\n" + io.getMessage());
        } catch (SecurityException se) {
            textArea.append("Couldn't redirect STDOUT to this console\n" + se.getMessage());
        }

        try {
            PipedOutputStream pout2 = new PipedOutputStream(this.pin2);
            System.setErr(new PrintStream(pout2, true));
        } catch (java.io.IOException io) {
            textArea.append("Couldn't redirect STDERR to this console\n" + io.getMessage());
        } catch (SecurityException se) {
            textArea.append("Couldn't redirect STDERR to this console\n" + se.getMessage());
        }

        quit = false; // signals the Threads that they should exit

        // Starting two seperate threads to read from the PipedInputStreams				
        //
        reader = new Thread(this);
        reader.setDaemon(true);
        reader.start();
        //
        reader2 = new Thread(this);
        reader2.setDaemon(true);
        reader2.start();

        /*
        errorThrower = new Thread(this);
        errorThrower.setDaemon(true);
        errorThrower.start();
         */
        popUp();
    }

    JPopupMenu popup;
    JDialog dialog = null;

    private void popUp() {
        final JPanel component = this;
        popup = new JPopupMenu();
        JMenuItem save = new JMenuItem("Save");
        JMenuItem clear = new JMenuItem("Clear");
        JMenuItem undock = new JMenuItem("Zoom");

        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser chooser = new JFileChooser();
                chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                chooser.setDialogTitle("Select File to save into");

                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        File file = chooser.getSelectedFile();
                        PrintWriter out = new PrintWriter(new FileOutputStream(file));
                        out.print(textArea.getText());
                        out.flush();
                        out.close();
                    } catch (FileNotFoundException nfe) {
                        System.out.println("Exception while saving file " + nfe.getMessage());
                    }
                }
            }
        });
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                textArea.setText("");
            }
        });

        if (dialog == null) {
            dialog = new JDialog();
            dialog.setTitle("Console");
            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(new JScrollPane(zoomArea), BorderLayout.CENTER);
            
            dialog.pack();

            dialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                }

                public void windowActivated(WindowEvent evt) {
                    zoomArea.setText(textArea.getText());                   
                    zoomArea.setCaretPosition(zoomArea.getDocument().getLength());
                }

                public void windowDeactivated(WindowEvent evt) {
                   
                }

                public void windowIconified(WindowEvent evt) {
                    // mb.frame.setState(Frame.ICONIFIED);
                }

                public void windowDeiconified(WindowEvent evt) {
                    zoomArea = textArea;
                    zoomArea.setCaretPosition(zoomArea.getDocument().getLength());
                }

                public void windowOpened(WindowEvent evt) {
                    zoomArea.setText(textArea.getText());
                    zoomArea.setCaretPosition(zoomArea.getDocument().getLength());
                }

                public void windowClosed(WindowEvent evt) {
                }
            });

            int inFocus = JComponent.WHEN_FOCUSED;
            InputMap iMap = zoomArea.getInputMap(inFocus);
            ActionMap aMap = zoomArea.getActionMap();

            KeyStroke ctrlS = KeyStroke.getKeyStroke("control S");
            iMap.put(ctrlS, "ctrl-S");

            Action saveAction = new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    final JFileChooser chooser = new JFileChooser();
                    chooser.setDialogType(JFileChooser.SAVE_DIALOG);
                    chooser.setDialogTitle("Select File to save into");

                    int returnVal = chooser.showSaveDialog(null);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        try {
                            File file = chooser.getSelectedFile();
                            PrintWriter out = new PrintWriter(new FileOutputStream(file));
                            out.print(zoomArea.getText());
                            out.flush();
                            out.close();
                        } catch (FileNotFoundException nfe) {
                            System.out.println("Exception while saving file " + nfe.getMessage());
                        }
                    }
                }
            };
            aMap.put("ctrl-S", saveAction);

            JMenuBar menubar = new JMenuBar();
            JMenuItem saveMenuItem = new JMenuItem("Save");
            JMenuItem clearMenuItem = new JMenuItem("Clear");
            JMenuItem closeMenuItem = new JMenuItem("Close");

            JMenu file = new JMenu("File");
            file.add(saveMenuItem);
            file.add(clearMenuItem);
            file.add(new JSeparator());
            file.add(closeMenuItem);
            menubar.add(file);

            saveMenuItem.addActionListener(saveAction);
            clearMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    zoomArea.setText("");
                }
            });
            closeMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.setVisible(false);
                }
            });

            dialog.setJMenuBar(menubar);

        }
        undock.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                if (dialog.isVisible()) {
                    dialog.setVisible(false);
                } else {
                    final Container tla = Console.this.getTopLevelAncestor();
                    dialog.setLocation(((sql.fredy.admin.Admin) tla).getAdminPosX(), ((sql.fredy.admin.Admin) tla).getAdminPosY());
                    //dialog.setLocation(MouseInfo.getPointerInfo().getLocation());
                    dialog.setVisible(true);                 
                }

            }
        });

        popup.add(save);
        popup.add(clear);
        popup.add(undock);

        MouseListener mouseListener = new MouseAdapter() {

            // left mouse button
            public void mouseClicked(MouseEvent e) {
                int leftButton = e.BUTTON1;
                int rightButton = e.BUTTON3;
                int centerButton = e.BUTTON2;

                // only if right or center button
                if ((e.getButton() == rightButton) || (e.getButton() == centerButton)) {
                    popup.show(textArea, e.getX(), e.getY());
                }
            }

        };
        textArea.addMouseListener(mouseListener);
    }

    public synchronized void run() {
        try {
            while (Thread.currentThread() == reader) {
                try {
                    this.wait(100);
                } catch (InterruptedException ie) {
                }
                if (pin.available() != 0) {
                    String input = this.readLine(pin);
                    textArea.append(input);
                    textArea.setCaretPosition(textArea.getDocument().getLength());

                    if (dialog.isVisible()) {
                        zoomArea.append(input);
                        zoomArea.setCaretPosition(zoomArea.getDocument().getLength());
                    }

                }
                if (quit) {
                    return;
                }
            }

            while (Thread.currentThread() == reader2) {
                try {
                    this.wait(100);
                } catch (InterruptedException ie) {
                }
                if (pin2.available() != 0) {
                    String input = this.readLine(pin2);
                    textArea.append(input);
                    textArea.setCaretPosition(textArea.getDocument().getLength());

                    if (dialog.isVisible()) {
                        zoomArea.append(input);
                        zoomArea.setCaretPosition(zoomArea.getDocument().getLength());
                    }
                }
                if (quit) {
                    return;
                }
            }
        } catch (Exception e) {
            textArea.append("\nConsole reports an Internal error.");
            textArea.append("The error is: " + e);
        }

    }

    public synchronized String readLine(PipedInputStream in) throws IOException {
        String input = "";
        do {
            int available = in.available();
            if (available == 0) {
                break;
            }
            byte b[] = new byte[available];
            in.read(b);
            input = input + new String(b, 0, b.length);
        } while (!input.endsWith("\n") && !input.endsWith("\r\n") && !quit);
        return input;
    }

    public static void main(String[] arg) {
        
        JFrame frame = new JFrame("Console");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new Console(),BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
                
    }
}
