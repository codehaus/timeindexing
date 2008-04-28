// BufferedWriter.java

package com.timeindexing.plugin;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexProperties;

import java.nio.ByteBuffer;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A buffered writer plugin.
 * Takes an IndexItem and writes the bytes to the output stream
 * only when the buffer is full.
 */
public class BufferedWriter implements WriterPlugin {
    OutputStream out = null;

    private int BUFSIZE = 8192;

    // the output buffer
    byte [] outbuf = null;

    // the buffer position
    int bufPos = 0;

    /*
     * The EOL mark
     */
    static byte[] eol = null;

    static {
	eol = new Character((char)182).toString().getBytes();
    }

    /*
     * Newline
     */
    static byte[] nl = null;

    static {
	nl = new Character((char)10).toString().getBytes();
    }

    /**
     * Construct a BufferedWriter.
     * The default buffer size is 8192.
     */
    public BufferedWriter() {
	BUFSIZE = 8192;
	outbuf = new byte[BUFSIZE];
    }

    /**
     * Construct a BufferedWriter given a buffer size.
     */
    public BufferedWriter(int bufSize) {
	BUFSIZE = bufSize;
	outbuf = new byte[BUFSIZE];
    }

    /**
     * Construct a BufferedWriter given an OutputStream
     * and a buffer size.
     */
    public BufferedWriter(OutputStream out, int bufSize) {
	setOutputStream(out);
	BUFSIZE = bufSize;
	outbuf = new byte[BUFSIZE];
    }

    /**
     * Output properties include:
     * <ul>
     * <li> "eolmark" ->
     * <li> "newline" -> 
     * </ul>
     */
    public long write(IndexItem item, IndexProperties outputProperties) throws IOException {
	ByteBuffer itemdata = item.getData();
	long writeCount = 0;
	long writeTotal = 0;

	boolean doEOL = false;
	byte [] eolBytes = null;

	if (outputProperties != null) {
	    doEOL = Boolean.valueOf((String)outputProperties.get("newline")).booleanValue();
	    eolBytes = nl;
	}

	/*
	 * pump out the data
	 */

	// work out how much room is in the buffer
	int bufRoom = BUFSIZE - bufPos;

	// now grab some bytes from the data
	if (bufRoom >= itemdata.remaining()) {
	    int count = itemdata.remaining();
	    // the buffer can accomdate all the data
	    // so copy it to the buffer
	    itemdata.get(outbuf, 0, count);
	    bufPos += count;

	    // and return that we wrote 0 bytes;
	    return 0;
	    
	} else {
	    // the buffer will accomdate the first part of the data
	    itemdata.get(outbuf, 0, bufRoom);

	    // the buffer is now full, so write it out
	    writeCount = write(outbuf, BUFSIZE);
	    writeTotal += writeCount;


	    // we now try and output full buffers of data
	    while (itemdata.remaining() >= BUFSIZE) {
		itemdata.get(outbuf, 0, BUFSIZE);
		writeCount = write(outbuf, BUFSIZE);
		writeTotal += writeCount;
	    }

	    // now get the last bit of data into the buffer
	    if (itemdata.remaining() > 0) {
		int count = itemdata.remaining();

		itemdata.get(outbuf, 0, count);
		bufPos += count;
	    }


	    return writeTotal;
	}
    }

    /**
     * Flush out any remainig data.
     */
    public long flush() throws IOException {
	int count = bufPos;

	// flush the remaining buffered data
	write(outbuf, count);

	return count;
    }

    /**
     * Actually write the bytes out.
     */
    private long write(byte[] outbuf, int count) throws IOException {
	out.write(outbuf, 0, count);

	// reset the buffer
	bufPos = 0;
	
	return count;
    }

    /**
     * Get the OutputStream for the OutputPlugin.
     */
    public OutputStream getOutputStream() {
	return out;
    }

    /**
     * Set the OutputStream for the OutputPlugin.
     */
    public WriterPlugin setOutputStream(OutputStream outStream) {
	out = outStream;
	return this;
    }

    /**
     * Called as the first thing.
     * Useful for doing any processing before output starts.
     */
    public Object begin() throws IOException {
	return null;
    }

    /**
     * Called as the last thing.
     * Useful for doing any processing after output has finished.
     */
    public Object end() throws IOException {
	return null;
    }

  
 }
