// InlineIndex.java

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
import com.timeindexing.io.HeaderFileInteractor;
import com.timeindexing.io.IndexHeaderIO;
import com.timeindexing.io.InlineIndexInteractor;
import com.timeindexing.io.InlineIndexIO;
import com.timeindexing.event.*;

import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * An implementation of an inline Index object.
 * It represents the index header, the index stream and the data stream.
 */
public class InlineIndex extends AbstractIndex implements ManagedIndex  {
    // The file name
    String fileName = null;
    // The object that interacts with the header file
    HeaderFileInteractor  headerInteractor = null;
    // The object that interacts with the inline index file
    InlineIndexInteractor indexInteractor = null;
    // The last position that has been flushed to an index
    Position lastFlushPosition = null;
    // The last time that the idnex was flushed.
    Timestamp lastFlushTime = null;
    // The style to load the index
    LoadStyle loadStyle = LoadStyle.HOLLOW;
    
    /**
     * Create an InlineIndex
     */
    public InlineIndex(Properties indexProperties) {
	if (indexProperties.containsKey("name")) {
	    indexName = indexProperties.getProperty("name");
	} else {
	    throw new Error("No 'name' specified for InlineIndex");
	}

	if (indexProperties.containsKey("filename")) {
	    fileName = indexProperties.getProperty("filename");
	} else {
	    throw new Error("No 'filename' specified for InlineIndex");
	}

	if (indexProperties.containsKey("loadstyle")) {
	    String loadstyle = indexProperties.getProperty("loadstyle").toLowerCase();

	    if (loadstyle.equals("all")) {
		loadStyle = LoadStyle.ALL;
	    } else if (loadstyle.equals("hollow")) {
		loadStyle = LoadStyle.HOLLOW;
	    } else if (loadstyle.equals("none")) {
		loadStyle = LoadStyle.NONE;
	    } else {
		loadStyle = LoadStyle.HOLLOW;
	    }
	}

	init();
    }

    /**
     * Create an InlineIndex
     */
    public InlineIndex(String name, String filename) {
	indexName = name;
	fileName = filename;

	init();
    }

    /**
     * Initialize the object.
     */
    protected void init() {
	header = new IncoreIndexHeader(this, indexName);
	indexCache = new FileIndexCache(this);

	headerInteractor = new IndexHeaderIO(this);
	indexInteractor = new InlineIndexIO(this);

	// eventMulticaster.addPrimaryEventListener((IndexPrimaryEventListener)headerInteractor);
	// eventMulticaster.addAddEventListener((IndexAddEventListener)headerInteractor);
	// eventMulticaster.addPrimaryEventListener((IndexPrimaryEventListener)indexInteractor);
	// eventMulticaster.addAddEventListener((IndexAddEventListener)indexInteractor);

    }

    /**
     * Called when an InlineIndex needs to be opend.
     */
    public boolean open() {
	try {
	    headerInteractor.open(getFileName());
	    indexInteractor.open(getFileName());

	    // synchronize the header read using the headerInteractor
	    // with the header object
	    header.syncHeader((ExtendedIndexHeader)headerInteractor);


	    //System.err.print(headerInteractor);

	    // go to point just after header
	    indexInteractor.seek(header.getFirstOffset().value());
	    // load the index
	    long appendPosition = indexInteractor.loadIndex(loadStyle);
	    // go to point just after last  item
	    indexInteractor.seek(appendPosition);

	    //System.err.println(" append position = " + appendPosition);

	    eventMulticaster.firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.OPENED, this));

	    return true;
	} catch (IOException ioe) {
	    return false;
	}
    }

    /**
     * Called when an InlineIndex needs to be created.
     */
    public boolean create() {
	// things to do the first time in
	// set the ID, the startTime, and the index type
	ID indexID = new UID();
	header.setID(indexID);

	header.setStartTime(Clock.time.asMicros());
	header.setFirstOffset(new Offset(0));
	header.setLastOffset(new Offset(0));

	indexType = IndexType.INLINE;

	try {
	    headerInteractor.create(getFileName());

	    // String indexFilename = headerInteractor.getIndexFileName());
 
	    indexInteractor.create(getFileName());
	

	    // activate the index
	    activate();

	    // pass an event to the listeners
	    eventMulticaster.firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.CREATED, this));

	    return true;
	} catch (IOException ioe) {
	    return false;
	}
	    
    }


    /**
     * Flush this index.
     */
    public boolean flush() {
	lastFlushTime = Clock.time.asMicros();
	//lastFlushPosition = indexCache.length();

	eventMulticaster.firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.FLUSHED, this));

	return true;
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

	try {
	    // now tell the file interactors to close
	    headerInteractor.close();
	    indexInteractor.close();
	    

	    eventMulticaster.firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.CLOSED, this));

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
	
	// create a FileIndexItem
	FileIndexItem item = new FileIndexItem(actualTS, recordTS, dataitem, dataitem.getDataType(), new SID(id), new SID(0));

	// add the item to the cache
	long newSize = super.addItem(item);

	// and write it out to the file
	try {
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
	    throw new Error("IO Exception writing item");
	}

	return newSize;
    }

    /**
     * Retrieve an Index Item into the Index.
     * @param item the IndexItem to add
     * @return the no of items in the index.
     */
    public long retrieveItem(IndexItem item) {
	// add the item to the index item cache
	// the cache will return its load position
	long loadPos = indexCache.addItem(item);

	// now set the item's position and
	// bind it to the index
	ManagedIndexItem itemM = (ManagedIndexItem)item;
	itemM.setPosition(new AbsolutePosition(loadPos - 1));
	itemM.setIndex(this);

	return loadPos;
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
	return indexCache.hollowItem(p);
    }

   /**
     * Get the filename of the index.
     */
    public String getFileName() {
	return fileName;
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
