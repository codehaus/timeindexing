// Overlap.java

package com.timeindexing.basic;

/**
 * An enumeration of 2 values to enable choice between 
 * having 2 ways for an Interval to overlap an index.
 * Strict requires that the Interval must be within the
 * bounds of the Index.
 * Free allows either end of the Interval to go beyond the bounds
 * of an Index.
 */
public interface Overlap {
    /**
     * States if the Interval must be strictly in the bounds of an Index.
     */
    public final static Overlap STRICT = new Overlap() {
	    
	    public String toString() {
		return "STRICT";
	    }
	};

    /**
     * States if the Interval can be free of the bounds of an Index.
     */
    public final static Overlap FREE = new Overlap() {
	    
	    public String toString() {
		return "FREE";
	    }
	};
}
