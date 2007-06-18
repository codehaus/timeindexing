// OutputEventGenerator.java

package com.timeindexing.event;

import javax.swing.event.EventListenerList;

/**
 * A base class for classes that wish to
 * multicast OutputEvents to the relvant listenters.
 */
public class OutputEventGenerator {
    protected EventListenerList outputListenerList = null;

    /**
     * Add a OutputEventListener.
     */
    public void addOutputEventListener(OutputEventListener l) {
	if (outputListenerList == null) {
	    outputListenerList = new EventListenerList();
	}

	outputListenerList.add(OutputEventListener.class, l);
    }

    /**
     * Remove a OutputEventListener.
     */
    public void removeOutputEventListener(OutputEventListener l) {
	if (outputListenerList != null) {
	    outputListenerList.remove(OutputEventListener.class, l);
	}
    }

    /**
     * List all OutputEventListeners.
     */
    public Object[] getOutputEventListeners() {
	if (outputListenerList == null) {
	    outputListenerList = new EventListenerList();
	}

	return outputListenerList.getListenerList();

    }

    /**
     * Are there any Output Event Listeners
     */
    public boolean hasOutputEventListeners() {
	if (outputListenerList == null || outputListenerList.getListenerCount() == 0) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * Notify all listeners that have registered interest for
     * notification on Output Events.
     */
    public void fireOutputEvent(OutputEvent outputEvent) {
	Object[] listeners = getOutputEventListeners();

	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i] == OutputEventListener.class) {
		OutputEventListener oeL = (OutputEventListener)listeners[i+1];
		OutputEvent oE = (OutputEvent)outputEvent;

		oeL.outputNotification(oE);
	    }
	}
    }

}
