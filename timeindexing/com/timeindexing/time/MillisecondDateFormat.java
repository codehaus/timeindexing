// MillisecondDateFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.util.TimeZone;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * Convert a timestamp to a string using the MillisecondDateFormat.
 * e.g. <tt>2003/08/07 16:29:58.880</tt>
 */
public class MillisecondDateFormat extends AbstractDateFormat {
    /*
     * A format for milliseconds.  3 obligatory digits.
     */
    private static NumberFormat millisformat = 	millisformat =  new DecimalFormat("000");


   /**
     * Construct a MillisecondDateFormat object.
     */
    public MillisecondDateFormat() {
    }

    /**
     * Format a time as seconds and nanoseconds.
     */
    public String format(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long millisOnly = nanoseconds  / 1000000;

	return formatter.format((new Date(milliseconds))) + "." + millisformat.format(millisOnly);
    }
}
