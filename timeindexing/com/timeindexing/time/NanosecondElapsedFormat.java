// NanosecondElapsedFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * Convert a timestamp to a NanosecondElapsedFormat.
 * e.g. <tt>00:00:01.593469450</tt>
 */
public class NanosecondElapsedFormat extends AbstractElapsedFormat {
    /*
     * A format for nanoseconds.  9 obligatory digits.
     */
    private static NumberFormat nanosformat = new DecimalFormat("000000000");

    /**
     * Construct a NanosecondElapsedFormat object.
     */
    public NanosecondElapsedFormat() {
    }

    /**
     * Format a time using seconds, given seconds and nanoseconds.
     */
    public String secondsFormat(long seconds, int nanoseconds) {
	return seconds + "." + nanosformat.format(nanoseconds);
    }


    /**
     * Format a time for 1 hour interval, given seconds and nanoseconds.
     */
    public String hourFormat(long seconds, int nanoseconds) {
	boolean isNegative = seconds < 0 || nanoseconds < 0;
	long milliseconds = (Math.abs(seconds) * 1000) + (Math.abs(nanoseconds) / 1000000);
	long nanosOnly = Math.abs(nanoseconds);

	return (isNegative ? "-" : "") +  firstHourformat.format((new Date(milliseconds))) + "." + nanosformat.format(nanosOnly);
    }

   /**
     * Format a time for 1 day interval, given seconds and nanoseconds.
     */
    public String dayFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long nanosOnly = nanoseconds;

	return firstDayformat.format((new Date(milliseconds))) + "." + nanosformat.format(nanosOnly);
    }


    /**
     * Format a time for 1 year interval, given seconds and nanoseconds.
     */
    public String yearFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long nanosOnly = nanoseconds;
	long secsPerDay = 24 * 60 * 60;

	long days = seconds / secsPerDay;

	return daysformat.format(days) + " " + firstYearformat.format((new Date(milliseconds))) + "." + nanosformat.format(nanosOnly);
    }


    /**
     * Format a time for any interval, given seconds and nanoseconds.
     */
    public String fullFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long nanosOnly = nanoseconds;
	long secsPerDay = 24 * 60 * 60;

	long days = seconds / secsPerDay % 365;
	long years = seconds / secsPerDay / 365;

	System.err.println("years = " + years + " days = " + days  + " millis = " + milliseconds + " nanos = " + nanoseconds);

	return yearsformat.format(years) + " " +  daysformat.format(days) + " " +  catchAllformat.format((new Date(milliseconds))) + "." + nanosformat.format(nanosOnly);
    }

}
