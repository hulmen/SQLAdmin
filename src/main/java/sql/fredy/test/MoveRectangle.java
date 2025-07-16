/*
 * MoveRectangle.java
 *
 * Created on December 15, 2004, 11:09 AM
 *
 * This software is part of the Admin-Framework 

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
 */

package sql.fredy.test;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

/**
 *
 * @author sql@hulmen.ch
 */
public class MoveRectangle  extends JPanel implements MouseListener, MouseMotionListener{
    
    final static Color bg = Color.yellow;
    final static Color fg = Color.black;
    final static Color red = Color.red;
    final static Color white = Color.white;
    
    final static BasicStroke stroke = new BasicStroke(2.0f);
    final static BasicStroke wideStroke = new BasicStroke(8.0f);
    
    private int startX = 10;
    private int startY = 10;
    
    private Rectangle2D.Double rectangle;
    private Rectangle2D.Double rectangle2;
    
    private int rectWidth  = 80;
    private int rectHeight = 110;
    
    private int startX2 = 100;
    private int startY2 = 130;
    
    private int oldX=0;
    private int oldY=0;
    private int oldWidth=0;
    private int oldHeight=0;
    
    // mit diesen beiden Werten wird die neue Position berechnet
    private int mouseX = 0;
    private int mouseY = 0;
    
    private int mouseX2 = 0;
    private int mouseY2 = 0;
    
    /** Creates a new instance of MoveRectangle */
    public MoveRectangle() {
        this.setBackground(bg);
        this.setForeground(fg);
        this.setPreferredSize(new Dimension(600,400));
        this.setLayout(new BorderLayout());
        addMouseMotionListener(this);
        addMouseListener(this);
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        paint(g);
    }
    
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Dimension d = this.getSize();
        
        g2.setPaint(bg);
        g2.clearRect(0,0,d.width,d.height);
        
        Color fg3D = Color.lightGray;
        
        g2.setPaint(fg3D);
        g2.draw3DRect(0, 0, d.width - 1, d.height - 1, true);
        
        g2.setPaint(fg);
        g2.draw3DRect(3, 3, d.width - 7, d.height - 7, false);
        
        
        fg3D = Color.blue;
        g2.setPaint(fg3D);
        g2.setStroke(stroke);
        
        if ( startX < 3 ) startX = 3;
        if ( startY < 3 ) startY = 3;
        
        if ( startX > (d.width - rectWidth))   startX = d.width -rectWidth;
        if ( startY > (d.height - rectHeight)) startY = d.height - rectHeight;
        
        if ( startX2 < 3 ) startX2 = 3;
        if ( startY2 < 3 ) startY2 = 3;
        
        if ( startX2 > (d.width - rectWidth))   startX2 = d.width -rectWidth;
        if ( startY2 > (d.height - rectHeight)) startY2 = d.height - rectHeight;
        
        g2.setColor(new Color(115,208,255));
        rectangle = new Rectangle2D.Double(startX, startY, rectWidth, rectHeight);
        rectangle2= new Rectangle2D.Double(startX2, startY2, rectWidth, rectHeight);
        
        //g2.draw(rectangle );
        g2.fill(rectangle );
        
        g2.setColor(new Color(0,0,194));
        g2.fill(rectangle2);
        
        // und jetzt die Linie zwischen beiden Rechtecken
        g2.setColor(Color.BLACK);
        int lx1 = startX;
        if ( rectangle2.x > rectangle.x ) lx1 = startX  + rectWidth;
        
        int lx2 = startX2;
        if ( rectangle.x > rectangle2.x ) lx2 = startX2 + rectWidth;
        
        
        g2.draw(new Line2D.Double(lx1,startY + rectHeight/2,lx2,startY2 + rectHeight/2));
        
    }
    
    
    public void mouseClicked(MouseEvent e) {
        //System.out.println("mouseClicked X=" + e.getX() + " Y=" + e.getY());
    }
    
    public void mouseDragged(MouseEvent e) {
        
        // verschiebe das Rechteck
        //System.out.println("mouseDragged X=" + e.getX() + " Y=" + e.getY());
        if ( (mouseX > 0) && ( mouseY > 0) ) {
            int diffX = e.getX() - mouseX;
            int diffY = e.getY() - mouseY;
            startX = startX + diffX;
            startY = startY + diffY;
            mouseX = e.getX();
            mouseY = e.getY();
            Dimension dim = this.getSize();
            this.repaint();
            
        }
        
        if ( (mouseX2 > 0) && ( mouseY2 > 0) ) {
            int diffX = e.getX() - mouseX2;
            int diffY = e.getY() - mouseY2;
            startX2 = startX2 + diffX;
            startY2 = startY2 + diffY;
            mouseX2 = e.getX();
            mouseY2 = e.getY();
            Dimension dim = this.getSize();
            this.repaint();            
        }
        
    }
    
    public void mouseEntered(MouseEvent e) {
        //System.out.println("mouseEntered X=" + e.getX() + " Y=" + e.getY());
        
    }
    
    public void mouseExited(MouseEvent e) {
        //System.out.println("mouseExited X=" + e.getX() + " Y=" + e.getY());
        
    }
    
    public void mouseMoved(MouseEvent e) {
        //System.out.println("mouseMoved X=" + e.getX() + " Y=" + e.getY());
        
    }
    
    public void mousePressed(MouseEvent e) {
        // hat gedrueckt, heisst, verschieben faengt an
        if ( rectangle.contains(e.getX(),e.getY()) ) {
            mouseX = e.getX();
            mouseY = e.getY();
        } else {
        if ( rectangle2.contains(e.getX(),e.getY()) ) {
            mouseX2 = e.getX();
            mouseY2 = e.getY();
        }
        }
    }
    
    public void mouseReleased(MouseEvent e) {
        //System.out.println("mouseReleased X=" + e.getX() + " Y=" + e.getY());
        
        // das verschieben ist fertig
        mouseX = 0;
        mouseY = 0;
        mouseX2 = 0;
        mouseY2 = 0;
    }
    public static void main(java.lang.String[] args) {
        MoveRectangle gt = new MoveRectangle();
        JFrame frame = new JFrame("Bewegen");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(BorderLayout.CENTER,gt);
        frame.pack();
        frame.setVisible(true);
    }
}
