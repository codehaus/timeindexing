package uk.ti;

import com.timeindexing.basic.ID;
import com.timeindexing.basic.UID;
import com.timeindexing.index.Index;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexType;
import com.timeindexing.index.DataType;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.time.Timestamp;
import com.timeindexing.time.MillisecondTimestamp;
import com.timeindexing.data.DataItem;
import com.timeindexing.data.ByteBufferItem;
import com.timeindexing.plugin.ReaderPlugin;
import com.timeindexing.plugin.ReaderResult;
import com.timeindexing.plugin.*;

import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.nio.ByteBuffer;
import java.io.*;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Append a timeindex file from a file or stdin.
 */
public class TIAppend {
    File inputFile = null;

    TimeIndexFactory factory = null;

    Properties indexProperties = null;

    Index index = null;


    public static void main(String [] args) {
	if (args.length == 2) {
	    // have tifile name and input file name
	    new TIAppend(args[0], args[1]);
	} else if (args.length == 1) {
	    // have tifile name only, assume input is stdin
	    new TIAppend(args[0]);
	} else {
	    help(System.err);
	}
    }

    public static void help(PrintStream out) {
	out.println("tiappend <tifile> [<file>]");
    }

    /**
     * Build a TIAppend object with a timeindex filename and an input filename.
     * If the input filename is "-", use stdin.
     */
    public TIAppend(String tiFileName, String inputFileName) {
	InputStream input = null;

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

	append(tiFileName, input);
    }

    /**
     * Build a TIAppend object with a timeindex filename only
     */
    public TIAppend(String tiFileName) {
	InputStream input = null;

	input = System.in;

	append(tiFileName, input);
    }

    /**
     * Build a TIAppend object with a timeindex file name and an input stream
     */
    public TIAppend(String tiFileName, InputStream input) {
	append(tiFileName, input);
    }

    /**
     * Initialise.
     */
    public boolean init() {
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
 

	factory = new TimeIndexFactory();

	indexProperties = new Properties();

	return true;

    }

    /**
     * Append the output.
     */
    public boolean append(String tiFileName, InputStream input) {
	if (input == null) {
	    throw new NullPointerException("Input is null in TIAppend::append()");
	}

	init();

	indexProperties.setProperty("filename", tiFileName);
	
	// open and load the Index
	index = factory.append(indexProperties);

	DataItem item = null;

	ReaderPlugin plugin = null;
	ReaderResult result = null;
	Timestamp dataTS = null;

	// set up plugin based on system property
	String pluginname = System.getProperty("plugin");

	if (pluginname == null) {
	    plugin = new Line(input);
	} else if (pluginname.equals("line")) {
	    plugin = new Line(input);
	} else 	if (pluginname.equals("web")) {
	    plugin = new  WebServerLogLine(input);
	} else 	if (pluginname.equals("mail")) {
	    plugin = new  MailServerLogLine(input);
	} else 	if (pluginname.equals("block")) {
	    plugin = new  Block(input);
	    ((Block)plugin).setBlockSize(16);
	} else if (pluginname.equals("file")) {
	    plugin = new FileItem((FileInputStream)input);
	} else {
	     plugin = new Line(input);
	}

	// do stuff
	index.activate();
	try {
	    long indexSize = 0;

	    while ((result = plugin.read()) != null) {
		dataTS = result.getDataTimestamp();
		item = new ByteBufferItem(result.getData());

		indexSize = index.addItem(item, dataTS);

		//index.hollowItem(indexSize - 1);
	    }

	    // wind it up
	    index.close();

	}  catch (IOException ioe) {
	    System.err.println("Read error on input");
	}

	try {
	    input.close();
	} catch (IOException ioe) {
	    System.err.println("Failed to close input");
	}
	 
	return true;
    }
}
