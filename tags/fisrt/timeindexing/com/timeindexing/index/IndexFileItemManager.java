//IndexFileItemManager.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.FixedOffset;

/**
 * An interface for classes that need to manage
 * IndexItems that reside in an Index file.
 * It has the methods needed to manage an item from a file,
 * but are not needed by the application  layer.
 */
public interface IndexFileItemManager extends IndexItemManager {
    /**
     * Does this IndexItem actually hold the data.
     */
    public boolean hasData();

    /**
     * Set the data to be a new DataAbstraction.
     */
    public IndexFileItemManager setData(DataAbstraction data);

    /**
     * Get the file offset for the index for this index item.
     */
    public FixedOffset getIndexOffset();

    /**
     * Set the file offset for the index item for this index item.
     */
    public IndexFileItemManager setIndexOffset(FixedOffset offset);

    /**
     * Get the file offset for the data for this index item.
     */
    public FixedOffset getDataOffset();

    /**
     * Set the file offset for the data for this index item.
     */
    public IndexFileItemManager setDataOffset(FixedOffset offset);

}
