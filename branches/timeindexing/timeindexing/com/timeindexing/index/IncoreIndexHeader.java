// IncoreIndexHeader.java


package com.timeindexing.index;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.Clock;
import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;

import java.util.Map;
import java.util.HashMap;

/**
 * The implementation of an index header for use 
 * in Index Caches.
 */
public class IncoreIndexHeader extends DefaultIndexHeader implements ManagedIndexHeader {
    Index myIndex = null;

    /**
     * Create a IncoreIndexHeader object.
     */
    public IncoreIndexHeader(Index index, String name) {
	myIndex = index;
	setName(name);
    }


}
