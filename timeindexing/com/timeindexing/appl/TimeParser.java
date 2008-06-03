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



// TimeParser.java

package com.timeindexing.appl;

import com.timeindexing.time.*;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * Parses a time from a given input.
 */
public class TimeParser {
    /**
     * Construct a TimeParser.
     */
    public TimeParser() {
    }

    
    /** 
     * Parse timestamps given as a String.
     * Absolute or relative timestamps can be specified. 
     * <p>
     * The timestamps are of the format "time.subseconds"
     * Formats processed for time are:
     * <ul>
     * <li> yyyy/MM/dd HH:mm:ss 
     * <li> yyyy/MM/dd HH:mm
     * <li> yyyy/MM/dd
     * <li> HH:mm:ss
     * <li> mm:ss
     * </ul>
     * The returned Timestamp is at a resolution based on the
     * resolution of the passed in string.
     * Formats for subseconds can resolve down to nanoseconds.
     * e.g.  .2 is 2/10ths of a seconds, .200 is 200 milliseconds,
     * .25000 is 25000 microseconds, and so on.     
     * @param timeArg the inout string
     * @param absolute should the timestamp be parsed as an absolute
     * time or a relative time.
     * @return a Timestamp object  if the input is valid, null otherwise.
     */
    public Timestamp parse(String timeArg, boolean absolute) {
	Date date = null;
	long seconds = 0;
	int subSeconds = 0;
	String timeStr = null;
	String subSecondStr =null;

	ParsePosition pos = new ParsePosition(0);

	SimpleDateFormat fullestFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss Z");
	SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm Z");
	SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy/MM/dd Z");
	SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm:ss Z");
	SimpleDateFormat hourFormat = new SimpleDateFormat("mm:ss Z");
	SimpleDateFormat secondFormat = new SimpleDateFormat("ss Z");


	// Split the time using . as the separator
	// Before the . are dates and times in the second domain.
	// After the dot are times in the subsecond domain
	int postitionOfDot = timeArg.indexOf('.');

	if (postitionOfDot == -1) {                        // there is NO .
	    // a time in whole seconds
	    timeStr = timeArg + " +0000";
	} else {
	    // split the time into two distinct parts
	    String secondPart = timeArg.substring(0, postitionOfDot);
	    String subSecondPart = timeArg.substring(postitionOfDot+1);
	    
	    timeStr = secondPart + " +0000";
	    subSecondStr = subSecondPart;
	}
	    

	//System.err.println("Parse input = " + dateStr);

	if (date == null) {	// the time string is null
	    // let's try dayFormat
	    date = dayFormat.parse(timeStr, pos);
	    //System.err.println("Parse day = " + date);
	}

	if (date == null) {	// the time string is null
	    // let's try hourFormat
	    date = hourFormat.parse(timeStr, pos);
	    //System.err.println("Parse hour = " + date);
	}

	if (date == null) {	// the time string is null
	    // let's try secondFormat
	    date = secondFormat.parse(timeStr, pos);
	    //System.err.println("Parse secs = " + date);
	}

	if (date == null) {	// the time string is null
	    // let's try Format
	    date = fullestFormat.parse(timeStr, pos);
	    //System.err.println("Parse date = " + date);
	}

	if (date == null) {	// the time string is null
	    // let's try Format
	    date = fullFormat.parse(timeStr, pos);
	    //System.err.println("Parse date = " + date);
	}

	if (date == null) {	// the time string is null
	    // let's try Format
	    date = ymdFormat.parse(timeStr, pos);
	    //System.err.println("Parse date = " + date);
	}

	if (date == null) {	//  all the time parses failed 
	    // so set it to zero
	    seconds = 0;
	} else {
	    seconds = date.getTime()/1000;
	}


	//System.err.println("Parsed seconds = " + seconds);

	// check the ParsePosition
	int next = pos.getIndex();


	// how long was the subsecond part
	int subSecondLen = 0;

	//System.err.println("subSecondStr = " + subSecondStr);

	if (postitionOfDot > -1) {              // there were subseconds
	    StringBuffer subSecondBuf = null;
	    subSecondLen = subSecondStr.length();
	    if (subSecondLen <= 3) {
		// time string was seconds.sss
		subSecondBuf = new StringBuffer("000");
		subSecondBuf.replace(0, subSecondLen, subSecondStr);

	    } else if (subSecondLen <= 6) {
		// time string was seconds.ssssss
		subSecondBuf = new StringBuffer("000000");
		subSecondBuf.replace(0, subSecondLen, subSecondStr);
	    
	    } else if (subSecondLen <= 9) {
		// time string was seconds.sssssssss
		subSecondBuf = new StringBuffer("000000000");
		subSecondBuf.replace(0, subSecondLen, subSecondStr);
	    
	    } else {
		// we can;t do more than nanoseconds at present
		// so welll take the first 9 digits
		subSecondBuf = new StringBuffer("000000000");
		subSecondBuf.replace(0, 9, subSecondStr.substring(0,9));
	    
	    }


	    try {
		subSeconds = Integer.parseInt(subSecondBuf.toString());
	    } catch (NumberFormatException nfe) {
		System.err.println("Failed to parseInt() \"" + subSecondBuf + "\"");
		subSeconds = 0;
	    }

	}
	
	Timestamp timestamp =  null;

	// create a timestamp with the relvant granulariy

	    if (subSecondLen == 0) {
		// time string was seconds.
		if (absolute) {
		    timestamp = new SecondTimestamp(seconds, subSeconds);
		} else { // elapsed 
		    timestamp = new ElapsedSecondTimestamp(seconds, subSeconds);
		}
	    } else if (subSecondLen <= 3) {
		// time string was seconds.sss
		if (absolute) {
		    timestamp = new MillisecondTimestamp(seconds, subSeconds*1000000);
		} else { // elapsed 
		    timestamp = new ElapsedMillisecondTimestamp(seconds, subSeconds*1000000);
		}
	    } else if (subSecondLen <= 6) {
		// time string was seconds.ssssss
		if (absolute) {
		    timestamp = new MicrosecondTimestamp(seconds, subSeconds*1000);
		} else { // elapsed 
		    timestamp = new ElapsedMicrosecondTimestamp(seconds, subSeconds*1000);
		}
	    } else if (subSecondLen <= 9) {
		// time string was seconds.sssssssss
		if (absolute) {
		    timestamp = new NanosecondTimestamp(seconds, subSeconds);
		} else { // elapsed 
		    timestamp = new ElapsedNanosecondTimestamp(seconds, subSeconds);
		}
	    } else {
		// we can;t do more than nanoseconds at present
		// so welll take the first 9 digits
		if (absolute) {
		    timestamp = new NanosecondTimestamp(seconds, subSeconds);
		} else { // elapsed 
		    timestamp = new ElapsedNanosecondTimestamp(seconds, subSeconds);
		}

	    }



	    //System.err.println("Parsed TS = " + timestamp);

	return timestamp;
    }
}

