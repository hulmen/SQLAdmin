package sql.fredy.datadrill.save;
/**
 * This software is part of the Admin-Framework and free software
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs
 * for DB-Administrations, as create / delete / alter and query tables
 * it also creates indices and generates simple Java-Code to access DBMS-tables
 * and exports data into various formats
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
 */

import applications.basics.*;
import javax.swing.*;

public class SqlParserObject {

    public String parameter;
    public String value;
    public int DATEFIELD=0;
    public int TEXTFIELD=1;
    public int NUMBERFIELD=2;
    public int COMBOBOX =3;
    public int TWINBOX  =4;


    String origText;
    
    /**
     * Get the value of origText.
     * @return value of origText.
     */
    public String getOrigText() {
	return origText;
    }
    
    /**
     * Set the value of origText.
     * @param v  Value to assign to origText.
     */
    public void setOrigText(String  v) {
	this.origText = v;
    }
    


    int fieldType = TEXTFIELD;
    
    /**
     * Get the value of fieldType.
     * @return value of fieldType.
     */
    public int getFieldType() {
	return fieldType;
    }
    
    /**
     * Set the value of fieldType.
     * @param v  Value to assign to fieldType.
     */
    public void setFieldType(int  v) {
	this.fieldType = v;
    }

    public void setTyp(String v) {
	setFieldType(TEXTFIELD);
	if ( v.toLowerCase().equals("date") ) setFieldType(DATEFIELD);
	if ( v.toLowerCase().equals("alpha") ) setFieldType(TEXTFIELD);
	if ( v.toLowerCase().equals("numeric") ) setFieldType(NUMBERFIELD);
	if ( v.toLowerCase().equals("combo") ) setFieldType(COMBOBOX);
	if ( v.toLowerCase().equals("twin") ) setFieldType(TWINBOX);

    }

    String query;
    
    /**
     * Get the value of query.
     * @return value of query.
     */
    public String getQuery() {
	return query;
    }
    
    /**
     * Set the value of query.
     * @param v  Value to assign to query.
     */
    public void setQuery(String  v) {
	this.query = v;
    }
    


    public String getText() {
	String s = "";
	if ( getFieldType() == DATEFIELD ) s = ((DateFieldPanel)component).getText();
	if ( getFieldType() == TEXTFIELD ) s = ((FieldPanel)component).getText();
	if ( getFieldType() == COMBOBOX  ) s = ((DBcomboBoxPanel)component).getText();
	if ( getFieldType() == TWINBOX   ) s = ((TwinBoxPanel)component).getText();

	return s;

    }
    
    JComponent component;
    
 
    public String getValue() {
	String s="";
	if ( getFieldType() == DATEFIELD   ) s = ((DateFieldPanel)component).getText();
	if ( getFieldType() == TEXTFIELD   ) s = ((FieldPanel)component).getText();
	if ( getFieldType() == NUMBERFIELD ) s = ((FieldPanel)component).getText();

	return s;
    }
    
    /**
     * Set the value of component.
     * @param v  Value to assign to component.
     */
    public void setComponent() {
	if ( getFieldType() == DATEFIELD   ) component = new DateFieldPanel(parameter);
	if ( getFieldType() == TEXTFIELD   ) component = new FieldPanel(parameter,20,JTextFieldFilter.ALPHA_NUMERIC);
	if ( getFieldType() == NUMBERFIELD ) component = new FieldPanel(parameter,20,JTextFieldFilter.NUMERIC);
  
    }
    
    public JComponent getComponent() { return component; }

    public SqlParserObject() {
	
	parameter = null;
	value     = null;
	
    }
    
}
