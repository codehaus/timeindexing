// AbstractCachePolicy.java

package com.timeindexing.cache;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeCalculator;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Generic functionality for CachePolicies.
 */
public abstract  class AbstractCachePolicy implements CachePolicy {
    // The IndexCache
    IndexCache cache = null;

    // The set of items that are being monitored
    LinkedList monitorList = null;

    /**
     * Set the IndexCache that this is a policy for.
     */
    public CachePolicy setIndexCache(IndexCache c) {
	cache = c;
	return this;
    }

    /**
     * Get the IndexCache that this is a policy for.
     */
    public IndexCache getIndexCache() {
	return cache;
    }


    /**
     * Called at the beginning of cache.addItem()
     * @param item the item being added
     * @param pos the position the item is being added to
     */
    public Object notifyAddItemBegin(IndexItem item,long pos) {
	// do nothing
	return null;
    }

    /**
     * Called at the beginning of cache.addItem()
     * @param item the item being added
     * @param pos the position the item is being added to
     */
    public Object notifyAddItemEnd(IndexItem item, long pos) {
	// do nothing
	return null;
    }

    /**
     * Called at the beginning of cache.getItem()
     * @param pos the position being requested
     */
    public Object notifyGetItemBegin(IndexItem item, long pos) {
	// do nothing
	return null;
    }

    /**
     * Called at the end of cache.getItem()
     * @param item the item being returned
     */
    public Object notifyGetItemEnd(IndexItem item, long pos) {
	// do nothing
	return null;
    }

    /**
     * Show the monitorList.
     */
    protected void show() {
	//System.err.print(monitorList.size() + ": ");

	Iterator iterator = monitorList.iterator();

	while (iterator.hasNext()) {
	    ManagedIndexItem item = (ManagedIndexItem)iterator.next();

	    //System.err.print(item.getPosition() + " / " + item.getLastAccessTime() + ", ");
	}
    }
}
