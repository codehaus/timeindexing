// ElapsedNanosecondTimestamp.java

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
 * A timestamp that only has significant data down to nanosecond level.
 */
public class ElapsedNanosecondTimestamp implements RelativeTimestamp, NanosecondScale, Serializable {
    /*
     * A format for nanoseconds.  6 obligatory digits.
     */
    private static NumberFormat nanosformat = new DecimalFormat("000000000");

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
     * Construct a ElapsedNanosecondTimestamp with 'now' as the timestamp.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    public ElapsedNanosecondTimestamp() {
	value = 0 | Timestamp.ELAPSED_NANOSECOND;
    }
	
    /**
     * Construct a ElapsedNanosecondTimestamp from a number of nanoseconds.
     */
    public ElapsedNanosecondTimestamp(long valueTS) {
	long ts = 0;

	// check if top bits are set
	if ((valueTS & Timestamp.TOP_6_MASK) == ELAPSED_NANOSECOND) {
	    // clear them
	    ts = valueTS & ~Timestamp.ELAPSED_NANOSECOND;
	} else {
	    // take raw value
	    ts = valueTS;
	}

	if (ts >= 0) {
	    value = ts | Timestamp.ELAPSED_NANOSECOND;
	} else {
	    value = Math.abs(ts) | Timestamp.ELAPSED_NANOSECOND;
	    isNegative = true;
	}
    }
	
   /**
     * Construct a ElapsedNanosecondTimestamp from a number of seconds
     * and a number of  nanoseconds.
     */
    public ElapsedNanosecondTimestamp(long seconds, int nanoseconds) {
	if (seconds >= 0) {
	    value = seconds * 1000000000;
	    value += nanoseconds;
	    value |= Timestamp.ELAPSED_NANOSECOND;
	} else {
	    value = Math.abs(seconds) * 1000000000;
	    value += nanoseconds;
	    value |= Timestamp.ELAPSED_NANOSECOND;
	    isNegative = true;
	}
    }

    /**
     * Get the number of seconds
     */
    public long getSeconds() {
	long valueT = value ^ Timestamp.ELAPSED_NANOSECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    return (isNegative ? -valueT / 1000000000 : valueT / 1000000000);
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     */
    public int getNanoSeconds() {
	long valueT = value ^ Timestamp.ELAPSED_NANOSECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    return ((int)(valueT % 1000000000)) * (int)1;
	}
    }

    /**
     * Get the toString() version of a NanosecondTimestamp.
     */
    public String toString() {
	long valueT = value ^ Timestamp.ELAPSED_NANOSECOND;

	long seconds = valueT / 1000000000;
	long nanosOnly = (valueT % 1000000000);

	return ("(" + (isNegative ? -seconds : seconds) + "." + nanosformat.format(nanosOnly) + ")");
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
