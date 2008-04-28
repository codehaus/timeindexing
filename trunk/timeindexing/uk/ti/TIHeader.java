package uk.ti;


import com.timeindexing.index.Index;
import com.timeindexing.index.ExtendedIndex;
import com.timeindexing.index.ManagedIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.ManagedFileIndexItem;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.Set;
import java.util.Iterator;
import java.net.URI;

/**
 * Header of a timeindex file to stdout.
 */
public class TIHeader extends TIAbstractRestore {
    TimeIndexFactory factory = null;

    Properties properties = null;

    Index index = null;

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
	properties.setProperty("readonly", "true");
	return doit(filename, output);
    }

    /**
     * Print an index to the OutputStream.
     */
    protected void printIndex(Index index, OutputStream out) {
	StringBuffer buf = new StringBuffer(512);

	buf.append("Type: " + index.getIndexType());
	buf.append("  Name: \"" + index.getName() + "\"");
	buf.append("  ID: " + index.getID());
	buf.append("  URI: " + index.getURI());
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

	boolean readonly = index.isReadOnly();
	buf.append("ReadOnly: " + (readonly ? "true" : "false"));
	buf.append("\t");

	boolean locked = index.isWriteLocked();
	buf.append("WriteLocked: " + (locked ? "true" : "false"));
	buf.append("\t");

	long dataSize = index.getDataSize();
	buf.append("DataSize: " + (dataSize == 0 ? "variable" : ""+dataSize));
	buf.append("\n");

	/*
	ManagedIndex mIndex = (ManagedIndex)index;

	IndexProperties options = mIndex.getHeader().getAllOptions();
	Set keys = options.keySet();

	Iterator optionsI = keys.iterator();

	while (optionsI.hasNext()) {
	    Object optionSpec = optionsI.next();
	    buf.append(optionSpec);
	    buf.append(" = ");
	    buf.append(options.get(optionSpec));
	    buf.append("\n");
	}

	*/
	try {
	    out.write(buf.toString().getBytes());
	} catch (java.io.IOException ioe) {
	    ;
	}
    }

    /* Copied from TIAbstractRestore
     * This uses a local IndexOpener
     */

    /**
     * Initialise.
     */
    public boolean init() {
	factory = new TimeIndexFactory();

	properties = new Properties();

	return true;

    }
    
    /**
     * Do the main processing.
     */
    public boolean doit(String filename, OutputStream output) throws TimeIndexException {
	// set the filename property
	properties.setProperty("indexpath", filename);
	//properties.setProperty("datapath", filename);

	// set he close property if it's not already set
	if (! properties.containsKey("close")) {
	    properties.setProperty("close", "true");
	}


	// open and load a hollow Index
	index = factory.open(properties);



	// print it out
	printIndex(index, output);
	    
	// close 
	close();
	    
	return true;

    }
   
    /**
     * Close 
     */
    public void close() {
	// close the index
	boolean doClose = Boolean.valueOf(properties.getProperty("close")).booleanValue();
	if (doClose) {
	    //System.err.println("Closing \"" + index.getName() + "\"");
	    try {
		factory.close(index);
	    } catch (TimeIndexException tie) {
		System.err.println("Closing error " + tie);
	    }
	}
    }
	


}

