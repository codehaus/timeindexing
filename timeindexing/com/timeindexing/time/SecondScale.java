// SecondScale.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;

/**
 * A time that is in the Second Scale.
 */
public interface SecondScale extends Scale {
    public SecondScale SCALE = new SecondScale() {
	    /**
	     * Scale is 10 ^ 0
	     */
	    public long value() {
		return 1;
	    }

	    public String toString() {
		return "Second Scale";
	    }

	};
}
