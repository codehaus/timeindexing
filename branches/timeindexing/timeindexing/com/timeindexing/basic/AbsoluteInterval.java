// AbsoluteInterval.java

package com.timeindexing.basic;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.AbsoluteTimestamp;
import com.timeindexing.time.RelativeTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.Lifetime;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexTimestampSelector;

/**
 * An absolute interval is an interval where all the arguments
 * can be resolved into two positions into an Index, and none
 * of the specifiers can be adjusted.
 * <p>
 * TODO: finish implementations.
 * <p>
 */
public abstract class AbsoluteInterval implements Interval {
    /*
     * The start absolute position.
     */
    Position start = null;

    /*
     * The end absolute position.
     */
    Position end = null;

    /*
     * Is this Interval fully resolved with two Positions.
     */
    boolean resolved = false;

    /*
     * What style was the Interval specified as.
     */
    int style = -1;  // start with an illegal value

    /**
     * Get the interval start.
     */
    public Value start() {
	return start;
    }

    /**
     * Get the interval end.
     */
    public Value end() {
	return end;
    }


    /**
     * Has this interval been resolved to two Positions.
     * This needs to be done before an Interval can be used.
     */
    public boolean isResolved() {
	return resolved;
    }

    /**
     * Resolve this interval w.r.t a specified index.
     * Returns a clone with resolved positions.
     */
    public abstract AbsoluteInterval resolve(Index index, IndexTimestampSelector selector, Lifetime lifetime);
}
