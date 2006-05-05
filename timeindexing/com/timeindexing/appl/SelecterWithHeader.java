// SelecterWithHeader.java

package com.timeindexing.appl;

import com.timeindexing.index.IndexProperties;
import com.timeindexing.index.TimeIndexException;
import java.io.OutputStream;
import java.io.IOException;


/**
 * Display a selection of a TimeIndex to an OutputStream
 * given the Index filename.
 * This always puts out a header before the selection of data from the 
 * TimeIndex.
 */
public class SelecterWithHeader extends Selecter {
    /**
     * Construct a SelecterWithHeader object.
     */
    public SelecterWithHeader(String filename, OutputStream out) {
	super(filename, out);
    }


    /**
     * Do the output
     */
    protected void output(IndexProperties selectionProperties) throws IOException, TimeIndexException {
	SelectionStreamer outputter = new SelectionWithHeaderStreamer(index, output);
	long total = 0;

	total = outputter.doOutput(selectionProperties);
    }
}
