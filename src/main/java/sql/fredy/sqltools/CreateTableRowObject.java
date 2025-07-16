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
package sql.fredy.sqltools;

public class CreateTableRowObject extends java.lang.Object {

    private boolean primaryKey = false;
    private String name = "";
    private String type = "";
    private int length = 0;
    private boolean notNull = false;
    private String defaultValue = "";
    private String checkConstraint = "";
    private String references = "";
    private String index = "";

    public CreateTableRowObject() {

    }

    public CreateTableRowObject(java.util.Vector v) {
        setVector(v);
    }

    public java.util.Vector getVector() {
        java.util.Vector v = new java.util.Vector();
        v.addElement((Boolean) isPrimaryKey());
        v.addElement((String) getName());
        v.addElement((String) getType());
        v.addElement((Integer) getLength());
        v.addElement((Boolean) isNotNull());
        v.addElement((String) getDefaultValue());
        v.addElement((String) getCheckConstraint());
        v.addElement((String) getReferences());
        v.addElement((String) getIndex());

        return v;
    }

    public void setVector(java.util.Vector v) {
        setPrimaryKey( (Boolean) v.elementAt(0));
        setName((String) v.elementAt(1));
        setType((String) v.elementAt(2));

        if (v.elementAt(3).getClass().isInstance(new String())) {
            System.out.println("length: " + (String) v.elementAt(3));
            
            setLength((Integer.valueOf((String) v.elementAt(3))));
        } else {
            System.out.println("length: " + (Integer) v.elementAt(3));
            setLength(((Integer) v.elementAt(3)));
        }

        setNotNull( (boolean) v.elementAt(4));
        setDefaultValue((String) v.elementAt(5));
        setCheckConstraint((String) v.elementAt(6));
        setReferences((String) v.elementAt(7));
        setIndex((String) v.elementAt(8));
    }

    /**
     * Getter for property checkConstraint.
     *
     * @return Value of property checkConstraint.
     *
     */
    public java.lang.String getCheckConstraint() {
        return checkConstraint;
    }

    /**
     * Setter for property checkConstraint.
     *
     * @param checkConstraint New value of property checkConstraint.
     *
     */
    public void setCheckConstraint(java.lang.String checkConstraint) {
        this.checkConstraint = checkConstraint;
    }

    /**
     * Getter for property defaultValue.
     *
     * @return Value of property defaultValue.
     *
     */
    public java.lang.String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Setter for property defaultValue.
     *
     * @param defaultValue New value of property defaultValue.
     *
     */
    public void setDefaultValue(java.lang.String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Getter for property index.
     *
     * @return Value of property index.
     *
     */
    public java.lang.String getIndex() {
        return index;
    }

    /**
     * Setter for property index.
     *
     * @param index New value of property index.
     *
     */
    public void setIndex(java.lang.String index) {
        this.index = index;
    }

    /**
     * Getter for property length.
     *
     * @return Value of property length.
     *
     */
    public int getLength() {
        return length;
    }

    /**
     * Setter for property length.
     *
     * @param length New value of property length.
     *
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * Getter for property name.
     *
     * @return Value of property name.
     *
     */
    public java.lang.String getName() {
        return name;
    }

    /**
     * Setter for property name.
     *
     * @param name New value of property name.
     *
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }

    /**
     * Getter for property notNull.
     *
     * @return Value of property notNull.
     *
     */
    public boolean isNotNull() {
        return notNull;
    }

    /**
     * Setter for property notNull.
     *
     * @param notNull New value of property notNull.
     *
     */
    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    /**
     * Getter for property primaryKey.
     *
     * @return Value of property primaryKey.
     *
     */
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * Setter for property primaryKey.
     *
     * @param primaryKey New value of property primaryKey.
     *
     */
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * Getter for property references.
     *
     * @return Value of property references.
     *
     */
    public java.lang.String getReferences() {
        return references;
    }

    /**
     * Setter for property references.
     *
     * @param references New value of property references.
     *
     */
    public void setReferences(java.lang.String references) {
        this.references = references;
    }

    /**
     * Getter for property type.
     *
     * @return Value of property type.
     *
     */
    public java.lang.String getType() {
        return type;
    }

    /**
     * Setter for property type.
     *
     * @param type New value of property type.
     *
     */
    public void setType(java.lang.String type) {
        this.type = type;
    }

}
