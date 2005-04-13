// ElapsedMicrosecondTimestamp.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;
import java.util.Date;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * A timestamp that only has significant data down to microsecond level.
 */
public class ElapsedMicrosecondTimestamp implements RelativeTimestamp, MicrosecondScale, Serializable {
    /*
     * The value of the timestamp
     */
    long value;

    /*
     * Is the value negative.
     */
    boolean isNegative = false;

    /**
     * Construct a ElapsedMicrosecondTimestamp with 'now' as the timestamp.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    public ElapsedMicrosecondTimestamp() {
	value = 0 | Timestamp.ELAPSED_MICROSECOND;
    }
	
    /**
     * Construct a ElapsedMicrosecondTimestamp from a number of microseconds.
     */
    public ElapsedMicrosecondTimestamp(long valueTS) {
	long ts = 0;

	// check if top bits are set
	if ((valueTS & Timestamp.TOP_4_MASK) == Timestamp.ELAPSED_MICROSECOND) {
	    // clear them
	    ts = valueTS & ~Timestamp.ELAPSED_MICROSECOND;
	} else {
	    // take raw value
	    ts = valueTS;
	}

	if (ts >= 0) {
	    value = ts | Timestamp.ELAPSED_MICROSECOND;
	} else {
	    value = Math.abs(ts) | Timestamp.ELAPSED_MICROSECOND;
	    isNegative = true;
	}
    }
	
    /**
     * Construct a ElapsedMicrosecondTimestamp from a Microsecond TimeSpecifier.
     */
    public ElapsedMicrosecondTimestamp(Microsecond timeSpecifier) {
	this((long)0, (int)(timeSpecifier.value()*1000));
    }

    /**
     * Construct a ElapsedMicrosecondTimestamp from a number of  seconds
     * and a number of nanoseconds.
     */
    public ElapsedMicrosecondTimestamp(long seconds, int nanoseconds) {
	long ts = 0;

	ts = seconds * 1000000;
	ts += nanoseconds / 1000;

	if (ts >= 0) {
	    value = ts | Timestamp.ELAPSED_MICROSECOND;
	} else {
	    value = Math.abs(ts) | Timestamp.ELAPSED_MICROSECOND;
	    isNegative = true;
	}
    }

    /**
     * Get the number of seconds
     */
    public long getSeconds() {
	long valueT = value ^ Timestamp.ELAPSED_MICROSECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    return (isNegative ? -valueT / 1000000 : valueT / 1000000);
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     */
    public int getNanoSeconds() {
	long valueT = value ^ Timestamp.ELAPSED_MICROSECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    if (valueT / 1000000 == 0) {  // zero seconds
		return  ((int)(isNegative? (-valueT % 1000000): (valueT % 1000000)))  * (int)1000;
	    } else {
		return ((int)(valueT % 1000000)) * (int)1000;
	    }
	}
    }

    /**
     * Get the Scale.
     */
    public Scale getScale() {
	return MicrosecondScale.SCALE;
    }

    /**
     * Get the toString() version of a MicrosecondTimestamp.
     */
    public String toString() {
	return ("(" + new MicrosecondElapsedFormat().format(this) + ")");
    }

    /**
     * Get the raw value.
     * Used in other parts of the implementation.
     */
    public long value() {
	return value;
    }

    /**
     * Is the Timestamp negative.
     */
    public boolean isNegative() {
	return isNegative;
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
