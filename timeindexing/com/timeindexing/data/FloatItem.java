// FloatItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of FloatItem.
 */
public class FloatItem extends AbstractDataItem implements DataItem {
    /*
     * The float
     */
    Float theFloat = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a FloatItem from a float
     */
    public FloatItem(float f) {
	theFloat = new Float(f);
    }

    /**
     * Construct a FloatItem from an Float
     */
    public FloatItem(Float f) {
	theFloat = f;
    }

    /**
     * Create a FloatItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    FloatItem(ByteBuffer b) {
	theBuffer = b;
    }

    /**
     * Get the data itself
     */
    public ByteBuffer getBytes() {
	if (theBuffer == null) {
	    construct();
	}

	return theBuffer;
    }

    /**
     * Get the size of the item
     */
    public long getSize() {
	if (theBuffer == null) {
	    construct();
	}

	return theBuffer.limit();
    }

    /**
     * Get the DataType of the DataItem.
     */
    public DataType getDataType() {
	return DataType.FLOAT;
    }

    /**
     * Get the Float object from this FloatItem.
     * @return a Float
     */
    public Object getObject() {
	if (theFloat == null) {
	    deconstruct();
	}

	return theFloat;
    }

    /**
     * Construct the ByteBuffer from the String.
     */
    private void construct() {
	theBuffer = ByteBuffer.allocate(4);
	theBuffer = theBuffer.putFloat(theFloat);
	theBuffer.rewind();
    }

    /**
     * Deconstruct the ByteBuffer into a String
     */
    private void deconstruct() {
	// We need to extract the Float from the ByteBuffer.
	// The we can construct an Float from that.
	float value = theBuffer.getFloat();

	theFloat = new Float(value);
	
    }
}
