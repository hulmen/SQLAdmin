package applications.basics;

/**

		       Fredy Fischer
		       Hulmenweg 36
		       8405 Winterthur
		       Switzerland

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
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;
import java.awt.event.*;

public class ImageMenuItem extends JMenuItem {
 
    public ImageMenuItem() {

    }

public ImageMenuItem(String text, String image, String toolTip) {
	if ( image != null ) {
                ImageIcon img = getImageIcon(image);
		if ( img != null ) {
		    this.setIcon(grayed(img.getImage()));
		    this.setRolloverIcon(img);
		    this.setRolloverEnabled(true);
		}
	}
	if (text != null ) this.setText(text);
	if (toolTip != null ) this.setToolTipText(toolTip);
}


    public String getInfo() {


	return "This is a ImageMenuItem\n" +
               "done for Fredy's Java-things\n" +
	       "parameters are: text-on-ITem,image,toolTip-Text\n" +
	       "it loads Images either from the device.images\n" +
	       "where it resides or from the property given over by \n" +
	       "-D image=<path-to-images>";


    }

    public ImageIcon getImageIcon(String image) {

	ImageIcon img = null;

	if ( image != null ) {
	    LoadImage li = new LoadImage(image);
            img = li.getImage(image);
	}
	return img;

    }


    public ImageIcon grayed(Image orig) {
	ImageFilter filter = new GrayFilter();
	ImageProducer producer = new FilteredImageSource(orig.getSource(), filter);
	ImageIcon imgIcon = new ImageIcon( createImage(producer) ) ;
	return imgIcon;
    }    

}
/**
class GrayFilter extends RGBImageFilter {
    public GrayFilter() { canFilterIndexColorModel = true; }
    public int filterRGB(int x, int y, int rgb) {
	int a = rgb & 0xff000000;
	int r = (((rgb & 0xff0000) + 0x1800000)/3) & 0xff0000;
	int g = (((rgb & 0x00ff00) + 0x018000)/3) & 0x00ff00;
	int b = (((rgb & 0x0000ff) + 0x000180)/3) & 0x0000ff;
	return a | r | g | b;
    }
}
**/
