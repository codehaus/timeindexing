//IndexItemManager.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.AbsolutePosition;

/**
 * An interface for classes that need to manage IndexItems.
 * It has the methods needed to manage an item in an index,
 * but are not needed by the application  layer.
 */
public interface IndexItemManager extends IndexItem {
    /**
     * Set the Index for an IndexItem
     */
    public IndexItemManager setIndex(Index index);

    /**
     * Set the position for the index item in an index.
     */
    public IndexItemManager setPosition(AbsolutePosition position);

    /**
     * Set the last access time of the item.
     */
    public IndexItemManager setLastAccessTime(Timestamp accessTime);

}
