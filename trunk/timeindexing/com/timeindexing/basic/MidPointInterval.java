// MidPointInterval.java

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
import com.timeindexing.index.GetItemException;

/**
 * A mid-point interval is an interval where the arguments
 * are a mid-point plus 2 offsets from that mid-point.  The interval
 * can be resolved into two positions into an Index.  Neither
 * of the specifiers can be adjusted.
 *  * <ul>
 * <li> AbsoluteTimestamp, - Count, + Count
 * <li> AbsoluteTimestamp, - RelativeTimestamp, + RelativeTimestamp
 * <li> Position, - Count, + Count
 * <li> Position, - RelativeTimestamp, + RelativeTimestamp
 * </ul>
 */
public class MidPointInterval extends AbsoluteInterval implements Interval, Cloneable {
    /*
     * Definitions for each style of the Interval specifiers.
     */

    /**
     * A Timestamp to 2 Count specifiers.
     */
    public final static int ABSOLUTETIMESTAMP_COUNT_COUNT = 10;

    /**
     * A Timestamp to 2 Timestamp specifiers.
     */
    public final static int ABSOLUTETIMESTAMP_RELATIVETIMESTAMP_RELATIVETIMESTAMP = 11;

    /**
     * A Position plus 2 Count specifiers.
     */
    public final static int POSITION_COUNT_COUNT = 12;

    /**
     * A Position plus 2 Timestamp specifiers.
     */
    public final static int POSITION_RELATIVETIMESTAMP_RELATIVETIMESTAMP = 13;

    /*
     * The mid Position.
     */
    Position midPosition = null;

    /*
     * The mid Timestamp.
     */
    AbsoluteTimestamp midTimestamp  = null;

    /*
     * A Count object.
     */
    Count count1 = null;

    /*
     * A Count object.
     */
    Count count2 = null;

    /*
     * A relative Timestamp.
     */
    RelativeTimestamp offset1 = null;

    /*
     * A relative Timestamp.
     */
    RelativeTimestamp offset2 = null;

    /*
     * What style was the Interval specified as.
     */
    int style = -1;  // start with an illegal value


    /**
     * Construct an MidPointInterval from a Timestamp and 2 Counts.
     */
    public MidPointInterval(AbsoluteTimestamp t0, Count c1, Count c2) {
	checkNulls(t0, c1, c2);
	
	midTimestamp = t0;
	count1 = c1;
	count2 = c2;

	style = ABSOLUTETIMESTAMP_COUNT_COUNT;
	resolved = false;
    }

    /**
     * Construct an MidPointInterval from a Timestamp and 2 RelativeTimestamps.
     */
    public MidPointInterval(AbsoluteTimestamp t0, RelativeTimestamp rt1, RelativeTimestamp rt2) {
	checkNulls(t0, rt1, rt2);
	
	midTimestamp = t0;
	offset1 = rt1;
	offset2 = rt2;

	style = ABSOLUTETIMESTAMP_RELATIVETIMESTAMP_RELATIVETIMESTAMP;
	resolved = false;
    }

    /**
     * Construct an MidPointInterval from a Position and 2 Counts.
     */
    public MidPointInterval(Position p0, Count c1, Count c2) {
	checkNulls(p0, c1, c2);
	
	midPosition = p0;
	count1 = c1;
	count2 = c2;

	AdjustablePosition newStart = new AbsoluteAdjustablePosition(p0);
	newStart.adjust(c1);
	AdjustablePosition newEnd = new AbsoluteAdjustablePosition(p0);
	newEnd.adjust(c2);

	start = new AbsolutePosition(newStart);
	end = new AbsolutePosition(newEnd);

	style = POSITION_COUNT_COUNT;
	resolved = true;
    }

    /**
     * Construct an MidPointInterval from a Timestamp and 2 RelativeTimestamps.
     */
    public MidPointInterval(Position p0, RelativeTimestamp rt1, RelativeTimestamp rt2) {
	checkNulls(p0, rt1, rt2);
	
	midPosition = p0;
	offset1 = rt1;
	offset2 = rt2;

	style = POSITION_RELATIVETIMESTAMP_RELATIVETIMESTAMP;
	resolved = false;
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
     * Resolve this interval w.r.t a specified index.
     * The IndexTimestampSelector determines whether to use Index timestamps or 
     * Data timestamps.
     * The Lifetime determines whether timestamps are continuous or discrete.
     * This only affects start points and midpoints.
     * Returns a clone with resolved positions.
     */
    public AbsoluteInterval resolve(Index index, IndexTimestampSelector selector, Lifetime lifetime) {
	MidPointInterval newInterval = null;

	try {
	    switch (style) {
	    case ABSOLUTETIMESTAMP_COUNT_COUNT: {
		TimestampMapping posMid = index.locate(midTimestamp, selector, lifetime);


		if (posMid == null) {
		    return null;
		} else {
		    // resolved start time
		    newInterval = (MidPointInterval)this.clone();

		    newInterval.start = new AbsolutePosition((Position)new AbsoluteAdjustablePosition(posMid).adjust(count1));
		    newInterval.end = new AbsolutePosition((Position)new AbsoluteAdjustablePosition(posMid).adjust(count2));

		    newInterval.resolved = true;
		    return newInterval;
		}
	    }


	    case ABSOLUTETIMESTAMP_RELATIVETIMESTAMP_RELATIVETIMESTAMP: {
		TimestampMapping posMid = index.locate(midTimestamp, selector, lifetime);
		// calcualte the start timestamp
		Timestamp startTS = TimeCalculator.addTimestamp(posMid.timestamp(), offset1);
		// calcualte the end timestamp
		Timestamp endTS = TimeCalculator.addTimestamp(posMid.timestamp(), offset2);

		TimestampMapping posStart = index.locate(startTS, selector, lifetime);
		TimestampMapping posEnd = index.locate(endTS, selector, Lifetime.CONTINUOUS);

		if (posStart == null || posEnd == null) {
		    return null;
		} else {
		    // both resolved
		    newInterval = (MidPointInterval)this.clone();

		    newInterval.start = (Position)posStart;
		    newInterval.end = (Position)posEnd;

		    newInterval.resolved = true;
		    return newInterval;
		}
	    }

	    case POSITION_COUNT_COUNT: {
		// nothing to do
		return newInterval;
	    }

	    case POSITION_RELATIVETIMESTAMP_RELATIVETIMESTAMP: {
		IndexItem item = null;
		Timestamp midTS = null;
		Timestamp startTS = null;
		Timestamp endTS = null;

		try {
		    index.getItem(midPosition);
		} catch (GetItemException gie) {
		    return null;
		}

		if (selector == IndexTimestampSelector.DATA) {
		    midTS = item.getDataTimestamp();
		    System.err.println("POSITION_RELATIVETIMESTAMP_RELATIVETIMESTAMP data TS = " + midTS);
		} else {
		    midTS = item.getIndexTimestamp();
		    System.err.println("POSITION_RELATIVETIMESTAMP_RELATIVETIMESTAMP: index TS = " + midTS);
		}

		// calcualte the start and the end timestamp
		startTS = TimeCalculator.addTimestamp(midTS, offset1);
		endTS = TimeCalculator.addTimestamp(midTS, offset2);
		System.err.println("POSITION_RELATIVETIMESTAMP_RELATIVETIMESTAMP: startTS = " + startTS + " end TS = " + endTS);
		
		TimestampMapping posStart = index.locate(startTS, selector, lifetime);
		TimestampMapping posEnd = index.locate(endTS, selector, Lifetime.CONTINUOUS);

		System.err.println("POSITION_RELATIVETIMESTAMP_RELATIVETIMESTAMP: start = " + posStart + " end = " + posEnd);

		if (posStart == null || posEnd == null) {
		    return null;
		} else {
		    // both resolved
		    newInterval = (MidPointInterval)this.clone();

		    newInterval.start = (Position)posStart;
		    newInterval.end = (Position)posEnd;

		    newInterval.resolved = true;
		    return newInterval;
		}
	    }
	    default:
		throw new Error("MidPointInterval: Unexpected value for style => " + style);
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
	case ABSOLUTETIMESTAMP_COUNT_COUNT: {
	    buffer.append( count1 + " , " + midTimestamp + " , " + count2);
	    break;
	}

	case ABSOLUTETIMESTAMP_RELATIVETIMESTAMP_RELATIVETIMESTAMP: {
	    buffer.append( offset1 + " , " + midTimestamp + " , " + offset2);
	    break;
	}

	case POSITION_COUNT_COUNT: {
	    buffer.append( count1 + " , " + midPosition + " , " + count2);
	    break;
	}

	case POSITION_RELATIVETIMESTAMP_RELATIVETIMESTAMP: {
	    buffer.append( offset1 + " , " + midPosition + " , " + offset2);
	    break;
	}
	default:
	    throw new Error("MidPointInterval: Unexpected value for style => " + style);
	}

	buffer.append(")");

	return buffer.toString();
    }

}
