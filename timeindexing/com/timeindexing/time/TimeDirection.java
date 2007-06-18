// TimeDirection.java

package com.timeindexing.time;


/**
 * An interface for constants that specify the direction in time
 * of a TimeSpecifier.
 * Namely, forward, backward, or nothing in time.
 */
public interface  TimeDirection {
    /**
     * Get the value associated with this TimeDirection.
     */
    public int value();

    /**
     * The direction is forward in time.
     */
    public final static int FORWARD_VALUE = 1;

    /**
     * The direction is forward in time.
     */
    public final static TimeDirection FORWARD = new TimeDirection() {
	    public int value() {
		return FORWARD_VALUE;
	    }

	    public String toString() {
		return "forward";
	    }
	};

    /**
     * The direction is backward in time.
     */
    public final static int BACKWARD_VALUE = 2;

    /**
     * The direction is backward in time.
     */
    public final static TimeDirection BACKWARD = new TimeDirection() {
	    public int value() {
		return BACKWARD_VALUE;
	    }

	    public String toString() {
		return "backward";
	    }
	};

    /**
     * The direction is nothing.
     */
    public final static int NOTHING_VALUE = 3;

    /**
     * The direction is nothing.
     */
    public final static TimeDirection NOTHING = new TimeDirection() {
	    public int value() {
		return NOTHING_VALUE;
	    }

	    public String toString() {
		return "nothing";
	    }
	};

}
