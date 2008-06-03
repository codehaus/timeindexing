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



// EndPointInterval.java

package com.timeindexing.basic;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.AbsoluteTimestamp;
import com.timeindexing.time.RelativeTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.TimeSpecifier;
import com.timeindexing.time.Lifetime;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.index.GetItemException;

/**
 * An end point interval is an interval where the arguments
 * are a start-point plus a value from that start-point.
 *
 * <ul>
 * <li> AbsoluteTimestamp, Value
 * <li> Position, Value
 * </ul>
 * where the values  are:
 * <ul>
 * <li> Count
 * <li> Position
 * <li> AbsoluteTimestamp
 * <li> RelativeTimestamp
 * <li> TimeSpecifier
 * </ul>
 */
public class EndPointInterval extends AbsoluteInterval implements Interval, Cloneable {
    /*
     * The start Position.
     */
    AbsolutePosition startPosition = null;

    /*
     * The start Timestamp.
     */
    AbsoluteTimestamp startTimestamp  = null;

    /*
     * End value
     */
    Value endValue = null;

    /*
     * Is the start point an AbsoluteTimestamp\
     */
    boolean startPointIsTimestamp = false;

    /**
     * Construct an EndPointInterval from a Timestamp and a Value
     */
    public EndPointInterval(AbsoluteTimestamp t0, Value v1) {
	checkNulls(t0, v1);
	
	startTimestamp = t0;
	endValue = v1;

	resolved = false;
	startPointIsTimestamp = true;
    }

    /**
     * Construct an EndPointInterval from a Position and a Value
     */
    public EndPointInterval(AbsolutePosition p0, Value v1) {
	checkNulls(p0, v1);
	
	startPosition = p0;
	endValue = v1;

	resolved = false;
	startPointIsTimestamp = false;
    }

    /**
     * Resolve this interval w.r.t a specified index.
     * The IndexTimestampSelector determines whether to use Index timestamps or 
     * Data timestamps.
     * The Lifetime determines whether timestamps are continuous or discrete.
     * This only affects start points and midpoints.
     * Returns a clone with resolved positions.
     */
    public AbsoluteInterval resolve(Index index, IndexTimestampSelector selector, Lifetime lifetime) {
	EndPointInterval newInterval = null;
	Position startPos = null;
	Position endPos = null;

	try {
	    if (startPointIsTimestamp) {  // start point is Timestamp
		// determine the position of the start timestamp
		TimestampMapping posStart = index.locate(startTimestamp, selector, lifetime);

		if (posStart == null) {
		    // it couldn't be found
		    return null;
		} else {
		    startPos = posStart.position();
		    endPos = resolveValue(index, posStart, endValue, selector, Lifetime.CONTINUOUS);
		}
	    } else {                    // start point is Position
		// determine the timestamp of the startpoint
		TimestampMapping posStart = index.locate(startPosition, selector, lifetime);

		if (posStart == null) {
		    // it couldn't be found
		    return null;
		} else {
		    // use original position
		    startPos = startPosition;
		    // this is the end position
		    endPos = resolveValue(index, posStart, endValue, selector, Lifetime.CONTINUOUS);
		}

	    }

	    //System.err.println("EndPointInterval: startPos = " + startPos + ". endPos = " + endPos);

	    // allocate a new Interval for the result
	    newInterval = (EndPointInterval)this.clone();

	    // now fill in the resulting new interval
	    if (startPos.value() < endPos.value()) {  // startPos before endPos
		newInterval.start = startPos;
		newInterval.end = endPos;
		newInterval.resolved = true;
	    } else {
		newInterval.start = endPos;
		newInterval.end = startPos;
		newInterval.resolved = true;
	    }

	    return newInterval;

	} catch (CloneNotSupportedException cnse) {
	    return null;
	}
    }

    /**
     * Resolve a Value w.r.t a Position.
     */
    protected Position resolveValue(Index index, TimestampMapping posStart, Value value, IndexTimestampSelector selector, Lifetime lifetime) {
	if (value instanceof Count) {
	    Count count = (Count)value;
	    //Count count = (Count)new RelativeAdjustableCount((Count)value).adjust(-1);

	    // calculate the new position given the start pos and a count
	    if (posStart.position().value() < 0) {
		return Position.TOO_LOW;
	    } else {
		Position result = new AbsolutePosition((Position)new AbsoluteAdjustablePosition(posStart).adjust(count));

		return result;
	    }

	} else if (value instanceof RelativeTimestamp) {
	    RelativeTimestamp offset = (RelativeTimestamp)value;

	    // calculate a new TS given the start pos and a RelativeTimestamp
	    Timestamp newTS = TimeCalculator.addTimestamp(posStart.timestamp(), offset);

	    // now locate the position for the new TS
	    TimestampMapping result = index.locate(newTS, selector, lifetime);

	    return result;
	} else if (value instanceof AbsoluteTimestamp) {
	    AbsoluteTimestamp time = (AbsoluteTimestamp)value;

	    // now locate the position for the timestamp
	    TimestampMapping result = index.locate(time, selector, lifetime);

	    return result;
	    
	} else if (value instanceof Position) {
	    // nothing to do
	    return (Position)value;
	} else if (value instanceof TimeSpecifier) {
	    TimeSpecifier timeSpecifier = (TimeSpecifier)value;

	    // instantiate the time specifier w.r.t the starttime
	    Timestamp time = timeSpecifier.instantiate(posStart.timestamp());

	    // now locate the position for the timestamp
	    TimestampMapping result = index.locate(time, selector, lifetime);

	    return result;
	} else {
	    throw new Error("resolveValue got argument of type: " + value.getClass().getName() + " which can't be accepted");
	}
    }
	
    /**
     * This used to check for nulls in the constructor.
     * If no exception is thrown things are good.
     */
    protected void checkNulls(Value v1, Value v2) {
	if (v1 == null) {
	    throw new IllegalIntervalValue("First arg to constructor was null");
	}
	if (v2 == null) {
	    throw new IllegalIntervalValue("Second arg to constructor was null");
	}
    }


    /**
     * Clone me
     */
    public Object clone() throws  CloneNotSupportedException {
	return super.clone();
    }

    /**
     * String version of interval.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer();

	buffer.append("Interval");

	buffer.append(" (");

	if (startPointIsTimestamp) {
	    buffer.append(startTimestamp.toString());
	} else {
	    buffer.append(startPosition.toString());
	}

	buffer.append(" + ");
	buffer.append(endValue.toString());
	buffer.append(")");

	if (isResolved()) {
	    buffer.append(" == ");
	    buffer.append(" (");
	    buffer.append(start().toString());
	    buffer.append(" -> ");
	    buffer.append(end().toString());
	    buffer.append(")");

	}

	return buffer.toString();
    }


}
