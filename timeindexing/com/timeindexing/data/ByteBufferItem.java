// ByteBufferItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of ByteBufferItem.
 */
public class ByteBufferItem implements DataItem {
    /*
     * The string
     */
    ByteBuffer data = null;

    /**
     * Construct a ByteBufferItem from a string.
     */
    public ByteBufferItem(ByteBuffer bb) {
	data = bb;
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
	return DataType.ANY_DT;
    }
}
