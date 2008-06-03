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



// TimestampGenerator.java

package com.timeindexing.time;

/**
 * This class create a Timestamp object of the best fit resolution given the no. of
 * seconds, the no. of subseconds, and whether the Timestamp  should
 * be an Absolute Timestamp or a Relative Timestamp.
 */
public class TimestampGenerator {
    /**
     * Construct a TimestampGenerator.
     */
    public TimestampGenerator() {
    }

    /**
     * Actually create the Timestamp given the seconds, the subseconds,
     * and whether the Timestamp should be absolute or relative.
     */
    public Timestamp createTimestamp(long seconds, int subSeconds, boolean absolute) {
	Timestamp timestamp =  null;

	//System.err.println("Seconds = " + seconds + " Subseconds = " + subSeconds);
	// create a timestamp with the relvant granulariy

	if (subSeconds == 0) {
	    // time string was seconds.
	    if (absolute) {
		timestamp = new SecondTimestamp(seconds, subSeconds);
	    } else { // elapsed 
		timestamp = new ElapsedSecondTimestamp(seconds, subSeconds);
	    }
	} else if (subSeconds < 1000) {
	    // time string was seconds.sss
	    if (absolute) {
		timestamp = new MillisecondTimestamp(seconds, subSeconds*1000000);
	    } else { // elapsed 
		timestamp = new ElapsedMillisecondTimestamp(seconds, subSeconds*1000000);
	    }
	} else if (subSeconds < 1000000) {
	    // time string was seconds.ssssss
	    if (absolute) {
		timestamp = new MicrosecondTimestamp(seconds, subSeconds*1000);
	    } else { // elapsed 
		timestamp = new ElapsedMicrosecondTimestamp(seconds, subSeconds*1000);
	    }
	} else if (subSeconds < 1000000000) {
	    // time string was seconds.sssssssss
	    if (absolute) {
		timestamp = new NanosecondTimestamp(seconds, subSeconds);
	    } else { // elapsed 
		timestamp = new ElapsedNanosecondTimestamp(seconds, subSeconds);
	    }
	} else {
	    // we can;t do more than nanoseconds at present
	    // so welll take the first 9 digits
	    if (absolute) {
		timestamp = new NanosecondTimestamp(seconds, subSeconds);
	    } else { // elapsed 
		timestamp = new ElapsedNanosecondTimestamp(seconds, subSeconds);
	    }

	}

	return timestamp;
    }
}
