// Lifetime.java

package com.timeindexing.time;


/**
 * An enumeration of 2 values to enable choice between
 * timestamps whose lifetime is continuous, and those which are discrete.
 * Continuous timestamps have a lifetime which starts at the time of the
 * timestamp and ends when a new timestamp is put into an index.
 * Discrete timestamps exist only at the point in time of the time of the
 * timestamp.
 */
public interface Lifetime {
    /**
     * A selector for timestamps with a Continuous lifetime.
     */
    public static final Lifetime CONTINUOUS = new Lifetime() {
	    
	    public String toString() {
		return "CONTINUOUS";
	    }
	};

    /**
     * A selector for timestamps with a Discrete lifetime.
     */
    public static final Lifetime DISCRETE = new Lifetime() {
	    public String toString() {
		return "DISCRETE";
	    }
	};
}
