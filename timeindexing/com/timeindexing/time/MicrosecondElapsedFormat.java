// MicrosecondElapsedFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * Convert a timestamp to a MicrosecondElapsedFormat.
 * e.g. <tt>00:00:01.593469</tt>
 */
public class MicrosecondElapsedFormat extends AbstractElapsedFormat {
    /*
     * A format for microseconds.  6 obligatory digits.
     */
    private static NumberFormat microsformat = new DecimalFormat("000000");

    /**
     * Construct a MicrosecondElapsedFormat object.
     */
    public MicrosecondElapsedFormat() {
    }

    /**
     * Format a time using seconds, given seconds and nanoseconds.
     */
    public String secondsFormat(long seconds, int nanoseconds) {
	return seconds + "." + microsformat.format(nanoseconds/1000);
    }


    /**
     * Format a time for 1 hour interval, given seconds and nanoseconds.
     */
    public String hourFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long microsOnly = nanoseconds/1000;

	return firstHourformat.format((new Date(milliseconds))) + "." + microsformat.format(microsOnly);
    }

   /**
     * Format a time for 1 day interval, given seconds and nanoseconds.
     */
    public String dayFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long microsOnly = nanoseconds/1000;

	return firstDayformat.format((new Date(milliseconds))) + "." + microsformat.format(microsOnly);
    }


    /**
     * Format a time for 1 year interval, given seconds and nanoseconds.
     */
    public String yearFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long microsOnly = nanoseconds/1000;
	long secsPerDay = 24 * 60 * 60;

	long days = seconds / secsPerDay;

	return daysformat.format(days) + " " + firstYearformat.format((new Date(milliseconds))) + "." + microsformat.format(microsOnly);
    }


    /**
     * Format a time for any interval, given seconds and nanoseconds.
     */
    public String fullFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long microsOnly = nanoseconds/1000;
	long secsPerDay = 24 * 60 * 60;

	long days = seconds / secsPerDay % 365;
	long years = seconds / secsPerDay / 365;

	return yearsformat.format(years) + " " +  daysformat.format(days) + " " +  catchAllformat.format((new Date(milliseconds))) + "." + microsformat.format(microsOnly);
    }

}
