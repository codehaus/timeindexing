Copyright 2008 Stuart Clayman
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
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
import com.timeindexing.index.IndexOpenException;
import com.timeindexing.index.IndexWriteLockedException;
import com.timeindexing.index.IndexReadOnlyException;
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
public class TestLock1 {
    File inputFile = null;

    TimeIndexFactory factory = null;

    Properties indexProperties = null;

    Index index = null;


    public static void main(String [] args) {
	if (args.length == 2) {
	    // have tifile name and input file name
	    new TestLock1(args[0], args[1]);
	} else if (args.length == 1) {
	    // have tifile name only, assume input is stdin
	    new TestLock1(args[0]);
	} else {
	    help(System.err);
	}
    }

    public static void help(PrintStream out) {
	out.println("tiappend <tifile> [<file>]");
    }

    /**
     * Build a TestLock1 object with a timeindex filename and an input filename.
     * If the input filename is "-", use stdin.
     */
    public TestLock1(String tiFileName, String inputFileName) {
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
     * Build a TestLock1 object with a timeindex filename only
     */
    public TestLock1(String tiFileName) {
	InputStream input = null;

	input = System.in;

	append(tiFileName, input);
    }

    /**
     * Build a TestLock1 object with a timeindex file name and an input stream
     */
    public TestLock1(String tiFileName, InputStream input) {
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
	    throw new NullPointerException("Input is null in TestLock1::append()");
	}

	init();

	indexProperties.setProperty("indexpath", tiFileName);

	try {
	    // open and load the Index
	    index = factory.append(indexProperties);

	    DataItem item = null;

	    ReaderPlugin plugin = null;
	    ReaderResult result = null;
	    Timestamp dataTS = null;

	    plugin = new Line(input);

	    // do stuff

	    boolean activated = false;

	    // try 5 times to activate
	    for (int trys=0; trys<5; trys++) {

		try {
		    index.activate();
		    activated = true;
		    break;
		} catch (IndexWriteLockedException iwle) {
		    System.err.println("TestLock1: Index " + index.getURI() + " is Write Locked");
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException ie) {
			;
		    }
		} catch (IndexReadOnlyException iroe) {
		    System.err.println("TestLock1: Index " + index.getURI() + " is Read Only");
		}
	    }

	    if (activated) {

		try {
		    long indexSize = 0;

		    while ((result = plugin.read()) != null) {
			dataTS = result.getDataTimestamp();
			item = new ByteBufferItem(result.getData());

			indexSize = index.addItem(item, dataTS);
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
	    } else {
		try {
		    index.close();
		    input.close();
		} catch (IOException ioe) {
		    System.err.println("Failed to close input");
		}

		return false;
	    }
	} catch (TimeIndexException tie) {
	    System.err.println("Failed on index \"" + indexProperties.getProperty("indexpath") + "\"" + tie);
	    return false;  // this keeps the compiler happy
	} 	    

    }
}
