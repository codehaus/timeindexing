// IndexCache.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Lifetime;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.basic.Position;
import com.timeindexing.index.IndexTimestampSelector;

/**
 * The time index itself.
 * The values of the index may or may not be in core.
 * It is up to the implementations to decide how much
 * will be kept in core.
 */
public interface IndexCache {
    /**
     * Add an Index Item to the Index.
     */
    public long addItem(IndexItem item);

    /**
     * Get the no of items in the index.
     */
    public long cacheSize();

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n);

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p);


    /**
     * Hollow out the IndexItem at the speicifed position.
     */
    public boolean hollowItem(long pos);

    /**
     * Hollow out the IndexItem at the speicifed position.
     */
    public boolean hollowItem(Position p);

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

}
