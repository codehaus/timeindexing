// SelectionPlugin.java

package com.timeindexing.plugin;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;
import com.timeindexing.appl.SelectionProcessor;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A class to output MP3 data
 */
public class SelectionPlugin extends DefaultOutputPlugin implements OutputPlugin {
    /**
     * Construct an SelectionPlugin object given
     * an index and an output stream.
     */
    public SelectionPlugin(Index anIndex, OutputStream output) {
	super(anIndex, output);
    }

    /**
     * Do some output, given some IndexProperties.
     */
    public long doOutput(IndexProperties properties) throws IOException {
	begin();

	SelectionProcessor selector = new SelectionProcessor();
 
	IndexView selection = null;

	if (index == null) {
	    System.err.println("No index has been set.");
	    end();
	    return 0;
	} else {
	    selection = selector.select((TimeIndex)index, properties);
	}


	if (selection == null) {
	    System.err.println("Didn't specify selection properly");
	    end();
	    return 0;
	} else {
	    end();
	    return processTimeIndex(selection);
	}

    }

}
