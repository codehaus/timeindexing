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

    /*
     * The EOL mark ¶
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
     * Construct a DefaultWriter.
     */
    public DefaultWriter() {
    }

    /**
     * Construct a DefaultWriter given an OutputStream.
     */
    public DefaultWriter(OutputStream out) {
	setOutputStream(out);
    }

    /**
     * Output properties include:
     * <ul>
     * <li> "eolmark" -> ¶
     * <li> "newline" -> \n
     * </ul>
     */
    public long write(IndexItem item, IndexProperties outputProperties) throws IOException {
	ByteBuffer itemdata = item.getData();
	byte [] outbuf = new byte[BUFSIZE];
	long writeCount = 0;

	boolean doEOL = false;
	byte [] eolBytes = null;

	if (outputProperties != null) {
	    doEOL = Boolean.valueOf((String)outputProperties.get("newline")).booleanValue();
	    eolBytes = nl;
	}

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

	if (doEOL) {
	    out.write(eolBytes);
	    writeCount += eolBytes.length;
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
