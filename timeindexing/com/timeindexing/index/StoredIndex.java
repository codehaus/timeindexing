// StoredIndex.java

package com.timeindexing.index;

/**
 * An interface for classes that need to process
 * Indexes that are saved in stores.
 * This is to be used by classes that store index
 * data, rather than have data incore.
 */
public interface StoredIndex extends ManagedIndex  {
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

}
