// SelectionWithHeaderStreamer.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexException;
import com.timeindexing.appl.SelectionProcessor;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A class to output a selction of the data toghether with a header.
 */
public class SelectionWithHeaderStreamer extends SelectionStreamer  {
    /**
     * Construct an SelectionWithHeaderStreamer object given
     * an index and an output stream.
     */
    public SelectionWithHeaderStreamer(Index anIndex, OutputStream output) {
	super(anIndex, output);
    }

    /**
     * Do some output, given some IndexProperties.
     * The IndexProperties specify a selection to make.
     * Only the selection is output.
     */
    public long doOutput(IndexProperties properties) throws IOException, TimeIndexException {
	outputProperties = properties;

	outputPlugin.setContext(index, out);

	outputPlugin.begin();

	// output the header
	writeCount += processHeader(index);


	// output main selection
	SelectionProcessor selector = new SelectionProcessor();
 
	IndexView selection = selector.select((IndexView)index, properties);

	writeCount += processTimeIndex((IndexView)selection);

	System.err.println("SelectionWithHeaderStreamer: " + this.hashCode() + " wrote = " + writeCount + ". Thread " + Thread.currentThread().getName() );
	outputPlugin.end();

	return writeCount;
    }

    /**
     * Process the header and output it.
     * @return the no of bytes output
     */
    protected long processHeader(Index index)  throws IOException, TimeIndexException {
	long writeCount = 0;

	// get the header
	IndexItem header = fetchIndexItem(0, index);

	// output the header
	writeCount = outputPlugin.write(header, outputProperties);

	return writeCount;
    }
}
