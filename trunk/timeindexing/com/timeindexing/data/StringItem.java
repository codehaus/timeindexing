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
