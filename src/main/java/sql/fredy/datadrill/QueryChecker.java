package sql.fredy.datadrill;





/**

 *    QueryChecker

 *    Created 3. Dez. 2003  Fredy Fischer

 *

 *    This is to check a query and eliminate errors

 *    actually these rules apply:

 *    - Eliminate all empty lines

 *    - if a 'AND' or a 'OR' statement follows on a 'WHERE'

 *      then eliminate the 'AND' or 'OR'

 *

 *    - if a 'ORDER'-Statement follows a 'WHERE' then eliminate

 *      the 'WHERE'-Statement

 *

 *    - if the last line is a 'WHERE' statement eliminate the 'WHERE'

 *      statement

 *

 *    This is realized in the following steps:

 *    0) Initialize with a String containing the query

 *       the method check(String)

 *    1) create a Vector out of the String where each line is a element

 *       of this vector

 *    2) Process the rules mentionned above

 *    3) return the checked Query

 *

**/

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



import java.util.Vector;

import java.util.StringTokenizer;

import java.util.logging.*;



public class QueryChecker {



    private Vector tocheck = new Vector();

    private Vector checked = new Vector();

    private Logger logger  = Logger.getLogger("sql.fredy.datadrill");



    /**

     * Check the query

     * @param v  the query to check.

     */

    public String check(String  v) {

	createVector(v);

	performCheck();



	StringBuffer sb = new StringBuffer();

	for (int i=0; i < tocheck.size(); i++ ) {

	    sb.append((String) tocheck.elementAt(i));

	    sb.append("\n");

	}



	logger.log(Level.FINE,"String to check:\n" + v);

	logger.log(Level.FINE,"Checked string:\n" + sb.toString());

	return (balanceParenthesis(sb.toString()));

    }





    private void createVector(String s) {

	StringTokenizer st = new StringTokenizer(s,"\n");



	// Rule 1

	while (st.hasMoreTokens()) {

	    String t = (String)st.nextToken();

	    if (  ! t.startsWith("\n") )  tocheck.addElement(t);



	}



	// nur ein Test fuers logging
	StringBuffer sb = new StringBuffer();
	for (int i = 0; i < tocheck.size();i++) sb.append((String)tocheck.elementAt(i) + "\n" );
	logger.log(Level.FINE,"Concatenated:\n" + sb.toString());

    }


    private void performCheck() {
	String a,b;
	boolean isWhere,appendB,appendA;

	// Rule 2
	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();

		if ( (a.toLowerCase().equals("where")) &&
		     (b.toLowerCase().startsWith("and") ) ) {
		    logger.log(Level.FINE,"and follows where");
		    tocheck.removeElementAt(i+1);
		}
	    }

	}


	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();

		if ( (a.toLowerCase().equals("where")) &&
		     (b.toLowerCase().startsWith("or ") ) ) {
		    logger.log(Level.FINE,"or follows where");
		    tocheck.removeElementAt(i+1);
		}
	    }
	}


	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();

		if ( (a.toLowerCase().equals("and")) &&
		     (b.toLowerCase().startsWith("and") ) ) {
		    logger.log(Level.FINE,"and follows and");
		    tocheck.removeElementAt(i);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();

		if ( (a.toLowerCase().equals("or")) &&
		     (b.toLowerCase().startsWith("or ") ) ) {
		    logger.log(Level.FINE,"or follows or");
		    tocheck.removeElementAt(i);

		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();

		if ( (a.toLowerCase().equals("or")) &&
		     (b.toLowerCase().startsWith("and") ) ) {
		    logger.log(Level.FINE,"and follows or");
		    tocheck.removeElementAt(i);

		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();

		if ( (a.toLowerCase().equals("and")) &&
		     (b.toLowerCase().startsWith("or ") ) ) {
		    logger.log(Level.FINE,"or follows and");
		    tocheck.removeElementAt(i+1);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1 ;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();

		if ( (a.toLowerCase().equals("where")) &&
		     (b.toLowerCase().startsWith("order") ) ) {
		    logger.log(Level.FINE,"order follows where");
		    tocheck.removeElementAt(i);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();

		if ( (a.toLowerCase().equals("or")) &&
		 (b.toLowerCase().startsWith("order") ) ) {
		    logger.log(Level.FINE,"order follows or");
		    tocheck.removeElementAt(i);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("and")) &&
		     (b.toLowerCase().startsWith("order") ) ) {
		    logger.log(Level.FINE,"order follows and");
		    tocheck.removeElementAt(i);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();

		if ( (a.toLowerCase().equals("or")) &&
		     // (b.toLowerCase().startsWith(")") ) ) {
		     (b.toLowerCase().equals(")") ) ) {
		    logger.log(Level.FINE,") follows or");
		    tocheck.removeElementAt(i);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("where")) &&
		     (b.toLowerCase().startsWith(")") ) ) {
		    logger.log(Level.FINE,") follows where");
		    tocheck.removeElementAt(i);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("and")) &&
		     (b.toLowerCase().startsWith(")") ) ) {
		    logger.log(Level.FINE,") follows and");
		    tocheck.removeElementAt(i);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("or")) &&
		     //(b.toLowerCase().startsWith("(") ) ) {
		     (b.toLowerCase().equals("(") ) ) {
		    logger.log(Level.FINE,"( follows or");
		    tocheck.removeElementAt(i);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals(")")) &&
		     (b.toLowerCase().equals("(") ) ) {
		    logger.log(Level.FINE,"( follows )");
		    tocheck.removeElementAt(i);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("(")) &&
		     (b.toLowerCase().equals("or ") ) ) {
		    logger.log(Level.FINE,"or follows (");
		    tocheck.removeElementAt(i);
		}
	    }
	}

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("or")) &&
		     (b.toLowerCase().equals(")") ) ) {
		    logger.log(Level.FINE,") follows or");
		    tocheck.removeElementAt(i);
		}
	    }
	}



	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("(")) &&
		     (b.toLowerCase().equals("and") ) ) {
		    logger.log(Level.FINE,"and follows (");
		    tocheck.removeElementAt(i);
		}
	    }
	}


	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("(")) &&
		     (b.toLowerCase().equals(")") ) ) {
		    logger.log(Level.FINE,") follows (");
		    tocheck.removeElementAt(i);
                    tocheck.removeElementAt(i+1);
		}
	    }
	}

	/**
	 *  we do not check this one
	 *
	 * for (int j=0; j < tocheck.size();j++) {
	 *   for (int i=0;i< tocheck.size() -1;i++) {
	 *	a = ((String) tocheck.elementAt(i));
	 *      b = ((String) tocheck.elementAt(i+1));
	 *	a = a.trim();
	 *	b = b.trim();
	 *	if ( (a.toLowerCase().equals(")")) &&
	 *	     (b.toLowerCase().equals("and") ) ) {
	 *	    logger.log(Level.FINE,"and follows )");
	 *	    tocheck.removeElementAt(i);
	 *	}
	 *   }
	 * }
	 **/

	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("and")) &&
		     (b.toLowerCase().equals(")") ) ) {
		    logger.log(Level.FINE,") follows and");
		    tocheck.removeElementAt(i);
		}
	    }
	}


	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("or")) &&
		     (b.toLowerCase().startsWith(")") ) ) {
		    logger.log(Level.FINE,") follows or");
		    tocheck.removeElementAt(i);
		}
	    }
	}


	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size() -1;i++) {
		a = ((String) tocheck.elementAt(i));
		b = ((String) tocheck.elementAt(i+1));
		a = a.trim();
		b = b.trim();
		if ( (a.toLowerCase().equals("or")) &&
		     (b.toLowerCase().equals("or ") ) ) {
		    logger.log(Level.FINE,"or follows or");
		    tocheck.removeElementAt(i);
		}
	    }
	}


	for (int j=0; j < tocheck.size();j++) {
	    for (int i=0;i< tocheck.size();i++) {
		a = ((String) tocheck.elementAt(i));
		a = a.trim();
		if ( a.toLowerCase().equals("()")) {
		    logger.log(Level.FINE,"line contains ()");
		    tocheck.removeElementAt(i);
		}
	    }
	}


	// Rule 4
	boolean todo=true;
	try {
	    while (todo) {
		if (
		    ( ((String) tocheck.elementAt(tocheck.size()-1)).toLowerCase().trim().equals("where") ) ||
		    ( ((String) tocheck.elementAt(tocheck.size()-1)).toLowerCase().trim().equals("and") )   ||
		    ( ((String) tocheck.elementAt(tocheck.size()-1)).toLowerCase().trim().equals("or") )    ||
		    ( ((String) tocheck.elementAt(tocheck.size()-1)).toLowerCase().trim().equals("()") )    ||
		    ( ((String) tocheck.elementAt(tocheck.size()-1)).toLowerCase().trim().equals("order") ) ) {
		    logger.log(Level.FINE,"Cleaning up last line");
		    tocheck.removeElementAt(tocheck.size()-1);
		} else {
		    todo = false;
		}
	    }
	} catch ( Exception e) {
	    logger.log(Level.FINEST,"query seems to be empty");
	}

	checked = tocheck;
    }

    private String balanceParenthesis(String s) {
    	String rightChecked = RightParenthesisChecker(s);
    	String leftChecked = LeftParenthesisChecker(rightChecked);
    	return (leftChecked);
    }

	private String RightParenthesisChecker( String s ){
		int x = 0;
		int z = 0;
		StringBuffer b = new StringBuffer(s);
		char pB = '(';
		char pE = ')';

		for (int i=0; i < b.length(); i++ ) {
			if ( b.charAt(i) == pB ) {
				x++;
			}
			if ( b.charAt(i) == pE ) {
				x--;
			}
			if (x < 0) {
				b.setCharAt(i,' ');
				x=0;
			}

		}
		return b.toString();
	}
	private String LeftParenthesisChecker( String s ){
		int x = 0;
		int z = 0;
		StringBuffer b = new StringBuffer(s);
		char pB = '(';
		char pE = ')';

		for (int i = b.length()-1; i >= 0 ; --i ) {
			if ( b.charAt(i) == pE ) {
				x++;
			}
			if ( b.charAt(i) == pB ) {
				x--;
			}
			if (x < 0) {
				b.setCharAt(i,' ');
				x=0;
			}
		}
		return b.toString();
	}



    public QueryChecker() {

    }

}

