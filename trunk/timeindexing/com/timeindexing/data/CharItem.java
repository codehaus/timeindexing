// CharItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of CharItem.
 */
public class CharItem extends AbstractDataItem implements DataItem {
    /*
     * The char
     */
    Character theChar = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a CharItem from a char
     */
    public CharItem(char f) {
	theChar = new Character(f);
    }

    /**
     * Construct a CharItem from an Char
     */
    public CharItem(Character f) {
	theChar = f;
    }

    /**
     * Create a CharItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    CharItem(ByteBuffer b) {
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
	return DataType.CHAR;
    }

    /**
     * Get the Character object from this CharItem.
     * @return a Character
     */
    public Object getObject() {
	if (theChar == null) {
	    deconstruct();
	}

	return theChar;
    }

    /**
     * Construct the ByteBuffer from the String.
     */
    private void construct() {
	theBuffer = ByteBuffer.allocate(2);
	theBuffer = theBuffer.putChar(theChar);
	theBuffer.rewind();
    }

    /**
     * Deconstruct the ByteBuffer into a String
     */
    private void deconstruct() {
	// We need to extract the Char from the ByteBuffer.
	// The we can construct an Char from that.
	char value = theBuffer.getChar();

	theChar = new Character(value);
	
    }
}
