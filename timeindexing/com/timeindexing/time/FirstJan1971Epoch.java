// FirstJan1971Epoch.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;

/**
 * An epoch that is based on times starting at midnight on 1st Jan 1971.
 */
public interface FirstJan1971Epoch extends Epoch {
    public FirstJan1971Epoch EPOCH = new FirstJan1971Epoch() {
	    /**
	     * Get the name of the Epoch.
	     */
	    public String getName() {
		return "1st Jan 1971";
	    }

	    /**
	     * Format a Timestamp.
	     */
	    public String format(Timestamp t) {
		Scale scale = t.getScale();

		if (scale instanceof SecondScale) {
		    return ("[" + new SecondDateFormat().format(t) + "]");
		} else 	if (scale instanceof MillisecondScale) {
		    return ("[" + new MillisecondDateFormat().format(t) + "]");
		} else if (scale instanceof MicrosecondScale) {
		    return ("[" + new MicrosecondDateFormat().format(t) + "]");
		} else if (scale instanceof NanosecondScale) {
		    return ("[" + new NanosecondDateFormat().format(t) + "]");
		} else {
		    throw new Error("Unhandled type of scale for argument to asScale(). It is: " +
				    scale.getClass().getName());
		}
		

	    }

	    public String toString() {
		return getName();
	    }

	};
}
