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
