// Year.java

package com.timeindexing.time;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A TimeSpecifier for years.
 */
public class Year extends AbstractTimeSpecifier implements TimeSpecifier {
    /**
     * Construct a Year TimeSpecifier.
     */
    public Year(long count, TimeDirection direction) {
	setHowMany(count);
	setDirection(direction);
    }

    /**
     * Construct a Year TimeSpecifier.
     */
    public Year(long count, TimeDirection direction, TimeSpecifier modifier) {
	setHowMany(count);
	setDirection(direction);
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

	// allocate a calendar, because it has year processing
	Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT-0"));
	// set the calendar to the time in timestampToUse
	calendar.setTimeInMillis( timestampToUse.getSeconds() * 1000);


	if (modificationDirection == TimeDirection.FORWARD_DT) {
	    // add howMany years
	    calendar.add(Calendar.YEAR, (int)(howMany));

	    long millis = calendar.getTimeInMillis();
	    long seconds = millis / 1000;


	    Timestamp subSecondPart = TimeCalculator.subtractTimestamp(timestampToUse, new SecondTimestamp(timestampToUse.getSeconds()));

	    returnTimestamp = TimeCalculator.addTimestamp(new SecondTimestamp(seconds), subSecondPart);
	} else {
	    // subtract howMany years
	    calendar.add(Calendar.YEAR, (int)(-howMany));

	    long millis = calendar.getTimeInMillis();
	    long seconds = millis / 1000;

	    Timestamp subSecondPart = TimeCalculator.subtractTimestamp(timestampToUse, new SecondTimestamp(timestampToUse.getSeconds()));

	    returnTimestamp = TimeCalculator.addTimestamp(new SecondTimestamp(seconds), subSecondPart);
	}

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

	buffer.append(howMany);
	buffer.append(" year ");
	buffer.append(modificationDirection.toString());

	return buffer.toString();
    }

}

