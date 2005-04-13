// IntervalSpecifier.java

package com.timeindexing.time;

import com.timeindexing.basic.Interval;

/**
 * An interface for objects that specify some specifier for an Interval
 */
public interface  IntervalSpecifier extends Cloneable {
    /**
     * Get the start of interval TimeSpecifier.
     */
    public TimeSpecifier getStartSpecifier();

    /**
     * Get the end of interval TimeSpecifier.
     */
    public TimeSpecifier getEndSpecifier();

    /**
     * Instantiate this IntervalSpecifier w.r.t a particular Timestamp.
     * It returns a Interval which has been modified by 
     */
    public Interval instantiate(AbsoluteTimestamp t);
}
