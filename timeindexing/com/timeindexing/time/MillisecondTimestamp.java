/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



// MillisecondTimestamp.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;
import java.util.Date;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * A timestamp that only has significant data down to millisecond level.
 * The Java runtime currently goes down to milliseconds,
 * so these are easy to get in Java.
 */
public class MillisecondTimestamp extends AbstractAbsoluteTimestamp implements AbsoluteTimestamp, MillisecondScale, Serializable {
    /*
     * The mask used for before/after epoch
     */
    public final static long BEFORE_EPOCH = Timestamp.MILLISECOND_SIGN;

    /*
     * The value of the timestamp
     */
    long value;

    /**
     * Construct a MillisecondTimestamp with 'now' as the timestamp.
     */
    public MillisecondTimestamp() {
	this(Clock.time);
    }
	
    /**
     * Construct a MillisecondTimestamp with 'now' as the timestamp,
     * using the specified Clock.
     */
    MillisecondTimestamp(Clock clock) {
	value = clock.getRawTime();
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
	// check to see if the no of seconds is in the correct range.
	if (Math.abs(seconds) > (Timestamp.MILLISECOND_SIGN/1000)) {   // the number is too big
	    throw new IllegalArgumentException("The number of seconds is out of range. The maximum size is " + (Timestamp.MILLISECOND_SIGN/1000));
	}
	    

	if (seconds < 0) {			// a before epoch time
	    value = (-seconds) * 1000;
	    value |= BEFORE_EPOCH;
	} else {				// an after epoch time
	    value = seconds * 1000;
	}

	// add on the nanoseconds
	value += (nanoseconds / 1000000);
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
	    if (isAfterEpoch()) {
		return value / 1000;
	    } else {
		// the time is before epoch
		// so throw away the BEFORE_EPOCH bit
		long valueB = value ^ BEFORE_EPOCH;
		return -(valueB / 1000);
	    }
	}
    }

    /**
     * Get the number of nanoseconds for this timestamp
     */
    public int getNanoSeconds() {
	if (value == 0) {
	    return 0;
	} else {
	    if (isAfterEpoch()) {
		return ((int)(value % 1000)) * (int)1000000;
	    } else {
		// the time is before epoch
		// so throw away the BEFORE_EPOCH bit
		long valueB = value ^ BEFORE_EPOCH;
		return ((int)(valueB % 1000)) * (int)1000000;

	    }
	}
    }

    /**
     * Get the Scale.
     */
    public Scale getScale() {
	return MillisecondScale.SCALE;
    }

    /**
     * Get the toString() version of a MillisecondTimestamp.
     */
    public String toString() {
	//return ("[" + new MillisecondDateFormat().format((Timestamp)this) + "]");
	return getEpoch().format(this);
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
