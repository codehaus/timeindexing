// DefaultReader.java

package com.timeindexing.plugin;

import com.timeindexing.index.IndexItem;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.IOException;

/**
 * An default reader plugin.
 * Takes an IndexItem and writes the bytes to the input stream.
 */
public abstract  class DefaultReader implements ReaderPlugin {
    InputStream in = null;
    boolean eof = false;

    /**
     * Determine if the reader has hit EOF.
     */
    public  boolean isEOF() {
	return eof;
    }


    /**
     *  The reader has hit EOF.
     */
    public  ReaderPlugin setEOF() {
	eof = true;
	return this;
    }

     /**
     * Get the InputStream for the InputPlugin.
     */
    public InputStream getInputStream() {
	return in;
    }

    /**
     * Set the InputStream for the InputPlugin.
     */
    public ReaderPlugin setInputStream(InputStream inStream) {
	in = inStream;
	return this;
    }
  
 }
