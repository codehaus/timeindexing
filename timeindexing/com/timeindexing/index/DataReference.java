// DataReference.java

package com.timeindexing.index;

import com.timeindexing.basic.Offset;
import com.timeindexing.basic.Size;

/**
 * An interface for objects that act as references
 * to data from an index.
 * These are used when data is not read into an
 * incore object, but a reference to it is held.
 */
public interface DataReference extends DataAbstraction {
    /**
     * Get the offset in the underlying storage.
     */
    public Offset getOffset();

}
