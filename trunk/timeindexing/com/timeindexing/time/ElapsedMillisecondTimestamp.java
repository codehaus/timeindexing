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
// ElapsedMillisecondTimestamp.java

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
 * A value of zero is 0.000, not Jan 1st 1970.
 */
public class ElapsedMillisecondTimestamp implements RelativeTimestamp, MillisecondScale,  Serializable {
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
     * Construct a ElapsedTimestamp from a Millisecond TimeSpecifier.
     */
    public ElapsedMillisecondTimestamp(Millisecond timeSpecifier) {
	this((long)0, (int)(timeSpecifier.value()*1000000));
    }

    /**
     * Construct a  ElapsedMillisecondTimestamp from a number of seconds
     * and a number of nanoseconds.
     */
    public  ElapsedMillisecondTimestamp(long seconds, int nanoseconds) {
	long ts = 0;

	ts = seconds * 1000;
	ts += nanoseconds / 1000000;


	if (ts >= 0) {
	    value = ts | Timestamp.ELAPSED_MILLISECOND;	
	} else {
	    value = Math.abs(ts) | Timestamp.ELAPSED_MILLISECOND;
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
	    if (valueT / 1000 == 0) {  // zero seconds
		return  ((int)(isNegative? (-valueT % 1000): (valueT % 1000)))  * (int)1000000;
	    } else {
		return ((int)(valueT % 1000)) * (int)1000000;
	    }
	}
    }

    /**
     * Does the Timestamp specify a negative time.
     */
    public boolean isNegative() {
	return isNegative;
    }

    /**
     * Get the Scale.
     */
    public Scale getScale() {
	return MillisecondScale.SCALE;
    }

    /**
     * Get the toString() version of a ElapsedTimestamp.
     */
    public String toString() {
	return ("(" + new MillisecondElapsedFormat().format(this) + ")");
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
