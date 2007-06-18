// EndOfHour.java

package com.timeindexing.time;

/**
 * A TimeSpecifier for end of hour.
 */
public class EndOfHour extends AbstractTimeSpecifier implements TimeSpecifier {
    /**
     * Construct a EndOfHour TimeSpecifier.
     */
    public EndOfHour() {
	setHowMany(1);
	setDirection(TimeDirection.FORWARD);
    }

    /**
     * Construct a EndOfHour TimeSpecifier.
     */
    public EndOfHour(TimeSpecifier modifier) {
	setHowMany(1);
	setDirection(TimeDirection.FORWARD);
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

	// to get to the start of a hour we need to round 
	// the no of seconds in timestampToUse to the
	// the no of seconds in a hour

	// seconds per hour
	long secondsPerHour = 60 * 60;

	// get the no of seconds
	// this is actually start of hour backward
	long remainingSeconds = timestampToUse.getSeconds() / secondsPerHour * secondsPerHour;

	// start of next hour 
	long startOfNextHour = remainingSeconds + secondsPerHour;

	// subtract 1 clock tick
	returnTimestamp = TimeCalculator.subtractTimestamp(new SecondTimestamp(startOfNextHour), Clock.TICK);

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

	buffer.append(" end of hour ");

	return buffer.toString();
    }

}

