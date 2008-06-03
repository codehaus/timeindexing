/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
