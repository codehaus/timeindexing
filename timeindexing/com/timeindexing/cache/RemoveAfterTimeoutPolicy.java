// RemoveAfterTimeoutPolicy.java

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
 * Remove items after they have been used.
 */
public class RemoveAfterTimeoutPolicy extends AbstractCachePolicy implements CachePolicy {
    // how long an IndexItem can queue before removeing
    RelativeTimestamp timeout = null;

    /**
     * Construct this policy object
     */
    public RemoveAfterTimeoutPolicy() {
	monitorList = new LinkedList();
	// 0.5 second
	timeout = new ElapsedMillisecondTimestamp(500);
    }

    /**
     * Construct this policy object
     */
    public RemoveAfterTimeoutPolicy(RelativeTimestamp elapsed) {
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
	// than the timeout, then remove it
	// and remove one item from the monitorList
	if (monitorList.size() > 0) {
	    ManagedIndexItem first = (ManagedIndexItem)monitorList.getFirst();

	    Timestamp firstTimeout = TimeCalculator.elapsedSince(first.getLastAccessTime());
	    System.err.println("firstTimeout = " + firstTimeout);


	    if (TimeCalculator.greaterThan(firstTimeout, timeout)) {
		// the first element has a big enough timeout
		System.err.print("Removeing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime() + ".Timeout = " + firstTimeout);
		monitorList.remove(first);
	    
		cache.removeItem(first.getPosition());
	    }
		
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
	    System.err.println("Queuing " + item.getPosition() + ".Remove list size = " + monitorList.size());
	}

	return null;
    }
	    

    /**
     * TO String
     */
    public String toString() {
	return "RemoveAfterTimeoutPolicy: queueWindow = " + monitorList.size() + "/" + timeout;
    }
}
