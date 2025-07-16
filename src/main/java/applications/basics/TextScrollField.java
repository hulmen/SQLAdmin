package applications.basics;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


public class TextScrollField extends JPanel {

    JTextArea textArea;

    String content;
    
    /**
       * Get the value of content.
       * @return Value of content.
       */
    public String getContent() {return content;}
    
    /**
       * Set the value of content.
       * @param v  Value to assign to content.
       */
    public void setContent(String  v) {this.content = v;}
    
    
    int rows;
    
    /**
       * Get the value of rows.
       * @return Value of rows.
       */
    public int getRows() {return rows;}
    
    /**
       * Set the value of rows.
       * @param v  Value to assign to rows.
       */
    public void setRows(int  v) {
	this.rows = v;
	textArea.setRows(v);
    }
    
    
    
    int cols;
    
    /**
       * Get the value of cols.
       * @return Value of cols.
       */
    public int getCols() {return cols;}
    
    /**
       * Set the value of cols.
       * @param v  Value to assign to cols.
       */
    public void setCols(int  v) {
	this.cols = v;
	textArea.setColumns(v);
    }
    
    
   public  String text;
   
    /**
       * Get the value of text.
       * @return Value of text.
       */
    public String getText() {return textArea.getText();}
    
    /**
       * Set the value of text.
       * @param v  Value to assign to text.
       */
    public void setText(String  v) {textArea.setText(v);}
    
    private void initAll() {

	textArea = new JTextArea();
	textArea.setLineWrap(false);
	JScrollPane scrollPane = new JScrollPane(textArea);
	this.add(scrollPane);

    }


    public TextScrollField(int rows, int cols) {

	initAll();
	this.setRows(rows);
	this.setCols(cols);
	this.setText("");
    }


    public TextScrollField() {
	initAll();
	this.setRows(5);
	this.setCols(10);
	this.setText("");
    }
    
    public TextScrollField(int rows, int cols, String text) {
	initAll();
	this.setRows(rows);
	this.setCols(cols);
	this.setText(text);
    }
}
