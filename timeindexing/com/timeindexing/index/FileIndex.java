// FileIndex.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Clock;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.Offset;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.data.DataItem;
import com.timeindexing.io.LoadStyle;
import com.timeindexing.io.IndexFileInteractor;
import com.timeindexing.event.*;

import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * A place holder abstract class for stored Index objects
 * that are stored in files.
 */
public abstract class FileIndex extends AbstractManagedIndex implements StoredIndex  {
    // The object that interacts with the inline index file
    IndexFileInteractor indexInteractor = null;
    // The last position that has been flushed to an index
    Position lastFlushPosition = null;
    // The last time that the idnex was flushed.
    Timestamp lastFlushTime = null;
    // The style to load the index
    LoadStyle loadStyle = LoadStyle.NONE;
    // The path name of the header
    String headerPathName = null;


    /**
     * Flush this index.
     */
    public boolean flush() {
	// if the index is activated
	// then flush out any changes
	if (this.isActivated()) {
	    lastFlushTime = Clock.time.asMicros();
	    //lastFlushPosition = indexCache.length();

	    try {
		
		indexInteractor.flush();

		eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.FLUSHED, this));

		return true;
	    } catch (IOException ioe) {
		return false;
	    }
	} else {
	    // nothing to do, and notihg flushed
	    return false;
	}
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

	try {
	    // now tell the file interactor to close
	    indexInteractor.close();
	    

	    eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.CLOSED, this));

	    closed = true;
	    activated = false;

	    return true;
	} catch (IOException ioe) {
	    return false;
	}

    }


   /**
     * Add a Data Item to the Index.
     * The data timestamp will be the same as the record timestamp.
     * The ID will be generated.
     * There are no annotations.
     * @param item the DataItem to add
     * @return the no of items in the index.
     */
    public synchronized long addItem(DataItem dataitem) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
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
	
	// create a FileIndexItem
	FileIndexItem item = new FileIndexItem(actualTS, recordTS, dataitem, dataitem.getDataType(), new SID(id), new SID(0));

	// add the item to the cache
	long newSize = super.addItem(item);

	// and write it out to the file
	try {
	    // do a write now
	    // TODO: have non immediate write version
	    // and get flush() to do the work
	    indexInteractor.writeItem(item);


	    if (newSize == 1) { // this is the first index item
		header.setFirstOffset(item.getIndexOffset()); // so set the offset of the first item
	    }

	    // set the offset of the last item
	    header.setLastOffset(item.getIndexOffset());

	    // this is the last item flushed to the file
	    lastFlushPosition = new AbsolutePosition(newSize);

	    //System.err.print(newSize + "\t" + header.getLastOffset() + "\r");

	} catch (IOException ioe) {
	    throw new AddItemException(ioe);
	}

	return newSize;
    }

    /**
     * Get an Index Item from the Index.
     */
    public IndexItem getItem(long n) throws GetItemException {
	setLastAccessTime();

	IndexItem item = null;

	if (indexCache.containsItem(n)) { 	// if the cache has the item
	    // get it from the cache
	    item = indexCache.getItem(n);
	} else {
	    //System.err.println("FileIndex: load-on-demand item: " + n);
	    try {
		// get the item from the index interactor
		indexInteractor.getItem(n, false);
		// get it out of the cache
		item = indexCache.getItem(n);
	    } catch (IOException ioe) {
		throw new GetItemException("Cant load item " + n);
	    }
	}

	// tell all the listeners that an item has been accessed
	eventMulticaster.fireAccessEvent(new IndexAccessEvent(indexName, header.getID(), item, this));

	return item;
    }

    /**
     * Retrieve an Index Item into the Index.
     * @param item the IndexItem to add
     * @param position the position to load the IndexItem at
     * @return the no of items in the cache
     */
    public long retrieveItem(IndexItem item, long position) {
	// add the item to the index item cache
	// the cache will return the size of the index
	long cacheSize = indexCache.addItem(item, position);

	// now set the item's position and
	// bind it to the index
	ManagedIndexItem itemM = (ManagedIndexItem)item;
	itemM.setPosition(new AbsolutePosition(position));
	itemM.setIndex(this);

	return cacheSize;
    }


    /**
     * Read data for an index item
     * given a DataReference.
     */
    public DataHolderObject readData(DataReference dataReference) {
	//System.err.println("Reading reference: " + dataReference);
	return indexInteractor.convertDataReference(dataReference);
    }
     
    /**
     * Hollow out the IndexItem at the speicifed position.
     */
    public boolean hollowItem(long pos) {
	return indexCache.hollowItem(pos);
    }
 
    /**
     * Hollow the IndexItem at the position.
     */
    public boolean hollowItem(Position p) {
        if (p == Position.TOO_LOW) {
	    throw new PositionOutOfBoundsException("Position TOO_LOW");
	} else if (p == Position.TOO_HIGH) {
	    throw  new PositionOutOfBoundsException("Position TOO_HIGH");
	} else {
	    return indexCache.hollowItem(p);
	}
    }

   /**
     * Get the path of the index header.
     */
    public String getHeaderPathName() {
	return headerPathName;
    }

    /**
     * Get the  last time the index was flushed.
     */
    public Timestamp getLastFlushTime() {
	return lastFlushTime;
    }

    /**
     * Get the IndexItem Position when the index was last flushed.
     */
    public Position getLastFlushPosition() {
	return lastFlushPosition;
    }
    
    /**
     * Get the Offset of the fisrt item.
     */
    public Offset getFirstOffset() {
	return header.getFirstOffset();
    }

    /**
     * Get the Offset of the last item.
     */
    public Offset getLastOffset() {
	return header.getLastOffset();
    }

}
