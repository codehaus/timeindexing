// RemoveAfterUsePolicy.java

package com.timeindexing.cache;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeCalculator;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Remove items after they have been used.
 */
public class RemoveAfterUsePolicy extends AbstractCachePolicy implements CachePolicy {
    // how many to queue before removeing
    int queueWindow = 0;

    /**
     * Construct this policy object
     */
    public RemoveAfterUsePolicy() {
	monitorList = new LinkedList(); //new IndexItemComparator());
	queueWindow = 40;
    }

    /**
     * Construct this policy object
     */
    public RemoveAfterUsePolicy(int winSize) {
	monitorList = new LinkedList(); // new IndexItemComparator());
	queueWindow = winSize;
    }

    /**
     * Called at the beginning of cache.getItem()
     * @param pos the position being requested
     */
    public Object notifyGetItemBegin(IndexItem item, long pos) {
	// if the item is in the monitorList remove it.
	if (monitorList.contains(item)) {
	    monitorList.remove(item);
	    System.err.println("DeQueue " + item.getPosition() + ".Remove list size = " + monitorList.size());
	}

	// if there's something to delete
	// remove one item
	if (monitorList.size() >= queueWindow) {
	    ManagedIndexItem first = (ManagedIndexItem)monitorList.getFirst();
	    System.err.print("Removeing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime() + ".Remove list size = " + monitorList.size());
	    monitorList.remove(first);
	    
	    cache.removeItem(first.getPosition());
	}

	return null;
    }

    /**
     * Called at the end of cache.getItem()
     * @param item the item being returned
     */
    public Object notifyGetItemEnd(IndexItem item, long pos) {
	monitorList.add(item);
	System.err.println("Queuing " + item.getPosition() + ".Remove list size = " + monitorList.size());

	return null;
    }
	    

    /**
     * TO String
     */
    public String toString() {
	return "RemoveAfterUsePolicy: queueWindow = " + monitorList.size() + "/" + queueWindow;
    }
}
