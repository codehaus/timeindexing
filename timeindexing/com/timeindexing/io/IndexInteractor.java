// IndexInteractor.java

package com.timeindexing.io;

import com.timeindexing.index.StoredIndex;

/**
 * An interface for objects that interact with indexes.
 */
public interface IndexInteractor { 
    /**
     * Get the index which this is doing I/O for.
     */
    public StoredIndex getIndex();
}
