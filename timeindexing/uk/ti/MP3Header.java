package uk.ti;


import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.time.NanosecondElapsedFormat;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;


/**
 * Header of a timeindex file to stdout.
 */
public class MP3Header extends TIAbstractRestore {
    public static void main(String [] args) {
	if (args.length == 1) {
	    // have tifile name only,
	    new MP3Header(args[0]);
	} else {
	    help(System.err);
	}
    }

    public static void help(PrintStream out) {
	out.println("tiheader <tifile>");
    }

    /**
     * Build a MP3Header object with a timeindex filename only
     */
    public MP3Header(String tiFileName) {
	init();
	header(tiFileName, System.out);
    }

    /**
     * Header the output.
     */
    public boolean header(String filename, OutputStream output) {
	properties.setProperty("loadstyle", "none");
	return doit(filename, output);
    }

    /**
     * Print an index to the OutputStream.
     */
    protected void printIndex(Index index, OutputStream out) {
	StringBuffer buf = new StringBuffer(512);

	buf.append("Name: " + index.getName());
	buf.append("\tLength: " + index.getLength() + " items, ");
	buf.append("\n");
	buf.append("\n");

	buf.append("Start: " + index.getStartTime());
	buf.append("\n");

	buf.append("    First: " + index.getFirstTime());
	buf.append("\t   " + NanosecondElapsedFormat.format(index.getFirstDataTime()));
	buf.append("\n");

	buf.append("    Last:  " + index.getLastTime());

	buf.append("\t   " +  NanosecondElapsedFormat.format(index.getLastDataTime()));
	buf.append("\n");

	buf.append("End: " + index.getEndTime());
	buf.append("\n");

	/*
	boolean terminated = index.isTerminated();
	buf.append("Terminated: " + (terminated ? "true" : "false"));
	buf.append("\t");

	long dataSize = index.getDataSize();
	buf.append("DataSize: " + (dataSize == 0 ? "variable" : ""+dataSize));
	buf.append("\n");
	*/
	try {
	    out.write(buf.toString().getBytes());
	} catch (java.io.IOException ioe) {
	    ;
	}
    }

}
