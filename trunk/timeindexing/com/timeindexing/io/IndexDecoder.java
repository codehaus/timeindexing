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
import com.timeindexing.index.DataTypeDirectory;
import com.timeindexing.index.Description;
import com.timeindexing.index.HeaderOption;
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

    String originalFileSpecifier = null;
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
    public IndexDecoder(String filename) throws IOException  {
	itemSize = INDEX_ITEM_SIZE;
	open(filename);
    }

    /**
     * Construct a decoder.
     */
    public IndexDecoder(File file) throws IOException  {
	open(file.getCanonicalPath());
    }

    /**
     * Get the header path name.
     */
    public String getHeaderPathName() {
	return fileName;
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
	originalFileSpecifier = filename;

	return open();
    }

    /**
     * Open an index header  to read it.
     */
    public boolean open() throws IOException {
	try {
	    fileName = FileUtils.resolveFileName(originalFileSpecifier, ".tih");
	    File file = new File(fileName);

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
	    long  fileSize = headerFile.length();
	    long readCount = 0;

	    // memory map the header 
	    ByteBuffer readBuf = ByteBuffer.allocate((int)fileSize);

	    // seek(0);
	    channel.position(0);

	    // read a block of data
	    if ((readCount = channel.read(readBuf)) != fileSize) {
		throw new IOException("Header read failure. Didn't get " + fileSize + " bytes.");
	    }

	    readBuf.flip();

	    /* read the complusory parts of the header */

	    // The first two bytes are T & I
	    byte byteT = readBuf.get();
	    byte byteI = readBuf.get();
	    // byte 3 = 0x03
	    byte three = readBuf.get();
	    byte type = readBuf.get();

	    // check first 4 bytes
	    if (byteT == 'T' &&
		byteI == 'I' &&
		three == 0x03 &&
		type == 0x01) {
		// we've opened a TimeIndex Header

	    
		// get the version no
		versionMajor = (int)readBuf.get();
		versionMinor = (int)readBuf.get();

		// read index ID
		indexID = new SID(readBuf.getLong());

		// read the Index name
		short nameSize = readBuf.getShort();
		byte[] nameRaw = new byte[nameSize-1];
		readBuf.get(nameRaw, 0, nameSize-1);
		indexName = new String(nameRaw);

		// soak up index name padding
		readBuf.get();

		// get the index type
		indexType = (int)readBuf.get();

		long aTime = 0;

		// startTime
		aTime = readBuf.getLong();
		startTime = timestampDecoder.decode(aTime);

		// endTime
		aTime = readBuf.getLong();
		endTime = timestampDecoder.decode(aTime);


		// firstTime
		aTime = readBuf.getLong();
		firstTime = timestampDecoder.decode(aTime);

		// lastTime
		aTime = readBuf.getLong();
		lastTime = timestampDecoder.decode(aTime);


		// firstDataTime
		aTime = readBuf.getLong();
		firstDataTime = timestampDecoder.decode(aTime);

		// lastDataTime
		aTime = readBuf.getLong();
		lastDataTime = timestampDecoder.decode(aTime);

		// item size
		itemSize = readBuf.getInt();

		// data size
		dataSize = readBuf.getLong();

		// length
		length = readBuf.getLong();

		// first offset
		firstOffset = new Offset(readBuf.getLong());

		// last offset
		lastOffset = new Offset(readBuf.getLong());

		// last offset
		// terminated
		byte terminatedByte = readBuf.get();
		if (terminatedByte > 0) {
		    terminated = true;
		} else {
		    terminated = false;
		}

		/* now process the optional values */

		HeaderOption anOption = null;
		Object value = null;

		while (readBuf.position() < fileSize) {
		    byte optionValue = readBuf.get();

		     switch (optionValue) {
		     case HeaderOption.DESCRIPTION: {
			 value = processDescription(HeaderOptionProcess.READ, readBuf);
			 anOption = HeaderOption.DESCRIPTION_HO;

			 setDescription((Description)value);
			 break;
		     }
	    
		     case HeaderOption.INDEXPATH: {
			 value = processIndexPath(HeaderOptionProcess.READ, readBuf);
			 anOption = HeaderOption.INDEXPATH_HO;

			 setIndexPathName((String)value);
			 break;
		     }

		     case HeaderOption.DATAPATH: {
			 value = processDataPath(HeaderOptionProcess.READ, readBuf);
			 anOption = HeaderOption.DATAPATH_HO;

			 setDataPathName((String)value);
			 break;
		     }

		     case HeaderOption.DATATYPE: {
			 value = processDataType(HeaderOptionProcess.READ, readBuf);
			 anOption = HeaderOption.DATATYPE_HO;

			 setIndexDataType((DataType)value);
			 break;
		     }

		     case HeaderOption.IS_IN_TIME_ORDER: {
			 value = processIsInTimeOrder(HeaderOptionProcess.READ, readBuf);
			 anOption = HeaderOption.IS_IN_TIME_ORDER_HO;

			 // if the value is false
			 // then the index is NOT in time order
			 if (((Boolean)value).booleanValue() == false) {
			     notInTimeOrder();
			 }

			 break;
		     }

		     default: {
		     }
		     }

		     setOption(anOption, value);
		     //System.err.println("Read option " + anOption);
		}
		  

		// release the memory mapped buffer
		readBuf = null;

		return fileSize; // was headerFile.getFilePointer();
	    } else {
		throw new IOException(fileName + " is not a TimeIndex Header");
	    }
	} catch (EOFException eofe) {
	    System.err.println("EOF at position: " + channel.position());
	    System.err.println(this);
	    throw eofe;
	}
    }


    /**
     * Process a description
     */
    protected Object processDescription(HeaderOptionProcess what, ByteBuffer readBuf) {
	// the buffer is positioned just after the option byte

	// read the description
	int dataTypeID = readBuf.getInt();
	DataType dataType = DataTypeDirectory.find(dataTypeID);

	short descSize = readBuf.getShort();
	byte[] descRaw = new byte[descSize-1];
	readBuf.get(descRaw, 0, descSize-1);
	readBuf.get();  // get NUL
	return  new Description(descRaw, dataType);
    }


    /**
     * Process a index path
     */
    protected Object processIndexPath(HeaderOptionProcess what, ByteBuffer readBuf) {
	// the buffer is positioned just after the option byte

	// read the index path
	short nameSize = readBuf.getShort();
	byte[] nameRaw = new byte[nameSize-1];
	readBuf.get(nameRaw, 0, nameSize-1);
	readBuf.get();  // get NUL
	return  new String(nameRaw);
    }

    /**
     * Process a data path
     */
    protected Object processDataPath(HeaderOptionProcess what, ByteBuffer readBuf) {
	// the buffer is positioned just after the option byte

	// read the data path
	short nameSize = readBuf.getShort();
	byte[] nameRaw = new byte[nameSize-1];
	readBuf.get(nameRaw, 0, nameSize-1);
	readBuf.get();  // get NUL
	return  new String(nameRaw);
    }

    /**
     * Process a data type
     */
    protected Object processDataType(HeaderOptionProcess what, ByteBuffer readBuf) {
	// the buffer is positioned just after the option byte

	// read the data type
	int  dataTypeID = readBuf.getInt();

	short nameSize = readBuf.getShort();
	byte[] nameRaw = new byte[nameSize-1];
	readBuf.get(nameRaw, 0, nameSize-1);
	readBuf.get();  // get NUL

	DataType dataType = null;

	if ((dataType = DataTypeDirectory.find(dataTypeID)) == null) {
	    // did'nt find data type
	    // so register it
	    dataType = DataTypeDirectory.register(new String(nameRaw), dataTypeID);
	}

	return dataType;
    }

    /**
     * Is the index in time order
     */
    protected Boolean  processIsInTimeOrder(HeaderOptionProcess what, ByteBuffer readBuf) {
	// the buffer is positioned just after the option byte

	// read the status
	byte value = readBuf.get();

	Boolean inOrder = null;

	if (value == 1) {
	    inOrder = new Boolean(true);
	} else {
	    inOrder = new Boolean(false);
	}

	return inOrder;
    }
}
