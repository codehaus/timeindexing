// IncoreIndexItem.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimestampDecoder;
import com.timeindexing.time.Clock;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Size;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.data.DataItem;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

/**
 * A Full implementation of an IndexItem.
 * Every value is explicitly held.
 */
public class IncoreIndexItem implements IndexItem, ManagedIndexItem, Serializable {
    Timestamp dataTS = null;
    Timestamp indexTS = null;
    DataAbstraction data = null;
    Size size = null;
    DataType type = DataType.ANY_DT;
    ID id = null;
    ID annotationID = null;
    transient AbsolutePosition position = null;
    transient Index myIndex = null;
    transient Timestamp lastAccessTime = null;

    /**
     * Construct a IncoreIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param data some data as a Item
     * @param type the type of the data
     * @param id an index ID
     * @param annotationID an ID for annotations
     */
    public IncoreIndexItem(Timestamp dataTS, Timestamp indexTS, DataItem dataitem,
			 DataType type, ID id, ID annotationID) {
	this(dataTS, indexTS,  new DataHolderObject(dataitem.getBytes(),  dataitem.getSize()),
	     type, id, annotationID);
    }
	
    /**
     * Construct a IncoreIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param data some data as a Item
     * @param type the type of the data
     * @param id an index ID
     * @param annotationID an ID for annotations
     */
    protected IncoreIndexItem(Timestamp dataTS, Timestamp indexTS, DataAbstraction data, 
			    DataType type, ID id, ID annotationID) {
	this.dataTS = dataTS;
	this.indexTS = indexTS;
	this.size = data.getSize();
	this.data = data;
        this.type = type;
	this.id = id;
	this.annotationID = annotationID;
	lastAccessTime = Timestamp.ZERO;
    }
	
    /**
     * The timestamp of the current IndexItem.
     */
    public Timestamp getIndexTimestamp() {
	setLastAccessTime();
	return indexTS;
    }

    /**
     * The timestamp in the data of the current IndexItem.
     */
    public Timestamp getDataTimestamp() {
	setLastAccessTime();
	return dataTS;
    }

    /**
     * A ByteBuffer of the Data being indexed.
     */
    public ByteBuffer getData() {
	setLastAccessTime();
	return ((DataHolder)data).getBytes();
    }

    /**
     * The size of the data item being referenced.
     */
    public Size getDataSize() {
	setLastAccessTime();
	return size;
    }


    /**
     * The DataAbstraction of the Data being indexed.
     */
    public DataAbstraction getDataAbstraction() {
	setLastAccessTime();
	return data;
    }

    /**
     * The type of the data item being referenced.
     */
    public DataType getDataType() {
	setLastAccessTime();
	return type;
    }

    /**
     * The item ID.
     */
    public ID getItemID() {
	setLastAccessTime();
	return id;
    }

    /**
     * The ID of annotations associated with this IndexItem.
     */
    public ID getAnnotations() {
	setLastAccessTime();
	return annotationID;
    }


    /**
     * Get the index position this IndexItem is in.
     */
    public AbsolutePosition getPosition() {
	//setLastAccessTime();
	return position;
    }

    /**
     * Set the index position that this IndexItem is in.
     */
    public ManagedIndexItem setPosition(AbsolutePosition pos) {
	position = pos;
	return this;
    }

    /**
     * Get the index this IndexItem is in.
     */
    public Index getIndex() {
	setLastAccessTime();
	return myIndex;
    }

    /**
     * Set the index this IndexItem is in.
     */
    public ManagedIndexItem setIndex(Index index) {
	myIndex = index;
	return this;
    }


    /**
     * Get the last time this IndexItem was accessed.
     */
    public Timestamp getLastAccessTime() {
	return lastAccessTime;
    }

    /**
     * Set the last access time of the item.
     */
    public ManagedIndexItem setLastAccessTime() {
	lastAccessTime = Clock.time.asMicros();
	//System.err.println("Set last access time for IndexItem: " + position + " to " + getLastAccessTime());
	return this;
    }

    public boolean equals(Object obj) {
	//setLastAccessTime();
	
	return  super.equals(obj);

    }


    public int hashCode() {
	//setLastAccessTime();
	return (int)id.value(); // super.hashCode();  //myIndex.hashCode() + position.hashCode();
    }

    /** 
     * Write out the IncoreIndexItem.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
	out.writeLong(dataTS.value());
	out.writeLong(indexTS.value());
	out.writeLong(size.value());
	out.write(((DataHolder)data).getBytes().array());
	out.writeInt(type.value());
	out.writeLong(id.value());
	if (annotationID == null) {
	    out.writeLong(0);
	} else {
	    ;  // NEVER in this implementation 
	}
	out.flush();
    }

    /** 
     * Read in the IncoreIndexItem.
     */ 
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
	TimestampDecoder timestampD = new TimestampDecoder();
	long ts = 0;

	ts = in.readLong();
	dataTS = timestampD.decode(ts);

	ts = in.readLong();
	indexTS = timestampD.decode(ts);

	long sizeRead = in.readLong();
	size = new Size(sizeRead);

	byte [] tmpdata = new byte[(int)sizeRead];
	in.read(tmpdata, 0, (int)sizeRead);
	ByteBuffer buffer = ByteBuffer.wrap(tmpdata);
	data = new DataHolderObject(buffer, sizeRead);

	int typeValue = in.readInt();
	type = DataTypeDirectory.find(typeValue);

	id = new SID(in.readLong());
	
	long annotationReader = in.readLong();
	if (annotationReader == 0) {
	    annotationID = null;
	} else {
	    annotationID = new SID(annotationReader);
	}	
    }

}
