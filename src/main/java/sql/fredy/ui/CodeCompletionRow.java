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

import java.util.Vector;

/**
 *
 * @author sql@hulmen.ch
 */
public class CodeCompletionRow {

    private String name;
    private String abbreviation;
    private String codeTemplate;
    private String description;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * @param abbreviation the abbreviation to set
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * @return the codeTemplate
     */
    public String getCodeTemplate() {
        return codeTemplate;
    }

    /**
     * @param codeTemplate the codeTemplate to set
     */
    public void setCodeTemplate(String codeTemplate) {
        this.codeTemplate = codeTemplate;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Vector getVector() {
        Vector v = new Vector();
        v.add((String) getName());
        v.add((String) getAbbreviation());        
        
        // we only display a reduced amount of text in the list
        v.add((String) getCodeTemplate());
        v.add((String) getDescription());
        return v;
    }
    
}
