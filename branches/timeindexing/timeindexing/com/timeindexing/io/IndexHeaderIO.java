// IndexHeaderIO.java

package com.timeindexing.io;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.index.Index;
import com.timeindexing.index.FileIndexItem;
import com.timeindexing.index.ManagedIndex;
import com.timeindexing.index.IndexHeader;
import com.timeindexing.index.DataType;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimestampDecoder;
import com.timeindexing.time.Clock;
import com.timeindexing.event.*;

import com.timeindexing.index.InlineIndex;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * An Index Header interactor.
 * This is an UNFINISHED implementation of an Index Header Interactor. 
 * It represents the index header in core.
 */
public class IndexHeaderIO extends IndexDecoder implements HeaderFileInteractor, IndexHeader, Cloneable, IndexPrimaryEventListener, IndexAddEventListener { //, IndexAccessEventListener {
    // The index that this does header I/O for
    ManagedIndex myIndex = null;

    /**
     * Create a IndexHeaderIO.
     */
    public IndexHeaderIO(ManagedIndex indexMgr) {
	myIndex = indexMgr;
	headerBuf = ByteBuffer.allocate(2048);
	openMode = "rw";
    }



    /**
     * Open an index header.
     */
    public boolean open(String filename) throws IOException {
	boolean opened = super.open(filename);

	if (opened) {
	    read();
	    return true;
	} else {
	    return false;
	}
    }
	
    /**
     * Create an index header.
     */
    public boolean create(String filename) throws IOException {
	itemSize = INDEX_ITEM_SIZE;
	indexType = myIndex.getIndexType();

	boolean opened =  super.open(filename);

	// sync the app level header with my copy of the header
	syncHeader(myIndex);

	if (opened) {
	    write();
	    return true;
	} else {
	    return false;
	}
    }


    /**
     * Flush the current values to the header file.
     */
    public long flush()  throws IOException {
	// sync the incore header with this
	syncHeader(myIndex);

	if (isOpen()) {
	    // the header file is open, so
	    // write the contents out
	    write();
	    return 0L;
	} else {
	    // not open yet
	    if (open()) {
		// so open it and then write contents out
		write();
		return 0L;
	    } else {
		//create it and then write it
		// we should;nt get here as RandomAccessFile
		// will create files on demand
		throw new IOException("File not open in flush() " + fileName);
	    }
	}
    }
	    
    /**
     * Operation on close
     */
    public long close() throws IOException {
	// sync the incore header with this
	syncHeader(myIndex);

	/*	// if the index is activated
	// set the end time
	if (myIndex.isActivated()) {
	    // set the end time
	    endTime = myIndex.getEndTime();
	}
	*/
	write();

	return super.close();
    }

    /**
     * Write the contents of the header file out
     * It assumes the header file is alreayd open for writing.
     */
    public long write() throws IOException {
	int count = 0;

	// seek to start
	headerFile.seek(0);

	// clear the header buffer
	headerBuf.clear();

	//  fill the buffer with the bytes

	// TimeIndex Header magic
	headerBuf.put(FileType.T);    count++;
	headerBuf.put(FileType.I);    count++;
	headerBuf.put(FileType.BYTE_3);    count++;
	headerBuf.put(FileType.HEADER);    count++;

	// version major
	headerBuf.put((byte)versionMajor);    count++;
	// version minor
	headerBuf.put((byte)versionMinor);    count++;

	// write the ID
	headerBuf.putLong(indexID.value());  count+= 8;

	// size of name, +1 for null terminator
	headerBuf.putShort((short)(indexName.length()+1));     count+=2;

	// the name
	headerBuf.put(indexName.getBytes());         count += indexName.length();
	// plus null terminator
	headerBuf.put((byte)0x00);    count++;

	
	// System.err.println("Now Count = " + count);

	// indexType
	headerBuf.put((byte)indexType);    count++;

	// write the start time
	headerBuf.putLong(startTime.value());  count+= 8;

	// write the end time
	headerBuf.putLong(endTime.value());  count+= 8;

	// write the first time
	headerBuf.putLong(firstTime.value());  count+= 8;

	// write the last time
	headerBuf.putLong(lastTime.value());  count+= 8;

	// write the first data time
	headerBuf.putLong(firstDataTime.value());  count+= 8;

	// write the last data time
	headerBuf.putLong(lastDataTime.value());  count+= 8;

	// write the item size
	headerBuf.putInt(itemSize);  count+= 4;

	// write the data size
	headerBuf.putLong(dataSize);  count+= 8;

	// write the length
	headerBuf.putLong(length);  count+= 8;

	// write the offset of the first item
	headerBuf.putLong(firstOffset.value());  count+= 8;

	// write the offset of the last item
	headerBuf.putLong(lastOffset.value());  count+= 8;

	if (isTerminated()) {
	    // set terminated
	    headerBuf.put((byte)0x01);    count++;
	} else {
	    // not terminated
	    headerBuf.put((byte)0x00);    count++;
	}

	/*
	 * now write it out
	 */
	headerBuf.flip();
	channel.write(headerBuf);
	
	// System.err.println("IndexHeaderIO wrote out: " + count);

	return (long)count;
    }
	

    
    /**
     * A notification that an Index has been opened.
     */
    public  void opened(IndexPrimaryEvent ipe) {
	Index index = (Index)ipe.getSource();
	//System.err.println("IndexHeaderIO opened: IndexPrimaryEvent source = " + index.getName());

	indexName = ipe.getName();
	indexID = ipe.getID();

    }

    /**
     * A notification that an Index has been closed.
     */
    public  void closed(IndexPrimaryEvent ipe) {
	Index index = (Index)ipe.getSource();
	//System.err.println("IndexHeaderIO closed: IndexPrimaryEvent source = " + index.getName());

    }

    /**
     * A notification that an Index has been flushed.
     */
    public  void flushed(IndexPrimaryEvent ipe) {
	// TODO: beeter handling
	try {
	    flush();
	} catch (IOException ioe) {
	    ;
	}
    }

    /**
     * A notification that an Index has been created.
     */
    public  void created(IndexPrimaryEvent ipe) {
	Index index = (Index)ipe.getSource();
	//System.err.println("IndexHeaderIO created: IndexPrimaryEvent source = " + index.getName() + " ID = " + index.getID());

	indexName = ipe.getName();
	indexID = ipe.getID();
    }

   /**
     * A notification that an IndexItem has been added to an Index.
     */
    public void itemAdded(IndexAddEvent iae) {
	Index index = (Index)iae.getSource();
	FileIndexItem item = (FileIndexItem)iae.getIndexItem();
	//System.err.println("IndexHeaderIO: IndexAddEvent source = " + index.getName());

	length = myIndex.getLength();

	if (length == 1) {
	    firstTime = myIndex.getFirstTime();
	    //FL firstOffset = item.getIndexOffset();
	}

	lastTime = myIndex.getLastTime();
	endTime = myIndex.getEndTime();
	//FL lastOffset = item.getIndexOffset();

	/*
	 * Don;t write every time
	try {
	    write();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
	*/

    }
}

