// AbsoluteTimestamp.java

package com.timeindexing.time;

import com.timeindexing.basic.Absolute;

/**
 * An absolute timestamp.
 * For all Timestamps that hold absolute values w.r.t. the Epoch.
 */
public interface AbsoluteTimestamp extends Absolute, Timestamp {
    /**
     * Is the time after the Epoch.
     */
    public boolean isAfterEpoch();

    /**
     * Is the time before the Epoch.
     */
    public boolean isBeforeEpoch();


}
