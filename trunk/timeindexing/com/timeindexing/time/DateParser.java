// DateParser.java

package com.timeindexing.time;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.TimeZone;


/**
 * A Date parserthat parses times and dates reltive to GMT.
 * The date parsing required here is not interested in TimeZones,
 * so this forces a time zone speicfication.
 */
public class DateParser {
    SimpleDateFormat formatter = null;

    /**
     * Construct a DateFormatter.
     */
    protected DateParser(String pattern) {
	formatter = new SimpleDateFormat(pattern + " Z");
    }

    /**
     * Parse a Date from a string using this format from
     * the specified postition.
     */
    public Date parse(String input, ParsePosition pos) {
	return formatter.parse(input + " +0000", pos);
    }

    /**
     * Parse a Date from a string using this format.
     */
    public Date parse(String input) {
	return formatter.parse(input + " +0000", new ParsePosition(0));
    }
}
