// IndexIndexreader.java

package com.timeindexing.io;

import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.index.DataReference;
import com.timeindexing.index.DataHolderObject;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * An interface for readers of inline indexes.
 */
public interface InlineIndexReader { 
    public long open(String filename) throws IOException;

    /**
     * Read an index header from the header stream.
     */
    public long readHeader() throws IOException;

    /**
     * Read the contents of the item
     * It assumes the index file is alreayd open for writing.
     * @param position the byte offset in the file to start reading an item from
     * @param withData read the data for this IndexItem if withData is true,
     * the data needs to be read at a later time, otherwise
     */
    public ManagedIndexItem readItem(long position, boolean withData) throws IOException;

    /**
     * Read some data, given an offset and a size.
     */
    public ByteBuffer readData(long offset, long size) throws IOException;

    /**
     * Read some data, given a DataReferenceObject
     */
    public ByteBuffer readData(DataReference ref)  throws IOException;

    /**
     * Read some data, given a DataReference
     * and return it as a DataHolderObject.
     */
    public DataHolderObject convertDataReference(DataReference ref) ;

    /**
     * Load the index data, based on a specified LoadStyle.
     */
    public long loadIndex(LoadStyle loadStyle) throws IOException;

    /**
     * Seek to a certain position.
     * @return true if actually had to move the position,
     * returns false if in correct place
     */
    public boolean seek(long position) throws IOException;

    /**
     * Close the inline index.
     */
    public long close() throws IOException;
}
