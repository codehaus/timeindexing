// HollowAtDataVolumeRemoveAfterTimeoutPolicy.java

package com.timeindexing.cache;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.time.Clock;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.RelativeTimestamp;
import com.timeindexing.time.ElapsedMillisecondTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.util.DoubleLinkedList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Hollow items after the amount of held data
 * reaches a certain volume.
 * Then the items will be removed after a time since
 * it was last used
 */
public class HollowAtDataVolumeRemoveAfterTimeoutPolicy extends AbstractCachePolicy implements CachePolicy {
    // The list of items to remove
    DoubleLinkedList removeList = null;

    // how long an IndexItem can queued before removeing
    RelativeTimestamp timeout = null;

    // The volume to hold before removing
    long volumeThreshold = 0;

    /**
     * Construct this policy object
     */
    public HollowAtDataVolumeRemoveAfterTimeoutPolicy() {
	monitorList = new DoubleLinkedList();
	removeList = new DoubleLinkedList();
	// 50
	volumeThreshold = 50 * 1024;
	// 0.5 second
	timeout = new ElapsedMillisecondTimestamp(500);
    }

    /**
     * Construct this policy object
     */
    public HollowAtDataVolumeRemoveAfterTimeoutPolicy(long volume, RelativeTimestamp elapsed) {
	monitorList = new DoubleLinkedList();
	removeList = new DoubleLinkedList();

	volumeThreshold = volume;
 	timeout = elapsed;
   }



    /**
     * Called at the beginning of cache.addItem()
     * @param item the item being added
     * @param pos the position the item is being added to
     */
    public Object notifyAddItemBegin(IndexItem item,long pos) {
	//System.err.print("notifyAddItemBegin: ");
	notifyGetItemBegin(item, pos);
	return null;
    }

    /**
     * Called at the beginning of cache.addItem()
     * @param item the item being added
     * @param pos the position the item is being added to
     */
    public Object notifyAddItemEnd(IndexItem item, long pos) {
	//System.err.print("notifyAddItemEnd: ");
	notifyGetItemEnd(item, pos);
	return null;
    }    

    /**
     * Called at the beginning of cache.getItem()
     * @param pos the position being requested
     */
    public Object notifyGetItemBegin(IndexItem item, long pos) {
	// if the item is in the monitorList remove it.
	monitorList.remove(item);

	// if the item is in the removeList remove it.
	removeList.remove(item);

	// if the first item in the monitorList
	// was last accessed with an elapsed time greater
	// than the timeout, then remove it
	// remove one item
	if (monitorList.size() > 0) {

	    //System.err.print("Hollowing " + ". Volume = " + cache.getDataVolume());

 	    while (cache.getDataVolume() > volumeThreshold) {
		ManagedIndexItem first = (ManagedIndexItem)monitorList.getFirst();

		//System.err.println("Hollowing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime() + ". Volume = " + cache.getDataVolume() + " Hollow list size = " + monitorList.size());

		monitorList.remove(first);
    
		cache.hollowItem(first.getPosition());
		
		// add the item to the remove list for later removal
		removeList.add(first);
	    }

	    //System.err.println(". Volume = " + cache.getDataVolume()  + ". Thread " + Thread.currentThread().getName() );

	}

	// now check to see if we need to remove something as well
	while (removeList.size() > 0) {

	    // loop until we break out
	    ManagedIndexItem first = (ManagedIndexItem)removeList.getFirst();

	    Timestamp firstTimeout = TimeCalculator.elapsedSince(first.getLastAccessTime());
	    //System.err.println("firstTimeout = " + firstTimeout);


	    if (TimeCalculator.greaterThan(firstTimeout, timeout)) {
		// the first element has a big enough timeout
		// System.err.println("Removing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime() + ".Timeout = " + firstTimeout + ". Remove list size = " + removeList.size());
		    
		//System.err.println("Removing " + first.getPosition() + ". Thread " + Thread.currentThread().getName() );		removeList.remove(first);
		removeList.remove(first);
		cache.removeItem(first.getPosition());
	    } else {
		break;
	    }
	}

	return null;
    }

    /**
     * Called at the end of cache.getItem()
     * @param item the item being returned
     */
    public Object notifyGetItemEnd(IndexItem item, long pos) {
	// add this item to the monitorList
	monitorList.add(item);

	return null;
    }
	    

    /**
     * TO String
     */
    public String toString() {
	return "HollowAtDataVolumeRemoveAfterTimeoutPolicy: queueWindow = " + monitorList.size() + "/" + volumeThreshold;
    }
}
