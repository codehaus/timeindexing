// IndexDecoder.java

package com.timeindexing.io;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Offset;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexHeader;
import com.timeindexing.index.ManagedIndexHeader;
import com.timeindexing.index.DefaultIndexHeader;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.DataType;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimestampDecoder;
import com.timeindexing.time.Clock;

import com.timeindexing.index.InlineIndex;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;


/**
 * This opens an Index header file and determines what type of
 * of index is represented.
 */
public class IndexDecoder extends DefaultIndexHeader implements ManagedIndexHeader, IndexHeaderReader {
    String openMode = "r";

    String fileName = null;
    RandomAccessFile headerFile = null;
    FileChannel channel = null;
    ByteBuffer headerBuf = null;

    TimestampDecoder timestampDecoder = new TimestampDecoder();


    /*
     * The size of an index item in the index file
     */
    int INDEX_ITEM_SIZE = 52;


    /**
     * Construct a decoder.
     */
    protected IndexDecoder() {
    }

    /**
     * Construct a decoder.
     */
    public IndexDecoder(String fileName) throws IOException  {
	itemSize = INDEX_ITEM_SIZE;
	open(fileName);
    }

    /**
     * Construct a decoder.
     */
    public IndexDecoder(File file) throws IOException  {
	open(file.getCanonicalPath());
    }


    /**
     * Get the major version no.
     */
    public int getVersionMajor() {
	return versionMajor;
    }

    /**
     * Get the minor version no.
     */
    public int getVersionMinor() {
	return versionMinor;
    }

    /**
     * Get the index type.
     */
    public int getIndexType() {
	return indexType;
    }



    /**
     * Return the index item size in bytes.
     * This may vary once special indexes are implemented.
     */
    public int getItemSize() {
	return itemSize;
    }

    /**
     * Open an index header  to read it.
     */
    public boolean open(String filename) throws IOException {
	fileName = filename;

	return open();
    }

    /**
     * Open an index header  to read it.
     */
    public boolean open() throws IOException {
	try {
	    String actualFileName = null;

	    // encure filename ends in .tih
	    if (fileName.endsWith(".tih")) {
		actualFileName = fileName;
	    } else {
		actualFileName = fileName + ".tih";
	    }
	    File file = new File(actualFileName);

	    headerFile = new RandomAccessFile(file, openMode);

	    channel = headerFile.getChannel();

	} catch (FileNotFoundException fnfe) {
	    throw new IOException("Could not open header file: " + fileName);
	}

	return true;
	
    }


    /**
     * Is the IndexHeader open
     */
    public boolean isOpen() {
	if (headerFile == null) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Operation on close
     */
    public long close() throws IOException {
	long size = -1;

	size = channel.size();

	channel.close();

	return size;
    }

 
   /**
     * Read an index header from the header stream.
     * TODO:  Use Channels and ByteBuffer
     */
    public long read() throws IOException {
	try { 
	    headerFile.seek(0);

	    // The first two bytes are T & I
	    byte byteT = headerFile.readByte();
	    byte byteI = headerFile.readByte();
	    // byte 3 = 0x03
	    byte three = headerFile.readByte();
	    byte type = headerFile.readByte();

	    // check first 4 bytes
	    if (byteT == 'T' &&
		byteI == 'I' &&
		three == 0x03 &&
		type == 0x01) {
		// we've opened a TimeIndex Header

	    
		// get the version no
		versionMajor = (int)headerFile.readByte();
		versionMinor = (int)headerFile.readByte();

		// read index ID
		indexID = new SID(headerFile.readLong());

		// read the Index name
		short nameSize = headerFile.readShort();
		byte[] nameRaw = new byte[nameSize-1];
		headerFile.readFully(nameRaw, 0, nameSize-1);
		indexName = new String(nameRaw);

		// soak up index name padding
		headerFile.readByte();

		// get the index type
		indexType = (int)headerFile.readByte();

		long aTime = 0;

		// startTime
		aTime = headerFile.readLong();
		startTime = timestampDecoder.decode(aTime);

		// endTime
		aTime = headerFile.readLong();
		endTime = timestampDecoder.decode(aTime);


		// firstTime
		aTime = headerFile.readLong();
		firstTime = timestampDecoder.decode(aTime);

		// lastTime
		aTime = headerFile.readLong();
		lastTime = timestampDecoder.decode(aTime);


		// firstDataTime
		aTime = headerFile.readLong();
		firstDataTime = timestampDecoder.decode(aTime);

		// lastDataTime
		aTime = headerFile.readLong();
		lastDataTime = timestampDecoder.decode(aTime);

		// item size
		itemSize = headerFile.readInt();

		// data size
		dataSize = headerFile.readLong();

		// length
		length = headerFile.readLong();

		// first offset
		firstOffset = new Offset(headerFile.readLong());

		// last offset
		lastOffset = new Offset(headerFile.readLong());

		// last offset
		// terminated
		byte terminatedByte = headerFile.readByte();
		if (terminatedByte > 0) {
		    terminated = true;
		} else {
		    terminated = false;
		}

		return headerFile.getFilePointer();
	    } else {
		throw new IOException(fileName + " is not a TimeIndex Header");
	    }
	} catch (EOFException eofe) {
	    System.err.println("EOF at position: " + channel.position());
	    System.err.println(this);
	    throw eofe;
	}
    }

}
