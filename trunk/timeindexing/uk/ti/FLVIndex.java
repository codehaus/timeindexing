package uk.ti;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.DataType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexFactoryException;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.index.IndexCreateException;
import com.timeindexing.index.IndexSpecificationException;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.ByteBufferItem;
import com.timeindexing.plugin.ReaderPlugin;
import com.timeindexing.plugin.ReaderResult;
import com.timeindexing.plugin.*;
import com.timeindexing.module.video.FLVIndexer;
import com.timeindexing.module.video.FLVIndexerDisplay;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.List;
import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.*;

/**
 * Create a timeindex file from a file or stdin.
 */
public class FLVIndex {
    File inputFile = null;
    String inputFileName = null;
    String type = null;

    public static void main(String [] args) {
	if (args.length == 2) {
	    // have tifile name and input file name
	    new FLVIndex(args[0], args[1]);
	} else if (args.length == 1) {
	    // have tifile name only, assume input is stdin
	    new FLVIndex(args[0]);
	} else {
	    help(System.err);
	}
    }

    public static void help(PrintStream out) {
	out.println("ticreate <tifile> [<file>]");
    }

    /**
     * Build a FLVIndex object with a timeindex filename and an input filename.
     * If the input filename is "-", use stdin.
     */
    public FLVIndex(String tiFileName, String inputFileName) {
	InputStream input = null;
	TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

	this.inputFileName = inputFileName;

	System.err.println("inputFileName = " + inputFileName);

	if (inputFileName.equals("-")) {
	    input = System.in;
	} else {
	    try {
		inputFile = new File(inputFileName);
		input = new FileInputStream(inputFile);
	    } catch (FileNotFoundException fnfe) {
		System.err.println("Could not open input file: " + inputFileName);
		System.exit(0);
	    }
	}

	create(tiFileName, input);
    }

    /**
     * Build a FLVIndex object with a timeindex filename only
     */
    public FLVIndex(String tiFileName) {
	InputStream input = null;

	input = System.in;

	create(tiFileName, input);
    }

    /**
     * Build a FLVIndex object with a timeindex file name and an input stream
     */
    public FLVIndex(String tiFileName, InputStream input) {
	create(tiFileName, input);
    }

    /**
     * Allocate an View onto an Index
     */
    public IndexView allocate(Properties indexProperties) throws TimeIndexFactoryException, IndexSpecificationException,  IndexCreateException {
	TimeIndexFactory factory = new TimeIndexFactory();

	IndexType indexType = null;

	if (type == null) {
	    throw new Error("Set system propery -Dindextype=[inline|external|shadow]");
	} else if (type.equals("inline")) {
	    indexType = IndexType.INLINE;
	} else if (type.equals("external")) {
	    indexType = IndexType.EXTERNAL;
	} else if (type.equals("shadow")) {
	    if (inputFileName == null) {
		throw new Error("inputFileName == null");
	    }
	    if (inputFileName.equals("-")) {
		throw new Error("Index can;t shadow stdin.  Use a filename");
	    } else {
		indexType = IndexType.SHADOW;
	    }
	} else {
	   throw new Error("Set system propery -Dindextype=[inline|external|shadow]");
	} 

	return factory.create(indexType, indexProperties);
    }

    /**
     * Create the output.
     */
    public boolean create(String tiFileName, InputStream input) {
	if (input == null) {
	    throw new NullPointerException("Input is null in FLVIndex::create()");
	}

	// set up the Index based on system property
	type = System.getProperty("indextype");

	// allocate local objs
	//BufferedReader buffered = new BufferedReader (new InputStreamReader(input));

	Properties indexProperties = new Properties();

	indexProperties.setProperty("indexpath", tiFileName);

	if (type == null) {
	    throw new Error("Set system propery -Dindextype=[external|shadow|inline]");
	}
	 
	if (inputFileName != null && type.equals("shadow")) {
	    indexProperties.setProperty("datapath", inputFileName);
	} else {
	    indexProperties.setProperty("datapath", tiFileName);
	}

	indexProperties.setProperty("name", (inputFile == null ? "ticreate" : inputFile.getName()));

	try {
	    IndexView index = allocate(indexProperties);

	    FLVIndexer flvVideoIndexer = new FLVIndexer(index, input);

	    // get a message every 100ms
	    FLVIndexerDisplay display = new FLVIndexerDisplay(100);
	    flvVideoIndexer.addAddEventListener(display);

	    display.start();

	    // do the input, don;t pass any IndexProperties
	    flvVideoIndexer.doInput(null);

	    display.stop();

	    try {
		input.close();
	    } catch (IOException ioe) {
		System.err.println("Failed to close input");
	    }
	 
	    return true;
	} catch (IOException ioe) {
	    System.err.println("I/O Problem with index \"" + indexProperties.getProperty("indexpath") + "\"");
	    ioe.printStackTrace(System.err);
	    
	    return false;  // this keeps the compiler happy

	} catch (TimeIndexException tie) {
	    System.err.println("Problem with index \"" + indexProperties.getProperty("indexpath") + "\"");
	    tie.printStackTrace(System.err);
	    
	    return false;  // this keeps the compiler happy

	} 

    }
}
