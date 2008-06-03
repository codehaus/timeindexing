Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
package com.timeindexing.appl;

import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.Count;
import com.timeindexing.basic.EndPointInterval;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.Position;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.time.AbsoluteTimestamp;
import com.timeindexing.time.RelativeTimestamp;
import com.timeindexing.time.TimeDateParser;

/**
 * The IndexPropertiesProcessor has various methods for IndexProperties 
 * and which contain useful functions.
 */
public class IndexPropertiesProcessor {
    /**
     * Create an Interval from an Index and some IndexProperties and creates an
     * Interval.
     */
    public Interval createInterval(Index index, IndexProperties properties) {
	Interval interval = null;
	AbsolutePosition startPos = null;
	Position endPos = null;
	AbsoluteTimestamp startTime = null;
	boolean useStartPos = false;
	PositionParser positionParser = new PositionParser();
	CountParser countParser = new CountParser();
	TimeDateParser timeParser = new TimeDateParser();

	if (properties.containsKey("startpos")) {

	    //System.err.print((String) properties.get("startpos"));

	    // convert string to position object
	    startPos = positionParser.parse((String) properties.get("startpos"));
	    if (startPos == null) {
		startPos = new AbsolutePosition(0);
	    }

	    useStartPos = true;

	} else if (properties.containsKey("starttime")) {

	    //System.err.print((String) properties.get("starttime"));

	    // convert string to time object
	    try {
		startTime = (AbsoluteTimestamp) timeParser.parse((String) properties.get("starttime"), true);
	    } catch (NumberFormatException nfe) {
		//System.err.println("IndexPropertiesProcessor: starttime = " + properties.get("starttime"));
	    }

	    if (startTime == null) {
		startPos = new AbsolutePosition(0);
		useStartPos = true;
	    } else {
		useStartPos = false;
	    }

	} else {
	    //System.err.print("START");

	    startPos = new AbsolutePosition(0);
	    useStartPos = true;
	}

	//System.err.print(" End = ");

	/*
	 * Determine end of selection. We already know something about the start
	 * of the selection.
	 */
	if (properties.containsKey("endpos")) {

	    //System.err.print((String) properties.get("endpos"));

	    // convert string to position object
	    endPos = positionParser.parse((String) properties.get("endpos"));
	    if (endPos == null) {
		endPos = new AbsolutePosition(index.getLength());
	    }

	    if (useStartPos) {
		interval = new EndPointInterval(startPos, endPos);
	    } else {
		interval = new EndPointInterval(startTime, endPos);
	    }

	} else if (properties.containsKey("endtime")) {

	    //System.err.print((String) properties.get("endtime"));

	    // convert string to time object
	    AbsoluteTimestamp endTime = null;

	    try {
		endTime = (AbsoluteTimestamp) timeParser.parse((String) properties.get("endtime"), true);
	    } catch (NumberFormatException nfe) {
		//System.err.println("IndexPropertiesProcessor: endtime = " + properties.get("endtime"));
	    }

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

	    //System.err.print((String) properties.get("count"));

	    // convert string to count object
	    Count count = countParser.parse((String) properties.get("count"));

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

	    //System.err.print((String) properties.get("for"));

	    // convert string to relative time object
	    RelativeTimestamp elapsed = (RelativeTimestamp) timeParser.parse((String) properties.get("for"), false);
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

	    //System.err.print("END");

	    endPos = new AbsolutePosition(index.getLength());

	    if (useStartPos) {
		interval = new EndPointInterval(startPos, endPos);
	    } else {
		interval = new EndPointInterval(startTime, endPos);
	    }

	}
		
	return interval;

    }


    /**
     * Create a useful filename given a root name and some IndexProperties.
     */
    public String fileNameGenerator(String rootName, IndexProperties properties) {
	StringBuffer generatedName = new StringBuffer(128);

	// add name
	generatedName.append(rootName);	

	/*
	 * Determine start of selection.
	 */
	if (properties.containsKey("startpos")) {
	    // get the start position
	    generatedName.append("-");
	    generatedName.append((String)properties.get("startpos"));

	} else if (properties.containsKey("starttime")) {
	    // get the start time
	    generatedName.append("-");
	    generatedName.append((String)properties.get("starttime"));

	} else {
	    // the start
	    ;
	}
	    
	/*
	 * Determine end of selection.
	 * We already know something about the start of the selection.
	 */
	if (properties.containsKey("endpos")) {
	    // get the end position
	    generatedName.append("-to-");
	    generatedName.append((String)properties.get("endpos"));

	} else if (properties.containsKey("endtime")) {
	    // get the end time
	    generatedName.append("-to-");
	    generatedName.append((String)properties.get("endtime"));

	} else if (properties.containsKey("count")) {
	    // get a count
	    generatedName.append("+");
	    generatedName.append((String)properties.get("count"));

	} else if (properties.containsKey("for")) {
	    // get the elapsed time
	    generatedName.append("+");
	    generatedName.append((String)properties.get("for"));

	} else {
	    //the end
	}

	return generatedName.toString();


    }

    /**
     * Pretty Print some IndexProperties for specifying an Interval.
     */
    public String prettyPrint(IndexProperties properties) {
	StringBuffer buffer = new StringBuffer(128);

	buffer.append(" Start = ");

	if (properties.containsKey("startpos")) {
	    buffer.append((String) properties.get("startpos"));
	} else if (properties.containsKey("starttime")) {
	    buffer.append((String) properties.get("starttime"));
	} else {
	    buffer.append("START");
	}

	buffer.append(" End = ");

	/*
	 * Determine end of selection. We already know something about the start
	 * of the selection.
	 */
	if (properties.containsKey("endpos")) {
	    buffer.append((String) properties.get("endpos"));
	} else if (properties.containsKey("endtime")) {
	    buffer.append((String) properties.get("endtime"));
	} else if (properties.containsKey("count")) {
	    buffer.append((String) properties.get("count"));
	} else if (properties.containsKey("for")) {
	    buffer.append((String) properties.get("for"));
	} else {
	    buffer.append("END");
	}

	return buffer.toString();
    
    }
}
