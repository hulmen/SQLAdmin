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
package sql.fredy.ui;

import java.awt.BorderLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

/**
 *
 * @author sql@hulmen.ch
 */


public class StickyNotes extends JPanel {

    private Logger logger = Logger.getLogger("sql.fredy.ui.StickyNotes");
    private RSyntaxTextArea note;
    
    public StickyNotes() {
        this.setLayout(new BorderLayout());
        this.setFocusable(true);
        this.addFocusListener( new FocusAdapter() {
            public void focusLost(FocusEvent e) {
                System.out.println("Focus lost");
            }
            
            public void focusGained(FocusEvent e) {
                System.out.println("Focus gained");
            }
        });
        
       
        
        
    }
    
    public static void main(String args[]) {
        JFrame frame = new JFrame("StickNotes");
        StickyNotes s = new StickyNotes();
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(s,BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }
    
    
    
}
