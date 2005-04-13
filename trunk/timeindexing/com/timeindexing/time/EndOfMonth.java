// EndOfMonth.java

package com.timeindexing.time;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A TimeSpecifier for months.
 */
public class EndOfMonth extends AbstractTimeSpecifier implements TimeSpecifier {
    /**
     * Construct a EndOfMonth TimeSpecifier.
     */
    public EndOfMonth() {
	setHowMany(1);
	setDirection(TimeDirection.FORWARD_DT);
    }

    /**
     * Construct a EndOfMonth TimeSpecifier.
     */
    public EndOfMonth(TimeSpecifier modifier) {
	setHowMany(1);
	setDirection(TimeDirection.FORWARD_DT);
	afterDoing(modifier);
    }

    /**
     * Instantiate this TimeSpecifier w.r.t a particular Timestamp.
     * It returns a Timestamp which has been modified by the amount of the TimeSpecifier.
     */
    public Timestamp instantiate(Timestamp t) {
	Timestamp timestampToUse = null;

	// determine what timestamp to use
	if (getPredecessor() == null) {
	    // there is no sub modifier so we use t directly
	    timestampToUse = t;
	} else {
	    // there is a sub modifier, so we use the Timestamp 
	    // returned by instantiating the getPredecessor() with t
	    timestampToUse = getPredecessor().instantiate(t);
	}

	Timestamp returnTimestamp = null;

	// to get to the start of a month we need to 
	// allocate a calendar, because it has month processing
	Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"));
	// set the calendar to the time in timestampToUse
	calendar.setTimeInMillis( timestampToUse.getSeconds() * 1000);

	// add howMany months
	int calYear = calendar.get(Calendar.YEAR);
	int calMonth = calendar.get(Calendar.MONTH);
	// set to start of month
	calendar.set(calYear, calMonth, 1, 0, 0, 0);

	// skip to start of next month
	calendar.add(Calendar.MONTH, 1);

	// get calendar as millis
	long millis = calendar.getTimeInMillis();
	// convert to seconds
	long startOfNextMonth = millis / 1000;

	// convert to a Timestamp, and go back 1 clock tick.
	// to get back to previous month
	returnTimestamp = TimeCalculator.subtractTimestamp(new SecondTimestamp(startOfNextMonth), Clock.TICK);

	return returnTimestamp;
    }

    /**
     * To String
     */
    /**
     * To String
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(128);

	if (hasPredecessor()) {
	    buffer.append(getPredecessor().toString());
	    buffer.append(" then ");
	}

	buffer.append(" end of month ");

	return buffer.toString();
    }

}

