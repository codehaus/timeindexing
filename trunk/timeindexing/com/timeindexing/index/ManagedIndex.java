//ManagedIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.Offset;

/**
 * An interface for classes that need to manage
 * Indexes.
 * It has the methods needed to manage an index,
 * but are not needed by the application  layer.
 */
public interface ManagedIndex extends ExtendedIndex {
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
     * Open this index.
     */
     public boolean open();

    /**
     * Create this index.
     */
     public boolean create();

    /**
     * Flush this index.
     */
    public boolean flush();

    /**
     * Close this index.
     */
     public boolean close();
    
}
