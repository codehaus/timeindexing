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
// Clock.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;
import java.util.TimeZone;

/**
 * A simple wall clock class that returns AbsoluteTimestamps.
 */
public class Clock {
    /**
     * A publically usable clock that tells the time.
     */
    public static final Clock time = new Clock();

    /**
     * A single tick of a clock.
     * This is 1 unit of the lowest resolution that TimeIndexing can handle.
     * In the current implementation it is 1 nanosecond.
     */
    public static final Timestamp TICK = new OneTick();

    /**
     * The zero point on the clock.
     */
    public static final Timestamp ZERO = new ZeroTimestamp();


    /**
     * A TimeZone for getting the current DST offset
     */
    private TimeZone timeZone = null;

    /*
     * The DST offset currently
     */
    private int dstOffset = 0;

    /**
     * The next time to check if the DST has changed.
     * We check the dstOffset hourly, to see if it has changed
     * during the running of the program.
     * This  is done because doing TimeZone.getOffset() for
     * each call to getRawTime() is too expensive.
     */
    private long dstOffsetCheckTime = 0;

    /**
     * Construct a Clock
     */
    public Clock() {
	timeZone = TimeZone.getDefault();
	processDSTOffset(System.currentTimeMillis());
    }

    /**
     * Construct a Clock in a specific TimeZone
     */
    public Clock(TimeZone tz) {
	timeZone = tz;
	processDSTOffset(System.currentTimeMillis());
    }

    /**
     * Return the current time.
     * The Scale is the best that the platform will support naturally.
     * Currently, Java only resolves times to milliseconds,
     * so the Scale is MillisecondScale.
     */
    public Timestamp time(){
	return asMillis();
    }

    /**
     * Get the system time.
     * This gets the clock to the best resolution that the platform will 
     * support. Currently, Java only resolves times to milliseconds.
     */
    long getRawTime() {
	long time = System.currentTimeMillis();

	// check to see if we need to refetch the DST offset
	if (time > dstOffsetCheckTime) {
	    // we've gone past the check time
	    // determine the dstOffset
	    // and set the new check time
	    processDSTOffset(time);
	}

	return time + dstOffset;
    }
    
    /**
     * Get the DST offset and set the new time to check 
     * for the offset again.
     */
    private void processDSTOffset(long time) {
	final int anHour = 60 * 60 * 1000;
	dstOffset = timeZone.getOffset(time);

	dstOffsetCheckTime = (time / anHour + 1) * anHour;

	//System.err.println("time = " + time + " dstOffset = " + dstOffset + " dstOffsetCheckTime = " + dstOffsetCheckTime);
    }
       
    /**
     * Convert a Timestamp to a specific Scale.
     */
    public Timestamp asScale(Scale scale) {
	if (scale instanceof SecondScale) {
	    return asSeconds();
	} else 	if (scale instanceof MillisecondScale) {
	    return asMillis();
	} else if (scale instanceof MicrosecondScale) {
	    return asMicros();
	} else if (scale instanceof NanosecondScale) {
	    return asNanos();
	} else {
	    throw new Error("Unhandled type of scale for argument to asScale(). It is: " +
			    scale.getClass().getName());
	}
    }

    /**
     * Get the current time resolved to seconds.
     */
    public Timestamp asSeconds() {
	return new SecondTimestamp(this);
    }

    /**
     * Get the current time resolved to milliseconds.
     */
    public Timestamp asMillis() {
	return new MillisecondTimestamp(this);
    }

    
     /**
     * Get the current time resolved to microseconds.
     */
    public Timestamp asMicros() {
	return new MicrosecondTimestamp(this);
    }

    /**
     * Get the current time resolved to nanoseconds.
     */
    public Timestamp asNanos() {
	return new NanosecondTimestamp(this);
    }

}

/**
 * A special class for one clock tick.
 */
class OneTick extends NanosecondTimestamp {
    protected OneTick() {
	super(1);
    }

    public String toString() {
	return "0.00000001";
    }
}
