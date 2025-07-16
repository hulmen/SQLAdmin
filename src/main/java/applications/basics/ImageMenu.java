package applications.basics;

/**

sql@hulmen.ch

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
import java.util.logging.*;

public class ImageMenu extends JMenu {

    private Logger logger = Logger.getLogger("applications.basics");

    public ImageMenu() {
    }

    public ImageMenu(String text, String image, String toolTip) {
        try {
            if (image != null) {
                ImageIcon img = getImageIcon(image);
                this.setIcon(grayed(img.getImage()));
                this.setRolloverIcon(img);
                this.setRolloverEnabled(true);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "There is something wrong with the image: {0}", e.getMessage());
        }
        if (text != null) {
            this.setText(text);
        }
        if (toolTip != null) {
            this.setToolTipText(toolTip);
        }
    }

    public String getInfo() {


        return "This is a ImageMenu\n"
                + "done for Fredy's Java-things\n"
                + "parameters are: text-on-button,image,toolTip-Text\n"
                + "it loads Images either from the device.images\n"
                + "where it resides or from the property given over by \n"
                + "-D image=<path-to-images>";


    }

    public ImageIcon getImageIcon(String image) {

        ImageIcon img = new ImageIcon();

        if (image != null) {
            LoadImage li = new LoadImage(image);
            img = li.getImage(image);
        }
        return img;

    }

    public ImageIcon grayed(Image orig) {
        ImageFilter filter = new GrayFilter();
        ImageProducer producer = new FilteredImageSource(orig.getSource(), filter);
        ImageIcon imgIcon = new ImageIcon(createImage(producer));
        return imgIcon;
    }

    public static void main(String args[]) {

        System.out.println("Fredy's ImageMenu\n"
                + "is based on JMenu and does a Rollover-Image\n"
                + "use it as follows: java -D image=<path-to-images> applications.basics.ImageMenu <text> <image> <tooltip>\n");
        if (args.length != 3) {
            System.exit(0);
        }
        JFrame frame = new JFrame("TEST");
        ImageButton imgB = new ImageButton(args[0], args[1], args[2]);
        frame.getContentPane().add(imgB);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {

            public void windowActivated(WindowEvent e) {
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }

            public void windowDeactivated(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowOpened(WindowEvent e) {
            }
        });

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
