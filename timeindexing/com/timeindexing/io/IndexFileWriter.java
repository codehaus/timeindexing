// IndexFileWriter.java

package com.timeindexing.io;

import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.index.IndexCreateException;

import java.io.IOException;

/**
 * An interface for writers of indexes.
 */
public interface IndexFileWriter { 
    public long open(IndexProperties indexProperties) throws IOException, IndexOpenException;

    /**
     * create an Index
     */
    public long create(IndexProperties indexProperties) throws IOException, IndexCreateException;

    /**
     * Write the header to the  index.
     * @param headerType the type of header, e.g FileType.INLINE_INDEX or FileType.EXTERNAL_INDEX
     */
    public long writeHeader(byte headerType) throws IOException;

    /**
     * Write an IndexItem to the  index.
     */
    public long writeItem(ManagedIndexItem item) throws IOException;

    /**
     * Get the append position
     */
    public long getAppendPosition();

    /**
     * Goto the append position
     */
    public boolean gotoAppendPosition() throws IOException;

    /**
     * Flush the  index.
     */
    public long flush() throws IOException;
    /**
     * Close the  index.
     */
    public long close() throws IOException;

}
