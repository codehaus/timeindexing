// DataAbstraction.java

package com.timeindexing.index;

import com.timeindexing.basic.Size;

/**
 * An interface that provides a common type for data objects.  
 * This interface is used by objects that hold values which are
 * implementations of a DataAbstraction.
 */
public interface DataAbstraction {
    /**
     * Get the size of the data.
     */

    public Size getSize();
}
