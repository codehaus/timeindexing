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
    /**
     * Create an IncoreIndex
     */
    public IncoreIndex() {
    }

    /**
     * Initialize the object.
     */
    protected void init() {
	header = new IncoreIndexHeader(this,indexName);
	indexCache = new DefaultIndexCache(this);

	header.setIndexType(IndexType.INCORE_DT);

	// creating an Incore Index is effectively opening it
	closed = false;
    }



    /**
     * Open this index.
     */
     public boolean open(Properties properties) throws IndexSpecificationException, IndexOpenException {
	// check the passed in properties
	checkProperties(properties);

	// init the objects
	init();

	eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.OPENED, this));
	return true;
    }

    /**
     * Create this index.
     */
    public boolean create(Properties properties) throws IndexSpecificationException, IndexCreateException {
	// check the passed in properties
	checkProperties(properties);

	// init the objects
	init();

	// things to do the first time in
	// set the ID, the startTime, first offset, last offset
	ID indexID = new UID();
	header.setID(indexID);
	header.setStartTime(Clock.time.asMicros());
	header.setFirstOffset(new Offset(0));
	header.setLastOffset(new Offset(0));


	if (dataType != null) {
	    header.setIndexDataType(dataType);
	}

	eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.CREATED, this));
	closed = false;
	activate();


	return true;
    }

    /**
     * Get the  last time the index was flushed.
     */
    public Timestamp getLastFlushTime() {
	return Timestamp.ZERO;
    }

    /**
     * Get the IndexItem Position when the index was last flushed.
     * There is nothing to flush in an IncoreIndex, so
     * the Position of the last item is returned.
     */
    public Position getLastFlushPosition() {
	return new AbsolutePosition(getLength()-1);
    }
    
    /**
     * Get the Offset of the fisrt item.
     * There are no file offsets for an IncoreIndex, so
     * The offset into the index is returned.
     */
    public Offset getFirstOffset() {
	return new Offset(0);
    }

    /**
     * Get the Offset of the last item.
     * There are no file offsets for an IncoreIndex, so
     * The offset into the index is returned.
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
    public synchronized long addItem(DataItem dataitem) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException   {
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
    public synchronized long addItem(DataItem dataitem, Timestamp dataTS) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
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
     * Close this index.
     */
    public boolean close() {
	// if the index is activated
	// set the end time in the header
	if (this.isActivated()) {
	    header.setEndTime(Clock.time.asMicros());
	}

	eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.CLOSED, this));

	closed = true;
	activated = false;

	return true;
    }

    protected void checkProperties(Properties indexProperties) throws IndexSpecificationException {
	if (indexProperties.containsKey("name")) {
	    indexName = indexProperties.getProperty("name");
	} else {
	    throw new IndexSpecificationException("No 'name' specified for ExternalIndex");
	}

	if (indexProperties.containsKey("datatype")) {
	    dataType = DataTypeDirectory.find(indexProperties.getProperty("dataType"));
	}
    }

    /**
     * Get the headerfor the index.
     */
    public ManagedIndexHeader getHeader() {
	return header;
    }


}
