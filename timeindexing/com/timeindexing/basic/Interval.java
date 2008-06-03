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
// Interval.java

package com.timeindexing.basic;

/**
 * An interface for an interval.
 *
 * Intervals can be:
 * <ul>
 * <li> AbsoluteTimestamp -> AbsoluteTimestamp
 * <li> AbsoluteTimestamp -> Position
 * <li> AbsoluteTimestamp + Count
 * <li> AbsoluteTimestamp + RelativeTimestamp
 * <li> Position -> Timestamp
 * <li> Position -> Position
 * <li> Position + Count
 * <li> Position + RelativeTimestamp
 * </ul>
 *
 * <p> or </p>
 * <ul>
 * <li> AbsoluteTimestamp, - Count, + Count
 * <li> AbsoluteTimestamp, - RelativeTimestamp, + RelativeTimestamp
 * <li> Position, - Count, + Count
 * <li> Position, - RelativeTimestamp, + RelativeTimestamp
 * </ul>
 */
public interface Interval {
    /**
     * Get the interval start.
     */
    public Value start();

    /**
     * Get the interval end.
     */
    public Value end();
}
