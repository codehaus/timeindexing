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
    /**
     * Construct a SecondElapsedFormat object.
     */
    public SecondElapsedFormat() {
    }

    /**
     * Format a time using seconds, given seconds and nanoseconds.
     */
    public String secondsFormat(long seconds, int nanoseconds) {
	return "" + seconds;
    }


    /**
     * Format a time for 1 hour interval, given seconds and nanoseconds.
     */
    public String hourFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);

	return firstHourformat.format((new Date(milliseconds)));
    }

   /**
     * Format a time for 1 day interval, given seconds and nanoseconds.
     */
    public String dayFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);

	return firstDayformat.format((new Date(milliseconds)));
    }


    /**
     * Format a time for 1 year interval, given seconds and nanoseconds.
     */
    public String yearFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long secsPerDay = 24 * 60 * 60;

	long days = seconds / secsPerDay;

	return daysformat.format(days) + " " + firstYearformat.format((new Date(milliseconds)));
    }


    /**
     * Format a time for any interval, given seconds and nanoseconds.
     */
    public String fullFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long secsPerDay = 24 * 60 * 60;

	long days = seconds / secsPerDay % 365;
	long years = seconds / secsPerDay / 365;

	return yearsformat.format(years) + " " +  daysformat.format(days) + " " +  catchAllformat.format((new Date(milliseconds)));
    }

}
