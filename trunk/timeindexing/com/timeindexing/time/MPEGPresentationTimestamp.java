// ElapsedNanosecondTimestamp.java

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
public class MPEGPresentationTimestamp implements RelativeTimestamp, UnitBasedTimestamp,  NanosecondScale, Serializable {
    /*
     * The no of units per second for MPEGPresentationTimestamps,
     * called PTS in the literature.
     */
    private final static long unitsPerSecond = 90000;

    /*
     * The amount of time one unit takes.
     */
    private final static double timePerUnit = ((double)1) / unitsPerSecond * 1000000000;
       
    /*
     * The scale of this Timestamp.
     */
    private final static Scale SCALE = new Scale() {
	    public long value() {
		return -unitsPerSecond;
	    };
	};

    /*
     * The value of the timestamp in units from zero.
     */
    long value;

    /*
     * Is the value negative.
     */
    boolean isNegative = false;

    /**
     * Construct a MPEGPresentationTimestamp with 'now' as the timestamp.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    public MPEGPresentationTimestamp() {
	value = 0 | Timestamp.ELAPSED_UNITS;
    }
	
    /**
     * Construct a MPEGPresentationTimestamp from a number of units
     */
    public MPEGPresentationTimestamp(long valueTS) {
	long ts = 0;

	// check if top bits are set
	if ((valueTS & Timestamp.TOP_8_MASK) == ELAPSED_UNITS) {
	    // clear them
	    ts = valueTS & ~Timestamp.ELAPSED_UNITS;
	} else {
	    // take raw value
	    ts = valueTS;
	}

	if (ts >= 0) {
	    value = ts | Timestamp.ELAPSED_UNITS;
	} else {
	    value = Math.abs(ts) | Timestamp.ELAPSED_UNITS;
	    isNegative = true;
	}
    }
	
   /**
     * Construct a MPEGPresentationTimestamp from a number of seconds
     * and a number of  nanoseconds.
     */
    public MPEGPresentationTimestamp(long seconds, int nanoseconds) {
	long ts = 0;

	ts = seconds * unitsPerSecond;
	ts += (int)(nanoseconds / timePerUnit);

	if (ts >= 0) {
	    value = ts |  Timestamp.ELAPSED_UNITS;
	} else {
	    value = Math.abs(ts) | Timestamp.ELAPSED_UNITS;
	    isNegative = true;
	}
    }

    /**
     * Get the number of seconds
     */
    public long getSeconds() {
	long valueT = value ^ Timestamp.ELAPSED_UNITS;

	if (valueT == 0) {
	    return 0;
	} else {
	    return (isNegative ? ((long)(-valueT / unitsPerSecond)) : ((long)(valueT / unitsPerSecond)));
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     */
    public int getNanoSeconds() {
	long valueT = value ^ Timestamp.ELAPSED_UNITS;

	if (valueT == 0) {
	    return 0;
	} else {
	    if (valueT / unitsPerSecond == 0) {  // zero seconds
		return  (int)(isNegative? ((-valueT % unitsPerSecond) * timePerUnit) : ((valueT % unitsPerSecond) * timePerUnit));
	    } else {
		return  (int)((valueT % unitsPerSecond) * timePerUnit);
	    }
	}
    }

    /**
     * Get the no of units that have elapsed.
     */
    public long getUnits() {
	long valueT = value ^ Timestamp.ELAPSED_UNITS;

	if (valueT == 0) {
	    return 0;
	} else {
	    return (isNegative ? -valueT  : valueT );
	}
    }

    /**
     * Get the no of units per second, for this kind of timestamp.
     */
    public long getUnitsPerSecond() {
	return unitsPerSecond;
    }


    /**
     * Get the Scale.
     */
    public Scale getScale() {
	return this.SCALE;
    }

    /**
     * Get the toString() version of a NanosecondTimestamp.
     */
    public String toString() {
	return ("(" + (isNegative ? "-" : "") + getUnits() + ")");
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
