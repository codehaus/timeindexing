Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
// IntegerItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of IntegerItem.
 */
public class IntegerItem extends AbstractDataItem implements DataItem {
    /*
     * The integer
     */
    Integer theInteger = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a IntegerItem from an int
     */
    public IntegerItem(int i) {
	theInteger = new Integer(i);
    }

    /**
     * Construct a IntegerItem from an Integer
     */
    public IntegerItem(Integer i) {
	theInteger = i;
    }

    /**
     * Create a IntegerItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    IntegerItem(ByteBuffer b) {
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
	return DataType.INTEGER;
    }

    /**
     * Get the Integer object from this IntegerItem.
     * @return an Integer
     */
    public Object getObject() {
	if (theInteger == null) {
	    deconstruct();
	}

	return theInteger;
    }

    /**
     * Construct the ByteBuffer from the String.
     */
    private void construct() {
	theBuffer = ByteBuffer.allocate(4);
	theBuffer = theBuffer.putInt(theInteger);
	theBuffer.rewind();
    }

    /**
     * Deconstruct the ByteBuffer into a String
     */
    private void deconstruct() {
	// We need to extract the Int from the ByteBuffer.
	// The we can construct an Integer from that.
	int value = theBuffer.getInt();

	theInteger = new Integer(value);
	
    }
}
