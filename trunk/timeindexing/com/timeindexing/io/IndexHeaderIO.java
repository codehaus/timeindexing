// IndexHeaderIO.java

package com.timeindexing.io;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.index.Index;
import com.timeindexing.index.FileIndexItem;
import com.timeindexing.index.ManagedIndex;
import com.timeindexing.index.IndexHeader;
import com.timeindexing.index.HeaderOption;
import com.timeindexing.index.DataType;
import com.timeindexing.index.Description;
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
import java.util.Set;
import java.util.Iterator;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * An Index Header interactor.
 * This is an UNFINISHED implementation of an Index Header Interactor. 
 * It represents the index header in core.
 */
public class IndexHeaderIO extends IndexDecoder implements HeaderFileInteractor, IndexHeader, Cloneable {
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
	return create(filename, null);
    }


    /**
     * Create an index header.
     */
    public boolean create(String filename, Properties options) throws IOException {
	itemSize = INDEX_ITEM_SIZE;
	indexType = myIndex.getIndexType();

	boolean opened =  super.open(filename);

	// sync the app level header with my copy of the header
	syncHeader(myIndex.getHeader());

	if (opened) {
	    //write();
	    return true;
	} else {
	    return false;
	}
    }


    /**
     * Flush the current values to the header file.
     */
    public long flush()  throws IOException {
	//System.err.println("In IndexHeaderIO.flush()");
	// sync the incore header with this
	syncHeader(myIndex.getHeader());

	if (myIndex.isActivated()) {
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
	} else {
	    // flushed nothing
	    return 0L;
	}
    }
	    
    /**
     * Operation on close
     */
    public long close() throws IOException {
	//System.err.println("In IndexHeaderIO.close()");
	// sync the incore header with this
	syncHeader(myIndex.getHeader());

	if (myIndex.isActivated()) {
	    write();
	}

	return super.close();
    }

    /**
     * Write the contents of the header file out
     * It assumes the header file is alreayd open for writing.
     */
    public long write() throws IOException {
	//System.err.println("In IndexHeaderIO.write()");
	int count = 0;

	// seek to start
	headerFile.seek(0);

	// clear the header buffer
	headerBuf.clear();

	//  fill the buffer with the bytes

	// TimeIndex Header magic
	headerBuf.put(FileType.T);    
	headerBuf.put(FileType.I);    
	headerBuf.put(FileType.BYTE_3);    
	headerBuf.put(FileType.HEADER);    

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

	// indexType
	headerBuf.put((byte)indexType);    

	// write the start time
	headerBuf.putLong(startTime.value()); 

	// write the end time
	headerBuf.putLong(endTime.value());  

	// write the first time
	headerBuf.putLong(firstTime.value());

	// write the last time
	headerBuf.putLong(lastTime.value()); 

	// write the first data time
	headerBuf.putLong(firstDataTime.value());  

	// write the last data time
	headerBuf.putLong(lastDataTime.value()); 

	// write the item size
	headerBuf.putInt(itemSize); 

	// write the data size
	headerBuf.putLong(dataSize);

	// write the length
	headerBuf.putLong(length);  

	// write the offset of the first item
	headerBuf.putLong(firstOffset.value()); 

	// write the offset of the last item
	headerBuf.putLong(lastOffset.value());  

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
			processDataType(HeaderOptionProcess.SIZE, anOption, null);
			break;
		    }
			
		    case HeaderOption.IS_IN_TIME_ORDER: {
			processIsInTimeOrder(HeaderOptionProcess.SIZE, anOption, null);
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

}

