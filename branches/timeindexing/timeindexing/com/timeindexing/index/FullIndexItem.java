// FullIndexItem.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.TimestampDecoder;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.basic.SID;
import com.timeindexing.basic.Size;
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
public class FullIndexItem implements IndexItem, Serializable {
    Timestamp dataTS = null;
    Timestamp indexTS = null;
    DataAbstraction data = null;
    Size size = null;
    int type = DataType.ANY;
    ID id = null;
    ID annotationID = null;

    /**
     * Construct a FullIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param data some data as a Item
     * @param type the type of the data
     * @param id an index ID
     * @param annotationID an ID for annotations
     */
    public FullIndexItem(Timestamp dataTS, Timestamp indexTS, DataItem dataitem,
			 int type, ID id, ID annotationID) {
	this(dataTS, indexTS,  new DataHolderObject(dataitem.getBytes(),  dataitem.getSize()),
	     type, id, annotationID);
    }
	
    /**
     * Construct a FullIndexItem from
     * @param dataTS a data timestamp. The Data timestamp is the same as the Sender timestamp.
     * @param indexTS an index timestamp. The Index timestamp is the same as the Receiver timestamp.
     * @param data some data as a Item
     * @param type the type of the data
     * @param id an index ID
     * @param annotationID an ID for annotations
     */
    protected FullIndexItem(Timestamp dataTS, Timestamp indexTS, DataAbstraction data, 
			    int type, ID id, ID annotationID) {
	this.dataTS = dataTS;
	this.indexTS = indexTS;
	this.size = data.getSize();
	this.data = data;
        this.type = type;
	this.id = id;
	this.annotationID = annotationID;
    }
	
    /**
     * The timestamp of the current IndexItem.
     */
    public Timestamp getIndexTimestamp() {
	return indexTS;
    }

    /**
     * The timestamp in the data of the current IndexItem.
     */
    public Timestamp getDataTimestamp() {
	return dataTS;
    }

    /**
     * A ByteBuffer of the Data being indexed.
     */
    public ByteBuffer getData() {
	return ((DataHolder)data).getBytes();
    }
    /**
     * The size of the data item being referenced.
     */
    public Size getDataSize() {
	return size;
    }

    /**
     * The type of the data item being referenced.
     */
    public int getDataType() {
	return type;
    }

    /**
     * The item ID.
     */
    public ID getItemID() {
	return id;
    }

    /**
     * The ID of annotations associated with this IndexItem.
     */
    public ID getAnnotations() {
	return annotationID;
    }


    /** 
     * Write out the FullIndexItem.
     */
    private void writeObject(ObjectOutputStream out) throws IOException {
	out.writeLong(dataTS.value());
	out.writeLong(indexTS.value());
	out.writeLong(size.value());
	out.write(((DataHolder)data).getBytes().array());
	out.writeInt(type);
	out.writeLong(id.value());
	if (annotationID == null) {
	    out.writeLong(0);
	} else {
	    ;  // NEVER in this implementation 
	}
	out.flush();
    }

    /** 
     * Read in the FullIndexItem.
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

	type = in.readInt();
	id = new SID(in.readLong());
	
	long annotationReader = in.readLong();
	if (annotationReader == 0) {
	    annotationID = null;
	} else {
	    annotationID = new SID(annotationReader);
	}	
    }

}
