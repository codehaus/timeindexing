//ManagedStoredIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.Offset;

/**
 * An interface for classes that need to manage
 * Indexes that are saved in stores.
 * It has the methods needed to manage an index,
 * but are not needed by the application  layer.
 */
public interface ManagedStoredIndex extends ManagedIndex {
    /**
     * Retrieve an Index Item into the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     */
    public long retrieveItem(IndexItem item);

    /**
     * Read data for an index item
     * given a DataReference.
     */
    public DataHolderObject readData(DataReference dataReference);

    /**
     * Get the path name of the header of the index.
     */
    public String getIndexPathName();


}
