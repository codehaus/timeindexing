// IndexInteractor.java

package com.timeindexing.io;

import com.timeindexing.index.StoredIndex;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.basic.Position;

import java.io.IOException;

/**
 * An interface for objects that interact with indexes.
 */
public interface IndexInteractor { 
    /**
     * Get the index which this is doing I/O for.
     */
    public StoredIndex getIndex();

    /**
     * Get the item
     * @param position the position of the index item to get
     * @param withData read the data for this IndexItem if withData is true,
     * the data needs to be read at a later time, otherwise
     */
    public ManagedIndexItem getItem(Position position, boolean withData) throws IOException;


    /**
     * Add an IndexItem to the  index.
     */
    public long addItem(ManagedIndexItem item) throws IOException;

}
