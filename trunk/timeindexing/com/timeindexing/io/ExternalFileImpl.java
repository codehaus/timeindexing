// ExternalFileImpl.java

package com.timeindexing.io;

import com.timeindexing.time.*;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexHeader;
import com.timeindexing.index.IndexCache;
import com.timeindexing.index.DataCache;
import com.timeindexing.index.DataStyle;
import com.timeindexing.index.AnnotationStyle;


import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import java.util.Properties;

/**
 * An implementation of an Index object.
 * This is an UNFINISHED implementation of a full index.
 * It represents the index header, the index stream and the data stream.
 */
public class ExternalFileImpl implements Index, Serializable {
    IndexHeader header = null;
    IndexCache indexCache = null;
    DataCache dataCache=null;
    
    // HeaderInteractor
    // IndexInteractor
    // DataInteractor


    // TODO
    String indexName = null;
    ID indexID = null;
    Timestamp startTime = Timestamp.ZERO;
    Timestamp endTime = Timestamp.ZERO;
    Timestamp firstTime = Timestamp.ZERO;
    Timestamp lastTime = Timestamp.ZERO;
    long itemSize = 0;
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

    /**
     * Create a ExternalFileImpl.
     */
    public ExternalFileImpl(Properties indexProperties) {
	indexName = indexProperties.getProperty("name");
	indexID = new UID();
	startTime = new MillisecondTimestamp();
    }

	
    public ExternalFileImpl(String name) {
	indexName = name;
	indexID = new UID();
	startTime = new MillisecondTimestamp();
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
     * Get the size of the items.
     */
    public long getItemSize() {
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
     * Add an Index Item to the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     */
    public synchronized long addItem(IndexItem item) {
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
     * Get the no of items in the index.
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
	    // this quick implementation uses LinkedList.
	    // the REAL implementation will use a data structure
	    // that has a get(long n) method  rather than
	    // java.util.LinkedList
	    int intN = (int)n;
	    return (IndexItem)indexItems.get(intN);
	}
    }

    /**
     * Does a timestamp fall within the bounds of the Index.
     * The bounds are the first time data is put in and the last
     * time data is put in the Index.
     */
    public boolean contains(Timestamp t) {
	// if firstTime <= t <= lastTime
	//   return true
	// otherwise return false
	if (TimeCalculator.lessThanEquals(this.getFirstTime(), t) &&
	    TimeCalculator.lessThanEquals(t, this.getLastTime())) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Flush this index.
     */
    public boolean flush() {
	return false;
    }

    /**
     * Close this index.
     */
    public boolean close() {
	endTime = new MillisecondTimestamp();
	return true;
    }

}
