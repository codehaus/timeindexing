// DefaultDataCache.java

package com.timeindexing.cache;

import com.timeindexing.index.DataAbstraction;

/**
 * The default implementation of a cache which holds the actual data objects.
 */
public class DefaultDataCache implements DataCache {
    /**
     * Hold a Data Object.
     */
    public int addData(DataAbstraction data) {
	return 0;
    }

    /**
     * Flush this data cache.
     */
    public boolean flush() {
	return false;
    }

    /**
     * Close this data cache.
     */
    public boolean close() {
	return false;
    }


}
