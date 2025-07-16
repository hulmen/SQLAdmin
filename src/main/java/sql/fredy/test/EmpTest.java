package sql.fredy.test;

import java.io.*;

import java.net.URL;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;



public class EmpTest {

    public Statement stmt;
    public Connection con;

    String database = "/usr/local/empress/data/repairs";

    public EmpTest () {

	try {
	    Class.forName("empress.jdbc.clientDriver").newInstance();
	    String url = "jdbc:empress://localhost:8900/SERVER=localhost;PORT=6322;DATABASE=" +
		database;
	    	   
	    con = DriverManager.getConnection(url,"user","password");
	    stmt = con.createStatement();
	    System.out.println("Connection established\nURL: " + url);
	    con.close();
	} catch (Exception e) {
	    System.out.println("Exception: " + e.getMessage().toString() + "\n\n");
	    e.printStackTrace();
	}
	System.exit(0);
    }
    public static void main(String a[]) {
	EmpTest e = new EmpTest();
    }
}
