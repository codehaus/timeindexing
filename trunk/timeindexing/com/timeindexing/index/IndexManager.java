//IndexManager.java

package com.timeindexing.index;

/**
 * An interface for classes that need to manage
 * Indexes.
 * It has the methods needed to manage an index,
 * but are not needed by the application  layer.
 */
public interface IndexManager extends Index {
    /**
     * Get the type of the index.
     */
    public int getType();

    /**
     * Get the file name of the header.
     */
    public String getFileName();

    /**
     * Retrieve an Index Item into the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     */
    public long retrieveItem(IndexItem item);

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
