// SelectionStreamer.java

package com.timeindexing.appl;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.appl.SelectionProcessor;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A class to output a selctino of the data.
 */
public class SelectionStreamer extends OutputStreamer  {
    /**
     * Construct an SelectionStreamer object given
     * an index and an output stream.
     */
    public SelectionStreamer(Index anIndex, OutputStream output) {
	super(anIndex, output);
    }

    /**
     * Do some output, given some IndexProperties.
     * The IndexProperties specify a selection to make.
     * Only the selection is output.
     */
    public long doOutput(IndexProperties properties) throws IOException {
	outputProperties = properties;

	outputPlugin.begin();
	SelectionProcessor selector = new SelectionProcessor();
 
	IndexView selection = selector.select((IndexView)index, properties);
	writeCount = processTimeIndex((IndexView)selection);
	outputPlugin.end();

	return writeCount;
    }

}
