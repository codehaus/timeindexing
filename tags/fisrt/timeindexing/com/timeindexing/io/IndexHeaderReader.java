// IndexHeaderReader.java

package com.timeindexing.io;

import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * An index header reader
 * This is the generic interface to an index reader.
 */
public interface IndexHeaderReader {
    /**
     * Open an index header
     */
    public boolean open(String filename) throws IOException;

    /**
     * Is the index header open
     */
    public boolean isOpen();

    /**
     * Read an index header from the header stream.
     */
    public long read() throws IOException;

    /**
     * Close an index header reader.
     */
    public long close() throws IOException;
}
