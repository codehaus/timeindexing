// InlineIndexIO.java

package com.timeindexing.io;

import com.timeindexing.index.DataType;
import com.timeindexing.index.Index;
import com.timeindexing.index.ManagedIndex;
import com.timeindexing.index.ManagedStoredIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.FileIndexItem;
import com.timeindexing.index.DataAbstraction;
import com.timeindexing.index.DataHolderObject;
import com.timeindexing.index.DataReference;
import com.timeindexing.index.DataReferenceObject;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.HeaderOption;
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.index.IndexCreateException;
import com.timeindexing.event.*;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Size;
import com.timeindexing.basic.Offset;
import com.timeindexing.time.TimestampDecoder;
import com.timeindexing.time.Timestamp;
import com.timeindexing.index.InlineIndex;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This does I/O for an Index with inline data.
 * There are major / primary events on:
 * <ul>
 * <li> creation </li>
 * <li> flush </li>
 * <li> close </li>
 * <li> add item </li>
 * <li> access item </li>
 */
public class InlineIndexIO extends AbstractFileIO implements IndexFileInteractor {
    /**
     * Construct an Inline Index.
     */
    public InlineIndexIO(ManagedStoredIndex indexMgr) {
	myIndex = indexMgr;
	headerBuf = ByteBuffer.allocate(HEADER_SIZE);
	indexBufWrite = ByteBuffer.allocate(INDEX_ITEM_SIZE);
	indexBufRead = ByteBuffer.allocate(INDEX_ITEM_SIZE);
	indexFlushBuffer = ByteBuffer.allocate(FLUSH_SIZE);
    }

    /**
     * Operation on creation.
     */
    public long create(IndexProperties indexProperties) throws IOException, IndexCreateException{
	creating = true;

	originalIndexSpecifier = (String)indexProperties.get("indexpath");

	// use the original specifier as a first cut for the header file name
	// and the index file name
	headerFileName = originalIndexSpecifier;
	indexFileName = originalIndexSpecifier;

	indexName = (String)indexProperties.get("name");
	indexID = (ID)indexProperties.get("indexid");

	try {
	    open();

	    myIndex.getHeader().setOption(HeaderOption.INDEXPATH_HO, indexFileName);

	    //was TODO: and should be myIndex.getHeader().setIndexPathName(indexFileName);

	    long position = writeHeader(FileType.INLINE_INDEX);
	    indexAppendPosition = position;

	    return position;
	} catch (IndexOpenException ioe) {
	    throw new IndexCreateException(ioe.getMessage());
	}
    }

    /**
     * Open an index file  to read it.
     */
    public long open(IndexProperties indexProperties) throws IOException, IndexOpenException {
	creating = false;

	headerInteractor = (IndexHeaderIO)indexProperties.get("header");
	originalIndexSpecifier = (String)indexProperties.get("headerpath");

	headerFileName = headerInteractor.getHeaderPathName();
	indexFileName = headerInteractor.getIndexPathName();

	open();

	long position = readHeader(FileType.INLINE_INDEX);

	// check ID in header == ID in index
	// and   name in header == name in index
	if (headerInteractor.getID().equals(indexID) && 
	    headerInteractor.getName().equals(indexName)) {
	    // The values in the header match up so we
	    // must be looking in the right place.
	    return position;
	} else {
	    // The values in the header are different
	    // so something is wrong
	    throw new IndexOpenException("The file '" + indexFileName +
					 "' is not an index associated with the header");
	}
    }

    /**
     * Open an index   to read it.
     */
    protected long open() throws IOException, IndexOpenException {
	try {
	    // resolve the index filename.  It replaces the first
	    // attempt with the real thing
	    indexFileName = FileUtils.resolveFileName(indexFileName, ".tii");
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

	    //System.err.println("InlineIndexIO: opened \"" + actualFileName + "\"");

	} catch (FileNotFoundException fnfe) {
	    throw new IndexOpenException("Could not find index file: " + indexFileName);
	}

	return 0;
	
    }


    /**
     * Align the index for an append of the Data
     */
    protected long alignForData() throws IOException   {
	 return indexChannelPosition + INDEX_ITEM_SIZE;
    }


    /**
     * Processing of the idnex item.
     */
    protected long processIndexItem(ByteBuffer buffer) throws IOException  {
	// write the index item
	long count = bufferedIndexWrite(buffer);

	indexChannelPosition += INDEX_ITEM_SIZE;

	indexAppendPosition = indexChannelPosition;

	return count;
    }

    /**
     * Processing of the data.
     */
    protected long processData(ByteBuffer buffer) throws IOException  {
	// write the data
	long count= bufferedDataWrite(buffer);

	indexChannelPosition += buffer.limit();  // was item.getDataSize().value();

	indexAppendPosition = indexChannelPosition;

	return count;
    }

    /**
     * Write a buffer of index items.
     */
    protected long bufferedIndexWrite(ByteBuffer buffer) throws IOException {
	return bufferedWrite(buffer, indexChannel, indexFlushBuffer);
    }


    /**
     * Write a buffer of data.
     */
    protected long bufferedDataWrite(ByteBuffer buffer) throws IOException {
	return bufferedWrite(buffer, indexChannel, indexFlushBuffer);
    }


    /**
     * Actually read in the data.
     */
    protected long readDataIntoBuffer(ByteBuffer buffer, long size) throws IOException {
	long readCount = 0;

	// read the data of index item
	if ((readCount = indexChannel.read(buffer)) != size) {
	    throw new IOException("Index Item Data too short: position = " +
				  indexChannel.position() + " read count = " + readCount);
	}
	indexChannelPosition += readCount;

	return readCount;
    }	
    

    /**
     * Memory map some data from a channel.
     */
    protected ByteBuffer memoryMapData(long offset, long size) throws IOException {
	return indexChannel.map(FileChannel.MapMode.READ_ONLY, offset, size);
    }
    

    /**
     * Seek to a certain position.
     * @return true if actually had to move the position,
     * returns false if in correct place
     */
    protected boolean seekToIndex(long position) throws IOException {
	if (indexChannelPosition != position) {
	    // we need to seek to a different place
	    indexChannel.position(position);
	    indexChannelPosition = position;
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Seek to a certain position in the data file.
     * @return true if actually had to move the position,
     * returns false if in correct place
     */
    protected boolean seekToData(long position) throws IOException {
	if (indexChannelPosition != position) {
	    // we need to seek to a different place
	    indexChannel.position(position);
	    indexChannelPosition = position;
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Operation on flush.
     * Returns how many bytes were written.
     */
    public long flush() throws IOException {
	long written = 0;

	// flush out any reaming data
	written += flushBuffer(indexChannel, indexFlushBuffer);
	return written;
    }

    /**
     * Operation on close
     * @return the size of the index
     */
    public long close() throws IOException {
	long size = -1;
	// flush out any reaming data
	long lastWrite = flush();

	//System.err.println("InlineIndexIO: at close wrote = " + lastWrite);
	
	size = indexChannel.size();
	
	//System.err.println("InlineIndexIO: size at close = " + size);

	indexChannel.close();

	return size;
    }

}