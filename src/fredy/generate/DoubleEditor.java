/*
*   Copyright (c) 2001 Martin Newstead (seth_brundell@bigfoot.com).  All Rights Reserved.
* 
*   The author makes no representations or warranties about the suitability of the
*   software, either express or implied, including but not limited to the
*   implied warranties of merchantability, fitness for a particular
*   purpose, or non-infringement. The author shall not be liable for any damages
*   suffered by licensee as a result of using, modifying or distributing
*   this software or its derivatives.
*
*   The author requests that he be notified of any application, applet, or other binary that 
*   makes use of this code and that some acknowedgement is given. Comments, questions and 
*   requests for change will be welcomed.
*/

/*
 * extended by Fredy for doubles
 */


package sql.fredy.generate;

import java.util.*;
import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import mseries.ui.*;

/**
*   A SpinnerEditor that renders integer (int)data.
*   @see mseries.ui.MIntegerSpinnerModel
*/
public class DoubleEditor extends DefaultSpinnerEditor 
{

    private NumberFormat nf;
    boolean setting=false;
    String format="zzzzzzz9.99";

    /**
    *   Default constructor that creates the editor with the default format of
    *   zzzzzzz9.99
    */
    public DoubleEditor()
    {
        init(format);
    }

    /**
    *   Constructor used to set a custom format. The notation used is based on that of PIC(9)
    *   fields in Cobol. It uses two symbols to describe how a character is represented. The
    *   total maximum width of the display in characters is the number of characters in the 
    *   format including the dot. The format expressed here is used to set the number of
    *   digits before and after the decimal point, a locale sensitive NumberFormat object is
    *   used to place commas, dots in the appropriate places.
    *<PRE>
    *   z - a zero value in this position is supressed
    *   9 - a place holder to determine the size of the field
    *
    *   For Examples with a value of 123.4 you would see 
    *
    *   zz9.99  - 123.40
    *   zzz9.99 - 123.40
    *   zzz9.9  - 123.4
    *   9999.99 - 0123.40
    *
    *</PRE>
    *   Of course a NumberFormat object can be set using the setFormatter method
    */
    public DoubleEditor(String pattern)
    {
        init(pattern);
    }

    private void init(String pattern)
    {
        nf=configureFormatter(pattern);
        display.setDocument(new IntegerDocument()); 
    }

    /**
    *   Get the value of the field
    *   @return the current value
    */
    public Object getValue()
    {
        Number retVal = new Double(0.0);

        try 
        {
            retVal = nf.parse(display.getText());
        } 
        catch (ParseException e) 
        {
            System.out.println(e);
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
        }
	long   l = ((Long)retVal).longValue();
	Long   L = new Long(l);
        double x = L.doubleValue();

        //double x = ((Double)retVal).doubleValue();
        return new Double(x);
    }

    /**
    *   Gets the textfield that allows user input
    *   @return the textfield configured to allow doubles to be entered 
    *   & spun
    */
    public JTextField getTextfield()
    {
        return display;
    }

    /**
    *   Sets the value in the field
    *   @param value the new value
    */
    public void setValue(Object value) 
    {
        setting=true;
        Double x;
        if (value instanceof java.lang.Double)
        {
            x = (Double)value;
            display.setText(nf.format(x.doubleValue()));
        }
        setting=false;
    }

    /**
    *   Sets the format for display using the
    *   @param format the display format,
    */
    public String getFormat()
    {
        return this.format;
    }

    /**
    *   Sets a custom formatter
    *   @param the formatter, 
    */
    public void setFormatter(NumberFormat formatter)
    {
        nf=formatter;
    }

    /*
    *   Sets the number of digits either side of the decimal point
    */
    private NumberFormat configureFormatter(String pattern)
    {
        NumberFormat nf = NumberFormat.getInstance();

        int minFD=0;
        int maxFD=0;
        int minID=0;
        int maxID=0;

        int i=0;
        int l=pattern.length();

        i = pattern.indexOf(".");
        maxID = (i > 0) ? i : l;
        
        maxFD=l-maxID-1;
        
        i=pattern.indexOf("9");
        minID=maxID-i;
        nf.setMaximumFractionDigits(maxFD);
        nf.setMinimumFractionDigits(maxFD);
        nf.setMaximumIntegerDigits(maxID);
        nf.setMinimumIntegerDigits(minID);
        
        return nf;
    }

    /**
    *   The 'magic' behind any custom text field. This document only allows [0..9]
    *   to be typed or pasted.
    */
    protected class IntegerDocument extends PlainDocument
    {
        // This method process the characters that were typed or pasted
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException
        {
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;

            for (int i = 0; i < result.length; i++) 
            {
                if (Character.isDigit(source[i]) || source[i]=='-' || source[i]=='.')
                {
                    result[j++] = source[i];
                }
            }
            if (setting || isEditable())
            {
                super.insertString(offs, new String(result, 0, j), a);
            }
        }

        public void remove(int offs, int len) throws BadLocationException
        {
            if (setting || isEditable())
            {
                super.remove(offs, len);
            }
        }
    }
}
