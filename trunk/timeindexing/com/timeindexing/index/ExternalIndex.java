// ExternalIndex.java

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
import com.timeindexing.io.ExternalIndexIO;
import com.timeindexing.event.*;

import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * An implementation of an external Index object.
 * It represents the index header, the index stream and the data stream.
 */
public class ExternalIndex extends FileIndex implements ManagedIndex  {
    // The file name of the data file
    String dataPathName = null;
    
    /**
     * Create an ExternalIndex
     */
    public ExternalIndex() throws IndexSpecificationException {
    }

    /**
     * Initialize the object.
     */
    protected void init() {
	header = new IncoreIndexHeader(this, indexName);
	indexCache = new FileIndexCache(this);

	indexCache.setPolicy(new HollowAfterTimeoutPolicy()); //new HollowAtDataVolumeRemoveAfterTimeoutPolicy());

	setIndexType(IndexType.EXTERNAL_DT);

	indexInteractor = new ExternalIndexIO(this);

    }

    /**
     * Called when an ExternalIndex needs to be opend.
     */
    public synchronized boolean open(Properties properties) throws IndexSpecificationException, IndexOpenException {
	// check the passed in properties
	checkOpenProperties(properties);

	// check to see if this index is already open and registered
	try {
	    String uri = generateURI(headerPathName).toString();

	    if (isOpen(uri)) {
		throw new IndexOpenException("Index is already created and is open");
	    }
	} catch (URISyntaxException use) {
	    throw new IndexSpecificationException("Index badly specified as " + headerPathName);
	}

	// init the objects
	init();

	try {
	    IndexProperties indexProperties = new IndexProperties();

	    indexProperties.put("indexpath", headerPathName);
	    indexProperties.put("readonly", readOnly);
	    
	    // open the index and the data
	    indexInteractor.open(indexProperties);

	    // load the index
	    indexInteractor.loadIndex(loadStyle);

	    eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(getURI().toString(), header.getID(), IndexPrimaryEvent.OPENED, this));

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
     * Called when an ExternalIndex needs to be created.
     */
    public synchronized boolean create(Properties properties) throws IndexSpecificationException, IndexCreateException {
	String uri = null;

	// check the passed in properties
	checkCreateProperties(properties);

	// init the objects
	init();

	setName(indexName);

	// check to see if this index is already open and registered
	try {
	    uri = generateURI(headerPathName).toString();

	    if (isOpen(uri)) {
		throw new IndexCreateException("Index is already created and is open");
	    }
	} catch (URISyntaxException use) {
	    throw new IndexSpecificationException("Index badly specified as " + headerPathName);
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

	    IndexProperties indexProperties = new IndexProperties();
	    indexProperties.put("name", indexName);
	    indexProperties.put("indexid", indexID);
	    indexProperties.put("indexpath", headerPathName);
	    indexProperties.put("datapath", dataPathName);

	    // create the relevant objects
	    indexInteractor.create(indexProperties);	
	
	    // activate the index
	    activate();

	    // flush out the current settings
	    indexInteractor.flush();

	    // pass an event to the listeners
	    eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(getURI().toString(), header.getID(), IndexPrimaryEvent.CREATED, this));

	    // now we're open
	    closed = false;


	    // register myself in the TimeIndex directory
	    TimeIndexDirectory.addHandle(this);

	    return true;
	} catch (IOException ioe) {
	    throw new IndexCreateException(ioe);
	} catch (TimeIndexException ioe) {
	    throw new IndexCreateException(ioe);
	}	    
    }

    /**
     * Check that all the properties needed to open are passed in.
     */
    protected void checkOpenProperties(Properties indexProperties) throws IndexSpecificationException {
	if (indexProperties.containsKey("indexpath")) {
	    headerPathName = indexProperties.getProperty("indexpath");
	} else {
	    throw new IndexSpecificationException("No 'indexpath' specified for ExternalIndex");
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
		loadStyle = LoadStyle.NONE;
	    }
	} else {
	    loadStyle = LoadStyle.NONE;
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


    /**
     * Check that all the properties needed to create are passed in.
     */
    protected void checkCreateProperties(Properties indexProperties) throws IndexSpecificationException {
	if (indexProperties.containsKey("name")) {
	    indexName = indexProperties.getProperty("name");
	} else {
	    throw new IndexSpecificationException("No 'name' specified for ExternalIndex");
	}

	if (indexProperties.containsKey("indexpath")) {
	    headerPathName = indexProperties.getProperty("indexpath");
	} else {
	    throw new IndexSpecificationException("No 'indexpath' specified for ExternalIndex");
	}


	if (indexProperties.containsKey("datapath")) {
	    dataPathName = indexProperties.getProperty("datapath");
	} else {
	    // cant; find a datapath property
	    // assume they are the same
	    dataPathName = headerPathName;
	}

	if (indexProperties.containsKey("datatype")) {
	    dataType = DataTypeDirectory.find(indexProperties.getProperty("dataType"));
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
		loadStyle = LoadStyle.NONE;
	    }
	} else {
	    loadStyle = LoadStyle.NONE;
	}
    }


}
