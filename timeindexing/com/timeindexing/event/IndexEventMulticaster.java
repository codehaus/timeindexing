// IndexEventMulticaster.java

package com.timeindexing.event;

import javax.swing.event.EventListenerList;

/**
 * A class that multicast IndexEvents to the relvant listenters.
 * Usage based on javax.swing.event.EventListenerList.
 * @see javax.swing.event.EventListenerList
 */
public class IndexEventMulticaster implements IndexEventGenerator {
    /*
     * EventListenerList for Primary Events.
     */
    EventListenerList listenerListPrimaryEvent = null;

    /*
     * EventListenerList for Index Item Add Events.
     */
    EventListenerList listenerListAddEvent = null;

    /*
     * EventListenerList for Index Item Access Events.
     */
    EventListenerList listenerListAccessEvent = null;


    IndexEvent indexEvent = null;

    /**
     * Create a an IndexEventMulticaster.
     * It can fire IndexPrimaryEvents, IndexAddEvents, and
     * IndexAccessEvents.
     */
    public IndexEventMulticaster() {
	listenerListPrimaryEvent = new EventListenerList();
	listenerListAddEvent = new EventListenerList();
	listenerListAccessEvent = new EventListenerList();
    }

    /**
     * Add a IndexPrimaryEventListener.
     */
    public void addPrimaryEventListener(IndexPrimaryEventListener l) {
	listenerListPrimaryEvent.add(IndexPrimaryEventListener.class, l);
    }

    /**
     * Remove a IndexPrimaryEventListener.
     */
    public void removePrimaryEventListener(IndexPrimaryEventListener l) {
	listenerListPrimaryEvent.remove(IndexPrimaryEventListener.class, l);
    }

    /**
     * List all IndexPrimaryEventListeners.
     */
    public Object[] getPrimaryEventListeners() {
	return listenerListPrimaryEvent.getListenerList();
    }


    /**
     * Notify all listeners that have registered interest for
     * notification on Primary events.
     */
    public void firePrimaryEvent(IndexEvent indexEvent) {
	// Guaranteed to return a non-null array
	Object[] listeners = getPrimaryEventListeners();

	//System.err.println("Firing Primary Event for: " + listeners.length / 2);

	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i] == IndexPrimaryEventListener.class) {
		IndexPrimaryEventListener ipeL = (IndexPrimaryEventListener)listeners[i+1];
		IndexPrimaryEvent ipE = (IndexPrimaryEvent)indexEvent;

		if (ipE.getEventSpecifier() == IndexPrimaryEvent.OPENED) {
		    ipeL.opened(ipE);

		} else if (ipE.getEventSpecifier() == IndexPrimaryEvent.CLOSED) {
		    ipeL.closed(ipE);

		} else if (ipE.getEventSpecifier() == IndexPrimaryEvent.COMMITTED) {
		    ipeL.committed(ipE);

		} else if (ipE.getEventSpecifier() == IndexPrimaryEvent.CREATED) {
		    ipeL.created(ipE);
		} else {
		    throw new Error("IndexEventMulticaster: cant fire event when IndexPrimaryEvent specifier == " + ipE.getEventSpecifier());
		}
	    }
	}
    }


    /**
     * Add a IndexAddEventListener.
     */
    public void addAddEventListener(IndexAddEventListener l) {
	listenerListAddEvent.add(IndexAddEventListener.class, l);
    }

    /**
     * Remove a IndexAddEventListener.
     */
    public void removeAddEventListener(IndexAddEventListener l) {
	listenerListAddEvent.remove(IndexAddEventListener.class, l);
    }

    /**
     * List all IndexAddEventListeners.
     */
    public Object[] getAddEventListeners() {
	return listenerListAddEvent.getListenerList();
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


    /**
     * Add a IndexAccessEventListener.
     */
    public void addAccessEventListener(IndexAccessEventListener l) {
	listenerListAccessEvent.add(IndexAccessEventListener.class, l);
    }

    /**
     * Remove a IndexAccessEventListener.
     */
    public void removeAccessEventListener(IndexAccessEventListener l) {
	listenerListAccessEvent.remove(IndexAccessEventListener.class, l);
    }

    /**
     * List all IndexAccessEventListeners.
     */
    public Object[] getAccessEventListeners() {
	return listenerListAccessEvent.getListenerList();
    }


    /**
     * Notify all listeners that have registered interest for
     * notification on Access events.
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
