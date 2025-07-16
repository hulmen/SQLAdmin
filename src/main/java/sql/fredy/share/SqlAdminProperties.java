/*
 * The MIT License
 *
 * Copyright 2019 fredy.
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
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package sql.fredy.share;


import java.util.logging.Level;
import java.util.logging.Logger;
//import org.apache.commons.configuration2.XMLConfiguration;
//import org.apache.commons.configuration2.builder.fluent.Configurations;
//import org.apache.commons.configuration2.ex.ConfigurationException;

/**
 *
 * @author Fredy Fischer, 2019-04-25 SqlAdminProperties shall handle all
 * properties shared within SQLAdmin. it espcially covers the login parameters
 * The properties-file will be stored and loade from in one of these locations
 * in this order: if the environment variable admin.work is set from this
 * location from the user.home directory
 *
 */
public class SqlAdminProperties {

  //  private Configurations configs;
  //  private XMLConfiguration config;
    private Logger logger = Logger.getLogger("sql.fredy.share");

    private static final String FILENAME = "adminproperties.xml";

    public SqlAdminProperties() {
/*
        configs = new Configurations();
        
        // we first load the configuration file
        String directory = null;
        directory = System.getProperty("admin.work");
        if ( directory == null ) {
            directory = System.getProperty("user.home");
        }
        try {
            config = configs.xml(directory + System.getProperty("file.separator") + FILENAME);
        } catch (ConfigurationException cex) {
            logger.log(Level.SEVERE, "Exception while reading configuration {0}", cex.getMessage());
        }
  */      
    }

}
