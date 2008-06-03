Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
// AbstractElapsedFormat.java


package com.timeindexing.time;

import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;

/**
 * This abstact class has the base methods for objects that 
 * format timestamps as elapsed times.
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

    /*
     * Secs Per Minute.
     */
    protected final long secsPerMinute = 60;
    /*
     * Secs Per Hour.
     */
    protected final long secsPerHour = 60 * 60;
    /*
     * Secs Per Day.
     */
    protected final long secsPerDay = 24 * 60 * 60;
    /*
     * Secs Per Year (approximately).
     */
    protected final long secsPerYear = 365 * 24 * 60 * 60;

    /**
     * Format a Timestamp.
     */
    public String format(Timestamp t) {
	return format((RelativeTimestamp)t);
    }

    /**
     * Format a RelativeTimestamp.
     */
    public String format(RelativeTimestamp t) {
	long seconds = Math.abs(t.getSeconds());

	// determine the right formatting scale
	if (seconds < secsPerMinute) {
	    return secondsFormat(t);
	} else if (seconds < secsPerHour) {
	    return hourFormat(t);
	} else if (seconds < secsPerDay) {
	    return dayFormat(t);
	} else if (seconds < secsPerYear) {
	    return yearFormat(t);
	} else {
	    return fullFormat(t);
	}
    }

    /**
     * Format a time using seconds and nanoseconds, given a Timestamp.
     */
    public abstract String secondsFormat(RelativeTimestamp t);

    /**
     * Format a time for 1 hour interval, given a Timestamp.
     */
    public abstract String hourFormat(RelativeTimestamp t);

    /**
     * Format a time for 1 day interval, given a Timestamp.
     */
    public abstract String dayFormat(RelativeTimestamp t);

    /**
     * Format a time for 1 year interval, given a Timestamp.
     */
    public abstract String yearFormat(RelativeTimestamp t);

   /**
     * Format a time for any interval, given a Timestamp.
     */
    public abstract String fullFormat(RelativeTimestamp t);
}
