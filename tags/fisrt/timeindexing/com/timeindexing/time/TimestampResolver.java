// TimestampResolver.java

package com.timeindexing.time;

import com.timeindexing.index.Index;
import com.timeindexing.basic.Position;

/*
 * TODO:  finish implementation.
 * Detemrien how realtes to IndexNavigator.
 */

/**
 * A TimestampResolver takes Timestamps and tries to determine the position
 * in an Index associated with that Timestamp.
 */
public class TimestampResolver {
    /**
     * Detemrine a Position given a Timestamp
     */
    public Position find(Index index, Timestamp t) {
	if (index.contains(t)) {
	    // go for it
	    return null;  // position at which Timestamp t is valid
	} else {
	    return null;
	}
    }

    /**
     * Detemrine if a given Timestamp is in an Index
     */
    public boolean contains(Index index, Timestamp t) {
	// if index.firstTime <= t <= index.lastTime
	//   return true
	// otherwise return false
	    return index.contains(t);
    }

    
}
