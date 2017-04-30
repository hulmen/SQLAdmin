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
 
 * This Class is part of Fredy's SQL-Admin Tool.

 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, like: - create/ drop tables - create indices - perform
 * sql-statements - simple form - a guided query - Data Export and a other
 * usefull things in DB-arena
 *
 * Admin (Version see below) Copyright (c) 1999 Fredy Fischer sql@hulmen.ch
 *
 * Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 *
 * The icons used in this application are from Dean S. Jones
 *
 * Icons Copyright(C) 1998 by Dean S. Jones dean@gallant.com
 * www.gallant.com/icons.htm
 *
 * CalendarBean is Copyright (c) by Kai Toedter
 *
 * MSeries is Copyright (c) by Martin Newstead
 *
 * POI is from the Apache Foundation
 *
 */


package sql.fredy.share;

/**
 *
 * @author sql@hulmen.ch
 */
public class CellObject {
    
    private Object object;
    private String printable;
    private int type;
    private String typeText;
    private String javaType;
    
    public CellObject() {
        setObject(null);
        setPrintable("null");
        setType(0);
        setTypeText(null);
    }
    
    
    public CellObject(Object o, String p, int type, String typeText,String javaType) {
        setObject(o);
        setPrintable(p);
        setType(type);
        setTypeText(typeText);
        setJavaType(javaType);
    }
    
    
    /**
     * @return the objct
     */
    public Object getObject() {
        return object;
    }

    /**
     * @param objct the objct to set
     */
    public void setObject(Object object) {
        this.object = object;
    }

    /**
     * @return the printable
     */
    public String getPrintable() {
        return printable;
    }

    /**
     * @param printable the printable to set
     */
    public void setPrintable(String printable) {
        this.printable = printable;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the typeText
     */
    public String getTypeText() {
        return typeText;
    }

    /**
     * @param typeText the typeText to set
     */
    public void setTypeText(String typeText) {
        this.typeText = typeText;
    }

    /**
     * @return the javaType
     */
    public String getJavaType() {
        return javaType;
    }

    /**
     * @param javaType the javaType to set
     */
    public void setJavaType(String javaType) {
        this.javaType = javaType;
    }

}
