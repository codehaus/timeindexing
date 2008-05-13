// TimeIndexShutdownHook.java

package com.timeindexing.index;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;

/**
 * This class is a Runtime system shutdown hook,
 * that forces all open Indexes to be closed cleanly
 * when the Runtime exits.
 */
public class TimeIndexShutdownHook extends Thread {
    /**
     * Construct a TimeIndexShutdownHook, given a Map of Indexes.
     */
    protected TimeIndexShutdownHook() {
    }

    /**
     * The thread is started by the Runtime
     * and it jumps into this run() method.
     */
    public void run() {
	// loop round until there is nothing more to close
	//System.err.println("TimeIndexShutdownHook: starting");

	while (true) {

	    Set indexURIs = new HashSet(TimeIndexDirectory.listIndexes());

	    if (indexURIs == null || indexURIs.size() == 0) {
		// there are no Indexes to close
		break;
	    } else {
		// there are some Indexes to close
		Iterator indexURIsI = indexURIs.iterator();

		while (indexURIsI.hasNext()) {
		    String indexURI = (String)indexURIsI.next();

		    // get the Index associated with the name
		    ManagedIndex index = (ManagedIndex)TimeIndexDirectory.find(indexURI);

		    try {
			// commit any changes
			index.commit();

			// really close it
			index.reallyClose();

			// and remove the handle
			TimeIndexDirectory.removeHandle(index);

			System.err.println("TimeIndexShutdownHook: close " + indexURI + " Thread " + Thread.currentThread().getName()) ;

		    } catch (TimeIndexException tie) {
			System.err.println("TimeIndexShutdownHook: error closing " + indexURI + " Message = " + tie.getMessage());
		    }
		}
	    }
	}

	//System.err.println("TimeIndexShutdownHook: finished");
		
    }
     
}
