// AbstractCachePolicy.java

package com.timeindexing.cache;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeCalculator;
import java.util.LinkedList;
import java.util.List;
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
     * Is an item in the first N elements of a List.
     */
    public boolean isInFirst(int n, List list, IndexItem item) {
	// visit first N, or whole list if size of list < N
	int max = list.size() > n ? n : list.size();

	for (int i=0; i<max; i++) {
	    if (item.equals((IndexItem)list.get(i))) {
		// found the item, so it is in the first N
		return true;
	    }
	}

	// haven't found the item
	return false;
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
