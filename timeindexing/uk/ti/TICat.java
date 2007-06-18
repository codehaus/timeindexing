package uk.ti;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.GetItemException;
import com.timeindexing.index.IndexClosedException;
import com.timeindexing.cache.RemoveAfterUsePolicy;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;


import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Cat a timeindex file to stdout.
 */
public class TICat extends TIAbstractRestore {
    boolean newline = false;

    public static void main(String [] args) {
	/*
	 * Handle broken pipes properly
	 */
	/*
	final Signal sigpipe = new Signal("PIPE");

	SignalHandler handler = new SignalHandler () {
		public void handle(Signal sig) {
		    System.exit(sigpipe.hashCode());
		}
	    };
	Signal.handle(sigpipe, handler);
	*/



	try {
	    if (args.length == 1) {
		// have tifile name only,
		new TICat(args[0], true);
	    } else if (args.length == 2) {
		// have flag andtifile name 
		if (args[0].charAt(0) == '-') {
		    new TICat(args[1], true);
		} else {
		    new TICat(args[1], false);
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
     * Build a TICat object with a timeindex filename only
     */
    public TICat(String tiFileName, boolean nl)  throws TimeIndexException {
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

	index.setCachePolicy(new RemoveAfterUsePolicy());

	long total = index.getLength();
	for (long i=0; i<total; i++) {
	    IndexItem itemN = index.getItem(i);
	    printIndexItem(itemN, out);
	}
	    
	    
    }


    /**
     * Print an individual IndexItem to the OutputStream.
     */
    protected void printIndexItem(IndexItem item, OutputStream out) {
	// follow all references
	try {
	    while (item.isReference()) {
		item = item.follow();
	    }
	} catch (GetItemException gie) {
	    return;
	} catch (IndexClosedException ice) {
	    return;
	}

	ByteBuffer itemdata = item.getData();
	byte [] outbuf = new byte[1024];

	try {
	    //out.write(itemdata.array());
	    while (itemdata.remaining() >= 1024) {
		itemdata.get(outbuf, 0, 1024);
		out.write(outbuf);
	    }
	    int remaining = itemdata.remaining();
	    itemdata.get(outbuf, 0, remaining);
	    out.write(outbuf, 0, remaining);

	    //if (newline) out.write('\n');
	} catch (java.io.IOException ioe) {
	    ;
	}
    
    }
}
