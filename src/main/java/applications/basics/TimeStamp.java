package applications.basics;

/**
   Need a correct timeStamp?
   This will do it

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

import java.util.Date;
import java.sql.Timestamp;


public class TimeStamp  {


    
    java.sql.Timestamp timestamp;
    
    /**
       * Get the value of timestamp.
       * @return Value of timestamp.
       */
    public java.sql.Timestamp getTimestamp() {return timestamp;}
    
    /**
       * Set the value of timestamp.
       * @param v  Value to assign to timestamp.
       */
    public void setTimestamp(java.sql.Timestamp  v) {this.timestamp = v;}
    

    public TimeStamp() {

	java.util.Date d = new java.util.Date();
	setTimestamp( new Timestamp( d.getTime() ) );

    }

    public static void main( String args[] ){
	TimeStamp tst = new TimeStamp();
	System.out.println("Timestamp: " + tst.getTimestamp());
    }
}
