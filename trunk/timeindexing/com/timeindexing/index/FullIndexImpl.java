// FullIndexImpl.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Clock;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Lifetime;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Offset;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.Interval;
import com.timeindexing.data.DataItem;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import java.util.Properties;

/**
 * A FIRST CUT implementation of an Index object.
 * This is an UNFINISHED implementation of a full index.
 * It represents the index header, the index stream and the data stream.
 */
public class FullIndexImpl implements ManagedStoredIndex, Serializable {
    String indexName = null;
    ID indexID = null;
    int indexType = -1;
    Timestamp startTime = Timestamp.ZERO;
    Timestamp endTime = Timestamp.ZERO;
    Timestamp firstTime = Timestamp.ZERO;
    Timestamp lastTime = Timestamp.ZERO;
    int itemSize = 0;
    boolean fixedSize = false;
    long dataSize = 0;
    boolean bivariant = false;
    Map dataTypeMap = null;
    int dataStyle = DataStyle.INLINE;
    boolean hasAnnotations = false;
    int annotationStyle = AnnotationStyle.NONE;
    Map externalIndexMap = null;
    int length = 0;
    List indexItems = null;
    boolean activated = false;
    Timestamp lastAccessTime = null;
    boolean terminated = false;

    /**
     * Create a FullIndexImpl.
     */
    public FullIndexImpl(Properties indexProperties) {
	indexName = indexProperties.getProperty("name");
	indexID = new UID();
	indexType = IndexType.JAVASERIAL;
	startTime = Clock.time.asMicros();
    }

	
    public FullIndexImpl(String name) {
	indexName = name;
	indexType = IndexType.JAVASERIAL;
	indexID = new UID();
	startTime = Clock.time.asMicros();
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
	return indexID;
    }


    /**
     * Get the type of the index.
     */
    public int getIndexType() {
	return indexType;
    }

    /**
     * Get the filename of the index.
     * An Serializable index has no access to the filename.
     */
    public String getIndexPathName() {
	return null;
    }

    /**
     * Get the start time of the index.
     * This is when the index was created not necessarliy when the first item
     * was added to the index.
     */
    public Timestamp getStartTime() {
	return startTime;
    }

    /**
     * Get the end time of the index.
     * This is the time the last item was closed, not necessarliy when the last item
     * was added to the index.
     */
    public Timestamp getEndTime() {
	return endTime;
    }

    /**
     * Get the time the first IndexItem was put into the Index.
     */
    public Timestamp getFirstTime() {
	return firstTime;
    }

    /**
     * Get the time the last IndexItem was put into the Index.
     */
    public Timestamp getLastTime() {
	return lastTime;
    }


    /**
     * Get the data time for the first IndexItem in the Index.
     */
    public Timestamp getFirstDataTime() {
	return null;
    }

    /**
     * Get the data time for the last IndexItem in the Index.
     */
    public Timestamp getLastDataTime() {
	return null;
    }

    /**
     * Get the size of the items.
     */
    public int getItemSize() {
	return itemSize;
    }

    /**
     * Is the data fixed size.
     * That is, are all the data items the same no of bytes.
     */
    public boolean isFixedSizeData() {
	return fixedSize;
    }

    /**
     * Get the size of the data items, if there is fixed size data.
     */
    public long getDataSize() {
	return dataSize;
    }


    /**
     * Get the type name of the things in the data stream.
     */
    public String getDataType(ID typeID) {
	if (dataTypeMap == null) {
	    return null;
	} else {
	    return (String)dataTypeMap.get(typeID);
	}
    }

    /**
     * Does this index have a typed name.
     */
    public boolean hasDataType(String typeName) {
	if (dataTypeMap == null) {
	    return false;
	} else {
	    return dataTypeMap.containsValue(typeName);
	}
    }

    /**
     * Add a new data type
     * @return true, if a new type was added; false, if the index had this ID/typeName pair already
     */
    public boolean addDataType(ID typeID, String typeName) {
	if (dataTypeMap == null) {
	    dataTypeMap = new HashMap();
	}

	dataTypeMap.put(typeID, typeName);
	return true;
    }

    /**
     * Get the data style.
     * Either inline or external.
     */
    public int getDataStyle() {
	return dataStyle;
    }

    /**
     * Does this index have annotations.
     */
    public boolean hasAnnotations() {
	return hasAnnotations;
    }

    /**
     * Get the annotation style.
     * Either inline or external.
     */
    public int getAnnotationStyle() {
	return annotationStyle;
    }

    /**
     * Get the index URI of a nominated index.
     */
    public String getIndexURI(ID indexID) {
	if (externalIndexMap == null) {
	    return null;
	} else {
	    return (String)externalIndexMap.get(indexID);
	}
    }

    /**
     * Does this index have the URI of some other index
     */
    public boolean hasIndexURI(String URIName) {
	if (externalIndexMap ==  null) {
	    return false;
	} else {
	    return dataTypeMap.containsValue(URIName);
	}
    }

    /**
     * Add a new indexID/indexURI
     * @return true, if a new index URI was added; false, if the index had this ID/URI pair already
     */
    public boolean addIndexURI(ID indexID, String URIName) {
	if (externalIndexMap == null) {
	    externalIndexMap = new HashMap();
	}

	externalIndexMap.put(indexID, URIName);
	return true;
    }

    /**
     * Is the index bivariant.
     * That is, does the index have separate values for the index timestamp
     * and the data timestamp.
     */
    public boolean isBivariant() {
	return bivariant;
    }

   /**
     * Add a Data Item to the Index.
     * The data timestamp will be the same as the record timestamp.
     * The ID will be generated.
     * There are no annotations.
     * @param item the DataItem to add
     * @return the no of items in the index.
     */
    public synchronized long addItem(DataItem dataitem) {
	// set the ID to be the length
	// as it's unique
	long id = getLength();
	// the record Timestamp is now (as microseconds)
	Timestamp recordTS = Clock.time.asMicros();
	Timestamp dataTS = recordTS;

	IncoreIndexItem item = new IncoreIndexItem(dataTS, recordTS, dataitem, DataType.ANY, new SID(id), new SID(0));

	return addItem(item);
    }

    /**
     * Add a Data Item to the Index plus a Timestamp from the Data.
     * The ID will be generated.
     * There are no annotations.
     * @param item the IndexItem to add
     * @param dataTS the Timestamp for the data
     * @return the no of items in the index.
     */
    public synchronized long addItem(DataItem dataitem, Timestamp dataTS) {
	// set the ID to be the length
	// as it's unique
	long id = getLength();
	// the record Timestamp is now (as microseconds)
	Timestamp recordTS = Clock.time.asMicros();

	IncoreIndexItem item = new IncoreIndexItem(dataTS, recordTS, dataitem, DataType.ANY, new SID(id), new SID(0));

	return addItem(item);
    }

    /**
     * Add an Index Item to the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     */
    protected synchronized long addItem(IndexItem item) {
	// create a new list if needed
	if (indexItems == null) {
	    indexItems = new LinkedList();
	}

	// add the IndexItem
	indexItems.add(item);

	Timestamp itemTimestamp = item.getIndexTimestamp();

	if (firstTime == null || firstTime.equals(Timestamp.ZERO)) {
	    // the IndexItem is the first one in the Index
	    firstTime = itemTimestamp;
	}
	    
	// Set the last time and the end time of the index to be
	// the time of the last item added
	// Doing a close() will adjust the end time as well.
	lastTime = itemTimestamp;
	endTime = itemTimestamp;

	// increase the length
	length++;

	// return the length
	return length;

    }

    /**
     * Retrieve an Index Item into the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     */
    public long retrieveItem(IndexItem item) {
	return addItem(item);
    }

    /**
     * Get the no of items in the index.
     */
    public synchronized long getLength() {
	return length;
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n) {
	lastAccessTime = Clock.time.asMicros();

	if (indexItems == null) {
	    return null;
	} else {
	    // this quick implementation uses LinkedList.
	    // the REAL implementation will use a data structure
	    // that has a get(long n) method  rather than
	    // java.util.LinkedList
	    int intN = (int)n;
	    return (IndexItem)indexItems.get(intN);
	}
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(Position p) {
	return getItem(p.value());
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
	    IndexItem first = getItem(0);
	    IndexItem last = getItem(getLength()-1);

	    Timestamp firstTS =  null;
	    Timestamp lastTS = null;

	    if (sel == IndexTimestampSelector.DATA) {
		firstTS = first.getDataTimestamp();
		lastTS = last.getDataTimestamp();
	    } else {
		firstTS =  first.getIndexTimestamp();
		lastTS = last.getIndexTimestamp();
	    }

	    if (TimeCalculator.lessThanEquals(firstTS, t) &&
		TimeCalculator.lessThanEquals(t, lastTS)) {
		return true;
	    } else {
		return false;
	    }

    }


    /**
     * Select an Interval.
     */
    public Index select(Interval interval) {
	throw new UnsupportedOperationException("Called select().  This is not implemented here");
    }

    /**
     * Is this a selection.
     */
    public boolean isSelection() {
	throw new UnsupportedOperationException("Called isSelection().  This is not implemented here");
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
	return terminated;
    }

    /**
     * Make the Index finalized.
     * It is not possible to add items ever again
     * to an Index that has been terminated.
     */
    public Index terminate() {
	terminated = true;
	return this;
    }

    /**
     * Read data for an index item
     * given a DataReference.
     */
    public DataHolderObject readData(DataReference dataReference) {
	throw new RuntimeException("No need to read data. All data is available.  This should never happen.");
    }


    /**
     * Hollow out the IndexItem at the speicifed position.
     * This does nothing as it will throw away data that cannot be 
     * retrieved.
     */
    public boolean hollowItem(long pos) {
	return false;
    }
 
    /**
     * Hollow out the IndexItem at the speicifed position.
     * This does nothing as it will throw away data that cannot be 
     * retrieved.
     */
    public boolean hollowItem(Position pos) {
	return false;
    }
 
    /**
     * Get the  last time the index was flushed.
     */
    public Timestamp getLastFlushTime() {
	return Timestamp.ZERO;
    }

    /**
     * Get the IndexItem Position when the index was last flushed.
     */
    public Position getLastFlushPosition() {
	return null;
    }

    /**
     * Get the Offset of the fisrt item.
     */
    public Offset getFirstOffset() {
	return new Offset(0);
    }

    /**
     * Get the Offset of the last item.
     */
    public Offset getLastOffset() {
	return new Offset(getLength()-1);
    }


    /**
     * Open this index.
     */
    public boolean open() {
	return true;
    }

    /**
     * Create this index.
     */
    public boolean create() {
	activate();
	return true;
    }

    /**
     * Flush this index.
     */
    public boolean flush() {
	return true;
    }

    /**
     * Close this index.
     */
    public boolean close() {
	endTime = Clock.time.asMicros();
	return true;
    }

    /**
     * Try and determine the position associated
     * with the speicifed Timestamp.
     * @return null if no position is found
     */
    public TimestampMapping locate(Timestamp t, IndexTimestampSelector sel, Lifetime lifetime) {
	return null;
    }

    /**
     * Get the headerfor the index.
     */
    public ManagedIndexHeader getHeader() {
	return null;
    }

}
