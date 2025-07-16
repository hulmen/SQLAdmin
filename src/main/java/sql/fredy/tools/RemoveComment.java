/**
 * RemoveComment is a part of Admin it displays the MetaData of a RDBMS as a tree.
 * Goal is to remove SQL-Comments
 * Single-Line comments starting with minimum -- (or more -)
 * Multiline comments removes lines in between to linecomments
 *
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author tkfir
 */
public class RemoveComment {

    private static String singleLineCommentIndicator = "--";
    private static String multilineIndicatorStart = "/*";
    private static String multilineIndicatorEnd = "*/";

    public String removeSingleLineComment(String line) {
        String[] lines = line.split("\n");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            if (i > 0) {
                sb.append("\n");
            }
            int p = lines[i].indexOf(singleLineCommentIndicator);
            if (p < 0) {
                sb.append(lines[i]);
            } else {
                if (p > 1) {
                    sb.append(lines[i].substring(0, lines[i].indexOf(singleLineCommentIndicator)));
                }
            }
        }
        return sb.toString();
    }

    public String removeMultiLine(String text) {
        int start = 0;
        int end = text.length();
        while (true) {
            start = text.indexOf(multilineIndicatorStart);
            if (start == 0) {
                break;
            }
            end = text.indexOf(multilineIndicatorEnd);
            text = text.substring(start, end) + text.substring(end, text.length());
        }
        return text;
    }

    public String removeComments(String text) {
        return removeMultiLine(removeSingleLineComment(text));                
    }
    
    /* read from commandline
     *
     * 1st parameter as displaytext
     * 2nd parameter as defaultvalue
     */
    public static String readFromPrompt(String text, String defValue) {
        String fromPrompt = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(text + " (Default: " + defValue + ") ");
        try {
            fromPrompt = br.readLine();
            if (fromPrompt.length() < 1) {
                fromPrompt = defValue;
            }
        } catch (IOException ioe) {
            fromPrompt = defValue;
        }
        return fromPrompt;
    }

    public static void main(String args[]) {
        RemoveComment rc = new RemoveComment();
        String s = "";
        while (true) {
            s = readFromPrompt("TestZeile: (q = quit, -f file loads file)", "");
            if (s.equalsIgnoreCase("q")) {
                System.exit(0);
            }
            if (s.startsWith("-f")) {
                
            } else {
                System.out.println(rc.removeSingleLineComment(s) + "|");
            }
        }

    }

}
