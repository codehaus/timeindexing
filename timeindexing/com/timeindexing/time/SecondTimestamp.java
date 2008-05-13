// SecondTimestamp.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;
import java.util.Date;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * A timestamp that only has significant data down to second level.
 */
public class SecondTimestamp extends AbstractAbsoluteTimestamp implements AbsoluteTimestamp, SecondScale, Serializable {
    /*
     * The mask used for before/after epoch
     */
    public final static long BEFORE_EPOCH = Timestamp.SECOND_SIGN;

    /*
     * The value of the timestamp
     */
    long value;

    /**
     * Construct a SecondTimestamp with 'now' as the timestamp.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    public SecondTimestamp() {
	this(Clock.time);
    }
	
    /**
     * Construct a SecondTimestamp with 'now' as the timestamp.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    SecondTimestamp(Clock clock) {
	value = (clock.getRawTime() / 1000) | Timestamp.SECOND;
    }
	
    /**
     * Construct a SecondTimestamp from a number of seconds.
     */
    public SecondTimestamp(long ts) {
	value = ts | Timestamp.SECOND;
    }
	
	
    /**
     * Construct a SecondTimestamp from a number of seconds
     * and a number of nanoseconds.
     */
    public SecondTimestamp(long seconds, int nanoseconds) {
	// check to see if the no of seconds is in the correct range.
	if (Math.abs(seconds) > (Timestamp.SECOND_SIGN-1)) {   // the number is too big
	    throw new IllegalArgumentException("The number of seconds is out of range. The maximum size is " + (Timestamp.SECOND_SIGN-1));
	}
	    
	if (seconds < 0) {			// a before epoch time
	    value = (-seconds);
	} else {
	    value = seconds;
	}

	// add on the nanoseconds
	value += (nanoseconds / 1000000000);
	value |= Timestamp.SECOND;
    }

    /**
     * Construct a SecondTimestamp from a Date object.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    public SecondTimestamp(Date date) {
	value = (date.getTime() / 1000) | Timestamp.SECOND;
    }
	
    /**
     * Get the number of seconds
     */
    public long getSeconds() {
	long valueT = value ^ Timestamp.SECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    if (isAfterEpoch()) {
		return valueT;
	    } else {
		// the time is before epoch
		// so throw away the BEFORE_EPOCH bit
		long valueB = valueT ^ BEFORE_EPOCH;
		return -(valueB);
	    }
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     * Always zero for SecondTimestamps.
     */
    public int getNanoSeconds() {
	return 0;
    }

    /**
     * Get the Scale.
     */
    public Scale getScale() {
	return SecondScale.SCALE;
    }

    /**
     * Get the toString() version of a SecondTimestamp.
     */
    public String toString() {
	//return ("[" + new SecondDateFormat().format((Timestamp)this) + "]");
	return getEpoch().format(this);
    }

    /**
     * Get the raw value.
     * Used in other parts of the implementation.
     */
    public long value() {
	return value;
    }


    /**
     * Is the time after the Epoch.
     */
    public boolean isAfterEpoch() {
	if ((value & BEFORE_EPOCH) == 0) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Is the time before the Epoch.
     */
    public boolean isBeforeEpoch() {
	if ((value & BEFORE_EPOCH) == 0) {
	    return false;
	} else {
	    return true;
	}
    }

    /** 
     * Write out the timestamp.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
	out.writeLong(value);
    }

    /** 
     */ 
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	value = in.readLong();
    }
}
