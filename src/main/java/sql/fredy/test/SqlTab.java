package sql.fredy.test;

import javax.swing.*;
import java.awt.*;

public interface SqlTab
{
	public String getSQLError();	
	public int getNumRows();
	public String getQuery();
	public void setViewPortSize(Dimension d);
}
