// Timestamp.java

package com.timeindexing.time;

import com.timeindexing.basic.Value;
import com.timeindexing.basic.Scale;

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
 * 2 bits type - 1 sign bit for pre/post epoch - 51 bits seconds - 10 bits milliseconds
 * About 73,000,000 years in seconds
 * <p>
 * MICROSECONDS
 * 2 bits type - 1 sign bit for pre/post epoch - 41 bits seconds - 20 bits microseconds
 * Allows for about 69,000 years in seconds.
 * <p>
 * NANOSECONDS
 * 2 bits type - 1 sign bit for pre/post epoch - 31 bits seconds - 30 bits nanoseconds
 * Allows for about 68 years in seconds.
 * <p>
 * SECONDS
 * 4 bits type - 1 sign bit for pre/post epoch - 59 bits seconds
 * About 18,000,000,000 years in seconds.
 * <p>
 * For AbsoluteTimestamps epoch can be zero or Jan 1st 1971 at midnight, for
 * RelativeTimestamps epoch is zero.
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
     * Millisecond sign bit.
     * S = 001.
     */
    public final static long MILLISECOND_SIGN = (long)0x01 << 61;

    /**
     * Microseconds since epoch.
     * B = 01.
     */
    public final static long MICROSECOND = (long)0x01 << 62;

    /**
     * Microsecond sign bit.
     * S = 011.
     */
    public final static long MICROSECOND_SIGN = (long)0x01 << 61;

    /**
     * Nanoseconds since epoch.
     * B = 10.
     */
    public final static long NANOSECOND = (long)0x02 << 62;

    /**
     * Nanosecond sign bit.
     * S = 101.
     */
    public final static long NANOSECOND_SIGN = (long)0x01 << 61;

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
     * Elapsed millisecond sign bit.
     * S = 11001.
     */
    public final static long ELAPSED_MILLISECOND_SIGN = (long)0x01 << 59;

    /**
     * Elapsed microseconds.
     * B = 1101.
     */
    public final static long ELAPSED_MICROSECOND = (long)0x0d << 60;

    /**
     * Elapsed microsecond sign bit.
     * S = 11011.
     */
    public final static long ELAPSED_MICROSECOND_SIGN = (long)0x01 << 59;

    /**
     * Seconds since epoch.
     * B = 1110.
     */
    public final static long SECOND = (long)0x0e << 60;

    /**
     * Second sign bit.
     * S = 11101.
     */
    public final static long SECOND_SIGN = (long)0x01 << 59;

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
     * Elapsed nanosecond sign bit.
     * S = 1111001.
     */
    public final static long ELAPSED_NANOSECOND_SIGN = (long)0x01 << 57;

    /**
     * Elapsed second
     * B = 111101.
     */
    public final static long ELAPSED_SECOND = (long)0x3d << 58;

    /**
     * Elapsed second sign bit.
     * S = 1111011.
     */
    public final static long ELAPSED_SECOND_SIGN = (long)0x01 << 57;

    /**
     * Spare
     * B = 111110.
     */
    public final static long SPARE_3E = (long)0x3e << 58;

    /**
     * Spare 3E sign bit.
     * S = 1111101.
     */
    public final static long SPARE_3E_SIGN = (long)0x01 << 57;

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
     * Elapsed Units
     * B = 11111100.
     */
    public final static long ELAPSED_UNITS = (long)0xfc << 56;

    /**
     * Elapsed units sign bit.
     * S = 111111001.
     */
    public final static long ELAPSED_UNITS_SIGN = (long)0x01 << 55;

    /**
     * Spare
     * B = 11111101.
     */
    public final static long SPARE_FD = (long)0xfd << 56;

    /**
     * Spare FD sign bit.
     * S = 111111011.
     */
    public final static long SPARE_FD_SIGN = (long)0x01 << 55;

    /**
     * Spare
     * B = 11111110.
     */
    public final static long SPARE_FE = (long)0xfe << 56;

    /**
     * Spare FE sign bit.
     * S = 111111101.
     */
    public final static long SPARE_FE_SIGN = (long)0x01 << 55;

    /**
     * Extened timestamp.
     * B = 11111111.
     */
    public final static long EXTENDED_4 = (long)0xff << 56;


    /**
     * Mask for top 8 bits
     * B = 11111111.
     */
    public final static long TOP_8_MASK = (long)0xff << 56;

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

    /**
     * Get the scale of a Timestamp.
     */
    public Scale getScale();
}
