// AbstractIndexIO.java

package com.timeindexing.io;

import com.timeindexing.index.StoredIndex;
import com.timeindexing.index.ManagedIndexItem;
import com.timeindexing.basic.Position;

import java.util.LinkedList;
import java.io.IOException;

/**
 * An object for doing IO for an Index.
 * It has a handle on the Index it's doing I/O for, and
 * handles the thread for the I/O.
 */
public abstract class AbstractIndexIO implements IndexInteractor,  Runnable {
    // The index this is doing I/O for
    StoredIndex myIndex = null;

    // The Thread for this I/O
    Thread myThread = null;

    // A work queue for read requests
    LinkedList readQueue = null;


    // A work queue for write requests
    LinkedList writeQueue = null;

    /**
     * Get the index which this is doing I/O for.
     */
    public StoredIndex getIndex() {
	return myIndex;
    }

    /**
     * Initialize the thread
     * @param name the name of the thread
     */
    public Thread initThread(String name) {
	myThread = new Thread(this, name);
	readQueue = new LinkedList();
	writeQueue = new LinkedList();
	return myThread;
    }


    /**
     * Get the thread
     */
    public Thread getThread() {
	return myThread;
    }

    /**
     * Start the thread
     */
    public Thread startThread() {
	if (myThread != null) {
	    myThread.start();
	    //System.err.println("Started Thread " + myThread);
	    return myThread;
	} else {
	    return null;
	}
    }

    /**
     * Stop the thread
     */
    public Thread stopThread() {
	if (myThread != null) {
	    myThread.stop();
	    //System.err.println("Stopped Thread " + myThread);
	    return myThread;
	} else {
	    return null;
	}
    }

}
