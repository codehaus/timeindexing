// SecondTimestamp.java

package com.timeindexing.time;

import java.util.Date;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * A timestamp that only has significant data down to second level.
 */
public class SecondTimestamp implements AbsoluteTimestamp, SecondScale, Serializable {
    /*
     * The mask used for before/after epoch
     */
    public final static long BEFORE_EPOCH = (long)0x01 << 59;

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
	value = (System.currentTimeMillis() / 1000) | Timestamp.SECOND;
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
	value = seconds;
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
	    return valueT;
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     */
    public int getNanoSeconds() {
	long valueT = value ^ Timestamp.SECOND;

	return 0;
    }

    /**
     * Get the toString() version of a SecondTimestamp.
     */
    public String toString() {
	return ("[" + new SecondDateFormat().format(this) + "]");
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
