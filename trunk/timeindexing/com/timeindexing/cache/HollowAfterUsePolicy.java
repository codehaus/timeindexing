// HollowAfterUsePolicy.java

package com.timeindexing.cache;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimeCalculator;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Hollow items after they have been used.
 */
public class HollowAfterUsePolicy extends AbstractCachePolicy implements CachePolicy {
    // how many to queue before hollowing
    int queueWindow = 0;

    //TreeSet monitorList = new TreeSet();

    /**
     * Construct this policy object
     */
    public HollowAfterUsePolicy() {
	monitorList = new LinkedList(); // new TreeSet(new IndexItemComparator()); 
	queueWindow = 40;
    }

    /**
     * Construct this policy object
     */
    public HollowAfterUsePolicy(int winSize) {
	monitorList = new  LinkedList(); // new TreeSet(new IndexItemComparator()); //
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
	    System.err.println("DeQueue " + item.getPosition() + ".Hollow list size = " + monitorList.size());
	}

	// if there's something to delete
	// remove one item
	if (monitorList.size() >= queueWindow) {
	    IndexItem first = (IndexItem)monitorList.getFirst();


	    System.err.print("Hollowing " + first.getPosition() + ". Last accesse time: " + first.getLastAccessTime() + ".Hollow list size = " + monitorList.size());
	    monitorList.remove(first);
	    
	    cache.hollowItem(first.getPosition());
	}

	return null;
    }

    /**
     * Called at the end of cache.getItem()
     * @param item the item being returned
     */
    public Object notifyGetItemEnd(IndexItem item, long pos) {
	monitorList.add(item);
	System.err.println("Queuing " + item.getPosition() + ".Hollow list size = " + monitorList.size());

	return null;
    }
	    

    /**
     * TO String
     */
    public String toString() {
	return "HollowAfterUsePolicy: queueWindow = " + monitorList.size() + "/" + queueWindow;
    }

    private class IndexItemComparator implements Comparator {
	public int compare(Object o1, Object o2) {
	    ManagedIndexItem item1 = (ManagedIndexItem)o1;
	    ManagedIndexItem item2 = (ManagedIndexItem)o2;

	    if (TimeCalculator.lessThan(item1.getLastAccessTime(), item2.getLastAccessTime())) {
		//System.err.println("Item " + item1.getPosition() + " < " + item2.getPosition());
		return -1;
	    } else 
	    if (TimeCalculator.greaterThan(item1.getLastAccessTime(), item2.getLastAccessTime())) {
		//System.err.println("Item " + item1.getPosition() + " > " + item2.getPosition());
		return 1;
	    } else {
		if (item1.equals(item2)) {
		    System.err.println("Item " + item1.getPosition() + " equals " + item2.getPosition());
		    return 0;
		} else {
		    //System.err.println("Item " + item1.getPosition() + " == " + item2.getPosition());
		    return 1;
		}
	    }
	}
    }

}

