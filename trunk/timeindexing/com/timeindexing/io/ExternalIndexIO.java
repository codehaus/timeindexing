// ExternalIndexIO.java

package com.timeindexing.io;

import com.timeindexing.index.DataType;
import com.timeindexing.index.Index;
import com.timeindexing.index.ManagedIndex;
import com.timeindexing.index.StoredIndex;
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
import com.timeindexing.index.IndexTypeException;
import com.timeindexing.event.*;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Size;
import com.timeindexing.basic.Offset;
import com.timeindexing.time.TimestampDecoder;
import com.timeindexing.time.Timestamp;
import com.timeindexing.index.ExternalIndex;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * This does I/O for an Index with external data.
 * There are major / primary events on:
 * <ul>
 * <li> creation </li>
 * <li> flush </li>
 * <li> close </li>
 * <li> add item </li>
 * <li> access item </li>
 * </ul>
 */
public class ExternalIndexIO extends AbstractFileIO implements IndexFileInteractor {
    // data file objs
    String dataFileName = null;
    RandomAccessFile dataFile = null;
    FileChannel dataChannel = null;
    long dataChannelPosition = 0;
    long dataFirstPosition = 0;
    long dataAppendPosition = 0;
    ByteBuffer dataFlushBuffer = null;
    ByteBuffer dataHeaderBuf = null;


    String dataIndexName = null;
    ID dataIndexID = null;


    /**
     * Construct an External Index.
     */
    public ExternalIndexIO(StoredIndex managedIndex) {
	myIndex = managedIndex;
	headerBuf = ByteBuffer.allocate(HEADER_SIZE);
	dataHeaderBuf = ByteBuffer.allocate(HEADER_SIZE);
	indexBufWrite = ByteBuffer.allocate(INDEX_ITEM_SIZE);
	indexBufRead = ByteBuffer.allocate(INDEX_ITEM_SIZE);
	indexFlushBuffer = ByteBuffer.allocate(FLUSH_SIZE);
	dataFlushBuffer = ByteBuffer.allocate(FLUSH_SIZE);
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
	
	try {
	    open();

	    myIndex.setOption(HeaderOption.INDEXPATH_HO, indexFileName);
	    myIndex.setOption(HeaderOption.DATAPATH_HO, dataFileName);

	    //myIndex.getHeader().setIndexPathName(indexFileName);
	    //myIndex.getHeader().setDataPathName(dataFileName);

	    long indexHeaderPosition = writeHeader(FileType.EXTERNAL_INDEX);
	    indexAppendPosition = indexHeaderPosition;

	    long dataHeaderPosition = writeDataHeader(FileType.EXTERNAL_DATA);
	    dataAppendPosition = dataHeaderPosition;

	    return indexAppendPosition;

	} catch (IndexOpenException ioe) {
	    throw new IndexCreateException(ioe.getMessage());
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

	open();

	long indexHeaderPosition = readHeader(FileType.EXTERNAL_INDEX);

	long dataHeaderPosition = readDataHeader(FileType.EXTERNAL_DATA);

	// check ID in header == ID in index
	// and   ID in header == ID in data 
	// and   name in header == name in index
	// and   name in header == name in data 
	if (headerInteractor.getID().equals(indexID) && 
	    headerInteractor.getID().equals(dataIndexID) &&
	    headerInteractor.getName().equals(indexName) &&
	    headerInteractor.getName().equals(dataIndexName)) {
	    // The values in the header match up so we
	    // must be looking in the right place.

	    // sync the read header with the index object
	    myIndex.syncHeader(headerInteractor);

	    return indexHeaderPosition;
	} else {
	    // The values in the header are different
	    // so something is wrong
	    throw new IndexOpenException("The file '" + indexFileName +
					 "' is not an index for the data file '" +
					 dataFileName);
	}
    }


    /**
     * Open an index  to read it.
     * It normaises indexFileName and dataFileName.
     */
    protected long open() throws IOException, IndexOpenException {
	// open the index file
	try {
	    // resolve the index filename.  It replaces the first
	    // attempt with the real thing
	    indexFileName = FileUtils.resolveFileName(indexFileName, ".tix");

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
	    dataFileName = FileUtils.resolveFileName(dataFileName, ".tid");
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

	    dataFile = new RandomAccessFile(file, "rw");
	    dataChannel = dataFile.getChannel();

	} catch (FileNotFoundException fnfe) {
	    throw new IndexOpenException("Could not find data file: " + dataFileName);
	}


	return 0;
	
    }

    /**
     * Align the index for an append of the Data
     */
    protected long alignForData() throws IOException   {
	// seek to the append position in the data
	seekToData(dataAppendPosition);

	return dataChannelPosition; 
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

	dataChannelPosition += buffer.limit();  // was item.getDataSize().value();

	dataAppendPosition = dataChannelPosition;

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
	return bufferedWrite(buffer, dataChannel, dataFlushBuffer);
    }


    /**
     * Actually read in the data.
     */
    protected long readDataIntoBuffer(ByteBuffer buffer, long size) throws IOException {
	long readCount = 0;

	// read the data of index item
	if ((readCount = dataChannel.read(buffer)) != size) {
	    throw new IOException("Index Item Data too short: position = " +
				  dataChannel.position() + " read count = " + readCount);
	}
	dataChannelPosition += readCount;

	return readCount;
    }	
    

    /**
     * Memory map some data from a channel.
     */
    protected ByteBuffer memoryMapData(long offset, long size) throws IOException {
	return dataChannel.map(FileChannel.MapMode.READ_ONLY, offset, size);
    }
    
   /**
     * Read some data, given a DataReference
     * and return it as a DataHolderObject.
     */
    public DataHolderObject convertDataReference(DataReference dataReference) {
	try { 
	    ByteBuffer rawData = readData(dataReference);
	    return new DataHolderObject(rawData, dataReference.getSize());

 	} catch (IOException ioe) {
	    ioe.printStackTrace();
	    return null;
	}
   }


    /**
     * Seek to a certain position.
     * @return true if actually had to move the position,
     * returns false if in correct place
     */
    protected boolean seekToIndex(long position) throws IOException {
	//System.err.println("Index seek to " + position);
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
	//System.err.println("Data seek to " + position);
	if (dataChannelPosition != position) {
	    // we need to seek to a different place
	    dataChannel.position(position);
	    dataChannelPosition = position;
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
	written += flushBuffer(dataChannel, dataFlushBuffer);


	// flush the header
	headerInteractor.flush();

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

	//System.err.println("ExternalIndexIO: at close wrote = " + lastWrite);
	
	size = indexChannel.size();
	
	//System.err.println("ExternalIndexIO: size at close = " + size);

	indexChannel.close();
	dataChannel.close();


	// close the header
	headerInteractor.close();

	return size;
    }

    /**
     * Read an index header from the header stream.
     */
    public long readDataHeader(byte headerType) throws IOException, IndexOpenException {
	// seek to start
	seekToData(0);

	// The first two bytes are T & I
	byte byteT = dataFile.readByte();
	byte byteI = dataFile.readByte();
	// byte 3 = 0x03
	byte three = dataFile.readByte();
	// the type of the header
	byte type = dataFile.readByte();

	// check first 4 bytes
	if (byteT == FileType.T &&
	    byteI == FileType.I &&
	    three == FileType.BYTE_3 &&
	    type == headerType) {
	    // we've opened a TimeIndex Header

	    
	    // get the version no
	    dataFile.readByte();
	    dataFile.readByte();

	    // get the index ID
	    dataIndexID = new SID(dataFile.readLong());

	    // read the Index name
	    short nameSize = dataFile.readShort();
	    byte[] nameRaw = new byte[nameSize-1];
	    dataFile.readFully(nameRaw, 0, nameSize-1);
	    dataIndexName = new String(nameRaw);

	    // soak up index name padding
	    dataFile.readByte();

	    //System.err.println("External Data Header read size = " + dataChannel.position());

	    dataChannelPosition = dataChannel.position();
	    dataFirstPosition = dataChannelPosition;
	    return dataChannelPosition;
	
	} else {
	    throw new IndexOpenException("File is not a time index file");
	}
    }

    /**
     * Write the data file header.
     * Write the contents of the header out
     * It assumes the data file is alreayd open for writing.
     */
    public long writeDataHeader(byte headerType) throws IOException {
	// seek to start
	seekToData(0);

	// clear the header buffer
	dataHeaderBuf.clear();

	// fill the buffer with the bytes

	// TimeIndex Header magic
	dataHeaderBuf.put(FileType.T);
	dataHeaderBuf.put(FileType.I);
	dataHeaderBuf.put(FileType.BYTE_3);
	dataHeaderBuf.put(headerType);

	// version major
	dataHeaderBuf.put((byte)versionMajor);
	// version minor
	dataHeaderBuf.put((byte)versionMinor);

	// write the ID
	dataHeaderBuf.putLong(indexID.value());

	// size of name, +1 for null terminator
	dataHeaderBuf.putShort((short)(indexName.length()+1));

	// the name
	dataHeaderBuf.put(indexName.getBytes());
	// plus null terminator
	dataHeaderBuf.put((byte)0x00);

	// now write it out
	dataHeaderBuf.flip();
	long writeCount = dataChannel.write(dataHeaderBuf);

	dataChannelPosition = dataChannel.position();

	
	//System.err.println("Inline Index Header size = " + count);

	return writeCount;

    } 


    /**
     * Goto the append position
     */
    public boolean gotoAppendPosition() throws IOException {
	//System.err.println("Seek to append position = " + indexAppendPosition);
	seekToIndex(indexAppendPosition);
	//System.err.println("Seek to append data position = " + dataAppendPosition);
	return seekToData(dataAppendPosition);
    }

    /**
     * Goto the first position
     */
    public boolean gotoFirstPosition() throws IOException {
	//System.err.println("Seek to first position = " + indexFirstPosition);
	seekToIndex(indexFirstPosition);	
	//System.err.println("Seek to first data position = " + dataFirstPosition);
	return seekToData(dataFirstPosition);
    }

    /**
     * Set the append position from the indexChannelPosition.
     */
    public boolean setAppendPosition() throws IOException {
	//System.err.println("Index append position = " + indexChannelPosition);
	indexAppendPosition = indexChannelPosition;

	//System.err.println("Data append position = " + dataChannelPosition);
	dataAppendPosition = dataChannelPosition;

	return true;
    }


    /**
     * Calculate the append position from the last item of the index.
     */
    public long calculateAppendPosition() throws IOException {
	if (headerInteractor.getLength() == 0) {
	    // the index has zero items
	    // so there is nothing to read
	    // so we use the current channel positions
	    setAppendPosition();

	    return getAppendPosition();

	} else { 
	    // where is last item
	    Offset lastOffset = headerInteractor.getLastOffset();

	    // get last item
	    // lastOffset points to just before the last IndexItem
	    ManagedFileIndexItem itemM = (ManagedFileIndexItem)readItem(lastOffset.value(), false);

	    // work out append position
	    // from index data offset + data size
	    long indexAppendPoint = lastOffset.value() + headerInteractor.getItemSize();
	    long dataAppendPoint = itemM.getDataOffset().value() + itemM.getDataSize().value();

	    // set append position
	    indexAppendPosition = indexAppendPoint;
	    dataAppendPosition = dataAppendPoint;

	    return getAppendPosition();
	}
    }


}
