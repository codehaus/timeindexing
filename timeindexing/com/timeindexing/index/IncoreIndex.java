// IncoreIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Clock;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Offset;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.data.DataItem;
import com.timeindexing.event.*;

import java.util.Properties;

/**
 * An implementation of an incore Index object.
 * It represents the index header, the index stream and the data stream.
 */
public class IncoreIndex extends AbstractIndex implements ManagedIndex {
    IndexEventMulticaster eventMulticaster = null;
    
    /**
     * Create an IncoreIndex
     */
    public IncoreIndex(Properties indexProperties) {
	indexName = indexProperties.getProperty("name");

	init();
    }

    /**
     * Create an IncoreIndex
     */
    public IncoreIndex(String name) {
	indexName = name;

	init();
    }

    /**
     * Initialize the object.
     */
    protected void init() {
	ID indexID = new UID();
	Timestamp startTime = Clock.time.asMicros();

	indexType = IndexType.INCORE;

	header = new IncoreIndexHeader(this,indexName);
	header.setID(indexID);
	header.setStartTime(startTime);
	indexCache = new DefaultIndexCache(this);
	eventMulticaster = new IndexEventMulticaster();
    }


    /**
     * Get the filename of the index.
     * An IncoreIndex has no file - it's in-core!
     */
    public String getFileName() {
	return null;
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
     * Add a Data Item to the Index.
     * The data timestamp will be the same as the record timestamp.
     * The ID will be generated.
     * There are no annotations.
     * @param item the DataItem to add
     * @return the no of items in the index.
     */
    public synchronized long addItem(DataItem dataitem) {
	return addItem(dataitem, null);
    }

    /**
     * Add a Data Item to the Index plus a Timestamp from the Data.
     * The ID will be generated.
     * There are no annotations.
     * @param item the IndexItem to add
     * @param dataTS the Timestamp for the data, null implies that
     * the data Timestamp is the same as the record Timestamp
     * @return the no of items in the index.
     */
    public synchronized long addItem(DataItem dataitem, Timestamp dataTS) {
	// set the ID to be the length
	// as it's unique
	long id = getLength();
	// the record Timestamp is now (as microseconds)
	Timestamp recordTS = Clock.time.asMicros();
	// the actual data Timestamp is the record Timestamp
	// if the dataTS param is null, it is the speicifed value otherwise
	Timestamp actualTS = (dataTS == null ? recordTS : dataTS);

	IncoreIndexItem item = new IncoreIndexItem(actualTS, recordTS, dataitem, dataitem.getDataType(), new SID(id), new SID(0));

	return addItem(item);
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
     * Read data for an index item
     * given a DataReference.
     */
    public DataHolderObject readData(DataReference dataReference) {
	throw new Error("No need to read data. All data is available");
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
     * Hollow the IndexItem at the position.
     * This does nothing as it will throw away data that cannot be 
     * retrieved.
     */
    public boolean hollowItem(Position p) {
	return false;
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
	return true;
    }


}
