// IndexHeaderIO.java

package com.timeindexing.io;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.index.Index;
import com.timeindexing.index.FileIndexItem;
import com.timeindexing.index.ManagedIndex;
import com.timeindexing.index.StoredIndex;
import com.timeindexing.index.IndexHeader;
import com.timeindexing.index.HeaderOption;
import com.timeindexing.index.DataType;
import com.timeindexing.index.Description;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimestampDecoder;
import com.timeindexing.time.Clock;
import com.timeindexing.event.*;

import java.io.File;
import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Properties;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.nio.channels.FileLock;
import java.nio.channels.FileChannel;
import java.nio.channels.NonWritableChannelException;
import java.net.URI;

/**
 * An Index Header IO object.
 * It represents the file version of an index header in core.
 */
public class IndexHeaderIO extends IndexDecoder implements HeaderFileInteractor, IndexHeader, Cloneable {
    // The IndexFileInteractor that interacts between the
    // StoredIndex and the underlying files.
    IndexFileInteractor interactor = null;
    // The index that this does header I/O for
    StoredIndex myIndex = null;
    // The FileLock used when locking the index
    FileLock lock = null;
    // are we creating an Index
    boolean creating = false;

    /**
     * Create a IndexHeaderIO.
     */
    public IndexHeaderIO(IndexFileInteractor indexInteractor) {
	interactor = indexInteractor;
	myIndex = interactor.getIndex();
	headerBuf = ByteBuffer.allocate(2048);
    }


    /**
     * Open an index header.
     */
    public boolean open(String filename) throws IOException {
	try {
	    fileName = FileUtils.resolveFileName(filename, ".tih");
	    File file = new File(fileName);

	    String openMode = null;

	    // determine if the files should be opened 
	    // read-only or read-write
	    if (! creating) {  // we are NOT creating, just opening
		if (file.canWrite()) {   // we have write permission
		    openMode = "rw";
		    setReadOnly(false);
		} else {                 // we don;t have write permission
		    openMode = "r";
		    setReadOnly(true);
		}
	    } else {   // we ARE creating
		openMode = "rw";
		setReadOnly(false);
	    }


	    headerFile = new RandomAccessFile(file, openMode);

	    channel = headerFile.getChannel();

	    if (! creating) {
		read();
	    }

	    return true;

	} catch (FileNotFoundException fnfe) {
	    throw new IOException("Could not open header file: " + fileName);
	}
    }
	
    /**
     * Create an index header.
     */
    public boolean create(String filename) throws IOException {
	return create(filename, null);
    }


    /**
     * Create an index header.
     */
    public boolean create(String filename, Properties options) throws IOException {
	// going into creating mode
	creating = true;

	// set up things that must be set
	setItemSize(INDEX_ITEM_SIZE);
	setIndexType(myIndex.getIndexType());

	// open the channel
	boolean opened =  open(filename);

	// clear it
	headerFile.setLength(0);

	// Check to see if the Index is already locked.
	// This stops two processes trying to create the same
	// index and the same time
	FileLock newLock = getWriteLock();

	if (newLock == null) {
	    // no lock was returned, so it's already locked
	    throw new IOException("Index in " + fileName + " is WRITE LOCKED");
	} else {
	    // a lock was returned, which we keep a hnadle on.
	    lock = newLock;
	}


	// sync the app level header with my copy of the header
	syncHeader(myIndex.getHeader());

	// finishing the creation task
	creating = false;

	if (opened) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Flush the current values to the header file.
     */
    public long flush()  throws IOException {
	long writeCount = 0;

	// sync the incore header with this
	syncHeader(myIndex.getHeader());

	if (myIndex.isActivated() && myIndex.isChanged()) {
	    if (isOpen()) {
		// the header file is open, so
		// write the contents out
		writeCount = write();
		return writeCount;
	    } else {
		// not open yet
		if (open()) {
		    // so open it and then write contents out
		    writeCount = write();
		    return writeCount;
		} else {
		    //create it and then write it
		    // we should;nt get here as RandomAccessFile
		    // will create files on demand
		    throw new IOException("File not open in flush() " + fileName);
		}
	    }
	} else {
	    // flushed nothing
	    return 0L;
	}
    }
	
    /**
     * Get a write-lock on this index.
     */
    public FileLock getWriteLock() {
	// if lock is null
	// try and get a write lock on the header file
	// this stops multiple apps from writing to the same
	// index, and corrupting the data
	if (lock == null) {
	    try {
		FileLock newLock = channel.tryLock();
		lock = newLock;
	    } catch (IOException ioe) {
		// couldn;t get a lock in a severe way
		return null;
	    }

	    if (lock == null) {
		// no lock was returned, so it's already locked
		//System.err.println(fileName + " already has a lock");
		return null;
	    } else {
		// got given a lock, so now we have it locked
		//System.err.println(fileName + " got new lock: " + lock);
		return lock;
	    }
	} else {
	    // lock is not null
	    // we already have the Index write lock
	    return lock;
	}
    }
    
    /**
     * Release a FileLock.
     */
    public boolean releaseWriteLock() {
	if (lock == null) {
	    // there is no lock
	    return false;
	} else {
	    // there is a saved lock
	    try {
		lock.release();
		lock = null;
		return true;
	    } catch (IOException ioe) {
		// some bizzare I/O error occured
		// TODO: detemine what to do
		return false;
	    }
	}
    }

    /**
     * Has the Index been write-locked.
     */
    public boolean isWriteLocked() {
	// if lock is null, then we don;t have the
	// Index locked, but some other app might
	if (lock == null) {
	    try {
		// try and get a lock
		FileLock newLock = channel.tryLock();

		if (newLock == null) {
		    // didn't get a lock, so it must be locked by someone else
		    return true;
		} else {
		    // got a lock, so it must NOT be locked by someone else.
		    // We're only checking the lock
		    // so we relese it now
		    newLock.release();

		    return false;
		}
	    } catch (IOException ioe) {
		// some bizzare I/O error occured
		// TODO: detemine what to do
		return false;
	    } catch (NonWritableChannelException nwce) {
		// we tried to check for a lock
		// on a file we don;t have write permission on
		return false;
	    }
	} else {
	    // lock is not null
	    // we already have a write lock
	    // therefore it's locked
	    return true;
	}
    }

    /**
     * Sync this header with the Index header, and write it
     * out, if required to.
     */
    protected void syncWithIndex() throws IOException {
	// sync the incore header with this
	syncHeader(myIndex.getHeader());

	// write out header, iff the index has been activated
	if (myIndex.isActivated() && myIndex.isChanged()) {
	    write();
	}
    }

    /**
     * Operation on close
     */
    public long close() throws IOException {
	//System.err.println("In IndexHeaderIO.close()");
	syncWithIndex();

	return reallyClose();
    }

    /**
     * The final close functionaliy.
     * Release the write lock, and 
     * really really close.
     */
    protected long reallyClose() throws IOException {
	// release the lock if it exists
	if (lock != null) {
	    releaseWriteLock();
	}

	// do super class close()
	return super.close();
    }

    /**
     * Write the contents of the header file out
     * It assumes the header file is alreayd open for writing.
     * @return the total no of bytes written
     */
    public long write() throws IOException {
	// seek to start
	headerFile.seek(0);

	return writeToChannel(channel);
    }


    /**
     * Write the contents of the header file out
     * It assumes the header file is alreayd open for writing.
     * @return the total no of bytes written
     */
    protected long writeToChannel(FileChannel channel) throws IOException {
	//System.err.println("In IndexHeaderIO.write()");
	int count = 0;

	// clear the header buffer
	headerBuf.clear();

	//  fill the buffer with the bytes

	// TimeIndex Header magic
	headerBuf.put(FileType.T);    
	headerBuf.put(FileType.I);    
	headerBuf.put(FileType.BYTE_3);    
	headerBuf.put(FileType.HEADER);    

	// version major
	headerBuf.put((byte)getVersionMajor());    
	// version minor
	headerBuf.put((byte)getVersionMinor());    

	// write the ID
	headerBuf.putLong(getID().value());  

	// size of name, +1 for null terminator
	headerBuf.putShort((short)(getName().length()+1));     

	// the name
	headerBuf.put(getName().getBytes());         
	// plus null terminator
	headerBuf.put((byte)0x00);    

	// indexType
	headerBuf.put((byte)getIndexType().value());    

	// write the start time
	headerBuf.putLong(getStartTime().value()); 

	// write the end time
	headerBuf.putLong(getEndTime().value());  

	// write the first time
	headerBuf.putLong(getFirstTime().value());

	// write the last time
	headerBuf.putLong(getLastTime().value()); 

	// write the first data time
	headerBuf.putLong(getFirstDataTime().value());  

	// write the last data time
	headerBuf.putLong(getLastDataTime().value()); 

	// write the item size
	headerBuf.putInt(getItemSize()); 

	// write the data size
	headerBuf.putLong(getDataSize());

	// write the length
	headerBuf.putLong(getLength());  

	// write the offset of the first item
	headerBuf.putLong(getFirstOffset().value()); 

	// write the offset of the last item
	headerBuf.putLong(getLastOffset().value());  

	// is the index terminated
	if (isTerminated()) {
	    // set terminated
	    headerBuf.put((byte)0x01);    
	} else {
	    // not terminated
	    headerBuf.put((byte)0x00);    
	}

	

	/*
	 * now write it out
	 */
	headerBuf.flip();
	count = channel.write(headerBuf);
	
	// System.err.println("IndexHeaderIO wrote out: " + count);

	ByteBuffer optionBuf = convertOptions();

	if (optionBuf == null) {
	    ; // nothing to do
	} else {
	    long optionCount = channel.write(optionBuf);
	    count += optionCount;
	    //System.err.println("IndexHeaderIO wrote option bytes: " + optionCount);
	}

	return (long)count;
    }
       
    /**
     * Convert the options to their file format.
     */
    protected ByteBuffer convertOptions() {
	ByteBuffer optionBuffer = null;

	Set optionsUsed = listOptions();

	if (optionsUsed == null) {
	    // no options at all
	    return null;
	} else {
	    // First pass over options
	    // works out how much space is needed
	    int spaceNeeded = 0;

	    // the options are put into the ByteBuffer
	    Iterator optionI = optionsUsed.iterator();

	    while (optionI.hasNext()) {
		HeaderOption anOption = (HeaderOption)optionI.next();

		switch (anOption.value()) {
		case HeaderOption.DESCRIPTION: {
		    spaceNeeded += processDescription(HeaderOptionProcess.SIZE, anOption, null);
		    break;
		}
	    
		case HeaderOption.INDEXPATH: {
		    spaceNeeded += processIndexPath(HeaderOptionProcess.SIZE, anOption, null);
		    break;
		}

		case HeaderOption.DATAPATH: {
		    spaceNeeded += processDataPath(HeaderOptionProcess.SIZE, anOption, null);
		    break;
		}

		case HeaderOption.DATATYPE: {
		    spaceNeeded += processDataType(HeaderOptionProcess.SIZE, anOption, null);
		    break;
		}
		    
		case HeaderOption.IS_IN_TIME_ORDER: {
		    spaceNeeded += processIsInTimeOrder(HeaderOptionProcess.SIZE, anOption, null);
		    break;
		}
	
		case HeaderOption.NO_DATA_FILE_HEADER: {
		    spaceNeeded += processNoDataFileHeader(HeaderOptionProcess.SIZE, anOption, null);
		    break;
		}
	
		case HeaderOption.REFERENCEMAPPING: {
		    spaceNeeded += processReferenceMapping(HeaderOptionProcess.SIZE, anOption, null);
		    break;
		}
		    

		default: {
		}
		}
	    }

	    //System.err.println("Option space needed = " + spaceNeeded);

	    if (spaceNeeded == 0) {
		// there are zero options
		// so return null
		return null;
	    } else {

		// the relevant size ByteBuffer is allocated
		optionBuffer = ByteBuffer.allocate(spaceNeeded);

		// the options are put into the ByteBuffer
		// reuse optionI
		optionI = optionsUsed.iterator();

		while (optionI.hasNext()) {
		    HeaderOption anOption = (HeaderOption)optionI.next();

		    switch (anOption.value()) {
		    case HeaderOption.DESCRIPTION: {
			processDescription(HeaderOptionProcess.WRITE, anOption, optionBuffer);
			break;
		    }
	    
		    case HeaderOption.INDEXPATH: {
			processIndexPath(HeaderOptionProcess.WRITE, anOption, optionBuffer);
			break;
		    }

		    case HeaderOption.DATAPATH: {
			processDataPath(HeaderOptionProcess.WRITE, anOption, optionBuffer);
			break;
		    }

		    case HeaderOption.DATATYPE: {
			processDataType(HeaderOptionProcess.WRITE, anOption, optionBuffer);
			break;
		    }
			
		    case HeaderOption.IS_IN_TIME_ORDER: {
			processIsInTimeOrder(HeaderOptionProcess.WRITE, anOption, optionBuffer);
			break;
		    }
	
		    case HeaderOption.NO_DATA_FILE_HEADER: {
			spaceNeeded += processNoDataFileHeader(HeaderOptionProcess.WRITE, anOption, optionBuffer);
			break;
		    }
	
		    case HeaderOption.REFERENCEMAPPING: {
			processReferenceMapping(HeaderOptionProcess.WRITE, anOption, optionBuffer);
			break;
		    }
		    
		    default: {
		    }
		    }

		    //System.err.println("Added option " + anOption);
		}

		optionBuffer.flip();
		return optionBuffer;
	    }
	}
    }

    /**
     * A description.
     */
    protected int processDescription(HeaderOptionProcess what, HeaderOption anOption, ByteBuffer optionBuffer) {
	// the description
	Description desc = (Description)getOption(anOption);

	// 1 for option byte, 2 for the size
	// 4 for data-type code
	// n for the description, and 1 for the NUL
	int size = 1 + 2 + 4 + desc.length() + 1;

	if (desc == null || desc.equals("")) {
	    ; // nothing to do
	    return  0;
	} else {
	    if (what == HeaderOptionProcess.SIZE) {
		return size;
	    } else {
		// output
		optionBuffer.put(anOption.value()); // the option bytes
		optionBuffer.putInt(desc.getDataType().value()); // the data-type
		optionBuffer.putShort((short)(desc.length()+1)); // the length plus 1 for NUL
		optionBuffer.put(desc.getBytes());         	// the description
		optionBuffer.put((byte)0x00);    	// plus null terminator

		return size;
	    }
	}
    }


    /**
     * An index path name.
     */
    protected int processIndexPath(HeaderOptionProcess what, HeaderOption anOption, ByteBuffer optionBuffer) {
	// the index path
	String path = (String)getOption(anOption);

	// 1 for option byte, 2 for the size
	// n for the path, and 1 for the NUL
	int size = 1 + 2  + path.length() + 1;

	if (path == null || path.equals("")) {
	    ; // nothing to do
	    return  0;
	} else {
	    if (what == HeaderOptionProcess.SIZE) {
		return size;
	    } else {
		// output
		optionBuffer.put(anOption.value()); // the option bytes
		optionBuffer.putShort((short)(path.length()+1)); // the length plus 1 for NUL
		optionBuffer.put(path.getBytes());         	// the path
		optionBuffer.put((byte)0x00);    	// plus null terminator

		return size;
	    }
	}
    }


    /**
     * A data path name.
     */
    protected int processDataPath(HeaderOptionProcess what, HeaderOption anOption, ByteBuffer optionBuffer) {
	// the data path
	String path = (String)getOption(anOption);

	// 1 for option byte, 2 for the size
	// n for the path, and 1 for the NUL
	int size = 1 + 2  + path.length() + 1;

	if (path == null || path.equals("")) {
	    ; // nothing to do
	    return  0;
	} else {
	    if (what == HeaderOptionProcess.SIZE) {
		return size;
	    } else {
		// output
		optionBuffer.put(anOption.value()); // the option bytes
		optionBuffer.putShort((short)(path.length()+1)); // the length plus 1 for NUL
		optionBuffer.put(path.getBytes());         	// the path
		optionBuffer.put((byte)0x00);    	// plus null terminator

		return size;
	    }
	}
    }

    /**
     * A data type.
     */
    protected int processDataType(HeaderOptionProcess what, HeaderOption anOption, ByteBuffer optionBuffer) {
	// the data type
	DataType datatype = (DataType)getOption(anOption);

	// 1 for option byte, 4 for the type ID,  2 for the size
	// n for the path, and 1 for the NUL
	int size = 1 + 4 + 2  + datatype.mimeType().length() + 1;


	if (what == HeaderOptionProcess.SIZE) {
	    return size;
	} else {
	    // output
	    optionBuffer.put(anOption.value()); // the option bytes
	    optionBuffer.putInt(datatype.value()); // the data-type
	    optionBuffer.putShort((short)(datatype.mimeType().length()+1)); // the length plus 1 for NUL
	    optionBuffer.put(datatype.mimeType().getBytes());         	// the mime type
	    optionBuffer.put((byte)0x00);    	// plus null terminator

	    return size;
	}
    }

    /**
     * Is the index in time order
     */
    protected int processIsInTimeOrder(HeaderOptionProcess what, HeaderOption anOption, ByteBuffer optionBuffer) {
	// the data type
	Boolean inTimeOrder = (Boolean)getOption(anOption); 

	// 1 for option byte, 1 for boolean
	int size = 1 + 1;

	
    	if (what == HeaderOptionProcess.SIZE) {
	    return size;
	} else {
	    // output
	    optionBuffer.put(anOption.value()); // the option bytes
	    optionBuffer.put(inTimeOrder.booleanValue() ? (byte)1 : (byte)0);

	    return size;
	}

    }

    /**
     * Is there a header on the data file
     */
    protected int processNoDataFileHeader(HeaderOptionProcess what, HeaderOption anOption, ByteBuffer optionBuffer) {
	// the data type
	Boolean noDataFileHeader = (Boolean)getOption(anOption); 

	// 1 for option byte, 1 for boolean
	int size = 1 + 1;

	
    	if (what == HeaderOptionProcess.SIZE) {
	    return size;
	} else {
	    // output
	    optionBuffer.put(anOption.value()); // the option bytes
	    optionBuffer.put(noDataFileHeader.booleanValue() ? (byte)1 : (byte)0);

	    return size;
	}

    }

    /**
     * The referenced indexes mapping
     */
    protected int processReferenceMapping(HeaderOptionProcess what, HeaderOption anOption, ByteBuffer optionBuffer) {
	// the referenced indexes map: ID -> URI
	Map referenceMap = (Map)getOption(anOption);

	int size = 0;


	if (what == HeaderOptionProcess.SIZE) {
	    // 1 for option byte, 2 for the no of entries 
	    // 8 for EACH ID
	    // 2 for the size, and n for EACH URI, and 1 for the NUL
	    size = 1 + 2;

	    // skip through all the URIs to find their length
	    Iterator mapIterator = referenceMap.values().iterator();

	    while (mapIterator.hasNext()) {
		URI uri = (URI)mapIterator.next();

		// add the size of the ID
		size += 8;

		// add the size of the URI + 1 for the NUL
		size +=2;
		size += uri.toString().length();
		size += 1;
	    }
		   

	    return size;
	} else {
	    // output
	    optionBuffer.put(anOption.value()); // the option bytes
	    
	    // put out the size
	    optionBuffer.putShort((short)referenceMap.size());
	    System.err.println("IndexHeaderIO: " + referenceMap.size() + " references");
	    

	    // skip through all the kays
	    Iterator mapIterator = referenceMap.keySet().iterator();

	    while (mapIterator.hasNext()) {
		ID id = (ID)mapIterator.next();

		// add the ID
		optionBuffer.putLong(id.value());

		// get the URI
		URI uri = (URI)referenceMap.get(id);

		// add the the URI + the NUL
		String uriString = uri.toString();
		optionBuffer.putShort((short)(uriString.length()+1));
		optionBuffer.put(uriString.getBytes());     // the URI as a string
		optionBuffer.put((byte)0x00);    	// plus null terminator

		System.err.println("IndexHeaderIO: reference: " + id + " => " + uri);
	    }

	    return size;
	}
    }

}

