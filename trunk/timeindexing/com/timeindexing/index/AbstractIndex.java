// AbstractIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Clock;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Lifetime;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.Position;
import com.timeindexing.data.DataItem;
import com.timeindexing.event.*;

import java.util.Properties;
//import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Comparator;

/**
 * An abstract implementation of an Index object.
 * It represents the index header, the index stream and the data stream.
 */
public abstract class AbstractIndex implements ExtendedIndex, ExtendedIndexHeader,  IndexEventGenerator {
    // The Index name
    String indexName = null;

    // Is the Index closed.
    // Items can only be added when the Index is NOT closed.
    boolean closed = true;

    // Is the Index activated.
    // Items can only be added when the Index is activated.
    // An index that is finalized can NEVER be activated.
    boolean activated = false;

    // Values from the header are kep here.
    // They can be mapped out to a file if necessary.
    ManagedIndexHeader header = null;

    // a cache of all the index items.
    // it can have various policies for holding IndexItems.
    IndexCache indexCache = null;

    // The last time that an index item was accessed from the index.
    Timestamp lastAccessTime = null;

    // an event multicaster
    IndexEventMulticaster eventMulticaster = new IndexEventMulticaster();

    // Local variables for temporary use
    // index type
    //int indexType = -1;


    // data type
    DataType dataType = DataType.NOTSET_DT;


    TreeMap searchTree = null;


    protected AbstractIndex() {
	;
    }

    /**
     * Get the name of the index.
     */
    public String getName() {
	return indexName;
    }

    /**
     * Get an ID of an index.
     */
    public ID getID() {
	return header.getID();
    }


    /**
     * Get the start time of the index.
     * This is when the index was created not necessarliy when the first item
     * was added to the index.
     */
    public Timestamp getStartTime() {
	return header.getStartTime();
    }

    /**
     * Get the end time of the index.
     * This is the time the last item was closed, not necessarliy when the last item
     * was added to the index.
     */
    public Timestamp getEndTime() {
	return header.getEndTime();
    }

    /**
     * Get the time the first IndexItem was put into the Index.
     */
    public Timestamp getFirstTime() {
	return header.getFirstTime();
    }

    /**
     * Get the time the last IndexItem was put into the Index.
     */
    public Timestamp getLastTime() {
	return header.getLastTime();
    }


    /**
     * Get the data time for the first IndexItem in the Index.
     */
    public Timestamp getFirstDataTime() {
	return header.getFirstDataTime();
    }

    /**
     * Get the data time for the last IndexItem in the Index.
     */
    public Timestamp getLastDataTime() {
	return header.getLastDataTime();
    }

    /**
     * Get the size of the items.
     */
    public int getItemSize() {
	return header.getItemSize();
    }

    /**
     * Does the index have fixed size data.
     */
    public boolean isFixedSizeData() {
	return header.isFixedSizeData();
    }

   /**
     * Get the size of the data items, if there is fixed size data.
     */
    public long getDataSize() {
	return header.getDataSize();
    }


    /**
     * Get the type name of the things in the data stream.
     */
    public String getDataType(ID typeID) {
	return header.getDataType(typeID);
    }

    /**
     * Does this index have a typed name.
     */
    public boolean hasDataType(String typeName) {
	return header.hasDataType(typeName);
    }

    /**
     * Add a new data type
     * @return true, if a new type was added; false, if the index had this ID/typeName pair already
     */
    public boolean addDataType(ID typeID, String typeName) {
	return header.addDataType(typeID, typeName);
    }

    /**
     * Get the index type.
     * Either inline or external.
     */
    public IndexType getIndexType() {
	return header.getIndexType();
    }

    /**
     * Get the index data type.
     * Some indexes have the same type throughout,
     * other have mixed type data.
     */
    public DataType getIndexDataType() {
	return header.getIndexDataType();
    }

    /**
     * Is the index still in time order.
     */
    public boolean isInTimeOrder() {
	return header.isInTimeOrder();
    }

    /**
     * Does this index have annotations.
     */
    public boolean hasAnnotations() {
	return header.hasAnnotations();
    }

    /**
     * Get the annotation style.
     * Either inline or external.
     */
    public int getAnnotationStyle() {
	return header.getAnnotationStyle();
    }

    /**
     * Get the index URI of a nominated index.
     */
    public String getIndexURI(ID indexID) {
	return header.getIndexURI(indexID);
    }

    /**
     * Does this index have the URI of some other index
     */
    public boolean hasIndexURI(String URIName) {
	return header.hasIndexURI(URIName);
    }

    /**
     * Add a new indexID/indexURI
     * @return true, if a new index URI was added; false, if the index had this ID/URI pair already
     */
    public boolean addIndexURI(ID indexID, String URIName) {
	return header.addIndexURI(indexID, URIName);
    }

    /**
     * Get the no of items in the index.
     */
    public synchronized long getLength() {
	return header.getLength();
    }

    /**
     * Add a Data Item to the Index.
     */
    public abstract long addItem(DataItem item) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException;

    /**
     * Add a Data Item to the Index with a speicifed Data Timestamp
     */
    public abstract long addItem(DataItem item, Timestamp dataTime) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException;

    /**
     * Add an Index Item to the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     * @throws IndexTerminatedException if the index has been terminated
     * and an attempt is made to add an Item
     * @throws IndexActivationException if the index has NOT been activated
     * and an attempt is made to add an Item
     */
    public synchronized long addItem(IndexItem item) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {

	// can't add anything if the index is terminated
	// check this first as this can never change.
	if (isTerminated()) {
	    throw new IndexTerminatedException("addItem: Index terminated " + this);
	}

	// now check the values that can possibly change

	// can't add anything if the index is closed
	if (isClosed()) {
	    throw new IndexClosedException("addItem: Index closed " + this);
	}

	// can't add anything if the index is NOT activated
	if (!isActivated()) {
	    throw new IndexActivationException("addItem: Index NOT activated " + this);
	}


	// add the item to the index item cache
	// the new size of the index is returned
	long cacheSize = indexCache.addItem(item);
	long newSize = header.getLength()+1;

	// get the time this item was set
	Timestamp last = item.getIndexTimestamp();

	// tell the header how big the index is now
	header.setLength(newSize);

	if (newSize == 1) { // this is the first index item
	    header.setFirstTime(last); // so set the record time of the first item
	    header.setFirstDataTime(item.getDataTimestamp()); // and set the data time of the first item
	}

	// now set last and end times in the header
	header.setLastTime(last);
	header.setEndTime(last);
	// and the last data time
	header.setLastDataTime(item.getDataTimestamp());

	// now set the item's position and
	// bind it to the index
	ManagedIndexItem itemM = (ManagedIndexItem)item;
	itemM.setPosition(new AbsolutePosition(newSize - 1));
	itemM.setIndex(this);

	// tell all the listeners that an item has been added
	eventMulticaster.fireAddEvent(new IndexAddEvent(indexName, header.getID(), item, this));

	return newSize;
    }


    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n) throws GetItemException {
	setLastAccessTime();

	IndexItem item = indexCache.getItem(n);

	// tell all the listeners that an item has been accessed
	eventMulticaster.fireAccessEvent(new IndexAccessEvent(indexName, header.getID(), item, this));

	return item;
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p) throws GetItemException {
        if (p == Position.TOO_LOW) {
	    throw new PositionOutOfBoundsException("Position TOO_LOW");
	} else if (p == Position.TOO_HIGH) {
	    throw  new PositionOutOfBoundsException("Position TOO_HIGH");
	} else {
	    setLastAccessTime();

	    IndexItem item = indexCache.getItem(p);

	    // tell all the listeners that an item has been accessed
	    eventMulticaster.fireAccessEvent(new IndexAccessEvent(indexName, header.getID(), item, this));

	    return item;
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) throws GetItemException {
	TimestampMapping tsm = locate(t, sel, lifetime);
	return getItem(tsm.position());
    }

    
    /**
     * Get the  last time an IndexItem was accessed from the index.
     */
    public Timestamp getLastAccessTime() {
	return lastAccessTime;
    }


    /**
     * Set the  last time an IndexItem was accessed from the index.
     */
    protected Index setLastAccessTime() {
	lastAccessTime = Clock.time.asMicros();
	return this;
    }


   /**
     * Get the indexPath of the index.
     */
    public String getIndexPathName() {
	return header.getIndexPathName();
    }


    /**
     * Get the path of the data if the index data style
     * is external or shadow.
     * @return null if there is no data path
     */
    public String getDataPathName() {
	return header.getDataPathName();
    }

    /**
     * Get the description for an index.
     * @return null if there is no description
     */
    public Description getDescription() {
	return header.getDescription();
    }

    /**
     * Set the description.
     * This is one of the few attributes of an index that can be set directly.
     */
    public Index updateDescription(Description description) {
	header.setDescription(description);
	return this;
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
	    if (TimeCalculator.lessThanEquals(this.getFirstTime(), t) &&
		TimeCalculator.lessThanEquals(t, this.getLastTime())) {
		System.err.println("IndexTS = " + t + " is contained in " +
				   this.getFirstTime() + " to " +
				   this.getLastTime());
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
	System.err.println("AbstractIndex: locate: " + "TS = " + t);

	if (! contains(t, selector)) { // timestamp t is not in this index
	    // now try and determine if it is too low or too high
	    if (selector == IndexTimestampSelector.DATA) {
		if (TimeCalculator.lessThan(t, this.getFirstDataTime())) {
		    return new TimestampMapping(t, Position.TOO_LOW);
		} else if (TimeCalculator.greaterThan(t, this.getLastDataTime())) {
		    return new TimestampMapping(t, Position.TOO_HIGH);
		} else {
		    throw new RuntimeException("AbstractIndex: locate failed to process timestamp " + t + ". First Data time = " + this.getFirstDataTime() + ". Last Data time = " + this.getLastDataTime() + ".");
		}
	    } else {
		if (TimeCalculator.lessThan(t, this.getFirstTime())) {
		    return new TimestampMapping(t, Position.TOO_LOW);
		} else if (TimeCalculator.greaterThan(t, this.getLastTime())) {
		    return new TimestampMapping(t, Position.TOO_HIGH);
		} else {
		    throw new RuntimeException("AbstractIndex: locate failed toprocess  timestamp " + t + ". First Index time = " + this.getFirstTime() + ". Last Index time = " + this.getLastTime() + ".");
		}
	    }
	} else {
	    // ensure the searchTree i set up
	    if (searchTree == null) {
		searchTree = new TreeMap(itemComparator);

	    }
	    // now search for the timestamp
	    // and build up a tree cache for later reuse.
	    try {
		TimestampMapping mapping = binarySearch(t, 0, getLength()-1, selector, lifetime, 0);
		System.err.println("AbstractIndex: locate: " + "mapping = " + mapping);
		return mapping;
	    } catch (GetItemException gie) {
		return null;
	    }
	}	
    }


    /**
     * Do a binary search of the list.
     */
    protected TimestampMapping binarySearch(Timestamp t, long start, long end, IndexTimestampSelector selector, Lifetime lifetime, int depth) throws GetItemException {
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
		
    /**
     * Is the Index activated.
     */
    public boolean isActivated() {
	return activated;
    }

    /**
     * Make the Index activated.
     */
    public Index activate() {
	activated = true;
	return this;
    }


    /**
     * Is the Index terminated.
     */
    public boolean isTerminated() {
	return header.isTerminated();
    }

    /**
     * Make the Index finalized.
     * It is not possible to add items ever again
     * to an Index that has been terminated.
     */
    public Index terminate() {
	header.terminate();
	return this;
    }

    /**
     * Flush this index.
     */
    public boolean flush() {

	eventMulticaster.firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.FLUSHED, this));

	return true;
    }


    /**
     * Is the Index closed.
     */
    public boolean isClosed() {
	return closed;
    }

   /**
     * Close this index.
     */
    public boolean close() {
	closed = true;
	activated = false;

	eventMulticaster.firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.CLOSED, this));

	return true;
    }


    /**
     * Get the event listener.
     */
    public IndexEventMulticaster eventMulticaster() {
	return eventMulticaster;
    }

    /**
     * Add a IndexPrimaryEventListener.
     */
    public void addPrimaryEventListener(IndexPrimaryEventListener l) {
	eventMulticaster.addPrimaryEventListener(l);
    }

    /**
     * Remove a IndexPrimaryEventListener.
     */
     public void removePrimaryEventListener(IndexPrimaryEventListener l) {
	 eventMulticaster.removePrimaryEventListener(l);
     }

    /**
     * Add a IndexAddEventListener.
     */
    public void addAddEventListener(IndexAddEventListener l) {
	eventMulticaster.addAddEventListener(l);
    }

    /**
     * Remove a IndexAddEventListener.
     */
    public void removeAddEventListener(IndexAddEventListener l) {
	eventMulticaster.removeAddEventListener(l);
    }

    /**
     * Add a IndexAccessEventListener.
     */
    public void addAccessEventListener(IndexAccessEventListener l) {
	eventMulticaster.addAccessEventListener(l);
    }

    /**
     * Remove a IndexAccessEventListener.
     */
    public void removeAccessEventListener(IndexAccessEventListener l) {
	eventMulticaster.removeAccessEventListener(l);
    }


}
