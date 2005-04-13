// MillisecondScale.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;

/**
 * A time that is in the Millisecond Scale.
 */
public interface MillisecondScale extends Scale {
    public MillisecondScale SCALE = new MillisecondScale() {
	    /**
	     * Scale is 10 ^ -3
	     */
	    public long value() {
		return 1000;
	    }

	    public String toString() {
		return "Millisecond Scale";
	    }
	};

}
