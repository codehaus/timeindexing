// DefaultIndexCache.java

package com.timeindexing.cache;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.Lifetime;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndex;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.index.PositionOutOfBoundsException;
import com.timeindexing.index.DataHolder;
import com.timeindexing.index.DataAbstraction;
import com.timeindexing.util.DoubleLinkedList;
import com.timeindexing.util.MassiveBitSet;

/**
 * The default implementation of a cache which holds the index items.
 */
public class DefaultIndexCache implements IndexCache {
    // the size of the cache
    long cacheSize = 0;

    // a doubly linked list of all the IndexItems
    DoubleLinkedList indexItems = null;

    // a BitSet which says if an IndexItem is loaded or not
    MassiveBitSet loadedMask = null;

    // The current cache policy
    CachePolicy policy = null;

    // The amount of data held.
    long volumeHeld = 0;

    Timestamp firstIndexTime = Timestamp.ZERO;
    Timestamp lastIndexTime = Timestamp.ZERO;
    Timestamp firstDataTime = Timestamp.ZERO;
    Timestamp lastDataTime = Timestamp.ZERO;
    ManagedIndex myIndex = null;

    /**
     * Create a DefaultIndexCache object.
     */
    public DefaultIndexCache(ManagedIndex index) {
	myIndex = index;
	indexItems = new DoubleLinkedList();
	loadedMask = new MassiveBitSet();
    }

    /**
     * Get the no of items in the cache
     */
    public synchronized long size() {
	return cacheSize;
    }

    /**
     * Add an Index Item to the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     */
    public synchronized long addItem(IndexItem item, Position position) {
	return addItem(item, position.value());
    }

    /**
     * Add an Index Item to the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     */
    public synchronized long addItem(IndexItem item, long position) {
	// create a new list if needed
	if (indexItems == null) {
	    indexItems = new DoubleLinkedList();
	}

	// call the policy
	if (policy != null) {
	    policy.notifyAddItemBegin(item, position);
	}

	loadedMask.set(position);

	// add the IndexItem to the rlevant position
	// if the doubly linked list is not big enough
	// list cells are allocated to fill the gap
	// TODO: it would be better to have a spare data structure
	if (position < indexItems.size()) {
	    // set relevant element in list
	    indexItems.set(position, item);
	} else if (indexItems.size() == position) {
	    // add as next list element
	    indexItems.add(item);
	} else {
	    // fill in with psace space
	    sparce(indexItems.size(), position-1);
	    indexItems.add(item);
	}

	// increase the cacheSize
	cacheSize++;

	Timestamp itemIndexTimestamp = item.getIndexTimestamp();
	Timestamp itemDataTimestamp = item.getDataTimestamp();

	if (firstIndexTime == null || firstIndexTime.equals(Timestamp.ZERO)) {
	    // the IndexItem is the first one in the Index
	    firstIndexTime = itemIndexTimestamp;
	}
	    
	if (firstDataTime == null || firstDataTime.equals(Timestamp.ZERO)) {
	    // the IndexItem is the first one in the Index
	    firstDataTime = itemDataTimestamp;
	}
	    
	// Set the last time of the index to be
	// the time of the last item added
	lastIndexTime = itemIndexTimestamp;
	lastDataTime = itemDataTimestamp;

	// calculate the held volume
	DataAbstraction data = ((ManagedIndexItem)item).getDataAbstraction();
	
	if (data instanceof DataHolder) {
	    // it really has the data 
	    volumeHeld += item.getDataSize().value();

	    //System.err.println("Volume + = " + volumeHeld);
	}



	// call the policy
	if (policy != null) {
	    policy.notifyAddItemEnd(item, position);
	}

	// return the cacheSize
	return cacheSize;

    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long pos) {
	if (indexItems == null) {
	    return null;
	} else {
	    if (pos < 0) {
		throw new PositionOutOfBoundsException("Index value too low: " + pos);
	    } else if (pos >= indexItems.size()) {
		throw new PositionOutOfBoundsException("Index value too high: " + pos);
	    } else {
		IndexItem item = (IndexItem)indexItems.get(pos);

		// call the policy
		if (policy != null) {
		    policy.notifyGetItemBegin(item, pos);
		}

		// call the policy
		if (policy != null) {
		    policy.notifyGetItemEnd(item, pos);
		}

		return item;
	    }
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p) {
	return getItem(p.value());
    }


    /**
     * Contains the IndexItem at the speicifed position.
     * If the cache contains the item, it means it is loaded.
     */
    public boolean containsItem(long pos) {
	return loadedMask.get(pos);
    }


    /**
     * Contains the IndexItem at the speicifed position.
     * If the cache contains the item, it means it is loaded.
     */
    public boolean containsItem(Position p) {
	return containsItem(p.value());
    }
	    
    /**
     * Hollow the IndexItem at the position.
     * This does nothing by default as the data will be lost.
     */
    public boolean hollowItem(long pos) {
	if (indexItems == null) {
	    return false;
	} else {
	    if (pos < 0) {
		throw new PositionOutOfBoundsException("Index value too low: " + pos);
	    } else if (pos >= indexItems.size()) {
		throw new PositionOutOfBoundsException("Index value too high: " + pos);
	    } else {
		return false;
	    }
	}
    }
	

    /**
     * Hollow the IndexItem at the position.
     */
    public boolean hollowItem(Position p) {
	return hollowItem(p.value());
    }


    /**
     * Remove the IndexItem at the speicifed position.
     * This does nothing by default as the data will be lost.
     */
    public boolean removeItem(long pos) {
	if (indexItems == null) {
	    return false;
	} else {
	    if (pos < 0) {
		throw new PositionOutOfBoundsException("Index value too low: " + pos);
	    } else if (pos >= indexItems.size()) {
		throw new PositionOutOfBoundsException("Index value too high: " + pos);
	    } else {
		return false;
	    }
	}
    }

    /**
     * Remove the IndexItem at the speicifed position.
     */
    public boolean removeItem(Position p) {
	return removeItem(p.value());
    }

    /**
     * Clear the whole cache
     * This does nothing by default as the data will be lost.
     */
    public boolean clear() {
	return false;
    }

    /**
     * Get the current data volume held by IndexItems in this cache.
     */
    public long getDataVolume() {
	return volumeHeld;
    }


    /**
     * Get the time the first IndexItem was put into the Index.
     */
    public Timestamp getFirstIndexTime() {
	return firstIndexTime;
    }

    /**
     * Get the time the last IndexItem was put into the Index.
     */
    public Timestamp getLastIndexTime() {
	return lastIndexTime;
    }

    /**
     * Get the time the first IndexItem was put into the Index.
     */
    public Timestamp getFirstDataTime() {
	return firstDataTime;
    }

    /**
     * Get the time the last IndexItem was put into the Index.
     */
    public Timestamp getLastDataTime() {
	return lastDataTime;
    }

    /**
     * Set the cache policy.
     * @return the old cache policy
     */
    public CachePolicy setPolicy(CachePolicy pol) {
	CachePolicy oldPolicy = policy;

	policy = pol;
	policy.setIndexCache(this);

	return oldPolicy;
    }

    /**
     * Get the current cache policy.
     */
    public CachePolicy getPolicy() {
	return policy;
    }

    /**
     * Create some sparce elements in the cache.
     */
    protected long sparce(long start, long end) {
	long  current = start;

	//System.err.println("Sparce from " + start + " to " + end);

	while (current <= end) {
	    indexItems.add(null);

	    current++;
	}

	return current;
    }

}
