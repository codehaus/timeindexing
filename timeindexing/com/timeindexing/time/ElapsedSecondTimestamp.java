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



// ElapsedSecondTimestamp.java

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
public class ElapsedSecondTimestamp implements RelativeTimestamp, SecondScale, Serializable {
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
     * Construct a ElapsedSecondTimestamp from a Second TimeSpecifier.
     */
    public ElapsedSecondTimestamp(Second timeSpecifier) {
	this((long)timeSpecifier.value(), (int)0);
    }

    /**
     * Construct a ElapsedSecondTimestamp from a number of seconds.
     */
    public ElapsedSecondTimestamp(long seconds, int nanoseconds) {
	long ts = 0;

	ts  = seconds + (nanoseconds / 1000000000);

	if (ts >= 0) {
	    value = ts | Timestamp.ELAPSED_SECOND;
	} else {
	    value = Math.abs(ts) | Timestamp.ELAPSED_SECOND;
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
     * Get the Scale.
     */
    public Scale getScale() {
	return SecondScale.SCALE;
    }

    /**
     * Get the toString() version of a SecondTimestamp.
     */
    public String toString() {
	return ("(" + new SecondElapsedFormat().format(this) + ")");
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
