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



// TimeDateParser.java

package com.timeindexing.time;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Calendar;

/**
 * Parses a time or date from a given input.
 */
public class TimeDateParser {
    /*
     * A format for year/month/day plus hour:minutes:seconds
     */
    protected DateParser fullestFormat = new DateParser("yyyy/MM/dd HH:mm:ss");
    /*
     * A format for day/month/year plus hour:minutes:seconds
     */
    protected DateParser dmyFullestFormat = new DateParser("dd/MM/yyyy HH:mm:ss");
    /*
     * A format for year/month/day plus hour:minutes:seconds with no spaces
     */
    protected DateParser fullestFormatNS = new DateParser("yyyy/MM/dd-HH:mm:ss");
    /*
     * A format for year/month/day plus hour:minutes
     */
    protected DateParser fullFormat = new DateParser("yyyy/MM/dd HH:mm");
    /*
     * A format for day/month/year plus hour:minutes
     */
    protected DateParser dmyFullFormat = new DateParser("dd/MM/yyyy HH:mm");
    /*
     * A format for year/month/day plus hour:minutes with no spaces
     */
    protected DateParser fullFormatNS = new DateParser("yyyy/MM/dd-HH:mm");
    /*
     * A format for year/month/day
     */
    protected DateParser ymdFormat = new DateParser("yyyy/MM/dd");
    /*
     * A format for day/month/year
     */
    protected DateParser dmyFormat = new DateParser("dd/MM/yyyy");
    /*
     * A format for the first day
     */
    protected DateParser dayFormat = new DateParser("HH:mm:ss");
    /*
     * A format for the first hour
     */
    protected DateParser hourFormat = new DateParser("mm:ss");
    /*
     * A format for seconds only
     */
    protected DateParser secondFormat = new DateParser("ss");

    Calendar calendar = null;

    /**
     * Construct a TimeDateParser.
     */
    public TimeDateParser() {
	calendar = Calendar.getInstance();
    }

    
    /** 
     * Parse timestamps given as a String.
     * Absolute or relative timestamps can be specified. 
     * <p>
     * The timestamps are of the format "time.subseconds"
     * Formats processed for time are:
     * <ul>
     * <li> yyyy/MM/dd HH:mm:ss 
     * <li> dd/MM/yyyy HH:mm:ss 
     * <li> yyyy/MM/dd HH:mm
     * <li> dd/MM/yyyy HH:mm
     * <li> yyyy/MM/dd
     * <li> dd/MM/yyyy
     * <li> HH:mm:ss
     * <li> mm:ss
     * <li> ss
     * </ul>
     * The returned Timestamp is at a resolution based on the
     * resolution of the passed in string.
     * Formats for subseconds can resolve down to nanoseconds.
     * e.g.  .2 is 2/10ths of a seconds, .200 is 200 milliseconds,
     * .25000 is 25000 microseconds, and so on.     
     * @param timeArg the input string
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
	    int subSecondLen = 0;

	    // Split the time using . as the separator
	    // Before the . are dates and times in the second domain.
	    // After the dot are times in the subsecond domain
	    int postitionOfDot = timeArg.indexOf('.');

	    if (postitionOfDot == -1) {                        // there is NO .
		// a time in whole seconds
		timeStr = timeArg;
	    } else {
		// split the time into two distinct parts
		String secondPart = timeArg.substring(0, postitionOfDot);
		String subSecondPart = timeArg.substring(postitionOfDot+1);
	    
		timeStr = secondPart;
		subSecondStr = subSecondPart;
		subSecondLen = subSecondStr.length();
	    }
	    

	    //System.err.println("Parse input = " + dateStr);

	    if (date == null) {	// the time string is null
		// let's try dayFormat
		date = dayFormat.parse(timeStr);
		//System.err.println("Parse day = " + date);
	    }

	    if (date == null) {	// the time string is null
		// let's try hourFormat
		date = hourFormat.parse(timeStr);
		//System.err.println("Parse hour = " + date);
	    }

	    if (date == null) {	// the time string is null
		// let's try secondFormat
		// No Don't - it just doesn't work
		// SimpleDateFormat is full of irregularities.
		// WAS date = secondFormat.parse(timeStr);
		try {
		    long number = Long.parseLong(timeStr);
		    //System.err.println("Parse secs = " + number);
		    date = new Date (number*1000);
		} catch (NumberFormatException nfe) {
		    ;
		}
		//System.err.println("Parse secs = " + date);
	    }


	    if (date == null) {	// the time string is null
		// let's try Format
		date = fullestFormatNS.parse(timeStr);
		//System.err.println("Parse Fullest date = " + date);
	    }

	    checkpoint(date);

	    if (date == null) {	// the time string is null
		// let's try Format
		date = fullestFormat.parse(timeStr);
		//System.err.println("Parse Fullest date = " + date);
	    }

	    checkpoint(date);

	    if (date == null || calendar.get(Calendar.YEAR) < 100) {	// the time string is null
		// let's try Format
		date = dmyFullestFormat.parse(timeStr);
		//System.err.println("Parse Fullest date = " + date);
	    }

	    checkpoint(date);

	    if (date == null) {	// the time string is null
		// let's try Format
		date = fullFormatNS.parse(timeStr);
		//System.err.println("Parse Full  date = " + date);
	    }

	    checkpoint(date);

	    if (date == null) {	// the time string is null
		// let's try Format
		date = fullFormat.parse(timeStr);
		//System.err.println("Parse Full  date = " + date);
	    }

	    checkpoint(date);

	    if (date == null) {	// the time string is null
		// let's try Format
		date = dmyFullFormat.parse(timeStr);
		//System.err.println("Parse Full  date = " + date);
	    }

	    checkpoint(date);

	    if (date == null || calendar.get(Calendar.YEAR) < 100) {	// the time string is null
		// let's try Format
		date = ymdFormat.parse(timeStr);
		//System.err.println("Parse YMD date = " + date);
	    }

	    checkpoint(date);

	    if (date == null || calendar.get(Calendar.YEAR) < 100) {	// the time string is null
		// let's try Format
		date = dmyFormat.parse(timeStr);
		//System.err.println("Parse DMY date = " + date);
	    }

	    checkpoint(date);

	    if (date == null) {	//  all the time parses failed 
		// so return null
		return null;
	    } else {
		seconds = date.getTime()/1000;
	    }

	    //System.err.println("Parsed seconds = " + seconds);

	    checkpoint(date);

	    // get the no of subseconds
	    if (postitionOfDot > -1) {              // there were subseconds
		subSeconds = parseSubseconds(subSecondStr);
		//System.err.println("subSeconds = " + subSeconds);
	    } 

	    Timestamp timestamp = createTimestamp(seconds, subSeconds, subSecondLen, absolute);


	    return timestamp;
    }


    
    /** 
     * Parse timestamps given as a String.
     * Absolute or relative timestamps can be specified.
     * The parse format also has to be specified.
     * <p>
     * The timestamps are of the format "time.subseconds"
     * Formats processed for time are those supported by 
     * java.text.SimpleDateFormat.parse().
     * <p>
     * The returned Timestamp is at a resolution based on the
     * resolution of the passed in string.
     * Formats for subseconds can resolve down to nanoseconds.
     * e.g.  .2 is 2/10ths of a seconds, .200 is 200 milliseconds,
     * .25000 is 25000 microseconds, and so on.     
     * @param timeArg the input string
     * @param absolute should the timestamp be parsed as an absolute
     * time or a relative time.
     * @param parseFormat  the parse format to use when trying to parse timeStr.
     * @return a Timestamp object  if the input is valid, null otherwise.
     */
    public Timestamp parse(String timeArg, boolean absolute, String parseFormat) {
	Date date = null;
	long seconds = 0;
	int subSeconds = 0;
	String timeStr = null;
	String subSecondStr =null;
	int subSecondLen = 0;

	// Split the time using . as the separator
	// Before the . are dates and times in the second domain.
	// After the dot are times in the subsecond domain
	int postitionOfDot = timeArg.indexOf('.');

	if (postitionOfDot == -1) {                        // there is NO .
	    // a time in whole seconds
	    timeStr = timeArg;
	} else {
	    // split the time into two distinct parts
	    String secondPart = timeArg.substring(0, postitionOfDot);
	    String subSecondPart = timeArg.substring(postitionOfDot+1);
	    
	    timeStr = secondPart;
	    subSecondStr = subSecondPart;
	    subSecondLen = subSecondStr.length();
	}
	    

	// try and parse using the specified parseFormat
	DateParser parser = new DateParser(parseFormat);
	date = parser.parse(timeStr);

	if (date == null) {	//  all the time parses failed 
	    // so return null;
	    return null;
	} else {
	    seconds = date.getTime()/1000;
	}

	//System.err.println("Parsed seconds = " + seconds);

	if (postitionOfDot > -1) {              // there were subseconds
	    subSeconds = parseSubseconds(subSecondStr);
	}

	Timestamp timestamp = createTimestamp(seconds, subSeconds, subSecondLen, absolute);

	return timestamp;
    }


    /**
     * Parse the subsecond part of a time String.
     * @return the number seconds.
     */
    protected int parseSubseconds(String subSecondStr) {
	// the no of subseconds
	int subSeconds = 0;

	//System.err.println("subSecondStr = " + subSecondStr);

	StringBuffer subSecondBuf = null;
	int subSecondLen = subSecondStr.length();

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

	return subSeconds;
    }

    /**
     * Create a Timestamp given some seconds, some subSeconds, the length of the original
     * subSecond string, and whether to create an absolute or relative Timestamp.
     */
    protected Timestamp createTimestamp(long seconds, int subSeconds, int subSecondLen, boolean absolute) {
        Timestamp timestamp =  null;
 
        // create a timestamp with the relvant granulariy
 
	if (subSecondLen == 0) {
	    // time string was seconds.
	    if (absolute) {
		timestamp = new SecondTimestamp(seconds, subSeconds);
	    } else { // elapsed
		timestamp = new ElapsedSecondTimestamp(seconds, subSeconds);                }
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
 
         return timestamp;
    }


    private void checkpoint(Date date) {
	if (date != null) {
	    calendar.setTime(date);
	    //System.err.println("Year = " + calendar.get(Calendar.YEAR));
	}
    }

}

