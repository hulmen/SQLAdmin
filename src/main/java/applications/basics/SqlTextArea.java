package applications.basics;

/**

 This is part of David Good's contribution

**/


import java.awt.*;
import java.sql.*;
import java.util.*;
import javax.swing.*;
import java.lang.reflect.*;


public class SqlTextArea extends JPanel implements SqlTab
{
	public JTextArea TextArea = new JTextArea(20,80);
	private String query;
	String szError;
	int iNumRows = 0;
	t_connect con;
	JScrollPane scrollpane;
		
	public SqlTextArea()
	{}
	
	
	public SqlTextArea (String host, String user, String password,String db, String query)
	{
		setLayout(new FlowLayout());
		this.query = query;
		TextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
		con = new t_connect(host,user,password,db);
		execQuery(query);
		scrollpane = new JScrollPane(TextArea);
		add(scrollpane);
	}
	
	public void setViewPortSize(Dimension d)
	{
		//scrollpane.setPreferredSize(d);
	}
	
	private void execQuery(String query)
	{
		try
		{
			ResultSet rs = con.stmt.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
						
			SqlResults s = new SqlResults(rs,rsmd,rsmd.getColumnCount());
			s.loadText(TextArea);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public String getSQLError()
	{
		return szError;
	}

	public int getNumRows()
	{
		return iNumRows;
	}
	
	public String getQuery()
	{
		return query;
	}
	
	class SqlColumn extends LinkedList
	{
		int maxLength = 0;
		
		SqlColumn(String colName, int width)
		{
			super();
			maxLength = width;
			add(colName);
			char[] underline = new char[colName.length()];
			Arrays.fill(underline,'-');
			add(new String(underline));
		}
		public Object get(int index)
		{
			String szData = (super.get(index)).toString();
			
			char[] filler = new char[maxLength - szData.length()];
			Arrays.fill(filler,' ');
			
			return szData + new String(filler);
			
		}
		
		public boolean add(Object o)
		{
			super.add(o);
			if (o.toString().length() >= maxLength)
				maxLength = o.toString().length()+1;
				
			return true;
		}
	}
	
	class SqlResults extends Vector
	{
		private int size;
		
		SqlResults(ResultSet rs, ResultSetMetaData rsmd,int size)
		{
			super(size);
			this.size = size;
			
			try
			{
				for (int y = 1;y <= size ;y++)
					add(new SqlColumn(rsmd.getColumnName(y),rsmd.getColumnDisplaySize(y)));
				
				while(rs.next())
				{
					for (int y = 0;y < size ;y++)
					{
						if (rs.getObject(y+1) == null)
							((SqlColumn)get(y)).add("null");
						else
							((SqlColumn)get(y)).add(rs.getObject(y+1));
						
					}
					iNumRows++;
				}
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		void loadText(JTextArea TextArea)
		{
			int listLength = iNumRows + 2;//takes account of row header
						
			for (int i = 0;i < listLength;i++)
			{
				for (int j = 0;j < size; j++)
				{
					TextArea.append((((SqlColumn)get(j)).get(i)).toString());
				}
				TextArea.append("\n");
			}
		}
	}	
}
