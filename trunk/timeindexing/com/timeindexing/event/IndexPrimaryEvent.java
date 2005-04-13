// IndexPrimaryEvent.java

package com.timeindexing.event;

import com.timeindexing.basic.ID;

/**
 * An Index Primary Event, which is generated 
 * at primary moments.
 * These include: open , close, committed, create.
 */
public class IndexPrimaryEvent extends IndexEvent {
    /**
     * Used when an Index is opened.
     */
    public static final int OPENED        = 1;

    /**
     * Used when an Index is closed.
     */
    public static final int CLOSED       = 2;

    /**
     * Used when an Index is committed.
     */
    public static final int COMMITTED    = 3;

    /**
     * Used when an Index is created.
     */
    public static final int CREATED    = 4;


    /*
     * The specifier for this event
     */
    int eventSpecifier = 0;

    /**
     * Construct an IndexPrimaryEvent.
     * Constructed from an index name, an index ID, an event specifier,
     * and the source of the event.
     */
    public IndexPrimaryEvent(String aName, ID anID, int eventSpec, Object aSource) {
	super(aName, anID, aSource);
	eventSpecifier = eventSpec;
    }

    /**
     * Get the event specifier.
     */
    public int getEventSpecifier() {
	return eventSpecifier;
    }
}
