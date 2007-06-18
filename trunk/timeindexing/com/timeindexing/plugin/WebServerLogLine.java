// WebServerLogLine.java

package com.timeindexing.plugin;

import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.index.DataType;

import java.nio.ByteBuffer;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.text.*;
import java.text.ParseException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 * A plugin that takes an input stream and
 * returns a line at a time.
 * The data is parsed as a Web log file, and it
 * expects to see dates of the following format: [01/Jul/2003:10:15:05 +0100] .
 */
public class WebServerLogLine extends Line {
    // the raw regexp
    String pattern = null;
    // the compiled regexp
    Pattern compPat = null;
    // a matcher
    Matcher matcher = null;
    // the matched set of characters
    String match = null;
    // a date format 
    SimpleDateFormat format = null;
    Date date = null;
    Timestamp timestamp = null;
    int count = 0;
    Calendar calendar = null;
    DateFormatSymbols formatData = null;
    HashMap monthMap = null;


    /**
     * Construct a WebServerLogLine plugin from an InputStream.
     */
    public WebServerLogLine(InputStream inStream) {
	super(inStream);

	init();
    }

    /**
     * Construct a WebServerLogLine plugin from a Reader.
     */
    public WebServerLogLine(Reader inReader) {
	super(inReader);
	init();
    }

    /**
     * Construct a WebServerLogLine plugin from a BufferedReader.
     */
    public WebServerLogLine(BufferedReader inReader) {
	super(inReader);
	init();
    }

    /**
     * Initialise the plugin
     */
    protected ReaderPlugin init() {
	// The regexp pattern
	pattern = "\\[.*\\]";

	// A web server log date format
	format = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss zzzz]");

	// Regexp set up
	compPat = Pattern.compile(pattern);
	matcher = compPat.matcher("");

	// how many 
	count = 0;

	calendar = Calendar.getInstance();
	formatData = new DateFormatSymbols();
	//initMonthMap();

	return this;
    }

    /*
     * Initialise the monthMap
     */
    protected void initMonthMap() {
	monthMap = new HashMap();
	String [] months = formatData.getShortMonths();

	for (int m=0; m<months.length; m++) {
	    monthMap.put(months[m], new Integer(m));
	}
    }

    /**
     * Process the line
     */
    protected ReaderResult process(String line) {
	timestamp = null;
	count++;

	// pass the matcher the current  line
	matcher.reset(line);

	// try and find the pattern
	if (matcher.find()) {
	    // get the group of characters that matches the pattern
	    match = matcher.group();

	    // Date parsing is very slow
	    // it may be worth writing bespoke code for  this
	    try {
		// try and parse the match as a date
		//System.err.print("Match = " + match);
		date = bespokeParse(match);  // was format.parse(match)
		//System.err.println(". Date = " + date);
	    } catch (ParseException pe) {
		// coudln;t parse a date out of the match characters
		// return now, without a timestamp
		System.err.println(pe.getMessage() + " at position " + pe.getErrorOffset());
		System.err.println("WebServerLogLine: coulbn't parse date in " + line);
		byte [] lineBytes = line.getBytes();
		ByteBuffer lineBuffer = ByteBuffer.allocate(lineBytes.length + 1);
		lineBuffer.put(lineBytes);
		lineBuffer.put(System.getProperty("line.separator").getBytes());
		return new DefaultReaderResult(lineBuffer, null, DataType.TEXT);
		//return new DefaultReaderResult(ByteBuffer.wrap(line.getBytes()), null, DataType.TEXT);
	    }

	    // we got here so we parsed a date successfully

	    // convert the Date to a Timestamp
	    timestamp = new MillisecondTimestamp(date.getTime());

	    // return the line as a ReaderResult with the timestamp
	    byte [] lineBytes = line.getBytes();
	    ByteBuffer lineBuffer = ByteBuffer.allocate(lineBytes.length + 1);
	    lineBuffer.put(lineBytes);
	    lineBuffer.put(System.getProperty("line.separator").getBytes());
	    DefaultReaderResult result = new DefaultReaderResult(lineBuffer, timestamp, DataType.TEXT);
	    //DefaultReaderResult result = new DefaultReaderResult(ByteBuffer.wrap(line.getBytes()), timestamp, DataType.TEXT);

	    return result;
	} else {
	    System.err.println("WebServerLogLine: didn't find pattern in " + line);
	    byte [] lineBytes = line.getBytes();
	    ByteBuffer lineBuffer = ByteBuffer.allocate(lineBytes.length + 1);
	    lineBuffer.put(lineBytes);
	    lineBuffer.put(System.getProperty("line.separator").getBytes());
	    return new DefaultReaderResult(lineBuffer, null, DataType.TEXT);
	    //return new DefaultReaderResult(ByteBuffer.wrap(line.getBytes()), null, DataType.TEXT);
	}
    }

    /**
     * Provide a bespoke parser for dates which look like [08/Jul/2001:07:03:39 +0000]
     */
    public Date bespokeParse(String aDate) throws ParseException {
	/*
         offset: what to do
	 0: get [
	 1: get 2 digits - convert to day no
	 3: get /
	 4: get 3 chars - convert to month no
	 7: get /
	 8: get 4 digits - convert to year 
	 12: get:
	 13: get 2 digits - convert to hour
	 15: get:
	 16: get 2 digits - convert to minutes
	 18: get:
	 19: get 2 digits - convert to seconds
	 21: get ' '
	 22: get +
	 23: get 4 digits - convert to zone
	 27: get ]
	*/

	int offset = 0;

	try {
	    int value = 0;

	    calendar.clear();

	    // [
	    offset = 0;
	    if (aDate.charAt(offset) != '[') {
		throw new ParseException("Expected [ ", offset);
	    }

	    // day no
	    offset = 1;
	    value = Integer.parseInt(aDate.substring(offset, offset+2));
	    calendar.set(Calendar.DAY_OF_MONTH, value);

	    // /
	    offset = 3;
	    if (aDate.charAt(offset) != '/') {
		throw new ParseException("Expected : ", offset);
	    }

	    // month no
	    offset = 4;
	    value = monthIndex(aDate.substring(offset, offset+3), offset);
	    calendar.set(Calendar.MONTH, value);

	    // /
	    offset = 7;
	    if (aDate.charAt(offset) != '/') {
		throw new ParseException("Expected : ", offset);
	    }

	    // year no
	    offset = 8;
	    value = Integer.parseInt(aDate.substring(offset, offset+4));
	    calendar.set(Calendar.YEAR, value);

	    // :
	    offset = 12;
	    if (aDate.charAt(offset) != ':') {
		throw new ParseException("Expected : ", offset);
	    }

	    // hour
	    offset = 13;
	    value = Integer.parseInt(aDate.substring(offset, offset+2));
	    calendar.set(Calendar.HOUR, value);


	    // :
	    offset = 15;
	    if (aDate.charAt(offset) != ':') {
		throw new ParseException("Expected : ", offset);
	    }

	    // minutes
	    offset = 16;
	    value = Integer.parseInt(aDate.substring(offset, offset+2));
	    calendar.set(Calendar.MINUTE, value);

	    // :
	    offset = 18;
	    if (aDate.charAt(offset) != ':') {
		throw new ParseException("Expected : ", offset);
	    }

	    // seconds
	    offset = 19;
	    value = Integer.parseInt(aDate.substring(offset, offset+2));
	    calendar.set(Calendar.SECOND, value);

	    // 
	    offset = 21;
	    if (aDate.charAt(offset) != ' ') {
		throw new ParseException("Expected : ", offset);
	    }

	    // TODO: finish timezone processing

	    
	    return calendar.getTime();
	} catch (NumberFormatException nfe) {
	    throw new ParseException("Expected number; got " + nfe.getMessage(), offset);
	}
    }

    public int monthIndex(String monthString, int offset) throws ParseException {
	String [] months = formatData.getShortMonths();

	for (int m=0; m<months.length; m++) {
	    if (months[m].equals(monthString)) {
		return m;
	    }
	}

	throw new ParseException("Month \"" + monthString + "\" is not valid", offset);

	/*
	Integer month = (Integer)monthMap.get(monthString);
	
	if (month == null) {
	    throw new ParseException("Month \"" + monthString + "\" is not valid", offset);
	} else {
	    return month.intValue();
	}
	*/
    }

    /**
     * Processing at EOF.
     * Return values states if something happended.
     */
    protected boolean eofProcess() {
	 return true;
    }
}
		    
