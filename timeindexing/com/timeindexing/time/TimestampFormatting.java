// TimestampFormatting.java

package com.timeindexing.time;

/**
 * An interface for objects that can format a Timestamp.
 */
public interface TimestampFormatting {
    /**
     * Format a Timestamp.
     */
    public String format(Timestamp t);

    /**
     * Format given a time as seconds and nanoseconds.
     */
    public String format(long seconds, int nanoseconds);
}
