// FileIndexItem.java

package com.timeindexing.index;

import com.timeindexing.time.Clock;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimestampDecoder;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
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
public class FileIndexItem extends IncoreIndexItem implements IndexItem, ManagedFileIndexItem, Serializable {
    Offset dataOffset = null; 
    Offset indexOffset = null;
    final static ByteBuffer emptyBuffer = ByteBuffer.allocate(0);

    /**
     * Construct a FileIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param data some data as a Item
     * @param type the type of the data
     * @param id an index ID
     * @param annotationID an ID for annotations
     */
    public FileIndexItem(Timestamp dataTS, Timestamp indexTS, DataItem dataitem,
			 int type, ID id, ID annotationID) {
	this(dataTS, indexTS,  new DataHolderObject(dataitem.getBytes(),  dataitem.getSize()),
	     type, id, annotationID);
    }
	
    /**
     * Construct a FileIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param data some data as a DataAbstraction
     * @param type the type of the data
     * @param id an index ID
     * @param annotationID a ID for annotations
     */
    public FileIndexItem(Timestamp dataTS, Timestamp indexTS, DataAbstraction data, int type,
			 ID id, ID annotationID) {
	super(dataTS, indexTS,  data, type, id, annotationID);
    }
	
    /**
     * A ByteBuffer of the Data being indexed.
     * @return an empty buffer, if this IndexItem doesn't have the data to hand
     */
    public ByteBuffer getData() {
	setLastAccessTime(Clock.time.asMicros());

	if (data instanceof DataHolder) { // its a data holding object
	    return ((DataHolder)data).getBytes();
	} else {
	    return emptyBuffer;
	}
    }

    /**
     * Does this IndexItem actually hold the data.
     */
    public boolean hasData() {
	if (data instanceof DataHolder) { // its a data holding object
	    return true;
	} else {		// its NOT a data holding object
	    return false;
	}
    }

    /**
     * Get the DataAbstraction held by the IndexItem.
     */
    public DataAbstraction getDataAbstraction() {
	return data;
    }

    /**
     * Set the data to be a new DataAbstraction.
     */
    public ManagedFileIndexItem setData(DataAbstraction data) {
	this.data = data;
	return this;
    }

    /**
     * Get the file offset for the index for this index item.
     */
    public Offset getIndexOffset() {
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