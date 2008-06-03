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
import java.util.Map;
import java.util.HashMap;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.net.URI;

/**
 * This opens an Index header file and determines what type of
 * of index is represented.
 */
public class IndexDecoder extends DefaultIndexHeader implements ManagedIndexHeader, IndexHeaderReader {
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
	setItemSize(INDEX_ITEM_SIZE);
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

	    String openMode = "r";

	    headerFile = new RandomAccessFile(file, openMode);

	    channel = headerFile.getChannel();

	} catch (FileNotFoundException fnfe) {
	    throw new IOException("Could not open header file: " + fileName);
	}

	return true;
	
    }



    /**
     * Does the index header file exist
     */
    public boolean exists(String filename) {
	String fileName = FileUtils.resolveFileName(filename, ".tih");
	File file = new File(fileName);

	if (file.exists()) {
	    return true;
	} else {
	    return false;
	}
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
	long size = 0;

	if (channel != null) {
	    size = channel.size();

	    channel.close();
	}

	return size;
    }

 
   /**
     * Read an index header from the header stream.
     */
    public long read() throws IOException {
	// seek(0);
	channel.position(0);
	
	return readFromChannel(channel, getHeaderLength());
    }
	    
   /**
     * Read an index header from the header stream.
     */
    public long readFromChannel(FileChannel channel, long headerSize) throws IOException {
	try { 
	    long readCount = 0;

	    // memory map the header 
	    ByteBuffer readBuf = ByteBuffer.allocate((int)headerSize);

	    // read a block of data
	    if ((readCount = channel.read(readBuf)) != headerSize) {
		throw new IOException("Header read failure. Didn't get " + headerSize + " bytes.");
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
		setVersionMajor((int)readBuf.get());
		setVersionMinor((int)readBuf.get());

		// read index ID
		setID(new SID(readBuf.getLong()));

		// read the Index name
		short nameSize = readBuf.getShort();
		byte[] nameRaw = new byte[nameSize-1];
		readBuf.get(nameRaw, 0, nameSize-1);
		setName(new String(nameRaw));

		// soak up index name padding
		readBuf.get();

		// get the index type
		int indexTypeValue = (int)readBuf.get();

		switch (indexTypeValue) {
		    case IndexType.INLINE_VALUE: {
			setIndexType(IndexType.INLINE);
			break;
		    }
		    case IndexType.EXTERNAL_VALUE: {
			setIndexType(IndexType.EXTERNAL);
			break;
		    }
		    case IndexType.SHADOW_VALUE: {
			setIndexType(IndexType.SHADOW);
			break;
		    }
		    case IndexType.INCORE_VALUE: {
			throw new IOException("Header read failure. Got unexpected IndexType: INCORE");
		    }
		    default: {
			throw new IOException("Header read failure. Got unexpected IndexType: " + indexTypeValue);
		    }
		}


		long aTime = 0;

		// startTime
		aTime = readBuf.getLong();
		setStartTime(timestampDecoder.decode(aTime));

		// endTime
		aTime = readBuf.getLong();
		setEndTime(timestampDecoder.decode(aTime));


		// firstTime
		aTime = readBuf.getLong();
		setFirstTime(timestampDecoder.decode(aTime));

		// lastTime
		aTime = readBuf.getLong();
		setLastTime(timestampDecoder.decode(aTime));


		// firstDataTime
		aTime = readBuf.getLong();
		setFirstDataTime(timestampDecoder.decode(aTime));

		// lastDataTime
		aTime = readBuf.getLong();
		setLastDataTime(timestampDecoder.decode(aTime));

		// item size
		setItemSize(readBuf.getInt());

		// data size
		setDataSize(readBuf.getLong());

		// length
		setLength(readBuf.getLong());

		// first offset
		setFirstOffset(new Offset(readBuf.getLong()));

		// last offset
		setLastOffset(new Offset(readBuf.getLong()));

		// terminated
		byte terminatedByte = readBuf.get();
		if (terminatedByte > 0) {
		    setTerminated(true);
		} else {
		    setTerminated(false);
		}

		/* now process the optional values */

		HeaderOption anOption = null;
		Object value = null;

		while (readBuf.position() < headerSize) {
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

		     case HeaderOption.NO_DATA_FILE_HEADER: {
			 value = processIsInTimeOrder(HeaderOptionProcess.READ, readBuf);
			 anOption = HeaderOption.NO_DATA_FILE_HEADER_HO;

			 // if the value is true
			 // then there is no data file header
			 if (((Boolean)value).booleanValue() == true) {
			     setOption(anOption, Boolean.TRUE);
			 }

			 break;
		     }

		     case HeaderOption.REFERENCEMAPPING: {
			 value = processReferenceMapping(HeaderOptionProcess.READ, readBuf);
			 anOption = HeaderOption.REFERENCEMAPPING_HO;

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

		return headerSize;
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
     * Determine the length of a Header
     */
    protected long getHeaderLength() throws IOException {
	return headerFile.length();
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
	    inOrder = Boolean.valueOf(true);
	} else {
	    inOrder = Boolean.valueOf(false);
	}

	return inOrder;
    }

    /**
     * Is there a header on the data file
     */
    protected Boolean  processNoDataFileHeader(HeaderOptionProcess what, ByteBuffer readBuf) {
	// the buffer is positioned just after the option byte

	// read the status
	byte value = readBuf.get();

	Boolean noDataHeader = null;

	if (value == 1) {
	    noDataHeader = Boolean.valueOf(true);
	} else {
	    noDataHeader = Boolean.valueOf(false);
	}

	return noDataHeader;
    }

    /**
     * Process the referenced indexes mapping
     */
    protected Object processReferenceMapping(HeaderOptionProcess what, ByteBuffer readBuf) {
	// the buffer is positioned just after the option byte

	// the no of entries
	short size = readBuf.getShort();

	Map referenceMap = new HashMap();

	for (int done = 0; done < size; done++) {
	    // get the ID
	    long id = readBuf.getLong();
	    // get the URI name
	    short nameSize = readBuf.getShort();
	    byte[] nameRaw = new byte[nameSize-1];
	    readBuf.get(nameRaw, 0, nameSize-1);
	    readBuf.get();  // get NUL

	    //System.err.println("IndexDecoder: reference " + id + " => " + new String(nameRaw));
	    
	    referenceMap.put(new SID(id), URI.create(new String(nameRaw)));

	}

	return referenceMap;
    }
}
