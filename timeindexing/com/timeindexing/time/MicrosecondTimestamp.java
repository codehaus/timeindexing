// MicrosecondTimestamp.java

package com.timeindexing.time;

import java.util.Date;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * A timestamp that only has significant data down to microsecond level.
 */
public class MicrosecondTimestamp implements AbsoluteTimestamp, MicrosecondScale, Serializable {
    /*
     * The mask used for before/after epoch
     */
    public final static long BEFORE_EPOCH = (long)0x01 << 61;

    /*
     * The value of the timestamp
     */
    long value;

    /**
     * Construct a MicrosecondTimestamp with 'now' as the timestamp.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    public MicrosecondTimestamp() {
	value = (System.currentTimeMillis() * (long)1000) | Timestamp.MICROSECOND;
    }
	
    /**
     * Construct a MicrosecondTimestamp from a number of microseconds.
     */
    public MicrosecondTimestamp(long ts) {
	value = ts | Timestamp.MICROSECOND;
    }
	
    /**
     * Construct a MicrosecondTimestamp from a number of seconds
     * and a number of nanoseconds.
     */
    public MicrosecondTimestamp(long seconds, int nanoseconds) {
	value = seconds * 1000000;
	value += nanoseconds / 1000;
	value |= Timestamp.MICROSECOND;
    }


    /**
     * Construct a MicrosecondTimestamp from a Date object.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    public MicrosecondTimestamp(Date date) {
	value = (date.getTime() * (long)1000) | Timestamp.MICROSECOND;
    }
	
    /**
     * Get the number of seconds
     */
    public long getSeconds() {
	long valueT = value ^ Timestamp.MICROSECOND;

	if (valueT == 0) {
	    return 0;
	} else {
	    return valueT / 1000000;
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     */
    public int getNanoSeconds() {
	long valueT = value ^ Timestamp.MICROSECOND;

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
	return ("[" + new MicrosecondDateFormat().format(this) + "]");
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
