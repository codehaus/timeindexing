// AbstractIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Clock;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Lifetime;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.Position;
import com.timeindexing.data.DataItem;
import com.timeindexing.event.*;

import java.util.Properties;

/**
 * An abstract implementation of an Index object.
 * It represents the index header, the index stream and the data stream.
 */
public abstract class AbstractIndex implements ExtendedIndex, ExtendedIndexHeader {
    // The Index name
    String indexName = null;

    // The Index type.
    int indexType = -1;

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
     * Get the type of the index.
     */
    public int getIndexType() {
	return indexType;
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
     * Get the data style.
     * Either inline or external.
     */
    public int getDataStyle() {
	return header.getDataStyle();
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
    public abstract long addItem(DataItem item);

    /**
     * Add a Data Item to the Index with a speicifed Data Timestamp
     */
    public abstract long addItem(DataItem item, Timestamp dataTime);

    /**
     * Add an Index Item to the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     */
    public synchronized long addItem(IndexItem item) {
	// TODO: check isActivated() and isTerminated()
	// before doing this

	// can't add anything if the index is terminated
	if (isTerminated()) {
	    throw new Error("Index terminated " + this);
	}

	if (!isActivated()) {
	    throw new Error("Index NOT activated " + this);
	}


	// add the item to the index item cache
	// the new size of the index is returned
	long cacheSize = indexCache.addItem(item);
	long newSize = header.getLength()+1;

	// get the time this item was set
	Timestamp last = indexCache.getLastIndexTime();

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
    public IndexItem getItem(long n) {
	lastAccessTime = Clock.time.asMicros();
	return indexCache.getItem(n);
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p) {
	lastAccessTime = Clock.time.asMicros();
	return indexCache.getItem(p);
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) {
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
     * Does a timestamp fall within the bounds of the Index.
     * The bounds are the first time data is put in and the last
     * time data is put in the Index.
     */
    public boolean contains(Timestamp t, IndexTimestampSelector sel) {
	return indexCache.contains(t, sel);
    }

    /**
     * Try and determine the position associated
     * with the speicifed data Timestamp.
     */
    public TimestampMapping locate(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) {
	return indexCache.locate(t, sel, lifetime);
    }


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
     * Close this index.
     */
    public boolean close() {

	eventMulticaster.firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.CLOSED, this));

	return true;
    }


    /**
     * Get the event listener.
     */
    public IndexEventMulticaster getEventMulticaster() {
	return eventMulticaster;
    }

    
}
