// IndexAddEvent.java

package com.timeindexing.event;

import com.timeindexing.index.IndexItem;
import com.timeindexing.basic.ID;

/**
 * An Index Add Event, which is generated 
 * when an IndexItem is added to an Index.
 */
public class IndexAddEvent extends IndexEvent {
    /*
     * The IndexItem which was added and passed in this event.
     */
    IndexItem item = null;

    /**
     * Construct an IndexAddEvent.
     * Constructed from an index name, an index ID,
     * the IndexItem, and the source of the event.
     */
    public IndexAddEvent(String aName, ID anID, IndexItem anItem, Object aSource) {
	super(aName, anID, aSource);
	item = anItem;
    }

    /**
     * Get the IndexItem from the event.
     */
    public IndexItem getIndexItem() {
	return item;
    }
     

    
}
