// TimeIndexFactory.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.io.IndexDecoder;

import java.util.Properties;
import java.io.File;
import java.io.IOException;

/**
 * This is the TimeIndexFactory which returns different implementations
 * of a TimeIndex, depending on the arguments to create() or retrieve().
 */
public class TimeIndexFactory {
    /**
     * Get a new Time Index object given a constant, as defined in TimeType.
     * @param kind One of IndexType.INLINE, IndexType.EXTERNAL, IndexType.INCORE,
     * IndexType.JAVASERIAL.
     * @param indexProperties properties of the index needed at creat time, such as  its name.
     */
    public IndexView create(int kind, Properties indexProperties) throws IndexCreateException {
	switch (kind) {
	case IndexType.INLINE: {
	    ManagedIndex newIndex = new InlineIndex(indexProperties); 

	    newIndex.create();

	    // get the ID and name values
	    ID indexID = newIndex.getID();
	    String indexName = newIndex.getName();
	    
	    // save these in the TimeIndex directories
	    TimeIndexDirectory.register(newIndex, indexName, indexID);

	    return new TimeIndex(new TimeIndexInteractor(newIndex));
	}

	case IndexType.EXTERNAL: {
	    throw new TimeIndexFactoryException("TimeIndexFactory: No code for EXTERNAL yet.");
	    //break;
	}

	case IndexType.INCORE: {
	    ManagedIndex newIndex = new IncoreIndex(indexProperties);

	    newIndex.create();

	    // get the ID and name values
	    ID indexID = newIndex.getID();
	    String indexName = newIndex.getName();
	    
	    // save these in the TimeIndex directories
	    TimeIndexDirectory.register(newIndex, indexName, indexID);

	    return new TimeIndex(new TimeIndexInteractor(newIndex));
	}

	case IndexType.JAVASERIAL: {
	    // create the new index object
	    ManagedIndex newIndex = new FullIndexImpl(indexProperties);

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
    public IndexView convert(Index index, int kind, Properties indexProperties) {
	// TODO: implement me
	return null;
    }

    /**
     * Retrieve a TimeIndex object by file name.
     * e.g. TimeIndexFactory.open(new File(someDirectory, fileName))
     *
     * @param indexFile  the file  of the index
     */
    public IndexView open(File indexFile) {
	Properties indexProperties = new Properties();
	indexProperties.setProperty("filename", indexFile.getPath());
	
	return open(indexProperties);
    }

    /**
     * Retrieve a TimeIndex object by file name.
     * e.g. TimeIndexFactory.open(properties)
     *
     * @param indexFilename  the filename of the index
     */
    public IndexView open(Properties indexProperties) {
	try {
	    // decode the named file
	    File indexFile = null;

	    if (indexProperties.containsKey("filename")) {
		String fileName = indexProperties.getProperty("filename");
		indexFile = new File(fileName);
	    } else {
		throw new Error("No 'filename' specified for TimeIndexFactory");
	    }

	    // create an index decoder
	    IndexDecoder decoder = new IndexDecoder(indexFile);

	    // read the header
	    decoder.read();
	    // close it
	    decoder.close();

	    
	    // now open the index properly

	    String indexName = decoder.getName();
	    ID indexID = decoder.getID();
	    int kind = decoder.getIndexType();

	    //Properties indexProperties = new Properties();
	    indexProperties.setProperty("name", indexName);
	    indexProperties.setProperty("filename", indexFile.getCanonicalPath());

	    switch (kind) {
	    case IndexType.INLINE: {
		ManagedIndex newIndex = new InlineIndex(indexProperties); 

		newIndex.open();

		// save these in the TimeIndex directories
		TimeIndexDirectory.register(newIndex, indexName, indexID);

		return new TimeIndex(new TimeIndexInteractor(newIndex));
	    }

	    case IndexType.EXTERNAL: {
		throw new TimeIndexFactoryException("TimeIndexFactory: No code for EXTERNAL yet.");
		//break;
	    }

	    case IndexType.INCORE: {
		ManagedIndex newIndex = new IncoreIndex(indexProperties);

		newIndex.open();

		// save these in the TimeIndex directories
		TimeIndexDirectory.register(newIndex, indexName, indexID);

		return new TimeIndex(new TimeIndexInteractor(newIndex));
	    }

	    case IndexType.JAVASERIAL: {
		// create the new index object
		ManagedIndex newIndex = new FullIndexImpl(indexProperties);

		newIndex.open();

		// save these in the TimeIndex directories
		TimeIndexDirectory.register(newIndex, indexName, indexID);

		return new TimeIndex(new TimeIndexInteractor(newIndex));
	    }

	    default:
		throw new TimeIndexFactoryException("TimeIndexFactory: Illegal value for kind: " + kind);
	    }

	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
	return null;
    }


    /**
     * Append to an index.
     * e.g. TimeIndexFactory.append(new File(someDirectory, fileName))
     *
     * @param indexFile  the file  of the index
     */
    public IndexView append(File indexFile) {
	Properties indexProperties = new Properties();
	indexProperties.setProperty("filename", indexFile.getPath());
	
	return append(indexProperties);
    }

    /**
     * Append to an index.
     * e.g. TimeIndexFactory.appemd(properties)
     *
     * @param indexFilename  the filename of the index
     */
    public IndexView append(Properties indexProperties) {
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
}	
