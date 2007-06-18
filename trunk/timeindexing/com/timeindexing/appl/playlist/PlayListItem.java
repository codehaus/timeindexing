// PlayListItem.java

package com.timeindexing.appl.playlist;

import com.timeindexing.index.Index;
import com.timeindexing.basic.Interval;

/**
 * A PlayItem contains an Index and an Interval
 */
public interface PlayListItem {
    /**
     * Get the Index for this PlayItem.
     */
    public Index getIndex();

    /**
     * Get the Interval for this PlayItem.
     */
    public Interval getInterval();
}
