// ReaderResultItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;
import com.timeindexing.plugin.ReaderResult;
import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of ByteBufferItem.
 */
public class ReaderResultItem implements DataItem {
    /*
     * The reader result
     */
    ReaderResult result = null;

    /**
     * Construct a ReaderResultItem from a ReaderResult.
     */
    public ReaderResultItem(ReaderResult rr) {
	result = rr;
    }

    /**
     * Get the data itself
     */
    public ByteBuffer getBytes() {
	return result.getData();
    }

    /**
     * Get the size of the item
     */
    public long getSize() {
	//System.err.println("ReaderResultItem: size = " + getBytes().limit());
	return getBytes().limit();
    }

    /**
     * Get the DataType of the DataItem.
     */
    public DataType getDataType() {
	return result.getDataType();
    }
}
