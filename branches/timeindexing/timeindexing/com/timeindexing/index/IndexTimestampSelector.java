// IndexTimestampSelector.java

package com.timeindexing.index;


/**
 * An enumeration of 2 values to enable choice between 
 * using Index timestamps or Data timestamps
 * in Index selections and Intervals.
 */
public interface IndexTimestampSelector {
    /**
     * A selector for Index timestamps.
     */
    public static final IndexTimestampSelector INDEX = new IndexTimestampSelector() {
	    
	    public String toString() {
		return "INDEX";
	    }
	};

    /**
     * A selector for Data timestamps.
     */
    public static final IndexTimestampSelector DATA = new IndexTimestampSelector() {
	    public String toString() {
		return "DATA";
	    }
	};
}
