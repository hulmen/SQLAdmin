package sql.fredy.io;

/**
 * Makes a new Version of a file, means renames the File with accelerating
 * numbers....
 *
 * this tool is part of the Admin-Suite
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, as create / delete / alter and query tables it also
 * creates indices and generates simple Java-Code to access DBMS-tables and
 * exports data into various formats
 *
 *
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
*
 */
import java.util.*;
import java.io.*;

public class MakeVersion {

    String fileName;

    /**
     * Get the value of fileName.
     *
     * @return value of fileName.
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the value of fileName.
     *
     * @param v Value to assign to fileName.
     */
    public void setFileName(String v) {
        this.fileName = v;
        this.file = new File(v);
    }

    String path;

    /**
     * Get the value of path.
     *
     * @return value of path.
     */
    public String getPath() {
        return getFile().getParent();
    }

    File file = null;

    /**
     * Get the value of file.
     *
     * @return value of file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Set the value of file.
     *
     * @param v Value to assign to file.
     */
    public void setFile(String v) {
        this.file = new File(v);
    }

    public void setFile(File v) {
        this.file = v;
    }

    public MakeVersion(String s) {
        setFileName(s);
        doIt();
    }

    public MakeVersion(File f) {
        setFile(f);
        doIt();
    }

    private void doIt() {
        if (file.exists()) {
            int i = 0;
            boolean v = false;
            while (!v) {
                i++;
                File f = new File(getFile().getParent() + File.separator
                        + getFile().getName() + "." + Integer.toString(i));
                if (!f.exists()) {
                    v = getFile().renameTo(f);
                }
            }
        }
    }

    public static void main(String args[]) {
        System.out.println("File Versioner Version 1.1\n"
                + "use java sql.fredy.io.MakeVersion full-file-name");
        

        if (args.length < 1) {
            String datei;
            datei = readFromPrompt("File to version: ", "");
            if (datei.length() > 1) {
                MakeVersion m = new MakeVersion(datei);
            }
        } else {
            System.out.println("Makeing new Version of " + args[0]);
            MakeVersion m = new MakeVersion(args[0]);

        }

    }

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
}
