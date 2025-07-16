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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import java.nio.charset.*;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedMap;
import javax.swing.JButton;
import javax.swing.JLabel;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.border.*;

/**
 *
 * @author sql@hulmen.ch
 */
public class CharsetSelector extends JPanel {

    private JComboBox charsets;
    public JButton cancel;
    
    public CharsetSelector() {
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER,selectionPanel());
        this.add(BorderLayout.SOUTH,buttonPanel());       
    }
    
    public CharsetSelector(boolean nobutton) {
        this.setLayout(new BorderLayout());
        this.add(BorderLayout.CENTER,selectionPanel());
    }

    private JPanel selectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        
        panel.add(new JLabel("Pls select encoding: "));
        panel.add(getCharsets());
        panel.setBorder(new EtchedBorder());
        
        return panel;
        
    }
    
    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        
        cancel = new JButton("Close");
       
        panel.add(cancel);
        panel.setBorder(new EtchedBorder());
        
        return panel;
    }
    
    
    private JComboBox getCharsets() {

        charsets = new JComboBox();
        SortedMap map = Charset.availableCharsets();
        Set keys = map.keySet();

        Iterator i = keys.iterator();
        while (i.hasNext()) {
             charsets.addItem((String) i.next());                 
        }
        charsets.setSelectedItem((String)System.getProperty("file.encoding"));
                
        charsets.addActionListener(new ActionListener() {               
                public void actionPerformed(ActionEvent e) {
                    System.setProperty("file.encoding", (String)charsets.getSelectedItem());
                }
            });
        
        
        return charsets;

    }

}
