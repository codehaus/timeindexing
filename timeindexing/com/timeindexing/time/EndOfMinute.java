// EndOfMinute.java

package com.timeindexing.time;

/**
 * A TimeSpecifier for end of minute.
 */
public class EndOfMinute extends AbstractTimeSpecifier implements TimeSpecifier {
    /**
     * Construct a EndOfMinute TimeSpecifier.
     */
    public EndOfMinute() {
	setHowMany(1);
	setDirection(TimeDirection.FORWARD_DT);
    }

    /**
     * Construct a EndOfMinute TimeSpecifier.
     */
    public EndOfMinute(TimeSpecifier modifier) {
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

	// to get to the start of a minute we need to round 
	// the no of seconds in timestampToUse to the
	// the no of seconds in a minute

	// seconds per minute
	long secondsPerMinute = 60;

	// get the no of seconds
	// this is actually start of minute backward
	long remainingSeconds = timestampToUse.getSeconds() / secondsPerMinute * secondsPerMinute;

	// start of next minute 
	long startOfNextMinute = remainingSeconds + secondsPerMinute;

	// subtract 1 clock tick
	returnTimestamp = TimeCalculator.subtractTimestamp(new SecondTimestamp(startOfNextMinute), Clock.TICK);

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

	buffer.append(" end of minute ");

	return buffer.toString();
    }

}

