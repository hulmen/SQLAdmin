/*
 * Look - it's a disclaimer!
 *
 * Copyright (c) 1995 Widget Workshop, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL or COMMERCIAL purposes and
 * without fee is hereby granted. 
 * 
 * WIDGET WORKSHOP MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. WIDGET WORKSHOP SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * THIS SOFTWARE IS NOT DESIGNED OR INTENDED FOR USE OR RESALE AS ON-LINE
 * CONTROL EQUIPMENT IN HAZARDOUS ENVIRONMENTS REQUIRING FAIL-SAFE
 * PERFORMANCE, SUCH AS IN THE OPERATION OF NUCLEAR FACILITIES, AIRCRAFT
 * NAVIGATION OR COMMUNICATION SYSTEMS, AIR TRAFFIC CONTROL, DIRECT LIFE
 * SUPPORT MACHINES, OR WEAPONS SYSTEMS, IN WHICH THE FAILURE OF THE
 * SOFTWARE COULD LEAD DIRECTLY TO DEATH, PERSONAL INJURY, OR SEVERE
 * PHYSICAL OR ENVIRONMENTAL DAMAGE ("HIGH RISK ACTIVITIES").  WIDGET WORKSHOP
 * SPECIFICALLY DISCLAIMS ANY EXPRESS OR IMPLIED WARRANTY OF FITNESS FOR
 * HIGH RISK ACTIVITIES.
 */
/* What won't those crazy lawyers think up next? */

package sql.fredy.datadrill;


/**
  I reuse this to display Database states
  Many thanks

  Fredy Fischer
  **/


import java.awt.*;
import java.awt.image.*;

/**
 * "Gauge" is a simple control, used to display a range of values
 * using a "meter" format.
 * <P>
 * Accessible Attributes:
 * <PRE>
 * MinimumValue      Low end of the scale
 * MaximumValue      High end of the scale
 * CurrentValue      Current setting (where the Pointer points)
 * CriticalThreshold Percentage of MaxValue at which we start drawing using the Critical color
 * WarningThreshold  Percentage of MaxValue at which we start drawing using the Warning color
 * Legend            What we're measuring (e.g., Speed)
 * Units             What units we're using (e.g., MPH)
 * </PRE>
 * <P>
 * Attributes not yet publically accessible (next version, perhaps):
 * <PRE>
 * MinimumAngle  Angle at which Minimum Value will be drawn
 * MaximumAngle  Angle at which Maximum Value will be drawn
 * Margin        Distance between the Scale and the Component's bounds() rect
 * Separation    Distance between Gauge subobjects (e.g, between Pointer and Scale)
 * CriticalColor Color used to draw the Critical Section of the Scale(e.g., Color.red)
 * WarningColor  Color used to draw the Warning Section of the Scale
 * NormalColor   Color used to draw the Normal Section of the Scale
 * InternalColor Color used to draw the interior of the Scale
 * PenRadius     Used to determine thickness of Pointer and Scale
 * Draw3D        Should the background be drawn as a raised rectangle?
 * TickSize      How long (in pixels) is a BigTick?
 * BigTick       Interval starting at Minimum at which BigTicks will be drawn
 * LittleTick    Interval starting at Minimum at which LittleTicks will be drawn
 * </PRE>
 * <P>
 * @author	Grant R. Gainey
 * @version	1.0 03-JAN-1996
 *
 */

public class Gauge extends Canvas {
	/*
	 * Base attributes
	 */

	// Publically settable:
	private int 		minVal, maxVal, currVal;
	private double 		warnPercent, critPercent;
	private String 		legend;
	private String 		units;

	// Still internal-only:
	private Color 		warnColor, critColor, internalColor, normalColor;
	private int		separation = 5;
	private int 		margin = separation;
	private double 		minAngleRads = Math.PI;	// 180 degrees
	private double 		maxAngleRads = 0.0;	// zero degrees
	private int		penRadius = 1;
	private boolean		draw3D = true;
	private int		tickSize = 8;
	private int		bigTick = 10;
	private int		littleTick = 5;

	/*
	 * Computed attributes (derived from Base or elsewhere)
	 */
	private int		warnVal, critVal;
	private String 		maxStr, minStr;
	private Dimension 	minSize, maxSize, legendSize, unitsSize;
	private Point 		pivotLoc;
	private int   		pointerLen;
	private int 		minAngleDegs, maxAngleDegs;
	private Rectangle 	scaleRect;
	private FontMetrics 	myMetrics = null;
	private int		halfTick = (int)Math.round(tickSize/2.0);

	/**
	 * Constructs a Gauge object. Uses all default values.  
	 * @see Gauge#init
	 */
	public Gauge() {
		init();
	};

	/**
	 * Constructs a Gauge object with specified Min and Max. 
	 * @param min Minimum Gauge value
	 * @param max Maximum Gauge Value
	 * @see Gauge#init
	 */
	public Gauge(int min, int max) {
		init();
		setMinimum(min);
		setMaximum(max);
		setCurrent(min);
	};

	/**
	 * Constructs a Gauge with the specified Min, Max, Legend, and Units values.
	 * @param min		Minimum Value
	 * @param max		Maximum Value
	 * @param warn		Percentage of Max of start of Warning area
	 * @param crit		Percentage of Max of start of Critical area
	 * @param newLegend		Gauge Legend
	 * @param newUnits		Gauge Units
	 */
	public Gauge(int min, int max, float warn, float crit, String newLegend, String newUnits) {
		init();
		setMinimum(min);
		setMaximum(max);
		setCurrent(min);

		warnPercent = warn;
		critPercent = crit;
		warnVal = (int)Math.round(maxVal*warnPercent);
		critVal = (int)Math.round(maxVal*critPercent);

		setWarningThreshold(warn);
		setCriticalThreshold(crit);		

		setLegend(newLegend);
		setUnits(newUnits);
	};

	/*
	 * Utility Routines
	 */

	// Degrees-to-Radians
	private double d2r (int degs) {
		return Math.round((degs/180)*Math.PI);
	};

	// Radians-to-Degrees
	private int r2d (double rads) {
		return (int)Math.round((rads/Math.PI)*180);
	};

	// Based on current font, determine dimensions of all strings.
	// Reset PointerLen here.
	private void setStringMetrics() {
		if (myMetrics==null) return;

		minSize = new Dimension(myMetrics.stringWidth(minStr),
					myMetrics.getHeight());
		maxSize = new Dimension(myMetrics.stringWidth(maxStr),
					myMetrics.getHeight());
		legendSize = new Dimension(myMetrics.stringWidth(legend),
					   myMetrics.getHeight());
		unitsSize = new Dimension(myMetrics.stringWidth(units),
					   myMetrics.getHeight());
		margin += myMetrics.getHeight();
		pointerLen = (int)Math.round(size().width/2.0) - margin - separation;
	};

	// Return rect into which the scale-arc will be drawn
	private Rectangle findScaleRect() {
		return new Rectangle(margin, pivotLoc.y - pointerLen, 2*pointerLen, pointerLen);
	};

	// Determine locations of Pivot and Scale, and len of Pointer
	// Called after any size-change
	private void partition() {
		pivotLoc = new Point( size().width/2, size().height/2 );
		pointerLen = (int)Math.round(size().width/2.0) - margin - separation;
		scaleRect = findScaleRect();
	};

	/**
	 * init() sets Guage's attributes to a "sane" set of initial values.
	 * <P>
	 * Defaults are:
	 * <PRE>
	 *  Minimum         = 0
	 *  Maximum         = 200
	 *  Current         = Minimum
	 *  Legend          = "Velocity"
	 *  Units           = "KPH"
	 *  WarningThresh   = 0.7
	 *  CriticalThresh  = 0.9
	 *  CriticalColor   = Color.red
	 *  WarningColor    = Color.yellow
	 *  InternalColor   = Color.black
	 *  NormalColor     = getForeground()
	 *  ForegroundColor = Color.white
	 *  BackgroundColor = Color.gray
	 *  Font            = "Courier", BOLD, 18
	 *  </PRE>
	 */
	protected void init() {
		minVal = 0;
		maxVal = 200;
		currVal = minVal;

		warnPercent = 0.7;
		critPercent = 0.9;
		warnVal = (int)Math.round(maxVal*warnPercent);
		critVal = (int)Math.round(maxVal*critPercent);

		legend = new String("");
		units = new String("");
		minStr = String.valueOf(minVal);
		maxStr = String.valueOf(maxVal);

		setStringMetrics();

		warnColor = Color.yellow;
		critColor = Color.red;
		internalColor = Color.black;

		partition();
		minAngleDegs = r2d(minAngleRads);
		maxAngleDegs = r2d(maxAngleRads);

		setForeground(Color.white);
		normalColor = getForeground();
		setBackground(Color.gray);
		setFont(new Font("Courier", Font.PLAIN, 10));
	};

	/**
	 * Change the Gauge's Legend (what this Gauge is measuring; e.g., Speed or Swap Rate)
	 * @param newLegend	Legend's new value
	 */
	public void setLegend(String newLegend) {
		legend = newLegend;
		if (myMetrics!=null) {
			legendSize = new Dimension(myMetrics.stringWidth(legend),
					   	   myMetrics.getHeight());
		}
		repaint();
	};

	/**
	 * Change the Gauge's Units (what Value is measured in; e.g., KPH or PPM)
	 * @param newUnits	Units's new value
	 */
	public void setUnits(String newUnits) {
		units = newUnits;
		if (myMetrics!=null) {
			unitsSize = new Dimension(myMetrics.stringWidth(units),
					  	  myMetrics.getHeight());
		}
		repaint();
	};

	/**
	 * Change the Gauge's Minimum Value 
	 * @param min New Minimum
	 */
	public void setMinimum(int min) {
		minVal = min;
		minStr = String.valueOf(minVal);
		if (myMetrics!=null) {
			minSize = new Dimension(myMetrics.stringWidth(minStr),
						myMetrics.getHeight());
		}
		repaint();
	};

	public int getMinimum() {
		return minVal;
	};

	/**
	 * Change the Gauge's Maximum Value 
	 * @param max New Maximum
	 */
	public void setMaximum(int max) {
		maxVal = max;
		maxStr = String.valueOf(maxVal);
		if (myMetrics!=null) {
			maxSize = new Dimension(myMetrics.stringWidth(maxStr),
						myMetrics.getHeight());
		}
		warnVal = (int)Math.round(maxVal*warnPercent);
		critVal = (int)Math.round(maxVal*critPercent);
		repaint();
	};

	public int getMaximum() {
		return maxVal;
	};

	/**
	 * Change the Gauge's Current Value (the value the pointer points at)
	 * @param newCurr New Current
	 */
	public void setCurrent(int newCurr) {
		currVal = newCurr;
		repaint();
	};

	public int getCurrent() {
		return currVal;
	};

	/**
	 * Change the Gauge's Warning Threshold.  This is the percentage of the Maximum Value
	 * at which the Warning Region begins.
	 * @param newWarnPercnt	New Warning Threshold
	 */
	public void setWarningThreshold(float newWarnPcnt) {
		warnPercent = newWarnPcnt;
		warnVal = (int)Math.round(maxVal*warnPercent);
		repaint();
	};

	public double getWarningThreshold() {
		return warnPercent;
	};

	/**
	 * Change the Gauge's Critical Threshold.  This is the percentage of the Maximum Value
	 * at which the Critical Region begins.
	 * @param newCritPercnt	New Critical Threshold
	 */
	public void setCriticalThreshold(float newCritPcnt) {
		critPercent = newCritPcnt;
		critVal = (int)Math.round(maxVal*critPercent);
		repaint();
	};

	public double getCriticalThreshold() {
		return critPercent;
	};

	/**
	 * At update(); draw into an off-screen image and put up the whole
	 * image.  This clears up any flickering.
	 */
	public void update(Graphics g) {
		Image osImg = createImage(getSize().width, getSize().height);
		osImg.getGraphics().fillRect(0,0,getSize().width, getSize().height);
		paint(osImg.getGraphics());
		g.drawImage(osImg,0,0,null);
	};

	/**
	 * If Draw3d is set, draw the background as a filled 3D rect in the
	 * current Background Color.  Otherwise, draw a flat rect.
	 */
	public void drawBackground(Graphics g) {
		g.setColor(getBackground());

		if (draw3D) {
			for (int i=0;i<=3;i++) {
				g.fill3DRect(i,i,getSize().width-(2*i), getSize().height-(2*i),true);
			}
		} else {
			g.fillRect(0,0, getSize().width, getSize().height);
		}
	};

	/**
	 * drawTick draws one tick-mark from center of scale-arc _INWARDS_ 
	 * @param where		Value at which to draw the tick-mark
	 * @param offset		How long to make the tick-mark
	 */
	public void drawTick(Graphics g, int where, int offset) {
		int arcCenter = pointerLen+separation; 
		Point startPt = mapValToPoint(where, arcCenter-offset);
		Point endPt   = mapValToPoint(where, arcCenter);
		g.setColor(getValColor(where));
		g.drawLine(startPt.x, startPt.y, endPt.x, endPt.y);
	};

	/**
	 * drawScale draws the ScaleArc itself, and the tick-marks
	 */
	public void drawScale(Graphics g) {

		// Draw the meter-interior.  Then draw the Arc itself.  Then, draw tickmarks.
		g.setColor(internalColor);
		g.fillArc(margin, margin, 
			  getSize().width - 2*margin, getSize().height - 2*margin,
			  maxAngleDegs, (minAngleDegs-maxAngleDegs));

		for (int i=-penRadius;i<=penRadius;i++) {
			// Draw the critical-color segment
			g.setColor(critColor);
			g.drawArc(margin+i, margin+i, 
				  getSize().width - 2*margin - 2*i, getSize().height - 2*margin - 2*i,
				  maxAngleDegs, 
				  (int)Math.round(minAngleDegs*(1.0-critPercent)));

			// Draw the warning-color segment
			g.setColor(warnColor);
			g.drawArc(margin+i, margin+i, 
				  getSize().width - 2*margin - 2*i, getSize().height - 2*margin - 2*i,
				  (int)Math.round(minAngleDegs*(1.0-critPercent)), 
				  (int)Math.round(minAngleDegs*(critPercent-warnPercent)));

			// Draw the normal-color segment
			g.setColor(normalColor);
			g.drawArc(margin+i, margin+i, 
				  getSize().width - 2*margin - 2*i, getSize().height - 2*margin - 2*i,
				  minAngleDegs, 
				  (int)Math.round(minAngleDegs*(-warnPercent)));
		}

		for (int i=minVal;i<=maxVal;i+=littleTick) {drawTick(g,i,halfTick);}
		for (int i=minVal;i<=maxVal;i+=bigTick)    {drawTick(g,i,tickSize);}
	};

	/**
	 * drawStrings draws the Legend, Units, Min, and Max.
	 */
	public void drawStrings(Graphics g) {
		g.setColor(getForeground());
		g.setFont(getFont());
		g.drawString(minStr, separation, pivotLoc.y + minSize.height);
		g.drawString(maxStr, location().x+getSize().width - 2*separation - maxSize.width,
			     pivotLoc.y + minSize.height);
		g.drawString(legend, 
			     pivotLoc.x - (int)Math.round(legendSize.width/2), 
			     pivotLoc.y + legendSize.height + margin);
		g.drawString(units, 
			     pivotLoc.x - (int)Math.round(unitsSize.width/2), 
			     pivotLoc.y + legendSize.height+margin + unitsSize.height+separation);
	};

	/**
	 * getValColor returns the color associated with the region into which the
	 * value falls.
	 *	@param val		value of interest
	 */
	public Color getValColor(int val) {
		if (val < warnVal) {
			return normalColor;
		} else if (val < critVal) {
			return warnColor;
		} else {
			return critColor;
		}
	};

	/**
	 * mapValToPoint returns a Point on the Scale for the specified value
	 */
	public Point mapValToPoint(int val) {
		double diffRads = minAngleRads - maxAngleRads;
		double valPcnt  = (double)val/(double)maxVal;
		double valRads  = minAngleRads - diffRads*valPcnt;
		return new Point(pivotLoc.x + (int)Math.round(Math.cos(valRads)*pointerLen), 
				 pivotLoc.y - (int)Math.round(Math.sin(valRads)*pointerLen));
	};

	/**
	 * mapValToPoint returns a Point "radius" units from the Pivit for the specified value
	 */
	public Point mapValToPoint(int val, int radius) {
		double diffRads = minAngleRads - maxAngleRads;
		double valPcnt  = (double)val/(double)maxVal;
		double valRads  = minAngleRads - diffRads*valPcnt;
		return new Point(pivotLoc.x + (int)Math.round(Math.cos(valRads)*radius), 
				 pivotLoc.y - (int)Math.round(Math.sin(valRads)*radius));
	};

	/**
	 * drawPointer draws the Pivot and the Pointer
	 */
	public void drawPointer(Graphics g) {
		g.setColor(getValColor(currVal));
		g.fillOval(pivotLoc.x-5, pivotLoc.y-5, 11, 11);
		Point scalePt = mapValToPoint(currVal);
		for (int i=-penRadius;i<=penRadius;i++) {
			g.drawLine(pivotLoc.x, pivotLoc.y+i, scalePt.x, scalePt.y+i);
		}
	};

	/**
	 * When resized, recalculate where everything lives.
	 * <P>
	 * Rules for resizing a Gauge:
	 * <P>
	 *  <UL>
	 *  <LI>Gauge MUST be square; take the smaller of w/h as new side-length
	 *  <LI>Gauge MUST be at least its min-size
	 *  </UL>
	 */
	public void setSize(int w, int h) {
		int side = Math.max(Math.min(w,h), minimumSize().width);
		super.setSize(side, side);
		partition();
		repaint();
	};

	/**
	 * @see Gauge#resize(int,int)
	 */
	public void setSize(Dimension dim) {
		int side = Math.max(Math.min(dim.width,dim.height), minimumSize().width);
		dim.width = side; dim.height = side;
		super.setSize(dim);
		partition();
		repaint();
	};

	/**
	 * @see Gauge#resize(int,int)
	 */
	public void reshape(int x, int y, int w, int h) {
		int side = Math.max(Math.min(w,h), minimumSize().width);
		super.reshape(x,y,side,side);
		partition();
		repaint();
	};

	/**
	 * A Gauge wants to be no smaller than (100,100)
	 */
	public Dimension minimumSize() {
		return new Dimension(150,150);
	};

	/**
	 * Gauges prefer their minSize.
	 * @see Gauge#minimumSize
	 */
	public Dimension preferredSize() {
		return minimumSize();
	};

	/**
	 * paint() paints in this order:
	 * <OL>
	 * <LI>Background (includes Internal area)
	 * <LI>Scale (includes TickMarks)
	 * <LI>Strings (Legend, Units, Min, Max)
	 * <LI>Pointer (inlcudes Pivot)
	 * </OL>
	 */
	public void paint(Graphics g) {
		if (myMetrics==null) {
			myMetrics = getGraphics().getFontMetrics();
			setStringMetrics();
		}

		//Image osImg = createImage(getSize().width, getSize().height);
		//Graphics osGfx = osImg.getGraphics();
		drawBackground(g);
		drawScale(g);
		drawStrings(g);
		drawPointer(g);

		//g.drawImage(osImg, 0, 0, this);
//System.out.println(this.toString());
	};

	public void repaint() {
		super.repaint();
	};

	public void repaint(long when) {
		super.repaint(when);
	};

	public void repaint(int x, int y, int w, int h) {
		super.repaint(x,y,w,h);
	};

	public void repaint(long when, int x, int y, int w, int h) {
		super.repaint(when,x,y,w,h);
	};

	public boolean mouseDown(Event evt, int x, int y) {
		return super.mouseDown(evt,x,y);
	};

	public boolean mouseDrag(Event evt, int x, int y) {
		return super.mouseDrag(evt,x,y);
	};

	public boolean mouseUp(Event evt, int x, int y) {
		return super.mouseUp(evt,x,y);
	}

	public String toString() {
                String strBuf = getClass().getName();
		strBuf += "Rect: " +bounds().toString()+ '\n';
		strBuf += "Min : " +minVal+ " Max: " +maxVal+ " Curr: " +currVal+ '\n';
		strBuf += "wP  : " +warnPercent+ " cP:" + critPercent+ '\n';
		strBuf += "wV  : " +warnVal+ " cV:" + critVal+ '\n';
		strBuf += "Lgnd: " +legend+'\n';
		strBuf += "Unit: " +units+'\n';
		strBuf += "MnAR: " +minAngleRads+ " MnAD: " +minAngleDegs+ '\n';
		strBuf += "MxAR: " +maxAngleRads+ " MxAD: " +maxAngleDegs+ '\n';
		strBuf += "PivotLoc: " +pivotLoc.toString()+ '\n';
		strBuf += "PtrLen: " +pointerLen+ '\n';
		strBuf += "SclRect : " +scaleRect.toString()+ '\n';
		return strBuf;
	}
}

