package applications.basics;

/**

  some helpfull things very often needed


    Copyright (c) 1999 Fredy Fischer
                       sql@hulmen.ch

		       Fredy Fischer
		       Hulmenweg 36
		       8405 Winterthur
		       Switzerland

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






**/

import java.util.*;

public class Tools {

    public Tools() {
    }

    public String toDay() {
	Calendar cal;
	cal = Calendar.getInstance();
	String y = Integer.toString(cal.get(cal.YEAR));
	String m = Integer.toString(cal.get(cal.MONTH)+ 1);
	m = trailing(m);
	String d = Integer.toString(cal.get(cal.DAY_OF_MONTH));
	d = trailing(d);
	
	 return y + "-" + d + "-" + m;

    }

    public java.sql.Date getSqlDate(String datum) {
	StringTokenizer st = new StringTokenizer(datum,"-");
  	Calendar cal;
	cal = Calendar.getInstance();      
        int y = cal.YEAR;
        int m = cal.MONTH + 1;
        int d = cal.DAY_OF_MONTH;
	y = Integer.parseInt(st.nextToken());
        m = Integer.parseInt(st.nextToken()) - 1;
        d = Integer.parseInt(st.nextToken());
        cal.set(y,m,d);
        return new java.sql.Date(cal.getTimeInMillis());	   
    }


    public String trailing(String v) {
	if (v.length() < 2 ) v = "0"+ v;
	return v;
    }
}
