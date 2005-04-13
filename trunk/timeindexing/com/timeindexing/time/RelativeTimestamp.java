// RelativeTimestamp.java

package com.timeindexing.time;

import com.timeindexing.basic.Relative;

/**
 * An relative timestamp.
 * For all Timestamps that hold relative / elapsed values.
 */
public interface RelativeTimestamp extends Relative, Timestamp {

    /**
     * Is the Timestamp negative.
     * That is, a specification of time before zero for RelativeTimestamps.
     */
    public boolean isNegative();
}
