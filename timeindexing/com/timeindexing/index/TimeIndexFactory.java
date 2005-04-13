// TimeIndexFactory.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.io.IndexTyper;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.ByteBufferItem;
import com.timeindexing.event.*;

import java.util.Properties;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


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
     * Create a new Time Index object given an IndexType.
     * If the index already exists then an IndexView onto
     * that index will be returned.
     * @param kind One of IndexType.INLINE, IndexType.EXTERNAL, IndexType.SHADOW, IndexType.INCORE.
     * @param indexFile  the file spec of the index
     */
    public IndexView create(IndexType kind, File indexFile) throws TimeIndexFactoryException, IndexSpecificationException, IndexCreateException {
	Properties indexProperties = new Properties();
	indexProperties.setProperty("indexpath", indexFile.getPath());

	return create(kind, indexProperties);
    }


    /**
     * Create a new Time Index object given an IndexType.
     * If the index already exists then an IndexView onto
     * that index will be returned.
     * @param kind One of IndexType.INLINE, IndexType.EXTERNAL, IndexType.SHADOW, IndexType.INCORE.
     * @param uri the uri spec of the index
     */
    public IndexView create(IndexType kind, URI uri) throws TimeIndexFactoryException, IndexSpecificationException, IndexCreateException {
	Properties indexProperties = new Properties();
	indexProperties.setProperty("uri", uri.toString());

	return create(kind, indexProperties);
    }



    /**
     * Create a new Time Index object given an IndexType.
     * If the index already exists then an IndexView onto
     * that index will be returned.
     * @param kind One of IndexType.INLINE, IndexType.EXTERNAL, IndexType.SHADOW, IndexType.INCORE.
     * @param indexProperties properties of the index needed at creat time, such as  its name.
     */
    public IndexView create(IndexType kind, Properties indexProperties) throws TimeIndexFactoryException, IndexSpecificationException, IndexCreateException {

	ManagedIndex anIndex = null;
	String fileName = null;
	File indexFile = null;
	String indexPath = null;

	// check indexpath for non incore indexes
	if (kind.value() != IndexType.INCORE) {
	    
	    if (indexProperties.containsKey("indexpath")) {
		fileName = indexProperties.getProperty("indexpath");
		indexFile = new File(fileName);

		try {

		    indexPath = indexFile.getCanonicalPath();
		    //indexPath = indexFile.toURI().toString();
		} catch (IOException ioe) {
		    // there was an I/O error detemrining the canonical path
		    // thorw an exception
		    throw new IndexCreateException("Bad index 'indexpath' for TimeIndexFactory in create()");
		}


		// patch up indexpath to canonical form
		indexProperties.setProperty("indexpath", indexPath);

		    
		// now create a URI from the canonical path
		try {
		    URI indexURI = new URI("index", "", indexPath, null);
		
		    if ((anIndex = TimeIndexDirectory.find(indexURI.toString())) != null) {
			// the index is already opened and registered 
			// so return a new TimeIndex object

			return anIndex.asView();
		    } else {
			// it doesnt exist, so we will carry on and create it
		    }
		} catch (URISyntaxException usi) {
		    // there was an error detemrining the URI
		    // thorw an exception
		    throw new IndexCreateException("Bad index 'indexpath' for TimeIndexFactory in create()");
		}

	    } else if (indexProperties.containsKey("uri")) {
		try {
		    URI indexURI = new URI(indexProperties.getProperty("uri"));

		    if (! indexURI.isOpaque()) {
			// its a URI for a stored Index.

			if ((anIndex = TimeIndexDirectory.find(indexURI.toString())) != null) {
			    // the index is already opened and registered 
			    // so return a new TimeIndex object

			    return anIndex.asView();
			} else {
			    // it doesnt exist, so we will carry on and create it
			    // we'll set the "indexpath" property
			    indexPath = indexURI.getPath();
			    indexProperties.setProperty("indexpath", indexPath);
			}
		    } else {
			// the URI was not valid for a stored index
			// thorw an exception
			throw new IndexCreateException("Bad index 'uri' for TimeIndexFactory in create()");
		    }
		} catch (URISyntaxException usi) {
		    // there was an error detemrining the URI
		    // thorw an exception
		    throw new IndexCreateException("Bad index 'uri' for TimeIndexFactory in create()");
		}

	    } else {
		throw new IndexSpecificationException("No 'indexpath' or 'uri' specified for TimeIndexFactory in create()");
	    }

	} else {
	    // check indexProperties for an INCORE index

	    if (indexProperties.containsKey("name")) {
		// it's an incore index
		String indexName = indexProperties.getProperty("name");
		
		try {
		    URI indexURI = new URI("index", indexName, null);

		    if ((anIndex = TimeIndexDirectory.find(indexURI.toString())) != null) {
			// the index is already opened and registered 
			// so return a new TimeIndex object

			return anIndex.asView();
		    } 
		} catch (URISyntaxException use) {
		    throw new IndexSpecificationException("Bad index 'name' for TimeIndexFactory in create()");
		}
	    } else if (indexProperties.containsKey("uri")) {
		try {
		    URI indexURI = new URI(indexProperties.getProperty("uri"));

		    if (indexURI.isOpaque()) {
			// its a URI for an INCORE Index.

			if ((anIndex = TimeIndexDirectory.find(indexURI.toString())) != null) {
			    // the index is already opened and registered 
			    // so return a new TimeIndex object

			    return anIndex.asView();
			} else {
			    // it doesnt exist, so we will carry on and create it
			    // we'll set the "name" property
			    String indexName = indexURI.getSchemeSpecificPart();
			    indexProperties.setProperty("name", indexName);
			}
		    } else {
			// the URI was not valid for an INCORE index
			// thorw an exception
			throw new IndexCreateException("Bad index 'uri' for TimeIndexFactory in create()");
		    }
		} catch (URISyntaxException usi) {
		    // there was an error detemrining the URI
		    // thorw an exception
		    throw new IndexCreateException("Bad index 'uri' for TimeIndexFactory in create()");
		}

	    } else {
		throw new IndexSpecificationException("No 'name' or 'uri' specified for TimeIndexFactory in create()");
	    }
	} 


	// it can't be opened so we really need to do the create

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
     * Retrieve a TimeIndex object by URI.
     * e.g. TimeIndexFactory.open(new URI("index", "", "/path/to/index", null));
     *
     * @param indexURI  the URI  of the index
     */
    public IndexView open(URI indexURI) throws TimeIndexFactoryException, IndexSpecificationException, IndexOpenException {
	Properties indexProperties = new Properties();
	indexProperties.setProperty("uri", indexURI.toString());
	
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
	ManagedIndex anIndex = null;
	String fileName = null;
	File indexFile = null;
	String indexPath = null;
	boolean incore = false;

	// first pass over the properties
	if (indexProperties.containsKey("indexpath")) {
	    try {
		fileName = indexProperties.getProperty("indexpath");
		indexFile = new File(fileName);
		indexPath = indexFile.getCanonicalPath();
		incore = false;

	    } catch (IOException ioe) {
		// there was an I/O error detemrining the canonical path
		// thorw an exception
		throw new IndexOpenException("Bad index 'indexpath' for TimeIndexFactory in open()");
	    }

	} else if (indexProperties.containsKey("name")) {
	    incore = true;

	} else if (indexProperties.containsKey("uri")) {
	    try {
		URI indexURI = new URI(indexProperties.getProperty("uri"));

		if (indexURI.isOpaque()) {
		    // its a URI for an INCORE Index.
		    String indexName = indexURI.getSchemeSpecificPart();
		    indexProperties.setProperty("name", indexName);
		    incore = true;

		} else {
		    // its a URI for a stored Index.
		    indexPath = indexURI.getPath();
		    indexProperties.setProperty("indexpath", indexPath);
		    incore = false;

		}
	    } catch (URISyntaxException usi) {
		// there was an error detemrining the URI
		// thorw an exception
		throw new IndexOpenException("Bad index 'uri' for TimeIndexFactory in create()");
	    }
	} else {
	    throw new IndexSpecificationException("No 'indexpath' or 'name' or 'uri' specified for TimeIndexFactory in open()");
	}

	// we had enough property data to try and actually open an index

	// process INCORE indexes differently from other indexes
	// if the index is in the Directory return a view on it
	if (incore) {
	    String indexName = indexProperties.getProperty("name");

	    try {
		URI indexURI = new URI("index", indexName, null);

		if ((anIndex = TimeIndexDirectory.find(indexURI.toString())) != null) {
		    // the index is already opened and registered 
		    // so return a new TimeIndex object

		    return anIndex.asView();
		} else {
		    throw new IndexOpenException("Can't open non-existant IncoreIndex. Specified name is " + indexName);
		}

	    } catch (URISyntaxException use) {
		throw new IndexSpecificationException("Bad index 'name' for TimeIndexFactory in create()");
	    }
	} else {
	    // its a stored Index, so it really needs to be opened
	    
	    // an IndexTyper
	    // try and open the index and decode the tye
	    IndexTyper decoder = indexTyper(indexProperties);
	    
	    // get some values out of the header

	    String indexName = decoder.getName();
	    ID indexID = decoder.getID();
	    IndexType kind = decoder.getIndexType();

	    if ((anIndex = TimeIndexDirectory.find(indexID)) != null) {
		// the index is already opened and registered 
		// so return a new TimeIndex object

		return anIndex.asView();
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
     * Create a new Index object from an existing Index object.
     * The type of the new Index is based on a constant, as defined in TimeType.
     * @param index the original index to convert
     * @param kind One of IndexType.INLINE, IndexType.EXTERNAL, IndexType.INCORE.
     * @param indexProperties properties of the index needed at creat time, such as  its name.
     */
    public IndexView save(Index index, IndexType kind, Properties indexProperties) throws TimeIndexFactoryException, IndexSpecificationException, IndexCreateException, TimeIndexException {
	IndexView newIndexView = create(kind, indexProperties);

	// Get a direct handle on the index
	ManagedIndex newIndex = TimeIndexDirectory.find(newIndexView.getID());

	Iterator itemIterator = index.iterator();

	while (itemIterator.hasNext()) {
	    ManagedIndexItem anItem = (ManagedIndexItem)itemIterator.next();

	    if (anItem.isReference()) {
		IndexReferenceDataHolder reference =  (IndexReferenceDataHolder)anItem.getDataAbstraction();

		// add a new reference to the index
		newIndex.addReference(reference, anItem.getDataTimestamp());

	    } else {
		// the index item is unpacked
		// and a DataItem created
		// so it can b added to the new index properly
		DataItem dataItem = new ByteBufferItem(anItem.getData(), anItem.getDataType());

		// the DataItem is added to the new index with the 
		// data timestamp from the original index item
		newIndex.addItem(dataItem, anItem.getDataTimestamp());
	    }
	}

	newIndex.commit();
	    
	return newIndexView;
	    
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
     * e.g. TimeIndexFactory.append(new File(someDirectory, fileName))
     *
     * @param uri  the URI  of the index
     */
    public IndexView append(URI uri) throws TimeIndexFactoryException, IndexSpecificationException, IndexOpenException {
	Properties indexProperties = new Properties();
	indexProperties.setProperty("uri", uri.toString());
	
	return append(indexProperties);
    }

    /**
     * Append to an index.
     * e.g. TimeIndexFactory.appemd(properties)
     *
     * @param indexProperties  a Properties spec of the index
     */
    public IndexView append(Properties indexProperties)  throws TimeIndexFactoryException, IndexSpecificationException, IndexOpenException {
	indexProperties.setProperty("loadstyle", "none");
	return open(indexProperties);
    }


    /**
     * Try and find an Index, given its ID.
     */
    public IndexView find(ID indexID) {
	ManagedIndex anIndex = null;

	if ((anIndex = TimeIndexDirectory.find(indexID)) != null) {
	    // the index is already opened and registered 
	    // so return a new TimeIndex object

	    return anIndex.asView();
	} else {
	    return null;
	}

    }

    /**
     * Close an index
     * @return whether the index was really closed. It might still be held open
     * by another TimeIndex.
     */
    public boolean close(Index index) throws IndexCommitException, IndexCloseException {
	// commit the contents
	boolean committed = index.commit();

	// TODO: what should we do if committed == false

	// close all the objects in the index
	boolean closed = index.close();

	return closed;
    }

    /**
     * Open an Index and decode the header.
     * The Index is closed when this method returns.
     */
    private IndexTyper indexTyper(Properties indexProperties) throws TimeIndexFactoryException,  IndexSpecificationException, IndexOpenException  {
	IndexTyper decoder = null;

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
	    decoder = new IndexTyper(indexFile);

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
     * A notification that an Index has been committed.
     */
    public  void committed(IndexPrimaryEvent ipe) {
	//System.err.println("Commit event from " + ipe.getName() + ". Class = " + ipe.getSource().getClass().getName());
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
