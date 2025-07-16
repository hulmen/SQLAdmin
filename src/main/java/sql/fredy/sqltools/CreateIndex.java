package sql.fredy.sqltools;

/** Create Index: Create Index as a part of Admin
 *  Version 1.01 25. May 1999
 *          1.1  08. Dec 2002
 *  Fredy Fischer
 *
 *  this has been created to Create Indices on SQL-Databases
 *  it only returns a JPanel, so it can easily been 
 *  used in different kind of windows
 */

/** 
    Admin is a Tool around JDBC-enabled SQL-RDBMS to do basic jobs
    for DB-Administrations, like:
    - SQL-Monitor to sned statements to the RDBMS
    - create/ drop tables
    - create  indices
    - perform sql-statements
    - simple form
    - a guided query
    - create Java Code
    - export Data

    and a other usefull things in DB-arena

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

import sql.fredy.ui.CloseableFrame;
import sql.fredy.ui.ImageButton;
import sql.fredy.ui.Tables;
import sql.fredy.metadata.TableRows;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.logging.*;
import javax.swing.*;
import javax.swing.BorderFactory; 
import javax.swing.JLabel;
import javax.swing.JPanel; 
import sql.fredy.connection.DataSource;


public class CreateIndex extends JPanel {



// Fredy's make Version
private static String fredysVersion = "Version 2.1  08. Dec. 2002";

public String getVersion() {return fredysVersion; }


    private Logger logger;
    private boolean connected=false;
    private TableRows tableRows;
    private JTextArea query;
    private JTextField index;
    private JCheckBox unique;
    private ImageButton create,add;
  
    private Tables table;
    private JPanel workPanel,mainPanel;
    public String getTable() { return table.getSelectedValue().toString(); }
    public String getIndex() { return index.getText(); }

    /** to find out, when the user wants to close
     *  this application, set a listener onto (JButton)CreateIndex.cancel 
     **/
    public ImageButton cancel;

    /**
     * The Connection from the Connection Pool.
     *
     * @return the Connection to the DB.
     */
    public Connection getConnection() {

        Connection con = null;
        try {
            con = DataSource.getInstance().getConnection();
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IO Exception while creating connection  {0}", ex.getMessage());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Exception while creating connection  {0}", ex.getMessage());
        } catch (PropertyVetoException ex) {
            logger.log(Level.SEVERE, "Property Veto Exception while creating connection  {0}", ex.getMessage());
        } finally {
            return con;
        }

    }


    public String getQuery() { return query.getText(); }
    
    /**
       * Get the value of host.
       * @return Value of host.
       */

    private String host;
    public String getHost() {return host;}
    
    /**
       * Set the value of host.
       * @param v  Value to assign to host.
       */
    public void setHost(String  v) {this.host = v;}
    



    private String user;
    
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
    

    private String password;
    
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


    private String database;
    
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


    
    String schema;
    
    /**
       * Get the value of schema.
       * @return Value of schema.
       */
    public String getSchema() {return schema;}
    
    /**
       * Set the value of schema.
       * @param v  Value to assign to schema.
       */
    public void setSchema(String  v) {this.schema = v;}
    


    String dbTable;
    
    /**
       * Get the value of the Table.
       * @return Value of the Table.
       */
    public String getDbTable() {return dbTable;}
    
    /**
       * Set the value of dbTable.
       * @param v  Value to assign to dbTable.
       */
    public void setDbTable(String  v) {this.dbTable = v;}
    
    private String uni() {
	if ( unique.isSelected() ) {
	    return "UNIQUE";
	} else {
	    return " ";
	}
    }

    private JPanel mainPanel() {

	mainPanel = new JPanel();
	mainPanel.setLayout(new BorderLayout());

	mainPanel.add("West",tables());

	workPanel = rows();
	mainPanel.add("Center",workPanel);
	mainPanel.add("South",buttons());

	return mainPanel;
    }


    private void doUpdate() {

	mainPanel.remove(workPanel);
	workPanel = rows();
	mainPanel.add("Center",workPanel);
	mainPanel.updateUI();

    }


    
    private JPanel tables() {
	
	JPanel panel = new JPanel();
	panel.setLayout(new GridBagLayout());
	
	table = new Tables(getSchema());
	table.setSelectedIndex(0);
        // set the Listener onto the table-List
        MouseListener mouseListener = new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
         if (e.getClickCount() == 1) {
	     doUpdate();
	 }
         if (e.getClickCount() == 2) {
	     doUpdate();
	 }
        }};
        table.addMouseListener(mouseListener);        


	JScrollPane scp = new JScrollPane(table);
        panel.setBorder(BorderFactory.createEtchedBorder());


        GridBagConstraints gbc;
	Insets insets = new Insets(1,1,1,1);
	gbc = new GridBagConstraints();
    	gbc.insets = insets;

       	gbc.anchor= GridBagConstraints.NORTHWEST;
	gbc.fill  = GridBagConstraints.BOTH;
	gbc.weightx = 1.0; 
	gbc.weighty = 1.0;
	gbc.gridx = 0;
	gbc.gridy = 0;	

	panel.add(scp,gbc);
	return panel;
    }

    private JPanel buttons() {
	JPanel panel = new JPanel();
	panel.setLayout(new FlowLayout());
        panel.setBorder(BorderFactory.createEtchedBorder());
	
	create = new ImageButton("Create","updatecolumn.gif","Create this index");
	//create.setToolTipText("Create this index");
	create.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		execQuery();
		}});
	

	cancel = new ImageButton("Cancel","exit.gif","Exit");
       
	panel.add(create);
	panel.add(cancel);

	return panel;
    }
	

    private JPanel rows() {

	JPanel panel = new JPanel();
	panel.setLayout(new BorderLayout());

	JPanel panel3 = new JPanel();
	panel3.setLayout(new GridBagLayout());
        panel3.setBorder(BorderFactory.createEtchedBorder());


	add = new ImageButton(null,"vcrforward.gif","add selected column to index");
	add.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		String s = getQuery();
		if (s.charAt(s.length()-2) != '(' ) query.append(",");
		query.append(tableRows.getSelectedValue().toString() + " ");
		tableRows.removeElement(tableRows.getSelectedIndex());
		query.updateUI();
		}});


	tableRows = new TableRows(getTable());
	tableRows.setEnabled(false);
	JScrollPane scp = new JScrollPane(tableRows);
	
	query = new JTextArea(5,20);
	query.setWrapStyleWord(true);
	query.setLineWrap(true);
	query.setEditable(true);

	JScrollPane queryPane = new JScrollPane(query);

	JPanel panel2 = new JPanel();
	panel2.setLayout(new FlowLayout());
	
	panel2.add(new JLabel("Indexname: "));
	index = new JTextField(20);
	index.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		if (getIndex().length() > 1) {
		    tableRows.setEnabled(true);
		    //query.setText("alter table " + getTable() + " add index ");
		    query.setText("create " + uni() + " index " + getIndex() + " on " + getTable() + " ( ");
		    // query.append(getIndex() + " ( ");
		    
		}
		}});
	

	panel2.add(index);

	unique = new JCheckBox("Unique");
	unique.setSelected(false);
	panel2.add(unique);


        GridBagConstraints gbc;
	Insets insets = new Insets(1,1,1,1);
	gbc = new GridBagConstraints();
    	gbc.insets = insets;

       	gbc.anchor= GridBagConstraints.NORTHWEST;
	gbc.fill  = GridBagConstraints.BOTH;
	gbc.gridx = 0;
	gbc.gridy = 0;

	panel3.add(scp,gbc);

	gbc.fill  = GridBagConstraints.NONE;
      	gbc.anchor= GridBagConstraints.CENTER;
	gbc.gridx = 1;
	gbc.gridy = 0;
	panel3.add(add,gbc);

	gbc.fill  = GridBagConstraints.BOTH;
	gbc.gridx = 2;
	gbc.gridy = 0;
	gbc.weightx = 1.0; 
	gbc.weighty = 1.0;
	panel3.add(queryPane,gbc);

	panel.add("North",panel2);
	panel.add("Center",panel3);

	return panel;
    }
	
    private void message(String msg) {
	logger.log(Level.INFO,"User: " + getUser() + " " + msg);
	JOptionPane.showMessageDialog(null, msg,"Message",JOptionPane.WARNING_MESSAGE);

    }


    private void execQuery() {

	if ( ! getQuery().endsWith(")") )query.append(")");
        query.updateUI();
        Connection con = null;
        Statement stmt = null;
	try {
            con = getConnection();
            stmt = con.createStatement();
	    int records = stmt.executeUpdate(getQuery());
	    message("index successfully created");
	    logger.log(Level.INFO,"User " + getUser() + " successful SQ	L-Cmd: " + getQuery());
	} catch (SQLException excpt) {
		     message("Could not create Index:\n"+excpt.getMessage().toString());
	} finally {
            if ( stmt != null ) {
                try {
                    stmt.close();
                } catch (SQLException e ) {
                    logger.log(Level.WARNING,"Exception on close Statement {0}", e.getMessage());
                }
            }
            
            if ( con != null ) {
                try {
                    con.close();
                } catch (SQLException e ) {
                    logger.log(Level.WARNING,"Exception on close Connection {0}", e.getMessage());
                }
            }
        }
    }



    public CreateIndex (String schema) {
	
	setSchema(schema);
        init();

    }

    private void init() {
	logger = Logger.getLogger("sql.fredy.admin");

	workPanel = new JPanel();

	this.setLayout(new BorderLayout());
	this.add("Center",mainPanel());

    }

    public static void main(String args[]) {

	String host     = "localhost";
	String user     = System.getProperty("user.name");
        String schema   = "%";
        String database = null;
        String password = null;

	System.out.println("CreateIndex\n" +
			   "-----------\n" +
			   "Syntax: java sql.fredy.sqltools.CreateIndex\n" +
			   "        Parameters: -s Schema (default: %)\n");

	int i = 0;
	while ( i < args.length) {
 
            if (args[i].equals("-s")) {
                i++;
                schema = args[i];
            }
	    i++;
	};

	if ( database == null) {
	    System.out.println("No database provided. Program stopped!");
	    System.exit(0);
	}


	CloseableFrame cf = new CloseableFrame("Fredy's Create Index");
        CreateIndex    ci = new CreateIndex(schema);
	cf.getContentPane().setLayout(new BorderLayout());
	cf.getContentPane().add(BorderLayout.CENTER,ci);
	ci.cancel.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    System.exit(0);
		}
	    });
	

        cf.pack();
	cf.setVisible(true);



    }

 
}
           
