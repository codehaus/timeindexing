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
// ShortItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of ShortItem.
 */
public class ShortItem extends AbstractDataItem implements DataItem {
    /*
     * The short
     */
    Short theShort = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a ShortItem from a short
     */
    public ShortItem(short f) {
	theShort = new Short(f);
    }

    /**
     * Construct a ShortItem from an Short
     */
    public ShortItem(Short f) {
	theShort = f;
    }

    /**
     * Create a ShortItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    ShortItem(ByteBuffer b) {
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
	return DataType.SHORT;
    }

    /**
     * Get the Short object from this ShortItem.
     * @return a Short
     */
    public Object getObject() {
	if (theShort == null) {
	    deconstruct();
	}

	return theShort;
    }

    /**
     * Construct the ByteBuffer from the String.
     */
    private void construct() {
	theBuffer = ByteBuffer.allocate(2);
	theBuffer = theBuffer.putShort(theShort);
	theBuffer.rewind();
    }

    /**
     * Deconstruct the ByteBuffer into a String
     */
    private void deconstruct() {
	// We need to extract the Short from the ByteBuffer.
	// The we can construct an Short from that.
	short value = theBuffer.getShort();

	theShort = new Short(value);
	
    }
}
