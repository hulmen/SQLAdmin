package sql.fredy.test;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author a_tkfir
 */
public class DerbyTest {

    private DerbyDataSource derby;
    private Logger logger = Logger.getLogger("sql.fredy.test");

    public DerbyTest() {
        Connection con = null;
        try {
            /*
         get the derby-things without starting up the server
             */
            derby = DerbyDataSource.getInstance();

            con = derby.getConnection();

            SQLWarning connectionWarning = con.getWarnings();
            if (connectionWarning != null) {
                logger.log(Level.INFO, "Connection Warning: {0} {1}", new Object[]{connectionWarning.getMessage(), connectionWarning.getSQLState()});
            }

            // now we try to get MetaData
            DatabaseMetaData dmd = con.getMetaData();
            logger.log(Level.INFO, "DB Product Name: {0} {1} Driver: {2} {3}", new Object[]{dmd.getDatabaseProductName(), dmd.getDatabaseProductVersion(), dmd.getDriverName(), dmd.getDriverVersion()});

            // Test something
            ResultSet rs = con.createStatement().executeQuery("Show tables");
            while (rs.next()) {
                System.out.println(rs.getString(1));
            }
            rs.close();

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Derby is not running " + sqlex.getMessage());
            sqlex.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(DerbyTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(DerbyTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        String s = readFromPrompt("Stop ?", "j");
        try {
            con.close();
            

        } catch (SQLException sqlex) {
            logger.log(Level.WARNING, "Derby is not running " + sqlex.getMessage());
        }

        derby.stop();

    }

    /* read from commandline
     *
     * 1st parameter as displaytext
     * 2nd parameter as defaultvalue
     */
    public static String readFromPrompt(String text, String defValue) {
        String fromPrompt = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(text + " (Default: " + defValue + ") ");
        try {
            fromPrompt = br.readLine();
            if (fromPrompt.length() < 1) {
                fromPrompt = defValue;
            }
        } catch (IOException ioe) {
            fromPrompt = defValue;
        }
        return fromPrompt;
    }

    public static void main(String[] args) {
        DerbyTest derbyTest = new DerbyTest();
    }
}
