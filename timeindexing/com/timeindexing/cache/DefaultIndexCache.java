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
import com.timeindexing.index.PositionOutOfBoundsException;
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
    MassiveBitSet loadedMap = null;

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
	loadedMap = new MassiveBitSet();
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

	loadedMap.set(position);

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


	// return the cacheSize
	return cacheSize;

    }

    /**
     * Get the no of items in the cache
     */
    public synchronized long cacheSize() {
	return cacheSize;
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n) {
	if (indexItems == null) {
	    return null;
	} else {
	    if (n < 0) {
		throw new PositionOutOfBoundsException("Index value too low: " + n);
	    } else if (n >= indexItems.size()) {
		throw new PositionOutOfBoundsException("Index value too high: " + n);
	    } else {
		return (IndexItem)indexItems.get(n);
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
     * Hollow the IndexItem at the position.
     * This does nothing by default as the data will be lost.
     */
    public boolean hollowItem(long n) {
	if (indexItems == null) {
	    return false;
	} else {
	    if (n < 0) {
		throw new PositionOutOfBoundsException("Index value too low: " + n);
	    } else if (n >= indexItems.size()) {
		throw new PositionOutOfBoundsException("Index value too high: " + n);
	    } else {
		return false;
	    }
	}
    }
	


    /**
     * Contains the IndexItem at the speicifed position.
     * If the cache contains the item, it means it is loaded.
     */
    public boolean containsItem(long pos) {
	return loadedMap.get(pos);
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
     */
    public boolean hollowItem(Position p) {
	return hollowItem(p.value());
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
     * Create some sparce elements in the cache.
     */
    protected long sparce(long start, long end) {
	long  current = start;

	System.err.println("Sparce from " + start + " to " + end);

	while (current <= end) {
	    indexItems.add(null);

	    current++;
	}

	return current;
    }

}
