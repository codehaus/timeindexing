package uk.ti;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.TimeIndexException;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;


/**
 * Cat a timeindex file to stdout.
 */
public class TICat extends TIAbstractRestore {
    boolean newline = true;

    public static void main(String [] args) {

	try {
	    if (args.length == 1) {
		// have tifile name only,
		new TICat(args[0], true);
	    } else if (args.length == 2) {
		// have flag andtifile name 
		if (args[0].charAt(0) == '-') {
		    new TICat(args[1], false);
		} else {
		    new TICat(args[1], true);
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
    protected void printIndex(Index index, OutputStream out) {
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

	    if (newline) out.write('\n');
	} catch (java.io.IOException ioe) {
	    ;
	}
    
    }
}
