// IndexCache.java

package com.timeindexing.cache;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Lifetime;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.basic.Position;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.index.IndexItem;

/**
 * The time index itself.
 * The values of the index may or may not be in core.
 * It is up to the implementations to decide how much
 * will be kept in core.
 */
public interface IndexCache {
    /**
     * Get the no of items in the index.
     */
    public long size();

    /**
     * Add an Index Item to the Index.
     */
    public long addItem(IndexItem item, Position position);

    /**
     * Add an Index Item to the Index.
     */
    public long addItem(IndexItem item, long position);

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n);

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p);

    /**
     * Contains the IndexItem at the speicifed position.
     * If the cache contains the item, it means it is loaded.
     */
    public boolean containsItem(long pos);

    /**
     * Contains the IndexItem at the speicifed position.
     * If the cache contains the item, it means it is loaded.
     */
    public boolean containsItem(Position p);

    /**
     * Hollow out the IndexItem at the speicifed position.
     */
    public boolean hollowItem(long pos);

    /**
     * Hollow out the IndexItem at the speicifed position.
     */
    public boolean hollowItem(Position p);

    /**
     * Remove the IndexItem at the speicifed position.
     */
    public boolean removeItem(long pos);

    /**
     * Remove the IndexItem at the speicifed position.
     */
    public boolean removeItem(Position p);

    /**
     * Clear the whole cache
     */
    public boolean clear();

     /**
     * Get the current data volume held by IndexItems in a cache.
     */
    public long getDataVolume();

    /**
     * Get the time the first IndexItem was put into the Index.
     */
    public Timestamp getFirstIndexTime();

    /**
     * Get the time the last IndexItem was put into the Index.
     */
    public Timestamp getLastIndexTime();

    /**
     * Get the time the first IndexItem was put into the Index.
     */
    public Timestamp getFirstDataTime();

    /**
     * Get the time the last IndexItem was put into the Index.
     */
    public Timestamp getLastDataTime();

    /**
     * Set the cache policy.
     * @return the old cache policy
     */
    public CachePolicy setPolicy(CachePolicy policy);

    /**
     * Get the current cache policy.
     */
    public CachePolicy getPolicy();

}
