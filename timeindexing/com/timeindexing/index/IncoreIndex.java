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
import com.timeindexing.cache.DefaultIndexCache;
import com.timeindexing.event.*;
import java.util.Properties;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * An implementation of an incore Index object.
 * It represents the index header, the index stream and the data stream.
 */
public class IncoreIndex extends AbstractManagedIndex implements ManagedIndex {
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

	setIndexType(IndexType.INCORE_DT);

	// creating an Incore Index is effectively opening it
	closed = false;
    }



    /**
     * Open this index.
     */
    public boolean open(Properties properties) throws IndexSpecificationException, IndexOpenException {
	// check the passed in properties
	checkOpenProperties(properties);

	// check to see if this index is already open and registered
	try {
	    String uri = generateURI(getName()).toString();

	    if (isOpen(uri)) {
		throw new IndexOpenException("Index is already created and is open");
	    }
	} catch (URISyntaxException use) {
	    throw new IndexSpecificationException("Index badly specified as " + getName());
	}


	// init the objects
	init();


	// register myself in the TimeIndex directory
	TimeIndexDirectory.addHandle(this);

	eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(getURI().toString(), header.getID(), IndexPrimaryEvent.OPENED, this));
	return true;
    }

    /**
     * Create this index.
     */
    public boolean create(Properties properties) throws IndexSpecificationException, IndexCreateException {
	// check the passed in properties
	checkCreateProperties(properties);

	// init the objects
	init();

	setName(indexName);

	// check to see if this index is already open and registered
	try {
	    String uri = generateURI(getName()).toString();

	    if (isOpen(uri)) {
		throw new IndexCreateException("Index is already created and is open");
	    }
	} catch (URISyntaxException use) {
	    throw new IndexSpecificationException("Index badly specified as " + getName());
	}

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

	try {
	    setURI(new URI("index", indexName, null));
	} catch (URISyntaxException use) {
	    ;
	}

	eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(getURI().toString(), header.getID(), IndexPrimaryEvent.CREATED, this));
	closed = false;

	// activate the index
	try {
	    activate();
	} catch (TimeIndexException ioe) {
	    // this can't happen in this kind of index
	    // but this is here, just in case 
	    throw new IndexCreateException(ioe);
	}


	// register myself in the TimeIndex directory
	TimeIndexDirectory.addHandle(this);

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
     * Make the Index activated.
     */
    public Index activate() throws IndexReadOnlyException, IndexWriteLockedException {
	activated = true;
	return this;
    }

    /**
     * Has the Index been write-locked.
     */
    public boolean isWriteLocked() {
	// an Incore Index can NEVER be write-locked
	return false;
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

	// mark as being changed
	changed = true;

	return addItem(item);
    }

   
    /**
     * Add a Referemnce to an IndexItem in a Index.
     */
    public long addReference(IndexItem item, Index other) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
	return addReference(item, other, item.getDataTimestamp());
    }

    /**
     * Add a Referemnce to an IndexItem in a Index.
     */
    public long addReference(IndexItem otherItem, Index otherIndex, Timestamp dataTS) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
	URI indexURI = otherIndex.getURI();
	ID indexID = otherIndex.getID();

	// check if we have a seen this index URI before
	if (! hasIndexURI(indexURI)) {
	    // its a new index being referenced
	    // so keep tabs on it
	    addIndexURI(indexID, indexURI);
	}

        IndexReference reference  = new IndexReferenceDataHolder(otherIndex.getID(), otherItem.getPosition());

	return addReference(reference, dataTS);
    }

    /**
     * Add a Referemnce to an IndexItem in a Index.
     * This version takes the Index URI, the Index ID, the IndexItem's Position,
     * and the IndexItem's data Timestamp.
     * It is used internally when doing a TimeIndexFactory.save().
     */
    public long addReference(IndexReference reference, Timestamp dataTS) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {

        IndexReferenceDataHolder dataHolder = null;

	// Convert the IndexReference into an IndexReferenceDataHolder
	if (reference instanceof IndexReferenceDataHolder) {
	    // The reference is already one
	    dataHolder = (IndexReferenceDataHolder)reference;
	} else {
	    // we need to convert the reference into an IndexReferenceDataHolder
	    ID indexID = reference.getIndexID();
	    Position itemPosition = reference.getIndexItemPosition();

	    dataHolder = new IndexReferenceDataHolder(indexID, itemPosition);
	}

	// set the ID to be the length
	// as it's unique
	long id = getLength();
	// the record Timestamp is now (as microseconds)
	Timestamp recordTS = Clock.time.asMicros();
	// the actual data Timestamp is the record Timestamp
	// if the dataTS param is null, it is the speicifed value otherwise
	Timestamp actualTS = (dataTS == null ? recordTS : dataTS);

	IncoreIndexItem item = new IncoreIndexItem(actualTS, recordTS, dataHolder, DataType.REFERENCE_DT, new SID(id), new SID(0));

	dataHolder.setIndexItem(item);


	long newSize = addItem((IndexItem)item);

	// mark as being changed
	changed = true;

	return newSize;
    }



   /**
     * Close this index.
     */
    public boolean reallyClose() {
	// if the index is activated
	// set the end time in the header
	if (this.isActivated()) {
	    header.setEndTime(Clock.time.asMicros());
	}

	eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(getURI().toString(), header.getID(), IndexPrimaryEvent.CLOSED, this));

	closed = true;
	activated = false;
	changed = false;

	return true;
    }

    /**
     * Check that all the properties needed to open are passed in.
     */
    protected void checkOpenProperties(Properties indexProperties) throws IndexSpecificationException {
	if (indexProperties.containsKey("name")) {
	    indexName = indexProperties.getProperty("name");
	} else {
	    throw new IndexSpecificationException("No 'name' specified for ExternalIndex");
	}

	if (indexProperties.containsKey("readonly")) {
	    String readonly = indexProperties.getProperty("readonly").toLowerCase();

	    if (readonly.equals("true")) {
		readOnly = Boolean.TRUE;
	    } else {
		readOnly = Boolean.FALSE;
	    }
	}
    }


    protected void checkCreateProperties(Properties indexProperties) throws IndexSpecificationException {
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
     * Construct a URI from a name
     */
    public URI generateURI(String name) throws URISyntaxException {
	URI uri = new URI("index", name,  null);

	return uri;
    }

}
