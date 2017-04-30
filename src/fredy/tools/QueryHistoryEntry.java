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
package sql.fredy.tools;

import java.util.Vector;
import java.sql.Timestamp;

public class QueryHistoryEntry {

    private java.sql.Timestamp runat;

    public void setRunat(java.sql.Timestamp v) {
        this.runat = v;
    }

    public java.sql.Timestamp getRunat() {
        return runat;
    }

    private String database;

    public void setDatabase(String v) {
        this.database = v;
    }

    public String getDatabase() {
        return database;
    }

    private String query;

    public void setQuery(String v) {
        this.query = v;
    }

    public String getQuery() {
        return query;
    }

    public Vector getVector() {
        Vector v = new Vector();
        v.addElement((java.sql.Timestamp) getRunat());
        v.addElement((String) getDatabase());
        v.addElement((String) getQuery());

        return v;
    }

}
