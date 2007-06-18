// KeepEverythingPolicy.java

package com.timeindexing.cache;

import com.timeindexing.util.DoubleLinkedList;

/**
 * Keep every IndexItem and it's data.
 */
public class KeepEverythingPolicy extends AbstractCachePolicy implements CachePolicy {
    /**
     * Construct this policy object
     */
    public KeepEverythingPolicy() {
	monitorList = new DoubleLinkedList();
    }

}
