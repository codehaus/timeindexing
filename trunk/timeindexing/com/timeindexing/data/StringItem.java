// StringItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * A item of data presented by a data reader
 * This is an implementation of StringItem.
 */
public class StringItem implements DataItem {
    /*
     * The string
     */
    ByteBuffer stringBuffer = null;

    /**
     * Construct a StringItem from a string.
     */
    public StringItem(String s) {
	stringBuffer = ByteBuffer.wrap(s.getBytes());
    }

    /**
     * Get the data itself
     */
    public ByteBuffer getBytes() {
	return stringBuffer;
    }

    /**
     * Get the size of the item
     */
    public long getSize() {
	return stringBuffer.limit();
    }

    /**
     * Get the DataType of the DataItem.
     */
    public DataType getDataType() {
	return DataType.STRING;
    }
}
