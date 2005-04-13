// MicrosecondDateFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.util.TimeZone;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * Convert a timestamp to a string using the MicrosecondDateFormat.
 * e.g. <tt>2003/08/07 16:29:58.880123</tt>
 */
public class MicrosecondDateFormat extends AbstractDateFormat {
    /*
     * A format for microseconds.  6 obligatory digits.
     */
    private static NumberFormat microsformat = new DecimalFormat("000000");


    /**
     * Construct a MicrosecondDateFormat object.
     */
    public MicrosecondDateFormat() {
    }

    /**
     * Format a time as seconds and nanoseconds.
     */
    public String format(AbsoluteTimestamp t) {
	long seconds = t.getSeconds();
	int nanoseconds = t.getNanoSeconds();

	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long microsOnly = nanoseconds  / 1000;

	return formatter.format((new Date(milliseconds))) + "." + microsformat.format(microsOnly);
    }
}
