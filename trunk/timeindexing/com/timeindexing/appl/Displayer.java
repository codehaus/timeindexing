// Displayer.java

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
 * Display a whole TimeIndex to an OutputStream
 */
public class Displayer {
    TimeIndexFactory factory = null;

    Properties properties = null;

    OutputStream output = null;

    Index index = null;

    /**
     * Construct a Displayer object, with output to System.out.
     */
    public Displayer(String filename) {
	this(filename, System.out);
    }

    /**
     * Construct a Displayer object.
     */
    public Displayer(String filename, OutputStream out) {
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
    public Displayer display(boolean withNLs) throws IOException, TimeIndexException {
	// open
	open();

	// do the output    
	output(withNLs);

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
    protected void output(boolean withNLs) throws IOException, TimeIndexException {
	OutputStreamer streamer = new OutputStreamer(index, output);

	streamer.doOutput(new IndexProperties().putProperty("newline", Boolean.toString(withNLs)));
    }

    /**
     * Close 
     */
    protected void close() throws TimeIndexException {
	factory.close(index);
    }
	
}
