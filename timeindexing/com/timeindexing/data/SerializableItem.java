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



// SerializableItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of SerializableItem.
 */
public class SerializableItem extends AbstractDataItem implements DataItem {
    /*
     * The object
     */
    Serializable object = null;

    /*
     * The ByteBuffer
     */
    ByteBuffer theBuffer = null;

    /**
     * Construct a SerializableItem from an object that is serializable
     */
    public SerializableItem(Serializable o) {
	object = o;
    }

    /**
     * Create a SerializableItem from a ByteBuffer.
     * This is used internally by DataItemFactory.
     */
    SerializableItem(ByteBuffer b) {
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
	return DataType.SERIALIZABLE;
    }

    /**
     * Get the Serializable object from this SerializableItem.
     * @return a Serializable object
     */
    public Object getObject() {
	if (object == null) {
	    deconstruct();
	}

	return object;
    }

    /**
     * Construct the ByteBuffer from the Serializable.
     */
    private void construct() {
	// Create a ByteArrayOutputStream to store the serialized 
	// version of the objects
	ByteArrayOutputStream bos = new ByteArrayOutputStream();

	try {
	    // Wrap the ByteArrayOutputStream in an ObjectOutputStream
	    ObjectOutputStream oos = new ObjectOutputStream(bos);
	    // and write the objects to the stream
	    oos.writeObject(object);
	    oos.close();
	} catch (Exception e) {
	    throw new Error("Failed to write serializable data for " + this);
	}

	// the bytes are now held in the ByteArrayOutputStream
	// so we get the bytes of the ByteArrayOutputStream
	byte[] rawBytes = bos.toByteArray();

	// and wrap them in a ByteBuffer
	theBuffer = ByteBuffer.wrap(rawBytes);
    }

    /**
     * Deconstruct the ByteBuffer into a Serializable
     */
    private void deconstruct() {
	// We need to construct a byte[] of the right size,
	// and extract the ByteBuffer into the byte[].
	// The we can construct a ByteArrayInputStream around the byte[].
	int len = (int)getSize();

	byte[] theBytes = new byte[len];
	theBuffer.get(theBytes);

	try {
	    ByteArrayInputStream bis = new ByteArrayInputStream(theBytes);

	    // Wrap the ByteArrayInputStream with an ObjectInputStream
	    ObjectInputStream ois = new ObjectInputStream(bis);

	    // and read the object
	    object = (Serializable)ois.readObject();

	    // and close the ObjectInputStream
	    ois.close();
	} catch (Exception e) {
	    throw new Error("Failed to read serializable data for " + this);
	}

    }
}
