// IndexAccessEventListener.java

package com.timeindexing.event;

import java.util.EventListener;

/**
 * An Index Access Event, which is used
 * when an IndexItem is accessed in an Index.
 */
public interface IndexAccessEventListener extends EventListener {
    /**
     * A notification that an IndexItem has been accessed in an Index.
     */
    public void itemAccessed(IndexAccessEvent iae);
}
