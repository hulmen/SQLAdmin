package applications.basics;

/**

  just a test, to see the values of these types

**/


import java.util.*;
import java.sql.*;

public class SqlTypes {

    public SqlTypes() {


       System.out.println("java.sql.Types.CHAR :" + java.sql.Types.CHAR );
       System.out.println("java.sql.Types.VARCHAR :" + java.sql.Types.VARCHAR );
       System.out.println("java.sql.Types.BINARY :" + java.sql.Types.BINARY );
       System.out.println("java.sql.Types.LONGVARBINARY :" + java.sql.Types.LONGVARBINARY );
       System.out.println("java.sql.Types.VARBINARY :" + java.sql.Types.VARBINARY );
       System.out.println("java.sql.Types.TIME :" + java.sql.Types.TIME );
       System.out.println("java.sql.Types.DATE :" + java.sql.Types.DATE );
       System.out.println("java.sql.Types.TIMESTAMP :" + java.sql.Types.TIMESTAMP );
       System.out.println("java.sql.Types.INTEGER :" + java.sql.Types.INTEGER );
       System.out.println("java.sql.Types.NUMERIC :" + java.sql.Types.NUMERIC );
       System.out.println("java.sql.Types.DOUBLE :" + java.sql.Types.DOUBLE );
       System.out.println("java.sql.Types.FLOAT :" + java.sql.Types.FLOAT );
       System.out.println("java.sql.Types.BIGINT :" + java.sql.Types.BIGINT );
       System.out.println("java.sql.Types.BIT :" + java.sql.Types.BIT );
       System.out.println("java.sql.Types.BLOB :" + java.sql.Types.BLOB );
       System.out.println("java.sql.Types.CLOB :" + java.sql.Types.CLOB );
       System.out.println("java.sql.Types.DECIMAL :" + java.sql.Types.DECIMAL );
    }

    public static void main(String args[]) {
	SqlTypes s = new SqlTypes();
    }
}
