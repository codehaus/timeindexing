// PositionParser.java

package com.timeindexing.appl;

import com.timeindexing.basic.Position;
import com.timeindexing.basic.AbsolutePosition;

/**
 * Parses a position from a given input.
 */
public class PositionParser {
    /**
     * Construct a PositionParser.
     */
    public PositionParser() {
    }

    /**
     * Parse a position given as a String.
     * e.g "500"
     * @return a Position object if the input is valid, null otherwise.
     */
    public Position parse(String posStr) {
	long result = 0;

	try {
	    result = Long.parseLong(posStr);
	    return new AbsolutePosition(result);
	} catch (NumberFormatException nfe) {
	    return null;
	}
    }
}

