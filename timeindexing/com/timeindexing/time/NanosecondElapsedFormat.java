// NanosecondElapsedFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Convert a timestamp to a NanosecondDateFormat.
 * e.g. <tt>00:00:01.593469450</tt>
 */
public class NanosecondElapsedFormat {
    /*
     * A format for nanoseconds.  9 obligatory digits.
     */
    private static NumberFormat nanosformat = new DecimalFormat("000000000");

    /*
     * A format for the first hour
     */
    private static DateFormat firstHourformat = new SimpleDateFormat("mm:ss");

    /*
     * A format for the first day
     */
    private static DateFormat firstDayformat = new SimpleDateFormat("HH:mm:ss");

    /*
     * A format for the first year
     */
    private static NumberFormat daysformat = new DecimalFormat("000");
    private static DateFormat firstYearformat = new SimpleDateFormat("HH:mm:ss");

    /*
     * Catchall format 
     */
    private static NumberFormat yearsformat = new DecimalFormat("0000");
    private static DateFormat catchAllformat = new SimpleDateFormat("HH:mm:ss");

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
	long secsPerHour = 60 * 60;
	long secsPerDay = 24 * 60 * 60;
	long secsPerYear = 365 * 24 * 60 * 60;

	if (seconds < secsPerHour) {
	    return hourFormat(seconds, nanoseconds);
	} else if (seconds < secsPerDay) {
	    return dayFormat(seconds, nanoseconds);
	} else if (seconds < secsPerYear) {
	    return yearFormat(seconds, nanoseconds);
	} else {
	    return fullFormat(seconds, nanoseconds);
	}
    }

    /**
     * Format a time for 1 hour interval, given seconds and nanoseconds.
     */
    public static String hourFormat(Timestamp t) {
	return hourFormat(t.getSeconds(), t.getNanoSeconds());
    }


    /**
     * Format a time for 1 hour interval, given seconds and nanoseconds.
     */
    public static String hourFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long nanosOnly = nanoseconds;

	return firstHourformat.format((new Date(milliseconds))) + "." + nanosformat.format(nanosOnly);
    }

     /**
     * Format a time for 1 day interval, given seconds and nanoseconds.
     */
    public static String dayFormat(Timestamp t) {
	return dayFormat(t.getSeconds(), t.getNanoSeconds());
    }


   /**
     * Format a time for 1 day interval, given seconds and nanoseconds.
     */
    public static String dayFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long nanosOnly = nanoseconds;

	return firstDayformat.format((new Date(milliseconds))) + "." + nanosformat.format(nanosOnly);
    }

    /**
     * Format a time for 1 year interval, given seconds and nanoseconds.
     */
    public static String yearFormat(Timestamp t) {
	return yearFormat(t.getSeconds(), t.getNanoSeconds());
    }


    /**
     * Format a time for 1 year interval, given seconds and nanoseconds.
     */
    public static String yearFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long nanosOnly = nanoseconds;
	long secsPerDay = 24 * 60 * 60;

	long days = seconds / secsPerDay;

	return daysformat.format(days) + " " + firstYearformat.format((new Date(milliseconds))) + "." + nanosformat.format(nanosOnly);
    }

    /**
     * Format a time for any interval, given seconds and nanoseconds.
     */
    public static String fullFormat(Timestamp t) {
	return fullFormat(t.getSeconds(), t.getNanoSeconds());
    }


    /**
     * Format a time for any interval, given seconds and nanoseconds.
     */
    public static String fullFormat(long seconds, int nanoseconds) {
	long milliseconds = (seconds * 1000) + (nanoseconds / 1000000);
	long nanosOnly = nanoseconds;
	long secsPerDay = 24 * 60 * 60;

	long days = seconds / secsPerDay % 365;
	long years = seconds / secsPerDay / 365;

	return yearsformat.format(years) + " " +  daysformat.format(days) + " " +  catchAllformat.format((new Date(milliseconds))) + "." + nanosformat.format(nanosOnly);
    }

}
