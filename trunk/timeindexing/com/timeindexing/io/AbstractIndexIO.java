// AbstractIndexIO.java

package com.timeindexing.io;

import com.timeindexing.index.StoredIndex;

public abstract class AbstractIndexIO implements IndexInteractor {
    // The index this is doing I/O for
    StoredIndex myIndex = null;

    /**
     * Get the index which this is doing I/O for.
     */
    public StoredIndex getIndex() {
	return myIndex;
    }
}
