//ManagedFileIndexItem.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.Offset;

/**
 * An interface for classes that need to manage
 * IndexItems that reside in an Index file.
 * It has the methods needed to manage an item from a file,
 * but are not needed by the application  layer.
 */
public interface ManagedFileIndexItem extends ManagedIndexItem {
    /**
     * Does this IndexItem actually hold the data.
     */
    public boolean hasData();

    /**
     * Set the data to be a new DataAbstraction.
     */
    public ManagedFileIndexItem setData(DataAbstraction data);

    /**
     * Get the file offset for the index for this index item.
     */
    public Offset getIndexOffset();

    /**
     * Set the file offset for the index item for this index item.
     */
    public ManagedFileIndexItem setIndexOffset(Offset offset);

    /**
     * Get the file offset for the data for this index item.
     */
    public Offset getDataOffset();

    /**
     * Set the file offset for the data for this index item.
     */
    public ManagedFileIndexItem setDataOffset(Offset offset);

}
