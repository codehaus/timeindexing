// ElapsedMicrosecondTimestamp.java

package com.timeindexing.time;

import java.util.Date;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * A timestamp that only has significant data down to microsecond level.
 */
public class ElapsedMicrosecondTimestamp implements RelativeTimestamp, MicrosecondScale, Serializable {
    /*
     * A format for microseconds.  6 obligatory digits.
     */
    private static NumberFormat microsformat = new DecimalFormat("000000");

    /*
     * A format for whole seconds
     */
    private static DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

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
     * Construct a ElapsedMicrosecondTimestamp from a number of  seconds
     * and a number of nanoseconds.
     */
    public ElapsedMicrosecondTimestamp(long seconds, int nanoseconds) {
	if (seconds >= 0) {
	    value = seconds * 1000000;
	    value += nanoseconds / 1000;
	    value |= Timestamp.ELAPSED_MICROSECOND;
	} else {
	    value = Math.abs(seconds) * 1000000;
	    value += nanoseconds / 1000;
	    value |= Timestamp.ELAPSED_MICROSECOND;
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
	    return ((int)(valueT % 1000000)) * (int)1000;
	}
    }

    /**
     * Get the toString() version of a MicrosecondTimestamp.
     */
    public String toString() {
	long valueT = value ^ Timestamp.ELAPSED_MICROSECOND;

	long seconds = valueT / 1000000;
	long microsOnly = (valueT % 1000000);

	return ("(" + (isNegative ? -seconds : seconds) + "." + microsformat.format(microsOnly) + ")");
    }

    /**
     * Get the raw value.
     * Used in other parts of the implementation.
     */
    public long value() {
	return value;
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
