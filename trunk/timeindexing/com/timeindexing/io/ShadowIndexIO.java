/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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

	originalIndexSpecifier = (String)indexProperties.get("canonicalpath");

	headerInteractor = new IndexHeaderIO(this);

	// use the original specifier as a first cut for the idnex file name
	headerFileName = originalIndexSpecifier;
	indexFileName = (String)indexProperties.get("indexpath");

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

	    headerInteractor.setOption(HeaderOption.NO_DATA_FILE_HEADER_HO, Boolean.TRUE);

	    long position = writeHeader(FileType.SHADOW_INDEX);
	    indexAppendPosition = position;
	    dataAppendPosition = 0;

	    flush();

	    initThread(indexName + "-IOThread");
	    startThread();

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

	// now check to see if this index should be opened
	// as read-only
	Boolean readOnly = (Boolean)indexProperties.get("readonly");

	if (readOnly.equals(Boolean.TRUE)) {
	    headerInteractor.setReadOnly(true);
	}

	// open the relevant files
	open();

	// read the headers
	long position = readMetaData();

	initThread(indexName + "-IOThread");
	startThread();

	return position;
   }


    /**
     * Open an index   to read it.
     */
    protected long open() throws IOException, IndexOpenException {
	// open the index file
	try {
	    // resolve the index filename.  It replaces the first
	    // attempt with the real thing
	    indexFileName = FileUtils.resolveFileName(indexFileName, ".tix");

	    File file = new File(indexFileName);

	    // determine if the files should be opened 
	    // read-only or read-write
	    String openMode = null;

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
		    indexFileName = file.getName(); //getAbsolutePath();
		}

		if (file.canWrite()) {
		    openMode = "rw";
		} else {
		    openMode = "r";
		}

	    } else {                            // we ARE creating
		// file names can be be realtive
		// so we have to resolve filenames

		if (! file.isAbsolute()) {
		    indexFileName = new File(indexFileName).getName();
		}

		// can;t create anything without writing
		openMode = "rw";
	    }

	    indexFile = new RandomAccessFile(file, openMode);
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
     * Read all the meta data.
     */
    public long readMetaData() throws IOException, IndexOpenException {
 
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
    public synchronized long flush() throws IOException {
	long written = 0;

	// flush out any reaming data
	ByteBuffer indexBuffer = indexFlushBuffers.current();
	written += flushBuffer(indexChannel, indexBuffer, indexFlushBuffers);

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
