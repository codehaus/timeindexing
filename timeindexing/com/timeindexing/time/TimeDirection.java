// TimeDirection.java

package com.timeindexing.time;


/**
 * An interface for constants that specify the direction in time of a Time.
 * Namely, forward or backward in time.
 */
public interface  TimeDirection {
    /**
     * Get the value associated with this TimeDirection.
     */
    public int value();

    /**
     * The modification is forward in time.
     */
    public final static int FORWARD = 1;

    /**
     * The modification is forward in time.
     */
    public final static TimeDirection FORWARD_DT = new TimeDirection() {
	    public int value() {
		return FORWARD;
	    }

	    public String toString() {
		return "forward";
	    }
	};

    /**
     * The modification is backward in time.
     */
    public final static int BACKWARD = 2;

    /**
     * The modification is backward in time.
     */
    public final static TimeDirection BACKWARD_DT = new TimeDirection() {
	    public int value() {
		return BACKWARD;
	    }

	    public String toString() {
		return "backward";
	    }
	};

}
