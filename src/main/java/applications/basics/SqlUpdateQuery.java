package applications.basics;

/**

   This is to send SQL-Update-Statements 
   to a RDBMS


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

import java.sql.*;


public class SqlUpdateQuery {

    boolean debug = false; 
    String host;
    
    /**
       * Get the value of host.
       * @return Value of host.
       */
    public String getHost() {return host;}
    
    /**
       * Set the value of host.
       * @param v  Value to assign to host.
       */
    public void setHost(String  v) {this.host = v;}
    
    
    String user;
    
    /**
       * Get the value of user.
       * @return Value of user.
       */
    public String getUser() {return user;}
    
    /**
       * Set the value of user.
       * @param v  Value to assign to user.
       */
    public void setUser(String  v) {this.user = v;}
    
    
    String password;
    
    /**
       * Get the value of password.
       * @return Value of password.
       */
    public String getPassword() {return password;}
    
    /**
       * Set the value of password.
       * @param v  Value to assign to password.
       */
    public void setPassword(String  v) {this.password = v;}
    
    
    String database;
    
    /**
       * Get the value of database.
       * @return Value of database.
       */
    public String getDatabase() {return database;}
    
    /**
       * Set the value of database.
       * @param v  Value to assign to database.
       */
    public void setDatabase(String  v) {this.database = v;}
    
    
    String query;
    
    /**
       * Get the value of query.
       * @return Value of query.
       */
    public String getQuery() {return query;}
    
    /**
       * Set the value of query.
       * @param v  Value to assign to query.
       */
    public void setQuery(String  v) {this.query = v;}

    String error=null;
    public void setError(String v) { error = v; }
    public String getError() { return error; }

    
    int lastInsertId=0;
    
    /**
       * Get the value of lastInsertId.
       * @return Value of lastInsertId.
       */
    public int getLastInsertId() {return lastInsertId;}
    
    /**
       * Set the value of lastInsertId.
       * @param v  Value to assign to lastInsertId.
       */
    public void setLastInsertId(int  v) {this.lastInsertId = v;}
    
    
    public SqlUpdateQuery(String host, 
			  String user, 
			  String password, 
			  String database, 
			  String query) {

	setHost(host);
	setUser(user);
	setPassword(password);
	setDatabase(database);
	setQuery(query);

	t_connect c = new t_connect(getHost(), getUser(), getPassword(), getDatabase());
	if (c.getError() != null) {
	    message("Connection Error: "+ c.getError());
	}	

	try {
            setError(null);
	    int records = c.stmt.executeUpdate(getQuery());
            ResultSet sqlresult = c.stmt.executeQuery("select last_insert_id()");
	    sqlresult.next();
	    if (sqlresult != null) setLastInsertId(sqlresult.getInt(1));
	    c.close(); 	
	} catch (Exception e) {
 	    setError(e.getMessage().toString());
	    message(e.getMessage().toString() );
	}
    }

    private void message(String m) { 
      if (debug ) System.out.println("Message from SqlUpdateQuery: \n" + m + "\nQuery was: \n" + getQuery()); 
    }


    public static void main(String args[] ) {

	if (args.length != 5) {
	    System.out.println("Syntax: java applications.basics.SqlUpdateQuery host user password database query");
            System.out.println("\n..and you gave " + args.length + " parameters");
	    System.exit(0);
	}
	SqlUpdateQuery s = new SqlUpdateQuery(args[0],args[1],args[2],args[3],args[4] );
	System.exit(0);	   

    }
}
