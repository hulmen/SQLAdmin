package applications.basics;

/**

   This is part of David Good's contribution


**/

import java.sql.*;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.tree.*;
import java.io.*;

public class SqlPickList extends JPanel
{
	private t_connect con;
	private DatabaseMetaData dmd;
	ResultSet rsTables;
	Vector vTables = new Vector(10,10);
	JTree jtTables;
	JButton cbPaste;
	Container jPanel1 = this;
	
	SqlPickList (String host, String user, String password,String db,ActionListener paste, String schema)
	{
		try
		{
			
			con = new t_connect(host,user,password,db);
			dmd = con.con.getMetaData();
			rsTables = dmd.getTables(null,schema,null,new String[]{"TABLE","VIEW"});

			getTableList(rsTables);

			jPanel1.setLayout (new BorderLayout());

			cbPaste = new JButton("Paste");
			cbPaste.addActionListener(paste);
			cbPaste.setActionCommand("");

			jPanel1.add(cbPaste,BorderLayout.SOUTH);

			jtTables = new JTree(vTables);
			
			jtTables.addMouseListener( new MouseAdapter() 
			{
				public void mousePressed(MouseEvent e) 
				{
					 TreePath selPath = jtTables.getPathForLocation(e.getX(), e.getY());
					 if(selPath != null) 
					 {
					 	
						cbPaste.setActionCommand(" " + ((PickListItem)((DefaultMutableTreeNode)(selPath.getLastPathComponent())).getUserObject()).getName() + " ");
						if(e.getClickCount() == 2) 
						{
							e.consume();
							cbPaste.doClick();
							
						}
					 }
				 }
			 });


			JScrollPane scp = new JScrollPane(jtTables);
			scp.setSize(100,80);

			jPanel1.add(scp);

			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void getTableList(ResultSet rs) throws Exception
	{
		while (rs.next())
			vTables.add(new ColumnList(rs.getString(3)));
	}
	
		
	public class ColumnList extends Vector implements PickListItem
	{
		private String tableName;
		
		public ColumnList(String TableName) throws Exception
		{
			super(10,5);
			tableName = TableName;
			ResultSet rs = dmd.getColumns(null,null,tableName,null);
			while (rs.next())
				add(new Column(rs.getString(4)));
		}
		
		public String toString()
		{
			return tableName;
		}
		
		public String getName()
		{
			return tableName;
		}
		
		public class Column implements PickListItem
		{	
			private String columnName;

			public Column (String columnName)
			{
				this.columnName = columnName;
			}

			public String toString()
			{
				return columnName;
			}

			public String getName()
			{
				return ColumnList.this.tableName + "." + columnName;
			}
		}		
	}
	
	
			
			
}			
	
