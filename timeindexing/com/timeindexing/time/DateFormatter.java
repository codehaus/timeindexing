// DateFormatter.java

package com.timeindexing.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * A Date formatter that displays times and dates reltive to GMT.
 * The date formatting required here is not interested in TimeZones,
 * but the java.text.SimpleDateFormat has no mechanism for specifying
 * a TimeZone; it uses the default one of the app.
 * <p>
 * This is a hack to get around this.
 * It sets the TimeZone to GMT, allocates a SimpleDateFormat,
 * and then resets the TimeZone back to the default.
 * The problem is that another thread may need the default TimeZone
 * whilst this is happening.  To overcome this
 * we synchronize on TimeZone.class.   All very nasty.
 */
public class DateFormatter {
    SimpleDateFormat formatter = null;

    /**
     * Construct a DateFormatter.
     */
    public DateFormatter(String pattern) {
	synchronized (TimeZone.class) {
	    TimeZone current = TimeZone.getDefault();
	    TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

	    formatter = new SimpleDateFormat(pattern);

	    TimeZone.setDefault(current);
	}
    }

    /**
     * Formats a Date into a date/time string.
     * @param date the time value to be formatted into a time string.
     * @return the formatted time string.
     */
    public String format(Date date) {
	return formatter.format(date);
    }
}
