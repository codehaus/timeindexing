// AbstractDateFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * This abstact class has the base methods for objects that 
 * format timestamps as absolute dates.
 */
public abstract class AbstractDateFormat implements TimestampFormatting {
    /*
     * A format for whole seconds
     */
    protected static DateFormatter defaultFormatter = new DateFormatter("yyyy/MM/dd HH:mm:ss");

    /*
     * The DateFormatter
     */
    DateFormatter formatter = null;

    /**
     * The default constructor.
     */
    public AbstractDateFormat() {
	formatter = defaultFormatter;
    }
	
    /**
     * A constructor which takes a new DateFormatter.
     */
    public AbstractDateFormat(DateFormatter aFormatter) {
	formatter = aFormatter;
    }

    /**
     * Format a Timestamp.
     */
    public String format(Timestamp t) {
	// this is an experimental piece of code that
	// converts AbsoluteTimestamps in the first year
	// i.e. 1/1/1970 => 31/12/1970 into RelativeTimestamps
	if (t.getSeconds() < (60 * 60 * 24 * 365)) {
	    return TimeCalculator.toRelative((AbsoluteTimestamp)t).toString();
	} else {
	    return format((AbsoluteTimestamp)t);
	}
    }

    /**
     * Format a date.
     * This calls on the DateFormatter to do some formatting.
     */
    protected final String format(Date date) {
	return formatter.format(date);
    }

    /**
     * Format a AbsoluteTimestamp.
     */
    public abstract String format(AbsoluteTimestamp t);
}
