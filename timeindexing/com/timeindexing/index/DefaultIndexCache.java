// DefaultIndexCache.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.Lifetime;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.util.DoubleLinkedList;

//import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Comparator;

/**
 * The default implementation of a cache which holds the index items.
 */
public class DefaultIndexCache implements IndexCache {
    int length = 0;
    DoubleLinkedList indexItems = null;
    TreeMap searchTree = null;

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
    }

    /**
     * Add an Index Item to the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     */
    public synchronized long addItem(IndexItem item) {
	// create a new list if needed
	if (indexItems == null) {
	    indexItems = new DoubleLinkedList();
	}

	// add the IndexItem
	indexItems.add(item);

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

	// increase the length
	length++;

	// return the length
	return length;

    }

    /**
     * Get the no of items in the index.
     * TODO: put this in the header
     */
    public synchronized long length() {
	return length;
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
	    } else if (n >= length()) {
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
	    } else if (n >= length()) {
		throw new PositionOutOfBoundsException("Index value too high: " + n);
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
     * Does a timestamp fall within the bounds of the Index.
     * The bounds are the first time data is put in and the last
     * time data is put in the Index.
     */
    public boolean contains(Timestamp t, IndexTimestampSelector selector) {
	// if firstIndexTime <= t <= lastIndexTime
	//   return true
	// otherwise return false
	if (selector == IndexTimestampSelector.DATA) {
	    if (TimeCalculator.lessThanEquals(this.getFirstDataTime(), t) &&
		TimeCalculator.lessThanEquals(t, this.getLastDataTime())) {
		System.err.println("DataTS = " + t + " is contained in " +
				   this.getFirstDataTime() + " to " +
				   this.getLastDataTime());
		return true;
	    } else {
		return false;
	    }
	} else {
	    if (TimeCalculator.lessThanEquals(this.getFirstIndexTime(), t) &&
		TimeCalculator.lessThanEquals(t, this.getLastIndexTime())) {
		System.err.println("IndexTS = " + t + " is contained in " +
				   this.getFirstIndexTime() + " to " +
				   this.getLastIndexTime());
		return true;
	    } else {
		return false;
	    }
	}
    }


    /**
     * Try and determine the position associated
     * with the speicifed Timestamp.
     * @return null if no position is found
     */
    public TimestampMapping locate(Timestamp t, IndexTimestampSelector selector, Lifetime lifetime) {
	System.err.println("DefaultIndexCache: locate: " + "TS = " + t);

	if (! contains(t, selector)) { // timestamp t is not in this index
	    // now try and determine if it is too low or too high
	    if (selector == IndexTimestampSelector.DATA) {
		if (TimeCalculator.lessThan(t, this.getFirstDataTime())) {
		    return new TimestampMapping(t, Position.TOO_LOW);
		} else if (TimeCalculator.greaterThan(t, this.getLastDataTime())) {
		    return new TimestampMapping(t, Position.TOO_HIGH);
		} else {
		    throw new RuntimeException("DefaultIndexCache: locate failed to process timestamp " + t + ". First Data time = " + this.getFirstDataTime() + ". Last Data time = " + this.getLastDataTime() + ".");
		}
	    } else {
		if (TimeCalculator.lessThan(t, this.getFirstIndexTime())) {
		    return new TimestampMapping(t, Position.TOO_LOW);
		} else if (TimeCalculator.greaterThan(t, this.getLastIndexTime())) {
		    return new TimestampMapping(t, Position.TOO_HIGH);
		} else {
		    throw new RuntimeException("DefaultIndexCache: locate failed toprocess  timestamp " + t + ". First Index time = " + this.getFirstIndexTime() + ". Last Index time = " + this.getLastIndexTime() + ".");
		}
	    }
	} else {
	    // ensure the searchTree i set up
	    if (searchTree == null) {
		searchTree = new TreeMap(itemComparator);

	    }
	    // now search for the timestamp
	    return binarySearch(t, 0, length()-1, selector, lifetime, 0);
	}	
    }


    /**
     * Do a binary search of the list.
     */
    protected TimestampMapping binarySearch(Timestamp t, long start, long end, IndexTimestampSelector selector, Lifetime lifetime, int depth) {
	IndexItem item = null;
	IndexItem itemN = null;

	Timestamp itemTS = null;
	Timestamp itemTSN = null;
	

	long halfway = (start + end) / 2;
	long halfwayN = halfway+1;
	AbsolutePosition halfwayPos = new AbsolutePosition(halfway);
	AbsolutePosition halfwayPosNext = new AbsolutePosition(halfway+1);
	
	//System.err.print("binarySearch " + depth + ": " + t + "\t" + start + "\t" + end + "\t");

	// try and find index item in search tree, if not found
	// go and get it from list
	if ((item = (IndexItem)searchTree.get(halfwayPos)) == null) {
	    // position not in the tree
	    // so get it from the index and put it in the tree
	    item = getItem(halfway);
	    searchTree.put(halfwayPos, item);
	    //System.err.print("GET " + halfway + "\t");
	} else {
	    //System.err.print("TREE " + halfway + "\t");
	}

	// try and find index item in search tree, if not found
	// go and get it from list
	if ((itemN = (IndexItem)searchTree.get(halfwayPosNext)) == null) {
	    // position not in the tree
	    // so get it from the index and put it in the tree
	    itemN = getItem(halfwayN);
	    searchTree.put(halfwayPosNext, itemN);
	    //System.err.print("GET " + halfwayN + "\t");
	} else {
	    //System.err.print("TREE " + halfwayN + "\t");
	}

	// get the relevant timestamps out of rge Index Items
	if (selector == IndexTimestampSelector.DATA) {
	    itemTS = item.getDataTimestamp();
	    itemTSN =itemN.getDataTimestamp();
	} else {
	    itemTS = item.getIndexTimestamp();
	    itemTSN = itemN.getIndexTimestamp();
	}


	//System.err.print(itemTS + "\t" + itemTSN);
	//System.err.println();
	    

	if (TimeCalculator.greaterThanEquals(t, itemTS) &&
	    TimeCalculator.lessThan(t, itemTSN)) {

	    // if the timestamp is between itemTS and itemTSN
	    // then we are close

	    // the time is between two timestamps
	    if (lifetime == Lifetime.CONTINUOUS) {
		// if lifetimes are continuous then item has a lifetime
		// from its own timestamp upto itemN's timestamp
		// which means that item is the IndexItem to return
		return new TimestampMapping(itemTS, item.getPosition());
	    } else {
		// if lifetimes are discrete then item's lifetime
		// is a point in time and Timestamp t is after item,
		// which means that itemN is the IndexItem to return
		return new TimestampMapping(itemTSN,  itemN.getPosition());
	    }
	} else if (TimeCalculator.equals(t, itemTSN)) {
	    // if the timestamp equals  itemTSN
	    // we are there
	    return new TimestampMapping(itemTSN,  itemN.getPosition());
	} else if (TimeCalculator.lessThan(t, itemTS)) {
	    // the timestamp is in first half, so search that half
	    return binarySearch(t, start, halfway, selector, lifetime, depth+1);
	} else {
	    // the timestamp is in second half, so search that half
	    return binarySearch(t, halfway, end, selector, lifetime, depth+1);
	}
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

    private Comparator itemComparator = new Comparator() {
	    public int compare(Object o1, Object o2) {
		Position p1 = (Position)o1;
		Position p2 = (Position)o2;
		
		long vp1 = p1.value();
		long vp2 = p2.value();

		if (vp1 < vp2) {
		    return -1;
		} else if (vp1 > vp2) {
		    return 1;
		} else { // vp1 == vp2
		    return 0;
		}
	    }

	    public boolean equals(Object obj) {
		return super.equals(obj);
	    }
	};
		
		

}
