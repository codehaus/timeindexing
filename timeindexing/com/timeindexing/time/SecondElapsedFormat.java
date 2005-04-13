// SecondElapsedFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * Convert a timestamp to a SecondElapsedFormat.
 * e.g. <tt>00:02:01</tt>
 */
public class SecondElapsedFormat extends AbstractElapsedFormat {
    protected final long secsPerDay = 24 * 60 * 60;

    /**
     * Construct a SecondElapsedFormat object.
     */
    public SecondElapsedFormat() {
    }

    /**
     * Format a time using seconds, given seconds and nanoseconds.
     */
    public String secondsFormat(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());
	StringBuffer buffer = new StringBuffer(32);

	if (t.isNegative()) {
	    buffer.append("-");
	}

	buffer.append(seconds);

	return buffer.toString();
    }


    /**
     * Format a time for 1 hour interval, given seconds and nanoseconds.
     */
    public String hourFormat(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());

	// some milliseconds needed for a Date object
	long milliseconds = (seconds * 1000);

	StringBuffer buffer = new StringBuffer(32);

	if (t.isNegative()) {
	    buffer.append("-");
	}

	buffer.append(firstHourformat.format((new Date(milliseconds))));

	return buffer.toString();
    }

   /**
     * Format a time for 1 day interval, given seconds and nanoseconds.
     */
    public String dayFormat(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());

	// some milliseconds needed for a Date object
	long milliseconds = (seconds * 1000);


	StringBuffer buffer = new StringBuffer(32);

	if (t.isNegative()) {
	    buffer.append("-");
	}

	buffer.append(firstDayformat.format((new Date(milliseconds))));

	return buffer.toString();
    }


    /**
     * Format a time for 1 year interval, given seconds and nanoseconds.
     */
    public String yearFormat(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());

	// some milliseconds needed for a Date object
	long milliseconds = (seconds * 1000);

	long days = seconds / secsPerDay;

	StringBuffer buffer = new StringBuffer(32);

	if (t.isNegative()) {
	    buffer.append("-");
	}

	buffer.append(daysformat.format(days));
	buffer.append(" ");
	buffer.append(firstYearformat.format((new Date(milliseconds))));

	return buffer.toString();
    }


    /**
     * Format a time for any interval, given seconds and nanoseconds.
     */
    public String fullFormat(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());

	// some milliseconds needed for a Date object
	long milliseconds = (seconds * 1000);

	long days = seconds / secsPerDay % 365;
	long years = seconds / secsPerDay / 365;

	StringBuffer buffer = new StringBuffer(32);

	if (t.isNegative()) {
	    buffer.append("-");
	}

	buffer.append(yearsformat.format(years));
	buffer.append(" ");
	buffer.append(daysformat.format(days));
	buffer.append(" ");
	buffer.append(catchAllformat.format((new Date(milliseconds))));

	return buffer.toString();
    }

}
