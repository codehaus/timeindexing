// IndexAddEventGenerator.java

package com.timeindexing.event;

import javax.swing.event.EventListenerList;

/**
 * A base class for classes that wish to
 * multicast IndexAddEvents to the relvant listenters.
 */
public class IndexAddEventGenerator {
    /*
     * EventListenerList for Index Item Add Events.
     */
    EventListenerList listenerListAddEvent = null;

   /**
     * Add a IndexAddEventListener.
     */
    public void addAddEventListener(IndexAddEventListener l) {
	if (listenerListAddEvent == null) {
	    listenerListAddEvent = new EventListenerList();
	}

	listenerListAddEvent.add(IndexAddEventListener.class, l);
    }

    /**
     * Remove a IndexAddEventListener.
     */
    public void removeAddEventListener(IndexAddEventListener l) {
	if (listenerListAddEvent != null) {
	    listenerListAddEvent.remove(IndexAddEventListener.class, l);
	}
    }

    /**
     * List all IndexAddEventListeners.
     */
    public Object[] getAddEventListeners() {
	if (listenerListAddEvent == null) {
	    listenerListAddEvent = new EventListenerList();
	}

	return listenerListAddEvent.getListenerList();

    }


    /**
     * Are there any Add Event Listeners
     */
    public boolean hasAddEventListeners() {
	if (listenerListAddEvent == null || listenerListAddEvent.getListenerCount() == 0) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on Add events.
     */
    public void fireAddEvent(IndexEvent indexEvent) {
	// Guaranteed to return a non-null array
	Object[] listeners = getAddEventListeners();

	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i] == IndexAddEventListener.class) {
		IndexAddEventListener iaeL = (IndexAddEventListener)listeners[i+1];
		IndexAddEvent iaE = (IndexAddEvent)indexEvent;

		iaeL.itemAdded(iaE);

	    }
	}
    }
}
