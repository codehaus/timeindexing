// TimeIndexFactory.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.io.IndexDecoder;

import java.util.Properties;
import java.io.File;
import java.io.IOException;
import com.timeindexing.event.*;

/**
 * This is the TimeIndexFactory which returns different implementations
 * of a TimeIndex, depending on the arguments to create() or retrieve().
 * <p>
 * The factory listens to events from Indexes, and will pass them
 * on to any of its listeners.
 */
public class TimeIndexFactory implements IndexPrimaryEventListener, IndexAddEventListener, IndexAccessEventListener, IndexEventGenerator {
    // an event multicaster
    IndexEventMulticaster eventMulticaster = null;

    /**
     * Create a TimeIndexFactory.
     */
    public TimeIndexFactory() {
	eventMulticaster = new IndexEventMulticaster();
    }

    /**
     * Get a new Time Index object given a constant, as defined in TimeType.
     * @param kind One of IndexType.INLINE, IndexType.EXTERNAL, IndexType.INCORE,
     * IndexType.JAVASERIAL.
     * @param indexProperties properties of the index needed at creat time, such as  its name.
     */
    public IndexView create(int kind, Properties indexProperties) throws TimeIndexFactoryException, IndexSpecificationException, IndexCreateException {

	switch (kind) {
	case IndexType.INLINE: {
	    ManagedIndex newIndex = new InlineIndex(indexProperties); 

	    newIndex.addPrimaryEventListener(this);

	    newIndex.create();

	    // get the ID and name values
	    ID indexID = newIndex.getID();
	    String indexName = newIndex.getName();
	    
	    // save these in the TimeIndex directories
	    TimeIndexDirectory.register(newIndex, indexName, indexID);

	    return new TimeIndex(new TimeIndexInteractor(newIndex));
	}

	case IndexType.EXTERNAL: {
	    ManagedIndex newIndex = new ExternalIndex(indexProperties); 

	    newIndex.addPrimaryEventListener(this);

	    newIndex.create();

	    // get the ID and name values
	    ID indexID = newIndex.getID();
	    String indexName = newIndex.getName();
	    
	    // save these in the TimeIndex directories
	    TimeIndexDirectory.register(newIndex, indexName, indexID);

	    return new TimeIndex(new TimeIndexInteractor(newIndex));

	}

	case IndexType.SHADOW: {
	    ManagedIndex newIndex = new ShadowIndex(indexProperties); 

	    newIndex.addPrimaryEventListener(this);

	    newIndex.create();

	    // get the ID and name values
	    ID indexID = newIndex.getID();
	    String indexName = newIndex.getName();
	    
	    // save these in the TimeIndex directories
	    TimeIndexDirectory.register(newIndex, indexName, indexID);

	    return new TimeIndex(new TimeIndexInteractor(newIndex));

	}

	case IndexType.INCORE: {
	    ManagedIndex newIndex = new IncoreIndex(indexProperties);

	    newIndex.addPrimaryEventListener(this);

	    newIndex.create();

	    // get the ID and name values
	    ID indexID = newIndex.getID();
	    String indexName = newIndex.getName();
	    
	    // save these in the TimeIndex directories
	    TimeIndexDirectory.register(newIndex, indexName, indexID);

	    return new TimeIndex(new TimeIndexInteractor(newIndex));
	}

	default:
	    throw new TimeIndexFactoryException("TimeIndexFactory: Illegal value for kind: " + kind);
	}
	    
    }

    /**
     * Create a new Index object for an existing Index object.
     * The type of the new Index is based on a constant, as defined in TimeType.
     * @param kind One of IndexType.INLINE, IndexType.EXTERNAL, IndexType.INCORE,
     * IndexType.JAVASERIAL.
     * @param indexProperties properties of the index needed at creat time, such as  its name.
     */
    public IndexView convert(Index index, int kind, Properties indexProperties) throws TimeIndexFactoryException { // IndexSpecificationException, IndexCreateException {
	// TODO: implement me
	throw new TimeIndexFactoryException("Not implemented yet");
    }

    /**
     * Retrieve a TimeIndex object by file name.
     * e.g. TimeIndexFactory.open(new File(someDirectory, fileName))
     *
     * @param indexFile  the file  of the index
     */
    public IndexView open(File indexFile) throws TimeIndexFactoryException, IndexSpecificationException, IndexOpenException {
	Properties indexProperties = new Properties();
	indexProperties.setProperty("indexpath", indexFile.getPath());
	
	return open(indexProperties);
    }

    /**
     * Retrieve a TimeIndex object by file name.
     * e.g. TimeIndexFactory.open(properties)
     *
     * @param indexFilename  the filename of the index
     * @return null if the index can't be opened
     */
    public IndexView open(Properties indexProperties)  throws TimeIndexFactoryException,  IndexSpecificationException, IndexOpenException {
	// an IndexDecoder
	IndexDecoder decoder = null;
	// decode the named file
	String fileName = null;
	File indexFile = null;
	String indexPath = null;

	try {
	    if (indexProperties.containsKey("indexpath")) {
		fileName = indexProperties.getProperty("indexpath");
		indexFile = new File(fileName);
		indexPath = indexFile.getCanonicalPath();
	    } else {
		throw new IndexSpecificationException("No 'indexpath' specified for TimeIndexFactory");
	    }

	    // create an index decoder
	    decoder = new IndexDecoder(indexFile);

	    // read the header
	    decoder.read();
	} catch (IOException ioe) {
	    // there was an I/O error opening or reading the header so 
	    // thorw an exception
	    throw new IndexOpenException(ioe);
	} finally {
	    try {
		// close it
		if (decoder != null) {
		    decoder.close();
		}
	    } catch (IOException ioeClose) {
		// there was an I/O error on closing
		// things are very bad
		throw new IndexOpenException("Can't close " + fileName + " when attempting to decode the index header");
	    }
	}	    

	    
	// now open the index properly

	String indexName = decoder.getName();
	ID indexID = decoder.getID();
	int kind = decoder.getIndexType();

	//Properties indexProperties = new Properties();
	indexProperties.setProperty("name", indexName);
	indexProperties.setProperty("indexpath", indexPath);

	switch (kind) {
	case IndexType.INLINE: {
	    ManagedIndex newIndex = new InlineIndex(indexProperties); 

	    newIndex.addPrimaryEventListener(this);

	    newIndex.open();

	    // save these in the TimeIndex directories
	    TimeIndexDirectory.register(newIndex, indexName, indexID);

	    return new TimeIndex(new TimeIndexInteractor(newIndex));
	}

	case IndexType.EXTERNAL: {
	    ManagedIndex newIndex = new ExternalIndex(indexProperties); 

	    newIndex.addPrimaryEventListener(this);

	    newIndex.open();

	    // save these in the TimeIndex directories
	    TimeIndexDirectory.register(newIndex, indexName, indexID);

	    return new TimeIndex(new TimeIndexInteractor(newIndex));
	}

	case IndexType.SHADOW: {
	    ManagedIndex newIndex = new ShadowIndex(indexProperties); 

	    newIndex.addPrimaryEventListener(this);

	    newIndex.open();

	    // save these in the TimeIndex directories
	    TimeIndexDirectory.register(newIndex, indexName, indexID);

	    return new TimeIndex(new TimeIndexInteractor(newIndex));
	}

	case IndexType.INCORE: {
	    ManagedIndex newIndex = new IncoreIndex(indexProperties);

	    newIndex.addPrimaryEventListener(this);

	    newIndex.open();

	    // save these in the TimeIndex directories
	    TimeIndexDirectory.register(newIndex, indexName, indexID);

	    return new TimeIndex(new TimeIndexInteractor(newIndex));
	}


	default:
	    throw new TimeIndexFactoryException("TimeIndexFactory: Illegal value for kind: " + kind);
	}

    }


    /**
     * Append to an index.
     * e.g. TimeIndexFactory.append(new File(someDirectory, fileName))
     *
     * @param indexFile  the file  of the index
     */
    public IndexView append(File indexFile) throws TimeIndexFactoryException, IndexSpecificationException, IndexOpenException {
	Properties indexProperties = new Properties();
	indexProperties.setProperty("indexpath", indexFile.getPath());
	
	return append(indexProperties);
    }

    /**
     * Append to an index.
     * e.g. TimeIndexFactory.appemd(properties)
     *
     * @param indexFilename  the filename of the index
     */
    public IndexView append(Properties indexProperties)  throws TimeIndexFactoryException, IndexSpecificationException, IndexOpenException {
	indexProperties.setProperty("loadstyle", "none");
	return open(indexProperties);
    }


    /**
     * Close an index
     */
    public boolean close(Index index) {
	// flush out the contents
	boolean flushed = index.flush();

	// TODO: what should we do if flushed == false

	// close all the objects in the index
	boolean closed = index.close();

	// tell the directory to remove the index 
	TimeIndexDirectory.unregister(index);

	return closed;
    }

    /*
     * Methods for the event generation.
     */

    /**
     * Get the event listener.
     */
    public IndexEventMulticaster eventMulticaster() {
	return eventMulticaster;
    }

    /**
     * Add a IndexPrimaryEventListener.
     */
    public void addPrimaryEventListener(IndexPrimaryEventListener l) {
	eventMulticaster.addPrimaryEventListener(l);
    }

    /**
     * Remove a IndexPrimaryEventListener.
     */
     public void removePrimaryEventListener(IndexPrimaryEventListener l) {
	 eventMulticaster.removePrimaryEventListener(l);
     }

    /**
     * Add a IndexAddEventListener.
     */
    public void addAddEventListener(IndexAddEventListener l) {
	eventMulticaster.addAddEventListener(l);
    }

    /**
     * Remove a IndexAddEventListener.
     */
    public void removeAddEventListener(IndexAddEventListener l) {
	eventMulticaster.removeAddEventListener(l);
    }

    /**
     * Add a IndexAccessEventListener.
     */
    public void addAccessEventListener(IndexAccessEventListener l) {
	eventMulticaster.addAccessEventListener(l);
    }

    /**
     * Remove a IndexAccessEventListener.
     */
    public void removeAccessEventListener(IndexAccessEventListener l) {
	eventMulticaster.removeAccessEventListener(l);
    }

    /*
     * Methods for listening to events from indexes.
     */

    /**
     * A notification that an Index has been created.
     */
    public  void created(IndexPrimaryEvent ipe) {
	//System.err.println("Create event from " + ipe.getName() + ". Class = " + ipe.getSource().getClass().getName());
	eventMulticaster.firePrimaryEvent(ipe);
    }

    /**
     * A notification that an Index has been opened.
     */
    public  void opened(IndexPrimaryEvent ipe) {
	//System.err.println("Open event from " + ipe.getName() + ". Class = " + ipe.getSource().getClass().getName());
	eventMulticaster.firePrimaryEvent(ipe);
    }

    /**
     * A notification that an Index has been closed.
     */
    public  void closed(IndexPrimaryEvent ipe) {
	//System.err.println("Closed event from " + ipe.getName() + ". Class = " + ipe.getSource().getClass().getName());
	eventMulticaster.firePrimaryEvent(ipe);
    }

    /**
     * A notification that an Index has been flushed.
     */
    public  void flushed(IndexPrimaryEvent ipe) {
	//System.err.println("Flush event from " + ipe.getName() + ". Class = " + ipe.getSource().getClass().getName());
	eventMulticaster.firePrimaryEvent(ipe);
    }

	
    /**
     * A notification that an IndexItem has been added to an Index.
     */
    public void itemAdded(IndexAddEvent iae) {
	eventMulticaster.fireAddEvent(iae);
    }

    /**
     * A notification that an IndexItem has been accessed in an Index.
     */
    public void itemAccessed(IndexAccessEvent iae) {
	eventMulticaster.fireAccessEvent(iae);
    }
}