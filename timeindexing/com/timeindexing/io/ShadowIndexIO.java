// ShadowIndexIO.java

package com.timeindexing.io;

import com.timeindexing.index.StoredIndex;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.HeaderOption;
import com.timeindexing.index.IndexCreateException;
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.basic.ID;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This does I/O for a shadow Index with external data.
 */
public class  ShadowIndexIO extends ExternalIndexIO implements IndexFileInteractor {
    /**
     * Construct a shadow Index.
     */
    public ShadowIndexIO(StoredIndex indexMgr) {
	super(indexMgr);
    }

    /**
     * Operation on creation.
     */
    public long create(IndexProperties indexProperties) throws IOException, IndexCreateException {
	creating = true;

	originalIndexSpecifier = (String)indexProperties.get("indexpath");

	headerInteractor = new IndexHeaderIO(this);

	// use the original specifier as a first cut for the idnex file name
	headerFileName = originalIndexSpecifier;
	indexFileName = originalIndexSpecifier;

	dataFileName = (String)indexProperties.get("datapath");
	indexName = (String)indexProperties.get("name");
	indexID = (ID)indexProperties.get("indexid");

	// create the header
	headerInteractor.create(originalIndexSpecifier);
	

	// determine the URI
	try {
	    URI uri = new URI("index", "", FileUtils.removeExtension(originalIndexSpecifier), null);

	    headerInteractor.setURI(uri);
	    // tell the index
	    getIndex().setURI(uri);
	} catch (URISyntaxException use) {
	    System.err.println("ShadowIndexIO: setting URI failed using " + originalIndexSpecifier + " => " + FileUtils.removeExtension(originalIndexSpecifier));
	}

	try {
	    open();

	    indexFile.setLength(0);

	    getIndex().setOption(HeaderOption.INDEXPATH_HO, indexFileName);
	    getIndex().setOption(HeaderOption.DATAPATH_HO, dataFileName);

	    long position = writeHeader(FileType.SHADOW_INDEX);
	    indexAppendPosition = position;
	    dataAppendPosition = 0;

	    return position;
	} catch (IndexOpenException ioe) {
	    throw new IndexCreateException(ioe);
	}
    }

    /**
     * Open an index file  to read it.
     */
    public long open(IndexProperties indexProperties) throws IOException, IndexOpenException {
	creating = false;

	originalIndexSpecifier = (String)indexProperties.get("indexpath");

	headerInteractor = new IndexHeaderIO(this);

	// open the index header
	headerInteractor.open(originalIndexSpecifier);

	//headerInteractor = (IndexHeaderIO)indexProperties.get("header");

	headerFileName = headerInteractor.getHeaderPathName();
	indexFileName = headerInteractor.getIndexPathName();
	dataFileName = headerInteractor.getDataPathName();

	// determine the URI
	// if the index path name is relative we need to make it an absolute path name
	File indexSpecFile = new File(headerFileName);
	String uriPath = null;

	if (indexSpecFile.isAbsolute()) {
	    uriPath = headerFileName;
	} else {
	    uriPath = indexSpecFile.getAbsolutePath();
	}

	try {
	    headerInteractor.setURI(new URI("index", "", FileUtils.removeExtension(uriPath), null));
	} catch (URISyntaxException use) {
	    ;
	}

	open();

	long position = readHeader(FileType.SHADOW_INDEX);

	// check ID in header == ID in index
	// and   name in header == name in index
	if (headerInteractor.getID().equals(indexID) && 
	    headerInteractor.getName().equals(indexName)) {
	    // The values in the header match up so we
	    // must be looking in the right place.

	    // sync the read header with the index object
	    getIndex().syncHeader(headerInteractor);

	    return position;
	} else {
	    // The values in the header are different
	    // so something is wrong
	    throw new IndexOpenException("The file '" + indexFileName +
					 "' is not an index for the data file '" +
					 dataFileName);
	}

   }

    /**
     * Open an index   to read it.
     */
    protected long open() throws IOException, IndexOpenException {
	// open the index file
	try {
	    indexFileName = FileUtils.resolveFileName(indexFileName, ".tix");

	    //System.err.println("HeaderFileName = " + headerFileName + ". " + "IndexFileName = " + indexFileName);

	    File file = new File(indexFileName);


	    /*
	     * Attempt to resolve relative named
	     * index file to absolute one
	     */
	    if (! creating) {
		// file names can be be realtive
		// so we have to resolve filenames

		if (! file.isAbsolute()) {
		    File headerFile = new File(headerFileName);

		    file = new File(headerFile.getParent(), indexFileName);
		    indexFileName = file.getAbsolutePath();
		}
	    }



	    indexFile = new RandomAccessFile(file, "rw");
	    indexChannel = indexFile.getChannel();

	} catch (FileNotFoundException fnfe) {
	    throw new IndexOpenException("Could not find index file: " + indexFileName);
	}

	// open the data file
	try {
	    dataFileName = FileUtils.resolveFileName(dataFileName, null);
	    File file = new File(dataFileName);

	    /*
	     * Attempt to resolve relative named
	     * data file to absolute one
	     */
	    if (! creating) {
		// file names can be be realtive
		// so we have to resolve filenames

		if (! file.isAbsolute()) {
		    File headerFile = new File(headerFileName);

		    file = new File(headerFile.getParent(), dataFileName);
		    dataFileName = file.getAbsolutePath();
		}
	    }

	    dataFile = new RandomAccessFile(file, "r");
	    dataChannel = dataFile.getChannel();

	} catch (FileNotFoundException fnfe) {
	    throw new IndexOpenException("Could not find data file: " + dataFileName);
	}

	return 0;
	
    }

    
    /**
     * Processing of the data.
     */
    protected long processData(ByteBuffer buffer) throws IOException  {
	// don't actually write the data
	// work out how much we would have written
	long count = buffer.limit();

	dataChannelPosition += buffer.limit();  // was item.getDataSize().value();

	dataAppendPosition = dataChannelPosition;

	return count;
    }

    /**
     * Operation on flush.
     * Returns how many bytes were written.
     */
    public long flush() throws IOException {
	long written = 0;

	// flush out any reaming data
	written += flushBuffer(indexChannel, indexFlushBuffer);

	// flush the header
	headerInteractor.flush();

	return written;
    }

    /**
     * Read an index header from the header stream.
     * Nothing to do as there is no header.
     */
    public long readDataHeader(byte headerType) throws IOException {
	return 0;
    }

    /**
     * Write the data file header.
     * Nothing to do as there is no header.
     */
    public long writeDataHeader(byte headerType) throws IOException {
	return 0;
    }
}
