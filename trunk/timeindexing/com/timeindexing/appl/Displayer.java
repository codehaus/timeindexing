// Displayer.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.plugin.DefaultOutputPlugin;
import com.timeindexing.plugin.DefaultWriter;
import com.timeindexing.plugin.OutputPlugin;
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
    public Displayer display(IndexProperties displayProperties) throws IOException, TimeIndexException {
	// open
	open();

	// do the output    
	output(displayProperties, new DefaultOutputPlugin(new DefaultWriter()));

	// close 
	close();

	return this;
    }

    /**
     * Display the output, using a specified OutputPlugin.
     */
    public Displayer display(IndexProperties displayProperties, OutputPlugin outputPlugin) throws IOException, TimeIndexException {
	// open
	open();

	// do the output    
	output(displayProperties, outputPlugin);

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
     * Do the output with a specific OutputPlugin.
     */
    protected void output(IndexProperties displayProperties, OutputPlugin outputPlugin) throws IOException, TimeIndexException {
	// Set up an OutputStreamer
	OutputStreamer streamer = new OutputStreamer(index, output, outputPlugin);

	// now do the output
	streamer.doOutput(displayProperties);
    }

    /**
     * Close 
     */
    protected void close() throws TimeIndexException {
	factory.close(index);
    }
	
}
