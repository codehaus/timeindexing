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
import com.timeindexing.io.LoadStyle;
import com.timeindexing.io.HeaderFileInteractor;
import com.timeindexing.io.IndexHeaderIO;
import com.timeindexing.io.IndexFileInteractor;
import com.timeindexing.io.ExternalIndexIO;
import com.timeindexing.event.*;

import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.IOException;

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
    public ExternalIndex(Properties indexProperties) throws IndexSpecificationException {
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
		loadStyle = LoadStyle.HOLLOW;
	    }
	}

	init();
    }

    /**
     * Initialize the object.
     */
    protected void init() {
	header = new IncoreIndexHeader(this, indexName);
	indexCache = new FileIndexCache(this);

	headerInteractor = new IndexHeaderIO(this);
	indexInteractor = new ExternalIndexIO(this);

	header.setIndexType(IndexType.EXTERNAL_DT);
    }

    /**
     * Called when an ExternalIndex needs to be opend.
     */
    public boolean open() throws IndexOpenException {
	try {
	    // open the index header
	    headerInteractor.open(headerPathName);

	    IndexProperties indexProperties = new IndexProperties();

	    // add the header to the properties
	    indexProperties.put("header", headerInteractor);

	    indexProperties.put("headerpath", headerPathName);

	    // add the indexpath to the properties
	    indexProperties.put("indexpath", (String)((ManagedIndexHeader)headerInteractor).getOption(HeaderOption.INDEXPATH_HO));

	    // add the datapath to the properties
	    // as it was in the header
	    indexProperties.put("datapath", (String)((ManagedIndexHeader)headerInteractor).getOption(HeaderOption.DATAPATH_HO));
	    // WAS indexProperties.put("datapath", dataPathName);
	    
	    
	    // open the index and the data
	    indexInteractor.open(indexProperties);

	    // synchronize the header read using the headerInteractor
	    // with the header object
	    header.syncHeader((ManagedIndexHeader)headerInteractor);


	    //System.err.print(headerInteractor);

	    // go to point just after header
	    indexInteractor.gotoFirstPosition();
	    // load the index
	    indexInteractor.loadIndex(loadStyle);
	    // go to point just after last  item
	    indexInteractor.gotoAppendPosition();

	    //System.err.println(" append position = " + appendPosition);

	    eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.OPENED, this));

	    // now we're open
	    closed = false;

	    return true;
	} catch (IOException ioe) {
	    throw new IndexOpenException(ioe);
	}
    }

    /**
     * Called when an ExternalIndex needs to be created.
     */
    public boolean create() throws IndexCreateException {
	// things to do the first time in
	// set the ID, the startTime, and the index type
	ID indexID = new UID();
	header.setID(indexID);
	//header.setIndexType(indexType);
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

	    headerInteractor.create(headerPathName);

	    // String indexFilename = headerInteractor.getIndexFileName());
 
	    indexInteractor.create(indexProperties);
	

	    // activate the index
	    activate();

	    // pass an event to the listeners
	    eventMulticaster().firePrimaryEvent(new IndexPrimaryEvent(indexName, header.getID(), IndexPrimaryEvent.CREATED, this));

	    // now we're open
	    closed = false;

	    return true;
	} catch (IOException ioe) {
	    throw new IndexCreateException(ioe);
	}
	    
    }

    /**
     * Get the headerfor the index.
     */
    public ManagedIndexHeader getHeader() {
	return header;
    }


}
