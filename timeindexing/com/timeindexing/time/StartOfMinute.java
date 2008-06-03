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



// StartOfMinute.java

package com.timeindexing.time;

/**
 * A TimeSpecifier for start of minute.
 */
public class StartOfMinute extends AbstractTimeSpecifier implements TimeSpecifier {
    /**
     * Construct a StartOfMinute TimeSpecifier.
     */
    public StartOfMinute() {
	setHowMany(1);
	setDirection(TimeDirection.BACKWARD);
    }

    /**
     * Construct a StartOfMinute TimeSpecifier.
     */
    public StartOfMinute(TimeSpecifier modifier) {
	setHowMany(1);
	setDirection(TimeDirection.BACKWARD);
	afterDoing(modifier);
    }

    /**
     * Instantiate this TimeSpecifier w.r.t a particular Timestamp.
     * It returns a Timestamp which has been modified by the amount of the TimeSpecifier.
     */
    public Timestamp instantiate(Timestamp t) {
	Timestamp timestampToUse = null;

	// determine what timestamp to use
	if (getPredecessor() == null) {
	    // there is no sub modifier so we use t directly
	    timestampToUse = t;
	} else {
	    // there is a sub modifier, so we use the Timestamp 
	    // returned by instantiating the getPredecessor() with t
	    timestampToUse = getPredecessor().instantiate(t);
	}

	Timestamp returnTimestamp = null;

	// to get to the start of a minute we need to round 
	// the no of seconds in timestampToUse to the
	// the no of seconds in a minute

	// seconds per minute
	long secondsPerMinute = 60;

	// get the no of seconds
	// this is actually start of minute backward
	long remainingSeconds = timestampToUse.getSeconds() / secondsPerMinute * secondsPerMinute;

	// convert the remaining seconds to a Timestamp
	returnTimestamp = new SecondTimestamp(remainingSeconds);

	return returnTimestamp;
    }

    /**
     * To String
     */
    /**
     * To String
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(128);

	if (hasPredecessor()) {
	    buffer.append(getPredecessor().toString());
	    buffer.append(" then ");
	}

	buffer.append(" start of minute ");

	return buffer.toString();
    }

}

