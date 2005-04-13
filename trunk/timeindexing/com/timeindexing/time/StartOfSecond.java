// StartOfSecond.java

package com.timeindexing.time;

/**
 * A TimeSpecifier for start of second.
 */
public class StartOfSecond extends AbstractTimeSpecifier implements TimeSpecifier {
    /**
     * Construct a StartOfSecond TimeSpecifier.
     */
    public StartOfSecond() {
	setHowMany(1);
	setDirection(TimeDirection.BACKWARD_DT);
    }

    /**
     * Construct a StartOfSecond TimeSpecifier.
     */
    public StartOfSecond(TimeSpecifier modifier) {
	setHowMany(1);
	setDirection(TimeDirection.BACKWARD_DT);
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

	// to get to the start of a second we need to just get the no of seconds

	// get the no of seconds
	// this is actually start of second backward
	long remainingSeconds = timestampToUse.getSeconds();

	// convert the remaining seconds to a Timestamp
	returnTimestamp = new SecondTimestamp(remainingSeconds);

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

	buffer.append(" start of second ");

	return buffer.toString();
    }

}

