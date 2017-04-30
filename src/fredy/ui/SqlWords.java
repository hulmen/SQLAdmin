  
/**
 * This Class is part of Fredy's SQL-Admin Tool.
 *
 * Admin is a Tool around JDBC-enabled SQL-Databases to do basic jobs for
 * DB-Administrations, like: - create/ drop tables - create indices - perform
 * sql-statements - simple form - a guided query - Data Export and a other
 * usefull things in DB-arena
 *
 * Admin (Version see below) Copyright (c) 1999 Fredy Fischer sql@hulmen.ch
 *
 * Fredy Fischer Hulmenweg 36 8405 Winterthur Switzerland
 *
 *
 * The icons used in this application are from Dean S. Jones
 *
 * Icons Copyright(C) 1998 by Dean S. Jones dean@gallant.com
 * www.gallant.com/icons.htm
 *
 * CalendarBean is Copyright (c) by Kai Toedter
 *
 * MSeries is Copyright (c) by Martin Newstead
 *
 * POI is from the Apache Foundation
 *
 *
 * Copyright (c) 2017 Fredy Fischer, sql@hulmen.ch
 *  
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package sql.fredy.ui;

import java.util.ArrayList;
import java.util.Iterator;


public class SqlWords {
    public ArrayList<String> sqlWords;
    public Iterator sqlWordsIterator;

    
    public SqlWords() {
        sqlWords = new ArrayList();
        sqlWords.add("ADD");
        sqlWords.add("ALL");
        sqlWords.add("ALLOCATE");
        sqlWords.add("ALTER");
        sqlWords.add("AND");
        sqlWords.add("ANY");
        sqlWords.add("ARE");
        sqlWords.add("ARRAY");
        sqlWords.add("AS");
        sqlWords.add("ASENSITIVE");
        sqlWords.add("ASYMMETRIC");
        sqlWords.add("AT");
        sqlWords.add("ATOMIC");
        sqlWords.add("AUTHORIZATION");
        sqlWords.add("BEGIN");
        sqlWords.add("BETWEEN");
        sqlWords.add("BIGINT");
        sqlWords.add("BINARY");
        sqlWords.add("BLOB");
        sqlWords.add("BOOLEAN");
        sqlWords.add("BOTH");
        sqlWords.add("BY");
        sqlWords.add("CALL");
        sqlWords.add("CALLED");
        sqlWords.add("CASCADED");
        sqlWords.add("CASE");
        sqlWords.add("CAST");
        sqlWords.add("CHAR");
        sqlWords.add("CHARACTER");
        sqlWords.add("CHECK");
        sqlWords.add("CLOB");
        sqlWords.add("CLOSE");
        sqlWords.add("COLLATE");
        sqlWords.add("COLUMN");
        sqlWords.add("COMMIT");
        sqlWords.add("CONDITION");
        sqlWords.add("CONNECT");
        sqlWords.add("CONSTRAINT");
        sqlWords.add("CONTINUE");
        sqlWords.add("CORRESPONDING");
        sqlWords.add("CREATE");
        sqlWords.add("CROSS");
        sqlWords.add("CUBE");
        sqlWords.add("CURRENT");
        sqlWords.add("CURRENT_DATE");
        sqlWords.add("CURRENT_DEFAULT_TRANSFORM_GROUP");
        sqlWords.add("CURRENT_PATH");
        sqlWords.add("CURRENT_ROLE");
        sqlWords.add("CURRENT_TIME");
        sqlWords.add("CURRENT_TIMESTAMP");
        sqlWords.add("CURRENT_TRANSFORM_GROUP_FOR_TYPE");
        sqlWords.add("CURRENT_USER");
        sqlWords.add("CURSOR");
        sqlWords.add("CYCLE");
        sqlWords.add("DATE");
        sqlWords.add("DAY");
        sqlWords.add("DEALLOCATE");
        sqlWords.add("DEC");
        sqlWords.add("DECIMAL");
        sqlWords.add("DECLARE");
        sqlWords.add("DEFAULT");
        sqlWords.add("DELETE");
        sqlWords.add("DEREF");
        sqlWords.add("DESCRIBE");
        sqlWords.add("DETERMINISTIC");
        sqlWords.add("DISCONNECT");
        sqlWords.add("DISTINCT");
        sqlWords.add("DO");
        sqlWords.add("DOUBLE");
        sqlWords.add("DROP");
        sqlWords.add("DYNAMIC");
        sqlWords.add("EACH");
        sqlWords.add("ELEMENT");
        sqlWords.add("ELSE");
        sqlWords.add("ELSEIF");
        sqlWords.add("END");
        sqlWords.add("ESCAPE");
        sqlWords.add("EXCEPT");
        sqlWords.add("EXEC");
        sqlWords.add("EXECUTE");
        sqlWords.add("EXISTS");
        sqlWords.add("EXIT");
        sqlWords.add("EXTERNAL");
        sqlWords.add("FALSE");
        sqlWords.add("FETCH");
        sqlWords.add("FILTER");
        sqlWords.add("FLOAT");
        sqlWords.add("FOR");
        sqlWords.add("FOREIGN");
        sqlWords.add("FREE");
        sqlWords.add("FROM");
        sqlWords.add("FULL");
        sqlWords.add("FUNCTION");
        sqlWords.add("GET");
        sqlWords.add("GLOBAL");
        sqlWords.add("GRANT");
        sqlWords.add("GROUP");
        sqlWords.add("GROUPING");
        sqlWords.add("HANDLER");
        sqlWords.add("HAVING");
        sqlWords.add("HOLD");
        sqlWords.add("HOUR");
        sqlWords.add("IDENTITY");
        sqlWords.add("IF");
        sqlWords.add("IMMEDIATE");
        sqlWords.add("IN");
        sqlWords.add("INDICATOR");
        sqlWords.add("INNER");
        sqlWords.add("INOUT");
        sqlWords.add("INPUT");
        sqlWords.add("INSENSITIVE");
        sqlWords.add("INSERT");
        sqlWords.add("INT");
        sqlWords.add("INTEGER");
        sqlWords.add("INTERSECT");
        sqlWords.add("INTERVAL");
        sqlWords.add("INTO");
        sqlWords.add("IS");
        sqlWords.add("ITERATE");
        sqlWords.add("JOIN");
        sqlWords.add("LANGUAGE");
        sqlWords.add("LARGE");
        sqlWords.add("LATERAL");
        sqlWords.add("LEADING");
        sqlWords.add("LEAVE");
        sqlWords.add("LEFT");
        sqlWords.add("LIKE");
        sqlWords.add("LOCAL");
        sqlWords.add("LOCALTIME");
        sqlWords.add("LOCALTIMESTAMP");
        sqlWords.add("LOOP");
        sqlWords.add("MATCH");
        sqlWords.add("MEMBER");
        sqlWords.add("MERGE");
        sqlWords.add("METHOD");
        sqlWords.add("MINUTE");
        sqlWords.add("MODIFIES");
        sqlWords.add("MODULE");
        sqlWords.add("MONTH");
        sqlWords.add("MULTISET");
        sqlWords.add("NATIONAL");
        sqlWords.add("NATURAL");
        sqlWords.add("NCHAR");
        sqlWords.add("NCLOB");
        sqlWords.add("NEW");
        sqlWords.add("NO");
        sqlWords.add("NONE");
        sqlWords.add("NOT");
        sqlWords.add("NULL");
        sqlWords.add("NUMERIC");
        sqlWords.add("OF");
        sqlWords.add("OLD");
        sqlWords.add("ON");
        sqlWords.add("ONLY");
        sqlWords.add("OPEN");
        sqlWords.add("OR");
        sqlWords.add("ORDER");
        sqlWords.add("OUT");
        sqlWords.add("OUTER");
        sqlWords.add("OUTPUT");
        sqlWords.add("OVER");
        sqlWords.add("OVERLAPS");
        sqlWords.add("PARAMETER");
        sqlWords.add("PARTITION");
        sqlWords.add("PRECISION");
        sqlWords.add("PREPARE");
        sqlWords.add("PRIMARY");
        sqlWords.add("PROCEDURE");
        sqlWords.add("RANGE");
        sqlWords.add("READS");
        sqlWords.add("REAL");
        sqlWords.add("RECURSIVE");
        sqlWords.add("REF");
        sqlWords.add("REFERENCES");
        sqlWords.add("REFERENCING");
        sqlWords.add("RELEASE");
        sqlWords.add("REPEAT");
        sqlWords.add("RESIGNAL");
        sqlWords.add("RESULT");
        sqlWords.add("RETURN");
        sqlWords.add("RETURNS");
        sqlWords.add("REVOKE");
        sqlWords.add("RIGHT");
        sqlWords.add("ROLLBACK");
        sqlWords.add("ROLLUP");
        sqlWords.add("ROW");
        sqlWords.add("ROWS");
        sqlWords.add("SAVEPOINT");
        sqlWords.add("SCOPE");
        sqlWords.add("SCROLL");
        sqlWords.add("SEARCH");
        sqlWords.add("SECOND");
        sqlWords.add("SELECT");
        sqlWords.add("SENSITIVE");
        sqlWords.add("SESSION_USER");
        sqlWords.add("SET");
        sqlWords.add("SIGNAL");
        sqlWords.add("SIMILAR");
        sqlWords.add("SMALLINT");
        sqlWords.add("SOME");
        sqlWords.add("SPECIFIC");
        sqlWords.add("SPECIFICTYPE");
        sqlWords.add("SQL");
        sqlWords.add("SQLEXCEPTION");
        sqlWords.add("SQLSTATE");
        sqlWords.add("SQLWARNING");
        sqlWords.add("START");
        sqlWords.add("STATIC");
        sqlWords.add("SUBMULTISET");
        sqlWords.add("SYMMETRIC");
        sqlWords.add("SYSTEM");
        sqlWords.add("SYSTEM_USER");
        sqlWords.add("TABLE");
        sqlWords.add("TABLESAMPLE");
        sqlWords.add("THEN");
        sqlWords.add("TIME");
        sqlWords.add("TIMESTAMP");
        sqlWords.add("TIMEZONE_HOUR");
        sqlWords.add("TIMEZONE_MINUTE");
        sqlWords.add("TO");
        sqlWords.add("TRAILING");
        sqlWords.add("TRANSLATION");
        sqlWords.add("TREAT");
        sqlWords.add("TRIGGER");
        sqlWords.add("TRUE");
        sqlWords.add("UNDO");
        sqlWords.add("UNION");
        sqlWords.add("UNIQUE");
        sqlWords.add("UNKNOWN");
        sqlWords.add("UNNEST");
        sqlWords.add("UNTIL");
        sqlWords.add("UPDATE");
        sqlWords.add("USER");
        sqlWords.add("USING");
        sqlWords.add("VALUE");
        sqlWords.add("VALUES");
        sqlWords.add("VARCHAR");
        sqlWords.add("VARYING");
        sqlWords.add("WHEN");
        sqlWords.add("WHENEVER");
        sqlWords.add("WHERE");
        sqlWords.add("WHILE");
        sqlWords.add("WINDOW");
        sqlWords.add("WITH");
        sqlWords.add("WITHIN");
        sqlWords.add("WITHOUT");
        sqlWords.add("YEAR");
        sqlWordsIterator = sqlWords.iterator();
    }
}
