// DataReferenceObject.java

package com.timeindexing.index;

import com.timeindexing.basic.Offset;
import com.timeindexing.basic.Size;

/**
 * An implementations for objects that act as references
 * to data from an index.
 * These are used when data is not read into an
 * incore object, but a reference to it is held.
 */
public class DataReferenceObject implements DataReference  {
    /*
     * The offset into a file.
     */
    Offset offset = null;

    /*
     * The size of the data
     */
    Size size = null;

    /**
     * Construct a DataReference
     */
    public DataReferenceObject(Offset offsetR, Size sizeR) {
	offset = offsetR;
	size = sizeR;
    }

    /**
     * Get the offset in the underlying storage.
     */
    public Offset getOffset() {
	return offset;
    }

     /**
     * Get the size of the data in the underlying storage.
     */
    public Size getSize() {
	return size;
    }
}
