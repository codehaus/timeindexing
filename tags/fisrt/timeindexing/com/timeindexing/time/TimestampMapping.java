// TimestampMapping.java

package com.timeindexing.time;

import com.timeindexing.basic.Absolute;
import com.timeindexing.basic.Position;

/**
 * A TimestampMapping is an object that holds the mapping of a Timestamp to a  Position
 * in an index.
 * The Timestamp to Position is looked up at runtime.  These objects are used to
 * cache the found Position.
 */

public class TimestampMapping implements Position, Absolute {
    /*
     * The Posititon that the Timestamp resovles to.
     */
    Position mappedPosition = null;

    /*
     * The cached Timestamp.
     */
    Timestamp myTimestamp = null;

    /**
     * Construct a TimestampMapping
     */
    public TimestampMapping(Timestamp t, Position p) {
	myTimestamp = t;
	mappedPosition = p;
    }

    /**
     * Get the value
     */
    public long value() {
	return mappedPosition.value();
    }

    /**
     * Get the position
     */
    public Position position() {
	return mappedPosition;
    }

    /**
     * Get the Timestamp
     */
    public Timestamp timestamp() {
	return myTimestamp;
    }

    /**
     * Clone me.
     * Needed in order to be a Position.
     */
    public Object clone() throws  CloneNotSupportedException {
	return super.clone();
    }

    /**
     * The toString method shows the Position.
     */
    public String toString() {
	return position().toString();
    }
}
	    
     
