// IndexNavigator.java

package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.basic.Position;

import java.io.IOException;

/**
 * An index navigator
 * This is the generic interface to an index navigator.
 */
public interface IndexNavigator {
    /**
     * Read an index item from an index stream.
     */
    public IndexItem read() throws IOException;

    /**
     * Close an index navigator.
     */
    public void close() throws IOException;

    /**
     * Is the navigator ready to present more data.
     */
    public boolean ready() throws IOException;

    /**
     * Goto the IndexItem where
     * indexItem.getIndexTimestamp() >= t
     */
    public IndexItem findIndexTimestamp(Timestamp t) throws IOException;

    /**
     * Goto the IndexItem where
     * indexItem.getDataTimestamp() >= t
     */
    public IndexItem findDataTimestamp(Timestamp t) throws IOException;

    /**
     * Goto the nth position of IndexItems.
     */
    public long gotoPosition(Position p) throws IOException;


    /**
     * Skip n IndexItems.
     */
    public long skip(long n) throws IOException;

    /**
     * Reset the index to the zeroth Position IndexItem
     * This is equivalent to gotoPosition(Position 0)
     */
    public void reset() throws IOException;
}

