// Selecter.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

/**
 * Display a selection of TimeIndex to an OutputStream
 * given the Index filename.
 */
public class Selecter {
    TimeIndexFactory factory = null;

    Properties properties = null;

    OutputStream output = null;

    Index index = null;

    /**
     * Construct a Selecter object, with output to System.out.
     */
    public Selecter(String filename) {
	this(filename, System.out);
    }

    /**
     * Construct a Selecter object.
     */
    public Selecter(String filename, OutputStream out) {
	factory = new TimeIndexFactory();
	properties = new Properties();

	// set the filename property
	properties.setProperty("indexpath", filename);

	// set the output
	output = out;
    }

    /**
     * Display the output.
     */
    public Selecter select(IndexProperties selectionProperties) throws IOException, TimeIndexException {
	// open
	open();

	// do the output    
	output(selectionProperties);

	// close 
	close();

	return this;
    }

    /**
     * Open
     */
    protected void open() throws TimeIndexException {
	index = factory.open(properties);
    }
	
    /**
     * Do the output
     */
    protected void output(IndexProperties selectionProperties) throws IOException, TimeIndexException {
	SelectionStreamer outputter = new SelectionStreamer(index, output);
	long total = 0;

	total = outputter.doOutput(selectionProperties);
    }

    /**
     * Close 
     */
    public void close() throws TimeIndexException {
	factory.close(index);
	index = null;
    }
}
