// NanosecondDateFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.util.TimeZone;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * Convert a timestamp to a string using the  NanosecondDateFormat.
 * e.g. <tt>2003/08/07 16:29:58.880123400</tt>
 */
public class NanosecondDateFormat extends AbstractDateFormat {
    /*
     * A format for nanoseconds.  9 obligatory digits.
     */
    private static NumberFormat nanosformat = new DecimalFormat("000000000");

    /**
     * Construct a NanosecondDateFormat object.
     */
    public NanosecondDateFormat() {
    }

    /**
     * Format a time as seconds and nanoseconds.
     */
    public String format(AbsoluteTimestamp t) {
	long seconds = t.getSeconds();
	int nanoseconds = t.getNanoSeconds();
	
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long nanosOnly = nanoseconds;

	return formatter.format((new Date(milliseconds))) + "." + nanosformat.format(nanosOnly);
    }
}
