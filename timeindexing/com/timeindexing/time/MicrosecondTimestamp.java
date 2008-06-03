Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
// MicrosecondTimestamp.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;
import java.util.Date;
import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

/**
 * A timestamp that only has significant data down to microsecond level.
 */
public class MicrosecondTimestamp extends AbstractAbsoluteTimestamp implements AbsoluteTimestamp, MicrosecondScale, Serializable {
    /*
     * The mask used for before/after epoch
     */
    public final static long BEFORE_EPOCH = Timestamp.MICROSECOND_SIGN;

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
	this(Clock.time);
    }

    /**
     * Construct a MicrosecondTimestamp with 'now' as the timestamp.
     * The Java runtime currently goes down to milliseconds,
     * so these are a bit inaccurate.
     */
    MicrosecondTimestamp(Clock clock) {
	value = (clock.getRawTime() * (long)1000) | Timestamp.MICROSECOND;
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
	// check to see if the no of seconds is in the correct range.
	if (Math.abs(seconds) > (Timestamp.MICROSECOND_SIGN/1000000)) {   // the number is too big
	    throw new IllegalArgumentException("The number of seconds is out of range. The maximum size is " + (Timestamp.MICROSECOND_SIGN/1000000));
	}
	    

	if (seconds < 0) {			// a before epoch time
	    value = (-seconds) * 1000000;
	    value |= BEFORE_EPOCH;
	} else {
	    value = seconds * 1000000;
	}

	// add on the nanoseconds
	value += nanoseconds / 1000;
	// set mask
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
	    if (isAfterEpoch()) {
		return valueT / 1000000;
	    } else {
		// the time is before epoch
		// so throw away the BEFORE_EPOCH bit
		long valueB = valueT ^ BEFORE_EPOCH;
		return -(valueB / 1000000);
	    }
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
	    if (isAfterEpoch()) {
		return ((int)(valueT % 1000000)) * (int)1000;
	    } else {
		// the time is before epoch
		// so throw away the BEFORE_EPOCH bit
		long valueB = valueT ^ BEFORE_EPOCH;
		return ((int)(valueB % 1000000)) * (int)1000;
	    }
	}
    }

    /**
     * Get the Scale.
     */
    public Scale getScale() {
	return MicrosecondScale.SCALE;
    }

    /**
     * Get the toString() version of a MicrosecondTimestamp.
     */
    public String toString() {
	//return ("[" + new MicrosecondDateFormat().format((Timestamp)this) + "]");
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
