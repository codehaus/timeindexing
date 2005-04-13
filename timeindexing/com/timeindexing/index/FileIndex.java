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
import com.timeindexing.io.FileUtils;
import com.timeindexing.event.*;

import java.util.Properties;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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
     * Commit this index.
     */
    public boolean commit() throws IndexCommitException  {
	// if the index is activated and has changed
	// then flush out any changes
	if (this.isActivated() && isChanged()) {
	    lastFlushTime = Clock.time.time();
	    //lastFlushPosition = indexCache.length();

	    try {
		
		// get the index interactor to flush out the data
		indexInteractor.flush();

		// mark as NOT being changed
		changed = false;

		eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(getURI().toString(), header.getID(), IndexPrimaryEvent.COMMITTED, this));

		return true;
	    } catch (IOException ioe) {
		throw new IndexCommitException("Got IOException message '" + ioe.getMessage() + "' from index " + getURI().toString() + " when attemting to commit");
	    }
	} else {
	    // nothing to do, and notihg flushed
	    return false;
	}
    }

   /**
     * Close this index.
     */
    public boolean reallyClose() throws IndexCloseException  {
	// if the index is activated
	// set the end time in the header
	if (this.isActivated()) {
	    header.setEndTime(Clock.time.time());
	}

	try {
	    // now tell the file interactor to close
	    indexInteractor.close();
	    

	    // mark as NOT being changed
	    changed = false;

	    eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(getURI().toString(), header.getID(), IndexPrimaryEvent.CLOSED, this));

	    closed = true;
	    activated = false;
	    indexInteractor = null;

	    return true;
	} catch (IOException ioe) {
	    throw new IndexCloseException("Got IOException message '" + ioe.getMessage() + "' from index " + getURI().toString() + " when attemting to close");
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
	Timestamp recordTS = Clock.time.time();
	// the actual data Timestamp is the record Timestamp
	// if the dataTS param is null, it is the speicifed value otherwise
	Timestamp actualTS = (dataTS == null ? recordTS : dataTS);
	
	// create a FileIndexItem
	FileIndexItem item = new FileIndexItem(actualTS, recordTS, dataitem, dataitem.getDataType(), new SID(id), new SID(0));


	// writeItem calls addItem(IndexItem)
	long newSize = writeItem(item);

	// mark as being changed
	changed = true;

	// if autoCommit is on, then commit
	if (autoCommitOn) {
	    try {
		commit();
	    } catch (IndexCommitException ice) {
		throw new AddItemException("Can't add this item. " + ice.getMessage());
	    }
	}

	return newSize;
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
	Timestamp recordTS = Clock.time.time();
	// the actual data Timestamp is the record Timestamp
	// if the dataTS param is null, it is the speicifed value otherwise
	Timestamp actualTS = (dataTS == null ? recordTS : dataTS);

	// create a FileIndexItem
	FileIndexItem item = new FileIndexItem(actualTS, recordTS, dataHolder, DataType.REFERENCE_DT, new SID(id), new SID(0));

	dataHolder.setIndexItem(item);

	long newSize = writeItem(item);

	// mark as being changed
	changed = true;

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
	    //System.err.println("FileIndex: " + getName() + " load-on-demand item: " + n);
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
	eventMulticaster.fireAccessEvent(new IndexAccessEvent(getURI().toString(), header.getID(), item, this));

	return item;
    }

    protected long writeItem(FileIndexItem item) throws IndexTerminatedException, IndexClosedException, IndexActivationException, AddItemException {
	// add the item to the cache
	long newSize = super.addItem(item);

	// and write it out to the file
	try {
	    // do a write now
	    indexInteractor.addItem(item);


	    if (newSize == 1) { // this is the first index item
		header.setFirstOffset(item.getIndexOffset()); // so set the offset of the first item
	    }

	    // set the offset of the last item
	    header.setLastOffset(item.getIndexOffset());

	    // this is the last item flushed to the file
	    lastFlushPosition = new AbsolutePosition(newSize);

	    //System.err.print(newSize + "\t" + header.getLastOffset() + "\r");

	    return newSize;

	} catch (IOException ioe) {
	    throw new AddItemException(ioe);
	}
    }

    /**
     * Retrieve an Index Item into the Index.
     * @param item the IndexItem to add
     * @param position the position to load the IndexItem at
     * @return the no of items in the cache
     */
    public long retrieveItem(IndexItem item, long position) {
	// now set the item's position and
	// bind it to the index
	ManagedIndexItem itemM = (ManagedIndexItem)item;
	itemM.setPosition(new AbsolutePosition(position));
	itemM.setIndex(this);

	// add the item to the index item cache
	// the cache will return the size of the index
	long cacheSize = indexCache.addItem(item, position);


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

    /**
     * Make the Index activated.
     */
    public Index activate() throws IndexReadOnlyException, IndexWriteLockedException {
	// can't activate an index that is read-only
	if (isReadOnly()) {
	    throw new IndexReadOnlyException("Can't activate index. It is READ ONLY");
	}

	// things are looking good
	// so do the activation process

	// get a lock
	FileLock lock = indexInteractor.getWriteLock();

	if (lock == null) { 
	    // didn't get a lock, so it must be locked by someone else
	    throw new IndexWriteLockedException("FileIndex: Can't activate index " + getURI() + ". It is WRITE LOCKED");
	} else {
	    // we got the lock
	    activated = true;
	    return this;
	}
    }

    /**
     * Has the Index been write-locked.
     */
    public boolean isWriteLocked() {
	return indexInteractor.isWriteLocked();
    }

    /**
     * Construct a URI from a pathname.
     */
    public URI generateURI(String pathname) throws URISyntaxException {
	URI uri = new URI("index", "", FileUtils.removeExtension(pathname), null);

	return uri;
    }
}
