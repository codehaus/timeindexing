// IndexAccessEvent.java

package com.timeindexing.event;

import com.timeindexing.index.IndexItem;
import com.timeindexing.basic.ID;

/**
 * An Index Access Event, which is generated 
 * when an IndexItem is accessed in an Index.
 */
public class IndexAccessEvent extends IndexEvent {
    /*
     * The IndexItem which was accessed and passed in this event.
     */
    IndexItem item = null;

    /**
     * Construct an IndexAccessEvent.
     * Constructed from an index name, an index ID,
     * the IndexItem, and the source of the event.
     */
    public IndexAccessEvent(String aName, ID anID, IndexItem anItem, Object aSource) {
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
