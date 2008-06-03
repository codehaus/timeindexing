// BigDecimalItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;
import java.math.BigInteger;
import java.math.BigDecimal;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of BigDecimalItem.
 */
public class BigDecimalItem extends AbstractDataItem implements DataItem {
    /*
     * The decimal
     */
    BigDecimal decimal = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a BigDecimalItem from a big decimal.
     */
    public BigDecimalItem(BigDecimal s) {
	decimal = s;
    }

    /**
     * Create a BigDecimalItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    BigDecimalItem(ByteBuffer b) {
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
	return DataType.BIG_DECIMAL;
    }

    /**
     * Get the BigDecimal object from this BigDecimalItem.
     * @return a BigDecimal
     */
    public Object getObject() {
	if (decimal == null) {
	    deconstruct();
	}

	return decimal;
    }

    /**
     * Construct the ByteBuffer from the BigDecimal.
     */
    private void construct() {
	// A big decimal is represented as a BigInteger plus an integer scale
	BigInteger integer = decimal.unscaledValue();
	int scale = decimal.scale();

	// now allocate a buffer big enough for the BigInteger and the int
	byte[] bigIntArray = integer.toByteArray();

	theBuffer = ByteBuffer.allocate(bigIntArray.length + 4);

	// now put the scale and the integer in the ByteBuffer
	theBuffer.putInt(scale);
	theBuffer.put(bigIntArray);
	theBuffer.rewind();
    }

    /**
     * Deconstruct the ByteBuffer into a BigDecimal
     */
    private void deconstruct() {
	// We need to construct a byte[] of the right size,
	// and extract the ByteBuffer into the byte[].
	// The we can construct a BigInteger around the byte[].
	int len = (int)getSize();

	// subtract 4, as we don;t need the scale
	byte[] theBytes = new byte[len-4];

	// get the scale
	int scale = theBuffer.getInt();

	// now get the BigInteger
	theBuffer.get(theBytes);

	// construct the BigDecimal from a BigInteger and a scale
	decimal = new BigDecimal(new BigInteger(theBytes), scale);
	
    }
}
