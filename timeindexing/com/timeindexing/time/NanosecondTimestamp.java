// NanosecondTimestamp.java

package com.timeindexing.time;

import java.util.Date;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * A timestamp that only has significant data down to nanosecond level.
 */
public class NanosecondTimestamp implements AbsoluteTimestamp, NanosecondScale, Serializable {
    /*
     * The mask used for before/after epoch
     */
    public final static long BEFORE_EPOCH = (long)0x01 << 61;

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
	value = (System.currentTimeMillis() * (long)1000000) | Timestamp.NANOSECOND;
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
	value = seconds * 1000000000;
	value += nanoseconds;
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
	    return valueT / 1000000000;
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
	    return ((int)(valueT % 1000000000)) * (int)1;
	}
    }

    /**
     * Get the toString() version of a NanosecondTimestamp.
     * This formats the first day specially.
     */
    public String toString() {
        long timevalue =  value ^ Timestamp.NANOSECOND;
	long milliseconds = timevalue / 1000000;

	return ("[" + NanosecondDateFormat.format(this) + "]");
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
