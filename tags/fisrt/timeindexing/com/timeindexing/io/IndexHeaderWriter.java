// IndexHeaderWriter.java

package com.timeindexing.io;

import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * An index header writer
 * This is the generic interface to an index writer.
 */
public interface IndexHeaderWriter {
    /**
     * Flush the current values to the header file.
     */
    public long flush() throws IOException;

    /**
     * Write the contents of the header file out
     * It assumes the header file is alreayd open for writing.
     */
    public long write() throws IOException;

    /**
     * Open an index header
     */
    public boolean open(String filename) throws IOException;

    /**
     * Create an index header
     */
    public boolean create(String filename) throws IOException;

    /**
     * Is the index header open
     */
    public boolean isOpen();

    /**
     * Close an index header reader.
     */
    public long close() throws IOException;

}
