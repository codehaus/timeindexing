// DataReader.java

package com.timeindexing.data;

import java.io.IOException;

/**
 * A data reader gets data from the application.
 * This is the generic interface to an data reader.
 */
public interface DataReader {
    /**
     * Read an item of data from the input
     */
    public DataItem read() throws IOException;

    /**
     * Close an data reader.
     */
    public boolean close() throws IOException;

    /**
     * Is the reader ready to present more data.
     */
    public boolean ready() throws IOException;
}
