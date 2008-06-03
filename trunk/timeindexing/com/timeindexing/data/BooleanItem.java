// BooleanItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of BooleanItem.
 */
public class BooleanItem extends AbstractDataItem implements DataItem {
    /*
     * The boolean
     */
    Boolean theBoolean = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a BooleanItem from a boolean
     */
    public BooleanItem(boolean f) {
	theBoolean = new Boolean(f);
    }

    /**
     * Construct a BooleanItem from an Boolean
     */
    public BooleanItem(Boolean f) {
	theBoolean = f;
    }

    /**
     * Create a BooleanItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    BooleanItem(ByteBuffer b) {
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
	return DataType.BOOLEAN;
    }

    /**
     * Get the Boolean object from this BooleanItem.
     * @return a Boolean
     */
    public Object getObject() {
	if (theBoolean == null) {
	    deconstruct();
	}

	return theBoolean;
    }

    /**
     * Construct the ByteBuffer from the String.
     */
    private void construct() {
	theBuffer = ByteBuffer.allocate(1);

	byte respesentation = (byte)(theBoolean ? 127 : 0);

	theBuffer = theBuffer.put(respesentation);
	theBuffer.rewind();
    }

    /**
     * Deconstruct the ByteBuffer into a String
     */
    private void deconstruct() {
	// We need to extract the Boolean from the ByteBuffer.
	// The we can construct an Boolean from that.
	byte value = theBuffer.get();

	theBoolean = new Boolean(value == 0 ? false : true);
	
    }
}
