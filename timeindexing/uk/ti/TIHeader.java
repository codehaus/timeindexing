package uk.ti;


import com.timeindexing.index.Index;
import com.timeindexing.index.ExtendedIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.TimeIndexException;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;


/**
 * Header of a timeindex file to stdout.
 */
public class TIHeader extends TIAbstractRestore {
    public static void main(String [] args) {
	try {
	    if (args.length == 1) {
		// have tifile name only,
		new TIHeader(args[0]);
	    } else {
		help(System.err);
	    }
 	} catch (TimeIndexException tie) {
	    System.err.println("Cannot open index \"" + args[0] + "\"");
	    tie.printStackTrace(System.err);
	}

    }

    public static void help(PrintStream out) {
	out.println("tiheader <tifile>");
    }

    /**
     * Build a TIHeader object with a timeindex filename only
     */
    public TIHeader(String tiFileName) throws TimeIndexException {
	init();
	header(tiFileName, System.out);
    }

    /**
     * Header the output.
     */
    public boolean header(String filename, OutputStream output) throws TimeIndexException {
	properties.setProperty("loadstyle", "none");
	return doit(filename, output);
    }

    /**
     * Print an index to the OutputStream.
     */
    protected void printIndex(Index index, OutputStream out) {
	StringBuffer buf = new StringBuffer(512);

	buf.append("Name: \"" + index.getName() + "\"");
	buf.append("  ID: " + index.getID());
	buf.append("  Length: " + index.getLength() + " items, ");
	buf.append("\n");

	String indexPath = index.getIndexPathName();

	if (indexPath != null) {
	    buf.append("Index Path: \"" + indexPath + "\" ");
	}

	String dataPath = index.getDataPathName();

	if (dataPath != null) {
	    buf.append("Data Path: \"" + dataPath + "\" ");
	}
	buf.append("\n");

	
	buf.append("Start:  " + index.getStartTime() + " ");
	buf.append(" End: " + index.getEndTime() + " ");

	buf.append("\n");

	buf.append("First:  " + index.getFirstTime() + " ");
	buf.append("Last:  " + index.getLastTime() + " ");

	buf.append("\n");

	buf.append("FirstD: " + index.getFirstDataTime() + " ");
	buf.append("LastD: " + index.getLastDataTime() + " ");
	buf.append("\n");

	boolean terminated = index.isTerminated();
	buf.append("Terminated: " + (terminated ? "true" : "false"));
	buf.append("\t");

	long dataSize = index.getDataSize();
	buf.append("DataSize: " + (dataSize == 0 ? "variable" : ""+dataSize));
	buf.append("\n");
	try {
	    out.write(buf.toString().getBytes());
	} catch (java.io.IOException ioe) {
	    ;
	}
    }

}