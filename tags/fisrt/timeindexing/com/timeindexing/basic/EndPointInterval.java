// EndPointInterval.java

package com.timeindexing.basic;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.AbsoluteTimestamp;
import com.timeindexing.time.RelativeTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Lifetime;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexTimestampSelector;

/**
 * An end-point interval is an interval where both of the the arguments
 * are end-points of the interval, and
 * can be resolved into two positions into an Index.  Neither
 * of the specifiers can be adjusted.
 * 
 * Intervals can be:
 * <ul>
 * <li> AbsoluteTimestamp -> AbsoluteTimestamp
 * <li> AbsoluteTimestamp -> Position
 * <li> AbsoluteTimestamp + Count
 * <li> AbsoluteTimestamp + RelativeTimestamp
 * <li> Position -> AbsoluteTimestamp
 * <li> Position -> Position
 * <li> Position + Count
 * <li> Position + RelativeTimestamp
 * </ul>
 * <p>
 * TODO: finish implementations.
 * <p>
 */
public class EndPointInterval extends AbsoluteInterval implements Interval, Cloneable {
    /*
     * Definitions for each style of the Interval specifiers.
     */
    
    /**
     * A Timestamp to Timestamp specifier.
     */
    public final static int ABSOLUTETIMESTAMP_TO_ABSOLUTETIMESTAMP = 0;

    /**
     * A Timestamp to Position specifier.
     */
    public final static int ABSOLUTETIMESTAMP_TO_POSITION = 1;

    /**
     * A Timestamp plus Count specifier.
     */
    public final static int ABSOLUTETIMESTAMP_PLUS_COUNT = 2;

    /**
     * A Timestamp to Timestamp specifier.
     */
    public final static int ABSOLUTETIMESTAMP_TO_RELATIVETIMESTAMP = 3;
    /**
     * A Position to Timestamp specifier.
     */
    public final static int POSITION_TO_ABSOLUTETIMESTAMP = 4;

    /**
     * A Position to Position specifier.
     */
    public final static int POSITION_TO_POSITION = 5;

    /**
     * A Position plus Count specifier.
     */
    public final static int POSITION_PLUS_COUNT = 6;

    /**
     * A Position to Timestamp specifier.
     */
    public final static int POSITION_TO_RELATIVETIMESTAMP = 7;

    /*
     * The start timestamp, if one was specified.
     */
    Timestamp startTimestamp = null;

    /*
     * The end timestamp, if one was specified.
     */
    Timestamp endTimestamp = null;

    /*
     * A Count object.
     */
    Count count = null;

    /*
     * A relative Timestamp.
     */
    RelativeTimestamp elapsed = null;

    /*
     * What style was the Interval specified as.
     */
    int style = -1;  // start with an illegal value

    /**
     * Construct an EndPointInterval from a Timestamp and a Timestamp.
     *
     * Because it is possible for there to be more than one timeindex item
     * for a particular Timestamp, having both the start Timestamp and
     * the end Timestamp be equal, is NOT illegal.
     */
    public EndPointInterval(AbsoluteTimestamp t0, AbsoluteTimestamp t1) {
	checkNulls(t0, t1);

	if (TimeCalculator.lessThanEquals(t0, t1)) {  // t0 before t1
	    startTimestamp = t0;
	    endTimestamp = t1;
	} else {		//  t1 before t0
	    startTimestamp = t1;
	    endTimestamp = t0;
	}

	style = ABSOLUTETIMESTAMP_TO_ABSOLUTETIMESTAMP;
	resolved = false;
    }

    /**
     * Construct an EndPointInterval from a Timestamp and a Position.
     */
    public EndPointInterval(AbsoluteTimestamp t0, Position pos) {
	checkNulls(t0, pos);

	startTimestamp = t0;
	end = pos;

	style = ABSOLUTETIMESTAMP_TO_POSITION;
	resolved = false;
    }
	
    /**
     * Construct an EndPointInterval from a Timestamp and a Count.
     */
    public EndPointInterval(AbsoluteTimestamp t0, Count c) {
	checkNulls(t0, c);
	
	startTimestamp = t0;
	count = c;

	style = ABSOLUTETIMESTAMP_PLUS_COUNT;
	resolved = false;
    }

    /**
     * Construct an EndPointInterval from a AbsoluteTimestamp and a RelativeTimestamp
     */
    public EndPointInterval(AbsoluteTimestamp t0, RelativeTimestamp relT) {
	checkNulls(t0, relT);
	
	startTimestamp = t0;
	elapsed = relT;

	style = ABSOLUTETIMESTAMP_TO_RELATIVETIMESTAMP;
	resolved = false;
    }

    /**
     * Construct an EndPointInterval from a Position and a Timestamp.
     */
    public EndPointInterval(Position posStart, AbsoluteTimestamp t) {
	checkNulls(posStart, t);

	start = posStart;
	endTimestamp = t;

	style = POSITION_TO_ABSOLUTETIMESTAMP;
	resolved = false;
    }

    /**
     * Construct an EndPointInterval from a Position and a Position.
     * The order doesn't matter.
     */
    public EndPointInterval(Position posStart, Position posEnd) {
	checkNulls(posStart, posEnd);

	if (posStart.value() < posEnd.value()) {
	    start = posStart;
	    end = posEnd;
	} else {
	    start = posEnd;
	    end = posStart;
	}

	style = POSITION_TO_POSITION;
	resolved = true;
    }


    /**
     * Construct an EndPointInterval from a Position and a Count.
     */
    public EndPointInterval(Position pos, Count c) {
	checkNulls(pos, c);

	// Save the count
	count = c;

	// The new end point, takes 'pos', creates an AdjustablePosition,
	// and skips by amount 'count'
	AdjustablePosition newEnd = new AbsoluteAdjustablePosition(pos);
	newEnd.adjust(count);

	start = pos;
	end = new AbsolutePosition(newEnd);

	style = POSITION_PLUS_COUNT;
	resolved = true;
    }


    /**
     * Construct an EndPointInterval from a Position and a RelativeTimestamp.
     */
    public EndPointInterval(Position pos, RelativeTimestamp relT) {
	checkNulls(pos, relT);

	start = pos;
	elapsed = relT;

	style = POSITION_TO_RELATIVETIMESTAMP;
	resolved = false;
	System.err.println("EndPointInterval: constructed POSITION_TO_RELATIVETIMESTAMP");
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
     * Resolve this interval w.r.t a specified index.
     * The IndexTimestampSelector determines whether to use Index timestamps or 
     * Data timestamps.
     * The Lifetime determines whether timestamps are continuous or discrete.
     * This only affects start points.
     * Returns a clone with resolved positions on success,
     * returns null on failire to resolve.
     */
    public AbsoluteInterval resolve(Index index, IndexTimestampSelector selector, Lifetime lifetime) {
	EndPointInterval newInterval = null;

	try {
	    switch (style) {
	    case ABSOLUTETIMESTAMP_TO_ABSOLUTETIMESTAMP: {
		TimestampMapping posStart = index.locate(startTimestamp, selector, lifetime);
		TimestampMapping posEnd = index.locate(endTimestamp, selector, Lifetime.CONTINUOUS);

		if (posStart == null || posEnd == null) {
		    return null;
		} else {
		    // both resolved
		    newInterval = (EndPointInterval)this.clone();

		    newInterval.start = (Position)posStart;
		    newInterval.end = (Position)posEnd;

		    newInterval.resolved = true;
		    return newInterval;
		}
	    }

	    case ABSOLUTETIMESTAMP_TO_POSITION: {
		TimestampMapping posStart = index.locate(startTimestamp, selector, lifetime);


		if (posStart == null) {
		    return null;
		} else {
		    // resolved start time
		    newInterval = (EndPointInterval)this.clone();

		    newInterval.start = (Position)posStart;
		    newInterval.end = end;

		    newInterval.resolved = true;
		    return newInterval;
		}
	    }

	    case ABSOLUTETIMESTAMP_PLUS_COUNT: {
		TimestampMapping posStart = index.locate(startTimestamp, selector, lifetime);


		if (posStart == null) {
		    return null;
		} else {
		    // resolved start time
		    newInterval = (EndPointInterval)this.clone();

		    newInterval.start = (Position)posStart;
		    newInterval.end = new AbsolutePosition((Position)new AbsoluteAdjustablePosition(posStart).adjust(count));

		    newInterval.resolved = true;
		    return newInterval;
		}
	    }


	    case ABSOLUTETIMESTAMP_TO_RELATIVETIMESTAMP: {
		TimestampMapping posStart = index.locate(startTimestamp, selector, lifetime);
		// calcualte the end timestamp
		Timestamp endTS = TimeCalculator.addTimestamp(posStart.timestamp(), elapsed);
		TimestampMapping posEnd = index.locate(endTS, selector, Lifetime.CONTINUOUS);

		if (posStart == null || posEnd == null) {
		    return null;
		} else {
		    // both resolved
		    newInterval = (EndPointInterval)this.clone();

		    newInterval.start = (Position)posStart;
		    newInterval.end = (Position)posEnd;

		    newInterval.resolved = true;
		    return newInterval;
		}
	    }

	    case POSITION_TO_ABSOLUTETIMESTAMP: {
		TimestampMapping posEnd = index.locate(endTimestamp, selector, Lifetime.CONTINUOUS);


		if (posEnd == null) {
		    return null;
		} else {
		    // resolved start time
		    newInterval = (EndPointInterval)this.clone();

		    newInterval.start = start;
		    newInterval.end = (Position)posEnd;

		    newInterval.resolved = true;
		    return newInterval;
		}
	    }

	    case POSITION_TO_POSITION: {
		// nothing to calculate
		newInterval = (EndPointInterval)this.clone();
		return newInterval;
	    }

	    case POSITION_PLUS_COUNT: {
		// nothing to calculate
		newInterval = (EndPointInterval)this.clone();
		return newInterval;
	    }

	    case POSITION_TO_RELATIVETIMESTAMP: {
		System.err.println("POSITION_TO_RELATIVETIMESTAMP: start = " + start);
		IndexItem item = index.getItem(start);
		Timestamp startTS = null;

		if (selector == IndexTimestampSelector.DATA) {
		    startTS = item.getDataTimestamp();
		    System.err.println("POSITION_TO_RELATIVETIMESTAMP: data TS = " + startTS);
		} else {
		    startTS = item.getIndexTimestamp();
		    System.err.println("POSITION_TO_RELATIVETIMESTAMP: index TS = " + startTS);
		}

		// calcualte the end timestamp
		Timestamp endTS = TimeCalculator.addTimestamp(startTS, elapsed);
		System.err.println("POSITION_TO_RELATIVETIMESTAMP: end TS = " + endTS);
		
		TimestampMapping posEnd = index.locate(endTS, selector, Lifetime.CONTINUOUS);

		System.err.println("POSITION_TO_RELATIVETIMESTAMP: end = " + posEnd);

		if (posEnd == null) {
		    return null;
		} else {
		    // both resolved
		    newInterval = (EndPointInterval)this.clone();

		    newInterval.start = start;
		    newInterval.end = (Position)posEnd;

		    newInterval.resolved = true;
		    return newInterval;
		}
	    }

	    default:
		throw new Error("EndPointInterval: Unexpected value for style => " + style);
	    }
	} catch (CloneNotSupportedException cnse) {
	    return null;
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

	switch (style) {
	case ABSOLUTETIMESTAMP_TO_ABSOLUTETIMESTAMP: {
	    buffer.append( startTimestamp + " -> " + endTimestamp);
	    break;
	}

	case ABSOLUTETIMESTAMP_TO_POSITION: {
	    buffer.append( startTimestamp + " -> " + end);
	    break;
	}

	case ABSOLUTETIMESTAMP_PLUS_COUNT: {
	    buffer.append( startTimestamp + " + " + count);
	    break;
	}

	case ABSOLUTETIMESTAMP_TO_RELATIVETIMESTAMP: {
	    buffer.append( startTimestamp + " , " + elapsed);
	    break;
	}

	case POSITION_TO_ABSOLUTETIMESTAMP: {
	    buffer.append( start + " -> " + endTimestamp);
	    break;
	}

	case POSITION_TO_POSITION: {
	    buffer.append( start + " -> " + end);
	    break;
	}

	case POSITION_PLUS_COUNT: {
	    buffer.append( start + " + " + count);
	    break;
	}

	case POSITION_TO_RELATIVETIMESTAMP: {
	    buffer.append( start + " , " + elapsed);
	    break;
	}

	default:
	    throw new Error("EndPointInterval: Unexpected value for style => " + style);
	}

	buffer.append(")");

	return buffer.toString();
    }

}
