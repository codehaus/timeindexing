// NanosecondDateFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Convert a timestamp to a NanosecondDateFormat.
 * e.g. <tt>2003/08/07 16:29:58.880123400</tt>
 */
public class NanosecondDateFormat {
    /*
     * A format for nanoseconds.  9 obligatory digits.
     */
    private static NumberFormat nanosformat = new DecimalFormat("000000000");

    /*
     * A format for whole seconds
     */
    private static DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /**
     * Format a Timestamp.
     */
    public static String format(Timestamp t) {
	return format(t.getSeconds(), t.getNanoSeconds());
    }

    /**
     * Format a time as seconds and nanoseconds.
     */
    public static String format(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long nanosOnly = nanoseconds;

	return format.format((new Date(milliseconds))) + "." + nanosformat.format(nanosOnly);
    }
}
