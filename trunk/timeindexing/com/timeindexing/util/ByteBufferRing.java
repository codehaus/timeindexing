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



// ByteBufferRing.java

package com.timeindexing.util;

import java.nio.ByteBuffer;

/**
 * A ring of ByteBuffers.
 * There will be a 'current' buffer in the ring.
 * A buffer can be locked, so it can;t be used again.
 * A buffer can be freed, so it can be used again.
 */
public class ByteBufferRing {
    // A doubly linked list of ByteBuffers that acts as the ring
    DoubleLinkedList ring = null;

    // A doubly linked list of buffers that are locked
    DoubleLinkedList locked = null;

    // the size of the ring
    int ringSize = 0;

    // the size of the buffers in the ring
    int bufferSize = 0;

    // The current buffer position in the ring
    int currentBufferPosition = 0;

    /**
     * Construct a ByteBufferRing with N ByteBuffers.
     */
    public ByteBufferRing(int count, int bufSize) {
	// allocate  the DoubleLinkedList to hold the buffers
	ring = new DoubleLinkedList();
	locked = new DoubleLinkedList();

	bufferSize = bufSize;
	currentBufferPosition = 0;

	// now allocate the buffer space
	grow(count);
    }

    /**
     * Get the size of the ring.
     */
    public int size() {
	return ringSize;
    }

    /**
     * Get the size of the buffers in the ring.
     */
    public int getBufferSize() {
	return bufferSize;
    }

    /**
     * Get the current buffer.
     */
    public ByteBuffer current() {
	if (free() > 0) {
	    return (ByteBuffer)ring.get(currentBufferPosition);
	} else {
	    return null;
	}
    }

    /**
     * How many free buffers are there.
     */
    public  int free() {
	return (int)ring.size();
    }

    /**
     * Lock the current buffer.
     * Returns the no of free buffers.
     */
    public int lock() {
	// get the current buffer
	ByteBuffer buffer = (ByteBuffer)ring.get(currentBufferPosition);

	// remove it from the ring
	ring.remove(buffer);

	// put it in the locked list
	locked.add(buffer);

	if (free() == 0) {
	    // were out of buffers
	    grow(ringSize);
	}

	// update the current buffer position
	currentBufferPosition = (currentBufferPosition + 1) % free();

	return free();
    }

    /**
     * Unlock a buffer.
     * Returns the no of free buffers.
     */
    public int unlock(ByteBuffer buffer) {
	// remove it from the locked list
	locked.remove(buffer);

	// put it in the locked list
	ring.add(buffer);

	return free();
    }

    /**
     * Grow the ring by N new buffers.
     */
    public int grow(int growSize) {
	// now allocate the buffer space
	for (int i=0; i < growSize; i++) {
	    ByteBuffer aBuffer = ByteBuffer.allocate(bufferSize);

	    ring.add(aBuffer);
	}

	ringSize += growSize;

	//System.err.println("ByteBufferRing grown to " + size());
	return size();
	
    }

    /**
     * String
     */
    public String toString() {
	return "ByteBufferRing " +
	    "size: " + size() +
	    " free: " + free() +
	    " locked: " + locked.size() +
	    " position: " + currentBufferPosition;
    }
	    
    
}
