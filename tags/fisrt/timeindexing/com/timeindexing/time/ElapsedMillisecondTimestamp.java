// ElapsedMillisecondTimestamp.java

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
 * A timestamp that only has significant data down to millisecond level.
 * The Java runtime currently goes down to milliseconds,
 * so these are easy to get in Java.
 * A value of zero is 0.000, not Jan 1st 1970.
 */
public class ElapsedMillisecondTimestamp implements RelativeTimestamp, MillisecondScale,  Serializable {
    /*
     * A format for milliseconds.  3 obligatory digits.
     */
    private static NumberFormat millisformat = new DecimalFormat("000");

    /*
     * A format for whole seconds
     */
    private static DateFormat format = new SimpleDateFormat("sss");

    /*
     * The value of the timestamp
     */
    long value;

    /*
     * Is the value negative.
     */
    boolean isNegative = false;

    /**
     * Construct a ElapsedTimestamp with nowasthe timestamp.
     */
    public ElapsedMillisecondTimestamp() {
	value = 0 | Timestamp.ELAPSED_MILLISECOND;
    }
	
    /**
     * Construct a ElapsedTimestamp from a number of milliseconds.
     */
    public ElapsedMillisecondTimestamp(long valueTS) {
	long ts = 0;

	// check if top bits are set
	if ((valueTS & Timestamp.TOP_4_MASK) == ELAPSED_MILLISECOND) {
	    // clear them
	    ts = valueTS & ~Timestamp.ELAPSED_MILLISECOND;
	} else {
	    // take raw value
	    ts = valueTS;
	}

	if (ts >= 0) {
	    value = ts | Timestamp.ELAPSED_MILLISECOND;	
	} else {
	    value = Math.abs(ts) | Timestamp.ELAPSED_MILLISECOND;
	    isNegative = true;
	}
    }
	
    /**
     * Construct a  ElapsedMillisecondTimestamp from a number of seconds
     * and a number of nanoseconds.
     */
    public  ElapsedMillisecondTimestamp(long seconds, int nanoseconds) {
	if (seconds >= 0) {
	    value = seconds * 1000;
	    value += nanoseconds / 1000000;
	    value |= Timestamp.ELAPSED_MILLISECOND;
	} else {
	    value = Math.abs(seconds) * 1000;
	    value += nanoseconds / 1000000;
	    value |= Timestamp.ELAPSED_MILLISECOND;
	    isNegative = true;
	}
    }

    /**
     * Get the number of seconds
     */
    public long getSeconds() {
	long valueT = value ^ Timestamp.ELAPSED_MILLISECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    return (isNegative ? -valueT / 1000 : valueT / 1000);
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     */
    public int getNanoSeconds() {
	long valueT = value ^ Timestamp.ELAPSED_MILLISECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    return ((int)(valueT % 1000)) * (int)1000000;
	}
    }

    /**
     * Get the toString() version of a ElapsedTimestamp.
     */
    public String toString() {
	long valueT = value ^ Timestamp.ELAPSED_MILLISECOND;

	long seconds = valueT / 1000;
	long millisOnly = (valueT % 1000);

	
	return ("(" + (isNegative ? -seconds : seconds) + "." + millisformat.format(millisOnly) + ")");
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
