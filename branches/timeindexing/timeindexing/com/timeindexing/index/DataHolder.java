// DataHolder.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;

import java.nio.ByteBuffer;

/**
 * An interface for objects that act as holders
 * of data from an index.
 */
public interface DataHolder extends DataAbstraction {
    /**
     * Get the bytes.
     */
    public ByteBuffer getBytes();

    /**
     * Get the last time this object was accessed.
     */
    public Timestamp getLastAccessTime();

    /**
     * Get the time the data was read from storage into this object.
     */
    public Timestamp getReadTime();
}
