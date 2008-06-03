/*
 * Copyright 2003-2008 Stuart Clayman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



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
