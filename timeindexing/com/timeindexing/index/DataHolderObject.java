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
// DataHolderObject.java

package com.timeindexing.index;

import com.timeindexing.basic.Size;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Clock;

import java.nio.ByteBuffer;


/**
 * An implementations for objects that act as holders
 * of data from an index.
 */
public class DataHolderObject implements DataHolder {
    ByteBuffer theBuffer = null;
    Size size = null;
    Timestamp lastAccessTime = null;
    Timestamp readTime = null;

    /**
     * Construct a new DataHolderObject
     */
    public DataHolderObject(ByteBuffer buffer, Size bufSize) {
	theBuffer = buffer;
	size = bufSize;
	readTime = Clock.time.time();
	lastAccessTime = Timestamp.ZERO;
    }

    /**
     * Construct a new DataHolderObject
     */
    public DataHolderObject(ByteBuffer buffer, long bufSize) {
	this(buffer, new Size(bufSize));
    }

    /**
     * Get the bytes.
     */
    public ByteBuffer getBytes() {
	lastAccessTime = Clock.time.time();
	// this returns a new Read Only Buffer
	return theBuffer.asReadOnlyBuffer();
    }

    /**
     * Get the size of the data.
     */
    public Size getSize() {
	return size;
    }

    /**
     * Get the last time this object was accessed.
     */
    public Timestamp getLastAccessTime() {
	return lastAccessTime;
    }

    /**
     * Get the time the data was read from storage into this object.
     */
    public Timestamp getReadTime() {
	return readTime;
    }
}
