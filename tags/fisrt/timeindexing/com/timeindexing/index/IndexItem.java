//IndexItem.java

package com.timeindexing.index;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.Size;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.time.Timestamp;

import java.nio.ByteBuffer;

/**
 * Values that an index item must return.
 */
public interface IndexItem {
    /**
     * The timestamp in the data of the current IndexItem.
     * The Data timestamp is the same as the Sender timestamp.
     */
    public Timestamp getDataTimestamp();

    /**
     * The timestamp of the current IndexItem.
     * The Index timestamp is the same as the Receiver timestamp.
     */
    public Timestamp getIndexTimestamp();

    /**
     * A reference to the Data being indexed.
     */
    public ByteBuffer getData();

    /**
     * The size of the data item being referenced.
     */
    public Size getDataSize();

    /**
     * The type of the data item being referenced.
     */
    public int getDataType();

    /**
     * The index ID.
     */
    public ID getItemID();

    /**
     * The ID of annotations associated with this IndexItem.
     */
    public ID getAnnotations();

    /**
     * Get the index position this IndexItem is in.
     */
    public AbsolutePosition getPosition();

    /**
     * Get the index this IndexItem is in.
     */
    public Index getIndex();

    /**
     * Get the last time this object was accessed.
     */
    public Timestamp getLastAccessTime();

}
