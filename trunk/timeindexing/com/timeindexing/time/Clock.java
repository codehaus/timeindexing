// Clock.java

package com.timeindexing.time;

/**
 * A simple wall clock class.
 */
public class Clock {
    /**
     * A publically usable clock that tells the time.
     */
    public static final Clock time = new Clock();

    /**
     * Get the current time resolved to seconds.
     */
    public Timestamp asSeconds() {
	return new SecondTimestamp();
    }

    /**
     * Get the current time resolved to milliseconds.
     */
    public Timestamp asMillis() {
	return new MillisecondTimestamp();
    }

    
     /**
     * Get the current time resolved to microseconds.
     */
    public Timestamp asMicros() {
	return new MicrosecondTimestamp();
    }

    /**
     * Get the current time resolved to nanoseconds.
     */
    public Timestamp asNanos() {
	return new NanosecondTimestamp();
    }

    
}
