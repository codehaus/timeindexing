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
import com.timeindexing.cache.*;
import com.timeindexing.io.LoadStyle;
import com.timeindexing.io.IndexHeaderIO;
import com.timeindexing.io.IndexFileInteractor;
import com.timeindexing.io.InlineIndexIO;
import com.timeindexing.event.*;

import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * An implementation of an inline Index object.
 * It represents the index header, the index stream and the data stream.
 */
public class InlineIndex extends FileIndex implements ManagedIndex  {
    /**
     * Create an InlineIndex
     */
    public InlineIndex() {
    }

    /**
     * Create an InlineIndex
     */
    public InlineIndex(String name, String indexPath) {
	indexName = name;
	headerPathName = indexPath;
    }

    /**
     * Initialize the object.
     */
    protected void init() {
	header = new IncoreIndexHeader(this, indexName);
	indexCache = new FileIndexCache(this);

	indexCache.setPolicy(new HollowAfterTimeoutPolicy());

	setIndexType(IndexType.INLINE_DT);

	indexInteractor = new InlineIndexIO(this);
    }

    /**
     * Called when an InlineIndex needs to be opend.
     */
    public synchronized boolean open(Properties properties) throws IndexSpecificationException, IndexOpenException {
	// check the passed in properties
	checkProperties(properties);

	// check to see if this index is already open and registered
	if (isOpen(headerPathName)) {
	    throw new IndexOpenException("Index is already created and is open");
	}

	// init the objects
	init();

	try {
	    IndexProperties indexProperties = new IndexProperties();

	    indexProperties.put("indexpath", headerPathName);

	    // open the index 
	    indexInteractor.open(indexProperties);

	    // load the index
	    indexInteractor.loadIndex(loadStyle);


	    eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.OPENED, this));
	    
	    // now we're open
	    closed = false;


	    // register myself in the TimeIndex directory
	    TimeIndexDirectory.addHandle(this);

	    return true;
	} catch (IOException ioe) {
	    throw new IndexOpenException(ioe);
	}
    }

    /**
     * Called when an InlineIndex needs to be created.
     */
    public synchronized boolean create(Properties properties) throws IndexSpecificationException, IndexCreateException {
	// check the passed in properties
	checkProperties(properties);

	// check to see if this index is already open and registered
	if (isOpen(headerPathName)) {
	    throw new IndexCreateException("Index is already created and is open");
	}

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

	try {
	    IndexProperties indexProperties = new IndexProperties();
	    indexProperties.put("name", indexName);
	    indexProperties.put("indexid", indexID);
	    indexProperties.put("indexpath", headerPathName);

	    indexInteractor.create(indexProperties);
	

	    // activate the index
	    activate();

	    // pass an event to the listeners
	    eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.CREATED, this));

	    // now we're open
	    closed = false;


	    // register myself in the TimeIndex directory
	    TimeIndexDirectory.addHandle(this);

	    return true;
	} catch (IOException ioe) {
	    throw new IndexCreateException(ioe);
	}
	    
    }

    protected void checkProperties(Properties indexProperties) throws IndexSpecificationException {
	if (indexProperties.containsKey("name")) {
	    indexName = indexProperties.getProperty("name");
	} else {
	    throw new IndexSpecificationException("No 'name' specified for InlineIndex");
	}

	if (indexProperties.containsKey("indexpath")) {
	    headerPathName = indexProperties.getProperty("indexpath");
	} else {
	    throw new IndexSpecificationException("No 'indexpath' specified for InlineIndex");
	}

	if (indexProperties.containsKey("datatype")) {
	    dataType = DataTypeDirectory.find(indexProperties.getProperty("datatype"));
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
	} else {
	    loadStyle = LoadStyle.HOLLOW;
	}

    }

}
