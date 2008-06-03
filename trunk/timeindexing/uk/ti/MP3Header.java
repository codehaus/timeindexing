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


import com.timeindexing.index.Index;
import com.timeindexing.index.ManagedIndexHeader;
import com.timeindexing.time.NanosecondElapsedFormat;
import com.timeindexing.io.IndexDecoder;
import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.TimeZone;

/**
 * Header of a timeindex file to stdout.
 */
public class MP3Header  {
    IndexDecoder decoder = null;


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
     * Initialise.
     */
    public boolean init() {
	// set 0 timezone
	TimeZone.setDefault(TimeZone.getTimeZone("GMT"));

	return true;

    }

    /**
     * Header the output.
     */
    public boolean header(String filename, OutputStream output) {
	return doit(filename, output);
    }

    /**
     * Do the main processing.
     */
    public boolean doit(String filename, OutputStream output) {
	// set the filename property
	File indexFile = new File(filename);

	try {
	    // create an index decoder
	    decoder = new IndexDecoder(indexFile);

	    // read the header
	    decoder.read();

	    // print it out
	    printHeader(decoder, output);
	    
	    // close 
	    decoder.close();
	    
	    return true;

	} catch (IOException ioe) {
	    System.err.println("Cannot open index \"" + filename + "\"");
	    return false;
	}
    }
   
    /**
     * Print the header of an index to the OutputStream.
     */
    protected void printHeader(ManagedIndexHeader index, OutputStream out) {
	StringBuffer buf = new StringBuffer(512);

	buf.append("Name: " + index.getName());
	buf.append("\tLength: " + index.getLength() + " items, ");
	buf.append("\n");
	buf.append("\n");

	String indexPath = index.getIndexPathName();

	if (indexPath != null) {
	    buf.append("Index Path: \"" + indexPath + "\" ");
	}

	String dataPath = index.getDataPathName();

	if (dataPath != null) {
	    buf.append("Data Path: \"" + dataPath + "\" ");
	}
	buf.append("\n\n");

	buf.append("Start: " + index.getStartTime());
	buf.append("\n");

	buf.append("    First: " + index.getFirstTime());
	buf.append("\t   " + new NanosecondElapsedFormat().format(index.getFirstDataTime()));
	buf.append("\n");

	buf.append("    Last:  " + index.getLastTime());

	buf.append("\t   " +  new NanosecondElapsedFormat().format(index.getLastDataTime()));
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
