// IndexOpener.java

package uk.ti;

import com.timeindexing.basic.ID;
import com.timeindexing.io.IndexDecoder;
import com.timeindexing.index.Index;
import com.timeindexing.index.ManagedIndex;
import com.timeindexing.index.InlineIndex;
import com.timeindexing.index.ExternalIndex;
import com.timeindexing.index.ShadowIndex;
import com.timeindexing.index.IncoreIndex;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.index.IndexSpecificationException;

import java.util.Properties;
import java.io.File;
import java.io.IOException;

public class IndexOpener {
    /**
     * Retrieve a Index object by file name.
     * e.g. IndexOpener.open(new File(someDirectory, fileName))
     *
     * @param indexFile  the file  of the index
     */
    public ManagedIndex open(File indexFile) throws IndexSpecificationException, IndexOpenException, TimeIndexException {
	Properties indexProperties = new Properties();
	indexProperties.setProperty("indexpath", indexFile.getPath());
	
	return open(indexProperties);
    }

    /**
     * Retrieve a Index object by file name.
     * e.g. IndexOpener.open(properties)
     *
     * @param indexFilename  the filename of the index
     * @return null if the index can't be opened
     */
    public ManagedIndex open(Properties indexProperties)  throws IndexSpecificationException, IndexOpenException, TimeIndexException {
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
		throw new IndexSpecificationException("No 'indexpath' specified for IndexOpener");
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
	IndexType kind = decoder.getIndexType();

	//Properties indexProperties = new Properties();
	indexProperties.setProperty("name", indexName);
	indexProperties.setProperty("indexpath", indexPath);

	switch (kind.value()) {
	case IndexType.INLINE: {
	    ManagedIndex newIndex = new InlineIndex(); 

	    newIndex.open(indexProperties);

	    return newIndex;
	}

	case IndexType.EXTERNAL: {
	    ManagedIndex newIndex = new ExternalIndex(); 

	    newIndex.open(indexProperties);

	    return newIndex;
	}

	case IndexType.SHADOW: {
	    ManagedIndex newIndex = new ShadowIndex(); 

	    newIndex.open(indexProperties);

	    return newIndex;
	}

	case IndexType.INCORE: {
	    ManagedIndex newIndex = new IncoreIndex();

	    newIndex.open(indexProperties);

	    return newIndex;
	}


	default:
	    throw new TimeIndexException("IndexOpener: Illegal value for kind: " + kind);
	}

    }
    public boolean close(Index index) {
	// close all the objects in the index
	boolean closed = index.close();

	return closed;
    }

}
