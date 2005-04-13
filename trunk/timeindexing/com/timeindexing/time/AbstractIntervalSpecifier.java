// AbstractIntervalSpecifier.java

package com.timeindexing.time;

import com.timeindexing.basic.Interval;

/**
 * An interface for objects that specify some specifier for an Interval
 */
public abstract class AbstractIntervalSpecifier implements IntervalSpecifier {
    // The start TimeSpecifier
    TimeSpecifier startSpecifier = null;

    // The end TimeSpecifier
    TimeSpecifier endSpecifier = null;

    /**
     * Get the start of interval TimeSpecifier.
     */
    public TimeSpecifier getStartSpecifier() {
	return startSpecifier;
    }

    /**
     * Set the start of interval TimeSpecifier.
     */
    void setStartSpecifier(TimeSpecifier start) {
	startSpecifier = start;
    }

    /**
     * Get the end of interval TimeSpecifier.
     */
    public TimeSpecifier getEndSpecifier() {
	return endSpecifier;
    }

    /**
     * Get the end of interval TimeSpecifier.
     */
    void setEndSpecifier (TimeSpecifier end) {
	endSpecifier = end;
    }

    /**
     * Instantiate this IntervalSpecifier w.r.t a particular Timestamp.
     * It returns a Interval which has been modified by 
     */
    public abstract Interval instantiate(AbsoluteTimestamp t);

    /**
     * Clone this object.
     */
    public Object clone() throws CloneNotSupportedException {
	return super.clone();
    }

}
