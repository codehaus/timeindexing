// Block.java

package com.timeindexing.plugin;

import com.timeindexing.time.Timestamp;

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
public class Block implements ReaderPlugin {
    BufferedReader input = null;
    ByteBuffer block = null;
    int blockSize = 8192;
    boolean eof = false;

    /**
     * Construct a Block plugin from an InputStream.
     */
    public Block(InputStream inStream) {
	this( new BufferedReader (new InputStreamReader(inStream)));
	
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
     * Determine if the reader has hit EOF.
     */
    public  boolean isEOF() {
	return eof;
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
	return new DefaultReaderResult(block, null);
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
