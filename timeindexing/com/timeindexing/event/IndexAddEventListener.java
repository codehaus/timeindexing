// IndexAddEventListener.java

package com.timeindexing.event;

import java.util.EventListener;

/**
 * An Index Add Event, which is used
 * when an IndexItem is added to an Index.
 */
public interface IndexAddEventListener extends  EventListener {
    /**
     * A notification that an IndexItem has been added to an Index.
     */
    public void itemAdded(IndexAddEvent iae);
}
