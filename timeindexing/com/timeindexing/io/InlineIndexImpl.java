// InlineIndexImpl.java

package com.timeindexing.io;

import com.timeindexing.index.DataType;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;

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
public class InlineIndexImpl {
    String fileName = null;
    RandomAccessFile indexFile = null;

    int versionMajor = 0;
    int versionMinor = 0;
    String indexName = null;
    DataType dataType = null;
    
    /**
     * Construct an Inline Index.
     */
    public InlineIndexImpl() {
    }

    /**
     * Operation on creation.
     */
    public boolean create() {
	
    }

    /**
     * Open an index file  to read it.
     */
    public boolean open(String filename) throws IOException {
	fileName = filename;

	return open();
    }

    /**
     * Open an index   to read it.
     */
    public boolean open() throws IOException {
	try {
	    indexFile = new RandomAccessFile(fileName, "rw");
	} catch (FileNotFoundException fnfe) {
	    throw new IOException("Could not open input file: " + fileName);
	}

	return true;
	
    }

    /**
     * Read an index header from the header stream.
     */
    public void read() throws IOException {
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
	    byte nameSize = indexFile.readByte();
	    byte[] nameRaw = new byte[nameSize-1];
	    indexFile.readFully(nameRaw, 0, nameSize-1);
	    indexName = new String(nameRaw);

	    // soak up index name padding
	    long offset = indexFile.getFilePointer();
	    for (int pad = 8 - ((int)offset % 8); pad>0; pad--) {
		indexFile.readByte();
	    }
	}
    }


    /**
     * Write the contents of the header out
     * It assumes the index file is alreayd open for writing.
     */
    public int write() throws IOException {
	int count = 0;

	// seek to start
	indexFile.seek(0);

	// write the bytes

	// TimeIndex Header magic
	indexFile.writeByte(FileType.T);    count++;
	indexFile.writeByte(FileType.I);    count++;
	indexFile.writeByte(FileType.BYTE_3);    count++;
	indexFile.writeByte(FileType.INLINE_INDEX);    count++;

	// version major
	indexFile.writeByte((byte)versionMajor);    count++;
	// version major
	indexFile.writeByte((byte)versionMinor);    count++;

	// dataType
	indexFile.writeByte((byte)dataType);    count++;

	// size of name, +1 for null terminator
	indexFile.writeByte((byte)(indexName.length()+1));     count++;

	// the name
	indexFile.writeBytes(indexName);         count += indexName.length();
	// plus null terminator
	indexFile.writeByte(0x00);    count++;

	// pad to 8 byte boundary
	System.err.println("Count = " + count);
	for (int pad = 8 - (count % 8); pad>0; pad--) {
	    indexFile.writeByte(0x00);    count++;
	}
	
	System.err.println("Now Count = " + count);
    }
}
