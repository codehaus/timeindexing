// NanosecondScale.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;

/**
 * A time that is in the Nanosecond Scale.
 */
public interface NanosecondScale extends Scale {
    public NanosecondScale SCALE = new NanosecondScale() {
	    /**
	     * Scale is 10 ^ -9
	     */
	    public long value() {
		return 1000000000;
	    }

	    public String toString() {
		return "Nanosecond Scale";
	    }

	};
}
