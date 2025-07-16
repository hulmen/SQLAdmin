package applications.basics;

/**

 It works, but is not used....

**/

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;


public class SliderField extends JSlider {


    
    String text;
    
    /**
       * Get the value of text.
       * @return Value of text.
       */
    public String getText() {
	return Integer.toString(this.getValue());
    }
    
    /**
       * Set the value of text.
       * @param v  Value to assign to text.
       */
    public void setText(String  v) {
	this.setValue(Integer.parseInt(v));
    }
    
    public SliderField(int orientation, int min, int max, int value){
	this.setOrientation(orientation);
	this.setMinimum(min);
	this.setMaximum(max);
	this.setValue(value);
    }


}
