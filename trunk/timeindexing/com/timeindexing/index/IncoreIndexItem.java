// IncoreIndexItem.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimestampDecoder;
import com.timeindexing.time.Clock;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Size;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.data.DataItem;

import java.io.Serializable;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;
import java.net.URI;
import java.util.Properties;

/**
 * A Full implementation of an IndexItem.
 * Every value is explicitly held.
 */
public class IncoreIndexItem implements IndexItem, ManagedIndexItem {
    transient Timestamp dataTS = null;
    transient Timestamp indexTS = null;
    transient DataAbstraction data = null;
    transient Size size = null;
    transient DataType type = DataType.ANY;
    transient ID id = null;
    transient long annotationValue = 0;
    transient AbsolutePosition position = null;
    transient Index theIndex = null;
    transient Timestamp lastAccessTime = null;


    final static ByteBuffer EMPTY_BUFFER = ByteBuffer.allocate(0);

    /**
     * Construct a IncoreIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param dataitem some data as a Item
     * @param type the type of the data
     * @param id an index ID
     * @param annotationValue the meta data for annotations
     */
    public IncoreIndexItem(Timestamp dataTS, Timestamp indexTS, DataItem dataitem,
			 DataType type, ID id, long annotationValue) {
	this(dataTS, indexTS,  new DataHolderObject(dataitem.getBytes(),  dataitem.getSize()),
	     type, id, annotationValue);
    }
	
    /**
     * Construct a IncoreIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param data some data as a Item
     * @param type the type of the data
     * @param id an index ID
     * @param annotationValue the meta data for annotations
     */
    protected IncoreIndexItem(Timestamp dataTS, Timestamp indexTS, DataAbstraction data, 
			    DataType type, ID id, long annotationValue) {
	this(dataTS, indexTS,  data, data.getSize(), type, id, annotationValue);
    }
	
    /**
     * Construct a IncoreIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param data some data as a Item
     * @param type the type of the data
     * @param id an index ID
     * @param annotationValue the meta data for annotations
     */
    protected IncoreIndexItem(Timestamp dataTS, Timestamp indexTS, DataAbstraction data, 
			    Size dataSize, DataType type, ID id, long annotationValue) {
	this.dataTS = dataTS;
	this.indexTS = indexTS;
	this.data = data;
	this.size = dataSize;
        this.type = type;
	this.id = id;
	this.annotationValue = annotationValue;
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
     * The meta data of annotations associated with this IndexItem.
     */
    public long getAnnotationMetaData() {
	setLastAccessTime();
	return annotationValue;
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
	return theIndex;
    }

    /**
     * Set the index this IndexItem is in.
     */
    public ManagedIndexItem setIndex(Index index) {
	theIndex = index;
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
	lastAccessTime = Clock.time.time();
	//System.err.println("Set last access time for IndexItem: " + position + " to " + getLastAccessTime());
	return this;
    }

    /** 
     * Is the data held by the IndexItem, actually an IndexReference.
     */
    public boolean isReference() {
	if (type.equals(DataType.REFERENCE)) {
	    return true;
	} else {
	    return false;
	}
    }

    /**
     * Follow this reference.
     * @return null if the DataAbstraction is not an IndexReference
     * @throws GetItemException if the reference cannot be followed successfully
     */
    public IndexItem follow() throws GetItemException, IndexClosedException {
	if (! isReference()) {	// this is not a reference
	    return null;
	} else {
	    ManagedIndex myIndex = (ManagedIndex)getIndex();
	    IndexReference reference = (IndexReference)data;

	    Index otherIndex = null;
	    IndexItem otherItem = null;

	    URI otherIndexURI = reference.getIndexURI();
	    ID otherIndexID = reference.getIndexID();
	    Position otherItemPosition = reference.getIndexItemPosition();

	    TimeIndexFactory indexFactory = new TimeIndexFactory();

	    if (otherIndexURI.isOpaque()) {
		// its a URI for an IncoreIndex.

		if (myIndex.isTrackingIndex(otherIndexID)) {
		    // we already have that index
		    otherIndex = myIndex.getTrackedIndex(otherIndexID);

		    // get the index item
		    otherItem = otherIndex.getItem(otherItemPosition);

		    return otherItem;
		} else {
		    // we haven't seen that index before
		    if ((otherIndex = indexFactory.find(otherIndexID)) != null) {
			// we found the other index
			myIndex.trackReferencedIndex(otherIndex);

			// get the index item
			otherItem = otherIndex.getItem(otherItemPosition);

			return otherItem;
		    } else {
			// the Index is non existant
			throw new GetItemException("Index with URI  " + otherIndexURI +
					       " does not exist, so item at position " +
					       otherItemPosition + " cannot be fetched");
		    }
		}

	    
	    } else {
		// its a URI for a FileIndex.

		if (myIndex.isTrackingIndex(otherIndexID)) {
		    // we already have that index
		    otherIndex = myIndex.getTrackedIndex(otherIndexID);

		    // get the index item
		    otherItem = otherIndex.getItem(otherItemPosition);

		    return otherItem;
		} else {
		    // we haven't seen that index before
		    if ((otherIndex = indexFactory.find(otherIndexID)) != null) {
			// we found the other index, and it's already open
			myIndex.trackReferencedIndex(otherIndex);

			// so get the IndexItem
			otherItem = otherIndex.getItem(otherItemPosition);

			return otherItem;
		    } else {
			try {
			    System.err.println("follow: opening " + otherIndexURI.getPath());
			    // we need to open the Index to get the IndexItem
			    Properties properties = new Properties();

			    // set the filename property
			    properties.setProperty("indexpath", otherIndexURI.getPath());

			    // try opening it
			    otherIndex = indexFactory.open(properties);

			    // keep track of the opened index
			    myIndex.trackReferencedIndex(otherIndex);

			    // get the IndexItem
			    otherItem = otherIndex.getItem(otherItemPosition);

			    return otherItem;
			} catch (TimeIndexException tie) {
			    // we cant; open the index
			    throw new GetItemException("Index with URI  " + otherIndexURI +
						       " does not exist, so item at position " +
						       otherItemPosition + " cannot be fetched");
			}
		    }	
		}
	    }
	}
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
	out.writeLong(annotationValue);
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
	ByteBuffer buffer = ByteBuffer.wrap(tmpdata);
	data = new DataHolderObject(buffer, sizeRead);

	int typeValue = in.readInt();
	type = DataTypeDirectory.find(typeValue);

	id = new SID(in.readLong());
	
	annotationValue = in.readLong();

    }

}
