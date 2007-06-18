// MailServerLogLine.java

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
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
/**
 * A plugin that takes an input stream and
 * returns a line at a time.
 * The data is parsed as a Mail log file, and it
 * expects to see dates of the following format: Mar 17 14:01:03 .
 * Mail server logs do not put the year in the log file, so
 * we need a mechanism to put one in.
 * The solution chosen is to find out what this year is.
 * It is possible to tell this plugin
 * what the year is using the setYear() method.
 */
public class MailServerLogLine extends Line {
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
    // the date found
    Date date = null;
    // the year to use for mail log entries
    int year = 1970;
    // the calculated Timestamp
    Timestamp timestamp = null;

    // last month
    String lastMonth = "";
    // year roll count
    int yearCount = 0;

    /**
     * Construct a MailServerLogLine plugin from an InputStream.
     */
    public MailServerLogLine(InputStream inStream) {
	super(inStream);

	init();
    }

    /**
     * Construct a MailServerLogLine plugin from a Reader.
     */
    public MailServerLogLine(Reader inReader) {
	super(inReader);
	init();
    }

    /**
     * Construct a MailServerLogLine plugin from a BufferedReader.
     */
    public MailServerLogLine(BufferedReader inReader) {
	super(inReader);
	init();
    }

    /**
     * Initialise the plugin
     */
    protected ReaderPlugin init() {
	// Calculate this year
	Calendar calendar = Calendar.getInstance();
	year = calendar.get(Calendar.YEAR);

	// The regexp pattern for Mar 17 14:01:03
	pattern = "^[A-Z][a-z][a-z] [\\d ]\\d \\d\\d:\\d\\d:\\d\\d";

	// A web server log date format
	// pretend it has the year on the end
	format = new SimpleDateFormat("MMM dd HH:mm:ss yyyy");

	// Regexp set up
	compPat = Pattern.compile(pattern);
	matcher = compPat.matcher("");

	return this;
    }

    /**
     * Process the line
     */
    protected ReaderResult process(String line) {
	// pass the matcher the current  line
	matcher.reset(line);
	
	// try and find the pattern
	if (matcher.find()) {
	    // get the group of characters that matches the pattern
	    match = matcher.group();

	    //System.err.println("match = " + match);

	    try {
		// find the month in match
		String month = match.substring(0,3);

		// only worth checking in January
		if (month.equals("Jan")) {
		    if (lastMonth.equals("Dec")) {
			// we just rolled over a year boundary
			// so increment yearCount
			yearCount++;
		    }
		}
		lastMonth = month;
		
		// try and parse the match as a date
		date = format.parse(match + " " + (year+yearCount));

	    } catch (ParseException pe) {
		// coudln;t parse a date out of the match characters
		// return now, without a timestamp
		 return new DefaultReaderResult(ByteBuffer.wrap(line.getBytes()), null, DataType.TEXT);
	    }

	    // we got here so we parsed a date successfully

	    // convert the Date to a Timestamp
	    timestamp = new MillisecondTimestamp(date.getTime());

	    // return the line as a ReaderResult with the timestamp
	    return new DefaultReaderResult(ByteBuffer.wrap(line.getBytes()), timestamp, DataType.TEXT);
	} else {
	    return new DefaultReaderResult(ByteBuffer.wrap(line.getBytes()), null, DataType.TEXT);
	}
    }


    /**
     * Set the year to start from
     */
    protected MailServerLogLine setYear(int yr) {
	year = yr;
	return this;
    }

    /**
     * Processing at EOF.
     * Return values states if something happended.
     */
    protected boolean eofProcess() {
	 return true;
    }
}
		    
