// IndexAccessEventGenerator.java

package com.timeindexing.event;

import javax.swing.event.EventListenerList;

/**
 * A base class for classes that wish to
 * multicast IndexAccessEvents to the relvant listenters.
 */
public class IndexAccessEventGenerator {
    /*
     * EventListenerList for Index Item Add Events.
     */
    EventListenerList listenerListAccessEvent = null;

   /**
     * Add a IndexAccessEventListener.
     */
    public void addAccessEventListener(IndexAccessEventListener l) {
	if (listenerListAccessEvent == null) {
	    listenerListAccessEvent = new EventListenerList();
	}

	listenerListAccessEvent.add(IndexAccessEventListener.class, l);
    }

    /**
     * Remove a IndexAccessEventListener.
     */
    public void removeAccessEventListener(IndexAccessEventListener l) {
	if (listenerListAccessEvent != null) {
	    listenerListAccessEvent.remove(IndexAccessEventListener.class, l);
	}
    }

    /**
     * List all IndexAccessEventListeners.
     */
    public Object[] getAccessEventListeners() {
	if (listenerListAccessEvent == null) {
	    listenerListAccessEvent = new EventListenerList();
	}

	return listenerListAccessEvent.getListenerList();

    }


    /**
     * Are there any Add Event Listeners
     */
    public boolean hasAccessEventListeners() {
	if (listenerListAccessEvent == null || listenerListAccessEvent.getListenerCount() == 0) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on Add events.
     */
    public void fireAccessEvent(IndexEvent indexEvent) {
	// Guaranteed to return a non-null array
	Object[] listeners = getAccessEventListeners();

	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i] == IndexAccessEventListener.class) {
		IndexAccessEventListener iaeL = (IndexAccessEventListener)listeners[i+1];
		IndexAccessEvent iaE = (IndexAccessEvent)indexEvent;

		iaeL.itemAccessed(iaE);

	    }
	}
    }
}
