// Selecter.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexFactory;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.event.IndexPrimaryEventListener;
import com.timeindexing.event.OutputEventListener;
import com.timeindexing.event.OutputEventGenerator;
import com.timeindexing.event.OutputEvent;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Properties;

/**
 * Display a selection of TimeIndex to an OutputStream
 * given the Index filename.
 */
public class Selecter extends OutputEventGenerator implements OutputEventListener {
    TimeIndexFactory factory = null;

    Properties properties = null;

    OutputStream output = null;

    Index index = null;

    long amountOutput = 0;

    SelectionStreamer outputter = null;

    boolean listeningToStreamer = false;

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
	//System.err.println("Selecter: " + hashCode() + " open." + " Thread " + Thread.currentThread().getName());
	index = factory.open(properties);
    }
	
    /**
     * Do the output
     */
    protected void output(IndexProperties selectionProperties) throws IOException, TimeIndexException {
	//System.err.println("Selecter: " + hashCode() + " output." + " Thread " + Thread.currentThread().getName());

	long total = 0;

	outputter = new SelectionStreamer(index, output);
	// listen to SelectionStreamer
	outputter.addOutputEventListener(this);

	total = outputter.doOutput(selectionProperties);
    }

    /**
     * Close 
     */
    public void close() throws TimeIndexException {
	//System.err.println("Selecter: " + hashCode() + " close." + " Thread " + Thread.currentThread().getName());

	factory.close(index);
    }

    /**
     * Get the number of bytes output.
     */
    public long getBytesOutput() {
	return amountOutput;
    }

    /**
     * Add a OutputEventListener.
     */
    public void addOutputEventListener(OutputEventListener l) {
	// do the super class code for external callers
	super.addOutputEventListener(l);

	// now ensure we listen to the streamer
	// to get its events
	/*
	if (!listeningToStreamer) {
	    outputter.addOutputEventListener(this);
	    listeningToStreamer = true;
	}
	*/
    }

    /**
     * Receive OutputEvents from the SelectionStreamer
     * and pass then to all the Listeners this has.
     */
    public void outputNotification(OutputEvent oe) {
	// add to the total amount output 
	amountOutput += oe.getBytesOutput();

	// pass the event on to listeners, if there are any
	if (hasOutputEventListeners()) {
	    fireOutputEvent(oe);
	}
    }

    /**
     * Add a IndexPrimaryEventListener.
     */
    public void addPrimaryEventListener(IndexPrimaryEventListener l) {
	factory.addPrimaryEventListener(l);
    }

    /**
     * Remove a IndexPrimaryEventListener.
     */
    public void removePrimaryEventListener(IndexPrimaryEventListener l) {
	factory.removePrimaryEventListener(l);
    }
}