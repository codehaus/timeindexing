// DefaultWriter.java

package com.timeindexing.plugin;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexProperties;

import java.nio.ByteBuffer;
import java.io.OutputStream;
import java.io.IOException;

/**
 * An default writer plugin.
 * Takes an IndexItem and writes the bytes to the output stream.
 */
public class DefaultWriter implements WriterPlugin {
    OutputStream out = null;

    private final static int BUFSIZE = 1024;
    static byte[] eol = null;

    static {
	eol = new Character((char)182).toString().getBytes();
    }

    /**
     * 
     */
    public long write(IndexItem item, IndexProperties outputProperties) throws IOException {
	ByteBuffer itemdata = item.getData();
	byte [] outbuf = new byte[BUFSIZE];
	long writeCount = 0;

	boolean doMarkEOL = Boolean.valueOf((String)outputProperties.get("newline")).booleanValue();

	// pump out the data

	while (itemdata.remaining() >= BUFSIZE) {
	    itemdata.get(outbuf, 0, BUFSIZE);
	    out.write(outbuf);
	    writeCount += BUFSIZE;
	}
	int remaining = itemdata.remaining();
	itemdata.get(outbuf, 0, remaining);
	out.write(outbuf, 0, remaining);
	writeCount += remaining;

	if (doMarkEOL) {
	    out.write(eol);
	    writeCount += eol.length;
	}

	return writeCount;
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
  
 }
