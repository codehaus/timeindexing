// IndexTyper.java

package com.timeindexing.io;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.SID;
import com.timeindexing.index.IndexType;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.BufferUnderflowException;

/**
 * This opens an Index file and determines what type of
 * of index is represented.
 */
public class IndexTyper {
    String fileName = null;
    RandomAccessFile headerFile = null;
    FileChannel channel = null;
    ByteBuffer headerBuf = null;

    String indexName = null;
    ID indexID = null;
    IndexType indexType = null;

    /**
     * Construct a typer
     */
    protected IndexTyper() {
    }

    /**
     * Construct a decoder.
     */
    public IndexTyper(String filename) throws IOException  {
	boolean openResult = open(filename);

	if (openResult == false) {
	    throw new IOException("IndexTyper: " + filename + " does not exist");
	}
    }

    /**
     * Construct a decoder.
     */
    public IndexTyper(File file) throws IOException  {
	boolean openResult = open(file.getCanonicalPath());

	if (openResult == false) {
	    throw new IOException("IndexTyper: " + file.getCanonicalPath() + " does not exist");
	}
    }

    /**
     * Get the path name.
     */
    public String getPathName() {
	return fileName;
    }

    /**
     * Open an index header  to read it.
     */
    public boolean open(String filename) throws IOException {
	if (open(filename, ".tih")) {
	    // found .tih file
	    //System.err.println("IndexTyper: opened " + filename + ".tih"); 
	    return true;
	} else if (open(filename, ".tii")) {
	    // it might be an INLINE index, with no header file
	    //System.err.println("IndexTyper: opened " + filename + ".tii"); 
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Open an index header  to read it.
     */
    public boolean open(String filename, String extention) throws IOException {
	try {
	    fileName = FileUtils.resolveFileName(filename, extention);
	    File file = new File(fileName);

	    String openMode = "r";

	    headerFile = new RandomAccessFile(file, openMode);

	    channel = headerFile.getChannel();


	    return true;
	
	} catch (FileNotFoundException fnfe) {
	    return false;
	}
    }



    /**
     * Does the index header file exist
     */
    public boolean exists(String filename) {
	String fileName = FileUtils.resolveFileName(filename, ".tih");

	if (fileName == null) {
	    return false;
	} else {
	    return true;
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
		three == 0x03) {
		// we've opened a TimeIndex file

	    
		// get the version no
		readBuf.get();
		readBuf.get();

		// read index ID
		setID(new SID(readBuf.getLong()));

		// read the Index name
		short nameSize = readBuf.getShort();
		byte[] nameRaw = new byte[nameSize-1];
		readBuf.get(nameRaw, 0, nameSize-1);
		setName(new String(nameRaw));

		// soak up index name padding
		readBuf.get();

		if (type == FileType.HEADER) {
		    //it's a header, so there is more data to read
		    
		    // get the index type
		    int indexTypeValue = (int)readBuf.get();

		    switch (indexTypeValue) {
		    case IndexType.INLINE: {
			setIndexType(IndexType.INLINE_DT);
			break;
		    }
		    case IndexType.EXTERNAL: {
			setIndexType(IndexType.EXTERNAL_DT);
			break;
		    }
		    case IndexType.SHADOW: {
			setIndexType(IndexType.SHADOW_DT);
			break;
		    }
		    case IndexType.INCORE: {
			throw new IOException("Header read failure. Got unexpected IndexType: INCORE");
		    }
		    default: {
			throw new IOException("Header read failure. Got unexpected IndexType: " + indexTypeValue);
		    }
		    }
		} else if (type == FileType.INLINE_INDEX) {
		    setIndexType(IndexType.INLINE_DT);
		} else if (type == FileType.EXTERNAL_INDEX) {
		    setIndexType(IndexType.EXTERNAL_DT);
		} else if (type == FileType.SHADOW_INDEX) {
		    setIndexType(IndexType.SHADOW_DT);
		} else {
			throw new IOException("Header read failure. Got unexpected IndexType: " + type);
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
	} catch (BufferUnderflowException bue) {
	    throw new IOException(fileName + " does not have the expected data");
	}
    }

    /**
     * Get the name of the index.
     */
    public String getName() {
	return indexName;
    }

    /**
     * Set the name of the index.
     */
    public void setName(String name) {
	indexName = name;
    }

    /**
     * Get an ID of an index.
     */
    public ID getID() {
	return indexID;
    }

    /**
     * Set the ID of the index.
     */
    public void setID(ID id) {
	indexID = id;
    }

    /**
     * Get the data style.
     * Either inline or external or shadow.
     */
    public IndexType getIndexType() {
	return indexType;
    }

    /**
     * Set the data style.
     * Either inline or external or shadow.
     */
    public void setIndexType(IndexType type) {
	indexType = type;
    }

    /**
     * Determine the length of a Header
     */
    protected long getHeaderLength() throws IOException {
	return headerFile.length();
    }


}

