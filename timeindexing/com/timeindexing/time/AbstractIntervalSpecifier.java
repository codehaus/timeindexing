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
