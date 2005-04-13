// EndOfWeek.java

package com.timeindexing.time;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A TimeSpecifier for weeks.
 */
public class EndOfWeek extends AbstractTimeSpecifier implements TimeSpecifier {
    /**
     * Construct a EndOfWeek TimeSpecifier.
     */
    public EndOfWeek() {
	setHowMany(1);
	setDirection(TimeDirection.FORWARD_DT);
    }

    /**
     * Construct a EndOfWeek TimeSpecifier.
     */
    public EndOfWeek(TimeSpecifier modifier) {
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

	// to get to the start of a week we need to 
	// allocate a calendar, because it has week processing
	Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"));
	// set the calendar to the time in timestampToUse
	calendar.setTimeInMillis( timestampToUse.getSeconds() * 1000);

	// set day to start of week
	calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());

	// now reset the calendar time
	int calYear = calendar.get(Calendar.YEAR);
	int calMonth = calendar.get(Calendar.MONTH);
	// what isthe day of the month
	int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

	// set to start of day at start of week
	calendar.set(calYear, calMonth, dayOfMonth, 0, 0, 0);

	// now skip forward 7 days
	calendar.add(Calendar.DAY_OF_MONTH, 7);

	// get calendar as millis
	long millis = calendar.getTimeInMillis();
	// convert to seconds
	long startOfNextWeek = millis / 1000;

	// convert to a Timestamp, and go back 1 clock tick.
	// to get back to previous week
	returnTimestamp =  TimeCalculator.subtractTimestamp(new SecondTimestamp(startOfNextWeek), Clock.TICK);

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

	buffer.append(" end of week ");

	return buffer.toString();
    }

}

