// Timestamp.java

package com.timeindexing.time;

import com.timeindexing.basic.Value;

/*
 * <p>
 * There are various implementations of Timestamp with different
 * number of bits for seconds and the subsecond part.
 * This is to accomodate fine resolution timestamps which need 
 * nanoseconds, and also coarse resolution timestamps which may need
 * to span centuries but need very little subsecond data.
 * <p>
 * All timestamps will fit in 64 bits.
 * The timestamp styles are:
 * <p>
 * TRADITIONAL - MILLISECONDS since Epoch
 * 2 bits type - 1 bit pre/post epoch - 61 bits millisecond data
 * About 73,000,000 years in seconds
 * <p>
 * MICROSECONDS
 * 2 bits type - 1 bit pre/post epoch - 41 bits seconds - 20 bits microsecond
 * Allows for about 69,000 years in seconds.
 * <p>
 * NANOSECONDS
 * 2 bits type - 1 bit pre/post epoch - 31 bits seconds - 30 bits nanosecond
 * Allows for about 68 years in seconds.
 * <p>
 * SECONDS
 * 4 bits type - 1 bit pre/post epoch - 59 bits seconds
 * About 18,000,000,000 years in seconds.
 */

/*
 * Could consider other kinds of Timestamp.
 * year / month / day
 * year / season
 * year / BC | AD
 */

/**
 * A timestamp.
 * This goes down to nanoseconds for tasks that might need it.
 * The Java runtime currently goes down to milliseconds,
 * but this doesn't mean the underlying format
 * should not have more resolution.
 */
public interface Timestamp extends Value {
    /**
     * Milliseconds since epoch.
     * B = 00.
     */
    public final static long MILLISECOND = (long)0x00 << 62;

    /**
     * Microseconds since epoch.
     * B = 01.
     */
    public final static long MICROSECOND = (long)0x01 << 62;

    /**
     * Nanoseconds since epoch.
     * B = 10.
     */
    public final static long NANOSECOND = (long)0x02 << 62;

    /**
     * Extened timestamp.
     * B = 11.
     */
    public final static long EXTENDED = (long)0x03 << 62;

    /**
     * Mask for top 2 bits
     * B = 11.
     */
    public final static long TOP_2_MASK = (long)0x03 << 62;

    /**
     * Elapsed milliseconds.
     * B = 1100.
     */
    public final static long ELAPSED_MILLISECOND = (long)0x0c << 60;

    /**
     * Elapsed microseconds.
     * B = 1101.
     */
    public final static long ELAPSED_MICROSECOND = (long)0x0d << 60;

    /**
     * Seconds since epoch.
     * B = 1110.
     */
    public final static long SECOND = (long)0x0e << 60;

    /**
     * Extened timestamp.
     * B = 1111.
     */
    public final static long EXTENDED_2 = (long)0x0f << 60;

    /**
     * Mask for top 4 bits
     * B = 1111.
     */
    public final static long TOP_4_MASK = (long)0x0f << 60;

    /**
     * Elapsed nano seconds
     * B = 111100.
     */
    public final static long  ELAPSED_NANOSECOND = (long)0x3c << 58;

    /**
     * Elapsed second
     * B = 111101.
     */
    public final static long ELAPSED_SECOND = (long)0x3d << 58;

    /**
     * Year + season
     * B = 111110.
     */
    public final static long YEAR_SEASON = (long)0x3e << 58;

    /**
     * Extened timestamp.
     * B = 111111.
     */
    public final static long EXTENDED_3 = (long)0x3f << 58;


    /**
     * Mask for top 6 bits
     * B = 111111.
     */
    public final static long TOP_6_MASK = (long)0x3f << 58;

    /**
     * A timestamp of ZERO;
     */
    public final static Timestamp ZERO = new ZeroTimestamp();

    /**
     * Get the number of seconds
     */
    public long getSeconds();

    /**
     * Get the number of nanoseconds
     */
    public int getNanoSeconds();

}
