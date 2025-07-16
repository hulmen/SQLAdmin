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

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;

/**
 *
 * @author sql@hulmen.ch
 */
public class SearchReplaceGui extends JDialog {

    private JTextArea searchText;
    private JTextArea replaceWith;
    public JButton unselect;
    public ImageButton next;
    public ImageButton prev;
    public ImageButton replace;   
    public ImageButton replaceAll;
    private JCheckBox regExp;
    private JCheckBox matchCase;
    private JCheckBox wholeWord;

    private JPanel checkPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBorder(new EtchedBorder());

        regExp = new JCheckBox("Reg Exp");
        regExp.setToolTipText("use regular expressions");
        matchCase = new JCheckBox("match case");
        matchCase.setToolTipText("case matters");
        wholeWord = new JCheckBox("Whole word");
        wholeWord.setToolTipText("search as word");

        unselect = new JButton("unselect");
        
        panel.add(regExp);
        panel.add(matchCase);
        panel.add(wholeWord);
        panel.add(unselect);

        return panel;
    }

    public boolean isRegExp() {
        return regExp.isSelected();
    }

    public boolean isMatchCase() {
        return matchCase.isSelected();
    }

    public boolean isWholeWord() {
        return wholeWord.isSelected();
    }

    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        prev = new ImageButton(null, "vcrback.gif", "find previous");
        next = new ImageButton(null, "vcrforward.gif", "find next");

        replace = new ImageButton(null, "draw.gif", "replace once");
        replaceAll = new ImageButton(null,"documentdraw.gif","replace all");
        
        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.insets = insets;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
               
        panel.add(prev, gbc);
        gbc.gridx = 1;
        panel.add(next,gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;               
        panel.add(replace, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;               
        panel.add(replaceAll, gbc);
        
        
        
        panel.setBorder(new EtchedBorder());

        return panel;
    }

    private JPanel textPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(new EtchedBorder());

        searchText = new JTextArea(2, 20);
        replaceWith = new JTextArea(2, 20);

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Search for:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(searchText, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(new JLabel("Replace with:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(replaceWith, gbc);

        return panel;
    }

    public String getSearchFor() {
        return searchText.getText();
    }

    public String getReplaceWith() {
        return replaceWith.getText();
    }

    public SearchReplaceGui() {
        this.getContentPane().setLayout(new GridBagLayout());
        this.setTitle("Search & Replace");

        GridBagConstraints gbc;
        Insets insets = new Insets(2, 2, 2, 2);
        gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.insets = insets;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.getContentPane().add(textPanel(), gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.getContentPane().add(buttonPanel(), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.getContentPane().add(checkPanel(), gbc);

        this.pack();                
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
    }

    public static void main(String a[]) {
        SearchReplaceGui srg = new SearchReplaceGui();
        srg.setVisible(true);
    }
}
