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



// BigIntegerItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;
import java.math.BigInteger;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of BigIntegerItem.
 */
public class BigIntegerItem extends AbstractDataItem implements DataItem {
    /*
     * The integer
     */
    BigInteger integer = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a BigIntegerItem from a big integer.
     */
    public BigIntegerItem(BigInteger s) {
	integer = s;
    }

    /**
     * Create a BigIntegerItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    BigIntegerItem(ByteBuffer b) {
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
	return DataType.BIG_INTEGER;
    }

    /**
     * Get the BigInteger object from this BigIntegerItem.
     * @return a BigInteger
     */
    public Object getObject() {
	if (integer == null) {
	    deconstruct();
	}

	return integer;
    }

    /**
     * Construct the ByteBuffer from the BigInteger.
     */
    private void construct() {
	theBuffer = ByteBuffer.wrap(integer.toByteArray());
    }

    /**
     * Deconstruct the ByteBuffer into a BigInteger
     */
    private void deconstruct() {
	// We need to construct a byte[] of the right size,
	// and extract the ByteBuffer into the byte[].
	// The we can construct a Integer around the byte[].
	int len = (int)getSize();

	byte[] theBytes = new byte[len];
	theBuffer.get(theBytes);

	integer = new BigInteger(theBytes);
	
    }
}
