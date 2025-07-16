/*
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

package sql.fredy.tools;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Fredy Fischer
 */
public class DateHelper {
    
    /*
     returns the offset for Zürich in milliseconds for today
     */
    public int getGmtOffset() {
        TimeZone timezone = TimeZone.getTimeZone("Europe/Zurich");

        //daylight saving time?
        boolean sommerzeit = timezone.inDaylightTime(new Date());

        // difference to  UTC / GMT
        int offset = timezone.getOffset(Calendar.ZONE_OFFSET);

        // if daylight saving, we ad an hour
        if (sommerzeit) {
            offset = offset + 3600000;
        }

        return offset;
    }

    /*
    returns the offset for  in milliseconds for Zürich am angegebenen Datum in Sekunden zurück
    */
    public int getGmtOffsetForDate(int seconds) {
        TimeZone timezone = TimeZone.getTimeZone("Europe/Zurich");

        
        long millis = Long.valueOf(seconds * 1000);
        
        //haben wir Sommerzeit an diesem Datum?
        boolean sommerzeit = timezone.inDaylightTime(new Date(millis));

        // wie gross ist der Abstand zu UTC / GMT
        int offset = timezone.getOffset(Calendar.ZONE_OFFSET);

        // wenn Sommerzeit, kommt eine Stunde dazu
        if (sommerzeit) {
            offset = offset + 3600000;
        }

        return offset;
    }
    
    public int getGmtOffsetForDate(int seconds,String timeZone) {
        TimeZone timezone = TimeZone.getTimeZone(timeZone);

        //haben wir Sommerzeit an diesem Datum?
        long millis = Long.valueOf(seconds * 1000);
        
        boolean sommerzeit = timezone.inDaylightTime(new Date(millis));

        // wie gross ist der Abstand zu UTC / GMT
        int offset = timezone.getOffset(Calendar.ZONE_OFFSET);

        // wenn Sommerzeit, kommt eine Stunde dazu
        if (sommerzeit) {
            offset = offset + 3600000;
        }

        return offset;
    }
    
    
    /*
     gibt den Offset in Millisekunden für die eingegebene ZeitZone 'timeZone'heute zurück
     */
    public int getGmtOffset(String timeZone) {
        TimeZone timezone = TimeZone.getTimeZone(timeZone);

        //haben wir Sommerzeit?
        boolean sommerzeit = timezone.inDaylightTime(new Date());

        // wie gross ist der Abstand zu UTC / GMT
        int offset = timezone.getOffset(Calendar.ZONE_OFFSET);

        // wenn Sommerzeit, kommt eine Stunde dazu
        if (sommerzeit) {
            offset = offset + 3600000;
        }

        return offset;
    }

    /*
     der Parameter 'seconds' enthält die Zeit in Sekunden gemäss GMT 
     die Methode liefert die lokalisierte Zeit für Zürich in Sekunden zurück
     */
    public int getLocalizedSeconds(int seconds) {

        Calendar datum = Calendar.getInstance();
        datum.setTimeZone( TimeZone.getTimeZone("GMT"));
        datum.setTimeInMillis(Long.valueOf(seconds*1000));
        
        // wir fügen die Schweizer Zeit hinzu
        seconds = (seconds * 1000) + getGmtOffsetForDate(seconds);

        datum.setTimeInMillis(seconds);

        Long timeInMillis = datum.getTimeInMillis() / 1000;
        if (timeInMillis < 0) {
            timeInMillis = 0l;
        }

        
        datum.setTimeZone( TimeZone.getTimeZone("Europe/Zurich"));
        
        return timeInMillis.intValue();
    }

    /*
     der Parameter 'seconds' enthält die Zeit in Sekunden gemäss GMT 
     die Methode liefert die lokalisierte Zeit für Zürich in Sekunden zurück
     */
    public int getLocalizedSeconds(int seconds, String timeZone) {

        Calendar datum = Calendar.getInstance();

        // wir fügen die Zeit gemäss der angegebenen Zeitzone hinzu
        seconds = (seconds * 1000) + getGmtOffsetForDate(seconds,timeZone);

        datum.setTimeInMillis(seconds);

        Long timeInMillis = datum.getTimeInMillis() / 1000;
        if (timeInMillis < 0) {
            timeInMillis = 0l;
        }

        return timeInMillis.intValue();
    }

    /*
     empfängt einen timeStamp gemäss GMT und wandelt ihn in die Zürich Zeitzone um
     */
    public Timestamp getLocalizedTime(Timestamp timestamp) {
        long timeInMillis = timestamp.getTime();

        int offset = getGmtOffsetForDate((Long.valueOf(timestamp.getTime() / 1000)).intValue());

        timeInMillis = timeInMillis + Integer.valueOf(offset).longValue();
        timestamp.setTime(timeInMillis);

        return timestamp;
    }
    public Timestamp getLocalizedTime(int seconds) {
        long timeInMillis = Integer.valueOf(seconds * 1000 ).longValue();

        int offset = getGmtOffsetForDate(seconds);

        timeInMillis = timeInMillis + Integer.valueOf(offset).longValue();
        
        return new Timestamp(timeInMillis);       
    }
    
    
    
    /*
     empfängt einen timeStamp gemäss GMT und wandelt ihn in die mitgegebene Zeitzone um
     */
    public Timestamp getLocalizedTime(Timestamp timestamp, String timeZone) {
        long timeInMillis = timestamp.getTime();

        int offset = getGmtOffsetForDate( Long.valueOf(timestamp.getTime() / 1000).intValue(),timeZone);

        timeInMillis = timeInMillis + Integer.valueOf(offset).longValue();
        timestamp.setTime(timeInMillis);

        return timestamp;
    }

    /*
     Empfängt ein java.sql.Date Objekt in GMT Timezone und gibt es für Zurich-Zeitzone zurück
     */
    public java.sql.Date getLocalizedDate(java.sql.Date date) {
          Timestamp ts = getLocalizedTime(new Timestamp(date.getTime()));
          return new java.sql.Date(ts.getTime());        
    }

    /*
     gib das aktuelle Datum als Calendar-Objekt zurück
     */
    public Calendar getActualCalendar() {
        Calendar heute = Calendar.getInstance();
        return heute;
    }

    /*
     gib das aktuelle Datum als java.util.Date-Objekt zurück
     */
    public java.util.Date getActualDate() {
        return new Date();
    }

    /*
     gib das aktuelle Datum als java.sql.Date-Objekt zurück
     */
    public java.sql.Date getActualSQLDate() {
        return new java.sql.Date(getActualDate().getTime());
    }

    /*
     gib einen aktuellen TimeStamp zurück
     */
    public Timestamp now() {
        return new java.sql.Timestamp(getActualDate().getTime());
    }

    /*
     gib einen Timestamp formatiert als dd.mm.jjjj hh:mm:ss zurück    
     */
    public String getFormattedTimestampSeconds(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(timestamp.getTime());
    }

    /*
     gib einen Timestamp formatiert als dd.mm.jjjj hh:mm zurück    
     */
    public String getFormattedTimestampMinutes(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return sdf.format(timestamp.getTime());
    }

    /*
     gib einen Timestamp formatiert als dd.mm.jjjj hh:mm zurück    
     */
    public String getFormattedTimestampDate(Timestamp timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(timestamp.getTime());
    }

    /*
     gib ein java.sql.Date Objekt formatiert als dd.mm.jjjj hh:mm:ss zurück    
     */
    public String getFormattedDateSeconds(java.sql.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(date.getTime());
    }

    /*
     gib einen java.sql.Date Objekt formatiert als dd.mm.jjjj hh:mm zurück    
     */
    public String getFormattedDateMinutes(java.sql.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        return sdf.format(date.getTime());
    }

    /*
     gib einen Timestamp formatiert als dd.mm.jjjj hh:mm zurück    
     */
    public String getFormattedDate(java.sql.Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date.getTime());
    }

    public static void main(String args[]) {
        DateHelper h = new DateHelper();
        System.out.println(h.getLocalizedTime(h.now()));
        System.out.println("Offset for test time: " + h.getGmtOffsetForDate(1386000112));
        

    }
    
    
    
}
