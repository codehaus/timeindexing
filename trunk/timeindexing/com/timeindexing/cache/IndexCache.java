Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
     * Increase the data volume
     * @return the new data volume
     */
    public long increaseDataVolume(long v);


     /**
     * Decrease the data volume
     * @return the new data volume
     */
    public long decreaseDataVolume(long v);

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
