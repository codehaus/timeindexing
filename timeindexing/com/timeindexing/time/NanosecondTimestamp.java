// NanosecondTimestamp.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;
import java.util.Date;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * A timestamp that only has significant data down to nanosecond level.
 */
public class NanosecondTimestamp extends AbstractAbsoluteTimestamp implements AbsoluteTimestamp, NanosecondScale, Serializable {
    /*
     * The mask used for before/after epoch
     */
    public final static long BEFORE_EPOCH = Timestamp.NANOSECOND_SIGN;

    /*
     * The value of the timestamp
     */
    long value;

    /**
     * Construct a NanosecondTimestamp with 'now' as the timestamp.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    public NanosecondTimestamp() {
	this(Clock.time);
    }
	
    /**
     * Construct a NanosecondTimestamp with 'now' as the timestamp.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    NanosecondTimestamp(Clock clock) {
	value = (clock.getRawTime() * (long)1000000) | Timestamp.NANOSECOND;
    }
	
    /**
     * Construct a NanosecondTimestamp from a number of nanoseconds.
     */
    public NanosecondTimestamp(long ts) {
	value = ts | Timestamp.NANOSECOND;
    }
	
    /**
     * Construct a NanosecondTimestamp from a number of seconds
     * and a number of nanoseconds.
     */
    public NanosecondTimestamp(long seconds, int nanoseconds) {
	// check to see if the no of seconds is in the correct range.
	if (Math.abs(seconds) > (Timestamp.NANOSECOND_SIGN/1000000000)) {   // the number is too big
	    throw new IllegalArgumentException("The number of seconds is out of range. The maximum size is " + (Timestamp.NANOSECOND_SIGN/1000000000));
	}

	if (seconds < 0) {			// a before epoch time
	    value = (-seconds) * 1000000000;
	    value |= BEFORE_EPOCH;
	} else {
	    value = seconds * 1000000000;
	}

	// add on the nanoseconds
	value += nanoseconds;
	// set mask
	value |= Timestamp.NANOSECOND;
    }
	

    /**
     * Construct a NanosecondTimestamp from a Date object.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    public NanosecondTimestamp(Date date) {
	value = (date.getTime() * (long)1000000) | Timestamp.NANOSECOND;
    }
	
    /**
     * Get the number of seconds
     */
    public long getSeconds() {
	long valueT = value ^ Timestamp.NANOSECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    if (isAfterEpoch()) {
		return valueT / 1000000000;
	    } else {
		// the time is before epoch
		// so throw away the BEFORE_EPOCH bit
		long valueB = valueT ^ BEFORE_EPOCH;
		return -(valueB / 1000000000);
	    }
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     */
    public int getNanoSeconds() {
	long valueT = value ^ Timestamp.NANOSECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    if (isAfterEpoch()) {
		return ((int)(valueT % 1000000000)) * (int)1;
	    } else {
		// the time is before epoch
		// so throw away the BEFORE_EPOCH bit
		long valueB = valueT ^ BEFORE_EPOCH;
		return ((int)(valueB % 1000000000)) * (int)1;
	    }
	}
    }

    /**
     * Get the Scale.
     */
    public Scale getScale() {
	return NanosecondScale.SCALE;
    }

    /**
     * Get the toString() version of a NanosecondTimestamp.
     * This formats the first day specially.
     */
    public String toString() {
	//return ("[" + new NanosecondDateFormat().format((Timestamp)this) + "]");
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
