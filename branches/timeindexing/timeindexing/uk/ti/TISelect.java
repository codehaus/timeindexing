package uk.ti;

import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.time.Clock;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.AbsoluteTimestamp;
import com.timeindexing.time.NanosecondTimestamp;
import com.timeindexing.time.RelativeTimestamp;
import com.timeindexing.time.ElapsedNanosecondTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.time.TimestampMapping;
import com.timeindexing.time.Lifetime;
import com.timeindexing.basic.Interval;
import com.timeindexing.basic.AbsoluteInterval;
import com.timeindexing.basic.EndPointInterval;
import com.timeindexing.basic.MidPointInterval;
import com.timeindexing.basic.AbsolutePosition;
import com.timeindexing.basic.AbsoluteAdjustablePosition;
import com.timeindexing.basic.Position;
import com.timeindexing.basic.RelativeCount;
import com.timeindexing.basic.Overlap;
import com.timeindexing.basic.Count;
import java.io.File;
import java.io.PrintStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Date;
import java.util.TimeZone;
import java.util.Random;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * First test of index selection.
 */
public class TISelect extends TIAbstractRestore {
    int intervalStart = 0;
    int intervalEnd = 0;
    TimeIndexFactory factory = null;
    Properties argProps = null;

    public static void main(String [] args) {
	Properties properties = new Properties();
	TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

	/*
	 * Handle broken pipes properly
	 */
	final Signal sigpipe = new Signal("PIPE");

	SignalHandler handler = new SignalHandler () {
		public void handle(Signal sig) {
		    System.exit(sigpipe.hashCode());
		}
	    };
	Signal.handle(sigpipe, handler);
 

	if (args.length == 1) {
	    // have tifile name,
	    properties.setProperty("filename", args[0]);
	    new TISelect(properties);
	} else if (args.length == 0) {
	    help(System.err);
	} else {
	    processArgs(args, properties);
	    new TISelect(properties);
	}
    }

    /**
     * Process the arguments
     */
    public static void processArgs(String[] args, Properties properties) {
	int argc = 0;
	boolean startDone = false;
	boolean endDone = false;

	while (argc< args.length) {
	    if (args[argc].charAt(0) == '-') {		// its a flag
		if (args[argc].equals("-sp")) {
		    if (startDone) {
			System.err.println("An interval start already processed");
		    } else {
			properties.setProperty("startpos", args[argc+1]);
			startDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-st")) {
		    if (startDone) {
			System.err.println("An interval start already processed");
		    } else {
			properties.setProperty("starttime", args[argc+1]);
			startDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-ep")) {
		    if (endDone) {
			System.err.println("An interval end already processed");
		    } else {
			properties.setProperty("endpos", args[argc+1]);
			endDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-et")) {
		    if (endDone) {
			System.err.println("An interval end already processed");
		    } else {
			properties.setProperty("endtime", args[argc+1]);
			endDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-c")) {
		    if (endDone) {
			System.err.println("An interval end already processed");
		    } else {
			properties.setProperty("count", args[argc+1]);
			endDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-f")) {
		    if (endDone) {
			System.err.println("An interval end already processed");
		    } else {
			properties.setProperty("for", args[argc+1]);
			endDone = true;
		    }
		    // skip to 2 args
		    argc += 2;

		} else if (args[argc].equals("-n")) {
		    properties.setProperty("newline", "true");
		    // skip to next arg
		    argc++;
		}
	    } else {
		properties.setProperty("filename", args[argc]);
		// skip to next arg
		argc++;
	    }
	}
    }

    public static void help(PrintStream out) {
	out.println("tiselect [-sp pos|-st time] [-ep pos|-et time|-c count|-f elapsedtime] [-n] <tifile>");
    }


    /**
     * Build a TISelect object with a timeindex filename only
     */
    public TISelect(Properties properties) {
	init();
	argProps = properties;
	String filename = properties.getProperty("filename");
	select(filename, System.out);
    }

    /**
     * Build a TISelect object with a timeindex filename only
     */
    public TISelect(Properties properties, OutputStream output) {
	init();
	argProps = properties;
	String filename = properties.getProperty("filename");
	select(filename, output);
    }

    /**
     * Select some output.
     */
    public boolean select(String filename, OutputStream output) {
	return doit(filename, output);
    }

    /**
     * Print an index to the OutputStream.
     */
    protected void printIndex(Index indexIn, OutputStream out) {
	Position startPos = null;
	Position endPos = null;
	AbsoluteTimestamp startTime = null;
	boolean useStartPos = false;
	Interval interval = null;
	TimeIndex index = null;
	IndexView selection = null;

	index = (TimeIndex)indexIn;

	if (argProps.containsKey("startpos")) {
	    // convert string to position object
	    startPos = positionParser(argProps.getProperty("startpos"));
	    if (startPos == null) {
		startPos = new AbsolutePosition(0);
	    }

	    useStartPos = true;

	} else if (argProps.containsKey("starttime")) {
	    // convert string to time object
	    startTime = (AbsoluteTimestamp)timeParser(argProps.getProperty("starttime"), true);
	    if (startTime == null) {
		startPos = new AbsolutePosition(0);
		useStartPos = true;
	    } else {
		useStartPos = false;
	    }


	} else {
	    startPos = new AbsolutePosition(0);
	    useStartPos = true;
	}
	    
	if (argProps.containsKey("endpos")) {
	    // convert string to position object
	    endPos = positionParser(argProps.getProperty("endpos"));
	    if (endPos == null) {
		endPos = new AbsolutePosition(index.getLength());
	    }

	    if (useStartPos) {
		interval = new EndPointInterval(startPos, endPos);
	    } else {
		interval = new EndPointInterval(startTime, endPos);
	    }

	} else if (argProps.containsKey("endtime")) {
	    // convert string to time object
	    AbsoluteTimestamp endTime = (AbsoluteTimestamp)timeParser(argProps.getProperty("endtime"), true);
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


	} else if (argProps.containsKey("count")) {
	    // convert string to count object
	    Count count = countParser(argProps.getProperty("count"));

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



	} else if (argProps.containsKey("for")) {
	    // convert string to relative time object
	    RelativeTimestamp elapsed = (RelativeTimestamp)timeParser(argProps.getProperty("for"), false);
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
	    endPos = new AbsolutePosition(index.getLength());

	    if (useStartPos) {
		interval = new EndPointInterval(startPos, endPos);
	    } else {
		interval = new EndPointInterval(startTime, endPos);
	    }

	}
	   
	System.err.println("Interval = " + interval);
	
	// select from the interval
	selection = index.select(interval, IndexTimestampSelector.DATA, Overlap.FREE, Lifetime.DISCRETE);

	if (selection == null) {
	     System.err.println("Didn't do selection properly");
	} else {
	    // output the selection
	    long total = selection.getLength();

	    for (long i=0; i<total; i++) {
		IndexItem itemN = selection.getItem(i);
		printIndexItem(itemN, out);
	    }
	}
    }

    /**
     * Print an individual IndexItem to the OutputStream.
     */
    protected void printIndexItem(IndexItem item, OutputStream out) {
	ByteBuffer itemdata = item.getData();
	byte [] outbuf = new byte[1024];
	boolean newline = true;

	// check args for newline
	if (argProps.containsKey("newline")) {
	    String nlValue = argProps.getProperty("newline");

	    if (nlValue == null) {
		newline = false;
	    } else if (nlValue.equals("true") || nlValue.equals("1")) {
		newline = true;
	    } else {
		newline = false;
	    }
	} else {
	    newline = false;
	}

	// pump out the data
	try {
	    //out.write(itemdata.array());
	    while (itemdata.remaining() >= 1024) {
		itemdata.get(outbuf, 0, 1024);
		out.write(outbuf);
	    }
	    int remaining = itemdata.remaining();
	    itemdata.get(outbuf, 0, remaining);
	    out.write(outbuf, 0, remaining);

	    if (newline) out.write('\n');
	} catch (java.io.IOException ioe) {
	    ;
	}
    
    }

    /**
     * Parse a position
     */
    public Position positionParser(String posStr) {
	long result = 0;

	try {
	    result = Long.parseLong(posStr);
	    return new AbsolutePosition(result);
	} catch (NumberFormatException nfe) {
	    return null;
	}
    }

    /**
     * Parse a count
     */
    public Count countParser(String countStr) {
	long result = 0;

	try {
	    result = Long.parseLong(countStr);
	    return new RelativeCount(result);
	} catch (NumberFormatException nfe) {
	    return null;
	}
    }

    /** 
     * Parse absolute timestamp.
     */
    public Timestamp timeParser(String timeStr, boolean absolute) {
	Date date = null;
	long seconds = 0;
	int subSeconds = 0;

	ParsePosition pos = new ParsePosition(0);

	SimpleDateFormat fullestFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	SimpleDateFormat ymdFormat = new SimpleDateFormat("yyyy/MM/dd");
	SimpleDateFormat dayFormat = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat hourFormat = new SimpleDateFormat("mm:ss");

	System.err.println("Parse input = " + timeStr);

	if (date == null) {	// the time string is null
	    // let's try dayFormat
	    date = dayFormat.parse(timeStr, pos);
	    System.err.println("Parse day = " + date);
	}

	if (date == null) {	// the time string is null
	    // let's try hourFormat
	    date = hourFormat.parse(timeStr, pos);
	    System.err.println("Parse hour = " + date);
	}

	if (date == null) {	// the time string is null
	    // let's try Format
	    date = fullestFormat.parse(timeStr, pos);
	    System.err.println("Parse date = " + date);
	}

	if (date == null) {	// the time string is null
	    // let's try Format
	    date = fullFormat.parse(timeStr, pos);
	    System.err.println("Parse date = " + date);
	}

	if (date == null) {	// the time string is null
	    // let's try Format
	    date = ymdFormat.parse(timeStr, pos);
	    System.err.println("Parse date = " + date);
	}

	if (date == null) {	//  all the time parses failed 
	    // so set it to zero
	    seconds = 0;
	} else {
	    seconds = date.getTime()/1000;
	}


	System.err.println("Parsed seconds = " + seconds);

	int next = pos.getIndex();

	if (next < timeStr.length() && timeStr.charAt(next) == '.') { // there some more time
	    StringBuffer subSecondBuf = new StringBuffer("000000000");
	    String subSecondStr = timeStr.substring(next+1, timeStr.length());
	    subSecondBuf.replace(0, subSecondStr.length(), subSecondStr);

	    System.err.println("subSecondBuf = " + subSecondBuf);

	    try {
		subSeconds = Integer.parseInt(subSecondBuf.toString());
	    } catch (NumberFormatException nfe) {
		subSeconds = 0;
	    }

	}
	System.err.println("Parsed subseconds = " + subSeconds);

	Timestamp timestamp =  null;

	if (absolute) {
	    timestamp = new NanosecondTimestamp(seconds, subSeconds);
	} else { // elapsed 
	    timestamp = new ElapsedNanosecondTimestamp(seconds, subSeconds);
	}

	System.err.println("Parsed TS = " + timestamp);

	return timestamp;
    }

	
	
}
