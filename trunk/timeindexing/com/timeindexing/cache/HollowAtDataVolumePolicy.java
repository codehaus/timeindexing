// HollowAtDataVolumePolicy.java

package com.timeindexing.cache;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Hollow items after the amount of held data
 * reaches a certain volume
 */
public class HollowAtDataVolumePolicy extends AbstractCachePolicy implements CachePolicy {
    // The volume to hold before removing
    long volumeThreshold = 0;

    /**
     * Construct this policy object
     */
    public HollowAtDataVolumePolicy() {
	monitorList = new LinkedList();
	// 50k
	volumeThreshold = 50 * 1024;
    }

    /**
     * Construct this policy object
     */
    public HollowAtDataVolumePolicy(long volume) {
	monitorList = new LinkedList();
	volumeThreshold = volume;
    }

    /**
     * Called at the beginning of cache.getItem()
     * @param pos the position being requested
     */
    public Object notifyGetItemBegin(IndexItem item, long pos) {
	// if the item is in the monitorList remove it.
	if (monitorList.contains(item)) {
	    monitorList.remove(item);
	    //System.err.println("DeQueue " + item.getPosition() + ".Hollow list size = " + monitorList.size());
	}

	// if the first item in the monitorList
	// was last accessed with an elapsed time greater
	// than the timeout, then remove it
	// remove one item
	if (monitorList.size() > 0) {
	    ManagedIndexItem first = (ManagedIndexItem)monitorList.getFirst();

	    if (cache.getDataVolume() > volumeThreshold) {
		//System.err.print("Hollowing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime());
		monitorList.remove(first);
    
		cache.hollowItem(first.getPosition());
	    }
	}

	return null;
    }

    /**
     * Called at the end of cache.getItem()
     * @param item the item being returned
     */
    public Object notifyGetItemEnd(IndexItem item, long pos) {
	monitorList.add(item);
	//System.err.println("Queuing " + item.getPosition() + ".Hollow list size = " + monitorList.size());

	return null;
    }
	    

    /**
     * TO String
     */
    public String toString() {
	return "HollowAtDataVolumePolicy: queueWindow = " + monitorList.size() + "/" + volumeThreshold;
    }
}
