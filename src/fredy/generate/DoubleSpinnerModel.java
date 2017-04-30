

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
*
*
*   extended by Fredy for DoubleValues
*
*/
package sql.fredy.generate;

import mseries.ui.*;
import javax.swing.event.*;

/**
*   Model for MSpinner to manage Double. The range of parameters are expressed in the 
*   constructors
*/
public class DoubleSpinnerModel extends mseries.ui.DefaultSpinnerModel
{
    private double value=0;
    private double val=0;
    /** The default minimum value */
    protected double min=Double.MIN_VALUE;
    /** The default maximum value */
    protected double max=Double.MAX_VALUE;
    private boolean hasMin;
    private boolean hasMax;
    protected double step=1;
    private boolean roll=false;

    public DoubleSpinnerModel()
    {
    }

    /**
    *   Constructor
    *   @param start the initial value
    *   @param max the maximum value
    *   @param min the minimum value
    *   @param step the amount to increment/decrement the value by when 
    *   getNextValue/getPreviousValue are executed
    *   @param roll true if the value rolls over the maximum back to the minimum
    */
    public DoubleSpinnerModel(Double start, Comparable max, Comparable min, double step, boolean roll)
    {
        setMaximum(max);
        setMinimum(min);
        this.step=step;
        this.roll=roll;
        setValue(start);
    }

    /**
    *   Constructor
    *   @param start the initial value
    *   @param max the maximum value
    *   @param min the minimum value
    *   @param step the amount to increment/decrement the value by when 
    *   getNextValue/getPreviousValue are executed
    *   @param roll true if the value rolls over the maximum back to the minimum
    */
    public DoubleSpinnerModel(double start, double max, double min, double step, boolean roll)
    {
        setMaximum(max);
        setMinimum(min);
        this.step=step;
        this.roll=roll;
        setValue(start);
    }
    /**
    *   Doesn't need to do anything for Integers, the step is set when the 
    *   model is constructed.
    */
    public void setStep(int step)
    {
    }
    /**
    *   Returns the current value of the field
    *   @return the current value of the field
    */
    public Object getValue() 
    {
        return new Double(value);
    }

    /**
    *   Sets the value
    *   @param newValue the new value
    */
    public void setValue(double newValue)
    {
        if (newValue >= min && newValue<=max)
        {
            value=newValue;
            notifyListeners();
        }
    }
    /**
    *   Sets the value
    *   @param newValue the new value
    */
    public void setValue(Object newValue)
    {
        setValue(((Double)newValue).doubleValue());
    }

    /**
    *   Advances and returns the current value in the sequence according to the step, maximum
    *   value and roll attribute
    *   @return the next value
    */
    public Object getNextValue()
    {
        val=((Double)getValue()).doubleValue();
        Double l = new Double(val+step);
        Double m=(Double)getMaximum();
        Double ml=new Double(m.doubleValue());
        if (ml.compareTo(l) < 0)
        {
            if (roll)
            {
                val=min;
            }
        }
        else
        {
            val+=step;
        }
        setValue(new Double((double)val));
        return getValue();
    }

    /**
    *   Retracts and returns the current value in the sequence according to the step, minimum
    *   value and roll attribute
    *   @return the next value
    */
    public Object getPreviousValue()
    {
        val=((Double)getValue()).doubleValue();

        Double l= new Double(val-step);
        Double m=(Double)getMinimum();
        Double ml=new Double(m.doubleValue());
        if (ml.compareTo(l) > 0)
        {
            if (roll)
            {
                val=max;
            }
        }
        else
        {
            val-=step;
        }
        setValue(new Double((double)val));
        return getValue();
    }

    /**
    *   Used to force a minimum value when the field is decremented using the down button
    *   @param min the minimum value
    */
    public void setMinimum(Comparable min)
    {
        if (min instanceof java.lang.Double)
        {
            hasMin=true;
            Double x = (Double)min;
            this.min=x.doubleValue();
        }
        notifyListeners();
    }

    /**
    *   Used to force a minimum value when the field is decremented using the down button
    *   @param min the minimum value
    */
    public void setMinimum(double min)
    {
        setMinimum(new Double(min));
    }
    /**
    *   Returns the current minimum value
    *   @return The current minimum value
    */
    public Comparable getMinimum()
    {
        return new Double(this.min);
    }
    
    /**
     * Used to force a maximum value when the field is incremented using the up button
     * @param max the maximum value
     */
    public void setMaximum(Comparable max)
    {
        if (max instanceof java.lang.Double)
        {
            hasMax=true;
            Double x = (Double)max;
            this.max=x.doubleValue();
        }
        notifyListeners();
    }

    /**
     * Used to force a maximum value when the field is incremented using the up button
     * @param max the maximum value
     */
    public void setMaximum(double max)
    {
        setMaximum(new Double(max));
    }
    /**
     *  Returns the current maximum value
     *  @return The current maximum value
     */
    public Comparable getMaximum()
    {
        return new Double(this.max);
    }
}
