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
import java.text.SimpleDateFormat;
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

	return this;
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
		date = format.parse(match);
	    } catch (ParseException pe) {
		// coudln;t parse a date out of the match characters
		// return now, without a timestamp
		System.err.println("WebServerLogLine: coulbn't parse date in " + line);
		byte [] lineBytes = line.getBytes();
		ByteBuffer lineBuffer = ByteBuffer.allocate(lineBytes.length + 1);
		lineBuffer.put(lineBytes);
		lineBuffer.put(System.getProperty("line.separator").getBytes());
		return new DefaultReaderResult(lineBuffer, null, DataType.TEXT_DT);
		//return new DefaultReaderResult(ByteBuffer.wrap(line.getBytes()), null, DataType.TEXT_DT);
	    }

	    // we got here so we parsed a date successfully

	    // convert the Date to a Timestamp
	    timestamp = new MillisecondTimestamp(date.getTime());

	    // return the line as a ReaderResult with the timestamp
	    byte [] lineBytes = line.getBytes();
	    ByteBuffer lineBuffer = ByteBuffer.allocate(lineBytes.length + 1);
	    lineBuffer.put(lineBytes);
	    lineBuffer.put(System.getProperty("line.separator").getBytes());
	    DefaultReaderResult result = new DefaultReaderResult(lineBuffer, timestamp, DataType.TEXT_DT);
	    //DefaultReaderResult result = new DefaultReaderResult(ByteBuffer.wrap(line.getBytes()), timestamp, DataType.TEXT_DT);

	    return result;
	} else {
	    System.err.println("WebServerLogLine: didn't find pattern in " + line);
	    byte [] lineBytes = line.getBytes();
	    ByteBuffer lineBuffer = ByteBuffer.allocate(lineBytes.length + 1);
	    lineBuffer.put(lineBytes);
	    lineBuffer.put(System.getProperty("line.separator").getBytes());
	    return new DefaultReaderResult(lineBuffer, null, DataType.TEXT_DT);
	    //return new DefaultReaderResult(ByteBuffer.wrap(line.getBytes()), null, DataType.TEXT_DT);
	}
    }

    /**
     * Processing at EOF.
     * Return values states if something happended.
     */
    protected boolean eofProcess() {
	 return true;
    }
}
		    
