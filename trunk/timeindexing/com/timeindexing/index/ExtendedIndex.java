//ExtendedIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.Offset;

/**
 * An interface for classes that need extended
 * Indexes.
 * It has the methods needed to access extra values of an index,
 * but are not generally needed by the application  layer.
 */
public interface ExtendedIndex extends Index, ExtendedIndexHeader {
    /**
     * Get the type of the index.
     */
    public int getIndexType();

    /**
     * Get the  last time the index was flushed.
     */
    public Timestamp getLastFlushTime();

    /**
     * Get the IndexItem Position when the index was last flushed.
     */
    public Position getLastFlushPosition();
    
    /**
     * Get the Offset of the fisrt item.
     */
    public Offset getFirstOffset();

    /**
     * Get the Offset of the last item.
     */
    public Offset getLastOffset();

}
