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
package sql.fredy.test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.basic.BasicLookAndFeel;

/**
 *
 * @author fredy
 */
public class LookAndFeel extends JFrame {

    public LookAndFeel() {
        super("LnF");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu lookAndFeel = new JMenu("L n F");

        this.getContentPane().setLayout(new BorderLayout());

        ArrayList<String> availablbeLnf = new ArrayList();
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            JMenuItem mi = new JMenuItem(info.getName());
            mi.setActionCommand(info.getClassName());
            lookAndFeel.add(mi);
            mi.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        javax.swing.UIManager.setLookAndFeel(e.getActionCommand());                        
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(LookAndFeel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InstantiationException ex) {
                        Logger.getLogger(LookAndFeel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(LookAndFeel.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnsupportedLookAndFeelException ex) {
                        Logger.getLogger(LookAndFeel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });

            availablbeLnf.add(info.getName());
        }
                
        
        JMenuItem jt1 = new JMenuItem("Acryl");
        JMenuItem jt2 = new JMenuItem("Aero");
        JMenuItem jt3 = new JMenuItem("Aluminium");
                            
        menuBar.add(lookAndFeel);
        this.setJMenuBar(menuBar);
        
        JComboBox lnf = new JComboBox(availablbeLnf.toArray());
        this.getContentPane().add(lnf, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);

    }

    public static void main(String args[]) {
        LookAndFeel l = new LookAndFeel();

    }
}
