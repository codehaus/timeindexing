// MillisecondTimestamp.java

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
 */
public class MillisecondTimestamp implements AbsoluteTimestamp, MillisecondScale, Serializable {
    /*
     * The mask used for before/after epoch
     */
    public final static long BEFORE_EPOCH = (long)0x01 << 61;

    /*
     * A format for milliseconds.  3 obligatory digits.
     */
    private static NumberFormat millisformat = new DecimalFormat("000");

    /*
     * A format for whole seconds
     */
    private static DateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    /*
     * The value of the timestamp
     */
    long value;

    /**
     * Construct a MillisecondTimestamp with 'now' as the timestamp.
     */
    public MillisecondTimestamp() {
	value = System.currentTimeMillis();
    }
	
    /**
     * Construct a MillisecondTimestamp from a number of milliseconds.
     */
    public MillisecondTimestamp(long ts) {
	value = ts;
    }
	
    /**
     * Construct a MillisecondTimestamp from a number of seconds
     * and a number of nanoseconds.
     */
    public MillisecondTimestamp(long seconds, int nanoseconds) {
	value = seconds * 1000;
	value += nanoseconds / 1000000;
    }
	
    /**
     * Construct a MillisecondTimestamp from a Date object
     */
    public MillisecondTimestamp(Date date) {
	value = date.getTime();
    }
	
    /**
     * Get the number of seconds
     */
    public long getSeconds() {
	if (value == 0) {
	    return 0;
	} else {
	    return value / 1000;
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     */
    public int getNanoSeconds() {
	if (value == 0) {
	    return 0;
	} else {
	    return ((int)(value % 1000)) * (int)1000000;
	}
    }

    /**
     * Get the toString() version of a MillisecondTimestamp.
     */
    public String toString() {
	long timevalue = value ^ Timestamp.MILLISECOND;
	long milliseconds = timevalue;
	long millisOnly = (timevalue % 1000);

	return ("[" + format.format(new Date(milliseconds)) + "." + millisformat.format(millisOnly) + "]");
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
