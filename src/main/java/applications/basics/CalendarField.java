package applications.basics;

/**
 * CalendarField is a CalendarField based on JCalendard from Kai Toedter The
 * latest version of CalendarBean you find at
 * www.geocities.com/SiliconValley/2123/calendarbean it extends this to a
 * setText and a getText-Method to integrate it into Admin-functions.
 *
 *
 * Admin is a Tool around mySQL to do basic jobs for DB-Administrations, like: -
 * create/ drop tables - create indices - perform sql-statements - simple form -
 * a guided query and a other usefull things in DB-arena
 *
 * Copyright (c) 2017 Fredy Fischer, sql@hulmen.ch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 *
 *
 *
 *
 */
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.text.DateFormatter;

public class CalendarField extends JPanel {

    public void clear() {
        this.setDate(toDay());
    }

    /**
     * this is to set the date in the form yyyy-mm-dd
     *
     */
    Calendar cal;
    JFrame frame;

    public void setDate(String v) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = null;
        try {
            today = sdf.parse(v);
        } catch (ParseException ex) {
            System.out.println("CalendarField: " + v + " is not a valid date, using today");
            Calendar cal = Calendar.getInstance();
            today = cal.getTime();
        }

        dateSpinner.setValue(today);
    }

    public Calendar getCalendar() {
        return (Calendar) dateSpinner.getValue();
    }

    public String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(((Calendar) dateSpinner.getValue()).getTime());

    }

    private String trailing(String v) {
        if (v.length() < 2) {
            v = "0" + v;
        }
        return v;
    }

    private String toDay() {
        String y = Integer.toString(cal.get(Calendar.YEAR));
        String m = Integer.toString(cal.get(Calendar.MONTH) + 1);
        m = trailing(m);
        String d = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        d = trailing(d);

        return y + "-" + d + "-" + m;

    }

    JSpinner dateSpinner;

    public CalendarField(String v) {
        
        this.setLayout(new FlowLayout());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = null;
        try {
            today = sdf.parse(v);
        } catch (ParseException ex) {
            System.out.println("CalendarField: " + v + " is not a valid date, using today");
            Calendar cal = Calendar.getInstance();
            today = cal.getTime();
        }

        cal = Calendar.getInstance();
        cal.setTime(today);

        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateSpinner = new JSpinner(dateModel);


        JSpinner.DateEditor editor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        DateFormatter formatter = (DateFormatter) editor.getTextField().getFormatter();
        formatter.setAllowsInvalid(false); 
        formatter.setOverwriteMode(true);
        dateSpinner.setEditor(editor);
        
        
        dateSpinner.setValue(cal.getTime());
        //dateSpinner.addChangeListener(e -> System.out.println(dateSpinner.getValue()));

        this.add(dateSpinner);

    }

    public Point getPosition() {

        try {
            Point p = new Point();
            p = this.getLocationOnScreen();
            return p;
        } catch (Exception e) {
            return new Point(1, 1);
        }
    }

    public static void main(String args[]) {

         
        String v = "";
        if (args.length > 0) {
            v = args[0];
        } else {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            v = sdf.format(cal.getTime());
        }
        CalendarField cf = new CalendarField(v);
        
        JFrame f = new JFrame("CalendarField for " + v);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(new BorderLayout());
        f.getContentPane().add(cf, BorderLayout.CENTER);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
}
