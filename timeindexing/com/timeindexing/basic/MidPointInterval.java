// MidPointInterval.java

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
 * A better interval is an interval where the arguments
 * are a mid-point plus 2 values from that mid-point. Neither
 * of the specifiers can be adjusted.
 *
 * <ul>
 * <li> AbsoluteTimestamp, Value, Value
 * <li> Position, Value , Value
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
public class MidPointInterval extends AbsoluteInterval implements Interval, Cloneable {
    /*
     * The mid Position.
     */
    AbsolutePosition midPosition = null;

    /*
     * The mid Timestamp.
     */
    AbsoluteTimestamp midTimestamp  = null;

    /*
     * Start value
     */
    Value startValue = null;

    /*
     * End value
     */
    Value endValue = null;

    /*
     * Is the mid point an AbsoluteTimestamp\
     */
    boolean midPointIsTimestamp = false;

    /**
     * The resolved mid point.
     */
    TimestampMapping midPoint = null;

    /**
     * Construct an MidPointInterval from a Timestamp and 2 Values
     */
    public MidPointInterval(AbsoluteTimestamp t0, Value v1, Value v2) {
	checkNulls(t0, v1, v2);
	
	midTimestamp = t0;
	startValue = v1;
	endValue = v2;

	resolved = false;
	midPointIsTimestamp = true;
    }

    /**
     * Construct an MidPointInterval from a Position and 2 Values
     */
    public MidPointInterval(AbsolutePosition p0, Value v1, Value v2) {
	checkNulls(p0, v1, v2);
	
	midPosition = p0;
	startValue = v1;
	endValue = v2;

	resolved = false;
	midPointIsTimestamp = false;
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
	MidPointInterval newInterval = null;
	Position startPos = null;
	Position endPos = null;

	try {
	    // allocate a new Interval for the result
	    newInterval = (MidPointInterval)this.clone();

	    if (midPointIsTimestamp) {  // mid point is Timestamp
		// determine the position of the mid timestamp
		midPoint = index.locate(midTimestamp, selector, lifetime);

		if (midPoint == null) {
		    // it couldn't be found
		    return null;
		} else {
		    startPos = resolveValue(index, midPoint, startValue, selector, lifetime);
		    endPos = resolveValue(index, midPoint, endValue, selector, Lifetime.CONTINUOUS);
		}
	    } else {                    // mid point is Position
		// determine the timestamp of the midpoint
		midPoint = index.locate(midPosition, selector, lifetime);


		if (midPoint == null) {
		    // it couldn't be found
		    return null;
		} else {
		    startPos = resolveValue(index, midPoint, startValue, selector, lifetime);
		    endPos = resolveValue(index, midPoint, endValue, selector, Lifetime.CONTINUOUS);
		}

	    }

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
    protected Position resolveValue(Index index, TimestampMapping posMid, Value value, IndexTimestampSelector selector, Lifetime lifetime) {
	if (value instanceof Count) {
	    Count count = (Count)value;

	    // calculate the new position given the mid pos and a count
	    Position result = new AbsolutePosition((Position)new AbsoluteAdjustablePosition(posMid).adjust(count));

	    return result;

	} else if (value instanceof RelativeTimestamp) {
	    RelativeTimestamp offset = (RelativeTimestamp)value;

	    // calculate a new TS given the mid pos and a RelativeTimestamp
	    Timestamp newTS = TimeCalculator.addTimestamp(posMid.timestamp(), offset);

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

	    // instantiate the time specifier w.r.t the midtime
	    Timestamp time = timeSpecifier.instantiate(posMid.timestamp());

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
    protected void checkNulls(Value v1, Value v2, Value v3) {
	if (v1 == null) {
	    throw new IllegalIntervalValue("First arg to constructor was null");
	}
	if (v2 == null) {
	    throw new IllegalIntervalValue("Second arg to constructor was null");
	}
	if (v3 == null) {
	    throw new IllegalIntervalValue("Third arg to constructor was null");
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

	buffer.append("Interval (");

	buffer.append(startValue.toString());
	buffer.append(" - ");

	if (midPointIsTimestamp) {
	    buffer.append(midTimestamp.toString());
	} else {
	    buffer.append(midPosition.toString());
	}

	buffer.append(" + ");
	buffer.append(endValue.toString());
	buffer.append(")");

	if (isResolved()) {
	    buffer.append(" == ");
	    buffer.append(" (");
	    buffer.append(start().toString());
	    buffer.append(" <- ");
	    buffer.append(midPoint.toString());
	    buffer.append(" -> ");
	    buffer.append(end().toString());
	    buffer.append(")");

	}

	return buffer.toString();
    }


}
