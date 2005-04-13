// MicrosecondScale.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;

/**
 * A time that is in the Microsecond Scale.
 */
public interface MicrosecondScale extends Scale {
    public MicrosecondScale SCALE = new MicrosecondScale() {
	    /**
	     * Scale is 10 ^ -6
	     */
	    public long value() {
		return 1000000;
	    }

	    public String toString() {
		return "Microsecond Scale";
	    }

	};
}
