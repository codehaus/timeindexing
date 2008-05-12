// FileIndexItem.java

package com.timeindexing.index;

import com.timeindexing.time.Clock;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimestampDecoder;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.Size;
import com.timeindexing.basic.Offset;
import com.timeindexing.data.DataItem;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

/**
 * An implementatio of an IndexItem that is saved in a file.
 * Every value is explicitly held.
 */
public class FileIndexItem extends IncoreIndexItem implements IndexItem, ManagedFileIndexItem {
    transient Offset dataOffset = null; 
    transient Offset indexOffset = null;

    /**
     * Construct a FileIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param dataitem some data as a Item
     * @param type the type of the data
     * @param id an index ID
     * @param annotationValue the meta data for annotations
     */
    public FileIndexItem(Timestamp dataTS, Timestamp indexTS, DataItem dataitem,
			DataType type, ID id, long annotationValue) {
	this(dataTS, indexTS,  new DataHolderObject(dataitem.getBytes(),  dataitem.getSize()),
	     type, id, annotationValue);
    }
	
    /**
     * Construct a FileIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param data some data as a DataAbstraction
     * @param type the type of the data
     * @param id an index ID
     * @param annotationValue the meta data for annotations
     */
    public FileIndexItem(Timestamp dataTS, Timestamp indexTS, DataAbstraction data, DataType type,
			 ID id, long annotationValue) {
	super(dataTS, indexTS,  data, type, id, annotationValue);
    }
	
    /**
     * Construct a FileIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param data some data as a DataAbstraction
     * @param size the size of the DataAbstraction
     * @param type the type of the data
     * @param id an index ID
     * @param annotationValue the meta data for annotations
     */
    public FileIndexItem(Timestamp dataTS, Timestamp indexTS, DataAbstraction data, 
			 Size size, DataType type, ID id, long annotationValue) {
	super(dataTS, indexTS,  data, size, type, id, annotationValue);
    }
	
    /**
     * A ByteBuffer of the Data being indexed.
     * @return an empty buffer, if this IndexItem doesn't have the data to hand
     */
    public synchronized ByteBuffer getData() {
	setLastAccessTime();

	if (data instanceof DataHolder) {
	    // its a data holding object

	    ByteBuffer buffer =  ((DataHolder)data).getBytes();

	    /*
	    if (! buffer.isReadOnly()) {
		throw new RuntimeException("FileIndexItem: getData got a ByteBuffer that is NOT read only " + getPosition() +  " Thread " + Thread.currentThread().getName());
	    } else {
		return buffer;
		}*/
	    return buffer;

	} else if (data instanceof DataReference) {
	    // its a data reference object

	    FileIndex index  = (FileIndex)getIndex();

	    // read the data
	    DataHolderObject dataObj = index.readData(position.value(), (DataReference)data);

	    // if we got the data 
	    if (dataObj != null) {
		// then set the data
		setData(dataObj);

		// calculate the held volume of this Item
		// and inform the cache of the new data volume
		index.getCache().increaseDataVolume(getDataSize().value());

		//System.err.println("Volume + = " + volumeHeld);

		// get the buffer to return
		ByteBuffer buffer =  ((DataHolder)dataObj).getBytes();

		return buffer;

	    } else {
		// we didn't get the data back
		throw new RuntimeException("FileIndexItem: DateReference " +  getPosition() + " " + data + " Thread " + Thread.currentThread().getName());
	    }

	} else {
	    System.err.println("FileIndexItem: returning EMPTY_BUFFER  data class " + data.getClass().getName() +  " Thread " + Thread.currentThread().getName());
	    return EMPTY_BUFFER;
	}
    }

    /**
     * Does this IndexItem actually hold the data.
     */
    public boolean hasData() {
	setLastAccessTime();

	if (data instanceof IndexReference) {
	    return false;
	} else if (data instanceof DataHolder) { // its a data holding object
	    return true;
	} else {		// its NOT a data holding object
	    return false;
	}
    }

    /**
     * Get the DataAbstraction held by the IndexItem.
     */
    public DataAbstraction getDataAbstraction() {
	setLastAccessTime();
	return data;
    }

    /**
     * Set the data to be a new DataAbstraction.
     */
    public synchronized ManagedFileIndexItem setData(DataAbstraction data) {
	setLastAccessTime();
	this.data = data;
	return this;
    }

    /**
     * Get the file offset for the index for this index item.
     */
    public Offset getIndexOffset() {
	setLastAccessTime();
	return indexOffset;
    }

    /**
     * Set the file offset for the index for this index item.
     */
    public ManagedFileIndexItem setIndexOffset(Offset offset) {
	indexOffset = offset;
	return this;
    }

    /**
     * Get the file offset for the data for this index item.
     */
    public Offset getDataOffset() {
	setLastAccessTime();
	return dataOffset;
    }

    /**
     * Set the  file offset for the data for this index item.
     */
    public ManagedFileIndexItem setDataOffset(Offset offset) {
	dataOffset = offset;
	return this;
    }

}
