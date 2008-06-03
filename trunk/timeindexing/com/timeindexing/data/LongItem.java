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



// LongItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of LongItem.
 */
public class LongItem extends AbstractDataItem implements DataItem {
    /*
     * The long
     */
    Long theLong = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a LongItem from a long
     */
    public LongItem(long f) {
	theLong = new Long(f);
    }

    /**
     * Construct a LongItem from an Long
     */
    public LongItem(Long f) {
	theLong = f;
    }

    /**
     * Create a LongItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    LongItem(ByteBuffer b) {
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
	return DataType.LONG;
    }

    /**
     * Get the Long object from this LongItem.
     * @return a Lonf
     */
    public Object getObject() {
	if (theLong == null) {
	    deconstruct();
	}

	return theLong;
    }

    /**
     * Construct the ByteBuffer from the String.
     */
    private void construct() {
	theBuffer = ByteBuffer.allocate(8);
	theBuffer = theBuffer.putLong(theLong);
	theBuffer.rewind();
    }

    /**
     * Deconstruct the ByteBuffer into a String
     */
    private void deconstruct() {
	// We need to extract the Long from the ByteBuffer.
	// The we can construct an Long from that.
	long value = theBuffer.getLong();

	theLong = new Long(value);
	
    }
}
