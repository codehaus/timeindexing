// ElapsedSecondTimestamp.java

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
public class ElapsedSecondTimestamp implements RelativeTimestamp, SecondScale, Serializable {
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
     * Construct a ElapsedSecondTimestamp with 'now' as the timestamp.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    public ElapsedSecondTimestamp() {
	value = 0 | Timestamp.ELAPSED_SECOND;
    }
	
    /**
     * Construct a ElapsedSecondTimestamp from a number of seconds.
     */
    public ElapsedSecondTimestamp(long valueTS) {
	long ts = 0;

	// check if top bits are set
	if ((valueTS & Timestamp.TOP_6_MASK) == ELAPSED_SECOND) {
	    // clear them
	    ts = valueTS & ~Timestamp.ELAPSED_SECOND;
	} else {
	    // take raw value
	    ts = valueTS;
	}

	if (ts >= 0) {
	    value = ts | Timestamp.ELAPSED_SECOND;
	} else {
	    value = Math.abs(ts) | Timestamp.ELAPSED_SECOND;
	    isNegative = true;
	}
       }
	
    /**
     * Construct a ElapsedSecondTimestamp from a number of seconds.
     */
    public ElapsedSecondTimestamp(long seconds, int nanoseconds) {
	if (seconds >= 0) {
	    value = seconds;
	    value |= Timestamp.ELAPSED_SECOND;
	} else {
	    value = Math.abs(seconds);
	    value |= Timestamp.ELAPSED_SECOND;
	    isNegative = true;
	}
    }

	
    /**
     * Get the number of seconds
     */
    public long getSeconds() {
	long valueT = value ^ Timestamp.ELAPSED_SECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    return (isNegative ? -valueT : valueT);
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     */
    public int getNanoSeconds() {
	// nothing to return at nanosecond scale
	//long valueT = value ^ Timestamp.ELAPSED_SECOND;

	return 0;
    }

    /**
     * Get the toString() version of a SecondTimestamp.
     */
    public String toString() {
	long seconds = value ^ Timestamp.ELAPSED_SECOND;

	return ("(" + (isNegative ? -seconds : seconds) +  ")");
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