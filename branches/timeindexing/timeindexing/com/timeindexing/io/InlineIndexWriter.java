// IndexIndexWriter.java

package com.timeindexing.io;

import com.timeindexing.index.ManagedIndexItem;

import java.io.IOException;

/**
 * An interface for writers of inline indexes.
 */
public interface InlineIndexWriter { 
    public long open(String filename) throws IOException;

    /**
     * create an InlineIndex
     */
    public long create(String filename) throws IOException;

    /**
     * Write the header to the inline index.
     */
    public long writeHeader() throws IOException;

    /**
     * Write an IndexItem to the inline index.
     */
    public long writeItem(ManagedIndexItem item) throws IOException;
    /**
     * Flush the inline index.
     */
    public long flush() throws IOException;
    /**
     * Close the inline index.
     */
    public long close() throws IOException;

}
