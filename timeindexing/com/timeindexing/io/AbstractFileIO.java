// AbstractFileIO.java

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
import com.timeindexing.index.DataTypeDirectory;
import com.timeindexing.index.IndexProperties;
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


public abstract class AbstractFileIO extends AbstractIndexIO implements IndexFileInteractor {
    // The index this is doing I/O for
    ManagedStoredIndex myIndex = null;
    // The header for this index
    IndexHeaderIO headerInteractor = null;

    String indexName = null;
    ID indexID = null;
    DataType dataType = DataType.NOTSET_DT;

    String originalIndexSpecifier = null; // the original spec for an index
    String headerFileName = null; // the resolved header file name

    // index file objs
    String indexFileName = null; // the resolved index file name
    RandomAccessFile indexFile = null; // the actual index file
    FileChannel indexChannel = null;
    long indexChannelPosition = 0;
    long indexFirstPosition = 0;
    long indexAppendPosition = 0;

    // buffers
    ByteBuffer headerBuf = null;
    ByteBuffer indexBufWrite = null;
    ByteBuffer indexBufRead = null;
    ByteBuffer indexFlushBuffer = null;

    // TimestampDecoder
    TimestampDecoder timestampDecoder = new TimestampDecoder();

    int versionMajor = 0;
    int versionMinor = 0;
    
    // are we creating or opening
    boolean creating = false;

    /*
     * The size of a header
     */
    final static int HEADER_SIZE = 512;

    /*
     * The size of an index item in the index file
     */
    static int INDEX_ITEM_SIZE = 52;


    /*
     * The size of a flush buffer
     */
    final static int FLUSH_SIZE = 1 * 1024;


    /**
     * Read an index header from the header stream.
     */
    public long readHeader(byte headerType) throws IOException, IndexOpenException {
	// seek to start
	seekToIndex(0);

	// The first two bytes are T & I
	byte byteT = indexFile.readByte();
	byte byteI = indexFile.readByte();
	// byte 3 = 0x03
	byte three = indexFile.readByte();
	// the type of the header
	byte type = indexFile.readByte();

	// check first 4 bytes
	if (byteT == FileType.T &&
	    byteI == FileType.I &&
	    three == FileType.BYTE_3 &&
	    type == headerType) {
	    // we've opened a TimeIndex Header

	    
	    // get the version no
	    versionMajor = (int)indexFile.readByte();
	    versionMinor = (int)indexFile.readByte();

	    // get the index ID
	    indexID = new SID(indexFile.readLong());

	    // read the Index name
	    short nameSize = indexFile.readShort();
	    byte[] nameRaw = new byte[nameSize-1];
	    indexFile.readFully(nameRaw, 0, nameSize-1);
	    indexName = new String(nameRaw);

	    // soak up index name padding
	    indexFile.readByte();

	    //System.err.println("Inline Index Header read size = " + indexFile.getFilePointer());

	    indexChannelPosition = indexChannel.position();
	    indexFirstPosition = indexChannelPosition;
	    return indexChannelPosition;
	
	} else {
	    throw new IndexOpenException("File is not a time index file");
	}
    }

    /**
     * Write the contents of the header out
     * It assumes the index file is alreayd open for writing.
     */
    public long writeHeader(byte headerType) throws IOException {
	// seek to start
	seekToIndex(0);

	// clear the header buffer
	headerBuf.clear();

	// fill the buffer with the bytes

	// TimeIndex Header magic
	headerBuf.put(FileType.T);
	headerBuf.put(FileType.I);
	headerBuf.put(FileType.BYTE_3);
	headerBuf.put(headerType);

	// version major
	headerBuf.put((byte)versionMajor);
	// version minor
	headerBuf.put((byte)versionMinor);

	// write the ID
	headerBuf.putLong(indexID.value());

	// size of name, +1 for null terminator
	headerBuf.putShort((short)(indexName.length()+1));

	// the name
	headerBuf.put(indexName.getBytes());
	// plus null terminator
	headerBuf.put((byte)0x00);

	// now write it out
	headerBuf.flip();
	long writeCount = indexChannel.write(headerBuf);

	indexChannelPosition = indexChannel.position();

	
	//System.err.println("Inline Index Header size = " + count);

	return writeCount;

    }


    /**
     * Write the contents of the item
     * It assumes the index file is alreayd open for writing.
     */
    public long writeItem(ManagedIndexItem itemM) throws IOException {
	// cast the item to the correct class
	ManagedFileIndexItem item = (ManagedFileIndexItem)itemM;

	long count = 0;

	long actualSize = itemM.getDataSize().value();

	if (actualSize >= Integer.MAX_VALUE) {
	    // buffers can only be so big
	    // check we can allocate one big enough
		throw new Error("InlineIndexIO: writeItem() has not YET implemented reading of data > " + Integer.MAX_VALUE + ". Actual size is " + actualSize);
	} else {

	    // where are we in the file
	    long currentIndexPosition = alignForIndexItem();

	    //System.err.println("P(W) = " + currentPosition);

	    // tell the IndexItem where its index is
	    item.setIndexOffset(new Offset(currentIndexPosition));

	    // set the data position
	    long currentDataPosition = alignForData();

	    // tell the IndexItem where its data is
	    item.setDataOffset(new Offset(currentDataPosition));

	    // clear the index buf
	    indexBufWrite.clear();

	    // fill the buffer
	    indexBufWrite.putLong(item.getIndexTimestamp().value());
	    indexBufWrite.putLong(item.getDataTimestamp().value());
	    indexBufWrite.putLong(currentDataPosition);
	    indexBufWrite.putLong(item.getDataSize().value());
	    indexBufWrite.putInt(item.getDataType().value());
	    indexBufWrite.putLong(item.getItemID().value());
	    indexBufWrite.putLong(item.getAnnotations().value()); // TODO: fix annoation code

	    // make it ready for writing
	    indexBufWrite.flip();

	    // write the index item
	    count +=  processIndexItem(indexBufWrite);
	
	    // write the data
	    count += processData(item.getData());

	    // return how many bytes were written
	    return count;
	}
    }


    /**
     * Align the index for an append of an IndexItem.
     */
    protected long alignForIndexItem()  throws IOException  {
	// seek to the append position in the index
	seekToIndex(indexAppendPosition);

	return indexChannelPosition;
    }

    /**
     * Align the index for an append of the Data
     * This is done differently for each type of index.
     */
    protected abstract long alignForData() throws IOException ;

    /**
     * Processing of the idnex item.
     */
    protected abstract long processIndexItem(ByteBuffer buffer) throws IOException;

    /**
     * Processing of the data.
     */
    protected abstract long processData(ByteBuffer buffer) throws IOException;

    /**
     * Write a buffer of index items.
     */
    protected abstract long bufferedIndexWrite(ByteBuffer buffer) throws IOException;

    /**
     * Write a buffer of data.
     */
    protected abstract long bufferedDataWrite(ByteBuffer buffer) throws IOException;


    /**
     * Write a buffer of data.
     * This flushes out large buffers a slice at a time.
     */
    protected long bufferedWrite(ByteBuffer buffer, FileChannel channel, ByteBuffer flushBuffer) throws IOException {
        long written = 0;
        int origLimit = buffer.limit();
        ByteBuffer slice = null;

        
        while (buffer.hasRemaining()) {



            /*
            System.err.println("flushBuffer() FB(P) = " + flushBuffer.position() +
                           " FB(C) = " + flushBuffer.capacity() +
                           " B(P) = " + buffer.position() + 
                           " B(L) = " + buffer.limit() +
                           " B(C) = " + buffer.capacity());
            */

            // no of bytes available in flushBuffer
            int available = flushBuffer.capacity() - flushBuffer.position();
            // no of bytes to place
            int todo = buffer.limit() - buffer.position();

            // if the flushBuffer is too full to take the specified buffer
            if (todo > available) {
                // take some bytes from the input buffer
                // set the limit to be the amount available
                buffer.limit(buffer.position() + available);

                // take a slice
                slice = buffer.slice();

                //  put the slice in
                flushBuffer.put(slice);
                
                // this should have filled the flushBuffer
                // so flush the buffer
                written += flushBuffer(channel, flushBuffer);

                // adjust the pointers into the buffer
                buffer.position(buffer.limit());
                buffer.limit(origLimit);
            } else {

                //  put the new contents in
                flushBuffer.put(buffer);
            }

        
        }

	return written;
    }


    /**
     * Actually flush the buffer out.
     * Returns how man bytes were written.
     */
    protected long flushBuffer(FileChannel channel, ByteBuffer flushBuffer)  throws IOException {
	long written = 0;

	if (flushBuffer.position() > 0) {
	    flushBuffer.flip();
	    written = channel.write(flushBuffer);

	    // clear it
	    flushBuffer.clear();

	}

	return written;
    }

    /**
     * Read the contents of the item
     * It assumes the index file is alreayd open for writing.
     * @param offset the byte offset in the file to start reading an item from
     * @param withData read the data for this IndexItem if withData is true,
     * the data needs to be read at a later time, otherwise
     */
    public ManagedIndexItem readItem(Offset offset, boolean withData) throws IOException {
	return readItem(offset.value(), withData);
    }

    /**
     * Read the contents of the item
     * It assumes the index file is alreayd open for writing.
     * @param offset the byte offset in the file to start reading an item from
     * @param withData read the data for this IndexItem if withData is true,
     * the data needs to be read at a later time, otherwise
     */
    public ManagedIndexItem readItem(long position, boolean withData) throws IOException {
	// tmp var for reading index item values
	Timestamp indexTS = null;
	Timestamp dataTS = null;
	DataAbstraction data = null;
	long offset = -1;
	long size = 0;
	int type = DataType.NOTSET;
	long id = 0;
	long annotationID = 0;
	ManagedFileIndexItem indexItem = null;
	
	// goto a position in the index
	seekToIndex(position);

	// where are we in the index file
	long currentIndexPosition = indexChannelPosition;

	// read an IndexItem into indexBufRead
	readIndexItem(currentIndexPosition);

	// we read the right amount, so carry on

	indexTS = timestampDecoder.decode(indexBufRead.getLong());
	dataTS = timestampDecoder.decode(indexBufRead.getLong());
	offset = indexBufRead.getLong();
	size = indexBufRead.getLong();
	type = indexBufRead.getInt();
	id = indexBufRead.getLong();
	annotationID = indexBufRead.getLong();

	if (withData) {	// go and get the data now, if it's needed
	    // TODO: add code that checks how big the data
	    // actually is.
	    // only read it if the index isn't too big
	    // and the data isn't too big
	    ByteBuffer buffer = readData(offset, size);

	    // we got the data successfully, so build a DataHolderObject
	    data = new DataHolderObject(buffer, new Size(size));
	} else {	    // don;t get the data now
	    skipData(offset, size);

	    // no need to get the  data, so build a DataReferenceObject
	    data = new DataReferenceObject(new Offset(offset), new Size(size));
	}

	indexItem = new FileIndexItem(dataTS, indexTS, data, DataTypeDirectory.find(type), new SID(id), new SID(annotationID));

	// tell the IndexItem where its index is
	indexItem.setIndexOffset(new Offset(currentIndexPosition));

	indexItem.setDataOffset(new Offset(offset));


	//System.err.println("Item size = (52 + " + size + ")");
			   
	return indexItem;
    }

    /**
     * Read an IndexItem given an offset.
     * @param offset the byte offset in the file to start reading an item from
     */
    public ByteBuffer readIndexItem(Offset offset, long size) throws IOException {
	return readIndexItem(offset.value());
    }

    /**
     * Read an IndexItem given an offset.
     * @param offset the byte offset in the file to start reading an item from
     */
    public ByteBuffer readIndexItem(long offset) throws IOException {
	int readCount = 0;


	// goto a position in the index
	seekToIndex(offset);

	//System.err.println("P(R) = " + indexChannel.position());

	// clear the index buf
	indexBufRead.clear();

	// read a block of data
	if ((readCount = indexChannel.read(indexBufRead)) != INDEX_ITEM_SIZE) {
	    throw new IOException("Index Item too short: position = " +
				  offset + " read count = " + readCount);
	}

	// make buffer ready to get data from
	indexBufRead.flip();

	// update indexChannelPosition
	indexChannelPosition += readCount;

	return indexBufRead;
    }

    /**
     * Read some data, given an offset and a size.
     * @param offset the byte offset in the file to start reading an item from
     * @param size the number of bytes to read
     */
    public ByteBuffer readData(Offset offset, long size) throws IOException {
	return readData(offset.value(), size);
    }


    /**
     * Read some data, given an offset and a size.
     * @param offset the byte offset in the file to start reading an item from
     * @param size the number of bytes to read
     */
    public ByteBuffer readData(long offset, long size) throws IOException {
	ByteBuffer buffer = null;
	long readCount = 0;

	if (size >= Integer.MAX_VALUE) {
	    // buffers can only be so big
	    // check we can allocate one big enough
		throw new Error("InlineIndexIO: readItem() has not YET implemented reading of data > " + Integer.MAX_VALUE + ". Actual size is " + size);
	} else if (size <= 4096) {
	    // the data is less than a page size so read it
	    // allocate a buffer
	    buffer = ByteBuffer.allocate((int)size);

	    // seek to the right place
	    seekToData(offset);

	    readCount = readDataIntoBuffer(buffer, size);

	    buffer.limit((int)size);
	    buffer.position(0);

	    return buffer;

	} else {
	    // the data is bigger than a page size
	    // so its better to
	    // getting data using memory mapping
	    buffer = memoryMapData(offset, size);
	    seekToData(offset+size);
	    return buffer;

	}
    }


    /**
     * Actually read in the data.
     */
    protected abstract long readDataIntoBuffer(ByteBuffer buffer, long size) throws IOException;

    /**
     * Read some data, given a DataReference.
     */
    public ByteBuffer readData(DataReference ref) throws IOException {
	long offset = ref.getOffset().value();
	long size = ref.getSize().value();

	return readData(offset, size);	
    }

    /**
     * Memory map some data from a channel.
     */
    protected abstract ByteBuffer memoryMapData(long offset, long size) throws IOException ;


    /**
     * Seek to a certain position.
     * @return true if actually had to move the position,
     * returns false if in correct place
     */
    protected boolean seekToIndex(Offset offset) throws IOException {
	return seekToIndex(offset.value());
    }

   /**
     * Seek to a certain position.
     * @return true if actually had to move the position,
     * returns false if in correct place
     */
    protected  abstract boolean seekToIndex(long position) throws IOException;

    /**
     * Seek to a certain position.
     * @return true if actually had to move the position,
     * returns false if in correct place
     */
    protected boolean seekToData(Offset offset) throws IOException {
	return seekToData(offset.value());
    }

    /**
     * Seek to a certain position in the data file.
     * @return true if actually had to move the position,
     * returns false if in correct place
     */
    protected abstract boolean seekToData(long position) throws IOException;

    /**
     * Skip over some data, given an offset and a size.
     * @param offset the byte offset in the file to start reading an item from
     * @param size the number of bytes to read
     */
    public boolean skipData(long offset, long size) throws IOException {
	// skip to right place
	seekToData(offset + size);

	return true;
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
     * Load the index
     */
    public long loadIndex(LoadStyle loadStyle) throws IOException {

	if (loadStyle == LoadStyle.ALL) {
	    indexAppendPosition = loadAll(true);
	    setAppendPosition();
	    return indexAppendPosition;

	} else if (loadStyle == LoadStyle.HOLLOW) {
	    indexAppendPosition = loadAll(false);
	    setAppendPosition();
	    return indexAppendPosition;

	} else if (loadStyle == LoadStyle.NONE) {
	    // where is last item
	    Offset lastOffset = headerInteractor.getLastOffset();

            if (lastOffset.value() == 0) {
		 // the index has zero items
		// so there is nothing to read
		gotoFirstPosition();
                setAppendPosition();
	    } else {
		// get last item
                // lastOffset points to just before the last IndexItem
		ManagedFileIndexItem item = (ManagedFileIndexItem)readItem(lastOffset.value(), false);

		// work out append position
		// from index data offset + data size
		long appendPoint = item.getDataOffset().value() + item.getDataSize().value();

		// set append position
		indexAppendPosition = appendPoint;
	    }

	    return indexAppendPosition;

	} else {
	    throw new RuntimeException("Unknow LoadStyle" + loadStyle);
	}
    }

    /**
     * Load all of the items.
     * @return the position in the index after loading all the items
     */
    private long loadAll(boolean doLoadData) throws IOException {
	ManagedIndexItem item = null;
	long position = indexChannelPosition;
	long itemCount = headerInteractor.getLength();
	Offset lastOffset = headerInteractor.getLastOffset();
	long count = 0;


	// we have just read the header
	// so we are going to read all the items
	
        if (lastOffset.value() == 0) {
	    // the index has zero items
	    // so there is nothing to read
	    gotoFirstPosition();
	} else {
	    for (count=0; count < itemCount; count++) {
		// read an item
		item =  readItem(position, doLoadData);

		// set the position for next time
		position = indexChannelPosition;

		// post the read item into the index
		// this is the Index callback
		myIndex.retrieveItem(item);
	    }
	}

	return indexChannelPosition;

    }

    /**
     * Get the append position
     */
    public long getAppendPosition() {
	return indexAppendPosition;
    }

    /**
     * Goto the append position
     */
    public boolean gotoAppendPosition() throws IOException {
	return seekToIndex(indexAppendPosition);
    }

    /**
     * Goto the first position
     */
    public boolean gotoFirstPosition() throws IOException {
	return seekToIndex(indexFirstPosition);
    }

    /**
     * Set the append position from the indexChannelPosition.
     */
    public boolean setAppendPosition() throws IOException {
	//System.err.println("Index append position = " + indexChannelPosition);
	indexAppendPosition = indexChannelPosition;

	return true;
    }

    /**
     * Set the index item size.
     * The size is determined by the header I/O object
     * at index create time.
     * Return the old index item size in bytes.
     */
    protected IndexFileInteractor setItemSize(int itemSize) {	
	INDEX_ITEM_SIZE = itemSize;
	return this;
    }


}
