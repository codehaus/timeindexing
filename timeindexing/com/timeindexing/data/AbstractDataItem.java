// AbstractDataItem.java

package com.timeindexing.data;

import java.nio.ByteBuffer;

import com.timeindexing.index.DataType;

/**
 * An abstract base class for all DataItem implementations.
 */
public abstract class AbstractDataItem implements DataItem {

    /**
     * Stringify.
     */
    public String toString() {
	return getObject().toString();
    }

}
