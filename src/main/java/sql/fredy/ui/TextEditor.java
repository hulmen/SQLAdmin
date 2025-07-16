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

    License of fifesoft applies:
* RSyntaxTextArea and AutoComplete is from www.fifesoft.com
 * ---------------- Start fifesoft License ------------------------------------- 
 *      Copyright (c) 2012, Robert Futrell
 *      All rights reserved.
 *
 *      Redistribution and use in source and binary forms, with or without
 *      modification, are permitted provided that the following conditions are met:
 *          * Redistributions of source code must retain the above copyright
 *            notice, this list of conditions and the following disclaimer.
 *          * Redistributions in binary form must reproduce the above copyright
 *            notice, this list of conditions and the following disclaimer in the
 *            documentation and/or other materials provided with the distribution.
 *          * Neither the name of the author nor the names of its contributors may
 *            be used to endorse or promote products derived from this software
 *            without specific prior written permission.
 *
 *      THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *      ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *      WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *      DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 *      DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *      (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *      LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *      ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *      (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *      SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ---------------- End fifesoft License -------------------------------------
 *    

 */
package sql.fredy.ui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.logging.*;
import java.math.BigInteger;
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

public class TextEditor extends JPanel {

    public ImageButton ok, cancel;
    private Toolkit toolKit;
    private Logger logger = Logger.getLogger("sql.fredy.ui");

    private String lastDirectoryPath = null;
    private String fileName = null;

    private JComboBox editingStyle;

    //public JTextArea text;
    public RSyntaxTextArea text;

    public String getText() {
        return text.getText();

    }

    public void setText(String v) {
        text.setText(v);
    }

    public void setTitle(String t) {
        this.setTitle(t);
    }

    public TextEditor() {
        doIt();
    }

    public TextEditor(String text) {
        doIt();
        this.text.setText(text);
    }

    public void setSyntaxStyle(String style) {
        text.setSyntaxEditingStyle(style);
        text.updateUI();
    }

    private void doIt() {
        toolKit = this.getToolkit();
        this.setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        text = new RSyntaxTextArea(20, 80);
        text.setFont(new Font("Monospaced", Font.PLAIN, 12));

        text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        text.setCodeFoldingEnabled(true);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        editorExtensions();  // add Menus and funcstions to the popupmenu of the editor

        //text = new JTextArea(24,80);
        //text.setFont(new  java.awt.Font("Monospaced", Font.PLAIN, 12));       
        panel.add(BorderLayout.CENTER, new RTextScrollPane(text));

        text.setDragEnabled(true);

        text.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent fe) {

            }

            public void focusLost(FocusEvent fe) {
            }
        });

        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout());

        ImageButton clear = new ImageButton(null, "clear.gif", null);
        clear.setToolTipText("clear text area");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = text.getText();
                StringSelection ss = new StringSelection(s);
                toolKit.getSystemClipboard().setContents(ss, ss);
                text.setText("");
            }
        });
        buttons.add(clear);

        ImageButton copy = new ImageButton(null, "copy.gif", null);
        copy.setToolTipText("Copy selection to clipboard");
        copy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    String s = text.getSelectedText();
                    if (s.length() > 0) {
                        StringSelection ss = new StringSelection(s);
                        toolKit.getSystemClipboard().setContents(ss, ss);
                    }
                } catch (Exception ec) {
                    toolKit.beep();
                }
            }
        });
        buttons.add(copy);

        ImageButton paste = new ImageButton(null, "paste.gif", null);
        paste.setToolTipText("Paste");
        paste.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Clipboard c = toolKit.getSystemClipboard();
                Transferable t = c.getContents(this);
                try {
                    String s = (String) t.getTransferData(DataFlavor.stringFlavor);
                    text.insert(s, text.getCaretPosition());
                } catch (Exception eexc) {
                    toolKit.beep();
                }
            }
        });
        buttons.add(paste);

        ImageButton cut = new ImageButton(null, "cut.gif", null);
        cut.setToolTipText("Cut");
        cut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s = text.getSelectedText();
                StringSelection ss = new StringSelection(s);
                toolKit.getSystemClipboard().setContents(ss, ss);
                text.replaceRange(null, text.getSelectionStart(), text.getSelectionEnd());
            }
        });
        buttons.add(cut);

        ImageButton saveAs = new ImageButton(null, "container.gif", "Save Text to file");
        saveAs.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter();
                fw.setFilter(new String[]{"sql", "SQL", "txt", "TXT", "java", "JAVA", "vbs", "VBS", "php", "PHP", "sh", "bsh", "ksh", "bash", "perl"});
                fw.setContent(text.getText());
                fw.setFileName("?");
                fw.setSwitch("a");
                fw.write();
            }
        });
        buttons.add(saveAs);

        ImageButton saveText = new ImageButton(null, "save.gif", "Save Text to file");
        saveText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.FileWriter fw = new sql.fredy.io.FileWriter(text.getText(), fileName);
                /*
                fw.setFilter(new String[]{"sql", "SQL", "txt", "TXT","java","JAVA","vbs","VBS","php","PHP","sh","bsh","ksh","bash","perl"});
                fw.setContent(text.getText());
                fw.setFileName("?");
                fw.setSwitch("a");
                fw.write();
                 */
            }
        });
        buttons.add(saveText);

        ImageButton loadText = new ImageButton(null, "load.gif", "Load text from file");
        loadText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sql.fredy.io.ReadFile rf = new sql.fredy.io.ReadFile();
                rf.setFilter(new String[]{"sql", "SQL", "txt", "TXT", "java", "JAVA", "jsp", "js", "vbs", "VBS", "php", "PHP", "sh", "bsh", "ksh", "bash", "perl"});
                rf.setFileName("?");
                if (text.getText().length() > 0) {
                    text.append(";\n");
                }
                text.append(rf.getText());
                text.updateUI();
                setFileStyle(rf.getFileName());
            }
        });
        buttons.add(loadText);

        JCheckBoxMenuItem wrapText = new JCheckBoxMenuItem("Wrap Text");
        wrapText.setSelected(true);
        wrapText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (wrapText.isSelected()) {
                    text.setLineWrap(true);
                    text.setWrapStyleWord(true);
                } else {
                    text.setLineWrap(false);
                }
                text.updateUI();
            }
        });
        buttons.add(wrapText);

        String[] editingStyles = {
            "JAVA",
            "JAVASCRIPT",
            "JSON",
            "JSP",
            "SQL",
            "HTML",
            "XML",
            "UNIX_SHELL",
            "VISUAL_BASIC",
            "C",
            "TCL",
            "ACTIONSCRIPT",
            "ASSEMBLER_X86",
            "BBCODE",
            "CLOJURE",
            "CPLUSPLUS",
            "CSHARP",
            "CSS",
            "DELPHI",
            "DTD",
            "FORTRAN",
            "GROOVY",
            "HTACCESS",
            "LATEX",
            "LISP",
            "LUA",
            "MAKEFILE",
            "MXML",
            "NONE",
            "NSIS",
            "PERL",
            "PHP",
            "PROPERTIES_FILE",
            "PYTHON",
            "RUBY",
            "SAS",
            "SCALA",
            "WINDOWS_BATCH",};
        editingStyle = new JComboBox(editingStyles);
        editingStyle.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String style = (String) editingStyle.getSelectedItem();
                changeStyle(style);
            }
        });

        buttons.add(new JLabel("Editing Style:"));
        buttons.add(editingStyle);

        buttons.setBorder(new EtchedBorder());
        panel.add(BorderLayout.NORTH, new JScrollPane(buttons));

        panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Text"));

        this.add(BorderLayout.CENTER, panel);
        this.add(BorderLayout.SOUTH, buttonPanel());

    }

    private void editorExtensions() {
        // add Editor Stuff here

        JMenuItem hex = new JMenuItem("Display Selection as Hex");

        // Font-size
        JMenu fontMenu = new JMenu("Fontsize");

        JMenuItem font8 = new JMenuItem("8");
        JMenuItem font10 = new JMenuItem("10");
        JMenuItem font12 = new JMenuItem("12");
        JMenuItem font14 = new JMenuItem("14");
        JMenuItem font16 = new JMenuItem("16");
        JMenuItem font18 = new JMenuItem("18");

        JMenuItem zoomPlus = new JMenuItem("bigger");  // not used now
        JMenuItem zoomMinus = new JMenuItem("smaller"); // not used now

        fontMenu.add(font8);
        fontMenu.add(font10);
        fontMenu.add(font12);
        fontMenu.add(font14);
        fontMenu.add(font16);
        fontMenu.add(font18);

        font8.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                text.setFont(text.getFont().deriveFont(8.0f));
            }
        });
        font10.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                text.setFont(text.getFont().deriveFont(10.0f));
            }
        });
        font12.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                text.setFont(text.getFont().deriveFont(12.0f));
            }
        });
        font14.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                text.setFont(text.getFont().deriveFont(14.0f));
            }
        });
        font16.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                text.setFont(text.getFont().deriveFont(16.0f));
            }
        });
        font18.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                text.setFont(text.getFont().deriveFont(18.0f));
            }
        });

        hex.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {

                //String hexCode = String.format("%040x", new BigInteger(1, text.getText().substring(text.getSelectionStart(), text.getSelectionEnd()).getBytes()));
                String[] hexCode = stringToHex(text.getText().substring(text.getSelectionStart(), text.getSelectionEnd()));

                JDialog hexPanel = new JDialog();
                hexPanel.setTitle("Text to hex");
                hexPanel.getContentPane().setLayout(new BorderLayout());
                JTextArea text = new JTextArea(16, 50);
                text.setFont(new Font("Monospaced", Font.PLAIN, 13));
                text.setText(hexCode[1]);
                hexPanel.getContentPane().add(new JScrollPane(text), BorderLayout.EAST);
                JTextArea hexText = new JTextArea(16, 50);
                hexText.setFont(new Font("Monospaced", Font.PLAIN, 13));
                hexText.setText(hexCode[0]);
                hexPanel.getContentPane().add(new JScrollPane(hexText), BorderLayout.CENTER);
                hexPanel.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                hexPanel.pack();
                hexPanel.setVisible(true);

                //JOptionPane.showMessageDialog(null, hexCode,text.getText().substring(text.getSelectionStart(), text.getSelectionEnd()) + " toHex",JOptionPane.PLAIN_MESSAGE);
            }
        });

        JMenuItem snr = new JMenuItem("Search & Replace");
        JPopupMenu qpm = text.getPopupMenu();

        qpm.addSeparator();
        qpm.add(snr);
        qpm.addSeparator();
        qpm.add(fontMenu);
        qpm.addSeparator();
        qpm.add(hex);

        // a bigger Font has been selected: DOES NOT WORK
        zoomPlus.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                //System.out.println("Fontsize: " + fontP.getSize());                                      
                text.setFont(text.getFont().deriveFont(text.getFont().getStyle(), text.getFont().getSize2D() + 2.0f));
                text.updateUI();
            }
        });

        // a smallerFont has been selected: DOES NOT WORK
        zoomMinus.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {

                Font fontM = text.getFont();
                if (fontM.getSize2D() < 2.0) {
                    text.setFont(fontM.deriveFont(fontM.getSize2D() - 2.0f));
                    text.updateUI();
                }
            }
        });

        //Search & Replace: look here http://www.fifesoft.com/rsyntaxtextarea/examples/example4.php
        final SearchContext context = new SearchContext();
        final SearchReplaceGui srg = new SearchReplaceGui();

        snr.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                srg.setVisible(true);
            }
        });

        srg.next.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                if (srg.getSearchFor().length() > 0) {
                    context.setSearchFor(srg.getSearchFor());
                    context.setMatchCase(srg.isMatchCase());
                    context.setRegularExpression(srg.isRegExp());
                    context.setSearchForward(true);
                    context.setWholeWord(srg.isWholeWord());

                    SearchResult sr = SearchEngine.find(text, context);
                    DocumentRange dr = sr.getMatchRange();

                    SearchEngine.markAll(text, context);

                    int startPos = text.getCaretPosition();
                    int lng = SearchEngine.getNextMatchPos(srg.getSearchFor(), text.getText().substring(text.getCaretPosition()), true, srg.isMatchCase(), srg.isWholeWord());

                    text.setCaretPosition(startPos + lng);
                    text.updateUI();

                }
            }
        });
        srg.prev.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                if (srg.getSearchFor().length() > 0) {
                    context.setSearchFor(srg.getSearchFor());
                    context.setMatchCase(srg.isMatchCase());
                    context.setRegularExpression(srg.isRegExp());
                    context.setSearchForward(false);
                    context.setWholeWord(srg.isWholeWord());

                    SearchResult sr = SearchEngine.find(text, context);
                    DocumentRange dr = sr.getMatchRange();

                    SearchEngine.markAll(text, context);

                    int startPos = text.getCaretPosition();
                    int lng = SearchEngine.getNextMatchPos(srg.getSearchFor(), text.getText().substring(0, text.getCaretPosition()), false, srg.isMatchCase(), srg.isWholeWord());

                    if (lng < 0) {
                        lng = 0;
                    }
                    text.setCaretPosition(lng);
                    text.updateUI();

                }
            }
        });

        srg.replace.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                if ((srg.getSearchFor().length() > 0) && (srg.getReplaceWith().length() > 0)) {
                    context.setSearchFor(srg.getSearchFor());
                    context.setReplaceWith(srg.getReplaceWith());
                    context.setMatchCase(srg.isMatchCase());
                    context.setRegularExpression(srg.isRegExp());
                    context.setSearchForward(true);
                    context.setWholeWord(srg.isWholeWord());

                    SearchResult sr = SearchEngine.find(text, context);
                    DocumentRange dr = sr.getMatchRange();

                    SearchEngine.markAll(text, context);

                    SearchEngine.replace(text, context);

                    int startPos = text.getCaretPosition();
                    int lng = SearchEngine.getNextMatchPos(srg.getSearchFor(), text.getText().substring(text.getCaretPosition()), true, srg.isMatchCase(), srg.isWholeWord());

                    text.setCaretPosition(startPos + lng);
                    text.updateUI();

                }

            }
        });

        srg.replaceAll.addActionListener(new ActionListener() {
            // die Aktion, die ausgelöst werdne soll, bei der Auswahl dieses Elementes
            public void actionPerformed(ActionEvent e) {
                if ((srg.getSearchFor().length() > 0) && (srg.getReplaceWith().length() > 0)) {
                    context.setSearchFor(srg.getSearchFor());
                    context.setReplaceWith(srg.getReplaceWith());
                    context.setMatchCase(srg.isMatchCase());
                    context.setRegularExpression(srg.isRegExp());
                    context.setSearchForward(true);
                    context.setWholeWord(srg.isWholeWord());

                    SearchEngine.replaceAll(text, context);
                }
            }
        });

    }

    private void setFileStyle(String file) {
        file = file.toLowerCase();

        String[] extension = file.split("\\.");
        int l = extension.length - 1;
        String fileType = extension[l].toLowerCase();

        if ("sql".equals(fileType)) {
            changeStyle("SQL");
        }
        if ("java".equals(fileType)) {
            changeStyle("JAVA");
        }
        if ("js".equals(fileType)) {
            changeStyle("JAVASCRIPT");
        }
        if ("jsp".equals(fileType)) {
            changeStyle("JSP");
        }
        if ("json".equals(fileType)) {
            changeStyle("JSON");
        }
        if ("html".equals(fileType)) {
            changeStyle("HTML");
        }
        if ("xml".equals(fileType)) {
            changeStyle("XML");
        }
        if ("sh".equals(fileType)) {
            changeStyle("UNIX_SHELL");
        }
        if ("bsh".equals(fileType)) {
            changeStyle("UNIX_SHELL");
        }
        if ("ksh".equals(fileType)) {
            changeStyle("UNIX_SHELL");
        }
        if ("bash".equals(fileType)) {
            changeStyle("UNIX_SHELL");
        }
        if ("vbs".equals(fileType)) {
            changeStyle("VISUAL_BASIC");
        }
        if ("c".equals(fileType)) {
            changeStyle("C");
        }
        if ("tcl".equals(fileType)) {
            changeStyle("TCL");
        }
        if ("css".equals(fileType)) {
            changeStyle("CSS");
        }
        if ("perl".equals(fileType)) {
            changeStyle("PERL");
        }
        if ("php".equals(fileType)) {
            changeStyle("PHP");
        }
        if ("bat".equals(fileType)) {
            changeStyle("WINDOWS_BATCH");
        }
        if ("dtd".equals(fileType)) {
            changeStyle("DTD");
        }
        if ("mk".equals(fileType)) {
            changeStyle("MAKEFILE");
        }
        if ("make".equals(fileType)) {
            changeStyle("MAKEFILE");
        }

    }

    public String[] stringToHex(String str) {
        StringBuilder hex = new StringBuilder();
        StringBuilder txt = new StringBuilder();
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i % 16 == 0) {
                hex.append("\n");
                txt.append("\n");
            }

            hex.append(Integer.toHexString((int) chars[i])).append(" ");
            txt.append(chars[i]).append(" ");
        }

        return new String[]{hex.toString(), txt.toString()};
    }

    public void changeStyle(String style) {
        editingStyle.setSelectedItem((String) style);
        if ("ACTIONSCRIPT".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_ACTIONSCRIPT);
        }
        if ("ASSEMBLER_X86".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_ASSEMBLER_X86);
        }
        if ("BBCODE".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_BBCODE);
        }
        if ("C".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_C);
        }
        if ("CLOJURE".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CLOJURE);
        }
        if ("CPLUSPLUS".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS);
        }
        if ("CSHARP".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSHARP);
        }
        if ("CSS".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_CSS);
        }
        if ("DELPHI".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_DELPHI);
        }
        if ("DTD".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_DTD);
        }
        if ("FORTRAN".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_FORTRAN);
        }
        if ("GROOVY".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY);
        }
        if ("HTACCESS".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTACCESS);
        }
        if ("HTML".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_HTML);
        }
        if ("JAVA".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        }
        if ("JAVASCRIPT".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT);
        }
        if ("JSON".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
        }
        if ("JSP".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSP);
        }
        if ("LATEX".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LATEX);
        }
        if ("LISP".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LISP);
        }
        if ("LUA".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_LUA);
        }
        if ("MAKEFILE".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MAKEFILE);
        }
        if ("MXML".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MXML);
        }
        if ("NONE".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NONE);
        }
        if ("NSIS".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_NSIS);
        }
        if ("PERL".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PERL);
        }
        if ("PHP".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PHP);
        }
        if ("PROPERTIES_FILE".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE);
        }
        if ("PYTHON".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
        }
        if ("RUBY".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_RUBY);
        }
        if ("SAS".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SAS);
        }
        if ("SCALA".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SCALA);
        }
        if ("SQL".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_SQL);
        }
        if ("TCL".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_TCL);
        }
        if ("UNIX_SHELL".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL);
        }
        if ("VISUAL_BASIC".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_VISUAL_BASIC);
        }
        if ("WINDOWS_BATCH".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_WINDOWS_BATCH);
        }
        if ("XML".equals(style)) {
            text.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML);
        }

        text.revalidate();
        text.updateUI();
    }

    private JPanel buttonPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder());
        ok = new ImageButton(null, "ok.gif", null);
        cancel = new ImageButton(null, "exit.gif", null);
        panel.setName("buttonpanel");
        panel.add(ok);
        panel.add(cancel);
        return panel;
    }

    private JPanel textPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout());

        GridBagConstraints gbc;
        Insets insets = new Insets(5, 5, 5, 5);

        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;

        JScrollPane pane = new JScrollPane(text);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel.add(pane, gbc);

        return panel;

    }

    public static void main(String args[]) {
        final TextEditor f = new TextEditor();
        JFrame e = new JFrame("Editor");
        e.getContentPane().add(f);
        e.pack();
        e.setVisible(true);
        e.addWindowListener(new WindowAdapter() {
            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                System.out.println(f.getText());
                System.exit(0);
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
        f.cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

    }

    /**
     * @return the lastDirectoryPath
     */
    public String getLastDirectoryPath() {
        return lastDirectoryPath;
    }

    /**
     * @param lastDirectoryPath the lastDirectoryPath to set
     */
    public void setLastDirectoryPath(String lastDirectoryPath) {
        this.lastDirectoryPath = lastDirectoryPath;
    }

}
