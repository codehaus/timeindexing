// DataCache.java

package com.timeindexing.cache;

import com.timeindexing.index.DataAbstraction;

/**
 * The data objects referenced by the index.
 * The data may or may not be in core.
 * It is up to the implementations to decide if a data object
 * will be kept in core.
 */
public interface DataCache {
    /**
     * Hold a Data Object.
     */
    public int addData(DataAbstraction data);

    /**
     * Flush this data cache.
     */
    public boolean flush();

    /**
     * Close this data cache.
     */
    public boolean close();

}
