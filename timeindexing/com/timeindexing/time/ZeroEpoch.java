// ZeroEpoch.java

package com.timeindexing.time;

import com.timeindexing.basic.Scale;

/**
 * An epoch that is based on the Zero Epoch.  All times start at 0.
 */
public interface ZeroEpoch extends Epoch {
    public ZeroEpoch EPOCH = new ZeroEpoch() {
	    /**
	     * Get the name of the Epoch.
	     */
	    public String getName() {
		return "Zero Epoch";
	    }

	    /**
	     * Format a Timestamp.
	     */
	    public String format(Timestamp t) {
		Timestamp relative = TimeCalculator.toRelative((AbsoluteTimestamp)t);
		Scale scale = t.getScale();

		if (scale instanceof SecondScale) {
		    return ("(" + new SecondElapsedFormat().format(relative) + ")");
		} else 	if (scale instanceof MillisecondScale) {
		    return ("(" + new MillisecondElapsedFormat().format(relative) + ")");
		} else if (scale instanceof MicrosecondScale) {
		    return ("(" + new MicrosecondElapsedFormat().format(relative) + ")");
		} else if (scale instanceof NanosecondScale) {
		    return ("(" + new NanosecondElapsedFormat().format(relative) + ")");
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
