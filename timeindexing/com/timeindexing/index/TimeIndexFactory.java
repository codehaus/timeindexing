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
    public IndexView create(IndexType kind, Properties indexProperties) throws TimeIndexFactoryException, IndexSpecificationException, IndexCreateException {

	ManagedIndex anIndex = null;
	String fileName = null;
	File indexFile = null;
	String indexPath = null;

	// check indexpath for non incore indexes
	if (kind.value() != IndexType.INCORE) {
	    
	    try {
		if (indexProperties.containsKey("indexpath")) {
		    fileName = indexProperties.getProperty("indexpath");
		    indexFile = new File(fileName);
		    indexPath = indexFile.getCanonicalPath();
		    //indexPath = indexFile.toURI().toString();

		    // patch up indexpath to canonical form
		    indexProperties.setProperty("indexpath", indexPath);

		    
		    // we don;t want ot do a create() followed by a create()
		    // so we check to see if it exists
		    IndexDecoder decoder = null;
		    try {
			// this will succeed if the index exists
			decoder = openDecoder(indexProperties);

			// get some values out of the header

			ID indexID = decoder.getID();

			if ((anIndex = TimeIndexDirectory.find(indexID)) != null) {
			    // the index is already opened and registered 
			    // so return a new TimeIndex object

			    // tell the directory there is another handle
			    TimeIndexDirectory.addHandle(anIndex);

			    return new TimeIndex(anIndex);
			} 
		    } catch (Exception ioe) {
			// it doesnt exist, so we will carry on and create it
		    }
		} else {
		    throw new IndexSpecificationException("No 'indexpath' specified for TimeIndexFactory in create()");
		}
	    } catch (IOException ioe) {
		// there was an I/O error detemrining the canonical path
		// thorw an exception
		throw new IndexCreateException(ioe);
	    }

	} else {
	    String indexName = indexProperties.getProperty("name");

	    if ((anIndex = TimeIndexDirectory.find(indexName)) != null) {
		// the index is already opened and registered 
		// so return a new TimeIndex object

		// tell the directory there is another handle
		TimeIndexDirectory.addHandle(anIndex);

		return new TimeIndex(anIndex);
	    } 
	} 


	// it can't be opened so really do the create
	switch (kind.value()) {
	case IndexType.INLINE: {
	    ManagedIndex newIndex = new InlineIndex(); 

	    newIndex.addPrimaryEventListener(this);

	    newIndex.create(indexProperties);

	    return new TimeIndex(newIndex);
	}

	case IndexType.EXTERNAL: {
	    ManagedIndex newIndex = new ExternalIndex(); 

	    newIndex.addPrimaryEventListener(this);

	    newIndex.create(indexProperties);

	    return new TimeIndex(newIndex);
	}

	case IndexType.SHADOW: {
	    ManagedIndex newIndex = new ShadowIndex(); 

	    newIndex.addPrimaryEventListener(this);

	    newIndex.create(indexProperties);

	    return new TimeIndex(newIndex);
	}

	case IndexType.INCORE: {
	    ManagedIndex newIndex = new IncoreIndex();

	    newIndex.addPrimaryEventListener(this);

	    newIndex.create(indexProperties);

	    return new TimeIndex(newIndex);
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

	ManagedIndex anIndex = null;
	String fileName = null;
	File indexFile = null;
	String indexPath = null;
	boolean incore = false;

	try {
	    if (indexProperties.containsKey("indexpath")) {
		fileName = indexProperties.getProperty("indexpath");
		indexFile = new File(fileName);
		indexPath = indexFile.getCanonicalPath();
	    } else if (indexProperties.containsKey("incore")) {
		incore = true;
	    } else {
		throw new IndexSpecificationException("No 'indexpath' specified for TimeIndexFactory in open()");
	    }
	} catch (IOException ioe) {
	    // there was an I/O error detemrining the canonical path
	    // thorw an exception
	    throw new IndexOpenException(ioe);
	}

	// process incore indexes differently from other indexes
	if (incore) {
	    String indexName = indexProperties.getProperty("name");

	    if ((anIndex = TimeIndexDirectory.find(indexName)) != null) {
		// the index is already opened and registered 
		// so return a new TimeIndex object

		// tell the directory there is another handle
		TimeIndexDirectory.addHandle(anIndex);

		return new TimeIndex(anIndex);
	    } else {
		throw new IndexOpenException("Can't open non-existant IncoreIndex. Specified name is " + indexName);
	    }

	} else {

	    // try and open the index and decode the header
	    decoder = openDecoder(indexProperties);
	    
	    // get some values out of the header

	    String indexName = decoder.getName();
	    ID indexID = decoder.getID();
	    IndexType kind = decoder.getIndexType();

	    if ((anIndex = TimeIndexDirectory.find(indexID)) != null) {
		// the index is already opened and registered 
		// so return a new TimeIndex object

		// tell the directory there is another handle
		TimeIndexDirectory.addHandle(anIndex);

		return new TimeIndex(anIndex);

	    } else {
		// actually open the index
		//Properties indexProperties = new Properties();
		indexProperties.setProperty("name", indexName);
		indexProperties.setProperty("indexid", indexID.toString());
		indexProperties.setProperty("indexpath", indexPath);

		switch (kind.value()) {
		case IndexType.INLINE: {
		    ManagedIndex newIndex = new InlineIndex(); 

		    newIndex.addPrimaryEventListener(this);

		    newIndex.open(indexProperties);

		    return new TimeIndex(newIndex);
		}

		case IndexType.EXTERNAL: {
		    ManagedIndex newIndex = new ExternalIndex(); 

		    newIndex.addPrimaryEventListener(this);

		    newIndex.open(indexProperties);

		    return new TimeIndex(newIndex);
		}

		case IndexType.SHADOW: {
		    ManagedIndex newIndex = new ShadowIndex(); 

		    newIndex.addPrimaryEventListener(this);

		    newIndex.open(indexProperties);

		    return new TimeIndex(newIndex);
		}

		case IndexType.INCORE: {
		    throw new TimeIndexFactoryException("TimeIndexFactory: Unexpected value for kind: " + kind + ". A stored index cant have this kind??");
		}

		default:
		    throw new TimeIndexFactoryException("TimeIndexFactory: Illegal value for kind: " + kind);
		}
	    }
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
     * @return whether the index was really closed. It might still be held open
     * by another TimeIndex.
     */
    public boolean close(Index index) {
	// flush out the contents
	boolean flushed = index.flush();

	// TODO: what should we do if flushed == false

	// close all the objects in the index
	boolean closed = index.close();

	return closed;
    }

    protected IndexDecoder openDecoder(Properties indexProperties) throws TimeIndexFactoryException,  IndexSpecificationException, IndexOpenException  {
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

	    return decoder;
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
