// SecondDateFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.util.TimeZone;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * Convert a timestamp to a string using the SecondDateFormat.
 * e.g. <tt>2003/08/07 16:29:58</tt>
 */
public class SecondDateFormat extends AbstractDateFormat {
   /**
     * Construct a MillisecondDateFormat object.
     */
    public SecondDateFormat() {
    }

    /**
     * Format a time as seconds and nanoseconds.
     */
    public String format(long seconds, int nanoseconds) {
	long milliseconds = seconds * 1000;

	return formatter.format((new Date(milliseconds)));
    }
}
