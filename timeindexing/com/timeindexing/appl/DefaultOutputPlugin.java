// DefaultOutputPlugin.java

package com.timeindexing.plugin;

import com.timeindexing.index.Index;
import com.timeindexing.index.TimeIndex;
import com.timeindexing.index.IndexItem;
import com.timeindexing.index.IndexView;
import com.timeindexing.index.IndexProperties;

import java.io.OutputStream;
import java.io.IOException;

/**
 * A class to output any  data
 */
public class DefaultOutputPlugin implements OutputPlugin {
    Index index = null;
    OutputStream out = null;
    long writeCount = 0;

    private final static int BUFSIZE = 1024;

    /**
     * Construct an DefaultOutputPlugin object given
     * an index and an output stream.
     */
    public DefaultOutputPlugin(Index anIndex, OutputStream output) {
	index = anIndex;
	out = output;
    }

    /**
     * Get the index we are doing output for.
     */
    public Index getIndex() {
	return index;
    }

    /**
     * Get the OutputStream for the OutputPlugin.
     */
    public OutputStream getOutputStream() {
	return out;
    }

    /**
     * Do some output, given some IndexProperties.
     */
    public long doOutput(IndexProperties properties) throws IOException {
	begin();
	writeCount = processTimeIndex((TimeIndex)index);
	end();

	return writeCount;
    }

    /**
     * Does nothing.
     */
    public Object begin() {
	return null;
    }

    /**
     * Does nothing.
     */
    public Object end() {
	return null;
    }
   
    /**
     * Process the TimeIndex
     */
    public long processTimeIndex(IndexView selection) throws IOException {
	// output the selection
	long writeCount = 0;
	long total = selection.getLength();

	for (long i=0; i<total; i++) {
	    IndexItem itemN = selection.getItem(i);
	    writeCount += outputPlugin.write(itemN);
	}

	return writeCount;
    }



 }
