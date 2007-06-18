// AbstractPlayListItem.java

package com.timeindexing.appl.playlist;

import com.timeindexing.index.Index;
import com.timeindexing.basic.Interval;

/**
 * AbstractPlayListItem is an abstract base class for all PlayItems.
 */
public abstract class AbstractPlayListItem implements PlayListItem {
    // The Index this PlayListItem refers to
    Index index = null;

    // The Interval this PlayListItem refers to
    Interval interval = null;

    /**
     * Get the Index for this PlayItem.
     */
    public Index getIndex() {
	return index;
    }

    /**
     * Set the Index for this PlayItem.
     */
    public PlayListItem setIndex(Index anIndex) {
	index = anIndex;
	return this;
    }

    /**
     * Get the Interval for this PlayItem.
     */
    public Interval getInterval() {
	return interval;
    }

    /**
     * Set the Interval for this PlayItem.
     */
    public PlayListItem setInterval(Interval anInterval) {
	interval = anInterval;
	return this;
    }
}
