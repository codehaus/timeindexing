// AbstractElapsedFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * This abstact class has the base methods for objects that 
 * format elapsed timestamps.
 */
public abstract class AbstractElapsedFormat implements TimestampFormatting {

    /*
     * A format for the first hour
     */
    protected static DateFormatter firstHourformat = new DateFormatter("mm:ss");

    /*
     * A format for the first day
     */
    protected static DateFormatter firstDayformat = new DateFormatter("HH:mm:ss");

    /*
     * A format for the first year
     */
    protected static NumberFormat daysformat = new DecimalFormat("000");
    protected static DateFormatter firstYearformat = new DateFormatter("HH:mm:ss");

    /*
     * Catchall format 
     */
    protected static NumberFormat yearsformat = new DecimalFormat("0000");
    protected static DateFormatter catchAllformat = new DateFormatter("HH:mm:ss");

    /**
     * Format a Timestamp.
     */
    public String format(Timestamp t) {
	return format(t.getSeconds(), t.getNanoSeconds());
    }

    /**
     * Format a time as seconds and nanoseconds.
     */
    public String format(long seconds, int nanoseconds) {
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
     * Format a time using seconds and nanoseconds, given a Timestamp.
     */
    public String secondsFormat(Timestamp t) {
	return secondsFormat(t.getSeconds(), t.getNanoSeconds());
    }

    /**
     * Format a time using seconds, given seconds and nanoseconds.
     */
    public abstract String secondsFormat(long seconds, int nanoseconds);

    /**
     * Format a time for 1 hour interval, given a Timestamp.
     */
    public String hourFormat(Timestamp t) {
	return hourFormat(t.getSeconds(), t.getNanoSeconds());
    }


    /**
     * Format a time for 1 hour interval, given seconds and nanoseconds.
     */
    public abstract String hourFormat(long seconds, int nanoseconds);

     /**
     * Format a time for 1 day interval, given a Timestamp.
     */
    public String dayFormat(Timestamp t) {
	return dayFormat(t.getSeconds(), t.getNanoSeconds());
    }

   /**
     * Format a time for 1 day interval, given seconds and nanoseconds.
     */
    public abstract String dayFormat(long seconds, int nanoseconds);

    /**
     * Format a time for 1 year interval, given a Timestamp.
     */
    public String yearFormat(Timestamp t) {
	return yearFormat(t.getSeconds(), t.getNanoSeconds());
    }

    /**
     * Format a time for 1 year interval, given seconds and nanoseconds.
     */
    public abstract String yearFormat(long seconds, int nanoseconds);

   /**
     * Format a time for any interval, given a Timestamp.
     */
    public String fullFormat(Timestamp t) {
	return fullFormat(t.getSeconds(), t.getNanoSeconds());
    }

    /**
     * Format a time for any interval, given seconds and nanoseconds.
     */
    public abstract String fullFormat(long seconds, int nanoseconds);

}
