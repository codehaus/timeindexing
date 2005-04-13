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
    protected static DateFormatter formatter = new DateFormatter("yyyy/MM/dd HH:mm:ss");

    /**
     * Format a Timestamp.
     */
    public String format(Timestamp t) {
	return format((AbsoluteTimestamp)t);
    }

    /**
     * Format a AbsoluteTimestamp.
     */
    public abstract String format(AbsoluteTimestamp t);
}
