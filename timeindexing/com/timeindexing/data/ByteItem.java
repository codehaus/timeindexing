// ByteItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of ByteItem.
 */
public class ByteItem extends AbstractDataItem implements DataItem {
    /*
     * The byte
     */
    Byte theByte = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a ByteItem from a byte
     */
    public ByteItem(byte f) {
	theByte = new Byte(f);
    }

    /**
     * Construct a ByteItem from an Byte
     */
    public ByteItem(Byte f) {
	theByte = f;
    }

    /**
     * Create a ByteItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    ByteItem(ByteBuffer b) {
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
	return DataType.BYTE;
    }

    /**
     * Get the Byte object from this ByteItem.
     * @return a Byte
     */
    public Object getObject() {
	if (theByte == null) {
	    deconstruct();
	}

	return theByte;
    }

    /**
     * Construct the ByteBuffer from the String.
     */
    private void construct() {
	theBuffer = ByteBuffer.allocate(1);
	theBuffer = theBuffer.put(theByte);
	theBuffer.rewind();
    }

    /**
     * Deconstruct the ByteBuffer into a String
     */
    private void deconstruct() {
	// We need to extract the Byte from the ByteBuffer.
	// The we can construct an Byte from that.
	byte value = theBuffer.get();

	theByte = new Byte(value);
	
    }
}
