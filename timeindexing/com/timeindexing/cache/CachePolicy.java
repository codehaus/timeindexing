// CachePolicy.java

package com.timeindexing.cache;

import com.timeindexing.index.IndexItem;

/**
 * A policy for maintaining the cache.
 */
public interface CachePolicy {
    /**
     * Set the IndexCache that this is a policy for.
     */
    public CachePolicy setIndexCache(IndexCache cache);

    /**
     * Get the IndexCache that this is a policy for.
     */
    public IndexCache getIndexCache();

    /**
     * Called at the beginning of cache.addItem()
     * @param item the item being added
     * @param pos the position the item is being added to
     */
    public Object notifyAddItemBegin(IndexItem item,long pos);

    /**
     * Called at the beginning of cache.addItem()
     * @param item the item being added
     * @param pos the position the item is being added to
     */
    public Object notifyAddItemEnd(IndexItem item, long pos);

    /**
     * Called at the beginning of cache.getItem()
     * @param pos the position being requested
     */
    public Object notifyGetItemBegin(IndexItem item, long pos);

    /**
     * Called at the end of cache.getItem()
     * @param item the item being returned
     */
    public Object notifyGetItemEnd(IndexItem item, long pos);

}
