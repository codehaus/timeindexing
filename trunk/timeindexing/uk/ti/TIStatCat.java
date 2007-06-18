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
import com.timeindexing.cache.*;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.Properties;


import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Cat a timeindex file to stdout
 * and print out stats to stderr.
 */
public class TIStatCat extends TIAbstractRestore {
    boolean newline = false;

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
		new TIStatCat(args[0], true);
	    } else if (args.length == 2) {
		// have flag andtifile name 
		if (args[0].charAt(0) == '-') {
		    new TIStatCat(args[1], true);
		} else {
		    new TIStatCat(args[1], false);
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
     * Build a TIStatCat object with a timeindex filename only
     */
    public TIStatCat(String tiFileName, boolean nl)  throws TimeIndexException {
	init();
	newline = nl;
	cat(tiFileName, System.out);
    }

    /**
     * Cat the output.
     */
    public boolean cat(String filename, OutputStream output) throws TimeIndexException {
	return doit(filename, output);
    }

    /**
     * Print an index to the OutputStream.
     */
    protected void printIndex(Index index, OutputStream out)  throws TimeIndexException {
	WritableByteChannel stdout = Channels.newChannel(System.out);

	//index.setCachePolicy(new HollowAtDataVolumeRemoveAfterTimeoutPolicy(10*1024, new ElapsedMillisecondTimestamp(200)));
	index.setCachePolicy(new RemoveAfterUsePolicy());

	RelativeTimestamp last = null;
	RelativeTimestamp snap = null;

	// start a stop watch for some timing info
	StopWatch stopWatch = new StopWatch();
	stopWatch.start();

	last = stopWatch.read();

	int writeCount = 0;

	long total = index.getLength();
	for (long i=0; i<total; i++) {
	    IndexItem itemN = index.getItem(i);
	    writeCount += printIndexItem(itemN, stdout);

	    // print out stats every second
	    snap = stopWatch.read();

	    if (TimeCalculator.subtractTimestamp(snap, last).getSeconds() == 1) {

		System.err.println("Elapsed = " + snap + " writeCount = " + (int)(writeCount/1000) + " KBps");
		last = snap;
		writeCount = 0;
	    }
		


	}
	
	stopWatch.stop();
	    
    }


    /**
     * Print an individual IndexItem to the OutputStream.
     * @return the number of bytes written
     */
    protected int printIndexItem(IndexItem item, WritableByteChannel stdout) {
	// follow all references
	try {
	    while (item.isReference()) {
		item = item.follow();
	    }
	} catch (GetItemException gie) {
	    return 0;
	}

	ByteBuffer itemdata = item.getData();
	byte [] outbuf = new byte[1024];
	int writeCount = 0;

	try {
	    //out.write(itemdata.array());
	    /* 
	    while (itemdata.remaining() >= 1024) {
		itemdata.get(outbuf, 0, 1024);
		out.write(outbuf);
		writeCount += 1024;
	    }
	    int remaining = itemdata.remaining();
	    itemdata.get(outbuf, 0, remaining);
	    out.write(outbuf, 0, remaining);
	    writeCount += remaining;

	    */

	    writeCount = stdout.write(itemdata);
	    
	    return writeCount;

	    //if (newline) out.write('\n');
	} catch (java.io.IOException ioe) {
	    return writeCount;
	}
    
    }
}
