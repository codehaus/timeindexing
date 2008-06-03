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
// ByteBufferItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of ByteBufferItem.
 */
public class ByteBufferItem implements DataItem {
    //The data
    ByteBuffer data = null;

    // the data type
    DataType dataType = null;

    /**
     * Construct a ByteBufferItem from a ByteBuffer.
     */
    public ByteBufferItem(ByteBuffer bb) {
	data = bb;
	dataType = DataType.ANY;
    }

    /**
     * Construct a ByteBufferItem from a ByteBuffer and a DataType
     */
    public ByteBufferItem(ByteBuffer bb, DataType type) {
	data = bb;
	dataType = type;
    }

    /**
     * Get the data itself
     */
    public ByteBuffer getBytes() {
	return data;
    }

    /**
     * Get the size of the item
     */
    public long getSize() {
	//System.err.println("ByteBufferItem: size = " + data.limit());
	return data.limit();
    }

    /**
     * Get the DataType of the DataItem.
     */
    public DataType getDataType() {
	return dataType;
    }


    /**
     * Get the ByteBuffer object from this ByteBufferItem.
     * @return a ByteBuffer
     */
    public Object getObject() {
	return data;
    }


}
