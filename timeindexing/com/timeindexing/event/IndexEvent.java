// IndexEvent.java

package com.timeindexing.event;

import com.timeindexing.basic.ID;
import java.util.EventObject;

/**
 * An Index Event, which is generated at key situations
 * through an Index's lifetime.
 */
public abstract class IndexEvent extends EventObject {
    /*
     * The nae of the Index.
     */
    String name = null;

    /*
     * The ID of the Index.
     */
    ID theID = null;

    /**
     * Construct an IndexEvent.
     * Constructed from an index name, an index ID,
     * and the source of the event.
     */
    public IndexEvent(String aName, ID anID, Object aSource) {
	super(aSource);
	name = aName;
	theID = anID;
    }

    /**
     * Get the name of the index.
     */
    public String getName() {
	return name;
    }

    /**
     * Get an ID of an index.
     */
    public ID getID() {
	return theID;
    }

    /**
     * Get the source of the event.
     */
    public Object getSource() {
	return source;
    }

}
