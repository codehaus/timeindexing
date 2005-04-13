// SelectionProcessor.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.AbsoluteInterval;
import com.timeindexing.basic.EndPointInterval;
import com.timeindexing.basic.MidPointInterval;
import com.timeindexing.basic.AbsoluteAdjustablePosition;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.Overlap;
import com.timeindexing.basic.Count;
import com.timeindexing.time.Clock;
import com.timeindexing.time.*;


/**
 * This class is used by applications to do processing
 * of values in order to get selection from an Index.
 * <p>
 * The values passed to the SelectionProcessor get passed in 
 * as an IndexProperties object.
 * Types of values in the properties are:
 * <ul>
 * <li> "startpos" -> value accepted by PositionParser
 * <li> "starttime" -> value accepted by TimeDateParser
 * <li> "endpos" ->  value accepted by PositionParser
 * <li> "endtime" -> value accepted by TimeDateParser
 * <li> "count" -> value accepted by CountParser
 * <li> "for" -> value accepted by TimeDateParser
 * </ul>
 */
public class SelectionProcessor {

    /**
     * Construct a SelectionProcessor.
     */
    public SelectionProcessor() {
	;
    }

    /**
     * Process a selction given an Index and some index properties.
     * It will ignore properties not relevant to the selection.
     * It uses a Lifetime enumeration of DISCRETE.
     */
    public IndexView select(IndexView index, IndexProperties properties) {
	return select(index, properties, Lifetime.DISCRETE);
    }

    /**
     * Process a selction given an Index and some index properties.
     * It will ignore properties not relevant to the selection.
     */
    public IndexView select(IndexView index, IndexProperties properties, Lifetime lifetime) {
	AbsolutePosition startPos = null;
	Position endPos = null;
	AbsoluteTimestamp startTime = null;
	boolean useStartPos = false;
	Interval interval = null;
	IndexView selection = null;
	PositionParser positionParser = new PositionParser();
	CountParser countParser = new CountParser();
	TimeDateParser timeParser = new TimeDateParser();

	/*
	 * Determine start of selection.
	 */
	if (properties.containsKey("startpos")) {
	    // convert string to position object
	    startPos = positionParser.parse((String)properties.get("startpos"));
	    if (startPos == null) {
		startPos = new AbsolutePosition(0);
	    }

	    useStartPos = true;

	} else if (properties.containsKey("starttime")) {
	    // convert string to time object
	    startTime = (AbsoluteTimestamp)timeParser.parse((String)properties.get("starttime"), true);
	    if (startTime == null) {
		startPos = new AbsolutePosition(0);
		useStartPos = true;
	    } else {
		useStartPos = false;
	    }


	} else {
	    startPos = new AbsolutePosition(0);
	    useStartPos = true;
	}
	    
	/*
	 * Determine end of selection.
	 * We already know something about the start of the selection.
	 */
	if (properties.containsKey("endpos")) {
	    // convert string to position object
	    endPos = positionParser.parse((String)properties.get("endpos"));
	    if (endPos == null) {
		endPos = new AbsolutePosition(index.getLength());
	    }

	    if (useStartPos) {
		interval = new EndPointInterval(startPos, endPos);
	    } else {
		interval = new EndPointInterval(startTime, endPos);
	    }

	} else if (properties.containsKey("endtime")) {
	    // convert string to time object
	    AbsoluteTimestamp endTime = (AbsoluteTimestamp)timeParser.parse((String)properties.get("endtime"), true);
	    if (endTime == null) {
		endPos = new AbsolutePosition(index.getLength());
	    }

	    if (useStartPos) {
		if (endTime == null) {
		    interval = new EndPointInterval(startPos, endPos);
		} else {
		    interval = new EndPointInterval(startPos, endTime);
		} 
	    } else {
		if (endTime == null) {
		    interval = new EndPointInterval(startTime, endPos);
		} else {
		    interval = new EndPointInterval(startTime, endTime);
		}
	    }


	} else if (properties.containsKey("count")) {
	    // convert string to count object
	    Count count = countParser.parse((String)properties.get("count"));

	    if (count == null) {
		endPos = new AbsolutePosition(index.getLength());
	    }

	    if (useStartPos) {
		if (count == null) {
		    interval = new EndPointInterval(startPos, endPos);
		} else {
		    interval = new EndPointInterval(startPos, count);
		} 
	    } else {
		if (count == null) {
		    interval = new EndPointInterval(startTime, endPos);
		} else {
		    interval = new EndPointInterval(startTime, count);
		}
	    }



	} else if (properties.containsKey("for")) {
	    // convert string to relative time object
	    RelativeTimestamp elapsed = (RelativeTimestamp)timeParser.parse((String)properties.get("for"), false);
	    if (elapsed == null) {
		endPos = new AbsolutePosition(index.getLength());
	    }

	    if (useStartPos) {
		if (elapsed == null) {
		    interval = new EndPointInterval(startPos, endPos);
		} else {
		    interval = new EndPointInterval(startPos, elapsed);
		} 
	    } else {
		if (elapsed == null) {
		    interval = new EndPointInterval(startTime, endPos);
		} else {
		    interval = new EndPointInterval(startTime, elapsed);
		}
	    }


	} else {
	    endPos = new AbsolutePosition(index.getLength());

	    if (useStartPos) {
		interval = new EndPointInterval(startPos, endPos);
	    } else {
		interval = new EndPointInterval(startTime, endPos);
	    }

	}
	   
	// select from the interval
	selection = index.select(interval, IndexTimestampSelector.DATA, Overlap.FREE, lifetime);

	return selection;
    }

}
