// InlineIndexInteractorImpl.java

package com.timeindexing.io;

import com.timeindexing.index.DataType;
import com.timeindexing.index.Index;
import com.timeindexing.index.ManagedIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.FileIndexItem;
import com.timeindexing.index.DataAbstraction;
import com.timeindexing.index.DataHolderObject;
import com.timeindexing.index.DataReference;
import com.timeindexing.index.DataReferenceObject;
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
public class InlineIndexInteractorImpl implements InlineIndexInteractor, IndexPrimaryEventListener, IndexAddEventListener { // , IndexAccessEventListener {
    ManagedIndex myIndex = null;
    String fileName = null;
    RandomAccessFile indexFile = null;
    FileChannel channel = null;
    ByteBuffer headerBuf = null;
    ByteBuffer indexBufWrite = null;
    ByteBuffer indexBufRead = null;
    ByteBuffer flushBuffer = null;
    TimestampDecoder timestampDecoder = new TimestampDecoder();

    int versionMajor = 0;
    int versionMinor = 0;
    String indexName = null;
    ID indexID = null;
    int dataType = DataType.NOTSET;
    
    long channelPosition = 0;

    /*
     * The size of a header
     */
    final static int HEADER_SIZE = 512;

    /*
     * The size of a index 
     */
    final static int INDEX_SIZE = 52;


    /*
     * The size of a flush buffer
     */
    final static int FLUSH_SIZE = 1 * 1024;



    /**
     * Construct an Inline Index.
     */
    public InlineIndexInteractorImpl(ManagedIndex indexMgr) {
	myIndex = indexMgr;
	headerBuf = ByteBuffer.allocate(HEADER_SIZE);
	indexBufWrite = ByteBuffer.allocate(INDEX_SIZE);
	indexBufRead = ByteBuffer.allocate(INDEX_SIZE);
	flushBuffer = ByteBuffer.allocate(FLUSH_SIZE);
    }

    /**
     * Operation on creation.
     */
    public long create(String filename) throws IOException {
	fileName = filename;
	indexName = myIndex.getName();


	open();

	long position = writeHeader();

	return position;
    }

    /**
     * Open an index file  to read it.
     */
    public long open(String filename) throws IOException {
	fileName = filename;

	open();

	long position = readHeader();

	return position;

    }

    /**
     * Open an index   to read it.
     */
    protected long open() throws IOException {
	try {
	    String actualFileName = null;

	    // encure filename ends in .tih
	    if (fileName.endsWith(".tii")) {
		actualFileName = fileName;
	    } else {
		actualFileName = fileName + ".tii";
	    }
	    File file = new File(actualFileName);

	    indexFile = new RandomAccessFile(file, "rw");
	    channel = indexFile.getChannel();

	    //System.err.println("InlineIndexInteractorImpl: opened \"" + actualFileName + "\"");

	} catch (FileNotFoundException fnfe) {
	    throw new IOException("Could not open index file: " + fileName);
	}

	return 0;
	
    }

    /**
     * Read an index header from the header stream.
     */
    public long readHeader() throws IOException {
	// seek to start
	indexFile.seek(0);

	// The first two bytes are T & I
	byte byteT = indexFile.readByte();
	byte byteI = indexFile.readByte();
	// byte 3 = 0x03
	byte three = indexFile.readByte();
	byte type = indexFile.readByte();

	// check first 4 bytes
	if (byteT == FileType.T &&
	    byteI == FileType.I &&
	    three == FileType.BYTE_3 &&
	    type == FileType.INLINE_INDEX) {
	    // we've opened a TimeIndex Header

	    
	    // get the version no
	    versionMajor = (int)indexFile.readByte();
	    versionMinor = (int)indexFile.readByte();

	    // get the data type
	    dataType = (int)indexFile.readByte();

	    // read the Index name
	    short nameSize = indexFile.readShort();
	    byte[] nameRaw = new byte[nameSize-1];
	    indexFile.readFully(nameRaw, 0, nameSize-1);
	    indexName = new String(nameRaw);

	    // soak up index name padding
	    indexFile.readByte();

	    //System.err.println("Inline Index Header read size = " + indexFile.getFilePointer());
	
	}

	channelPosition = channel.position(); //indexFile.getFilePointer();
	return channelPosition;
    }


    /**
     * Write the contents of the header out
     * It assumes the index file is alreayd open for writing.
     */
    public long writeHeader() throws IOException {
	// seek to start
	indexFile.seek(0);

	// clear the header buffer
	headerBuf.clear();

	// fill the buffer with the bytes

	// TimeIndex Header magic
	headerBuf.put(FileType.T);
	headerBuf.put(FileType.I);
	headerBuf.put(FileType.BYTE_3);
	headerBuf.put(FileType.INLINE_INDEX);

	// version major
	headerBuf.put((byte)versionMajor);
	// version minor
	headerBuf.put((byte)versionMinor);

	// dataType
	headerBuf.put((byte)dataType);

	// size of name, +1 for null terminator
	headerBuf.putShort((short)(indexName.length()+1));

	// the name
	headerBuf.put(indexName.getBytes());
	// plus null terminator
	headerBuf.put((byte)0x00);

	// now write it out
	headerBuf.flip();
	long writeCount = channel.write(headerBuf);

	channelPosition = channel.position();

	
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

	// where are we in the file
	long currentPosition = channelPosition;  // channel.position();

	//System.err.println("P(W) = " + channelPosition);

	// tell the IndexItem where its index is
	item.setIndexOffset(new Offset(currentPosition));

	// an index item is INDEX_SIZE bytes, so
	// in an inline index the data will start at
	// currentPosition + INDEX_SIZE
	long dataPos = currentPosition + INDEX_SIZE;

	// tell the IndexItem where its data is
	item.setDataOffset(new Offset(dataPos));

	// clear the index buf
	indexBufWrite.clear();

	// fill the buffer
	indexBufWrite.putLong(item.getIndexTimestamp().value());
	indexBufWrite.putLong(item.getDataTimestamp().value());
	indexBufWrite.putLong(dataPos);
	indexBufWrite.putLong(item.getDataSize().value());
	indexBufWrite.putInt(item.getDataType());
	indexBufWrite.putLong(item.getItemID().value());
	indexBufWrite.putLong(item.getAnnotations().value()); // TODO: fix annoation code

	// make it ready for writing
	indexBufWrite.flip();

	count += bufferedWrite(indexBufWrite);
	channelPosition += INDEX_SIZE;
	
	count += bufferedWrite(item.getData());
	channelPosition += item.getDataSize().value();

	
	return count;
    }


    public long bufferedWrite(ByteBuffer buffer) throws IOException {
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
		written += flushBuffer();

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
    public long flushBuffer()  throws IOException {
	long written = 0;

	if (flushBuffer.position() > 0) {
	    flushBuffer.flip();
	    written = channel.write(flushBuffer);

	    // clear it
	    flushBuffer.clear();

	    //System.err.println("flushBuffer() wrote: " + written);
	}

	return written;
    }

    /**
     * Read the contents of the item
     * It assumes the index file is alreayd open for writing.
     * @param position the byte offset in the file to start reading an item from
     * @param withData read the data for this IndexItem if withData is true,
     * the data needs to be read at a later time, otherwise
     */
    public ManagedIndexItem readItem(long position, boolean withData) throws IOException {
	// where are we in the file
	long currentPosition = channelPosition; // channel.position();
	int readCount = 0;

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
	

	seek(position);

	//System.err.println("P(R) = " + currentPosition);


	// an index item is INDEX_SIZE bytes, so
	// in an inline index the data will start at
	// currentPosition + INDEX_SIZE
	long dataPos = currentPosition + INDEX_SIZE;

	// clear the index buf
	indexBufRead.clear();

	// read a block of data
	if ((readCount = channel.read(indexBufRead)) != INDEX_SIZE) {
	    throw new IOException("Index Item too short: position = " +
				  currentPosition + " read count = " + readCount);
	}

	channelPosition += readCount;

	// we read the right amount, so carry on

	indexBufRead.flip();
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
	    // no need to get the  data, so build a DataReferenceObject
	    data = new DataReferenceObject(new Offset(offset), new Size(size));
	    // skip to right place
	    seek(offset + size);
	}

	indexItem = new FileIndexItem(dataTS, indexTS, data, type, new SID(id), new SID(annotationID));

	// tell the IndexItem where its index is
	indexItem.setIndexOffset(new Offset(currentPosition));

	indexItem.setDataOffset(new Offset(dataPos));


	//System.err.println("Item size = (52 + " + size + ")");
			   
	return indexItem;
    }


    /**
     * Read some data, given an offset and a size.
     */
    public ByteBuffer readData(long offset, long size) throws IOException {
	ByteBuffer buffer = null;
	int readCount = 0;


	// buffers can only be so big
	// check we can allocate one big enough
	if (size > Integer.MAX_VALUE) {
	    throw new Error("InlineIndexInteractorImpl: readItem() has not implemented reading of data > " + Integer.MAX_VALUE + ". Actual size is " + size);
	    // TODO: implement proper reading of data
	} else {

	    // allocate a buffer
	    buffer = ByteBuffer.allocate((int)size);
	    // seek to the right place
	    seek(offset);

	    // read the data of index item
	    if ((readCount = channel.read(buffer)) != size) {
		throw new IOException("Index Item Data too short: position = " +
				      channel.position() + " read count = " + readCount);
	    }

	    channelPosition += readCount;
	}

	return buffer;
    }


    /**
     * Read some data, given a DataReference.
     */
    public ByteBuffer readData(DataReference ref) throws IOException {
	long offset = ref.getOffset().value();
	long size = ref.getSize().value();

	return readData(offset, size);	
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
    public boolean seek(long position) throws IOException {
	if (channelPosition != position) {
	    // we need to seek to a different place
	    channel.position(position);
	    channelPosition = position;
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Load the index
     */
    public long loadIndex(int loadStyle) throws IOException {
	ManagedIndexItem item = null;
	long position = channelPosition;
	long itemCount = myIndex.length();
	long count = 0;
	boolean doLoadData = false;

	switch (loadStyle) {
	case LoadStyle.ALL:
	    doLoadData = true;
	    break;

	case LoadStyle.HOLLOW:
	    doLoadData = false;
	    break;

	default:
	    return 0;
	}

	// if we have fallen through to here
	// then we need to load some index items
	    
	// we have just read the header
	// so we are going to read all the items
	while(count < itemCount) {
	    // read an item
	    item =  readItem(position, doLoadData);
	    // set the position for next time
	    //position = channel.position();
	    position = channelPosition;
	    // post the read item into the index
	    myIndex.retrieveItem(item);
	    // incrments the count
	    count++;
	}

	return count;
    }

    /**
     * Operation on flush.
     * Returns how many bytes were written.
     */
    public long flush() {
	long written = 0;

	try {
	    written = flushBuffer();
	    return written;
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	} finally {
	    return written;
	}

    }

    /**
     * Operation on close
     */
    public long close() {
	long size = -1;
	try {
	    long lastWrite = flush();

	    //System.err.println("InlineIndexInteractorImpl: at close wrote = " + lastWrite);

	    size = channel.size();

	    //System.err.println("InlineIndexInteractorImpl: size at close = " + size);

	    channel.close();

	} catch (IOException ioe) {
	    ioe.printStackTrace();
	} finally {
	    return size;
	}
    }

    /**
     * A notification that an Index has been opened.
     */
    public  void opened(IndexPrimaryEvent ipe) {
	ManagedIndex index = (ManagedIndex)ipe.getSource();
	//System.err.println("InlineIndexInteractorImpl opened: IndexPrimaryEvent source = " + index.getName());

	indexName = ipe.getName();
	indexID = ipe.getID();	
    }

    /**
     * A notification that an Index has been closed.
     */
    public  void closed(IndexPrimaryEvent ipe) {
	Index index = (Index)ipe.getSource();
	//System.err.println("InlineIndexInteractorImpl: closed() IndexPrimaryEvent source = " + index.getName());

    }

    /**
     * A notification that an Index has been flushed.
     */
    public  void flushed(IndexPrimaryEvent ipe) {
	flush();
    }

    /**
     * A notification that an Index has been created.
     */
    public  void created(IndexPrimaryEvent ipe) {
	Index index = (Index)ipe.getSource();
	//System.err.println("InlineIndexInteractorImpl: IndexPrimaryEvent source = " + index.getName());

	indexName = ipe.getName();
	indexID = ipe.getID();

    }

    /**
     * A notification that an IndexItem has been added to an Index.
     */
    public void itemAdded(IndexAddEvent iae) {
	Index index = (Index)iae.getSource();
	//System.err.println("InlineIndexInteractorImpl: IndexAddEvent source = " + index.getName());

	IndexItem item = iae.getIndexItem();

	try {
	    writeItem((ManagedIndexItem)item);
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
    }
}
