// HollowAtDataVolumeRemoveAfterTimeoutPolicy.java

package com.timeindexing.cache;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.time.Clock;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.RelativeTimestamp;
import com.timeindexing.time.ElapsedMillisecondTimestamp;
import com.timeindexing.time.TimeCalculator;
import java.util.LinkedList;
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
    LinkedList removeList = null;

    // The pre-queue that hold a few IndexItems.
    // These are fed into the monitorList slowly
    // to avoid the problem of when there is a quick
    // addItem() then a getItem().
    // This causes the policy to go awry.
    // The preQueue avoid most of the problems
    LinkedList preQueue = null;

    // how long an IndexItem can queue before removeing
    RelativeTimestamp timeout = null;

    // The volume to hold before removing
    long volumeThreshold = 0;

    /**
     * Construct this policy object
     */
    public HollowAtDataVolumeRemoveAfterTimeoutPolicy() {
	monitorList = new LinkedList();
	removeList = new LinkedList();
	preQueue = new LinkedList();
	// 50k
	volumeThreshold = 50 * 1024;
	// 0.5 second
	timeout = new ElapsedMillisecondTimestamp(500);
    }

    /**
     * Construct this policy object
     */
    public HollowAtDataVolumeRemoveAfterTimeoutPolicy(long volume, RelativeTimestamp elapsed) {
	monitorList = new LinkedList();
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
	if (monitorList.contains(item)) {
	    monitorList.remove(item);
	    //System.err.println("DeQueue " + item.getPosition() + ".Hollow list size = " + monitorList.size());
	}

	// if the item is in the removeList remove it.
	if (removeList.contains(item)) {
	    removeList.remove(item);
	    //System.err.println("Dont remove " + item.getPosition() + ".Remove list size = " + removeList.size());
	}

	// if the first item in the monitorList
	// was last accessed with an elapsed time greater
	// than the timeout, then remove it
	// remove one item
	if (monitorList.size() > 0) {
	    ManagedIndexItem first = (ManagedIndexItem)monitorList.getFirst();

	    if (cache.getDataVolume() > volumeThreshold) {
		//System.err.println("Hollowing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime() + ". Volume = " + cache.getDataVolume() + " Hollow list size = " + monitorList.size());
		monitorList.remove(first);
    
		cache.hollowItem(first.getPosition());

		// add the item to the remove list for later removal
		removeList.add(first);
	    }
	}

	// now check to see if we need to remove something as well
	if (removeList.size() > 0) {
	    ManagedIndexItem first = (ManagedIndexItem)removeList.getFirst();

	    Timestamp firstTimeout = TimeCalculator.elapsedSince(first.getLastAccessTime());
	    //System.err.println("firstTimeout = " + firstTimeout);


	    if (TimeCalculator.greaterThan(firstTimeout, timeout)) {
		// the first element has a big enough timeout
		//System.err.println("Removing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime() + ".Timeout = " + firstTimeout + ". Remove list size = " + removeList.size());
		removeList.remove(first);
	    
		cache.removeItem(first.getPosition());
	    }
	}

	// now move an element fro the prque to the monitorList
	if (preQueue.size() > 2) {
	    ManagedIndexItem first = (ManagedIndexItem)preQueue.getFirst();

	    preQueue.remove(first);
	    monitorList.add(first);
	}

	return null;
    }

    /**
     * Called at the end of cache.getItem()
     * @param item the item being returned
     */
    public Object notifyGetItemEnd(IndexItem item, long pos) {
	// if this item is not in the monitorList, then add it
	if (! preQueue.contains(item) && ! monitorList.contains(item)) {
	    preQueue.add(item);
	    //System.err.println("PreQueuing " + item.getPosition() + ". Prequeue size = " + preQueue.size());
	}

	return null;
    }
	    

    /**
     * TO String
     */
    public String toString() {
	return "HollowAtDataVolumeRemoveAfterTimeoutPolicy: queueWindow = " + monitorList.size() + "/" + volumeThreshold;
    }
}
