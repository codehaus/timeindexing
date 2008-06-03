// DoubleItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of DoubleItem.
 */
public class DoubleItem extends AbstractDataItem implements DataItem {
    /*
     * The double
     */
    Double theDouble = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a DoubleItem from a double
     */
    public DoubleItem(double d) {
	theDouble = new Double(d);
    }

    /**
     * Construct a DoubleItem from an Double
     */
    public DoubleItem(Double d) {
	theDouble = d;
    }

    /**
     * Create a DoubleItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    DoubleItem(ByteBuffer b) {
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
	return DataType.DOUBLE;
    }

    /**
     * Get the Double object from this DoubleItem.
     * @return a Double
     */
    public Object getObject() {
	if (theDouble == null) {
	    deconstruct();
	}

	return theDouble;
    }

    /**
     * Construct the ByteBuffer from the String.
     */
    private void construct() {
	theBuffer = ByteBuffer.allocate(8);
	theBuffer = theBuffer.putDouble(theDouble);
	theBuffer.rewind();
    }

    /**
     * Deconstruct the ByteBuffer into a String
     */
    private void deconstruct() {
	// We need to extract the Double from the ByteBuffer.
	// The we can construct an Double from that.
	double value = theBuffer.getDouble();

	theDouble = new Double(value);
	
    }
}
