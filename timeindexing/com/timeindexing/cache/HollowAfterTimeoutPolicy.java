// HollowAfterTimeoutPolicy.java

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
 * Hollow items after they have been used.
 */
public class HollowAfterTimeoutPolicy extends AbstractCachePolicy implements CachePolicy {
    // how long an IndexItem can queue before hollowing
    RelativeTimestamp timeout = null;

    /**
     * Construct this policy object
     */
    public HollowAfterTimeoutPolicy() {
	monitorList = new LinkedList();
	// 0.5 second
	timeout = new ElapsedMillisecondTimestamp(500);
    }

    /**
     * Construct this policy object
     */
    public HollowAfterTimeoutPolicy(RelativeTimestamp elapsed) {
	monitorList = new LinkedList();
	timeout = elapsed;
    }

    /**
     * Called at the beginning of cache.getItem()
     * @param pos the position being requested
     */
    public Object notifyGetItemBegin(IndexItem item, long pos) {
	// if the first item in the monitorList
	// was last accessed with an elapsed time greater
	// than the timeout, then hollow it
	// and remove one item from the monitorList
	if (monitorList.size() > 0) {
	    ManagedIndexItem first = (ManagedIndexItem)monitorList.getFirst();

	    Timestamp firstTimeout = TimeCalculator.elapsedSince(first.getLastAccessTime());
	    System.err.println("firstTimeout = " + firstTimeout);


	    if (TimeCalculator.greaterThan(firstTimeout, timeout)) {
		// the first element has a big enough timeout
		System.err.print("Hollowing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime() + ".Timeout = " + firstTimeout);
		monitorList.remove(first);
	    
		cache.hollowItem(first.getPosition());
	    } 

	    /*
	    else {
		// the timeout is not big enough
		// so remove it from the beginning of the list
		// and add it to the end
		monitorList.remove(first);
		monitorList.add(first);
		System.err.println("ReQueuing " + item.getPosition() + ".Hollow list size = " + monitorList.size());
	    }
	    */
		
	}

	return null;
    }

    /**
     * Called at the end of cache.getItem()
     * @param item the item being returned
     */
    public Object notifyGetItemEnd(IndexItem item, long pos) {
	// if this item is not in the monitorList, then add it
	if (! monitorList.contains(item)) {
	    monitorList.add(item);
	    System.err.println("Queuing " + item.getPosition() + ".Hollow list size = " + monitorList.size());
	}

	return null;
    }
	    

    /**
     * TO String
     */
    public String toString() {
	return "HollowAfterTimeoutPolicy: queueWindow = " + monitorList.size() + "/" + timeout;
    }
}
