// DefaultReaderResult.java

package com.timeindexing.plugin;

import com.timeindexing.time.Timestamp;
import java.nio.ByteBuffer;

/**
 * A default implementation for the value returned by a reader plugin.
 */
public class DefaultReaderResult implements ReaderResult {
    ByteBuffer data = null;
    Timestamp dataTS = null;

    /**
     * Construct a DefaultReaderResult
     */
    public DefaultReaderResult(ByteBuffer bb, Timestamp dTS) {
	data = bb;
	dataTS = dTS;
    }

    /**
     * Get the data associated with this ReaderResult.
     */
    public ByteBuffer getData() {
	return data;
    }

    /**
     * Get the data timestamp.
     * @return null if the data has no specific timestamp.
     */
    public Timestamp getDataTimestamp() {
	return dataTS;
    }
}
