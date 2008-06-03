// StringItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of StringItem.
 */
public class StringItem extends AbstractDataItem implements DataItem {
    /*
     * The string
     */
    String string = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a StringItem from a string.
     */
    public StringItem(String s) {
	string = s;
    }

    /**
     * Create a StringItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    StringItem(ByteBuffer b) {
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
	return DataType.STRING;
    }

    /**
     * Get the String object from this StringItem.
     * @return a String
     */
    public Object getObject() {
	if (string == null) {
	    deconstruct();
	}

	return string;
    }

    /**
     * Construct the ByteBuffer from the String.
     */
    private void construct() {
	theBuffer = ByteBuffer.wrap(string.getBytes());
    }

    /**
     * Deconstruct the ByteBuffer into a String
     */
    private void deconstruct() {
	// We need to construct a byte[] of the right size,
	// and extract the ByteBuffer into the byte[].
	// The we can construct a String around the byte[].
	int len = (int)getSize();

	byte[] theBytes = new byte[len];
	theBuffer.get(theBytes);

	string = new String(theBytes);
	
    }
}
