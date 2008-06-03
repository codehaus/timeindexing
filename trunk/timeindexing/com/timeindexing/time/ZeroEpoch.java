Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
		    return ("[" + new SecondElapsedFormat().format(relative) + "]");
		} else 	if (scale instanceof MillisecondScale) {
		    return ("[" + new MillisecondElapsedFormat().format(relative) + "]");
		} else if (scale instanceof MicrosecondScale) {
		    return ("[" + new MicrosecondElapsedFormat().format(relative) + "]");
		} else if (scale instanceof NanosecondScale) {
		    return ("[" + new NanosecondElapsedFormat().format(relative) + "]");
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
