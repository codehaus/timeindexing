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
// IntervalSpecifier.java

package com.timeindexing.time;

import com.timeindexing.basic.Interval;

/**
 * An interface for objects that specify some specifier for an Interval
 */
public interface  IntervalSpecifier extends Cloneable {
    /**
     * Get the start of interval TimeSpecifier.
     */
    public TimeSpecifier getStartSpecifier();

    /**
     * Get the end of interval TimeSpecifier.
     */
    public TimeSpecifier getEndSpecifier();

    /**
     * Instantiate this IntervalSpecifier w.r.t a particular Timestamp.
     * It returns a Interval which has been modified by 
     */
    public Interval instantiate(AbsoluteTimestamp t);
}
