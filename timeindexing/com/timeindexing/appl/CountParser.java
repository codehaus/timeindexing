// CountParser.java

package com.timeindexing.appl;

import com.timeindexing.basic.Count;
import com.timeindexing.basic.RelativeCount;

/**
 * Parses a count from a given input.
 */
public class CountParser {
    /**
     * Construct a CountParser.
     */
    public CountParser() {
    }


    /**
     * Parse a count  given as a String.
     * e.g. "20"
     * @return a Count object if the input is valid, null otherwise.
     */
    public Count parse(String countStr) {
	long result = 0;

	try {
	    result = Long.parseLong(countStr);
	    return new RelativeCount(result);
	} catch (NumberFormatException nfe) {
	    return null;
	}
    }
}

