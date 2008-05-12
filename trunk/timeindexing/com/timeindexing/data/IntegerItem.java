// IntegerItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of IntegerItem.
 */
public class IntegerItem implements DataItem {
    /*
     * The integer
     */
    ByteBuffer theBuffer = ByteBuffer.allocate(4);

    /**
     * Construct a IntegerItem from an Integer
     */
    public IntegerItem(int i) {
	theBuffer = theBuffer.putInt(i);
	theBuffer.rewind();
    }

    /**
     * Get the data itself
     */
    public ByteBuffer getBytes() {
	return theBuffer;
    }

    /**
     * Get the size of the item
     */
    public long getSize() {
	return theBuffer.limit();
    }

    /**
     * Get the DataType of the DataItem.
     */
    public DataType getDataType() {
	return DataType.INTEGER;
    }
}
