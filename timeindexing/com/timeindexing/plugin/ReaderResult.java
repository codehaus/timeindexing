// ReaderResult.java

package com.timeindexing.plugin;

import com.timeindexing.time.Timestamp;
import com.timeindexing.index.DataType;
import java.nio.ByteBuffer;

/**
 * An interface for the value returned by a reader plugin.
 */
public interface ReaderResult {
    /**
     * Get the data associated with this ReaderResult.
     */
    public ByteBuffer getData();

    /**
     * Get the data timestamp.
     * @return null if the data has no specific timestamp.
     */
    public Timestamp getDataTimestamp();


    /**
     * Get the DataType for this data
     */
    public DataType getDataType();
       

}
