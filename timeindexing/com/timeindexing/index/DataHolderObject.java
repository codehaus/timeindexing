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
	return theBuffer;
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
