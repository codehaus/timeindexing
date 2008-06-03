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
