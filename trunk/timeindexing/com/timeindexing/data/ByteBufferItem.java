// ByteBufferItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of ByteBufferItem.
 */
public class ByteBufferItem implements DataItem {
    //The data
    ByteBuffer data = null;

    // the data type
    DataType dataType = null;

    /**
     * Construct a ByteBufferItem from a ByteBuffer.
     */
    public ByteBufferItem(ByteBuffer bb) {
	data = bb;
	dataType = DataType.ANY;
    }

    /**
     * Construct a ByteBufferItem from a ByteBuffer and a DataType
     */
    public ByteBufferItem(ByteBuffer bb, DataType type) {
	data = bb;
	dataType = type;
    }

    /**
     * Get the data itself
     */
    public ByteBuffer getBytes() {
	return data;
    }

    /**
     * Get the size of the item
     */
    public long getSize() {
	//System.err.println("ByteBufferItem: size = " + data.limit());
	return data.limit();
    }

    /**
     * Get the DataType of the DataItem.
     */
    public DataType getDataType() {
	return dataType;
    }
}
