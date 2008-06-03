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



// Block.java

package com.timeindexing.plugin;

import com.timeindexing.time.Timestamp;
import com.timeindexing.index.DataType;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;


/**
 * A plugin that takes an input stream and
 * returns a block at a time.
 */
public class Block extends DefaultReader implements ReaderPlugin {
    BufferedReader input = null;
    ByteBuffer block = null;
    int blockSize = 8192;

    /**
     * Construct a Block plugin from an InputStream.
     */
    public Block(InputStream inStream) {
	this( new BufferedReader (new InputStreamReader(inStream)));
	in = inStream;	
    }

    /**
     * Construct a Block plugin from a Reader.
     */
    public Block(Reader inReader) {
	this(new BufferedReader (inReader));
    }

    /**
     * Construct a Block plugin from a BufferedReader.
     */
    public Block(BufferedReader inReader) {
	input = inReader;
    }

    /**
     * Get next input buffer.
     */
    public ReaderResult read() throws IOException {
	// allocate the buffer first time through
	if (block == null) {
	    block = ByteBuffer.allocate(blockSize);
	}


	int ch = -1;
	block.clear();


	while (!eof) {
	    if ((ch = input.read()) == -1) {
		// hit EOF
		eof = true;
		// return the processed block
		return process(block);
	    } else {
		if (block.position() == block.limit()) {	// the block is full
		    // return the processed block
		    return process(block);
		} else {
		    block.put((byte)ch);
		}
	    }
	}
	return null;
    }



    /**
     * Process the block
     */
    protected ReaderResult process(ByteBuffer block) {
	// return the block as a ReaderResult

	block.limit(block.position());
	block.position(0);

	/*
	System.err.println("Block: " +
			   " B(P) = " + block.position() + 
			   " B(L) = " + block.limit() +
			   " B(C) = " + block.capacity());

	*/
	return new DefaultReaderResult(block, null, DataType.ANY);
    }

    /**
     * Get the block size.
     */
    public int getBlockSize() {
	return blockSize;
    }

    /**
     * Set the block size, in incrments of 1K.
     * Returns the actual value set.
     */
    public int setBlockSize(int noOfK) {
	blockSize = noOfK * 1024;
	return blockSize;
    }

    

}
