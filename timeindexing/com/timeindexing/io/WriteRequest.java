// WriteRequest.java


package com.timeindexing.io;

import com.timeindexing.util.ByteBufferRing;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * A class that represents a write request in the I/O thread.
 * It holds data on the channel being written to, the buffer to
 * write, and the ByteBufferRing the buffer came from.
 */
class WriteRequest {
    FileChannel channel = null;
    ByteBuffer buffer = null;
    ByteBufferRing ring = null;

    /**
     * Construct a WriteRequest.
     */
    public WriteRequest(FileChannel fc, ByteBuffer bb, ByteBufferRing r) {
	channel = fc;
	buffer = bb;
	ring = r;
    }
}

