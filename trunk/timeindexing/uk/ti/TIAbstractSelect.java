package uk.ti;

import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.IndexTimestampSelector;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.TimeIndexException;
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
import com.timeindexing.appl.SelectionProcessor;
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
public abstract class TIAbstractSelect extends TIAbstractRestore {
    int intervalStart = 0;
    int intervalEnd = 0;
    TimeIndexFactory factory = null;
    IndexProperties argProps = null;

 
    /**
     * Process the arguments and build an IndexProperties object
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
		properties.setProperty("indexpath", args[argc]);
		// skip to next arg
		argc++;
	    }
	}
    }

    /**
     * Build a TIAbstractSelect object with a timeindex filename only
     */
    public TIAbstractSelect(Properties properties) throws TimeIndexException  {
	init();
	argProps = new IndexProperties(properties);
	String filename = properties.getProperty("indexpath");
	select(filename, System.out);
    }

    /**
     * Build a TIAbstractSelect object with a timeindex filename only
     */
    public TIAbstractSelect(Properties properties, OutputStream output) throws TimeIndexException  {
	init();
	argProps = new IndexProperties(properties);
	String filename = properties.getProperty("indexpath");
	select(filename, output);
    }

    /**
     * Select some output.
     */
    public boolean select(String filename, OutputStream output) throws TimeIndexException {
	return doit(filename, output);
    }

    /**
     * Print an index to the OutputStream.
     */
    protected void printIndex(Index indexIn, OutputStream out) throws TimeIndexException  {
	SelectionProcessor selector = new SelectionProcessor();
 
	IndexView selection = selector.select((TimeIndex)indexIn, argProps);

	if (selection == null) {
	     System.err.println("Didn't do selection properly");
	} else {
	    processSelection(selection, out);
	}
    }

    /**
     * Do something with the selection.
     */
    public abstract void processSelection(IndexView selection, OutputStream out)throws TimeIndexException ;
	
}
