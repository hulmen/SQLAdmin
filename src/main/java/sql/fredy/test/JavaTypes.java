package sql.fredy.test;

import java.sql.*;

public class JavaTypes {

    public JavaTypes() {

	System.out.println("ARRAY                   \t" +  Types.ARRAY + "\n\t" +         
                          "BIGINT                  \t" +  Types.BIGINT + "\n\t" +  
                          "BINARY                  \t" +  Types.BINARY + "\n\t" +                 
                          "BIT                     \t" +  Types.BIT + "\n\t" +                 
                          "BLOB                    \t" +  Types.BLOB + "\n\t" +                 
                          "BOOLEAN                 \t" +  Types.BOOLEAN + "\n\t" +                                      
                          "CHAR                    \t" +  Types.CHAR + "\n\t" + 
			  "CLOB                    \t" +  Types.CLOB + "\n\t" +         
                          "DATALINK                \t" +  Types.DATALINK + "\n\t" +                 
                          "DATE                    \t" +  Types.DATE + "\n\t" +                 
                          "DECIMAL                 \t" +  Types.DECIMAL + "\n\t" +         
                          "DISTINCT                \t" +  Types.DISTINCT + "\n\t" +                 
                          "DOUBLE                  \t" +  Types.DOUBLE + "\n\t" +    
                          "FLOAT                   \t" +  Types.FLOAT + "\n\t" +                 
                          "INTEGER                 \t" +  Types.INTEGER + "\n\t" +                 
                          "JAVA_OBJECT             \t" +  Types.JAVA_OBJECT + "\n\t" +                 
                          "LONGNVARCHAR            \t" +  Types.LONGNVARCHAR + "\n\t" +                 
                          "LONGVARBINARY           \t" +  Types.LONGVARBINARY + "\n\t" +                         
                          "LONGVARCHAR             \t" +  Types.LONGVARCHAR + "\n\t" +    
                          "NCHAR                   \t" +  Types.NCHAR + "\n\t" + 
                          "NCLOB                   \t" +  Types.NCLOB + "\n\t" +         
                          "NULL                    \t" +  Types.NULL + "\n\t" +         
                          "NUMERIC                 \t" +  Types.NUMERIC + "\n\t" +         
                          "NVARCHAR                \t" +  Types.NVARCHAR + "\n\t" +         
                          "OTHER                   \t" +  Types.OTHER + "\n\t" +         
                          "REAL                    \t" +  Types.REAL + "\n\t" +                                   
                          "REF                     \t" +  Types.REF + "\n\t" +         
                          "REF_CURSOR              \t" +  Types.REF_CURSOR + "\n\t" +         
                          "ROWID                   \t" +  Types.ROWID + "\n\t" +         
                          "SMALLINT                \t" +  Types.SMALLINT + "\n\t" +         
                          "SQLXML                  \t" +  Types.SQLXML + "\n\t" +         
                          "STRUCT                  \t" +  Types.STRUCT + "\n\t" +         
                          "TIME                    \t" +  Types.TIME + "\n\t" +         
                          "TIMESTAMP               \t" +  Types.TIMESTAMP + "\n\t" +         
                          "TIMESTAMP WITH TIMEZONE \t" +  Types.TIMESTAMP_WITH_TIMEZONE + "\n\t" +         
                          "TIME WITH TIMEZONME     \t" +  Types.TIME_WITH_TIMEZONE + "\n\t" +         
                          "TINYINT                 \t" +  Types.TINYINT + "\n\t" +         
                          "VARBINARY               \t" +  Types.VARBINARY + "\n\t" +         
                          "VARCHAR                 \t" +  Types.VARCHAR );

    }

    public static void main(String a[]) {

	JavaTypes t = new JavaTypes();

    }
}
