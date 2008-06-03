/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
