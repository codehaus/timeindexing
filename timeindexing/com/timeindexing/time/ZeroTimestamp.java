// ZeroTimestamp.java

package com.timeindexing.time;

import java.util.Date;
import java.io.Serializable;

/**
 * A timestamp that is only zero
 */
public final class ZeroTimestamp extends MillisecondTimestamp implements Timestamp, Serializable {
    protected ZeroTimestamp() {
	super(0);
    }

    /**
     * Get the toString() version of a ZeroTimestamp.
     */
    public String toString() {
	return "ZERO";
    }
}
