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
	return "Mapping " + myTimestamp + " => " + position().toString();
    }
}
	    
     
