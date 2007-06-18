package uk.ti;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.GetItemException;
import com.timeindexing.time.StopWatch;
import com.timeindexing.time.RelativeTimestamp;
import com.timeindexing.time.ElapsedMillisecondTimestamp;
import com.timeindexing.time.TimeCalculator;
import com.timeindexing.appl.OutputStreamer;
import com.timeindexing.event.OutputEventListener;
import com.timeindexing.event.OutputEvent;
import com.timeindexing.cache.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;


import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Cat2 a timeindex file to stdout
 * and print out stats to stderr.
 */
public class TIStatCat2 extends TIAbstractRestore implements OutputEventListener {
    boolean newline = false;

    StopWatch stopWatch = null;
    RelativeTimestamp last = null;
    RelativeTimestamp snap = null;
    int writeCount = 0;


    /**
     * Main entry point
     */
    public static void main(String [] args) {
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



	try {
	    if (args.length == 1) {
		// have tifile name only,
		new TIStatCat2(args[0], true);
	    } else if (args.length == 2) {
		// have flag andtifile name 
		if (args[0].charAt(0) == '-') {
		    new TIStatCat2(args[1], true);
		} else {
		    new TIStatCat2(args[1], false);
		}
	    } else {
		help(System.err);
	    }
 	} catch (TimeIndexException tie) {
	    System.err.println("Cannot open index \"" + args[0] + "\"");
	}
    }

    public static void help(PrintStream out) {
	out.println("ticat <tifile>");
    }

    /**
     * Build a TIStatCat2 object with a timeindex filename only
     */
    public TIStatCat2(String tiFileName, boolean nl)  throws TimeIndexException {
	init();
	newline = nl;
	cat(tiFileName, System.out);
    }

    /**
     * Cat2 the output.
     */
    public boolean cat(String filename, OutputStream output) throws TimeIndexException {
	return doit(filename, output);
    }

    /**
     * Print an index to the OutputStream.
     */
    protected void printIndex(Index index, OutputStream out)  throws TimeIndexException {
	index.setCachePolicy(new RemoveAfterUsePolicy());

	OutputStreamer outputter = new OutputStreamer(index, out);
	outputter.addOutputEventListener(this);

	// start a stop watch for some timing info
	stopWatch = new StopWatch();
	stopWatch.start();

	last = stopWatch.read();

	long total = 0;

	try {
	    total = outputter.doOutput(null);

	    //System.err.println("Wrote " + total + " bytes.");
	} catch (TimeIndexException ioe) {
	    System.err.println("OutputStreamer failed: " + ioe);
	} catch (IOException ioe) {
	    System.err.println("OutputStreamer failed: " + ioe);
	}


	stopWatch.stop();
	    
    }

    /**
     * A notification that some output has been done
     */
    public void outputNotification(OutputEvent oe) {
	writeCount += oe.getBytesOutput();

	// print out stats every second
	snap = stopWatch.read();

	if (TimeCalculator.subtractTimestamp(snap, last).getSeconds() == 1) {

	    if (writeCount < 1024 ) {
		System.err.println("Elapsed = " + snap + " writeCount = " + (int)(writeCount) + " Bps");	  
	    } else if (writeCount < 1024 * 1024) {
		System.err.println("Elapsed = " + snap + " writeCount = " + (int)(writeCount/1024) + " KBps");
	    } else if (writeCount < 1024 * 1024 * 1024) {
		System.err.println("Elapsed = " + snap + " writeCount = " + (int)(writeCount/(1024*1024)) + " MBps");
	    } else {
		System.err.println("Elapsed = " + snap + " writeCount = " + (int)(writeCount) + " Bps");
	    }
		
	    last = snap;
	    writeCount = 0;
	}
			
    }
}
