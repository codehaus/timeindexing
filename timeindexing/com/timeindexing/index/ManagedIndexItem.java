//ManagedIndexItem.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.AbsolutePosition;

/**
 * An interface for classes that need to manage IndexItems.
 * It has the methods needed to manage an item in an index,
 * but are not needed by the application  layer.
 */
public interface ManagedIndexItem extends IndexItem {
    /**
     * Set the Index for an IndexItem
     */
    public ManagedIndexItem setIndex(Index index);

    /**
     * Set the position for the index item in an index.
     */
    public ManagedIndexItem setPosition(AbsolutePosition position);

    /**
     * Set the last access time of the item.
     */
    public ManagedIndexItem setLastAccessTime(Timestamp accessTime);

    /**
     * Get the DataAbstraction held by the IndexItem.
     */
    public DataAbstraction getDataAbstraction();

}
