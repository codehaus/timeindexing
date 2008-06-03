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
// DefaultWriter.java

package com.timeindexing.plugin;

import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexProperties;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.io.OutputStream;
import java.io.IOException;

/**
 * An default writer plugin.
 * Takes an IndexItem and writes the bytes to the output stream.
 */
public class DefaultWriter implements WriterPlugin {
    OutputStream out = null;
    WritableByteChannel channel = null;

    private final static int BUFSIZE = 1024;

    /*
     * The EOL mark �
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

	// pump out the data

	writeTotal = channel.write(itemdata);

	if (doEOL) {
	    ByteBuffer eolBuf = ByteBuffer.wrap(eolBytes);
	    writeCount = channel.write(eolBuf);
	    writeTotal += writeCount;
	}

	return writeTotal;
    }

    /**
     * Flush out any remainig data.
     */
    public long flush() throws IOException {
	out.flush();
	return 0;
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
	channel = Channels.newChannel(out);
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
